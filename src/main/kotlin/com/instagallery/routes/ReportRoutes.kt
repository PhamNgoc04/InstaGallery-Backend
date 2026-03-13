package com.instagallery.routes

import com.instagallery.models.common.ApiResponse
import com.instagallery.models.common.Role
import com.instagallery.models.request.CreateReportRequest
import com.instagallery.models.request.UpdateReportStatusRequest
import com.instagallery.services.ReportService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.reportRoutes() {
    val reportService: ReportService by inject()

    route("/api/v1") {
        
        authenticate("jwt") {
            
            // --- USER: TẠO REPORT ---
            post("/reports") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))

                val request = call.receive<CreateReportRequest>()
                reportService.createReport(userId, request)
                
                call.respond(HttpStatusCode.Created, ApiResponse.success(data = null, message = "Cảm ơn bạn đã báo cáo. Chúng tôi sẽ xem xét trong thời gian sớm nhất."))
            }

            // --- ADMIN: LẤY DANH SÁCH REPORT ---
            get("/admin/reports") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                val role = principal?.payload?.getClaim("role")?.asString()

                if (userId == null || role != Role.ADMIN.name) {
                    return@get call.respond(HttpStatusCode.Forbidden, ApiResponse.error("FORBIDDEN", "Chỉ Admin mới có quyền truy cập."))
                }

                val status = call.request.queryParameters["status"]
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 20

                val reports = reportService.getReports(status, page, limit)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = reports))
            }

            // --- ADMIN: CẬP NHẬT TRẠNG THÁI REPORT ---
            put("/admin/reports/{reportId}") {
                val principal = call.principal<JWTPrincipal>()
                val reviewerId = principal?.payload?.getClaim("userId")?.asLong()
                val role = principal?.payload?.getClaim("role")?.asString()

                if (reviewerId == null || role != Role.ADMIN.name) {
                    return@put call.respond(HttpStatusCode.Forbidden, ApiResponse.error("FORBIDDEN", "Chỉ Admin mới có quyền truy cập."))
                }

                val reportId = call.parameters["reportId"]?.toLongOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID báo cáo không hợp lệ."))

                val request = call.receive<UpdateReportStatusRequest>()
                reportService.updateReport(reportId, reviewerId, request)
                
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = null, message = "Đã cập nhật trạng thái báo cáo."))
            }
        }
    }
}
