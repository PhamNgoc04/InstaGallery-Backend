package com.instagallery.models.common

import kotlinx.serialization.Serializable

@Serializable
data class TrendingTagDto(
    val name: String,
    val postCount: Int
)
