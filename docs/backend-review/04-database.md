# 04. Database

## Cách kết nối DB
Project dùng:
- MySQL Connector/J
- HikariCP
- Exposed JDBC

Luồng khởi tạo nằm ở [DatabaseFactory.kt](/d:/InstaGallery/instagallery-backend/src/main/kotlin/com/instagallery/database/DatabaseFactory.kt:13):

- Đọc `database.url`, `database.user`, `database.password`
- Tạo `HikariConfig`
- `Database.connect(dataSource)`
- `SchemaUtils.create(...)` toàn bộ bảng khi app khởi động

## Cấu hình connection
Các cấu hình đáng chú ý:
- `maximumPoolSize = 10`
- `isAutoCommit = false`
- `transactionIsolation = TRANSACTION_REPEATABLE_READ`

Nhìn chung ổn cho local/prototype.

## Các bảng quan trọng

### `users`
Nguồn: [UsersTable.kt](/d:/InstaGallery/instagallery-backend/src/main/kotlin/com/instagallery/database/tables/UsersTable.kt:1)

Chứa:
- thông tin đăng nhập: email, username, password_hash
- hồ sơ: full_name, bio, website, gender, phone, dob, location
- phân quyền: role, user_type
- auth nâng cao: provider, providerId, 2FA
- trạng thái: isPrivate, isVerified, isActive
- counter: followerCount, followingCount, postCount
- soft delete: deletedAt

### `user_sessions`
Nguồn: [UserSessionsTable.kt](/d:/InstaGallery/instagallery-backend/src/main/kotlin/com/instagallery/database/tables/UserSessionsTable.kt:1)

Chứa:
- refresh token
- thiết bị
- IP
- hạn dùng

Đây là nền tảng cho multi-device session.

### `posts`
Nguồn: [PostsTable.kt](/d:/InstaGallery/instagallery-backend/src/main/kotlin/com/instagallery/database/tables/PostsTable.kt:1)

Chứa:
- user_id
- caption, location
- visibility
- comment visibility
- like/comment/share counter
- deletedAt để soft delete

### `bookings`
Nguồn: [BookingsTable.kt](/d:/InstaGallery/instagallery-backend/src/main/kotlin/com/instagallery/database/tables/BookingsTable.kt:1)

Chứa:
- clientId
- photographerId
- bookingDate
- duration, location, details
- price, currency
- status, cancellationReason

### `reports`
Nguồn: [ReportsTable.kt](/d:/InstaGallery/instagallery-backend/src/main/kotlin/com/instagallery/database/tables/ReportsTable.kt:1)

Chứa:
- reporterId
- targetType, targetId
- reason
- adminNote
- reviewedBy
- status

## Transaction đang dùng thế nào
- `DatabaseFactory.dbQuery { ... }` dùng `newSuspendedTransaction(Dispatchers.IO)`.
- Đây là pattern đúng cho Ktor coroutine + Exposed.
- Hầu hết repository đều đi qua `dbQuery`.

## Migration hiện tại
Hiện tại project chưa có hệ migration chuyên nghiệp kiểu Flyway/Liquibase.

Thay vào đó:
- app startup gọi `SchemaUtils.create(...)`
- route `/migrate-db` gọi `SchemaUtils.createMissingTablesAndColumns(...)`
- route `/reset-db` gọi `SchemaUtils.drop(...)` rồi tạo lại

## Vấn đề database hiện tại

### 1. Không có migration versioned
Rủi ro:
- khó deploy nhiều môi trường
- khó rollback
- khó biết schema thay đổi khi nào

### 2. Có route phá hoại DB mở công khai
Nguồn: [plugins/Routing.kt](/d:/InstaGallery/instagallery-backend/src/main/kotlin/com/instagallery/plugins/Routing.kt:29)

Các route:
- `/init-db`
- `/reset-db`
- `/fix-user-id`
- `/migrate-db`

Đây là rủi ro rất lớn nếu deploy ra môi trường có internet.

### 3. Hardcode DB credentials fallback
Nguồn: [DatabaseFactory.kt](/d:/InstaGallery/instagallery-backend/src/main/kotlin/com/instagallery/database/DatabaseFactory.kt:17), [application.conf](/d:/InstaGallery/instagallery-backend/src/main/resources/application.conf:18)

## Gợi ý cải thiện
- Dùng Flyway hoặc Liquibase.
- Bỏ toàn bộ route admin DB ra khỏi runtime public.
- Dùng `.env` hoặc secret manager, không để password fallback trong source.
- Thêm index/review index theo query thực tế cho feed, search, notification.
