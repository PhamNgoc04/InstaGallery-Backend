# InstaGallery API Collection (Frontend Spec)

> **Lưu ý Quan Trọng dành cho Frontend (Mobile/Web):**
> - **Base URL Nội bộ (Máy Ảo Android):** `http://10.0.2.2:8080/api/v1`
> - **Base URL Dây thật / Wifi chung:** `http://<IP-MÁY-TÍNH>:8080/api/v1`
> - Các API cần truyền ID trên đường dẫn sẽ có dạng `{id}` (Ví dụ: `/api/v1/posts/5`)
> - Payload chuẩn trả về chung: `{ "status": "SUCCESS/ERROR", "message": "...", "data": { ... } }`

---

## 📊 THỐNG KÊ TỔNG QUAN HỆ THỐNG (PROJECT METRICS)
Dự án được xây dựng theo chuẩn **Clean Architecture** sử dụng Ktor Framework, Kotlin Coroutines và DB Exposed SQL.
- **Tổng số Yêu cầu chức năng (FR):** Khớp đúng 46 Yêu cầu thực tiễn.
- **Tổng số Use Cases Cốt lõi:** 18 Use Cases cho 4 Tác nhân (Client, Photographer, Admin, System).
- **Tổng số Bảng Dữ Liệu (Database Tables):** **30 Tables** (Bao chùm toàn vẹn Dữ liệu tĩnh, Mạng xã hội, Bảo mật, Booking, Websocket).
- **Tổng số lượng API Endpoints:** **74 APIs** (Gồm 66 RESTful APIs cho App Mobile, 1 WebSocket Server, và 7 APIs quản trị Web Admin).

---

## 1. 🔐 Cụm Auth & Quản lý Thông tin Thiết bị
`Base Path: /api/v1/auth`

| HTTP Method | Endpoint | Yêu cầu Token? | Payload / Query | Mô tả chức năng |
| :--- | :--- | :---: | :--- | :--- |
| `POST` | `/register` | ❌ | `email`, `password`, `fullName` | Đăng ký tài khoản thường |
| `POST` | `/login` | ❌ | `email`, `password` | Đăng nhập tài khoản thường (Trả Access/Refresh Token) |
| `POST` | `/google` | ❌ | `googleTokenId` | Đăng nhập bằng tài khoản Google |
| `POST` | `/refresh` | ❌ | Header: `X-Refresh-Token` | Cấp lại Access Token mới dựa trên Refresh Token |
| `POST` | `/forgot-password` | ❌ | `email` | Gửi Email link khoá mã OTP quên mật khẩu |
| `POST` | `/reset-password` | ❌ | `otp`, `newPassword` | Đặt lại mật khẩu mới dựa vào email link |
| `POST` | `/2fa/verify-login`| ❌ | `userId`, `otpCode` | Nhập mã Google Authenticator nếu báo lỗi 2FA |
| `POST` | `/logout` | ✅ | Header: `X-Refresh-Token` | Vô hiệu hóa phân vùng Token hiện tại trên thiết bị |
| `PUT` | `/change-password` | ✅ | `oldPassword`, `newPassword` | Đổi mật khẩu. Bắt buộc đăng nhập lại sau khi đổi. |
| `POST` | `/2fa/setup` | ✅ | None | Gen mã Secret mới/Trả QR Code để quét G-Auth |
| `POST` | `/2fa/enable` | ✅ | `otpCode` | Nạp mã xác thực từ người dùng để chốt kích hoạt 2FA |

---

## 2. 👤 Cụm User Settings & Profile
`Base Path: /api/v1/users`

| HTTP Method | Endpoint | Yêu cầu Token? | Payload / Query | Mô tả chức năng |
| :--- | :--- | :---: | :--- | :--- |
| `GET` | `/me` | ✅ | None | Lấy thông tin cá nhân của người dùng hiện tại |
| `PUT` | `/me` | ✅ | `bio`, `website`, `isPrivate`...| Cập nhật các trường thông tin cơ bản |
| `PUT` | `/me/avatar` | ✅ | `MultipartFile` | Upload cất ảnh thẻ lên Server |
| `GET` | `/me/sessions` | ✅ | None | Render danh sách các Điện thoại/Trình duyệt đang Login |
| `DELETE` | `/me/sessions/{id}` | ✅ | None | Nút "Đăng xuất thiết bị khác" (Force Logout) |
| `POST` | `/me/deactivate`| ✅ | None | Đóng băng (Vô hiệu hóa tạm thời) tài khoản mình |

---

## 3. 🌐 Cụm Mạng Xã Hội (Người theo dõi, Chặn)
`Base Path: /api/v1/users`

