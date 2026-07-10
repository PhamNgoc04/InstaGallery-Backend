package com.instagallery.models.request

import kotlinx.serialization.Serializable

@Serializable
data class RegisterDeviceTokenRequest(
    val token: String,
    val platform: String = "ANDROID",
    val deviceId: String? = null,
    val appVersion: String? = null,
)
