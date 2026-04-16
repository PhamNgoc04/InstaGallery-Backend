package com.instagallery.models.request

import kotlinx.serialization.Serializable

@Serializable
data class BanUserRequest(
    val isBanned: Boolean,
    val reason: String? = null
)
