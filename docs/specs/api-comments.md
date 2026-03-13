# API Đặc Tả: Tương Tác Bình Luận (Nested Comments & Comment Likes)

*(Hoàn thiện tương tác MXH - Phase 3)*

## 1. Sửa bình luận (Edit Comment)
- **Cụm:** `Interaction` / `Comment`
- **Endpoint:** `PUT /api/v1/comments/{commentId}`
- **Access:** Bắt buộc Token (Chủ bình luận)
- **Mô tả:** Cập nhật nội dung của một bình luận đã có.

**Request Body (`UpdateCommentRequest`):**
```json
{
  "content": "Nội dung bình luận đã được sửa."
}
```

**Response Thành Công (200 OK):**
```json
{
  "success": true,
  "data": {
    "id": 45,
    "postId": 105,
    "userId": 5,
    "content": "Nội dung bình luận đã được sửa.",
    "createdAt": "2024-10-15T10:00:00Z",
    "updatedAt": "2024-10-15T11:05:00Z"
  }
}
```

---

## 2. Thích bình luận (Like Comment)
- **Cụm:** `Interaction` / `Comment`
- **Endpoint:** `POST /api/v1/comments/{commentId}/like`
- **Access:** Bắt buộc Token
- **Mô tả:** Thả tim cho một bình luận. Nếu đã thả tim rồi mà gọi lại thì sẽ Hủy tim (Toggle).

**Response Thành Công (200 OK):**
```json
{
  "success": true,
  "message": "Đã thích bình luận." // hoặc "Đã bỏ thích bình luận."
}
```

---

## 3. Quản lý Nested Comments (Reply to Comment)
- **Cụm:** `Interaction` / `Comment`
- **Tình trạng:** Hệ thống DB (`CommentsTable`) đã hỗ trợ `parent_id` từ trước, ta chỉ việc thêm logic khi tạo Comment có truyền `parentId` và API Get list Comments theo cấp bậc.
- **Endpoint 1:** `POST /api/v1/posts/{postId}/comments` (Truyền `parentId` vào body)
- **Endpoint 2:** `GET /api/v1/posts/{postId}/comments` (Lấy bình luận gốc, và kèm theo các reply)
