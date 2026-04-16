package com.instagallery.services

import com.instagallery.models.common.AdminGrowthDto
import com.instagallery.models.common.AdminStatsDto
import com.instagallery.plugins.AuthException
import com.instagallery.plugins.ValidationException
import com.instagallery.repositories.AdminRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AdminService : KoinComponent {
    private val adminRepository: AdminRepository by inject()

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
    suspend fun listUsers(page: Int, limit: Int, search: String?, status: String?): Any {
        val verifiedPage = if (page < 1) 1 else page
        val verifiedLimit = if (limit < 1) 20 else if (limit > 100) 100 else limit
        return adminRepository.listUsers(verifiedPage, verifiedLimit, search, status)
    }

    // --- FR-43: CHI TIẾT NGƯỜI DÙNG ---
    suspend fun getUserDetail(userId: Long): Any {
        return adminRepository.getUserDetail(userId)
            ?: throw AuthException("USER_NOT_FOUND", "Người dùng không tồn tại.")
    }

    // --- FR-46: TỪ KHÓA CẤM ---
    suspend fun getBannedKeywords(): Any {
        return adminRepository.getBannedKeywords()
    }

    suspend fun addBannedKeyword(keyword: String): Boolean {
        val clean = keyword.trim().lowercase()
        if (clean.isBlank()) {
            throw com.instagallery.plugins.ValidationException("INVALID_KEYWORD", "Từ khóa không được để trống.")
        }
        return adminRepository.addBannedKeyword(clean)
    }

    suspend fun removeBannedKeyword(keywordId: Long): Boolean {
        return adminRepository.removeBannedKeyword(keywordId)
    }
}
