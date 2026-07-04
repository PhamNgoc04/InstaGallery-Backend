package com.instagallery.routes

import com.instagallery.models.common.ApiResponse
import com.instagallery.models.request.CreatePostRequest
import com.instagallery.plugins.ValidationException
import com.instagallery.services.PostService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import org.koin.ktor.ext.getKoin

private val postRequestJson = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

private object PostRouteMessages {
    const val INVALID_TOKEN = "Token kh\u00f4ng h\u1ee3p l\u1ec7."
    const val INVALID_POST_ID = "ID b\u00e0i vi\u1ebft kh\u00f4ng h\u1ee3p l\u1ec7."
    const val INVALID_USER_ID = "ID ng\u01b0\u1eddi d\u00f9ng kh\u00f4ng h\u1ee3p l\u1ec7."
    const val MISSING_TAGGED_USER_ID = "Thi\u1ebfu tr\u01b0\u1eddng taggedUserId."
    const val INVALID_TAGGED_USER_ID = "ID ng\u01b0\u1eddi d\u00f9ng \u0111\u01b0\u1ee3c g\u1eafn th\u1ebb kh\u00f4ng h\u1ee3p l\u1ec7."
    const val CREATE_SUCCESS = "\u0110\u0103ng b\u00e0i vi\u1ebft th\u00e0nh c\u00f4ng"
    const val UPDATE_SUCCESS = "\u0110\u00e3 c\u1eadp nh\u1eadt b\u00e0i vi\u1ebft th\u00e0nh c\u00f4ng"
    const val DELETE_SUCCESS = "\u0110\u00e3 x\u00f3a b\u00e0i vi\u1ebft an to\u00e0n"
    const val TAG_SUCCESS = "\u0110\u00e3 g\u1eafn th\u1ebb ng\u01b0\u1eddi d\u00f9ng v\u00e0o b\u00e0i vi\u1ebft."
    const val REMOVE_TAG_SUCCESS = "\u0110\u00e3 x\u00f3a th\u1ebb ng\u01b0\u1eddi d\u00f9ng kh\u1ecfi b\u00e0i vi\u1ebft."
    const val COMMENT_SETTINGS_SUCCESS = "\u0110\u00e3 c\u1eadp nh\u1eadt c\u00e0i \u0111\u1eb7t b\u00ecnh lu\u1eadn."
}

