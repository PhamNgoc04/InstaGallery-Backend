package com.instagallery.repositories

import com.instagallery.database.DatabaseFactory.dbQuery
import com.instagallery.database.tables.AvailabilitySchedulesTable
import com.instagallery.database.tables.BookingsTable
import com.instagallery.database.tables.PortfoliosTable
import com.instagallery.database.tables.RatingsTable
import com.instagallery.database.tables.UsersTable
import com.instagallery.models.common.AvailabilityType
import com.instagallery.models.common.BookingDto
import com.instagallery.models.common.BookingPackageSnapshotDto
import com.instagallery.models.common.BookingStatus
import com.instagallery.models.common.DayOfWeekIso
import com.instagallery.models.common.PaginatedBookingsResponse
import com.instagallery.models.common.PaginationMeta
import com.instagallery.models.common.UserType
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.math.roundToInt

class BookingRepository {

    suspend fun createBooking(data: BookingCreateData): Long = dbQuery {
        val insertStmt = BookingsTable.insertAndGetId {
            it[clientId] = data.clientId
            it[photographerId] = data.photographerId
            data.serviceId?.let { serviceId -> it[BookingsTable.serviceId] = serviceId }
            it[packageName] = data.packageName
            it[packageSnapshot] = data.packageSnapshotJson
            it[shootingType] = data.shootingType
            it[sceneType] = data.sceneType
            it[bookingDate] = data.bookingDate
            it[durationHours] = data.durationHours?.toBigDecimal()
            it[locationBooking] = data.locationBooking
            it[addressDetail] = data.addressDetail
            it[details] = data.details
            it[peopleCount] = data.peopleCount
            it[contactPhone] = data.contactPhone
            it[addOns] = encodeList(data.addOns)
            it[referenceImages] = encodeList(data.referenceImages)
            it[price] = data.price?.toBigDecimal()
            it[currency] = data.currency
            it[status] = BookingStatus.PENDING
        }
        insertStmt.value
    }

    suspend fun checkPhotographerExists(photographerId: Long): Boolean = dbQuery {
        UsersTable
            .selectAll()
            .where {
                (UsersTable.id eq photographerId) and
                    (UsersTable.userType eq UserType.PHOTOGRAPHER) and
                    (UsersTable.isActive eq true) and
                    UsersTable.deletedAt.isNull()
            }
            .count() > 0
    }

    suspend fun getBookingById(bookingId: Long): ResultRow? = dbQuery {
        BookingsTable.selectAll().where { BookingsTable.id eq bookingId }.singleOrNull()
    }

    suspend fun getBookingDetail(userId: Long, bookingId: Long): BookingDto? = dbQuery {
        BookingsTable
            .selectAll()
            .where {
                (BookingsTable.id eq bookingId) and
                    ((BookingsTable.clientId eq userId) or (BookingsTable.photographerId eq userId))
            }
            .singleOrNull()
            ?.toBookingDto(viewerId = userId)
    }

    suspend fun updateBookingStatus(bookingId: Long, newStatus: BookingStatus, reason: String?): Boolean = dbQuery {
        val rows = BookingsTable.update({ BookingsTable.id eq bookingId }) {
            it[status] = newStatus
            reason?.let { r -> it[cancellationReason] = r }
            it[updatedAt] = Instant.now()
        }
        rows > 0
    }

    suspend fun getBookingsList(
        userId: Long,
        page: Int,
        limit: Int,
        statusFilter: BookingStatus?,
    ): PaginatedBookingsResponse = dbQuery {
        val offsetVal = ((page - 1) * limit).toLong()

        var query = BookingsTable
            .selectAll()
            .where { (BookingsTable.clientId eq userId) or (BookingsTable.photographerId eq userId) }

        if (statusFilter != null) {
            query = query.andWhere { BookingsTable.status eq statusFilter }
        }

        val totalRecords = query.count()
        val totalPages = kotlin.math.ceil(totalRecords.toDouble() / limit).toInt()

        val bookings = query
            .orderBy(BookingsTable.createdAt to SortOrder.DESC)
            .limit(limit, offsetVal)
            .map { row -> row.toBookingDto(viewerId = userId) }

        PaginatedBookingsResponse(
            bookings = bookings,
            meta = PaginationMeta(currentPage = page, totalPages = totalPages, hasNext = page < totalPages),
        )
    }

