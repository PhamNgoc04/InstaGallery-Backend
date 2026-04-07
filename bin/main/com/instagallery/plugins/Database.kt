package com.instagallery.plugins

import com.instagallery.database.DatabaseFactory
import io.ktor.server.application.*

fun Application.configureDatabase() {
    DatabaseFactory.init(environment)
    log.info("Database initialized via DatabaseFactory")
}
