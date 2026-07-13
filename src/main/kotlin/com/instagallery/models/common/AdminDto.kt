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

@Serializable
data class AdminUserSummaryDto(
    val userId: Long,
    val username: String,
    val email: String,
    val fullName: String,
    val profilePictureUrl: String? = null,
    val phoneNumber: String? = null,
    val location: String? = null,
    val role: String,
    val isActive: Boolean,
    val isVerified: Boolean,
    val userType: String,
    val followerCount: Int,
    val followingCount: Int,
    val postCount: Int,
    val createdAt: String,
    val specialties: String? = null,
    val ratingAvg: Double? = null,
    val bookingsCount: Long? = null,
    val revenue: Double? = null,
    val isFeatured: Boolean? = null
)

@Serializable
data class AdminUsersResponse(
    val users: List<AdminUserSummaryDto>,
    val total: Long,
    val page: Int,
    val limit: Int
)

@Serializable
data class AdminUserDetailDto(
    val userId: Long,
    val username: String,
    val email: String,
    val fullName: String,
    val profilePictureUrl: String? = null,
    val bio: String? = null,
    val phoneNumber: String? = null,
    val location: String? = null,
    val isActive: Boolean,
    val isPrivate: Boolean,
    val isVerified: Boolean,
    val userType: String,
    val role: String,
    val followerCount: Int,
    val followingCount: Int,
    val postCount: Int,
    val createdAt: String,
    val specialties: String? = null,
    val ratingAvg: Double? = null,
    val bookingsCount: Long? = null,
    val revenue: Double? = null,
    val isFeatured: Boolean? = null,
    val portfolioPhotos: List<String>? = null,
    val servicePackagesCount: Long? = null,
    val reviewsCount: Long? = null
)

@Serializable
data class AdminBannedKeywordDto(
    val id: Long,
    val wordOrRegex: String,
    val keyword: String,
    val isRegex: Boolean,
    val createdAt: String
)

@Serializable
data class AdminMediaDto(
    val id: Long,
    val url: String,
    val thumbnailUrl: String? = null,
    val mediaType: String,
    val position: Int,
    val width: Int? = null,
    val height: Int? = null,
    val fileSize: Long? = null,
    val duration: Int? = null,
    val createdAt: String
)

@Serializable
data class AdminPostDto(
    val id: Long,
    val postId: Long,
    val code: String,
    val authorId: Long,
    val authorName: String,
    val authorUsername: String,
    val authorAvatar: String? = null,
    val caption: String? = null,
    val location: String? = null,
    val visibility: String,
    val likeCount: Int,
    val commentCount: Int,
    val shareCount: Int,
    val reportCount: Long,
    val mediaCount: Int,
    val coverUrl: String? = null,
    val media: List<AdminMediaDto> = emptyList(),
    val status: String,
    val createdAt: String,
    val deletedAt: String? = null
)

@Serializable
data class AdminPostsResponse(
    val posts: List<AdminPostDto>,
    val total: Long,
    val page: Int,
    val limit: Int
)

@Serializable
data class AdminBookingDto(
    val id: Long,
    val bookingId: Long,
    val code: String,
    val clientId: Long,
    val clientName: String,
    val clientAvatarUrl: String? = null,
    val photographerId: Long,
    val photographerName: String,
    val photographerAvatarUrl: String? = null,
    val serviceId: Long? = null,
    val packageName: String? = null,
    val shootingType: String? = null,
    val sceneType: String? = null,
    val bookingDate: String,
    val durationHours: Double? = null,
    val locationBooking: String? = null,
    val addressDetail: String? = null,
    val details: String? = null,
    val peopleCount: Int? = null,
    val contactPhone: String? = null,
    val price: Double? = null,
    val currency: String,
    val status: String,
    val paymentStatus: String,
    val createdAt: String
)

@Serializable
data class AdminBookingsResponse(
    val bookings: List<AdminBookingDto>,
    val total: Long,
    val page: Int,
    val limit: Int
)

@Serializable
data class AdminRatingDto(
    val id: Long,
    val bookingId: Long,
    val photographerId: Long,
    val photographerName: String,
    val photographerAvatarUrl: String? = null,
    val reviewerId: Long,
    val reviewerName: String,
    val reviewerAvatarUrl: String? = null,
    val ratingValue: Int,
    val comment: String? = null,
    val status: String,
    val createdAt: String
)

@Serializable
data class AdminRatingsResponse(
    val ratings: List<AdminRatingDto>,
    val total: Long,
    val page: Int,
    val limit: Int
)

@Serializable
data class AdminMediaItemDto(
    val id: Long,
    val postId: Long,
    val ownerId: Long,
    val ownerName: String,
    val ownerAvatar: String? = null,
    val mediaFileUrl: String,
    val thumbnailUrl: String? = null,
    val mediaType: String,
    val width: Int? = null,
    val height: Int? = null,
    val fileSize: Long? = null,
    val duration: Int? = null,
    val status: String,
    val createdAt: String
)

@Serializable
data class AdminMediaLibraryResponse(
    val media: List<AdminMediaItemDto>,
    val total: Long,
    val page: Int,
    val limit: Int
)

@Serializable
data class AdminNotificationDto(
    val id: Long,
    val userId: Long,
    val userName: String,
    val senderId: Long? = null,
    val senderName: String? = null,
    val type: String,
    val targetType: String? = null,
    val targetId: Long? = null,
    val title: String? = null,
    val body: String? = null,
    val isRead: Boolean,
    val createdAt: String
)

@Serializable
data class AdminNotificationsResponse(
    val notifications: List<AdminNotificationDto>,
    val total: Long,
    val page: Int,
    val limit: Int
)

@Serializable
data class AdminCreateNotificationRequest(
    val title: String,
    val body: String,
    val target: String = "ALL"
)

@Serializable
data class AdminCreateNotificationResponse(
    val sentCount: Int
)

@Serializable
data class AdminPostStatusRequest(
    val status: String
)

@Serializable
data class AdminVerifyUserRequest(
    val isVerified: Boolean
)

@Serializable
data class AdminActivityLogDto(
    val id: Long,
    val userId: Long?,
    val actorName: String,
    val action: String,
    val targetType: String,
    val targetId: Long?,
    val ipAddress: String?,
    val userAgent: String?,
    val metadata: String?,
    val createdAt: String
)

@Serializable
data class AdminActivityLogsResponse(
    val logs: List<AdminActivityLogDto>,
    val total: Long,
    val page: Int,
    val limit: Int
)
