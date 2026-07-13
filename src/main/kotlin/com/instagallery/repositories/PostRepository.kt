package com.instagallery.repositories

import com.instagallery.database.DatabaseFactory.dbQuery
import com.instagallery.database.tables.*
import com.instagallery.models.common.*
import com.instagallery.models.request.CreatePostRequest
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull
import org.jetbrains.exposed.sql.SqlExpressionBuilder.notInList
import org.jetbrains.exposed.dao.id.EntityID
import java.time.Instant

class PostRepository {

    suspend fun getFollowerIds(userId: Long): List<Long> = dbQuery {
        FollowersTable
            .select(FollowersTable.followerId)
            .where { FollowersTable.followingId eq userId }
            .map { it[FollowersTable.followerId].value }
    }

    suspend fun createPost(userId: Long, request: CreatePostRequest): PostDto? = dbQuery {
        // 1. Insert the Post record
        val insertStatement = PostsTable.insertAndGetId {
            it[PostsTable.userId] = EntityID(userId, UsersTable)
            it[caption] = request.caption
            it[location] = request.location
            it[visibility] = request.visibility
            // likeCount and commentCount are defaulted to 0 by schema
        }
        val newPostId = insertStatement.value

        // 2. Insert Media.
        var order = 1
        val updatedMediaList = mutableListOf<FeedMediaDto>()
        val firstMediaIdList = mutableListOf<Long>()
        
        request.media.forEach { mediaItem ->
            val insertedMediaId = PostMediaTable.insertAndGetId {
                it[postId] = newPostId
                it[mediaFileUrl] = mediaItem.mediaFileUrl
                it[thumbnailUrl] = mediaItem.thumbnailUrl
                it[mediaType] = mediaItem.mediaType
                it[position] = order
                it[width] = mediaItem.width
                it[height] = mediaItem.height
                it[duration] = mediaItem.duration
            }.value

            if (order == 1) {
                firstMediaIdList.add(insertedMediaId)
            }
            
            updatedMediaList.add(
                FeedMediaDto(
                    id = insertedMediaId,
                    url = mediaItem.mediaFileUrl,
                    type = mediaItem.mediaType.name,
                    orderIndex = order,
                    width = mediaItem.width,
                    height = mediaItem.height
                )
            )
            order++
        }

        // 3. Process Tags (Hashtags)
        request.tags?.forEach { tagName ->
            // Insert tag if not exists, safely handled with insertIgnore in raw, 
            // but in Exposed we can check or use UPSERT logic.
            val existingTag = MediaTagsTable.selectAll().where { MediaTagsTable.name eq tagName }.singleOrNull()
            val tagId = existingTag?.get(MediaTagsTable.id)?.value 
                ?: MediaTagsTable.insertAndGetId { it[MediaTagsTable.name] = tagName }.value
                
            // Link Tag to Post 
            if (firstMediaIdList.isNotEmpty()) {
                val firstMediaId = firstMediaIdList.first()
                PostMediaTagsTable.insertIgnore { // custom or try catch
                    it[mediaId] = firstMediaId
                    it[PostMediaTagsTable.tagId] = tagId
                }
            }
        }

        // Return the constructed PostDto
        val postRow = PostsTable.selectAll().where { PostsTable.id eq newPostId }.singleOrNull()
        postRow?.let {
            PostDto(
                postId = newPostId,
                userId = userId,
                caption = it[PostsTable.caption],
                location = it[PostsTable.location],
                visibility = it[PostsTable.visibility],
                likeCount = it[PostsTable.likeCount],
                commentCount = it[PostsTable.commentCount],
                createdAt = it[PostsTable.createdAt].toString(),
                media = updatedMediaList.map { m -> m as FeedMediaDto }
            )
        }
    }

    suspend fun updatePost(postId: Long, userId: Long, request: com.instagallery.models.request.UpdatePostRequest): Boolean = dbQuery {
        val updatedCount = PostsTable.update({ (PostsTable.id eq postId) and (PostsTable.userId eq userId) and (PostsTable.deletedAt.isNull()) }) {
            request.caption?.let { cap -> it[caption] = cap }
            request.location?.let { loc -> it[location] = loc }
            request.visibility?.let { vis -> it[visibility] = vis }
        }

        if (updatedCount > 0 && request.tags != null) {
            // Re-process tags
            val existingMediaId = PostMediaTable.selectAll().where { PostMediaTable.postId eq postId }.firstOrNull()?.get(PostMediaTable.id)?.value
            
            if (existingMediaId != null) {
                // Remove old tags
                PostMediaTagsTable.deleteWhere { PostMediaTagsTable.mediaId eq existingMediaId }
                
                // Add new tags
                request.tags.forEach { tagName ->
                    val existingTag = MediaTagsTable.selectAll().where { MediaTagsTable.name eq tagName }.singleOrNull()
                    val tagId = existingTag?.get(MediaTagsTable.id)?.value 
                        ?: MediaTagsTable.insertAndGetId { it[MediaTagsTable.name] = tagName }.value
                        
                    PostMediaTagsTable.insertIgnore {
                        it[mediaId] = existingMediaId
                        it[PostMediaTagsTable.tagId] = tagId
                    }
                }
            }
        }
        updatedCount > 0
    }

