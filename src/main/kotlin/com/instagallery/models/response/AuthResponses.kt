package com.instagallery.models.response

import com.instagallery.models.common.Role
import com.instagallery.models.common.UserType
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val userId: Long,
    val email: String,
    val username: String,
    val fullName: String,
    val profilePictureUrl: String?,
    val role: Role,
    val userType: UserType,
    val token: String,
    val refreshToken: String
)

@Serializable
data class RegisterResponse(
    val userId: Long,
    val email: String,
    val username: String,
    val role: Role,
    val userType: UserType,
    val token: String,
    val refreshToken: String
)

@Serializable
data class TokenRefreshResponse(
    val token: String,
    val refreshToken: String,
    val expiresAt: Long
)

@Serializable
data class ForgotPasswordStartResponse(
    val debugResetToken: String? = null
)

@Serializable
data class SessionDto(
    val id: Long,
    val deviceInfo: String?,
    val ipAddress: String?,
    val createdAt: String,
    val expiredAt: String,
    val isCurrent: Boolean
)

@Serializable
data class UserSessionsResponse(
    val sessions: List<SessionDto>
)
