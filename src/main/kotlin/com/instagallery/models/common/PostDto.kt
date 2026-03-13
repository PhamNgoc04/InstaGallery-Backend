package com.instagallery.models.common

import kotlinx.serialization.Serializable

@Serializable
data class FeedMediaDto(
    val id: Long,
    val url: String,
    val type: String,
    val orderIndex: Int
)

@Serializable
data class PostDto(
    val postId: Long,
    val userId: Long,
    val caption: String?,
    val location: String?,
    val visibility: PostVisibility,
    val likeCount: Int,
    val commentCount: Int,
    val createdAt: String, // ISO format
    val media: List<FeedMediaDto> = emptyList() // Flattened media items
)

@Serializable
data class FeedPostDto(
    val postId: Long,
    val userId: Long,
    val username: String,
    val userAvatar: String?,
    val caption: String?,
    val location: String?,
    val likeCount: Int,
    val commentCount: Int,
    val createdAt: String,
    val media: List<FeedMediaDto>
)

@Serializable
data class PaginatedFeedResponse(
    val posts: List<FeedPostDto>,
    val meta: PaginationMeta
)

@Serializable
data class PaginationMeta(
    val currentPage: Int,
    val totalPages: Int,
    val hasNext: Boolean
)
