package com.instagallery.services

import com.instagallery.models.common.ConversationResponse
import com.instagallery.models.common.MessageDto
import com.instagallery.models.common.PaginatedMessagesResponse
import com.instagallery.models.common.WsEventResponse
import com.instagallery.plugins.AuthException
import com.instagallery.plugins.ValidationException
import com.instagallery.repositories.ChatRepository
import com.instagallery.utils.ConnectionManager
import io.ktor.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.coroutines.isActive
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ChatService : KoinComponent {
    private val chatRepo: ChatRepository by inject()
    private val notificationService: NotificationService by inject()

    suspend fun getConversations(userId: Long): ConversationResponse {
        return chatRepo.getConversationsForUser(userId)
    }

    suspend fun getMessages(userId: Long, conversationId: Long, page: Int, limit: Int): PaginatedMessagesResponse {
        val inConv = chatRepo.isUserInConversation(userId, conversationId)
        if (!inConv) {
            throw AuthException("UNAUTHORIZED_ACCESS", "Bạn không có quyền xem tin nhắn nhóm này.")
        }
        
        val verifiedPage = if (page < 1) 1 else page
        val verifiedLimit = if (limit < 1) 50 else if (limit > 100) 100 else limit

        return chatRepo.getMessages(userId, conversationId, verifiedPage, verifiedLimit)
    }

    suspend fun markConversationRead(userId: Long, conversationId: Long): Int {
        val inConv = chatRepo.isUserInConversation(userId, conversationId)
        if (!inConv) {
            throw AuthException("UNAUTHORIZED_ACCESS", "Báº¡n khÃ´ng cÃ³ quyá»n xem tin nháº¯n nhÃ³m nÃ y.")
        }

        return chatRepo.markConversationRead(userId, conversationId)
    }

    suspend fun sendMessageHTTP(senderId: Long, conversationId: Long, content: String, type: String, replyToId: Long?): MessageDto {
        val contentClean = content.trim()
        val inConv = chatRepo.isUserInConversation(senderId, conversationId)
        if (!inConv) {
            throw AuthException("UNAUTHORIZED_ACCESS", "Bạn không có quyền chat ở nhóm này.")
        }

        // Save to Database
        val savedMessage = chatRepo.saveMessage(senderId, conversationId, contentClean, type, replyToId)

        // Broadcast to all members currently connected via WebSocket
        val membersList = chatRepo.getMembersInConversation(conversationId)
        membersList
            .filter { memberId -> memberId != senderId }
            .forEach { memberId ->
                notificationService.notifyMessageReceived(
                    recipientUserId = memberId,
                    actorUserId = senderId,
                    conversationId = conversationId,
                    preview = contentClean,
                )
            }
        val eventObj = WsEventResponse("NEW_MESSAGE", savedMessage)

        membersList.forEach { memberId ->
            val wsSession = ConnectionManager.getSession(memberId)
            if (wsSession != null && wsSession.isActive) {
                val finalEvent = eventObj.copy(data = savedMessage.copy(isMe = (memberId == senderId)))
                val finalPayload = Json.encodeToString(finalEvent)
                try {
                    wsSession.send(Frame.Text(finalPayload))
                } catch (e: Exception) {
                    ConnectionManager.removeSession(memberId)
                }
            }
        }
        return savedMessage.copy(isMe = true)
    }

    // --- WebSocket Logic ---
    suspend fun handleWsMessage(senderId: Long, conversationId: Long, content: String, type: String, replyToId: Long?) {
        val contentClean = content.trim()
        val inConv = chatRepo.isUserInConversation(senderId, conversationId)
        if (!inConv) return // Ignore silently on WS or push error frame

        // Save to Database
        val savedMessage = chatRepo.saveMessage(senderId, conversationId, contentClean, type, replyToId)

        // Broadcast to all members currently connected via WebSocket
        val membersList = chatRepo.getMembersInConversation(conversationId)
        membersList
            .filter { memberId -> memberId != senderId }
            .forEach { memberId ->
                notificationService.notifyMessageReceived(
                    recipientUserId = memberId,
                    actorUserId = senderId,
                    conversationId = conversationId,
                    preview = contentClean,
                )
            }
        
        val eventObj = WsEventResponse("NEW_MESSAGE", savedMessage)
        val jsonPayload = Json.encodeToString(eventObj)
        val frame = Frame.Text(jsonPayload)

        membersList.forEach { memberId ->
            val wsSession = ConnectionManager.getSession(memberId)
            if (wsSession != null && wsSession.isActive) {
                // Determine `isMe` for each specific recipient
                val finalEvent = eventObj.copy(data = savedMessage.copy(isMe = (memberId == senderId)))
                val finalPayload = Json.encodeToString(finalEvent)
                
                try {
                    wsSession.send(Frame.Text(finalPayload))
                } catch (e: Exception) {
                    ConnectionManager.removeSession(memberId)
                }
            }
        }
    }

    // --- FR-41: TẠO HOẶC LẤY CONVERSATION ---
    suspend fun getOrCreateConversation(userId: Long, targetUserId: Long): Any {
        if (userId == targetUserId) {
            throw ValidationException("SELF_CONVERSATION", "Không thể tạo cuộc trò chuyện với chính mình.")
        }
        return chatRepo.getOrCreateConversation(userId, targetUserId)
    }

    // --- FR-41: XÓA / ẨN CONVERSATION ---
    suspend fun deleteConversation(userId: Long, conversationId: Long) {
        val inConv = chatRepo.isUserInConversation(userId, conversationId)
        if (!inConv) {
            throw AuthException("UNAUTHORIZED_ACCESS", "Bạn không có quyền xóa cuộc trò chuyện này.")
        }
        chatRepo.hideConversationForUser(userId, conversationId)
    }
}
