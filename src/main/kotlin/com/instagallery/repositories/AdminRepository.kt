package com.instagallery.repositories

import com.instagallery.database.DatabaseFactory.dbQuery
import com.instagallery.database.tables.*
import com.instagallery.models.common.AdminBannedKeywordDto
import com.instagallery.models.common.AdminBookingDto
import com.instagallery.models.common.AdminBookingsResponse
import com.instagallery.models.common.AdminGrowthDto
import com.instagallery.models.common.AdminMediaDto
import com.instagallery.models.common.AdminMediaItemDto
import com.instagallery.models.common.AdminMediaLibraryResponse
import com.instagallery.models.common.AdminNotificationDto
import com.instagallery.models.common.AdminNotificationsResponse
import com.instagallery.models.common.AdminPostDto
import com.instagallery.models.common.AdminPostsResponse
import com.instagallery.models.common.AdminRatingDto
import com.instagallery.models.common.AdminRatingsResponse
import com.instagallery.models.common.AdminStatsDto
import com.instagallery.models.common.AdminUserDetailDto
import com.instagallery.models.common.AdminUserSummaryDto
import com.instagallery.models.common.AdminUsersResponse
import com.instagallery.models.common.BookingStatus
import com.instagallery.models.common.ReportStatus
import com.instagallery.models.common.ReportTargetType
import com.instagallery.models.common.UserType
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNotNull
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.javatime.date
import java.time.Instant
import java.time.LocalDate

class AdminRepository {

    suspend fun getOverviewStats(): AdminStatsDto = dbQuery {
        val users = UsersTable.selectAll().count()
        val photographers = UsersTable.selectAll().where { UsersTable.userType eq UserType.PHOTOGRAPHER }.count()
        val posts = PostsTable.selectAll().where { PostsTable.deletedAt.isNull() }.count()
        val bookings = BookingsTable.selectAll().count()
        val reports = ReportsTable.selectAll().where { ReportsTable.status eq ReportStatus.PENDING }.count()

        AdminStatsDto(users, photographers, posts, bookings, reports)
    }

    suspend fun getGrowth(type: String, days: Int): List<AdminGrowthDto> = dbQuery {
        val startDate = LocalDate.now().minusDays(days.toLong())
        val queryResult = mutableMapOf<String, Long>()

        when (type.uppercase()) {
            "USERS" -> {
                val dates = UsersTable
                    .select(UsersTable.createdAt.date(), UsersTable.id.count())
                    .where { UsersTable.createdAt.date() greaterEq startDate }
                    .groupBy(UsersTable.createdAt.date())
                    .toList()
                    
                dates.forEach { row ->
                    queryResult[row[UsersTable.createdAt.date()].toString()] = row[UsersTable.id.count()]
                }
            }
            "POSTS" -> {
                val dates = PostsTable
                    .select(PostsTable.createdAt.date(), PostsTable.id.count())
                    .where { PostsTable.createdAt.date() greaterEq startDate }
                    .groupBy(PostsTable.createdAt.date())
                    .toList()

                dates.forEach { row ->
                    queryResult[row[PostsTable.createdAt.date()].toString()] = row[PostsTable.id.count()]
                }
            }
            "BOOKINGS" -> {
                val dates = BookingsTable
                    .select(BookingsTable.createdAt.date(), BookingsTable.id.count())
                    .where { BookingsTable.createdAt.date() greaterEq startDate }
                    .groupBy(BookingsTable.createdAt.date())
                    .toList()

                dates.forEach { row ->
                    queryResult[row[BookingsTable.createdAt.date()].toString()] = row[BookingsTable.id.count()]
                }
            }
        }

        // Fill missing dates with 0
        val growthList = mutableListOf<AdminGrowthDto>()
        for (i in (days - 1) downTo 0) {
            val dateStr = LocalDate.now().minusDays(i.toLong()).toString()
            growthList.add(AdminGrowthDto(dateStr, queryResult[dateStr] ?: 0))
        }

        growthList
    }

