package com.instagallery.database.tables

import com.instagallery.models.common.Role
import com.instagallery.models.common.UserType
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.timestamp

object UsersTable : LongIdTable("users") {
    val username = varchar("username", 50).uniqueIndex()
    val email = varchar("email", 100).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val fullName = varchar("full_name", 100)
    val profilePictureUrl = varchar("profile_picture_url", 255).nullable()
    val bio = text("bio").nullable()
    val website = varchar("website", 255).nullable()
    val gender = varchar("gender", 20).nullable()
    val phoneNumber = varchar("phone_number", 20).nullable()
    val dateOfBirth = date("date_of_birth").nullable()
    val location = varchar("location", 255).nullable()
    val userType = enumerationByName("user_type", 20, UserType::class).default(UserType.CLIENT)
    val role = enumerationByName("role", 10, Role::class).default(Role.USER)
    
    val isVerified = bool("is_verified").default(false)
    val isActive = bool("is_active").default(true)
    
    val followerCount = integer("follower_count").default(0)
    val followingCount = integer("following_count").default(0)
    val postCount = integer("post_count").default(0)
    
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    val updatedAt = timestamp("updated_at").defaultExpression(CurrentTimestamp)
    val deletedAt = timestamp("deleted_at").nullable()
}
