package com.instagallery.database

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.vendors.currentDialect
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Stores a local date-time as the same clock the caller sent.
 * Exposed's built-in datetime column sends a zoned instant, and MySQL DATETIME
 * with serverTimezone=UTC then keeps the UTC clock. Seed SQL inserts a literal
 * clock, so the two paths disagreed by the JVM offset.
 */
class WallClockDateTimeColumnType : ColumnType<LocalDateTime>() {
    override fun sqlType(): String = currentDialect.dataTypeProvider.dateTimeType()

    override fun nonNullValueToString(value: LocalDateTime): String = "'${format(value)}'"

    override fun notNullValueToDB(value: LocalDateTime): Any = format(value)

    override fun valueFromDB(value: Any): LocalDateTime = when (value) {
        is LocalDateTime -> value
        is Timestamp -> value.toLocalDateTime()
        is String -> parse(value)
        else -> parse(value.toString())
    }

    private fun format(value: LocalDateTime): String = value.format(FORMATTER)

    private fun parse(raw: String): LocalDateTime {
        val normalized = raw.trim().replace('T', ' ').substringBefore('.').take(19)
        return LocalDateTime.parse(normalized, FORMATTER)
    }

    companion object {
        private val FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    }
}

fun Table.wallClockDateTime(name: String): Column<LocalDateTime> =
    registerColumn(name, WallClockDateTimeColumnType())
