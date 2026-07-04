package com.instagallery.services

import com.instagallery.models.common.UserDto
import com.instagallery.models.common.UserProfileDto
import com.instagallery.models.request.UpdateUserProfileRequest
import com.instagallery.plugins.AuthException
import com.instagallery.plugins.ValidationException
import com.instagallery.repositories.UserRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDate
import java.time.format.DateTimeParseException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class UserService : KoinComponent {
    private val userRepository: UserRepository by inject()

    suspend fun getCurrentUser(userId: Long): UserDto {
        return userRepository.getUserById(userId)
            ?: throw AuthException("USER_NOT_FOUND", "Tài khoản không tồn tại hoặc đã bị xóa.")
    }

    suspend fun getCurrentUserProfile(userId: Long): UserProfileDto {
        return userRepository.getUserProfile(
            userId = userId,
            viewerId = userId,
            includePrivateEmail = true,
        ) ?: throw AuthException("USER_NOT_FOUND", "Tai khoan khong ton tai hoac da bi xoa.")
    }

    suspend fun getPublicUserProfile(userId: Long): UserProfileDto {
        return userRepository.getUserProfile(
            userId = userId,
            viewerId = null,
            includePrivateEmail = false,
        ) ?: throw AuthException("USER_NOT_FOUND", "Nguoi dung khong ton tai.")
    }

    suspend fun updateProfile(userId: Long, request: UpdateUserProfileRequest): UserProfileDto {
        // Validation: Verify if the user exists
        val currentUser = userRepository.getUserById(userId)
            ?: throw AuthException("USER_NOT_FOUND", "Tài khoản không tồn tại hoặc đã bị xóa.")

        if (!currentUser.isActive) {
            throw AuthException("ACCOUNT_LOCKED", "Tài khoản đang bị khóa và không thể cập nhật.")
        }

        // Date definition parse
        var parsedDate: LocalDate? = null
        if (request.dateOfBirth != null) {
            try {
                parsedDate = LocalDate.parse(request.dateOfBirth)
            } catch (e: DateTimeParseException) {
                throw ValidationException("INVALID_DATE_FORMAT", "Định dạng ngày tháng phải là YYYY-MM-DD.")
            }
        }

        // Action
        val isSuccess = userRepository.updateUserProfile(userId, request, parsedDate)
        if (!isSuccess) {
            throw Exception("Failed to update user profile.")
        }

        return userRepository.getUserProfile(
            userId = userId,
            viewerId = userId,
            includePrivateEmail = true,
        ) ?: throw AuthException("USER_NOT_FOUND", "Account does not exist.")
    }

    suspend fun deactivateAccount(userId: Long) {
        // Use exposed's update method to toggle the boolean
        // Using existing user fetching logic to verify user exists
        val user = userRepository.getUserById(userId)
            ?: throw ValidationException("USER_NOT_FOUND", "Tài khoản không tồn tại.")

        // We assume an updateUserStatus function exists or we can just call updateProfile
        // Since updateProfile in our current repo doesn't map isActive, let's just make a dedicated update call in Repo soon.
        com.instagallery.database.DatabaseFactory.dbQuery {
            com.instagallery.database.tables.UsersTable.update({ com.instagallery.database.tables.UsersTable.id eq userId }) {
                it[isActive] = false
                it[updatedAt] = java.time.Instant.now()
            }
        }
    }

    suspend fun getSuggestedUsers(userId: Long, limit: Int): List<UserDto> {
        // Validation: Verify if the user exists
        val currentUser = userRepository.getUserById(userId)
            ?: throw AuthException("USER_NOT_FOUND", "Tài khoản không tồn tại.")

        val verifiedLimit = if (limit < 1) 10 else if (limit > 50) 50 else limit
        return userRepository.getSuggestedUsers(userId, verifiedLimit)
    }

    // --- FR-09: CẬP NHẬT PRIVACY ---
    suspend fun updatePrivacy(userId: Long, isPrivate: Boolean) {
        userRepository.getUserById(userId)
            ?: throw AuthException("USER_NOT_FOUND", "Tài khoản không tồn tại.")

        com.instagallery.database.DatabaseFactory.dbQuery {
            com.instagallery.database.tables.UsersTable.update({ com.instagallery.database.tables.UsersTable.id eq userId }) {
                it[com.instagallery.database.tables.UsersTable.isPrivate] = isPrivate
                it[updatedAt] = java.time.Instant.now()
            }
        }
    }
}
