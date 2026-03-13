package com.instagallery.database.tables

import org.jetbrains.exposed.dao.id.LongIdTable

object FiltersTable : LongIdTable("filters") {
    val name = varchar("name", 50).uniqueIndex()
    val description = text("description").nullable()
    val configJson = text("config_json").nullable() // JSON object in MySQL
    val previewUrl = varchar("preview_url", 255).nullable()
    val isActive = bool("is_active").default(true)
}
