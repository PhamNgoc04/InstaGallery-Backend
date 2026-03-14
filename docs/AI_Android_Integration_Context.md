# 🤖 InstaGallery — AI Context & API Integration Guide (For Android Frontend)

> **Mục đích của file này:** Đây là "Cẩm nang Bối cảnh" (Context Prompt) toàn diện được thiết kế đặc biệt để cung cấp toàn bộ kiến thức về Backend của hệ thống InstaGallery cho các AI Assistant (Cursor, Github Copilot, Windsurf) hoặc các lập trình viên Android. File này chứa MỌI THỨ từ cấu hình mạng, cấu trúc thư mục, đến toàn bộ **94 API Endpoints** và Kotlin Data Models.

---

## 1. 🌐 Cài Đặt Mạng & Môi Trường (Network Configuration)

Để App Android gọi được tới Ktor Backend Local, bắt buộc tuân thủ 3 quy tắc mạng sau:

### 1.1 Khai báo Domain (Base URL)
- **Android Emulator:** `BASE_URL = "http://10.0.2.2:8080"` (Do Emulator ánh xạ `localhost` sang IP này).
- **Physical Device (cùng WiFi):** `BASE_URL = "http://<IPv4_CỦA_LAPTOP_TRÊN_LAN>:8080"` (Vd: `192.168.1.15:8080`).

### 1.2 Quyền Giao Tiếp HTTP (Cleartext Traffic)
Môi trường localhost chưa có chứng chỉ SSL. Kể từ Android 9 (API 28), Google cấm gọi API HTTP. Bạn CẦN PHẢI khai báo trong thẻ `<application>` của file `AndroidManifest.xml`:
```xml
<application
    ...
    android:usesCleartextTraffic="true">
```

### 1.3 Giao thức WebSockets cho Chat Realtime
Các endpoint dạng WebSockets BẮT BUỘC phải dùng tiền tố `ws://` thay vì `http://`.
- `WS_BASE_URL = "ws://10.0.2.2:8080/api/v1"`

---

## 2. 🚨 3 Cái Bẫy Chết Người (Critical Traps) Cần Lưu Ý

1. **Bẫy Load Ảnh bằng Coil (Màn hình đen):**
   - Backend hiện trả về link ảnh mock: `http://localhost/image.png` hoặc `http://127.0.0.1/...`.
   - 👉 **Giải pháp Frontend:** Viết 1 ext-function `String.toRealUrl()` để `replace("localhost", "10.0.2.2")` trước khi đưa URL vào thẻ `AsyncImage` của Jetpack Compose.

2. **Bẫy Vòng Lặp Vô Tận (Memory Leak) Token Refresh:**
   - Ktor Client sẽ dùng `Auth` plugin với `BearerTokens` và `loadTokens { ... }` để chặn lỗi `401 Unauthorized` và tự động gọi API `/api/v1/auth/refresh`.
   - 👉 **Giải pháp Frontend:** Nếu bản thân API `/refresh` cũng trả về 401 (Nghĩa là RefreshToken cũng đã hết hạn), bạn **phải Stop quá trình gọi mạng**, Clear DataStore/SharedPreferences và đẩy User văng ra màn hình Login ngay lập tức! (Không được để nó loop tiếp).

3. **Cơ chế truyền Token (Header):**
   - Ngoại trừ các API thuộc nhóm public (Đăng Nhập, Đăng Ký, Quên Mật Khẩu, Khám phá...). Mọi API khác đều phải chứa header: `Authorization: Bearer <Access_Token_Của_User>`. (Ktor Auth Plugin sẽ tự lo nếu setup đúng).

---

## 3. 🧩 Cấu Trúc Model Chuẩn (Data Models)

> **Hướng dẫn (Prompt) cho AI:** Dựa vào thiết kế dưới đây, hãy tạo các file Data Class bằng Kotlin (Sử dụng thư viện `kotlinx-serialization`).

### 3.1 Base Response Wrapper
Tất cả endpoint đều trả về theo định dạng chuẩn này.
```kotlin
import kotlinx.serialization.Serializable

@Serializable
data class BaseResponse<T>(
    val status: String, // "success" hoặc "error"
    val message: String? = null,
    val data: T? = null,
    val errorCode: String? = null // Chỉ có khi status = "error"
)
```

### 3.2 Core Data Models
Đây là các Data Transfer Object (DTO) cốt lõi của toàn hệ thống:

```kotlin
@Serializable
data class User(
    val id: Long,
    val username: String,
    val email: String,
    val fullName: String,
    val profilePictureUrl: String? = null,
    val bio: String? = null,
    val role: String, // "PHOTOGRAPHER", "CLIENT", "ADMIN"
    val isVerified: Boolean,
    val followerCount: Int,
    val followingCount: Int,
    val postCount: Int
)

@Serializable
data class Post(
    val id: Long,
    val userId: Long,
    val user: User? = null, // Có thể đính kèm thông tin chủ bài viết
    val caption: String?,
    val location: String? = null,
    val likeCount: Int,
    val commentCount: Int,
    val isLiked: Boolean = false, // Field tính toán trả về cho Frontend
    val isSaved: Boolean = false, // Field tính toán trả về cho Frontend
    val media: List<MediaItem>,
    val createdAt: String
)

@Serializable
data class MediaItem(
    val id: Long,
    val mediaFileUrl: String,
    val mediaType: String, // "IMAGE" hoặc "VIDEO"
    val position: Int
)

@Serializable
data class Comment(
    val id: Long,
    val postId: Long,
    val user: User,
    val content: String,
    val likeCount: Int,
    val replyCount: Int,
    val createdAt: String
)

@Serializable
data class Conversation(
    val id: Long,
    val title: String? = null,
    val type: String, // "DIRECT" or "GROUP"
    val unreadCount: Int = 0,
    val targetUser: User? = null, // Dành cho DIRECT chat
    val lastMessage: Message? = null
)

@Serializable
data class Message(
    val id: Long,
    val conversationId: Long,
    val senderId: Long,
    val content: String,
    val messageType: String, // "TEXT", "IMAGE"
    val createdAt: String
)

@Serializable
data class Booking(
    val id: Long,
    val clientId: Long,
    val photographerId: Long,
    val bookingDate: String,
    val status: String, // "PENDING","CONFIRMED","COMPLETED","CANCELLED"
    val price: Double? = null,
    val photographer: User? = null
)
```

---

## 4. 📦 Bản Đồ API Toàn Diện (94 Endpoints)

Base Path cho mọi API là: `[BASE_URL]/api/v1`
*(Lưu ý: Tất cả các Endpoint có ghi dấu `✅` ở cột Auth nghĩa là bắt buộc phải gắn Bearer Token)*

### 4.1 Nhóm 1: Xác Thực & Vòng Đời Người Dùng (Auth & Tokens)
| Phương thức | Endpoint | Auth | Chức năng & Input Payload |
|---|---|---|---|
| POST | `/auth/register` | ❌ | Body: `{username, email, password, full_name, role}` -> Trả về Tokens. |
| POST | `/auth/login` | ❌ | Body: `{username, password}` -> Trả về `{accessToken, refreshToken}`. |
| POST | `/auth/logout` | ✅ | Xóa Token session ở backend. |
| POST | `/auth/refresh` | ❌ | Body: `{refreshToken}` -> Trả về Token mới. |
| POST | `/auth/forgot-password` | ❌ | Body: `{email}` -> Gửi mail reset (mock). |
| POST | `/auth/reset-password` | ❌ | Body: `{token, new_password}`. |
| PUT | `/auth/change-password` | ✅ | Body: `{old_password, new_password}`. Xóa mọi session khác. |
| POST | `/auth/verify-email` | ❌ | Xác thực email đăng ký. |
| POST | `/auth/resend-verification`| ❌ | Gửi lại mail xác thực. |
| DELETE| `/users/me/sessions/{id}`| ✅ | Remote logout 1 thiết bị cụ thể. |
| GET | `/users/me/sessions` | ✅ | Lấy danh sách thiết bị đang đăng nhập. |
| POST | `/users/me/deactivate` | ✅ | Vô hiệu hóa tài khoản (Soft delete). |

### 4.2 Nhóm 2: Hồ Sơ Cá Nhân (User Profile)
| Phương thức | Endpoint | Auth | Chức năng & Input Payload |
|---|---|---|---|
| GET | `/users/me` | ✅ | Lấy full thông tin bản thân. |
| PUT | `/users/me` | ✅ | Body: `{bio, website, full_name, gender...}`. |
| PUT | `/users/me/avatar` | ✅ | Multipart Form: File ảnh -> Lấy Avatar URL. |
| GET | `/users/{username}` | Opt | Xem tường nhà người khác (posts, followers). |
| GET | `/photographers` | Opt | Danh sách chuyên gia nhiếp ảnh. |

### 4.3 Nhóm 3: Mạng Lưới Xã Hội (Followers & Following)
| Phương thức | Endpoint | Auth | Chức năng & Input Payload |
|---|---|---|---|
| POST | `/users/{id}/follow` | ✅ | Bấm theo dõi. Nhả Notification nếu thành công. |
| DELETE| `/users/{id}/follow` | ✅ | Hủy theo dõi. |
| GET | `/users/{id}/followers`| Opt | Lấy list người theo dõi (Có phân trang). |
| GET | `/users/{id}/following`| Opt | Lấy list người đang theo dõi (Có phân trang). |
| GET | `/users/suggestions` | ✅ | AI gợi ý người dùng nên theo dõi. |

