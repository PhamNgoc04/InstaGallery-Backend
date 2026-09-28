package com.instagallery.routes

import com.instagallery.models.common.ApiResponse
import com.instagallery.models.request.UpdateUserProfileRequest
import com.instagallery.services.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.getKoin

fun Route.userRoutes() {
    val userService = application.getKoin().get<UserService>()
    val authService = application.getKoin().get<com.instagallery.services.AuthService>()
    val interactionService = application.getKoin().get<com.instagallery.services.InteractionService>()

    route("/api/v1/users") {
        
        authenticate("jwt") {
            
            get("/me") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))

                val userDto = userService.getCurrentUserProfile(userId)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = userDto))
            }

            put("/me") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@put call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))

                val request = call.receive<UpdateUserProfileRequest>()
                val updatedUser = userService.updateProfile(userId, request)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = updatedUser, message = "Cập nhật hồ sơ thành công."))
            }

            get("/me/sessions") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))

                val currentRefreshToken = call.request.headers["X-Refresh-Token"]
                val sessions = authService.getMySessions(userId, currentRefreshToken)
                
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = sessions))
            }

            delete("/me/sessions/{id}") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@delete call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))

                val sessionId = call.parameters["id"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "Session ID rỗng hoặc không hợp lệ."))

                authService.revokeSession(userId, sessionId)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = null, message = "Đã đăng xuất thiết bị thành công"))
            }

            post("/me/deactivate") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))

                userService.deactivateAccount(userId)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = null, message = "Tài khoản của bạn đã bị vô hiệu hóa tạm thời."))
            }

            put("/me/avatar") {
                // In production, this would parse MultipartData and upload to S3/Firebase
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = null, message = "Avatar đã được cập nhật (Mock S3)."))
            }

            // --- FR-09: PRIVACY TOGGLE ---
            put("/me/privacy") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asLong()
                    ?: return@put call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))

                val body = call.receiveText()
                val isPrivate = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
                    .decodeFromString<Map<String, Boolean>>(body)["isPrivate"] ?: false

                userService.updatePrivacy(userId, isPrivate)
                val msg = if (isPrivate) "Tài khoản đã chuyển sang chế độ Riêng tư." else "Tài khoản đã chuyển sang chế độ Công khai."
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = mapOf("isPrivate" to isPrivate), message = msg))
            }

            // --- FR-22: SAVED POSTS ---
            get("/me/saved-posts") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asLong()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))

                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 20
                val saved = interactionService.getSavedPosts(userId, page, limit)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = saved))
            }

            // --- FR-20: LIKED POSTS ---
            get("/me/liked-posts") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asLong()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))

                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 20
                val liked = interactionService.getLikedPosts(userId, page, limit)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = liked))
            }

            // --- FR-19: TAGGED POSTS ---
            get("/me/tagged-posts") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asLong()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))

                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 20
                val tagged = interactionService.getTaggedPosts(userId, page, limit)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = tagged))
            }

            get("/me/comments") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asLong()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))

                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 20
                val comments = interactionService.getUserComments(userId, page, limit)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = comments))
            }

            // --- FR-28: ACTIVITY LOG ---
            get("/me/activity-log") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asLong()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))

                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 20
                val log = interactionService.getActivityLog(userId, page, limit)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = log))
            }

            // --- FR-31: BLOCKED USERS LIST ---
            get("/me/blocked") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asLong()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))

                val blocked = interactionService.getBlockedUsers(userId)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = blocked))
            }

            // --- FOLLOWERS (Social) ---
            post("/{id}/follow") {
                val principal = call.principal<JWTPrincipal>()
                val followerId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))
                
                val followingId = call.parameters["id"]?.toLongOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID người dùng không hợp lệ."))

                val result = interactionService.toggleFollow(followerId, followingId)
                val msg = when (result.status) {
                    "FOLLOWING" -> "Đã theo dõi người dùng này."
                    "REQUESTED" -> "Đã gửi lời mời theo dõi."
                    else -> "Đã bỏ theo dõi người dùng này."
                }
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = result, message = msg))
            }

            get("/suggestions") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))

                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
                val suggestions = userService.getSuggestedUsers(userId, limit)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = suggestions))
            }

            // --- PRIVATE FOLLOW REQUESTS ---
            get("/me/follow-requests") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))
                val requests = interactionService.listFollowRequests(userId)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = requests))
            }

            post("/me/follow-requests/{followerId}/{action}") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asLong()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))
                val followerId = call.parameters["followerId"]?.toLongOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "followerId không hợp lệ."))
                val action = call.parameters["action"]
                    ?: return@post call.respond(HttpStatusCode.BadRequest, ApiResponse.error("MISSING_ACTION", "Thiếu action."))
                val result = interactionService.respondFollowRequest(userId, followerId, action)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = result, message = "Request $action"))
            }

            post("/{id}/block") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asLong()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))
                val blockedId = call.parameters["id"]?.toLongOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID người dùng không hợp lệ."))
                val result = interactionService.toggleBlock(userId, blockedId)
                val msg = if (result.isBlocked) "Đã chặn người dùng này." else "Đã bỏ chặn người dùng này."
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = result, message = msg))
            }

            post("/{id}/mute") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asLong()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))
                val mutedId = call.parameters["id"]?.toLongOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID người dùng không hợp lệ."))
                val result = interactionService.toggleMute(userId, mutedId)
                val msg = if (result.isMuted) "Đã tắt tiếng người dùng này." else "Đã bỏ tắt tiếng người dùng này."
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = result, message = msg))
            }
        }

        // --- PUBLIC GET PROFILE ---
        get("/{id}") {
            val userIdToFind = call.parameters["id"]?.toLongOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID người dùng không hợp lệ."))

            try {
                val userDto = userService.getPublicUserProfile(userIdToFind)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = userDto))
            } catch (e: Exception) {
                // If the user doesn't exist, AuthException is thrown by getCurrentUser
                call.respond(HttpStatusCode.NotFound, ApiResponse.error("NOT_FOUND", e.message ?: "Người dùng không tồn tại"))
            }
        }

        // --- PUBLIC GET FOLLOWERS --- (These can be accessed without token, but for InstaGallery, usually token is required. We'll leave outside authenticateblock but verify token optionally if needed. Based on standard, it's public)
        get("/{id}/followers") {
            val userId = call.parameters["id"]?.toLongOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID người dùng không hợp lệ."))

            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 20

            val followersData = interactionService.getFollowers(userId, page, limit)
            call.respond(HttpStatusCode.OK, ApiResponse.success(data = followersData))
        }

        get("/{id}/following") {
            val userId = call.parameters["id"]?.toLongOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID người dùng không hợp lệ."))

            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 20

            val followingData = interactionService.getFollowing(userId, page, limit)
            call.respond(HttpStatusCode.OK, ApiResponse.success(data = followingData))
        }
    }
}
