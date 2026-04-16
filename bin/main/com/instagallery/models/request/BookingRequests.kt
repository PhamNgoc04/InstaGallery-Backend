package com.instagallery.models.request

import com.instagallery.models.common.BookingStatus
import kotlinx.serialization.Serializable

@Serializable
data class CreateBookingRequest(
    val photographerId: Long,
    val bookingDate: String, // ISO 8601
    val durationHours: Double? = null,
    val locationBooking: String? = null,
    val details: String? = null,
    val price: Double? = null,
    val currency: String = "VND"
)

@Serializable
data class UpdateBookingStatusRequest(
    val status: BookingStatus,
    val cancellationReason: String? = null
)
