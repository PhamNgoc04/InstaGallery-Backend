package com.instagallery.models.common

import kotlinx.serialization.Serializable

@Serializable
data class AlbumDto(
    val albumId: Long,
    val userId: Long,
    val title: String,
    val description: String?,
    val coverImageUrl: String?,
    val isPrivate: Boolean,
    val mediaCount: Int,
    val createdAt: String,
    val updatedAt: String,
)

@Serializable
data class AlbumDetailDto(
    val albumId: Long,
    val userId: Long,
    val title: String,
    val description: String?,
    val coverImageUrl: String?,
    val isPrivate: Boolean,
    val mediaCount: Int,
    val createdAt: String,
    val updatedAt: String,
    val posts: List<FeedPostDto>,
)

@Serializable
data class FollowActionResponse(
    val isFollowing: Boolean,
    val isRequested: Boolean,
    val status: String,
)

@Serializable
data class FollowRequestDto(
    val requestId: Long,
    val followerId: Long,
    val username: String,
    val fullName: String,
    val avatar: String?,
    val createdAt: String,
)

@Serializable
data class ToggleBlockResponse(
    val isBlocked: Boolean,
)

@Serializable
data class ToggleMuteResponse(
    val isMuted: Boolean,
)

@Serializable
data class ActivityLogDto(
    val id: Long,
    val action: String,
    val targetType: String,
    val targetId: Long?,
    val metadata: String?,
    val createdAt: String,
)

@Serializable
data class PaginatedActivityLogResponse(
    val activities: List<ActivityLogDto>,
    val meta: PaginationMeta,
)
