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
