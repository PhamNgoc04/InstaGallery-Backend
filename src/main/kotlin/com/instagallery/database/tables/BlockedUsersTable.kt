package com.instagallery.database.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp

object BlockedUsersTable : LongIdTable("blocked_users") {
    val blockerId = reference("blocker_id", UsersTable, onDelete = ReferenceOption.CASCADE).index()
    val blockedId = reference("blocked_id", UsersTable, onDelete = ReferenceOption.CASCADE).index()
    val reason = varchar("reason", 255).nullable()
    
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    
    init {
        uniqueIndex("idx_blocker_blocked", blockerId, blockedId)
    }
}