    // --- MODERATION ---
    suspend fun banUser(targetUserId: Long, isBanned: Boolean, reason: String?): Boolean = dbQuery {
        val count = UsersTable.update({ UsersTable.id eq targetUserId }) {
            it[isActive] = !isBanned
            if (isBanned) {
                it[deletedAt] = java.time.Instant.now()
            } else {
                it[deletedAt] = null
            }
            it[updatedAt] = java.time.Instant.now()
        }
        count > 0
    }

    suspend fun deletePostAsAdmin(postId: Long): Boolean = dbQuery {
        val count = PostsTable.update({ PostsTable.id eq postId }) {
            it[deletedAt] = java.time.Instant.now()
        }
        count > 0
    }

    suspend fun deleteCommentAsAdmin(commentId: Long): Boolean = dbQuery {
        val commentRow = CommentsTable
            .selectAll()
            .where { (CommentsTable.id eq commentId) and CommentsTable.deletedAt.isNull() }
            .singleOrNull() ?: return@dbQuery false

        val postId = commentRow[CommentsTable.postId].value
        val parentId = commentRow[CommentsTable.parentCommentId]?.value
        val subtreeIds = CommentTreeVisibility.visibleSubtreeIds(postId, commentId)
        val now = Instant.now()

        CommentsTable.update({ CommentsTable.id inList subtreeIds.toList() }) {
            it[CommentsTable.deletedAt] = now
            it[CommentsTable.updatedAt] = now
        }

        if (parentId != null) {
            val visibleReplyCount = CommentsTable
                .selectAll()
                .where {
                    (CommentsTable.parentCommentId eq parentId) and
                        CommentsTable.deletedAt.isNull()
                }
                .count()
                .toInt()

            CommentsTable.update({ CommentsTable.id eq parentId }) {
                it[CommentsTable.replyCount] = visibleReplyCount
                it[CommentsTable.updatedAt] = now
            }
        }

        val visibleCommentCount = CommentTreeVisibility.visibleCommentCount(postId)
        PostsTable.update({ PostsTable.id eq postId }) {
            it[PostsTable.commentCount] = visibleCommentCount
        }
        true
    }

    // --- FR-43: DANH SÁCH NGƯỜI DÙNG ---
    suspend fun listUsers(page: Int, limit: Int, search: String?, status: String?): AdminUsersResponse = dbQuery {
        val offset = ((page - 1) * limit).toLong()
        var condition: Op<Boolean> = Op.TRUE

        if (!search.isNullOrBlank()) {
            condition = condition and (
                (UsersTable.username like "%$search%") or (UsersTable.email like "%$search%") or (UsersTable.fullName like "%$search%")
            )
        }

        when (status?.uppercase()) {
            "ACTIVE" -> condition = condition and (UsersTable.isActive eq true) and UsersTable.deletedAt.isNull()
            "BANNED", "DEACTIVATED" -> condition = condition and ((UsersTable.isActive eq false) or UsersTable.deletedAt.isNotNull())
        }

        val query = UsersTable.selectAll().where { condition }
        val total = query.count()
        val users = query.orderBy(UsersTable.role to SortOrder.ASC, UsersTable.createdAt to SortOrder.DESC)
            .limit(limit, offset)
            .map { row ->
                val idVal = row[UsersTable.id].value
                val isPhotographer = row[UsersTable.userType] == UserType.PHOTOGRAPHER
                val stats = if (isPhotographer) getPhotographerStats(idVal) else null

                AdminUserSummaryDto(
                    userId = idVal,
                    username = row[UsersTable.username],
                    email = row[UsersTable.email],
                    fullName = row[UsersTable.fullName],
                    profilePictureUrl = row[UsersTable.profilePictureUrl],
                    phoneNumber = row[UsersTable.phoneNumber],
                    location = row[UsersTable.location],
                    role = row[UsersTable.role].name,
                    isActive = row[UsersTable.isActive] && row[UsersTable.deletedAt] == null,
                    isVerified = row[UsersTable.isVerified],
                    userType = row[UsersTable.userType].name,
                    followerCount = row[UsersTable.followerCount],
                    followingCount = row[UsersTable.followingCount],
                    postCount = row[UsersTable.postCount],
                    createdAt = row[UsersTable.createdAt].toString(),
                    specialties = stats?.specialties,
                    ratingAvg = stats?.ratingAvg,
                    bookingsCount = stats?.bookingsCount,
                    revenue = stats?.revenue,
                    isFeatured = stats?.isFeatured
                )
            }

        AdminUsersResponse(users = users, total = total, page = page, limit = limit)
    }

