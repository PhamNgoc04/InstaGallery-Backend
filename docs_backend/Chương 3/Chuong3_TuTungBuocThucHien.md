# 🚀 Lộ Trình Chi Tiết Xây Dựng & Viết Chương 3: Khảo Sát, Triển Khai & Đánh Giá Hệ Thống InstaGallery

Chương 3 là lúc biến các sơ đồ, cơ sở dữ liệu ở Chương 2 thành **ứng dụng chạy thực tế**. Đây là phần quan trọng nhất để hội đồng chấm điểm, vì nó chứng minh hệ thống của bạn thực sự hoạt động.

Lộ trình dưới đây chia làm **2 Phase chính**: **Phase A (Code & Chạy App)** và **Phase B (Viết Báo Cáo)**. Bạn phải hoàn thành **A** thì mới có "nguyên liệu" (ảnh chụp màn hình, kết quả test) để viết **B**.

---

## 🔥 PHASE A: IMPLEMENTATION (Tiến Hành Code & Chạy Hệ Thống)

*Lưu ý: Phiên bản Android hiện tại dùng Kotlin + XML/AppCompat + Material + ConstraintLayout, MVVM, Ktor Client, Koin và TokenStore. Không chuyển sang Jetpack Compose và tạm thời không dùng Firebase trong MVP trừ khi có quyết định riêng.*

### Bước 1: Khởi Tạo Môi Trường & Cơ Sở Dữ Liệu
> **Mục tiêu:** Có khung source code và 30 bảng CSDL đã sẵn sàng.

- [ ] Cài đặt MySQL Workbench (hoặc XAMPP/DBeaver), cấu hình MySQL cho backend Ktor và để Exposed tạo/migrate **30 bảng** theo schema đã chốt ở Chương 2.
- [ ] Setup Redis (chạy Docker nhanh nhất: `docker run -d -p 6379:6379 redis`).
- [ ] Tạo project **Backend** (ví dụ IntelliJ IDEA với Ktor). Kết nối thành công tới MySQL và Redis.
- [ ] Mở project **Android Frontend** hiện tại trên Android Studio, kiểm tra skeleton XML/AppCompat (`MainActivity`, `activity_main.xml`) build được.
- [ ] Dùng flow presigned URL cho media; tạm thời không tích hợp Firebase/S3 SDK trực tiếp trong Android. Storage provider phía backend có thể quyết định sau MVP.

### Bước 2: Code Tầng Core (Authentication & Security)
> **Mục tiêu:** Login/Register phải chạy được đầu tiên.

- [ ] **Backend:** Code API Đăng ký (`POST /auth/register`) với bcrypt hash mật khẩu.
- [ ] **Backend:** Code API Đăng nhập (`POST /auth/login`) tạo Access JWT (15p) & lưu Refresh JWT vào MySQL (`user_sessions`).
- [ ] **Backend:** Code luồng Quên mật khẩu dùng OTP qua Redis (đã thống nhất).
- [ ] **Frontend:** Dựng màn hình Login/Register bằng XML Fragment/ViewBinding, gọi API bằng Ktor Client. Lưu token qua `TokenStore` (`DataStoreTokenStore` cho dev/MVP, `SecureTokenStore` cho production).

### Bước 3: Code Phần Content (Feed, Post & Tương Tác)
> **Mục tiêu:** Giao diện Feed có ảnh để vuốt. Core của InstaGallery.

- [ ] **Backend:** Code API Upload Media (`POST /api/v1/media/presigned-url`, sau đó lưu URL vào CSDL bảng `post_media`).
- [ ] **Backend:** Code API Đăng Bài (`POST /posts`). 
- [ ] **Backend:** Code API Lấy Feed (`GET /api/v1/posts/feed` có phân trang).
- [ ] **Backend:** API Like, Save (`POST /posts/{id}/like`).
- [ ] **Frontend:** Màn hình Feed, list bài đăng, handle nút Like (đổi màu đỏ), Comment. Thiết kế Carousel vuốt ảnh bài post có >1 ảnh.

