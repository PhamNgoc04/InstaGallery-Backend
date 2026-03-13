# API Đặc Tả: Quản Lý Đa Phương Tiện Nâng Cao (Advanced Media)

*(Quản lý File & Ảnh theo chuẩn Production - Phase 3)*

## 1. Yêu cầu Presigned URL Upload (Get Upload URL)
- **Cụm:** `Media`
- **Endpoint:** `POST /api/v1/media/presigned-url`
- **Access:** Chủ sở hữu (Cần Token)
- **Mô tả:** Thay vì upload trực tiếp file vào Backend làm nghẽn cổ chai, App gọi API này báo cáo "tôi chuẩn bị up 1 file jpeg 2MB". Backend sẽ generate 1 URL (S3 Presigned) có hạn 15 phút trả về. App tự dùng URL đó ném HTTP PUT lên thẳng S3.

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
    "uploadUrl": "https://s3.aws.../posts/hash.jpg?Signature=...",
    "fileUrl": "https://cdn.instagallery.com/posts/hash.jpg",
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
