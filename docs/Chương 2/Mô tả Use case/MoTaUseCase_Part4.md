## --- Nhóm: Xác thực & Quản lý tài khoản (bổ sung) ---

### 2.2.4.4. Mô tả Use Case "Đăng xuất"

1. **Tên Use Case:** Đăng xuất.
2. **Mô tả vắn tắt:**
   Use case này cho phép người dùng (Client, Photographer, Admin) đăng xuất khỏi phiên đăng nhập hiện tại trên hệ thống InstaGallery.
3. **Luồng các sự kiện**

   3.1. Luồng cơ bản
   1) Use case này bắt đầu khi người dùng kích chọn tab "Profile" trên thanh Bottom Navigation, sau đó bấm biểu tượng ⚙️ (Settings).
   2) Hệ thống hiển thị danh sách cài đặt. Người dùng cuộn xuống và nhấn mục "Đăng xuất" (text màu đỏ).
   3) Hệ thống hiển thị hộp thoại xác nhận: "Bạn có chắc chắn muốn đăng xuất?"
   4) Người dùng nhấn "Đồng ý".
   5) Hệ thống gọi API POST /auth/logout với Refresh Token hiện tại.
   6) Server xóa bản ghi tương ứng trong Bảng user_sessions (vô hiệu hóa Refresh Token).
   7) Ứng dụng xóa tất cả tokens khỏi TokenStore trên thiết bị.
   8) Hệ thống chuyển người dùng về màn hình Đăng nhập (LoginScreen). Use case kết thúc.

   3.2. Các luồng rẽ nhánh
   1) Tại bước 4, nếu người dùng nhấn "Hủy", hệ thống đóng hộp thoại và quay lại trang Settings.
   2) Tại bước 5, nếu xảy ra lỗi mạng khi gọi API, hệ thống vẫn xóa tokens trên thiết bị (đăng xuất local) và chuyển về LoginScreen. Token trên server sẽ tự hết hạn.

4. **Các yêu cầu đặc biệt:**
   - Hệ thống phải xóa tất cả dữ liệu nhạy cảm trên thiết bị khi đăng xuất (tokens, cached user data).
5. **Tiền điều kiện:** Người dùng đã đăng nhập vào hệ thống.
6. **Hậu điều kiện:** Phiên đăng nhập bị xóa khỏi Bảng user_sessions, tokens bị xóa khỏi thiết bị, người dùng cần đăng nhập lại.
7. **Điểm mở rộng:** Không có.

---

### 2.2.4.6. Mô tả Use Case "Đổi mật khẩu"

1. **Tên Use Case:** Đổi mật khẩu.
2. **Mô tả vắn tắt:**
   Use case này cho phép người dùng (Client, Photographer, Admin) thay đổi mật khẩu đăng nhập khi đã đăng nhập vào hệ thống, yêu cầu xác thực mật khẩu cũ trước khi đặt mật khẩu mới.
3. **Luồng các sự kiện**

   3.1. Luồng cơ bản
   1) Use case này bắt đầu khi người dùng vào Settings → nhấn "Đổi mật khẩu".
   2) Hệ thống hiển thị form gồm 3 trường: Mật khẩu hiện tại, Mật khẩu mới, Xác nhận mật khẩu mới.
   3) Người dùng nhập đầy đủ thông tin và nhấn "Lưu".
   4) Hệ thống kiểm tra tính hợp lệ: mật khẩu mới tối thiểu 8 ký tự, chứa ít nhất 1 chữ hoa, 1 số và 1 ký tự đặc biệt; mật khẩu xác nhận khớp.
   5) Hệ thống gọi API PUT /auth/change-password { current_password, new_password }.
   6) Server xác thực mật khẩu hiện tại bằng bcrypt.verify(current_password, password_hash).
   7) Server hash mật khẩu mới bằng bcrypt (cost = 12) và UPDATE password_hash trong Bảng users.
   8) Server xóa TẤT CẢ phiên đăng nhập khác của user trong Bảng user_sessions (trừ phiên hiện tại), buộc các thiết bị khác đăng nhập lại.
   9) Hệ thống hiển thị "Đổi mật khẩu thành công" và quay lại màn hình Settings. Use case kết thúc.

   3.2. Các luồng rẽ nhánh
   1) Tại bước 6, nếu mật khẩu hiện tại không đúng, hệ thống hiển thị "Mật khẩu hiện tại không chính xác" và yêu cầu nhập lại.
   2) Tại bước 4, nếu mật khẩu mới dưới 8 ký tự, hệ thống hiển thị "Mật khẩu phải có ít nhất 8 ký tự".
   3) Tại bước 4, nếu mật khẩu xác nhận không khớp, hệ thống hiển thị "Mật khẩu xác nhận không khớp".
   4) Tại bước 4, nếu mật khẩu mới trùng với mật khẩu hiện tại, hệ thống hiển thị "Mật khẩu mới phải khác mật khẩu hiện tại".

