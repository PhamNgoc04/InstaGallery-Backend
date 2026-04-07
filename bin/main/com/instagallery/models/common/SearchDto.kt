package com.instagallery.models.common

import kotlinx.serialization.Serializable

@Serializable
data class GlobalSearchResponse(
    val users: List<UserSearchDto>,
    val posts: List<PostSearchDto>
)

@Serializable
data class UserSearchDto(
    val userId: Long,
    val fullName: String,
    val username: String,
    val avatar: String?,
    val role: String,
    val userType: String
)

@Serializable
data class PostSearchDto(
    val postId: Long,
    val caption: String?,
    val coverImage: String?,
    val likeCount: Int
)

@Serializable
data class SearchHistoryResponse(
    val hits: List<String>
)
