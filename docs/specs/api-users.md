# API Định Nghĩa: Quản Lý Người Dùng (User Profile)

## 1. Lấy Thông Tin Cá Nhân (Get Current Profiler)
- **Cụm:** `Users`
- **Endpoint:** `GET /api/v1/users/me`
- **Access:** Bắt buộc có JWT Token (Authorization: Bearer <token>)
- **Mô tả:** Lấy toàn bộ thông tin tài khoản đang đăng nhập hiện tại dựa trên token. Phục vụ cho màn hình Profile và Settings.

**Request Body:** Không có.

**Response Thành Công (200 OK):**
```json
{
  "success": true,
  "message": "Lấy thông tin người dùng thành công",
  "data": {
    "id": 1,
    "username": "ngocpham",
    "email": "ngocpb04@gmai.com",
    "full_name": "Ngoc Pham",
    "profile_picture_url": "https://s3.aws.com/ig/avas/123.jpg",
    "bio": "",
    "website": "",
    "gender": "MALE",
    "phone_number": "+84123456789",
    "date_of_birth": "1995-10-25",
    "location": "Ha Noi, Vietnam",
    "user_type": "CLIENT",
    "role": "USER",
    "is_verified": false,
    "follower_count": 150,
    "following_count": 200,
    "post_count": 45
  }
}
```

**Response Thất Bại (401 / 404):**
- `UNAUTHORIZED`: Token hết hạn, không hợp lệ, hoặc không được giử lên.
- `USER_NOT_FOUND`: Token đúng hệ thống nhưng ID không còn tồn tại trong bảng Users (Bị xóa cứng).

---

## 2. Cập Nhật Hồ Sơ Cá Nhân (Update Profile)
- **Cụm:** `Users`
- **Endpoint:** `PUT /api/v1/users/me`
- **Access:** Bắt buộc có JWT Token
- **Mô tả:** Chỉnh sửa thông tin cá nhân. Client có thể gửi 1 hoặc nhiều trường, các trường không gửi sẽ giữ nguyên giá trị cũ (Partial Update Pattern).

**Request Body (JSON - Tất cả các trường là Nullable):**
```json
{
  "full_name": "John Doe Updated",
  "bio": "New bio here",
  "website": "https://newsite.xyz",
  "gender": "MALE",
  "phone_number": "+84987654321",
  "date_of_birth": "1995-10-25", // Định dạng YYYY-MM-DD
  "location": "Ha Noi, Vietnam"
}
```
*(Lưu ý: Không cho phép đổi `email`, `username`, `password` qua API này. Việc này cần luồng riêng do liên quan đến OTP/Bảo mật).*

**Response Thành Công (200 OK):**
```json
{
  "success": true,
  "message": "Cập nhật hồ sơ thành công",
  "data": {
    "id": 1,
    "...": "Trả về UserDto đã cập nhật y hệt API GET /me ở trên"
  }
}
```

**Response Lỗi (400 / 401 / 404):**
- `UNAUTHORIZED`: Token die.
- `INVALID_DATE_FORMAT`: Gửi sai chuẩn ngày tháng năm.
- `USER_NOT_FOUND`: Token trỏ đến User không tồn tại.
