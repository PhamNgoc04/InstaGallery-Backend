package com.instagallery.repositories

import com.instagallery.database.DatabaseFactory.dbQuery
import com.instagallery.database.tables.*
import com.instagallery.models.common.FeedMediaDto
import com.instagallery.models.common.FeedPostDto
import com.instagallery.models.common.CommentReactionKind
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
import org.jetbrains.exposed.sql.or
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
                isLiked = commentReactionKind(userId, commentId) == CommentReactionKind.LIKE,
                isDisliked = commentReactionKind(userId, commentId) == CommentReactionKind.DISLIKE,
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
        toggleCommentReaction(userId, commentId, CommentReactionKind.LIKE)
        syncCommentReactionCounts(commentId)
        commentReactionFor(userId, commentId)
    }

    suspend fun toggleCommentDislike(userId: Long, commentId: Long): CommentReactionResponse = dbQuery {
        toggleCommentReaction(userId, commentId, CommentReactionKind.DISLIKE)
        syncCommentReactionCounts(commentId)
        commentReactionFor(userId, commentId)
    }

    suspend fun isFollowing(followerId: Long, followingId: Long): Boolean = dbQuery {
        FollowersTable
            .selectAll()
            .where { (FollowersTable.followerId eq followerId) and (FollowersTable.followingId eq followingId) }
            .count() > 0
    }

    suspend fun follow(followerId: Long, followingId: Long): Boolean = dbQuery {
        val existing = FollowersTable
            .selectAll()
            .where { (FollowersTable.followerId eq followerId) and (FollowersTable.followingId eq followingId) }
            .count() > 0
        if (existing) return@dbQuery false
        FollowersTable.insert {
            it[FollowersTable.followerId] = EntityID(followerId, UsersTable)
            it[FollowersTable.followingId] = EntityID(followingId, UsersTable)
        }
        adjustFollowCounts(followerId, followingId, 1)
        cancelFollowRequest(followerId, followingId)
        true
    }

    suspend fun unfollow(followerId: Long, followingId: Long): Boolean = dbQuery {
        val deleted = FollowersTable.deleteWhere {
            (FollowersTable.followerId eq followerId) and (FollowersTable.followingId eq followingId)
        }
        if (deleted > 0) {
            adjustFollowCounts(followerId, followingId, -1)
        }
        cancelFollowRequest(followerId, followingId)
        deleted > 0
    }

    suspend fun hasPendingFollowRequest(followerId: Long, followingId: Long): Boolean = dbQuery {
        FollowRequestsTable
            .selectAll()
            .where {
                (FollowRequestsTable.followerId eq followerId) and
                    (FollowRequestsTable.followingId eq followingId) and
                    (FollowRequestsTable.status eq com.instagallery.models.common.FollowRequestStatus.PENDING)
            }
            .count() > 0
    }

    suspend fun createFollowRequest(followerId: Long, followingId: Long) = dbQuery {
        val existing = FollowRequestsTable
            .selectAll()
            .where { (FollowRequestsTable.followerId eq followerId) and (FollowRequestsTable.followingId eq followingId) }
            .singleOrNull()
        if (existing == null) {
            FollowRequestsTable.insert {
                it[FollowRequestsTable.followerId] = EntityID(followerId, UsersTable)
                it[FollowRequestsTable.followingId] = EntityID(followingId, UsersTable)
                it[status] = com.instagallery.models.common.FollowRequestStatus.PENDING
            }
        } else {
            FollowRequestsTable.update({
                (FollowRequestsTable.followerId eq followerId) and (FollowRequestsTable.followingId eq followingId)
            }) {
                it[status] = com.instagallery.models.common.FollowRequestStatus.PENDING
                it[updatedAt] = java.time.Instant.now()
            }
        }
    }

    private fun cancelFollowRequest(followerId: Long, followingId: Long) {
        FollowRequestsTable.deleteWhere {
            (FollowRequestsTable.followerId eq followerId) and (FollowRequestsTable.followingId eq followingId)
        }
    }

    suspend fun listIncomingFollowRequests(userId: Long): List<com.instagallery.models.common.FollowRequestDto> = dbQuery {
        FollowRequestsTable
            .join(UsersTable, JoinType.INNER, FollowRequestsTable.followerId, UsersTable.id)
            .selectAll()
            .where {
                (FollowRequestsTable.followingId eq userId) and
                    (FollowRequestsTable.status eq com.instagallery.models.common.FollowRequestStatus.PENDING)
            }
            .orderBy(FollowRequestsTable.createdAt to SortOrder.DESC)
            .map { row ->
                com.instagallery.models.common.FollowRequestDto(
                    requestId = row[FollowRequestsTable.id].value,
                    followerId = row[UsersTable.id].value,
                    username = row[UsersTable.username],
                    fullName = row[UsersTable.fullName],
                    avatar = row[UsersTable.profilePictureUrl],
                    createdAt = row[FollowRequestsTable.createdAt].toString(),
                )
            }
    }

    suspend fun respondFollowRequest(ownerId: Long, followerId: Long, accept: Boolean): Boolean = dbQuery {
        val request = FollowRequestsTable
            .selectAll()
            .where {
                (FollowRequestsTable.followingId eq ownerId) and
                    (FollowRequestsTable.followerId eq followerId) and
                    (FollowRequestsTable.status eq com.instagallery.models.common.FollowRequestStatus.PENDING)
            }
            .singleOrNull() ?: return@dbQuery false

        val newStatus = if (accept) {
            com.instagallery.models.common.FollowRequestStatus.ACCEPTED
        } else {
            com.instagallery.models.common.FollowRequestStatus.REJECTED
        }
        FollowRequestsTable.update({ FollowRequestsTable.id eq request[FollowRequestsTable.id] }) {
            it[status] = newStatus
            it[updatedAt] = java.time.Instant.now()
        }
        if (accept) {
            val already = FollowersTable
                .selectAll()
                .where { (FollowersTable.followerId eq followerId) and (FollowersTable.followingId eq ownerId) }
                .count() > 0
            if (!already) {
                FollowersTable.insert {
                    it[FollowersTable.followerId] = EntityID(followerId, UsersTable)
                    it[FollowersTable.followingId] = EntityID(ownerId, UsersTable)
                }
                adjustFollowCounts(followerId, ownerId, 1)
            }
        }
        true
    }

    suspend fun isBlockedEitherWay(userA: Long, userB: Long): Boolean = dbQuery {
        BlockedUsersTable
            .selectAll()
            .where {
                ((BlockedUsersTable.blockerId eq userA) and (BlockedUsersTable.blockedId eq userB)) or
                    ((BlockedUsersTable.blockerId eq userB) and (BlockedUsersTable.blockedId eq userA))
            }
            .count() > 0
    }

    suspend fun toggleBlock(blockerId: Long, blockedId: Long): Boolean = dbQuery {
        val existing = BlockedUsersTable
            .selectAll()
            .where { (BlockedUsersTable.blockerId eq blockerId) and (BlockedUsersTable.blockedId eq blockedId) }
            .singleOrNull()
        if (existing != null) {
            BlockedUsersTable.deleteWhere {
                (BlockedUsersTable.blockerId eq blockerId) and (BlockedUsersTable.blockedId eq blockedId)
            }
            false
        } else {
            BlockedUsersTable.insert {
                it[BlockedUsersTable.blockerId] = EntityID(blockerId, UsersTable)
                it[BlockedUsersTable.blockedId] = EntityID(blockedId, UsersTable)
            }
            removeFollowRelation(blockerId, blockedId)
            removeFollowRelation(blockedId, blockerId)
            FollowRequestsTable.deleteWhere {
                ((FollowRequestsTable.followerId eq blockerId) and (FollowRequestsTable.followingId eq blockedId)) or
                    ((FollowRequestsTable.followerId eq blockedId) and (FollowRequestsTable.followingId eq blockerId))
            }
            true
        }
    }

    suspend fun toggleMute(muterId: Long, mutedId: Long): Boolean = dbQuery {
        val existing = MutedUsersTable
            .selectAll()
            .where { (MutedUsersTable.muterId eq muterId) and (MutedUsersTable.mutedId eq mutedId) }
            .singleOrNull()
        if (existing != null) {
            MutedUsersTable.deleteWhere {
                (MutedUsersTable.muterId eq muterId) and (MutedUsersTable.mutedId eq mutedId)
            }
            false
        } else {
            MutedUsersTable.insert {
                it[MutedUsersTable.muterId] = EntityID(muterId, UsersTable)
                it[MutedUsersTable.mutedId] = EntityID(mutedId, UsersTable)
            }
            true
        }
    }

    private fun removeFollowRelation(followerId: Long, followingId: Long) {
        val deleted = FollowersTable.deleteWhere {
            (FollowersTable.followerId eq followerId) and (FollowersTable.followingId eq followingId)
        }
        if (deleted > 0) {
            adjustFollowCounts(followerId, followingId, -1)
        }
    }

    private fun adjustFollowCounts(followerId: Long, followingId: Long, delta: Int) {
        UsersTable.update({ UsersTable.id eq followerId }) {
            with(org.jetbrains.exposed.sql.SqlExpressionBuilder) {
                it.update(UsersTable.followingCount, UsersTable.followingCount + delta)
            }
        }
        UsersTable.update({ UsersTable.id eq followingId }) {
            with(org.jetbrains.exposed.sql.SqlExpressionBuilder) {
                it.update(UsersTable.followerCount, UsersTable.followerCount + delta)
            }
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
    suspend fun getTaggedPosts(userId: Long, page: Int, limit: Int): PaginatedFeedResponse = dbQuery {
        val offset = ((page - 1) * limit).toLong()
        val query = PostTaggedUsersTable
            .join(PostsTable, JoinType.INNER, PostTaggedUsersTable.postId, PostsTable.id)
            .join(UsersTable, JoinType.INNER, PostsTable.userId, UsersTable.id)
            .selectAll()
            .where {
                (PostTaggedUsersTable.taggedUserId eq userId) and PostsTable.deletedAt.isNull()
            }

        val totalRecords = query.count()
        val totalPages = Math.ceil(totalRecords.toDouble() / limit).toInt()
        val rows = query
            .orderBy(PostTaggedUsersTable.createdAt to SortOrder.DESC)
            .limit(limit, offset)
            .toList()
        val postIds = rows.map { row -> row[PostsTable.id].value }
        val likedPostIds = likedPostIdsFor(userId, postIds)
        val savedPostIds = savedPostIdsFor(userId, postIds)

        PaginatedFeedResponse(
            posts = rows.map { row ->
                val postId = row[PostsTable.id].value
                row.toFeedPostDto(
                    isLiked = postId in likedPostIds,
                    isSaved = postId in savedPostIds,
                )
            },
            meta = PaginationMeta(
                currentPage = page,
                totalPages = totalPages,
                hasNext = page < totalPages,
            ),
        )
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

    suspend fun getBlockedUsers(userId: Long): List<com.instagallery.models.common.FollowDto> = dbQuery {
        BlockedUsersTable
            .join(UsersTable, JoinType.INNER, BlockedUsersTable.blockedId, UsersTable.id)
            .selectAll()
            .where { BlockedUsersTable.blockerId eq userId }
            .orderBy(BlockedUsersTable.createdAt to SortOrder.DESC)
            .map { row ->
                com.instagallery.models.common.FollowDto(
                    id = row[UsersTable.id].value,
                    username = row[UsersTable.username],
                    fullName = row[UsersTable.fullName],
                    avatar = row[UsersTable.profilePictureUrl],
                    role = row[UsersTable.role],
                    userType = row[UsersTable.userType],
                )
            }
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
        val alreadyShared = PostSharesTable.selectAll().where {
            (PostSharesTable.userId eq userId) and (PostSharesTable.postId eq postId)
        }.count() > 0
        if (!alreadyShared) {
            PostSharesTable.insert {
                it[PostSharesTable.userId] = userId
                it[PostSharesTable.postId] = postId
            }
        }
        val shareTotal = PostSharesTable.selectAll().where { PostSharesTable.postId eq postId }.count().toInt()
        PostsTable.update({ PostsTable.id eq postId }) {
            it[shareCount] = shareTotal
        }
        shareTotal.toLong()
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

    private fun toggleCommentReaction(userId: Long, commentId: Long, kind: CommentReactionKind) {
        val existing = commentReactionKind(userId, commentId)
        if (existing == kind) {
            CommentReactionsTable.deleteWhere {
                (CommentReactionsTable.userId eq userId) and (CommentReactionsTable.commentId eq commentId)
            }
            return
        }
        if (existing == null) {
            CommentReactionsTable.insert {
                it[CommentReactionsTable.userId] = userId
                it[CommentReactionsTable.commentId] = commentId
                it[reaction] = kind
            }
        } else {
            CommentReactionsTable.update({
                (CommentReactionsTable.userId eq userId) and (CommentReactionsTable.commentId eq commentId)
            }) {
                it[reaction] = kind
            }
        }
    }

    private fun commentReactionKind(userId: Long, commentId: Long): CommentReactionKind? {
        return CommentReactionsTable
            .selectAll()
            .where { (CommentReactionsTable.userId eq userId) and (CommentReactionsTable.commentId eq commentId) }
            .singleOrNull()
            ?.get(CommentReactionsTable.reaction)
    }

    private fun syncCommentReactionCounts(commentId: Long) {
        val likeTotal = CommentReactionsTable
            .selectAll()
            .where {
                (CommentReactionsTable.commentId eq commentId) and
                    (CommentReactionsTable.reaction eq CommentReactionKind.LIKE)
            }
            .count()
            .toInt()
        val dislikeTotal = CommentReactionsTable
            .selectAll()
            .where {
                (CommentReactionsTable.commentId eq commentId) and
                    (CommentReactionsTable.reaction eq CommentReactionKind.DISLIKE)
            }
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
            isLiked = commentReactionKind(userId, commentId) == CommentReactionKind.LIKE,
            isDisliked = commentReactionKind(userId, commentId) == CommentReactionKind.DISLIKE,
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
        return CommentReactionsTable
            .selectAll()
            .where {
                (CommentReactionsTable.userId eq userId) and
                    (CommentReactionsTable.commentId inList commentIds) and
                    (CommentReactionsTable.reaction eq CommentReactionKind.LIKE)
            }
            .map { row -> row[CommentReactionsTable.commentId].value }
            .toSet()
    }

    private fun dislikedCommentIdsFor(userId: Long, commentIds: List<Long>): Set<Long> {
        if (commentIds.isEmpty()) return emptySet()
        return CommentReactionsTable
            .selectAll()
            .where {
                (CommentReactionsTable.userId eq userId) and
                    (CommentReactionsTable.commentId inList commentIds) and
                    (CommentReactionsTable.reaction eq CommentReactionKind.DISLIKE)
            }
            .map { row -> row[CommentReactionsTable.commentId].value }
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
