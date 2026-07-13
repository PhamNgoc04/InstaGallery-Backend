package com.instagallery.repositories

import com.instagallery.database.DatabaseFactory.dbQuery
import com.instagallery.database.tables.PhotographerServicesTable
import com.instagallery.database.tables.UsersTable
import com.instagallery.models.common.PhotographerServiceDto
import com.instagallery.models.common.UserType
import com.instagallery.models.request.UpsertPhotographerServiceRequest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.time.Instant

class PhotographerServiceRepository {
    suspend fun listForPhotographer(
        photographerId: Long,
        includeInactive: Boolean,
    ): List<PhotographerServiceDto> = dbQuery {
        ensureDefaultServices(photographerId)
        var query = PhotographerServicesTable
            .selectAll()
            .where { PhotographerServicesTable.photographerId eq photographerId }

        if (!includeInactive) {
            query = query.andWhere { PhotographerServicesTable.isActive eq true }
        }

        query
            .orderBy(PhotographerServicesTable.isActive to SortOrder.DESC, PhotographerServicesTable.category to SortOrder.ASC)
            .map { row -> row.toPhotographerServiceDto() }
    }

    suspend fun getById(serviceId: Long): PhotographerServiceDto? = dbQuery {
        PhotographerServicesTable
            .selectAll()
            .where { PhotographerServicesTable.id eq serviceId }
            .singleOrNull()
            ?.toPhotographerServiceDto()
    }

    suspend fun getOwnedById(photographerId: Long, serviceId: Long): PhotographerServiceDto? = dbQuery {
        PhotographerServicesTable
            .selectAll()
            .where {
                (PhotographerServicesTable.id eq serviceId) and
                    (PhotographerServicesTable.photographerId eq photographerId)
            }
            .singleOrNull()
            ?.toPhotographerServiceDto()
    }

    suspend fun create(photographerId: Long, request: UpsertPhotographerServiceRequest): PhotographerServiceDto = dbQuery {
        val newId = PhotographerServicesTable.insertAndGetId {
            it[PhotographerServicesTable.photographerId] = photographerId
            it[name] = request.name.trim()
            it[category] = request.category.trim().uppercase()
            it[price] = request.price.toBigDecimal()
            it[currency] = request.currency.trim().uppercase().ifBlank { "VND" }
            it[durationMinutes] = request.durationMinutes
            it[photoCount] = request.photoCount
            it[editedPhotoCount] = request.editedPhotoCount
            it[makeupIncluded] = request.makeupIncluded
            it[outfitIncluded] = request.outfitIncluded
            it[locationSupport] = request.locationSupport
            it[description] = request.description
            it[includes] = servicesJson.encodeToString(request.includes)
            it[coverUrl] = request.coverUrl
            it[isActive] = request.isActive
        }.value

        PhotographerServicesTable
            .selectAll()
            .where { PhotographerServicesTable.id eq newId }
            .single()
            .toPhotographerServiceDto()
    }

    suspend fun update(
        photographerId: Long,
        serviceId: Long,
        request: UpsertPhotographerServiceRequest,
    ): PhotographerServiceDto? = dbQuery {
        val updated = PhotographerServicesTable.update({
            (PhotographerServicesTable.id eq serviceId) and
                (PhotographerServicesTable.photographerId eq photographerId)
        }) {
            it[name] = request.name.trim()
            it[category] = request.category.trim().uppercase()
            it[price] = request.price.toBigDecimal()
            it[currency] = request.currency.trim().uppercase().ifBlank { "VND" }
            it[durationMinutes] = request.durationMinutes
            it[photoCount] = request.photoCount
            it[editedPhotoCount] = request.editedPhotoCount
            it[makeupIncluded] = request.makeupIncluded
            it[outfitIncluded] = request.outfitIncluded
            it[locationSupport] = request.locationSupport
            it[description] = request.description
            it[includes] = servicesJson.encodeToString(request.includes)
            it[coverUrl] = request.coverUrl
            it[isActive] = request.isActive
            it[updatedAt] = Instant.now()
        }
        if (updated == 0) {
            null
        } else {
            PhotographerServicesTable
                .selectAll()
                .where { PhotographerServicesTable.id eq serviceId }
                .singleOrNull()
                ?.toPhotographerServiceDto()
        }
    }

    suspend fun setActive(photographerId: Long, serviceId: Long, isActive: Boolean): Boolean = dbQuery {
        PhotographerServicesTable.update({
            (PhotographerServicesTable.id eq serviceId) and
                (PhotographerServicesTable.photographerId eq photographerId)
        }) {
            it[PhotographerServicesTable.isActive] = isActive
            it[updatedAt] = Instant.now()
        } > 0
    }

    suspend fun archive(photographerId: Long, serviceId: Long): Boolean = setActive(photographerId, serviceId, false)

    suspend fun isPhotographer(userId: Long): Boolean = dbQuery {
        UsersTable
            .selectAll()
            .where {
                (UsersTable.id eq userId) and
                    (UsersTable.userType eq UserType.PHOTOGRAPHER) and
                    (UsersTable.isActive eq true) and
                    UsersTable.deletedAt.isNull()
            }
            .count() > 0
    }

