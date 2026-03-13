package com.instagallery.models.request

import kotlinx.serialization.Serializable

@Serializable
data class UpdatePortfolioRequest(
    val bioProfessional: String?,
    val hourlyRate: Double?,
    val specialties: String?,
    val equipment: String?,
    val location: String?
)

@Serializable
data class CreateRatingRequest(
    val score: Int,
    val comment: String? = null
)
