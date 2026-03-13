package com.instagallery.services

import com.instagallery.models.common.PaginatedPhotographersResponse
import com.instagallery.models.common.PortfolioDto
import com.instagallery.models.common.UserType
import com.instagallery.models.request.UpdatePortfolioRequest
import com.instagallery.plugins.AuthException
import com.instagallery.plugins.ValidationException
import com.instagallery.repositories.PortfolioRepository
import com.instagallery.repositories.UserRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PortfolioService : KoinComponent {
    private val portfolioRepository: PortfolioRepository by inject()
    private val userRepository: UserRepository by inject()

    suspend fun getMyPortfolio(userId: Long): PortfolioDto {
        return portfolioRepository.getPortfolioByUserId(userId)
            ?: throw AuthException("PORTFOLIO_NOT_FOUND", "Bạn chưa cài đặt hồ sơ nhiếp ảnh gia.")
    }

    suspend fun getUserPortfolio(userId: Long): PortfolioDto {
        // Option to verify if user exists
        val user = userRepository.getUserById(userId)
            ?: throw AuthException("USER_NOT_FOUND", "Người dùng không tồn tại.")
            
        // Might optionally check if user is a PHOTOGRAPHER
        if (user.userType != UserType.PHOTOGRAPHER) {
            // Depending on business logic, maybe return an empty mock or throw an error. Throwing for now.
             throw AuthException("INVALID_ROLE", "Người dùng này không phải là nhiếp ảnh gia.")
        }

        return portfolioRepository.getPortfolioByUserId(userId)
            ?: throw AuthException("PORTFOLIO_NOT_FOUND", "Nhiếp ảnh gia này chưa đăng tải hồ sơ.")
    }

    suspend fun updatePortfolio(userId: Long, request: UpdatePortfolioRequest): PortfolioDto {
        val user = userRepository.getUserById(userId)
            ?: throw AuthException("USER_NOT_FOUND", "Tài khoản không tồn tại.")

        if (user.userType != UserType.PHOTOGRAPHER) {
            throw AuthException("UNAUTHORIZED_ACTION", "Chỉ Nhiếp ảnh gia mới có thể cập nhật hồ sơ Portfolio.")
        }

        if (request.hourlyRate != null && request.hourlyRate < 0) {
            throw ValidationException("INVALID_RATE", "Mức giá thuê không thể là số âm.")
        }

        return portfolioRepository.upsertPortfolio(userId, request)
            ?: throw Exception("Lỗi hệ thống khi cập nhật hồ sơ.")
    }

    suspend fun discoverPhotographers(location: String?, specialty: String?, minRate: Double?, maxRate: Double?, page: Int, limit: Int): PaginatedPhotographersResponse {
        val verifiedPage = if (page < 1) 1 else page
        val verifiedLimit = if (limit < 1) 20 else if (limit > 50) 50 else limit

        if (minRate != null && maxRate != null && minRate > maxRate) {
            throw ValidationException("INVALID_RANGE", "Giá tối thiểu không thể lớn hơn giá tối đa.")
        }

        return portfolioRepository.getPhotographers(location, specialty, minRate, maxRate, verifiedPage, verifiedLimit)
    }
}
