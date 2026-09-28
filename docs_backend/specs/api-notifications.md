# API Đặc Tả: Thông Báo (Notifications)

*(Quản lý hệ thống chuông báo (Bell) trên thanh điều hướng góc phải màn hình)*

## 1. Lấy danh sách Thông báo của tôi (Get My Notifications)
- **Cụm:** `Notifications`
- **Endpoint:** `GET /api/v1/notifications`
- **Access:** Bắt buộc có JWT Token
- **Hỗ trợ Query:** `page` (Mặc định 1), `limit` (Mặc định 20)
- **Mô tả:** Lấy danh sách các thông báo mới nhất, sắp xếp theo thời gian giảm dần. Cấu trúc trả về hỗ trợ đa dạng loại hành động (New Like, New Comment, Booking Request...).

**Response Thành Công (200 OK):**
```json
{
  "success": true,
  "data": {
    "notifications": [
      {
        "notification_id": 50,
        "type": "NEW_LIKE", 
        "sender_id": 12,
        "sender_name": "Nguyen Khach",
        "sender_avatar": "https://s3...avatar.jpg",
        "title": "Lượt thích mới",
        "body": "Nguyen Khach đã thích bài viết của bạn.",
        "target_type": "POST",
        "target_id": 200,   // Frontend dùng ID này đễ làm redirect link 
        "is_read": false,
        "created_at": "2026-05-20T10:05:00Z"
      }
    ],
    "meta": {
      "current_page": 1,
      "total_pages": 3,
      "unread_count": 5 // Tiện cho Frontend hiển thị số đo đỏ trên chuông báo
    }
  }
}
```

---

## 2. Đánh dấu Đã Đọc (Mark as Read)
- **Cụm:** `Notifications`
- **Endpoint:** `PUT /api/v1/notifications/{id}/read`
- **Access:** Bắt buộc có JWT Token
- **Mô tả:** Đánh dấu một thông báo cụ thể là đã đọc.

**Response Thành Công (200 OK):**
```json
{
  "success": true,
  "message": "Đã đánh dấu thông báo là đã đọc"
}
```

---

## 3. Đánh dấu Tất Cả Đã Đọc (Mark All as Read)
- **Cụm:** `Notifications`
- **Endpoint:** `PUT /api/v1/notifications/read-all`
- **Access:** Bắt buộc có JWT Token
- **Mô tả:** Đánh dấu toàn bộ thông báo của người dùng là đã đọc.

**Response Thành Công (200 OK):**
```json
{
  "success": true,
  "message": "Đã đánh dấu tất cả thông báo là đã đọc"
}
```
