package com.instagallery.repositories

import com.instagallery.database.DatabaseFactory.dbQuery
import com.instagallery.database.tables.PortfoliosTable
import com.instagallery.database.tables.UsersTable
import com.instagallery.models.common.PhotographerDto
import com.instagallery.models.common.PortfolioDto
import com.instagallery.models.common.PaginationMeta
import com.instagallery.models.common.PaginatedPhotographersResponse
import com.instagallery.models.request.UpdatePortfolioRequest
import com.instagallery.database.tables.AvailabilitySchedulesTable
import com.instagallery.models.common.AvailabilityScheduleDto
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere

class PortfolioRepository {

    suspend fun getPortfolioByUserId(userId: Long): PortfolioDto? = dbQuery {
        val row = PortfoliosTable.selectAll().where { PortfoliosTable.userId eq userId }.singleOrNull()
        row?.toPortfolioDto()
    }

    suspend fun upsertPortfolio(userId: Long, request: UpdatePortfolioRequest): PortfolioDto? = dbQuery {
        val existing = PortfoliosTable.selectAll().where { PortfoliosTable.userId eq userId }.singleOrNull()
        
        if (existing == null) {
            // Insert
            PortfoliosTable.insert {
                it[PortfoliosTable.userId] = userId
                request.bioProfessional?.let { bio -> it[PortfoliosTable.description] = bio }
                request.hourlyRate?.let { rate -> it[PortfoliosTable.hourlyRate] = rate.toBigDecimal() }
                request.specialties?.let { spec -> it[PortfoliosTable.specialties] = spec }
                request.location?.let { loc -> it[PortfoliosTable.serviceArea] = loc }
            }
        } else {
            // Update
            PortfoliosTable.update({ PortfoliosTable.userId eq userId }) {
                request.bioProfessional?.let { bio -> it[PortfoliosTable.description] = bio }
                request.hourlyRate?.let { rate -> it[PortfoliosTable.hourlyRate] = rate.toBigDecimal() }
                request.specialties?.let { spec -> it[PortfoliosTable.specialties] = spec }
                request.location?.let { loc -> it[PortfoliosTable.serviceArea] = loc }
                it[PortfoliosTable.updatedAt] = java.time.Instant.now()
            }
        }
        
        getPortfolioByUserId(userId)
    }

    suspend fun getPhotographers(location: String?, specialty: String?, minRate: Double?, maxRate: Double?, page: Int, limit: Int): PaginatedPhotographersResponse = dbQuery {
        val offsetVal = ((page - 1) * limit).toLong()

        val query = (PortfoliosTable innerJoin UsersTable).selectAll()

        // Filtering
        if (!location.isNullOrBlank()) {
            query.andWhere { PortfoliosTable.serviceArea like "%$location%" }
        }
        if (!specialty.isNullOrBlank()) {
            query.andWhere { PortfoliosTable.specialties like "%$specialty%" }
        }
        if (minRate != null) {
            query.andWhere { PortfoliosTable.hourlyRate greaterEq minRate.toBigDecimal() }
        }
        if (maxRate != null) {
            query.andWhere { PortfoliosTable.hourlyRate lessEq maxRate.toBigDecimal() }
        }

        val totalRecords = query.count()
        val totalPages = Math.ceil(totalRecords.toDouble() / limit).toInt()

        val rows = query.limit(limit, offsetVal).toList()

        val photographers = rows.map { row ->
            PhotographerDto(
                userId = row[UsersTable.id].value,
                username = row[UsersTable.username],
                fullName = row[UsersTable.fullName],
                avatar = row[UsersTable.profilePictureUrl],
                hourlyRate = row[PortfoliosTable.hourlyRate]?.toDouble(),
                specialties = row[PortfoliosTable.specialties],
                rating = row[PortfoliosTable.ratingAvg].toDouble(),
                ratingCount = row[PortfoliosTable.reviewCount]
            )
        }

        PaginatedPhotographersResponse(
            photographers = photographers,
            meta = PaginationMeta(
                currentPage = page,
                totalPages = totalPages,
                hasNext = page < totalPages
            )
        )
    }

    suspend fun getAvailability(userId: Long): List<AvailabilityScheduleDto> = dbQuery {
        val portfolioId = PortfoliosTable
            .selectAll()
            .where { PortfoliosTable.userId eq userId }
            .singleOrNull()
            ?.get(PortfoliosTable.id)
            ?.value
            ?: return@dbQuery emptyList()

        AvailabilitySchedulesTable
            .selectAll()
            .where { AvailabilitySchedulesTable.portfolioId eq portfolioId }
            .map { row ->
                AvailabilityScheduleDto(
                    id = row[AvailabilitySchedulesTable.id].value,
                    type = row[AvailabilitySchedulesTable.type],
                    dayOfWeek = row[AvailabilitySchedulesTable.dayOfWeek],
                    specificDate = row[AvailabilitySchedulesTable.specificDate]?.toString(),
                    startTime = row[AvailabilitySchedulesTable.startTime],
                    endTime = row[AvailabilitySchedulesTable.endTime],
                    isBooked = row[AvailabilitySchedulesTable.isBooked]
                )
            }
    }

    suspend fun updateAvailability(userId: Long, schedules: List<AvailabilityScheduleDto>): Unit = dbQuery {
        val existingPortfolio = PortfoliosTable
            .selectAll()
            .where { PortfoliosTable.userId eq userId }
            .singleOrNull()
        val portfolioId = existingPortfolio
            ?.get(PortfoliosTable.id)
            ?.value
            ?: PortfoliosTable.insertAndGetId {
                it[PortfoliosTable.userId] = userId
            }.value

        PortfoliosTable.update({ PortfoliosTable.id eq portfolioId }) {
            it[PortfoliosTable.isAvailable] = schedules.isNotEmpty()
            it[PortfoliosTable.updatedAt] = java.time.Instant.now()
        }

        AvailabilitySchedulesTable.deleteWhere { AvailabilitySchedulesTable.portfolioId eq portfolioId }

        schedules.forEach { schedule ->
            AvailabilitySchedulesTable.insert {
                it[this.portfolioId] = portfolioId
                it[this.type] = schedule.type
                it[this.dayOfWeek] = schedule.dayOfWeek
                it[this.specificDate] = schedule.specificDate?.let { dateStr -> java.time.LocalDate.parse(dateStr) }
                it[this.startTime] = schedule.startTime
                it[this.endTime] = schedule.endTime
                it[this.isBooked] = false
            }
        }
    }

    private fun ResultRow.toPortfolioDto() = PortfolioDto(
        id = this[PortfoliosTable.id].value,
        userId = this[PortfoliosTable.userId].value,
        bioProfessional = this[PortfoliosTable.description],
        hourlyRate = this[PortfoliosTable.hourlyRate]?.toDouble(),
        specialties = this[PortfoliosTable.specialties],
        equipment = null, // Equipment column doesn't exist natively, sticking to what we have or dropping it later
        location = this[PortfoliosTable.serviceArea],
        rating = this[PortfoliosTable.ratingAvg].toDouble(),
        ratingCount = this[PortfoliosTable.reviewCount],
        createdAt = this[PortfoliosTable.createdAt].toString(),
        updatedAt = this[PortfoliosTable.updatedAt].toString()
    )
}
