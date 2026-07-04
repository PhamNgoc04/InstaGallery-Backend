package com.instagallery.models.common

import kotlinx.serialization.Serializable

@Serializable
data class FeedMediaDto(
    val id: Long,
    val url: String,
    val type: String,
    val orderIndex: Int,
    val width: Int? = null,
    val height: Int? = null
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
    val media: List<FeedMediaDto>,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false
)

// --- POST DETAIL ---
@Serializable
data class PostDetailDto(
    val postId: Long,
    val userId: Long,
    val username: String,
    val userAvatar: String?,
    val caption: String?,
    val location: String?,
    val visibility: PostVisibility,
    val likeCount: Int,
    val commentCount: Int,
    val createdAt: String,
    val media: List<FeedMediaDto>,
    val isLiked: Boolean,
    val isSaved: Boolean
)

@Serializable
data class PaginatedFeedResponse(
    val posts: List<FeedPostDto>,
    val meta: PaginationMeta,
    val feedContext: FeedContextDto? = null,
    val suggestedPosts: List<FeedPostDto> = emptyList()
)

@Serializable
data class PaginationMeta(
    val currentPage: Int,
    val totalPages: Int,
    val hasNext: Boolean
)

@Serializable
data class FeedContextDto(
    val mode: String,
    val followCount: Int,
    val reason: String,
    val primaryPostCount: Int,
    val suggestedPostCount: Int
)