    suspend fun getPostById(postId: Long): FeedPostDto? = dbQuery {
        val row = (PostsTable innerJoin UsersTable)
            .selectAll()
            .where { (PostsTable.id eq postId) and (PostsTable.deletedAt.isNull()) }
            .singleOrNull() ?: return@dbQuery null

        val mediaRows = PostMediaTable.selectAll().where { PostMediaTable.postId eq postId }.orderBy(PostMediaTable.position to SortOrder.ASC).toList()
        val mediaList = mediaRows.map { mRow ->
            FeedMediaDto(
                id = mRow[PostMediaTable.id].value,
                url = mRow[PostMediaTable.mediaFileUrl],
                type = mRow[PostMediaTable.mediaType].name,
                orderIndex = mRow[PostMediaTable.position],
                width = mRow[PostMediaTable.width],
                height = mRow[PostMediaTable.height]
            )
        }
        val visibleCommentCount = CommentTreeVisibility.visibleCommentCount(postId)

        FeedPostDto(
            postId = postId,
            userId = row[UsersTable.id].value,
            username = row[UsersTable.username],
            userAvatar = row[UsersTable.profilePictureUrl],
            caption = row[PostsTable.caption],
            location = row[PostsTable.location],
            likeCount = row[PostsTable.likeCount],
            commentCount = visibleCommentCount,
            createdAt = row[PostsTable.createdAt].toString(),
            media = mediaList
        )
    }

    suspend fun logicSoftDeletePost(postId: Long, userId: Long): Boolean = dbQuery {
        val count = PostsTable.update({ (PostsTable.id eq postId) and (PostsTable.userId eq userId) }) {
            it[deletedAt] = Instant.now()
        }
        count > 0
    }
    
    // --- POST DETAIL ---
    sealed class PostDetailResult {
        data class Success(val post: PostDetailDto) : PostDetailResult()
        object NotFound : PostDetailResult()
        object Forbidden : PostDetailResult()
    }

    suspend fun getPostDetail(postId: Long, currentUserId: Long): PostDetailResult = dbQuery {
        // 1. Query post JOIN user
        val postRow = (PostsTable innerJoin UsersTable)
            .selectAll().where { PostsTable.id eq postId }
            .singleOrNull()

        // 2. Check existence & soft-delete
        if (postRow == null || postRow[PostsTable.deletedAt] != null) {
            return@dbQuery PostDetailResult.NotFound
        }

        // 3. Check visibility
        val postOwnerId = postRow[UsersTable.id].value
        val visibility = postRow[PostsTable.visibility]
        if (visibility == PostVisibility.PRIVATE && currentUserId != postOwnerId) {
            return@dbQuery PostDetailResult.Forbidden
        }

        // 4. Get media
        val mediaList = PostMediaTable.selectAll()
            .where { PostMediaTable.postId eq postId }
            .orderBy(PostMediaTable.position to SortOrder.ASC)
            .map { mRow ->
                FeedMediaDto(
                    id = mRow[PostMediaTable.id].value,
                    url = mRow[PostMediaTable.mediaFileUrl],
                    type = mRow[PostMediaTable.mediaType].name,
                    orderIndex = mRow[PostMediaTable.position],
                    width = mRow[PostMediaTable.width],
                    height = mRow[PostMediaTable.height]
                )
            }

        // 5. Check isLiked
        val isLiked = LikesTable.selectAll()
            .where { (LikesTable.userId eq currentUserId) and (LikesTable.postId eq postId) }
            .count() > 0

        // 6. Check isSaved
        val isSaved = SavedPostsTable.selectAll()
            .where { (SavedPostsTable.userId eq currentUserId) and (SavedPostsTable.postId eq postId) }
            .count() > 0
        val visibleCommentCount = CommentTreeVisibility.visibleCommentCount(postId)

        // 7. Map to DTO
        PostDetailResult.Success(
            PostDetailDto(
                postId = postRow[PostsTable.id].value,
                userId = postOwnerId,
                username = postRow[UsersTable.username],
                userAvatar = postRow[UsersTable.profilePictureUrl],
                caption = postRow[PostsTable.caption],
                location = postRow[PostsTable.location],
                visibility = visibility,
                likeCount = postRow[PostsTable.likeCount],
                commentCount = visibleCommentCount,
                createdAt = postRow[PostsTable.createdAt].toString(),
                media = mediaList,
                isLiked = isLiked,
                isSaved = isSaved
            )
        )
    }