4. **Các yêu cầu đặc biệt:**
   - Mật khẩu mới phải được mã hóa bcrypt trước khi lưu.
   - Sau khi đổi mật khẩu, tất cả phiên đăng nhập khác phải bị hủy (bảo mật).
5. **Tiền điều kiện:** Người dùng đã đăng nhập vào hệ thống.
6. **Hậu điều kiện:** Mật khẩu mới được cập nhật trong Bảng users, các phiên đăng nhập cũ bị xóa.
7. **Điểm mở rộng:** Không có.

---

## --- Nhóm: Ảnh & Tương tác (bổ sung) ---

### 2.2.4.12. Mô tả Use Case "Xóa bài đăng"

1. **Tên Use Case:** Xóa bài đăng.
2. **Mô tả vắn tắt:**
   Use case này cho phép chủ bài đăng hoặc Quản trị viên (Admin) xóa bài đăng khỏi hệ thống bằng phương pháp soft delete.
3. **Luồng các sự kiện**

   3.1. Luồng cơ bản
   1) Use case này bắt đầu khi người dùng vào màn hình chi tiết bài đăng của mình, kích chọn biểu tượng menu "⋮" và chọn "Xóa".
   2) Hệ thống hiển thị hộp thoại xác nhận: "Xóa bài đăng này? Hành động này không thể hoàn tác."
   3) Người dùng nhấn "Xóa".
   4) Hệ thống gọi API DELETE /posts/{postId} (JWT), xác thực quyền sở hữu (user_id = current user).
   5) Server thực hiện soft delete: UPDATE Bảng posts SET deleted_at = NOW() WHERE id = postId.
   6) Server giảm post_count trong Bảng users của chủ bài đăng.
   7) Hệ thống hiển thị thông báo "Đã xóa bài đăng" và chuyển về màn hình trước (Feed hoặc Profile). Use case kết thúc.

   3.2. Các luồng rẽ nhánh
   1) Tại bước 3, nếu người dùng nhấn "Hủy", hệ thống đóng hộp thoại, bài đăng không bị xóa.
   2) Tại bước 4, nếu người dùng không phải chủ bài đăng và không phải Admin, hệ thống hiển thị "Bạn không có quyền xóa bài đăng này" và kết thúc.
   3) Tại bước 4, nếu bài đăng đã bị xóa trước đó (deleted_at IS NOT NULL), hệ thống hiển thị "Bài đăng không tồn tại" và kết thúc.

4. **Các yêu cầu đặc biệt:**
   - Sử dụng soft delete (cập nhật deleted_at) thay vì xóa vĩnh viễn khỏi database.
   - Các dữ liệu liên quan (likes, comments, notifications) không bị xóa vật lý, chỉ không hiển thị do bài đăng đã bị soft delete.
5. **Tiền điều kiện:** Người dùng đã đăng nhập, là chủ bài đăng hoặc Admin.
6. **Hậu điều kiện:** Bài đăng bị đánh dấu xóa mềm (deleted_at ≠ NULL), post_count giảm 1.
7. **Điểm mở rộng:** Không có.

---

### 2.2.4.21. Mô tả Use Case "Lưu bài đăng (Save / Unsave)"

1. **Tên Use Case:** Lưu bài đăng (Save / Unsave).
2. **Mô tả vắn tắt:**
   Use case này cho phép người dùng (Client, Photographer) lưu bài đăng yêu thích vào danh sách cá nhân để xem lại sau, hoặc bỏ lưu bài đã lưu.
