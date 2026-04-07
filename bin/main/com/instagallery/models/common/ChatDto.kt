package com.instagallery.models.common

import kotlinx.serialization.Serializable

@Serializable
data class ConversationDto(
    val id: Long,
    val title: String?,
    val type: ConversationType,
    val unreadCount: Int = 0,
    val partnerId: Long?,          // For direct message
    val partnerName: String?,      // For direct message
    val partnerAvatar: String?,    // For direct message
    val lastMessage: String?,
    val lastMessageTime: String?
)

@Serializable
data class ConversationResponse(
    val conversations: List<ConversationDto>
)

@Serializable
data class MessageDto(
    val messageId: Long,
    val senderId: Long,
    val senderName: String,
    val content: String?,
    val type: MessageType,
    val mediaUrl: String?,
    val replyToId: Long?,
    val createdAt: String,
    val isMe: Boolean = false // Will be mapped down later per user logic
)

@Serializable
data class PaginatedMessagesResponse(
    val messages: List<MessageDto>,
    val meta: PaginationMeta
)

@Serializable
data class WsEventResponse(
    val event: String,
    val data: MessageDto
)
