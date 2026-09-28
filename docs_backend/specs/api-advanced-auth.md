# API Đặc Tả: Xác Thực Nâng Cao (Advanced Auth)

*(Quản lý phiên đăng nhập, tạo mới token, và khôi phục mật khẩu - thuộc nhóm 139 REST API hiện tại dưới `/api/v1`)*

## 1. Cấp Mới Access Token (Refresh Token) 
- **Cụm:** `Auth`
- **Endpoint:** `POST /api/v1/auth/refresh`
- **Access:** Public. Body JSON `{ "refreshToken": "..." }`. Route này nằm trong giới hạn 5 request/phút.
- **Mô tả:** Access token hết hạn sau 15 phút. Client nhận refresh token thô một lần. Server chỉ lưu HMAC-SHA256 của token đó trong `user_sessions.refresh_token`. Refresh xoay vòng: token cũ bị xóa, response trả `token`, `refreshToken` và `expiresAt`. Gửi lại token cũ hoặc một chuỗi chưa băm đều bị từ chối.

**Response Thành Công (200 OK):**
```json
{
  "success": true,
  "data": {
    "token": "eyJhbG...",
    "refreshToken": "raw-uuid",
    "expiresAt": 1699999999
  }
}
```

---

## 2. Quên Mật Khẩu (Forgot Password)
- **Cụm:** `Auth`
- **Endpoint:** `POST /api/v1/auth/forgot-password`
- **Access:** Public
- **Body:** `{ "email": "ngochin1@gmail.com" }`
- **Mô tả:** Nếu email tồn tại, server tạo mã 6 số hạn 15 phút, băm HMAC-SHA256 rồi lưu. Mã không được in ra log. Response chỉ kèm `debugResetToken` khi biến môi trường `EXPOSE_DEBUG_RESET_TOKEN=true`. Chưa gửi email SMTP.

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
