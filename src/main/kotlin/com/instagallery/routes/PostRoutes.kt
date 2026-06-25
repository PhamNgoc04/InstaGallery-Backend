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
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token khÃ´ng há»£p lá»‡."))

                val request = call.receive<CreatePostRequest>()
                val post = postService.createPost(userId, request)

                call.respond(HttpStatusCode.Created, ApiResponse.success(data = post, message = "ÄÄƒng bÃ i viáº¿t thÃ nh cÃ´ng"))
            }

            get("/feed") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token khÃ´ng há»£p lá»‡."))

                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10

                val feed = postService.getFeed(userId, page, limit)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = feed))
            }

            get("/{id}") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token khÃ´ng há»£p lá»‡."))

                val postId = call.parameters["id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID bÃ i viáº¿t khÃ´ng há»£p lá»‡."))

                val detail = postService.getPostDetail(postId, userId)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = detail))
            }

            put("/{id}") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@put call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token khÃ´ng há»£p lá»‡."))

                val postId = call.parameters["id"]?.toLongOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID bÃ i viáº¿t khÃ´ng há»£p lá»‡."))

                val request = call.receive<com.instagallery.models.request.UpdatePostRequest>()
                postService.updatePost(userId, postId, request)

                call.respond(HttpStatusCode.OK, ApiResponse.success(data = null, message = "ÄÃ£ cáº­p nháº­t bÃ i viáº¿t thÃ nh cÃ´ng"))
            }

            delete("/{id}") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@delete call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token khÃ´ng há»£p lá»‡."))

                val postId = call.parameters["id"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID bÃ i viáº¿t khÃ´ng há»£p lá»‡."))

                postService.deletePost(userId, postId)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = null, message = "ÄÃ£ xÃ³a bÃ i viáº¿t an toÃ n"))
            }

            get("/users/{userId}/posts") {
                val targetUserId = call.parameters["userId"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID ngÆ°á»i dÃ¹ng khÃ´ng há»£p lá»‡."))

                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 20
                val posts = postService.getUserPosts(targetUserId, page, limit)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = posts))
            }

            post("/{id}/tags") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asLong()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token khÃ´ng há»£p lá»‡."))

                val postId = call.parameters["id"]?.toLongOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID bÃ i viáº¿t khÃ´ng há»£p lá»‡."))

                val body = call.receiveText()
                val taggedUserId = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
                    .decodeFromString<Map<String, Long>>(body)["taggedUserId"]
                    ?: return@post call.respond(HttpStatusCode.BadRequest, ApiResponse.error("MISSING_FIELD", "Thiáº¿u trÆ°á»ng taggedUserId."))

                postService.tagUserInPost(userId, postId, taggedUserId)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = null, message = "ÄÃ£ gáº¯n tháº» ngÆ°á»i dÃ¹ng vÃ o bÃ i viáº¿t."))
            }

            delete("/{id}/tags/{taggedUserId}") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asLong()
                    ?: return@delete call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token khÃ´ng há»£p lá»‡."))

                val postId = call.parameters["id"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID bÃ i viáº¿t khÃ´ng há»£p lá»‡."))

                val taggedUserId = call.parameters["taggedUserId"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID ngÆ°á»i dÃ¹ng Ä‘Æ°á»£c gáº¯n tháº» khÃ´ng há»£p lá»‡."))

                postService.removeTagFromPost(userId, postId, taggedUserId)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = null, message = "ÄÃ£ xÃ³a tháº» ngÆ°á»i dÃ¹ng khá»i bÃ i viáº¿t."))
            }

            put("/{id}/comment-settings") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asLong()
                    ?: return@put call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token khÃ´ng há»£p lá»‡."))

                val postId = call.parameters["id"]?.toLongOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID bÃ i viáº¿t khÃ´ng há»£p lá»‡."))

                val body = call.receiveText()
                val commentSetting = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
                    .decodeFromString<Map<String, String>>(body)["commentSetting"] ?: "ALL"

                postService.updateCommentSettings(userId, postId, commentSetting)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = null, message = "ÄÃ£ cáº­p nháº­t cÃ i Ä‘áº·t bÃ¬nh luáº­n."))
            }
        }
    }
}
