package com.instagallery.repositories

import com.instagallery.database.DatabaseFactory.dbQuery
import com.instagallery.database.tables.*
import com.instagallery.models.common.FeedMediaDto
import com.instagallery.models.common.FeedPostDto
import com.instagallery.models.common.CommentReactionResponse
import com.instagallery.models.common.PaginatedFeedResponse
import com.instagallery.models.common.PaginatedUserCommentsResponse
import com.instagallery.models.common.PaginationMeta
import com.instagallery.models.common.PostLikeUserDto
import com.instagallery.models.common.PostLikesResponse
import com.instagallery.models.common.UserCommentActivityDto
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.dao.id.EntityID
import java.time.Instant

class InteractionRepository {

    suspend fun checkPostExists(postId: Long): Boolean = dbQuery {
        PostsTable.selectAll().where { (PostsTable.id eq postId) and (PostsTable.deletedAt.isNull()) }.count() > 0
    }

    suspend fun checkCommentExists(commentId: Long): Boolean = dbQuery {
        CommentsTable.selectAll().where { (CommentsTable.id eq commentId) and (CommentsTable.deletedAt.isNull()) }.count() > 0
    }

    suspend fun checkCommentBelongsToPost(commentId: Long, postId: Long): Boolean = dbQuery {
        CommentsTable
            .selectAll()
            .where {
                (CommentsTable.id eq commentId) and
                    (CommentsTable.postId eq postId) and
                    CommentsTable.deletedAt.isNull()
            }
            .count() > 0
    }

    suspend fun getPostOwnerId(postId: Long): Long? = dbQuery {
        PostsTable
            .select(PostsTable.userId)
            .where { (PostsTable.id eq postId) and PostsTable.deletedAt.isNull() }
            .singleOrNull()
            ?.get(PostsTable.userId)
            ?.value
    }

    suspend fun getCommentOwnerId(commentId: Long): Long? = dbQuery {
        CommentsTable
            .select(CommentsTable.userId)
            .where { (CommentsTable.id eq commentId) and CommentsTable.deletedAt.isNull() }
            .singleOrNull()
            ?.get(CommentsTable.userId)
            ?.value
    }

