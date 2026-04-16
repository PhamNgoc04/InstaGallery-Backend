package com.instagallery.services

import com.instagallery.models.common.UserDto
import com.instagallery.models.common.UserType
import com.instagallery.models.request.LoginRequest
import com.instagallery.models.request.RegisterRequest
import com.instagallery.models.response.LoginResponse
import com.instagallery.models.response.RegisterResponse
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
    private lateinit var passwordResetRepository: com.instagallery.repositories.PasswordResetRepository
    private lateinit var jwtManager: JwtManager

    @BeforeEach
    fun setup() {
        stopKoin()
        userRepository = mockk()
        sessionRepository = mockk()
        passwordResetRepository = mockk()
        jwtManager = mockk()
        io.mockk.mockkObject(PasswordHasher)

        startKoin {
            modules(
                module {
                    single { userRepository }
                    single { sessionRepository }
                    single { passwordResetRepository }
                    single { jwtManager }
                }
            )
        }
        authService = AuthService()
    }

    @org.junit.jupiter.api.AfterEach
    fun teardown() {
        io.mockk.unmockkObject(PasswordHasher)
        stopKoin()
    }

    @Test
    fun `register should throw ValidationException when password is too weak`() = runTest {
        val req = RegisterRequest("test@test.com", "testuser", "short", "Test Name")
        val exception = assertThrows<ValidationException> {
            authService.register(req)
        }
        assertEquals("Password must be at least 8 characters long.", exception.message)
    }

    @Test
    fun `register should throw ValidationException when email is invalid`() = runTest {
        val req = RegisterRequest("invalidemail", "testuser", "password", "Test Name")
        val exception = assertThrows<ValidationException> {
            authService.register(req)
        }
        assertEquals("Email format is incorrect.", exception.message)
    }

    @Test
    fun `register should throw ValidationException when email already exists`() = runTest {
        val req = RegisterRequest("test@test.com", "testuser", "password", "Test Name")
        coEvery { userRepository.getUserByEmail(req.email) } returns mockk()

        val exception = assertThrows<ValidationException> {
            authService.register(req)
        }
        assertEquals("This email is already registered.", exception.message)
    }

    @Test
    fun `register should successfully create user`() = runTest {
        val req = RegisterRequest("test@test.com", "testuser", "password", "test user")
        
        coEvery { userRepository.getUserByEmail(req.email) } returns null
        coEvery { userRepository.getUserByUsername(req.username) } returns null
        every { PasswordHasher.hashPassword(req.passwordHash) } returns "hashedPass"
        
        val mockUserDto = UserDto(
            id = 1L,
            username = "testuser",
            email = "test@test.com",
            passwordHash = "hashedPass",
            fullName = "test user",
            profilePictureUrl = null,
            role = com.instagallery.models.common.Role.USER,
            userType = com.instagallery.models.common.UserType.CLIENT,
            isActive = true,
            isVerified = false
        )
        coEvery { userRepository.createUser(req, "hashedPass") } returns mockUserDto
        every { jwtManager.generateToken(mockUserDto) } returns "mock-access-token"
        coEvery { sessionRepository.createSession(1L, null, null, any(), any()) } returns 1L

        val response = authService.register(req)
        assertEquals(1L, response.userId)
        assertEquals("mock-access-token", response.token)
    }

    @Test
    fun `login should throw AuthException when user not found`() = runTest {
        val req = LoginRequest("nonexistent@test.com", "password")
        coEvery { userRepository.getUserByEmail(req.email) } returns null

        val exception = assertThrows<AuthException> {
            authService.login(req)
        }
        assertEquals("Account with this email does not exist.", exception.message)
    }

    @Test
    fun `login should successfully return token`() = runTest {
        val req = LoginRequest("test@test.com", "password")
        val mockUser = UserDto(1L, "testuser", "test@test.com", "hashedPass", "Test User", null, com.instagallery.models.common.Role.USER, com.instagallery.models.common.UserType.CLIENT, true, false)
        
        coEvery { userRepository.getUserByEmail(req.email) } returns mockUser
        
        every { PasswordHasher.verifyPassword(req.passwordHash, "hashedPass") } returns true
        
        every { jwtManager.generateToken(mockUser) } returns "mock-access-token"
        coEvery { sessionRepository.createSession(1L, null, null, any(), any()) } returns 1L

        val response = authService.login(req)
        
        assertNotNull(response)
        assertEquals("mock-access-token", response.token)
        assertEquals(mockUser.username, response.username)
    }
}
