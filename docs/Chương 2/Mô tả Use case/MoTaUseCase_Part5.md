# MÔ TẢ CHI TIẾT CÁC USE CASE HỆ THỐNG INSTAGALLERY (PHẦN BỔ SUNG CUỐI CÙNG)

Phần này bổ sung 14 Use Case còn thiếu từ việc phân tích tổng thể 45 Use Case duy nhất của hệ thống, bao gồm các chức năng nâng cao và vận hành.

## --- Nhóm: Xác thực & Quản lý tài khoản ---

### 2.2.4.3. Mô tả Use Case "Đăng nhập bằng Google (OAuth 2.0)"

1. **Tên Use Case:** Đăng nhập bằng Google.
2. **Mô tả vắn tắt:** Cho phép người dùng đăng nhập nhanh hoặc tạo tài khoản mới bằng tài khoản Google (OAuth 2.0).
3. **Luồng các sự kiện:**
   1) Người dùng nhấn nút "Đăng nhập với Google" tại màn hình Login.
   2) Ứng dụng mở Chrome Custom Tabs/In-App Browser gọi đến Google OAuth Consent Screen.
   3) Người dùng đồng ý cấp quyền (email, profile).
   4) Google trả về `id_token` cho ứng dụng. Ứng dụng gửi `id_token` này lên server qua API `POST /auth/google`.
   5) Server xác thực `id_token` thông qua thư viện Google Auth API. Lấy được email và tên.
   6) Server kiểm tra Bảng `users` xem email đã tồn tại hay chưa.
      - **Nếu có:** Tạo phiên đăng nhập, sinh JWT Access/Refresh Token.
      - **Nếu chưa:** Tự động tạo người dùng mới với loại tài khoản mặc định là Client, sau đó sinh Tokens.
   7) Trả Tokens về Client, chuyển vào màn hình Feed.
4. **Các yêu cầu đặc biệt:** Liên kết tài khoản mượt mà, không yêu cầu người dùng xác nhận email lại.
5. **Tiền điều kiện:** Có tài khoản Google hợp lệ.
6. **Hậu điều kiện:** Đăng nhập thành công, phiên đăng nhập được lưu.

### 2.2.4.7. Mô tả Use Case "Bật/Tắt xác thực 2 yếu tố (2FA)"

1. **Tên Use Case:** Quản lý Xác thực 2 yếu tố (2FA).
2. **Mô tả vắn tắt:** Người dùng tăng cường bảo mật bằng cách bật 2FA thông qua OTP Email hoặc ứng dụng Authenticator.
3. **Luồng các sự kiện:**
   1) Người dùng vào Settings → Bảo mật → Xác thực 2 yếu tố.
   2) Nhấn nút "Bật 2FA".
   3) Server sinh ngẫu nhiên TOTP Secret key, lưu tạm và trả về hình ảnh mã QR (Authenticator) hoặc gửi OTP Email.
   4) Người dùng quét QR vào Google Authenticator và nhập mã số 6 chữ số sinh ra để xác nhận.
   5) Server gọi API xác nhận mã. Nếu đúng, UPDATE `is_2fa_enabled = TRUE`, sinh 10 mã khôi phục (Recovery Codes) cho người dùng lưu trữ.
4. **Các yêu cầu đặc biệt:** Mã 2FA có hạn dùng 30s.

### 2.2.4.9. Mô tả Use Case "Chuyển đổi tài khoản Public / Private"

1. **Tên Use Case:** Tài khoản Riêng tư (Private).
2. **Mô tả vắn tắt:** Người dùng có quyền chuyển đổi hồ sơ sang chế độ Private, khi đó chỉ người follow được duyệt mới thấy bài đăng.
3. **Luồng các sự kiện:**
   1) Người dùng vào Settings → Quyền riêng tư.
   2) Bật/tắt switch "Tài khoản Private".
   3) Cảnh báo hệ thống: "Nếu là Private, người khác phải gửi yêu cầu follow. Các follower hiện tại không bị ảnh hưởng." Nhấn Đồng ý.
   4) Gọi API `PUT /users/me/privacy` (is_private).
   5) Server UPDATE Bảng `users`.

## --- Nhóm: Ảnh & Tương tác ---

### 2.2.4.16. Mô tả Use Case "Áp dụng bộ lọc & chỉnh sửa ảnh"

