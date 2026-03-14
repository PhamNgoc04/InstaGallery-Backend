# 🚀 BÁO CÁO NGHIỆM THU PHASE 1: HẠ TẦNG BACKEND INSTAGALLERY

> **Tài liệu này tổng hợp toàn bộ tri thức, các lỗi đã khắc phục, bộ API đã thử nghiệm và kiến trúc hiện tại của dự án InstaGallery Backend (Ktor).**
> **Ngày hoàn thành:** Tháng 3/2026.

---

## 1. TỔNG QUAN KIẾN TRÚC HIỆN TẠI (ARCHITECTURE REVIEW)
Hệ thống Backend hiện tại đang tuân thủ nghiêm ngặt **Kiến trúc 3 Tầng (3-Tier Architecture)** kết hợp với mô hình **Clean Architecture**:
* **Tầng Presentation (`routes/`):** 14 file Routing tiếp nhận Request, xác thực JWT và hứng/trả Data chuẩn JSON.
* **Tầng Business Logic (`services/`):** 13 file Service làm nhiệm vụ "Não Bộ", quyết định logic nghiệp vụ (Kiểm tra quyền, nạp thuật toán feed, phân tích hashtag).
* **Tầng Data Access (`repositories/`):** 14 file thao tác trực tiếp với MySQL thông qua Kotlin Exposed.
* **Thành phần hỗ trợ:**
  * `plugins/`: Khởi tạo Ktor (CORS, JWT, Serialization).
  * `database/`: Quản lý kết nối Database (`DatabaseFactory`) và 12 Bảng Table.
  * `models/`: Đóng gói dữ liệu đầu vào (Requests) và đầu ra (Responses - DTOs).

**Đánh giá:** Mã nguồn tuyệt đối gọn gàng, chia tác vụ rõ ràng theo nguyên tắc **Single Responsibility**. Sẵn sàng scale (mở rộng) khi có lượng lớn người dùng.

---

## 2. NHỮNG LỖI NGHIÊM TRỌNG ĐÃ KHẮC PHỤC (BUG FIXES)
Trong quá trình chạy thực tế (Integration Testing) với Postman, chúng ta đã "bắt" và tiêu diệt thành công các lỗi Chí Mạng sau:

1. **Lỗi 500 (Internal Server Error) do JWT Signature:**
   * **Nguyên nhân:** Có sự bất đồng bộ chuỗi Secret Key giữa file `Security.kt` và `JwtManager.kt`.
   * **Khắc phục:** Đồng nhất logic mã hóa, loại bỏ Secret cứng, đọc 100% từ cấu hình `application.conf`.
2. **Lỗi 400 (Self Follow) do Postman Script:**
   * **Nguyên nhân:** Quá trình đăng nhập Postman đọc sai tên biến trả về (`accessToken` thay vì `token`), dẫn đến việc lấy Token cũ (của User 1) cắm vào Request (của User 2), làm cho cả hai bị nhận diện là 1 người và báo lỗi không thể tự follow chính mình.
   * **Khắc phục:** Viết Script đè thẳng vào file JSON để fix vĩnh viễn biến "token" lúc Login.
3. **Lỗi 500 (Ambiguous Column - Cột Mơ Hồ) ở phần Interactions:**
   * **Nguyên nhân:** Hàm `getFollowers` sử dụng `innerJoin` mặc định trong khi cả 2 cột `followerId` và `followingId` đều trỏ về bảng Users, làm SQL bị "lú".
   * **Khắc phục:** Explicit Join quy định cực kỳ rõ ràng ánh xạ (Mapping) giữa các Table PK/FK ở `InteractionRepository.kt`.
4. **Lỗi 404 (Not Found) ở GET User Profile:**
   * **Nguyên nhân:** Trong `UserRoutes.kt` thiếu định nghĩa (Endpoint lộ ra Internet) cho hàm `GET /api/v1/users/{id}`.
   * **Khắc phục:** Đã chèn bổ sung route vào file, nối liền Front và Back.

---

