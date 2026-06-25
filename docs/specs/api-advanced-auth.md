# API Đặc Tả: Xác Thực Nâng Cao (Advanced Auth)

*(Quản lý phiên đăng nhập, tạo mới token, và khôi phục mật khẩu - thuộc nhóm 107 REST API hiện tại dưới `/api/v1`)*

## 1. Cấp Mới Access Token (Refresh Token) 
- **Cụm:** `Auth`
- **Endpoint:** `POST /api/v1/auth/refresh`
- **Access:** Bắt buộc có Refresh_Token Header (Ví dụ: `Authorization: Bearer <refresh_token>`)
- **Mô tả:** Khi Access Token hết hạn (VD: 24h), Frontend gửi Refresh Token (hạn 30 ngày) lên đây để lấy lại Access Token mới mà không cần bắt User gõ lại mật khẩu.

**Response Thành Công (200 OK):**
```json
{
  "success": true,
  "data": {
    "token": "eyJhbG...",
    "expires_at": 1699999999
  }
}
```

---

## 2. Quên Mật Khẩu (Forgot Password)
- **Cụm:** `Auth`
- **Endpoint:** `POST /api/v1/auth/forgot-password`
- **Access:** Public
- **Body:** `{ "email": "ngochin1@gmail.com" }`
- **Mô tả:** Backend sẽ kiểm tra Email và in ra Console một chuỗi Reset Token (Do ta chưa nối SMTP gửi Mail thật). Chuỗi này có hạn 15 phút.

**Response Thành Công (200 OK):**
```json
{
  "success": true,
  "message": "Nếu email tồn tại, một đường dẫn khôi phục sẽ được gửi đến hòm thư."
}
```

---

## 3. Đặt Lại Mật Khẩu (Reset Password)
- **Cụm:** `Auth`
- **Endpoint:** `POST /api/v1/auth/reset-password`
- **Access:** Public
- **Body:** 
```json
{ 
  "reset_token": "abcxyz...", 
  "new_password": "NewStrongPassword123" 
}
```
- **Mô tả:** Sử dụng Reset Token đã nhận được để gắn mật khẩu mới. Nếu thành công sẽ xóa toàn bộ ID Session hiện hành của tài khoản này (Bắt đăng nhập lại trên mọi thiết bị).

**Response Thành Công (200 OK):**
```json
{
  "success": true,
  "message": "Mật khẩu đã được thiết lập lại thành công."
}
```

---

## 4. Đổi Mật Khẩu Bên Trong App (Change Password)
- **Cụm:** `Auth`
- **Endpoint:** `PUT /api/v1/auth/change-password`
- **Access:** JWT Cần Thiết
- **Body:** `{ "old_password": "...", "new_password": "..." }`
- **Mô tả:** Dùng cho màn hình Setting trong App. User biết mật khẩu cũ và muốn đổi sang mật khẩu mới. Thành công sẽ Logout mọi thiết bị khác.

---

## 5. Lấy Danh Sách Thiết Bị Đang Đăng Nhập (Get My Sessions)
- **Cụm:** `Users`
- **Endpoint:** `GET /api/v1/users/me/sessions`
- **Access:** JWT Cần Thiết
- **Mô tả:** Liệt kê các Session ID đang hoạt động của người dùng (Giống tính năng "Nơi bạn đã đăng nhập" của Facebook).

**Response Thành Công:**
```json
{
  "success": true,
  "data": {
    "sessions": [
      {
        "id": 1,
        "device_info": "Chrome / Windows 11",
        "ip_address": "192.168.1.1",
        "created_at": "...",
        "expired_at": "...",
        "is_current": true
      }
    ]
  }
}
```

---

## 6. Đăng Xuất Từ Xa (Revoke Session)
- **Cụm:** `Users`
- **Endpoint:** `DELETE /api/v1/users/me/sessions/{id}`
- **Access:** JWT Cần Thiết
- **Mô tả:** Hủy Refresh_Token của một thiết bị cụ thể (Bắt Hacker văng ra khỏi App ở thiết bị kia).
