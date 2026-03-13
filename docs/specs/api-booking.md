# API Định Nghĩa: Đặt Lịch (Bookings)

*(Module dành cho tính năng khách hàng `CLIENT` đặt lịch chụp với nhiếp ảnh gia `PHOTOGRAPHER`)*

## 1. Tạo Đơn Đặt Lịch (Create Booking Request)
- **Cụm:** `Bookings`
- **Endpoint:** `POST /api/v1/bookings`
- **Access:** Bắt buộc có JWT Token
- **Mô tả:** Client (Người dùng cá nhân) muốn thuê một Nhiếp ảnh gia. Đơn sẽ rơi vào trạng thái `PENDING`.

**Quy Trình Kiểm Tra Ngầm (Validation):**
- System tự check ID Nhiếp ảnh gia có tồn tại không.
- Nhiếp ảnh gia có bị treo tài khoản không?
- Client không thể tự đặt lịch chính mình (`clientId` khác `photographerId`).

**Request Body (JSON):**
```json
{
  "photographer_id": 2,
  "booking_date": "2026-05-20T09:00:00Z", // Chuẩn ISO 8601
  "duration_hours": 4.5,
  "location_booking": "Studio ABC, Quận 1",
  "details": "Chụp ngoại cảnh áo dài truyền thống",
  "price": 2500000.0, // Thỏa thuận trước (Nếu có)
  "currency": "VND"
}
```

**Response Thành Công (201 Created):**
```json
{
  "success": true,
  "message": "Đã gửi yêu cầu đặt lịch thành công",
  "data": {
    "booking_id": 100,
    "client_id": 1,
    "photographer_id": 2,
    "status": "PENDING", 
    "booking_date": "...",
    "created_at": "..."
  }
}
```

**Response Lỗi (400 / 401 / 404):**
- `PHOTOGRAPHER_NOT_FOUND`: Nhiếp ảnh gia không tồn tại.
- `INVALID_SELF_BOOKING`: Không thể tự đặt lịch mình.
- `INVALID_DATE`: Ngày đặt lịch không hợp lệ (ngày quá khứ).

---

## 2. Lấy Danh Sách Đơn Đặt Lịch Của Tôi (Get My Bookings)
- **Cụm:** `Bookings`
- **Endpoint:** `GET /api/v1/bookings`
- **Access:** Bắt buộc có JWT Token
- **Mô tả:** Lấy danh sách Booking. API này cực kỳ thông minh, tự động trả về:
    - Nếu Login dưới quyền `CLIENT`, trả về đơn đi thuê.
    - Nếu Login dưới quyền `PHOTOGRAPHER`, trả về đơn có khách thuê mình.
- Hỗ trợ phân trang và Lọc theo Trạng Thái (Status filter).

**Query Parameters:**
- `page` (Mặc định 1)
- `limit` (Mặc định 10)
- `status` (Tùy chọn lọc: PENDING, CONFIRMED, COMPLETED, CANCELLED)

**Response Thành Công (200 OK):**
```json
{
  "success": true,
  "data": {
    "bookings": [
      {
        "booking_id": 100,
        "partner_id": 2, // ID của đầu dây bên kia (Photo/Client)
        "partner_name": "Nguyen Van A",
        "booking_date": "...",
        "status": "PENDING",
        "price": 2500000.0,
        "currency": "VND"
      }
    ],
    "meta": { "current_page": 1, "total_pages": 4, "has_next": true }
  }
}
```

---

## 3. Cập Nhật Trạng Thái Đơn - Chấp nhận / Từ chối (Update Status)
- **Cụm:** `Bookings`
- **Endpoint:** `PUT /api/v1/bookings/{bookingId}/status`
- **Access:** Bắt buộc có JWT Token
- **Mô tả:** Thay đổi luồng State Machine (Dòng đời của Đơn đặt lịch). Vd: Photo chấp nhận -> `CONFIRMED`. Khách hủy -> `CANCELLED`.

**Request Body (JSON):**
```json
{
  "status": "CONFIRMED", // Từ giá trị: CONFIRMED, CANCELLED, COMPLETED
  "cancellation_reason": null // Bắt buộc nhập nếu status là CANCELLED
}
```

**Response Thành Công (200 OK):**
```json
{
  "success": true,
  "message": "Trạng thái đơn hàng đã được cập nhật thành CONFIRMED"
}
```

**Response Lỗi (400 / 403 / 404):**
- `BOOKING_NOT_FOUND`: Đơn không tồn tại.
- `UNAUTHORIZED_ACTION`: Cố tình sửa đơn của người khác.
- `INVALID_STATE_TRANSITION`: Đơn đã `CANCELLED` thì không thể `CONFIRMED` lại được (Logic State Machine chặn).
- `MISSING_REASON`: Hủy đơn (`CANCELLED`) nhưng không truyền lý do.
