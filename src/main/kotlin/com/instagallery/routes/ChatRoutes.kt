package com.instagallery.routes

import com.instagallery.models.common.ApiResponse
import com.instagallery.models.request.WsMessageRequest
import com.instagallery.services.ChatService
import com.instagallery.utils.ConnectionManager
import com.instagallery.utils.JwtManager
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.inject

fun Route.chatRoutes() {
    val chatService: ChatService by inject()

    route("/api/v1/chat") {
        
        // --- REST APIs cho Lịch sử Chat ---
        authenticate("jwt") {
            get("/conversations") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))

                val result = chatService.getConversations(userId)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = result))
            }

            get("/conversations/{id}/messages") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))

                val conversationId = call.parameters["id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID hội thoại không hợp lệ."))

                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 50

                val result = chatService.getMessages(userId, conversationId, page, limit)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = result))
            }
        }
    }

    // --- WEBSOCKET API Phát Trực Tiếp ---
    // Note: Ktor websocket routes exist outside standard authenticated feature scopes
    // Manual token verification via query parameter is typical for WS.
    webSocket("/api/v1/ws/chat") {
        val token = call.request.queryParameters["token"]
        
        if (token == null) {
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Thiếu Token"))
            return@webSocket
        }
        
        // Manual verification since @authenticate doesn't map perfectly wrapper-wise to raw WS.
        val decodedJwt = try { com.auth0.jwt.JWT.decode(token) } catch (e: Exception) { null }
        if (decodedJwt == null) {
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Token không hợp lệ hoặc hết hạn"))
            return@webSocket
        }

        val userId = decodedJwt.getClaim("userId").asLong()
        
        // 1. Connection Established
        ConnectionManager.addSession(userId, this)

        try {
            // 2. Tắng nghe khung dữ liệu Push từ Client lên Server
            incoming.consumeEach { frame ->
                if (frame is Frame.Text) {
                    val text = frame.readText()
                    // Parse text -> WsMessageRequest
                    try {
                        val request = Json.decodeFromString<WsMessageRequest>(text)
                        
                        if (request.action == "SEND_MESSAGE") {
                            chatService.handleWsMessage(
                                senderId = userId,
                                conversationId = request.conversationId,
                                content = request.content,
                                type = request.messageType,
                                replyToId = request.replyToId
                            )
                        }
                        
                    } catch (e: Exception) {
                        // Bỏ qua các Frame sai cấu trúc (Noise / Malformed)
                    }
                }
            }
        } finally {
            // 3. User disconnects
            ConnectionManager.removeSession(userId)
        }
    }
}
