package com.instagallery.repositories

import com.instagallery.database.tables.AvailabilitySchedulesTable
import com.instagallery.database.tables.BookingsTable
import com.instagallery.database.tables.PhotographerServicesTable
import com.instagallery.database.tables.PortfoliosTable
import com.instagallery.database.tables.UsersTable
import com.instagallery.models.common.AvailabilityType
import com.instagallery.models.common.BookingStatus
import com.instagallery.models.common.DayOfWeekIso
import com.instagallery.models.common.UserType
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BookingReserveTest {
    @Test
    fun reservationRespectsWorkingHoursOverlapAndCancellation() = runBlocking {
        val databaseName = UUID.randomUUID().toString()
        val database = Database.connect(
            url = "jdbc:h2:mem:$databaseName;MODE=MySQL;DB_CLOSE_DELAY=-1",
            driver = "org.h2.Driver",
        )
        transaction(database) {
            SchemaUtils.create(
                UsersTable,
                PortfoliosTable,
                PhotographerServicesTable,
                AvailabilitySchedulesTable,
                BookingsTable,
            )
        }

        val closedPhotographerId = insertUser(database, "photo_closed", "photo-closed@example.com", UserType.PHOTOGRAPHER)
        val closedClientId = insertUser(database, "client_closed", "client-closed@example.com", UserType.CLIENT)
        transaction(database) {
            PortfoliosTable.insertAndGetId {
                it[userId] = closedPhotographerId
                it[isAvailable] = true
            }
        }
        val closedResult = BookingRepository().reserveBooking(
            sample(closedClientId, closedPhotographerId, LocalDateTime.of(2026, 10, 5, 10, 0)),
        )
        assertEquals(BookingReservation.NoWorkingHours, closedResult)

        val photographerId = insertUser(database, "photo_reserve", "photo-reserve@example.com", UserType.PHOTOGRAPHER)
        val firstClientId = insertUser(database, "client_reserve_a", "client-a-reserve@example.com", UserType.CLIENT)
        val secondClientId = insertUser(database, "client_reserve_b", "client-b-reserve@example.com", UserType.CLIENT)
        transaction(database) {
            val portfolioId = PortfoliosTable.insertAndGetId {
                it[userId] = photographerId
                it[isAvailable] = true
            }.value
            AvailabilitySchedulesTable.insertAndGetId {
                it[AvailabilitySchedulesTable.portfolioId] = portfolioId
                it[type] = AvailabilityType.RECURRING
                it[dayOfWeek] = DayOfWeekIso.MONDAY
                it[startTime] = "09:00"
                it[endTime] = "12:00"
            }
        }

        val repository = BookingRepository()
        val start = LocalDateTime.of(2026, 10, 5, 10, 0)
        val first = repository.reserveBooking(sample(firstClientId, photographerId, start))
        val overlap = repository.reserveBooking(sample(secondClientId, photographerId, start.plusMinutes(30)))

        assertTrue(first is BookingReservation.Created)
        assertEquals(BookingReservation.SlotTaken, overlap)

        transaction(database) {
            BookingsTable.update({ BookingsTable.id eq (first as BookingReservation.Created).bookingId }) {
                it[status] = BookingStatus.CANCELLED
            }
        }

        val afterCancel = repository.reserveBooking(sample(secondClientId, photographerId, start))
        assertTrue(afterCancel is BookingReservation.Created)
    }

    private fun insertUser(database: Database, username: String, email: String, userType: UserType): Long = transaction(database) {
        UsersTable.insertAndGetId {
            it[UsersTable.username] = username
            it[UsersTable.email] = email
            it[passwordHash] = "hash"
            it[fullName] = username
            it[UsersTable.userType] = userType
        }.value
    }

    private fun sample(clientId: Long, photographerId: Long, start: LocalDateTime) = BookingCreateData(
        clientId = clientId,
        photographerId = photographerId,
        serviceId = null,
        packageName = "Test",
        packageSnapshotJson = null,
        shootingType = null,
        sceneType = null,
        bookingDate = start,
        durationHours = 1.0,
        locationBooking = null,
        addressDetail = null,
        details = null,
        peopleCount = null,
        contactPhone = null,
        addOns = emptyList(),
        referenceImages = emptyList(),
        price = null,
        currency = "VND",
        durationMinutes = 60,
    )
}
