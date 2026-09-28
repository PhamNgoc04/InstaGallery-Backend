package com.instagallery.database.tables

import com.instagallery.models.common.CommentReactionKind
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp

object CommentReactionsTable : Table("comment_reactions") {
    val userId = reference("user_id", UsersTable, onDelete = ReferenceOption.CASCADE)
    val commentId = reference("comment_id", CommentsTable, onDelete = ReferenceOption.CASCADE).index()
    val reaction = enumerationByName("reaction", 10, CommentReactionKind::class)
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)

    override val primaryKey = PrimaryKey(userId, commentId)
}
