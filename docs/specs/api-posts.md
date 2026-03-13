# API Định Nghĩa: Quản Lý Bài Viết (Posts & Media)

## 1. Tạo Bài Viết Mới (Create Post)
- **Cụm:** `Posts`
- **Endpoint:** `POST /api/v1/posts`
- **Access:** Bắt buộc có JWT Token
- **Mô tả:** Đăng một bài viết mới kèm theo danh sách ID của các Media đã được upload trước đó.

**Request Body (JSON):**
```json
{
  "caption": "Chuyến đi Đà Lạt tuyệt vời! 🌲📸",
  "location": "Da Lat, Lam Dong",
  "visibility": "PUBLIC", // Từ Enum PostVisibility: PUBLIC, FOLLOWERS_ONLY, PRIVATE
  "media_ids": [101, 102], // Bắt buộc phải có ít nhất 1 ảnh/video
  "tags": ["dalat", "travel", "photography"] // Tùy chọn
}
```

**Response Thành Công (201 Created):**
```json
{
  "success": true,
  "message": "Đăng bài viết thành công",
  "data": {
    "post_id": 500,
    "user_id": 1,
    "caption": "Chuyến đi Đà Lạt tuyệt vời! 🌲📸",
    "createdAt": "2026-03-13T10:00:00Z",
    "media": [
      {
        "id": 101,
        "url": "https://s3.aws.com/ig/posts/img1.jpg",
        "type": "IMAGE",
        "order": 1
      }
    ]
  }
}
```

**Response Lỗi (400 / 401 / 404):**
- `EMPTY_MEDIA`: Không có `media_ids` nào.
- `MEDIA_NOT_FOUND`: Một trong các ID Media truyền lên không tồn tại hoặc không thuộc quyền sở hữu của User này.

---

## 2. Lấy Bảng Tin (Get Feed)
- **Cụm:** `Posts`
- **Endpoint:** `GET /api/v1/posts/feed`
- **Access:** Bắt buộc có JWT Token
- **Mô tả:** Lấy danh sách bài viết từ những người dùng mà tài khoản đang theo dõi (Followings), sắp xếp mới nhất lên đầu. Có hỗ trợ phân trang (Pagination).

**Query Parameters:**
- `page` (Int, Mặc định: 1)
- `limit` (Int, Mặc định: 10, Tối đa: 50)

**Response Thành Công (200 OK):**
```json
{
  "success": true,
  "data": {
    "posts": [
      {
        "post_id": 499,
        "user_id": 2,
        "username": "jane_travel",
        "user_avatar": "...",
        "caption": "Bình minh trên biển",
        "like_count": 45,
        "comment_count": 12,
        "media": [ /* Danh sách Media Url */ ],
        "created_at": "..."
      }
    ],
    "meta": {
      "current_page": 1,
      "total_pages": 5,
      "has_next": true
    }
  }
}
```

---

## 3. Xóa Bài Viết (Delete Post)
- **Cụm:** `Posts`
- **Endpoint:** `DELETE /api/v1/posts/{id}`
- **Access:** Bắt buộc có JWT Token
- **Mô tả:** Xóa một bài viết dựa trên ID. Chỉ Chủ sở hữu đoạn Post hoặc ADMIN mới có quyền xóa. 
*(Lưu ý: Thiết kế database dùng `deleted_at` nên đây là Soft Delete. Đánh dấu xóa nhưng vẫn còn dữ liệu log ở DB).*

**Response Thành Công (200 OK):**
```json
{
  "success": true,
  "message": "Đã xóa bài viết an toàn"
}
```

**Response Lỗi (401 / 403 / 404):**
- `POST_NOT_FOUND`: Bài viết không tồn tại.
- `FORBIDDEN_ACTION`: Không có quyền xóa bài viết của người khác.
