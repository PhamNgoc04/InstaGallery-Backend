package com.instagallery.models.common

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Long,
    val username: String,
    val email: String,
    val passwordHash: String,
    val fullName: String,
    val profilePictureUrl: String?,
    val role: Role,
    val userType: UserType,
    val isActive: Boolean,
    val isVerified: Boolean
)

@Serializable
data class UserProfileDto(
    val id: Long,
    val username: String,
    val email: String? = null,
    val fullName: String,
    val profilePictureUrl: String? = null,
    val bio: String? = null,
    val website: String? = null,
    val gender: String? = null,
    val phoneNumber: String? = null,
    val dateOfBirth: String? = null,
    val location: String? = null,
    val role: Role,
    val userType: UserType,
    val isVerified: Boolean,
    val isPrivate: Boolean,
    val followerCount: Int,
    val followingCount: Int,
    val postCount: Int,
    val isFollowing: Boolean? = null,
    val createdAt: String? = null
)
