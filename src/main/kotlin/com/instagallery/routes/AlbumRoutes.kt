package com.instagallery.routes

import com.instagallery.models.common.ApiResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.albumRoutes() {
    route("/api/v1/albums") {
        authenticate("jwt") {
            
            post {
                // TODO: Receive AlbumRequest (title, description, isPrivate)
                // TODO: Insert into AlbumsTable
                call.respond(HttpStatusCode.Created, ApiResponse.success(data = null, message = "Album created successfully"))
            }

            get {
                // TODO: Get list of albums for current user (or query param for other user's public albums)
                // TODO: Query AlbumsTable
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = listOf<Any>(), message = "Albums fetched successfully"))
            }

            get("/{id}") {
                val id = call.parameters["id"]?.toLongOrNull()
                // TODO: Fetch Album and check privacy. Fetch posts from AlbumMediaTable.
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = null, message = "Album details fetched"))
            }

            put("/{id}") {
                val id = call.parameters["id"]?.toLongOrNull()
                // TODO: Update title, description, coverImageUrl
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = null, message = "Album updated"))
            }

            delete("/{id}") {
                val id = call.parameters["id"]?.toLongOrNull()
                // TODO: Soft delete or Hard delete from AlbumsTable
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = null, message = "Album deleted"))
            }

            post("/{id}/media") {
                val id = call.parameters["id"]?.toLongOrNull()
                // TODO: Expect post_ids payload, insert into AlbumMediaTable
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = null, message = "Media added to album"))
            }
            
            delete("/{id}/media/{mediaId}") {
                val id = call.parameters["id"]?.toLongOrNull()
                val mediaId = call.parameters["mediaId"]?.toLongOrNull()
                // TODO: Remove post from AlbumMediaTable
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = null, message = "Media removed from album"))
            }
        }
    }
}