3. **Luồng các sự kiện**

   3.1. Luồng cơ bản — Save
   1) Người dùng nhấn nút 🔖 (bookmark) trên bài đăng trong Feed, Explore hoặc PostDetail.
   2) Hệ thống cập nhật giao diện ngay lập tức (Optimistic UI): icon bookmark chuyển sang filled (đã lưu).
   3) Hệ thống gọi API POST /posts/{postId}/save (JWT).
   4) Server INSERT vào Bảng saved_posts (user_id, post_id, saved_at). Use case kết thúc.

   3.1b. Luồng cơ bản — Unsave
   1) Người dùng nhấn nút 🔖 lần nữa trên bài đã lưu.
   2) Hệ thống gọi API DELETE /posts/{postId}/save (JWT).
   3) Server DELETE bản ghi tương ứng trong Bảng saved_posts.
   4) Giao diện cập nhật: icon bookmark trở về outline (chưa lưu). Use case kết thúc.

   3.2. Các luồng rẽ nhánh
   1) Tại bước 3 (Save), nếu bài đã được lưu trước đó (trùng UNIQUE user_id + post_id), server trả về lỗi và UI không thay đổi.
   2) Tại bất kỳ bước nào, nếu xảy ra lỗi mạng, hệ thống rollback UI (Optimistic UI) và hiển thị thông báo lỗi.

4. **Các yêu cầu đặc biệt:**
   - Save sử dụng Optimistic UI tương tự Like để trải nghiệm mượt mà.
5. **Tiền điều kiện:** Người dùng đã đăng nhập vào hệ thống.
6. **Hậu điều kiện:** Bài đăng được lưu/bỏ lưu trong Bảng saved_posts.
7. **Điểm mở rộng:** Không có.

---

### 2.2.4.29. Mô tả Use Case "Báo cáo vi phạm"

1. **Tên Use Case:** Báo cáo vi phạm.
2. **Mô tả vắn tắt:**
   Use case này cho phép người dùng (Client, Photographer) báo cáo nội dung vi phạm (bài đăng, bình luận hoặc người dùng) để Admin xem xét và xử lý.
3. **Luồng các sự kiện**

   3.1. Luồng cơ bản
   1) Use case này bắt đầu khi người dùng nhấn biểu tượng menu "⋮" trên bài đăng/comment của người khác và chọn "Báo cáo".
   2) Hệ thống hiển thị danh sách lý do báo cáo: Nội dung không phù hợp, Spam, Quấy rối, Vi phạm bản quyền, Khác.
   3) Người dùng chọn lý do và (tùy chọn) nhập mô tả chi tiết.
   4) Người dùng nhấn "Gửi báo cáo".
   5) Hệ thống gọi API POST /reports { target_type, target_id, reason }.
   6) Server INSERT vào Bảng reports (reporter_id, target_type, target_id, reason, status = PENDING).
   7) Hệ thống hiển thị "Cảm ơn bạn đã báo cáo. Chúng tôi sẽ xem xét nội dung này." Use case kết thúc.

   3.2. Các luồng rẽ nhánh
   1) Tại bước 1, nếu người dùng cố báo cáo nội dung của chính mình, hệ thống không hiển thị tùy chọn "Báo cáo" trong menu.
   2) Tại bước 5, nếu người dùng đã báo cáo cùng nội dung này trước đó (trùng reporter_id + target_type + target_id), hệ thống hiển thị "Bạn đã báo cáo nội dung này rồi".
   3) Tại bất kỳ bước nào, nếu xảy ra lỗi mạng, hệ thống hiển thị thông báo lỗi.

4. **Các yêu cầu đặc biệt:**
   - Mỗi người chỉ được báo cáo 1 nội dung 1 lần.
   - Không thông báo cho người bị báo cáo (bảo mật).
5. **Tiền điều kiện:** Người dùng đã đăng nhập vào hệ thống.
6. **Hậu điều kiện:** Báo cáo được tạo trong Bảng reports với status = PENDING, chờ Admin xử lý.
7. **Điểm mở rộng:** Không có.

---

## --- Nhóm: Portfolio & Booking (bổ sung) ---

### 2.2.4.39. Mô tả Use Case "Tạo / Chỉnh sửa Portfolio"

1. **Tên Use Case:** Tạo / Chỉnh sửa Portfolio.
2. **Mô tả vắn tắt:**
   Use case này cho phép Nhiếp ảnh gia (Photographer) tạo mới hoặc chỉnh sửa hồ sơ năng lực (portfolio) của mình trên hệ thống, bao gồm chuyên môn, giá/giờ, khu vực hoạt động.
