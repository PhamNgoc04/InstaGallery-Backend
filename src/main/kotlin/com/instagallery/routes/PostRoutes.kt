package com.instagallery.routes

import com.instagallery.models.common.ApiResponse
import com.instagallery.models.request.CreatePostRequest
import com.instagallery.services.PostService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.getKoin

fun Route.postRoutes() {
    val postService = application.getKoin().get<PostService>()

    route("/api/v1/posts") {
        
        authenticate("jwt") {
            
            post {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))

                val request = call.receive<CreatePostRequest>()
                val post = postService.createPost(userId, request)
                
                call.respond(HttpStatusCode.Created, ApiResponse.success(data = post, message = "Đăng bài viết thành công"))
            }

            get("/feed") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))

                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10

                val feed = postService.getFeed(userId, page, limit)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = feed))
            }

            get("/{id}") {
                val postId = call.parameters["id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID bài viết không hợp lệ."))

                val post = postService.getPostDetails(postId)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = post))
            }

            put("/{id}") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@put call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))

                val postId = call.parameters["id"]?.toLongOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID bài viết không hợp lệ."))

                val request = call.receive<com.instagallery.models.request.UpdatePostRequest>()
                postService.updatePost(userId, postId, request)
                
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = null, message = "Đã cập nhật bài viết thành công"))
            }

            delete("/{id}") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@delete call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))

                val postId = call.parameters["id"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID bài viết không hợp lệ."))

                postService.deletePost(userId, postId)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = null, message = "Đã xóa bài viết an toàn"))
            }

            // --- FR-10: GET USER'S POSTS ---
            get("/users/{userId}/posts") {
                val targetUserId = call.parameters["userId"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID người dùng không hợp lệ."))

                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 20
                val posts = postService.getUserPosts(targetUserId, page, limit)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = posts))
            }

            // --- FR-19: TAG USER IN POST ---
            post("/{id}/tags") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asLong()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))

                val postId = call.parameters["id"]?.toLongOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID bài viết không hợp lệ."))

                val body = call.receiveText()
                val taggedUserId = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
                    .decodeFromString<Map<String, Long>>(body)["taggedUserId"]
                    ?: return@post call.respond(HttpStatusCode.BadRequest, ApiResponse.error("MISSING_FIELD", "Thiếu trường taggedUserId."))

                postService.tagUserInPost(userId, postId, taggedUserId)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = null, message = "Đã gắn thẻ người dùng vào bài viết."))
            }

            // --- FR-19: REMOVE TAG FROM POST ---
            delete("/{id}/tags/{taggedUserId}") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asLong()
                    ?: return@delete call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))

                val postId = call.parameters["id"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID bài viết không hợp lệ."))

                val taggedUserId = call.parameters["taggedUserId"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID người dùng được gắn thẻ không hợp lệ."))

                postService.removeTagFromPost(userId, postId, taggedUserId)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = null, message = "Đã xóa thẻ người dùng khỏi bài viết."))
            }

            // --- FR-33: COMMENT SETTINGS ---
            put("/{id}/comment-settings") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asLong()
                    ?: return@put call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))

                val postId = call.parameters["id"]?.toLongOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID bài viết không hợp lệ."))

                val body = call.receiveText()
                val commentSetting = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
                    .decodeFromString<Map<String, String>>(body)["commentSetting"] ?: "ALL"

                postService.updateCommentSettings(userId, postId, commentSetting)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = null, message = "Đã cập nhật cài đặt bình luận."))
            }
        }
    }
}