    // --- FR-43: CHI TIẾT NGƯỜI DÙNG ---
    suspend fun getUserDetail(userId: Long): AdminUserDetailDto? = dbQuery {
        UsersTable.selectAll().where { UsersTable.id eq userId }.singleOrNull()?.let { row ->
            val isPhotographer = row[UsersTable.userType] == UserType.PHOTOGRAPHER
            val stats = if (isPhotographer) getPhotographerStats(userId) else null

            val portfolioPhotos = if (isPhotographer) {
                (PostMediaTable innerJoin PostsTable)
                    .select(PostMediaTable.mediaFileUrl)
                    .where { (PostsTable.userId eq userId) and PostsTable.deletedAt.isNull() }
                    .orderBy(PostMediaTable.createdAt to SortOrder.DESC)
                    .map { it[PostMediaTable.mediaFileUrl] }
            } else null

            val servicePackagesCount = if (isPhotographer) {
                PhotographerServicesTable.selectAll().where { PhotographerServicesTable.photographerId eq userId }.count()
            } else null

            val reviewsCount = if (isPhotographer) {
                RatingsTable.selectAll().where { RatingsTable.rateeId eq userId }.count()
            } else null

            AdminUserDetailDto(
                userId = row[UsersTable.id].value,
                username = row[UsersTable.username],
                email = row[UsersTable.email],
                fullName = row[UsersTable.fullName],
                profilePictureUrl = row[UsersTable.profilePictureUrl],
                bio = row[UsersTable.bio],
                phoneNumber = row[UsersTable.phoneNumber],
                location = row[UsersTable.location],
                isActive = row[UsersTable.isActive] && row[UsersTable.deletedAt] == null,
                isPrivate = row[UsersTable.isPrivate],
                isVerified = row[UsersTable.isVerified],
                userType = row[UsersTable.userType].name,
                role = row[UsersTable.role].name,
                followerCount = row[UsersTable.followerCount],
                followingCount = row[UsersTable.followingCount],
                postCount = row[UsersTable.postCount],
                createdAt = row[UsersTable.createdAt].toString(),
                specialties = stats?.specialties,
                ratingAvg = stats?.ratingAvg,
                bookingsCount = stats?.bookingsCount,
                revenue = stats?.revenue,
                isFeatured = stats?.isFeatured,
                portfolioPhotos = portfolioPhotos,
                servicePackagesCount = servicePackagesCount,
                reviewsCount = reviewsCount
            )
        }
    }

    suspend fun verifyUser(targetUserId: Long, isVerified: Boolean): Boolean = dbQuery {
        UsersTable.update({ UsersTable.id eq targetUserId }) {
            it[UsersTable.isVerified] = isVerified
            it[updatedAt] = Instant.now()
        } > 0
    }

    suspend fun featurePhotographer(targetUserId: Long, isFeatured: Boolean): Boolean = dbQuery {
        val exists = PortfoliosTable.selectAll().where { PortfoliosTable.userId eq targetUserId }.count() > 0
        if (exists) {
            PortfoliosTable.update({ PortfoliosTable.userId eq targetUserId }) {
                it[PortfoliosTable.isFeatured] = isFeatured
                it[updatedAt] = Instant.now()
            } > 0
        } else {
            PortfoliosTable.insert {
                it[userId] = targetUserId
                it[PortfoliosTable.isFeatured] = isFeatured
                it[isAvailable] = true
            }
            true
        }
    }

