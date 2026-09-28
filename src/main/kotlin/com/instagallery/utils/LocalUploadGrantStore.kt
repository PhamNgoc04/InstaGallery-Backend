package com.instagallery.utils

import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

object LocalUploadGrantStore {
    private data class Grant(val userId: Long, val expiresAt: Instant)

    private val grants = ConcurrentHashMap<String, Grant>()

    fun issue(userId: Long, folder: String, fileName: String, ttlSeconds: Long = 900) {
        prune()
        grants[key(folder, fileName)] = Grant(userId, Instant.now().plusSeconds(ttlSeconds))
    }

    fun consume(userId: Long, folder: String, fileName: String): Boolean {
        val mapKey = key(folder, fileName)
        val grant = grants[mapKey] ?: return false
        if (grant.expiresAt.isBefore(Instant.now())) {
            grants.remove(mapKey, grant)
            return false
        }
        if (grant.userId != userId) return false
        return grants.remove(mapKey, grant)
    }

    private fun key(folder: String, fileName: String) = "$folder/$fileName"

    private fun prune() {
        val now = Instant.now()
        grants.entries.removeIf { it.value.expiresAt.isBefore(now) }
    }
}
