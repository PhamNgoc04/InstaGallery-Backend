package com.instagallery.models.request

import com.instagallery.models.common.MediaType
import com.instagallery.models.common.PostVisibility
import kotlinx.serialization.Serializable

@Serializable
data class CreateMediaItemRequest(
    val mediaFileUrl: String,
    val thumbnailUrl: String? = null,
    val mediaType: MediaType = MediaType.IMAGE,
    val width: Int? = null,
    val height: Int? = null,
    val duration: Int? = null
)

@Serializable
data class CreatePostRequest(
    val caption: String? = null,
    val location: String? = null,
    val visibility: PostVisibility = PostVisibility.PUBLIC,
    val media: List<CreateMediaItemRequest>,
    val tags: List<String>? = null
)

@Serializable
data class UpdatePostRequest(
    val caption: String? = null,
    val location: String? = null,
    val visibility: PostVisibility? = null,
    val tags: List<String>? = null
)
