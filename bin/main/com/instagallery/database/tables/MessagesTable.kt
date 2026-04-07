package com.instagallery.database.tables

import com.instagallery.models.common.MessageType
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp

object MessagesTable : LongIdTable("messages") {
    val conversationId = reference("conversation_id", ConversationsTable, onDelete = ReferenceOption.CASCADE).index()
    val senderId = reference("sender_id", UsersTable, onDelete = ReferenceOption.CASCADE).index()
    val content = text("content").nullable()
    val messageType = enumerationByName("message_type", 20, MessageType::class).default(MessageType.TEXT)
    val mediaUrl = varchar("media_url", 500).nullable()
    val replyToId = reference("reply_to_id", MessagesTable, onDelete = ReferenceOption.SET_NULL).nullable()
    val isDeleted = bool("is_deleted").default(false)
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)

    init {
        index("idx_conv_created", isUnique = false, conversationId, createdAt)
    }
}
