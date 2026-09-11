# 2.3.2. Mô tả chi tiết các bảng trong cơ sở dữ liệu (Phần 3)

## --- Nhóm 5: Nhắn tin ---

### Bảng 18: conversations (Cuộc hội thoại)

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|---|---|---|---|
| id | BIGINT | PK | ID hội thoại |
| title | VARCHAR(255) | NULL | Tiêu đề nhóm chat (chỉ cho nhóm) |
| type | ENUM('DIRECT','GROUP') | NOT NULL, DEFAULT 'DIRECT' | Phân loại chat 1-1 hay chat nhóm |
| created_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Ngày tạo phòng |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, INDEX | Thời điểm cập nhật cuối / tin nhắn mới nhất |

---

### Bảng 19: conversation_members (Thành viên hội thoại)

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|---|---|---|---|
| conversation_id | BIGINT | PK, FK → conversations(id) ON DELETE CASCADE | ID phòng chat |
| user_id | BIGINT | PK, FK → users(id) ON DELETE CASCADE, INDEX | ID thành viên tham gia |
| role | ENUM('MEMBER','ADMIN') | NOT NULL, DEFAULT 'MEMBER' | Quyền quản trị nhóm |
| nickname | VARCHAR(50) | NULL | Biệt danh trong cuộc hội thoại |
| is_muted | BOOLEAN | NOT NULL, DEFAULT FALSE | Tắt thông báo từ phòng chat |
| joined_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Thời điểm tham gia |
| last_read_at | TIMESTAMP | NULL | Thời điểm đọc tin nhắn cuối |

---

### Bảng 20: messages (Tin nhắn)

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|---|---|---|---|
| id | BIGINT | PK | ID tin nhắn |
| conversation_id | BIGINT | FK → conversations(id) ON DELETE CASCADE, INDEX | ID phòng chat |
| sender_id | BIGINT | FK → users(id) ON DELETE CASCADE, INDEX | ID người gửi |
| content | TEXT | NULL | Nội dung tin nhắn dạng văn bản |
| message_type | ENUM('TEXT','IMAGE','VIDEO','FILE') | NOT NULL, DEFAULT 'TEXT' | Phân loại loại tin nhắn |
| media_url | VARCHAR(500) | NULL | URL đính kèm media |
| reply_to_id | BIGINT | FK → messages(id) SET NULL, NULL | Trả lời tin nhắn cũ |
| is_deleted | BOOLEAN | NOT NULL, DEFAULT FALSE | Xóa tin nhắn (phía người dùng) |
| created_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Ngày gửi tin nhắn |

> Composite Index: `idx_conv_created(conversation_id, created_at)` để load lịch sử chat nhanh hơn.

---

## --- Nhóm 6: Album ---

### Bảng 21: albums (Album ảnh bộ sưu tập)

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|---|---|---|---|
| id | BIGINT | PK | ID album |
| user_id | BIGINT | FK → users(id) ON DELETE CASCADE, INDEX | ID người tạo |
| title | VARCHAR(100) | NOT NULL | Tên album |
| description | TEXT | NULL | Mô tả chi tiết album |
| cover_image_url | VARCHAR(255) | NULL | Ảnh bìa album |
| is_private | BOOLEAN | NOT NULL, DEFAULT FALSE | Album ở chế độ riêng tư |
| created_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Ngày tạo |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | Ngày cập nhật |
| deleted_at | TIMESTAMP | NULL | Soft delete |

---

### Bảng 22: album_media (Ảnh/Media trong Album)

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|---|---|---|---|
| id | BIGINT | PK | ID bản ghi mapping |
| album_id | BIGINT | FK → albums(id) ON DELETE CASCADE, INDEX | ID album |
| post_id | BIGINT | FK → posts(id) ON DELETE CASCADE | ID bài đăng đính kèm |
| added_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Thời điểm thêm vào |

> UNIQUE INDEX: `idx_album_post_unique(album_id, post_id)`.
