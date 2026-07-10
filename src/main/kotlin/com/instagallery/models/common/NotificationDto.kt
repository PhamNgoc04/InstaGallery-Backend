package com.instagallery.models.common

import kotlinx.serialization.Serializable

@Serializable
data class NotificationDto(
    val notificationId: Long,
    val type: NotificationType,
    val senderId: Long?,
    val senderName: String?,
    val senderAvatar: String?,
    val title: String?,
    val body: String?,
    val targetType: NotificationTargetType?,
    val targetId: Long?,
    val isRead: Boolean,
    val createdAt: String
)

@Serializable
data class PaginatedNotificationsResponse(
    val notifications: List<NotificationDto>,
    val meta: NotificationPaginationMeta
)

@Serializable
data class NotificationSocketEvent(
    val event: String,
    val notification: NotificationDto
)

@Serializable
data class NotificationPaginationMeta(
    val currentPage: Int,
    val totalPages: Int,
    val unreadCount: Int
)