| HTTP Method | Endpoint | Yêu cầu Token? | Payload / Query | Mô tả chức năng |
| :--- | :--- | :---: | :--- | :--- |
| `GET` | `/{id}` | ✅ | None | Lấy hồ sơ tường nhà người khác (hiển thị bio/post count) |
| `GET` | `/{id}/followers` | ✅ | `?page=&limit=` | Lấy danh sách Người bám đuôi (Fan) |
| `GET` | `/{id}/following` | ✅ | `?page=&limit=` | Lấy danh sách Người đang theo dõi (Idols) |
| `GET` | `/suggestions` | ✅ | `?limit=10` | Lưới Gợi ý kết bạn / Follow dạo |
| `POST` | `/{id}/follow` | ✅ | None | Nút [Theo dõi] đúp (Toggle: Tự Follow / Tự Hủy) |
| `GET` | `/me/follow-requests`| ✅ | None | Nhìn danh sách Đang chờ duyệt (Nếu account là Private) |
| `POST` | `/me/follow-requests/{id}/{action}` | ✅ | Path action: `accept` / `reject` | Duyệt / Từ chối lời mời follow của 1 user khác |
| `POST` | `/{id}/block` | ✅ | None | Chặn toàn bộ tường nhà người dùng này |
| `POST` | `/{id}/mute` | ✅ | None | Tắt tiếng (ẩn bài viết thả xuống feed của mình) |

---

## 4. 🖼️ Cụm Tương Tác Nội Dung Cốt Lõi (Posts & Feeds)
`Base Path: /api/v1/posts` và `/api/v1/comments`

| HTTP Method | Endpoint | Yêu cầu Token? | Payload / Query | Mô tả chức năng |
| :--- | :--- | :---: | :--- | :--- |
| `GET` | `/api/v1/posts` | ✅ | `?page=&limit=` | Màn Hình Home: Lấy danh sách Bảng tin Feed mới nhất |
| `POST` | `/api/v1/posts` | ✅ | `caption`, `commentVisibility`, `media[]` | Đăng tải 1 bộ hình ảnh / bài viết mới |
| `GET` | `/api/v1/posts/{id}`| ✅ | None | Render Detail Bài viết |
| `PUT` | `/api/v1/posts/{id}`| ✅ | `caption`, `location` | Sửa lại mô tả bài viết |
| `DELETE` | `/api/v1/posts/{id}`| ✅ | None | Thùng rác xóa bài viết |
| `POST` | `/api/v1/posts/{id}/like` | ✅ | None | Thả Trái tim cho 1 bài viết (Toggle logic) |
| `POST` | `/api/v1/posts/{id}/save` | ✅ | None | Nút Ruy Băng lưu Bookmark |
| `GET` | `/api/v1/posts/{id}/comments`| ✅ | `?page=&limit=` | Màn hình lưới Comments của 1 bài Post |
| `POST` | `/api/v1/posts/{id}/comments`| ✅ | `content` | Bắn bình luận mới vào Post |
| `PUT` | `/api/v1/comments/{id}` | ✅ | `content` | Chỉnh sửa nội dung Comment |
| `DELETE` | `/api/v1/comments/{id}` | ✅ | None | Xóa nhanh Comment |
| `POST` | `/api/v1/comments/{id}/like` | ✅ | None | Like (Thích) một lời bình ấn tượng của người ta |

---

## 5. 🔍 Cụm Thuật toán Khám phá & Lọc Tìm Kiếm
`Base Path: /api/v1/explore` và `/api/v1/search`

| HTTP Method | Endpoint | Yêu cầu Token? | Payload / Query | Mô tả chức năng |
| :--- | :--- | :---: | :--- | :--- |
| `GET` | `/explore` | ✅ | `?page=&limit=` | Thuật toán lưới rải đinh random Content HOT |
| `GET` | `/explore/trending` | ✅ | None | Tìm danh sách Hastags (Thẻ tag) xịn thịnh hành |
| `GET` | `/search` | ✅ | `?q=text` | Lọc bọc Tìm kiếm mảng Users, Posts, Tags |
| `GET` | `/search/history` | ✅ | None | Bóc History gõ chữ bên dưới kính lúp |
| `DELETE` | `/search/history` | ✅ | None | Nhấn "Xóa lịch sử gõ phím" |

---

## 6. 📸 Cụm Tính Năng Nhiếp Ảnh (Portfolio & Booking)

