# 2.3.2. Mô tả chi tiết các bảng trong cơ sở dữ liệu (Phần 2)

## --- Nhóm 3: Tương tác xã hội ---

### Bảng 9: likes (Lượt thích bài viết)

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|---|---|---|---|
| id | BIGINT | PK | ID lượt thích |
| user_id | BIGINT | FK → users(id) ON DELETE CASCADE | ID người thích |
| post_id | BIGINT | FK → posts(id) ON DELETE CASCADE, INDEX | ID bài viết |
| created_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP, INDEX | Thời điểm thích |

> UNIQUE INDEX: `uk_user_post_likes(user_id, post_id)` — ngăn thích trùng lặp.

---

### Bảng 10: comment_likes (Lượt thích bình luận)

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|---|---|---|---|
| id | BIGINT | PK | ID lượt thích |
| user_id | BIGINT | FK → users(id) ON DELETE CASCADE | ID người thích |
| comment_id | BIGINT | FK → comments(id) ON DELETE CASCADE, INDEX | ID bình luận |
| created_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP, INDEX | Thời điểm thích |

> UNIQUE INDEX: `uk_user_comment_likes(user_id, comment_id)` — ngăn thích trùng lặp.

---

### Bảng 11: comment_dislikes (Lượt không thích bình luận)

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|---|---|---|---|
| id | BIGINT | PK | ID lượt không thích |
| user_id | BIGINT | FK → users(id) ON DELETE CASCADE | ID người không thích |
| comment_id | BIGINT | FK → comments(id) ON DELETE CASCADE, INDEX | ID bình luận |
| created_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP, INDEX | Thời điểm không thích |

> UNIQUE INDEX: `uk_user_comment_dislikes(user_id, comment_id)` — ngăn không thích trùng lặp.

---

### Bảng 12: comments (Bình luận)

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|---|---|---|---|
| id | BIGINT | PK | ID bình luận |
| post_id | BIGINT | FK → posts(id) ON DELETE CASCADE, INDEX | ID bài viết |
| user_id | BIGINT | FK → users(id) ON DELETE CASCADE, INDEX | ID người bình luận |
| content | TEXT | NOT NULL | Nội dung bình luận |
| parent_comment_id | BIGINT | FK → comments(id) ON DELETE CASCADE, NULL, INDEX | Reply cho bình luận nào |
| like_count | INT | NOT NULL, DEFAULT 0 | Trực quan hóa số lượt thích |
| reply_count | INT | NOT NULL, DEFAULT 0 | Trực quan hóa số phản hồi |
| depth | TINYINT | NOT NULL, DEFAULT 0 | Độ sâu của cây bình luận (Check <= 3) |
| created_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Thời điểm tạo |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | Thời điểm sửa |
| deleted_at | TIMESTAMP | NULL | Soft delete |

---

### Bảng 13: saved_posts (Bài đăng đã lưu)

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|---|---|---|---|
| id | BIGINT | PK | ID lưu bài viết |
| user_id | BIGINT | FK → users(id) ON DELETE CASCADE | ID người lưu |
| post_id | BIGINT | FK → posts(id) ON DELETE CASCADE, INDEX | ID bài viết |
| saved_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Thời điểm lưu |

> UNIQUE INDEX: `uk_user_post_saved(user_id, post_id)` — ngăn lưu trùng lặp.

---

## --- Nhóm 4: Quan hệ người dùng ---

### Bảng 14: followers (Theo dõi người dùng)

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|---|---|---|---|
| follower_id | BIGINT | PK, FK → users(id) ON DELETE CASCADE | ID người follow |
| following_id | BIGINT | PK, FK → users(id) ON DELETE CASCADE, INDEX | ID người được follow |
| created_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP, INDEX | Thời điểm follow |

> Ràng buộc: logic kiểm soát ứng dụng không cho phép `follower_id = following_id`.

---

### Bảng 15: follow_requests (Yêu cầu theo dõi)

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|---|---|---|---|
| id | BIGINT | PK | ID yêu cầu |
| follower_id | BIGINT | FK → users(id) ON DELETE CASCADE, INDEX | ID người gửi yêu cầu |
| following_id | BIGINT | FK → users(id) ON DELETE CASCADE, INDEX | ID người nhận |
| status | ENUM('PENDING','ACCEPTED','REJECTED') | NOT NULL, DEFAULT 'PENDING', INDEX | Trạng thái yêu cầu |
| created_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Ngày gửi |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | Ngày phản hồi |

> UNIQUE INDEX: `idx_follower_following_req(follower_id, following_id)`.

---

### Bảng 16: blocked_users (Chặn người dùng)

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|---|---|---|---|
| id | BIGINT | PK | ID chặn |
| blocker_id | BIGINT | FK → users(id) ON DELETE CASCADE, INDEX | ID người chặn |
| blocked_id | BIGINT | FK → users(id) ON DELETE CASCADE, INDEX | ID bị chặn |
| reason | VARCHAR(255) | NULL | Lý do chặn |
| created_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Ngày chặn |

> UNIQUE INDEX: `idx_blocker_blocked(blocker_id, blocked_id)`.

---

### Bảng 17: muted_users (Tắt tiếng người dùng)

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|---|---|---|---|
| id | BIGINT | PK | ID tắt tiếng |
| muter_id | BIGINT | FK → users(id) ON DELETE CASCADE, INDEX | ID người tắt tiếng |
| muted_id | BIGINT | FK → users(id) ON DELETE CASCADE, INDEX | ID bị tắt tiếng |
| created_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Ngày tắt tiếng |

> UNIQUE INDEX: `idx_muter_muted(muter_id, muted_id)`.