3. **Luồng các sự kiện**

   3.1. Luồng cơ bản — Tạo Portfolio mới
   1) Use case này bắt đầu khi Photographer vào trang Profile của mình và nhấn nút "Tạo Portfolio" (hiển thị khi chưa có portfolio).
   2) Hệ thống hiển thị form tạo portfolio gồm: Tiêu đề, Mô tả, Chuyên môn (multi-select tags: Wedding, Portrait, Landscape,...), Giá/giờ, Đơn vị tiền tệ, Khu vực hoạt động, Trạng thái sẵn sàng nhận booking.
   3) Photographer nhập đầy đủ thông tin và nhấn "Lưu".
   4) Hệ thống gọi API POST /users/me/portfolio.
   5) Server INSERT vào Bảng portfolios (user_id, title, specialties, hourly_rate, service_area, is_available).
   6) Hệ thống hiển thị "Đã tạo hồ sơ năng lực thành công!" và cập nhật trang Profile. Use case kết thúc.

   3.1b. Luồng cơ bản — Chỉnh sửa Portfolio
   1) Photographer vào trang Profile, nhấn "Chỉnh sửa Portfolio".
   2) Hệ thống gọi API GET /users/me/portfolio lấy dữ liệu portfolio hiện tại.
   3) Hiển thị form chỉnh sửa với dữ liệu pre-fill.
   4) Photographer sửa thông tin cần thay đổi và nhấn "Lưu".
   5) Hệ thống gọi API PUT /users/me/portfolio, cập nhật Bảng portfolios.
   6) Hiển thị "Đã cập nhật portfolio" và cập nhật giao diện. Use case kết thúc.

   3.2. Các luồng rẽ nhánh
   1) Tại bước 3/4 (tạo mới), nếu giá/giờ để trống hoặc ≤ 0, hệ thống hiển thị "Giá/giờ phải lớn hơn 0".
   2) Tại bước 1 (tạo mới), nếu Photographer đã có portfolio, hệ thống hiển thị nút "Chỉnh sửa" thay vì "Tạo mới".
   3) Tại bước 4 (chỉnh sửa), nếu xảy ra lỗi server, hệ thống hiển thị thông báo lỗi.

4. **Các yêu cầu đặc biệt:**
   - Mỗi Photographer chỉ có tối đa 1 portfolio (quan hệ 1-1 với Bảng users).
5. **Tiền điều kiện:** Người dùng đã đăng nhập với vai trò Photographer.
6. **Hậu điều kiện:** Portfolio được tạo/cập nhật thành công trong Bảng portfolios.
7. **Điểm mở rộng:** Không có.

---

## --- Nhóm: Thông báo ---

### 2.2.4.38. Mô tả Use Case "Xem thông báo"

1. **Tên Use Case:** Xem thông báo.
2. **Mô tả vắn tắt:**
   Use case này cho phép người dùng (Client, Photographer) xem danh sách thông báo về các hoạt động liên quan đến mình trên hệ thống, bao gồm like, comment, follow, và booking.
3. **Luồng các sự kiện**

   3.1. Luồng cơ bản
   1) Use case này bắt đầu khi người dùng kích chọn tab "🔔 Thông báo" trên thanh Bottom Navigation.
   2) Hệ thống gọi API GET /notifications (JWT), truy vấn Bảng notifications WHERE user_id = current user, sắp xếp theo created_at DESC, phân trang.
   3) Hệ thống hiển thị danh sách thông báo, mỗi thông báo gồm: icon loại (❤️ like, 💬 comment, 👤 follow, 📅 booking), avatar người gửi, nội dung, thời gian (relative).
   4) Thông báo chưa đọc (is_read = false) có nền highlight đậm hơn.
   5) Người dùng nhấn vào một thông báo.
   6) Hệ thống gọi API PUT /notifications/{id}/read, cập nhật is_read = true trong Bảng notifications.
   7) Hệ thống điều hướng đến nội dung liên quan: Like/Comment → PostDetailScreen, Follow → UserProfileScreen, Booking → BookingDetailScreen. Use case kết thúc.

   3.2. Các luồng rẽ nhánh
   1) Tại bước 2, nếu chưa có thông báo nào, hệ thống hiển thị Empty State: "Chưa có thông báo nào".
   2) Tại bước 1, người dùng nhấn nút "Đọc tất cả" (góc trên phải), hệ thống gọi API PUT /notifications/read-all, cập nhật is_read = true cho tất cả thông báo chưa đọc.
   3) Tại bước 7, nếu nội dung liên quan đã bị xóa (bài đăng deleted_at ≠ NULL), hệ thống hiển thị "Nội dung không còn tồn tại".

