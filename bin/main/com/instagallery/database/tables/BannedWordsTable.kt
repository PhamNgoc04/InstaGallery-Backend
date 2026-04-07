package com.instagallery.database.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp

object BannedWordsTable : LongIdTable("banned_words") {
    val wordOrRegex = varchar("word_or_regex", 255).uniqueIndex()
    val isRegex = bool("is_regex").default(false)
    
    val addedByAdminId = reference("added_by_admin_id", UsersTable, onDelete = ReferenceOption.SET_NULL).nullable()
    
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
}
