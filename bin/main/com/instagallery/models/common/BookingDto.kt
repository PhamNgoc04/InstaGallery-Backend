package com.instagallery.models.common

import kotlinx.serialization.Serializable

@Serializable
data class BookingDto(
    val bookingId: Long,
    val partnerId: Long,
    val partnerName: String,
    val bookingDate: String,
    val status: BookingStatus,
    val price: Double?,
    val currency: String,
    val locationBooking: String?,
    val details: String?,
    val durationHours: Double?,
    val createdAt: String
)

@Serializable
data class PaginatedBookingsResponse(
    val bookings: List<BookingDto>,
    val meta: PaginationMeta
)
