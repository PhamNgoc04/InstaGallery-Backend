package com.instagallery.services

import com.instagallery.models.common.CommentDto
import com.instagallery.models.common.CommentReactionResponse
import com.instagallery.models.common.PaginatedCommentsResponse
import com.instagallery.models.common.PaginatedFeedResponse
import com.instagallery.models.common.PostLikesResponse
import com.instagallery.models.common.ToggleLikeResponse
import com.instagallery.models.common.ToggleSaveResponse
import com.instagallery.models.request.CreateCommentRequest
import com.instagallery.plugins.AuthException
import com.instagallery.plugins.ValidationException
import com.instagallery.repositories.InteractionRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class InteractionService : KoinComponent {
    private val interactionRepo: InteractionRepository by inject()
    private val notificationService: NotificationService by inject()

    suspend fun toggleLike(userId: Long, postId: Long): ToggleLikeResponse {
        val postExists = interactionRepo.checkPostExists(postId)
        if (!postExists) throw AuthException("POST_NOT_FOUND", "Bài viết không tồn tại hoặc đã bị xóa.")

        val (isLiked, total) = interactionRepo.toggleLike(userId, postId)
        if (isLiked) {
            interactionRepo.getPostOwnerId(postId)?.let { ownerId ->
                notificationService.notifyPostLiked(ownerId, userId, postId)
            }
        }
        return ToggleLikeResponse(isLiked, total)
    }

    suspend fun toggleSave(userId: Long, postId: Long): ToggleSaveResponse {
        val postExists = interactionRepo.checkPostExists(postId)
        if (!postExists) throw AuthException("POST_NOT_FOUND", "Bài viết không tồn tại hoặc đã bị xóa.")

        val isSaved = interactionRepo.toggleSave(userId, postId)
        if (isSaved) {
            interactionRepo.getPostOwnerId(postId)?.let { ownerId ->
                notificationService.notifyPostSaved(ownerId, userId, postId)
            }
        }
        return ToggleSaveResponse(isSaved)
    }

    suspend fun createComment(userId: Long, postId: Long, request: CreateCommentRequest): CommentDto {
        val contentClean = request.content.trim()
        if (contentClean.isBlank()) {
            throw ValidationException("EMPTY_CONTENT", "Nội dung bình luận không được để trống.")
        }

        val postExists = interactionRepo.checkPostExists(postId)
        if (!postExists) throw AuthException("POST_NOT_FOUND", "Bài viết không tồn tại hoặc đã bị xóa.")

        if (request.parentId != null) {
            val parentExists = interactionRepo.checkCommentBelongsToPost(request.parentId, postId)
            if (!parentExists) throw AuthException("PARENT_COMMENT_NOT_FOUND", "Bình luận cha không tồn tại hoặc đã bị xóa.")
        }

        val comment = interactionRepo.createComment(userId, postId, contentClean, request.parentId)
        val recipientIds = linkedSetOf<Long>()
        interactionRepo.getPostOwnerId(postId)?.let(recipientIds::add)
        request.parentId?.let { parentCommentId ->
            interactionRepo.getCommentOwnerId(parentCommentId)?.let(recipientIds::add)
        }
        recipientIds.forEach { recipientId ->
            notificationService.notifyPostCommented(recipientId, userId, postId)
        }
        return comment
    }

    suspend fun getComments(userId: Long, postId: Long, page: Int, limit: Int): PaginatedCommentsResponse {
        val postExists = interactionRepo.checkPostExists(postId)
        if (!postExists) throw AuthException("POST_NOT_FOUND", "Bài viết không tồn tại.")

        val verifiedPage = if (page < 1) 1 else page
        val verifiedLimit = if (limit < 1) 20 else if (limit > 50) 50 else limit

        return interactionRepo.getComments(userId, postId, verifiedPage, verifiedLimit)
    }

    suspend fun updateComment(userId: Long, commentId: Long, request: com.instagallery.models.request.UpdateCommentRequest): CommentDto {
        val contentClean = request.content.trim()
        if (contentClean.isBlank()) {
            throw ValidationException("EMPTY_CONTENT", "Nội dung bình luận không được để trống.")
        }

        return interactionRepo.updateComment(userId, commentId, contentClean)
            ?: throw AuthException("FORBIDDEN_ACTION", "Bình luận không tồn tại hoặc bạn không có quyền sửa.")
    }

    suspend fun deleteComment(userId: Long, commentId: Long): Boolean {
        val success = interactionRepo.deleteComment(userId, commentId)
        if (!success) {
            throw AuthException("FORBIDDEN_ACTION", "Bình luận không tồn tại hoặc bạn không có quyền xóa.")
        }
        return true
    }

    suspend fun toggleCommentLike(userId: Long, commentId: Long): CommentReactionResponse {
        val commentExists = interactionRepo.checkCommentExists(commentId)
        if (!commentExists) throw AuthException("COMMENT_NOT_FOUND", "Bình luận không tồn tại hoặc đã bị xóa.")

        val reaction = interactionRepo.toggleCommentLike(userId, commentId)
        if (reaction.isLiked) {
            val postId = interactionRepo.getCommentPostId(commentId)
            val ownerId = interactionRepo.getCommentOwnerId(commentId)
            if (postId != null && ownerId != null) {
                notificationService.notifyCommentLiked(ownerId, userId, postId)
            }
        }
        return reaction
    }

    suspend fun toggleCommentDislike(userId: Long, commentId: Long): CommentReactionResponse {
        val commentExists = interactionRepo.checkCommentExists(commentId)
        if (!commentExists) throw AuthException("COMMENT_NOT_FOUND", "BÃ¬nh luáº­n khÃ´ng tá»“n táº¡i hoáº·c Ä‘Ã£ bá»‹ xÃ³a.")

        return interactionRepo.toggleCommentDislike(userId, commentId)
    }

    suspend fun toggleFollow(followerId: Long, followingId: Long): Boolean {
        if (followerId == followingId) {
            throw ValidationException("SELF_FOLLOW", "Bạn không thể tự theo dõi chính mình.")
        }
        
        // Ensure followingId exists
        val userRepository: com.instagallery.repositories.UserRepository by inject()
        val userExists = userRepository.getUserById(followingId) != null
        if (!userExists) {
            throw AuthException("USER_NOT_FOUND", "Người dùng không tồn tại.")
        }

        val isFollowing = interactionRepo.toggleFollow(followerId, followingId)
        if (isFollowing) {
            notificationService.notifyUserFollowed(followingId, followerId)
        }
        return isFollowing
    }

    suspend fun getFollowers(userId: Long, page: Int, limit: Int): com.instagallery.models.common.PaginatedFollowsResponse {
        val userRepository: com.instagallery.repositories.UserRepository by inject()
        val userExists = userRepository.getUserById(userId) != null
        if (!userExists) {
            throw AuthException("USER_NOT_FOUND", "Người dùng không tồn tại.")
        }

        val verifiedPage = if (page < 1) 1 else page
        val verifiedLimit = if (limit < 1) 20 else if (limit > 50) 50 else limit

        return interactionRepo.getFollowers(userId, verifiedPage, verifiedLimit)
    }

    suspend fun getFollowing(userId: Long, page: Int, limit: Int): com.instagallery.models.common.PaginatedFollowsResponse {
        val userRepository: com.instagallery.repositories.UserRepository by inject()
        val userExists = userRepository.getUserById(userId) != null
        if (!userExists) {
            throw AuthException("USER_NOT_FOUND", "Người dùng không tồn tại.")
        }

        val verifiedPage = if (page < 1) 1 else page
        val verifiedLimit = if (limit < 1) 20 else if (limit > 50) 50 else limit

        return interactionRepo.getFollowing(userId, verifiedPage, verifiedLimit)
    }

    // --- FR-22: SAVED POSTS ---
    suspend fun getSavedPosts(userId: Long, page: Int, limit: Int): PaginatedFeedResponse {
        val verifiedPage = if (page < 1) 1 else page
        val verifiedLimit = if (limit < 1) 20 else if (limit > 50) 50 else limit
        return interactionRepo.getSavedPosts(userId, verifiedPage, verifiedLimit)
    }

    // --- FR-20: LIKED POSTS ---
    suspend fun getLikedPosts(userId: Long, page: Int, limit: Int): PaginatedFeedResponse {
        val verifiedPage = if (page < 1) 1 else page
        val verifiedLimit = if (limit < 1) 20 else if (limit > 50) 50 else limit
        return interactionRepo.getLikedPosts(userId, verifiedPage, verifiedLimit)
    }

    // --- FR-19: TAGGED POSTS ---
    suspend fun getTaggedPosts(userId: Long, page: Int, limit: Int): Any {
        val verifiedPage = if (page < 1) 1 else page
        val verifiedLimit = if (limit < 1) 20 else if (limit > 50) 50 else limit
        return interactionRepo.getTaggedPosts(userId, verifiedPage, verifiedLimit)
    }

    suspend fun getUserComments(
        userId: Long,
        page: Int,
        limit: Int,
    ): com.instagallery.models.common.PaginatedUserCommentsResponse {
        val verifiedPage = if (page < 1) 1 else page
        val verifiedLimit = if (limit < 1) 20 else if (limit > 50) 50 else limit
        return interactionRepo.getUserComments(userId, verifiedPage, verifiedLimit)
    }

    // --- FR-28: ACTIVITY LOG ---
    suspend fun getActivityLog(userId: Long, page: Int, limit: Int): Any {
        val verifiedPage = if (page < 1) 1 else page
        val verifiedLimit = if (limit < 1) 20 else if (limit > 50) 50 else limit
        return interactionRepo.getActivityLog(userId, verifiedPage, verifiedLimit)
    }

    // --- FR-31: BLOCKED USERS LIST ---
    suspend fun getBlockedUsers(userId: Long): Any {
        return interactionRepo.getBlockedUsers(userId)
    }

    // --- FR-20: WHO LIKED A POST ---
    suspend fun getPostLikes(postId: Long, page: Int, limit: Int): PostLikesResponse {
        val postExists = interactionRepo.checkPostExists(postId)
        if (!postExists) throw AuthException("POST_NOT_FOUND", "Bài viết không tồn tại hoặc đã bị xóa.")

        val verifiedPage = if (page < 1) 1 else page
        val verifiedLimit = if (limit < 1) 20 else if (limit > 50) 50 else limit
        return interactionRepo.getPostLikes(postId, verifiedPage, verifiedLimit)
    }

    // --- FR-27: SHARE POST ---
    suspend fun sharePost(userId: Long, postId: Long): Long {
        val postExists = interactionRepo.checkPostExists(postId)
        if (!postExists) throw AuthException("POST_NOT_FOUND", "Bài viết không tồn tại hoặc đã bị xóa.")
        return interactionRepo.incrementShareCount(userId, postId)
    }

    // --- FR-27: GET SHARE COUNT ---
    suspend fun getShareCount(postId: Long): Long {
        val postExists = interactionRepo.checkPostExists(postId)
        if (!postExists) throw AuthException("POST_NOT_FOUND", "Bài viết không tồn tại hoặc đã bị xóa.")
        return interactionRepo.getShareCount(postId)
    }
}
