# API Đặc Tả: Hệ Thống Người Theo Dõi (Followers System)

Mã nguồn hiện tại: `src/main/kotlin/com/instagallery/routes/UserRoutes.kt`

## 1. Bật/tắt theo dõi (Follow / Unfollow toggle)

- **Cụm:** `Users` / `Social`
- **Endpoint:** `POST /api/v1/users/{userId}/follow`
- **Access:** Bắt buộc có Access Token
- **Mô tả:** Mã nguồn hiện tại dùng endpoint này như một dạng chuyển đổi tắt mở (toggle). Nếu chưa theo dõi thì tạo quan hệ theo dõi; nếu đã theo dõi thì hủy theo dõi.

Phản hồi gợi ý:

```json
{
  "status": "success",
  "data": {
    "isFollowing": true
  },
  "message": "Đã theo dõi người dùng này."
}
```

## 2. Danh sách người theo dõi (Followers)

- **Endpoint:** `GET /api/v1/users/{userId}/followers?page=1&limit=20`
- **Access:** Công khai (Public) theo mã nguồn hiện tại
- **Mô tả:** Lấy danh sách người dùng đang theo dõi `userId`.

## 3. Danh sách đang theo dõi (Following)

- **Endpoint:** `GET /api/v1/users/{userId}/following?page=1&limit=20`
- **Access:** Công khai (Public) theo mã nguồn hiện tại
- **Mô tả:** Lấy danh sách người dùng mà `userId` đang theo dõi.

## 4. Gợi ý theo dõi (Suggestions)

- **Endpoint:** `GET /api/v1/users/suggestions?limit=10`
- **Access:** Bắt buộc có Access Token
- **Mô tả:** Gợi ý người dùng nên theo dõi.

## 5. Yêu cầu theo dõi tài khoản riêng tư (Private follow requests)

| Phương thức | Endpoint | Quyền hạn | Ghi chú |
|---|---|:---:|---|
| GET | `/api/v1/users/me/follow-requests` | Khách hàng | Hiện đang là khung chức năng / TODO trong route |
| POST | `/api/v1/users/me/follow-requests/{followerId}/{action}` | Khách hàng | `action` thường là `accept` hoặc `reject`; đang là khung chức năng / TODO |

## 6. Chặn / tắt tiếng liên quan đến mạng lưới xã hội (Block / mute)

| Phương thức | Endpoint | Quyền hạn | Ghi chú |
|---|---|:---:|---|
| POST | `/api/v1/users/{id}/block` | Khách hàng | Bật/tắt chặn người dùng (toggle); đang là khung chức năng / TODO |
| POST | `/api/v1/users/{id}/mute` | Khách hàng | Bật/tắt tắt tiếng người dùng (toggle); đang là khung chức năng / TODO |
