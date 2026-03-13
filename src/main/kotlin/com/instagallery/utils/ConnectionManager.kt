package com.instagallery.utils

import io.ktor.websocket.*
import java.util.concurrent.ConcurrentHashMap

// Cấu trúc dữ liệu để theo dõi Users đang online trong hệ thống WebSockets
// Key: UserId (Long), Value: DefaultWebSocketSession (Ống nước WebSocket)
object ConnectionManager {
    private val sessions = ConcurrentHashMap<Long, DefaultWebSocketSession>()

    fun addSession(userId: Long, session: DefaultWebSocketSession) {
        sessions[userId] = session
    }

    fun removeSession(userId: Long) {
        sessions.remove(userId)
    }

    fun getSession(userId: Long): DefaultWebSocketSession? {
        return sessions[userId]
    }
}
