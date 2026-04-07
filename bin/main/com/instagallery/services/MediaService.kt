package com.instagallery.services

import com.instagallery.models.common.MediaDto
import com.instagallery.models.common.PresignedUrlResponse
import com.instagallery.models.request.AddPostMediaRequest
import com.instagallery.models.request.PresignedUrlRequest
import com.instagallery.models.request.ReorderMediaRequest
import com.instagallery.plugins.AuthException
import com.instagallery.plugins.ValidationException
import com.instagallery.repositories.MediaRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.UUID

class MediaService : KoinComponent {
    private val mediaRepository: MediaRepository by inject()

    // Mock S3 Generator for Phase 3
    suspend fun generatePresignedUrl(userId: Long, request: PresignedUrlRequest): PresignedUrlResponse {
        val extension = request.fileName.substringAfterLast('.', "")
        if (extension !in listOf("jpg", "jpeg", "png", "mp4", "mov")) {
            throw ValidationException("INVALID_EXTENSION", "Chỉ hỗ trợ file ảnh và video phổ biến.")
        }

        val folder = request.folder ?: "posts"
        val mockHash = UUID.randomUUID().toString()
        val cdnFileName = "$mockHash.$extension"
        val mockUploadUrl = "https://s3.aws-mock.com/instagallery-bucket/$folder/$cdnFileName?X-Amz-Signature=mock-sig-123"
        val cdnFileUrl = "https://cdn.instagallery.com/$folder/$cdnFileName"

        return PresignedUrlResponse(
            uploadUrl = mockUploadUrl,
            fileUrl = cdnFileUrl,
            expiresIn = 900 // 15 mins
        )
    }

    suspend fun addMediaToPost(userId: Long, postId: Long, request: AddPostMediaRequest): MediaDto {
        val ownsPost = mediaRepository.verifyPostOwnership(userId, postId)
        if (!ownsPost) {
            throw AuthException("UNAUTHORIZED_ACTION", "Bạn không có quyền sửa bài viết này hoặc bài viết không tồn tại.")
        }

        val currentCount = mediaRepository.getMediaCountForPost(postId)
        if (currentCount >= 10) {
            throw ValidationException("MEDIA_LIMIT_EXCEEDED", "Một bài viết chỉ tối đa 10 media (chuẩn Instagram).")
        }

        return mediaRepository.addMediaToPost(postId, request)
            ?: throw Exception("Lỗi hệ thống khi thêm media.")
    }

    suspend fun deleteMedia(userId: Long, mediaId: Long): Boolean {
        // Find postId of this media
        val postId = mediaRepository.getPostIdByMediaId(mediaId)
            ?: throw ValidationException("MEDIA_NOT_FOUND", "Media không tồn tại.")

        val ownsPost = mediaRepository.verifyPostOwnership(userId, postId)
        if (!ownsPost) {
            throw AuthException("UNAUTHORIZED_ACTION", "Bạn không có quyền xóa media này.")
        }

        val currentCount = mediaRepository.getMediaCountForPost(postId)
        if (currentCount <= 1) {
            throw ValidationException("CANT_DELETE_LAST_MEDIA", "Bài viết phải có ít nhất 1 hình ảnh hoặc video.")
        }

        return mediaRepository.deleteMedia(mediaId)
    }

    suspend fun reorderMedia(userId: Long, postId: Long, request: ReorderMediaRequest): Boolean {
        val ownsPost = mediaRepository.verifyPostOwnership(userId, postId)
        if (!ownsPost) {
            throw AuthException("UNAUTHORIZED_ACTION", "Bạn không có quyền sửa bài viết này.")
        }

        if (request.mediaIds.isEmpty()) {
            throw ValidationException("INVALID_REQUEST", "Danh sách media trống.")
        }

        return mediaRepository.reorderMedia(postId, request.mediaIds)
    }
}
