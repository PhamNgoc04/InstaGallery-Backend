package com.instagallery.models.common

import kotlinx.serialization.Serializable

@Serializable
data class PortfolioDto(
    val id: Long,
    val userId: Long,
    val bioProfessional: String?,
    val hourlyRate: Double?,
    val specialties: String?,
    val equipment: String?,
    val location: String?,
    val rating: Double = 0.0,
    val ratingCount: Int = 0,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class PhotographerDto(
    val userId: Long,
    val username: String,
    val fullName: String,
    val avatar: String?,
    val hourlyRate: Double?,
    val specialties: String?,
    val rating: Double,
    val ratingCount: Int
)

@Serializable
data class PaginatedPhotographersResponse(
    val photographers: List<PhotographerDto>,
    val meta: PaginationMeta
)

@Serializable
data class RatingDto(
    val id: Long,
    val photographerId: Long,
    val reviewerId: Long,
    val reviewerUsername: String,
    val reviewerAvatar: String?,
    val score: Int,
    val comment: String?,
    val createdAt: String
)

@Serializable
data class PaginatedRatingsResponse(
    val ratings: List<RatingDto>,
    val meta: PaginationMeta
)

@Serializable
data class AvailabilityScheduleDto(
    val id: Long? = null,
    val type: AvailabilityType,
    val dayOfWeek: DayOfWeekIso? = null,
    val specificDate: String? = null, // yyyy-MM-dd
    val startTime: String,
    val endTime: String,
    val isBooked: Boolean = false
)
