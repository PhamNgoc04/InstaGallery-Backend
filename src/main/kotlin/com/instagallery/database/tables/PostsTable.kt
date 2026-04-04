package com.instagallery.database.tables

import com.instagallery.models.common.PostVisibility
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp

object PostsTable : LongIdTable("posts") {
    val userId = reference("user_id", UsersTable, onDelete = ReferenceOption.CASCADE).index()
    val caption = text("caption").nullable()
    val location = varchar("location", 255).nullable()
    val visibility = enumerationByName("visibility", 20, PostVisibility::class).default(PostVisibility.PUBLIC).index()
    val commentVisibility = enumerationByName("comment_visibility", 20, com.instagallery.models.common.CommentVisibility::class).default(com.instagallery.models.common.CommentVisibility.ALLOW_ALL)
    val likeCount = integer("like_count").default(0)
    val commentCount = integer("comment_count").default(0)
    val shareCount = integer("share_count").default(0)
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp).index()
    val updatedAt = timestamp("updated_at").defaultExpression(CurrentTimestamp)
    val deletedAt = timestamp("deleted_at").nullable().index()
    
    init {
        index("idx_user_created", isUnique = false, userId, createdAt)
    }
}
