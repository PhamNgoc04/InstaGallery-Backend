# API Đặc Tả: Tìm Kiếm & Khám Phá (Search & Explore)

*(Module dành cho chức năng Tìm kiếm Nhiếp ảnh gia, Bài viết và lưu lại lịch sử tìm kiếm)*

## 1. Tìm Kiếm Tổng Hợp (Global Search)
- **Cụm:** `Search`
- **Endpoint:** `GET /api/v1/search`
- **Access:** Public. Nếu có header `Authorization: Bearer`, server chỉ lưu lịch sử khi JWT hợp lệ và tài khoản còn active. Token giả bị bỏ qua. Kết quả bài viết chỉ gồm bài `PUBLIC` của tài khoản không riêng tư.
- **Hỗ trợ Query:**
    - `q`: Từ khóa tìm kiếm (Ví dụ: "Chụp kỷ yếu")
    - `type`: Loại Entity muốn tìm (`USERS`, `POSTS`, hoặc `ALL` mặc định).
    - `page` & `limit` cho phân trang.
- **Mô tả:** Trả về kết quả tìm kiếm đa khung. Truy vấn dùng `LIKE`. Nếu user đã đăng nhập, cùng `user_id` và cùng `query_text` chỉ một dòng lịch sử; lần sau cập nhật thời điểm tìm.

**Response Thành Công (200 OK):**
```json
{
  "success": true,
  "data": {
    "users": [
      {
        "user_id": 10,
        "full_name": "Chụp Kỷ Yếu Hà Nội",
        "username": "kyyeuhanoi",
        "avatar": "...",
        "role": "PHOTOGRAPHER"
      }
    ],
    "posts": [
      {
        "post_id": 204,
        "caption": "Trọn bộ ảnh kỷ yếu lớp 12A1...",
        "cover_image": "...",
        "like_count": 150
      }
    ]
  }
}
```

---

## 2. Lấy Lịch Sử Tìm Kiếm Gần Đây (Get Search History)
- **Cụm:** `Search`
- **Endpoint:** `GET /api/v1/search/history`
- **Access:** Bắt buộc có JWT Token
- **Mô tả:** Lấy danh sách 10 từ khóa tìm kiếm gần nhất của User.

**Response Thành Công (200 OK):**
```json
{
  "success": true,
  "data": {
    "hits": [
      "Chụp ảnh cưới",
      "Studio quận 1",
      "Kỷ yếu"
    ]
  }
}
```

---

## 3. Xóa Lịch Sử Tìm Kiếm (Clear Search History)
- **Cụm:** `Search`
- **Endpoint:** `DELETE /api/v1/search/history`
- **Access:** Bắt buộc có JWT Token
- **Mô tả:** Cho phép người dùng dọn dẹp lịch sử tìm kiếm cá nhân.

**Response Thành Công (200 OK):**
```json
{
  "success": true,
  "message": "Đã xóa toàn bộ lịch sử tìm kiếm"
}
```