### Bước 4: Code Phân Hệ Booking & Portfolio (Đặc Sắc Của Đồ Án)
> **Mục tiêu:** Phân quyền và Workflow logic phức tạp giữa Client & Photographer.

- [ ] **Backend:** Trả về logic check Role (Client/Photographer). API cho xem Profile (`GET /users/{id}/portfolio`).
- [ ] **Backend:** API Đặt lịch (`POST /bookings`). Logic chuyển đổi state Machine (PENDING -> CONFIRMED -> COMPLETED).
- [ ] **Frontend:** UI Calendar chọn ngày đặt lịch, màn hình danh sách lịch chụp cho Photographer xác nhận. UI đánh giá Rate (Rating Bar).

### Bước 5: Code Chat Realtime (Option - Nâng cao)
> **Mục tiêu:** Ứng dụng trông "Xịn" hơn với WebSocket.

- [ ] **Backend:** Setup WebSocket Route.
- [ ] **Backend:** Emit và Receive Message 2 chiều.
- [ ] **Frontend:** Dựng UI Chat và Listen Event WebSocket.

---

## ✍️ PHASE B: VIẾT BÁO CÁO CHƯƠNG 3 (Dự kiến 30 - 40 trang)

*Khi code xong hoặc code hòm hòm, bạn bắt đầu chụp ảnh để dán vào file Word.*

### 3.1. Giới Thiệu Môi Trường Triển Khai (3-4 trang)
1. **Mô hình kiến trúc tổng quan:** Chèn 1 sơ đồ Client-Server-Database.
2. **Công cụ phát triển (Dev Tool):**
   - Viết 1-2 đoạn mô tả về Android Studio (phiên bản), IntelliJ, SDK Kotlin.
3. **Môi trường Server & Database:**
   - MySQL 8.x, Redis.
   - Hosting API (nếu có deploy lên Render/AWS/VPS) hoặc ghi là chạy Localhost.
4. **Các thư viện (Libraries) tiêu biểu:** 
   - Ktor Client (gọi API), kotlinx.serialization (JSON), Coil (load ảnh), Koin (DI), DataStore/TokenStore (session), Ktor Server.

### 3.2. Triển Khai Cơ Sở Dữ Liệu (4-5 trang)
1. Chụp màn hình (screenshot) **MySQL Workbench** thể hiện 30 bảng đã được tạo.
2. Chụp sơ đồ Relationship (ERD) tự sinh từ Workbench (hoặc vẽ lại) để chứng tỏ DB đã connect. Đính kèm 1-2 hình tạo Trigger/Index.

### 3.3. Xây Dựng Tổ Chức Mã Nguồn (Architecture & Code) (5-8 trang)
1. **Kiến trúc Android:** MVC, MVP hay MVVM? (Khuyến khích chọn MVVM + Clean Architecture). Chụp hình cây thư mục mã nguồn Android.
2. **Cấu trúc Backend API:** Chụp hình phân lớp Route, Controller/Service, Repository.
3. **Minh họa Code Logic Tiên Quyết:** 
   - Chụp/Copy 1 tẹo mã nguồn của hàm **Tạo Access Token JWT** (để hội đồng thấy mình tự tay làm bảo mật).
   - Đoạn mã nguồn gửi tin nhắn Real-time qua WebSocket hoặc đoạn mã xử lý State Machine của Booking.

### 3.4. Xây Dựng Giao Diện Và Chức Năng (15-20 trang — PHẦN CHIẾM DIỆN TÍCH NHẤT)
> **Cách làm:** Cứ 1 chức năng = 1 ảnh chụp màn hình máy ảo + phân tích tính năng bên cạnh.

1. **Giao diện Guest (Khách):** 
   - Chụp giao diện Splash, Đăng Nhập, Đăng Ký, Quên mật khẩu.
2. **Giao diện Main/Feed:**
   - Chụp trang chủ (nhìn giống Instagram). Phân tích thanh cuộn ảnh, thả tim.
   - Chụp trang Explore / Tìm kiếm (chia thành dạng Grid layout).
