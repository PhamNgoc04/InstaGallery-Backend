# Bộ Sưu Tập API InstaGallery — Đặc Tả Cho Frontend (Specs)

Tài liệu này dành cho lập trình viên thiết kế Mobile/Web frontend và đã được đồng bộ hóa chuẩn xác với mã nguồn Ktor hiện tại.

## 1. Địa chỉ máy chủ cơ sở (Base URLs)

- Giả lập Android Emulator: `http://10.0.2.2:8080/api/v1`
- Thiết bị vật lý dùng chung WiFi: `http://<IP-MAY-TINH>:8080/api/v1`
- Kết nối WebSocket Android Emulator: `ws://10.0.2.2:8080/api/v1/ws/chat?token=<JWT>`
- Kết nối WebSocket thiết bị vật lý: `ws://<IP-MAY-TINH>:8080/api/v1/ws/chat?token=<JWT>`

## 2. Số liệu thống kê kỹ thuật (Project Metrics)

- Đầu REST API dưới prefix `/api/v1`: **139 endpoints**.
- Route hệ thống ngoài `/api/v1`: **2** (`GET /` và `GET /health`).
- WebSocket: **1 endpoint**. Token chat phải là JWT đã ký và còn hạn.

> Danh sách đầy đủ các route được định vị chi tiết tại [Backend_APIs.md](Backend_APIs.md). Tài liệu này tập hợp phục vụ frontend liên kết gọi và các điểm đáng lưu ý khi tích hợp.

## 3. Cấu trúc đóng gói phản hồi (Response Wrapper)

Toàn bộ các phản hồi API của backend đều được chuẩn hóa theo cấu trúc đóng gói JSend:

```json
{
  "status": "SUCCESS",
  "message": "Thông điệp từ hệ thống...",
  "data": {}
}
```

Khi gặp sự cố xử lý, backend sẽ trả về trạng thái `status = "ERROR"` và có thể đính kèm mã lỗi xử lý `errorCode`.

## 4. Quy tắc phân quyền và Refresh Token (Auth and Token Rules)

- Các endpoint công khai (Public) không yêu cầu truyền mã `Authorization` ở tiêu đề.
- Các endpoint bảo mật yêu cầu truyền JWT token ở header: `Authorization: Bearer <accessToken>`.
- Cơ chế lấy Access Token mới thông qua Refresh Token được xử lý tại endpoint `POST /auth/refresh`. Body là `{ "refreshToken": "..." }`. Server lưu bản băm HMAC-SHA256. Response trả access token mới và refresh token mới. Token vừa dùng không gọi lại được.
- Đăng xuất truyền refresh token thô ở header `X-Refresh-Token`.
- WebSocket chat: `/ws/chat?token=<JWT>`. Server kiểm tra chữ ký, hạn token và tài khoản còn active. Khung tin tối đa 256KB.
- Upload local: gọi `POST /media/presigned-url`, rồi `PUT` đúng URL trả về kèm `Authorization: Bearer <accessToken>`. File tối đa 50MB.
- `POST /2fa/verify-login`, `/2fa/setup` và `/2fa/enable` trả `501`. Chưa dùng các route này để chặn đăng nhập.
- Login, đăng ký, refresh, quên mật khẩu, đặt lại mật khẩu và Google login bị giới hạn 5 request/phút.

## 5. Ánh xạ các Endpoint theo chức năng Frontend (Endpoint Map)

### 5.1. Mô đun Xác Thực (Auth) - đường dẫn cơ sở `/auth`

| Phương thức | Endpoint | Yêu cầu xác thực |
|---|---|:---:|
| POST | `/register` | Không |
| POST | `/login` | Không |
| POST | `/refresh` | Không |
| POST | `/forgot-password` | Không |
| POST | `/reset-password` | Không |
| POST | `/google` | Không |
| POST | `/2fa/verify-login` | Không, nhưng trả 501 |
| POST | `/logout` | Có |
| PUT | `/change-password` | Có |
| POST | `/2fa/setup` | Có, nhưng trả 501 |
| POST | `/2fa/enable` | Có, nhưng trả 501 |

### 5.2. Mô đun Người Dùng (Users) - đường dẫn cơ sở `/users`

