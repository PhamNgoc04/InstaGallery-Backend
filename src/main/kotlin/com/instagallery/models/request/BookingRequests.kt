package com.instagallery.models.request

import com.instagallery.models.common.BookingStatus
import kotlinx.serialization.Serializable

@Serializable
data class CreateBookingRequest(
    val photographerId: Long,
    val serviceId: Long? = null,
    val packageName: String? = null,
    val shootingType: String? = null,
    val sceneType: String? = null,
    val bookingDate: String, // ISO 8601
    val durationHours: Double? = null,
    val locationBooking: String? = null,
    val addressDetail: String? = null,
    val details: String? = null,
    val peopleCount: Int? = null,
    val contactPhone: String? = null,
    val addOns: List<String> = emptyList(),
    val referenceImages: List<String> = emptyList(),
    val price: Double? = null,
    val currency: String = "VND"
)

@Serializable
data class UpdateBookingStatusRequest(
    val status: BookingStatus,
    val cancellationReason: String? = null
)
