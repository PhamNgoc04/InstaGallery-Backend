package com.instagallery.models.common

import kotlinx.serialization.Serializable

@Serializable
data class PhotographerServiceDto(
    val serviceId: Long,
    val photographerId: Long,
    val name: String,
    val category: String,
    val price: Double,
    val currency: String,
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
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

@Serializable
data class PhotographerServicesResponse(
    val services: List<PhotographerServiceDto>,
)

@Serializable
data class BookingPackageSnapshotDto(
    val serviceId: Long? = null,
    val name: String,
    val category: String? = null,
    val price: Double? = null,
    val currency: String = "VND",
    val durationMinutes: Int? = null,
    val photoCount: Int? = null,
    val editedPhotoCount: Int? = null,
    val includes: List<String> = emptyList(),
)
