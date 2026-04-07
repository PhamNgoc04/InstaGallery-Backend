package com.instagallery.database.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp

object CommentLikesTable : LongIdTable("comment_likes") {
    val userId = reference("user_id", UsersTable, onDelete = ReferenceOption.CASCADE)
    val commentId = reference("comment_id", CommentsTable, onDelete = ReferenceOption.CASCADE).index()
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp).index()

    init {
        uniqueIndex("uk_user_comment_likes", userId, commentId)
    }
}