fun Route.postRoutes() {
    val postService = application.getKoin().get<PostService>()

    route("/api/v1/posts") {
        authenticate("jwt") {
            post {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@post call.respond(
                        HttpStatusCode.Unauthorized,
                        ApiResponse.error("UNAUTHORIZED", PostRouteMessages.INVALID_TOKEN)
                    )

                val requestJson = postRequestJson.parseToJsonElement(call.receiveText())
                val post = when (requestJson) {
                    is JsonObject -> {
                        val request = postRequestJson.decodeFromJsonElement<CreatePostRequest>(requestJson)
                        postService.createPost(userId, request)
                    }
                    is JsonArray -> {
                        throw ValidationException(
                            "BULK_CREATE_NOT_ALLOWED",
                            "Create post only accepts one post object per request."
                        )
                    }
                    else -> throw ValidationException("INVALID_BODY", "Request body must be a JSON object.")
                }

                call.respond(
                    HttpStatusCode.Created,
                    ApiResponse.success(data = post, message = PostRouteMessages.CREATE_SUCCESS)
                )
            }

            get("/feed") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@get call.respond(
                        HttpStatusCode.Unauthorized,
                        ApiResponse.error("UNAUTHORIZED", PostRouteMessages.INVALID_TOKEN)
                    )

                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10

                val feed = postService.getFeed(userId, page, limit)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = feed))
            }

            get("/{id}") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@get call.respond(
                        HttpStatusCode.Unauthorized,
                        ApiResponse.error("UNAUTHORIZED", PostRouteMessages.INVALID_TOKEN)
                    )

                val postId = call.parameters["id"]?.toLongOrNull()
                    ?: return@get call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse.error("INVALID_ID", PostRouteMessages.INVALID_POST_ID)
                    )

                val detail = postService.getPostDetail(postId, userId)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = detail))
            }

            put("/{id}") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@put call.respond(
                        HttpStatusCode.Unauthorized,
                        ApiResponse.error("UNAUTHORIZED", PostRouteMessages.INVALID_TOKEN)
                    )

                val postId = call.parameters["id"]?.toLongOrNull()
                    ?: return@put call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse.error("INVALID_ID", PostRouteMessages.INVALID_POST_ID)
                    )

                val request = call.receive<com.instagallery.models.request.UpdatePostRequest>()
                postService.updatePost(userId, postId, request)

                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse.success(data = null, message = PostRouteMessages.UPDATE_SUCCESS)
                )
            }

            delete("/{id}") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@delete call.respond(
                        HttpStatusCode.Unauthorized,
                        ApiResponse.error("UNAUTHORIZED", PostRouteMessages.INVALID_TOKEN)
                    )

                val postId = call.parameters["id"]?.toLongOrNull()
                    ?: return@delete call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse.error("INVALID_ID", PostRouteMessages.INVALID_POST_ID)
                    )

                postService.deletePost(userId, postId)
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse.success(data = null, message = PostRouteMessages.DELETE_SUCCESS)
                )
            }

            get("/users/{userId}/posts") {
                val viewerId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asLong()
                    ?: return@get call.respond(
                        HttpStatusCode.Unauthorized,
                        ApiResponse.error("UNAUTHORIZED", PostRouteMessages.INVALID_TOKEN)
                    )

                val targetUserId = call.parameters["userId"]?.toLongOrNull()
                    ?: return@get call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse.error("INVALID_ID", PostRouteMessages.INVALID_USER_ID)
                    )

                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 20
                val posts = postService.getUserPosts(viewerId, targetUserId, page, limit)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = posts))
            }

            post("/{id}/tags") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asLong()
                    ?: return@post call.respond(
                        HttpStatusCode.Unauthorized,
                        ApiResponse.error("UNAUTHORIZED", PostRouteMessages.INVALID_TOKEN)
                    )

                val postId = call.parameters["id"]?.toLongOrNull()
                    ?: return@post call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse.error("INVALID_ID", PostRouteMessages.INVALID_POST_ID)
                    )

                val body = call.receiveText()
                val taggedUserId = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
                    .decodeFromString<Map<String, Long>>(body)["taggedUserId"]
                    ?: return@post call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse.error("MISSING_FIELD", PostRouteMessages.MISSING_TAGGED_USER_ID)
                    )

                postService.tagUserInPost(userId, postId, taggedUserId)
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse.success(data = null, message = PostRouteMessages.TAG_SUCCESS)
                )
            }

            delete("/{id}/tags/{taggedUserId}") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asLong()
                    ?: return@delete call.respond(
                        HttpStatusCode.Unauthorized,
                        ApiResponse.error("UNAUTHORIZED", PostRouteMessages.INVALID_TOKEN)
                    )

                val postId = call.parameters["id"]?.toLongOrNull()
                    ?: return@delete call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse.error("INVALID_ID", PostRouteMessages.INVALID_POST_ID)
                    )

                val taggedUserId = call.parameters["taggedUserId"]?.toLongOrNull()
                    ?: return@delete call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse.error("INVALID_ID", PostRouteMessages.INVALID_TAGGED_USER_ID)
                    )

                postService.removeTagFromPost(userId, postId, taggedUserId)
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse.success(data = null, message = PostRouteMessages.REMOVE_TAG_SUCCESS)
                )
            }

            put("/{id}/comment-settings") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asLong()
                    ?: return@put call.respond(
                        HttpStatusCode.Unauthorized,
                        ApiResponse.error("UNAUTHORIZED", PostRouteMessages.INVALID_TOKEN)
                    )

                val postId = call.parameters["id"]?.toLongOrNull()
                    ?: return@put call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse.error("INVALID_ID", PostRouteMessages.INVALID_POST_ID)
                    )

                val body = call.receiveText()
                val commentSetting = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
                    .decodeFromString<Map<String, String>>(body)["commentSetting"] ?: "ALL"

                postService.updateCommentSettings(userId, postId, commentSetting)
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse.success(data = null, message = PostRouteMessages.COMMENT_SETTINGS_SUCCESS)
                )
            }
        }
    }
}
