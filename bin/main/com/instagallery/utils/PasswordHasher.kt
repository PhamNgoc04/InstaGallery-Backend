package com.instagallery.utils

import org.mindrot.jbcrypt.BCrypt

object PasswordHasher {
    fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt(12)) // Cost set to 12
    }

    fun verifyPassword(password: String, hashedPw: String): Boolean {
        return BCrypt.checkpw(password, hashedPw)
    }
}
