package com.instagallery.database.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp

object PortfoliosTable : LongIdTable("portfolios") {
    val userId = reference("user_id", UsersTable, onDelete = ReferenceOption.CASCADE).uniqueIndex()
    val title = varchar("title", 255).nullable()
    val description = text("description").nullable()
    val specialties = text("specialties").nullable() // JSON array in MySQL
    val hourlyRate = decimal("hourly_rate", 12, 2).nullable()
    val currency = varchar("currency", 3).default("VND")
    val serviceArea = varchar("service_area", 255).nullable()
    val isAvailable = bool("is_available").default(true).index()
    val ratingAvg = decimal("rating_avg", 3, 2).default(0.00.toBigDecimal()).index()
    val reviewCount = integer("review_count").default(0)
    val isFeatured = bool("is_featured").default(false).index()
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    val updatedAt = timestamp("updated_at").defaultExpression(CurrentTimestamp)
}