### 4.4 Nhóm 4: Sáng Tạo Nội Dung & Trang Chủ (Posts & Feed)
| Phương thức | Endpoint | Auth | Chức năng & Input Payload |
|---|---|---|---|
| POST | `/posts` | ✅ | Đăng bài. Body: `{caption, media: [{mediaUrl, mediaType}]}`. |
| GET | `/posts/{id}` | Opt | Chi tiết bài viết. Tự Map is_liked, is_saved. |
| PUT | `/posts/{id}` | ✅ | Sửa Caption bài. |
| DELETE| `/posts/{id}` | ✅ | Gỡ bài viết. |
| GET | `/feed` | ✅ | Lấy Feed tường nhà của người đang theo dõi (Pagination). |
| GET | `/users/{username}/posts`| Opt| Các bài viết của 1 người cụ thể (Grid màn Profile). |
| POST | `/posts/{id}/media` | ✅ | Upload thêm Media vào bài cũ. |
| DELETE| `/posts/{id}/media/{mId}`| ✅ | Xóa 1 ảnh khỏi bài. |
| PUT | `/posts/{id}/media/reorder`|✅ | Sắp xếp lại thứ tự ảnh trong bài. |

### 4.5 Nhóm 5: Khám Phá & Tìm Kiếm (Explore & Search)
| Phương thức | Endpoint | Auth | Chức năng & Input Payload |
|---|---|---|---|
| GET | `/explore` | Opt | Trang khám phá, thuật toán gợi ý ảnh đẹp. |
| GET | `/search` | ✅ | Param: `?q="abc"`. Tìm Username, Tên, và Hashtag. |
| GET | `/search/autocomplete` | ✅ | Gợi ý dropdown lúc type. |
| GET | `/search/trending` | Opt | Danh sách Tags đang nổi. |
| GET | `/search/history` | ✅ | Kéo lại những gì đã tìm trong quá khứ. |
| DELETE| `/search/history` | ✅ | Xóa lịch sử tìm kiếm. |
| GET | `/tags/{tagName}/posts` | Opt | Nhấn vào 1 hashtag -> Xem mọi ảnh gắn hashtag đó. |

### 4.6 Nhóm 6: Tương Tác Xã Hội (Likes, Comments, Saves)
| Phương thức | Endpoint | Auth | Chức năng & Input Payload |
|---|---|---|---|
| POST | `/interactions/posts/{id}/like` | ✅ | Bấm thả tim. |
| DELETE| `/interactions/posts/{id}/like` | ✅ | Bỏ thả tim. |
| GET | `/posts/{id}/likers` | Opt | Xem những ai đã tim bài này. |
| GET | `/posts/{id}/like/status`| ✅ | Check xem user hiện tại đã tim bài này chưa. |
| POST | `/posts/{id}/comments` | ✅ | Đăng comment. Body: `{content, parentCommentId}`. |
| GET | `/posts/{id}/comments` | Opt | Lấy Comments (Cấu trúc Tree, 2 level). |
| PUT | `/comments/{commentId}` | ✅ | Sửa text comment. |
| DELETE| `/comments/{commentId}` | ✅ | Thu hồi comment (3 cấp độ Auth: Owner, Post Owner, Admin). |
| POST | `/comments/{id}/like` | ✅ | Tim một dòng comment. |
| DELETE| `/comments/{id}/like` | ✅ | Bỏ tim comment. |
| GET | `/comments/{id}/replies`| Opt | Tải danh sách Reply cho 1 comment cha. |
| POST | `/posts/{id}/save` | ✅ | Lưu bài viết vào Bookmark cá nhân. |
| DELETE| `/posts/{id}/save` | ✅ | Bỏ lưu bài. |
| GET | `/users/me/saved-posts`| ✅ | Lấy grid bài viết đã lưu. |

### 4.7 Nhóm 7: Hợp Đồng Nhiếp Ảnh (Booking & Rating)
| Phương thức | Endpoint | Auth | Chức năng & Input Payload |
|---|---|---|---|
| POST | `/bookings` | ✅ | Body: `{photographer_id, booking_date, duration...}`. |
| GET | `/bookings` | ✅ | List booking của mình (Bao gồm cả người thuê & thợ). |
| GET | `/bookings/{id}` | ✅ | Chi tiết 1 lịch (Hoá đơn). |
| PUT | `/bookings/{id}` | ✅ | Photographer duyệt: Chuyển trạng thái `PENDING -> CONFIRMED`. |
| POST | `/bookings/{id}/cancel` | ✅ | Hủy lịch (Kèm lý do). |
| POST | `/ratings` | ✅ | Viết Review: Body `{booking_id, ratee_id, rating_value, comment}` (Chỉ booking COMPLETED). |
| GET | `/users/{id}/ratings` | Opt | Đọc Review của thợ ảnh. |
| GET | `/photographers/{id}/availability`| Opt | Check xem thợ này có rảnh ngày X không. |
| GET | `/portfolios/me` | PHOTOG| Lấy Portfolio của thợ. |
| PUT | `/portfolios/me` | PHOTOG| Cập nhật giá tiền `hourly_rate`, `service_area`. |
| GET | `/users/{id}/portfolio`| Opt | Khách xem Portfolio thợ. |

