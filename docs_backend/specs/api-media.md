# API Đặc Tả: Quản Lý Đa Phương Tiện Nâng Cao (Advanced Media)

*(Quản lý File & Ảnh theo chuẩn Production - Phase 3)*

## 1. Yêu cầu Presigned URL Upload (Get Upload URL)
- **Cụm:** `Media`
- **Endpoint:** `POST /api/v1/media/presigned-url`
- **Access:** Chủ sở hữu (Cần Token)
- **Mô tả:** App báo tên file sắp upload. Backend cấp một URL local có hạn 15 phút, ví dụ `http://localhost:8080/api/v1/media/local-upload/posts/<uuid>.jpg`. App phải `PUT` đúng URL đó và gửi cùng access token. Tên file không được tự đặt. File tối đa 50MB, chỉ nhận `jpg`, `jpeg`, `png`, `mp4`, `mov`.

**Request Body (`PresignedUrlRequest`):**
```json
{
  "fileName": "photo_01.jpg",
  "contentType": "image/jpeg",
  "folder": "posts" // posts, avatars, portfolios
}
```

**Response Thành Công (200 OK):**
```json
{
  "success": true,
  "data": {
    "uploadUrl": "http://localhost:8080/api/v1/media/local-upload/posts/<uuid>.jpg",
    "fileUrl": "http://localhost:8080/api/v1/media/local-files/posts/<uuid>.jpg",
    "expiresIn": 900
  }
}
```

---

## 2. Thêm Media vào Post (Add Media to Post)
- **Cụm:** `Media`
- **Endpoint:** `POST /api/v1/posts/{postId}/media`
- **Access:** Bắt buộc Token (Chỉ chủ bài báo mới được phép)
- **Mô tả:** Push 1 CDN link mới vừa upload lên S3 vào thành phần của Bài viết. Nếu ảnh là Video, cần truyền tham số `fileType=VIDEO`.

---

## 3. Xóa Media khỏi Post (Delete Post Media)
- **Cụm:** `Media`
- **Endpoint:** `DELETE /api/v1/posts/media/{mediaId}`
- **Access:** Token Chủ Bài Viết

---

## 4. Sắp xếp lại thứ tự (Reorder Media)
- **Cụm:** `Media`
- **Endpoint:** `PUT /api/v1/posts/{postId}/media/reorder`
- **Mô tả:** Truyền lại nguyên một mảng ID theo thứ tự tự do để Backend cập nhật `sort_order`.
