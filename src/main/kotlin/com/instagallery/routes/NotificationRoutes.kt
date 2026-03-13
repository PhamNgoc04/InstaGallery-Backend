package com.instagallery.routes

import com.instagallery.models.common.ApiResponse
import com.instagallery.services.NotificationService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.notificationRoutes() {
    val notificationService: NotificationService by inject()

    route("/api/v1/notifications") {
        
        authenticate("jwt") {
            
            get {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))

                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 20

                val result = notificationService.getMyNotifications(userId, page, limit)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = result))
            }

            put("/{id}/read") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@put call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))

                val notiId = call.parameters["id"]?.toLongOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID thông báo không hợp lệ."))

                notificationService.markAsRead(userId, notiId)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = null, message = "Đã đánh dấu thông báo là đã đọc"))
            }

            put("/read-all") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@put call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))

                notificationService.markAllAsRead(userId)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = null, message = "Đã đánh dấu tất cả thông báo là đã đọc"))
            }

        }
    }
}