1. **Tên Use Case:** Bộ lọc và Chỉnh sửa ảnh cơ bản.
2. **Mô tả vắn tắt:** Trong quá trình Đăng bài, người dùng chỉnh sửa màu sắc, filter trước khi upload.
3. **Luồng các sự kiện:**
   1) Tại giao diện tạo bài (sau khi chọn ảnh), nhấn tab "Filter" hoặc "Chỉnh sửa".
   2) Hệ thống cung cấp danh sách 10-15 bộ lọc có sẵn (thực hiện qua ColorMatrix trên Client).
   3) Người dùng điều chỉnh thanh trượt độ sáng, tương phản, bão hòa (crop, xoay).
   4) Hệ thống lưu giá trị matrix lên bitmap kết quả hoặc áp dụng realtime.
   5) Nhấn "Xong" → Ảnh đã qua xử lý được tiến hành encode (WebP) gửi lên Server.

### 2.2.4.17. Mô tả Use Case "Quản lý Album / Bộ sưu tập"

1. **Tên Use Case:** Quản lý Album.
2. **Mô tả vắn tắt:** Cho phép gom nhóm các bài đăng vào chung một Album mang tính chủ đề.
3. **Luồng các sự kiện:**
   1) Người dùng mở thẻ "Album" ở MyProfile → Nhấn "Tạo Album mới".
   2) Nhập tên, mô tả, chọn ảnh bìa, chọn Public/Private.
   3) API `POST /albums`.
   4) Tại bài đăng cụ thể, nhấn menu "Thêm vào Album", chọn danh sách album hiện có.
   5) Album được lưu trong Bảng `albums`, quan hệ N-N trong Bảng `album_posts`.

### 2.2.4.18. Mô tả Use Case "Gắn thẻ người dùng trong ảnh (Tag People)"

1. **Tên Use Case:** Tag People.
2. **Mô tả vắn tắt:** Cho phép định danh bạn bè tại vị trí (x, y) trên bức ảnh.
3. **Luồng các sự kiện:**
   1) Tại màn hình Đăng bài, nhấn "Gắn thẻ người dùng".
   2) Chạm tay vào một điểm cụ thể trên khung hình ảnh, lưu tọa độ tương đối X, Y.
   3) Ô tìm kiếm user hiện lên, người dùng chọn tài khoản để gắn tag.
   4) Sau khi đăng, dữ liệu được INSERT vào bảng `post_tags`.
   5) Người được tag sẽ nhận thông báo.

### 2.2.4.24. Mô tả Use Case "Duyệt Hashtag"

1. **Tên Use Case:** Khám phá theo Hashtag.
2. **Mô tả vắn tắt:** Nhấn vào chuỗi #Hashtag sẽ đưa đến danh sách hàng loạt bài viết cùng chủ đề.
3. **Luồng các sự kiện:**
   1) Người dùng nhấn dòng `#DaLat` trong phần Caption.
   2) Gọi API `GET /hashtags/DaLat/posts`.
   3) Hệ thống hiển thị Màn hình Hashtag Detail với Grid ảnh mượt mà, kèm theo số liệu: "XX triệu bài đăng". Nút "Follow Hashtag".

### 2.2.4.25. Mô tả Use Case "Mention (@username)"

1. **Tên Use Case:** Mention.
2. **Mô tả vắn tắt:** Đề cập ai đó gọi trực tiếp (tag name) trong Caption hoặc Comment.
3. **Luồng các sự kiện:**
   1) Người dùng đang viết bình luận, gõ ký tự "@", UI trả về dropdown Autocomplete bạn bè.
   2) Khi đăng tài liệu, Server tách chuỗi RegEx các user tag (@).
   3) Lưu bản ghi thông báo loại MENTION gửi cho chủ thuê bao được tag.

## --- Nhóm: Tương tác Xã hội & Điều hướng Nội dung ---

### 2.2.4.27. Mô tả Use Case "Xem nhật ký hoạt động (Activity Feed)"

1. **Tên Use Case:** Xem nhật ký hoạt động.
2. **Mô tả vắn tắt:** Người dùng có thể tra cứu toàn bộ vết tương tác do mình đã gây ra trong quá khứ.
3. **Luồng các sự kiện:**
   1) Tại Profile, vào Settings → Lịch sử hoạt động.
   2) Ứng dụng chia làm các danh mục: Lượt Thích / Bình luận cũ / Bài đăng đã xem.
   3) Có thể nhấn "Unlike" hoặc "Xóa comment" nhanh chóng từ lịch sử này.

