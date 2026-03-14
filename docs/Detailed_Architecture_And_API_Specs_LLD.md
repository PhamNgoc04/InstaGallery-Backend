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
