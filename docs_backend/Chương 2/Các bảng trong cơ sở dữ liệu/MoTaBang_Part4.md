# 2.3.2. Mô tả chi tiết các bảng trong cơ sở dữ liệu (Phần 4)

## --- Nhóm 7: Dịch vụ nhiếp ảnh & Đặt lịch ---

### Bảng 23: portfolios (Hồ sơ nhiếp ảnh gia)

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|---|---|---|---|
| id | BIGINT | PK | ID hồ sơ |
| user_id | BIGINT | FK → users(id) ON DELETE CASCADE, UNIQUE INDEX | ID nhiếp ảnh gia (1-1) |
| title | VARCHAR(255) | NULL | Tiêu đề giới thiệu |
| description | TEXT | NULL | Mô tả năng lực và dịch vụ |
| specialties | TEXT | NULL | JSON Array lưu các chuyên môn |
| hourly_rate | DECIMAL(12,2) | NULL | Mức giá cơ bản theo giờ |
| currency | VARCHAR(3) | NOT NULL, DEFAULT 'VND' | Đơn vị tiền tệ |
| service_area | VARCHAR(255) | NULL | Khu vực phục vụ |
| is_available | BOOLEAN | NOT NULL, DEFAULT TRUE, INDEX | Trạng thái rảnh/nhận việc |
| rating_avg | DECIMAL(3,2) | NOT NULL, DEFAULT 0.00, INDEX | Điểm đánh giá trung bình |
| review_count | INT | NOT NULL, DEFAULT 0 | Tổng số lượt đánh giá |
| created_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Ngày tạo |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | Ngày cập nhật |

---

### Bảng 24: photographer_services (Gói dịch vụ nhiếp ảnh)

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|---|---|---|---|
| id | BIGINT | PK | ID dịch vụ |
| photographer_id | BIGINT | FK → users(id) ON DELETE CASCADE, INDEX | ID nhiếp ảnh gia sở hữu |
| name | VARCHAR(120) | NOT NULL | Tên gói dịch vụ |
| category | VARCHAR(40) | NOT NULL, INDEX | Phân loại danh mục chụp |
| price | DECIMAL(12,2) | NOT NULL | Giá gói |
| currency | VARCHAR(3) | NOT NULL, DEFAULT 'VND' | Loại tiền tệ |
| duration_minutes | INT | NOT NULL | Thời lượng chụp ảnh (phút) |
| photo_count | INT | NULL | Số lượng ảnh bàn giao tối thiểu |
| edited_photo_count | INT | NULL | Số lượng ảnh được retouch/photoshop |
| makeup_included | BOOLEAN | NOT NULL, DEFAULT FALSE | Bao gồm trang điểm |
| outfit_included | BOOLEAN | NOT NULL, DEFAULT FALSE | Bao gồm trang phục chụp |
| location_support | BOOLEAN | NOT NULL, DEFAULT TRUE | Hỗ trợ tư vấn/chọn địa điểm |
| description | TEXT | NULL | Mô tả chi tiết gói dịch vụ |
| includes | TEXT | NULL | Các dịch vụ đi kèm khác |
| cover_url | VARCHAR(1024) | NULL | URL ảnh bìa minh họa cho gói |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE, INDEX | Gói dịch vụ đang kích hoạt |
| created_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Ngày tạo |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | Ngày cập nhật |

> Composite Index: `idx_photographer_services_category(photographer_id, category)`.

---

### Bảng 25: availability_schedules (Lịch rảnh của nhiếp ảnh gia)

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|---|---|---|---|
| id | BIGINT | PK | ID lịch rảnh |
| portfolio_id | BIGINT | FK → portfolios(id) ON DELETE CASCADE, INDEX | ID portfolio liên kết |
| type | ENUM('RECURRING','SPECIFIC_DATE') | NOT NULL | Lặp vô hạn theo tuần hay theo ngày cụ thể |
| day_of_week | ENUM('MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY','SUNDAY') | NULL | Thứ trong tuần (nếu type = RECURRING) |
| specific_date | DATE | NULL | Ngày cụ thể (nếu type = SPECIFIC_DATE) |
| start_time | VARCHAR(5) | NOT NULL | Giờ bắt đầu (HH:mm) |
| end_time | VARCHAR(5) | NOT NULL | Giờ kết thúc (HH:mm) |
| is_booked | BOOLEAN | NOT NULL, DEFAULT FALSE | Cột cũ. Server không dùng cột này để nhận booking |
| created_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Ngày tạo |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | Ngày cập nhật |

