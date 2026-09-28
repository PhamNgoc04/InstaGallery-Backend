package com.instagallery.plugins

import com.instagallery.routes.adminRoutes
import com.instagallery.routes.albumRoutes
import com.instagallery.routes.authRoutes
import com.instagallery.routes.bookingRoutes
import com.instagallery.routes.chatRoutes
import com.instagallery.routes.deviceRoutes
import com.instagallery.routes.exploreRoutes
import com.instagallery.routes.interactionRoutes
import com.instagallery.routes.mediaRoutes
import com.instagallery.routes.notificationRoutes
import com.instagallery.routes.photographerServiceRoutes
import com.instagallery.routes.portfolioRoutes
import com.instagallery.routes.postRoutes
import com.instagallery.routes.ratingRoutes
import com.instagallery.routes.reportRoutes
import com.instagallery.routes.searchRoutes
import com.instagallery.routes.userRoutes
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    configureRateLimiting()

    routing {
        get("/") {
            call.respondText("InstaGallery Backend is running! (Cổng 8080)", status = HttpStatusCode.OK)
        }

        get("/health") {
            call.respondText("InstaGallery Backend is running!", status = HttpStatusCode.OK)
        }

        authRoutes()
        userRoutes()
        postRoutes()
        interactionRoutes()
        bookingRoutes()
        chatRoutes()
        deviceRoutes()
        notificationRoutes()
        searchRoutes()
        portfolioRoutes()
        photographerServiceRoutes()
        ratingRoutes()
        mediaRoutes()
        exploreRoutes()
        reportRoutes()
        adminRoutes()
        albumRoutes()
    }
}
