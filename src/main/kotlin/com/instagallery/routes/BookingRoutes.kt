package com.instagallery.routes

import com.instagallery.models.common.ApiResponse
import com.instagallery.models.request.CreateBookingRequest
import com.instagallery.models.request.CreateRatingRequest
import com.instagallery.models.request.UpdateBookingStatusRequest
import com.instagallery.services.BookingService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import org.koin.ktor.ext.getKoin

fun Route.bookingRoutes() {
    val bookingService = application.getKoin().get<BookingService>()

    route("/api/v1/bookings") {
        authenticate("jwt") {
            post {
                val userId = call.currentUserIdOrUnauthorized() ?: return@post
                val request = call.receive<CreateBookingRequest>()
                val booking = bookingService.createBooking(userId, request)
                call.respond(HttpStatusCode.Created, ApiResponse.success(data = booking, message = "Da gui yeu cau dat lich."))
            }

            get {
                val userId = call.currentUserIdOrUnauthorized() ?: return@get
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
                val status = call.request.queryParameters["status"]

                val result = bookingService.getMyBookings(userId, page, limit, status)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = result))
            }

            get("/{id}") {
                val userId = call.currentUserIdOrUnauthorized() ?: return@get
                val bookingId = call.parameters["id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID booking khong hop le."))

                val booking = bookingService.getBookingDetail(userId, bookingId)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = booking))
            }

            put("/{id}/status") {
                val userId = call.currentUserIdOrUnauthorized() ?: return@put
                val bookingId = call.parameters["id"]?.toLongOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID booking khong hop le."))

                val request = call.receive<UpdateBookingStatusRequest>()
                bookingService.updateBookingStatus(userId, bookingId, request)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = null, message = "Da cap nhat trang thai booking."))
            }

            post("/{id}/review") {
                val userId = call.currentUserIdOrUnauthorized() ?: return@post
                val bookingId = call.parameters["id"]?.toLongOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID booking khong hop le."))

                val request = call.receive<CreateRatingRequest>()
                val review = bookingService.createReview(userId, bookingId, request)
                call.respond(HttpStatusCode.Created, ApiResponse.success(data = review, message = "Cam on ban da gui danh gia."))
            }

            delete("/{id}") {
                val userId = call.currentUserIdOrUnauthorized() ?: return@delete
                val bookingId = call.parameters["id"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID booking khong hop le."))

                bookingService.cancelBooking(userId, bookingId)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = null, message = "Da huy booking."))
            }
        }
    }
}

private suspend fun io.ktor.server.application.ApplicationCall.currentUserIdOrUnauthorized(): Long? {
    val userId = principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asLong()
    if (userId == null) {
        respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token khong hop le."))
    }
    return userId
}
