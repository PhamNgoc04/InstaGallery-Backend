package com.instagallery.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*

fun Application.configureSecurity() {
    val jwtSecret = environment.config.propertyOrNull("jwt.secret")?.getString() ?: "my-super-secret-key-for-instagallery-app-which-is-at-least-32-bytes"
    val jwtIssuer = environment.config.propertyOrNull("jwt.issuer")?.getString() ?: "http://localhost:8080/"
    val jwtAudience = environment.config.propertyOrNull("jwt.audience")?.getString() ?: "http://localhost:8080/api/v1"

    install(Authentication) {
        jwt("jwt") {
            verifier(
                JWT.require(Algorithm.HMAC256(jwtSecret))
                    .withIssuer(jwtIssuer)
                    .withAudience(jwtAudience)
                    .build()
            )

            validate { credential ->
                if (credential.payload.getClaim("userId").asLong() != null)
                    JWTPrincipal(credential.payload)
                else null
            }

            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "UNAUTHORIZED", "message" to "Token invalid or expired"))
            }
        }
    }
}
