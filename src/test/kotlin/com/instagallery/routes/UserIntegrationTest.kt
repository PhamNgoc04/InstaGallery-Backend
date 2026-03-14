package com.instagallery.routes

import com.instagallery.database.tables.*
import com.instagallery.models.common.ApiResponse
import com.instagallery.models.request.LoginRequest
import com.instagallery.models.request.RegisterRequest
import com.instagallery.models.response.LoginResponse
import com.instagallery.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.koin.core.context.stopKoin
import java.util.UUID

class UserIntegrationTest {

    val jsonSerializer = Json { ignoreUnknownKeys = true }

    @AfterEach
    fun teardown() {
        stopKoin()
    }

    @Test
    fun `Integration Test - Authenticated GET me flow`() = testApplication {
        // Setup Isolated App and Database for this specific test
        val uniqueDbName = UUID.randomUUID().toString()
        Database.connect(
            url = "jdbc:h2:mem:$uniqueDbName;DB_CLOSE_DELAY=-1;IGNORECASE=TRUE",
            driver = "org.h2.Driver"
        )

        transaction {
            SchemaUtils.create(
                UsersTable, UserSessionsTable, PortfoliosTable, ActivityLogsTable,
                ReportsTable, SearchHistoriesTable, FiltersTable,
                FollowersTable, LikesTable, CommentsTable, CommentLikesTable, SavedPostsTable,
                BookingsTable, RatingsTable, ConversationsTable, ConversationMembersTable,
                MessagesTable, NotificationsTable, PasswordResetTokensTable, PostsTable,
                PostMediaTable, MediaTagsTable, PostMediaTagsTable // Add all tables required by all repositories since Koin injects everything
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

        // 1. REGISTER a new user
        val registerRequest = RegisterRequest(
            username = "authuser",
            passwordHash = "authpassword123",
            email = "authuser@example.com",
            fullName = "Authenticated User"
        )

        client.post("/api/v1/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(jsonSerializer.encodeToString(registerRequest))
        }

        // 2. LOGIN to get token
        val loginRequest = LoginRequest(
            email = "authuser@example.com",
            passwordHash = "authpassword123"
        )

        val loginResponse = client.post("/api/v1/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(jsonSerializer.encodeToString(loginRequest))
        }

        assertEquals(HttpStatusCode.OK, loginResponse.status)
        val loginBodyStr = loginResponse.bodyAsText()
        
        // Parse the token from the response
        val apiResponse = jsonSerializer.decodeFromString<ApiResponse<LoginResponse>>(loginBodyStr)
        val accessToken = apiResponse.data?.token
        
        assertNotNull(accessToken, "Access token must not be null")
        assertTrue(accessToken.isNotEmpty(), "Access token must not be empty")

        // 3. Request /me WITHOUT token (Should fail Unauthorized)
        val unauthorizedMeResponse = client.get("/api/v1/users/me")
        assertEquals(HttpStatusCode.Unauthorized, unauthorizedMeResponse.status)

        // 4. Request /me WITH valid token (Should succeed)
        val authorizedMeResponse = client.get("/api/v1/users/me") {
            header(HttpHeaders.Authorization, "Bearer $accessToken")
        }
        
        assertEquals(HttpStatusCode.OK, authorizedMeResponse.status)
        val meBodyStr = authorizedMeResponse.bodyAsText()
        println("User Me Response: $meBodyStr")
        assertTrue(meBodyStr.contains("SUCCESS"))
        assertTrue(meBodyStr.contains("authuser"))
        assertTrue(meBodyStr.contains("authuser@example.com"))
    }
}
