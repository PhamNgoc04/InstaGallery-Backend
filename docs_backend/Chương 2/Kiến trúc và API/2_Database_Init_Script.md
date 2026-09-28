# KHỞI TẠO CƠ SỞ DỮ LIỆU INSTAGALLERY

> Cập nhật theo `docs_backend`: backend Ktor hiện dùng JetBrains Exposed để tạo và migrate schema. Vì vậy file này không còn giữ vai trò là một DDL thủ công như bản cũ.

## 1. Nguồn schema chuẩn

Schema mới nhất được mô tả tại `docs/database/database_schema.md`. Script SQL thủ công nằm ở `database/migrations/V001__booking_availability_id.sql` và `database/migrations/V002__chat_shares_constraints.sql`.

Quy mô hiện tại:

- `36` bảng dữ liệu.
- MySQL 8.x.
- ORM: JetBrains Exposed.
- Schema được tạo lúc khởi động khi không chạy production. Không còn route HTTP `/init-db`, `/reset-db`, `/migrate-db`.

## 2. Danh sách 36 bảng hiện tại

### Auth & Identity

- users
- user_sessions
- password_reset_tokens

### Nội dung & Media

- posts
- post_media
- filters
- media_tags
- post_media_tags
- post_tagged_users
- post_shares

### Tương tác xã hội

- likes
- comment_likes
- comment_dislikes
- comment_reactions
- comments
- saved_posts

### Quan hệ người dùng

- followers
- follow_requests
- blocked_users
- muted_users

### Nhắn tin

- conversations
- conversation_members
- messages

### Album

- albums
- album_media

### Dịch vụ nhiếp ảnh & Đặt lịch

- portfolios
- photographer_services
- availability_schedules
- bookings
- ratings

### Thông báo, thiết bị, tìm kiếm, kiểm duyệt

- notifications
- device_tokens
- search_histories
- reports
- banned_words
- activity_logs

## 3. Cách khởi tạo trong backend

Khi chạy backend ở môi trường local/dev:

1. Ktor đọc cấu hình kết nối MySQL.
2. `DatabaseFactory` mở kết nối thông qua HikariCP.
3. Nếu `KTOR_ENV` không phải `production` hoặc `prod`, Exposed tạo bảng và cột còn thiếu.
4. Production không tự sửa schema và không có route HTTP để xóa hay tạo lại database.

## 4. Ghi chú cho báo cáo

Trong báo cáo đồ án, nên trình bày đây là cơ chế khởi tạo schema bằng ORM thay vì dán toàn bộ SQL DDL thủ công. Danh sách bảng và mô tả chi tiết lấy theo `database_schema.md`.
