package com.instagallery.services

import com.instagallery.models.common.PostDto
import com.instagallery.models.common.PostVisibility
import com.instagallery.models.common.PaginatedPostsResponse
import com.instagallery.models.common.PaginationMeta
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
        val req = CreatePostRequest("", PostVisibility.PUBLIC, null, null, null)
        val exception = assertThrows<ValidationException> {
            postService.createPost(1L, req)
        }
        assertEquals("Nội dung bài viết không được để trống.", exception.message)
    }

    @Test
    fun `createPost should call repository and return post id`() = runTest {
        val req = CreatePostRequest("Hello world", PostVisibility.PUBLIC, null, null, null)
        coEvery { postRepository.createPost(1L, req) } returns 100L

        val postId = postService.createPost(1L, req)
        assertEquals(100L, postId)
    }

    @Test
    fun `getFeed should return mapped PageResponse`() = runTest {
        val mockData = listOf(
            PostDto(1L, 1L, "testuser", "profile.jpg", "Hello world", "PUBLIC", 0, 0, 0, null, null, emptyList(), null, emptyList(), null, "2024-01-01")
        )
        val mockResponse = PaginatedPostsResponse(mockData, PaginationMeta(1, 1, false))
        
        coEvery { postRepository.getFeedPosts(userId = 1L, page = 1, limit = 20) } returns mockResponse

        val response = postService.getFeed(1L, 1, 20)
        
        assertEquals(1, response.reports.size)
        assertEquals("Hello world", response.reports[0].content)
        assertEquals(1, response.meta.currentPage)
    }

    @Test
    fun `deletePost should throw AuthException when post mapped to another user`() = runTest {
        val mockPost = PostDto(100L, 2L, "another_user", "profile.jpg", "Hello rules", "PUBLIC", 0, 0, 0, null, null, emptyList(), null, emptyList(), null, "2024-01-01")
        coEvery { postRepository.getPostById(100L) } returns mockPost

        val exception = assertThrows<AuthException> {
            postService.deletePost(1L, 100L)
        }
        assertEquals("Bạn không có quyền xóa bài viết này.", exception.message)
    }

    @Test
    fun `deletePost should handle success deletion`() = runTest {
        val mockPost = PostDto(100L, 1L, "my_user", "profile.jpg", "Hello rules", "PUBLIC", 0, 0, 0, null, null, emptyList(), null, emptyList(), null, "2024-01-01")
        coEvery { postRepository.getPostById(100L) } returns mockPost
        coEvery { postRepository.deletePost(1L, 100L) } returns true

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
