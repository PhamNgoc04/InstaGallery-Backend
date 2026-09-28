package com.instagallery.routes

import com.instagallery.models.common.ApiResponse
import com.instagallery.repositories.UserRepository
import com.instagallery.services.SearchService
import com.instagallery.utils.JwtManager
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.getKoin

fun Route.searchRoutes() {
    val searchService = application.getKoin().get<SearchService>()
    val jwtManager = application.getKoin().get<JwtManager>()
    val userRepository = application.getKoin().get<UserRepository>()

    route("/api/v1/search") {
        get {
            val authHeader = call.request.headers["Authorization"]
            var userId: Long? = null

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                val token = authHeader.removePrefix("Bearer ").trim()
                val decoded = jwtManager.verifyTokenSync(token)
                val candidateId = decoded?.getClaim("userId")?.asLong()
                val user = candidateId?.let { userRepository.getUserById(it) }
                if (user != null && user.isActive) {
                    userId = user.id
                }
            }

            val query = call.request.queryParameters["q"] ?: ""
            val type = call.request.queryParameters["type"] ?: "ALL"
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 20

            val result = searchService.searchAll(userId, query, type, limit)
            call.respond(HttpStatusCode.OK, ApiResponse.success(data = result))
        }

        // --- 2. Lịch sử Tìm kiếm (Strict Auth) ---
        authenticate("jwt") {
            get("/history") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))

                val result = searchService.getSearchHistory(userId)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = result))
            }

            delete("/history") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@delete call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))

                searchService.clearSearchHistory(userId)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = null, message = "Đã xóa toàn bộ lịch sử tìm kiếm"))
            }
        }

        // --- FR-24: TRENDING SEARCH TERMS ---
        get("/trending") {
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
            val trending = searchService.getTrendingSearches(limit)
            call.respond(HttpStatusCode.OK, ApiResponse.success(data = trending))
        }
    }
}
