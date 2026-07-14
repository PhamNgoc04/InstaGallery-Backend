    package com.instagallery.services

import com.instagallery.models.common.AdminGrowthDto
import com.instagallery.models.common.AdminStatsDto
import com.instagallery.models.common.AdminBannedKeywordDto
import com.instagallery.models.common.AdminBookingDto
import com.instagallery.models.common.AdminBookingsResponse
import com.instagallery.models.common.AdminCreateNotificationResponse
import com.instagallery.models.common.AdminMediaLibraryResponse
import com.instagallery.models.common.AdminNotificationsResponse
import com.instagallery.models.common.AdminPostDto
import com.instagallery.models.common.AdminPostsResponse
import com.instagallery.models.common.AdminRatingsResponse
import com.instagallery.models.common.AdminUserDetailDto
import com.instagallery.models.common.AdminUsersResponse
import com.instagallery.models.common.BookingStatus
import com.instagallery.models.common.NotificationType
import com.instagallery.plugins.AuthException
import com.instagallery.plugins.ValidationException
import com.instagallery.repositories.AdminRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AdminService : KoinComponent {
    private val adminRepository: AdminRepository by inject()
    private val notificationService: NotificationService by inject()

    suspend fun getOverviewStats(): AdminStatsDto {
        return adminRepository.getOverviewStats()
    }

    suspend fun getGrowth(type: String, days: Int): List<AdminGrowthDto> {
        val validTypes = listOf("USERS", "POSTS", "BOOKINGS")
        val upperType = type.uppercase()

        if (upperType !in validTypes) {
            throw ValidationException("INVALID_TYPE", "Loại biểu đồ không hợp lệ. Vui lòng chọn USERS, POSTS hoặc BOOKINGS.")
        }

        val verifiedDays = if (days < 1) 7 else if (days > 90) 90 else days
        
        return adminRepository.getGrowth(upperType, verifiedDays)
    }

    // --- MODERATION ---
    suspend fun banUser(targetUserId: Long, request: com.instagallery.models.request.BanUserRequest): Boolean {
        val success = adminRepository.banUser(targetUserId, request.isBanned, request.reason)
        if (!success) {
            throw AuthException("USER_NOT_FOUND", "Người dùng không tồn tại.")
        }
        return true
    }

    suspend fun deletePost(postId: Long): Boolean {
        val success = adminRepository.deletePostAsAdmin(postId)
        if (!success) {
            throw AuthException("POST_NOT_FOUND", "Bài viết không tồn tại.")
        }
        return true
    }

    suspend fun deleteComment(commentId: Long): Boolean {
        val success = adminRepository.deleteCommentAsAdmin(commentId)
        if (!success) {
            throw AuthException("COMMENT_NOT_FOUND", "Bình luận không tồn tại.")
        }
        return true
    }

    // --- FR-43: DANH SÁCH NGƯỜI DÙNG ---
    suspend fun listUsers(page: Int, limit: Int, search: String?, status: String?): AdminUsersResponse {
        val verifiedPage = if (page < 1) 1 else page
        val verifiedLimit = if (limit < 1) 20 else if (limit > 100) 100 else limit
        return adminRepository.listUsers(verifiedPage, verifiedLimit, search, status)
    }

    // --- FR-43: CHI TIẾT NGƯỜI DÙNG ---
    suspend fun getUserDetail(userId: Long): AdminUserDetailDto {
        return adminRepository.getUserDetail(userId)
            ?: throw AuthException("USER_NOT_FOUND", "Người dùng không tồn tại.")
    }

    suspend fun verifyUser(targetUserId: Long, isVerified: Boolean): Boolean {
        val success = adminRepository.verifyUser(targetUserId, isVerified)
        if (!success) {
            throw AuthException("USER_NOT_FOUND", "Người dùng không tồn tại.")
        }
        return true
    }

    suspend fun featurePhotographer(targetUserId: Long, isFeatured: Boolean): Boolean {
        val success = adminRepository.featurePhotographer(targetUserId, isFeatured)
        if (!success) {
            throw AuthException("USER_NOT_FOUND", "Người dùng không tồn tại hoặc lỗi hồ sơ.")
        }
        return true
    }

    suspend fun listPosts(page: Int, limit: Int, search: String?, status: String?): AdminPostsResponse {
        return adminRepository.listPosts(verifiedPage(page), verifiedLimit(limit), search, status)
    }

    suspend fun getPostDetail(postId: Long): AdminPostDto {
        return adminRepository.getPostDetail(postId)
            ?: throw AuthException("POST_NOT_FOUND", "Bài viết không tồn tại.")
    }

    suspend fun updatePostStatus(postId: Long, status: String): Boolean {
        val normalized = status.uppercase()
        if (normalized !in listOf("ACTIVE", "VISIBLE", "APPROVED", "HIDDEN", "DELETED")) {
            throw ValidationException("INVALID_STATUS", "Trạng thái bài viết không hợp lệ.")
        }
        val success = adminRepository.updatePostStatus(postId, normalized)
        if (!success) {
            throw AuthException("POST_NOT_FOUND", "Bài viết không tồn tại.")
        }
        return true
    }

    suspend fun listBookings(page: Int, limit: Int, search: String?, status: String?): AdminBookingsResponse {
        return adminRepository.listBookings(verifiedPage(page), verifiedLimit(limit), search, status)
    }

    suspend fun getBookingDetail(bookingId: Long): AdminBookingDto {
        return adminRepository.getBookingDetail(bookingId)
            ?: throw AuthException("BOOKING_NOT_FOUND", "Booking không tồn tại.")
    }

    suspend fun updateBookingStatus(bookingId: Long, status: String): Boolean {
        val normalized = runCatching { BookingStatus.valueOf(status.uppercase()) }.getOrNull()
            ?: throw ValidationException("INVALID_STATUS", "Trạng thái booking không hợp lệ.")
        val success = adminRepository.updateBookingStatus(bookingId, normalized)
        if (!success) {
            throw AuthException("BOOKING_NOT_FOUND", "Booking không tồn tại.")
        }
        return true
    }

    suspend fun listRatings(page: Int, limit: Int, search: String?, status: String?): AdminRatingsResponse {
        val normalizedStatus = status?.takeIf { it.isNotBlank() }?.let(::normalizeRatingStatus)
        return adminRepository.listRatings(verifiedPage(page), verifiedLimit(limit), search, normalizedStatus)
    }

    suspend fun deleteRating(ratingId: Long): Boolean {
        val success = adminRepository.deleteRating(ratingId)
        if (!success) {
            throw AuthException("RATING_NOT_FOUND", "Đánh giá không tồn tại.")
        }
        return true
    }

    suspend fun updateRatingStatus(ratingId: Long, status: String): Boolean {
        val normalized = normalizeRatingStatus(status)
        val success = adminRepository.updateRatingStatus(ratingId, normalized)
        if (!success) {
            throw AuthException("RATING_NOT_FOUND", "Đánh giá không tồn tại.")
        }
        return true
    }

    suspend fun listMedia(page: Int, limit: Int, search: String?): AdminMediaLibraryResponse {
        return adminRepository.listMedia(verifiedPage(page), verifiedLimit(limit), search)
    }

    suspend fun deleteMedia(mediaId: Long): Boolean {
        val success = adminRepository.deleteMedia(mediaId)
        if (!success) {
            throw AuthException("MEDIA_NOT_FOUND", "Media không tồn tại.")
        }
        return true
    }

    suspend fun listNotifications(page: Int, limit: Int, search: String?): AdminNotificationsResponse {
        return adminRepository.listNotifications(verifiedPage(page), verifiedLimit(limit), search)
    }

    suspend fun createNotification(title: String, body: String, target: String): AdminCreateNotificationResponse {
        if (title.isBlank() || body.isBlank()) {
            throw ValidationException("INVALID_NOTIFICATION", "Tiêu đề và nội dung thông báo không được để trống.")
        }
        val recipientIds = adminRepository.listNotificationRecipientIds(target.trim().ifBlank { "ALL" })
        recipientIds.forEach { recipientId ->
            notificationService.createNotification(
                recipientUserId = recipientId,
                actorUserId = null,
                type = NotificationType.SYSTEM,
                targetType = null,
                targetId = null,
                title = title.trim(),
                body = body.trim(),
            )
        }
        return AdminCreateNotificationResponse(sentCount = recipientIds.size)
    }

    // --- FR-46: TỪ KHÓA CẤM ---
    suspend fun getBannedKeywords(): List<AdminBannedKeywordDto> {
        return adminRepository.getBannedKeywords()
    }

    suspend fun addBannedKeyword(keyword: String, isRegex: Boolean = false): Boolean {
        val clean = if (isRegex) keyword.trim() else keyword.trim().lowercase()
        if (clean.isBlank()) {
            throw com.instagallery.plugins.ValidationException("INVALID_KEYWORD", "Từ khóa không được để trống.")
        }
        return adminRepository.addBannedKeyword(clean, isRegex)
    }

    suspend fun removeBannedKeyword(keywordId: Long): Boolean {
        return adminRepository.removeBannedKeyword(keywordId)
    }

    suspend fun getActivityLogs(query: String?, page: Int, limit: Int): com.instagallery.models.common.AdminActivityLogsResponse {
        return adminRepository.getActivityLogs(query, verifiedPage(page), verifiedLimit(limit))
    }

    suspend fun logActivity(
        userId: Long?,
        action: String,
        targetType: com.instagallery.models.common.ActivityTargetType,
        targetId: Long?,
        ipAddress: String?,
        userAgent: String?,
        metadata: String? = null
    ) {
        adminRepository.logActivity(userId, action, targetType, targetId, ipAddress, userAgent, metadata)
    }

    private fun verifiedPage(page: Int): Int = if (page < 1) 1 else page

    private fun verifiedLimit(limit: Int): Int = if (limit < 1) 20 else if (limit > 100) 100 else limit

    private fun normalizeRatingStatus(status: String): String {
        return when (status.trim().uppercase()) {
            "APPROVED", "ACTIVE", "VISIBLE" -> "APPROVED"
            "PENDING", "REVIEWING", "IN_REVIEW", "MODERATING" -> "PENDING"
            "HIDDEN", "DELETED", "REMOVED" -> "HIDDEN"
            else -> throw ValidationException("INVALID_STATUS", "Trạng thái đánh giá không hợp lệ.")
        }
    }
}
