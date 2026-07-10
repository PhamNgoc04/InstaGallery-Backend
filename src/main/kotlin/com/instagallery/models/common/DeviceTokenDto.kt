package com.instagallery.models.common

import kotlinx.serialization.Serializable

@Serializable
data class DeviceTokenDto(
    val tokenId: Long,
    val userId: Long,
    val platform: String,
    val deviceId: String?,
    val appVersion: String?,
    val lastSeenAt: String,
)