    suspend fun listPosts(page: Int, limit: Int, search: String?, status: String?): AdminPostsResponse = dbQuery {
        val offset = ((page - 1) * limit).toLong()
        var condition: Op<Boolean> = Op.TRUE

        if (!search.isNullOrBlank()) {
            condition = condition and (
                (PostsTable.caption like "%$search%") or
                    (PostsTable.location like "%$search%") or
                    (UsersTable.username like "%$search%") or
                    (UsersTable.fullName like "%$search%")
                )
        }

        when (status?.uppercase()) {
            "ACTIVE", "VISIBLE", "APPROVED" -> condition = condition and PostsTable.deletedAt.isNull()
            "HIDDEN", "DELETED" -> condition = condition and PostsTable.deletedAt.isNotNull()
        }

        val query = (PostsTable innerJoin UsersTable).selectAll().where { condition }
        val total = query.count()
        val posts = query
            .orderBy(PostsTable.createdAt to SortOrder.DESC)
            .limit(limit, offset)
            .map { row -> row.toAdminPostDto(includeMedia = false) }

        AdminPostsResponse(posts = posts, total = total, page = page, limit = limit)
    }

    suspend fun getPostDetail(postId: Long): AdminPostDto? = dbQuery {
        (PostsTable innerJoin UsersTable)
            .selectAll()
            .where { PostsTable.id eq postId }
            .singleOrNull()
            ?.toAdminPostDto(includeMedia = true)
    }

    suspend fun updatePostStatus(postId: Long, status: String): Boolean = dbQuery {
        val shouldRestore = status.uppercase() in listOf("ACTIVE", "VISIBLE", "APPROVED")
        PostsTable.update({ PostsTable.id eq postId }) {
            it[deletedAt] = if (shouldRestore) null else Instant.now()
            it[updatedAt] = Instant.now()
        } > 0
    }

    suspend fun listBookings(page: Int, limit: Int, search: String?, status: String?): AdminBookingsResponse = dbQuery {
        val offset = ((page - 1) * limit).toLong()
        var condition: Op<Boolean> = Op.TRUE
        val statusFilter = status?.takeIf { it.isNotBlank() }?.let { runCatching { BookingStatus.valueOf(it.uppercase()) }.getOrNull() }

        if (statusFilter != null) {
            condition = condition and (BookingsTable.status eq statusFilter)
        }

        val query = BookingsTable.selectAll().where { condition }
        val total = query.count()
        val bookings = query
            .orderBy(BookingsTable.createdAt to SortOrder.DESC)
            .limit(limit, offset)
            .map { row -> row.toAdminBookingDto() }
            .filter { booking ->
                search.isNullOrBlank() ||
                    listOf(booking.code, booking.clientName, booking.photographerName, booking.packageName, booking.locationBooking)
                        .filterNotNull()
                        .any { it.contains(search, ignoreCase = true) }
            }

        AdminBookingsResponse(bookings = bookings, total = total, page = page, limit = limit)
    }

    suspend fun getBookingDetail(bookingId: Long): AdminBookingDto? = dbQuery {
        BookingsTable
            .selectAll()
            .where { BookingsTable.id eq bookingId }
            .singleOrNull()
            ?.toAdminBookingDto()
    }

    suspend fun updateBookingStatus(bookingId: Long, status: BookingStatus): Boolean = dbQuery {
        BookingsTable.update({ BookingsTable.id eq bookingId }) {
            it[BookingsTable.status] = status
            it[updatedAt] = Instant.now()
        } > 0
    }

    suspend fun listRatings(page: Int, limit: Int, search: String?, status: String?): AdminRatingsResponse = dbQuery {
        val offset = ((page - 1) * limit).toLong()
        var condition: Op<Boolean> = Op.TRUE
        if (!status.isNullOrBlank()) {
            condition = condition and (RatingsTable.status eq status)
        }

        val query = RatingsTable.selectAll().where { condition }
        val total = query.count()
        val ratings = query
            .orderBy(RatingsTable.createdAt to SortOrder.DESC)
            .limit(limit, offset)
            .map { row -> row.toAdminRatingDto() }
            .filter { rating ->
                search.isNullOrBlank() ||
                    listOf(rating.comment, rating.photographerName, rating.reviewerName)
                        .filterNotNull()
                        .any { it.contains(search, ignoreCase = true) }
            }

        AdminRatingsResponse(ratings = ratings, total = total, page = page, limit = limit)
    }

