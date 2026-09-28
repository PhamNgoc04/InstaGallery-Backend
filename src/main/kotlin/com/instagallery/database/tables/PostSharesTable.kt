package com.instagallery.database.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp

object PostSharesTable : Table("post_shares") {
    val userId = reference("user_id", UsersTable, onDelete = ReferenceOption.CASCADE)
    val postId = reference("post_id", PostsTable, onDelete = ReferenceOption.CASCADE).index()
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)

    override val primaryKey = PrimaryKey(userId, postId)
}