**A. Xếp hạng & Lịch đặt (Bookings / Portfolio)**
| HTTP Method | Endpoint | Yêu cầu Token? | Payload / Query | Mô tả chức năng |
| :--- | :--- | :---: | :--- | :--- |
| `GET` | `/api/v1/portfolios` | ❌ | `?location=HN&minRate=...` | Bảng Search dành cho Khách thuê kiếm Thợ chụp |
| `GET` | `/api/v1/portfolios/users/{id}`| ❌ | None | Khách ấn vô xem Info thợ cụ thể |
| `GET` | `/api/v1/portfolios/me` | ✅ | None | Thợ: Xem thông tin gian hàng của mình |
| `PUT` | `/api/v1/portfolios/me` | ✅ | `bio`, `specialty`, `hourlyRate` | Cập nhật Bảng giá dịch vụ |
| `POST`| `/api/v1/portfolios/me/availability`| ✅ | Array Lịch Rảnh (T2, T3) | Thợ setup thời gian nhận Khách tự động |
| `GET` | `/api/v1/portfolios/me/availability`| ✅ | None | Bóc Lịch dải khung giờ trống |
| `POST`| `/api/v1/bookings` | ✅ | `portfolioId`, `date`, `note`| (Client Request): Push lệnh Đặt Lịch |
| `GET` | `/api/v1/bookings/me` | ✅ | `status (Optional)` | Lấy List các Booking (Dành cho cả Thợ và Khách) |
| `GET` | `/api/v1/bookings/{id}` | ✅ | None | Chi tiết Booking (Invoice) |
| `PUT` | `/api/v1/bookings/{id}/status` | ✅ | `status=CONFIRM/CANCEL` | (Thợ Thao tác duyệt Booking) |
| `POST`| `/api/v1/ratings` | ✅ | `bookingId`, `score(1-5)` | Client Rate Đánh giá sa khi hoàn tất Booking chụp hình |

**B. Thu thập Album Xương sống (Albums)**
| HTTP Method | Endpoint | Yêu cầu Token? | Payload / Query | Mô tả chức năng |
| :--- | :--- | :---: | :--- | :--- |
| `GET` | `/api/v1/albums` | ✅ | None | Trả List thư mục Nhóm Albums |
| `POST`| `/api/v1/albums` | ✅ | `title`, `isPrivate` | Thêm tên Thư mục Albums mới |
| `GET` | `/api/v1/albums/{id}`| ✅ | None | Xem dải hình Posts bên trong 1 Album cụ thể |
| `PUT` | `/api/v1/albums/{id}`| ✅ | `title`, `coverImageUrl`| Modify Name hoặc Ảnh Lưới Gốc |
| `DELETE`| `/api/v1/albums/{id}`| ✅ | None | Đập vỡ Thư mục (Tuyệt đối không xóa hình gốc Post) |
| `POST`| `/api/v1/albums/{id}/media` | ✅ | `postId` | Attach 1 bài Post bất kì vào Bộ sưu tập này |
| `DELETE`| `/api/v1/albums/{id}/media/{mId}` | ✅ | None | Gỡ bài Post mId ra khỏi Thư mục |

---

## 7. 💬 Cụm Thời Gian Thực (Tin Nắn Chat & Notifications)

| HTTP Method | Endpoint | Yêu cầu Token? | Payload / Query | Mô tả chức năng |
| :--- | :--- | :---: | :--- | :--- |
| `GET` | `/api/v1/chat/conversations` | ✅ | None | Lấy giao diện danh sách Inboxes (Có Preview Tin nhắn cuối) |
| `GET` | `/api/v1/chat/conversations/{id}/messages`| ✅ | `?page=&limit=` | Chọc vô phòng Chat Id, Lấy hết Cục Bong Bóng Messenger |
| `WS` | `/api/v1/ws/chat?token={jwt}`| ✅ | **JSON Frame Giao Kết** | Đường dẫn cho Ktor WebSockets bắn Message Realtime 1-1 |
| `GET` | `/api/v1/notifications` | ✅ | None | Dropdown Push Noti của Android |
| `PUT` | `/api/v1/notifications/{id}/read` | ✅ | None | Marking Đã đọc |
| `PUT` | `/api/v1/notifications/read-all`| ✅ | None | Nhấn "Check All" |
| `POST`| `/api/v1/reports` | ✅ | `targetType`, `reason` | Bắn tố cáo (Porn/Spam) hình hoặc acc gửi cho Admin |

---

## 8. 🚨 Cụm Ban Quản Trị Hệ Thống (Admin - Frontend CMS Web)
`Base Path: /api/v1/admin`

| HTTP Method | Endpoint | Yêu cầu Token? | Payload / Query | Mô tả chức năng |
| :--- | :--- | :---: | :--- | :--- |
| `GET` | `/users` | ✅ (ROLE ADMIN) | `?status=` | Bốc toàn bộ Database hiển thị Table người dùng |
| `PUT` | `/users/{id}/status` | ✅ | `isActive=false` | Nút Ban / Tù túng Account Láo cá |
| `GET` | `/reports` | ✅ | None | Mở hòm thư Tố cáo Cộng Đồng |
| `PUT` | `/reports/{id}/resolve` | ✅ | `action=(BAN/DISMISS)` | Phán quyết án report. Phạt hay Mở giải? |
| `GET` | `/stats` | ✅ | None | Mở Biểu đồ KPI Chart Doanh thu Traffic |
| `GET` | `/banned-words` | ✅ | None | List Danh sách Văng Tục (Banned RegEx words) |
| `POST` | `/banned-words` | ✅ | `text`, `isRegex` | Insert từ khó nghe cho Server Filter |
| `DELETE` | `/banned-words/{id}` | ✅ | None | Ân xá gỡ bỏ 1 mã chặn lố |

> **Phiên bản LLD Document: MVP Android Project v1.0. Tỉ lệ hoàn thành Routing Frame: 100%.**
