package com.instagallery.repositories

import com.instagallery.database.DatabaseFactory.dbQuery
import com.instagallery.database.tables.*
import com.instagallery.models.common.*
import com.instagallery.models.request.CreateBookingRequest
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.Instant
import java.time.LocalDateTime

class BookingRepository {

    suspend fun createBooking(clientId: Long, request: CreateBookingRequest, parsedDate: LocalDateTime): Long = dbQuery {
        val insertStmt = BookingsTable.insertAndGetId {
            it[BookingsTable.clientId] = clientId
            it[photographerId] = request.photographerId
            it[bookingDate] = parsedDate
            it[durationHours] = request.durationHours?.toBigDecimal()
            it[locationBooking] = request.locationBooking
            it[details] = request.details
            it[price] = request.price?.toBigDecimal()
            it[currency] = request.currency
            it[status] = BookingStatus.PENDING
        }
        insertStmt.value
    }

    suspend fun checkPhotographerExists(photographerId: Long): Boolean = dbQuery {
        UsersTable.selectAll().where { UsersTable.id eq photographerId }.count() > 0
    }

    suspend fun getBookingById(bookingId: Long): ResultRow? = dbQuery {
        BookingsTable.selectAll().where { BookingsTable.id eq bookingId }.singleOrNull()
    }

    suspend fun updateBookingStatus(bookingId: Long, newStatus: BookingStatus, reason: String?): Boolean = dbQuery {
        val rows = BookingsTable.update({ BookingsTable.id eq bookingId }) {
            it[status] = newStatus
            reason?.let { r -> it[cancellationReason] = r }
            it[updatedAt] = Instant.now()
        }
        rows > 0
    }

    suspend fun getBookingsList(userId: Long, page: Int, limit: Int, statusFilter: BookingStatus?): PaginatedBookingsResponse = dbQuery {
        val offsetVal = ((page - 1) * limit).toLong()

        // Base Query: Where I'm the client or the photographer
        var query = BookingsTable.selectAll().where { (BookingsTable.clientId eq userId) or (BookingsTable.photographerId eq userId) }
        
        if (statusFilter != null) {
            query = query.andWhere { BookingsTable.status eq statusFilter }
        }

        val totalRecords = query.count()
        val totalPages = Math.ceil(totalRecords.toDouble() / limit).toInt()

        val results = query.orderBy(BookingsTable.createdAt to SortOrder.DESC).limit(limit, offsetVal).toList()

        val bookings = results.map { row ->
            val clientIdVal = row[BookingsTable.clientId].value
            val photoIdVal = row[BookingsTable.photographerId].value
            
            // Determine who the partner is
            val partnerIdVal = if (userId == clientIdVal) photoIdVal else clientIdVal
            
            // Fetch partner name (Ideally this should be an INNER JOIN, doing separate selected string for simplicity)
            val partnerRow = UsersTable.selectAll().where { UsersTable.id eq partnerIdVal }.singleOrNull()
            val partnerNameStr = partnerRow?.get(UsersTable.fullName) ?: "Unknown"

            BookingDto(
                bookingId = row[BookingsTable.id].value,
                partnerId = partnerIdVal,
                partnerName = partnerNameStr,
                bookingDate = row[BookingsTable.bookingDate].toString(),
                status = row[BookingsTable.status],
                price = row[BookingsTable.price]?.toDouble(),
                currency = row[BookingsTable.currency],
                locationBooking = row[BookingsTable.locationBooking],
                details = row[BookingsTable.details],
                durationHours = row[BookingsTable.durationHours]?.toDouble(),
                createdAt = row[BookingsTable.createdAt].toString()
            )
        }

        PaginatedBookingsResponse(
            bookings = bookings,
            meta = PaginationMeta(currentPage = page, totalPages = totalPages, hasNext = page < totalPages)
        )
    }
}
