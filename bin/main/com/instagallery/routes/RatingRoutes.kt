package com.instagallery.routes

import com.instagallery.models.common.ApiResponse
import com.instagallery.models.request.CreateRatingRequest
import com.instagallery.services.RatingService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.getKoin

fun Route.ratingRoutes() {
    val ratingService = application.getKoin().get<RatingService>()

    route("/api/v1") {
        
        // --- PUBLIC: Lấy list các Rating của một Nhiếp Ảnh Gia ---
        get("/users/{photographerId}/ratings") {
            val photographerId = call.parameters["photographerId"]?.toLongOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID Nhiếp ảnh gia không hợp lệ."))

            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10

            val paginatedResult = ratingService.getRatings(photographerId, page, limit)
            call.respond(HttpStatusCode.OK, ApiResponse.success(data = paginatedResult))
        }

        authenticate("jwt") {
            // --- PROTECTED: Gửi Đánh Giá ---
            post("/users/{photographerId}/ratings") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))

                val photographerId = call.parameters["photographerId"]?.toLongOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID Nhiếp ảnh gia không hợp lệ."))

                // API chuẩn thì the Booking ID is usually sent in Body or Query. We use body `CreateRatingRequest`.
                // Actually, `CreateRatingRequest` currently lacks `bookingId`. Let's assume it's sent in query for simplicity
                val bookingId = call.request.queryParameters["bookingId"]?.toLongOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, ApiResponse.error("MISSING_BOOKING_ID", "Cần truyền bookingId trên Query String."))

                val request = call.receive<CreateRatingRequest>()
                val newRating = ratingService.createRating(bookingId, userId, photographerId, request)

                call.respond(HttpStatusCode.Created, ApiResponse.success(data = newRating, message = "Cảm ơn bạn đã gửi đánh giá."))
            }

            // --- PROTECTED: Xóa Đánh Giá ---
            delete("/ratings/{ratingId}") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@delete call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))

                val ratingId = call.parameters["ratingId"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID đánh giá không hợp lệ."))

                ratingService.deleteRating(userId, ratingId)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = null, message = "Đã xóa đánh giá."))
            }
        }
    }
}
