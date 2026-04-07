package com.instagallery.services

import com.instagallery.models.common.PaginatedReportsResponse
import com.instagallery.models.common.ReportStatus
import com.instagallery.models.request.CreateReportRequest
import com.instagallery.models.request.UpdateReportStatusRequest
import com.instagallery.plugins.AuthException
import com.instagallery.repositories.ReportRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ReportService : KoinComponent {
    private val reportRepository: ReportRepository by inject()

    suspend fun createReport(reporterId: Long, request: CreateReportRequest): Long {
        return reportRepository.createReport(reporterId, request)
    }

    suspend fun getReports(status: String?, page: Int, limit: Int): PaginatedReportsResponse {
        val verifiedPage = if (page < 1) 1 else page
        val verifiedLimit = if (limit < 1) 20 else if (limit > 50) 50 else limit
        
        val statusFilter = status?.let {
            try {
                ReportStatus.valueOf(it.uppercase())
            } catch (e: Exception) {
                null
            }
        }

        return reportRepository.getReports(statusFilter, verifiedPage, verifiedLimit)
    }

    suspend fun updateReport(reportId: Long, reviewerId: Long, request: UpdateReportStatusRequest): Boolean {
        val success = reportRepository.updateReportStatus(reportId, reviewerId, request)
        if (!success) {
            throw AuthException("REPORT_NOT_FOUND", "Báo cáo không tồn tại.")
        }
        return true
    }
}
