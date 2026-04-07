package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Welcome to InstaGallery API")
        }
        
        route("/auth") {
            post("/register") {
                // TODO: Implement Register FR-01
                call.respondText("Register endpoint")
            }
            post("/login") {
                // TODO: Implement Login FR-02
                call.respondText("Login endpoint")
            }
        }
    }
}
