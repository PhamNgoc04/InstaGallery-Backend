### 2.2.5. Phân tích các use case bổ sung (Sequence Diagram)

---

#### 2.2.5.4. Sequence Diagram — Đăng xuất

```mermaid
sequenceDiagram
    actor U as Người dùng
    participant App as Android App
    participant API as Ktor Server
    participant DB as MySQL

    U->>App: Settings → "Đăng xuất"
    App-->>U: Dialog "Bạn có chắc chắn?"
    U->>App: Nhấn "Đồng ý"
    App->>API: POST /auth/logout { refreshToken } (JWT)
    API->>DB: DELETE FROM user_sessions WHERE refresh_token = ?
    DB-->>API: OK
    API-->>App: 200 OK
    App->>App: Xóa tokens khỏi TokenStore
    App->>App: Xóa cached user data
    App-->>U: Chuyển về LoginScreen
```

---

#### 2.2.5.6. Sequence Diagram — Đổi mật khẩu

```mermaid
sequenceDiagram
    actor U as Người dùng
    participant App as Android App
    participant API as Ktor Server
    participant DB as MySQL

    U->>App: Settings → "Đổi mật khẩu"
    App-->>U: Form: MK hiện tại, MK mới, Xác nhận MK
    U->>App: Nhập 3 trường, bấm "Lưu"
    App->>App: Validate: MK mới ≥ 8 ký tự, xác nhận khớp
    App->>API: PUT /auth/change-password { current_password, new_password } (JWT)

    API->>DB: SELECT password_hash FROM users WHERE id = ?
    API->>API: bcrypt.verify(current_password, password_hash)
    alt Sai mật khẩu hiện tại
        API-->>App: 401 { code: "WRONG_PASSWORD" }
        App-->>U: "Mật khẩu hiện tại không chính xác"
    end
    API->>API: Hash mật khẩu mới (bcrypt, cost=12)
    API->>DB: UPDATE users SET password_hash = ? WHERE id = ?
    API->>DB: DELETE FROM user_sessions<br/>WHERE user_id = ? AND id != current_session_id
    API-->>App: 200 OK
    App-->>U: "Đổi mật khẩu thành công"
```

---

#### 2.2.5.12. Sequence Diagram — Xóa bài đăng

```mermaid
sequenceDiagram
    actor U as Người dùng
    participant App as Android App
    participant API as Ktor Server
    participant DB as MySQL

    U->>App: PostDetail → Menu "⋮" → "Xóa"
    App-->>U: Dialog "Xóa bài đăng? Không thể hoàn tác."
    U->>App: Nhấn "Xóa"
    App->>API: DELETE /posts/{postId} (JWT)
    API->>DB: SELECT user_id FROM posts WHERE id = ?
    API->>API: Verify: current user = post owner OR role = ADMIN
    alt Không có quyền
        API-->>App: 403 Forbidden
        App-->>U: "Bạn không có quyền xóa bài đăng này"
    end
    API->>DB: BEGIN TRANSACTION
    API->>DB: UPDATE posts SET deleted_at = NOW() WHERE id = ?
    API->>DB: UPDATE users SET post_count = post_count - 1<br/>WHERE id = user_id
    API->>DB: COMMIT
    API-->>App: 200 OK
    App-->>U: "Đã xóa bài đăng" → quay lại Feed/Profile
```

---

#### 2.2.5.21. Sequence Diagram — Lưu bài đăng (Save / Unsave)

```mermaid
sequenceDiagram
    actor U as Người dùng
    participant App as Android App
    participant API as Ktor Server
    participant DB as MySQL

    Note over U,DB: === SAVE ===
    U->>App: Bấm 🔖 trên bài đăng
    App->>App: Optimistic UI: icon filled
    App->>API: POST /posts/{postId}/save (JWT)
    API->>DB: INSERT INTO saved_posts (user_id, post_id, saved_at)
    API-->>App: 200 OK

    Note over U,DB: === UNSAVE ===
    U->>App: Bấm 🔖 lần nữa
    App->>App: Optimistic UI: icon outline
    App->>API: DELETE /posts/{postId}/save (JWT)
    API->>DB: DELETE FROM saved_posts<br/>WHERE user_id = ? AND post_id = ?
    API-->>App: 200 OK
```

