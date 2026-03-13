package com.instagallery.database.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp

object PostMediaTagsTable : Table("post_media_tags") {
    val mediaId = reference("media_id", PostMediaTable, onDelete = ReferenceOption.CASCADE)
    val tagId = reference("tag_id", MediaTagsTable, onDelete = ReferenceOption.CASCADE).index()
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)

    override val primaryKey = PrimaryKey(mediaId, tagId)
}
