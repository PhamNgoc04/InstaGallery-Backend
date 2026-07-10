package com.instagallery.database.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp

object DeviceTokensTable : LongIdTable("device_tokens") {
    val userId = reference("user_id", UsersTable, onDelete = ReferenceOption.CASCADE).index()
    val token = varchar("token", 512).uniqueIndex()
    val platform = varchar("platform", 20).default("ANDROID")
    val deviceId = varchar("device_id", 128).nullable()
    val appVersion = varchar("app_version", 64).nullable()
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    val updatedAt = timestamp("updated_at").defaultExpression(CurrentTimestamp)
    val lastSeenAt = timestamp("last_seen_at").defaultExpression(CurrentTimestamp)
}
