package com.instagallery.repositories

import com.instagallery.database.DatabaseFactory.dbQuery
import com.instagallery.database.tables.FollowersTable
import com.instagallery.database.tables.PostsTable
import com.instagallery.database.tables.UsersTable
import com.instagallery.models.common.AuthProvider
import com.instagallery.models.common.Role
import com.instagallery.models.common.UserDto
import com.instagallery.models.common.UserProfileDto
import com.instagallery.models.common.UserType
import com.instagallery.models.request.RegisterRequest
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull

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

    suspend fun getUserByProvider(provider: AuthProvider, providerId: String): UserDto? = dbQuery {
        UsersTable.selectAll()
            .where { (UsersTable.provider eq provider) and (UsersTable.providerId eq providerId) }
            .singleOrNull()
            ?.toUserDto()
    }

    suspend fun getUserById(id: Long): UserDto? = dbQuery {
        UsersTable.selectAll().where { UsersTable.id eq id }.singleOrNull()?.toUserDto()
    }

    suspend fun getUserProfile(
        userId: Long,
        viewerId: Long?,
        includePrivateEmail: Boolean,
    ): UserProfileDto? = dbQuery {
        val row = UsersTable
            .selectAll()
            .where { (UsersTable.id eq userId) and UsersTable.deletedAt.isNull() }
            .singleOrNull() ?: return@dbQuery null

        val followerCount = FollowersTable
            .selectAll()
            .where { FollowersTable.followingId eq userId }
            .count()
            .toInt()
        val followingCount = FollowersTable
            .selectAll()
            .where { FollowersTable.followerId eq userId }
            .count()
            .toInt()
        val postCount = PostsTable
            .selectAll()
            .where { visibleProfilePostCondition(userId, viewerId) }
            .count()
            .toInt()
        val isFollowing = viewerId
            ?.takeIf { it != userId }
            ?.let { currentViewerId ->
                FollowersTable
                    .selectAll()
                    .where {
                        (FollowersTable.followerId eq currentViewerId) and
                            (FollowersTable.followingId eq userId)
                    }
                    .count() > 0
            }

        row.toUserProfileDto(
            includePrivateEmail = includePrivateEmail,
            followerCount = followerCount,
            followingCount = followingCount,
            postCount = postCount,
            isFollowing = isFollowing,
        )
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

    suspend fun createGoogleUser(
        email: String,
        username: String,
        fullName: String,
        profilePictureUrl: String?,
        providerId: String,
        hashedPw: String,
        userType: UserType,
    ): UserDto? = dbQuery {
        val insertStatement = UsersTable.insertAndGetId {
            it[UsersTable.email] = email
            it[UsersTable.username] = username
            it[UsersTable.passwordHash] = hashedPw
            it[UsersTable.fullName] = fullName
            it[UsersTable.profilePictureUrl] = profilePictureUrl?.take(255)
            it[UsersTable.userType] = userType
            it[UsersTable.provider] = AuthProvider.GOOGLE
            it[UsersTable.providerId] = providerId
            it[UsersTable.isVerified] = true
        }
        UsersTable.selectAll().where { UsersTable.id eq insertStatement.value }.singleOrNull()?.toUserDto()
    }

    suspend fun linkGoogleIdentity(
        userId: Long,
        providerId: String,
        fullName: String?,
        profilePictureUrl: String?,
        userType: UserType,
    ): Boolean = dbQuery {
        val updatedRows = UsersTable.update({ UsersTable.id eq userId }) {
            it[UsersTable.provider] = AuthProvider.GOOGLE
            it[UsersTable.providerId] = providerId
            it[UsersTable.isVerified] = true
            it[UsersTable.userType] = userType
            fullName?.takeIf { name -> name.isNotBlank() }?.let { name -> it[UsersTable.fullName] = name }
            profilePictureUrl?.let { url -> it[UsersTable.profilePictureUrl] = url.take(255) }
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

    private fun ResultRow.toUserProfileDto(
        includePrivateEmail: Boolean,
        followerCount: Int,
        followingCount: Int,
        postCount: Int,
        isFollowing: Boolean?,
    ) = UserProfileDto(
        id = this[UsersTable.id].value,
        username = this[UsersTable.username],
        email = if (includePrivateEmail) this[UsersTable.email] else null,
        fullName = this[UsersTable.fullName],
        profilePictureUrl = this[UsersTable.profilePictureUrl],
        bio = this[UsersTable.bio],
        website = this[UsersTable.website],
        gender = this[UsersTable.gender],
        phoneNumber = this[UsersTable.phoneNumber],
        dateOfBirth = this[UsersTable.dateOfBirth]?.toString(),
        location = this[UsersTable.location],
        role = this[UsersTable.role],
        userType = this[UsersTable.userType],
        isVerified = this[UsersTable.isVerified],
        isPrivate = this[UsersTable.isPrivate],
        followerCount = followerCount,
        followingCount = followingCount,
        postCount = postCount,
        isFollowing = isFollowing,
        createdAt = this[UsersTable.createdAt].toString(),
    )

    private fun visibleProfilePostCondition(
        userId: Long,
        viewerId: Long?,
    ): Op<Boolean> {
        val ownProfile = viewerId == userId
        return if (ownProfile) {
            (PostsTable.userId eq userId) and PostsTable.deletedAt.isNull()
        } else {
            (PostsTable.userId eq userId) and
                PostsTable.deletedAt.isNull() and
                (PostsTable.visibility eq com.instagallery.models.common.PostVisibility.PUBLIC)
        }
    }

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
