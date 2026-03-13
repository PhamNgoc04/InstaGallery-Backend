# API Định Nghĩa: Tương Tác Bài Viết (Likes, Comments, Saves)

## 1. Thả Tim / Bỏ Thả Tim Bài Viết (Toggle Like)
- **Cụm:** `Interactions`
- **Endpoint:** `POST /api/v1/posts/{postId}/like`
- **Access:** Bắt buộc có JWT Token
- **Mô tả:** Gửi yêu cầu để Thích một bài viết. Nếu đã thích rồi, tự động "Bỏ thích" (Toggle mechanism).

**Request Body:** Không có.

**Response Thành Công (200 OK):**
```json
{
  "success": true,
  "message": "Đã thả tim bài viết", // Hoặc "Đã bỏ tim bài viết"
  "data": {
    "is_liked": true,
    "total_likes": 46 // Cập nhật realtime số Like mới
  }
}
```

**Response Lỗi (400 / 401 / 404):**
- `POST_NOT_FOUND`: Bài viết không tồn tại.

---

## 2. Viết Bình Luận (Create Comment)
- **Cụm:** `Interactions`
- **Endpoint:** `POST /api/v1/posts/{postId}/comments`
- **Access:** Bắt buộc có JWT Token
- **Mô tả:** Viết bình luận vào một bài viết. Hỗ trợ "Reply" (trả lời) bình luận bằng `parent_id`.

**Request Body (JSON):**
```json
{
  "content": "Tuyệt vời quá bạn ơi! ❤️",
  "parent_id": null // Gửi ID của một comment khác nếu là Reply
}
```

**Response Thành Công (201 Created):**
```json
{
  "success": true,
  "message": "Bình luận thành công",
  "data": {
    "comment_id": 100,
    "post_id": 500,
    "user_id": 1,
    "username": "johndoe",
    "avatar": "...",
    "content": "Tuyệt vời quá bạn ơi! ❤️",
    "created_at": "2026-03-13T10:15:00Z"
  }
}
```

**Response Lỗi (400 / 401 / 404):**
- `EMPTY_CONTENT`: Bình luận không có chữ.
- `POST_NOT_FOUND`: Bài viết không có hoặc đã bị xóa.
- `PARENT_COMMENT_NOT_FOUND`: ID bình luận đang Reply không tồn tại.

---

## 3. Lấy Danh Sách Bình Luận (Get Comments)
- **Cụm:** `Interactions`
- **Endpoint:** `GET /api/v1/posts/{postId}/comments`
- **Access:** Bắt buộc JWT Token (Có thể mở Public nếu cần)
- **Mô tả:** Lấy danh sách bình luận Root (parent_id = null) của bài viết. Có phân trang.

**Query Parameters:**
- `page` (Mặc định 1)
- `limit` (Mặc định 20)
- *(Tùy chọn tương lai: `parent_id` để lấy danh sách reply của riêng 1 comment).*

**Response Thành Công (200 OK):**
```json
{
  "success": true,
  "data": {
    "comments": [
      {
        "comment_id": 100,
        "user_id": 1,
        "username": "johndoe",
        "avatar": "...",
        "content": "Tuyệt vời quá bạn ơi! ❤️",
        "reply_count": 2, // Số lượng câu trả lời
        "created_at": "..."
      }
    ],
    "meta": { "current_page": 1, "total_pages": 1, "has_next": false }
  }
}
```

---

## 4. Lưu / Bỏ Lưu Bài Viết (Toggle Save Post)
- **Cụm:** `Interactions`
- **Endpoint:** `POST /api/v1/posts/{postId}/save`
- **Access:** Bắt buộc JWT Token
- **Mô tả:** Đưa bài viết vào Bookmark cá nhân. Tính năng Toggle tương tự Like.

**Response Thành Công (200 OK):**
```json
{
  "success": true,
  "message": "Đã lưu bài viết vào Bookmark", // Hoặc "Đã bỏ lưu"
  "data": {
    "is_saved": true
  }
}
```
