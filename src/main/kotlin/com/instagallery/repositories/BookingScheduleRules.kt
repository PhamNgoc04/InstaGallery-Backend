package com.instagallery.repositories

import com.instagallery.models.common.AvailabilityType
import com.instagallery.models.common.DayOfWeekIso
import org.jetbrains.exposed.sql.vendors.MysqlDialect
import org.jetbrains.exposed.sql.vendors.currentDialect
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset

data class WorkingWindow(
    val id: Long,
    val type: AvailabilityType,
    val dayOfWeek: DayOfWeekIso?,
    val specificDate: LocalDate?,
    val start: LocalTime,
    val end: LocalTime,
)

object BookingScheduleRules {
    fun appointmentEnd(start: LocalDateTime, durationMinutes: Int): LocalDateTime? {
        if (durationMinutes <= 0) return null
        val end = start.plusMinutes(durationMinutes.toLong())
        if (end.toLocalDate() != start.toLocalDate()) return null
        if (!end.toLocalTime().isAfter(start.toLocalTime())) return null
        return end
    }

    fun containingWindow(
        windows: List<WorkingWindow>,
        start: LocalDateTime,
        durationMinutes: Int,
    ): WorkingWindow? {
        val end = appointmentEnd(start, durationMinutes) ?: return null
        val startTime = start.toLocalTime()
        val endTime = end.toLocalTime()
        val targetDate = start.toLocalDate()
        val targetDay = DayOfWeekIso.valueOf(start.dayOfWeek.name)

        val matches = windows.filter { window ->
            val onThisDay = when (window.type) {
                AvailabilityType.SPECIFIC_DATE -> window.specificDate == targetDate
                AvailabilityType.RECURRING -> window.dayOfWeek == targetDay
            }
            onThisDay &&
                window.end.isAfter(window.start) &&
                !startTime.isBefore(window.start) &&
                !endTime.isAfter(window.end)
        }

        return matches.firstOrNull { it.type == AvailabilityType.SPECIFIC_DATE }
            ?: matches.firstOrNull()
    }

    fun overlaps(
        leftStart: LocalDateTime,
        leftEnd: LocalDateTime,
        rightStart: LocalDateTime,
        rightEnd: LocalDateTime,
    ): Boolean {
        return leftStart.isBefore(rightEnd) && leftEnd.isAfter(rightStart)
    }

    /**
     * Exposed writes a local date-time as an instant, and MySQL with serverTimezone=UTC
     * stores that instant's UTC clock. Reading it back yields the UTC clock, so convert
     * to the JVM zone before comparing or returning it. H2 already round-trips the original value.
     */
    fun storedBookingStart(value: LocalDateTime): LocalDateTime {
        if (currentDialect !is MysqlDialect) return value
        val instant = value.atZone(ZoneOffset.UTC).toInstant()
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    }
}