    // Helper function for raw pagination
    suspend fun getFeedPosts(userId: Long, page: Int, limit: Int): PaginatedFeedResponse = dbQuery {
        val offsetVal = ((page - 1) * limit).toLong()

        // 1. Get Followings IDs
        val followingIds = FollowersTable
            .selectAll().where { FollowersTable.followerId eq userId }
            .map { it[FollowersTable.followingId].value }

        // 2. Query following-first feed.
        // Own posts are always visible to the owner; followed users contribute public/followers-only posts.
        val ownPostCondition = PostsTable.userId eq userId
        val followedVisibleCondition = (PostsTable.userId inList followingIds) and
            (PostsTable.visibility inList listOf(PostVisibility.PUBLIC, PostVisibility.FRIENDS_ONLY))
        val query = (PostsTable innerJoin UsersTable)
            .selectAll().where {
                PostsTable.deletedAt.isNull() and (ownPostCondition or followedVisibleCondition)
            }
            .orderBy(PostsTable.createdAt to SortOrder.DESC)

        val totalRecords = query.count()
        val totalPages = Math.ceil(totalRecords.toDouble() / limit).toInt()

        val postRows = query.limit(limit, offsetVal).toList()
        val suggestedLimit = if (page == 1) {
            (limit - postRows.size).coerceAtLeast(0)
        } else {
            0
        }
        val excludedUserIds = (followingIds + userId).distinct()
        val suggestedRows = if (suggestedLimit > 0) {
            (PostsTable innerJoin UsersTable)
                .selectAll().where {
                    (PostsTable.userId notInList excludedUserIds) and
                        PostsTable.deletedAt.isNull() and
                        (PostsTable.visibility eq PostVisibility.PUBLIC)
                }
                .orderBy(
                    PostsTable.likeCount to SortOrder.DESC,
                    PostsTable.commentCount to SortOrder.DESC,
                    PostsTable.createdAt to SortOrder.DESC
                )
                .limit(suggestedLimit)
                .toList()
        } else {
            emptyList()
        }

        val allPostIds = (postRows + suggestedRows).map { it[PostsTable.id].value }
        val likedPostIds = if (allPostIds.isEmpty()) {
            emptySet()
        } else {
            LikesTable.selectAll()
                .where { (LikesTable.userId eq userId) and (LikesTable.postId inList allPostIds) }
                .map { it[LikesTable.postId].value }
                .toSet()
        }
        val savedPostIds = if (allPostIds.isEmpty()) {
            emptySet()
        } else {
            SavedPostsTable.selectAll()
                .where { (SavedPostsTable.userId eq userId) and (SavedPostsTable.postId inList allPostIds) }
                .map { it[SavedPostsTable.postId].value }
                .toSet()
        }

        val commentCountsByPost = CommentTreeVisibility.visibleCommentCountsByPost(allPostIds)

        fun List<ResultRow>.toFeedPostDtos(): List<FeedPostDto> {
            return map { row ->
                val pId = row[PostsTable.id].value

                val mediaRows = PostMediaTable.selectAll()
                    .where { PostMediaTable.postId eq pId }
                    .orderBy(PostMediaTable.position to SortOrder.ASC)
                    .toList()
                val mediaList = mediaRows.map { mRow ->
                    FeedMediaDto(
                        id = mRow[PostMediaTable.id].value,
                        url = mRow[PostMediaTable.mediaFileUrl],
                        type = mRow[PostMediaTable.mediaType].name,
                        orderIndex = mRow[PostMediaTable.position],
                        width = mRow[PostMediaTable.width],
                        height = mRow[PostMediaTable.height]
                    )
                }

                FeedPostDto(
                    postId = pId,
                    userId = row[UsersTable.id].value,
                    username = row[UsersTable.username],
                    userAvatar = row[UsersTable.profilePictureUrl],
                    caption = row[PostsTable.caption],
                    location = row[PostsTable.location],
                    likeCount = row[PostsTable.likeCount],
                    isLiked = likedPostIds.contains(pId),
                    isSaved = savedPostIds.contains(pId),
                    commentCount = commentCountsByPost[pId] ?: 0,
                    createdAt = row[PostsTable.createdAt].toString(),
                    media = mediaList
                )
            }
        }

        val posts = postRows.toFeedPostDtos()
        val suggestedPosts = suggestedRows.toFeedPostDtos()
        val reason = when {
            totalRecords == 0L && followingIds.isEmpty() -> "NO_FOLLOWING"
            totalRecords == 0L -> "FOLLOWING_NO_POSTS"
            suggestedPosts.isNotEmpty() -> "BACKFILL_WITH_SUGGESTED"
            else -> "NORMAL"
        }

        PaginatedFeedResponse(
            posts = posts,
            meta = PaginationMeta(
                currentPage = page,
                totalPages = totalPages,
                hasNext = page < totalPages
            ),
            feedContext = FeedContextDto(
                mode = if (suggestedPosts.isNotEmpty()) "FOR_YOU" else "FOLLOWING",
                followCount = followingIds.size,
                reason = reason,
                primaryPostCount = totalRecords.toInt(),
                suggestedPostCount = suggestedPosts.size
            ),
            suggestedPosts = suggestedPosts
        )
    }