| Phương thức | Endpoint | Yêu cầu xác thực |
|---|---|:---:|
| GET | `/me` | Có |
| PUT | `/me` | Có |
| GET | `/me/sessions` | Có |
| DELETE | `/me/sessions/{id}` | Có |
| POST | `/me/deactivate` | Có |
| PUT | `/me/avatar` | Có |
| PUT | `/me/privacy` | Có |
| GET | `/me/saved-posts` | Có |
| GET | `/me/liked-posts` | Có |
| GET | `/me/tagged-posts` | Có |
| GET | `/me/comments` | Có |
| GET | `/me/activity-log` | Có |
| GET | `/me/blocked` | Có |
| GET | `/suggestions` | Có |
| GET | `/me/follow-requests` | Có |
| POST | `/me/follow-requests/{followerId}/{action}` | Có |
| GET | `/{id}` | Không |
| POST | `/{id}/follow` | Có |
| POST | `/{id}/block` | Có |
| POST | `/{id}/mute` | Có |
| GET | `/{id}/followers` | Không |
| GET | `/{id}/following` | Không |

### 5.3. Bài đăng và Tương Tác (Posts and Interactions)

| Phương thức | Endpoint | Yêu cầu xác thực |
|---|---|:---:|
| POST | `/posts` | Có |
| GET | `/posts/feed` | Có |
| GET | `/posts/{id}` | Có |
| PUT | `/posts/{id}` | Có |
| DELETE | `/posts/{id}` | Có |
| GET | `/posts/users/{userId}/posts` | Có |
| POST | `/posts/{id}/tags` | Có |
| DELETE | `/posts/{id}/tags/{taggedUserId}` | Có |
| PUT | `/posts/{id}/comment-settings` | Có |
| POST | `/posts/{id}/like` | Có |
| GET | `/posts/{id}/likes` | Có |
| POST | `/posts/{id}/save` | Có |
| POST | `/posts/{id}/comments` | Có |
| GET | `/posts/{id}/comments` | Có |
| POST | `/posts/{id}/share` | Có. Lần hai của cùng user không tăng `shareCount` |
| GET | `/posts/{id}/shares` | Có |
| PUT | `/comments/{commentId}` | Có |
| DELETE | `/comments/{commentId}` | Có |
| POST | `/comments/{commentId}/like` | Có |
| POST | `/comments/{commentId}/dislike` | Có |

### 5.4. Tập tin đa phương tiện và Album (Media and Albums)

| Phương thức | Endpoint | Yêu cầu xác thực |
|---|---|:---:|
| PUT | `/media/local-upload/{folder}/{fileName}` | Có, và phải đúng tên file vừa cấp |
| GET | `/media/local-files/{folder}/{fileName}` | Không |
| POST | `/media/presigned-url` | Có |
| POST | `/posts/{postId}/media` | Có |
| DELETE | `/posts/media/{mediaId}` | Có |
| PUT | `/posts/{postId}/media/reorder` | Có |
| POST | `/albums` | Có |
| GET | `/albums` | Có |
| GET | `/albums/{id}` | Có |
| PUT | `/albums/{id}` | Có |
| DELETE | `/albums/{id}` | Có |
| POST | `/albums/{id}/media` | Có |
| DELETE | `/albums/{id}/media/{mediaId}` | Có |

### 5.5. Khám Phá và Tìm Kiếm (Explore and Search)

| Phương thức | Endpoint | Yêu cầu xác thực |
|---|---|:---:|
| GET | `/explore` | Không |
| GET | `/explore/trending` | Không |
| GET | `/explore/hashtags/{tag}` | Không |
| GET | `/search` | Tùy chọn |
| GET | `/search/history` | Có |
| DELETE | `/search/history` | Có |
| GET | `/search/trending` | Không |

### 5.6. Dịch vụ đặt lịch chụp ảnh & Đánh giá (Portfolio, booking and rating)