    suspend fun isWithinAvailability(
        photographerId: Long,
        bookingDate: LocalDateTime,
        durationMinutes: Int,
    ): Boolean = dbQuery {
        val portfolioRow = PortfoliosTable
            .selectAll()
            .where { PortfoliosTable.userId eq photographerId }
            .singleOrNull()
            ?: return@dbQuery true

        if (!portfolioRow[PortfoliosTable.isAvailable]) return@dbQuery false

        val portfolioId = portfolioRow[PortfoliosTable.id].value

        val scheduleRows = AvailabilitySchedulesTable
            .selectAll()
            .where {
                (AvailabilitySchedulesTable.portfolioId eq portfolioId) and
                    (AvailabilitySchedulesTable.isBooked eq false)
            }
            .toList()

        if (scheduleRows.isEmpty()) return@dbQuery true

        val startTime = bookingDate.toLocalTime()
        val endTime = startTime.plusMinutes(durationMinutes.toLong())
        if (!endTime.isAfter(startTime)) return@dbQuery false
        val targetDate = bookingDate.toLocalDate()
        val targetDay = DayOfWeekIso.valueOf(bookingDate.dayOfWeek.name)

        scheduleRows.any { row ->
            val matchesDate = when (row[AvailabilitySchedulesTable.type]) {
                AvailabilityType.SPECIFIC_DATE -> row[AvailabilitySchedulesTable.specificDate] == targetDate
                AvailabilityType.RECURRING -> row[AvailabilitySchedulesTable.dayOfWeek] == targetDay
            }
            if (!matchesDate) {
                false
            } else {
                val slotStart = parseTime(row[AvailabilitySchedulesTable.startTime])
                val slotEnd = parseTime(row[AvailabilitySchedulesTable.endTime])
                slotStart != null &&
                    slotEnd != null &&
                    !startTime.isBefore(slotStart) &&
                    !endTime.isAfter(slotEnd)
            }
        }
    }

    suspend fun hasScheduleConflict(
        photographerId: Long,
        bookingDate: LocalDateTime,
        durationMinutes: Int,
    ): Boolean = dbQuery {
        val requestedStart = bookingDate
        val requestedEnd = requestedStart.plusMinutes(durationMinutes.toLong())
        val blockingStatuses = listOf(BookingStatus.PENDING, BookingStatus.CONFIRMED, BookingStatus.IN_PROGRESS)

        BookingsTable
            .selectAll()
            .where {
                (BookingsTable.photographerId eq photographerId) and
                    (BookingsTable.status inList blockingStatuses)
            }
            .any { row ->
                val existingStart = row[BookingsTable.bookingDate]
                val existingMinutes = row[BookingsTable.durationHours]
                    ?.toDouble()
                    ?.let { (it * 60).roundToInt() }
                    ?.coerceAtLeast(30)
                    ?: DEFAULT_DURATION_MINUTES
                val existingEnd = existingStart.plusMinutes(existingMinutes.toLong())
                requestedStart.isBefore(existingEnd) && requestedEnd.isAfter(existingStart)
            }
    }

