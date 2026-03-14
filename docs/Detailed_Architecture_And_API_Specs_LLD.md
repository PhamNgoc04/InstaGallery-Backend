# Hệ Thống Tài Liệu Kỹ Thuật Chuyên Sâu (Low-Level Design - LLD)
**Dự án:** InstaGallery Backend System  
**Framework:** Ktor 3.0 / Kotlin Coroutines / Koin / Exposed SQL  
**Mục đích:** Tài liệu tham chiếu tuyệt đối (Single Source of Truth) trước khi phát triển Mobile App.

---

## 1. MÔ HÌNH DỮ LIỆU CỐT LÕI (DATABASE SCHEMA OVERVIEW)
Hệ thống RDBMS (MySQL) được cấu trúc thành **23 Bảng (Tables)**, chia làm 6 phân hệ chính:

### Phân Hệ 1: Xác Thực & Người Dùng (Users & Auth)
- **`UsersTable`**: Bảng gốc. Lưu trữ thông tin cá nhân (FullName, Email, Mật khẩu Bcrypt băm, AvatarUrl, Bio, Website) và phân quyền (Role: USER/ADMIN).
- **`UserSessionsTable`**: Quản lý đa thiết bị. Lưu RefreshToken, DeviceInfo, IP, Expiry Date. Dùng để "đá văng" thiết bị trái phép.
- **`PasswordResetTokensTable`**: Lưu mã khôi phục mật khẩu gửi qua email, có thời hạn (Expiry).

### Phân Hệ 2: Mạng Xã Hội (Social Graph)
- **`FollowersTable`**: Ma trận quan hệ `(followerId, followingId)`. Ai theo dõi ai.
- **`ActivityLogsTable`**: Nhật ký hoạt động (Audit Trail) - Rất quan trọng cho Admin để theo dõi hành vi User.

### Phân Hệ 3: Nội Dung & Tương Tác (Content & Engagement)
- **`PostsTable`**: Bảng bài viết. Lưu nội dung (Caption), vị trí (Location), số lượt Tim (`likesCount`), số lượt Phản hồi (`commentsCount`).
- **`PostMediaTable`**: Quan hệ 1-Nhiều với Post. Một bài viết có nhiều ảnh/video. Lưu MediaUrl, MediaType (IMAGE/VIDEO), Thuật toán lọc (FilterId).
- **`PostMediaTagsTable`**: Ma trận gắn thẻ (Tag) người dùng khác trên bức ảnh.
- **`LikesTable` & `SavedPostsTable`**: Bảng đánh dấu trạng thái tương tác của 1 User lên 1 Post.
- **`CommentsTable` & `CommentLikesTable`**: Hệ thống bình luận đa tầng (có Reply).

### Phân Hệ 4: Chat Real-time (WebSockets)
- **`ConversationsTable`**: Bảng định danh phòng Chat (Type: DIRECT / GROUP). Lưu thời điểm cập nhật cuối `updatedAt` để xếp inbox.
- **`ConversationMembersTable`**: Danh sách User nằm trong phòng Chat.
- **`MessagesTable`**: Kho chứa tin nhắn thực. Lưu Nội dung, Loại tin (TEXT/IMAGE), Timestamp, và id của tin nhắn gốc nếu có (ReplyToId).

### Phân Hệ 5: Tìm Kiếm & Khám Phá (Search & Explore)
- **`SearchHistoriesTable`**: Lưu vết lịch sử tìm kiếm cá nhân hóa (`userId, keyword, timestamp`).
- **`MediaTagsTable` / `FiltersTable`**: Dữ liệu thuật toán liên quan đến Hashtag và Bộ lọc màu (Instagram Filters).

### Phân Hệ 6: Dịch Vụ Thương Mại (Business & Admin)
- **`PortfoliosTable`**: Hồ sơ năng lực của Nhiếp ảnh gia (Photographers).
- **`BookingsTable`**: Hệ thống Đặt lịch chụp ảnh (Lưu trạng thái PENDING/CONFIRMED/CANCELLED/COMPLETED, Giá tiền, Thời gian).
- **`RatingsTable`**: Đánh giá 1-5 sao (+ Review Text) sau khi hoàn thành Booking.
- **`ReportsTable`**: Bảng tố cáo Vi phạm (Spam/Nudity/Violence) do người dùng gửi lên, Admin duyệt (Status: OPEN/RESOLVED).

---

## 2. BẢNG TRA CỨU API ĐỊNH TUYẾN (API ENDPOINTS CHEATSHEET)
*Quy ước chung: Tất cả Payload gửi nhận đều định dạng `application/json`. Header bắt buộc: `Authorization: Bearer <token>`.*

