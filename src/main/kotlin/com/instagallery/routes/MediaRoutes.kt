package com.instagallery.routes

import com.instagallery.models.common.ApiResponse
import com.instagallery.models.request.AddPostMediaRequest
import com.instagallery.models.request.PresignedUrlRequest
import com.instagallery.models.request.ReorderMediaRequest
import com.instagallery.services.MediaService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.getKoin

fun Route.mediaRoutes() {
    val mediaService = application.getKoin().get<MediaService>()

    route("/api/v1") {
        
        authenticate("jwt") {
            // --- S3 Presigned URL (Mock) ---
            post("/media/presigned-url") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))

                val request = call.receive<PresignedUrlRequest>()
                val response = mediaService.generatePresignedUrl(userId, request)

                call.respond(HttpStatusCode.OK, ApiResponse.success(data = response))
            }

            // --- Add Media to Post ---
            post("/posts/{postId}/media") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))

                val postId = call.parameters["postId"]?.toLongOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID Bài viết không hợp lệ."))

                val request = call.receive<AddPostMediaRequest>()
                val newMedia = mediaService.addMediaToPost(userId, postId, request)

                call.respond(HttpStatusCode.Created, ApiResponse.success(data = newMedia, message = "Đã thêm media vào bài viết."))
            }

            // --- Delete Media from Post ---
            delete("/posts/media/{mediaId}") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@delete call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))

                val mediaId = call.parameters["mediaId"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID Media không hợp lệ."))

                mediaService.deleteMedia(userId, mediaId)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = null, message = "Đã xóa media khỏi bài viết."))
            }

            // --- Reorder Media in Post ---
            put("/posts/{postId}/media/reorder") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@put call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))

                val postId = call.parameters["postId"]?.toLongOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID Bài viết không hợp lệ."))

                val request = call.receive<ReorderMediaRequest>()
                mediaService.reorderMedia(userId, postId, request)

                call.respond(HttpStatusCode.OK, ApiResponse.success(data = null, message = "Đã cập nhật thứ tự media."))
            }
        }
    }
}
