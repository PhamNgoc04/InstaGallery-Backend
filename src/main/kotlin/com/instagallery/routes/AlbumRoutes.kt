package com.instagallery.routes

import com.instagallery.models.common.ApiResponse
import com.instagallery.models.request.AddAlbumMediaRequest
import com.instagallery.models.request.CreateAlbumRequest
import com.instagallery.models.request.UpdateAlbumRequest
import com.instagallery.services.AlbumService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.getKoin

fun Route.albumRoutes() {
    val albumService = application.getKoin().get<AlbumService>()

    route("/api/v1/albums") {
        authenticate("jwt") {
            post {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asLong()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))
                val request = call.receive<CreateAlbumRequest>()
                val album = albumService.createAlbum(userId, request)
                call.respond(HttpStatusCode.Created, ApiResponse.success(data = album, message = "Tạo album thành công."))
            }

            get {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asLong()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))
                val ownerId = call.request.queryParameters["userId"]?.toLongOrNull() ?: userId
                val albums = albumService.listAlbums(userId, ownerId)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = albums))
            }

            get("/{id}") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asLong()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))
                val albumId = call.parameters["id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID album không hợp lệ."))
                val album = albumService.getAlbum(userId, albumId)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = album))
            }

            put("/{id}") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asLong()
                    ?: return@put call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))
                val albumId = call.parameters["id"]?.toLongOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID album không hợp lệ."))
                val request = call.receive<UpdateAlbumRequest>()
                val album = albumService.updateAlbum(userId, albumId, request)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = album, message = "Đã cập nhật album."))
            }

            delete("/{id}") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asLong()
                    ?: return@delete call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))
                val albumId = call.parameters["id"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID album không hợp lệ."))
                albumService.deleteAlbum(userId, albumId)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = null, message = "Đã xóa album."))
            }

            post("/{id}/media") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asLong()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))
                val albumId = call.parameters["id"]?.toLongOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID album không hợp lệ."))
                val request = call.receive<AddAlbumMediaRequest>()
                val album = albumService.addMedia(userId, albumId, request)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = album, message = "Đã thêm bài viết vào album."))
            }

            delete("/{id}/media/{mediaId}") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asLong()
                    ?: return@delete call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))
                val albumId = call.parameters["id"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID album không hợp lệ."))
                val mediaId = call.parameters["mediaId"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID media không hợp lệ."))
                albumService.removeMedia(userId, albumId, mediaId)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = null, message = "Đã gỡ media khỏi album."))
            }
        }
    }
}
