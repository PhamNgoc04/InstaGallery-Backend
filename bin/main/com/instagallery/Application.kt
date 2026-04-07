package com.instagallery

import com.instagallery.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

import java.io.File

fun main() {
    try {
    
        embeddedServer(Netty, port = 8080, module = Application::module).start(wait = true)
    } catch (e: Throwable) {
        File("crash_report.txt").writeText("KTOR CRASH REPORT:\n${e.stackTraceToString()}")
        throw e
    }
}

fun Application.module() {
    configureDependencyInjection() // Must be first
    configureSerialization()    // JSON
    configureSecurity()         // JWT
    configureCORS()
    configureDatabase()         // MySQL + Exposed
    configureRateLimiting()
    configureStatusPages()      // Error handling
    configureSockets()          // WebSockets
    configureRouting()          // All routes
}
