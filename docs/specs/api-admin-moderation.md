# API Đặc Tả: Quản Trị Hệ Thống (Admin Moderation)

*(Tính năng Moderation dành cho Admin - Phase 4)*

## 1. Khóa/Mở khóa Người dùng (Ban/Unban User)
- **Cụm:** `Admin` / `Moderation`
- **Endpoint:** `PUT /api/v1/admin/users/{userId}/ban`
- **Access:** Bắt buộc Token (Role: `ADMIN`)
- **Mô tả:** Thay đổi trạng thái khóa của người dùng.

**Request Body (`BanUserRequest`):**
```json
{
  "isBanned": true,
  "reason": "Vi phạm nghiêm trọng tiêu chuẩn cộng đồng."
}
```

**Response Thành Công (200 OK):**
```json
{
  "success": true,
  "message": "Đã khóa người dùng thành công."
}
```

---

## 2. Quản Trị: Xóa Bài Viết (Delete Post)
- **Cụm:** `Admin` / `Moderation`
- **Endpoint:** `DELETE /api/v1/admin/posts/{postId}`
- **Access:** Bắt buộc Token (Role: `ADMIN`)
- **Mô tả:** Xóa mềm bài viết của bất kỳ người dùng nào vi phạm.

**Response Thành Công (200 OK):**
```json
{
  "success": true,
  "message": "Bài viết đã bị xóa bởi Quản trị viên."
}
```

---

## 3. Quản Trị: Xóa Bình Luận (Delete Comment)
- **Cụm:** `Admin` / `Moderation`
- **Endpoint:** `DELETE /api/v1/admin/comments/{commentId}`
- **Access:** Bắt buộc Token (Role: `ADMIN`)
- **Mô tả:** Xóa mềm bình luận vi phạm.

**Response Thành Công (200 OK):**
```json
{
  "success": true,
  "message": "Bình luận đã bị xóa bởi Quản trị viên."
}
```
