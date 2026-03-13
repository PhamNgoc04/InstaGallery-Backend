package com.instagallery.database.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.javatime.timestamp

object PasswordResetTokensTable : LongIdTable("password_reset_tokens") {
    val userId = reference("user_id", UsersTable)
    val token = varchar("token", 255).uniqueIndex() // UUID or hashed string
    val expiredAt = datetime("expired_at") // Using datetime to match Java LocalDateTime
    val createdAt = timestamp("created_at").defaultExpression(org.jetbrains.exposed.sql.javatime.CurrentTimestamp)
}
