package com.instagallery.database.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp

object PostTaggedUsersTable : Table("post_tagged_users") {
    val postId = reference("post_id", PostsTable, onDelete = ReferenceOption.CASCADE)
    val taggedUserId = reference("tagged_user_id", UsersTable, onDelete = ReferenceOption.CASCADE).index()
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)

    override val primaryKey = PrimaryKey(postId, taggedUserId)
}
