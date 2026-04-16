package com.instagallery.routes

import com.instagallery.models.common.ApiResponse
import com.instagallery.models.common.Role
import com.instagallery.models.request.BanUserRequest
import com.instagallery.services.AdminService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.getKoin

fun Route.adminRoutes() {
    val adminService = application.getKoin().get<AdminService>()

    route("/api/v1/admin") {
        
        authenticate("jwt") {
            
            // --- CẦN ROLE ADMIN ---
            intercept(ApplicationCallPipeline.Call) {
                val principal = call.principal<JWTPrincipal>()
                val role = principal?.payload?.getClaim("role")?.asString()

                if (role != Role.ADMIN.name) {
                    call.respond(HttpStatusCode.Forbidden, ApiResponse.error("FORBIDDEN", "Chỉ Admin mới có quyền truy cập."))
                    finish()
                }
            }

            // --- TỔNG QUAN HỆ THỐNG ---
            get("/stats") {
                val stats = adminService.getOverviewStats()
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = stats))
            }

            // --- BIỂU ĐỒ TĂNG TRƯỞNG ---
            get("/stats/growth") {
                val type = call.request.queryParameters["type"] ?: "USERS"
                val days = call.request.queryParameters["days"]?.toIntOrNull() ?: 7

                val growth = adminService.getGrowth(type, days)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = growth))
            }

            // --- QUẢN TRỊ NGƯỜI DÙNG: BAN / UNBAN ---
            put("/users/{userId}/ban") {
                val targetUserId = call.parameters["userId"]?.toLongOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID người dùng không hợp lệ."))

                val request = call.receive<BanUserRequest>()
                adminService.banUser(targetUserId, request)

                val msg = if (request.isBanned) "Đã khóa người dùng thành công." else "Đã mở khóa người dùng thành công."
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = null, message = msg))
            }

            // --- QUẢN TRỊ BÀI VIẾT: DELETE POST ---
            delete("/posts/{postId}") {
                val postId = call.parameters["postId"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID bài viết không hợp lệ."))

                adminService.deletePost(postId)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = null, message = "Bài viết đã bị xóa bởi Quản trị viên."))
            }

            // --- QUẢN TRỊ BÌNH LUẬN: DELETE COMMENT ---
            delete("/comments/{commentId}") {
                val commentId = call.parameters["commentId"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID bình luận không hợp lệ."))

                adminService.deleteComment(commentId)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = null, message = "Bình luận đã bị xóa bởi Quản trị viên."))
            }

            // --- FR-43: QUẢN LÝ NGƯỜI DÙNG ---
            get("/users") {
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 20
                val search = call.request.queryParameters["search"]
                val status = call.request.queryParameters["status"] // ACTIVE, BANNED, DEACTIVATED

                val users = adminService.listUsers(page, limit, search, status)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = users))
            }

            get("/users/{userId}") {
                val targetUserId = call.parameters["userId"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID người dùng không hợp lệ."))

                val user = adminService.getUserDetail(targetUserId)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = user))
            }

            // --- FR-46: TỪ KHÓA CẤM ---
            get("/banned-keywords") {
                val keywords = adminService.getBannedKeywords()
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = keywords))
            }

            post("/banned-keywords") {
                val body = call.receiveText()
                val keyword = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
                    .decodeFromString<Map<String, String>>(body)["keyword"]
                    ?: return@post call.respond(HttpStatusCode.BadRequest, ApiResponse.error("MISSING_FIELD", "Thiếu trường keyword."))

                adminService.addBannedKeyword(keyword)
                call.respond(HttpStatusCode.Created, ApiResponse.success(data = null, message = "Đã thêm từ khóa cấm: \"$keyword\"."))
            }

            delete("/banned-keywords/{id}") {
                val keywordId = call.parameters["id"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID từ khóa không hợp lệ."))

                adminService.removeBannedKeyword(keywordId)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = null, message = "Đã xóa từ khóa cấm."))
            }
        }
    }
}