    suspend fun deleteRating(ratingId: Long): Boolean = dbQuery {
        val ratingRow = RatingsTable.selectAll().where { RatingsTable.id eq ratingId }.singleOrNull()
            ?: return@dbQuery false
        val updated = RatingsTable.update({ RatingsTable.id eq ratingId }) {
            it[RatingsTable.status] = "HIDDEN"
        } > 0
        if (updated) {
            recalculatePortfolioScore(ratingRow[RatingsTable.rateeId].value)
        }
        updated
    }

    suspend fun updateRatingStatus(ratingId: Long, status: String): Boolean = dbQuery {
        val ratingRow = RatingsTable.selectAll().where { RatingsTable.id eq ratingId }.singleOrNull()
            ?: return@dbQuery false
        val updated = RatingsTable.update({ RatingsTable.id eq ratingId }) {
            it[RatingsTable.status] = status
        } > 0
        if (updated) {
            recalculatePortfolioScore(ratingRow[RatingsTable.rateeId].value)
        }
        updated
    }

    suspend fun listMedia(page: Int, limit: Int, search: String?): AdminMediaLibraryResponse = dbQuery {
        val offset = ((page - 1) * limit).toLong()
        val query = PostMediaTable.selectAll()
        val total = query.count()
        val media = query
            .orderBy(PostMediaTable.createdAt to SortOrder.DESC)
            .limit(limit, offset)
            .mapNotNull { row -> row.toAdminMediaItemDto() }
            .filter { item ->
                search.isNullOrBlank() ||
                    listOf(item.mediaFileUrl, item.thumbnailUrl, item.ownerName, item.mediaType, item.status)
                        .filterNotNull()
                        .any { it.contains(search, ignoreCase = true) }
            }

        AdminMediaLibraryResponse(media = media, total = total, page = page, limit = limit)
    }

    suspend fun deleteMedia(mediaId: Long): Boolean = dbQuery {
        PostMediaTable.deleteWhere { PostMediaTable.id eq mediaId } > 0
    }

    suspend fun listNotifications(page: Int, limit: Int, search: String?): AdminNotificationsResponse = dbQuery {
        val offset = ((page - 1) * limit).toLong()
        val query = NotificationsTable.selectAll()
        val total = query.count()
        val notifications = query
            .orderBy(NotificationsTable.createdAt to SortOrder.DESC)
            .limit(limit, offset)
            .map { row -> row.toAdminNotificationDto() }
            .filter { item ->
                search.isNullOrBlank() ||
                    listOf(item.title, item.body, item.userName, item.type)
                        .filterNotNull()
                        .any { it.contains(search, ignoreCase = true) }
            }

        AdminNotificationsResponse(notifications = notifications, total = total, page = page, limit = limit)
    }

    suspend fun listNotificationRecipientIds(target: String): List<Long> = dbQuery {
        var condition: Op<Boolean> = (UsersTable.isActive eq true) and UsersTable.deletedAt.isNull()

        when (target.uppercase()) {
            "PHOTOGRAPHER", "PHOTOGRAPHERS" -> condition = condition and (UsersTable.userType eq UserType.PHOTOGRAPHER)
            "CLIENT", "CLIENTS", "USER", "USERS" -> condition = condition and (UsersTable.userType eq UserType.CLIENT)
        }

        UsersTable
            .select(UsersTable.id)
            .where { condition }
            .map { it[UsersTable.id].value }
    }

    // --- FR-46: TỪ KHÓA CẤM ---
    suspend fun getBannedKeywords(): List<AdminBannedKeywordDto> = dbQuery {
        BannedWordsTable
            .selectAll()
            .orderBy(BannedWordsTable.createdAt to SortOrder.DESC)
            .map { row ->
                AdminBannedKeywordDto(
                    id = row[BannedWordsTable.id].value,
                    wordOrRegex = row[BannedWordsTable.wordOrRegex],
                    keyword = row[BannedWordsTable.wordOrRegex],
                    isRegex = row[BannedWordsTable.isRegex],
                    createdAt = row[BannedWordsTable.createdAt].toString()
                )
            }
    }