## 3. BỘ CÔNG CỤ POSTMAN (API CORE MVP)
> **THÀNH QUẢ LỚN NHẤT CỦA PHASE NÀY LÀ BỘ FILE: `InstaGallery_Local.postman_collection.json`**

Thay vì bơi trong "rừng" 94 APIs, chúng ta đã cô lập thành công **20 API Sống Còn (Core MVP)**, chia làm 4 Gói (Packages) và nhúng sẵn vào Postman Collection. App Android chỉ cần đụng đúng 4 Gói này là vận hành trơn tru:

### 📦 GÓI 1: KHAI PHÁ HỆ SINH THÁI (AUTH & FOLLOW)
1. **Login / Register:** Tạo tài khoản và Cấp JWT Token tự động (`POST /api/v1/auth/login`).
2. **User Profile:** Lấy thông tin cá nhân của một người dùng bất kỳ (`GET /api/v1/users/{id}`).
3. **Mạng lưới bạn bè:** Bấm Theo Dõi người khác (`POST /api/v1/users/{id}/follow`) và Xem danh sách Followers/Following.

### 📦 GÓI 2: THUẬT TOÁN KHÁM PHÁ (EXPLORE & SEARCH)
1. **Lướt Bảng Tin Khám Phá:** Thuật toán nhả Random Bài Viết ra cho người dùng (`GET /api/v1/explore`).
2. **Thẻ Trending:** Thống kê Hashtag thịnh hành (`GET /api/v1/explore/trending`).
3. **Tìm Kiếm Đa Năng (Global Search):** Gõ 1 từ khóa ra cả Tên, Hashtag, Bài viết (`GET /api/v1/search`). Lịch sử tìm kiếm được bảo quản an toàn (Lưu History).

### 📦 GÓI 3: TIN NHẮN THỜI GIAN THỰC (WEBSOCKETS CHAT)
1. **Lịch sử Hội Thoại:** Nạp danh sách Nhắn tin (Inbox) và Chi tiết tin nhắn quá khứ.
2. **Kết nối Real-Time:** Ống xả WebSocket `ws://localhost:8080/api/v1/ws/chat?token={token}` hoạt động cực kỳ mượt. Server phản ứng lại `NEW_MESSAGE` ngay tích tắc.

### 📦 GÓI 4: QUẢN LÝ VÒNG ĐỜI NGƯỜI DÙNG (EXTENDED CORE)
1. **Cập nhật Profile:** Đổi Tên, Đổi Ảnh Đại Diện (`PUT /api/v1/users/me`).
2. **Bảo mật:** Đổi mật khẩu, Cấp lại Token chống Disconnect (`POST /api/v1/auth/refresh`).
3. **Đá văng thiết bị:** Xem danh sách thiết bị đang Login và Đăng Xuất từ xa (`GET / DELETE /api/v1/users/me/sessions`).

---

## 4. CHIẾN LƯỢC CHO GIAI ĐOẠN 2 (PHASE 2 - ANDROID COMPOSE)

* **Nguyên Tắc Thép:** Không tiếp tục nới rộng Backend nữa (Không đụng đến 74 APIs còn lại lúc này).
* Theo phương pháp **Vertical Slicing (Cắt Lát Chiều Dọc)**: Chúng ta đã có Đáy Bê Tông (20 API Core Backend). Công việc tiếp theo là xây Tường + Trát Vữa bằng **Android Jetpack Compose**,
* Ứng dụng Android sẽ xoay quanh mô hình **Vibe Coding, ASH (Agents-Skills-Hooks)**:
   * Setup Koin, Ktor Client (chuyên gọi lên Backend).
   * Tạo Màn Hình Đăng Nhập -> Cắm API Login vào.
   * Tạo Màn Hình Newfeed -> Cắm API Lấy Bài Viết vào.
   * Tạo Khung Chat Realtime -> Cắm ống WS Ktor vào!

**🎉 TỔNG KẾT: Backend đã đi vào quỹ đạo siêu ổn định. Bạn đã có bản kiểm kê toàn diện. Tương lai phía trước là Màn Hình App lung linh của Android!**
