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
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.UUID

fun Route.mediaRoutes() {
    val mediaService = application.getKoin().get<MediaService>()
    val localMediaRoot = configuredLocalMediaRoot()

    route("/api/v1") {
        put("/media/local-upload/{folder}/{fileName}") {
            val folder = call.parameters["folder"]?.takeIf { it.isSafePathSegment() }
                ?: return@put call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_FOLDER", "Folder khÃ´ng há»£p lá»‡."))
            val fileName = call.parameters["fileName"]?.takeIf { it.isSafePathSegment() }
                ?: return@put call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_FILE", "TÃªn file khÃ´ng há»£p lá»‡."))

            val targetDirectory = File(localMediaRoot, folder).apply { mkdirs() }
            val targetFile = File(targetDirectory, fileName)
            val tempFile = File(targetDirectory, "$fileName.tmp-${UUID.randomUUID()}")
            call.receiveStream().use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            Files.move(
                tempFile.toPath(),
                targetFile.toPath(),
                StandardCopyOption.REPLACE_EXISTING,
            )

            call.respond(HttpStatusCode.OK, ApiResponse.success(data = null, message = "Upload local media thÃ nh cÃ´ng."))
        }

        get("/media/local-files/{folder}/{fileName}") {
            val folder = call.parameters["folder"]?.takeIf { it.isSafePathSegment() }
                ?: return@get call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_FOLDER", "Folder khÃ´ng há»£p lá»‡."))
            val fileName = call.parameters["fileName"]?.takeIf { it.isSafePathSegment() }
                ?: return@get call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_FILE", "TÃªn file khÃ´ng há»£p lá»‡."))

            val targetFile = File(File(localMediaRoot, folder), fileName)
            if (!targetFile.exists()) {
                return@get call.respond(HttpStatusCode.NotFound, ApiResponse.error("MEDIA_NOT_FOUND", "Media khÃ´ng tá»“n táº¡i."))
            }

            call.respondFile(targetFile)
        }

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

private fun String.isSafePathSegment(): Boolean {
    return isNotBlank() &&
        matches(SAFE_PATH_SEGMENT_REGEX) &&
        this != "." &&
        this != ".."
}

private fun configuredLocalMediaRoot(): File {
    val configuredPath = System.getenv(ENV_LOCAL_MEDIA_UPLOAD_DIR)
        ?.takeIf { it.isNotBlank() }
        ?: DEFAULT_LOCAL_MEDIA_UPLOAD_DIR
    return File(configuredPath).absoluteFile.apply { mkdirs() }
}

private val SAFE_PATH_SEGMENT_REGEX = Regex("[A-Za-z0-9._-]+")
private const val ENV_LOCAL_MEDIA_UPLOAD_DIR = "LOCAL_MEDIA_UPLOAD_DIR"
private const val DEFAULT_LOCAL_MEDIA_UPLOAD_DIR = "uploads"
