package com.instagallery.repositories

import com.instagallery.database.DatabaseFactory.dbQuery
import com.instagallery.database.tables.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.dao.id.EntityID
import java.time.Instant

class InteractionRepository {

    suspend fun checkPostExists(postId: Long): Boolean = dbQuery {
        PostsTable.selectAll().where { (PostsTable.id eq postId) and (PostsTable.deletedAt.isNull()) }.count() > 0
    }

    suspend fun checkCommentExists(commentId: Long): Boolean = dbQuery {
        CommentsTable.selectAll().where { (CommentsTable.id eq commentId) and (CommentsTable.deletedAt.isNull()) }.count() > 0
    }

    // ----- LIKES -----
    suspend fun toggleLike(userId: Long, postId: Long): Pair<Boolean, Int> = dbQuery {
        val existingLike = LikesTable.selectAll().where { (LikesTable.userId eq userId) and (LikesTable.postId eq postId) }.singleOrNull()
        
        val isLikedNow: Boolean

        if (existingLike != null) {
            // Unlike
            LikesTable.deleteWhere { (LikesTable.userId eq userId) and (LikesTable.postId eq postId) }
            PostsTable.update({ PostsTable.id eq postId }) {
                with(SqlExpressionBuilder) {
                    it.update(likeCount, likeCount - 1)
                }
            }
            isLikedNow = false
        } else {
            // Like
            LikesTable.insert {
                it[LikesTable.userId] = EntityID(userId, UsersTable)
                it[LikesTable.postId] = EntityID(postId, PostsTable)
            }
            PostsTable.update({ PostsTable.id eq postId }) {
                with(SqlExpressionBuilder) {
                    it.update(likeCount, likeCount + 1)
                }
            }
            isLikedNow = true
        }

        val updatedPost = PostsTable.selectAll().where { PostsTable.id eq postId }.single()
        Pair(isLikedNow, updatedPost[PostsTable.likeCount])
    }

    // ----- SAVES (BOOKMARKS) -----
    suspend fun toggleSave(userId: Long, postId: Long): Boolean = dbQuery {
        val existingSave = SavedPostsTable.selectAll().where { (SavedPostsTable.userId eq userId) and (SavedPostsTable.postId eq postId) }.singleOrNull()

        if (existingSave != null) {
            SavedPostsTable.deleteWhere { (SavedPostsTable.userId eq userId) and (SavedPostsTable.postId eq postId) }
            false
        } else {
            SavedPostsTable.insert {
                it[SavedPostsTable.userId] = EntityID(userId, UsersTable)
                it[SavedPostsTable.postId] = EntityID(postId, PostsTable)
            }
            true
        }
    }

    // ----- COMMENTS -----
    suspend fun createComment(userId: Long, postId: Long, content: String, parentId: Long?): com.instagallery.models.common.CommentDto = dbQuery {
        val insertStmt = CommentsTable.insertAndGetId {
            it[CommentsTable.postId] = EntityID(postId, PostsTable)
            it[CommentsTable.userId] = EntityID(userId, UsersTable)
            it[CommentsTable.content] = content
            it[CommentsTable.parentCommentId] = parentId?.let { p -> EntityID(p, CommentsTable) }
        }

        // Increment count
        PostsTable.update({ PostsTable.id eq postId }) {
            with(SqlExpressionBuilder) {
                it.update(commentCount, commentCount + 1)
            }
        }

        // Return Data
        val id = insertStmt.value
        val userRow = UsersTable.selectAll().where { UsersTable.id eq userId }.single()
        
        com.instagallery.models.common.CommentDto(
            commentId = id,
            postId = postId,
            userId = userId,
            username = userRow[UsersTable.username],
            avatar = userRow[UsersTable.profilePictureUrl],
            content = content,
            parentId = parentId,
            replyCount = 0,
            createdAt = java.time.Instant.now().toString()
        )
    }

