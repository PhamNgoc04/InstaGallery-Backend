package com.instagallery.services

import com.instagallery.models.common.UserDto
import com.instagallery.models.common.UserType
import com.instagallery.models.request.LoginRequest
import com.instagallery.models.request.RegisterRequest
import com.instagallery.models.response.AuthResponse
import com.instagallery.plugins.AuthException
import com.instagallery.plugins.ValidationException
import com.instagallery.repositories.SessionRepository
import com.instagallery.repositories.UserRepository
import com.instagallery.utils.JwtManager
import com.instagallery.utils.PasswordHasher
import io.mockk.coEvery
import io.mockk.every
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

class AuthServiceTest : KoinTest {

    private lateinit var authService: AuthService
    private lateinit var userRepository: UserRepository
    private lateinit var sessionRepository: SessionRepository
    private lateinit var passwordHasher: PasswordHasher
    private lateinit var jwtManager: JwtManager

    @BeforeEach
    fun setup() {
        stopKoin()
        userRepository = mockk()
        sessionRepository = mockk()
        passwordHasher = mockk()
        jwtManager = mockk()

        startKoin {
            modules(
                module {
                    single { userRepository }
                    single { sessionRepository }
                    single { passwordHasher }
                    single { jwtManager }
                }
            )
        }
        authService = AuthService()
    }

    @Test
    fun `register should throw ValidationException when username is empty`() = runTest {
        val req = RegisterRequest("", "test@test.com", "password", "Test Name", com.instagallery.models.common.UserType.USER)
        val exception = assertThrows<ValidationException> {
            authService.register(req)
        }
        assertEquals("Tên đăng nhập không được để trống.", exception.message)
    }

    @Test
    fun `register should throw ValidationException when email is invalid`() = runTest {
        val req = RegisterRequest("testuser", "invalidemail", "password", "Test Name", com.instagallery.models.common.UserType.USER)
        val exception = assertThrows<ValidationException> {
            authService.register(req)
        }
        assertEquals("Email không hợp lệ.", exception.message)
    }

    @Test
    fun `register should throw AuthException when email already exists`() = runTest {
        val req = RegisterRequest("testuser", "test@test.com", "password", "Test Name", com.instagallery.models.common.UserType.USER)
        coEvery { userRepository.getUserByEmail(req.email) } returns mockk()

        val exception = assertThrows<AuthException> {
            authService.register(req)
        }
        assertEquals("Email đã tồn tại.", exception.message)
    }

    @Test
    fun `register should successfully create user`() = runTest {
        val req = RegisterRequest("testuser", "test@test.com", "password", "test user", UserType.USER)
        
        coEvery { userRepository.getUserByEmail(req.email) } returns null
        coEvery { userRepository.getUserByUsername(req.username) } returns null
        every { passwordHasher.hashPassword(req.password) } returns "hashedPass"
        
        val mockUserDto = UserDto(
            id = 1L,
            username = "testuser",
            email = "test@test.com",
            fullName = "test user",
            role = com.instagallery.models.common.Role.USER,
            userType = UserType.USER,
            isActive = true,
            isVerified = false,
            passwordHash = "hashedPass"
        )
        coEvery { userRepository.createUser(req, "hashedPass") } returns mockUserDto

        val userId = authService.register(req)
        assertEquals(1L, userId)
    }

    @Test
    fun `login should throw AuthException when user not found`() = runTest {
        val req = LoginRequest("nonexistent@test.com", "password", null)
        coEvery { userRepository.getUserByEmail(req.email) } returns null

        val exception = assertThrows<AuthException> {
            authService.login(req)
        }
        assertEquals("Tài khoản hoặc mật khẩu không đúng.", exception.message)
    }

    @Test
    fun `login should successfully return token`() = runTest {
        val req = LoginRequest("test@test.com", "password", null)
        val mockUser = UserDto(1L, "testuser", "test@test.com", "hashedPassword", "Test User", null, com.instagallery.models.common.Role.USER, UserType.USER, true, false)
        
        coEvery { userRepository.getUserByEmail(req.email) } returns mockUser
        
        every { passwordHasher.checkPassword(req.password, "hashedPassword") } returns true
        
        every { jwtManager.generateAccessToken(1L, "testuser", "USER") } returns "mock-access-token"
        every { jwtManager.generateRefreshToken(1L) } returns "mock-refresh-token"
        coEvery { sessionRepository.createSession(any()) } returns 1L

        val response = authService.login(req)
        
        assertNotNull(response)
        assertEquals("mock-access-token", response.token)
        assertEquals("mock-refresh-token", response.refreshToken)
        assertEquals(mockUser.username, response.user.username)
    }
}
