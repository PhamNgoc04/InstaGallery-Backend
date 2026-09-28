package com.instagallery.utils

import io.ktor.server.config.ApplicationConfig
import org.slf4j.LoggerFactory

object RequiredConfig {
    private val log = LoggerFactory.getLogger(RequiredConfig::class.java)
    private const val DEV_JWT_SECRET = "dev-only-jwt-secret-change-before-any-shared-deploy"
    private const val LEGACY_JWT_SECRET = "my-super-secret-key-for-instagallery-app-which-is-at-least-32-bytes"
    private const val DEV_DB_PASSWORD = "123456789"

    fun isProduction(config: ApplicationConfig): Boolean {
        val ktorEnv = config.propertyOrNull("ktor.environment")?.getString()
            ?: System.getenv("KTOR_ENV")
        return ktorEnv.equals("production", ignoreCase = true) ||
            ktorEnv.equals("prod", ignoreCase = true)
    }

    fun jwtSecret(config: ApplicationConfig): String {
        val value = valueOf(config, "jwt.secret", "JWT_SECRET")
        if (isProduction(config)) {
            if (value.isNullOrBlank() || value == DEV_JWT_SECRET || value == LEGACY_JWT_SECRET) {
                error("JWT_SECRET is required in production and must not be a development default.")
            }
            return value
        }
        if (!value.isNullOrBlank()) return value
        log.warn("JWT_SECRET is not set. Using a development-only secret. Set KTOR_ENV=production to require a real secret.")
        return DEV_JWT_SECRET
    }

    fun databasePassword(config: ApplicationConfig): String {
        val value = valueOf(config, "database.password", "DB_PASSWORD")
        if (isProduction(config)) {
            if (value.isNullOrBlank() || value == DEV_DB_PASSWORD) {
                error("DB_PASSWORD is required in production and must not be the development default.")
            }
            return value
        }
        if (!value.isNullOrBlank()) return value
        log.warn("DB_PASSWORD is not set. Using the local development database password.")
        return DEV_DB_PASSWORD
    }

    fun databaseUrl(config: ApplicationConfig): String {
        val value = valueOf(config, "database.url", "DB_URL")
        if (!value.isNullOrBlank()) return value
        if (isProduction(config)) {
            error("DB_URL is required in production. Set environment variable DB_URL.")
        }
        return "jdbc:mysql://localhost:3306/instagallery?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC"
    }

    fun databaseUser(config: ApplicationConfig): String {
        val value = valueOf(config, "database.user", "DB_USER")
        if (!value.isNullOrBlank()) return value
        if (isProduction(config)) {
            error("DB_USER is required in production. Set environment variable DB_USER.")
        }
        return "root"
    }

    fun accessTokenMinutes(config: ApplicationConfig): Long =
        config.propertyOrNull("jwt.accessTokenMinutes")?.getString()?.toLongOrNull()
            ?: System.getenv("JWT_ACCESS_TOKEN_MINUTES")?.toLongOrNull()
            ?: 15L

    private fun valueOf(config: ApplicationConfig, path: String, envName: String): String? =
        config.propertyOrNull(path)?.getString()?.takeIf { it.isNotBlank() }
            ?: System.getenv(envName)?.takeIf { it.isNotBlank() }
}
