package com.instagallery.routes

import com.instagallery.models.common.ApiResponse
import com.instagallery.models.request.RegisterDeviceTokenRequest
import com.instagallery.services.DeviceTokenService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import org.koin.ktor.ext.getKoin

fun Route.deviceRoutes() {
    val deviceTokenService = application.getKoin().get<DeviceTokenService>()

    route("/api/v1/devices") {
        authenticate("jwt") {
            post("/fcm-token") {
                val userId = call.userIdOrUnauthorized() ?: return@post
                val request = call.receive<RegisterDeviceTokenRequest>()
                val token = deviceTokenService.registerToken(userId, request)

                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse.success(data = token, message = "FCM token da duoc dang ky."),
                )
            }

            delete("/fcm-token") {
                val userId = call.userIdOrUnauthorized() ?: return@delete
                val request = call.receive<RegisterDeviceTokenRequest>()
                deviceTokenService.unregisterToken(userId, request)

                call.respond(HttpStatusCode.OK, ApiResponse.success(data = mapOf("deleted" to true)))
            }
        }
    }
}

private suspend fun ApplicationCall.userIdOrUnauthorized(): Long? {
    val userId = principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asLong()
    if (userId == null) {
        respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token khong hop le."))
    }
    return userId
}
