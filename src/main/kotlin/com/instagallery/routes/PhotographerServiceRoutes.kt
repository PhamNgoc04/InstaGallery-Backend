package com.instagallery.routes

import com.instagallery.models.common.ApiResponse
import com.instagallery.models.request.UpdatePhotographerServiceStatusRequest
import com.instagallery.models.request.UpsertPhotographerServiceRequest
import com.instagallery.services.PhotographerServiceService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import org.koin.ktor.ext.getKoin

fun Route.photographerServiceRoutes() {
    val service = application.getKoin().get<PhotographerServiceService>()

    route("/api/v1/users/{photographerId}/services") {
        get {
            val photographerId = call.parameters["photographerId"]?.toLongOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID nhiep anh gia khong hop le."))

            val result = service.listPublic(photographerId)
            call.respond(HttpStatusCode.OK, ApiResponse.success(data = result))
        }
    }

    route("/api/v1/photographer/services") {
        authenticate("jwt") {
            get {
                val photographerId = call.photographerIdOrUnauthorized() ?: return@get
                val result = service.listMine(photographerId)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = result))
            }

            post {
                val photographerId = call.photographerIdOrUnauthorized() ?: return@post
                val request = call.receive<UpsertPhotographerServiceRequest>()
                val created = service.create(photographerId, request)
                call.respond(HttpStatusCode.Created, ApiResponse.success(data = created, message = "Da tao goi chup."))
            }

            get("/{id}") {
                val photographerId = call.photographerIdOrUnauthorized() ?: return@get
                val serviceId = call.parameters["id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID goi chup khong hop le."))
                val result = service.getMine(photographerId, serviceId)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = result))
            }

            put("/{id}") {
                val photographerId = call.photographerIdOrUnauthorized() ?: return@put
                val serviceId = call.parameters["id"]?.toLongOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID goi chup khong hop le."))
                val request = call.receive<UpsertPhotographerServiceRequest>()
                val updated = service.update(photographerId, serviceId, request)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = updated, message = "Da cap nhat goi chup."))
            }

            put("/{id}/status") {
                val photographerId = call.photographerIdOrUnauthorized() ?: return@put
                val serviceId = call.parameters["id"]?.toLongOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID goi chup khong hop le."))
                val request = call.receive<UpdatePhotographerServiceStatusRequest>()
                service.setActive(photographerId, serviceId, request.isActive)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = null, message = "Da cap nhat trang thai goi chup."))
            }

            delete("/{id}") {
                val photographerId = call.photographerIdOrUnauthorized() ?: return@delete
                val serviceId = call.parameters["id"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_ID", "ID goi chup khong hop le."))
                service.archive(photographerId, serviceId)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = null, message = "Da tam dung goi chup."))
            }
        }
    }
}

private suspend fun io.ktor.server.application.ApplicationCall.photographerIdOrUnauthorized(): Long? {
    val photographerId = principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asLong()
    if (photographerId == null) {
        respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token khong hop le."))
    }
    return photographerId
}
