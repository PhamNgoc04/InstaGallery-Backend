package com.instagallery.models.request

import com.instagallery.models.common.MediaType
import kotlinx.serialization.Serializable

@Serializable
data class PresignedUrlRequest(
    val fileName: String,
    val contentType: String,
    val folder: String? = "posts"
)

@Serializable
data class AddPostMediaRequest(
    val mediaFileUrl: String,
    val thumbnailUrl: String? = null,
    val mediaType: MediaType = MediaType.IMAGE,
    val filterId: Long? = null,
    val width: Int? = null,
    val height: Int? = null,
    val fileSize: Long? = null,
    val duration: Int? = null,
    val metadata: String? = null
)

@Serializable
data class ReorderMediaRequest(
    val mediaIds: List<Long>
)