    suspend fun getExplorePosts(page: Int, limit: Int, tag: String?): PaginatedFeedResponse = dbQuery {
        val offsetVal = ((page - 1) * limit).toLong()

        var query = (PostsTable innerJoin UsersTable)
            .selectAll().where { (PostsTable.deletedAt.isNull()) and (PostsTable.visibility eq PostVisibility.PUBLIC) }

        if (!tag.isNullOrBlank()) {
            // Join with PostMediaTagsTable and MediaTagsTable
            query = (PostsTable innerJoin UsersTable innerJoin PostMediaTable innerJoin PostMediaTagsTable innerJoin MediaTagsTable)
                .selectAll().where { 
                    (PostsTable.deletedAt.isNull()) and 
                    (PostsTable.visibility eq PostVisibility.PUBLIC) and 
                    (MediaTagsTable.name eq tag)
                }
        }

        // Sort by engagement (likes + comments desc)
        query = query.orderBy(PostsTable.likeCount to SortOrder.DESC, PostsTable.commentCount to SortOrder.DESC)

        val totalRecords = query.count()
        val totalPages = Math.ceil(totalRecords.toDouble() / limit).toInt()

        val postRows = query.limit(limit, offsetVal).toList()

        val commentCountsByPost = CommentTreeVisibility.visibleCommentCountsByPost(
            postRows.map { it[PostsTable.id].value },
        )

        val posts = postRows.map { row ->
            val pId = row[PostsTable.id].value
            
            val mediaRows = PostMediaTable.selectAll().where { PostMediaTable.postId eq pId }.orderBy(PostMediaTable.position to SortOrder.ASC).toList()
            val mediaList = mediaRows.map { mRow ->
                FeedMediaDto(
                    id = mRow[PostMediaTable.id].value,
                    url = mRow[PostMediaTable.mediaFileUrl],
                    type = mRow[PostMediaTable.mediaType].name,
                    orderIndex = mRow[PostMediaTable.position],
                    width = mRow[PostMediaTable.width],
                    height = mRow[PostMediaTable.height]
                )
            }

            FeedPostDto(
                postId = pId,
                userId = row[UsersTable.id].value,
                username = row[UsersTable.username],
                userAvatar = row[UsersTable.profilePictureUrl],
                caption = row[PostsTable.caption],
                location = row[PostsTable.location],
                likeCount = row[PostsTable.likeCount],
                commentCount = commentCountsByPost[pId] ?: 0,
                createdAt = row[PostsTable.createdAt].toString(),
                media = mediaList
            )
        }

        // Deduplicate in memory in case the join produced dupes due to multiple media tags
        val uniquePosts = posts.distinctBy { it.postId }

        PaginatedFeedResponse(
            posts = uniquePosts,
            meta = PaginationMeta(
                currentPage = page,
                totalPages = totalPages,
                hasNext = page < totalPages
            )
        )
    }

    suspend fun getTrendingTags(limit: Int): List<TrendingTagDto> = dbQuery {
        // Find tags ordered by usageCount
        val rows = MediaTagsTable
            .selectAll()
            .orderBy(MediaTagsTable.usageCount to SortOrder.DESC)
            .limit(limit)
            .toList()

        rows.map {
            TrendingTagDto(
                name = it[MediaTagsTable.name],
                postCount = it[MediaTagsTable.usageCount]
            )
        }
    }

