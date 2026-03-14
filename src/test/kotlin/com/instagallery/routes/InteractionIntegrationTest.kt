package com.instagallery.routes

import com.instagallery.database.tables.*
import com.instagallery.models.common.ApiResponse
import com.instagallery.models.common.PostVisibility
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
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.jsonPrimitive
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.koin.core.context.stopKoin
import java.util.UUID

class InteractionIntegrationTest {

    val jsonSerializer = Json { ignoreUnknownKeys = true }

    @AfterEach
    fun teardown() {
        stopKoin()
    }

    @Test
    fun `Integration Test - Authenticated Like Post flow`() = testApplication {
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
                PostMediaTable, MediaTagsTable, PostMediaTagsTable
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
            username = "likeruser",
            passwordHash = "likepassword123",
            email = "likeruser@example.com",
            fullName = "Liker User"
        )

        client.post("/api/v1/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(jsonSerializer.encodeToString(registerRequest))
        }

        // 2. LOGIN to get token
        val loginRequest = LoginRequest(
            email = "likeruser@example.com",
            passwordHash = "likepassword123"
        )

        val loginResponse = client.post("/api/v1/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(jsonSerializer.encodeToString(loginRequest))
        }

        val loginBodyStr = loginResponse.bodyAsText()
        val apiResponse = jsonSerializer.decodeFromString<ApiResponse<LoginResponse>>(loginBodyStr)
        val accessToken = apiResponse.data?.token
        assertNotNull(accessToken, "Access token must not be null")

        // 3. Create a DUMMY POST in the Database transaction
        var testPostId = 0L
        transaction {
            // Find the user we just registered
            val userRow = UsersTable.select(UsersTable.columns).where { UsersTable.email eq "likeruser@example.com" }.firstOrNull()
            assertNotNull(userRow, "User should exist after registration")
            val userIdVal = userRow[UsersTable.id].value

            testPostId = PostsTable.insertAndGetId {
                it[userId] = userIdVal
                it[caption] = "Test Post for Interaction"
                it[visibility] = PostVisibility.PUBLIC
            }.value
        }

        assertTrue(testPostId > 0, "Test post must have a valid ID")

        // 4. Send LIKE request with token
        val likeResponse = client.post("/api/v1/posts/$testPostId/like") {
            header(HttpHeaders.Authorization, "Bearer $accessToken")
        }

        assertEquals(HttpStatusCode.OK, likeResponse.status)
        val likeBodyStr = likeResponse.bodyAsText()
        println("Like Response: $likeBodyStr")
        
        // Assert response contents
        assertTrue(likeBodyStr.contains("SUCCESS"))
        
        val likeObj = jsonSerializer.parseToJsonElement(likeBodyStr).jsonObject
        val dataObj = likeObj["data"]?.jsonObject
        val isLiked = dataObj?.get("isLiked")?.jsonPrimitive?.booleanOrNull
        
        assertNotNull(isLiked, "isLiked field shouldn't be null")
        assertTrue(isLiked, "isLiked must be true after liking")
    }
}
