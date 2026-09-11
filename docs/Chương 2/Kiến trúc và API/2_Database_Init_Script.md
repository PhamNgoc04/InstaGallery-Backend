# KHỞI TẠO CƠ SỞ DỮ LIỆU INSTAGALLERY

> Cập nhật theo `docs_backend`: backend Ktor hiện dùng JetBrains Exposed để tạo và migrate schema. Vì vậy file này không còn giữ vai trò là một DDL thủ công như bản cũ.

## 1. Nguồn schema chuẩn

Schema mới nhất được mô tả tại:

- `docs_backend/database_schema.md`
- `Nội dung Backend/database_schema.md`
- `Nội dung Backend/instagallery_db_analysis.md`

Quy mô hiện tại:

- `30` bảng dữ liệu.
- MySQL 8.x.
- ORM: JetBrains Exposed.
- Các route hỗ trợ local/dev: `/init-db`, `/reset-db`, `/migrate-db`.

## 2. Danh sách 30 bảng hiện tại

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

### Tương tác xã hội

- likes
- comment_likes
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

### Dịch vụ nhiếp ảnh

- portfolios
- availability_schedules
- bookings
- ratings

### Thông báo, tìm kiếm, kiểm duyệt

- notifications
- search_histories
- reports
- banned_words
- activity_logs

## 3. Cách khởi tạo trong backend

Khi chạy backend ở môi trường local/dev:

1. Ktor đọc cấu hình kết nối MySQL.
2. `DatabaseFactory` mở kết nối thông qua HikariCP.
3. Exposed kiểm tra schema và tạo bảng/cột còn thiếu.
4. Các route `/init-db`, `/reset-db`, `/migrate-db` hỗ trợ thao tác nhanh trong quá trình phát triển.

Các route này có tác động trực tiếp đến database, vì vậy không dùng tùy tiện trong môi trường production.

## 4. Ghi chú cho báo cáo

Trong báo cáo đồ án, nên trình bày đây là cơ chế khởi tạo schema bằng ORM thay vì dán toàn bộ SQL DDL thủ công. Danh sách bảng và mô tả chi tiết lấy theo `database_schema.md`.
