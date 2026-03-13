package com.instagallery.database.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp

object FollowersTable : Table("followers") {
    val followerId = reference("follower_id", UsersTable, onDelete = ReferenceOption.CASCADE)
    val followingId = reference("following_id", UsersTable, onDelete = ReferenceOption.CASCADE).index()
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp).index()

    override val primaryKey = PrimaryKey(followerId, followingId)
    
    // CHECK (follower_id <> following_id) will be added natively in MySQL or via application logic
}