### 🔐 2.1. Authentication (Xác thực)
| HTTP | Endpoint | Chức năng (Ý nghĩa) |
|---|---|---|
| `POST` | `/api/v1/auth/register` | Tạo mới tài khoản (Email, Pass, FullName). Trả về 201 Created. |
| `POST` | `/api/v1/auth/login` | Kiểm tra Pass (Bcrypt). Trả JWT Access `token` & `refreshToken`. |
| `POST` | `/api/v1/auth/refresh` | **Cực quan trọng cho App:** Lấy Token mới bằng Token cũ (Gửi qua Header `X-Refresh-Token`). |
| `POST` | `/api/v1/auth/logout` | Huỷ bỏ phiên đăng nhập, Server thu hồi lại (Revoke) Refresh Token. |
| `PUT` | `/api/v1/auth/change-password` | Người dùng đổi mật khẩu từ cấu hình App. |
| `POST` | `/api/v1/auth/forgot-password` | Gửi Request yêu cầu xin mã khôi phục từ Email. |

### 👤 2.2. User Management (Phân hệ Người dùng)
| HTTP | Endpoint | Chức năng (Ý nghĩa) |
|---|---|---|
| `GET` | `/api/v1/users/me` | Lấy Profile của chính mình (Từ Token). |
| `PUT` | `/api/v1/users/me` | Cập nhật Tiểu sử (Bio), Website, Tên hiển thị. |
| `PUT` | `/api/v1/users/me/avatar` | Đổi Avatar (Gửi URL s3 hoặc Multipart file). |
| `GET` | `/api/v1/users/me/sessions` | Liệt kê các điện thoại/trình duyệt đang Login acc này. |
| `DEL` | `/api/v1/users/me/sessions/{id}` | **Đá văng (Force Logout)** một thiết bị khác. |
| `GET` | `/api/v1/users/{id}` | Xem tường cá nhân người khác. Trả về `UserDto`. |
| `POST` | `/api/v1/users/{id}/follow` | Nút [Theo dõi] / [Bỏ theo dõi]. Backend sẽ Toggle logic tự động. |
| `GET` | `/api/v1/users/{id}/followers` | Danh sách Fan (những kẻ bám đuôi). Có Phân trang `?page=x&limit=y`. |
| `GET` | `/api/v1/users/{id}/following` | Danh sách Idol (những người mình đang theo). Cùng form phân trang. |
| `GET` | `/api/v1/users/suggestions` | Gợi ý kết bạn (Dựa trên mức độ quen biết hoặc Random). |

### 📸 2.3. Post & Feed (Bài Viết & Bảng Tin)
| HTTP | Endpoint | Chức năng (Ý nghĩa) |
|---|---|---|
| `GET` | `/api/v1/posts` | **Màn hình Home (NewFeed):** Chỉ lấy bài của người mình Follow + Phân trang. |
| `POST` | `/api/v1/posts` | Đăng bài mới: Gửi `caption` + mảng `{mediaUrl, type, filterId, tags}`. |
| `GET` | `/api/v1/posts/{id}` | Xem chi tiết 1 bài viết (Lấy full mảng Media, mảng Comments). |
| `PUT` | `/api/v1/posts/{id}` | Sửa nội dung (Caption, Location) của bài gốc. |
| `DEL` | `/api/v1/posts/{id}` | Xóa vĩnh viễn (Phải là chủ bài viết hoặc Admin). |

### ❤️ 2.4. Interactions (Tương tác Mạng xã hội)
| HTTP | Endpoint | Chức năng (Ý nghĩa) |
|---|---|---|
| `POST` | `/api/v1/posts/{id}/like` | Bắn 1 phát thì Thả tim, bắn lại phát nữa thì Bỏ tim (Toggle). |
| `POST` | `/api/v1/posts/{id}/save` | Lưu bài vào mục "Saved Bookmark" cá nhân. |
| `POST` | `/api/v1/posts/{id}/comments`| Cấu trúc `{ "content": "Ngầu quá anh!" }` -> Tạo bình luận. |
| `GET` | `/api/v1/posts/{id}/comments` | Render UI List danh sách các bình luận có trong bài đó (Phân trang). |
| `PUT` | `/api/v1/comments/{id}` | Chỉnh sửa bình luận đã gửi. |
| `DEL` | `/api/v1/comments/{id}` | Xóa bình luận. |
| `POST` | `/api/v1/comments/{id}/like` | Thả tim vào bình luận của người khác. |

