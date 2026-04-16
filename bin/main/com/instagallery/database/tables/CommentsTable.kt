package com.instagallery.database.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp

object CommentsTable : LongIdTable("comments") {
    val postId = reference("post_id", PostsTable, onDelete = ReferenceOption.CASCADE).index()
    val userId = reference("user_id", UsersTable, onDelete = ReferenceOption.CASCADE).index()
    val content = text("content")
    val parentCommentId = reference("parent_comment_id", CommentsTable, onDelete = ReferenceOption.CASCADE).nullable().index()
    val likeCount = integer("like_count").default(0)
    val replyCount = integer("reply_count").default(0)
    val depth = byte("depth").default(0)
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    val updatedAt = timestamp("updated_at").defaultExpression(CurrentTimestamp)
    val deletedAt = timestamp("deleted_at").nullable()

    init {
        index("idx_post_created", isUnique = false, postId, createdAt)
    }
}
