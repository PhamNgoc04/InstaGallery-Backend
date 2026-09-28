package com.instagallery.repositories

import com.instagallery.database.DatabaseFactory.dbQuery
import com.instagallery.database.tables.ActivityLogsTable
import com.instagallery.models.common.ActivityLogDto
import com.instagallery.models.common.ActivityTargetType
import com.instagallery.models.common.PaginatedActivityLogResponse
import com.instagallery.models.common.PaginationMeta
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import com.instagallery.database.tables.UsersTable

// Repository Layer (repositories/): Nơi thực thi các câu lệnh SQL DSL của JetBrains Exposed. 
// Chuyển đổi các hàng dữ liệu (ResultRow) thành các Kotlin Data Class thuần túy (DTO).

class ActivityLogRepository {
    suspend fun log(
        userId: Long,
        action: String,
        targetType: ActivityTargetType,
        targetId: Long? = null,
        metadata: String? = null,
    ) {
        dbQuery {
            ActivityLogsTable.insert {
                it[ActivityLogsTable.userId] = EntityID(userId, UsersTable)
                it[ActivityLogsTable.action] = action
                it[ActivityLogsTable.targetType] = targetType
                it[ActivityLogsTable.targetId] = targetId
                it[ActivityLogsTable.metadata] = metadata
            }
        }
    }

    suspend fun listForUser(userId: Long, page: Int, limit: Int): PaginatedActivityLogResponse = dbQuery {
        val offset = ((page - 1) * limit).toLong()
        val query = ActivityLogsTable
            .selectAll()
            .where { ActivityLogsTable.userId eq userId }

        val totalRecords = query.count()
        val totalPages = Math.ceil(totalRecords.toDouble() / limit).toInt()
        val activities = query
            .orderBy(ActivityLogsTable.createdAt to SortOrder.DESC)
            .limit(limit, offset)
            .map { row ->
                ActivityLogDto(
                    id = row[ActivityLogsTable.id].value,
                    action = row[ActivityLogsTable.action],
                    targetType = row[ActivityLogsTable.targetType].name,
                    targetId = row[ActivityLogsTable.targetId],
                    metadata = row[ActivityLogsTable.metadata],
                    createdAt = row[ActivityLogsTable.createdAt].toString(),
                )
            }

        PaginatedActivityLogResponse(
            activities = activities,
            meta = PaginationMeta(
                currentPage = page,
                totalPages = totalPages,
                hasNext = page < totalPages,
            ),
        )
    }
}
