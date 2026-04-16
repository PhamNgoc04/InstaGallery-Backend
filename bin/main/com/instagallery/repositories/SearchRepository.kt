package com.instagallery.repositories

import com.instagallery.database.DatabaseFactory.dbQuery
import com.instagallery.database.tables.*
import com.instagallery.models.common.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class SearchRepository {

    suspend fun searchAll(query: String, type: String, limit: Int): GlobalSearchResponse = dbQuery {
        val likePattern = "%${query}%"
        
        val usersList = mutableListOf<UserSearchDto>()
        val postsList = mutableListOf<PostSearchDto>()

        if (type == "USERS" || type == "ALL") {
            val userRows = UsersTable
                .selectAll().where { (UsersTable.fullName like likePattern) or (UsersTable.username like likePattern) }
                .limit(limit)
                .toList()

            userRows.forEach { row ->
                usersList.add(
                    UserSearchDto(
                        userId = row[UsersTable.id].value,
                        fullName = row[UsersTable.fullName],
                        username = row[UsersTable.username],
                        avatar = row[UsersTable.profilePictureUrl],
                        role = row[UsersTable.role].name,
                        userType = row[UsersTable.userType].name
                    )
                )
            }
        }

        if (type == "POSTS" || type == "ALL") {
            val postRows = PostsTable
                .selectAll().where { (PostsTable.caption like likePattern) and (PostsTable.deletedAt.isNull()) and (PostsTable.visibility eq PostVisibility.PUBLIC) }
                .orderBy(PostsTable.likeCount to SortOrder.DESC) // Ưu tiên top posts
                .limit(limit)
                .toList()

            postRows.forEach { row ->
                val postIdVal = row[PostsTable.id].value
                // Lấy 1 hình cover làm đại diện
                val mediaRow = PostMediaTable.selectAll().where { PostMediaTable.postId eq postIdVal }.limit(1).singleOrNull()
                
                postsList.add(
                    PostSearchDto(
                        postId = postIdVal,
                        caption = row[PostsTable.caption]?.take(50), // Preview 50 ký tự
                        coverImage = mediaRow?.get(PostMediaTable.mediaFileUrl),
                        likeCount = row[PostsTable.likeCount]
                    )
                )
            }
        }

        GlobalSearchResponse(users = usersList, posts = postsList)
    }

    suspend fun saveSearchToHistory(userId: Long, query: String, resultsCount: Int?) = dbQuery {
        // Chỉ lưu nếu query không rỗng
        if (query.trim().isBlank()) return@dbQuery

        // Update nếu đã tồn tại để đẩy lên trên cùng
        val existing = SearchHistoriesTable.selectAll().where { (SearchHistoriesTable.userId eq userId) and (SearchHistoriesTable.queryText eq query) }.singleOrNull()
        
        if (existing != null) {
            val id = existing[SearchHistoriesTable.id]
            SearchHistoriesTable.update({ SearchHistoriesTable.id eq id }) {
                it[searchedAt] = java.time.Instant.now()
            }
        } else {
            SearchHistoriesTable.insert {
                it[SearchHistoriesTable.userId] = userId
                it[queryText] = query
                it[resultCount] = resultsCount
            }
        }
    }

    suspend fun getSearchHistory(userId: Long, limit: Int = 10): SearchHistoryResponse = dbQuery {
        val historyRows = SearchHistoriesTable
            .selectAll().where { SearchHistoriesTable.userId eq userId }
            .orderBy(SearchHistoriesTable.searchedAt to SortOrder.DESC)
            .limit(limit)
            .map { it[SearchHistoriesTable.queryText] }

        SearchHistoryResponse(hits = historyRows)
    }

    suspend fun clearSearchHistory(userId: Long): Boolean = dbQuery {
        val rows = SearchHistoriesTable.deleteWhere { SearchHistoriesTable.userId eq userId }
        rows > 0
    }

    suspend fun getTrendingSearches(limit: Int): List<Map<String, Any>> = dbQuery {
        // Return top searched queries ordered by result frequency
        SearchHistoriesTable
            .select(SearchHistoriesTable.queryText, SearchHistoriesTable.queryText.count())
            .groupBy(SearchHistoriesTable.queryText)
            .orderBy(SearchHistoriesTable.queryText.count() to SortOrder.DESC)
            .limit(limit)
            .map { row ->
                mapOf(
                    "keyword" to row[SearchHistoriesTable.queryText],
                    "count" to row[SearchHistoriesTable.queryText.count()]
                )
            }
    }
}
