package com.instagallery.database.tables

import com.instagallery.models.common.ConversationType
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp

object ConversationsTable : LongIdTable("conversations") {
    val title = varchar("title", 255).nullable()
    val type = enumerationByName("type", 10, ConversationType::class).default(ConversationType.DIRECT)
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    val updatedAt = timestamp("updated_at").defaultExpression(CurrentTimestamp).index("idx_updated_at")
}
