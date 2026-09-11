<div align="center">
  <img src="https://raw.githubusercontent.com/github/explore/80688e429a7d4ef2fca1e82350fe8e3517d3494d/topics/kotlin/kotlin.png" width="100" alt="Kotlin Logo">
  <br/>
  <h1>📸 InstaGallery Backend Core API</h1>
  <p><b>Hệ thống Backend Hiệu Năng Cao cho Nền tảng Mạng Xã Hội Hình Ảnh Chuyên Nghiệp</b></p>

  [![Kotlin](https://img.shields.io/badge/Kotlin-2.0.0-7F52FF.svg?style=for-the-badge&logo=kotlin)](https://kotlinlang.org)
  [![Ktor Framework](https://img.shields.io/badge/Ktor-3.0.2-087CFA.svg?style=for-the-badge&logo=ktor)](https://ktor.io)
  [![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1.svg?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
  [![Redis](https://img.shields.io/badge/Redis-7.0-DC382D.svg?style=for-the-badge&logo=redis&logoColor=white)](https://redis.io/)
  [![Gradle](https://img.shields.io/badge/Gradle-8.10.2-02303A.svg?style=for-the-badge&logo=gradle&logoColor=white)](https://gradle.org)
</div>

<hr/>

## 📖 1. Tổng quan Dự án (Project Overview)
**InstaGallery** không chỉ là một ứng dụng chia sẻ hình ảnh thông thường, mà là một **Hệ sinh thái Nhiếp ảnh Thu nhỏ**, nơi người dùng phổ thông và các Nhiếp ảnh gia chuyên nghiệp có thể giao lưu, kết nối và hợp tác. 

Hệ thống Backend được thiết kế theo tiêu chuẩn sản phẩm thương mại (Production-grade), áp dụng tư tưởng **Clean Architecture** kết hợp với mô hình **Event-driven** (thông qua WebSockets & Caching) nhằm đảm bảo khả năng xử lý truy xuất khối lượng lớn (high-throughput), mở rộng dễ dàng (scalability) và tính toàn vẹn của dữ liệu với **30 bảng CSDL liên kết chặt chẽ**.

---

## 🏛 2. Kiến trúc & Design Pattern (Architecture)
Toàn bộ mã nguồn được tuân thủ nghiêm ngặt theo các tiêu chuẩn kiến trúc phần mềm hiện đại:
- **Layered Architecture**: Phân tách rõ ràng giữa `Routes` (Controller Layer), `Services` (Business Logic Layer), và `Repositories` (Data Access Layer).
- **Dependency Injection (DI)**: Tích hợp **Koin** để quản lý Lifecycle của các Bean, đảm bảo tính Decoupling và hỗ trợ Mock Test dễ dàng.
- **ORM & Type-Safe SQL**: Sử dụng **JetBrains Exposed** với syntax Kotlin DSL, mang lại khả năng Query Type-safe và ngừa tối đa lỗ hổng SQL Injection.
- **Real-time Pipeline**: Giao tiếp Full-duplex thông qua **Ktor WebSockets** cho ứng dụng Chat và phân phối Notification tĩnh.

---

## ⚙️ 3. Hệ sinh thái Công Nghệ (Tech Stack Ecosystem)

| Phân lớp (Layer) | Công nghệ / Thư viện | Vai trò cốt lõi |
| :--- | :--- | :--- |
| **Language & Engine** | `Kotlin 2.0.0` / `Coroutines` | Xử lý đa luồng bất đồng bộ (Non-blocking I/O) tối ưu hiệu năng. |
| **Web Framework** | `Ktor 3.0.2` (Netty Engine) | Xử lý HTTP Requests/Responses & WebSockets, cung cấp routing DSL. |
| **Relational Database** | `MySQL 8.0` & `Exposed` | RDBMS với mô hình dữ liệu quan hệ phức tạp (30 entities). |
| **Caching Layer** | `Redis 7` | Caching Session, tối ưu hóa các Queries bảng xếp hạng (Trending). |
| **Authentication** | `JWT` & `Bcrypt` | Phân cấp Access/Refresh Token, mã hóa mật khẩu an toàn. |
| **DevOps & Infra** | `Docker Compose` | Quản lý Containerizing các services (DB, Redis) linh hoạt. |
| **Build Tool** | `Gradle 8.10.2` | Tự động hóa quá trình đóng gói và quản lý dependencies. |

---

## ✨ 4. Đặc tả Tính năng Chuyên sâu (Core Features)

### 🔐 4.1. Identity & Access Management (IAM)
- **Token Rotation System**: Cung cấp cơ chế Access Token (ngắn hạn) kèm Refresh Token (dài hạn), cấu hình thu hồi quyền bằng JWT Blacklisting.
- **Defense in Depth**: Bảo mật mật khẩu bằng *Bcrypt hashing*.
- **2-Factor Authentication (2FA)**: Tính năng OTP cho tầng xác thực bảo mật tài khoản cao cấp.
- **Account Controls**: Các cơ chế Deactivate, Block, Mute người dùng chuyên sâu.

### 🌐 4.2. Hệ thống Mạng Xã Hội (Social Graph)
- **Thuật toán Feed**: Phân phối Dòng thời gian (Timeline) theo dạng phân trang (Pagination), tối ưu Cursor.
- **Đa phương tiện (Media Collection)**: Đăng nội dung kèm tối đa 10 ảnh/video, tích hợp thẻ Tag người dùng.
- **Social Interactions**: Hệ thống thả tim (Like), lưu trữ (Bookmark), và bình luận (Nested Comments) với độ phức tạp cao.

### 💬 4.3. Giao tiếp Thời gian Thực (Real-Time Communication)
- **Live Chat Engine**: Khởi tạo Connection WebSockets chuyên dụng trên Ktor.
- Cơ chế quản lý Trạng thái Online/Offline, phòng trò chuyện (Conversations/Rooms).

### 📸 4.4. Module Nhiếp Ảnh Gia Chuyên Nghiệp (Photographer Marketplace)
- **Portfolio & Albums**: Tổ chức thư viện ảnh dự án theo Collection.
- **Booking Rules Engine**: Xem lịch rảnh/bận (Availability), thực hiện đặt lịch hẹn thuê chụp ảnh.
- **Reviews & Ratings**: Hành vi đánh giá chéo tạo độ uy tín cho từng Photographer.

---

## 🛡 5. Bảo mật Hệ thống (Enterprise Security)
Hệ thống tích hợp quy trình **7 Tầng Bảo Mật (7-Layer Security Model)**, bao gồm:
1. **CORS Policy & Rate Limiting**: Chặn các cuộc tấn công DDoS và kiểm soát nguồn truy cập.
2. **Ktor StatusPages**: Chuẩn hóa thông báo lỗi định dạng chung (Standardized Exception Handling), chống rò rỉ (leak) mã nguồn stack trace ra phía Client.
3. **Role-Based Access Control (RBAC)**: Phân tầng quyền hạn tuyệt đối giữa `USER`, `PHOTOGRAPHER`, và đặc biệt là phân vùng riêng cho `ADMIN`.

---

## 📂 6. Cấu trúc Thư mục Dự án

```text
├── src/main/kotlin/com/instagallery/
│   ├── Application.kt             # Entry point, config Netty Engine
│   ├── plugins/                   # Ktor Plugins (Routing, Security, DI, CORS)
│   ├── database/                  # Quản lý Connection Pooling & 30 Schema Tables
│   ├── models/                    # Data Transfer Objects (DTO), Requests, Responses
│   ├── repositories/              # DAO Layer (Exposed SQL Transactions)
│   ├── services/                  # Business Logic Layer (Coroutines-based)
│   ├── routes/                    # API Endpoints chia theo domain (Users, Posts, Admin,...)
│   └── utils/                     # Hashing, Password, Helpers
├── docs/                          # Kho tài liệu (Markdown)
├── docker-compose.yml             # Môi trường chạy nhanh DB & Redis
├── database_seed.sql              # Dữ liệu Dummy Seed để test (Có Admin và Users)
└── build.gradle.kts               # Configurations Gradle (Dependencies, Tasks)
```

---

## 🚀 7. Triển khai & Chạy thử Hệ thống (Getting Started)

Cần cài đặt **JDK 17/21** và **Docker** trên thiết bị của bạn. Vui lòng xem hướng dẫn cực kỳ chi tiết tại [Hướng dẫn Cài Đặt (Setup Guide)](docs/guides/setup_guide.md).

**Quick Start via Docker:**
```bash
# Build va chay Backend + MySQL + Redis
docker compose up --build -d
```

Sau khi ứng dụng khởi chạy (`http://localhost:8080`), hãy dùng trình duyệt, Postman, hoặc Curl để kiểm tra:
```bash
curl http://localhost:8080/health
```

---

## 🗂 8. Tài liệu API Thực Tế (API Documentation)
Toàn bộ dự án hiện có **139 REST APIs dưới `/api/v1`**, **5 HTTP routes hệ thống**, và **1 WebSocket endpoint** đã được ghi chép đặc tả chi tiết.
- Xem danh sách và luồng chạy: [📖 Backend API Overview](docs/api-specs/Backend_APIs.md)
- Mô hình dữ liệu quan hệ: [🗄️ Database Schema Cấu trúc Liên Kết](docs/database/database_schema.md)
- Postman Collection tự động đi kèm trong source code: `InstaGallery_Local.postman_collection.json`.

---
<p align="center"><i>Kiến tạo bởi tính thẩm mỹ, hoàn thiện bằng chuẩn kỹ thuật cao &mdash; InstaGallery Platform</i></p>