---

#### 2.2.5.26. Sequence Diagram — Chia sẻ bài đăng

```mermaid
sequenceDiagram
    actor U as Người dùng
    participant App as Android App
    participant API as Ktor Server
    participant DB as MySQL

    U->>App: Bấm 📤 (share) trên bài đăng
    App-->>U: Bottom sheet: "Sao chép link", danh sách users gần đây

    Note over U,DB: === SHARE QUA LINK ===
    U->>App: Chọn "Sao chép liên kết"
    App->>App: Tạo deep link: instagallery://post/{postId}
    App->>App: Copy vào clipboard
    App->>API: POST /posts/{postId}/share (JWT)
    API->>DB: UPDATE posts SET share_count = share_count + 1
    API-->>App: 200 OK
    App-->>U: "Đã sao chép liên kết"

    Note over U,DB: === SHARE QUA CHAT ===
    U->>App: Chọn user trong danh sách
    App->>API: POST /messages { conversation_id, content, type: POST_SHARE }
    API->>DB: INSERT INTO messages (...)
    API->>DB: UPDATE posts SET share_count = share_count + 1
    API-->>App: 201 Created
    App-->>U: "Đã gửi"
```

---

#### 2.2.5.29. Sequence Diagram — Báo cáo vi phạm

```mermaid
sequenceDiagram
    actor U as Người dùng
    participant App as Android App
    participant API as Ktor Server
    participant DB as MySQL

    U->>App: Menu "⋮" trên bài/comment → "Báo cáo"
    App-->>U: Hiển thị danh sách lý do báo cáo

    U->>App: Chọn lý do + (tùy chọn) ghi mô tả → "Gửi"
    App->>API: POST /reports { target_type, target_id, reason } (JWT)
    API->>DB: SELECT COUNT(*) FROM reports<br/>WHERE reporter_id = ? AND target_type = ? AND target_id = ?
    alt Đã báo cáo trước đó
        API-->>App: 409 Conflict
        App-->>U: "Bạn đã báo cáo nội dung này rồi"
    end
    API->>DB: INSERT INTO reports<br/>(reporter_id, target_type, target_id, reason, status='PENDING')
    API-->>App: 201 Created
    App-->>U: "Cảm ơn bạn đã báo cáo."
```

---

#### 2.2.5.30. Sequence Diagram — Chặn người dùng (Block / Unblock)

```mermaid
sequenceDiagram
    actor U as Người dùng
    participant App as Android App
    participant API as Ktor Server
    participant DB as MySQL

    Note over U,DB: === BLOCK ===
    U->>App: Profile người khác → Menu "⋮" → "Chặn"
    App-->>U: Dialog "Chặn @username?"
    U->>App: Nhấn "Chặn"
    App->>API: POST /users/{userId}/block (JWT)
    API->>DB: INSERT INTO blocked_users (blocker_id, blocked_id)
    API->>DB: DELETE FROM followers<br/>WHERE (follower_id, following_id) IN cả 2 chiều
    API->>DB: UPDATE users SET follower_count, following_count (trigger)
    API-->>App: 200 OK
    App-->>U: "Đã chặn @username"

    Note over U,DB: === UNBLOCK ===
    U->>App: Settings → "Tài khoản đã chặn" → "Bỏ chặn"
    App->>API: DELETE /users/{userId}/block (JWT)
    API->>DB: DELETE FROM blocked_users<br/>WHERE blocker_id = ? AND blocked_id = ?
    API-->>App: 200 OK
    App-->>U: "Đã bỏ chặn"
```

---

#### 2.2.5.33. Sequence Diagram — Xóa tài khoản

