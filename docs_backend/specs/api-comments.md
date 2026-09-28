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

## 2. Xóa bình luận (Delete Comment)
- **Cụm:** `Interaction` / `Comment`
- **Endpoint:** `DELETE /api/v1/comments/{commentId}`
- **Access:** Bắt buộc Token (Chủ bình luận)
- **Mô tả:** Xóa một bình luận đã đăng.

**Response Thành Công (200 OK):**
```json
{
  "success": true,
  "data": null,
  "message": "Đã xóa bình luận."
}
```

---

## 3. Thích & Bỏ thích bình luận (Like Comment)
- **Cụm:** `Interaction` / `Comment`
- **Endpoint:** `POST /api/v1/comments/{commentId}/like`
- **Access:** Bắt buộc Token
- **Mô tả:** Thả tim cho một bình luận. Dữ liệu nằm ở `comment_reactions`, một dòng cho mỗi user và comment. Nếu đã like thì xóa dòng. Nếu đang dislike thì đổi thành like.

**Response Thành Công (200 OK):**
```json
{
  "success": true,
  "data": {
    "commentId": 45,
    "isLiked": true,
    "isDisliked": false,
    "likeCount": 12,
    "dislikeCount": 1
  },
  "message": "Đã thích bình luận."
}
```

---

## 4. Không thích & Bỏ không thích bình luận (Dislike Comment)
- **Cụm:** `Interaction` / `Comment`
- **Endpoint:** `POST /api/v1/comments/{commentId}/dislike`
- **Access:** Bắt buộc Token
- **Mô tả:** Bấm không thích (Dislike) một bình luận. Nếu đã dislike thì toggle hủy dislike. Nếu đang like thì chuyển sang dislike.

**Response Thành Công (200 OK):**
```json
{
  "success": true,
  "data": {
    "commentId": 45,
    "isLiked": false,
    "isDisliked": true,
    "likeCount": 11,
    "dislikeCount": 2
  },
  "message": "Disliked comment."
}
```

---

## 5. Quản lý Nested Comments (Reply to Comment)
- **Cụm:** `Interaction` / `Comment`
- **Tình trạng:** Hệ thống DB (`CommentsTable`) hỗ trợ `parent_id`, cho phép bình luận đa cấp bậc (cha-con).
- **Endpoint 1:** `POST /api/v1/posts/{postId}/comments` (Truyền `parentId` vào body `CreateCommentRequest`)
- **Endpoint 2:** `GET /api/v1/posts/{postId}/comments` (Lấy danh sách bình luận kèm replies phân cấp và trạng thái `isLiked`/`isDisliked` của người dùng hiện tại)

