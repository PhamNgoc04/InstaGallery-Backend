package com.instagallery.repositories

import com.instagallery.database.DatabaseFactory.dbQuery
import com.instagallery.database.tables.*
import com.instagallery.models.common.*
import com.instagallery.models.request.CreatePostRequest
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.dao.id.EntityID
import java.time.Instant

class PostRepository {

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

        // 2. Link Media. (Assuming Media items are already uploaded and exist in PostMediaTable but orphaned, 
        // or here we assume frontend creates media first, then creates post. For simplicity of the scope, 
        // if media exists, we update them to point to this postId with order, or insert if they don't.
        // Actually, in typical flow, media is uploaded -> gets ID. Then Create Post assigns these existing IDs to this Post.
        // Let's UPDATE the existing media records to associate with this new post.
        var order = 1
        val updatedMediaList = mutableListOf<FeedMediaDto>()
        
        request.mediaIds.forEach { mId ->
            PostMediaTable.update({ PostMediaTable.id eq mId }) {
                it[postId] = newPostId
                it[position] = order
            }
            
            // Fetch to return
            val row = PostMediaTable.selectAll().where { PostMediaTable.id eq mId }.singleOrNull()
            if (row != null) {
                updatedMediaList.add(
                    FeedMediaDto(
                        id = row[PostMediaTable.id].value,
                        url = row[PostMediaTable.mediaFileUrl],
                        type = row[PostMediaTable.mediaType].name,
                        orderIndex = row[PostMediaTable.position]
                    )
                )
            }
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
            // In our schema we have PostMediaTagsTable connecting PostMedia and Tag.
            // For simplicity, we just link the tag to the first media of the post, or change the schema implicitly.
            // Looking at `instagallery_db_analysis.md`, `post_media_tags` links `media_id` and `tag_id`.
            if (request.mediaIds.isNotEmpty()) {
                val firstMediaId = request.mediaIds.first()
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

    suspend fun logicSoftDeletePost(postId: Long, userId: Long): Boolean = dbQuery {
        val count = PostsTable.update({ (PostsTable.id eq postId) and (PostsTable.userId eq userId) }) {
            it[deletedAt] = Instant.now()
        }
        count > 0
    }
    
    // Helper function for raw pagination
    suspend fun getFeedPosts(userId: Long, page: Int, limit: Int): PaginatedFeedResponse = dbQuery {
        val offsetVal = ((page - 1) * limit).toLong()
        
        // 1. Get Followings IDs
        val followingIds = FollowersTable
            .selectAll().where { FollowersTable.followerId eq userId }
            .map { it[FollowersTable.followingId].value }

        val targetUserIds = followingIds + userId // Includes own posts in feed
        
        // 2. Query valid posts
        val query = (PostsTable innerJoin UsersTable)
            .selectAll().where { (PostsTable.userId inList targetUserIds) and (PostsTable.deletedAt.isNull()) and (PostsTable.visibility eq PostVisibility.PUBLIC) }
            .orderBy(PostsTable.createdAt to SortOrder.DESC)

        val totalRecords = query.count()
        val totalPages = Math.ceil(totalRecords.toDouble() / limit).toInt()

        val postRows = query.limit(limit, offsetVal).toList()

        // 3. Map to DTOs
        val posts = postRows.map { row ->
            val pId = row[PostsTable.id].value
            
            // Get Media for this post
            val mediaRows = PostMediaTable.selectAll().where { PostMediaTable.postId eq pId }.orderBy(PostMediaTable.position to SortOrder.ASC).toList()
            val mediaList = mediaRows.map { mRow ->
                FeedMediaDto(
                    id = mRow[PostMediaTable.id].value,
                    url = mRow[PostMediaTable.mediaFileUrl],
                    type = mRow[PostMediaTable.mediaType].name,
                    orderIndex = mRow[PostMediaTable.position]
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
                commentCount = row[PostsTable.commentCount],
                createdAt = row[PostsTable.createdAt].toString(),
                media = mediaList
            )
        }

        PaginatedFeedResponse(
            posts = posts,
            meta = PaginationMeta(
                currentPage = page,
                totalPages = totalPages,
                hasNext = page < totalPages
            )
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

        val posts = postRows.map { row ->
            val pId = row[PostsTable.id].value
            
            val mediaRows = PostMediaTable.selectAll().where { PostMediaTable.postId eq pId }.orderBy(PostMediaTable.position to SortOrder.ASC).toList()
            val mediaList = mediaRows.map { mRow ->
                FeedMediaDto(
                    id = mRow[PostMediaTable.id].value,
                    url = mRow[PostMediaTable.mediaFileUrl],
                    type = mRow[PostMediaTable.mediaType].name,
                    orderIndex = mRow[PostMediaTable.position]
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
                commentCount = row[PostsTable.commentCount],
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
}
