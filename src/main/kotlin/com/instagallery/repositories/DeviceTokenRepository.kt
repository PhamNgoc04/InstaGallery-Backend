package com.instagallery.repositories

import com.instagallery.database.DatabaseFactory.dbQuery
import com.instagallery.database.tables.DeviceTokensTable
import com.instagallery.database.tables.UsersTable
import com.instagallery.models.common.DeviceTokenDto
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.time.Instant

class DeviceTokenRepository {
    suspend fun upsertToken(
        userId: Long,
        token: String,
        platform: String,
        deviceId: String?,
        appVersion: String?,
    ): DeviceTokenDto = dbQuery {
        val now = Instant.now()
        val existing = DeviceTokensTable
            .selectAll()
            .where { DeviceTokensTable.token eq token }
            .singleOrNull()

        val tokenId = if (existing == null) {
            DeviceTokensTable.insertAndGetId {
                it[DeviceTokensTable.userId] = EntityID(userId, UsersTable)
                it[DeviceTokensTable.token] = token
                it[DeviceTokensTable.platform] = platform
                it[DeviceTokensTable.deviceId] = deviceId
                it[DeviceTokensTable.appVersion] = appVersion
                it[DeviceTokensTable.createdAt] = now
                it[DeviceTokensTable.updatedAt] = now
                it[DeviceTokensTable.lastSeenAt] = now
            }.value
        } else {
            val existingId = existing[DeviceTokensTable.id].value
            DeviceTokensTable.update({ DeviceTokensTable.id eq existingId }) {
                it[DeviceTokensTable.userId] = EntityID(userId, UsersTable)
                it[DeviceTokensTable.platform] = platform
                it[DeviceTokensTable.deviceId] = deviceId
                it[DeviceTokensTable.appVersion] = appVersion
                it[DeviceTokensTable.updatedAt] = now
                it[DeviceTokensTable.lastSeenAt] = now
            }
            existingId
        }

        DeviceTokensTable
            .selectAll()
            .where { DeviceTokensTable.id eq tokenId }
            .single()
            .toDeviceTokenDto()
    }

    suspend fun getTokensForUser(userId: Long): List<String> = dbQuery {
        DeviceTokensTable
            .select(DeviceTokensTable.token)
            .where { DeviceTokensTable.userId eq userId }
            .map { it[DeviceTokensTable.token] }
    }

    suspend fun deleteUserToken(userId: Long, token: String): Boolean = dbQuery {
        DeviceTokensTable.deleteWhere {
            (DeviceTokensTable.userId eq userId) and (DeviceTokensTable.token eq token)
        } > 0
    }

    suspend fun deleteTokens(tokens: List<String>): Int = dbQuery {
        if (tokens.isEmpty()) {
            0
        } else {
            DeviceTokensTable.deleteWhere {
                DeviceTokensTable.token inList tokens
            }
        }
    }

    private fun ResultRow.toDeviceTokenDto(): DeviceTokenDto {
        return DeviceTokenDto(
            tokenId = this[DeviceTokensTable.id].value,
            userId = this[DeviceTokensTable.userId].value,
            platform = this[DeviceTokensTable.platform],
            deviceId = this[DeviceTokensTable.deviceId],
            appVersion = this[DeviceTokensTable.appVersion],
            lastSeenAt = this[DeviceTokensTable.lastSeenAt].toString(),
        )
    }
}