    suspend fun getCommentPostId(commentId: Long): Long? = dbQuery {
        CommentsTable
            .select(CommentsTable.postId)
            .where { (CommentsTable.id eq commentId) and CommentsTable.deletedAt.isNull() }
            .singleOrNull()
            ?.get(CommentsTable.postId)
            ?.value
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
        val parentRow = parentId?.let { p ->
            CommentsTable
                .selectAll()
                .where {
                    (CommentsTable.id eq p) and
                        (CommentsTable.postId eq postId) and
                        CommentsTable.deletedAt.isNull()
                }
                .single()
        }
        val depth = parentRow?.let {
            (it[CommentsTable.depth].toInt() + 1)
                .coerceAtMost(Byte.MAX_VALUE.toInt())
                .toByte()
        } ?: 0

        val insertStmt = CommentsTable.insertAndGetId {
            it[CommentsTable.postId] = EntityID(postId, PostsTable)
            it[CommentsTable.userId] = EntityID(userId, UsersTable)
            it[CommentsTable.content] = content
            it[CommentsTable.parentCommentId] = parentId?.let { p -> EntityID(p, CommentsTable) }
            it[CommentsTable.depth] = depth
        }

        if (parentId != null) {
            syncReplyCount(parentId)
        }
        syncPostCommentCount(postId)

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

    suspend fun getComments(userId: Long, postId: Long, page: Int, limit: Int): com.instagallery.models.common.PaginatedCommentsResponse = dbQuery {
        val offsetVal = ((page - 1) * limit).toLong()

        // Fetch ROOT comments only (parentId is null)
        val query = (CommentsTable innerJoin UsersTable)
            .selectAll().where { (CommentsTable.postId eq postId) and (CommentsTable.parentCommentId.isNull()) and (CommentsTable.deletedAt.isNull()) }
            .orderBy(CommentsTable.createdAt to SortOrder.DESC)

        val totalRecords = query.count()
        val totalPages = Math.ceil(totalRecords.toDouble() / limit).toInt()

        val commentRows = query.limit(limit, offsetVal).toList()
        
        // Fetch all replies for this post, then attach only descendants of the paged root comments.
        val rootCommentIds = commentRows.map { it[CommentsTable.id].value }
        
        val allRepliesRows = if (rootCommentIds.isNotEmpty()) {
            (CommentsTable innerJoin UsersTable)
                .selectAll()
                .where { (CommentsTable.postId eq postId) and (CommentsTable.parentCommentId.isNotNull()) and (CommentsTable.deletedAt.isNull()) }
                .orderBy(CommentsTable.createdAt to SortOrder.ASC)
                .toList()
        } else {
            emptyList()
        }

        val allCommentIds = (commentRows + allRepliesRows)
            .map { row -> row[CommentsTable.id].value }
            .distinct()
        val likedCommentIds = likedCommentIdsFor(userId, allCommentIds)
        val dislikedCommentIds = dislikedCommentIdsFor(userId, allCommentIds)
        
        val repliesByParentId = allRepliesRows.groupBy { it[CommentsTable.parentCommentId]!!.value }

        fun buildReplies(parentId: Long): List<com.instagallery.models.common.CommentDto> {
            return repliesByParentId[parentId].orEmpty().map { rRow ->
                val replyId = rRow[CommentsTable.id].value
                val childReplies = buildReplies(replyId)
                com.instagallery.models.common.CommentDto(
                    commentId = replyId,
                    postId = rRow[CommentsTable.postId].value,
                    userId = rRow[UsersTable.id].value,
                    username = rRow[UsersTable.username],
                    avatar = rRow[UsersTable.profilePictureUrl],
                    content = rRow[CommentsTable.content],
                    parentId = rRow[CommentsTable.parentCommentId]?.value,
                    likeCount = rRow[CommentsTable.likeCount],
                    dislikeCount = rRow[CommentsTable.dislikeCount],
                    isLiked = replyId in likedCommentIds,
                    isDisliked = replyId in dislikedCommentIds,
                    replyCount = childReplies.size,
                    createdAt = rRow[CommentsTable.createdAt].toString(),
                    replies = childReplies
                )
            }
        }

        val commentsList = commentRows.map { row ->
            val cId = row[CommentsTable.id].value
            val repliesList = buildReplies(cId)

            com.instagallery.models.common.CommentDto(
                commentId = cId,
                postId = row[CommentsTable.postId].value,
                userId = row[UsersTable.id].value,
                username = row[UsersTable.username],
                avatar = row[UsersTable.profilePictureUrl],
                content = row[CommentsTable.content],
                parentId = null,
                likeCount = row[CommentsTable.likeCount],
                dislikeCount = row[CommentsTable.dislikeCount],
                isLiked = cId in likedCommentIds,
                isDisliked = cId in dislikedCommentIds,
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
        val count = CommentsTable.update({
            (CommentsTable.id eq commentId) and
                (CommentsTable.userId eq userId) and
                CommentsTable.deletedAt.isNull()
        }) {
            it[CommentsTable.content] = content
            it[CommentsTable.updatedAt] = java.time.Instant.now()
        }

        if (count > 0) {
            val row = (CommentsTable innerJoin UsersTable)
                .selectAll()
                .where { (CommentsTable.id eq commentId) and CommentsTable.deletedAt.isNull() }
                .single()
            com.instagallery.models.common.CommentDto(
                commentId = row[CommentsTable.id].value,
                postId = row[CommentsTable.postId].value,
                userId = row[UsersTable.id].value,
                username = row[UsersTable.username],
                avatar = row[UsersTable.profilePictureUrl],
                content = row[CommentsTable.content],
                parentId = row[CommentsTable.parentCommentId]?.value,
                likeCount = row[CommentsTable.likeCount],
                dislikeCount = row[CommentsTable.dislikeCount],
                isLiked = CommentLikesTable
                    .selectAll()
                    .where { (CommentLikesTable.userId eq userId) and (CommentLikesTable.commentId eq commentId) }
                    .count() > 0,
                isDisliked = CommentDislikesTable
                    .selectAll()
                    .where { (CommentDislikesTable.userId eq userId) and (CommentDislikesTable.commentId eq commentId) }
                    .count() > 0,
                replyCount = row[CommentsTable.replyCount],
                createdAt = row[CommentsTable.createdAt].toString()
            )
        } else {
            null
        }
    }

    suspend fun deleteComment(userId: Long, commentId: Long): Boolean = dbQuery {
        val commentRow = CommentsTable.selectAll().where {
            (CommentsTable.id eq commentId) and
                (CommentsTable.userId eq userId) and
                CommentsTable.deletedAt.isNull()
        }.singleOrNull()
        
        if (commentRow != null) {
            val postId = commentRow[CommentsTable.postId].value
            val parentId = commentRow[CommentsTable.parentCommentId]?.value
            val subtreeIds = CommentTreeVisibility.visibleSubtreeIds(postId, commentId)
            val now = Instant.now()

            CommentsTable.update({ CommentsTable.id inList subtreeIds.toList() }) {
                it[CommentsTable.deletedAt] = now
                it[CommentsTable.updatedAt] = now
            }

            if (parentId != null) {
                syncReplyCount(parentId)
            }
            syncPostCommentCount(postId)

            true
        } else {
            false
        }
    }

    suspend fun toggleCommentLike(userId: Long, commentId: Long): CommentReactionResponse = dbQuery {
        val existingLike = CommentLikesTable.selectAll().where { (CommentLikesTable.userId eq userId) and (CommentLikesTable.commentId eq commentId) }.singleOrNull()

        if (existingLike != null) {
            CommentLikesTable.deleteWhere { (CommentLikesTable.userId eq userId) and (CommentLikesTable.commentId eq commentId) }
        } else {
            CommentDislikesTable.deleteWhere { (CommentDislikesTable.userId eq userId) and (CommentDislikesTable.commentId eq commentId) }
            CommentLikesTable.insert {
                it[CommentLikesTable.userId] = EntityID(userId, UsersTable)
                it[CommentLikesTable.commentId] = EntityID(commentId, CommentsTable)
            }
        }

        syncCommentReactionCounts(commentId)
        commentReactionFor(userId, commentId)
    }

    suspend fun toggleCommentDislike(userId: Long, commentId: Long): CommentReactionResponse = dbQuery {
        val existingDislike = CommentDislikesTable.selectAll().where { (CommentDislikesTable.userId eq userId) and (CommentDislikesTable.commentId eq commentId) }.singleOrNull()

        if (existingDislike != null) {
            CommentDislikesTable.deleteWhere { (CommentDislikesTable.userId eq userId) and (CommentDislikesTable.commentId eq commentId) }
        } else {
            CommentLikesTable.deleteWhere { (CommentLikesTable.userId eq userId) and (CommentLikesTable.commentId eq commentId) }
            CommentDislikesTable.insert {
                it[CommentDislikesTable.userId] = EntityID(userId, UsersTable)
                it[CommentDislikesTable.commentId] = EntityID(commentId, CommentsTable)
            }
        }

        syncCommentReactionCounts(commentId)
        commentReactionFor(userId, commentId)
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

    // --- FR-22: SAVED POSTS ---
    suspend fun getSavedPosts(userId: Long, page: Int, limit: Int): PaginatedFeedResponse = dbQuery {
        val offset = ((page - 1) * limit).toLong()
        val query = SavedPostsTable
            .join(PostsTable, JoinType.INNER, SavedPostsTable.postId, PostsTable.id)
            .join(UsersTable, JoinType.INNER, PostsTable.userId, UsersTable.id)
            .selectAll()
            .where {
                (SavedPostsTable.userId eq userId) and
                    PostsTable.deletedAt.isNull()
            }

        val totalRecords = query.count()
        val totalPages = Math.ceil(totalRecords.toDouble() / limit).toInt()
        val rows = query
            .orderBy(SavedPostsTable.savedAt to SortOrder.DESC)
            .limit(limit, offset)
            .toList()
        val postIds = rows.map { row -> row[PostsTable.id].value }
        val likedPostIds = likedPostIdsFor(userId, postIds)

        PaginatedFeedResponse(
            posts = rows.map { row ->
                row.toFeedPostDto(
                    isLiked = row[PostsTable.id].value in likedPostIds,
                    isSaved = true,
                )
            },
            meta = PaginationMeta(
                currentPage = page,
                totalPages = totalPages,
                hasNext = page < totalPages,
            ),
        )
    }

    // --- FR-20: LIKED POSTS ---
    suspend fun getLikedPosts(userId: Long, page: Int, limit: Int): PaginatedFeedResponse = dbQuery {
        val offset = ((page - 1) * limit).toLong()
        val query = LikesTable
            .join(PostsTable, JoinType.INNER, LikesTable.postId, PostsTable.id)
            .join(UsersTable, JoinType.INNER, PostsTable.userId, UsersTable.id)
            .selectAll()
            .where {
                (LikesTable.userId eq userId) and
                    PostsTable.deletedAt.isNull()
            }

        val totalRecords = query.count()
        val totalPages = Math.ceil(totalRecords.toDouble() / limit).toInt()
        val rows = query
            .orderBy(LikesTable.createdAt to SortOrder.DESC)
            .limit(limit, offset)
            .toList()
        val postIds = rows.map { row -> row[PostsTable.id].value }
        val savedPostIds = savedPostIdsFor(userId, postIds)

        PaginatedFeedResponse(
            posts = rows.map { row ->
                row.toFeedPostDto(
                    isLiked = true,
                    isSaved = row[PostsTable.id].value in savedPostIds,
                )
            },
            meta = PaginationMeta(
                currentPage = page,
                totalPages = totalPages,
                hasNext = page < totalPages,
            ),
        )
    }

    // --- FR-19: TAGGED POSTS ---
    suspend fun getTaggedPosts(userId: Long, page: Int, limit: Int): Any = dbQuery {
        // TODO: Query PostTaggedUsersTable WHERE taggedUserId JOIN PostsTable
        mapOf("posts" to emptyList<Any>(), "meta" to mapOf("currentPage" to page, "totalPages" to 0))
    }

    suspend fun getUserComments(userId: Long, page: Int, limit: Int): PaginatedUserCommentsResponse = dbQuery {
        val offset = ((page - 1) * limit).toLong()
        val query = CommentsTable
            .join(PostsTable, JoinType.INNER, CommentsTable.postId, PostsTable.id)
            .join(UsersTable, JoinType.INNER, PostsTable.userId, UsersTable.id)
            .selectAll()
            .where {
                (CommentsTable.userId eq userId) and
                    CommentsTable.deletedAt.isNull() and
                    PostsTable.deletedAt.isNull()
            }

        val totalRecords = query.count()
        val totalPages = Math.ceil(totalRecords.toDouble() / limit).toInt()
        val rows = query
            .orderBy(CommentsTable.createdAt to SortOrder.DESC)
            .limit(limit, offset)
            .toList()
        val thumbnailByPostId = firstMediaByPostId(rows.map { row -> row[PostsTable.id].value })

        PaginatedUserCommentsResponse(
            comments = rows.map { row ->
                val postId = row[PostsTable.id].value
                UserCommentActivityDto(
                    commentId = row[CommentsTable.id].value,
                    postId = postId,
                    postAuthorUsername = row[UsersTable.username],
                    postAuthorAvatar = row[UsersTable.profilePictureUrl],
                    postCaption = row[PostsTable.caption],
                    postThumbnailUrl = thumbnailByPostId[postId],
                    content = row[CommentsTable.content],
                    createdAt = row[CommentsTable.createdAt].toString(),
                )
            },
            meta = PaginationMeta(
                currentPage = page,
                totalPages = totalPages,
                hasNext = page < totalPages,
            ),
        )
    }

    // --- FR-28: ACTIVITY LOG ---
    suspend fun getActivityLog(userId: Long, page: Int, limit: Int): Any = dbQuery {
        // TODO: Query ActivityLogsTable WHERE actorId = userId ORDER BY createdAt DESC
        mapOf("activities" to emptyList<Any>(), "meta" to mapOf("currentPage" to page, "totalPages" to 0))
    }

    // --- FR-31: BLOCKED USERS ---
    suspend fun getBlockedUsers(userId: Long): Any = dbQuery {
        // TODO: Query BlockedUsersTable WHERE blockerId = userId JOIN UsersTable
        listOf<Any>()
    }

    // --- FR-20: WHO LIKED A POST ---
    suspend fun getPostLikes(postId: Long, page: Int, limit: Int): PostLikesResponse = dbQuery {
        val offset = ((page - 1) * limit).toLong()
        val query = LikesTable
            .join(UsersTable, JoinType.INNER, LikesTable.userId, UsersTable.id)
            .selectAll()
            .where { LikesTable.postId eq postId }

        val totalRecords = query.count()
        val totalPages = Math.ceil(totalRecords.toDouble() / limit).toInt()

        val users = query
            .orderBy(LikesTable.createdAt to SortOrder.DESC)
            .limit(limit, offset)
            .map { row ->
                PostLikeUserDto(
                    userId = row[UsersTable.id].value,
                    username = row[UsersTable.username],
                    fullName = row[UsersTable.fullName],
                    avatar = row[UsersTable.profilePictureUrl]
                )
            }

        PostLikesResponse(
            users = users,
            meta = PaginationMeta(
                currentPage = page,
                totalPages = totalPages,
                hasNext = page < totalPages
            )
        )
    }

    // --- FR-27: INCREMENT SHARE COUNT ---
    suspend fun incrementShareCount(userId: Long, postId: Long): Long = dbQuery {
        PostsTable.update({ PostsTable.id eq postId }) {
            with(SqlExpressionBuilder) {
                it.update(PostsTable.shareCount, PostsTable.shareCount + 1)
            }
        }
        PostsTable.selectAll().where { PostsTable.id eq postId }
            .first()[PostsTable.shareCount].toLong()
    }

    // --- FR-27: GET SHARE COUNT ---
    suspend fun getShareCount(postId: Long): Long = dbQuery {
        PostsTable.selectAll().where { PostsTable.id eq postId }
            .firstOrNull()?.get(PostsTable.shareCount)?.toLong() ?: 0L
    }

    private fun syncReplyCount(commentId: Long) {
        val visibleReplyCount = CommentsTable
            .selectAll()
            .where {
                (CommentsTable.parentCommentId eq commentId) and
                    CommentsTable.deletedAt.isNull()
            }
            .count()
            .toInt()

        CommentsTable.update({ CommentsTable.id eq commentId }) {
            it[CommentsTable.replyCount] = visibleReplyCount
            it[CommentsTable.updatedAt] = Instant.now()
        }
    }

    private fun syncPostCommentCount(postId: Long) {
        val visibleCommentCount = CommentTreeVisibility.visibleCommentCount(postId)
        PostsTable.update({ PostsTable.id eq postId }) {
            it[PostsTable.commentCount] = visibleCommentCount
        }
    }

    private fun syncCommentReactionCounts(commentId: Long) {
        val likeTotal = CommentLikesTable
            .selectAll()
            .where { CommentLikesTable.commentId eq commentId }
            .count()
            .toInt()
        val dislikeTotal = CommentDislikesTable
            .selectAll()
            .where { CommentDislikesTable.commentId eq commentId }
            .count()
            .toInt()

        CommentsTable.update({ CommentsTable.id eq commentId }) {
            it[likeCount] = likeTotal
            it[dislikeCount] = dislikeTotal
            it[updatedAt] = Instant.now()
        }
    }

    private fun commentReactionFor(userId: Long, commentId: Long): CommentReactionResponse {
        val commentRow = CommentsTable
            .select(CommentsTable.likeCount, CommentsTable.dislikeCount)
            .where { CommentsTable.id eq commentId }
            .single()

        return CommentReactionResponse(
            isLiked = CommentLikesTable
                .selectAll()
                .where { (CommentLikesTable.userId eq userId) and (CommentLikesTable.commentId eq commentId) }
                .count() > 0,
            isDisliked = CommentDislikesTable
                .selectAll()
                .where { (CommentDislikesTable.userId eq userId) and (CommentDislikesTable.commentId eq commentId) }
                .count() > 0,
            likeCount = commentRow[CommentsTable.likeCount],
            dislikeCount = commentRow[CommentsTable.dislikeCount],
        )
    }

    private fun ResultRow.toFeedPostDto(
        isLiked: Boolean,
        isSaved: Boolean,
    ): FeedPostDto {
        val postId = this[PostsTable.id].value
        return FeedPostDto(
            postId = postId,
            userId = this[UsersTable.id].value,
            username = this[UsersTable.username],
            userAvatar = this[UsersTable.profilePictureUrl],
            caption = this[PostsTable.caption],
            location = this[PostsTable.location],
            likeCount = this[PostsTable.likeCount],
            commentCount = CommentTreeVisibility.visibleCommentCount(postId),
            createdAt = this[PostsTable.createdAt].toString(),
            media = mediaForPost(postId),
            isLiked = isLiked,
            isSaved = isSaved,
        )
    }

    private fun mediaForPost(postId: Long): List<FeedMediaDto> {
        return PostMediaTable
            .selectAll()
            .where { PostMediaTable.postId eq postId }
            .orderBy(PostMediaTable.position to SortOrder.ASC)
            .map { row ->
                FeedMediaDto(
                    id = row[PostMediaTable.id].value,
                    url = row[PostMediaTable.mediaFileUrl],
                    type = row[PostMediaTable.mediaType].name,
                    orderIndex = row[PostMediaTable.position],
                    width = row[PostMediaTable.width],
                    height = row[PostMediaTable.height],
                )
            }
    }

    private fun firstMediaByPostId(postIds: List<Long>): Map<Long, String> {
        if (postIds.isEmpty()) return emptyMap()
        return PostMediaTable
            .selectAll()
            .where { PostMediaTable.postId inList postIds.distinct() }
            .orderBy(PostMediaTable.position to SortOrder.ASC)
            .groupBy { row -> row[PostMediaTable.postId].value }
            .mapValues { (_, rows) -> rows.first()[PostMediaTable.mediaFileUrl] }
    }

    private fun likedPostIdsFor(userId: Long, postIds: List<Long>): Set<Long> {
        if (postIds.isEmpty()) return emptySet()
        return LikesTable
            .selectAll()
            .where { (LikesTable.userId eq userId) and (LikesTable.postId inList postIds) }
            .map { row -> row[LikesTable.postId].value }
            .toSet()
    }

    private fun likedCommentIdsFor(userId: Long, commentIds: List<Long>): Set<Long> {
        if (commentIds.isEmpty()) return emptySet()
        return CommentLikesTable
            .selectAll()
            .where { (CommentLikesTable.userId eq userId) and (CommentLikesTable.commentId inList commentIds) }
            .map { row -> row[CommentLikesTable.commentId].value }
            .toSet()
    }

    private fun dislikedCommentIdsFor(userId: Long, commentIds: List<Long>): Set<Long> {
        if (commentIds.isEmpty()) return emptySet()
        return CommentDislikesTable
            .selectAll()
            .where { (CommentDislikesTable.userId eq userId) and (CommentDislikesTable.commentId inList commentIds) }
            .map { row -> row[CommentDislikesTable.commentId].value }
            .toSet()
    }

    private fun savedPostIdsFor(userId: Long, postIds: List<Long>): Set<Long> {
        if (postIds.isEmpty()) return emptySet()
        return SavedPostsTable
            .selectAll()
            .where { (SavedPostsTable.userId eq userId) and (SavedPostsTable.postId inList postIds) }
            .map { row -> row[SavedPostsTable.postId].value }
            .toSet()
    }
}
