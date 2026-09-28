package com.instagallery.database.tables

import com.instagallery.models.common.BookingStatus
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp

object BookingStatusEventsTable : LongIdTable("booking_status_events") {
    val bookingId = reference("booking_id", BookingsTable, onDelete = ReferenceOption.CASCADE).index()
    val fromStatus = enumerationByName("from_status", 20, BookingStatus::class).nullable()
    val toStatus = enumerationByName("to_status", 20, BookingStatus::class)
    val actorUserId = reference("actor_user_id", UsersTable, onDelete = ReferenceOption.RESTRICT).nullable().index()
    val reason = text("reason").nullable()
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp).index()
}
