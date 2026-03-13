package com.instagallery.models.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateCommentRequest(
    val content: String,
    val parentId: Long? = null
)

@Serializable
data class UpdateCommentRequest(
    val content: String
)
