package com.instagallery.database.tables

import com.instagallery.models.common.AvailabilityType
import com.instagallery.models.common.DayOfWeekIso
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.timestamp

object AvailabilitySchedulesTable : LongIdTable("availability_schedules") {
    val portfolioId = reference("portfolio_id", PortfoliosTable, onDelete = ReferenceOption.CASCADE).index()
    
    val type = enumerationByName("type", 20, AvailabilityType::class)
    
    // For RECURRING (e.g. Every MONDAY)
    val dayOfWeek = enumerationByName("day_of_week", 20, DayOfWeekIso::class).nullable()
    
    // For SPECIFIC_DATE
    val specificDate = date("specific_date").nullable()
    
    // Time slots (e.g. 08:00 to 17:00). We can store as HH:mm string or Time type.
    val startTime = varchar("start_time", 5) 
    val endTime = varchar("end_time", 5)
    
    val isBooked = bool("is_booked").default(false)
    
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    val updatedAt = timestamp("updated_at").defaultExpression(CurrentTimestamp)
}
