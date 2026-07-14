package com.instagallery.repositories

import com.instagallery.database.DatabaseFactory.dbQuery
import com.instagallery.database.tables.*
import com.instagallery.models.common.*
import com.instagallery.models.request.CreateReportRequest
import com.instagallery.models.request.UpdateReportStatusRequest
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class ReportRepository {

    suspend fun createReport(reporterId: Long, request: CreateReportRequest): Long = dbQuery {
        ReportsTable.insertAndGetId {
            it[ReportsTable.reporterId] = reporterId
            it[targetType] = request.targetType
            it[targetId] = request.targetId
            // The API spec used `reason` & `description`. 
            // The schema `ReportsTable` only has `reason`, so we combine them or just use reason.
            it[reason] = if (request.description.isNullOrBlank()) request.reason else "${request.reason} - ${request.description}"
            it[status] = ReportStatus.PENDING
        }.value
    }

    suspend fun getReports(statusFilter: ReportStatus?, page: Int, limit: Int): PaginatedReportsResponse = dbQuery {
        val offsetVal = ((page - 1) * limit).toLong()

        var query = ReportsTable
            .join(UsersTable, JoinType.INNER, ReportsTable.reporterId, UsersTable.id)
            .selectAll()
        
        if (statusFilter != null) {
            query = query.adjustWhere { ReportsTable.status eq statusFilter }
        }

        query = query.orderBy(ReportsTable.createdAt to SortOrder.DESC)

        val totalRecords = query.count()
        val totalPages = Math.ceil(totalRecords.toDouble() / limit).toInt()

        val rows = query.limit(limit, offsetVal).toList()

        val reports = rows.map { row ->
            val targetType = row[ReportsTable.targetType]
            val targetId = row[ReportsTable.targetId]
            val targetPreview = resolveTargetPreview(targetType, targetId)
            ReportDto(
                id = row[ReportsTable.id].value,
                reporterId = row[UsersTable.id].value,
                reporterUsername = row[UsersTable.username],
                reporterName = row[UsersTable.fullName].ifBlank { row[UsersTable.username] },
                reporterAvatarUrl = row[UsersTable.profilePictureUrl],
                targetType = targetType,
                targetId = targetId,
                targetName = targetPreview.name,
                targetCoverUrl = targetPreview.coverUrl,
                reason = row[ReportsTable.reason],
                status = row[ReportsTable.status],
                adminNote = row[ReportsTable.adminNote],
                reviewedBy = row[ReportsTable.reviewedBy]?.value,
                createdAt = row[ReportsTable.createdAt].toString()
            )
        }

        PaginatedReportsResponse(
            reports = reports,
            meta = PaginationMeta(currentPage = page, totalPages = totalPages, hasNext = page < totalPages)
        )
    }

    suspend fun updateReportStatus(reportId: Long, reviewerId: Long, request: UpdateReportStatusRequest): Boolean = dbQuery {
        val count = ReportsTable.update({ ReportsTable.id eq reportId }) {
            it[status] = request.status
            it[adminNote] = request.adminNote
            it[reviewedBy] = reviewerId
            it[updatedAt] = java.time.Instant.now()
        }
        count > 0
    }

    private fun resolveTargetPreview(
        targetType: ReportTargetType,
        targetId: Long,
    ): TargetPreview {
        return when (targetType) {
            ReportTargetType.POST -> {
                TargetPreview(
                    coverUrl = firstPostMediaUrl(targetId),
                )
            }

            ReportTargetType.COMMENT -> {
                val commentRow = CommentsTable
                    .selectAll()
                    .where { CommentsTable.id eq targetId }
                    .singleOrNull()
                val postId = commentRow?.get(CommentsTable.postId)?.value
                TargetPreview(
                    coverUrl = postId?.let(::firstPostMediaUrl),
                )
            }

            ReportTargetType.USER -> {
                val userRow = UsersTable
                    .selectAll()
                    .where { UsersTable.id eq targetId }
                    .singleOrNull()
                TargetPreview(
                    name = userRow?.get(UsersTable.fullName)
                        ?.takeIf(String::isNotBlank)
                        ?: userRow?.get(UsersTable.username),
                    coverUrl = userRow?.get(UsersTable.profilePictureUrl),
                )
            }

            else -> TargetPreview()
        }
    }

    private fun firstPostMediaUrl(postId: Long): String? {
        return PostMediaTable
            .selectAll()
            .where { PostMediaTable.postId eq postId }
            .orderBy(PostMediaTable.position to SortOrder.ASC)
            .limit(1)
            .singleOrNull()
            ?.let { row ->
                row[PostMediaTable.mediaFileUrl]
                    .takeIf(String::isNotBlank)
                    ?: row[PostMediaTable.thumbnailUrl]?.takeIf(String::isNotBlank)
            }
    }

    private data class TargetPreview(
        val name: String? = null,
        val coverUrl: String? = null,
    )
}
