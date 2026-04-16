package com.instagallery.services

import com.instagallery.database.tables.BookingsTable
import com.instagallery.models.common.BookingDto
import com.instagallery.models.common.BookingStatus
import com.instagallery.models.common.PaginatedBookingsResponse
import com.instagallery.models.request.CreateBookingRequest
import com.instagallery.models.request.UpdateBookingStatusRequest
import com.instagallery.plugins.AuthException
import com.instagallery.plugins.ValidationException
import com.instagallery.repositories.BookingRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDateTime
import java.time.format.DateTimeParseException

class BookingService : KoinComponent {
    private val bookingRepo: BookingRepository by inject()

    suspend fun createBooking(clientId: Long, request: CreateBookingRequest): BookingDto {
        if (clientId == request.photographerId) {
            throw ValidationException("INVALID_SELF_BOOKING", "Bạn không thể tự đặt lịch cho chính mình.")
        }

        val photoExists = bookingRepo.checkPhotographerExists(request.photographerId)
        if (!photoExists) {
            throw AuthException("PHOTOGRAPHER_NOT_FOUND", "Nhiếp ảnh gia không tồn tại.")
        }

        var parsedDate: LocalDateTime
        try {
            // ISO-8601 example: 2026-05-20T09:00:00
            parsedDate = LocalDateTime.parse(request.bookingDate.replace("Z", "")) 
        } catch (e: DateTimeParseException) {
            throw ValidationException("INVALID_DATE_FORMAT", "Định dạng ngày booking phải là ISO 8601 (VD: 2026-05-20T09:00:00).")
        }

        if (parsedDate.isBefore(LocalDateTime.now())) {
            throw ValidationException("INVALID_DATE", "Thời gian đặt lịch không hợp lệ (không được trong quá khứ).")
        }

        val newId = bookingRepo.createBooking(clientId, request, parsedDate)
        
        // Fetch to return a complete DTO
        // Normally you can manually build it avoiding a second DB hit, but fetching is safer.
        val res = bookingRepo.getBookingsList(clientId, 1, 1, null)
        return res.bookings.first { it.bookingId == newId }
    }

    suspend fun getMyBookings(userId: Long, page: Int, limit: Int, status: String?): PaginatedBookingsResponse {
        val verifiedPage = if (page < 1) 1 else page
        val verifiedLimit = if (limit < 1) 10 else if (limit > 50) 50 else limit
        
        var enumStatus: BookingStatus? = null
        if (status != null) {
            try {
                enumStatus = BookingStatus.valueOf(status.uppercase())
            } catch (e: Exception) {
                throw ValidationException("INVALID_STATUS", "Trạng thái filter không hợp lệ.")
            }
        }

        return bookingRepo.getBookingsList(userId, verifiedPage, verifiedLimit, enumStatus)
    }

    suspend fun updateBookingStatus(userId: Long, bookingId: Long, request: UpdateBookingStatusRequest) {
        val bookingRow = bookingRepo.getBookingById(bookingId) 
            ?: throw AuthException("BOOKING_NOT_FOUND", "Đơn đặt lịch không tồn tại.")

        val clientId = bookingRow[BookingsTable.clientId].value
        val photoId = bookingRow[BookingsTable.photographerId].value
        val currentStatus = bookingRow[BookingsTable.status]

        // 1. Authorization check
        if (userId != clientId && userId != photoId) {
            throw AuthException("UNAUTHORIZED_ACTION", "Bạn không có quyền can thiệp vào đơn này.")
        }

        // 2. State Machine validations
        if (currentStatus == BookingStatus.CANCELLED) {
            throw ValidationException("INVALID_STATE_TRANSITION", "Không thể thay đổi đơn đã bị hủy.")
        }

        if (currentStatus == BookingStatus.COMPLETED) {
            throw ValidationException("INVALID_STATE_TRANSITION", "Không thể thay đổi đơn đã hoàn thành.")
        }

        if (request.status == BookingStatus.CANCELLED && request.cancellationReason.isNullOrBlank()) {
            throw ValidationException("MISSING_REASON", "Vui lòng nhập lý do hủy đơn.")
        }

        // Example Logic Ext: Only photographer can CONFIRM or mark COMPLETED. Both can CANCEL.
        if ((request.status == BookingStatus.CONFIRMED || request.status == BookingStatus.COMPLETED) && userId != photoId) {
            throw AuthException("UNAUTHORIZED_ACTION", "Chỉ Nhiếp ảnh gia mới có quyền Xác nhận/Hoàn thành chuyến chụp.")
        }

        // 3. Update execution
        bookingRepo.updateBookingStatus(bookingId, request.status, request.cancellationReason)
    }

    // --- FR-38: GET BOOKING DETAIL ---
    suspend fun getBookingDetail(userId: Long, bookingId: Long): BookingDto {
        val bookingRow = bookingRepo.getBookingById(bookingId)
            ?: throw AuthException("BOOKING_NOT_FOUND", "Đơn đặt lịch không tồn tại.")

        val clientId = bookingRow[BookingsTable.clientId].value
        val photoId = bookingRow[BookingsTable.photographerId].value

        if (userId != clientId && userId != photoId) {
            throw AuthException("UNAUTHORIZED_ACTION", "Bạn không có quyền xem đơn này.")
        }

        return bookingRepo.getBookingsList(userId, 1, 1, null)
            .bookings.first { it.bookingId == bookingId }
    }

    // --- FR-38: HỦY BOOKING ---
    suspend fun cancelBooking(userId: Long, bookingId: Long) {
        val bookingRow = bookingRepo.getBookingById(bookingId)
            ?: throw AuthException("BOOKING_NOT_FOUND", "Đơn đặt lịch không tồn tại.")

        val clientId = bookingRow[BookingsTable.clientId].value
        val currentStatus = bookingRow[BookingsTable.status]

        if (userId != clientId) {
            throw AuthException("UNAUTHORIZED_ACTION", "Chỉ khách hàng mới có thể hủy đơn.")
        }

        if (currentStatus == BookingStatus.CANCELLED || currentStatus == BookingStatus.COMPLETED) {
            throw ValidationException("INVALID_STATE", "Không thể hủy đơn đã hoàn thành hoặc đã bị hủy.")
        }

        bookingRepo.updateBookingStatus(bookingId, BookingStatus.CANCELLED, "Khách hàng tự hủy")
    }
}
