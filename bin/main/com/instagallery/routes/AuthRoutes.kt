package com.instagallery.routes

import com.instagallery.models.common.ApiResponse
import com.instagallery.models.request.LoginRequest
import com.instagallery.models.request.RegisterRequest
import com.instagallery.services.AuthService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.origin
import io.ktor.server.auth.*
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
                    message = "Đăng nhập thành công"
                )
            )
        }
        post("/refresh") {
            val request = call.receive<com.instagallery.models.request.RefreshTokenRequest>()
            
            val deviceInfo = call.request.headers["User-Agent"]
            val ipAddress = call.request.origin.remoteHost

            val response = authService.refreshToken(request.refreshToken, deviceInfo, ipAddress)
            call.respond(HttpStatusCode.OK, ApiResponse.success(data = response, message = "Lấy Token mới thành công"))
        }

        post("/forgot-password") {
            val request = call.receive<com.instagallery.models.request.ForgotPasswordRequest>()
            
            val resetToken = authService.requestPasswordReset(request.email)
            if (resetToken != null) {
                // In production, trigger an email service payload here.
                println("[MOCK EMAIL] To: ${request.email} | Reset Token: $resetToken")
            }

            call.respond(HttpStatusCode.OK, ApiResponse.success(data = null, message = "Nếu email tồn tại, một đường dẫn khôi phục sẽ được gửi đến hòm thư."))
        }

        post("/reset-password") {
            val request = call.receive<com.instagallery.models.request.ResetPasswordRequest>()
            
            authService.resetPassword(request)
            
            call.respond(HttpStatusCode.OK, ApiResponse.success(data = null, message = "Mật khẩu đã được thiết lập lại thành công."))
        }

        post("/google") {
            // TODO: Receive Google JWT Token
            // TODO: Verify with Google Auth Library
            // TODO: Upsert into UsersTable (with provider=GOOGLE, providerId)
            // TODO: Generate and return local JWT Access Token
            call.respond(HttpStatusCode.OK, ApiResponse.success(data = null, message = "Đăng nhập Google thành công"))
        }

        post("/2fa/verify-login") {
            // TODO: Receive UserId and OTP Code
            // TODO: Verify OTP against twoFactorSecret
            // TODO: Generate and return JWT tokens if OTP is valid
            call.respond(HttpStatusCode.OK, ApiResponse.success(data = null, message = "Xác thực 2FA thành công"))
        }

        authenticate("jwt") {
            post("/logout") {
                val refreshToken = call.request.headers["X-Refresh-Token"]
                    ?: return@post call.respond(HttpStatusCode.BadRequest, ApiResponse.error("MISSING_REFRESH", "Vui lòng cung cấp X-Refresh-Token header để Logout hợp lệ."))
                
                authService.logout(refreshToken)
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = null, message = "Đăng xuất thành công"))
            }

            put("/change-password") {
                val principal = call.principal<io.ktor.server.auth.jwt.JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@put call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))

                val request = call.receive<com.instagallery.models.request.ChangePasswordRequest>()
                authService.changePassword(userId, request)
                
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = null, message = "Đổi mật khẩu thành công. Vui lòng đăng nhập lại trên các thiết bị."))
            }

            post("/2fa/setup") {
                val principal = call.principal<io.ktor.server.auth.jwt.JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))
                
                // TODO: Generate new TOTP Secret, update UsersTable.twoFactorSecret
                // TODO: Generate QR Code URI
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = null, message = "Cấu hình 2FA thành công"))
            }

            post("/2fa/enable") {
                val principal = call.principal<io.ktor.server.auth.jwt.JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("UNAUTHORIZED", "Token không hợp lệ."))
                
                // TODO: Receive OTP, verify against secret. If true, set isTwoFactorEnabled = true
                call.respond(HttpStatusCode.OK, ApiResponse.success(data = null, message = "Đã bật xác thực 2 bước"))
            }
        }
    }
}
