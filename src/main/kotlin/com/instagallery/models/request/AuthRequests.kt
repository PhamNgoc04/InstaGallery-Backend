package com.instagallery.models.request

import com.instagallery.models.common.UserType
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val email: String,
    val username: String,
    val passwordHash: String, // From frontend, will be hashed again or validated
    val fullName: String,
    val userType: UserType = UserType.ENTHUSIAST
)

@Serializable
data class LoginRequest(
    val email: String,
    val passwordHash: String // From frontend
)

@Serializable
data class ForgotPasswordRequest(val email: String)

@Serializable
data class ResetPasswordRequest(val resetToken: String, val newPasswordHash: String)

@Serializable
data class ChangePasswordRequest(val oldPasswordHash: String, val newPasswordHash: String)
