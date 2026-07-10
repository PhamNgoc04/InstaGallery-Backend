package com.instagallery.repositories

import com.instagallery.database.DatabaseFactory.dbQuery
import com.instagallery.database.tables.NotificationsTable
import com.instagallery.database.tables.UsersTable
import com.instagallery.models.common.NotificationDto
import com.instagallery.models.common.NotificationPaginationMeta
import com.instagallery.models.common.NotificationTargetType
import com.instagallery.models.common.NotificationType
import com.instagallery.models.common.PaginatedNotificationsResponse
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.Instant

class NotificationRepository {

    suspend fun createNotification(
        userId: Long,
        senderId: Long?,
        type: NotificationType,
        targetType: NotificationTargetType?,
        targetId: Long?,
        title: String?,
        body: String?,
        dedupe: Boolean = false,
    ): NotificationDto? = dbQuery {
        if (senderId != null && senderId == userId) return@dbQuery null

        if (dedupe) {
            val existing = NotificationsTable
                .selectAll()
                .where {
                    (NotificationsTable.userId eq userId) and
                        (NotificationsTable.senderId eq senderId) and
                        (NotificationsTable.type eq type) and
                        (NotificationsTable.targetType eq targetType) and
                        (NotificationsTable.targetId eq targetId)
                }
                .singleOrNull()
            if (existing != null) {
                val existingId = existing[NotificationsTable.id].value
                NotificationsTable.update({ NotificationsTable.id eq existingId }) {
                    it[NotificationsTable.title] = title
                    it[NotificationsTable.body] = body
                    it[NotificationsTable.isRead] = false
                    it[NotificationsTable.createdAt] = Instant.now()
                }
                return@dbQuery NotificationsTable
                    .selectAll()
                    .where { NotificationsTable.id eq existingId }
                    .single()
                    .toNotificationDto()
            }
        }

        val notificationId = NotificationsTable.insertAndGetId {
            it[NotificationsTable.userId] = EntityID(userId, UsersTable)
            it[NotificationsTable.senderId] = senderId?.let { actorId -> EntityID(actorId, UsersTable) }
            it[NotificationsTable.type] = type
            it[NotificationsTable.targetType] = targetType
            it[NotificationsTable.targetId] = targetId
            it[NotificationsTable.title] = title
            it[NotificationsTable.body] = body
        }.value

        NotificationsTable
            .selectAll()
            .where { NotificationsTable.id eq notificationId }
            .single()
            .toNotificationDto()
    }

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

        val notificationsList = notiRows.map { row -> row.toNotificationDto() }

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

    suspend fun getUnreadCount(userId: Long): Long = dbQuery {
        NotificationsTable.selectAll()
            .where { (NotificationsTable.userId eq userId) and (NotificationsTable.isRead eq false) }
            .count()
    }

    suspend fun deleteNotification(userId: Long, notificationId: Long): Boolean = dbQuery {
        val rows = NotificationsTable.deleteWhere {
            (NotificationsTable.id eq notificationId) and (NotificationsTable.userId eq userId)
        }
        rows > 0
    }

    private fun ResultRow.toNotificationDto(): NotificationDto {
        val senderIdVal = this[NotificationsTable.senderId]?.value
        var senderName: String? = null
        var senderAvatar: String? = null

        if (senderIdVal != null) {
            val userRow = UsersTable.selectAll().where { UsersTable.id eq senderIdVal }.singleOrNull()
            senderName = userRow?.get(UsersTable.fullName)
            senderAvatar = userRow?.get(UsersTable.profilePictureUrl)
        }

        return NotificationDto(
            notificationId = this[NotificationsTable.id].value,
            type = this[NotificationsTable.type],
            senderId = senderIdVal,
            senderName = senderName,
            senderAvatar = senderAvatar,
            title = this[NotificationsTable.title],
            body = this[NotificationsTable.body],
            targetType = this[NotificationsTable.targetType],
            targetId = this[NotificationsTable.targetId],
            isRead = this[NotificationsTable.isRead],
            createdAt = this[NotificationsTable.createdAt].toString()
        )
    }
}
