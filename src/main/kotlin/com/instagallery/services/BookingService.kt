package com.instagallery.services

import com.instagallery.database.tables.BookingsTable
import com.instagallery.models.common.BookingDto
import com.instagallery.models.common.BookingPackageSnapshotDto
import com.instagallery.models.common.BookingStatus
import com.instagallery.models.common.NotificationType
import com.instagallery.models.common.PaginatedBookingsResponse
import com.instagallery.models.common.PhotographerServiceDto
import com.instagallery.models.common.RatingDto
import com.instagallery.models.request.CreateBookingRequest
import com.instagallery.models.request.CreateRatingRequest
import com.instagallery.models.request.UpdateBookingStatusRequest
import com.instagallery.plugins.AuthException
import com.instagallery.plugins.ValidationException
import com.instagallery.repositories.BookingCreateData
import com.instagallery.repositories.BookingRepository
import com.instagallery.repositories.PhotographerServiceRepository
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDateTime
import java.time.format.DateTimeParseException
import kotlin.math.roundToInt

class BookingService : KoinComponent {
    private val bookingRepo: BookingRepository by inject()
    private val photographerServiceRepo: PhotographerServiceRepository by inject()
    private val ratingService: RatingService by inject()
    private val notificationService: NotificationService by inject()

    suspend fun createBooking(clientId: Long, request: CreateBookingRequest): BookingDto {
        if (clientId == request.photographerId) {
            throw ValidationException("INVALID_SELF_BOOKING", "Ban khong the tu dat lich cho chinh minh.")
        }

        val photoExists = bookingRepo.checkPhotographerExists(request.photographerId)
        if (!photoExists) {
            throw AuthException("PHOTOGRAPHER_NOT_FOUND", "Nhiep anh gia khong ton tai.")
        }

        val parsedDate = parseBookingDate(request.bookingDate)
        if (parsedDate.isBefore(LocalDateTime.now())) {
            throw ValidationException("INVALID_DATE", "Thoi gian dat lich khong hop le (khong duoc trong qua khu).")
        }

        val selectedService = request.serviceId?.let { serviceId ->
            photographerServiceRepo.getOwnedById(request.photographerId, serviceId)
                ?.takeIf { it.isActive }
                ?: throw ValidationException("SERVICE_NOT_AVAILABLE", "Goi chup khong ton tai hoac dang tam dung.")
        }

        val packageSnapshot = buildPackageSnapshot(request, selectedService)
        val durationMinutes = resolveDurationMinutes(request, selectedService)
        val durationHours = durationMinutes / 60.0

        if (!bookingRepo.isWithinAvailability(request.photographerId, parsedDate, durationMinutes)) {
            throw ValidationException("BOOKING_OUTSIDE_AVAILABILITY", "Khung gio nay nam ngoai lich lam viec cua nhiep anh gia.")
        }

        if (bookingRepo.hasScheduleConflict(request.photographerId, parsedDate, durationMinutes)) {
            throw ValidationException("BOOKING_SLOT_UNAVAILABLE", "Khung gio nay da co lich dat khac.")
        }

        val newId = bookingRepo.createBooking(
            BookingCreateData(
                clientId = clientId,
                photographerId = request.photographerId,
                serviceId = selectedService?.serviceId ?: request.serviceId,
                packageName = packageSnapshot.name,
                packageSnapshotJson = bookingJson.encodeToString(packageSnapshot),
                shootingType = normalizeText(request.shootingType),
                sceneType = normalizeText(request.sceneType),
                bookingDate = parsedDate,
                durationHours = durationHours,
                locationBooking = normalizeText(request.locationBooking),
                addressDetail = normalizeText(request.addressDetail),
                details = normalizeText(request.details),
                peopleCount = request.peopleCount?.coerceIn(1, 500),
                contactPhone = normalizeText(request.contactPhone),
                addOns = request.addOns.mapNotNull(::normalizeText),
                referenceImages = request.referenceImages.mapNotNull(::normalizeText),
                price = selectedService?.price ?: request.price,
                currency = (selectedService?.currency ?: request.currency).uppercase(),
            ),
        )

        notificationService.notifyBookingCreated(request.photographerId, clientId, newId)

        return bookingRepo.getBookingDetail(clientId, newId)
            ?: throw AuthException("BOOKING_NOT_FOUND", "Khong the tai booking vua tao.")
    }

    suspend fun getMyBookings(userId: Long, page: Int, limit: Int, status: String?): PaginatedBookingsResponse {
        val verifiedPage = if (page < 1) 1 else page
        val verifiedLimit = if (limit < 1) 10 else if (limit > 50) 50 else limit

        val enumStatus = status?.let {
            try {
                BookingStatus.valueOf(it.uppercase())
            } catch (e: Exception) {
                throw ValidationException("INVALID_STATUS", "Trang thai filter khong hop le.")
            }
        }

        return bookingRepo.getBookingsList(userId, verifiedPage, verifiedLimit, enumStatus)
    }

