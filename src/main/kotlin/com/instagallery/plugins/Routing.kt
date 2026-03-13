package com.instagallery.plugins

import com.instagallery.routes.authRoutes
import com.instagallery.routes.bookingRoutes
import com.instagallery.routes.chatRoutes
import com.instagallery.routes.interactionRoutes
import com.instagallery.routes.notificationRoutes
import com.instagallery.routes.postRoutes
import com.instagallery.routes.searchRoutes
import com.instagallery.routes.userRoutes
import com.instagallery.routes.portfolioRoutes
import com.instagallery.routes.ratingRoutes
import com.instagallery.routes.mediaRoutes
import com.instagallery.routes.exploreRoutes
import com.instagallery.routes.reportRoutes
import com.instagallery.routes.adminRoutes
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/health") {
            call.respondText("InstaGallery Backend is running!", status = HttpStatusCode.OK)
        }

        get("/init-db") {
            org.jetbrains.exposed.sql.transactions.transaction {
                org.jetbrains.exposed.sql.SchemaUtils.create(
                    com.instagallery.database.tables.UsersTable, com.instagallery.database.tables.UserSessionsTable, com.instagallery.database.tables.PortfoliosTable,
                    com.instagallery.database.tables.PostsTable, com.instagallery.database.tables.FiltersTable, com.instagallery.database.tables.PostMediaTable, com.instagallery.database.tables.MediaTagsTable, com.instagallery.database.tables.PostMediaTagsTable,
                    com.instagallery.database.tables.FollowersTable, com.instagallery.database.tables.LikesTable, com.instagallery.database.tables.CommentsTable, com.instagallery.database.tables.CommentLikesTable, com.instagallery.database.tables.SavedPostsTable,
                    com.instagallery.database.tables.BookingsTable, com.instagallery.database.tables.RatingsTable, com.instagallery.database.tables.ConversationsTable, com.instagallery.database.tables.ConversationMembersTable, com.instagallery.database.tables.MessagesTable,
                    com.instagallery.database.tables.NotificationsTable, com.instagallery.database.tables.ActivityLogsTable, com.instagallery.database.tables.ReportsTable, com.instagallery.database.tables.SearchHistoriesTable, com.instagallery.database.tables.PasswordResetTokensTable
                )
            }
            call.respondText("Tự động tạo 22 Bảng Dữ Liệu thành công!", status = HttpStatusCode.OK)
        }
        
        // Register feature routes here
        authRoutes()
        userRoutes()
        postRoutes()
        interactionRoutes()
        bookingRoutes()
        chatRoutes()
        notificationRoutes()
        searchRoutes()
        portfolioRoutes()
        ratingRoutes()
        mediaRoutes()
        exploreRoutes()
        reportRoutes()
        adminRoutes()

        route("/api/v1") {
            // Include modular routes here like authRoutes(), userRoutes(), etc.
        }
    }
}