---

### Bảng 26: bookings (Đặt lịch chụp hình)

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|---|---|---|---|
| id | BIGINT | PK | ID booking |
| client_id | BIGINT | FK → users(id) ON DELETE CASCADE, INDEX | Khách hàng đặt chụp |
| photographer_id | BIGINT | FK → users(id) ON DELETE CASCADE, INDEX | Nhiếp ảnh gia thực hiện |
| service_id | BIGINT | FK → photographer_services(id) ON DELETE SET NULL, NULL | Gói chụp nếu có |
| availability_id | BIGINT | FK → availability_schedules(id) ON DELETE SET NULL, NULL | Khung giờ đã khớp lúc giữ chỗ |
| booking_date | DATETIME | NOT NULL, INDEX | Thời điểm chụp, không gắn `Z` |
| duration_hours | DECIMAL(4,1) | NULL | Thời gian thực hiện (giờ) |
| location_booking | VARCHAR(255) | NULL | Địa điểm chụp |
| details | TEXT | NULL | Chi tiết yêu cầu |
| price | DECIMAL(12,2) | NULL | Giá thỏa thuận |
| currency | VARCHAR(3) | NOT NULL, DEFAULT 'VND' | Đơn vị tiền tệ |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'PENDING', INDEX | `PENDING`, `CONFIRMED`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`, `REJECTED` |
| cancellation_reason | TEXT | NULL | Lý do hủy buổi chụp |
| created_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Ngày tạo |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | Ngày cập nhật |

> Composite Index: `idx_photographer_date(photographer_id, booking_date)`.

---

### Bảng 27: ratings (Đánh giá sau hoàn thành booking)

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|---|---|---|---|
| id | BIGINT | PK | ID đánh giá |
| booking_id | BIGINT | FK → bookings(id) ON DELETE CASCADE, UNIQUE INDEX | ID booking (1-1) |
| rater_id | BIGINT | FK → users(id) ON DELETE CASCADE, INDEX | Người đánh giá (Client/Photographer) |
| ratee_id | BIGINT | FK → users(id) ON DELETE CASCADE, INDEX | Người được đánh giá |
| rating_value | SMALLINT | NOT NULL, INDEX | Số điểm sao (1 - 5) |
| comment | TEXT | NULL | Nhận xét chi tiết |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'APPROVED', INDEX | `APPROVED`, `PENDING`, `HIDDEN` |
| created_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Thời điểm đánh giá |

---

## --- Nhóm 8: Thông báo & Thiết bị ---

### Bảng 28: notifications (Thông báo đẩy)

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|---|---|---|---|
| id | BIGINT | PK | ID thông báo |
| user_id | BIGINT | FK → users(id) ON DELETE CASCADE, INDEX | ID người nhận |
| sender_id | BIGINT | FK → users(id) ON DELETE SET NULL, NULL | Người gây ra hành động |
| type | ENUM('LIKE','COMMENT','FOLLOW','BOOKING','MESSAGE','MENTION','SYSTEM') | NOT NULL | Loại thông báo |
| target_type | ENUM('POST','COMMENT','USER','BOOKING','CONVERSATION') | NULL | Loại đối tượng hướng đến |
| target_id | BIGINT | NULL | ID đối tượng hướng đến |
| title | VARCHAR(255) | NULL | Tiêu đề thông báo |
| body | TEXT | NULL | Nội dung chi tiết |
| is_read | BOOLEAN | NOT NULL, DEFAULT FALSE | Đã đọc hay chưa |
| created_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP, INDEX | Ngày tạo |

> Composite Index: `idx_user_read(user_id, is_read)` để load thông báo chưa đọc.

---

### Bảng 29: device_tokens (Thiết bị & Push Token FCM)

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|---|---|---|---|
| id | BIGINT | PK | ID token thiết bị |
| user_id | BIGINT | FK → users(id) ON DELETE CASCADE, INDEX | ID chủ sở hữu |
| token | VARCHAR(512) | NOT NULL, UNIQUE INDEX | FCM token phục vụ push notification |
| platform | VARCHAR(20) | NOT NULL, DEFAULT 'ANDROID' | Hệ điều hành thiết bị |
| device_id | VARCHAR(128) | NULL | Mã định danh thiết bị vật lý |
| app_version | VARCHAR(64) | NULL | Phiên bản ứng dụng đang dùng |
| created_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Ngày gán |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | Ngày cập nhật |
| last_seen_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Thời điểm tương tác cuối |

---

## --- Nhóm 9: Tìm kiếm ---

### Bảng 30: search_histories (Lịch sử tìm kiếm)

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|---|---|---|---|
| id | BIGINT | PK | ID lịch sử |
| user_id | BIGINT | FK → users(id) ON DELETE CASCADE, INDEX | Người tìm kiếm |
| query_text | VARCHAR(255) | NOT NULL | Từ khóa đã nhập |
| result_count | INT | NULL | Số lượng kết quả hệ thống trả về |
| searched_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP, INDEX | Thời điểm tìm kiếm |

> UNIQUE `uk_user_search_query(user_id, query_text)`. Cùng từ khóa thì cập nhật dòng cũ.

---

## --- Nhóm 10: Kiểm duyệt & Audit ---

### Bảng 31: reports (Báo cáo vi phạm)

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|---|---|---|---|
| id | BIGINT | PK | ID báo cáo |
| reporter_id | BIGINT | FK → users(id) ON DELETE CASCADE, INDEX | Người gửi báo cáo |
| target_type | VARCHAR(20) | NOT NULL | `POST`, `COMMENT`, `USER`, `BOOKING`, `MESSAGE`, `RATING` |
| target_id | BIGINT | NOT NULL | ID đối tượng cụ thể bị báo cáo |
| reason | TEXT | NOT NULL | Lý do báo cáo vi phạm |
| admin_note | TEXT | NULL | Phản hồi từ quản trị viên |
| reviewed_by | BIGINT | FK → users(id) ON DELETE SET NULL, NULL | Admin xử lý |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'PENDING', INDEX | `PENDING`, `REVIEWING`, `RESOLVED`, `DISMISSED` |
| created_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Thời điểm tạo báo cáo |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | Thời điểm giải quyết |

> Unique `uk_reporter_target(reporter_id, target_type, target_id)`. Gửi lại cùng mục tiêu không tạo dòng mới. Index cũ `idx_report_target` có thể còn trên database đã chạy.

---

### Bảng 32: banned_words (Từ cấm/regex kiểm duyệt)

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|---|---|---|---|
| id | BIGINT | PK | ID danh mục cấm |
| word_or_regex | VARCHAR(255) | NOT NULL, UNIQUE INDEX | Từ hoặc pattern biểu thức chính quy |
| is_regex | BOOLEAN | NOT NULL, DEFAULT FALSE | Có phải là regex hay không |
| added_by_admin_id | BIGINT | FK → users(id) ON DELETE SET NULL, NULL | Người thêm |
| created_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Thời điểm tạo |

---

### Bảng 33: activity_logs (Nhật ký hành động hệ thống)

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|---|---|---|---|
| id | BIGINT | PK | ID bản ghi |
| user_id | BIGINT | FK → users(id) ON DELETE SET NULL, NULL, INDEX | Người thực hiện hành động |
| action | VARCHAR(100) | NOT NULL, INDEX | Hành động (VD: LOGIN, DELETE_POST, ...) |
| target_type | ENUM('POST','USER','COMMENT','BOOKING','MEDIA','SESSION') | NOT NULL | Đối tượng tương tác |
| target_id | BIGINT | NULL | ID đối tượng bị tác động |
| ip_address | VARCHAR(45) | NULL | Địa chỉ IP máy khách |
| user_agent | VARCHAR(500) | NULL | Browser/App Client Agent |
| metadata | TEXT | NULL | Thông tin kỹ thuật dạng JSON bổ sung |
| created_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP, INDEX | Thời điểm tạo |
