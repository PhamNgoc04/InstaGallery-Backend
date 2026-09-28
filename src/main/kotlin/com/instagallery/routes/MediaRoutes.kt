package com.instagallery.routes

import com.instagallery.models.common.ApiResponse
import com.instagallery.models.request.AddPostMediaRequest
import com.instagallery.models.request.PresignedUrlRequest
import com.instagallery.models.request.ReorderMediaRequest
import com.instagallery.services.MediaService
import com.instagallery.utils.LocalUploadGrantStore
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.getKoin
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.Locale
import java.util.UUID

fun Route.mediaRoutes() {
    val mediaService = application.getKoin().get<MediaService>()
    val localMediaRoot = configuredLocalMediaRoot()

    route("/api/v1") {
        get("/media/local-files/{folder}/{fileName}") {
            val folder = call.parameters["folder"]?.takeIf { it.isSafePathSegment() }
                ?: return@get call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_FOLDER", "Folder không hợp lệ."))
            val fileName = call.parameters["fileName"]?.takeIf { it.isSafePathSegment() }
                ?: return@get call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_FILE", "Tên file không hợp lệ."))

            val targetFile = File(File(localMediaRoot, folder), fileName)
            if (!targetFile.exists()) {
                return@get call.respond(HttpStatusCode.NotFound, ApiResponse.error("MEDIA_NOT_FOUND", "Media không tồn tại."))
            }

            call.respondFile(targetFile)
        }

        authenticate("jwt") {
            put("/media/local-upload/{folder}/{fileName}") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asLong()
                    ?: return@put call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))
                val folder = call.parameters["folder"]?.takeIf { it.isSafePathSegment() }
                    ?: return@put call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_FOLDER", "Folder không hợp lệ."))
                val fileName = call.parameters["fileName"]?.takeIf { it.isSafePathSegment() }
                    ?: return@put call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_FILE", "Tên file không hợp lệ."))
                val extension = fileName.substringAfterLast('.', "").lowercase(Locale.US)
                if (extension !in ALLOWED_UPLOAD_EXTENSIONS) {
                    return@put call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_EXTENSION", "Chỉ hỗ trợ file hình ảnh (jpg, jpeg, png, webp)."))
                }
                val contentLength = call.request.headers[HttpHeaders.ContentLength]?.toLongOrNull()
                if (contentLength != null && contentLength > MAX_LOCAL_UPLOAD_BYTES) {
                    return@put call.respond(HttpStatusCode.PayloadTooLarge, ApiResponse.error("FILE_TOO_LARGE", "File hình ảnh vượt quá dung lượng tối đa 15MB."))
                }
                if (!LocalUploadGrantStore.consume(userId, folder, fileName)) {
                    return@put call.respond(HttpStatusCode.Forbidden, ApiResponse.error("UPLOAD_NOT_GRANTED", "URL upload không hợp lệ hoặc đã hết hạn."))
                }

                val targetDirectory = File(localMediaRoot, folder).apply { mkdirs() }
                val targetFile = File(targetDirectory, fileName)
                val tempFile = File(targetDirectory, "$fileName.tmp-${UUID.randomUUID()}")
                val written = try {
                    call.receiveStream().use { input ->
                        tempFile.outputStream().use { output ->
                            input.copyLimited(output, MAX_LOCAL_UPLOAD_BYTES)
                        }
                    }
                } catch (exception: Exception) {
                    tempFile.delete()
                    throw exception
                }
                if (written < 0) {
                    tempFile.delete()
                    return@put call.respond(HttpStatusCode.PayloadTooLarge, ApiResponse.error("FILE_TOO_LARGE", "File hình ảnh vượt quá dung lượng tối đa 15MB."))
                }
                if (written == 0L) {
                    tempFile.delete()
                    return@put call.respond(HttpStatusCode.BadRequest, ApiResponse.error("EMPTY_FILE", "File upload đang trống."))
                }
                Files.move(
                    tempFile.toPath(),
                    targetFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING,
                )

                call.respond(HttpStatusCode.OK, ApiResponse.success(data = null, message = "Upload local media thành công."))
            }

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

private fun InputStream.copyLimited(output: OutputStream, maxBytes: Long): Long {
    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
    var total = 0L
    while (true) {
        val read = read(buffer)
        if (read < 0) break
        total += read
        if (total > maxBytes) return -1
        output.write(buffer, 0, read)
    }
    return total
}

private val SAFE_PATH_SEGMENT_REGEX = Regex("[A-Za-z0-9._-]+")
private val ALLOWED_UPLOAD_EXTENSIONS = setOf("jpg", "jpeg", "png", "webp")
private const val MAX_LOCAL_UPLOAD_BYTES = 15L * 1024 * 1024
private const val ENV_LOCAL_MEDIA_UPLOAD_DIR = "LOCAL_MEDIA_UPLOAD_DIR"
private const val DEFAULT_LOCAL_MEDIA_UPLOAD_DIR = "uploads"
