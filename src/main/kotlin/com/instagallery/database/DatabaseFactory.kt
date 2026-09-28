package com.instagallery.database

import com.instagallery.repositories.CommentTreeVisibility
import com.instagallery.utils.RequiredConfig
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import com.instagallery.database.tables.*

// Database Layer (database/): Được cấu hình với connection pool HikariCP (10 connections), 
// chạy cách ly trên thread pool Dispatchers.IO để không bao giờ làm nghẽn luồng xử lý IO của Netty.

object DatabaseFactory {
    fun init(environment: ApplicationEnvironment) {
        val dbUrl = RequiredConfig.databaseUrl(environment.config)
        val dbUser = RequiredConfig.databaseUser(environment.config)
        val dbPassword = RequiredConfig.databasePassword(environment.config)

        val config = HikariConfig().apply {
            driverClassName = "com.mysql.cj.jdbc.Driver"
            jdbcUrl = dbUrl
            username = dbUser
            password = dbPassword
            maximumPoolSize = 10
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }

        val dataSource = HikariDataSource(config)
        Database.connect(dataSource)

        if (RequiredConfig.isProduction(environment.config)) {
            return
        }

        transaction {
            SchemaUtils.createMissingTablesAndColumns(
                UsersTable,
                UserSessionsTable,
                PortfoliosTable,
                PhotographerServicesTable,
                PostsTable,
                FiltersTable,
                PostMediaTable,
                MediaTagsTable,
                PostMediaTagsTable,
                FollowersTable,
                FollowRequestsTable,
                PostTaggedUsersTable,
                LikesTable,
                CommentsTable,
                CommentLikesTable,
                CommentDislikesTable,
                CommentReactionsTable,
                PostSharesTable,
                SavedPostsTable,
                BookingsTable,
                BookingStatusEventsTable,
                RatingsTable,
                ConversationsTable,
                ConversationMembersTable,
                MessagesTable,
                NotificationsTable,
                DeviceTokensTable,
                ActivityLogsTable,
                ReportsTable,
                SearchHistoriesTable,
                AlbumsTable,
                AlbumMediaTable,
                BlockedUsersTable,
                MutedUsersTable,
                BannedWordsTable,
                AvailabilitySchedulesTable,
                PasswordResetTokensTable
            )
            CommentTreeVisibility.syncAllPostCommentCounts()
            backfillCommentReactions()
        }
    }

    private fun backfillCommentReactions() {
        CommentLikesTable.selectAll().forEach { row ->
            copyCommentReaction(
                userId = row[CommentLikesTable.userId].value,
                commentId = row[CommentLikesTable.commentId].value,
                reaction = com.instagallery.models.common.CommentReactionKind.LIKE,
            )
        }
        CommentDislikesTable.selectAll().forEach { row ->
            copyCommentReaction(
                userId = row[CommentDislikesTable.userId].value,
                commentId = row[CommentDislikesTable.commentId].value,
                reaction = com.instagallery.models.common.CommentReactionKind.DISLIKE,
            )
        }
    }

    private fun copyCommentReaction(
        userId: Long,
        commentId: Long,
        reaction: com.instagallery.models.common.CommentReactionKind,
    ) {
        val existing = CommentReactionsTable.selectAll().where {
            (CommentReactionsTable.userId eq userId) and (CommentReactionsTable.commentId eq commentId)
        }.singleOrNull()
        if (existing != null) return
        CommentReactionsTable.insert {
            it[CommentReactionsTable.userId] = userId
            it[CommentReactionsTable.commentId] = commentId
            it[CommentReactionsTable.reaction] = reaction
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO, TransactionManager.defaultDatabase) { block() }
}