| Phương thức | Endpoint | Yêu cầu xác thực |
|---|---|:---:|
| GET | `/portfolios` | Không |
| GET | `/portfolios/users/{userId}` | Không |
| GET | `/portfolios/users/{userId}/availability` | Không |
| GET | `/portfolios/me` | Có |
| PUT | `/portfolios/me` | Có |
| POST | `/portfolios/me/availability` | Có |
| GET | `/portfolios/me/availability` | Có |
| GET | `/users/{photographerId}/services` | Không |
| GET | `/photographer/services` | Có |
| POST | `/photographer/services` | Có |
| GET | `/photographer/services/{id}` | Có |
| PUT | `/photographer/services/{id}` | Có |
| PUT | `/photographer/services/{id}/status` | Có |
| DELETE | `/photographer/services/{id}` | Có |
| POST | `/bookings` | Có. Giờ không gắn `Z`. Trùng giờ trả `BOOKING_SLOT_UNAVAILABLE` |
| GET | `/bookings` | Có |
| GET | `/bookings/{id}` | Có |
| PUT | `/bookings/{id}/status` | Có |
| POST | `/bookings/{id}/review` | Có |
| DELETE | `/bookings/{id}` | Có. Hủy và trả khung giờ |
| GET | `/users/{photographerId}/ratings` | Không |
| POST | `/users/{photographerId}/ratings` | Có |
| DELETE | `/ratings/{ratingId}` | Có |
| PUT | `/ratings/{ratingId}` | Có |

### 5.7. Nhắn tin thời gian thực và Thông báo (Chat and Notifications)

| Phương thức | Endpoint | Yêu cầu xác thực |
|---|---|:---:|
| POST | `/chat/conversations` | Có. Body `{ "targetUserId": 2 }`. `201` trả `conversationId` và `isNew`. Một cặp user một hội thoại |
| GET | `/chat/conversations` | Có. Bỏ qua hội thoại user đang ẩn |
| GET | `/chat/conversations/{id}/messages` | Có. Mở lại hội thoại đã ẩn |
| PUT | `/chat/conversations/{id}/read` | Có |
| POST | `/chat/conversations/{id}/messages` | Có |
| DELETE | `/chat/conversations/{id}` | Có. Chỉ ghi `hidden_at`, không xóa thành viên |
| WS | `/ws/chat?token=<JWT>` | Quyền hạn Token |
| POST | `/devices/fcm-token` | Có |
| DELETE | `/devices/fcm-token` | Có |
| GET | `/notifications` | Có |
| GET | `/notifications/unread-count` | Có |
| PUT | `/notifications/{id}/read` | Có |
| PUT | `/notifications/read-all` | Có |
| DELETE | `/notifications/{id}` | Có |

### 5.8. Quản trị viên và Báo cáo vi phạm (Reports and Admin)

| Phương thức | Endpoint | Yêu cầu xác thực |
|---|---|:---:|
| POST | `/reports` | Có |
| GET | `/admin/reports` | Admin |
| PUT | `/admin/reports/{reportId}` | Admin |
| GET | `/admin/stats` | Admin |
| GET | `/admin/stats/growth` | Admin |
| PUT | `/admin/users/{userId}/ban` | Admin |
| DELETE | `/admin/posts/{postId}` | Admin |
| DELETE | `/admin/comments/{commentId}` | Admin |
| GET | `/admin/users` | Admin |
| GET | `/admin/users/{userId}` | Admin |
| PUT | `/admin/users/{userId}/verification` | Admin |
| PUT | `/admin/users/{userId}/featured` | Admin |
| GET | `/admin/posts` | Admin |
| GET | `/admin/posts/{postId}` | Admin |
| PUT | `/admin/posts/{postId}/status` | Admin |
| GET | `/admin/bookings` | Admin |
| GET | `/admin/bookings/{bookingId}` | Admin |
| PUT | `/admin/bookings/{bookingId}/status` | Admin |
| GET | `/admin/ratings` | Admin |
| PUT | `/admin/ratings/{ratingId}/status` | Admin |
| DELETE | `/admin/ratings/{ratingId}` | Admin |
| GET | `/admin/media` | Admin |
| DELETE | `/admin/media/{mediaId}` | Admin |
| GET | `/admin/notifications` | Admin |
| POST | `/admin/notifications` | Admin |
| GET | `/admin/banned-keywords` | Admin |
| POST | `/admin/banned-keywords` | Admin |
| DELETE | `/admin/banned-keywords/{id}` | Admin |
| GET | `/admin/activity-logs` | Admin |

## 6. Các API hệ thống

Những route này không nằm trong prefix `/api/v1` và không được gọi từ ứng dụng:

| Phương thức | Endpoint | Ghi chú |
|---|---|---|
| GET | `/` | Server đang chạy |
| GET | `/health` | Không kiểm tra MySQL |

Không còn `/init-db`, `/reset-db`, `/fix-user-id`, `/migrate-db`.
