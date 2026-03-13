package com.instagallery.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.instagallery.models.common.UserDto
import io.ktor.server.config.*
import java.util.Date

class JwtManager(private val config: ApplicationConfig) {
    private val secret = config.property("jwt.secret").getString()
    private val issuer = config.property("jwt.issuer").getString()
    private val audience = config.property("jwt.audience").getString()
    // Default expiration: 7 days
    private val expirationMs = 7L * 24 * 60 * 60 * 1000

    fun generateToken(user: UserDto): String {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("userId", user.id)
            .withClaim("email", user.email)
            .withClaim("role", user.role.name)
            .withExpiresAt(Date(System.currentTimeMillis() + expirationMs))
            .sign(Algorithm.HMAC256(secret))
    }

    // Dùng cho WebSockets (Non-routing scope) - Phân rã JWT và xác thực thủ công
    fun verifyTokenSync(token: String): com.auth0.jwt.interfaces.DecodedJWT? {
        return try {
            val algorithm = Algorithm.HMAC256(secret)
            val verifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .withAudience(audience)
                .build()
            verifier.verify(token)
        } catch (e: Exception) {
            null
        }
    }
}
