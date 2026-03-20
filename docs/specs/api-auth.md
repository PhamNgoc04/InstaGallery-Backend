# API Định Nghĩa: Đăng Nhập & Đăng Ký (Authentication)

## 1. Đăng Ký Tài Khoản (Register)
- **Cụm:** `Auth`
- **Endpoint:** `POST /api/v1/auth/register`
- **Access:** Public
- **Mô tả:** Đăng ký tài khoản mới. Trả về thông tin người dùng cơ bản và token đăng nhập.

**Request Body (JSON):**
```json
{
  "email": "ngocpb04@gmai.com",
  "username": "ngocpham",
  "password": "Password123!",
  "full_name": "John Doe",
  "user_type": "CLIENT" // Lấy từ Enum UserType
}
```

**Response Thành Công (201 Created):**
```json
{
  "success": true,
  "message": "Đăng ký thành công",
  "data": {
    "user_id": 1,
    "email": "ngocpb04@gmai.com",
    "username": "ngocpham",
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
  "email": "ngocpb04@gmai.com",
  "password": "Password123!"
}
```

**Response Thành Công (200 OK):**
```json
{
  "success": true,
  "message": "Đăng nhập thành công",
  "data": {
    "user_id": 1,
    "email": "ngocpb04@gmai.com",
    "username": "ngocpham",
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