    suspend fun updateBookingStatus(userId: Long, bookingId: Long, request: UpdateBookingStatusRequest) {
        val bookingRow = bookingRepo.getBookingById(bookingId)
            ?: throw AuthException("BOOKING_NOT_FOUND", "Don dat lich khong ton tai.")

        val clientId = bookingRow[BookingsTable.clientId].value
        val photoId = bookingRow[BookingsTable.photographerId].value
        val currentStatus = bookingRow[BookingsTable.status]

        if (userId != clientId && userId != photoId) {
            throw AuthException("UNAUTHORIZED_ACTION", "Ban khong co quyen can thiep vao don nay.")
        }

        validateStatusTransition(
            currentStatus = currentStatus,
            nextStatus = request.status,
            actorUserId = userId,
            clientId = clientId,
            photographerId = photoId,
            reason = request.cancellationReason,
        )

        bookingRepo.updateBookingStatus(bookingId, request.status, request.cancellationReason)
        val recipientId = if (userId == clientId) photoId else clientId
        notificationService.notifyBookingStatusChanged(
            recipientUserId = recipientId,
            actorUserId = userId,
            bookingId = bookingId,
            statusLabel = request.status.viLabel(),
            type = request.status.toNotificationType(),
        )
    }

    suspend fun getBookingDetail(userId: Long, bookingId: Long): BookingDto {
        return bookingRepo.getBookingDetail(userId, bookingId)
            ?: throw AuthException("BOOKING_NOT_FOUND", "Don dat lich khong ton tai hoac ban khong co quyen xem.")
    }

    suspend fun cancelBooking(userId: Long, bookingId: Long) {
        val bookingRow = bookingRepo.getBookingById(bookingId)
            ?: throw AuthException("BOOKING_NOT_FOUND", "Don dat lich khong ton tai.")

        val clientId = bookingRow[BookingsTable.clientId].value
        val currentStatus = bookingRow[BookingsTable.status]

        if (userId != clientId) {
            throw AuthException("UNAUTHORIZED_ACTION", "Chi khach hang moi co the huy don.")
        }

        validateStatusTransition(
            currentStatus = currentStatus,
            nextStatus = BookingStatus.CANCELLED,
            actorUserId = userId,
            clientId = clientId,
            photographerId = bookingRow[BookingsTable.photographerId].value,
            reason = "Khach hang tu huy",
        )

        bookingRepo.updateBookingStatus(bookingId, BookingStatus.CANCELLED, "Khach hang tu huy")
        notificationService.notifyBookingStatusChanged(
            recipientUserId = bookingRow[BookingsTable.photographerId].value,
            actorUserId = userId,
            bookingId = bookingId,
            statusLabel = BookingStatus.CANCELLED.viLabel(),
            type = NotificationType.BOOKING_CANCELLED,
        )
    }

    suspend fun createReview(userId: Long, bookingId: Long, request: CreateRatingRequest): RatingDto {
        val bookingRow = bookingRepo.getBookingById(bookingId)
            ?: throw AuthException("BOOKING_NOT_FOUND", "Don dat lich khong ton tai.")

        val clientId = bookingRow[BookingsTable.clientId].value
        val photographerId = bookingRow[BookingsTable.photographerId].value

        if (userId != clientId) {
            throw AuthException("UNAUTHORIZED_ACTION", "Chi khach hang cua booking moi co the danh gia.")
        }

        val rating = ratingService.createRating(
            bookingId = bookingId,
            raterId = userId,
            rateeId = photographerId,
            request = request,
        )
        notificationService.createNotification(
            recipientUserId = photographerId,
            actorUserId = userId,
            type = NotificationType.REVIEW_RECEIVED,
            targetType = com.instagallery.models.common.NotificationTargetType.BOOKING,
            targetId = bookingId,
            title = "Đánh giá mới",
            body = "Khách hàng đã gửi đánh giá cho booking đã hoàn thành.",
            dedupe = true,
        )
        return rating
    }

