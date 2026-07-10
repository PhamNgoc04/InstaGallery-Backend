package com.instagallery.services

import com.instagallery.models.common.DeviceTokenDto
import com.instagallery.models.request.RegisterDeviceTokenRequest
import com.instagallery.plugins.ValidationException
import com.instagallery.repositories.DeviceTokenRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DeviceTokenService : KoinComponent {
    private val deviceTokenRepository: DeviceTokenRepository by inject()

    suspend fun registerToken(userId: Long, request: RegisterDeviceTokenRequest): DeviceTokenDto {
        val token = request.token.trim()
        if (token.isBlank()) {
            throw ValidationException("EMPTY_FCM_TOKEN", "FCM token khong duoc de trong.")
        }

        return deviceTokenRepository.upsertToken(
            userId = userId,
            token = token,
            platform = request.platform.trim().ifBlank { "ANDROID" }.uppercase(),
            deviceId = request.deviceId?.trim()?.takeIf { it.isNotBlank() },
            appVersion = request.appVersion?.trim()?.takeIf { it.isNotBlank() },
        )
    }

    suspend fun unregisterToken(userId: Long, request: RegisterDeviceTokenRequest): Boolean {
        val token = request.token.trim()
        if (token.isBlank()) return false
        return deviceTokenRepository.deleteUserToken(userId, token)
    }
}
