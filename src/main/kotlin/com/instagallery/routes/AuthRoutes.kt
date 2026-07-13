package com.instagallery.routes

import com.instagallery.models.common.ApiResponse
import com.instagallery.models.request.ForgotPasswordRequest
import com.instagallery.models.request.GoogleLoginRequest
import com.instagallery.models.request.LoginRequest
import com.instagallery.models.request.RefreshTokenRequest
import com.instagallery.models.request.RegisterRequest
import com.instagallery.models.request.ResetPasswordRequest
import com.instagallery.services.AuthService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.plugins.origin
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import org.koin.ktor.ext.getKoin

fun Route.authRoutes() {
    val authService = application.getKoin().get<AuthService>()

    route("/api/v1/auth") {
        post("/register") {
            val request = call.receive<RegisterRequest>()
            val response = authService.register(request)

            call.respond(
                HttpStatusCode.Created,
                ApiResponse.success(
                    data = response,
                    message = "Đăng ký thành công"
                )
            )
        }

        post("/login") {
            val request = call.receive<LoginRequest>()
            val response = authService.login(request)

            call.respond(
                HttpStatusCode.OK,
                ApiResponse.success(
                    data = response,
                    message = "Login successful."
                )
            )
        }

        post("/refresh") {
            val request = call.receive<RefreshTokenRequest>()
            val deviceInfo = call.request.headers["User-Agent"]
            val ipAddress = call.request.origin.remoteHost

            val response = authService.refreshToken(request.refreshToken, deviceInfo, ipAddress)
            call.respond(HttpStatusCode.OK, ApiResponse.success(data = response, message = "Token refreshed."))
        }

        post("/forgot-password") {
            val request = call.receive<ForgotPasswordRequest>()
            val response = authService.requestPasswordReset(request.email)

            call.respond(
                HttpStatusCode.OK,
                ApiResponse.success(
                    data = response,
                    message = "If the email exists, a password reset code has been sent."
                )
            )
        }

        post("/reset-password") {
            val request = call.receive<ResetPasswordRequest>()
            authService.resetPassword(request)

            call.respond(
                HttpStatusCode.OK,
                ApiResponse.success(
                    data = true,
                    message = "Password has been reset successfully."
                )
            )
        }

        post("/google") {
            val request = call.receive<GoogleLoginRequest>()
            val deviceInfo = call.request.headers["User-Agent"]
            val ipAddress = call.request.origin.remoteHost
            val response = authService.loginWithGoogle(request, deviceInfo, ipAddress)

            call.respond(HttpStatusCode.OK, ApiResponse.success(data = response, message = "Google login successful."))
        }

        post("/2fa/verify-login") {
            call.respond(HttpStatusCode.OK, ApiResponse.success(data = true, message = "2FA verification successful."))
        }

        authenticate("jwt") {
            post("/logout") {
                val refreshToken = call.request.headers["X-Refresh-Token"]
                    ?: return@post call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse.error("MISSING_REFRESH", "Please provide X-Refresh-Token header.")
                    )

                authService.logout(refreshToken)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = true, message = "Logout successful."))
            }

            put("/change-password") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@put call.respond(
                        HttpStatusCode.Unauthorized,
                        ApiResponse.error("UNAUTHORIZED", "Token is invalid.")
                    )

                val request = call.receive<com.instagallery.models.request.ChangePasswordRequest>()
                authService.changePassword(userId, request)

                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse.success(
                        data = true,
                        message = "Password changed. Please log in again on your devices."
                    )
                )
            }

            post("/2fa/setup") {
                val principal = call.principal<JWTPrincipal>()
                principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@post call.respond(
                        HttpStatusCode.Unauthorized,
                        ApiResponse.error("UNAUTHORIZED", "Token is invalid.")
                    )

                call.respond(HttpStatusCode.OK, ApiResponse.success(data = true, message = "2FA setup created."))
            }

            post("/2fa/enable") {
                val principal = call.principal<JWTPrincipal>()
                principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@post call.respond(
                        HttpStatusCode.Unauthorized,
                        ApiResponse.error("UNAUTHORIZED", "Token is invalid.")
                    )

                call.respond(HttpStatusCode.OK, ApiResponse.success(data = true, message = "2FA enabled."))
            }
        }
    }
}
