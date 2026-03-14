package com.instagallery.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import com.instagallery.database.tables.*
import java.util.UUID

object TestDatabaseFactory {
    private var isInitialized = false

    fun init() {
        if (isInitialized) return
        isInitialized = true
        // Use a unique database name per test run to ensure total isolation.
        val uniqueDbName = UUID.randomUUID().toString()
        val config = HikariConfig().apply {
            driverClassName = "org.h2.Driver"
            // Mode=MySQL ensures syntax compatibility with Exposed's MySQL functions
            jdbcUrl = "jdbc:h2:mem:$uniqueDbName;MODE=MySQL;DB_CLOSE_DELAY=-1;IGNORECASE=TRUE"
            maximumPoolSize = 5
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }

        val dataSource = HikariDataSource(config)
        Database.connect(dataSource)

        transaction {
            SchemaUtils.create(
                UsersTable, UserSessionsTable, PortfoliosTable,
                PostsTable, FiltersTable, PostMediaTable, MediaTagsTable, PostMediaTagsTable,
                FollowersTable, LikesTable, CommentsTable, CommentLikesTable, SavedPostsTable,
                BookingsTable, RatingsTable, ConversationsTable, ConversationMembersTable, MessagesTable,
                NotificationsTable, ActivityLogsTable, ReportsTable, SearchHistoriesTable, PasswordResetTokensTable
            )
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}
