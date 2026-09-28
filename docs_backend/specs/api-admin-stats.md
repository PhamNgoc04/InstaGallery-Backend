# API Đặc Tả: Admin Dashboard (Stats & Growth)

*(Thống kê tổng quan hệ thống dành cho Admin - Phase 4)*

## 1. Lấy thông số tổng quan (Overview Stats)
- **Cụm:** `Admin`
- **Endpoint:** `GET /api/v1/admin/stats`
- **Access:** Bắt buộc Token (Role: `ADMIN`)
- **Mô tả:** Trả về các con số thống kê tổng quát của hệ thống như tổng số User, Bài viết, Đặt lịch hẹn, và Doanh thu/Lượt giao dịch.

**Response Thành Công (200 OK):**
```json
{
  "success": true,
  "data": {
    "totalUsers": 12500,
    "totalPhotographers": 450,
    "totalPosts": 32000,
    "totalBookings": 1500,
    "activeReports": 24
  }
}
```

---

## 2. Lấy dữ liệu biểu đồ tăng trưởng (Growth Chart)
- **Cụm:** `Admin`
- **Endpoint:** `GET /api/v1/admin/stats/growth`
- **Access:** Bắt buộc Token (Role: `ADMIN`)
- **Mô tả:** Trả về dữ liệu tăng trưởng người dùng hoặc bài viết trong `N` ngày qua phục vụ vẽ biểu đồ.

**Query Parameters:**
- `type` (String): Loại dữ liệu (`USERS`, `POSTS`, `BOOKINGS`). Mặc định: `USERS`.
- `days` (Int): Số ngày gần nhất. Mặc định: `7`.

**Response Thành Công (200 OK):**
```json
{
  "success": true,
  "data": [
    { "date": "2024-10-10", "count": 120 },
    { "date": "2024-10-11", "count": 145 },
    { "date": "2024-10-12", "count": 102 }
  ]
}
```
