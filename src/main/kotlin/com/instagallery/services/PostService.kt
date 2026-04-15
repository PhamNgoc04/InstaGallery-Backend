package com.instagallery.services

import com.instagallery.models.common.PaginatedFeedResponse
import com.instagallery.models.common.PostDto
import com.instagallery.models.request.CreatePostRequest
import com.instagallery.plugins.AuthException
import com.instagallery.plugins.ValidationException
import com.instagallery.repositories.PostRepository
import com.instagallery.repositories.UserRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PostService : KoinComponent {
    private val postRepository: PostRepository by inject()
    private val userRepository: UserRepository by inject()

    suspend fun createPost(userId: Long, request: CreatePostRequest): PostDto {
        // Validation 
        if (request.media.isEmpty()) {
            throw ValidationException("EMPTY_MEDIA", "A post must have at least one media item.")
        }
        if (request.media.size > 10) {
            throw ValidationException("MEDIA_LIMIT_EXCEEDED", "Chỉ được đăng tối đa 10 ảnh hoặc video trong một bài viết (chuẩn Instagram).")
        }

        // Action
        return postRepository.createPost(userId, request) 
            ?: throw Exception("Failed to create post")
    }

    suspend fun getPostDetails(postId: Long): com.instagallery.models.common.FeedPostDto {
        return postRepository.getPostById(postId)
            ?: throw ValidationException("POST_NOT_FOUND", "Bài viết không tồn tại hoặc đã bị xóa.")
    }

    suspend fun getFeed(userId: Long, page: Int, limit: Int): PaginatedFeedResponse {
        val verifiedPage = if (page < 1) 1 else page
        val verifiedLimit = if (limit < 1) 10 else if (limit > 50) 50 else limit

        return postRepository.getFeedPosts(userId, verifiedPage, verifiedLimit)
    }

    suspend fun deletePost(userId: Long, postId: Long) {
        val success = postRepository.logicSoftDeletePost(postId, userId)
        if (!success) {
            throw AuthException("FORBIDDEN_ACTION", "Post not found or you don't have permission to delete it.")
        }
    }

    suspend fun updatePost(userId: Long, postId: Long, request: com.instagallery.models.request.UpdatePostRequest) {
        val success = postRepository.updatePost(postId, userId, request)
        if (!success) {
            throw AuthException("FORBIDDEN_ACTION", "Post not found or you don't have permission to edit it.")
        }
    }

    suspend fun getExplorePosts(page: Int, limit: Int, tag: String?): PaginatedFeedResponse {
        val verifiedPage = if (page < 1) 1 else page
        val verifiedLimit = if (limit < 1) 20 else if (limit > 50) 50 else limit

        return postRepository.getExplorePosts(verifiedPage, verifiedLimit, tag)
    }

    suspend fun getTrendingTags(limit: Int): List<com.instagallery.models.common.TrendingTagDto> {
        val verifiedLimit = if (limit < 1) 10 else if (limit > 50) 50 else limit
        return postRepository.getTrendingTags(verifiedLimit)
    }
}
