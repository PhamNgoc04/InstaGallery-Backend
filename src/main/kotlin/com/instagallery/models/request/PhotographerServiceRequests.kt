package com.instagallery.models.request

import kotlinx.serialization.Serializable

@Serializable
data class UpsertPhotographerServiceRequest(
    val name: String,
    val category: String,
    val price: Double,
    val currency: String = "VND",
    val durationMinutes: Int,
    val photoCount: Int? = null,
    val editedPhotoCount: Int? = null,
    val makeupIncluded: Boolean = false,
    val outfitIncluded: Boolean = false,
    val locationSupport: Boolean = true,
    val description: String? = null,
    val includes: List<String> = emptyList(),
    val coverUrl: String? = null,
    val isActive: Boolean = true,
)

@Serializable
data class UpdatePhotographerServiceStatusRequest(
    val isActive: Boolean,
)
