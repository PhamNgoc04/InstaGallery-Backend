package com.instagallery.repositories

import com.instagallery.database.DatabaseFactory.dbQuery
import com.instagallery.database.tables.ReportsTable
import com.instagallery.database.tables.UsersTable
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
            ReportDto(
                id = row[ReportsTable.id].value,
                reporterId = row[UsersTable.id].value,
                reporterUsername = row[UsersTable.username],
                targetType = row[ReportsTable.targetType],
                targetId = row[ReportsTable.targetId],
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
}
