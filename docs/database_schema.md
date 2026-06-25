# 🗄️ InstaGallery — Tổng Hợp Cấu Trúc Database

> **Công nghệ**: MySQL · **ORM**: Jetbrains Exposed · **Tổng số bảng**: 30
>
> Cập nhật lần cuối: 2026-06-25

---

## 📑 Mục Lục

| # | Nhóm | Các bảng |
|---|------|----------|
| 1 | [🔐 Auth & Identity](#-auth--identity) | `users`, `user_sessions`, `password_reset_tokens` |
| 2 | [📸 Nội dung & Media](#-nội-dung--media) | `posts`, `post_media`, `filters`, `media_tags`, `post_media_tags` |
| 3 | [💬 Tương tác xã hội](#-tương-tác-xã-hội) | `likes`, `comment_likes`, `comments`, `saved_posts` |
| 4 | [👥 Quan hệ người dùng](#-quan-hệ-người-dùng) | `followers`, `follow_requests`, `blocked_users`, `muted_users` |
| 5 | [💌 Nhắn tin](#-nhắn-tin) | `conversations`, `conversation_members`, `messages` |
| 6 | [📁 Album](#-album) | `albums`, `album_media` |
| 7 | [📷 Dịch vụ nhiếp ảnh](#-dịch-vụ-nhiếp-ảnh) | `portfolios`, `availability_schedules`, `bookings`, `ratings` |
| 8 | [🔔 Thông báo](#-thông-báo) | `notifications` |
| 9 | [🔍 Tìm kiếm](#-tìm-kiếm) | `search_histories` |
| 10 | [🛡️ Kiểm duyệt & Audit](#️-kiểm-duyệt--audit) | `reports`, `banned_words`, `activity_logs` |

---

## 📊 ERD Tổng Quan

```mermaid
erDiagram
    USERS ||--o{ POSTS : "đăng"
    USERS ||--o{ LIKES : "thích"
    USERS ||--o{ COMMENTS : "bình luận"
    USERS ||--o{ FOLLOWERS : "follow/following"
    USERS ||--o{ FOLLOW_REQUESTS : "yêu cầu follow"
    USERS ||--o{ BLOCKED_USERS : "chặn"
    USERS ||--o{ MUTED_USERS : "tắt tiếng"
    USERS ||--o| PORTFOLIOS : "sở hữu portfolio"
    USERS ||--o{ BOOKINGS : "đặt lịch (client)"
    USERS ||--o{ BOOKINGS : "nhận đặt (photographer)"
    USERS ||--o{ NOTIFICATIONS : "nhận thông báo"
    USERS ||--o{ USER_SESSIONS : "phiên đăng nhập"
    USERS ||--o{ ALBUMS : "tạo album"
    USERS ||--o{ SAVED_POSTS : "lưu bài"
    USERS ||--o{ SEARCH_HISTORIES : "lịch sử tìm kiếm"
    USERS ||--o{ MESSAGES : "gửi tin nhắn"

    POSTS ||--o{ POST_MEDIA : "chứa media"
    POSTS ||--o{ LIKES : "được thích"
    POSTS ||--o{ COMMENTS : "có bình luận"
    POSTS ||--o{ SAVED_POSTS : "được lưu"
    POSTS ||--o{ ALBUM_MEDIA : "trong album"

    POST_MEDIA ||--o{ POST_MEDIA_TAGS : "được gắn tag"
    MEDIA_TAGS ||--o{ POST_MEDIA_TAGS : "dùng cho media"
    FILTERS ||--o{ POST_MEDIA : "áp dụng filter"

    COMMENTS ||--o{ COMMENT_LIKES : "được thích"
    COMMENTS ||--o{ COMMENTS : "trả lời (self-ref)"

    CONVERSATIONS ||--o{ CONVERSATION_MEMBERS : "có thành viên"
    CONVERSATIONS ||--o{ MESSAGES : "chứa tin nhắn"
    MESSAGES ||--o{ MESSAGES : "trả lời (self-ref)"

    PORTFOLIOS ||--o{ AVAILABILITY_SCHEDULES : "lịch rảnh"
    BOOKINGS ||--o| RATINGS : "được đánh giá"

    ALBUMS ||--o{ ALBUM_MEDIA : "chứa bài viết"
```

---

## 🔐 Auth & Identity

### `users`
> Bảng trung tâm của toàn hệ thống. Lưu thông tin người dùng, xác thực, và cấu hình quyền riêng tư.

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|-----|-------------|-----------|-------|
| `id` | BIGINT | PK, AUTO_INCREMENT | ID người dùng |
| `username` | VARCHAR(50) | UNIQUE INDEX | Tên đăng nhập |
| `email` | VARCHAR(100) | UNIQUE INDEX | Email |
| `password_hash` | VARCHAR(255) | NOT NULL | Mật khẩu đã hash |
| `full_name` | VARCHAR(100) | NOT NULL | Họ và tên |
| `profile_picture_url` | VARCHAR(255) | NULLABLE | URL ảnh đại diện |
| `bio` | TEXT | NULLABLE | Tiểu sử |
| `website` | VARCHAR(255) | NULLABLE | Website cá nhân |
| `gender` | VARCHAR(20) | NULLABLE | Giới tính |
| `phone_number` | VARCHAR(20) | NULLABLE | Số điện thoại |
| `date_of_birth` | DATE | NULLABLE | Ngày sinh |
| `location` | VARCHAR(255) | NULLABLE | Địa chỉ |
| `user_type` | ENUM | DEFAULT `CLIENT` | `CLIENT` / `PHOTOGRAPHER` |
| `role` | ENUM | DEFAULT `USER` | `USER` / `ADMIN` |
| `provider` | ENUM | DEFAULT `LOCAL` | `LOCAL` / `GOOGLE` / `FACEBOOK` |
| `provider_id` | VARCHAR(255) | NULLABLE, UNIQUE | ID từ OAuth provider |
| `is_two_factor_enabled` | BOOLEAN | DEFAULT `false` | Bật 2FA |
| `two_factor_secret` | VARCHAR(255) | NULLABLE | Secret key 2FA |
| `is_private` | BOOLEAN | DEFAULT `false` | Tài khoản riêng tư |
| `is_verified` | BOOLEAN | DEFAULT `false` | Tài khoản xác minh |
| `is_active` | BOOLEAN | DEFAULT `true` | Tài khoản đang hoạt động |
| `follower_count` | INT | DEFAULT `0` | Cache số người theo dõi |
| `following_count` | INT | DEFAULT `0` | Cache số đang theo dõi |
| `post_count` | INT | DEFAULT `0` | Cache số bài đăng |
| `created_at` | TIMESTAMP | DEFAULT NOW | Thời điểm tạo |
| `updated_at` | TIMESTAMP | DEFAULT NOW | Thời điểm cập nhật |
| `deleted_at` | TIMESTAMP | NULLABLE | Soft delete |

**Indexes:** `username`, `email`

---

### `user_sessions`
> Quản lý refresh token theo thiết bị (hỗ trợ đăng xuất theo thiết bị cụ thể).

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|-----|-------------|-----------|-------|
| `id` | BIGINT | PK | ID phiên |
| `user_id` | BIGINT | FK → `users`, INDEX | Chủ phiên |
| `device_info` | VARCHAR(255) | NULLABLE | Thông tin thiết bị |
| `ip_address` | VARCHAR(45) | NULLABLE | IP đăng nhập |
| `refresh_token` | VARCHAR(255) | UNIQUE INDEX | Refresh JWT token |
| `created_at` | TIMESTAMP | DEFAULT NOW | Thời điểm tạo |
| `expired_at` | TIMESTAMP | INDEX | Thời điểm hết hạn |

---

### `password_reset_tokens`
> Token một lần dùng để đặt lại mật khẩu qua email.

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|-----|-------------|-----------|-------|
| `id` | BIGINT | PK | ID token |
| `user_id` | BIGINT | FK → `users` | Chủ sở hữu |
| `token` | VARCHAR(255) | UNIQUE INDEX | UUID / chuỗi hashed |
| `expired_at` | DATETIME | NOT NULL | Thời gian hết hạn |
| `created_at` | TIMESTAMP | DEFAULT NOW | Thời điểm tạo |

---

## 📸 Nội dung & Media

### `posts`
> Bài đăng của người dùng. Hỗ trợ nhiều media, kiểm soát hiển thị và comment visibility.

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|-----|-------------|-----------|-------|
| `id` | BIGINT | PK | ID bài đăng |
| `user_id` | BIGINT | FK → `users` CASCADE, INDEX | Tác giả |
| `caption` | TEXT | NULLABLE | Chú thích bài đăng |
| `location` | VARCHAR(255) | NULLABLE | Địa điểm |
| `visibility` | ENUM | DEFAULT `PUBLIC`, INDEX | `PUBLIC` / `FOLLOWERS` / `PRIVATE` |
| `comment_visibility` | ENUM | DEFAULT `ALLOW_ALL` | `ALLOW_ALL` / `FOLLOWERS` / `DISABLED` |
| `like_count` | INT | DEFAULT `0` | Cache số lượt thích |
| `comment_count` | INT | DEFAULT `0` | Cache số bình luận |
| `share_count` | INT | DEFAULT `0` | Cache số chia sẻ |
| `created_at` | TIMESTAMP | DEFAULT NOW, INDEX | Thời điểm đăng |
| `updated_at` | TIMESTAMP | DEFAULT NOW | Thời điểm sửa |
| `deleted_at` | TIMESTAMP | NULLABLE, INDEX | Soft delete |

**Composite Index:** `idx_user_created(user_id, created_at)`

---

### `post_media`
> Các file media (ảnh/video) đính kèm trong một bài đăng, hỗ trợ carousel.

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|-----|-------------|-----------|-------|
| `id` | BIGINT | PK | ID media |
| `post_id` | BIGINT | FK → `posts` CASCADE, INDEX | Bài đăng chứa |
| `media_file_url` | VARCHAR(500) | NOT NULL | URL file gốc |
| `thumbnail_url` | VARCHAR(500) | NULLABLE | URL thumbnail |
| `media_type` | ENUM | DEFAULT `IMAGE` | `IMAGE` / `VIDEO` |
| `position` | INT | DEFAULT `0` | Thứ tự trong carousel |
| `filter_id` | BIGINT | FK → `filters` SET_NULL, NULLABLE | Filter áp dụng |
| `width` | INT | NULLABLE | Chiều rộng (px) |
| `height` | INT | NULLABLE | Chiều cao (px) |
| `file_size` | BIGINT | NULLABLE | Kích thước file (bytes) |
| `duration` | INT | NULLABLE | Thời lượng video (giây) |
| `metadata` | TEXT | NULLABLE | JSON: EXIF data |
| `created_at` | TIMESTAMP | DEFAULT NOW | Thời điểm tạo |

**Composite Index:** `idx_post_position(post_id, position)`

---

### `filters`
> Danh sách các bộ lọc ảnh có thể áp dụng khi đăng media.

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|-----|-------------|-----------|-------|
| `id` | BIGINT | PK | ID filter |
| `name` | VARCHAR(50) | UNIQUE INDEX | Tên filter |
| `description` | TEXT | NULLABLE | Mô tả |
| `config_json` | TEXT | NULLABLE | JSON: cấu hình filter |
| `preview_url` | VARCHAR(255) | NULLABLE | URL ảnh preview |
| `is_active` | BOOLEAN | DEFAULT `true` | Filter đang hoạt động |

---

### `media_tags`
> Bộ từ khóa/hashtag dùng để gắn nhãn cho media.

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|-----|-------------|-----------|-------|
| `id` | BIGINT | PK | ID tag |
| `name` | VARCHAR(50) | UNIQUE INDEX | Tên hashtag |
| `description` | TEXT | NULLABLE | Mô tả |
| `usage_count` | INT | DEFAULT `0`, INDEX | Số lần sử dụng |
| `created_at` | TIMESTAMP | DEFAULT NOW | Thời điểm tạo |

---

### `post_media_tags`
> Bảng trung gian: gắn nhiều tag vào một media cụ thể.

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|-----|-------------|-----------|-------|
| `media_id` | BIGINT | PK (composite), FK → `post_media` CASCADE | ID media |
| `tag_id` | BIGINT | PK (composite), FK → `media_tags` CASCADE, INDEX | ID tag |
| `created_at` | TIMESTAMP | DEFAULT NOW | Thời điểm gắn tag |

---

## 💬 Tương tác xã hội

### `likes`
> Người dùng thích một bài đăng (mỗi cặp user-post là duy nhất).

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|-----|-------------|-----------|-------|
| `id` | BIGINT | PK | ID like |
| `user_id` | BIGINT | FK → `users` CASCADE | Người thích |
| `post_id` | BIGINT | FK → `posts` CASCADE, INDEX | Bài được thích |
| `created_at` | TIMESTAMP | DEFAULT NOW, INDEX | Thời điểm thích |

**Unique Index:** `uk_user_post_likes(user_id, post_id)`

---

### `comments`
> Bình luận có hỗ trợ nested (trả lời bình luận) với giới hạn độ sâu.

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|-----|-------------|-----------|-------|
| `id` | BIGINT | PK | ID bình luận |
| `post_id` | BIGINT | FK → `posts` CASCADE, INDEX | Bài đăng |
| `user_id` | BIGINT | FK → `users` CASCADE, INDEX | Tác giả |
| `content` | TEXT | NOT NULL | Nội dung |
| `parent_comment_id` | BIGINT | FK → `comments` CASCADE, NULLABLE, INDEX | Trả lời bình luận nào |
| `like_count` | INT | DEFAULT `0` | Cache số lượt thích |
| `reply_count` | INT | DEFAULT `0` | Cache số trả lời |
| `depth` | TINYINT | DEFAULT `0` | Độ sâu lồng nhau |
| `created_at` | TIMESTAMP | DEFAULT NOW | Thời điểm tạo |
| `updated_at` | TIMESTAMP | DEFAULT NOW | Thời điểm sửa |
| `deleted_at` | TIMESTAMP | NULLABLE | Soft delete |

**Composite Index:** `idx_post_created(post_id, created_at)`

---

### `comment_likes`
> Người dùng thích một bình luận cụ thể.

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|-----|-------------|-----------|-------|
| `id` | BIGINT | PK | ID |
| `user_id` | BIGINT | FK → `users` CASCADE | Người thích |
| `comment_id` | BIGINT | FK → `comments` CASCADE, INDEX | Bình luận được thích |
| `created_at` | TIMESTAMP | DEFAULT NOW, INDEX | Thời điểm thích |

**Unique Index:** `uk_user_comment_likes(user_id, comment_id)`

---

### `saved_posts`
> Người dùng lưu bài viết vào bộ sưu tập cá nhân.

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|-----|-------------|-----------|-------|
| `id` | BIGINT | PK | ID |
| `user_id` | BIGINT | FK → `users` CASCADE | Người lưu |
| `post_id` | BIGINT | FK → `posts` CASCADE, INDEX | Bài được lưu |
| `saved_at` | TIMESTAMP | DEFAULT NOW | Thời điểm lưu |

**Unique Index:** `uk_user_post_saved(user_id, post_id)`

---

## 👥 Quan hệ người dùng

### `followers`
> Theo dõi trực tiếp (không cần phê duyệt — dành cho tài khoản công khai).

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|-----|-------------|-----------|-------|
| `follower_id` | BIGINT | PK (composite), FK → `users` CASCADE | Người theo dõi |
| `following_id` | BIGINT | PK (composite), FK → `users` CASCADE, INDEX | Người được theo dõi |
| `created_at` | TIMESTAMP | DEFAULT NOW, INDEX | Thời điểm follow |

> ⚠️ Constraint `follower_id ≠ following_id` được kiểm soát tại tầng application.

---

### `follow_requests`
> Yêu cầu theo dõi (dùng khi tài khoản `is_private = true`).

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|-----|-------------|-----------|-------|
| `id` | BIGINT | PK | ID yêu cầu |
| `follower_id` | BIGINT | FK → `users` CASCADE, INDEX | Người gửi yêu cầu |
| `following_id` | BIGINT | FK → `users` CASCADE, INDEX | Người nhận yêu cầu |
| `status` | ENUM | DEFAULT `PENDING`, INDEX | `PENDING` / `ACCEPTED` / `REJECTED` |
| `created_at` | TIMESTAMP | DEFAULT NOW | Thời điểm gửi |
| `updated_at` | TIMESTAMP | DEFAULT NOW | Thời điểm xử lý |

**Unique Index:** `idx_follower_following_req(follower_id, following_id)`

---

### `blocked_users`
> Người dùng chặn người dùng khác.

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|-----|-------------|-----------|-------|
| `id` | BIGINT | PK | ID bản ghi |
| `blocker_id` | BIGINT | FK → `users` CASCADE, INDEX | Người chặn |
| `blocked_id` | BIGINT | FK → `users` CASCADE, INDEX | Người bị chặn |
| `reason` | VARCHAR(255) | NULLABLE | Lý do chặn |
| `created_at` | TIMESTAMP | DEFAULT NOW | Thời điểm chặn |

**Unique Index:** `idx_blocker_blocked(blocker_id, blocked_id)`

---

### `muted_users`
> Tắt tiếng người dùng khác (vẫn follow nhưng không thấy bài đăng).

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|-----|-------------|-----------|-------|
| `id` | BIGINT | PK | ID bản ghi |
| `muter_id` | BIGINT | FK → `users` CASCADE, INDEX | Người tắt tiếng |
| `muted_id` | BIGINT | FK → `users` CASCADE, INDEX | Người bị tắt tiếng |
| `created_at` | TIMESTAMP | DEFAULT NOW | Thời điểm tắt tiếng |

**Unique Index:** `idx_muter_muted(muter_id, muted_id)`

---

## 💌 Nhắn tin

### `conversations`
> Cuộc hội thoại (có thể là 1-1 hoặc nhóm).

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|-----|-------------|-----------|-------|
| `id` | BIGINT | PK | ID cuộc hội thoại |
| `title` | VARCHAR(255) | NULLABLE | Tên nhóm chat (nếu có) |
| `type` | ENUM | DEFAULT `DIRECT` | `DIRECT` / `GROUP` |
| `created_at` | TIMESTAMP | DEFAULT NOW | Thời điểm tạo |
| `updated_at` | TIMESTAMP | DEFAULT NOW, INDEX | Thời điểm tin nhắn cuối |

**Index:** `idx_updated_at`

---

### `conversation_members`
> Thành viên tham gia cuộc hội thoại với vai trò và trạng thái đọc.

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|-----|-------------|-----------|-------|
| `conversation_id` | BIGINT | PK (composite), FK → `conversations` CASCADE | Cuộc hội thoại |
| `user_id` | BIGINT | PK (composite), FK → `users` CASCADE, INDEX | Thành viên |
| `role` | ENUM | DEFAULT `MEMBER` | `MEMBER` / `ADMIN` |
| `nickname` | VARCHAR(50) | NULLABLE | Biệt danh trong nhóm |
| `is_muted` | BOOLEAN | DEFAULT `false` | Tắt thông báo |
| `joined_at` | TIMESTAMP | DEFAULT NOW | Thời điểm tham gia |
| `last_read_at` | TIMESTAMP | NULLABLE | Đã đọc đến lúc nào |

---

### `messages`
> Tin nhắn trong cuộc hội thoại, hỗ trợ trả lời và nhiều loại nội dung.

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|-----|-------------|-----------|-------|
| `id` | BIGINT | PK | ID tin nhắn |
| `conversation_id` | BIGINT | FK → `conversations` CASCADE, INDEX | Cuộc hội thoại |
| `sender_id` | BIGINT | FK → `users` CASCADE, INDEX | Người gửi |
| `content` | TEXT | NULLABLE | Nội dung văn bản |
| `message_type` | ENUM | DEFAULT `TEXT` | `TEXT` / `IMAGE` / `VIDEO` / `FILE` |
| `media_url` | VARCHAR(500) | NULLABLE | URL file đính kèm |
| `reply_to_id` | BIGINT | FK → `messages` SET_NULL, NULLABLE | Trả lời tin nhắn nào |
| `is_deleted` | BOOLEAN | DEFAULT `false` | Soft delete |
| `created_at` | TIMESTAMP | DEFAULT NOW | Thời điểm gửi |

**Composite Index:** `idx_conv_created(conversation_id, created_at)`

---

## 📁 Album

### `albums`
> Album ảnh do người dùng tạo để tổ chức bài đăng.

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|-----|-------------|-----------|-------|
| `id` | BIGINT | PK | ID album |
| `user_id` | BIGINT | FK → `users` CASCADE, INDEX | Chủ album |
| `title` | VARCHAR(100) | NOT NULL | Tên album |
| `description` | TEXT | NULLABLE | Mô tả |
| `cover_image_url` | VARCHAR(255) | NULLABLE | Ảnh bìa |
| `is_private` | BOOLEAN | DEFAULT `false` | Riêng tư |
| `created_at` | TIMESTAMP | DEFAULT NOW | Thời điểm tạo |
| `updated_at` | TIMESTAMP | DEFAULT NOW | Thời điểm cập nhật |
| `deleted_at` | TIMESTAMP | NULLABLE | Soft delete |

**Composite Index:** `idx_user_albums(user_id, created_at)`

---

### `album_media`
> Bảng trung gian: liên kết bài đăng vào album.

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|-----|-------------|-----------|-------|
| `id` | BIGINT | PK | ID |
| `album_id` | BIGINT | FK → `albums` CASCADE, INDEX | Album |
| `post_id` | BIGINT | FK → `posts` CASCADE | Bài đăng |
| `added_at` | TIMESTAMP | DEFAULT NOW | Thời điểm thêm vào album |

**Unique Index:** `idx_album_post_unique(album_id, post_id)`

---

## 📷 Dịch vụ nhiếp ảnh

### `portfolios`
> Hồ sơ chuyên nghiệp của nhiếp ảnh gia (1-1 với user).

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|-----|-------------|-----------|-------|
| `id` | BIGINT | PK | ID portfolio |
| `user_id` | BIGINT | FK → `users` CASCADE, UNIQUE INDEX | Nhiếp ảnh gia |
| `title` | VARCHAR(255) | NULLABLE | Tiêu đề dịch vụ |
| `description` | TEXT | NULLABLE | Mô tả dịch vụ |
| `specialties` | TEXT | NULLABLE | JSON Array: `["wedding", "portrait"]` |
| `hourly_rate` | DECIMAL(12,2) | NULLABLE | Giá theo giờ |
| `currency` | VARCHAR(3) | DEFAULT `VND` | Đơn vị tiền tệ |
| `service_area` | VARCHAR(255) | NULLABLE | Khu vực phục vụ |
| `is_available` | BOOLEAN | DEFAULT `true`, INDEX | Đang nhận việc |
| `rating_avg` | DECIMAL(3,2) | DEFAULT `0.00`, INDEX | Điểm đánh giá trung bình |
| `review_count` | INT | DEFAULT `0` | Tổng số đánh giá |
| `created_at` | TIMESTAMP | DEFAULT NOW | Thời điểm tạo |
| `updated_at` | TIMESTAMP | DEFAULT NOW | Thời điểm cập nhật |

---

### `availability_schedules`
> Lịch rảnh của nhiếp ảnh gia (theo tuần hoặc ngày cụ thể).

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|-----|-------------|-----------|-------|
| `id` | BIGINT | PK | ID lịch |
| `portfolio_id` | BIGINT | FK → `portfolios` CASCADE, INDEX | Portfolio sở hữu |
| `type` | ENUM | NOT NULL | `RECURRING` / `SPECIFIC_DATE` |
| `day_of_week` | ENUM | NULLABLE | ISO day: `MONDAY`…`SUNDAY` |
| `specific_date` | DATE | NULLABLE | Ngày cụ thể nếu type = `SPECIFIC_DATE` |
| `start_time` | VARCHAR(5) | NOT NULL | Giờ bắt đầu (HH:mm) |
| `end_time` | VARCHAR(5) | NOT NULL | Giờ kết thúc (HH:mm) |
| `is_booked` | BOOLEAN | DEFAULT `false` | Đã được đặt |
| `created_at` | TIMESTAMP | DEFAULT NOW | Thời điểm tạo |
| `updated_at` | TIMESTAMP | DEFAULT NOW | Thời điểm cập nhật |

---

### `bookings`
> Lịch đặt chụp ảnh giữa khách hàng và nhiếp ảnh gia.

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|-----|-------------|-----------|-------|
| `id` | BIGINT | PK | ID booking |
| `client_id` | BIGINT | FK → `users` CASCADE, INDEX | Khách hàng |
| `photographer_id` | BIGINT | FK → `users` CASCADE, INDEX | Nhiếp ảnh gia |
| `booking_date` | DATETIME | NOT NULL, INDEX | Ngày giờ chụp |
| `duration_hours` | DECIMAL(4,1) | NULLABLE | Số giờ chụp |
| `location_booking` | VARCHAR(255) | NULLABLE | Địa điểm chụp |
| `details` | TEXT | NULLABLE | Yêu cầu chi tiết |
| `price` | DECIMAL(12,2) | NULLABLE | Giá thỏa thuận |
| `currency` | VARCHAR(3) | DEFAULT `VND` | Đơn vị tiền tệ |
| `status` | ENUM | DEFAULT `PENDING`, INDEX | `PENDING` / `CONFIRMED` / `COMPLETED` / `CANCELLED` |
| `cancellation_reason` | TEXT | NULLABLE | Lý do hủy |
| `created_at` | TIMESTAMP | DEFAULT NOW | Thời điểm tạo |
| `updated_at` | TIMESTAMP | DEFAULT NOW | Thời điểm cập nhật |

**Composite Index:** `idx_photographer_date(photographer_id, booking_date)`
> ⚠️ Constraint `client_id ≠ photographer_id` được kiểm soát tại tầng application.

---

### `ratings`
> Đánh giá sau khi hoàn thành booking (1-1 với booking).

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|-----|-------------|-----------|-------|
| `id` | BIGINT | PK | ID đánh giá |
| `booking_id` | BIGINT | FK → `bookings` CASCADE, UNIQUE INDEX | Booking được đánh giá |
| `rater_id` | BIGINT | FK → `users` CASCADE, INDEX | Người đánh giá |
| `ratee_id` | BIGINT | FK → `users` CASCADE, INDEX | Người được đánh giá |
| `rating_value` | SMALLINT | NOT NULL, INDEX | Điểm: 1–5 |
| `comment` | TEXT | NULLABLE | Nhận xét |
| `created_at` | TIMESTAMP | DEFAULT NOW | Thời điểm đánh giá |

---

## 🔔 Thông báo

### `notifications`
> Thông báo đẩy cho người dùng về các sự kiện liên quan.

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|-----|-------------|-----------|-------|
| `id` | BIGINT | PK | ID thông báo |
| `user_id` | BIGINT | FK → `users` CASCADE, INDEX | Người nhận |
| `sender_id` | BIGINT | FK → `users` SET_NULL, NULLABLE | Người kích hoạt |
| `type` | ENUM | NOT NULL | `LIKE` / `COMMENT` / `FOLLOW` / `BOOKING` / `MESSAGE`… |
| `target_type` | ENUM | NULLABLE | `POST` / `COMMENT` / `USER` / `BOOKING` |
| `target_id` | BIGINT | NULLABLE | ID của đối tượng liên quan |
| `title` | VARCHAR(255) | NULLABLE | Tiêu đề thông báo |
| `body` | TEXT | NULLABLE | Nội dung thông báo |
| `is_read` | BOOLEAN | DEFAULT `false` | Đã đọc chưa |
| `created_at` | TIMESTAMP | DEFAULT NOW, INDEX | Thời điểm tạo |

**Composite Index:** `idx_user_read(user_id, is_read)`

---

## 🔍 Tìm kiếm

### `search_histories`
> Lịch sử tìm kiếm của người dùng.

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|-----|-------------|-----------|-------|
| `id` | BIGINT | PK | ID bản ghi |
| `user_id` | BIGINT | FK → `users` CASCADE, INDEX | Người tìm kiếm |
| `query_text` | VARCHAR(255) | NOT NULL | Từ khóa tìm kiếm |
| `result_count` | INT | NULLABLE | Số kết quả trả về |
| `searched_at` | TIMESTAMP | DEFAULT NOW, INDEX | Thời điểm tìm kiếm |

---

## 🛡️ Kiểm duyệt & Audit

### `reports`
> Báo cáo vi phạm nhắm đến bài đăng, bình luận, hoặc người dùng.

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|-----|-------------|-----------|-------|
| `id` | BIGINT | PK | ID báo cáo |
| `reporter_id` | BIGINT | FK → `users` CASCADE, INDEX | Người báo cáo |
| `target_type` | ENUM | NOT NULL | `POST` / `USER` / `COMMENT` |
| `target_id` | BIGINT | NOT NULL | ID đối tượng bị báo cáo |
| `reason` | TEXT | NOT NULL | Lý do báo cáo |
| `admin_note` | TEXT | NULLABLE | Ghi chú của admin |
| `reviewed_by` | BIGINT | FK → `users` SET_NULL, NULLABLE | Admin xử lý |
| `status` | ENUM | DEFAULT `PENDING`, INDEX | `PENDING` / `REVIEWED` / `RESOLVED` |
| `created_at` | TIMESTAMP | DEFAULT NOW | Thời điểm tạo |
| `updated_at` | TIMESTAMP | DEFAULT NOW | Thời điểm cập nhật |

**Composite Index:** `idx_report_target(target_type, target_id)`

---

### `banned_words`
> Danh sách từ/pattern bị cấm trong nội dung (hỗ trợ regex).

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|-----|-------------|-----------|-------|
| `id` | BIGINT | PK | ID |
| `word_or_regex` | VARCHAR(255) | UNIQUE INDEX | Từ hoặc pattern regex |
| `is_regex` | BOOLEAN | DEFAULT `false` | Có phải regex không |
| `added_by_admin_id` | BIGINT | FK → `users` SET_NULL, NULLABLE | Admin thêm vào |
| `created_at` | TIMESTAMP | DEFAULT NOW | Thời điểm thêm |

---

### `activity_logs`
> Audit log toàn diện mọi hành động quan trọng trong hệ thống.

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|-----|-------------|-----------|-------|
| `id` | BIGINT | PK | ID log |
| `user_id` | BIGINT | FK → `users` SET_NULL, NULLABLE, INDEX | Người thực hiện |
| `action` | VARCHAR(100) | NOT NULL, INDEX | Hành động: `LOGIN`, `UPLOAD`, `DELETE`… |
| `target_type` | ENUM | NOT NULL | `POST` / `USER` / `COMMENT` / `BOOKING`… |
| `target_id` | BIGINT | NULLABLE | ID đối tượng bị tác động |
| `ip_address` | VARCHAR(45) | NULLABLE | IP thực hiện hành động |
| `user_agent` | VARCHAR(500) | NULLABLE | Thông tin trình duyệt/app |
| `metadata` | TEXT | NULLABLE | JSON: thông tin bổ sung |
| `created_at` | TIMESTAMP | DEFAULT NOW, INDEX | Thời điểm xảy ra |

**Composite Index:** `idx_activity_target(target_type, target_id)`

---

## 🔗 Tóm Tắt Quan Hệ

```
users (1) ──────────────────── (N) posts
users (1) ──────────────────── (N) likes
users (1) ──────────────────── (N) comments
users (N) ──────────────────── (N) followers [via followers table]
users (1) ──────────────────── (N) follow_requests
users (N) ──────────────────── (N) blocked_users [via blocked_users table]
users (N) ──────────────────── (N) muted_users [via muted_users table]
users (1) ──────────────────── (1) portfolios
users (1) ──────────────────── (N) bookings (as client)
users (1) ──────────────────── (N) bookings (as photographer)
users (1) ──────────────────── (N) notifications
users (1) ──────────────────── (N) user_sessions
users (1) ──────────────────── (N) albums
users (N) ──────────────────── (N) conversations [via conversation_members]

posts (1) ──────────────────── (N) post_media
posts (1) ──────────────────── (N) comments
posts (N) ──────────────────── (N) albums [via album_media]

post_media (N) ─────────────── (N) media_tags [via post_media_tags]

conversations (1) ───────────── (N) messages
messages (1) ────────────────── (N) messages [reply_to_id self-ref]

bookings (1) ────────────────── (1) ratings
portfolios (1) ──────────────── (N) availability_schedules
```

---

## ⚙️ Chiến lược kỹ thuật

| Chiến lược | Chi tiết |
|------------|----------|
| **Soft Delete** | Các bảng `users`, `posts`, `comments`, `albums` có cột `deleted_at` |
| **Counter Cache** | `users` cache `follower_count`, `following_count`, `post_count`; `posts` cache `like_count`, `comment_count` |
| **Composite Index** | Tối ưu query feed (`user_id + created_at`), chat (`conversation_id + created_at`) |
| **Enum by Name** | Tất cả ENUM lưu dạng chuỗi (`enumerationByName`) để dễ migrate |
| **Cascading** | Xóa user → cascade xóa posts, sessions, albums (dữ liệu phụ thuộc) |
| **SET_NULL** | Xóa user admin → set null ở `reviewed_by`, `sender_id` (giữ lịch sử) |
| **Unique Constraints** | Tránh duplicate: like, follow, save, block, mute đều có unique index |