### 🚀 2.5. Explore & Search (Thuật toán Tìm kiếm)
| HTTP | Endpoint | Chức năng (Ý nghĩa) |
|---|---|---|
| `GET` | `/api/v1/explore` | **Màn hình Khám phá kính lúp:** Bốc random Post có độ tương tác cao. |
| `GET` | `/api/v1/explore/trending` | Danh sách thẻ (#coding) đang hot nhất tuần qua. |
| `GET` | `/api/v1/search?q={text}` | Trả về 1 lúc 3 mảng: `users:[]`, `posts:[]`, `hashtags:[]`. UI render theo Tab. |
| `GET` | `/api/v1/search/history` | Dưới ô tìm kiếm, hiển thị các thẻ History người dùng từng gõ. |
| `DEL` | `/api/v1/search/history` | Nút "Xóa tất cả" lịch sử tìm kiếm. |

### 💬 2.6. Chat Realtime (Tin nhắn Ktor WebSockets)
| HTTP/WS | Endpoint | Chức năng (Ý nghĩa) |
|---|---|---|
| `GET` | `/api/v1/chat/conversations` | Lấy danh sách Inbox (Hộp thư). Trả về Avatar, Tên người nhắn, và `LastMessagePreview`. |
| `GET` | `/api/v1/chat/conversations/{id}/messages` | Mở phòng Chat. Load toàn bộ bong bóng chat cũ lên màn hình. |
| `WS` | `ws://HOST/api/v1/ws/chat?token={token}` | **Mạch máu nối 2 thiết bị:** Frame JSON gửi đi: `{ "action": "SEND_MESSAGE", "conversationId": 1, "content": "Tin nhắn nè" }`. Server đẩy `NEW_MESSAGE` sang bên kia. |

### 👑 2.7. Admin, Booking, Report (Phần Hậu Cần Ngoại Vi - Chưa làm)
*(Các tính năng thuộc nhóm Admin Dashboard Web Platform, chưa tích hợp sớm trên Cấp độ Mobile App MVP)*
- Nhóm `AdminRoutes`: Quản lý User (Khóa, Mở khóa), Thống kê KPI Hệ thống.
- Nhóm `BookingRoutes/PortfolioRoutes`: Nghiệp vụ dành riêng cho Đối tác Nhiếp Ảnh định hình giá chụp và nhận đơn hàng.
- Nhóm `ReportRoutes`: Tiếp nhận khiếu nại spam do người dùng gửi lên.

---

## 3. LỜI KHUYÊN CHO ĐỘI FRONTEND (MOBILE APP)
Tài liệu trên là Bản Đồ Tối Thượng. Khi bạn chuyển sang làm Android Jetpack Compose, hãy tuân thủ trình tự sau:
1. **Dựng UI Auth trước:** Nối `login`, `register`, cất Token vào biến cục bộ (DataStore).
2. **Setup Ktor Client (App):** Cấu hình Interceptor. Nếu API trả về `401 Unauthorized` (Hết hạn Token) -> Bắt App tự động gọi ngầm `POST /refresh` để xin Token mới.
3. **Màn Hình Main (Bottom Nav):**
   * Tab Home: Bắn `GET /posts`. Render Feed `LazyColumn`. Nút tim bắn `POST /like`.
   * Tab Search: Bắn `GET /explore/trending` ngay lần đầu mở, lúc gõ thì gọi `GET /search`.
   * Tab Profile: Gắn `GET /users/me`. Lưới ảnh gọi API tải theo ID.
4. **Chat Màn Hình:** Tạo Coroutines Scope chạy ngầm giữ `WebSocketSession` để nghe chuông tin nhắn.

---

## 4. CHUYÊN ĐỀ MẠNG MÁY TÍNH: KẾT NỐI APP ANDROID VỚI KTOR SERVER

> Lỗi kinh điển nhất mà 99% lập trình viên Mobile gặp phải khi ráp nối Frontend vào Backend đang chạy trên máy tính cá nhân là dùng sai địa chỉ IP. Dưới đây là kiến thức nền tảng bắt buộc phải nhớ.

### 🔴 Tại sao KHÔNG THỂ dùng `http://localhost:8080` trên Mobile?
Khi bạn test bằng Postman trên Máy tính (Laptop), `localhost` (hoặc `127.0.0.1`) hiểu là **"chính cái máy tính này"**. Do Server Ktor cũng nằm trên đó nên gọi 1 phát là dính.
Mặt khác, điện thoại hoặc Máy ảo Android là **một cỗ máy hoàn toàn độc lập**.
- Nếu bên trong Code Android bạn khai báo gọi API tới `http://localhost:8080`, hệ điều hành Android sẽ tưởng bạn đang bảo nó gọi... **chính bản thân cái điện thoại đó**.
- Vì trên cái điện thoại KHÔNG HỀ chạy Ktor Server nào cả -> Gọi API sẽ báo lỗi văng App ngay lập tức (Lỗi `Connection Refused`).

### 🟢 Cách Khắc Phục Chuẩn (Có 2 Nhóm Thiết Bị)

**Trường hợp 1: Bạn Code bằng Máy Ảo Android Studio (Emulator)**
Máy ảo Android có một "cánh cửa thần kỳ" đặc biệt. Các kỹ sư Google đã quy định mã IP giả lập `10.0.2.2`.
- `10.0.2.2` trên Simulator sẽ tự động "Xuyên hầm đục tường" bay thẳng sang cái `localhost` của cái Máy Tính đang chạy nó.
- Cấu hình `BASE_URL` cho Android: `http://10.0.2.2:8080`

**Trường hợp 2: Bạn cắm Dây điện thoại thật (Physical Device) để Code**
Điện thoại thật thì không có "cửa thần kỳ" như máy ảo. Nó phải dùng tín hiệu Cục WiFi (Router mạng LAN) trong nhà bạn. Yêu cầu: Điện thoại và Laptop phải bắt CHUNG 1 CỤC WIFI.
1. Mở `cmd` trên Laptop -> Gõ `ipconfig`.
2. Tìm dòng `IPv4 Address` (Ví dụ: `192.168.1.55`). Đây là "Số nhà" của Laptop bạn trên mạng LAN.
3. Ktor Server của bạn đang mở cổng ảo `8080`. Giờ đây, chỉ cần đứng từ Điện thoại gọi tới IP của Laptop là vào được thẳng Server.
4. Cấu hình `BASE_URL` cho Android: `http://192.168.1.55:8080` *(Thay 192.168.1.55 thành số thật của máy bạn).*

**LƯU Ý CỘNG THÊM MỤC ANDROID MANIFEST:**
Kể từ Android 9 (API 28), Google cấm tiệt gọi API HTTP (Không mã hóa), ép buộc phải gọi HTTPS. Để ép App cho phép gọi HTTP nội bộ trong lúc đang code (Dev Environment), bạn buộc phải thêm dòng này vào `AndroidManifest.xml` (Thẻ `<application>`):
`android:usesCleartextTraffic="true"`

---

### 🚨 3 CÁI BẪY CHẾT NGƯỜI KHÁC KHI LÀM ANDROID (Cần Né Tránh)

**1. Bẫy WebSockets (URL Protocol Mismatch)**
- Khi gọi API bình thường (Đăng nhập, Lấy bài viết), bạn dùng chữ `http://...`
- NHƯNG khi cấu hình Ktor Client cho WebSockets (Chat), bạn BẮT BUỘC phải đổi giao thức thành chữ `ws://...` (Ví dụ `ws://10.0.2.2:8080`). Nếu bạn quên đổi và dùng chữ `http`, Ktor Client sẽ báo lỗi Socket sập ngay lập tức.

**2. Bẫy Tải Ảnh bằng Thư viện Coil (Hình Mờ Căm/Không Hiện)**
- Nếu Server trả về link ảnh là `http://localhost/image.png`, trên điện thoại sẽ méo mỏ vì không thể tải được gốc `localhost`.
- **Giải pháp:** Trong giai đoạn này (chưa có S3 thật), bạn phải config hàm `BaseUrl` cho toàn bộ các link ảnh chắp vá vào, luôn trả về IP thật `http://192.168.x.x/...` trước khi nhét vào thẻ `AsyncImage` của Jetpack Compose.

**3. Bẫy Vòng Lặp Vô Tận (Infinite Refresh Loop)**
- Token của hệ thống InstaGallery có thời hạn cực ngắn. Ktor Client (Android) có chức năng viết Interceptor: Hễ API báo `401 Unauthorized`, tự động gọi API `POST /refresh` rồi cầm Token mới gửi lại Request hỏng ban nãy.
- **Nguy hiểm:** Rất ngây thơ, nhiều người cấu hình sai đoạn `Interceptor` này. Dẫn đến việc Token Refresh cũng bị gọi liên tục -> Server Ktor lãnh đạn nghẽn mạng -> App Android sụp nguồn vì Memory Leak. Phải đặc biệt cẩn thận khi Setup `AuthPlugin` trong mã Android.
