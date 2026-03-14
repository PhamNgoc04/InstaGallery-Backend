package com.instagallery.database.tables

import com.instagallery.models.common.ActivityTargetType
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp

object ActivityLogsTable : LongIdTable("activity_logs") {
    val userId = reference("user_id", UsersTable, onDelete = ReferenceOption.SET_NULL).nullable().index()
    val action = varchar("action", 100).index()
    val targetType = enumerationByName("target_type", 20, ActivityTargetType::class)
    val targetId = long("target_id").nullable()
    val ipAddress = varchar("ip_address", 45).nullable()
    val userAgent = varchar("user_agent", 500).nullable()
    val metadata = text("metadata").nullable() // JSON metadata
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp).index()

    init {
        index("idx_activity_target", isUnique = false, targetType, targetId)
    }
}
