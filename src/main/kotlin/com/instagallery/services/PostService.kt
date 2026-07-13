package com.instagallery.services

import com.instagallery.models.common.PaginatedFeedResponse
import com.instagallery.models.common.PostDto
import com.instagallery.models.common.PostDetailDto
import com.instagallery.models.common.PostVisibility
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
    private val notificationService: NotificationService by inject()

    // --- CREATE POST ---
    suspend fun createPost(userId: Long, request: CreatePostRequest): PostDto {
        // Validation 
        if (request.media.isEmpty()) {
            throw ValidationException("EMPTY_MEDIA", "A post must have at least one media item.")
        }
        if (request.media.size > 10) {
            throw ValidationException("MEDIA_LIMIT_EXCEEDED", "Chỉ được đăng tối đa 10 ảnh hoặc video trong một bài viết (chuẩn Instagram).")
        }

        // Action
        val post = postRepository.createPost(userId, request)
            ?: throw Exception("Failed to create post")

        if (request.visibility != PostVisibility.PRIVATE) {
            postRepository.getFollowerIds(userId).forEach { followerId ->
                notificationService.notifyFollowedUserPosted(followerId, userId, post.postId)
            }
        }

        return post
    }

    suspend fun getPostDetails(postId: Long): com.instagallery.models.common.FeedPostDto {
        return postRepository.getPostById(postId)
            ?: throw ValidationException("POST_NOT_FOUND", "Bài viết không tồn tại hoặc đã bị xóa.")
    }

    // --- GET FEED --- 
    suspend fun getFeed(userId: Long, page: Int, limit: Int): PaginatedFeedResponse {
        val verifiedPage = if (page < 1) 1 else page
        val verifiedLimit = if (limit < 1) 10 else if (limit > 50) 50 else limit

        return postRepository.getFeedPosts(userId, verifiedPage, verifiedLimit)
    }

    // --- POST DETAIL ---
    suspend fun getPostDetail(postId: Long, currentUserId: Long): PostDetailDto {
        return when (val result = postRepository.getPostDetail(postId, currentUserId)) {
            is PostRepository.PostDetailResult.Success -> result.post
            is PostRepository.PostDetailResult.NotFound ->
                throw AuthException("POST_NOT_FOUND", "Bài đăng không tồn tại hoặc đã bị xóa.")
            is PostRepository.PostDetailResult.Forbidden ->
                throw AuthException("FORBIDDEN", "Bạn không có quyền xem bài đăng này.")
        }
    }

    // --- DELETE POST ---
    suspend fun deletePost(userId: Long, postId: Long) {
        val success = postRepository.logicSoftDeletePost(postId, userId)
        if (!success) {
            throw AuthException("FORBIDDEN_ACTION", "Post not found or you don't have permission to delete it.")
        }
    }

    // --- UPDATE POST ---
    suspend fun updatePost(userId: Long, postId: Long, request: com.instagallery.models.request.UpdatePostRequest) {
        val success = postRepository.updatePost(postId, userId, request)
        if (!success) {
            throw AuthException("FORBIDDEN_ACTION", "Post not found or you don't have permission to edit it.")
        }
    }

    // --- GET EXPLORE POSTS ---
    suspend fun getExplorePosts(page: Int, limit: Int, tag: String?): PaginatedFeedResponse {
        val verifiedPage = if (page < 1) 1 else page
        val verifiedLimit = if (limit < 1) 20 else if (limit > 50) 50 else limit

        return postRepository.getExplorePosts(verifiedPage, verifiedLimit, tag)
    }

    // --- GET TRENDING TAGS ---
    suspend fun getTrendingTags(limit: Int): List<com.instagallery.models.common.TrendingTagDto> {
        val verifiedLimit = if (limit < 1) 10 else if (limit > 50) 50 else limit
        return postRepository.getTrendingTags(verifiedLimit)
    }

    // --- FR-10: GET USER'S POSTS ---
    suspend fun getUserPosts(
        viewerId: Long,
        userId: Long,
        page: Int,
        limit: Int,
    ): PaginatedFeedResponse {
        val verifiedPage = if (page < 1) 1 else page
        val verifiedLimit = if (limit < 1) 20 else if (limit > 50) 50 else limit
        return postRepository.getPostsByUser(viewerId, userId, verifiedPage, verifiedLimit)
    }

    // --- FR-19: TAG USER IN POST ---
    suspend fun tagUserInPost(ownerId: Long, postId: Long, taggedUserId: Long) {
        val post = postRepository.getPostById(postId)
            ?: throw AuthException("POST_NOT_FOUND", "Бài viết không tồn tại.")
        postRepository.tagUserInPost(ownerId, postId, taggedUserId)
    }

    // --- FR-19: REMOVE TAG ---
    suspend fun removeTagFromPost(ownerId: Long, postId: Long, taggedUserId: Long) {
        postRepository.removeTagFromPost(ownerId, postId, taggedUserId)
    }

    // --- FR-33: COMMENT SETTINGS ---
    suspend fun updateCommentSettings(ownerId: Long, postId: Long, setting: String) {
        val validSettings = listOf("ALL", "FOLLOWING", "NONE")
        if (setting.uppercase() !in validSettings) {
            throw ValidationException("INVALID_SETTING", "Cài đặt bình luận phải là ALL, FOLLOWING hoặc NONE.")
        }
        postRepository.updateCommentSettings(ownerId, postId, setting.uppercase())
    }
}
