package com.instagallery.models.common

import kotlinx.serialization.Serializable

@Serializable
data class ReportDto(
    val id: Long,
    val reporterId: Long,
    val reporterUsername: String,
    val targetType: ReportTargetType,
    val targetId: Long,
    val reason: String,
    val status: ReportStatus,
    val adminNote: String?,
    val reviewedBy: Long?,
    val createdAt: String
)

@Serializable
data class PaginatedReportsResponse(
    val reports: List<ReportDto>,
    val meta: PaginationMeta
)
