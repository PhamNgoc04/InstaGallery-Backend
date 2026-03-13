package com.instagallery.services

import com.instagallery.models.request.LoginRequest
import com.instagallery.models.request.RegisterRequest
import com.instagallery.models.response.LoginResponse
import com.instagallery.models.response.RegisterResponse
import com.instagallery.plugins.AuthException
import com.instagallery.plugins.ValidationException
import com.instagallery.repositories.UserRepository
import com.instagallery.repositories.SessionRepository
import com.instagallery.utils.JwtManager
import com.instagallery.utils.PasswordHasher
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AuthService : KoinComponent {
    private val userRepository: UserRepository by inject()
    private val jwtManager: JwtManager by inject()

    private val sessionRepository: SessionRepository by inject()
    private val passwordResetRepository: com.instagallery.repositories.PasswordResetRepository by inject()

    suspend fun register(request: RegisterRequest, deviceInfo: String? = null, ipAddress: String? = null): RegisterResponse {
        // Validation
        if (!isValidEmail(request.email)) {
            throw ValidationException("INVALID_EMAIL", "Email format is incorrect.")
        }
        if (request.passwordHash.length < 8) {
            throw ValidationException("PASSWORD_TOO_WEAK", "Password must be at least 8 characters long.")
        }

        // Check duplicates
        if (userRepository.getUserByEmail(request.email) != null) {
            throw ValidationException("EMAIL_ALREADY_EXISTS", "This email is already registered.")
        }
        if (userRepository.getUserByUsername(request.username) != null) {
            throw ValidationException("USERNAME_ALREADY_EXISTS", "This username is already taken.")
        }

        // Hash password
        val hashedPw = PasswordHasher.hashPassword(request.passwordHash)

        // Save to DB
        val newUser = userRepository.createUser(request, hashedPw)
            ?: throw Exception("Failed to create user.") // Should not happen

        // Generate Token
        val token = jwtManager.generateToken(newUser)
        val refreshToken = java.util.UUID.randomUUID().toString()
        val expiredAt = java.time.LocalDateTime.now().plusDays(30)
        
        sessionRepository.createSession(newUser.id, deviceInfo, ipAddress, refreshToken, expiredAt)

        return RegisterResponse(
            userId = newUser.id,
            email = newUser.email,
            username = newUser.username,
            token = token,
            refreshToken = refreshToken
        )
    }

    suspend fun login(request: LoginRequest, deviceInfo: String? = null, ipAddress: String? = null): LoginResponse {
        val user = userRepository.getUserByEmail(request.email)
            ?: throw AuthException("USER_NOT_FOUND", "Account with this email does not exist.")

        if (!user.isActive) {
            throw AuthException("ACCOUNT_LOCKED", "Your account has been locked or suspended.")
        }

        val isValidPassword = PasswordHasher.verifyPassword(request.passwordHash, user.passwordHash)
        if (!isValidPassword) {
            throw AuthException("WRONG_PASSWORD", "Incorrect password.")
        }

        val token = jwtManager.generateToken(user)
        val refreshToken = java.util.UUID.randomUUID().toString()
        val expiredAt = java.time.LocalDateTime.now().plusDays(30)

        sessionRepository.createSession(user.id, deviceInfo, ipAddress, refreshToken, expiredAt)

        return LoginResponse(
            userId = user.id,
            email = user.email,
            username = user.username,
            role = user.role,
            token = token,
            refreshToken = refreshToken
        )
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]+$".toRegex()
        return email.matches(emailRegex)
    }

    suspend fun refreshToken(oldRefreshToken: String, deviceInfo: String? = null, ipAddress: String? = null): com.instagallery.models.response.TokenRefreshResponse {
        val sessionRow = sessionRepository.getSessionByRefreshToken(oldRefreshToken)
            ?: throw AuthException("INVALID_TOKEN", "Refresh token is invalid or expired.")

        val expiredAt = sessionRow[com.instagallery.database.tables.UserSessionsTable.expiredAt]
        if (expiredAt.isBefore(java.time.Instant.now())) {
            sessionRepository.deleteSessionByToken(oldRefreshToken)
            throw AuthException("TOKEN_EXPIRED", "Refresh token has expired. Please login again.")
        }

        val userId = sessionRow[com.instagallery.database.tables.UserSessionsTable.userId].value
        val user = userRepository.getUserById(userId)
            ?: throw AuthException("USER_NOT_FOUND", "Account does not exist.")

        if (!user.isActive) {
            throw AuthException("ACCOUNT_LOCKED", "Your account has been locked or suspended.")
        }

        // Tạo vòng lặp Session mới: Thu hồi Token cũ, cấp cái mới
        sessionRepository.deleteSessionByToken(oldRefreshToken)
        
        val newAccessToken = jwtManager.generateToken(user)
        val newRefreshToken = java.util.UUID.randomUUID().toString()
        val newExpiredAt = java.time.LocalDateTime.now().plusDays(30)
        
        sessionRepository.createSession(userId, deviceInfo, ipAddress, newRefreshToken, newExpiredAt)

        return com.instagallery.models.response.TokenRefreshResponse(
            token = newAccessToken,
            refreshToken = newRefreshToken,
            expiresAt = newExpiredAt.atZone(java.time.ZoneId.systemDefault()).toEpochSecond()
        )
    }

    suspend fun logout(refreshToken: String) {
        sessionRepository.deleteSessionByToken(refreshToken)
    }

    suspend fun getMySessions(userId: Long, currentToken: String?): com.instagallery.models.response.UserSessionsResponse {
        val sessions = sessionRepository.getSessionsByUserId(userId)
        val dtoList = sessions.map { row ->
            val refToken = row[com.instagallery.database.tables.UserSessionsTable.refreshToken]
            val isCurrent = currentToken != null && currentToken == refToken
            com.instagallery.models.response.SessionDto(
                id = row[com.instagallery.database.tables.UserSessionsTable.id].value,
                deviceInfo = row[com.instagallery.database.tables.UserSessionsTable.deviceInfo],
                ipAddress = row[com.instagallery.database.tables.UserSessionsTable.ipAddress],
                createdAt = row[com.instagallery.database.tables.UserSessionsTable.createdAt].toString(),
                expiredAt = row[com.instagallery.database.tables.UserSessionsTable.expiredAt].toString(),
                isCurrent = isCurrent
            )
        }
        return com.instagallery.models.response.UserSessionsResponse(sessions = dtoList)
    }

    suspend fun revokeSession(userId: Long, sessionId: Long) {
        sessionRepository.deleteSessionByIdAndUser(sessionId, userId)
    }

    suspend fun changePassword(userId: Long, request: com.instagallery.models.request.ChangePasswordRequest) {
        val user = userRepository.getUserById(userId)
            ?: throw AuthException("USER_NOT_FOUND", "Account does not exist.")

        val isValidPassword = PasswordHasher.verifyPassword(request.oldPasswordHash, user.passwordHash)
        if (!isValidPassword) {
            throw AuthException("WRONG_PASSWORD", "Mật khẩu cũ không chính xác.")
        }
        
        if (request.newPasswordHash.length < 8) {
            throw ValidationException("PASSWORD_TOO_WEAK", "Mật khẩu mới phải từ 8 ký tự trở lên.")
        }

        val newHashedPw = PasswordHasher.hashPassword(request.newPasswordHash)
        // Update user db row
        userRepository.updatePassword(userId, newHashedPw)
        // Khi đổi pass thành công, hủy toàn bộ Session
        sessionRepository.deleteAllSessionsForUser(userId)
    }

    suspend fun requestPasswordReset(email: String): String? {
        val user = userRepository.getUserByEmail(email)
            ?: return null // Return null to avoid email enumeration attacks

        val token = java.util.UUID.randomUUID().toString()
        val expiredAt = java.time.LocalDateTime.now().plusHours(1)

        passwordResetRepository.saveToken(user.id, token, expiredAt)
        return token
    }

    suspend fun resetPassword(request: com.instagallery.models.request.ResetPasswordRequest) {
        val tokenRow = passwordResetRepository.getToken(request.resetToken)
            ?: throw AuthException("INVALID_TOKEN", "Đường dẫn quá hạn hoặc không hợp lệ.")

        val expiredAt = tokenRow[com.instagallery.database.tables.PasswordResetTokensTable.expiredAt]
        if (expiredAt.isBefore(java.time.LocalDateTime.now())) {
            throw AuthException("TOKEN_EXPIRED", "Đường dẫn khôi phục đã hết hạn.")
        }

        val userId = tokenRow[com.instagallery.database.tables.PasswordResetTokensTable.userId].value
        val user = userRepository.getUserById(userId)
            ?: throw AuthException("USER_NOT_FOUND", "Tài khoản không tồn tại.")

        if (request.newPasswordHash.length < 8) {
            throw ValidationException("PASSWORD_TOO_WEAK", "Mật khẩu mới phải từ 8 ký tự trở lên.")
        }

        val newHashedPw = PasswordHasher.hashPassword(request.newPasswordHash)
        
        userRepository.updatePassword(userId, newHashedPw)
        
        // Invalidate all tokens and sessions
        passwordResetRepository.deleteTokensByUserId(userId)
        sessionRepository.deleteAllSessionsForUser(userId)
    }
}
