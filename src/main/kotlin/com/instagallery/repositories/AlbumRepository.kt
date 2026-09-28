package com.instagallery.repositories

import com.instagallery.database.DatabaseFactory.dbQuery
import com.instagallery.database.tables.AlbumMediaTable
import com.instagallery.database.tables.AlbumsTable
import com.instagallery.database.tables.PostMediaTable
import com.instagallery.database.tables.PostsTable
import com.instagallery.database.tables.UsersTable
import com.instagallery.models.common.AlbumDetailDto
import com.instagallery.models.common.AlbumDto
import com.instagallery.models.common.FeedMediaDto
import com.instagallery.models.common.FeedPostDto
import com.instagallery.models.request.CreateAlbumRequest
import com.instagallery.models.request.UpdateAlbumRequest
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.time.Instant

class AlbumRepository {
    suspend fun create(userId: Long, request: CreateAlbumRequest): AlbumDto? = dbQuery {
        val id = AlbumsTable.insertAndGetId {
            it[AlbumsTable.userId] = EntityID(userId, UsersTable)
            it[title] = request.title.trim()
            it[description] = request.description?.trim()?.ifBlank { null }
            it[coverImageUrl] = request.coverImageUrl?.trim()?.ifBlank { null }
            it[isPrivate] = request.isPrivate
        }.value
        AlbumsTable.selectAll().where { AlbumsTable.id eq id }.singleOrNull()?.toAlbumDto(0)
    }

    suspend fun listVisible(ownerId: Long, viewerId: Long): List<AlbumDto> = dbQuery {
        val condition = if (ownerId == viewerId) {
            (AlbumsTable.userId eq ownerId) and AlbumsTable.deletedAt.isNull()
        } else {
            (AlbumsTable.userId eq ownerId) and AlbumsTable.deletedAt.isNull() and (AlbumsTable.isPrivate eq false)
        }
        val rows = AlbumsTable
            .selectAll()
            .where { condition }
            .orderBy(AlbumsTable.createdAt to SortOrder.DESC)
            .toList()
        val counts = mediaCounts(rows.map { it[AlbumsTable.id].value })
        rows.map { it.toAlbumDto(counts[it[AlbumsTable.id].value] ?: 0) }
    }

    suspend fun findActive(albumId: Long): ResultRow? = dbQuery {
        AlbumsTable
            .selectAll()
            .where { (AlbumsTable.id eq albumId) and AlbumsTable.deletedAt.isNull() }
            .singleOrNull()
    }

    suspend fun getDetail(albumId: Long, viewerId: Long): AlbumDetailDto? = dbQuery {
        val album = AlbumsTable
            .selectAll()
            .where { (AlbumsTable.id eq albumId) and AlbumsTable.deletedAt.isNull() }
            .singleOrNull() ?: return@dbQuery null

        val ownerId = album[AlbumsTable.userId].value
        if (album[AlbumsTable.isPrivate] && ownerId != viewerId) return@dbQuery null

        val postRows = AlbumMediaTable
            .join(PostsTable, JoinType.INNER, AlbumMediaTable.postId, PostsTable.id)
            .join(UsersTable, JoinType.INNER, PostsTable.userId, UsersTable.id)
            .selectAll()
            .where { (AlbumMediaTable.albumId eq albumId) and PostsTable.deletedAt.isNull() }
            .orderBy(AlbumMediaTable.addedAt to SortOrder.DESC)
            .toList()

        val postIds = postRows.map { it[PostsTable.id].value }
        val mediaByPost = firstMediaByPostId(postIds)
        val posts = postRows.map { row ->
            val postId = row[PostsTable.id].value
            FeedPostDto(
                postId = postId,
                userId = row[UsersTable.id].value,
                username = row[UsersTable.username],
                userAvatar = row[UsersTable.profilePictureUrl],
                caption = row[PostsTable.caption],
                location = row[PostsTable.location],
                likeCount = row[PostsTable.likeCount],
                commentCount = row[PostsTable.commentCount],
                createdAt = row[PostsTable.createdAt].toString(),
                media = mediaByPost[postId].orEmpty(),
            )
        }

        AlbumDetailDto(
            albumId = album[AlbumsTable.id].value,
            userId = ownerId,
            title = album[AlbumsTable.title],
            description = album[AlbumsTable.description],
            coverImageUrl = album[AlbumsTable.coverImageUrl],
            isPrivate = album[AlbumsTable.isPrivate],
            mediaCount = posts.size,
            createdAt = album[AlbumsTable.createdAt].toString(),
            updatedAt = album[AlbumsTable.updatedAt].toString(),
            posts = posts,
        )
    }

    suspend fun update(userId: Long, albumId: Long, request: UpdateAlbumRequest): Boolean = dbQuery {
        val updated = AlbumsTable.update({
            (AlbumsTable.id eq albumId) and (AlbumsTable.userId eq userId) and AlbumsTable.deletedAt.isNull()
        }) {
            request.title?.trim()?.takeIf { title -> title.isNotBlank() }?.let { title -> it[AlbumsTable.title] = title }
            request.description?.let { description -> it[AlbumsTable.description] = description.trim().ifBlank { null } }
            request.coverImageUrl?.let { url -> it[AlbumsTable.coverImageUrl] = url.trim().ifBlank { null } }
            request.isPrivate?.let { privateFlag -> it[AlbumsTable.isPrivate] = privateFlag }
            it[updatedAt] = Instant.now()
        }
        updated > 0
    }

