package com.instagallery.database.tables

import com.instagallery.models.common.RatingVisibility
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp

object RatingsTable : LongIdTable("ratings") {
    val bookingId = reference("booking_id", BookingsTable, onDelete = ReferenceOption.CASCADE).uniqueIndex()
    val raterId = reference("rater_id", UsersTable, onDelete = ReferenceOption.CASCADE).index()
    val rateeId = reference("ratee_id", UsersTable, onDelete = ReferenceOption.CASCADE).index()
    val ratingValue = short("rating_value").index()
    val comment = text("comment").nullable()
    val status = enumerationByName("status", 20, RatingVisibility::class).default(RatingVisibility.APPROVED).index()
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
}
