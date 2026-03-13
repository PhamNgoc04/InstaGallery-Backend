package com.instagallery.models.request

import kotlinx.serialization.Serializable

@Serializable
data class WsMessageRequest(
    val action: String, // SEND_MESSAGE, TYPING
    val conversationId: Long,
    val content: String,
    val messageType: String = "TEXT",
    val replyToId: Long? = null
)