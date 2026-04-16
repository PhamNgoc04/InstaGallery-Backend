package com.instagallery.database.tables

import com.instagallery.models.common.FollowRequestStatus
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp

object FollowRequestsTable : LongIdTable("follow_requests") {
    val followerId = reference("follower_id", UsersTable, onDelete = ReferenceOption.CASCADE).index()
    val followingId = reference("following_id", UsersTable, onDelete = ReferenceOption.CASCADE).index()
    val status = enumerationByName("status", 20, FollowRequestStatus::class).default(FollowRequestStatus.PENDING).index()
    
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    val updatedAt = timestamp("updated_at").defaultExpression(CurrentTimestamp)
    
    init {
        uniqueIndex("idx_follower_following_req", followerId, followingId)
    }
}