    suspend fun getComments(postId: Long, page: Int, limit: Int): com.instagallery.models.common.PaginatedCommentsResponse = dbQuery {
        val offsetVal = ((page - 1) * limit).toLong()

        // Fetch ROOT comments only (parentId is null)
        val query = (CommentsTable innerJoin UsersTable)
            .selectAll().where { (CommentsTable.postId eq postId) and (CommentsTable.parentCommentId.isNull()) and (CommentsTable.deletedAt.isNull()) }
            .orderBy(CommentsTable.createdAt to SortOrder.DESC)

        val totalRecords = query.count()
        val totalPages = Math.ceil(totalRecords.toDouble() / limit).toInt()

        val commentRows = query.limit(limit, offsetVal).toList()
        
        // Fetch replies efficiently in a single query
        val rootCommentIds = commentRows.map { it[CommentsTable.id].value }
        
        val allRepliesRows = if (rootCommentIds.isNotEmpty()) {
            (CommentsTable innerJoin UsersTable)
                .selectAll()
                .where { (CommentsTable.parentCommentId inList rootCommentIds) and (CommentsTable.deletedAt.isNull()) }
                .orderBy(CommentsTable.createdAt to SortOrder.ASC)
                .toList()
        } else {
            emptyList()
        }
        
        val repliesByParentId = allRepliesRows.groupBy { it[CommentsTable.parentCommentId]!!.value }

        val commentsList = commentRows.map { row ->
            val cId = row[CommentsTable.id].value
            val replyRows = repliesByParentId[cId] ?: emptyList()
            
            val repliesList = replyRows.map { rRow ->
                com.instagallery.models.common.CommentDto(
                    commentId = rRow[CommentsTable.id].value,
                    postId = rRow[CommentsTable.postId].value,
                    userId = rRow[UsersTable.id].value,
                    username = rRow[UsersTable.username],
                    avatar = rRow[UsersTable.profilePictureUrl],
                    content = rRow[CommentsTable.content],
                    parentId = rRow[CommentsTable.parentCommentId]?.value,
                    replyCount = 0,
                    createdAt = rRow[CommentsTable.createdAt].toString(),
                    replies = null
                )
            }

            com.instagallery.models.common.CommentDto(
                commentId = cId,
                postId = row[CommentsTable.postId].value,
                userId = row[UsersTable.id].value,
                username = row[UsersTable.username],
                avatar = row[UsersTable.profilePictureUrl],
                content = row[CommentsTable.content],
                parentId = null,
                replyCount = repliesList.size,
                createdAt = row[CommentsTable.createdAt].toString(),
                replies = repliesList
            )
        }

        com.instagallery.models.common.PaginatedCommentsResponse(
            comments = commentsList,
            meta = com.instagallery.models.common.PaginationMeta(
                currentPage = page,
                totalPages = totalPages,
                hasNext = page < totalPages
            )
        )
    }

    suspend fun updateComment(userId: Long, commentId: Long, content: String): com.instagallery.models.common.CommentDto? = dbQuery {
        val count = CommentsTable.update({ (CommentsTable.id eq commentId) and (CommentsTable.userId eq userId) }) {
            it[CommentsTable.content] = content
            it[updatedAt] = java.time.Instant.now()
        }

        if (count > 0) {
            val row = (CommentsTable innerJoin UsersTable).selectAll().where { CommentsTable.id eq commentId }.single()
            com.instagallery.models.common.CommentDto(
                commentId = row[CommentsTable.id].value,
                postId = row[CommentsTable.postId].value,
                userId = row[UsersTable.id].value,
                username = row[UsersTable.username],
                avatar = row[UsersTable.profilePictureUrl],
                content = row[CommentsTable.content],
                parentId = row[CommentsTable.parentCommentId]?.value,
                replyCount = row[CommentsTable.replyCount],
                createdAt = row[CommentsTable.createdAt].toString()
            )
        } else {
            null
        }
    }

    suspend fun deleteComment(userId: Long, commentId: Long): Boolean = dbQuery {
        val commentRow = CommentsTable.selectAll().where { (CommentsTable.id eq commentId) and (CommentsTable.userId eq userId) }.singleOrNull()
        
        if (commentRow != null) {
            CommentsTable.update({ CommentsTable.id eq commentId }) {
                it[deletedAt] = java.time.Instant.now()
            }
            
            // Decrement post's commentCount
            PostsTable.update({ PostsTable.id eq commentRow[CommentsTable.postId] }) {
                with(SqlExpressionBuilder) {
                    it.update(commentCount, commentCount - 1)
                }
            }
            true
        } else {
            false
        }
    }

