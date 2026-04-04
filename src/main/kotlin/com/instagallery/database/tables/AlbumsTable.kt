package com.instagallery.database.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp

object AlbumsTable : LongIdTable("albums") {
    val userId = reference("user_id", UsersTable, onDelete = ReferenceOption.CASCADE).index()
    val title = varchar("title", 100)
    val description = text("description").nullable()
    val coverImageUrl = varchar("cover_image_url", 255).nullable()
    val isPrivate = bool("is_private").default(false)
    
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    val updatedAt = timestamp("updated_at").defaultExpression(CurrentTimestamp)
    val deletedAt = timestamp("deleted_at").nullable()
    
    init {
        index("idx_user_albums", isUnique = false, userId, createdAt)
    }
}
