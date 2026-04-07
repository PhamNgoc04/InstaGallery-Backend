package com.instagallery.database.tables

import com.instagallery.models.common.MediaType
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp

object PostMediaTable : LongIdTable("post_media") {
    val postId = reference("post_id", PostsTable, onDelete = ReferenceOption.CASCADE).index()
    val mediaFileUrl = varchar("media_file_url", 500)
    val thumbnailUrl = varchar("thumbnail_url", 500).nullable()
    val mediaType = enumerationByName("media_type", 20, MediaType::class).default(MediaType.IMAGE)
    val position = integer("position").default(0)
    val filterId = reference("filter_id", FiltersTable, onDelete = ReferenceOption.SET_NULL).nullable()
    val width = integer("width").nullable()
    val height = integer("height").nullable()
    val fileSize = long("file_size").nullable()
    val duration = integer("duration").nullable() // seconds
    val metadata = text("metadata").nullable() // JSON metadata (EXIF)
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)

    init {
        index("idx_post_position", isUnique = false, postId, position)
    }
}
