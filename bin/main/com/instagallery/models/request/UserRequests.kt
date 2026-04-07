package com.instagallery.models.request

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserProfileRequest(
    val fullName: String? = null,
    val profilePictureUrl: String? = null,
    val bio: String? = null,
    val website: String? = null,
    val gender: String? = null,
    val phoneNumber: String? = null,
    val dateOfBirth: String? = null, // Format: YYYY-MM-DD
    val location: String? = null
)
