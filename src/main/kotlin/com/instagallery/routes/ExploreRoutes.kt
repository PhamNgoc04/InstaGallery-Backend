package com.instagallery.routes

import com.instagallery.models.common.ApiResponse
import com.instagallery.services.PostService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.exploreRoutes() {
    val postService: PostService by inject()

    route("/api/v1/explore") {
        
        // --- PUBLIC: Lấy thẻ Trending ---
        get("/trending") {
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
            val tags = postService.getTrendingTags(limit)
            call.respond(HttpStatusCode.OK, ApiResponse.success(data = tags))
        }

        // --- PUBLIC: Lấy Explore Feed ---
        get {
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 20
            val tag = call.request.queryParameters["tag"]
            
            val feed = postService.getExplorePosts(page, limit, tag)
            call.respond(HttpStatusCode.OK, ApiResponse.success(data = feed))
        }
    }
}
