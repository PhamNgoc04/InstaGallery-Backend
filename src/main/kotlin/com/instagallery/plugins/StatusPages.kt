package com.instagallery.plugins

import com.instagallery.models.common.ApiResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import kotlinx.serialization.SerializationException

// Custom exceptions based on the roadmap
class AuthException(val code: String, message: String) : RuntimeException(message)
class ValidationException(val code: String, message: String, val details: List<String>? = null) : RuntimeException(message)
class NotFoundException(message: String) : RuntimeException(message)
class ForbiddenException(message: String) : RuntimeException(message)

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<AuthException> { call, cause ->
            call.respond(HttpStatusCode.Unauthorized, ApiResponse.error(cause.code, cause.message ?: "Auth error"))
        }

        exception<ValidationException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, ApiResponse.error(cause.code, cause.message ?: "", cause.details))
        }

        exception<NotFoundException> { call, cause ->
            call.respond(HttpStatusCode.NotFound, ApiResponse.error("NOT_FOUND", cause.message ?: "Resource not found"))
        }

        exception<ForbiddenException> { call, cause ->
            call.respond(HttpStatusCode.Forbidden, ApiResponse.error("FORBIDDEN", cause.message ?: "Access denied"))
        }

        exception<BadRequestException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_REQUEST_BODY", cause.message ?: "Invalid request body"))
        }

        exception<SerializationException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, ApiResponse.error("INVALID_REQUEST_BODY", cause.message ?: "Invalid JSON body"))
        }

        exception<Throwable> { call, cause ->
            call.application.log.error("Unhandled exception", cause)
            call.respond(HttpStatusCode.InternalServerError, ApiResponse.error("INTERNAL_ERROR", "Something went wrong"))
        }
    }
}
