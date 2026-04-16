package com.instagallery.services

import com.instagallery.models.common.GlobalSearchResponse
import com.instagallery.models.common.SearchHistoryResponse
import com.instagallery.plugins.ValidationException
import com.instagallery.repositories.SearchRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SearchService : KoinComponent {
    private val searchRepo: SearchRepository by inject()

    suspend fun searchAll(userId: Long?, query: String, type: String, limit: Int): GlobalSearchResponse {
        val q = query.trim()
        if (q.isBlank()) {
            throw ValidationException("EMPTY_QUERY", "Vui lòng nhập từ khóa tìm kiếm.")
        }

        val verifiedType = when(type.uppercase()) {
            "USERS" -> "USERS"
            "POSTS" -> "POSTS"
            else -> "ALL"
        }

        val verifiedLimit = if (limit < 1) 20 else if (limit > 50) 50 else limit

        val response = searchRepo.searchAll(q, verifiedType, verifiedLimit)

        // Count total results realistically
        val resultsCount = response.users.size + response.posts.size

        // Ghi lại lịch sử nếu có user login
        if (userId != null) {
            searchRepo.saveSearchToHistory(userId, q, resultsCount)
        }

        return response
    }

    suspend fun getSearchHistory(userId: Long, limit: Int = 10): SearchHistoryResponse {
        return searchRepo.getSearchHistory(userId, limit)
    }

    suspend fun clearSearchHistory(userId: Long) {
        searchRepo.clearSearchHistory(userId)
    }

    // --- FR-24: TRENDING SEARCHES ---
    suspend fun getTrendingSearches(limit: Int): List<Any> {
        val verifiedLimit = if (limit < 1) 10 else if (limit > 50) 50 else limit
        return searchRepo.getTrendingSearches(verifiedLimit)
    }
}
