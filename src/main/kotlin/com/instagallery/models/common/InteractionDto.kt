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
data class CommentReactionResponse(
    val isLiked: Boolean,
    val isDisliked: Boolean,
    val likeCount: Int,
    val dislikeCount: Int
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
    val likeCount: Int = 0,
    val dislikeCount: Int = 0,
    val isLiked: Boolean = false,
    val isDisliked: Boolean = false,
    val replyCount: Int,
    val createdAt: String,
    val replies: List<CommentDto>? = null
)

@Serializable
data class PaginatedCommentsResponse(
    val comments: List<CommentDto>,
    val meta: PaginationMeta
)

@Serializable
data class UserCommentActivityDto(
    val commentId: Long,
    val postId: Long,
    val postAuthorUsername: String,
    val postAuthorAvatar: String?,
    val postCaption: String?,
    val postThumbnailUrl: String?,
    val content: String,
    val createdAt: String
)

@Serializable
data class PaginatedUserCommentsResponse(
    val comments: List<UserCommentActivityDto>,
    val meta: PaginationMeta
)
