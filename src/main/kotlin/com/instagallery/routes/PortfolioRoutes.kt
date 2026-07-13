package com.instagallery.routes

import com.instagallery.models.common.ApiResponse
import com.instagallery.models.common.AvailabilityScheduleDto
import com.instagallery.models.request.UpdatePortfolioRequest
import com.instagallery.services.PortfolioService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.getKoin

fun Route.portfolioRoutes() {
    val portfolioService = application.getKoin().get<PortfolioService>()

    route("/api/v1/portfolios") {
        
        // --- PUBLIC: List and Search Photographers ---
        get {
            val location = call.request.queryParameters["location"]
            val specialty = call.request.queryParameters["specialty"]
            val minRate = call.request.queryParameters["minRate"]?.toDoubleOrNull()
            val maxRate = call.request.queryParameters["maxRate"]?.toDoubleOrNull()
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 20

            val paginatedResult = portfolioService.discoverPhotographers(location, specialty, minRate, maxRate, page, limit)
            call.respond(HttpStatusCode.OK, ApiResponse.success(data = paginatedResult))
        }

        // --- PUBLIC: Get Specific Photographer's Portfolio ---
        get("/users/{userId}") {
            val userId = call.parameters["userId"]?.toLongOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID Nhiếp ảnh gia không hợp lệ."))

            val portfolio = portfolioService.getUserPortfolio(userId)
            call.respond(HttpStatusCode.OK, ApiResponse.success(data = portfolio))
        }

        // --- PUBLIC: Get Specific Photographer's Availability ---
        get("/users/{userId}/availability") {
            val userId = call.parameters["userId"]?.toLongOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID Nhiếp ảnh gia không hợp lệ."))

            val availability = portfolioService.getAvailability(userId)
            call.respond(HttpStatusCode.OK, ApiResponse.success(data = availability))
        }

        // --- PROTECTED: Manage Own Portfolio ---
        authenticate("jwt") {
            
            get("/me") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))

                val portfolio = portfolioService.getMyPortfolio(userId)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = portfolio))
            }

            put("/me") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@put call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))

                val request = call.receive<UpdatePortfolioRequest>()
                val updatedPortfolio = portfolioService.updatePortfolio(userId, request)

                call.respond(HttpStatusCode.OK, ApiResponse.success(data = updatedPortfolio, message = "Cập nhật hồ sơ nhiếp ảnh gia thành công."))
            }

            post("/me/availability") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))
                
                val schedules = call.receive<List<AvailabilityScheduleDto>>()
                portfolioService.updateAvailability(userId, schedules)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = null, message = "Cập nhật thời gian làm việc thành công."))
            }

            get("/me/availability") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))
                
                val availability = portfolioService.getAvailability(userId)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = availability))
            }
        }
    }
}
