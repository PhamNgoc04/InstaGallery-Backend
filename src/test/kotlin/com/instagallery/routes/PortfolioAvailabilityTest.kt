package com.instagallery.routes

import com.instagallery.database.tables.*
import com.instagallery.models.common.ApiResponse
import com.instagallery.models.common.AvailabilityScheduleDto
import com.instagallery.models.common.AvailabilityType
import com.instagallery.models.common.DayOfWeekIso
import com.instagallery.models.common.UserType
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
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.junit.jupiter.api.AfterEach
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.koin.core.context.stopKoin
import java.util.UUID

class PortfolioAvailabilityTest {

    val jsonSerializer = Json { ignoreUnknownKeys = true }

    @AfterEach
    fun teardown() {
        stopKoin()
    }

    @Test
    fun `Test Portfolio Availability flow`() = testApplication {
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
                PhotographerServicesTable, BookingsTable, RatingsTable, ConversationsTable, ConversationMembersTable,
                MessagesTable, NotificationsTable, PasswordResetTokensTable, PostsTable,
                PostMediaTable, MediaTagsTable, PostMediaTagsTable, AvailabilitySchedulesTable
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

        // 1. REGISTER a photographer
        val registerRequest = RegisterRequest(
            username = "photographer_user",
            passwordHash = "password123",
            email = "photographer@example.com",
            fullName = "Photographer User",
            userType = UserType.PHOTOGRAPHER
        )

        client.post("/api/v1/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(jsonSerializer.encodeToString(registerRequest))
        }

        // 2. LOGIN to get token
        val loginRequest = LoginRequest(
            email = "photographer@example.com",
            passwordHash = "password123"
        )

        val loginResponse = client.post("/api/v1/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(jsonSerializer.encodeToString(loginRequest))
        }

        val loginBodyStr = loginResponse.bodyAsText()
        val apiResponse = jsonSerializer.decodeFromString<ApiResponse<LoginResponse>>(loginBodyStr)
        val accessToken = apiResponse.data?.token
        assertNotNull(accessToken)

        // Create portfolio for this photographer
        transaction {
            val userId = UsersTable.selectAll().where { UsersTable.email eq "photographer@example.com" }.single()[UsersTable.id].value
            PortfoliosTable.insert {
                it[this.userId] = userId
                it[this.description] = "Test portfolio"
                it[this.isAvailable] = true
            }
        }

        // 3. GET availability (empty initially)
        val getResponse = client.get("/api/v1/portfolios/me/availability") {
            header(HttpHeaders.Authorization, "Bearer $accessToken")
        }
        assertEquals(HttpStatusCode.OK, getResponse.status)
        val getBody = getResponse.bodyAsText()
        println("GET Empty Availability: $getBody")
        assertTrue(getBody.contains("SUCCESS"))

        // 4. POST availability
        val schedules = listOf(
            AvailabilityScheduleDto(
                type = AvailabilityType.RECURRING,
                dayOfWeek = DayOfWeekIso.MONDAY,
                startTime = "09:00",
                endTime = "17:00"
            )
        )

        val postResponse = client.post("/api/v1/portfolios/me/availability") {
            header(HttpHeaders.Authorization, "Bearer $accessToken")
            contentType(ContentType.Application.Json)
            setBody(jsonSerializer.encodeToString(schedules))
        }
        assertEquals(HttpStatusCode.OK, postResponse.status)
        println("POST Availability: ${postResponse.bodyAsText()}")

        // 5. GET availability again
        val getResponse2 = client.get("/api/v1/portfolios/me/availability") {
            header(HttpHeaders.Authorization, "Bearer $accessToken")
        }
        assertEquals(HttpStatusCode.OK, getResponse2.status)
        val getBody2 = getResponse2.bodyAsText()
        println("GET Filled Availability: $getBody2")
        assertTrue(getBody2.contains("MONDAY"))
        assertTrue(getBody2.contains("09:00"))
    }
}
