package com.instagallery.repositories

import com.instagallery.database.DatabaseFactory.dbQuery
import com.instagallery.database.tables.BookingsTable
import com.instagallery.database.tables.RatingsTable
import com.instagallery.database.tables.UsersTable
import com.instagallery.database.tables.PortfoliosTable
import com.instagallery.models.common.BookingStatus
import com.instagallery.models.common.PaginatedRatingsResponse
import com.instagallery.models.common.PaginationMeta
import com.instagallery.models.common.RatingDto
import com.instagallery.models.request.CreateRatingRequest
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class RatingRepository {

    suspend fun getBookingForRating(bookingId: Long, raterId: Long, rateeId: Long): Boolean = dbQuery {
        BookingsTable.selectAll().where {
            (BookingsTable.id eq bookingId) and
            (BookingsTable.clientId eq raterId) and
            (BookingsTable.photographerId eq rateeId) and
            (BookingsTable.status inList listOf(BookingStatus.COMPLETED, BookingStatus.CANCELLED))
        }.count() > 0
    }

    suspend fun checkExistingRating(bookingId: Long): Boolean = dbQuery {
        RatingsTable.selectAll().where { RatingsTable.bookingId eq bookingId }.count() > 0
    }

    suspend fun createRating(bookingId: Long, raterId: Long, rateeId: Long, request: CreateRatingRequest): RatingDto? = dbQuery {
        val insertId = RatingsTable.insertAndGetId {
            it[RatingsTable.bookingId] = bookingId
            it[RatingsTable.raterId] = raterId
            it[RatingsTable.rateeId] = rateeId
            it[ratingValue] = request.score.toShort()
            it[comment] = request.comment
        }

        // Calculate and update portfolio score
        recalculatePortfolioScore(rateeId)

        getRatingById(insertId.value)
    }

    suspend fun getRatings(rateeId: Long, page: Int, limit: Int): PaginatedRatingsResponse = dbQuery {
        val offsetVal = ((page - 1) * limit).toLong()

        val query = RatingsTable.innerJoin(UsersTable, { RatingsTable.raterId }, { UsersTable.id })
            .selectAll().where { RatingsTable.rateeId eq rateeId }

        val totalRecords = query.count()
        val totalPages = Math.ceil(totalRecords.toDouble() / limit).toInt()

        val rows = query.limit(limit, offsetVal).toList()
        
        val ratings = rows.map { row ->
            RatingDto(
                id = row[RatingsTable.id].value,
                photographerId = row[RatingsTable.rateeId].value,
                reviewerId = row[RatingsTable.raterId].value,
                reviewerUsername = row[UsersTable.username],
                reviewerAvatar = row[UsersTable.profilePictureUrl],
                score = row[RatingsTable.ratingValue].toInt(),
                comment = row[RatingsTable.comment],
                createdAt = row[RatingsTable.createdAt].toString()
            )
        }

        PaginatedRatingsResponse(
            ratings = ratings,
            meta = PaginationMeta(currentPage = page, totalPages = totalPages, hasNext = page < totalPages)
        )
    }

    suspend fun deleteRating(ratingId: Long): Boolean = dbQuery {
        val ratingRow = RatingsTable.selectAll().where { RatingsTable.id eq ratingId }.singleOrNull() ?: return@dbQuery false
        val rateeId = ratingRow[RatingsTable.rateeId].value
        
        val deletedCount = RatingsTable.deleteWhere { RatingsTable.id eq ratingId }
        
        if (deletedCount > 0) {
            recalculatePortfolioScore(rateeId)
            true
        } else {
            false
        }
    }

    suspend fun getRatingById(ratingId: Long): RatingDto? = dbQuery {
        val row = RatingsTable.innerJoin(UsersTable, { RatingsTable.raterId }, { UsersTable.id })
            .selectAll().where { RatingsTable.id eq ratingId }
            .singleOrNull() ?: return@dbQuery null

        RatingDto(
            id = row[RatingsTable.id].value,
            photographerId = row[RatingsTable.rateeId].value,
            reviewerId = row[RatingsTable.raterId].value,
            reviewerUsername = row[UsersTable.username],
            reviewerAvatar = row[UsersTable.profilePictureUrl],
            score = row[RatingsTable.ratingValue].toInt(),
            comment = row[RatingsTable.comment],
            createdAt = row[RatingsTable.createdAt].toString()
        )
    }

    private suspend fun recalculatePortfolioScore(photographerId: Long) = dbQuery {
        val ratings = RatingsTable.selectAll().where { RatingsTable.rateeId eq photographerId }.map { it[RatingsTable.ratingValue] }
        val reviewCount = ratings.size
        
        val averageScore = if (reviewCount > 0) {
            ratings.sum() / reviewCount.toDouble()
        } else {
            0.0
        }

        PortfoliosTable.update({ PortfoliosTable.userId eq photographerId }) {
            it[ratingAvg] = averageScore.toBigDecimal()
            it[PortfoliosTable.reviewCount] = reviewCount
        }
    }
}
