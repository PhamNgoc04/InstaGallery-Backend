package com.instagallery.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*

fun Application.configureCORS() {
    val configuredHosts = environment.config.propertyOrNull("cors.allowedHosts")
        ?.getList()
        ?.map { it.trim() }
        ?.filter { it.isNotEmpty() }
        .orEmpty()
    val allowedHosts = configuredHosts.ifEmpty {
        listOf(
            "localhost:3000",
            "localhost:5173",
            "localhost:8080",
            "127.0.0.1:3000",
            "127.0.0.1:5173",
            "127.0.0.1:8080",
        )
    }

    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Accept)
        allowedHosts.forEach { host ->
            allowHost(host, schemes = listOf("http", "https"))
        }
    }
}