    private fun validateStatusTransition(
        currentStatus: BookingStatus,
        nextStatus: BookingStatus,
        actorUserId: Long,
        clientId: Long,
        photographerId: Long,
        reason: String?,
    ) {
        if (currentStatus in terminalStatuses) {
            throw ValidationException("INVALID_STATE_TRANSITION", "Khong the thay doi don da ket thuc.")
        }
        if (nextStatus == BookingStatus.CANCELLED && reason.isNullOrBlank()) {
            throw ValidationException("MISSING_REASON", "Vui long nhap ly do huy don.")
        }
        if (nextStatus == BookingStatus.REJECTED && actorUserId != photographerId) {
            throw AuthException("UNAUTHORIZED_ACTION", "Chi nhiep anh gia moi co quyen tu choi booking.")
        }
        if (nextStatus in photographerOnlyStatuses && actorUserId != photographerId) {
            throw AuthException("UNAUTHORIZED_ACTION", "Chi nhiep anh gia moi co quyen cap nhat trang thai nay.")
        }
        if (nextStatus == BookingStatus.CANCELLED && actorUserId != clientId && actorUserId != photographerId) {
            throw AuthException("UNAUTHORIZED_ACTION", "Ban khong co quyen huy booking nay.")
        }

        val allowed = when (currentStatus) {
            BookingStatus.PENDING -> nextStatus in listOf(BookingStatus.CONFIRMED, BookingStatus.CANCELLED, BookingStatus.REJECTED)
            BookingStatus.CONFIRMED -> nextStatus in listOf(BookingStatus.IN_PROGRESS, BookingStatus.CANCELLED)
            BookingStatus.IN_PROGRESS -> nextStatus in listOf(BookingStatus.COMPLETED, BookingStatus.CANCELLED)
            BookingStatus.COMPLETED,
            BookingStatus.CANCELLED,
            BookingStatus.REJECTED -> false
        }
        if (!allowed) {
            throw ValidationException("INVALID_STATE_TRANSITION", "Trang thai booking khong hop le.")
        }
    }

    private fun parseBookingDate(rawDate: String): LocalDateTime {
        return try {
            LocalDateTime.parse(rawDate.replace("Z", ""))
        } catch (e: DateTimeParseException) {
            throw ValidationException("INVALID_DATE_FORMAT", "Dinh dang ngay booking phai la ISO 8601 (VD: 2026-05-20T09:00:00).")
        }
    }

    private fun buildPackageSnapshot(
        request: CreateBookingRequest,
        selectedService: PhotographerServiceDto?,
    ): BookingPackageSnapshotDto {
        if (selectedService != null) {
            return BookingPackageSnapshotDto(
                serviceId = selectedService.serviceId,
                name = selectedService.name,
                category = selectedService.category,
                price = selectedService.price,
                currency = selectedService.currency,
                durationMinutes = selectedService.durationMinutes,
                photoCount = selectedService.photoCount,
                editedPhotoCount = selectedService.editedPhotoCount,
                includes = selectedService.includes,
            )
        }

        val packageName = normalizeText(request.packageName)
            ?: normalizeText(request.shootingType)
            ?: "Custom Booking"
        return BookingPackageSnapshotDto(
            serviceId = request.serviceId,
            name = packageName,
            category = normalizeText(request.shootingType),
            price = request.price,
            currency = request.currency,
            durationMinutes = resolveDurationMinutes(request, null),
            includes = request.addOns,
        )
    }

    private fun resolveDurationMinutes(
        request: CreateBookingRequest,
        selectedService: PhotographerServiceDto?,
    ): Int {
        selectedService?.durationMinutes?.let { return it.coerceAtLeast(MIN_DURATION_MINUTES) }
        return request.durationHours
            ?.let { (it * 60).roundToInt() }
            ?.coerceAtLeast(MIN_DURATION_MINUTES)
            ?: BookingRepository.DEFAULT_DURATION_MINUTES
    }

    private fun normalizeText(value: String?): String? {
        return value?.trim()?.takeIf { it.isNotBlank() }
    }

    private fun BookingStatus.viLabel(): String {
        return when (this) {
            BookingStatus.PENDING -> "chờ xác nhận"
            BookingStatus.CONFIRMED -> "đã xác nhận"
            BookingStatus.IN_PROGRESS -> "đang chụp"
            BookingStatus.COMPLETED -> "hoàn thành"
            BookingStatus.CANCELLED -> "đã hủy"
            BookingStatus.REJECTED -> "bị từ chối"
        }
    }

    private fun BookingStatus.toNotificationType(): NotificationType {
        return when (this) {
            BookingStatus.CONFIRMED -> NotificationType.BOOKING_CONFIRMED
            BookingStatus.IN_PROGRESS -> NotificationType.BOOKING_IN_PROGRESS
            BookingStatus.COMPLETED -> NotificationType.BOOKING_COMPLETED
            BookingStatus.CANCELLED -> NotificationType.BOOKING_CANCELLED
            BookingStatus.REJECTED -> NotificationType.BOOKING_REJECTED
            BookingStatus.PENDING -> NotificationType.BOOKING_REQUEST
        }
    }

    private companion object {
        const val MIN_DURATION_MINUTES = 30
        val bookingJson = Json { ignoreUnknownKeys = true }
        val terminalStatuses = setOf(BookingStatus.COMPLETED, BookingStatus.CANCELLED, BookingStatus.REJECTED)
        val photographerOnlyStatuses = setOf(
            BookingStatus.CONFIRMED,
            BookingStatus.IN_PROGRESS,
            BookingStatus.COMPLETED,
            BookingStatus.REJECTED,
        )
    }
}