    suspend fun toggleCommentLike(userId: Long, commentId: Long): Boolean = dbQuery {
        val existingLike = CommentLikesTable.selectAll().where { (CommentLikesTable.userId eq userId) and (CommentLikesTable.commentId eq commentId) }.singleOrNull()

        if (existingLike != null) {
            CommentLikesTable.deleteWhere { (CommentLikesTable.userId eq userId) and (CommentLikesTable.commentId eq commentId) }
            CommentsTable.update({ CommentsTable.id eq commentId }) {
                with(SqlExpressionBuilder) {
                    it.update(likeCount, likeCount - 1)
                }
            }
            false
        } else {
            CommentLikesTable.insert {
                it[CommentLikesTable.userId] = EntityID(userId, UsersTable)
                it[CommentLikesTable.commentId] = EntityID(commentId, CommentsTable)
            }
            CommentsTable.update({ CommentsTable.id eq commentId }) {
                with(SqlExpressionBuilder) {
                    it.update(likeCount, likeCount + 1)
                }
            }
            true
        }
    }

    // ----- FOLLOWERS -----
    suspend fun toggleFollow(followerId: Long, followingId: Long): Boolean = dbQuery {
        val existingFollow = FollowersTable.selectAll().where { (FollowersTable.followerId eq followerId) and (FollowersTable.followingId eq followingId) }.singleOrNull()

        if (existingFollow != null) {
            // Unfollow
            FollowersTable.deleteWhere { (FollowersTable.followerId eq followerId) and (FollowersTable.followingId eq followingId) }
            false
        } else {
            // Follow
            FollowersTable.insert {
                it[FollowersTable.followerId] = EntityID(followerId, UsersTable)
                it[FollowersTable.followingId] = EntityID(followingId, UsersTable)
            }
            true
        }
    }

    suspend fun getFollowers(userId: Long, page: Int, limit: Int): com.instagallery.models.common.PaginatedFollowsResponse = dbQuery {
        val offsetVal = ((page - 1) * limit).toLong()

        // Fetch those who follow userId (UsersTable id = FollowersTable followerId)
        val query = UsersTable.innerJoin(FollowersTable, { UsersTable.id }, { FollowersTable.followerId })
            .selectAll().where { FollowersTable.followingId eq userId }
            
        val totalRecords = query.count()
        val totalPages = Math.ceil(totalRecords.toDouble() / limit).toInt()

        val rows = query.limit(limit, offsetVal).toList()

        val dtoList = rows.map { row ->
            com.instagallery.models.common.FollowDto(
                id = row[UsersTable.id].value,
                username = row[UsersTable.username],
                fullName = row[UsersTable.fullName],
                avatar = row[UsersTable.profilePictureUrl],
                role = row[UsersTable.role],
                userType = row[UsersTable.userType]
            )
        }

        com.instagallery.models.common.PaginatedFollowsResponse(
            users = dtoList,
            meta = com.instagallery.models.common.FollowPaginationMeta(currentPage = page, totalPages = totalPages, totalRecords = totalRecords.toInt())
        )
    }

    suspend fun getFollowing(userId: Long, page: Int, limit: Int): com.instagallery.models.common.PaginatedFollowsResponse = dbQuery {
        val offsetVal = ((page - 1) * limit).toLong()

        // Fetch those whom userId is following (UsersTable id = FollowersTable followingId); user is follower
        val query = UsersTable.innerJoin(FollowersTable, { UsersTable.id }, { FollowersTable.followingId })
            .selectAll().where { FollowersTable.followerId eq userId }
            
        val totalRecords = query.count()
        val totalPages = Math.ceil(totalRecords.toDouble() / limit).toInt()

        val rows = query.limit(limit, offsetVal).toList()

        val dtoList = rows.map { row ->
            com.instagallery.models.common.FollowDto(
                id = row[UsersTable.id].value,
                username = row[UsersTable.username],
                fullName = row[UsersTable.fullName],
                avatar = row[UsersTable.profilePictureUrl],
                role = row[UsersTable.role],
                userType = row[UsersTable.userType]
            )
        }

        com.instagallery.models.common.PaginatedFollowsResponse(
            users = dtoList,
            meta = com.instagallery.models.common.FollowPaginationMeta(currentPage = page, totalPages = totalPages, totalRecords = totalRecords.toInt())
        )
    }
}
