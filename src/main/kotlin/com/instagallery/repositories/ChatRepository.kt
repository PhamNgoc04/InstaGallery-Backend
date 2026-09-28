package com.instagallery.repositories

import com.instagallery.database.DatabaseFactory.dbQuery
import com.instagallery.database.tables.*
import com.instagallery.models.common.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greater
import org.jetbrains.exposed.sql.SqlExpressionBuilder.neq
import java.time.Instant

class ChatRepository {

    suspend fun getConversationsForUser(userId: Long): ConversationResponse = dbQuery {
        // Find which conversations this user is part of
        val convIds = ConversationMembersTable
            .selectAll().where {
                (ConversationMembersTable.userId eq userId) and ConversationMembersTable.hiddenAt.isNull()
            }
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
                unreadCount = countUnreadMessages(userId, convId),
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
        ConversationMembersTable.update({ ConversationMembersTable.conversationId eq conversationId }) {
            it[hiddenAt] = null
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

        revealConversation(userId, conversationId)
        markConversationReadInTransaction(userId, conversationId)

        PaginatedMessagesResponse(
            messages = messagesList,
            meta = PaginationMeta(currentPage = page, totalPages = totalPages, hasNext = page < totalPages)
        )
    }

    suspend fun markConversationRead(userId: Long, conversationId: Long): Int = dbQuery {
        revealConversation(userId, conversationId)
        markConversationReadInTransaction(userId, conversationId)
        totalUnreadCountForUser(userId)
    }

    // --- FR-41: GET OR CREATE CONVERSATION ---
    suspend fun getOrCreateConversation(userId: Long, targetUserId: Long): DirectConversationResult = dbQuery {
        val pairKey = directPairKey(userId, targetUserId)
        val existingId = findDirectConversationId(userId, targetUserId, pairKey)
        if (existingId != null) {
            revealConversation(userId, existingId)
            return@dbQuery DirectConversationResult(conversationId = existingId, isNew = false)
        }

        val newConvId = try {
            ConversationsTable.insertAndGetId {
                it[type] = ConversationType.DIRECT
                it[directPairKey] = pairKey
            }.value
        } catch (exception: org.jetbrains.exposed.exceptions.ExposedSQLException) {
            findDirectConversationId(userId, targetUserId, pairKey)
                ?: throw exception
        }

        if (ConversationMembersTable.selectAll().where {
                (ConversationMembersTable.conversationId eq newConvId) and
                    (ConversationMembersTable.userId eq userId)
            }.count() == 0L
        ) {
            ConversationMembersTable.insert {
                it[ConversationMembersTable.conversationId] = newConvId
                it[ConversationMembersTable.userId] = userId
            }
            ConversationMembersTable.insert {
                it[ConversationMembersTable.conversationId] = newConvId
                it[ConversationMembersTable.userId] = targetUserId
            }
        }

        revealConversation(userId, newConvId)
        DirectConversationResult(conversationId = newConvId, isNew = true)
    }

    suspend fun hideConversationForUser(userId: Long, conversationId: Long) = dbQuery {
        ConversationMembersTable.update({
            (ConversationMembersTable.conversationId eq conversationId) and
                (ConversationMembersTable.userId eq userId)
        }) {
            it[hiddenAt] = Instant.now()
        }
    }

    private fun findDirectConversationId(userId: Long, targetUserId: Long, pairKey: String): Long? {
        val keyed = ConversationsTable
            .selectAll()
            .where {
                (ConversationsTable.type eq ConversationType.DIRECT) and
                    (ConversationsTable.directPairKey eq pairKey)
            }
            .singleOrNull()
        if (keyed != null) return keyed[ConversationsTable.id].value

        val userConversationIds = ConversationMembersTable
            .select(ConversationMembersTable.conversationId)
            .where { ConversationMembersTable.userId eq userId }
            .map { it[ConversationMembersTable.conversationId].value }
            .toSet()
        val targetConversationIds = ConversationMembersTable
            .select(ConversationMembersTable.conversationId)
            .where { ConversationMembersTable.userId eq targetUserId }
            .map { it[ConversationMembersTable.conversationId].value }
            .toSet()

        val legacyId = userConversationIds.intersect(targetConversationIds).firstOrNull { conversationId ->
            val conversation = ConversationsTable
                .selectAll()
                .where {
                    (ConversationsTable.id eq conversationId) and
                        (ConversationsTable.type eq ConversationType.DIRECT) and
                        ConversationsTable.directPairKey.isNull()
                }
                .singleOrNull()
            if (conversation == null) {
                false
            } else {
                ConversationMembersTable
                    .selectAll()
                    .where { ConversationMembersTable.conversationId eq conversationId }
                    .count() == 2L
            }
        } ?: return null

        ConversationsTable.update({ ConversationsTable.id eq legacyId }) {
            it[directPairKey] = pairKey
        }
        return legacyId
    }

    private fun revealConversation(userId: Long, conversationId: Long) {
        ConversationMembersTable.update({
            (ConversationMembersTable.conversationId eq conversationId) and
                (ConversationMembersTable.userId eq userId)
        }) {
            it[hiddenAt] = null
        }
    }

    private fun directPairKey(leftUserId: Long, rightUserId: Long): String {
        val low = minOf(leftUserId, rightUserId)
        val high = maxOf(leftUserId, rightUserId)
        return "$low:$high"
    }

    private fun totalUnreadCountForUser(userId: Long): Int {
        return ConversationMembersTable
            .select(ConversationMembersTable.conversationId)
            .where {
                (ConversationMembersTable.userId eq userId) and ConversationMembersTable.hiddenAt.isNull()
            }
            .sumOf { row ->
                countUnreadMessages(
                    userId = userId,
                    conversationId = row[ConversationMembersTable.conversationId].value,
                )
            }
    }

    private fun countUnreadMessages(
        userId: Long,
        conversationId: Long,
    ): Int {
        val memberRow = ConversationMembersTable
            .selectAll()
            .where {
                (ConversationMembersTable.conversationId eq conversationId) and
                    (ConversationMembersTable.userId eq userId)
            }
            .singleOrNull()
        val lastReadAt = memberRow?.get(ConversationMembersTable.lastReadAt)
        val baseCondition = (MessagesTable.conversationId eq conversationId) and
            (MessagesTable.senderId neq userId) and
            (MessagesTable.isDeleted eq false)
        val unreadCondition = if (lastReadAt == null) {
            baseCondition
        } else {
            baseCondition and (MessagesTable.createdAt greater lastReadAt)
        }

        return MessagesTable
            .selectAll()
            .where { unreadCondition }
            .count()
            .toInt()
    }

    private fun markConversationReadInTransaction(userId: Long, conversationId: Long) {
        ConversationMembersTable.update({
            (ConversationMembersTable.conversationId eq conversationId) and
                (ConversationMembersTable.userId eq userId)
        }) {
            it[lastReadAt] = Instant.now()
        }
    }
}
