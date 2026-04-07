package com.instagallery.models.request

import com.instagallery.models.common.ReportStatus
import com.instagallery.models.common.ReportTargetType
import kotlinx.serialization.Serializable

@Serializable
data class CreateReportRequest(
    val targetType: ReportTargetType,
    val targetId: Long,
    val reason: String,
    val description: String? = null
)

@Serializable
data class UpdateReportStatusRequest(
    val status: ReportStatus,
    val adminNote: String? = null
)
