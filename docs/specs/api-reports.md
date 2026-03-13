# API Đặc Tả: Hệ Thống Báo Cáo (Reports API)

*(Tính năng Moderation dành cho Admin và Người dùng - Phase 4)*

## 1. Gửi báo cáo (Tạo Report)
- **Cụm:** `Moderation`
- **Endpoint:** `POST /api/v1/reports`
- **Access:** Bắt buộc Token (Tất cả User)
- **Mô tả:** Người dùng có thể báo cáo User khác, Bài viết, hoặc Bình luận vi phạm tiêu chuẩn cộng đồng.

**Request Body (`CreateReportRequest`):**
```json
{
  "targetType": "POST", // USER, POST, COMMENT
  "targetId": 105,
  "reason": "SPAM", // SPAM, INAPPROPRIATE, HARASSMENT, COPYRIGHT, OTHER
  "description": "Bài viết chứa nội dung rác lặp đi lặp lại."
}
```

**Response Thành Công (201 Created):**
```json
{
  "success": true,
  "message": "Cảm ơn bạn đã báo cáo. Chúng tôi sẽ xem xét trong thời gian sớm nhất."
}
```

---

## 2. Admin: Xem danh sách Report
- **Cụm:** `Moderation`
- **Endpoint:** `GET /api/v1/admin/reports`
- **Access:** Bắt buộc Token (Role: `ADMIN`)
- **Mô tả:** Lấy danh sách các reports đang chờ duyệt (`PENDING`), đã giải quyết (`RESOLVED`), hoặc bị từ chối (`REJECTED`).

**Query Parameters:**
- `status` (String, Optional): Trạng thái để lọc (VD: `PENDING`).
- `page` (Int): Trang hiện tại.
- `limit` (Int): Kích thước trang.

---

## 3. Admin: Xử lý Report
- **Cụm:** `Moderation`
- **Endpoint:** `PUT /api/v1/admin/reports/{reportId}`
- **Access:** Bắt buộc Token (Role: `ADMIN`)
- **Mô tả:** Chuyển trạng thái của report, và có thể dẫn tới hành động ẩn bài viết/khóa user (tùy thuộc vào design sau này). Ở mức cơ bản, chỉ cần đổi trạng thái thành `RESOLVED` hoặc `REJECTED`.

**Request Body (`UpdateReportStatusRequest`):**
```json
{
  "status": "RESOLVED",
  "adminNotes": "Đã kiểm tra và ẩn bài viết."
}
```
