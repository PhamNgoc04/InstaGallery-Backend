# Admin Backend Context

Tài liệu này dành cho frontend Admin Dashboard. Nội dung được đồng bộ với source hiện tại trong:

- `src/main/kotlin/com/instagallery/routes/AdminRoutes.kt`
- `src/main/kotlin/com/instagallery/routes/ReportRoutes.kt`
- `src/main/kotlin/com/instagallery/routes/AuthRoutes.kt`

Base URL local: `http://localhost:8080/api/v1`

## 1. Luồng xác thực cho Admin (Auth Flow)

| Phương thức | Endpoint | Yêu cầu xác thực | Ghi chú |
|---|---|:---:|---|
| POST | `/auth/login` | Không | Đăng nhập và nhận access/refresh token |
| POST | `/auth/refresh` | Không | Lấy access token mới khi hết hạn |
| POST | `/auth/logout` | Có | Đăng xuất, có thể cần truyền thêm `X-Refresh-Token` |
| PUT | `/auth/change-password` | Có | Đổi mật khẩu |

Admin Dashboard cần giải mã (decode) JWT để kiểm tra quyền hạn `role = ADMIN`. Backend cũng thực hiện kiểm tra vai trò này trong tất cả các route admin.

## 2. Các Administrative Routes hiện tại

Tất cả các endpoint dưới đây đều yêu cầu truyền tiêu đề xác thực (Authorization Header):

```http
Authorization: Bearer <accessToken>
```

| Phương thức | Endpoint | Query / Body | Mô tả |
|---|---|---|---|
| GET | `/admin/stats` | Không | Danh sách số liệu thống kê tổng quan (Dashboard Stats) |
| GET | `/admin/stats/growth` | `?type=USERS/POSTS&days=7` | Dữ liệu vẽ biểu đồ tăng trưởng người dùng/bài viết |
| GET | `/admin/users` | `?status=...` | Lấy danh sách tài khoản người dùng hiển thị trên trang quản trị |
| GET | `/admin/users/{userId}` | Không | Chi tiết thông tin một người dùng cụ thể |
| PUT | `/admin/users/{userId}/ban` | `{ "isBanned": true, "reason": "..." }` | Khóa hoặc mở khóa tài khoản người dùng |
| PUT | `/admin/users/{userId}/verification` | `{ "isVerified": true }` | Cập nhật xác minh tích xanh cho tài khoản |
| PUT | `/admin/users/{userId}/featured` | `{ "isFeatured": true }` | Đưa nhiếp ảnh gia lên làm nổi bật / hạ xuống |
| GET | `/admin/posts` | `?page=&limit=&search=&status=` | Danh sách bài đăng trên hệ thống |
| GET | `/admin/posts/{postId}` | Không | Chi tiết một bài đăng cụ thể |
| PUT | `/admin/posts/{postId}/status` | `{ "status": "..." }` | Thay đổi trạng thái hiển thị của bài đăng |
| DELETE | `/admin/posts/{postId}` | Không | Admin thực hiện xóa bài viết |
| DELETE | `/admin/comments/{commentId}` | Không | Admin thực hiện xóa bình luận |
| GET | `/admin/bookings` | `?page=&limit=&search=&status=` | Danh sách các lịch hẹn chụp ảnh (bookings) |
| GET | `/admin/bookings/{bookingId}` | Không | Chi tiết một lịch hẹn |
| PUT | `/admin/bookings/{bookingId}/status` | `{ "status": "..." }` | Cập nhật trạng thái lịch hẹn chụp |
| GET | `/admin/ratings` | `?page=&limit=&search=&status=` | Danh sách đánh giá (ratings) từ phía khách hàng |
| PUT | `/admin/ratings/{ratingId}/status` | `{ "status": "..." }` | Cập nhật trạng thái hiển thị của đánh giá |
| DELETE | `/admin/ratings/{ratingId}` | Không | Admin xóa hoặc ẩn đánh giá |
| GET | `/admin/media` | `?page=&limit=&search=` | Quản lý danh sách các tập tin hình ảnh/video |
| DELETE | `/admin/media/{mediaId}` | Không | Xóa vĩnh viễn tệp media khỏi hệ thống |
| GET | `/admin/notifications` | `?page=&limit=&search=` | Danh sách thông báo chung của hệ thống |
| POST | `/admin/notifications` | `{ "title": "...", "body": "...", "target": "..." }` | Tạo và gửi thông báo mới cho người dùng |
| GET | `/admin/banned-keywords` | Không | Danh sách các từ khóa hoặc regex bị cấm |
| POST | `/admin/banned-keywords` | `{ "wordOrRegex": "...", "isRegex": false }` | Thêm từ khóa bị cấm mới vào danh sách lọc tự động |
| DELETE | `/admin/banned-keywords/{id}` | Không | Xóa từ khóa cấm |
| GET | `/admin/activity-logs` | `?page=&limit=&search=` | Nhật ký hoạt động chi tiết hệ thống (Audit Logs) |

## 3. Luồng kiểm duyệt báo cáo vi phạm (Report Moderation Routes)

Các route báo cáo nằm trong `ReportRoutes.kt` nhưng sử dụng chung không gian tên kiểm duyệt admin để quản lý.

| Phương thức | Endpoint | Quyền hạn | Mô tả |
|---|---|:---:|---|
| POST | `/reports` | Người dùng | Người dùng tạo báo cáo nội dung vi phạm |
| GET | `/admin/reports` | Admin | Quản trị viên xem danh sách các báo cáo vi phạm |
| PUT | `/admin/reports/{reportId}` | Admin | Quản trị viên xử lý hoặc cập nhật trạng thái báo cáo |

## 4. Cấu trúc phản hồi chung (Response Wrapper)

Backend trả về phản hồi thành công theo định dạng sau:

```json
{
  "status": "SUCCESS",
  "message": "...",
  "data": {}
}
```

Khi gặp lỗi phản hồi:

```json
{
  "status": "ERROR",
  "message": "...",
  "errorCode": "..."
}
```

## 5. Ghi chú DTO (Typescript/Frontend Hints)

```typescript
interface UserDto {
  id: number;
  username: string;
  email: string;
  fullName: string;
  role: "USER" | "ADMIN";
  userType: "CLIENT" | "PHOTOGRAPHER";
  isActive: boolean;
  isVerified: boolean;
}

interface ReportDto {
  id: number;
  reporterId: number;
  targetType: "POST" | "COMMENT" | "USER";
  targetId: number;
  reason: string;
  status: "PENDING" | "REVIEWED" | "RESOLVED";
  createdAt: string;
}

interface BannedWordDto {
  id: number;
  wordOrRegex: string;
  isRegex: boolean;
  addedByAdminId?: number;
  createdAt: string;
}
```

## 6. Lưu ý về bảo mật (Security Notes)

- Không cho phép hiển thị (render) giao diện quản trị Admin UI nếu token JWT không chứa quyền hạn `role = ADMIN`.
- Nếu API phản hồi mã lỗi `401`, hãy thử thực hiện refresh token một lần rồi gửi lại yêu cầu ban đầu.
- Nếu phiên refresh token thất bại, xóa toàn bộ token lưu trữ cục bộ và chuyển hướng người dùng về trang đăng nhập.
- Không được thực hiện các yêu cầu gọi tới các route phục vụ debug/cài đặt như `/reset-db`, `/init-db` từ trang Admin Dashboard đang chạy môi trường production.
