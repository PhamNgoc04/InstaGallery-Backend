# 01. Cấu trúc project

## Cây thư mục chính

```text
src/
  main/
    kotlin/com/instagallery/
      Application.kt
      database/
      models/
      plugins/
      repositories/
      routes/
      services/
      utils/
    resources/
      application.conf
      logback.xml
  test/
    kotlin/com/instagallery/
```

## Giải thích từng phần

### `src/main/kotlin/com/instagallery/Application.kt`
- Điểm khởi động server.
- Gọi lần lượt các `configure...()` để cài plugin và route.
- Có `try/catch` để ghi `crash_report.txt` khi server crash.

### `database/`
- [DatabaseFactory.kt](/d:/InstaGallery/instagallery-backend/src/main/kotlin/com/instagallery/database/DatabaseFactory.kt:13): tạo `HikariDataSource`, `Database.connect`, và `SchemaUtils.create(...)`.
- `tables/`: khai báo schema Exposed cho từng bảng.

### `database/tables/`
Một số bảng nổi bật:
- `UsersTable`: user, role, type, OAuth, 2FA, privacy, counter.
- `UserSessionsTable`: refresh token theo thiết bị.
- `PostsTable`: caption, visibility, comment visibility, counter.
- `PostMediaTable`, `MediaTagsTable`, `PostMediaTagsTable`: media và hashtag.
- `FollowersTable`, `FollowRequestsTable`, `BlockedUsersTable`, `MutedUsersTable`: social graph.
- `CommentsTable`, `CommentLikesTable`, `LikesTable`, `SavedPostsTable`: tương tác.
- `PortfoliosTable`, `BookingsTable`, `RatingsTable`, `AvailabilitySchedulesTable`: nghiệp vụ nhiếp ảnh gia.
- `ConversationsTable`, `ConversationMembersTable`, `MessagesTable`: chat.
- `NotificationsTable`, `ActivityLogsTable`, `ReportsTable`, `SearchHistoriesTable`: vận hành và moderation.
- `AlbumsTable`, `AlbumMediaTable`, `BannedWordsTable`, `PasswordResetTokensTable`: tính năng mở rộng.

### `models/common`
- Chứa DTO dùng để trả response hoặc trao đổi dữ liệu giữa các tầng.
- `ApiResponse.kt`: wrapper response chuẩn của project.
- `Enums.kt`: enum dùng xuyên suốt hệ thống.
- `UserDto`, `PostDto`, `BookingDto`, `RatingDto`, `NotificationDto`...

### `models/request`
- Chứa request body cho từng module.
- Ví dụ: `RegisterRequest`, `CreatePostRequest`, `CreateBookingRequest`, `CreateRatingRequest`.
- Đây là nơi frontend gửi dữ liệu vào server.

### `models/response`
- Hiện mới tập trung vào auth response.
- Ví dụ: `LoginResponse`, `RegisterResponse`, `TokenRefreshResponse`, `UserSessionsResponse`.

### `plugins`
- Nơi cài Ktor plugin và cấu hình global.
- `Security.kt`: JWT auth.
- `StatusPages.kt`: exception -> HTTP response.
- `Routing.kt`: đăng ký tất cả route.
- `Database.kt`, `Serialization.kt`, `CORS.kt`, `RateLimiting.kt`, `WebSockets.kt`, `DependencyInjection.kt`.

### `repositories`
- Tầng truy cập dữ liệu.
- Chứa query Exposed, join, pagination, insert/update/delete.
- Mỗi repository thường bám theo một domain: user, post, booking, rating, report...

### `services`
- Tầng business logic.
- Validate input, authorize, áp quy tắc nghiệp vụ, gọi repository.
- Đây là nơi nên tập trung phần “luật” của hệ thống.

### `routes`
- Tầng HTTP/WebSocket.
- Nhận request, đọc JWT principal, parse body/query/path param, gọi service, trả `ApiResponse`.

### `utils`
- `JwtManager.kt`: tạo và verify JWT.
- `PasswordHasher.kt`: BCrypt hash/check.
- `ConnectionManager.kt`: giữ map userId -> WebSocket session.

### `resources/application.conf`
- Cấu hình Ktor, JWT, database.
- Hỗ trợ đọc env như `JWT_SECRET`, `DB_URL`, `DB_USER`, `DB_PASSWORD`.

### `build.gradle.kts`
- Khai báo plugin, dependency, cấu hình test.

### `src/test`
- Có test cho auth, post, interaction, user.
- Đây là điểm tích cực vì project không hoàn toàn thiếu test.

## Nhận xét về cấu trúc
- Về mặt học tập: cấu trúc rất dễ đọc.
- Về mặt production: cần tách rõ hơn giữa DTO public và model nội bộ, đồng thời giảm việc service chạm trực tiếp table/database.
