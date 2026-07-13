package com.instagallery.routes

import com.instagallery.models.request.LoginRequest
import com.instagallery.models.request.RegisterRequest
import com.instagallery.models.response.LoginResponse
import com.instagallery.models.common.ApiResponse
import com.instagallery.plugins.*
import com.instagallery.database.tables.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.stopKoin
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

class AuthIntegrationTest {

    @BeforeEach
    fun setup() {
    }

    @AfterEach
    fun teardown() {
        stopKoin()
    }

    @Test
    fun `Integration Test - Register and Login flow`() = testApplication {
        // Setup isolated InMemory DB for this test execution map
        val uniqueDbName = UUID.randomUUID().toString()
        val db = Database.connect("jdbc:h2:mem:$uniqueDbName;DB_CLOSE_DELAY=-1;IGNORECASE=TRUE", driver = "org.h2.Driver")
        
        transaction(db) {
            SchemaUtils.create(
                UsersTable, UserSessionsTable, PortfoliosTable,
                PostsTable, FiltersTable, PostMediaTable, MediaTagsTable, PostMediaTagsTable,
                FollowersTable, LikesTable, CommentsTable, CommentLikesTable, SavedPostsTable,
                PhotographerServicesTable, BookingsTable, RatingsTable, ConversationsTable, ConversationMembersTable, MessagesTable,
                NotificationsTable, ActivityLogsTable, ReportsTable, SearchHistoriesTable, PasswordResetTokensTable
            )
        }

        environment {
            config = MapApplicationConfig(
                "jwt.secret" to "test_secret_must_be_long_enough_for_hs256",
                "jwt.issuer" to "http://localhost/",
                "jwt.audience" to "http://localhost/",
                "jwt.realm" to "test_realm"
            )
        }

        application {
            configureDependencyInjection()
            configureSerialization()
            configureSecurity()
            configureStatusPages()
            configureSockets()
            configureRouting()
        }

        val jsonSerializer = Json { ignoreUnknownKeys = true }

        // 1. REGISTER
        val registerRequest = RegisterRequest(
            email = "integration@test.com",
            username = "integrationuser",
            passwordHash = "testpassword123",
            fullName = "Integrator"
        )
        
        val registerResponse = client.post("/api/v1/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(jsonSerializer.encodeToString(registerRequest))
        }

        assertEquals(HttpStatusCode.Created, registerResponse.status)
        val registerBodyStr = registerResponse.bodyAsText()
        println("Register Response: $registerBodyStr")
        assertTrue(registerBodyStr.contains("SUCCESS"))
        assertTrue(registerBodyStr.contains("Đăng ký thành công"))

        // 2. LOGIN
        val loginRequest = LoginRequest(
            email = "integration@test.com",
            passwordHash = "testpassword123"
        )

        val loginResponse = client.post("/api/v1/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(jsonSerializer.encodeToString(loginRequest))
        }

        assertEquals(HttpStatusCode.OK, loginResponse.status)
        
        // 3. VERIFY LOGIN RESPONSE (Parse token)
        val loginBodyStr = loginResponse.bodyAsText()
        assertTrue(loginBodyStr.contains("token"))
        
        val apiResponse = jsonSerializer.decodeFromString<ApiResponse<LoginResponse>>(loginBodyStr)
        val token = apiResponse.data?.token
        
        assertNotNull(token, "Token should not be null after successful login")
        assertTrue(token!!.isNotEmpty(), "Token should not be empty")
        
        // 4. TEST INVALID LOGIN
        val invalidLoginRequest = LoginRequest(
            email = "integration@test.com",
            passwordHash = "wrongpassword"
        )

        val invalidLoginResponse = client.post("/api/v1/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(jsonSerializer.encodeToString(invalidLoginRequest))
        }

        assertEquals(HttpStatusCode.Unauthorized, invalidLoginResponse.status)
    }
}