4. **Các yêu cầu đặc biệt:**
   - Tab thông báo hiển thị unread badge (số đỏ) khi có thông báo chưa đọc.
5. **Tiền điều kiện:** Người dùng đã đăng nhập vào hệ thống.
6. **Hậu điều kiện:** Danh sách thông báo được hiển thị, thông báo đã nhấn được đánh dấu đã đọc.
7. **Điểm mở rộng:** Không có.

---

## --- Nhóm: An toàn & Bảo mật (bổ sung) ---

### 2.2.4.30. Mô tả Use Case "Chặn người dùng (Block / Unblock)"

1. **Tên Use Case:** Chặn người dùng (Block / Unblock).
2. **Mô tả vắn tắt:**
   Use case này cho phép người dùng (Client, Photographer) chặn một người dùng khác. Người bị chặn không thể xem profile, bài đăng, nhắn tin hoặc follow người đã chặn mình.
3. **Luồng các sự kiện**

   3.1. Luồng cơ bản — Block
   1) Use case này bắt đầu khi người dùng vào profile người khác, nhấn menu "⋮" và chọn "Chặn người dùng".
   2) Hệ thống hiển thị hộp thoại xác nhận: "Chặn @username? Họ sẽ không thể xem profile, bài đăng hoặc nhắn tin cho bạn."
   3) Người dùng nhấn "Chặn".
   4) Hệ thống gọi API POST /users/{userId}/block (JWT).
   5) Server INSERT vào Bảng blocked_users (blocker_id, blocked_id).
   6) Server tự động xóa quan hệ follow giữa 2 người (nếu có): DELETE FROM followers WHERE (follower_id, following_id) IN cả 2 chiều.
   7) Server cập nhật follower_count / following_count tương ứng.
   8) Hệ thống hiển thị "Đã chặn @username" và cập nhật UI. Use case kết thúc.

   3.1b. Luồng cơ bản — Unblock
   1) Người dùng vào Settings → "Tài khoản đã chặn" → nhấn "Bỏ chặn" cạnh tên người bị chặn.
   2) Hệ thống gọi API DELETE /users/{userId}/block (JWT).
   3) Server DELETE bản ghi trong Bảng blocked_users.
   4) Giao diện cập nhật. Hai người chưa tự động follow lại (phải tự follow). Use case kết thúc.

   3.2. Các luồng rẽ nhánh
   1) Tại bước 1, không thể chặn chính mình — hệ thống không hiển thị tùy chọn "Chặn" trên profile của mình.
   2) Tại bước 4, nếu đã chặn người này rồi, server trả về 409 Conflict.

4. **Các yêu cầu đặc biệt:**
   - Khi bị chặn, tất cả API liên quan đến người chặn phải trả 404 (ẩn hoàn toàn sự tồn tại).
   - Các bài đăng của người chặn không hiển thị trong Feed, Explore, Search của người bị chặn.
5. **Tiền điều kiện:** Người dùng đã đăng nhập vào hệ thống.
6. **Hậu điều kiện:** Bản ghi chặn được tạo/xóa trong Bảng blocked_users, quan hệ follow bị hủy.
7. **Điểm mở rộng:** Không có.

---

### 2.2.4.33. Mô tả Use Case "Xóa tài khoản"

1. **Tên Use Case:** Xóa tài khoản.
2. **Mô tả vắn tắt:**
   Use case này cho phép người dùng xóa vĩnh viễn tài khoản của mình khỏi hệ thống, tuân thủ quy định GDPR/PDPA về quyền được quên (Right to be Forgotten).
