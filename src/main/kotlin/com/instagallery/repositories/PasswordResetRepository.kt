package com.instagallery.repositories

import com.instagallery.database.DatabaseFactory.dbQuery
import com.instagallery.database.tables.PasswordResetTokensTable
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.selectAll
import java.time.LocalDateTime
import java.time.ZoneId

class PasswordResetRepository {

    suspend fun saveToken(userId: Long, token: String, expiredAt: LocalDateTime): Long? = dbQuery {
        val insertStatement = PasswordResetTokensTable.insertAndGetId {
            it[PasswordResetTokensTable.userId] = userId
            it[PasswordResetTokensTable.token] = token
            it[PasswordResetTokensTable.expiredAt] = expiredAt
        }
        insertStatement.value
    }

    suspend fun getToken(token: String): ResultRow? = dbQuery {
        PasswordResetTokensTable.selectAll().where { PasswordResetTokensTable.token eq token }.singleOrNull()
    }

    suspend fun deleteTokensByUserId(userId: Long): Boolean = dbQuery {
        val rows = PasswordResetTokensTable.deleteWhere { PasswordResetTokensTable.userId eq userId }
        rows > 0
    }
}
