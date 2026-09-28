# 00. Tổng quan backend InstaGallery

> Cập nhật 23/09/2026: route debug database đã gỡ, WebSocket và search verify JWT, upload local cần token, secret không còn hardcode trong `application.conf`, mã reset được băm, CORS không còn `anyHost()`, bài `FRIENDS_ONLY` và tài khoản riêng tư được chặn với người ngoài. Các mục bên dưới mô tả kiến trúc; mục “Điểm yếu” đã được sửa cho phần bảo mật vừa nêu.

## Mục đích hệ thống
`instagallery-backend` là backend REST API/WebSocket cho một ứng dụng mạng xã hội chia sẻ ảnh có mở rộng nghiệp vụ booking nhiếp ảnh gia. Nhìn từ code hiện tại, hệ thống đang phục vụ 4 nhóm chức năng chính:

- Xác thực người dùng: đăng ký, đăng nhập, refresh token, đổi mật khẩu, reset mật khẩu.
- Social feed: bài viết, like, save, comment, follow, explore, search, notification.
- Marketplace cho nhiếp ảnh gia: portfolio, booking, rating.
- Moderation và vận hành: report, admin stats, admin moderation.

## Công nghệ đang dùng
- Ngôn ngữ: Kotlin 2.0.0
- Framework: Ktor 3.0.2
- Database access: Exposed JDBC + HikariCP
- Database chính: MySQL
- Auth: JWT access token + refresh token lưu DB
- Password hashing: BCrypt
- DI: Koin
- Serialization: `kotlinx.serialization`
- Realtime: Ktor WebSocket
- Build tool: Gradle Kotlin DSL
- Test: Ktor test host, JUnit 5, MockK, H2

## Entry point khởi động
File [Application.kt](/d:/InstaGallery/instagallery-backend/src/main/kotlin/com/instagallery/Application.kt:1) là điểm vào chính:

```mermaid
flowchart TD
    A[main] --> B[embeddedServer Netty 8080]
    B --> C[module]
    C --> D[Dependency Injection]
    C --> E[Serialization]
    C --> F[Security JWT]
    C --> G[CORS]
    C --> H[Database]
    C --> I[Rate Limiting]
    C --> J[StatusPages]
    C --> K[WebSockets]
    C --> L[Routing]
```

## Kiến trúc tổng thể đang áp dụng
Project đang đi theo hướng:

```text
Route -> Service -> Repository -> Database/Table
```

Đây là kiến trúc backend khá phổ biến và dễ học. Tuy nhiên ở code hiện tại nó mới là layered architecture ở mức thực dụng, chưa phải Clean Architecture đầy đủ. Một số service vẫn truy cập trực tiếp `UsersTable` hoặc `DatabaseFactory.dbQuery`, và một số endpoint đang trả dữ liệu mock/TODO.

## Điểm mạnh hiện tại
- Cấu trúc package rõ ràng, dễ lần theo luồng xử lý.
- Nhiều domain đã được tách route/service/repository riêng.
- Có `StatusPages`, JWT auth, refresh token, BCrypt.
- Có test unit và integration cơ bản, `./gradlew.bat test` hiện chạy thành công trong môi trường này.

## Điểm còn lại
- `UserAccount` nội bộ vẫn giữ `passwordHash` để kiểm tra đăng nhập. `UserDto` trả ra API không còn trường này.
- Một số service vẫn ghi thẳng bảng, ví dụ cập nhật privacy.
- 2FA vẫn trả `501` vì chưa có luồng OTP thật.
- Chưa có migration có phiên bản. Production không tự sửa schema.

## Kết luận nhanh
Đây là một backend học tập và prototype khá đầy đủ tính năng. Nó tốt để học luồng Ktor + Exposed + JWT + repository pattern, nhưng chưa an toàn để coi là production-ready nếu chưa xử lý lại security, contract API, migration, validation và nhất quán kiến trúc.
