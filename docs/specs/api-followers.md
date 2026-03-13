# API Đặc Tả: Hệ Thống Người Theo Dõi (Followers System)

*(Giao tiếp mạng xã hội giữa Users - Phase 2)*

## 1. Theo Dõi (Follow) Một Người Dùng
- **Cụm:** `Interaction` / `Social`
- **Endpoint:** `POST /api/v1/users/{userId}/follow`
- **Access:** Bắt buộc có AccessToken
- **Mô tả:** Request để Theodõi một User khác. Hệ thống sẽ:
  1. Thêm một dòng vào bảng `followers`.
  2. Tăng `following_count` của người gửi.
  3. Tăng `follower_count` của người nhận.
  4. (Tùy chọn) Gửi Push Notification đến người nhận.

**Response Thành Công (200 OK):**
```json
{
  "success": true,
  "message": "Đã theo dõi người dùng này."
}
```
**Ngoại lệ:**
- HTTP 400: Không thể tự Follow chính mình.
- HTTP 409: Đã theo dõi người này rồi.

---

## 2. Bỏ Theo Dõi (Unfollow)
- **Cụm:** `Interaction` / `Social`
- **Endpoint:** `DELETE /api/v1/users/{userId}/follow`
- **Access:** Bắt buộc có AccessToken
- **Mô tả:** Hủy theo dõi một User khác. Hệ thống sẽ giảm đếm Counter ở cả 2 đầu.

---

## 3. Lấy Danh Sách Người Theo Dõi Của Tôi (Get Followers)
- **Cụm:** `Users` / `Social`
- **Endpoint:** `GET /api/v1/users/{userId}/followers?page=1&limit=20`
- **Access:** Tùy chọn (Public Profile thì ai cũng có thể xem được)
- **Mô tả:** Lấy danh sách những người đang bấm Follow User này. 

**Response Thành Công:**
```json
{
  "success": true,
  "data": {
    "users": [
      {
        "id": 101,
        "username": "client01",
        "full_name": "Tên Khách Hàng",
        "avatar": "https://..."
      }
    ],
    "meta": {
      "total": 1500,
      "page": 1
    }
  }
}
```

---

## 4. Lấy Danh Sách Đang Theo Dõi (Get Following)
- **Cụm:** `Users` / `Social`
- **Endpoint:** `GET /api/v1/users/{userId}/following?page=1&limit=20`
- **Access:** Tùy chọn 
- **Mô tả:** Lấy xem User này đang theo dõi ngược lại những ai. Cấu trúc Response tương tự Get Followers.

---

## 5. Gợi Ý Theo Dõi (Suggestions)
- **Cụm:** `Users` / `Social`
- **Endpoint:** `GET /api/v1/users/suggestions`
- **Access:** Bắt buộc có AccessToken
- **Mô tả:** Gợi ý các user mới ngẫu nhiên hoặc có nhiều tương tác để khuyến khích kết nối ban đầu. Cấu trúc Response trả về mảng UserDto rút gọn.
