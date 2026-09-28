package com.instagallery.repositories

import com.instagallery.models.common.AvailabilityType
import com.instagallery.models.common.DayOfWeekIso
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class BookingScheduleRulesTest {
    private val mondayMorning = WorkingWindow(
        id = 1,
        type = AvailabilityType.RECURRING,
        dayOfWeek = DayOfWeekIso.MONDAY,
        specificDate = null,
        start = LocalTime.of(9, 0),
        end = LocalTime.of(12, 0),
    )

    private val mondayOverride = WorkingWindow(
        id = 2,
        type = AvailabilityType.SPECIFIC_DATE,
        dayOfWeek = null,
        specificDate = LocalDate.of(2026, 10, 5),
        start = LocalTime.of(9, 0),
        end = LocalTime.of(11, 0),
    )

    @Test
    fun recurringWindowContainsAppointmentOnThatWeekday() {
        val start = LocalDateTime.of(2026, 10, 5, 10, 0)
        val match = BookingScheduleRules.containingWindow(listOf(mondayMorning), start, 60)
        assertEquals(1L, match?.id)
    }

    @Test
    fun specificDateWindowWinsOverRecurring() {
        val start = LocalDateTime.of(2026, 10, 5, 10, 0)
        val match = BookingScheduleRules.containingWindow(listOf(mondayMorning, mondayOverride), start, 60)
        assertEquals(2L, match?.id)
    }

    @Test
    fun appointmentPastTheWindowIsRejected() {
        val start = LocalDateTime.of(2026, 10, 5, 11, 30)
        assertNull(BookingScheduleRules.containingWindow(listOf(mondayMorning), start, 60))
    }

    @Test
    fun overnightAppointmentIsRejected() {
        val start = LocalDateTime.of(2026, 10, 5, 23, 30)
        assertNull(BookingScheduleRules.appointmentEnd(start, 60))
    }

    @Test
    fun overlappingRangesShareTime() {
        val leftStart = LocalDateTime.of(2026, 10, 5, 10, 0)
        val leftEnd = LocalDateTime.of(2026, 10, 5, 11, 0)
        val rightStart = LocalDateTime.of(2026, 10, 5, 10, 30)
        val rightEnd = LocalDateTime.of(2026, 10, 5, 11, 30)
        assertTrue(BookingScheduleRules.overlaps(leftStart, leftEnd, rightStart, rightEnd))
    }
}
