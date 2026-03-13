package com.instagallery.routes

import com.instagallery.models.common.ApiResponse
import com.instagallery.models.request.CreateBookingRequest
import com.instagallery.models.request.UpdateBookingStatusRequest
import com.instagallery.services.BookingService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.bookingRoutes() {
    val bookingService: BookingService by inject()

    route("/api/v1/bookings") {
        
        authenticate("jwt") {
            
            post {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))

                val request = call.receive<CreateBookingRequest>()
                val booking = bookingService.createBooking(userId, request)
                
                call.respond(HttpStatusCode.Created, ApiResponse.success(data = booking, message = "Đã gửi yêu cầu đặt lịch thành công"))
            }

            get {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))

                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
                val status = call.request.queryParameters["status"]

                val result = bookingService.getMyBookings(userId, page, limit, status)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = result))
            }

            put("/{id}/status") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@put call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))

                val bookingId = call.parameters["id"]?.toLongOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID đơn đặt lịch không hợp lệ."))

                val request = call.receive<UpdateBookingStatusRequest>()
                bookingService.updateBookingStatus(userId, bookingId, request)
                
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = null, message = "Trạng thái đơn hàng đã được cập nhật thành ${request.status}"))
            }
        }
    }
}