### 4.8 Nhóm 8: Trò Chuyện Tức Thời (Real-Time WebSockets & Chat)
| Phương thức | Endpoint | Auth | Chức năng & Input Payload |
|---|---|---|---|
| GET | `/chat/conversations` | ✅ | Hộp thư Inbox (Danh sách Chat). |
| POST | `/chat/conversations` | ✅ | Mở Inbox mới (DIRECT hoặc GROUP). |
| GET | `/chat/conversations/{id}`| ✅ | Chi tiết 1 cuộc trò chuyện. |
| GET | `/chat/conversations/{id}/messages`|✅| Load History tin nhắn. |
| WS | `/ws/chat?token={token}`| ✅ | **Ống cắm Realtime (WebSocket)**. Gửi JSON frame để nhả chat qua lại. |
| POST | `/chat/conversations/{id}/messages`|✅| Fallback: Gửi tin nhắn qua REST API. |
| PUT | `/chat/conversations/{id}/read` | ✅ | Đánh dấu đã đọc (Set `last_read_at`). |
| POST | `/chat/conversations/{id}/members`|✅| Chức năng Group: Add người mới. |
| DELETE| `/chat/conversations/{id}/members/{uId}`|✅| Chức năng Group: Kick người ra. |
| PUT | `/chat/conversations/{id}/mute`| ✅ | Tắt chuông báo 1 box chat. |

### 4.9 Nhóm 9: Hệ Thống Thông Báo (Notifications)
| Phương thức | Endpoint | Auth | Chức năng & Input Payload |
|---|---|---|---|
| GET | `/notifications` | ✅ | Lịch sử thông báo (Like, Follow, Booking...). |
| PUT | `/notifications/{id}/read` | ✅ | Đánh dấu 1 thông báo đã xem. |
| PUT | `/notifications/read-all`| ✅ | Mark all as read. |
| GET | `/notifications/unread-count`|✅| Hiển thị Badge chấm đỏ (Số lượg chưa đọc). |

### 4.10 Nhóm 10: Server Utils, Upload & Admin
| Phương thức | Endpoint | Auth | Chức năng & Input Payload |
|---|---|---|---|
| GET | `/health` | ❌ | Ping Heartbeat của Server Ktor. |
| POST | `/upload/presigned-url`| ✅ | Xin link Storage Upload trực tiếp (S3/Firebase Bypass). |
| POST | `/reports` | ✅ | Report nội dung vi phạm `{target_type, target_id, reason}`. |
| (15 API Admin)| Khu vực `/admin/*` | ADMIN| Lấy Stats, Quản lý Users, Xóa Posts rác, Khóa ACC, Ban/Unban, Cấp Tích Xanh (is_verified). |

---

## 5. 🤖 Lời Gọi Prompt Mẫu Dành Cho Khâu Build Frontend (Dành Cho AI Android)

*(Khi bắt đầu một Phase code Android mới, bạn hãy copy đoạn text ở ô dưới gán cho Assistant của bạn ở Android Studio)*

> "Tôi đang bắt đầu xây dựng App Android cho hệ thống InstaGallery. Hệ thống Backend Ktor đã hoàn thiện với 94 Endpoint như quy định trong file Context này. 
> Nhiệm vụ của bạn là:
> 1. Xây dựng một **Network Module (Dependency Injection bằng Koin)** cung cấp `Ktor HttpClient`. Nhớ bắt buộc handle Token Refresh (`AuthPlugin` BearerTokens) và đổi Base URL `10.0.2.2` để chạy máy ảo. Ngăn chặn bug Infinite Loop Token. 
> 2. Generate ra các File **Kotlin Data Class Models** dựa vào cấu trúc ở mục 3 (có chứa `@Serializable`).
> 3. Tự động tạo hệ thống Interface Service (ví dụ `AuthApiService`, `FeedApiService`, `ChatWebsocketService`) chứa các hàm bám sát theo các bảng Routing tại Mục 4.
> 4. Hãy áp dụng Clean Architecture (Repository Pattern) và Jetpack Compose trong toàn bộ dự án."
