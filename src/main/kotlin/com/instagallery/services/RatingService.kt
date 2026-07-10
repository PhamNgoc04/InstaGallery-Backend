package com.instagallery.services

import com.instagallery.models.common.PaginatedRatingsResponse
import com.instagallery.models.common.RatingDto
import com.instagallery.models.common.NotificationTargetType
import com.instagallery.models.common.NotificationType
import com.instagallery.models.request.CreateRatingRequest
import com.instagallery.plugins.AuthException
import com.instagallery.plugins.ValidationException
import com.instagallery.repositories.RatingRepository
import com.instagallery.repositories.UserRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RatingService : KoinComponent {
    private val ratingRepository: RatingRepository by inject()
    private val userRepository: UserRepository by inject()
    private val notificationService: NotificationService by inject()

    suspend fun createRating(bookingId: Long, raterId: Long, rateeId: Long, request: CreateRatingRequest): RatingDto {
        if (raterId == rateeId) {
            throw ValidationException("SELF_RATING", "Bạn không thể tự đánh giá chính mình.")
        }
        
        if (request.score < 1 || request.score > 5) {
            throw ValidationException("INVALID_SCORE", "Điểm đánh giá phải từ 1 đến 5.")
        }

        val rateeExists = userRepository.getUserById(rateeId)
        if (rateeExists == null) {
            throw AuthException("USER_NOT_FOUND", "Nhiếp ảnh gia không tồn tại.")
        }

        val isEligible = ratingRepository.getBookingForRating(bookingId, raterId, rateeId)
        if (!isEligible) {
            throw AuthException("INELIGIBLE_TO_RATE", "Bạn không có quyền đánh giá vì chưa từng dùng dịch vụ hoặc ID Booking không khớp.")
        }

        val alreadyRated = ratingRepository.checkExistingRating(bookingId)
        if (alreadyRated) {
            throw ValidationException("DUPLICATE_RATING", "Bạn đã đánh giá booking này rồi.")
        }

        val rating = ratingRepository.createRating(bookingId, raterId, rateeId, request)
            ?: throw Exception("Lỗi không thể lưu đánh giá.")

        notificationService.createNotification(
            recipientUserId = rateeId,
            actorUserId = raterId,
            type = NotificationType.REVIEW_RECEIVED,
            targetType = NotificationTargetType.BOOKING,
            targetId = bookingId,
            title = "Đánh giá mới",
            body = "Khách hàng đã gửi đánh giá cho booking đã hoàn thành.",
            dedupe = true,
        )
        return rating
    }

    suspend fun getRatings(rateeId: Long, page: Int, limit: Int): PaginatedRatingsResponse {
        val rateeExists = userRepository.getUserById(rateeId)
        if (rateeExists == null) {
            throw AuthException("USER_NOT_FOUND", "Nhiếp ảnh gia không tồn tại.")
        }

        val verifiedPage = if (page < 1) 1 else page
        val verifiedLimit = if (limit < 1) 20 else if (limit > 50) 50 else limit

        return ratingRepository.getRatings(rateeId, verifiedPage, verifiedLimit)
    }

    suspend fun deleteRating(userId: Long, ratingId: Long): Boolean {
        // Need to check if this user owns the rating
        val rating = ratingRepository.getRatingById(ratingId)
            ?: throw ValidationException("RATING_NOT_FOUND", "Đánh giá không tồn tại.")

        if (rating.reviewerId != userId) {
             throw AuthException("UNAUTHORIZED_ACTION", "Bạn không có quyền xóa đánh giá của người khác.")
        }

        return ratingRepository.deleteRating(ratingId)
    }

    // --- FR-39: UPDATE RATING ---
    suspend fun updateRating(userId: Long, ratingId: Long, request: CreateRatingRequest): RatingDto {
        val rating = ratingRepository.getRatingById(ratingId)
            ?: throw ValidationException("RATING_NOT_FOUND", "Đánh giá không tồn tại.")

        if (rating.reviewerId != userId) {
            throw AuthException("UNAUTHORIZED_ACTION", "Bạn không có quyền sửa đánh giá của người khác.")
        }

        if (request.score < 1 || request.score > 5) {
            throw ValidationException("INVALID_SCORE", "Điểm đánh giá phải từ 1 đến 5.")
        }

        return ratingRepository.updateRating(ratingId, request)
            ?: throw Exception("Lỗi không thể cập nhật đánh giá.")
    }
}
