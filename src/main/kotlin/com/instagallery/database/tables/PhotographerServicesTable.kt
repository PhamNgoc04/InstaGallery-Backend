package com.instagallery.database.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp

object PhotographerServicesTable : LongIdTable("photographer_services") {
    val photographerId = reference("photographer_id", UsersTable, onDelete = ReferenceOption.CASCADE).index()
    val name = varchar("name", 120)
    val category = varchar("category", 40).index()
    val price = decimal("price", 12, 2)
    val currency = varchar("currency", 3).default("VND")
    val durationMinutes = integer("duration_minutes")
    val photoCount = integer("photo_count").nullable()
    val editedPhotoCount = integer("edited_photo_count").nullable()
    val makeupIncluded = bool("makeup_included").default(false)
    val outfitIncluded = bool("outfit_included").default(false)
    val locationSupport = bool("location_support").default(true)
    val description = text("description").nullable()
    val includes = text("includes").nullable()
    val coverUrl = varchar("cover_url", 1024).nullable()
    val isActive = bool("is_active").default(true).index()
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    val updatedAt = timestamp("updated_at").defaultExpression(CurrentTimestamp)

    init {
        index("idx_photographer_services_category", false, photographerId, category)
    }
}
