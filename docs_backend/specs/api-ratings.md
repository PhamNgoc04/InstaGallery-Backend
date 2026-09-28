# API Đặc Tả: Đánh Giá & Nhận Xét (Ratings & Reviews)

*(Phản hồi chất lượng cho Photographer sau Booking - Phase 3)*

## 1. Thêm mới đánh giá (Create Rating)
- **Cụm:** `Ratings`
- **Endpoint:** `POST /api/v1/users/{photographerId}/ratings`
- **Access:** Bắt buộc có AccessToken
- **Mô tả:** Người dùng đã từng thuê (Booking có Status = COMPLETED/CANCELLED) mới được phép Rate nhiếp ảnh gia này.

**Request Body (`CreateRatingRequest`):**
```json
{
  "score": 5,
  "comment": "Chụp rất có tâm, ảnh giao đúng hạn, chỉnh màu đẹp!"
}
```

**Response Thành Công (201 Created):**
```json
{
  "success": true,
  "message": "Cảm ơn bạn đã gửi đánh giá.",
  "data": {
    "ratingId": 50,
    "photographerId": 12,
    "reviewerId": 8,
    "score": 5,
    "comment": "Chụp rất có tâm..."
  }
}
```
*HTTP 403 Forbidden nếu gửi đánh giá khi chưa từng có booking hoàn thành.*

---

## 2. Xem danh sách đánh giá của Thợ ảnh (Get Ratings)
- **Cụm:** `Ratings`
- **Endpoint:** `GET /api/v1/users/{photographerId}/ratings?page=1&limit=10`
- **Access:** Public
- **Mô tả:** Liệt kê các bình luận, số sao và Tính luôn điểm số trung bình (về lý thuyết nên Query từ UsersTable.rating = Aggregate(score)).

---

## 3. Xóa Đánh Giá (Delete Rating)
- **Cụm:** `Ratings`
- **Endpoint:** `DELETE /api/v1/ratings/{ratingId}`
- **Access:** Chỉ User sở hữu Rating này, hoặc ADMIN
- **Mô tả:** Soft delete đánh giá và hệ thống tự động tính lại điểm trung bình cho photographer.