### 2.2.4.28. Mô tả Use Case "Xem gợi ý theo dõi (Suggestion)"

1. **Tên Use Case:** Gợi ý kết bạn.
2. **Mô tả vắn tắt:** Khám phá những Photographer hoặc bạn của bạn bè có thể bạn sẽ quan tâm.
3. **Luồng các sự kiện:**
   1) Thuật toán Recommendations ở API `GET /users/suggestions` đối chiếu Bảng `followers` xem có "bạn chung" không, kèm thuật toán KNN (chung sở thích tag).
   2) Trả về UI danh sách dạng ngang (Carousel) trong màn Feed khi "hết nội dung" hoặc trong Explore. Có nút "Follow" và "X" (Ẩn gợi ý).

### 2.2.4.31. Mô tả Use Case "Tắt tiếng người dùng (Mute)"

1. **Tên Use Case:** Mute.
2. **Mô tả vắn tắt:** Ẩn hết Post hoặc Story của người đang theo dõi mà không Unfollow. Tránh mất lòng.
3. **Luồng các sự kiện:**
   1) Mở menu "⋮" trên Feed của 1 người. Chọn "Tắt tiếng (Mute)".
   2) Gọi API `POST /users/{id}/mute`. Cập nhật trạng thái vào Bảng `muted_users`.
   3) Thuật toán hiển thị Feed sau thao tác này sẽ Filter bỏ mọi `post` sinh ra từ `muted_users`.

### 2.2.4.32. Mô tả Use Case "Giới hạn bình luận"

1. **Tên Use Case:** Restrict Comments.
2. **Mô tả vắn tắt:** Quản lý rủi ro trên Internet bằng việc chặn người lạ bình luận trong bức ảnh công khai.
3. **Luồng các sự kiện:**
   1) Settings → Quyền riêng tư → Bình luận.
   2) Chọn mức độ giới hạn: "Tất cả mọi người" (Default) / "Chỉ người tôi đang Follow" / "Chỉ Followers" / "Không ai cả".
   3) Lưu trạng thái cá nhân `comment_permission` vào database.

## --- Nhóm: Portfolio & Booking ---

### 2.2.4.41. Mô tả Use Case "Thiết lập lịch khả dụng (Availability Calendar)"

1. **Tên Use Case:** Thiết lập Lịch trống.
2. **Mô tả vắn tắt:** Nhiếp ảnh gia định nghĩa sẵn các ngày nhận/hủy sô chụp.
3. **Luồng các sự kiện:**
   1) Tại Dashboard của Photographer, nhấn Quản lý Lịch biểu.
   2) Chọn View theo tháng. Nhấn chọn ngày cụ thể hoặc chuỗi ngày thứ Bảy-Chủ Nhật.
   3) Đánh dấu trạng thái `Available` (có thể nhận Booking) hoặc `Blocked` (đi nghỉ mát, bận).
   4) Gọi API `PUT /photographers/availabilities`. Lịch này được Client tham chiếu khi lên đơn Booking.

## --- Nhóm: Quản trị ---

### 2.2.4.45. Mô tả Use Case "Quản lý từ khóa cấm (Profanity Filter)"

1. **Tên Use Case:** Quản lý Regex từ khóa nhạy cảm.
2. **Mô tả vắn tắt:** Admin thêm từ vựng/nhạy cảm vào Blacklist hệ thống. Quét tự động để lọc nội dung bẩn.
3. **Luồng các sự kiện:**
   1) Tại Web Admin, Admin truy cập Dashboard "Từ Cấm".
   2) Điền thêm text hoặc Regular Expression, ví dụ `*sex*`, `scam*`. Nhấn "Cập nhật".
   3) Lưu bảng `profanity_words`.
   4) Khi người dùng gửi Comment / Post Caption, API Backend chạy qua Interceptor khớp chuỗi. Nếu Match thì đưa vào trạng thái Đợi duyệt (`Pending Review`) thay vì hiển thị Publish. Ngăn ngừa khủng hoảng truyền thông của ứng dụng.
