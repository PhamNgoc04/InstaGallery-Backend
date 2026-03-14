# 🤖 InstaGallery — AI Context & API Integration Guide (For Android Frontend)

> **Mục đích của file này:** Đây là "Cẩm nang Bối cảnh" (Context Prompt) được thiết kế đặc biệt để bạn (người lập trình) hoặc các AI Assistant (Cursor, Github Copilot, Windsurf) tại dự án **Android Frontend** có thể đọc và hiểu ngay lập tức cách tích hợp Backend Ktor của hệ thống InstaGallery.

---

## 1. 🌐 Cài Đặt Mạng & Môi Trường (Network Configuration)

Để App Android gọi được tới Ktor Backend Local, bắt buộc tuân thủ 3 quy tắc sau:

### 1.1 Khai báo Domain (Base URL)
- **Nếu chạy Android Emulator:** `BASE_URL = "http://10.0.2.2:8080"` (Bắt buộc, không dùng `localhost`).
- **Nếu chạy Điện thoại thật cắm cáp (Cùng WiFi):** `BASE_URL = "http://<IPv4_CỦA_LAPTOP>:8080"` (Ví dụ: `192.168.1.x`).

### 1.2 Cho phép gọi HTTP (Cleartext Traffic)
Vì môi trường dev chưa có HTTPS (SSL), Kể từ Android 9+, bạn CẦN PHẢI khai báo trong thẻ `<application>` của file `AndroidManifest.xml`:
```xml
android:usesCleartextTraffic="true"
```

### 1.3 Giao thức WebSockets cho Chat
Khi cấu hình Ktor Client cho WebSockets, thư viện sẽ từ chối giao thức `http://`. Bạn **phải đổi URL thành `ws://`**:
- `WS_BASE_URL = "ws://10.0.2.2:8080/api/v1"`

---

## 2. 🚨 3 Lưu Ý Tuyệt Đối Khi Code (Critical Traps)

1. **Bẫy Load Ảnh bằng Thư viện Coil (Màn hình đen):**
   - Backend hiện đang trả về link ảnh dạng: `http://localhost/image.png` (Vì đang dùng Mock S3 storage).
   - 👉 **Code Frontend Cần Làm:** Trước khi truyền URL ảnh vào `AsyncImage` của Jetpack Compose, bạn phải viết một hàm `String.toRealUrl()` để `replace("localhost", "10.0.2.2")` hoặc IP của Laptop. Nếu không, điện thoại sẽ không tự hiển thị được ảnh của localhost.

2. **Bẫy Vòng Lặp Vô Tận khi Hết Token (Memory Leak):**
   - Nếu bạn dùng Ktor Client `Auth` plugin với tính năng `BearerTokens` và `loadTokens { ... }` để tự gọi API `/api/v1/auth/refresh` khi gặp lỗi 401.
   - 👉 **Code Frontend Cần Làm:** Luôn check kỹ nếu cái API Refresh Token cũng báo lỗi 401, bạn **phải Clear Session và ném User ra màn hình Login**. Tuyệt đối không để nó tự lặp lại vô tận việc gọi hàm Refresh.

3. **Cơ chế truyền Token (Header):**
   - Ngoại trừ các API Đăng Nhập, Đăng Ký, Quên Mật Khẩu... Mọi API khác đều phải nhồi header:
   - `Authorization: Bearer <Access_Token_Của_User>`

---

## 3. 📦 Các Gói Chức Năng Cốt Lõi (Core API Packages)

Hệ thống có cấu trúc URL chuẩn mực: `[BASE_URL]/api/v1/[MÔ_ĐUN]`

> 💡 *Hướng dẫn cho AI: Dựa vào danh sách dưới đây, hãy tự động tạo ra các file Ktor Client API Service (Ví dụ: `AuthApiService`, `PostApiService`) và các Kotlin Data Classes tương ứng.*

