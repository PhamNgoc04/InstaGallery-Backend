package com.instagallery.routes

import com.instagallery.models.common.ApiResponse
import com.instagallery.models.request.CreateCommentRequest
import com.instagallery.models.request.UpdateCommentRequest
import com.instagallery.services.InteractionService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.getKoin

fun Route.interactionRoutes() {
    val interactionService = application.getKoin().get<InteractionService>()

    route("/api/v1/posts") {
        
        authenticate("jwt") {
            
            // --- LIKES ---
            post("/{id}/like") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))

                val postId = call.parameters["id"]?.toLongOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID bài viết không hợp lệ."))

                val res = interactionService.toggleLike(userId, postId)
                val msg = if (res.isLiked) "Đã thả tim bài viết" else "Đã bỏ tim bài viết"
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = res, message = msg))
            }

            // --- SAVES (BOOKMARKS) ---
            post("/{id}/save") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))

                val postId = call.parameters["id"]?.toLongOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID bài viết không hợp lệ."))

                val res = interactionService.toggleSave(userId, postId)
                val msg = if (res.isSaved) "Đã lưu bài viết vào Bookmark" else "Đã bỏ lưu bài viết"
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = res, message = msg))
            }

            // --- COMMENTS ---
            post("/{id}/comments") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))

                val postId = call.parameters["id"]?.toLongOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID bài viết không hợp lệ."))

                val request = call.receive<CreateCommentRequest>()
                val res = interactionService.createComment(userId, postId, request)
                
                call.respond(HttpStatusCode.Created, ApiResponse.success(data = res, message = "Bình luận thành công"))
            }

            get("/{id}/comments") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))

                val postId = call.parameters["id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID bài viết không hợp lệ."))

                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 20

                val res = interactionService.getComments(postId, page, limit)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = res))
            }
        }
    }

    route("/api/v1/comments") {
        authenticate("jwt") {
            // --- EDIT COMMENT ---
            put("/{commentId}") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@put call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))

                val commentId = call.parameters["commentId"]?.toLongOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID bình luận không hợp lệ."))

                val request = call.receive<UpdateCommentRequest>()
                val updatedComment = interactionService.updateComment(userId, commentId, request)
                
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = updatedComment, message = "Đã sửa bình luận."))
            }

            // --- DELETE COMMENT ---
            delete("/{commentId}") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@delete call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))

                val commentId = call.parameters["commentId"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID bình luận không hợp lệ."))

                interactionService.deleteComment(userId, commentId)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = null, message = "Đã xóa bình luận."))
            }

            // --- LIKE / UNLIKE COMMENT ---
            post("/{commentId}/like") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))

                val commentId = call.parameters["commentId"]?.toLongOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID bình luận không hợp lệ."))

                val isLiked = interactionService.toggleCommentLike(userId, commentId)
                val msg = if (isLiked) "Đã thích bình luận." else "Đã bỏ thích bình luận."
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = mapOf("isLiked" to isLiked), message = msg))
            }
        }
    }
}
