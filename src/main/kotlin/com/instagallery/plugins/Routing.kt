package com.instagallery.plugins

import com.instagallery.routes.authRoutes
import com.instagallery.routes.bookingRoutes
import com.instagallery.routes.chatRoutes
import com.instagallery.routes.deviceRoutes
import com.instagallery.routes.interactionRoutes
import com.instagallery.routes.notificationRoutes
import com.instagallery.routes.postRoutes
import com.instagallery.routes.searchRoutes
import com.instagallery.routes.userRoutes
import com.instagallery.routes.portfolioRoutes
import com.instagallery.routes.photographerServiceRoutes
import com.instagallery.routes.ratingRoutes
import com.instagallery.routes.mediaRoutes
import com.instagallery.routes.exploreRoutes
import com.instagallery.routes.reportRoutes
import com.instagallery.routes.adminRoutes
import com.instagallery.routes.albumRoutes
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
                    com.instagallery.database.tables.UsersTable, com.instagallery.database.tables.UserSessionsTable, com.instagallery.database.tables.PortfoliosTable, com.instagallery.database.tables.PhotographerServicesTable,
                    com.instagallery.database.tables.PostsTable, com.instagallery.database.tables.FiltersTable, com.instagallery.database.tables.PostMediaTable, com.instagallery.database.tables.MediaTagsTable, com.instagallery.database.tables.PostMediaTagsTable,
                    com.instagallery.database.tables.FollowersTable, com.instagallery.database.tables.LikesTable, com.instagallery.database.tables.CommentsTable, com.instagallery.database.tables.CommentLikesTable, com.instagallery.database.tables.SavedPostsTable,
                    com.instagallery.database.tables.BookingsTable, com.instagallery.database.tables.RatingsTable, com.instagallery.database.tables.ConversationsTable, com.instagallery.database.tables.ConversationMembersTable, com.instagallery.database.tables.MessagesTable,
                    com.instagallery.database.tables.NotificationsTable, com.instagallery.database.tables.DeviceTokensTable, com.instagallery.database.tables.ActivityLogsTable, com.instagallery.database.tables.ReportsTable, com.instagallery.database.tables.SearchHistoriesTable, com.instagallery.database.tables.PasswordResetTokensTable,
                    com.instagallery.database.tables.FollowRequestsTable, com.instagallery.database.tables.AlbumsTable, com.instagallery.database.tables.AlbumMediaTable, com.instagallery.database.tables.BlockedUsersTable, com.instagallery.database.tables.MutedUsersTable, com.instagallery.database.tables.AvailabilitySchedulesTable, com.instagallery.database.tables.BannedWordsTable
                )
            }
            call.respondText("Tự động tạo 30 Bảng Dữ Liệu thành công!", status = HttpStatusCode.OK)
        }

        get("/reset-db") {
            org.jetbrains.exposed.sql.transactions.transaction {
                // Xoá trọn bộ các bảng dữ liệu cũ
                org.jetbrains.exposed.sql.SchemaUtils.drop(
                    com.instagallery.database.tables.UsersTable, com.instagallery.database.tables.UserSessionsTable, com.instagallery.database.tables.PortfoliosTable, com.instagallery.database.tables.PhotographerServicesTable,
                    com.instagallery.database.tables.PostsTable, com.instagallery.database.tables.FiltersTable, com.instagallery.database.tables.PostMediaTable, com.instagallery.database.tables.MediaTagsTable, com.instagallery.database.tables.PostMediaTagsTable,
                    com.instagallery.database.tables.FollowersTable, com.instagallery.database.tables.LikesTable, com.instagallery.database.tables.CommentsTable, com.instagallery.database.tables.CommentLikesTable, com.instagallery.database.tables.SavedPostsTable,
                    com.instagallery.database.tables.BookingsTable, com.instagallery.database.tables.RatingsTable, com.instagallery.database.tables.ConversationsTable, com.instagallery.database.tables.ConversationMembersTable, com.instagallery.database.tables.MessagesTable,
                    com.instagallery.database.tables.NotificationsTable, com.instagallery.database.tables.DeviceTokensTable, com.instagallery.database.tables.ActivityLogsTable, com.instagallery.database.tables.ReportsTable, com.instagallery.database.tables.SearchHistoriesTable, com.instagallery.database.tables.PasswordResetTokensTable,
                    com.instagallery.database.tables.FollowRequestsTable, com.instagallery.database.tables.AlbumsTable, com.instagallery.database.tables.AlbumMediaTable, com.instagallery.database.tables.BlockedUsersTable, com.instagallery.database.tables.MutedUsersTable, com.instagallery.database.tables.AvailabilitySchedulesTable, com.instagallery.database.tables.BannedWordsTable
                )
                // Tạo lại bảng mới tinh tươm
                org.jetbrains.exposed.sql.SchemaUtils.create(
                    com.instagallery.database.tables.UsersTable, com.instagallery.database.tables.UserSessionsTable, com.instagallery.database.tables.PortfoliosTable, com.instagallery.database.tables.PhotographerServicesTable,
                    com.instagallery.database.tables.PostsTable, com.instagallery.database.tables.FiltersTable, com.instagallery.database.tables.PostMediaTable, com.instagallery.database.tables.MediaTagsTable, com.instagallery.database.tables.PostMediaTagsTable,
                    com.instagallery.database.tables.FollowersTable, com.instagallery.database.tables.LikesTable, com.instagallery.database.tables.CommentsTable, com.instagallery.database.tables.CommentLikesTable, com.instagallery.database.tables.SavedPostsTable,
                    com.instagallery.database.tables.BookingsTable, com.instagallery.database.tables.RatingsTable, com.instagallery.database.tables.ConversationsTable, com.instagallery.database.tables.ConversationMembersTable, com.instagallery.database.tables.MessagesTable,
                    com.instagallery.database.tables.NotificationsTable, com.instagallery.database.tables.DeviceTokensTable, com.instagallery.database.tables.ActivityLogsTable, com.instagallery.database.tables.ReportsTable, com.instagallery.database.tables.SearchHistoriesTable, com.instagallery.database.tables.PasswordResetTokensTable,
                    com.instagallery.database.tables.FollowRequestsTable, com.instagallery.database.tables.AlbumsTable, com.instagallery.database.tables.AlbumMediaTable, com.instagallery.database.tables.BlockedUsersTable, com.instagallery.database.tables.MutedUsersTable, com.instagallery.database.tables.AvailabilitySchedulesTable, com.instagallery.database.tables.BannedWordsTable
                )
            }
            call.respondText("Đã dọn dẹp và Reset toàn bộ Database! ID sẽ bắt đầu lại từ 1.", status = HttpStatusCode.OK)
        }

        get("/fix-user-id") {
            org.jetbrains.exposed.sql.transactions.transaction {
                try {
                    // Update user ID from 2 to 1
                    exec("UPDATE users SET id = 1 WHERE id = 2;")
                    // Reset Auto Increment so the next created user is ID 2
                    exec("ALTER TABLE users AUTO_INCREMENT = 2;")
                } catch (e: Exception) {
                    System.err.println("Error executing fix: ${e.message}")
                }
            }
            call.respondText("Thành công: Đã ép User ID 2 lùi về 1, và Set Auto_Increment tiếp theo là 2!", status = HttpStatusCode.OK)
        }

        get("/migrate-db") {
            org.jetbrains.exposed.sql.transactions.transaction {
                org.jetbrains.exposed.sql.SchemaUtils.createMissingTablesAndColumns(
                    com.instagallery.database.tables.UsersTable, com.instagallery.database.tables.UserSessionsTable, com.instagallery.database.tables.PortfoliosTable, com.instagallery.database.tables.PhotographerServicesTable,
                    com.instagallery.database.tables.PostsTable, com.instagallery.database.tables.FiltersTable, com.instagallery.database.tables.PostMediaTable, com.instagallery.database.tables.MediaTagsTable, com.instagallery.database.tables.PostMediaTagsTable,
                    com.instagallery.database.tables.FollowersTable, com.instagallery.database.tables.LikesTable, com.instagallery.database.tables.CommentsTable, com.instagallery.database.tables.CommentLikesTable, com.instagallery.database.tables.SavedPostsTable,
                    com.instagallery.database.tables.BookingsTable, com.instagallery.database.tables.RatingsTable, com.instagallery.database.tables.ConversationsTable, com.instagallery.database.tables.ConversationMembersTable, com.instagallery.database.tables.MessagesTable,
                    com.instagallery.database.tables.NotificationsTable, com.instagallery.database.tables.DeviceTokensTable, com.instagallery.database.tables.ActivityLogsTable, com.instagallery.database.tables.ReportsTable, com.instagallery.database.tables.SearchHistoriesTable, com.instagallery.database.tables.PasswordResetTokensTable,
                    com.instagallery.database.tables.FollowRequestsTable, com.instagallery.database.tables.AlbumsTable, com.instagallery.database.tables.AlbumMediaTable, com.instagallery.database.tables.BlockedUsersTable, com.instagallery.database.tables.MutedUsersTable, com.instagallery.database.tables.AvailabilitySchedulesTable, com.instagallery.database.tables.BannedWordsTable
                )
            }
            call.respondText("Thành công: Đã tự động chèn các cột còn thiếu (provider, is_private...) vào Database mà không làm mất dữ liệu!", status = HttpStatusCode.OK)
        }
        
        // Register feature routes here
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
