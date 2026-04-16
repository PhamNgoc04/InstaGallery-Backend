package com.instagallery.repositories

import com.instagallery.database.DatabaseFactory.dbQuery
import com.instagallery.database.tables.*
import com.instagallery.models.common.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.Instant

class ChatRepository {

    suspend fun getConversationsForUser(userId: Long): ConversationResponse = dbQuery {
        // Find which conversations this user is part of
        val convIds = ConversationMembersTable
            .selectAll().where { ConversationMembersTable.userId eq userId }
            .map { it[ConversationMembersTable.conversationId].value }

        if (convIds.isEmpty()) return@dbQuery ConversationResponse(emptyList())

        // Fetch conversations
        val convRows = ConversationsTable
            .selectAll().where { ConversationsTable.id inList convIds }
            .orderBy(ConversationsTable.updatedAt to SortOrder.DESC)
            .toList()

        val conversationsList = convRows.map { row ->
            val convId = row[ConversationsTable.id].value
            val isDirect = row[ConversationsTable.type] == ConversationType.DIRECT
            
            var partnerId: Long? = null
            var partnerName: String? = null
            var partnerAvatar: String? = null
            
            if (isDirect) {
                // Find the *other* person
                val memberRow = ConversationMembersTable
                    .selectAll().where { (ConversationMembersTable.conversationId eq convId) and (ConversationMembersTable.userId neq userId) }
                    .singleOrNull()
                
                if (memberRow != null) {
                    partnerId = memberRow[ConversationMembersTable.userId].value
                    val userRow = UsersTable.selectAll().where { UsersTable.id eq partnerId }.singleOrNull()
                    if (userRow != null) {
                        partnerName = userRow[UsersTable.fullName]
                        partnerAvatar = userRow[UsersTable.profilePictureUrl]
                    }
                }
            }

            // Get last message text as preview
            val lastMsgRow = MessagesTable
                .selectAll().where { (MessagesTable.conversationId eq convId) and (MessagesTable.isDeleted eq false) }
                .orderBy(MessagesTable.createdAt to SortOrder.DESC)
                .limit(1)
                .singleOrNull()

            ConversationDto(
                id = convId,
                title = row[ConversationsTable.title],
                type = row[ConversationsTable.type],
                unreadCount = 0, // Unread tracking usually requires a separate table 'MessageReads' (Omitted for simplicity)
                partnerId = partnerId,
                partnerName = partnerName,
                partnerAvatar = partnerAvatar,
                lastMessage = lastMsgRow?.get(MessagesTable.content),
                lastMessageTime = lastMsgRow?.get(MessagesTable.createdAt)?.toString()
            )
        }

        ConversationResponse(conversationsList)
    }

    suspend fun isUserInConversation(userId: Long, conversationId: Long): Boolean = dbQuery {
        ConversationMembersTable
            .selectAll().where { (ConversationMembersTable.userId eq userId) and (ConversationMembersTable.conversationId eq conversationId) }
            .count() > 0
    }

    suspend fun getMembersInConversation(conversationId: Long): List<Long> = dbQuery {
        ConversationMembersTable
            .selectAll().where { ConversationMembersTable.conversationId eq conversationId }
            .map { it[ConversationMembersTable.userId].value }
    }

    suspend fun saveMessage(senderId: Long, conversationId: Long, content: String, typeStr: String, replyToId: Long?): MessageDto = dbQuery {
        val messageTypeEnum = try {
            MessageType.valueOf(typeStr)
        } catch (e: Exception) {
            MessageType.TEXT
        }

        val insertStmt = MessagesTable.insertAndGetId {
            it[MessagesTable.conversationId] = conversationId
            it[MessagesTable.senderId] = senderId
            it[MessagesTable.content] = content
            it[messageType] = messageTypeEnum
            it[MessagesTable.replyToId] = replyToId
        }

        // Bump the 'updated_at' on the Conversation to push it up the list
        val nowTime = Instant.now()
        ConversationsTable.update({ ConversationsTable.id eq conversationId }) {
            it[updatedAt] = nowTime
        }

        val userRow = UsersTable.selectAll().where { UsersTable.id eq senderId }.single()

        MessageDto(
            messageId = insertStmt.value,
            senderId = senderId,
            senderName = userRow[UsersTable.fullName],
            content = content,
            type = messageTypeEnum,
            mediaUrl = null,
            replyToId = replyToId,
            createdAt = nowTime.toString(),
            isMe = false
        )
    }

    suspend fun getMessages(userId: Long, conversationId: Long, page: Int, limit: Int): PaginatedMessagesResponse = dbQuery {
        val offsetVal = ((page - 1) * limit).toLong()

        val query = (MessagesTable innerJoin UsersTable)
            .selectAll().where { (MessagesTable.conversationId eq conversationId) and (MessagesTable.isDeleted eq false) }
            .orderBy(MessagesTable.createdAt to SortOrder.DESC)

        val totalRecords = query.count()
        val totalPages = Math.ceil(totalRecords.toDouble() / limit).toInt()

        val msgRows = query.limit(limit, offsetVal).toList()

        val messagesList = msgRows.map { row ->
            val senderIdFromDb = row[UsersTable.id].value
            MessageDto(
                messageId = row[MessagesTable.id].value,
                senderId = senderIdFromDb,
                senderName = row[UsersTable.fullName],
                content = row[MessagesTable.content],
                type = row[MessagesTable.messageType],
                mediaUrl = row[MessagesTable.mediaUrl],
                replyToId = row[MessagesTable.replyToId]?.value,
                createdAt = row[MessagesTable.createdAt].toString(),
                isMe = (senderIdFromDb == userId)
            )
        }

        PaginatedMessagesResponse(
            messages = messagesList,
            meta = PaginationMeta(currentPage = page, totalPages = totalPages, hasNext = page < totalPages)
        )
    }

    // --- FR-41: GET OR CREATE CONVERSATION ---
    suspend fun getOrCreateConversation(userId: Long, targetUserId: Long): Any = dbQuery {
        // Try to find existing 1-1 conversation between the two users
        val existingConvIds = ConversationMembersTable
            .select(ConversationMembersTable.conversationId)
            .where { ConversationMembersTable.userId eq userId }
            .map { it[ConversationMembersTable.conversationId].value }
            .toSet()

        val targetConvIds = ConversationMembersTable
            .select(ConversationMembersTable.conversationId)
            .where { ConversationMembersTable.userId eq targetUserId }
            .map { it[ConversationMembersTable.conversationId].value }
            .toSet()

        val commonConvId = existingConvIds.intersect(targetConvIds).firstOrNull()

        if (commonConvId != null) {
            mapOf("conversationId" to commonConvId, "isNew" to false)
        } else {
            // Create new conversation
            val newConvId = ConversationsTable.insertAndGetId {
                it[type] = com.instagallery.models.common.ConversationType.DIRECT
            }.value

            ConversationMembersTable.insert {
                it[ConversationMembersTable.conversationId] = newConvId
                it[ConversationMembersTable.userId] = userId
            }
            ConversationMembersTable.insert {
                it[ConversationMembersTable.conversationId] = newConvId
                it[ConversationMembersTable.userId] = targetUserId
            }

            mapOf("conversationId" to newConvId, "isNew" to true)
        }
    }

    // --- FR-41: HIDE CONVERSATION FOR USER ---
    suspend fun hideConversationForUser(userId: Long, conversationId: Long) = dbQuery {
        // Soft-delete by removing the participant record (only for this user)
        ConversationMembersTable.deleteWhere {
            (ConversationMembersTable.conversationId eq conversationId) and
            (ConversationMembersTable.userId eq userId)
        }
    }
}