3. **Giao diện Tạo Bài Đăng:**
   - Chọn ảnh từ Gallery device, màn hình Crop/Filter (nếu có), màn hình nhập Caption -> Xác nhận upload.
4. **Đặc Thù Đồ Án (Booking & Portfolio):**
   - Chụp hình trang hiển thị Portfolio cực đẹp của thợ ảnh.
   - Chụp màn giao diện chọn Ngày để Booking. Cảnh Booking Manager của Thợ ảnh (Tab: Đang chờ, Đã xác nhận).
5. **Giao diện Chat:**
   - Màn lịch sử trò chuyện.
   - Màn nhắn tin 2 người.
6. **Admin Dashboard (Nếu có web/admin riêng):**
   - Chụp màn Admin duyệt tài khoản, cấm tài khoản, thống kê số lượng bài báo cáo (Reports).

### 3.5. Kiểm Thử Hệ Thống (System Testing) (3-5 trang)
Hội đồng rất thích sinh viên có Kịch Bản Test (Test Case). Chọn ra 3 luồng quan trọng để kẻ Bảng Test:

*Ví dụ bảng Test cho chức năng Login:*
| STT | Tên ca kiểm thử | Dữ liệu đầu vào (Input) | Kết quả mong đợi (Expected) | Kết quả thực tế | Tình trạng |
|---|---|---|---|---|---|
| TC_01 | Login email sai | Email không tồn tại | Báo "Lỗi không tìm thấy tài khoản" | Hiện thông báo đúng chuẩn | Pass ✅ |
| TC_02 | Login sai mật khẩu| Email đúng, Pass sai | Báo "Sai mật khẩu" | Hiện chữ đỏ cảnh báo | Pass ✅ |
| TC_03 | Login hợp lệ | Email/Pass hoàn hảo | Chuyển sang màn hình Feed | Vào Feed thành công | Pass ✅ |

*Làm 3-5 bảng Test Case cho:*
- Luồng tạo tài khoản.
- Luồng Đăng bài (có chặn gửi ảnh quá 10MB không?).
- Luồng Đặt lịch (chọn trùng ngày thì hệ thống báo lỗi không?).

### 3.6. Kết Luận, Đánh Giá & Hướng Phát Triển (1 trang)
1. **Kết quả đạt được:** So với mục tiêu (Chương 1) đã làm được 90% hay 100%? Thiết kế đáp ứng yêu cầu UI/UX. Hệ thống hoạt động nhanh.
2. **Khó khăn gặp phải:**
   - Khó khăn trong việc optimize load ảnh N nốt mà không bị crash app (OutofMemory).
   - Xử lý JWT refresh token đôi khi chưa mượt mà trên môi trường mạng yếu.
3. **Hướng phát triển:**
   - Phát triển sang iOS (Dùng Kotlin Multiplatform hoặc làm bản mới).
   - Tích hợp thêm AI Recommendation (gợi ý ảnh thông minh hơn cho màn Explore).
   - Module Thanh toán VNPAY/Momo cho Booking.

---

## 🎯 QUYẾT ĐỊNH BƯỚC TIẾP THEO
Lộ trình phía trên là bao quát. Để tiếp tục ngay bây giờ, bạn chọn 1 trong 2:

* **Tuyển chọn 1 (Bắt đầu Code App & Backend):** Nếu bạn chưa code hoặc đang code dở, chúng ta bắt đầu từ Phase A (Bước 1 -> Bước 5). Tôi có thể hướng dẫn bạn setup network/auth baseline, cấu trúc feature-first MVVM + lightweight Clean Architecture cho Android XML/AppCompat, Ktor Client, Koin và TokenStore.
* **Tuyển chọn 2 (Soạn Template báo cáo Word):** Tôi sẽ tạo trước 1 file Markdown làm "khung xương" tài liệu cho Chương 3 (gồm các đề mục, bảng test case mẫu trống). Bạn chỉ việc lấy về, cứ code tới đâu thì tự động dán ảnh màn hình vào form trống đó.
