package com.instagallery.services

import com.instagallery.models.common.ActivityTargetType
import com.instagallery.models.common.AlbumDetailDto
import com.instagallery.models.common.AlbumDto
import com.instagallery.models.request.AddAlbumMediaRequest
import com.instagallery.models.request.CreateAlbumRequest
import com.instagallery.models.request.UpdateAlbumRequest
import com.instagallery.plugins.AuthException
import com.instagallery.plugins.ForbiddenException
import com.instagallery.plugins.NotFoundException
import com.instagallery.plugins.ValidationException
import com.instagallery.repositories.ActivityLogRepository
import com.instagallery.repositories.AlbumRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AlbumService : KoinComponent {
    private val albumRepository: AlbumRepository by inject()
    private val activityLogRepository: ActivityLogRepository by inject()

    suspend fun createAlbum(userId: Long, request: CreateAlbumRequest): AlbumDto {
        val title = request.title.trim()
        if (title.isBlank()) {
            throw ValidationException("INVALID_TITLE", "Tên album không được để trống.")
        }
        val album = albumRepository.create(userId, request.copy(title = title))
            ?: throw Exception("Failed to create album.")
        activityLogRepository.log(userId, "CREATE_ALBUM", ActivityTargetType.ALBUM, album.albumId)
        return album
    }

    suspend fun listAlbums(viewerId: Long, ownerId: Long): List<AlbumDto> {
        return albumRepository.listVisible(ownerId, viewerId)
    }

    suspend fun getAlbum(viewerId: Long, albumId: Long): AlbumDetailDto {
        return albumRepository.getDetail(albumId, viewerId)
            ?: throw NotFoundException("Album không tồn tại hoặc bạn không có quyền xem.")
    }

    suspend fun updateAlbum(userId: Long, albumId: Long, request: UpdateAlbumRequest): AlbumDetailDto {
        val updated = albumRepository.update(userId, albumId, request)
        if (!updated) {
            throw ForbiddenException("Album không tồn tại hoặc bạn không có quyền sửa.")
        }
        activityLogRepository.log(userId, "UPDATE_ALBUM", ActivityTargetType.ALBUM, albumId)
        return getAlbum(userId, albumId)
    }

    suspend fun deleteAlbum(userId: Long, albumId: Long) {
        val deleted = albumRepository.softDelete(userId, albumId)
        if (!deleted) {
            throw ForbiddenException("Album không tồn tại hoặc bạn không có quyền xóa.")
        }
        activityLogRepository.log(userId, "DELETE_ALBUM", ActivityTargetType.ALBUM, albumId)
    }

    suspend fun addMedia(userId: Long, albumId: Long, request: AddAlbumMediaRequest): AlbumDetailDto {
        val postIds = request.postIds.distinct().filter { it > 0 }
        if (postIds.isEmpty()) {
            throw ValidationException("EMPTY_MEDIA", "Cần ít nhất một bài viết để thêm vào album.")
        }
        val added = albumRepository.addPosts(userId, albumId, postIds)
        if (added < 0) {
            throw ForbiddenException("Album không tồn tại hoặc bạn không có quyền sửa.")
        }
        if (added == 0) {
            throw ValidationException("NO_POSTS_ADDED", "Không có bài viết hợp lệ để thêm vào album.")
        }
        activityLogRepository.log(userId, "ADD_ALBUM_MEDIA", ActivityTargetType.ALBUM, albumId)
        return getAlbum(userId, albumId)
    }

    suspend fun removeMedia(userId: Long, albumId: Long, mediaId: Long) {
        val removed = albumRepository.removeMedia(userId, albumId, mediaId)
        if (!removed) {
            throw AuthException("FORBIDDEN_ACTION", "Không thể gỡ media khỏi album.")
        }
        activityLogRepository.log(userId, "REMOVE_ALBUM_MEDIA", ActivityTargetType.ALBUM, albumId)
    }
}
