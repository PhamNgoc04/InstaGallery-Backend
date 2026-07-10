package com.instagallery.database.tables

import com.instagallery.models.common.BookingStatus
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.javatime.timestamp

object BookingsTable : LongIdTable("bookings") {
    val clientId = reference("client_id", UsersTable, onDelete = ReferenceOption.CASCADE).index()
    val photographerId = reference("photographer_id", UsersTable, onDelete = ReferenceOption.CASCADE).index()
    val serviceId = reference("service_id", PhotographerServicesTable, onDelete = ReferenceOption.SET_NULL).nullable().index()
    val packageName = varchar("package_name", 120).nullable()
    val packageSnapshot = text("package_snapshot").nullable()
    val shootingType = varchar("shooting_type", 40).nullable()
    val sceneType = varchar("scene_type", 40).nullable()
    val bookingDate = datetime("booking_date").index()
    val durationHours = decimal("duration_hours", 4, 1).nullable()
    val locationBooking = varchar("location_booking", 255).nullable()
    val addressDetail = varchar("address_detail", 255).nullable()
    val details = text("details").nullable()
    val peopleCount = integer("people_count").nullable()
    val contactPhone = varchar("contact_phone", 30).nullable()
    val addOns = text("add_ons").nullable()
    val referenceImages = text("reference_images").nullable()
    val price = decimal("price", 12, 2).nullable()
    val currency = varchar("currency", 3).default("VND")
    val status = enumerationByName("status", 20, BookingStatus::class).default(BookingStatus.PENDING).index()
    val cancellationReason = text("cancellation_reason").nullable()
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    val updatedAt = timestamp("updated_at").defaultExpression(CurrentTimestamp)

    init {
        index("idx_photographer_date", isUnique = false, photographerId, bookingDate)
        // CHECK (client_id <> photographer_id) application logic
    }
}
