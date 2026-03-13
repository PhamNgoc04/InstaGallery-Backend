package com.instagallery.repositories

import com.instagallery.database.DatabaseFactory.dbQuery
import com.instagallery.database.tables.NotificationsTable
import com.instagallery.database.tables.UsersTable
import com.instagallery.models.common.NotificationDto
import com.instagallery.models.common.NotificationPaginationMeta
import com.instagallery.models.common.PaginatedNotificationsResponse
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class NotificationRepository {

    suspend fun getNotifications(userId: Long, page: Int, limit: Int): PaginatedNotificationsResponse = dbQuery {
        val offsetVal = ((page - 1) * limit).toLong()

        // Count unread
        val unreadCount = NotificationsTable
            .selectAll().where { (NotificationsTable.userId eq userId) and (NotificationsTable.isRead eq false) }
            .count()
            .toInt()

        val query = NotificationsTable
            .selectAll().where { NotificationsTable.userId eq userId }
            .orderBy(NotificationsTable.createdAt to SortOrder.DESC)

        val totalRecords = query.count()
        val totalPages = Math.ceil(totalRecords.toDouble() / limit).toInt()

        val notiRows = query.limit(limit, offsetVal).toList()

        val notificationsList = notiRows.map { row ->
            val senderIdVal = row[NotificationsTable.senderId]?.value
            var senderName: String? = null
            var senderAvatar: String? = null
            
            if (senderIdVal != null) {
                val userRow = UsersTable.selectAll().where { UsersTable.id eq senderIdVal }.singleOrNull()
                senderName = userRow?.get(UsersTable.fullName)
                senderAvatar = userRow?.get(UsersTable.profilePictureUrl)
            }

            NotificationDto(
                notificationId = row[NotificationsTable.id].value,
                type = row[NotificationsTable.type],
                senderId = senderIdVal,
                senderName = senderName,
                senderAvatar = senderAvatar,
                title = row[NotificationsTable.title],
                body = row[NotificationsTable.body],
                targetType = row[NotificationsTable.targetType],
                targetId = row[NotificationsTable.targetId],
                isRead = row[NotificationsTable.isRead],
                createdAt = row[NotificationsTable.createdAt].toString()
            )
        }

        PaginatedNotificationsResponse(
            notifications = notificationsList,
            meta = NotificationPaginationMeta(
                currentPage = page,
                totalPages = totalPages,
                unreadCount = unreadCount
            )
        )
    }

    suspend fun markAsRead(userId: Long, notificationId: Long): Boolean = dbQuery {
        val rows = NotificationsTable.update({ (NotificationsTable.id eq notificationId) and (NotificationsTable.userId eq userId) }) {
            it[isRead] = true
        }
        rows > 0
    }

    suspend fun markAllAsRead(userId: Long): Boolean = dbQuery {
        val rows = NotificationsTable.update({ (NotificationsTable.userId eq userId) and (NotificationsTable.isRead eq false) }) {
            it[isRead] = true
        }
        rows > 0
    }
}
