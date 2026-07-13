package com.instagallery.services

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.instagallery.models.common.AuthProvider
import com.instagallery.models.common.UserDto
import com.instagallery.models.request.GoogleLoginRequest
import com.instagallery.models.request.LoginRequest
import com.instagallery.models.request.RegisterRequest
import com.instagallery.models.response.ForgotPasswordStartResponse
import com.instagallery.models.response.LoginResponse
import com.instagallery.models.response.RegisterResponse
import com.instagallery.plugins.AuthException
import com.instagallery.plugins.ValidationException
import com.instagallery.repositories.UserRepository
import com.instagallery.repositories.SessionRepository
import com.instagallery.utils.JwtManager
import com.instagallery.utils.PasswordHasher
import io.ktor.server.config.ApplicationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.security.SecureRandom
import java.text.Normalizer
import java.util.Locale
import java.util.UUID

class AuthService : KoinComponent {
    private val userRepository: UserRepository by inject()
    private val jwtManager: JwtManager by inject()
    private val config: ApplicationConfig by inject()

    private val sessionRepository: SessionRepository by inject()
    private val passwordResetRepository: com.instagallery.repositories.PasswordResetRepository by inject()
    private val secureRandom = SecureRandom()

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
            role = newUser.role,
            userType = newUser.userType,
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

