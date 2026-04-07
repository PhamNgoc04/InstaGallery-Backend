package com.instagallery.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import com.instagallery.database.tables.*

object DatabaseFactory {
    fun init(environment: ApplicationEnvironment) {
        // Falling back gracefully for when the Android Studio Green Play Button is used
        // which completely bypasses the application.conf file
        val dbUrl = environment.config.propertyOrNull("database.url")?.getString() ?: "jdbc:mysql://localhost:3306/instagallery?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC"
        val dbUser = environment.config.propertyOrNull("database.user")?.getString() ?: "root"
        val dbPassword = environment.config.propertyOrNull("database.password")?.getString() ?: "123456789"

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

//        transaction {
//            SchemaUtils.create(
//                UsersTable, UserSessionsTable, PortfoliosTable,
//                PostsTable, FiltersTable, PostMediaTable, MediaTagsTable, PostMediaTagsTable,
//                FollowersTable, LikesTable, CommentsTable, CommentLikesTable, SavedPostsTable,
//                BookingsTable, RatingsTable, ConversationsTable, ConversationMembersTable, MessagesTable,
//                NotificationsTable, ActivityLogsTable, ReportsTable, SearchHistoriesTable
//            )
//        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}
