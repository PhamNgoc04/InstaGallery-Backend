# InstaGallery - AI Context & API Integration Guide for Android

Tài liệu này là ngữ cảnh lập trình (context prompt) dành cho lập trình viên Android hoặc các trợ lý AI. Nội dung đã được đồng bộ với mã nguồn Ktor hiện tại.

## 1. Cấu hình mạng (Network Configuration)

### Base URL

- Android Emulator: `http://10.0.2.2:8080`
- Thiết bị vật lý dùng chung WiFi: `http://<IP-MAY-TINH>:8080`
- API base path: `/api/v1`
- WebSocket chat: `ws://10.0.2.2:8080/api/v1/ws/chat?token=<JWT>`

### Cleartext HTTP (Không mã hóa)

Môi trường local hiện tại chưa cấu hình HTTPS. Hệ điều hành Android 9+ yêu cầu khai báo thuộc tính này trong file `AndroidManifest.xml`:

```xml
<application
    ...
    android:usesCleartextTraffic="true">
```

### Bẫy IP Localhost (Localhost Image Trap)

Nếu phía backend phản hồi đường dẫn hình ảnh có chứa địa chỉ dạng `http://localhost/...` hoặc `http://127.0.0.1/...`, thiết bị giả lập Android Emulator cần phải tự động ánh xạ (map) các đường dẫn này về đầu địa chỉ IP `10.0.2.2` trước khi truyền vào thư viện hiển thị ảnh như Coil hoặc AsyncImage để tránh lỗi không tải được tài nguyên.

## 2. Số liệu thống kê Backend hiện tại (Backend Metrics)

- Database tables (Số lượng bảng): **33**.
- REST endpoints under `/api/v1`: **139**.
- System/debug HTTP endpoints outside `/api/v1`: **5**.
- Total HTTP endpoints: **144**.
- WebSocket endpoints: **1**.
- Total including WebSocket: **145**.

Nguồn thông tin chuẩn xác (Source of Truth):

- Danh sách API: [Backend_APIs.md](Backend_APIs.md)
- Cấu trúc cơ sở dữ liệu: [database_schema.md](../database/database_schema.md)

## 3. Quy chuẩn tích hợp xác thực (Auth Integration Rules)

- Các endpoint công khai (Public): login/register/forgot password/reset password/google/2FA verify/explore/search public/profile public/ratings public.
- Các endpoint yêu cầu xác thực: truyền thêm tiêu đề `Authorization: Bearer <accessToken>`.
- Luồng refresh token: gọi tới endpoint `POST /api/v1/auth/refresh`.
- Nếu đầu `/auth/refresh` tiếp tục phản hồi mã lỗi `401`, xóa sạch token ghi nhớ cục bộ và điều hướng người dùng về trang đăng nhập.
- Thao tác đăng xuất (Logout) có thể cần đính kèm tiêu đề chứa refresh token ở header `X-Refresh-Token`.
- WebSocket chat yêu cầu truyền JWT token thông qua tham số truy vấn (query param): `/api/v1/ws/chat?token=<JWT>`.

## 4. Cấu trúc phản hồi chung (Response Wrapper)

```kotlin
@Serializable
data class BaseResponse<T>(
    val status: String,
    val message: String? = null,
    val data: T? = null,
    val errorCode: String? = null
)
```

## 5. Dấu hiệu các DTO lõi (Core DTO hints)

```kotlin
@Serializable
data class User(
    val id: Long,
    val username: String,
    val email: String,
    val fullName: String,
    val profilePictureUrl: String? = null,
    val bio: String? = null,
    val role: String,
    val userType: String? = null,
    val isVerified: Boolean = false,
    val followerCount: Int = 0,
    val followingCount: Int = 0,
    val postCount: Int = 0
)

@Serializable
data class Post(
    val id: Long,
    val userId: Long,
    val caption: String? = null,
    val location: String? = null,
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val shareCount: Int = 0,
    val createdAt: String? = null
)

@Serializable
data class Conversation(
    val id: Long,
    val title: String? = null,
    val type: String,
    val lastMessage: Message? = null
)

@Serializable
data class Message(
    val id: Long,
    val conversationId: Long,
    val senderId: Long,
    val content: String? = null,
    val messageType: String = "TEXT",
    val createdAt: String? = null
)
```

## 6. Sơ đồ ánh xạ API hiện tại (API Map)

Sử dụng mặc định `BASE_URL = http://10.0.2.2:8080/api/v1`. Các endpoint bên dưới không lặp lại tiền tố `/api/v1`.

### Auth (Xác thực)

| Phương thức | Endpoint | Yêu cầu xác thực |
|---|---|:---:|
| POST | `/auth/register` | Không |
| POST | `/auth/login` | Không |
| POST | `/auth/refresh` | Không |
| POST | `/auth/forgot-password` | Không |
| POST | `/auth/reset-password` | Không |
| POST | `/auth/google` | Không |
| POST | `/auth/2fa/verify-login` | Không |
| POST | `/auth/logout` | Có |
| PUT | `/auth/change-password` | Có |
| POST | `/auth/2fa/setup` | Có |
| POST | `/auth/2fa/enable` | Có |

### Users (Người dùng)

