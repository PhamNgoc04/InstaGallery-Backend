package com.instagallery.database.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp

object SavedPostsTable : LongIdTable("saved_posts") {
    val userId = reference("user_id", UsersTable, onDelete = ReferenceOption.CASCADE)
    val postId = reference("post_id", PostsTable, onDelete = ReferenceOption.CASCADE).index()
    val savedAt = timestamp("saved_at").defaultExpression(CurrentTimestamp)

    init {
        uniqueIndex("uk_user_post_saved", userId, postId)
    }
}
