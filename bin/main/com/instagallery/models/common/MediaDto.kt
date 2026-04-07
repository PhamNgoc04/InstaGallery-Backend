package com.instagallery.models.common

import kotlinx.serialization.Serializable

@Serializable
data class PresignedUrlResponse(
    val uploadUrl: String,
    val fileUrl: String,
    val expiresIn: Int
)

@Serializable
data class MediaDto(
    val id: Long,
    val postId: Long,
    val mediaFileUrl: String,
    val thumbnailUrl: String?,
    val mediaType: MediaType,
    val position: Int,
    val filterId: Long?,
    val width: Int?,
    val height: Int?,
    val score: Long? = null,
    val duration: Int?,
    val createdAt: String
)
