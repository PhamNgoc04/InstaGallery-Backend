package com.instagallery.database.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp

object MediaTagsTable : LongIdTable("media_tags") {
    val name = varchar("name", 50).uniqueIndex()
    val description = text("description").nullable()
    val usageCount = integer("usage_count").default(0).index()
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
}
