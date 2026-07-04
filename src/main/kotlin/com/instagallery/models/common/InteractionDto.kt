package com.instagallery.models.common

import kotlinx.serialization.Serializable

@Serializable
data class ToggleLikeResponse(
    val isLiked: Boolean,
    val totalLikes: Int
)

@Serializable
data class ToggleSaveResponse(
    val isSaved: Boolean
)

@Serializable
data class PostLikeUserDto(
    val userId: Long,
    val username: String,
    val fullName: String,
    val avatar: String?
)

@Serializable
data class PostLikesResponse(
    val users: List<PostLikeUserDto>,
    val meta: PaginationMeta
)

@Serializable
data class CommentDto(
    val commentId: Long,
    val postId: Long,
    val userId: Long,
    val username: String,
    val avatar: String?,
    val content: String,
    val parentId: Long?,
    val replyCount: Int,
    val createdAt: String,
    val replies: List<CommentDto>? = null
)

@Serializable
data class PaginatedCommentsResponse(
    val comments: List<CommentDto>,
    val meta: PaginationMeta
)
