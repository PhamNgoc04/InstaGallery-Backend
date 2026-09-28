package com.instagallery.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.instagallery.models.common.Role
import com.instagallery.models.common.UserAccount
import io.ktor.server.config.*
import java.util.Date

class JwtManager(private val config: ApplicationConfig) {
    private val secret = RequiredConfig.jwtSecret(config)
    private val issuer = config.propertyOrNull("jwt.issuer")?.getString() ?: "http://localhost:8080/"
    private val audience = config.propertyOrNull("jwt.audience")?.getString() ?: "http://localhost:8080/api/v1"
    private val expirationMs = RequiredConfig.accessTokenMinutes(config) * 60 * 1000

    fun generateToken(user: UserAccount): String = generateToken(user.id, user.email, user.role)

    fun generateToken(userId: Long, email: String, role: Role): String {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("userId", userId)
            .withClaim("email", email)
            .withClaim("role", role.name)
            .withExpiresAt(Date(System.currentTimeMillis() + expirationMs))
            .sign(Algorithm.HMAC256(secret))
    }

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
