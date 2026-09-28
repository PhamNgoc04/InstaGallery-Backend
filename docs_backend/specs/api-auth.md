# API Định Nghĩa: Đăng Nhập & Đăng Ký (Authentication)

Envelope thực tế là `{ "status": "SUCCESS"|"ERROR", "data", "message", "error": { "code", "message" } }`. Body đăng ký dùng `fullName` và `userType`. Đăng nhập nhận `usernameOrEmail` hoặc `email`, và `password`. Access token 15 phút. Refresh token trả thô một lần, server lưu HMAC-SHA256. Admin demo: `admin@instagallery.com` / `Admin@123`. User demo: tên đăng nhập + `123!`.

## 1. Đăng Ký Tài Khoản (Register)
- **Cụm:** `Auth`
- **Endpoint:** `POST /api/v1/auth/register`
- **Access:** Public
- **Mô tả:** Đăng ký tài khoản mới. Trả về thông tin người dùng cơ bản và token đăng nhập.

**Request Body (JSON):**
```json
{
  "email": "phamngoc@gmail.com",
  "username": "phamngoc",
  "password": "phamngoc123!",
  "fullName": "Phạm Ngọc",
  "userType": "CLIENT"
}
```

**Response Thành Công (201 Created):**
```json
{
  "success": true,
  "message": "Đăng ký thành công",
  "data": {
    "user_id": 1,
    "email": "phamngoc@gmail.com",
    "username": "phamngoc",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

**Response Thất Bại (400 Bad Request / 409 Conflict):**
- `EMAIL_ALREADY_EXISTS`: Trùng email.
- `USERNAME_ALREADY_EXISTS`: Trùng username.
- `INVALID_EMAIL`: Sai định dạng email.
- `PASSWORD_TOO_WEAK`: Mật khẩu quá yếu (Yêu cầu ít nhất 8 ký tự).

---

## 2. Đăng Nhập (Login)
- **Cụm:** `Auth`
- **Endpoint:** `POST /api/v1/auth/login`
- **Access:** Public
- **Mô tả:** Đăng nhập để nhận Token xác thực, truy cập các tài nguyên bảo vệ.

**Request Body (JSON):**
```json
{
  "usernameOrEmail": "phamngoc@gmail.com",
  "password": "phamngoc123!"
}
```

**Response Thành Công (200 OK):**
```json
{
  "success": true,
  "message": "Đăng nhập thành công",
  "data": {
    "user_id": 1,
    "email": "phamngoc@gmail.com",
    "username": "phamngoc",
    "role": "USER",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

**Response Thất Bại (400 Bad Request / 401 Unauthorized):**
- `USER_NOT_FOUND`: Email không tồn tại hệ thống.
- `WRONG_PASSWORD`: Mật khẩu sai.
- `ACCOUNT_LOCKED`: Tài khoản bị khóa (is_active = false)

*(Note: JWT Token sẽ được ký dựa trên `user_id`, `email`, và `role` để Client dễ dàng kiểm tra quyền ở Frontend.)*
