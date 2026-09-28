# 04. Database

> Cập nhật 23/09/2026: không còn route HTTP xóa hoặc tạo database. `DatabaseFactory` chỉ gọi `SchemaUtils.createMissingTablesAndColumns` khi không phải production. `application.conf` không còn mật khẩu mặc định.

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
- bản băm HMAC-SHA256 của refresh token, không lưu chuỗi thô
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
- clientId, photographerId, serviceId
- availabilityId của khung giờ đã khớp
- bookingDate, duration, location, details
- price, currency
- status `PENDING`, `CONFIRMED`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`, `REJECTED`
- cancellationReason

Giữ chỗ và kiểm tra trùng giờ nằm trong một transaction. `is_booked` không được dùng để nhận booking.

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
- `DatabaseFactory.dbQuery { ... }` dùng `newSuspendedTransaction(Dispatchers.IO, TransactionManager.defaultDatabase)`.
- Đây là pattern đúng cho Ktor coroutine + Exposed.
- Hầu hết repository đều đi qua `dbQuery`.

## Migration hiện tại
Project có script SQL thủ công `database/migrations/V001__booking_availability_id.sql` và `V002__chat_shares_constraints.sql`. Chưa gắn Flyway hay Liquibase.

Cơ chế hiện tại trong `DatabaseFactory`:
- Khi chạy môi trường dev/test (`KTOR_ENV != "production"`), ứng dụng tự động gọi `SchemaUtils.createMissingTablesAndColumns(...)` cho toàn bộ **36 bảng** cơ sở dữ liệu. Bước này thêm bảng và cột thiếu, không đáng tin khi thêm unique index lên bảng đã có dữ liệu trùng.
- Đồng bộ bộ đếm bình luận cây (`CommentTreeVisibility.syncAllPostCommentCounts()`) và backfill dữ liệu phản ứng bình luận (`backfillCommentReactions()`).
- Khi `KTOR_ENV=production`, server tuyệt đối không tự ý can thiệp hoặc sửa đổi schema để tránh rủi ro dữ liệu.
- Các route HTTP phá database (`/init-db`, `/reset-db`, `/fix-user-id`, `/migrate-db`) đã được gỡ bỏ hoàn toàn khỏi hệ thống.

## Vấn đề database hiện tại

### 1. Chưa có Flyway/Liquibase
Đã có hai script SQL `V001` và `V002` chạy tay. Startup ngoài production vẫn dựa vào Exposed. Unique index trên bảng cũ cần script, vì `SchemaUtils` không xử lý dữ liệu trùng.

### 2. Route phá database đã gỡ
`/init-db`, `/reset-db`, `/fix-user-id` và `/migrate-db` không còn trong `Routing.kt`.

### 3. Secret không còn hardcode trong `application.conf`
Local thiếu biến môi trường thì `RequiredConfig` dùng mật khẩu development. `KTOR_ENV=production` từ chối mật khẩu `123456789` và secret JWT cũ.

## Gợi ý còn lại
- Dùng Flyway hoặc Liquibase thay cho `SchemaUtils` lúc khởi động.
- Thêm index theo query thực tế cho feed và search nếu dữ liệu lớn.
