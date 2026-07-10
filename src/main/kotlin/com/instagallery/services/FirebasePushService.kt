package com.instagallery.services

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.AndroidConfig
import com.google.firebase.messaging.AndroidNotification
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingException
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.MessagingErrorCode
import com.google.firebase.messaging.Notification
import com.instagallery.models.common.NotificationDto
import com.instagallery.models.common.NotificationType
import com.instagallery.repositories.DeviceTokenRepository
import io.ktor.server.config.ApplicationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileInputStream

class FirebasePushService : KoinComponent {
    private val config: ApplicationConfig by inject()
    private val deviceTokenRepository: DeviceTokenRepository by inject()
    private val logger = LoggerFactory.getLogger(FirebasePushService::class.java)

    private val firebaseApp: FirebaseApp? by lazy { initializeFirebaseApp() }

    suspend fun sendNotification(userId: Long, notification: NotificationDto) {
        val app = firebaseApp ?: return
        val tokens = deviceTokenRepository.getTokensForUser(userId)
        if (tokens.isEmpty()) return

        val invalidTokens = mutableListOf<String>()
        withContext(Dispatchers.IO) {
            tokens.forEach { token ->
                val message = notification.toFirebaseMessage(token)
                runCatching {
                    FirebaseMessaging.getInstance(app).send(message)
                }.onFailure { error ->
                    if (error is FirebaseMessagingException && error.isInvalidToken()) {
                        invalidTokens += token
                    } else {
                        logger.warn("Failed to send FCM notification ${notification.notificationId}", error)
                    }
                }
            }
        }

        if (invalidTokens.isNotEmpty()) {
            val removed = deviceTokenRepository.deleteTokens(invalidTokens)
            logger.info("Removed $removed invalid FCM token(s)")
        }
    }

    private fun initializeFirebaseApp(): FirebaseApp? {
        if (FirebaseApp.getApps().isNotEmpty()) {
            return FirebaseApp.getInstance()
        }

        return runCatching {
            val configuredPath = config.propertyOrNull("firebase.serviceAccountPath")
                ?.getString()
                ?.trim()
                ?.takeIf { it.isNotBlank() }
            val credentials = if (configuredPath != null) {
                FileInputStream(File(configuredPath)).use(GoogleCredentials::fromStream)
            } else {
                GoogleCredentials.getApplicationDefault()
            }

            FirebaseApp.initializeApp(
                FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .build(),
            )
        }.onSuccess {
            logger.info("Firebase Admin SDK initialized for FCM push notifications")
        }.onFailure { error ->
            logger.warn(
                "FCM push disabled. Set GOOGLE_APPLICATION_CREDENTIALS or firebase.serviceAccountPath to a Firebase service account JSON.",
                error,
            )
        }.getOrNull()
    }

    private fun NotificationDto.toFirebaseMessage(token: String): Message {
        val resolvedTitle = pushTitle()
        val resolvedBody = pushBody()

        return Message.builder()
            .setToken(token)
            .setNotification(
                Notification.builder()
                    .setTitle(resolvedTitle)
                    .setBody(resolvedBody)
                    .build(),
            )
            .setAndroidConfig(
                AndroidConfig.builder()
                    .setPriority(AndroidConfig.Priority.HIGH)
                    .setNotification(
                        AndroidNotification.builder()
                            .setChannelId(CHANNEL_REALTIME)
                            .setIcon(NOTIFICATION_ICON)
                            .build(),
                    )
                    .build(),
            )
            .putAllData(pushData(resolvedTitle, resolvedBody))
            .build()
    }

    private fun NotificationDto.pushData(resolvedTitle: String, resolvedBody: String): Map<String, String> {
        return buildMap {
            put("openNotifications", "true")
            put("notification_id", notificationId.toString())
            put("notificationId", notificationId.toString())
            put("type", type.name)
            put("title", resolvedTitle)
            put("body", resolvedBody)
            senderId?.let { put("sender_id", it.toString()) }
            senderId?.let { put("senderId", it.toString()) }
            senderName?.let { put("sender_name", it) }
            senderName?.let { put("senderName", it) }
            senderAvatar?.let { put("sender_avatar", it) }
            senderAvatar?.let { put("senderAvatar", it) }
            targetType?.let { put("target_type", it.name) }
            targetType?.let { put("targetType", it.name) }
            targetId?.let { put("target_id", it.toString()) }
            targetId?.let { put("targetId", it.toString()) }
            put("created_at", createdAt)
            put("createdAt", createdAt)
        }
    }

    private fun NotificationDto.pushTitle(): String {
        return when (type) {
            NotificationType.NEW_MESSAGE -> "Tin nhắn mới"
            NotificationType.BOOKING_REQUEST,
            NotificationType.BOOKING_CONFIRMED,
            NotificationType.BOOKING_IN_PROGRESS,
            NotificationType.BOOKING_COMPLETED,
            NotificationType.BOOKING_CANCELLED,
            NotificationType.BOOKING_REJECTED -> "Cập nhật đặt lịch"
            else -> title.orEmpty().ifBlank { senderName.orEmpty().ifBlank { "InstaGallery" } }
        }
    }

    private fun NotificationDto.pushBody(): String {
        val displayName = senderName.orEmpty().ifBlank { "InstaGallery" }
        return when (type) {
            NotificationType.NEW_MESSAGE -> "Bạn có tin nhắn mới từ $displayName."
            NotificationType.NEW_LIKE -> "$displayName đã thích bài viết của bạn."
            NotificationType.NEW_COMMENT -> "$displayName đã bình luận về bài viết của bạn."
            NotificationType.NEW_FOLLOWER -> "$displayName đã bắt đầu theo dõi bạn."
            NotificationType.POST_SAVED -> "$displayName đã lưu bài viết của bạn."
            NotificationType.COMMENT_LIKED -> "$displayName đã thích bình luận của bạn."
            NotificationType.NEW_POST -> "$displayName vừa đăng bài viết mới."
            NotificationType.BOOKING_REQUEST -> body ?: "Bạn có yêu cầu đặt lịch mới."
            NotificationType.BOOKING_CONFIRMED,
            NotificationType.BOOKING_IN_PROGRESS,
            NotificationType.BOOKING_COMPLETED,
            NotificationType.BOOKING_CANCELLED,
            NotificationType.BOOKING_REJECTED -> body ?: "Booking của bạn vừa được cập nhật."
            NotificationType.REVIEW_RECEIVED -> "$displayName đã đánh giá buổi chụp."
            NotificationType.MENTION -> "$displayName đã nhắc đến bạn."
            NotificationType.SYSTEM -> body ?: title ?: "Bạn có thông báo mới từ InstaGallery."
        }
    }

    private fun FirebaseMessagingException.isInvalidToken(): Boolean {
        return messagingErrorCode == MessagingErrorCode.UNREGISTERED ||
            messagingErrorCode == MessagingErrorCode.INVALID_ARGUMENT
    }

    private companion object {
        const val CHANNEL_REALTIME = "instagallery_realtime_heads_up_v2"
        const val NOTIFICATION_ICON = "ic_notification_small"
    }
}
