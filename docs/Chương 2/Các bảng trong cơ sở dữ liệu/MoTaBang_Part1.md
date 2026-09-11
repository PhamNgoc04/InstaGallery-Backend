# 2.3.2. Mô tả chi tiết các bảng trong cơ sở dữ liệu (Phần 1)

## --- Nhóm 1: Auth & Identity ---

### Bảng 1: users (Người dùng)

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|---|---|---|---|
| id | BIGINT | PK, AUTO_INCREMENT | ID người dùng |
| username | VARCHAR(50) | NOT NULL, UNIQUE INDEX | Tên đăng nhập |
| email | VARCHAR(100) | NOT NULL, UNIQUE INDEX | Email |
| password_hash | VARCHAR(255) | NOT NULL | Mật khẩu đã hash |
| full_name | VARCHAR(100) | NOT NULL | Họ và tên |
| profile_picture_url | VARCHAR(255) | NULL | URL ảnh đại diện |
| bio | TEXT | NULL | Tiểu sử |
| website | VARCHAR(255) | NULL | Website cá nhân |
| gender | VARCHAR(20) | NULL | Giới tính |
| phone_number | VARCHAR(20) | NULL | Số điện thoại |
| date_of_birth | DATE | NULL | Ngày sinh |
| location | VARCHAR(255) | NULL | Địa chỉ |
| user_type | ENUM('CLIENT','PHOTOGRAPHER') | NOT NULL, DEFAULT 'CLIENT' | Loại người dùng |
| role | ENUM('USER','ADMIN') | NOT NULL, DEFAULT 'USER' | Vai trò phân quyền |
| provider | ENUM('LOCAL','GOOGLE','FACEBOOK') | NOT NULL, DEFAULT 'LOCAL' | OAuth provider |
| provider_id | VARCHAR(255) | NULL, UNIQUE | ID từ OAuth provider |
| is_two_factor_enabled | BOOLEAN | NOT NULL, DEFAULT FALSE | Bật xác thực 2 lớp (2FA) |
| two_factor_secret | VARCHAR(255) | NULL | Secret key TOTP |
| is_private | BOOLEAN | NOT NULL, DEFAULT FALSE | Tài khoản ở chế độ riêng tư |
| is_verified | BOOLEAN | NOT NULL, DEFAULT FALSE | Tài khoản đã được admin verify |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE | Tài khoản đang hoạt động |
| follower_count | INT | NOT NULL, DEFAULT 0 | Cache số người theo dõi |
| following_count | INT | NOT NULL, DEFAULT 0 | Cache số người đang theo dõi |
| post_count | INT | NOT NULL, DEFAULT 0 | Cache số bài viết đã đăng |
| created_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Ngày tạo |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | Ngày cập nhật |
| deleted_at | TIMESTAMP | NULL | Soft delete |

---

### Bảng 2: user_sessions (Phiên đăng nhập)

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|---|---|---|---|
| id | BIGINT | PK | ID phiên đăng nhập |
| user_id | BIGINT | FK → users(id), INDEX | ID người dùng sở hữu phiên |
| device_info | VARCHAR(255) | NULL | Thông tin thiết bị đăng nhập |
| ip_address | VARCHAR(45) | NULL | Địa chỉ IP đăng nhập |
| refresh_token | VARCHAR(255) | NOT NULL, UNIQUE INDEX | Refresh token của JWT |
| created_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Thời gian tạo |
| expired_at | TIMESTAMP | NOT NULL, INDEX | Thời gian hết hạn |

---

### Bảng 3: password_reset_tokens (Token đặt lại mật khẩu)

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|---|---|---|---|
| id | BIGINT | PK | ID token |
| user_id | BIGINT | FK → users(id), NOT NULL | Người sở hữu |
| token | VARCHAR(255) | NOT NULL, UNIQUE INDEX | Token đặt lại mật khẩu |
| expired_at | DATETIME | NOT NULL | Thời gian hết hạn |
| created_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Thời gian tạo |

---

## --- Nhóm 2: Nội dung & Media ---

### Bảng 4: posts (Bài đăng)

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|---|---|---|---|
| id | BIGINT | PK | ID bài đăng |
| user_id | BIGINT | FK → users(id) ON DELETE CASCADE, INDEX | ID người đăng |
| caption | TEXT | NULL | Chú thích nội dung |
| location | VARCHAR(255) | NULL | Địa điểm |
| visibility | ENUM('PUBLIC','FOLLOWERS','PRIVATE') | NOT NULL, DEFAULT 'PUBLIC', INDEX | Chế độ hiển thị bài đăng |
| comment_visibility | ENUM('ALLOW_ALL','FOLLOWERS','DISABLED') | NOT NULL, DEFAULT 'ALLOW_ALL' | Quyền bình luận |
| like_count | INT | NOT NULL, DEFAULT 0 | Trực quan hóa số lượt thích |
| comment_count | INT | NOT NULL, DEFAULT 0 | Trực quan hóa số bình luận |
| share_count | INT | NOT NULL, DEFAULT 0 | Trực quan hóa số lượt chia sẻ |
| created_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP, INDEX | Thời điểm đăng |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | Thời điểm sửa |
| deleted_at | TIMESTAMP | NULL, INDEX | Soft delete |

---

### Bảng 5: post_media (File/Ảnh đính kèm bài viết)

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|---|---|---|---|
| id | BIGINT | PK | ID file đính kèm |
| post_id | BIGINT | FK → posts(id) ON DELETE CASCADE, INDEX | ID bài viết |
| media_file_url | VARCHAR(500) | NOT NULL | URL file gốc |
| thumbnail_url | VARCHAR(500) | NULL | URL file ảnh thu nhỏ |
| media_type | ENUM('IMAGE','VIDEO') | NOT NULL, DEFAULT 'IMAGE' | Loại file |
| position | INT | NOT NULL, DEFAULT 0 | Thứ tự trong carousel |
| filter_id | BIGINT | FK → filters(id) SET NULL, NULL | Bộ lọc áp dụng |
| width | INT | NULL | Chiều rộng file (px) |
| height | INT | NULL | Chiều cao file (px) |
| file_size | BIGINT | NULL | Dung lượng file (bytes) |
| duration | INT | NULL | Thời lượng video (giây) |
| metadata | TEXT | NULL | EXIF metadata |
| created_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Thời điểm tạo |

---

### Bảng 6: filters (Bộ lọc ảnh)

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|---|---|---|---|
| id | BIGINT | PK | ID filter |
| name | VARCHAR(50) | NOT NULL, UNIQUE INDEX | Tên bộ lọc |
| description | TEXT | NULL | Mô tả bộ lọc |
| config_json | TEXT | NULL | Cấu hình tham số của filter |
| preview_url | VARCHAR(255) | NULL | URL ảnh preview khi áp dụng filter |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE | Trạng thái hoạt động |

---

### Bảng 7: media_tags (Từ khóa phân loại/Hashtag)

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|---|---|---|---|
| id | BIGINT | PK | ID hashtag |
| name | VARCHAR(50) | NOT NULL, UNIQUE INDEX | Tên hashtag |
| description | TEXT | NULL | Mô tả |
| usage_count | INT | NOT NULL, DEFAULT 0, INDEX | Số lần từ khóa được dùng |
| created_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Ngày tạo |

---

### Bảng 8: post_media_tags (Mapping Media - Tag)

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|---|---|---|---|
| media_id | BIGINT | PK, FK → post_media(id) ON DELETE CASCADE | ID file media |
| tag_id | BIGINT | PK, FK → media_tags(id) ON DELETE CASCADE, INDEX | ID hashtag |
| created_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Ngày gắn hashtag |
