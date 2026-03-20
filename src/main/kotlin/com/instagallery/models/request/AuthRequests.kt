package com.instagallery.models.request

import com.instagallery.models.common.UserType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class RegisterRequest(
    val email: String,
    val username: String,
    @JsonNames("password") val passwordHash: String, // From frontend, will be hashed again or validated
    val fullName: String,
    val userType: UserType = UserType.CLIENT
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class LoginRequest(
    @JsonNames("usernameOrEmail") val email: String,
    @JsonNames("password") val passwordHash: String // From frontend
)

@Serializable
data class ForgotPasswordRequest(val email: String)

@Serializable
data class ResetPasswordRequest(val resetToken: String, val newPasswordHash: String)

@Serializable
data class ChangePasswordRequest(val oldPasswordHash: String, val newPasswordHash: String)
