# 📸 InstaGallery Backend API

<div align="center">
  <img src="https://img.shields.io/badge/Kotlin-2.0.0-blueviolet.svg?style=flat&logo=kotlin" alt="Kotlin">
  <img src="https://img.shields.io/badge/Ktor-3.0.2-blue.svg?style=flat&logo=ktor" alt="Ktor">
  <img src="https://img.shields.io/badge/MySQL-8.0-orange.svg?style=flat&logo=mysql" alt="MySQL">
  <img src="https://img.shields.io/badge/Redis-7.0-red.svg?style=flat&logo=redis" alt="Redis">
  <img src="https://img.shields.io/badge/Gradle-8.10.2-lightgrey.svg?style=flat&logo=gradle" alt="Gradle">
</div>

---

## 📖 Giới thiệu
**InstaGallery** là hệ thống Backend cho nền tảng Mạng xã hội hình ảnh chuyên nghiệp (với phong cách tương tự Instagram). Dự án cung cấp đầy đủ các API RESTful và WebSocket để phục vụ Client như Android (Jetpack Compose) cũng như Web/iOS. Hệ thống hỗ trợ các nền tảng nghiệp vụ từ cơ bản (Post, Like, Comment, Follow) cho tới các hệ thống nâng cao (Realtime Chat, Đặt lịch Nhiếp Ảnh Gia, Admin System).

## 🚀 Tính năng nổi bật
* **Bảo mật & Xác thực (Auth)**: Sử dụng JWT (Access/Refresh Token chia cấu trúc an toàn), Google OAuth, thiết lập bảo mật 2FA, mã OTP.
* **Hệ thống Mạng Xã Hội**: Đăng bài (tối đa 10 Media), hiển thị Dòng thời gian (Feed), Tương tác Like, Comment, Save bài viết, Explore tự động.
* **Tính năng Theo Dõi**: Follow/Unfollow, Ẩn (Mute) và Chặn (Block) tài khoản một cách bảo mật.
* **Realtime Chat**: Liên lạc trực tiếp, đồng bộ hóa thời gian thực bằng WebSockets.
* **Hệ thống Nhiếp Ảnh Gia**: Tìm kiếm Portfolio, cấu hình lịch làm việc, Đặt lịch (Booking) và ghi nhận Đánh giá (Rating/Reviews).
* **Quản trị viên (Admin)**: Dashboard thống kê hệ thống, xử lý khiếu nại (Report), và khóa tính năng/xóa bài vi phạm.
* **Database thiết kế mở rộng**: Hỗ trợ 30+ bảng được quản lý thông qua JetBrains Exposed ORM.

## 🛠 Công nghệ Sử dụng (Tech Stack)
- **Ngôn ngữ**: Kotlin 2.0.0
- **Web Framework**: Ktor 3.0.2 (Netty engine)
- **Database ORM**: JetBrains Exposed
- **Database chính**: MySQL 8.0
- **Bộ nhớ đệm (Cache)**: Redis 7
- **Dependency Injection**: Koin 

## 📂 Tài liệu Tham khảo
Đọc thêm tài liệu chuyên sâu được đính kèm ở thư mục `docs/`:
1. [⚙️ Hướng dẫn Cài Đặt (Local & Docker)](docs/setup_guide.md)
2. [📡 Danh sách Toàn bộ 85+ Endpoint APIs](docs/Backend_APIs.md)
3. [🗄️ Thiết kế Database Schema (30 Bảng)](docs/database_schema.md)

## 🏃 Bắt đầu cực nhanh (Quick Start)
Chỉ với 2 bước sử dụng Docker Compose:
```bash
# 1. Khởi chạy Database MySQL và Redis
docker-compose up -d

# 2. Xây dựng và khởi chạy ứng dụng (Yêu cầu có JDK 17/21)
./gradlew run
```
*Ghi chú: Lần chạy đầu tiên Ktor sẽ tự động tạo bảng (Migrate schema) và lắng nghe mặc định tại `http://localhost:8080`.*

---
*Developed with ❤️ for InstaGallery Platform.*
