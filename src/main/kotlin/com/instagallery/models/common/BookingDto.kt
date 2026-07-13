package com.instagallery.models.common

import kotlinx.serialization.Serializable

@Serializable
data class BookingDto(
    val bookingId: Long,
    val clientId: Long,
    val clientName: String,
    val clientAvatarUrl: String? = null,
    val photographerId: Long,
    val photographerName: String,
    val photographerAvatarUrl: String? = null,
    val partnerId: Long,
    val partnerName: String,
    val partnerAvatarUrl: String? = null,
    val serviceId: Long? = null,
    val packageName: String? = null,
    val packageSnapshot: BookingPackageSnapshotDto? = null,
    val shootingType: String? = null,
    val sceneType: String? = null,
    val bookingDate: String,
    val status: BookingStatus,
    val price: Double?,
    val currency: String,
    val locationBooking: String?,
    val addressDetail: String? = null,
    val details: String?,
    val durationHours: Double?,
    val durationMinutes: Int? = null,
    val peopleCount: Int? = null,
    val contactPhone: String? = null,
    val addOns: List<String> = emptyList(),
    val referenceImages: List<String> = emptyList(),
    val hasReview: Boolean = false,
    val createdAt: String
)

@Serializable
data class PaginatedBookingsResponse(
    val bookings: List<BookingDto>,
    val meta: PaginationMeta
)
