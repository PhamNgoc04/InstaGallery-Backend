package com.instagallery.services

import com.instagallery.models.common.PaginatedNotificationsResponse
import com.instagallery.repositories.NotificationRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NotificationService : KoinComponent {
    private val notificationRepo: NotificationRepository by inject()

    suspend fun getMyNotifications(userId: Long, page: Int, limit: Int): PaginatedNotificationsResponse {
        val verifiedPage = if (page < 1) 1 else page
        val verifiedLimit = if (limit < 1) 20 else if (limit > 50) 50 else limit

        return notificationRepo.getNotifications(userId, verifiedPage, verifiedLimit)
    }

    suspend fun markAsRead(userId: Long, notificationId: Long) {
        // Will silently ignore if notification doesn't exist or isn't owned by user
        notificationRepo.markAsRead(userId, notificationId)
    }

    suspend fun markAllAsRead(userId: Long) {
        notificationRepo.markAllAsRead(userId)
    }
}
