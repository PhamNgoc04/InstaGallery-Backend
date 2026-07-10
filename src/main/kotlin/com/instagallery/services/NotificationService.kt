package com.instagallery.services

import com.instagallery.models.common.NotificationDto
import com.instagallery.models.common.NotificationSocketEvent
import com.instagallery.models.common.NotificationTargetType
import com.instagallery.models.common.NotificationType
import com.instagallery.models.common.PaginatedNotificationsResponse
import com.instagallery.repositories.NotificationRepository
import com.instagallery.utils.ConnectionManager
import io.ktor.websocket.Frame
import io.ktor.websocket.send
import kotlinx.coroutines.isActive
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NotificationService : KoinComponent {
    private val notificationRepo: NotificationRepository by inject()
    private val firebasePushService: FirebasePushService by inject()

    suspend fun getMyNotifications(userId: Long, page: Int, limit: Int): PaginatedNotificationsResponse {
        val verifiedPage = if (page < 1) 1 else page
        val verifiedLimit = if (limit < 1) 20 else if (limit > 50) 50 else limit

        return notificationRepo.getNotifications(userId, verifiedPage, verifiedLimit)
    }

    suspend fun createNotification(
        recipientUserId: Long,
        actorUserId: Long?,
        type: NotificationType,
        targetType: NotificationTargetType?,
        targetId: Long?,
        title: String? = null,
        body: String? = null,
        dedupe: Boolean = false,
    ): NotificationDto? {
        if (actorUserId != null && actorUserId == recipientUserId) return null

        val notification = notificationRepo.createNotification(
            userId = recipientUserId,
            senderId = actorUserId,
            type = type,
            targetType = targetType,
            targetId = targetId,
            title = title,
            body = body,
            dedupe = dedupe,
        ) ?: return null

        pushRealtime(recipientUserId, notification)
        firebasePushService.sendNotification(recipientUserId, notification)
        return notification
    }

    suspend fun notifyPostLiked(recipientUserId: Long, actorUserId: Long, postId: Long) {
        createNotification(
            recipientUserId = recipientUserId,
            actorUserId = actorUserId,
            type = NotificationType.NEW_LIKE,
            targetType = NotificationTargetType.POST,
            targetId = postId,
            dedupe = true,
        )
    }

    suspend fun notifyPostCommented(recipientUserId: Long, actorUserId: Long, postId: Long) {
        createNotification(
            recipientUserId = recipientUserId,
            actorUserId = actorUserId,
            type = NotificationType.NEW_COMMENT,
            targetType = NotificationTargetType.POST,
            targetId = postId,
        )
    }

    suspend fun notifyPostSaved(recipientUserId: Long, actorUserId: Long, postId: Long) {
        createNotification(
            recipientUserId = recipientUserId,
            actorUserId = actorUserId,
            type = NotificationType.POST_SAVED,
            targetType = NotificationTargetType.POST,
            targetId = postId,
            dedupe = true,
        )
    }

    suspend fun notifyCommentLiked(recipientUserId: Long, actorUserId: Long, commentId: Long) {
        createNotification(
            recipientUserId = recipientUserId,
            actorUserId = actorUserId,
            type = NotificationType.COMMENT_LIKED,
            targetType = NotificationTargetType.COMMENT,
            targetId = commentId,
            dedupe = true,
        )
    }

    suspend fun notifyUserFollowed(recipientUserId: Long, actorUserId: Long) {
        createNotification(
            recipientUserId = recipientUserId,
            actorUserId = actorUserId,
            type = NotificationType.NEW_FOLLOWER,
            targetType = NotificationTargetType.USER,
            targetId = actorUserId,
            dedupe = true,
        )
    }

    suspend fun notifyFollowedUserPosted(recipientUserId: Long, actorUserId: Long, postId: Long) {
        createNotification(
            recipientUserId = recipientUserId,
            actorUserId = actorUserId,
            type = NotificationType.NEW_POST,
            targetType = NotificationTargetType.POST,
            targetId = postId,
            dedupe = true,
        )
    }

    suspend fun notifyBookingCreated(recipientUserId: Long, actorUserId: Long, bookingId: Long) {
        createNotification(
            recipientUserId = recipientUserId,
            actorUserId = actorUserId,
            type = NotificationType.BOOKING_REQUEST,
            targetType = NotificationTargetType.BOOKING,
            targetId = bookingId,
            title = "Yêu cầu đặt lịch mới",
            body = "Bạn có một booking mới đang chờ xác nhận.",
        )
    }

    suspend fun notifyBookingStatusChanged(
        recipientUserId: Long,
        actorUserId: Long,
        bookingId: Long,
        statusLabel: String,
        type: NotificationType,
    ) {
        createNotification(
            recipientUserId = recipientUserId,
            actorUserId = actorUserId,
            type = type,
            targetType = NotificationTargetType.BOOKING,
            targetId = bookingId,
            title = "Cập nhật booking",
            body = "Booking của bạn đã được cập nhật sang trạng thái $statusLabel.",
        )
    }

    suspend fun notifyMessageReceived(
        recipientUserId: Long,
        actorUserId: Long,
        conversationId: Long,
        preview: String,
    ) {
        createNotification(
            recipientUserId = recipientUserId,
            actorUserId = actorUserId,
            type = NotificationType.NEW_MESSAGE,
            targetType = NotificationTargetType.CONVERSATION,
            targetId = conversationId,
            title = "Tin nhắn mới",
            body = preview.ifBlank { "Bạn có tin nhắn mới." }.take(160),
        )
    }

    suspend fun markAsRead(userId: Long, notificationId: Long) {
        // Will silently ignore if notification doesn't exist or isn't owned by user
        notificationRepo.markAsRead(userId, notificationId)
    }

    suspend fun markAllAsRead(userId: Long) {
        notificationRepo.markAllAsRead(userId)
    }

    // --- FR-42: UNREAD COUNT ---
    suspend fun getUnreadCount(userId: Long): Long {
        return notificationRepo.getUnreadCount(userId)
    }

    // --- FR-42: DELETE NOTIFICATION ---
    suspend fun deleteNotification(userId: Long, notificationId: Long) {
        notificationRepo.deleteNotification(userId, notificationId)
    }

    private suspend fun pushRealtime(userId: Long, notification: NotificationDto) {
        val session = ConnectionManager.getSession(userId) ?: return
        if (!session.isActive) {
            ConnectionManager.removeSession(userId)
            return
        }

        val payload = json.encodeToString(NotificationSocketEvent(NOTIFICATION_EVENT, notification))
        runCatching {
            session.send(Frame.Text(payload))
        }.onFailure {
            ConnectionManager.removeSession(userId)
        }
    }

    private companion object {
        const val NOTIFICATION_EVENT = "NOTIFICATION_CREATED"
        val json = Json { encodeDefaults = false }
    }
}