    suspend fun softDelete(userId: Long, albumId: Long): Boolean = dbQuery {
        val updated = AlbumsTable.update({
            (AlbumsTable.id eq albumId) and (AlbumsTable.userId eq userId) and AlbumsTable.deletedAt.isNull()
        }) {
            it[deletedAt] = Instant.now()
            it[updatedAt] = Instant.now()
        }
        updated > 0
    }

    suspend fun addPosts(userId: Long, albumId: Long, postIds: List<Long>): Int = dbQuery {
        val album = AlbumsTable
            .selectAll()
            .where { (AlbumsTable.id eq albumId) and (AlbumsTable.userId eq userId) and AlbumsTable.deletedAt.isNull() }
            .singleOrNull() ?: return@dbQuery -1

        val ownedPostIds = PostsTable
            .selectAll()
            .where {
                (PostsTable.userId eq userId) and
                    (PostsTable.id inList postIds) and
                    PostsTable.deletedAt.isNull()
            }
            .map { it[PostsTable.id].value }
            .toSet()

        val existing = AlbumMediaTable
            .selectAll()
            .where { (AlbumMediaTable.albumId eq albumId) and (AlbumMediaTable.postId inList ownedPostIds) }
            .map { it[AlbumMediaTable.postId].value }
            .toSet()

        val toInsert = ownedPostIds - existing
        toInsert.forEach { postId ->
            AlbumMediaTable.insert {
                it[AlbumMediaTable.albumId] = EntityID(albumId, AlbumsTable)
                it[AlbumMediaTable.postId] = EntityID(postId, PostsTable)
            }
        }

        if (album[AlbumsTable.coverImageUrl].isNullOrBlank() && toInsert.isNotEmpty()) {
            val cover = PostMediaTable
                .selectAll()
                .where { PostMediaTable.postId eq toInsert.first() }
                .orderBy(PostMediaTable.position to SortOrder.ASC)
                .limit(1)
                .singleOrNull()
                ?.get(PostMediaTable.mediaFileUrl)
            if (cover != null) {
                AlbumsTable.update({ AlbumsTable.id eq albumId }) {
                    it[coverImageUrl] = cover
                    it[updatedAt] = Instant.now()
                }
            }
        } else if (toInsert.isNotEmpty()) {
            AlbumsTable.update({ AlbumsTable.id eq albumId }) {
                it[updatedAt] = Instant.now()
            }
        }
        toInsert.size
    }

    suspend fun removeMedia(userId: Long, albumId: Long, mediaId: Long): Boolean = dbQuery {
        val albumExists = AlbumsTable
            .selectAll()
            .where { (AlbumsTable.id eq albumId) and (AlbumsTable.userId eq userId) and AlbumsTable.deletedAt.isNull() }
            .count() > 0
        if (!albumExists) return@dbQuery false

        val deletedByRow = AlbumMediaTable.deleteWhere {
            (AlbumMediaTable.albumId eq albumId) and (AlbumMediaTable.id eq mediaId)
        }
        if (deletedByRow > 0) return@dbQuery true

        AlbumMediaTable.deleteWhere {
            (AlbumMediaTable.albumId eq albumId) and (AlbumMediaTable.postId eq mediaId)
        } > 0
    }

    private fun mediaCounts(albumIds: List<Long>): Map<Long, Int> {
        if (albumIds.isEmpty()) return emptyMap()
        return AlbumMediaTable
            .selectAll()
            .where { AlbumMediaTable.albumId inList albumIds }
            .groupBy { it[AlbumMediaTable.albumId].value }
            .mapValues { (_, rows) -> rows.size }
    }

    private fun firstMediaByPostId(postIds: List<Long>): Map<Long, List<FeedMediaDto>> {
        if (postIds.isEmpty()) return emptyMap()
        return PostMediaTable
            .selectAll()
            .where { PostMediaTable.postId inList postIds }
            .orderBy(PostMediaTable.position to SortOrder.ASC)
            .map { row ->
                row[PostMediaTable.postId].value to FeedMediaDto(
                    id = row[PostMediaTable.id].value,
                    url = row[PostMediaTable.mediaFileUrl],
                    type = row[PostMediaTable.mediaType].name,
                    orderIndex = row[PostMediaTable.position],
                    width = row[PostMediaTable.width],
                    height = row[PostMediaTable.height],
                )
            }
            .groupBy({ it.first }, { it.second })
    }

    private fun ResultRow.toAlbumDto(mediaCount: Int) = AlbumDto(
        albumId = this[AlbumsTable.id].value,
        userId = this[AlbumsTable.userId].value,
        title = this[AlbumsTable.title],
        description = this[AlbumsTable.description],
        coverImageUrl = this[AlbumsTable.coverImageUrl],
        isPrivate = this[AlbumsTable.isPrivate],
        mediaCount = mediaCount,
        createdAt = this[AlbumsTable.createdAt].toString(),
        updatedAt = this[AlbumsTable.updatedAt].toString(),
    )
}
