# 📁 Bàn Giao Ngữ Cảnh Hệ Thống Ktor (Admin_Backend_Context.md)

Chào đồng nghiệp AI phụ trách mảng Frontend ReactJS (Dashboard Quản trị)! Dưới đây là bức tranh toàn cảnh về mặt Dữ liệu và Định tuyến API được kết xuất trực tiếp từ mã nguồn Ktor Backend (Kotlin) tại `com.instagallery.routes`. Xin hãy lấy toàn bộ thông số này làm căn cứ (Design Tokens) để sinh mã UI Components và gọi API.

---

## 1. Tính Toàn Vẹn API (Admin & Auth Routing)

Tất cả các API dưới đây đều nằm dưới domain gốc: `http://localhost:8080/api/v1`

### 🔑 1.1 Khối Xác Thực (`AuthRoutes.kt`)
Luồng dành cho quản trị viên đăng nhập vào hệ thống Dashboard.

- **`POST /auth/login`**: Đăng nhập bằng Email và Password.
  - Payload Request: `{ "email": "...", "password": "..." }`
  - Auth: `None`
- **`POST /auth/google`**: Đăng nhập qua Gmail Auth.
  - Payload Request: `{ "googleTokenId": "..." }`
  - Auth: `None`
- **`POST /auth/refresh`**: Xin cấp Token mới.
  - Header: `X-Refresh-Token: <token>`
- **`POST /auth/logout`**: Đăng xuất tài khoản.
  - Header Auth: `Bearer <Access_Token>` & `X-Refresh-Token: <token>`

### 👑 1.2 Khối Quản trị Hệ Thống (`AdminRoutes.kt`)
Toàn bộ các Endpoints dưới đây **BẮT BUỘC** phải có Http Header: `Authorization: Bearer <Access_Token>`. Token này phải chứa Claim `role = ADMIN`, nếu không sẽ bị đá về mã lỗi `403 FORBIDDEN`.

| Phương thức | Endpoint | Thân Request / Query | Mô tả chức năng |
| :--- | :--- | :--- | :--- |
| `GET` | `/admin/stats` | None | Báo cáo các chỉ số tổng quan (Dashcards gốc) |
| `GET` | `/admin/stats/growth` | `?type=USERS/POSTS&days=7` | Xuất rãnh dữ liệu để vẽ biểu đồ Line/Bar chart |
| `PUT` | `/admin/users/{userId}/ban` | `{ "isBanned": true, "reason": "..." }` | Chặn / Mở chặn (Ban/Unban) một người dùng |
| `DELETE`| `/admin/posts/{postId}` | None | Admin dùng quyền xóa cưỡng chế 1 Bài viết (Vi phạm) |
| `DELETE`| `/admin/comments/{id}` | None | Xóa nhanh 1 bình luận độc hại / thô tục |

*(Lưu ý: API CRUD danh sách User List và Filter vui lòng sử dụng `GET /users?status=` đã được mở chung trong hệ thống).*

---

## 2. Khuôn mẫu JSON (Tiêu chuẩn DTO & Schema)

### 📌 Gói chuẩn Base Response (Khung gói tin)
Tất cả các lệnh Fetch sang Backend đều nhận về định dạng bọc này. Khi triển khai Axios Interceptor, hãy tự động trích xuất block chữ `data`:
```json
{
  "status": "SUCCESS",   // Hoặc "ERROR"
  "message": "Nội dung phản hồi...",
  "data": { ... }        // Thay đổi tùy biến Payload bên dưới
}
```

### 🧑‍💻 User Data Transfer Object (`UserDto`)
Structure trả về khi Get mảng danh sách người dùng:
```typescript
interface UserDto {
    id: number;
    username: string;
    email: string;
    fullName: string;
    profilePictureUrl: string | null;
    role: "USER" | "ADMIN";
    userType: "PHOTOGRAPHER" | "CLIENT";
    isActive: boolean;
    isVerified: boolean;
}
```

### 🚩 Reports Table Model (Dữ liệu thẻ Report Cần Duyệt)
Dành cho danh sách Hàng chờ Report hiển thị trên Admin:
```typescript
interface ReportDto {
    id: number;
    reporterId: number;         // Người đi tố cáo (User Ref)
    targetType: "POST" | "COMMENT" | "USER" | "BOOKING" | "MESSAGE";
    targetId: number;           // ID của Post/Comment dính án
    reason: string;
    status: "PENDING" | "REVIEWING" | "RESOLVED" | "DISMISSED";
    createdAt: string;          // ISO Timestamp 
}
```

### 🤬 Banned Words Schema (Từ cấm / Profanity)
```typescript
interface BannedWordDto {
    id: number;
    wordOrRegex: string;        // Chuỗi cấm ("đm", "vl")
    isRegex: boolean;           // Nếu true -> Dùng RegExp để match
    addedByAdminId: number;
    createdAt: string;
}
```

---

## 3. Bản Đồ Hằng Số Cốt Lõi (`Enums.kt`)
Khi Gen Components Form hoặc Filters tĩnh trên Website, hãy sử dụng các bộ Arrays sau:

```kotlin
enum class UserType { PHOTOGRAPHER, CLIENT }
enum class Role { USER, ADMIN }
enum class AuthProvider { LOCAL, GOOGLE, FACEBOOK, APPLE }

enum class PostVisibility { PUBLIC, PRIVATE, FRIENDS_ONLY }
enum class CommentVisibility { ALLOW_ALL, FOLLOWERS_ONLY, NO_ONE }
enum class MediaType { IMAGE, VIDEO }
enum class FollowRequestStatus { PENDING, ACCEPTED, REJECTED }

enum class ReportTargetType { POST, COMMENT, USER, BOOKING, MESSAGE }
enum class ReportStatus { PENDING, REVIEWING, RESOLVED, DISMISSED }

enum class AvailabilityType { RECURRING, SPECIFIC_DATE }
```

---

## 4. Quy ước Bảo Mật Truyền Tải (Security & JWT Auth)

1. **Chuẩn mã hóa JWT:** Token được Gen trực tiếp từ thuật toán `HMAC-SHA256` trên Ktor.
2. **Access Token Lifespan:** Độ dài khóa chính hiện tại chỉ có tác dụng trong **15 Phút**.
3. **Refresh Token Flow:** Khi Access Token hết hạn, API trả về mã lỗi `401 Unauthorized`. Frontend KHÔNG được văng màn hình Login ngay lập tức, mà phải gọi âm thầm lên `POST /api/v1/auth/refresh` bằng RefreshToken lưu trên bộ nhớ (Local Storage / Cookie) để cướp Token mới và Retrying (gọi lại API hỏng vừa rồi).
4. **Cấu trúc Claim trong Payload của JWT Token:**
   - `userId`: Long
   - `role`: String (Ví dụ: "ADMIN")
   *(Frontend Web ReactJS hãy Decode token này ở Redux/Zustand Store để check role trước khi Render UI Route).*

---
**END OF SPECIFICATION**
*Xin hãy lưu tài liệu này và sinh mã Frontend tối ưu nhất giúp Project đạt trạng thái 100% Đồng Bộ.*
