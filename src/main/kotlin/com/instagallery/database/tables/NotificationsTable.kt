package com.instagallery.database.tables

import com.instagallery.models.common.NotificationTargetType
import com.instagallery.models.common.NotificationType
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp

object NotificationsTable : LongIdTable("notifications") {
    val userId = reference("user_id", UsersTable, onDelete = ReferenceOption.CASCADE).index()
    val senderId = reference("sender_id", UsersTable, onDelete = ReferenceOption.SET_NULL).nullable()
    val type = enumerationByName("type", 30, NotificationType::class)
    val targetType = enumerationByName("target_type", 20, NotificationTargetType::class).nullable()
    val targetId = long("target_id").nullable()
    val title = varchar("title", 255).nullable()
    val body = text("body").nullable()
    val isRead = bool("is_read").default(false)
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp).index()

    init {
        index("idx_user_read", isUnique = false, userId, isRead)
    }
}
