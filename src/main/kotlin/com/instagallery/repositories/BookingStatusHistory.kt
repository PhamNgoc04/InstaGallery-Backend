package com.instagallery.repositories

import com.instagallery.database.tables.BookingStatusEventsTable
import com.instagallery.models.common.BookingStatus
import org.jetbrains.exposed.sql.insert

object BookingStatusHistory {
    fun record(
        bookingId: Long,
        fromStatus: BookingStatus?,
        toStatus: BookingStatus,
        actorUserId: Long?,
        reason: String?,
    ) {
        if (fromStatus == toStatus) return
        BookingStatusEventsTable.insert {
            it[BookingStatusEventsTable.bookingId] = bookingId
            it[BookingStatusEventsTable.fromStatus] = fromStatus
            it[BookingStatusEventsTable.toStatus] = toStatus
            actorUserId?.let { actorId -> it[BookingStatusEventsTable.actorUserId] = actorId }
            it[BookingStatusEventsTable.reason] = reason
        }
    }
}
