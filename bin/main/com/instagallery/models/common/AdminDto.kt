package com.instagallery.models.common

import kotlinx.serialization.Serializable

@Serializable
data class AdminStatsDto(
    val totalUsers: Long,
    val totalPhotographers: Long,
    val totalPosts: Long,
    val totalBookings: Long,
    val activeReports: Long
)

@Serializable
data class AdminGrowthDto(
    val date: String,
    val count: Long
)
