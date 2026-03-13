package com.instagallery.database.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp

object SearchHistoriesTable : LongIdTable("search_histories") {
    val userId = reference("user_id", UsersTable, onDelete = ReferenceOption.CASCADE).index()
    val queryText = varchar("query_text", 255)
    val resultCount = integer("result_count").nullable()
    val searchedAt = timestamp("searched_at").defaultExpression(CurrentTimestamp).index()
}
