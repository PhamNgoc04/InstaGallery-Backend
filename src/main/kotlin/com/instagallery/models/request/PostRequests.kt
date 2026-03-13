package com.instagallery.models.request

import com.instagallery.models.common.PostVisibility
import kotlinx.serialization.Serializable

@Serializable
data class CreatePostRequest(
    val caption: String? = null,
    val location: String? = null,
    val visibility: PostVisibility = PostVisibility.PUBLIC,
    val mediaIds: List<Long>,
    val tags: List<String>? = null
)
