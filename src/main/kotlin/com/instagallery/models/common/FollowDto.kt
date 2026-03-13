package com.instagallery.models.common

import kotlinx.serialization.Serializable

@Serializable
data class FollowDto(
    val id: Long,
    val username: String,
    val fullName: String,
    val avatar: String?,
    val role: Role,
    val userType: UserType,
    val isFollowing: Boolean? = null // To know if the current logged-in user follows this user
)

@Serializable
data class PaginatedFollowsResponse(
    val users: List<FollowDto>,
    val meta: FollowPaginationMeta
)

@Serializable
data class FollowPaginationMeta(
    val currentPage: Int,
    val totalPages: Int,
    val totalRecords: Int
)