    suspend fun addBannedKeyword(keyword: String, isRegex: Boolean): Boolean = dbQuery {
        val exists = BannedWordsTable
            .selectAll()
            .where { BannedWordsTable.wordOrRegex eq keyword }
            .count() > 0
        if (!exists) {
            BannedWordsTable.insert {
                it[wordOrRegex] = keyword
                it[BannedWordsTable.isRegex] = isRegex
            }
        }
        true
    }

    suspend fun removeBannedKeyword(keywordId: Long): Boolean = dbQuery {
        BannedWordsTable.deleteWhere { BannedWordsTable.id eq keywordId } > 0
    }

    private fun recalculatePortfolioScore(photographerId: Long) {
        val ratings = RatingsTable.selectAll()
            .where { (RatingsTable.rateeId eq photographerId) and (RatingsTable.status eq "APPROVED") }
            .map { it[RatingsTable.ratingValue] }
        val reviewCount = ratings.size
        val averageScore = if (reviewCount > 0) {
            ratings.sum() / reviewCount.toDouble()
        } else {
            0.0
        }

        PortfoliosTable.update({ PortfoliosTable.userId eq photographerId }) {
            it[PortfoliosTable.ratingAvg] = averageScore.toBigDecimal()
            it[PortfoliosTable.reviewCount] = reviewCount
        }
    }

    private fun ResultRow.toAdminPostDto(includeMedia: Boolean): AdminPostDto {
        val postId = this[PostsTable.id].value
        val mediaRows = PostMediaTable
            .selectAll()
            .where { PostMediaTable.postId eq postId }
            .orderBy(PostMediaTable.position to SortOrder.ASC)
            .toList()
        val media = mediaRows.map { row ->
            AdminMediaDto(
                id = row[PostMediaTable.id].value,
                url = row[PostMediaTable.mediaFileUrl],
                thumbnailUrl = row[PostMediaTable.thumbnailUrl],
                mediaType = row[PostMediaTable.mediaType].name,
                position = row[PostMediaTable.position],
                width = row[PostMediaTable.width],
                height = row[PostMediaTable.height],
                fileSize = row[PostMediaTable.fileSize],
                duration = row[PostMediaTable.duration],
                createdAt = row[PostMediaTable.createdAt].toString()
            )
        }
        val reportCount = ReportsTable
            .selectAll()
            .where { (ReportsTable.targetType eq ReportTargetType.POST) and (ReportsTable.targetId eq postId) }
            .count()
        val deletedAtValue = this[PostsTable.deletedAt]?.toString()
        val cover = media.firstOrNull()?.url ?: media.firstOrNull()?.thumbnailUrl

        return AdminPostDto(
            id = postId,
            postId = postId,
            code = "#POST${postId.toString().padStart(6, '0')}",
            authorId = this[UsersTable.id].value,
            authorName = this[UsersTable.fullName],
            authorUsername = this[UsersTable.username],
            authorAvatar = this[UsersTable.profilePictureUrl],
            caption = this[PostsTable.caption],
            location = this[PostsTable.location],
            visibility = this[PostsTable.visibility].name,
            likeCount = this[PostsTable.likeCount],
            commentCount = this[PostsTable.commentCount],
            shareCount = this[PostsTable.shareCount],
            reportCount = reportCount,
            mediaCount = media.size,
            coverUrl = cover,
            media = if (includeMedia) media else emptyList(),
            status = if (deletedAtValue == null) "ACTIVE" else "DELETED",
            createdAt = this[PostsTable.createdAt].toString(),
            deletedAt = deletedAtValue
        )
    }

