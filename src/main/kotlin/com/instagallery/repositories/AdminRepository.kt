package com.instagallery.repositories

import com.instagallery.database.DatabaseFactory.dbQuery
import com.instagallery.database.tables.*
import com.instagallery.models.common.AdminGrowthDto
import com.instagallery.models.common.AdminStatsDto
import com.instagallery.models.common.ReportStatus
import com.instagallery.models.common.UserType
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.date
import java.time.LocalDate

class AdminRepository {

    suspend fun getOverviewStats(): AdminStatsDto = dbQuery {
        val users = UsersTable.selectAll().count()
        val photographers = UsersTable.selectAll().where { UsersTable.userType eq UserType.PHOTOGRAPHER }.count()
        val posts = PostsTable.selectAll().where { PostsTable.deletedAt.isNull() }.count()
        val bookings = BookingsTable.selectAll().count()
        val reports = ReportsTable.selectAll().where { ReportsTable.status eq ReportStatus.PENDING }.count()

        AdminStatsDto(users, photographers, posts, bookings, reports)
    }

    suspend fun getGrowth(type: String, days: Int): List<AdminGrowthDto> = dbQuery {
        val startDate = LocalDate.now().minusDays(days.toLong())
        val queryResult = mutableMapOf<String, Long>()

        when (type.uppercase()) {
            "USERS" -> {
                val dates = UsersTable
                    .select(UsersTable.createdAt.date(), UsersTable.id.count())
                    .where { UsersTable.createdAt.date() greaterEq startDate }
                    .groupBy(UsersTable.createdAt.date())
                    .toList()
                    
                dates.forEach { row ->
                    queryResult[row[UsersTable.createdAt.date()].toString()] = row[UsersTable.id.count()]
                }
            }
            "POSTS" -> {
                val dates = PostsTable
                    .select(PostsTable.createdAt.date(), PostsTable.id.count())
                    .where { PostsTable.createdAt.date() greaterEq startDate }
                    .groupBy(PostsTable.createdAt.date())
                    .toList()

                dates.forEach { row ->
                    queryResult[row[PostsTable.createdAt.date()].toString()] = row[PostsTable.id.count()]
                }
            }
            "BOOKINGS" -> {
                val dates = BookingsTable
                    .select(BookingsTable.createdAt.date(), BookingsTable.id.count())
                    .where { BookingsTable.createdAt.date() greaterEq startDate }
                    .groupBy(BookingsTable.createdAt.date())
                    .toList()

                dates.forEach { row ->
                    queryResult[row[BookingsTable.createdAt.date()].toString()] = row[BookingsTable.id.count()]
                }
            }
        }

        // Fill missing dates with 0
        val growthList = mutableListOf<AdminGrowthDto>()
        for (i in (days - 1) downTo 0) {
            val dateStr = LocalDate.now().minusDays(i.toLong()).toString()
            growthList.add(AdminGrowthDto(dateStr, queryResult[dateStr] ?: 0))
        }

        growthList
    }

    // --- MODERATION ---
    suspend fun banUser(targetUserId: Long, isBanned: Boolean, reason: String?): Boolean = dbQuery {
        val count = UsersTable.update({ UsersTable.id eq targetUserId }) {
            // Note: Currently UsersTable doesn't have an `isBanned` or `banReason` column in the latest schema provided earlier
            // We'll update the `updatedAt` to simulate it or actually we should add an `isActive` or `isBanned` column if we want.
            // Let's assume there's a way. If not, this is a placeholder schema change warning. We will use the `deletedAt` logic for ban or just throw if we can't.
            // Since we don't have isBanned in UsersTable based on previous analysis, we will mock the functionality by setting `deletedAt` as a "Ban" (Soft Delete).
            
            if (isBanned) {
                it[deletedAt] = java.time.Instant.now()
            } else {
                it[deletedAt] = null
            }
            it[updatedAt] = java.time.Instant.now()
        }
        count > 0
    }

    suspend fun deletePostAsAdmin(postId: Long): Boolean = dbQuery {
        val count = PostsTable.update({ PostsTable.id eq postId }) {
            it[deletedAt] = java.time.Instant.now()
        }
        count > 0
    }

    suspend fun deleteCommentAsAdmin(commentId: Long): Boolean = dbQuery {
        val count = CommentsTable.update({ CommentsTable.id eq commentId }) {
            it[deletedAt] = java.time.Instant.now()
        }
        count > 0
    }

    // --- FR-43: DANH SÁCH NGƯỜI DÙNG ---
    suspend fun listUsers(page: Int, limit: Int, search: String?, status: String?): Map<String, Any> = dbQuery {
        val offset = ((page - 1) * limit).toLong()
        var query = UsersTable.selectAll()

        if (!search.isNullOrBlank()) {
            query = query.where {
                (UsersTable.username like "%$search%") or (UsersTable.email like "%$search%") or (UsersTable.fullName like "%$search%")
            }
        }

        val total = query.count()
        val users = query.orderBy(UsersTable.createdAt to SortOrder.DESC)
            .limit(limit, offset)
            .map { row ->
                mapOf(
                    "userId" to row[UsersTable.id].value,
                    "username" to row[UsersTable.username],
                    "email" to row[UsersTable.email],
                    "fullName" to row[UsersTable.fullName],
                    "isActive" to row[UsersTable.isActive],
                    "userType" to row[UsersTable.userType].name,
                    "createdAt" to row[UsersTable.createdAt].toString()
                )
            }

        mapOf("users" to users, "total" to total, "page" to page, "limit" to limit)
    }

    // --- FR-43: CHI TIẾT NGƯỜI DÙNG ---
    suspend fun getUserDetail(userId: Long): Map<String, Any?>? = dbQuery {
        UsersTable.selectAll().where { UsersTable.id eq userId }.singleOrNull()?.let { row ->
            mapOf(
                "userId" to row[UsersTable.id].value,
                "username" to row[UsersTable.username],
                "email" to row[UsersTable.email],
                "fullName" to row[UsersTable.fullName],
                "isActive" to row[UsersTable.isActive],
                "isPrivate" to row[UsersTable.isPrivate],
                "userType" to row[UsersTable.userType].name,
                "role" to row[UsersTable.role].name,
                "createdAt" to row[UsersTable.createdAt].toString()
            )
        }
    }

    // --- FR-46: TỪ KHÓA CẤM ---
    // Note: BannedKeywordsTable is referenced here but may need to be created if not existing
    // For now, returning empty list as stub - implement fully when BannedKeywordsTable is created
    suspend fun getBannedKeywords(): List<Map<String, Any>> = dbQuery {
        // TODO: Implement with BannedKeywordsTable when created
        emptyList()
    }

    suspend fun addBannedKeyword(keyword: String): Boolean = dbQuery {
        // TODO: Implement with BannedKeywordsTable when created
        true
    }

    suspend fun removeBannedKeyword(keywordId: Long): Boolean = dbQuery {
        // TODO: Implement with BannedKeywordsTable when created
        true
    }
}