3. **Luồng các sự kiện**

   3.1. Luồng cơ bản
   1) Use case này bắt đầu khi người dùng vào Settings → "Tài khoản" → "Xóa tài khoản".
   2) Hệ thống hiển thị cảnh báo: "⚠️ Xóa tài khoản là vĩnh viễn. Tất cả bài đăng, bình luận, và dữ liệu cá nhân sẽ bị xóa. Hành động này không thể hoàn tác."
   3) Người dùng nhập mật khẩu xác nhận và chọn lý do xóa (tùy chọn).
   4) Người dùng nhấn "Xóa tài khoản vĩnh viễn".
   5) Hệ thống gọi API DELETE /users/me { password } (JWT).
   6) Server xác thực mật khẩu bằng bcrypt.verify(password, password_hash).
   7) Server thực hiện soft-delete: UPDATE Bảng users SET deleted_at = NOW(), is_active = FALSE.
   8) Server xóa tất cả phiên đăng nhập: DELETE FROM user_sessions WHERE user_id = ?.
   9) Ứng dụng xóa tokens, chuyển về LoginScreen với thông báo "Tài khoản đã được xóa". Use case kết thúc.

   3.2. Các luồng rẽ nhánh
   1) Tại bước 6, nếu mật khẩu sai, hệ thống hiển thị "Mật khẩu không chính xác" và yêu cầu nhập lại.
   2) Tại bước 4, nếu người dùng nhấn "Hủy", hệ thống quay lại Settings.
   3) Nếu user có booking PENDING/CONFIRMED/IN_PROGRESS, hệ thống hiển thị "Bạn có booking đang xử lý. Vui lòng hủy/hoàn thành trước khi xóa tài khoản."

4. **Các yêu cầu đặc biệt:**
   - Sử dụng soft-delete (deleted_at) để có thể đáp ứng yêu cầu pháp lý nếu cần.
   - Dữ liệu bị xóa mềm sẽ được hệ thống tự động purge (xóa vật lý) sau 30 ngày.
   - Apple App Store yêu cầu bắt buộc app phải có tính năng xóa tài khoản.
5. **Tiền điều kiện:** Người dùng đã đăng nhập, không có booking đang xử lý.
6. **Hậu điều kiện:** Tài khoản bị đánh dấu xóa mềm, tất cả phiên đăng nhập bị hủy.
7. **Điểm mở rộng:** Không có.

---

### 2.2.4.26. Mô tả Use Case "Chia sẻ bài đăng"

1. **Tên Use Case:** Chia sẻ bài đăng (Share Post).
2. **Mô tả vắn tắt:**
   Use case này cho phép người dùng (Client, Photographer) chia sẻ bài đăng qua deep link hoặc gửi trực tiếp qua chat nội bộ cho người dùng khác.
3. **Luồng các sự kiện**

   3.1. Luồng cơ bản — Chia sẻ qua link
   1) Người dùng nhấn nút 📤 (share) trên bài đăng trong Feed hoặc PostDetail.
   2) Hệ thống hiển thị bottom sheet gồm: "Sao chép liên kết", "Chia sẻ qua...", danh sách người dùng gần đây (chat).
   3) Người dùng chọn "Sao chép liên kết".
   4) Hệ thống tạo deep link: `instagallery://post/{postId}` và copy vào clipboard.
   5) Hệ thống gọi API POST /posts/{postId}/share (JWT) để cập nhật share_count.
   6) Server UPDATE Bảng posts SET share_count = share_count + 1.
   7) Hiển thị "Đã sao chép liên kết". Use case kết thúc.

   3.1b. Luồng cơ bản — Chia sẻ qua chat nội bộ
   1) Tại bottom sheet, người dùng chọn một người dùng trong danh sách gần đây.
   2) Hệ thống gọi API POST /messages { conversation_id, content: post_link, message_type: "POST_SHARE" }.
   3) Server INSERT vào Bảng messages, cập nhật share_count.
   4) Hiển thị "Đã gửi". Use case kết thúc.

   3.2. Các luồng rẽ nhánh
   1) Tại bước 3, nếu người dùng chọn "Chia sẻ qua..." → mở Android share intent (chia sẻ ra app bên ngoài).
   2) Tại bước 1, nếu bài đăng có visibility = PRIVATE, hệ thống không hiển thị nút share.

4. **Các yêu cầu đặc biệt:**
   - Chỉ bài đăng PUBLIC mới có thể chia sẻ.
   - Deep link tự mở app nếu đã cài, fallback về web nếu chưa cài.
5. **Tiền điều kiện:** Người dùng đã đăng nhập, bài đăng có chế độ PUBLIC.
6. **Hậu điều kiện:** share_count tăng 1, link được copy hoặc tin nhắn được gửi.
7. **Điểm mở rộng:** Không có.