| Phương thức | Endpoint | Yêu cầu xác thực |
|---|---|:---:|
| GET | `/users/me` | Có |
| PUT | `/users/me` | Có |
| GET | `/users/me/sessions` | Có |
| DELETE | `/users/me/sessions/{id}` | Có |
| POST | `/users/me/deactivate` | Có |
| PUT | `/users/me/avatar` | Có |
| PUT | `/users/me/privacy` | Có |
| GET | `/users/me/saved-posts` | Có |
| GET | `/users/me/liked-posts` | Có |
| GET | `/users/me/tagged-posts` | Có |
| GET | `/users/me/comments` | Có |
| GET | `/users/me/activity-log` | Có |
| GET | `/users/me/blocked` | Có |
| GET | `/users/suggestions` | Có |
| GET | `/users/me/follow-requests` | Có |
| POST | `/users/me/follow-requests/{followerId}/{action}` | Có |
| GET | `/users/{id}` | Không |
| POST | `/users/{id}/follow` | Có |
| POST | `/users/{id}/block` | Có |
| POST | `/users/{id}/mute` | Có |
| GET | `/users/{id}/followers` | Không |
| GET | `/users/{id}/following` | Không |

### Posts, media and interactions (Bài đăng và tương tác)

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
| POST | `/posts/{id}/share` | Có |
| GET | `/posts/{id}/shares` | Có |
| PUT | `/comments/{commentId}` | Có |
| DELETE | `/comments/{commentId}` | Có |
| POST | `/comments/{commentId}/like` | Có |
| POST | `/comments/{commentId}/dislike` | Có |
| PUT | `/media/local-upload/{folder}/{fileName}` | Không |
| GET | `/media/local-files/{folder}/{fileName}` | Không |
| POST | `/media/presigned-url` | Có |
| POST | `/posts/{postId}/media` | Có |
| DELETE | `/posts/media/{mediaId}` | Có |
| PUT | `/posts/{postId}/media/reorder` | Có |

### Albums, explore and search (Album và Khám phá)

| Phương thức | Endpoint | Yêu cầu xác thực |
|---|---|:---:|
| POST | `/albums` | Có |
| GET | `/albums` | Có |
| GET | `/albums/{id}` | Có |
| PUT | `/albums/{id}` | Có |
| DELETE | `/albums/{id}` | Có |
| POST | `/albums/{id}/media` | Có |
| DELETE | `/albums/{id}/media/{mediaId}` | Có |
| GET | `/explore` | Không |
| GET | `/explore/trending` | Không |
| GET | `/explore/hashtags/{tag}` | Không |
| GET | `/search` | Tùy chọn |
| GET | `/search/history` | Có |
| DELETE | `/search/history` | Có |
| GET | `/search/trending` | Không |

### Portfolio, booking and rating (Dịch vụ và Đặt lịch chụp)

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
| POST | `/bookings` | Có |
| GET | `/bookings` | Có |
| GET | `/bookings/{id}` | Có |
| PUT | `/bookings/{id}/status` | Có |
| POST | `/bookings/{id}/review` | Có |
| DELETE | `/bookings/{id}` | Có |
| GET | `/users/{photographerId}/ratings` | Không |
| POST | `/users/{photographerId}/ratings` | Có |
| DELETE | `/ratings/{ratingId}` | Có |
| PUT | `/ratings/{ratingId}` | Có |

### Chat, notifications, reports and admin (Tin nhắn và Quản trị)

| Phương thức | Endpoint | Yêu cầu xác thực |
|---|---|:---:|
| POST | `/chat/conversations` | Có |
| GET | `/chat/conversations` | Có |
| GET | `/chat/conversations/{id}/messages` | Có |
| PUT | `/chat/conversations/{id}/read` | Có |
| POST | `/chat/conversations/{id}/messages` | Có |
| DELETE | `/chat/conversations/{id}` | Có |
| WS | `/ws/chat?token=<JWT>` | Cung cấp Token |
| POST | `/devices/fcm-token` | Có |
| DELETE | `/devices/fcm-token` | Có |
| GET | `/notifications` | Có |
| GET | `/notifications/unread-count` | Có |
| PUT | `/notifications/{id}/read` | Có |
| PUT | `/notifications/read-all` | Có |
| DELETE | `/notifications/{id}` | Có |
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

## 7. Mẫu gợi ý (prompt) cho các trợ lý AI Android

> Tôi đang xây dựng ứng dụng di động Android cho hệ thống InstaGallery. Backend Ktor hiện tại có 33 bảng cơ sở dữ liệu, 139 REST endpoints dưới dạng prefix `/api/v1`, 5 system HTTP routes kiểm định hệ thống, và 1 đường kết nối thời gian thực WebSocket `/api/v1/ws/chat`. Hãy tạo một Network Module sử dụng Ktor Client và Koin phục vụ cho cấu trúc Dependency Injection, viết logic xử lý đính kèm Bearer token + cơ chế refresh token luân chuyển tự động, cấu hình ánh xạ IP local `10.0.2.2` tương ứng cho emulator giả lập, và chỉ sinh mã nguồn cho các API service theo các endpoints quy hoạch rõ ràng trong file ngữ cảnh này.