    private fun ResultRow.toBookingDto(viewerId: Long): BookingDto {
        val bookingIdValue = this[BookingsTable.id].value
        val clientIdVal = this[BookingsTable.clientId].value
        val photoIdVal = this[BookingsTable.photographerId].value
        val clientRow = userRow(clientIdVal)
        val photoRow = userRow(photoIdVal)
        val partnerIdVal = if (viewerId == clientIdVal) photoIdVal else clientIdVal
        val partnerRow = if (viewerId == clientIdVal) photoRow else clientRow
        val durationHoursValue = this[BookingsTable.durationHours]?.toDouble()
        val durationMinutesValue = durationHoursValue?.let { (it * 60).roundToInt() }

        return BookingDto(
            bookingId = bookingIdValue,
            clientId = clientIdVal,
            clientName = clientRow.displayName(),
            clientAvatarUrl = clientRow?.get(UsersTable.profilePictureUrl),
            photographerId = photoIdVal,
            photographerName = photoRow.displayName(),
            photographerAvatarUrl = photoRow?.get(UsersTable.profilePictureUrl),
            partnerId = partnerIdVal,
            partnerName = partnerRow.displayName(),
            partnerAvatarUrl = partnerRow?.get(UsersTable.profilePictureUrl),
            serviceId = this[BookingsTable.serviceId]?.value,
            packageName = this[BookingsTable.packageName],
            packageSnapshot = this[BookingsTable.packageSnapshot]
                ?.let { raw -> runCatching { bookingJson.decodeFromString<BookingPackageSnapshotDto>(raw) }.getOrNull() },
            shootingType = this[BookingsTable.shootingType],
            sceneType = this[BookingsTable.sceneType],
            bookingDate = this[BookingsTable.bookingDate].toString(),
            status = this[BookingsTable.status],
            price = this[BookingsTable.price]?.toDouble(),
            currency = this[BookingsTable.currency],
            locationBooking = this[BookingsTable.locationBooking],
            addressDetail = this[BookingsTable.addressDetail],
            details = this[BookingsTable.details],
            durationHours = durationHoursValue,
            durationMinutes = durationMinutesValue,
            peopleCount = this[BookingsTable.peopleCount],
            contactPhone = this[BookingsTable.contactPhone],
            addOns = decodeList(this[BookingsTable.addOns]),
            referenceImages = decodeList(this[BookingsTable.referenceImages]),
            hasReview = RatingsTable.selectAll()
                .where { RatingsTable.bookingId eq bookingIdValue }
                .count() > 0,
            createdAt = this[BookingsTable.createdAt].toString(),
        )
    }

    private fun userRow(userId: Long): ResultRow? {
        return UsersTable.selectAll().where { UsersTable.id eq userId }.singleOrNull()
    }

    private fun ResultRow?.displayName(): String {
        return this?.get(UsersTable.fullName)?.takeIf { it.isNotBlank() }
            ?: this?.get(UsersTable.username)
            ?: "Unknown"
    }

    private fun encodeList(values: List<String>): String? {
        return values
            .map(String::trim)
            .filter(String::isNotBlank)
            .takeIf(List<String>::isNotEmpty)
            ?.let { bookingJson.encodeToString(it) }
    }

    private fun decodeList(raw: String?): List<String> {
        return raw
            ?.let { value -> runCatching { bookingJson.decodeFromString<List<String>>(value) }.getOrNull() }
            .orEmpty()
    }

    private fun parseTime(raw: String): LocalTime? {
        return runCatching { LocalTime.parse(raw) }.getOrNull()
    }

    companion object {
        const val DEFAULT_DURATION_MINUTES = 120
        val bookingJson = Json { ignoreUnknownKeys = true }
    }
}

data class BookingCreateData(
    val clientId: Long,
    val photographerId: Long,
    val serviceId: Long?,
    val packageName: String?,
    val packageSnapshotJson: String?,
    val shootingType: String?,
    val sceneType: String?,
    val bookingDate: LocalDateTime,
    val durationHours: Double?,
    val locationBooking: String?,
    val addressDetail: String?,
    val details: String?,
    val peopleCount: Int?,
    val contactPhone: String?,
    val addOns: List<String>,
    val referenceImages: List<String>,
    val price: Double?,
    val currency: String,
)
