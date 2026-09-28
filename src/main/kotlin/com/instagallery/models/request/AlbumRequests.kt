package com.instagallery.models.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateAlbumRequest(
    val title: String,
    val description: String? = null,
    val coverImageUrl: String? = null,
    val isPrivate: Boolean = false,
)

@Serializable
data class UpdateAlbumRequest(
    val title: String? = null,
    val description: String? = null,
    val coverImageUrl: String? = null,
    val isPrivate: Boolean? = null,
)

@Serializable
data class AddAlbumMediaRequest(
    val postIds: List<Long> = emptyList(),
)