    private fun ResultRow.toAdminBookingDto(): AdminBookingDto {
        val bookingId = this[BookingsTable.id].value
        val clientId = this[BookingsTable.clientId].value
        val photographerId = this[BookingsTable.photographerId].value
        val client = UsersTable.selectAll().where { UsersTable.id eq clientId }.singleOrNull()
        val photographer = UsersTable.selectAll().where { UsersTable.id eq photographerId }.singleOrNull()
        val status = this[BookingsTable.status]

        return AdminBookingDto(
            id = bookingId,
            bookingId = bookingId,
            code = "#BK${bookingId.toString().padStart(6, '0')}",
            clientId = clientId,
            clientName = client.displayName(),
            clientAvatarUrl = client?.get(UsersTable.profilePictureUrl),
            photographerId = photographerId,
            photographerName = photographer.displayName(),
            photographerAvatarUrl = photographer?.get(UsersTable.profilePictureUrl),
            serviceId = this[BookingsTable.serviceId]?.value,
            packageName = this[BookingsTable.packageName],
            shootingType = this[BookingsTable.shootingType],
            sceneType = this[BookingsTable.sceneType],
            bookingDate = this[BookingsTable.bookingDate].toString(),
            durationHours = this[BookingsTable.durationHours]?.toDouble(),
            locationBooking = this[BookingsTable.locationBooking],
            addressDetail = this[BookingsTable.addressDetail],
            details = this[BookingsTable.details],
            peopleCount = this[BookingsTable.peopleCount],
            contactPhone = this[BookingsTable.contactPhone],
            price = this[BookingsTable.price]?.toDouble(),
            currency = this[BookingsTable.currency],
            status = status.name,
            paymentStatus = if (status == BookingStatus.COMPLETED) "PAID" else "UNPAID",
            createdAt = this[BookingsTable.createdAt].toString()
        )
    }

    private fun ResultRow.toAdminRatingDto(): AdminRatingDto {
        val reviewerId = this[RatingsTable.raterId].value
        val photographerId = this[RatingsTable.rateeId].value
        val reviewer = UsersTable.selectAll().where { UsersTable.id eq reviewerId }.singleOrNull()
        val photographer = UsersTable.selectAll().where { UsersTable.id eq photographerId }.singleOrNull()

        return AdminRatingDto(
            id = this[RatingsTable.id].value,
            bookingId = this[RatingsTable.bookingId].value,
            photographerId = photographerId,
            photographerName = photographer.displayName(),
            photographerAvatarUrl = photographer?.get(UsersTable.profilePictureUrl),
            reviewerId = reviewerId,
            reviewerName = reviewer.displayName(),
            reviewerAvatarUrl = reviewer?.get(UsersTable.profilePictureUrl),
            ratingValue = this[RatingsTable.ratingValue].toInt(),
            comment = this[RatingsTable.comment],
            status = this[RatingsTable.status],
            createdAt = this[RatingsTable.createdAt].toString()
        )
    }

    private fun ResultRow.toAdminMediaItemDto(): AdminMediaItemDto? {
        val postId = this[PostMediaTable.postId].value
        val post = PostsTable.selectAll().where { PostsTable.id eq postId }.singleOrNull() ?: return null
        val ownerId = post[PostsTable.userId].value
        val owner = UsersTable.selectAll().where { UsersTable.id eq ownerId }.singleOrNull()

        return AdminMediaItemDto(
            id = this[PostMediaTable.id].value,
            postId = postId,
            ownerId = ownerId,
            ownerName = owner.displayName(),
            ownerAvatar = owner?.get(UsersTable.profilePictureUrl),
            mediaFileUrl = this[PostMediaTable.mediaFileUrl],
            thumbnailUrl = this[PostMediaTable.thumbnailUrl],
            mediaType = this[PostMediaTable.mediaType].name,
            width = this[PostMediaTable.width],
            height = this[PostMediaTable.height],
            fileSize = this[PostMediaTable.fileSize],
            duration = this[PostMediaTable.duration],
            status = if (post[PostsTable.deletedAt] == null) "IN_USE" else "ORPHANED",
            createdAt = this[PostMediaTable.createdAt].toString()
        )
    }

    private fun ResultRow.toAdminNotificationDto(): AdminNotificationDto {
        val userId = this[NotificationsTable.userId].value
        val senderId = this[NotificationsTable.senderId]?.value
        val user = UsersTable.selectAll().where { UsersTable.id eq userId }.singleOrNull()
        val sender = senderId?.let { id -> UsersTable.selectAll().where { UsersTable.id eq id }.singleOrNull() }

        return AdminNotificationDto(
            id = this[NotificationsTable.id].value,
            userId = userId,
            userName = user.displayName(),
            senderId = senderId,
            senderName = sender.displayName(),
            type = this[NotificationsTable.type].name,
            targetType = this[NotificationsTable.targetType]?.name,
            targetId = this[NotificationsTable.targetId],
            title = this[NotificationsTable.title],
            body = this[NotificationsTable.body],
            isRead = this[NotificationsTable.isRead],
            createdAt = this[NotificationsTable.createdAt].toString()
        )
    }