### 3.1 Nhóm Auth (`/api/v1/auth`)
- `POST /register`: Dữ liệu gửi `{username, email, password, full_name}`.
- `POST /login`: Dữ liệu gửi `{username, password}`. Trả về: `{ accessToken, refreshToken, user: { id, email... } }`.
- `POST /logout`: Hủy Token. Cần Bearer Token.
- `POST /refresh`: Làm mới Token. Gửi body chứa `{ refreshToken }`.

### 3.2 Nhóm User & Tương tác (`/api/v1/users` và `/api/v1/interactions`)
- `GET /users/me`: Lấy thông tin tài khoản đang login.
- `PUT /users/me`: Cập nhật Bio, Website, Full Name.
- `POST /users/{id}/follow`: Bấm Follow user khác.
- `POST /interactions/posts/{id}/like`: Bấm thả tim bài viết.

### 3.3 Nhóm Bài Đăng (`/api/v1/posts` và `/api/v1/explore`)
- `GET /explore/feed`: Lấy Feed bài viết của người đang theo dõi (cần Token).
- `GET /explore`: Khám phá chung (không bắt buộc Token).
- `POST /posts`: Đăng bài mới. Gửi `caption` và list `media`.
- `GET /posts/{id}`: Xem chi tiết 1 bài.

### 3.4 Nhóm Chat Realtime (`/api/v1/chat` và WebSockets)
- `GET /chat/conversations`: Danh sách Inbox hòm thư.
- `GET /chat/conversations/{id}/messages`: Lấy tin nhắn cũ.
- `WS /ws/chat?token={accessToken}`: Mở ống nối WebSocket.
    - Format gửi tin: Dùng JSON text frame (ví dụ: `{"type":"CHAT_MESSAGE", "conversation_id": 1, "content": "Hello"}`).

---

## 4. 🧩 Cấu Trúc Model Chuẩn (Data Models)

> 💡 *Hướng dẫn cho AI: Hệ thống Backend luôn trả Json bọc trong format chuẩn. Hãy tự thiết kế Class `BaseResponse<T>` trong Kotlin như sau:*

```kotlin
@Serializable
data class BaseResponse<T>(
    val status: String, // "success" hoặc "error"
    val data: T? = null,
    val message: String? = null,
    val errorCode: String? = null // Có nếu status = "error"
)
```

Ví dụ một Model `User` sẽ được Parser đọc:
```kotlin
@Serializable
data class User(
    val id: Long,
    val username: String,
    val email: String,
    val fullName: String,
    val profilePictureUrl: String?,
    val role: String,
    val isVerified: Boolean
)
```

Ví dụ một Model `Post` sẽ được Parser đọc:
```kotlin
@Serializable
data class Post(
    val id: Long,
    val userId: Long,
    val caption: String?,
    val likeCount: Int,
    val commentCount: Int,
    val media: List<MediaItem>,
    val createdAt: String
)

@Serializable
data class MediaItem(
    val id: Long,
    val mediaUrl: String,
    val mediaType: String, // "IMAGE" hoặc "VIDEO"
    val position: Int
)
```

---

## 🎯 Chỉ Thị Dành Cho Lập Trình Viên & AI Khởi Tạo Code Android

Khi bạn mang file này sang Project Android, bạn hãy copy dán prompt sau cho con AI bên đó:

**"Tôi đang bắt đầu xây dựng App Android cho hệ thống InstaGallery. Dựa vào bối cảnh API trong file `AI_Android_Integration_Context.md` này, bạn hãy:
1. Setup Ktor Client và Coil Image Loading. Hãy lưu ý 3 bẫy cấu hình mạng và host hình ảnh.
2. Viết sẵn cho tôi file `BaseResponse` và 3 class model cốt lõi là `User`, `Post`, `Tokens`.
3. Viết cho tôi `AuthApiService` chứa hàm Login và Register.
Hãy dùng Kotlin Coroutines và Koin để hoàn thành yêu cầu trên."**

*Chúc mừng bạn đã sở hữu trọn vẹn sức mạnh của vũ trụ Ktor Backend! Bước sang Frontend App Android bách chiến bách thắng nhé!* 🚀
