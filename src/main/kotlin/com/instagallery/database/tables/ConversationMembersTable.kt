package com.instagallery.database.tables

import com.instagallery.models.common.ConversationRole
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp

object ConversationMembersTable : Table("conversation_members") {
    val conversationId = reference("conversation_id", ConversationsTable, onDelete = ReferenceOption.CASCADE)
    val userId = reference("user_id", UsersTable, onDelete = ReferenceOption.CASCADE).index()
    val role = enumerationByName("role", 10, ConversationRole::class).default(ConversationRole.MEMBER)
    val nickname = varchar("nickname", 50).nullable()
    val isMuted = bool("is_muted").default(false)
    val joinedAt = timestamp("joined_at").defaultExpression(CurrentTimestamp)
    val lastReadAt = timestamp("last_read_at").nullable()

    override val primaryKey = PrimaryKey(conversationId, userId)
}