    private fun ensureDefaultServices(photographerId: Long) {
        val existingCount = PhotographerServicesTable
            .selectAll()
            .where { PhotographerServicesTable.photographerId eq photographerId }
            .count()
        if (existingCount > 0) return

        defaultServices.forEach { service ->
            PhotographerServicesTable.insertAndGetId {
                it[PhotographerServicesTable.photographerId] = photographerId
                it[name] = service.name
                it[category] = service.category
                it[price] = service.price.toBigDecimal()
                it[currency] = service.currency
                it[durationMinutes] = service.durationMinutes
                it[photoCount] = service.photoCount
                it[editedPhotoCount] = service.editedPhotoCount
                it[makeupIncluded] = service.makeupIncluded
                it[outfitIncluded] = service.outfitIncluded
                it[locationSupport] = service.locationSupport
                it[description] = service.description
                it[includes] = servicesJson.encodeToString(service.includes)
                it[coverUrl] = service.coverUrl
                it[isActive] = service.isActive
            }
        }
    }

    private fun ResultRow.toPhotographerServiceDto(): PhotographerServiceDto {
        val includesRaw = this[PhotographerServicesTable.includes]
        return PhotographerServiceDto(
            serviceId = this[PhotographerServicesTable.id].value,
            photographerId = this[PhotographerServicesTable.photographerId].value,
            name = this[PhotographerServicesTable.name],
            category = this[PhotographerServicesTable.category],
            price = this[PhotographerServicesTable.price].toDouble(),
            currency = this[PhotographerServicesTable.currency],
            durationMinutes = this[PhotographerServicesTable.durationMinutes],
            photoCount = this[PhotographerServicesTable.photoCount],
            editedPhotoCount = this[PhotographerServicesTable.editedPhotoCount],
            makeupIncluded = this[PhotographerServicesTable.makeupIncluded],
            outfitIncluded = this[PhotographerServicesTable.outfitIncluded],
            locationSupport = this[PhotographerServicesTable.locationSupport],
            description = this[PhotographerServicesTable.description],
            includes = includesRaw
                ?.let { raw -> runCatching { servicesJson.decodeFromString<List<String>>(raw) }.getOrNull() }
                .orEmpty(),
            coverUrl = this[PhotographerServicesTable.coverUrl],
            isActive = this[PhotographerServicesTable.isActive],
            createdAt = this[PhotographerServicesTable.createdAt].toString(),
            updatedAt = this[PhotographerServicesTable.updatedAt].toString(),
        )
    }

    private companion object {
        val servicesJson = Json { ignoreUnknownKeys = true }

        val defaultServices = listOf(
            DefaultService(
                name = "Portrait Basic",
                category = "PORTRAIT",
                price = 2_000_000.0,
                durationMinutes = 120,
                photoCount = 50,
                editedPhotoCount = 20,
                makeupIncluded = false,
                outfitIncluded = false,
                description = "Goi chup chan dung gon nhe cho anh ca nhan, profile va concept don gian.",
                includes = listOf("2 gio chup", "50 anh goc", "20 anh chinh sua", "Tu van tao dang"),
            ),
            DefaultService(
                name = "Portrait Premium",
                category = "PORTRAIT",
                price = 4_500_000.0,
                durationMinutes = 240,
                photoCount = 100,
                editedPhotoCount = 40,
                makeupIncluded = true,
                outfitIncluded = true,
                description = "Goi chup chan dung cao cap voi concept, trang phuc va makeup co ban.",
                includes = listOf("4 gio chup", "100 anh goc", "40 anh chinh sua", "Makeup co ban", "Tu van concept"),
            ),
            DefaultService(
                name = "Wedding Standard",
                category = "WEDDING",
                price = 12_000_000.0,
                durationMinutes = 480,
                photoCount = 300,
                editedPhotoCount = 80,
                makeupIncluded = false,
                outfitIncluded = false,
                description = "Goi chup phong su cuoi va anh cap doi trong ngay.",
                includes = listOf("8 gio chup", "300 anh goc", "80 anh chinh sua", "2 tho chup"),
            ),
            DefaultService(
                name = "Event Coverage",
                category = "EVENT",
                price = 3_000_000.0,
                durationMinutes = 180,
                photoCount = 150,
                editedPhotoCount = 30,
                makeupIncluded = false,
                outfitIncluded = false,
                description = "Goi chup su kien, hoi nghi, tiec va recap thuong hieu.",
                includes = listOf("3 gio chup", "150 anh goc", "30 anh chinh sua", "Anh recap trong 72 gio"),
            ),
        )
    }

    private data class DefaultService(
        val name: String,
        val category: String,
        val price: Double,
        val currency: String = "VND",
        val durationMinutes: Int,
        val photoCount: Int?,
        val editedPhotoCount: Int?,
        val makeupIncluded: Boolean,
        val outfitIncluded: Boolean,
        val locationSupport: Boolean = true,
        val description: String?,
        val includes: List<String>,
        val coverUrl: String? = null,
        val isActive: Boolean = true,
    )
}