```mermaid
sequenceDiagram
    actor U as Người dùng
    participant App as Android App
    participant API as Ktor Server
    participant DB as MySQL

    U->>App: Settings → "Tài khoản" → "Xóa tài khoản"
    App-->>U: Cảnh báo: "⚠️ Xóa vĩnh viễn, không thể hoàn tác"
    U->>App: Nhập mật khẩu xác nhận → "Xóa tài khoản"
    App->>API: DELETE /users/me { password } (JWT)

    API->>DB: SELECT password_hash FROM users WHERE id = ?
    API->>API: bcrypt.verify(password, hash)
    alt Sai mật khẩu
        API-->>App: 401 "Mật khẩu không chính xác"
        App-->>U: Hiển thị lỗi, yêu cầu nhập lại
    end

    API->>DB: SELECT COUNT(*) FROM bookings<br/>WHERE (client_id=? OR photographer_id=?)<br/>AND status IN ('PENDING','CONFIRMED','IN_PROGRESS')
    alt Có booking đang xử lý
        API-->>App: 409 "Có booking đang xử lý"
        App-->>U: "Vui lòng hủy/hoàn thành booking trước"
    end

    API->>DB: BEGIN TRANSACTION
    API->>DB: UPDATE users SET deleted_at=NOW(), is_active=FALSE
    API->>DB: DELETE FROM user_sessions WHERE user_id = ?
    API->>DB: COMMIT
    API-->>App: 200 OK
    App->>App: Xóa tokens, cached data
    App-->>U: "Tài khoản đã được xóa" → LoginScreen
```

---

#### 2.2.5.38. Sequence Diagram — Xem thông báo

```mermaid
sequenceDiagram
    actor U as Người dùng
    participant App as Android App
    participant API as Ktor Server
    participant DB as MySQL

    U->>App: Bấm tab 🔔 (Thông báo)
    App->>API: GET /notifications?limit=20 (JWT)
    API->>DB: SELECT notifications JOIN users<br/>WHERE user_id = ? ORDER BY created_at DESC
    API-->>App: { notifications[], unread_count }
    App-->>U: Hiển thị danh sách thông báo<br/>(chưa đọc: nền highlight)

    U->>App: Bấm vào 1 thông báo
    App->>API: PUT /notifications/{id}/read (JWT)
    API->>DB: UPDATE notifications SET is_read = TRUE WHERE id = ?
    API-->>App: 200 OK

    alt type = NEW_LIKE hoặc NEW_COMMENT
        App-->>U: Chuyển đến PostDetailScreen
    else type = NEW_FOLLOWER
        App-->>U: Chuyển đến UserProfileScreen
    else type = BOOKING_*
        App-->>U: Chuyển đến BookingDetailScreen
    end

    Note over U,App: Đọc tất cả
    U->>App: Bấm "Đọc tất cả"
    App->>API: PUT /notifications/read-all (JWT)
    API->>DB: UPDATE notifications SET is_read = TRUE<br/>WHERE user_id = ? AND is_read = FALSE
    API-->>App: 200 OK
    App-->>U: Tất cả thông báo đã đọc
```

---

#### 2.2.5.39. Sequence Diagram — Tạo / Chỉnh sửa Portfolio

```mermaid
sequenceDiagram
    actor P as Photographer
    participant App as Android App
    participant API as Ktor Server
    participant DB as MySQL

    Note over P,DB: === TẠO MỚI ===
    P->>App: Profile → "Tạo Portfolio"
    App-->>P: Form: tiêu đề, mô tả, chuyên môn,<br/>giá/giờ, khu vực, trạng thái
    P->>App: Nhập thông tin → "Lưu"
    App->>API: POST /users/me/portfolio (JWT)
    API->>API: Verify role = PHOTOGRAPHER
    API->>DB: SELECT COUNT(*) FROM portfolios WHERE user_id = ?
    alt Đã có portfolio
        API-->>App: 409 "Portfolio đã tồn tại"
    end
    API->>DB: INSERT INTO portfolios<br/>(user_id, title, specialties, hourly_rate, service_area, is_available)
    API-->>App: 201 { portfolio }
    App-->>P: "Đã tạo hồ sơ năng lực!"

    Note over P,DB: === CHỈNH SỬA ===
    P->>App: Profile → "Chỉnh sửa Portfolio"
    App->>API: GET /users/me/portfolio (JWT)
    API->>DB: SELECT * FROM portfolios WHERE user_id = ?
    API-->>App: { portfolio data }
    App-->>P: Form với dữ liệu pre-fill

    P->>App: Sửa thông tin → "Lưu"
    App->>API: PUT /users/me/portfolio (JWT)
    API->>DB: UPDATE portfolios SET ... WHERE user_id = ?
    API-->>App: 200 { updated portfolio }
    App-->>P: "Đã cập nhật portfolio"
```
