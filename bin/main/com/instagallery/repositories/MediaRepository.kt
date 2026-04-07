package com.instagallery.repositories

import com.instagallery.database.DatabaseFactory.dbQuery
import com.instagallery.database.tables.PostMediaTable
import com.instagallery.database.tables.PostsTable
import com.instagallery.models.common.MediaDto
import com.instagallery.models.request.AddPostMediaRequest
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList

class MediaRepository {

    suspend fun verifyPostOwnership(userId: Long, postId: Long): Boolean = dbQuery {
        PostsTable.selectAll().where { (PostsTable.id eq postId) and (PostsTable.userId eq userId) and (PostsTable.deletedAt.isNull()) }.count() > 0
    }

    suspend fun getMediaCountForPost(postId: Long): Long = dbQuery {
        PostMediaTable.selectAll().where { PostMediaTable.postId eq postId }.count()
    }

    suspend fun addMediaToPost(postId: Long, request: AddPostMediaRequest): MediaDto? = dbQuery {
        // Find current max position
        val currentMaxPos = PostMediaTable.selectAll().where { PostMediaTable.postId eq postId }
            .map { it[PostMediaTable.position] }
            .maxOrNull() ?: -1

        val newPos = currentMaxPos + 1

        val insertId = PostMediaTable.insertAndGetId {
            it[PostMediaTable.postId] = postId
            it[mediaFileUrl] = request.mediaFileUrl
            it[thumbnailUrl] = request.thumbnailUrl
            it[mediaType] = request.mediaType
            it[position] = newPos
            it[filterId] = request.filterId
            it[width] = request.width
            it[height] = request.height
            it[fileSize] = request.fileSize
            it[duration] = request.duration
            it[metadata] = request.metadata
        }
        
        getMediaById(insertId.value)
    }

    suspend fun deleteMedia(mediaId: Long): Boolean = dbQuery {
        PostMediaTable.deleteWhere { PostMediaTable.id eq mediaId } > 0
    }

    suspend fun reorderMedia(postId: Long, mediaIds: List<Long>): Boolean = dbQuery {
        // Execute batch update or loop through and update position
        var successCount = 0
        mediaIds.forEachIndexed { index, mediaId ->
            val updated = PostMediaTable.update({ (PostMediaTable.id eq mediaId) and (PostMediaTable.postId eq postId) }) {
                it[position] = index
            }
            if(updated > 0) successCount++
        }
        return@dbQuery successCount == mediaIds.size
    }

    suspend fun getMediaById(mediaId: Long): MediaDto? = dbQuery {
        val row = PostMediaTable.selectAll().where { PostMediaTable.id eq mediaId }.singleOrNull() ?: return@dbQuery null
        row.toMediaDto()
    }
    
    suspend fun getPostIdByMediaId(mediaId: Long): Long? = dbQuery {
        PostMediaTable.selectAll().where { PostMediaTable.id eq mediaId }.singleOrNull()?.get(PostMediaTable.postId)?.value
    }

    private fun ResultRow.toMediaDto() = MediaDto(
        id = this[PostMediaTable.id].value,
        postId = this[PostMediaTable.postId].value,
        mediaFileUrl = this[PostMediaTable.mediaFileUrl],
        thumbnailUrl = this[PostMediaTable.thumbnailUrl],
        mediaType = this[PostMediaTable.mediaType],
        position = this[PostMediaTable.position],
        filterId = this[PostMediaTable.filterId]?.value,
        width = this[PostMediaTable.width],
        height = this[PostMediaTable.height],
        duration = this[PostMediaTable.duration],
        createdAt = this[PostMediaTable.createdAt].toString()
    )
}
