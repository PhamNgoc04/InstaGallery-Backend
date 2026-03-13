package com.instagallery.database.tables

import com.instagallery.models.common.ReportStatus
import com.instagallery.models.common.ReportTargetType
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp

object ReportsTable : LongIdTable("reports") {
    val reporterId = reference("reporter_id", UsersTable, onDelete = ReferenceOption.CASCADE).index()
    val targetType = enumerationByName("target_type", 20, ReportTargetType::class)
    val targetId = long("target_id")
    val reason = text("reason")
    val adminNote = text("admin_note").nullable()
    val reviewedBy = reference("reviewed_by", UsersTable, onDelete = ReferenceOption.SET_NULL).nullable()
    val status = enumerationByName("status", 20, ReportStatus::class).default(ReportStatus.PENDING).index()
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    val updatedAt = timestamp("updated_at").defaultExpression(CurrentTimestamp)

    init {
        index("idx_target", isUnique = false, targetType, targetId)
    }
}
