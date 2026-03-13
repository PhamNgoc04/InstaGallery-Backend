package com.instagallery.repositories

import com.instagallery.database.DatabaseFactory.dbQuery
import com.instagallery.database.tables.UserSessionsTable
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import java.time.LocalDateTime
import java.time.ZoneId

class SessionRepository {

    suspend fun createSession(userId: Long, deviceInfo: String?, ipAddress: String?, refreshToken: String, expiredAt: LocalDateTime): Long? = dbQuery {
        val insertStatement = UserSessionsTable.insertAndGetId {
            it[UserSessionsTable.userId] = userId
            it[UserSessionsTable.deviceInfo] = deviceInfo
            it[UserSessionsTable.ipAddress] = ipAddress
            it[UserSessionsTable.refreshToken] = refreshToken
            it[UserSessionsTable.expiredAt] = expiredAt.atZone(ZoneId.systemDefault()).toInstant()
        }
        insertStatement.value
    }
    suspend fun getSessionByRefreshToken(refreshToken: String): ResultRow? = dbQuery {
        UserSessionsTable.selectAll().where { UserSessionsTable.refreshToken eq refreshToken }.singleOrNull()
    }

    suspend fun getSessionsByUserId(userId: Long): List<ResultRow> = dbQuery {
        UserSessionsTable.selectAll().where { UserSessionsTable.userId eq userId }.toList()
    }

    suspend fun deleteSessionByToken(refreshToken: String): Boolean = dbQuery {
        val rows = UserSessionsTable.deleteWhere { UserSessionsTable.refreshToken eq refreshToken }
        rows > 0
    }

    suspend fun deleteSessionByIdAndUser(sessionId: Long, userId: Long): Boolean = dbQuery {
        val rows = UserSessionsTable.deleteWhere { (UserSessionsTable.id eq sessionId) and (UserSessionsTable.userId eq userId) }
        rows > 0
    }

    suspend fun deleteAllSessionsForUser(userId: Long): Boolean = dbQuery {
        val rows = UserSessionsTable.deleteWhere { UserSessionsTable.userId eq userId }
        rows > 0
    }
}
