package com.instagallery.database.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp

object AlbumMediaTable : LongIdTable("album_media") {
    val albumId = reference("album_id", AlbumsTable, onDelete = ReferenceOption.CASCADE).index()
    val postId = reference("post_id", PostsTable, onDelete = ReferenceOption.CASCADE)
    
    val addedAt = timestamp("added_at").defaultExpression(CurrentTimestamp)
    
    init {
        uniqueIndex("idx_album_post_unique", albumId, postId)
    }
}