        return createLoginResponse(user, deviceInfo, ipAddress)
    }

    suspend fun loginWithGoogle(
        request: GoogleLoginRequest,
        deviceInfo: String? = null,
        ipAddress: String? = null,
    ): LoginResponse {
        if (request.idToken.isBlank()) {
            throw ValidationException("MISSING_GOOGLE_TOKEN", "Google ID token is required.")
        }

        val payload = verifyGoogleIdToken(request.idToken)
        val providerId = payload.subject
            ?: throw AuthException("INVALID_GOOGLE_TOKEN", "Google token does not contain a subject.")
        val email = payload.email?.trim()?.lowercase(Locale.US)
            ?: throw AuthException("INVALID_GOOGLE_TOKEN", "Google token does not contain an email.")

        if (payload.emailVerified != true) {
            throw AuthException("GOOGLE_EMAIL_NOT_VERIFIED", "Google email has not been verified.")
        }

        request.nonce
            ?.takeIf { it.isNotBlank() }
            ?.let { expectedNonce ->
                val actualNonce = payload["nonce"] as? String
                if (actualNonce != expectedNonce) {
                    throw AuthException("INVALID_GOOGLE_NONCE", "Google token nonce is invalid.")
                }
            }

        val fullName = (payload["name"] as? String)
            ?.takeIf { it.isNotBlank() }
            ?: email.substringBefore("@")
        val pictureUrl = payload["picture"] as? String

        var user = userRepository.getUserByProvider(AuthProvider.GOOGLE, providerId)

        if (user != null) {
            userRepository.linkGoogleIdentity(
                userId = user.id,
                providerId = providerId,
                fullName = fullName,
                profilePictureUrl = pictureUrl,
                userType = request.userType,
            )
            user = userRepository.getUserById(user.id)
        } else {
            val existingUser = userRepository.getUserByEmail(email)
            user = if (existingUser != null) {
                if (!existingUser.isActive) {
                    throw AuthException("ACCOUNT_LOCKED", "Your account has been locked or suspended.")
                }

                userRepository.linkGoogleIdentity(
                    userId = existingUser.id,
                    providerId = providerId,
                    fullName = fullName,
                    profilePictureUrl = pictureUrl,
                    userType = request.userType,
                )
                userRepository.getUserById(existingUser.id)
            } else {
                val username = createUniqueGoogleUsername(email)
                val generatedPassword = PasswordHasher.hashPassword(UUID.randomUUID().toString())
                userRepository.createGoogleUser(
                    email = email,
                    username = username,
                    fullName = fullName,
                    profilePictureUrl = pictureUrl,
                    providerId = providerId,
                    hashedPw = generatedPassword,
                    userType = request.userType,
                )
            }
        }

        val activeUser = user ?: throw Exception("Failed to create or link Google user.")
        if (!activeUser.isActive) {
            throw AuthException("ACCOUNT_LOCKED", "Your account has been locked or suspended.")
        }

        return createLoginResponse(activeUser, deviceInfo, ipAddress)
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]+$".toRegex()
        return email.matches(emailRegex)
    }

    private suspend fun createLoginResponse(
        user: UserDto,
        deviceInfo: String?,
        ipAddress: String?,
    ): LoginResponse {
        val token = jwtManager.generateToken(user)
        val refreshToken = UUID.randomUUID().toString()
        val expiredAt = java.time.LocalDateTime.now().plusDays(30)

        sessionRepository.createSession(user.id, deviceInfo, ipAddress, refreshToken, expiredAt)

        return LoginResponse(
            userId = user.id,
            email = user.email,
            username = user.username,
            fullName = user.fullName,
            profilePictureUrl = user.profilePictureUrl,
            role = user.role,
            userType = user.userType,
            token = token,
            refreshToken = refreshToken
        )
    }

    private suspend fun verifyGoogleIdToken(idToken: String): GoogleIdToken.Payload {
        val clientId = googleWebClientId()
        val verifier = GoogleIdTokenVerifier.Builder(NetHttpTransport(), GsonFactory.getDefaultInstance())
            .setAudience(listOf(clientId))
            .build()

        val googleIdToken = withContext(Dispatchers.IO) {
            verifier.verify(idToken)
        } ?: throw AuthException("INVALID_GOOGLE_TOKEN", "Google token is invalid or expired.")

        return googleIdToken.payload
    }

    private fun googleWebClientId(): String {
        val configuredClientId = config.propertyOrNull("google.webClientId")
            ?.getString()
            ?.takeIf { it.isNotBlank() }
        val envClientId = System.getenv("GOOGLE_WEB_CLIENT_ID")
            ?.takeIf { it.isNotBlank() }

        return configuredClientId ?: envClientId
            ?: throw ValidationException(
                "GOOGLE_NOT_CONFIGURED",
                "GOOGLE_WEB_CLIENT_ID is not configured on the backend."
            )
    }

    private suspend fun createUniqueGoogleUsername(email: String): String {
        val normalized = Normalizer.normalize(email.substringBefore("@"), Normalizer.Form.NFD)
            .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
            .lowercase(Locale.US)
            .replace("[^a-z0-9_]+".toRegex(), "_")
            .trim { it == '_' }
            .ifBlank { "google_user" }

        val base = normalized.take(40)
        var candidate = base
        var suffix = 1

        while (userRepository.getUserByUsername(candidate) != null) {
            val suffixText = suffix.toString()
            candidate = "${base.take(45 - suffixText.length)}$suffixText"
            suffix++
        }

        return candidate
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

    suspend fun requestPasswordReset(email: String): ForgotPasswordStartResponse {
        val user = userRepository.getUserByEmail(email)
            ?: return ForgotPasswordStartResponse() // Avoid email enumeration attacks

        val token = generateResetCode()
        val expiredAt = java.time.LocalDateTime.now().plusMinutes(15)

        passwordResetRepository.deleteTokensByUserId(user.id)
        passwordResetRepository.saveToken(user.id, token, expiredAt)

        println("[PASSWORD RESET] To: ${user.email} | Reset Code: $token | Expires: $expiredAt")

        return ForgotPasswordStartResponse(
            debugResetToken = token.takeIf { shouldExposeDebugResetToken() }
        )
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

    private fun generateResetCode(): String =
        secureRandom.nextInt(1_000_000).toString().padStart(6, '0')

    private fun shouldExposeDebugResetToken(): Boolean {
        val configured = config.propertyOrNull("auth.exposeDebugResetToken")
            ?.getString()
            ?.toBooleanStrictOrNull()
        val env = System.getenv("EXPOSE_DEBUG_RESET_TOKEN")
            ?.toBooleanStrictOrNull()
        return configured ?: env ?: false
    }
}
