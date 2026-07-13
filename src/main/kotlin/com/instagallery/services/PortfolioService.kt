package com.instagallery.services

import com.instagallery.models.common.PaginatedPhotographersResponse
import com.instagallery.models.common.PortfolioDto
import com.instagallery.models.common.UserType
import com.instagallery.models.common.AvailabilityScheduleDto
import com.instagallery.models.common.AvailabilityType
import com.instagallery.models.request.UpdatePortfolioRequest
import com.instagallery.plugins.AuthException
import com.instagallery.plugins.ValidationException
import com.instagallery.repositories.PortfolioRepository
import com.instagallery.repositories.UserRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeParseException

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

    suspend fun getAvailability(userId: Long): List<AvailabilityScheduleDto> {
        return portfolioRepository.getAvailability(userId)
    }

    suspend fun updateAvailability(userId: Long, schedules: List<AvailabilityScheduleDto>) {
        val user = userRepository.getUserById(userId)
            ?: throw AuthException("USER_NOT_FOUND", "Người dùng không tồn tại.")
        if (user.userType != UserType.PHOTOGRAPHER) {
            throw AuthException("UNAUTHORIZED_ACTION", "Chỉ Nhiếp ảnh gia mới có thể cập nhật lịch làm việc.")
        }
        portfolioRepository.updateAvailability(userId, normalizeAvailabilitySchedules(schedules))
    }

    suspend fun discoverPhotographers(location: String?, specialty: String?, minRate: Double?, maxRate: Double?, page: Int, limit: Int): PaginatedPhotographersResponse {
        val verifiedPage = if (page < 1) 1 else page
        val verifiedLimit = if (limit < 1) 20 else if (limit > 50) 50 else limit

        if (minRate != null && maxRate != null && minRate > maxRate) {
            throw ValidationException("INVALID_RANGE", "Giá tối thiểu không thể lớn hơn giá tối đa.")
        }

        return portfolioRepository.getPhotographers(location, specialty, minRate, maxRate, verifiedPage, verifiedLimit)
    }

    private fun normalizeAvailabilitySchedules(schedules: List<AvailabilityScheduleDto>): List<AvailabilityScheduleDto> {
        val normalized = schedules.mapIndexed { index, schedule ->
            val startTime = parseAvailabilityTime(schedule.startTime, index, "startTime")
            val endTime = parseAvailabilityTime(schedule.endTime, index, "endTime")
            if (!endTime.isAfter(startTime)) {
                throw ValidationException("INVALID_AVAILABILITY_TIME", "Gio ket thuc phai lon hon gio bat dau.")
            }

            when (schedule.type) {
                AvailabilityType.RECURRING -> {
                    val dayOfWeek = schedule.dayOfWeek
                        ?: throw ValidationException("INVALID_AVAILABILITY_DAY", "Lich lap lai phai co dayOfWeek.")
                    schedule.copy(
                        id = null,
                        dayOfWeek = dayOfWeek,
                        specificDate = null,
                        startTime = startTime.toString(),
                        endTime = endTime.toString(),
                        isBooked = false,
                    )
                }

                AvailabilityType.SPECIFIC_DATE -> {
                    val specificDate = schedule.specificDate
                        ?.let { rawDate -> parseAvailabilityDate(rawDate, index).toString() }
                        ?: throw ValidationException("INVALID_AVAILABILITY_DATE", "Lich ngay cu the phai co specificDate.")
                    schedule.copy(
                        id = null,
                        dayOfWeek = null,
                        specificDate = specificDate,
                        startTime = startTime.toString(),
                        endTime = endTime.toString(),
                        isBooked = false,
                    )
                }
            }
        }

        normalized
            .groupBy { schedule ->
                when (schedule.type) {
                    AvailabilityType.RECURRING -> "${schedule.type}:${schedule.dayOfWeek}"
                    AvailabilityType.SPECIFIC_DATE -> "${schedule.type}:${schedule.specificDate}"
                }
            }
            .forEach { (_, group) ->
                val ordered = group.sortedBy { it.startTime }
                ordered.zipWithNext().forEach { (left, right) ->
                    val nextStart = parseAvailabilityTime(right.startTime, 0, "startTime")
                    val previousEnd = parseAvailabilityTime(left.endTime, 0, "endTime")
                    if (nextStart.isBefore(previousEnd)) {
                        throw ValidationException("AVAILABILITY_OVERLAP", "Cac khung gio lam viec khong duoc chong len nhau.")
                    }
                }
            }

        return normalized.distinctBy {
            "${it.type}:${it.dayOfWeek}:${it.specificDate}:${it.startTime}:${it.endTime}"
        }
    }

    private fun parseAvailabilityTime(rawTime: String, index: Int, fieldName: String): LocalTime {
        val parts = rawTime.trim().split(":")
        val hour = parts.getOrNull(0)?.toIntOrNull()
        val minute = parts.getOrNull(1)?.toIntOrNull()
        if (parts.size != 2 || hour == null || minute == null || hour !in 0..23 || minute !in 0..59) {
            throw ValidationException("INVALID_AVAILABILITY_TIME", "Khung gio #${index + 1} co $fieldName khong hop le. Dinh dang dung la HH:mm.")
        }
        return LocalTime.of(hour, minute)
    }

    private fun parseAvailabilityDate(rawDate: String, index: Int): LocalDate {
        return try {
            LocalDate.parse(rawDate.trim())
        } catch (e: DateTimeParseException) {
            throw ValidationException("INVALID_AVAILABILITY_DATE", "Khung gio #${index + 1} co specificDate khong hop le. Dinh dang dung la yyyy-MM-dd.")
        }
    }
}
