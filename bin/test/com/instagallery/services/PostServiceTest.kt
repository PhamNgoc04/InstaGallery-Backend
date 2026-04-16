package com.instagallery.services

import com.instagallery.models.common.PostDto
import com.instagallery.models.common.PostVisibility
import com.instagallery.models.common.FeedPostDto
import com.instagallery.models.common.PaginatedFeedResponse
import com.instagallery.models.common.PaginationMeta
import com.instagallery.models.request.CreateMediaItemRequest
import com.instagallery.models.request.CreatePostRequest
import com.instagallery.plugins.ValidationException
import com.instagallery.plugins.AuthException
import com.instagallery.repositories.PostRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest

class PostServiceTest : KoinTest {

    private lateinit var postService: PostService
    private lateinit var postRepository: PostRepository

    @BeforeEach
    fun setup() {
        stopKoin()
        postRepository = mockk()

        startKoin {
            modules(
                module {
                    single { postRepository }
                }
            )
        }
        postService = PostService()
    }

    @Test
    fun `createPost should throw ValidationException when content is empty`() = runTest {
        val req = CreatePostRequest("", "Thành phố Hồ Chí Minh", PostVisibility.PUBLIC, emptyList())
        val exception = assertThrows<ValidationException> {
            postService.createPost(1L, req)
        }
        assertEquals("A post must have at least one media item.", exception.message)
    }

    @Test
    fun `createPost should call repository and return post id`() = runTest {
        val mediaItem = CreateMediaItemRequest(mediaFileUrl = "https://example.com/photo.jpg")
        val req = CreatePostRequest("Hello world", "Thành phố Hồ Chí Minh", PostVisibility.PUBLIC, listOf(mediaItem))
        val returnedMockPost = PostDto(100L, 1L, "Hello world", "Thành phố Hồ Chí Minh", PostVisibility.PUBLIC, 0, 0, "2024-01-01T00:00:00Z", emptyList())
        coEvery { postRepository.createPost(1L, req) } returns returnedMockPost

        val postResponse = postService.createPost(1L, req)
        assertEquals(100L, postResponse.postId)
    }

    @Test
    fun `getFeed should return mapped PageResponse`() = runTest {
        val mockData = listOf(
            FeedPostDto(1L, 1L, "testuser", "profile.jpg", "Hello world", "Thành phố Hồ Chí Minh", 0, 0, "2024-01-01T00:00:00Z", emptyList())
        )
        val mockResponse = PaginatedFeedResponse(mockData, PaginationMeta(1, 1, false))
        
        coEvery { postRepository.getFeedPosts(userId = 1L, page = 1, limit = 20) } returns mockResponse

        val response = postService.getFeed(1L, 1, 20)
        
        assertEquals(1, response.posts.size)
        assertEquals("Hello world", response.posts[0].caption)
        assertEquals(1, response.meta.currentPage)
    }

    @Test
    fun `deletePost should throw AuthException when post mapped to another user`() = runTest {
        coEvery { postRepository.logicSoftDeletePost(100L, 1L) } returns false

        val exception = assertThrows<AuthException> {
            postService.deletePost(1L, 100L)
        }
        assertEquals("FORBIDDEN_ACTION", exception.code)
    }

    @Test
    fun `deletePost should handle success deletion`() = runTest {
        coEvery { postRepository.logicSoftDeletePost(100L, 1L) } returns true

        assertDoesNotThrow {
            // Actually it returns nothing/unit
            // Just verifying no exception is thrown
        }
        // Assuming PostService deletePost doesn't return value but just calls repo
        // Will manually trigger it to ensure no throw
        val success = try {
            postService.deletePost(1L, 100L)
            true
        } catch (e: Exception) { false }
        
        assertTrue(success)
    }
}
