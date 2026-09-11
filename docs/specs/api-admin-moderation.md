# API Đặc Tả: Quản Trị Hệ Thống và Kiểm Duyệt (Moderation)

Mã nguồn hiện tại:
- `AdminRoutes.kt`
- `ReportRoutes.kt`

Base path: `/api/v1`

Tất cả API admin cần header: `Authorization: Bearer <accessToken>` với vai trò của tài khoản là `ADMIN`.

## 1. Số liệu thống kê Dashboard (Dashboard Stats)

### `GET /api/v1/admin/stats`
Lấy thông số tổng quan cho dashboard quản trị.

### `GET /api/v1/admin/stats/growth`
Lấy dữ liệu tăng trưởng người dùng hoặc bài viết theo ngày để vẽ biểu đồ.

Tham số truy vấn (Query Params):

| Query | Mô tả |
|---|---|
| `type` | `USERS` hoặc `POSTS` |
| `days` | Số ngày cần xem dữ liệu |

## 2. Quản lý người dùng (User Management)

### `GET /api/v1/admin/users`
Lấy danh sách người dùng cho màn hình quản trị.

Tham số truy vấn (Query Params):

| Query | Mô tả |
|---|---|
| `status` | Lọc trạng thái người dùng (hoạt động/bị khóa...) |

### `GET /api/v1/admin/users/{userId}`
Lấy chi tiết thông tin một người dùng cụ thể.

### `PUT /api/v1/admin/users/{userId}/ban`
Khóa tài khoản hoặc mở khóa tài khoản của người dùng.

Yêu cầu nội dung (Request Body JSON):
```json
{
  "isBanned": true,
  "reason": "Vi phạm tiêu chuẩn cộng đồng."
}
```

### `PUT /api/v1/admin/users/{userId}/verification`
Cập nhật trạng thái xác minh (tích xanh) của người dùng.

### `PUT /api/v1/admin/users/{userId}/featured`
Bật hoặc tắt trạng thái nhiếp ảnh gia nổi bật (featured photographer).

## 3. Kiểm duyệt nội dung (Content Moderation)

### `GET /api/v1/admin/posts`
Lấy danh sách các bài viết phục vụ cho công tác kiểm duyệt.

### `GET /api/v1/admin/posts/{postId}`
Lấy chi tiết một bài viết cụ thể.

### `PUT /api/v1/admin/posts/{postId}/status`
Cập nhật trạng thái hiển thị của bài viết.

### `DELETE /api/v1/admin/posts/{postId}`
Admin thực hiện xóa bài viết vi phạm.

### `DELETE /api/v1/admin/comments/{commentId}`
Admin thực hiện xóa bình luận vi phạm.

## 4. Quản trị dịch vụ đặt lịch, đánh giá, media, thông báo

### `GET /api/v1/admin/bookings`
Lấy danh sách các đơn đặt lịch (bookings) trong hệ thống.

### `GET /api/v1/admin/bookings/{bookingId}`
Lấy chi tiết một đơn đặt lịch.

### `PUT /api/v1/admin/bookings/{bookingId}/status`
Cập nhật trạng thái đơn đặt lịch.

### `GET /api/v1/admin/ratings`
Lấy danh sách các lượt đánh giá (ratings) của hệ thống.

### `PUT /api/v1/admin/ratings/{ratingId}/status`
Cập nhật trạng thái hiển thị của lượt đánh giá.

### `DELETE /api/v1/admin/ratings/{ratingId}`
Xóa hoặc ẩn lượt đánh giá vi phạm.

### `GET /api/v1/admin/media`
Lấy danh sách toàn bộ các file media đã tải lên.

### `DELETE /api/v1/admin/media/{mediaId}`
Xóa file media vi phạm khỏi hệ thống.

### `GET /api/v1/admin/notifications`
Lấy danh sách thông báo hệ thống.

### `POST /api/v1/admin/notifications`
Tạo và gửi thông báo mới từ phía quản trị viên.

## 5. Kiểm duyệt báo cáo vi phạm (Report Moderation)

### `POST /api/v1/reports`
Khách hàng gửi báo cáo vi phạm về nội dung (bài viết, bình luận, người dùng...).

Yêu cầu nội dung (Request Body JSON):
```json
{
  "targetType": "POST",
  "targetId": 1,
  "reason": "Nội dung rác (Spam)"
}
```

### `GET /api/v1/admin/reports`
Admin xem danh sách các báo cáo vi phạm được gửi lên.

### `PUT /api/v1/admin/reports/{reportId}`
Admin cập nhật trạng thái xử lý cho báo cáo vi phạm cụ thể.

## 6. Từ khóa bị cấm (Banned Keywords)

### `GET /api/v1/admin/banned-keywords`
Lấy danh sách các từ khóa hoặc mẫu biểu thức chính quy (regex) bị cấm.

### `POST /api/v1/admin/banned-keywords`
Thêm từ khóa hoặc biểu thức chính quy bị cấm mới vào bộ lọc tự động.

Yêu cầu nội dung (Request Body JSON):
```json
{
  "wordOrRegex": "spam_keyword",
  "isRegex": false
}
```

### `DELETE /api/v1/admin/banned-keywords/{id}`
Gỡ bỏ một từ khóa hoặc biểu thức chính quy ra khỏi danh sách bị cấm.

## 7. Nhật ký hệ thống (Activity Logs)

### `GET /api/v1/admin/activity-logs`
Lấy lịch sử nhật ký hoạt động của các hệ thống lõi.

## 8. Bảng tổng hợp REST Endpoints của Admin

| Phương thức | Endpoint | Phân quyền |
|---|---|:---:|
| GET | `/api/v1/admin/stats` | Admin |
| GET | `/api/v1/admin/stats/growth` | Admin |
| GET | `/api/v1/admin/users` | Admin |
| GET | `/api/v1/admin/users/{userId}` | Admin |
| PUT | `/api/v1/admin/users/{userId}/ban` | Admin |
| PUT | `/api/v1/admin/users/{userId}/verification` | Admin |
| PUT | `/api/v1/admin/users/{userId}/featured` | Admin |
| GET | `/api/v1/admin/posts` | Admin |
| GET | `/api/v1/admin/posts/{postId}` | Admin |
| PUT | `/api/v1/admin/posts/{postId}/status` | Admin |
| DELETE | `/api/v1/admin/posts/{postId}` | Admin |
| DELETE | `/api/v1/admin/comments/{commentId}` | Admin |
| GET | `/api/v1/admin/bookings` | Admin |
| GET | `/api/v1/admin/bookings/{bookingId}` | Admin |
| PUT | `/api/v1/admin/bookings/{bookingId}/status` | Admin |
| GET | `/api/v1/admin/ratings` | Admin |
| PUT | `/api/v1/admin/ratings/{ratingId}/status` | Admin |
| DELETE | `/api/v1/admin/ratings/{ratingId}` | Admin |
| GET | `/api/v1/admin/media` | Admin |
| DELETE | `/api/v1/admin/media/{mediaId}` | Admin |
| GET | `/api/v1/admin/notifications` | Admin |
| POST | `/api/v1/admin/notifications` | Admin |
| POST | `/api/v1/reports` | Khách hàng |
| GET | `/api/v1/admin/reports` | Admin |
| PUT | `/api/v1/admin/reports/{reportId}` | Admin |
| GET | `/api/v1/admin/banned-keywords` | Admin |
| POST | `/api/v1/admin/banned-keywords` | Admin |
| DELETE | `/api/v1/admin/banned-keywords/{id}` | Admin |
| GET | `/api/v1/admin/activity-logs` | Admin |
