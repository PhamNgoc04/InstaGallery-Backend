package com.instagallery.repositories

import com.instagallery.database.DatabaseFactory.dbQuery
import com.instagallery.database.tables.UsersTable
import com.instagallery.models.common.Role
import com.instagallery.models.common.UserDto
import com.instagallery.models.common.UserType
import com.instagallery.models.request.RegisterRequest
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.and

class UserRepository {

    suspend fun createUser(request: RegisterRequest, hashedPw: String): UserDto? = dbQuery {
        val insertStatement = UsersTable.insertAndGetId {
            it[email] = request.email
            it[username] = request.username
            it[passwordHash] = hashedPw
            it[fullName] = request.fullName
            it[userType] = request.userType
        }
        val resultRow = UsersTable.selectAll().where { UsersTable.id eq insertStatement.value }.singleOrNull()
        resultRow?.toUserDto()
    }

    suspend fun getUserByEmail(email: String): UserDto? = dbQuery {
        UsersTable.selectAll().where { UsersTable.email eq email }.singleOrNull()?.toUserDto()
    }

    suspend fun getUserById(id: Long): UserDto? = dbQuery {
        UsersTable.selectAll().where { UsersTable.id eq id }.singleOrNull()?.toUserDto()
    }

    suspend fun getUserByUsername(username: String): UserDto? = dbQuery {
        UsersTable.selectAll().where { UsersTable.username eq username }.singleOrNull()?.toUserDto()
    }

    suspend fun updateUserProfile(userId: Long, request: com.instagallery.models.request.UpdateUserProfileRequest, formattedDate: java.time.LocalDate?): Boolean = dbQuery {
        val updatedRows = UsersTable.update({ UsersTable.id eq userId }) {
            request.fullName?.let { name -> it[fullName] = name }
            request.profilePictureUrl?.let { url -> it[profilePictureUrl] = url }
            request.bio?.let { b -> it[bio] = b }
            request.website?.let { web -> it[website] = web }
            request.gender?.let { gen -> it[gender] = gen }
            request.phoneNumber?.let { phone -> it[phoneNumber] = phone }
            request.location?.let { loc -> it[location] = loc }
            formattedDate?.let { date -> it[dateOfBirth] = date }
            
            it[updatedAt] = java.time.Instant.now()
        }
        updatedRows > 0
    }

    suspend fun updatePassword(userId: Long, newHashedPw: String): Boolean = dbQuery {
        val updatedRows = UsersTable.update({ UsersTable.id eq userId }) {
            it[passwordHash] = newHashedPw
            it[updatedAt] = java.time.Instant.now()
        }
        updatedRows > 0
    }

    private fun ResultRow.toUserDto() = UserDto(
        id = this[UsersTable.id].value,
        username = this[UsersTable.username],
        email = this[UsersTable.email],
        passwordHash = this[UsersTable.passwordHash],
        fullName = this[UsersTable.fullName],
        profilePictureUrl = this[UsersTable.profilePictureUrl],
        role = this[UsersTable.role],
        userType = this[UsersTable.userType],
        isActive = this[UsersTable.isActive],
        isVerified = this[UsersTable.isVerified]
    )

    suspend fun getSuggestedUsers(userId: Long, limit: Int): List<UserDto> = dbQuery {
        // Recommend users that the current user hasn't followed yet and are active
        // For simplicity, we just fetch random active users excluding the current user
        // Using `Random` sort order for mock logic. Real system usually counts interactions.
        UsersTable
            .selectAll().where { (UsersTable.id neq userId) and (UsersTable.isActive eq true) }
            .orderBy(org.jetbrains.exposed.sql.Random())
            .limit(limit)
            .map { it.toUserDto() }
    }
}
