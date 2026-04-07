package com.instagallery.database.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp

object MutedUsersTable : LongIdTable("muted_users") {
    val muterId = reference("muter_id", UsersTable, onDelete = ReferenceOption.CASCADE).index()
    val mutedId = reference("muted_id", UsersTable, onDelete = ReferenceOption.CASCADE).index()
    
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    
    init {
        uniqueIndex("idx_muter_muted", muterId, mutedId)
    }
}
