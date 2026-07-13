package com.instagallery.services

import com.instagallery.models.common.PhotographerServiceDto
import com.instagallery.models.common.PhotographerServicesResponse
import com.instagallery.models.request.UpsertPhotographerServiceRequest
import com.instagallery.plugins.AuthException
import com.instagallery.plugins.ValidationException
import com.instagallery.repositories.PhotographerServiceRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PhotographerServiceService : KoinComponent {
    private val repository: PhotographerServiceRepository by inject()

    suspend fun listPublic(photographerId: Long): PhotographerServicesResponse {
        ensurePhotographer(photographerId)
        return PhotographerServicesResponse(
            services = repository.listForPhotographer(photographerId, includeInactive = false),
        )
    }

    suspend fun listMine(photographerId: Long): PhotographerServicesResponse {
        ensurePhotographer(photographerId)
        return PhotographerServicesResponse(
            services = repository.listForPhotographer(photographerId, includeInactive = true),
        )
    }

    suspend fun getMine(photographerId: Long, serviceId: Long): PhotographerServiceDto {
        ensurePhotographer(photographerId)
        return repository.getOwnedById(photographerId, serviceId)
            ?: throw AuthException("SERVICE_NOT_FOUND", "Goi chup khong ton tai.")
    }

    suspend fun create(photographerId: Long, request: UpsertPhotographerServiceRequest): PhotographerServiceDto {
        ensurePhotographer(photographerId)
        validateRequest(request)
        return repository.create(photographerId, request)
    }

    suspend fun update(
        photographerId: Long,
        serviceId: Long,
        request: UpsertPhotographerServiceRequest,
    ): PhotographerServiceDto {
        ensurePhotographer(photographerId)
        validateRequest(request)
        return repository.update(photographerId, serviceId, request)
            ?: throw AuthException("SERVICE_NOT_FOUND", "Goi chup khong ton tai.")
    }

    suspend fun setActive(photographerId: Long, serviceId: Long, isActive: Boolean) {
        ensurePhotographer(photographerId)
        if (!repository.setActive(photographerId, serviceId, isActive)) {
            throw AuthException("SERVICE_NOT_FOUND", "Goi chup khong ton tai.")
        }
    }

    suspend fun archive(photographerId: Long, serviceId: Long) {
        ensurePhotographer(photographerId)
        if (!repository.archive(photographerId, serviceId)) {
            throw AuthException("SERVICE_NOT_FOUND", "Goi chup khong ton tai.")
        }
    }

    private suspend fun ensurePhotographer(userId: Long) {
        if (!repository.isPhotographer(userId)) {
            throw AuthException("PHOTOGRAPHER_NOT_FOUND", "Nhiep anh gia khong ton tai hoac chua kich hoat.")
        }
    }

    private fun validateRequest(request: UpsertPhotographerServiceRequest) {
        if (request.name.isBlank()) {
            throw ValidationException("INVALID_SERVICE_NAME", "Ten goi chup khong duoc de trong.")
        }
        if (request.category.isBlank()) {
            throw ValidationException("INVALID_SERVICE_CATEGORY", "Loai chup khong duoc de trong.")
        }
        if (request.price < 0) {
            throw ValidationException("INVALID_SERVICE_PRICE", "Gia goi chup khong hop le.")
        }
        if (request.durationMinutes < 30) {
            throw ValidationException("INVALID_SERVICE_DURATION", "Thoi luong goi chup toi thieu 30 phut.")
        }
    }
}