    // --- FR-10: GET POSTS BY USER ---
    suspend fun getPostsByUser(
        viewerId: Long,
        userId: Long,
        page: Int,
        limit: Int,
    ): PaginatedFeedResponse = dbQuery {
        val offsetVal = ((page - 1) * limit).toLong()
        val visibleCondition = if (viewerId == userId) {
            (PostsTable.userId eq userId) and PostsTable.deletedAt.isNull()
        } else {
            (PostsTable.userId eq userId) and
                PostsTable.deletedAt.isNull() and
                (PostsTable.visibility eq PostVisibility.PUBLIC)
        }
        val baseQuery = (PostsTable innerJoin UsersTable)
            .selectAll()
            .where { visibleCondition }
        val totalRecords = baseQuery.count()
        val totalPages = Math.ceil(totalRecords.toDouble() / limit).toInt()

        val postRows = baseQuery.orderBy(PostsTable.createdAt to SortOrder.DESC)
            .limit(limit, offsetVal)
            .toList()
        val postIds = postRows.map { row -> row[PostsTable.id].value }
        val likedPostIds = if (postIds.isEmpty()) {
            emptySet()
        } else {
            LikesTable.selectAll()
                .where { (LikesTable.userId eq viewerId) and (LikesTable.postId inList postIds) }
                .map { row -> row[LikesTable.postId].value }
                .toSet()
        }
        val savedPostIds = if (postIds.isEmpty()) {
            emptySet()
        } else {
            SavedPostsTable.selectAll()
                .where { (SavedPostsTable.userId eq viewerId) and (SavedPostsTable.postId inList postIds) }
                .map { row -> row[SavedPostsTable.postId].value }
                .toSet()
        }

        val commentCountsByPost = CommentTreeVisibility.visibleCommentCountsByPost(
            postIds,
        )

        val posts = postRows.map { row ->
            val pId = row[PostsTable.id].value
            val mediaRows = PostMediaTable.selectAll().where { PostMediaTable.postId eq pId }
                .orderBy(PostMediaTable.position to SortOrder.ASC).toList()
            val mediaList = mediaRows.map { mRow ->
                FeedMediaDto(
                    id = mRow[PostMediaTable.id].value,
                    url = mRow[PostMediaTable.mediaFileUrl],
                    type = mRow[PostMediaTable.mediaType].name,
                    orderIndex = mRow[PostMediaTable.position],
                    width = mRow[PostMediaTable.width],
                    height = mRow[PostMediaTable.height]
                )
            }
            FeedPostDto(
                postId = pId,
                userId = row[UsersTable.id].value,
                username = row[UsersTable.username],
                userAvatar = row[UsersTable.profilePictureUrl],
                caption = row[PostsTable.caption],
                location = row[PostsTable.location],
                likeCount = row[PostsTable.likeCount],
                isLiked = likedPostIds.contains(pId),
                isSaved = savedPostIds.contains(pId),
                commentCount = commentCountsByPost[pId] ?: 0,
                createdAt = row[PostsTable.createdAt].toString(),
                media = mediaList
            )
        }

        PaginatedFeedResponse(
            posts = posts,
            meta = PaginationMeta(currentPage = page, totalPages = totalPages, hasNext = page < totalPages)
        )
    }

    // --- FR-19: TAG USER IN POST ---
    suspend fun tagUserInPost(ownerId: Long, postId: Long, taggedUserId: Long) = dbQuery {
        // TODO: Insert into PostTaggedUsersTable when table is created
    }

    suspend fun removeTagFromPost(ownerId: Long, postId: Long, taggedUserId: Long) = dbQuery {
        // TODO: Delete from PostTaggedUsersTable when table is created
    }

    // --- FR-33: COMMENT SETTINGS ---
    suspend fun updateCommentSettings(ownerId: Long, postId: Long, setting: String) = dbQuery {
        // PostsTable already has commentVisibility column as enum CommentVisibility
        // Map the string setting to the enum
        val visibility = when (setting) {
            "NONE" -> com.instagallery.models.common.CommentVisibility.NO_ONE
            "FOLLOWING" -> com.instagallery.models.common.CommentVisibility.FOLLOWERS_ONLY
            else -> com.instagallery.models.common.CommentVisibility.ALLOW_ALL
        }
        PostsTable.update({ (PostsTable.id eq postId) and (PostsTable.userId eq ownerId) }) {
            it[commentVisibility] = visibility
        }
    }
}