    private fun ResultRow?.displayName(): String {
        return this?.get(UsersTable.fullName)?.takeIf { it.isNotBlank() }
            ?: this?.get(UsersTable.username)
            ?: "Không rõ"
    }

    private fun getPhotographerStats(userId: Long): PhotographerStats {
        val portfolio = PortfoliosTable.selectAll().where { PortfoliosTable.userId eq userId }.singleOrNull()
        val specialties = portfolio?.get(PortfoliosTable.specialties)
        val ratingAvg = portfolio?.get(PortfoliosTable.ratingAvg)?.toDouble() ?: 0.0
        val isFeatured = portfolio?.get(PortfoliosTable.isFeatured) ?: false

        val bookingsCount = BookingsTable.selectAll().where { BookingsTable.photographerId eq userId }.count()
        val sumPrice = BookingsTable.price.sum()
        val revenueRow = BookingsTable
            .select(sumPrice)
            .where { (BookingsTable.photographerId eq userId) and (BookingsTable.status eq BookingStatus.COMPLETED) }
            .singleOrNull()
        val revenue = revenueRow?.get(sumPrice)?.toDouble() ?: 0.0

        return PhotographerStats(specialties, ratingAvg, bookingsCount, revenue, isFeatured)
    }

    suspend fun logActivity(
        userId: Long?,
        action: String,
        targetType: com.instagallery.models.common.ActivityTargetType,
        targetId: Long?,
        ipAddress: String?,
        userAgent: String?,
        metadata: String? = null
    ): Unit = dbQuery {
        ActivityLogsTable.insert {
            it[ActivityLogsTable.userId] = userId
            it[ActivityLogsTable.action] = action
            it[ActivityLogsTable.targetType] = targetType
            it[ActivityLogsTable.targetId] = targetId
            it[ActivityLogsTable.ipAddress] = ipAddress
            it[ActivityLogsTable.userAgent] = userAgent
            it[ActivityLogsTable.metadata] = metadata
        }
    }

    suspend fun getActivityLogs(
        query: String?,
        page: Int,
        limit: Int
    ): com.instagallery.models.common.AdminActivityLogsResponse = dbQuery {
        val offset = ((page - 1) * limit).toLong()
        
        var selectQuery: Query = ActivityLogsTable.selectAll()
        
        if (!query.isNullOrBlank()) {
            selectQuery = selectQuery.where {
                ActivityLogsTable.action like "%$query%"
            }
        }
        
        val total = selectQuery.count()
        
        val logs = selectQuery
            .orderBy(ActivityLogsTable.createdAt to SortOrder.DESC)
            .limit(limit, offset = offset)
            .map { row ->
                val actorId = row[ActivityLogsTable.userId]?.value
                val actor = actorId?.let { id ->
                    UsersTable.selectAll().where { UsersTable.id eq id }.singleOrNull()
                }
                com.instagallery.models.common.AdminActivityLogDto(
                    id = row[ActivityLogsTable.id].value,
                    userId = actorId,
                    actorName = actor.displayName(),
                    action = row[ActivityLogsTable.action],
                    targetType = row[ActivityLogsTable.targetType].name,
                    targetId = row[ActivityLogsTable.targetId],
                    ipAddress = row[ActivityLogsTable.ipAddress],
                    userAgent = row[ActivityLogsTable.userAgent],
                    metadata = row[ActivityLogsTable.metadata],
                    createdAt = row[ActivityLogsTable.createdAt].toString()
                )
            }
            
        com.instagallery.models.common.AdminActivityLogsResponse(logs, total, page, limit)
    }

    private data class PhotographerStats(
        val specialties: String?,
        val ratingAvg: Double,
        val bookingsCount: Long,
        val revenue: Double,
        val isFeatured: Boolean
    )
}
