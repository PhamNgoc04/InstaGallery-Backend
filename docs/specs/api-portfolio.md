# API Đặc Tả: Hồ Sơ Nhiếp Ảnh Gia (Portfolio)

*(Tìm kiếm thợ ảnh và quản lý hồ sơ kỹ năng - Phase 3)*

## 1. Tạo/Cập nhật Hồ sơ Nhiếp Ảnh Gia (Create/Update Portfolio)
- **Cụm:** `Portfolio`
- **Endpoint:** `PUT /api/v1/portfolios/me`
- **Access:** Roles `PHOTOGRAPHER` hoặc `ADMIN`
- **Mô tả:** Nhiếp ảnh gia cập nhật mức giá, thể loại ảnh chụp và khu vực hoạt động để khách hàng dễ dàng tìm kiếm.

**Request Body (`UpdatePortfolioRequest`):**
```json
{
  "bio_professional": "Nhận chụp ảnh cưới, sự kiện chuyên nghiệp với hơn 5 năm kinh nghiệm.",
  "hourly_rate": 500000.0,
  "specialties": "Wedding, Event, Portrait",
  "equipment": "Sony A7III, 24-70 f2.8, 85 f1.4",
  "location": "Hà Nội"
}
```

**Response Thành Công (200 OK):**
```json
{
  "success": true,
  "message": "Cập nhật hồ sơ nhiếp ảnh gia thành công.",
  "data": { ...PortfolioDto... }
}
```

---

## 2. Tìm kiếm Thợ ảnh chuyên nghiệp (Discover Photographers)
- **Cụm:** `Portfolio`
- **Endpoint:** `GET /api/v1/portfolios?location=Hanoi&specialty=Wedding&minRate=0&maxRate=1000000&page=1`
- **Access:** Public (không cần Token)
- **Mô tả:** Lọc ra danh sách thẻ Nhiếp ảnh gia. Tùy chọn lọc qua Quert Params.

**Response:**
```json
{
  "success": true,
  "data": {
    "photographers": [
       { "userId": 10, "username": "pro_shooter", "hourlyRate": 500000, "rating": 4.8 }
    ],
    "meta": {
      "total": 5,
      "page": 1
    }
  }
}
```

---

## 3. Lấy chi tiết Hồ sơ Nhiếp ảnh gia (Get Portfolio by UserID)
- **Cụm:** `Portfolio`
- **Endpoint:** `GET /api/v1/portfolios/users/{userId}`
- **Access:** Public
- **Mô tả:** Xem chi tiết bảng giá, thiết bị, và bio nghề nghiệp của thợ ảnh. Cấu trúc Response trả ra `PortfolioDto`.
