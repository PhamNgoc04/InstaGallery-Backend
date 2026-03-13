# API Đặc Tả: Khám Phá & Xu Hướng (Explore & Trending Tags)

*(Cung cấp tính năng khám phá nội dung mới mẻ dựa trên độ hot và xu hướng - Phase 3)*

## 1. Lấy danh sách Explore (Explore Feed)
- **Cụm:** `Post` / `Explore`
- **Endpoint:** `GET /api/v1/explore`
- **Access:** Công khai (Có thể dùng Token để cá nhân hóa)
- **Mô tả:** Trả về danh sách các bài viết phổ biến (nhiều Like, Comment) trên toàn hệ thống hoặc được gợi ý thuật toán.

**Query Parameters:**
- `page` (Int): Trang hiện tại (Mặc định: 1)
- `limit` (Int): Số bài trên một trang (Mặc định: 20)
- `tag` (String, Optional): Lọc bài viết theo Tag nhất định.

**Response Thành Công (200 OK):**
```json
{
  "success": true,
  "data": {
    "posts": [
      {
        "id": 105,
        "author": {
          "id": 5,
          "username": "alex_photo",
          "avatar": "https://..."
        },
        "contentUrl": "https://...",
        "likesCount": 1500,
        "commentsCount": 42
      }
    ],
    "meta": {
      "currentPage": 1,
      "totalPages": 5,
      "hasNext": true
    }
  }
}
```

---

## 2. API Thịnh Hành (Trending Tags)
- **Cụm:** `Post` / `Explore`
- **Endpoint:** `GET /api/v1/explore/trending`
- **Access:** Công khai
- **Mô tả:** Trả về danh sách Top N HashTags đang được nhắc đến nhiều nhất trong các bài đăng gần đây (giống Trending của X/Twitter).

**Query Parameters:**
- `limit` (Int): Số lượng Tag muốn lấy về (Mặc định: 10)

**Response Thành Công (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "name": "weddingphotography",
      "postCount": 4500
    },
    {
      "name": "portrait",
      "postCount": 3200
    }
  ]
}
```
