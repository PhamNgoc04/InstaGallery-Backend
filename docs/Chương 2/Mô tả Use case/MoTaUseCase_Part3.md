## --- Nhóm: Portfolio & Booking (tiếp) ---

### 2.2.4.40. Mô tả Use Case "Quản lý Booking"

1. **Tên Use Case:** Quản lý Booking.
2. **Mô tả vắn tắt:**
   Use case này cho phép Nhiếp ảnh gia (Photographer) xem danh sách, xác nhận, bắt đầu, hoàn thành hoặc hủy các yêu cầu đặt lịch chụp từ khách hàng.
3. **Luồng các sự kiện**

   3.1. Luồng cơ bản
   1) Use case này bắt đầu khi Photographer mở mục "Đơn booking" trên ứng dụng.
   2) Hệ thống gọi API GET /bookings?role=photographer, truy vấn Bảng bookings JOIN Bảng users lấy danh sách booking của Photographer, hiển thị danh sách có lọc theo trạng thái (All / Pending / Confirmed / In Progress / Completed / Cancelled).
   3) Photographer kích chọn một booking có trạng thái PENDING để xem chi tiết (tên khách hàng, ngày chụp, yêu cầu, giá dự kiến).
   4) Photographer nhấn nút "Xác nhận".
   5) Hệ thống gọi API PUT /bookings/{id} với status = "CONFIRMED". Server validate state machine: PENDING → CONFIRMED (hợp lệ), cập nhật Bảng bookings.
   6) Hệ thống INSERT thông báo vào Bảng notifications gửi cho Client: "Booking đã được xác nhận".
   7) Đến ngày chụp, Photographer nhấn "Bắt đầu". Hệ thống cập nhật status = IN_PROGRESS.
   8) Sau khi hoàn thành buổi chụp, Photographer nhấn "Hoàn thành". Hệ thống cập nhật status = COMPLETED.
   9) Hệ thống gửi thông báo cho Client, Client có thể đánh giá. Use case kết thúc.

   3.2. Các luồng rẽ nhánh
   1) Tại bước 4, nếu Photographer chọn "Hủy" thay vì "Xác nhận", hệ thống cập nhật status = CANCELLED và gửi thông báo cho Client.
   2) Tại bước 5, nếu chuyển trạng thái không hợp lệ (ví dụ: COMPLETED → CONFIRMED), hệ thống hiển thị lỗi "Không thể chuyển trạng thái" và kết thúc.
   3) Tại bước 7, Client cũng có thể hủy booking khi status là PENDING hoặc CONFIRMED.

4. **Các yêu cầu đặc biệt:**
   - Booking tuân theo State Machine: PENDING → CONFIRMED → IN_PROGRESS → COMPLETED. Có thể CANCELLED từ PENDING hoặc CONFIRMED.
5. **Tiền điều kiện:** Người dùng đã đăng nhập với vai trò Photographer.
6. **Hậu điều kiện:** Trạng thái booking được cập nhật trong Bảng bookings, Client nhận thông báo.
7. **Điểm mở rộng:** Không có.

---

### 2.2.4.36. Mô tả Use Case "Đánh giá thợ ảnh"

1. **Tên Use Case:** Đánh giá thợ ảnh.
2. **Mô tả vắn tắt:**
   Use case này cho phép khách hàng (Client) đánh giá sao và viết nhận xét về Nhiếp ảnh gia sau khi buổi chụp hoàn thành.
3. **Luồng các sự kiện**

   3.1. Luồng cơ bản
   1) Use case này bắt đầu khi Client mở booking có trạng thái COMPLETED. Hệ thống hiển thị nút "Đánh giá".
   2) Client nhấn nút "Đánh giá". Hệ thống hiển thị form đánh giá gồm: chọn sao (1-5) và ô nhập nhận xét.
   3) Client chọn số sao (ví dụ 4 sao), viết nhận xét và nhấn nút "Gửi đánh giá".
   4) Hệ thống gọi API POST /ratings, validate: booking.status = COMPLETED, chưa có đánh giá cho booking này (mỗi booking chỉ đánh giá 1 lần), rating_value BETWEEN 1 AND 5, rater_id ≠ ratee_id.
   5) Hệ thống INSERT vào Bảng ratings (booking_id, rater_id, ratee_id, rating_value, comment).
   6) Hệ thống cập nhật Bảng portfolios: tính lại rating_avg = tổng sao / tổng reviews, tăng review_count lên 1.
   7) Hệ thống hiển thị "Đánh giá đã được gửi. Cảm ơn bạn!". Use case kết thúc.

   3.2. Các luồng rẽ nhánh
   1) Tại bước 4, nếu booking chưa hoàn thành (status ≠ COMPLETED), hệ thống hiển thị "Chỉ đánh giá được khi buổi chụp đã hoàn thành".
   2) Tại bước 4, nếu đã có đánh giá cho booking này, hệ thống hiển thị "Bạn đã đánh giá buổi chụp này rồi".
   3) Tại bước 3, nếu Client không chọn sao (rating_value = 0), nút "Gửi" bị vô hiệu hóa.

4. **Các yêu cầu đặc biệt:**
   - Mỗi booking chỉ được đánh giá 1 lần. Không thể tự đánh giá chính mình.
5. **Tiền điều kiện:** Người dùng đã đăng nhập với vai trò Client. Booking có status = COMPLETED và chưa được đánh giá.
6. **Hậu điều kiện:** Đánh giá được lưu trong Bảng ratings. Rating trung bình và số lượt đánh giá của Photographer được cập nhật.
7. **Điểm mở rộng:** Không có.

---

## --- Nhóm: Chat ---

### 2.2.4.37. Mô tả Use Case "Nhắn tin (Chat)"

1. **Tên Use Case:** Nhắn tin (Chat).
2. **Mô tả vắn tắt:**
   Use case này cho phép người dùng (Client, Photographer) nhắn tin trực tiếp real-time với người dùng khác trên hệ thống.
3. **Luồng các sự kiện**

   3.1. Luồng cơ bản
   1) Use case này bắt đầu khi người dùng nhấn biểu tượng 💬 trên profile người khác hoặc vào tab Messages.
   2) Hệ thống gọi API POST /conversations kiểm tra trong Bảng conversations xem đã có cuộc trò chuyện DIRECT giữa 2 người hay chưa.
   3) Nếu chưa có, hệ thống INSERT vào Bảng conversations (type = DIRECT) và Bảng conversation_members (2 bản ghi cho 2 người).
   4) Hệ thống gọi API GET /conversations/{convId}/messages truy vấn Bảng messages lấy lịch sử tin nhắn (cursor pagination, tin mới nhất ở dưới) và hiển thị trên màn hình.
   5) Ứng dụng kết nối WebSocket đến server để nhận tin nhắn real-time.
   6) Người dùng nhập tin nhắn và nhấn nút "Gửi". Khi người dùng bắt đầu gõ, ứng dụng gửi sự kiện `typing` qua WebSocket; người nhận thấy trạng thái "Đang nhập..." dưới tên người gửi.
   7) Tin nhắn gửi qua WebSocket, server INSERT vào Bảng messages (conversation_id, sender_id, content, type = TEXT), UPDATE conversations.updated_at.
   8) Server broadcast tin nhắn đến người nhận qua WebSocket (nếu đang online).
   9) Tin nhắn hiển thị trên màn hình người gửi (bubble phải) và người nhận (bubble trái, kèm unread badge). Use case kết thúc.

   3.2. Các luồng rẽ nhánh
   1) Tại bước 2, nếu đã có cuộc trò chuyện, hệ thống trả về conversation hiện có thay vì tạo mới.
   2) Tại bước 8, nếu người nhận offline, tin nhắn được lưu trong database và hiển thị unread badge khi họ mở app.
   3) Tại bước 6, nếu lỗi kết nối WebSocket, hệ thống hiển thị "Không thể gửi tin nhắn, kiểm tra kết nối mạng".

4. **Các yêu cầu đặc biệt:**
   - Tin nhắn sử dụng WebSocket cho real-time communication.
   - Hỗ trợ gửi text, ảnh và video.
5. **Tiền điều kiện:** Người dùng đã đăng nhập vào hệ thống.
6. **Hậu điều kiện:** Tin nhắn được gửi và lưu trong Bảng messages, người nhận nhận được tin nhắn.
7. **Điểm mở rộng:** Không có.

---

## --- Nhóm: Quản trị ---

### 2.2.4.42. Mô tả Use Case "Quản lý người dùng"

1. **Tên Use Case:** Quản lý người dùng.
2. **Mô tả vắn tắt:**
   Use case này cho phép Quản trị viên (Admin) xem danh sách, tìm kiếm, khóa/mở khóa, xóa tài khoản và thay đổi quyền người dùng trên hệ thống.
3. **Luồng các sự kiện**

   3.1. Luồng cơ bản
   1) Use case này bắt đầu khi Admin truy cập trang quản lý và kích chọn "Quản lý người dùng".
   2) Hệ thống gọi API GET /admin/users, xác thực role = ADMIN, truy vấn Bảng users lấy danh sách người dùng. Hiển thị: avatar, tên, email, role, trạng thái, ngày tạo. Hỗ trợ filter theo role/status và phân trang.
   3) Admin tìm kiếm user cụ thể bằng tên, email hoặc username.
   4) Admin kích chọn user để xem chi tiết: thông tin cá nhân, số bài đăng, số followers, activity logs.
   5) Admin thực hiện hành động quản trị: Khóa (ban), Mở khóa (unban), Xác minh (verify), Xóa (soft delete) hoặc Đổi role.
   6) Ví dụ: Admin nhấn "Khóa". Hệ thống gọi API POST /admin/users/{userId}/ban, UPDATE Bảng users SET is_active = FALSE, xóa tất cả sessions trong Bảng user_sessions, INSERT vào Bảng activity_logs ghi action = BAN_USER.
   7) Hệ thống hiển thị "User đã bị khóa thành công". Use case kết thúc.

   3.2. Các luồng rẽ nhánh
   1) Tại bước 5, nếu Admin chọn "Mở khóa", hệ thống UPDATE is_active = TRUE và ghi log action = UNBAN_USER.
   2) Tại bước 5, nếu Admin chọn "Xóa", hệ thống thực hiện soft delete (UPDATE deleted_at = NOW()) thay vì xóa vĩnh viễn.
   3) Tại bước 2, nếu danh sách users rỗng (không có kết quả filter), hệ thống hiển thị "Không tìm thấy người dùng".

4. **Các yêu cầu đặc biệt:**
   - Chỉ Admin mới có quyền truy cập. Mọi hành động quản trị phải ghi log vào Bảng activity_logs.
5. **Tiền điều kiện:** Người dùng đã đăng nhập với vai trò Admin.
6. **Hậu điều kiện:** Thao tác quản trị được thực hiện và ghi log.
7. **Điểm mở rộng:** Không có.

---

### 2.2.4.43. Mô tả Use Case "Kiểm duyệt nội dung"

1. **Tên Use Case:** Kiểm duyệt nội dung.
2. **Mô tả vắn tắt:**
   Use case này cho phép Quản trị viên (Admin) xem và xử lý các báo cáo vi phạm nội dung trên hệ thống.
3. **Luồng các sự kiện**

   3.1. Luồng cơ bản
   1) Use case này bắt đầu khi Admin mở trang "Kiểm duyệt nội dung" trên trang quản lý.
   2) Hệ thống gọi API GET /admin/reports?status=PENDING, truy vấn Bảng reports JOIN Bảng users lấy danh sách báo cáo, ưu tiên PENDING lên trước.
   3) Admin kích chọn một báo cáo để xem chi tiết: nội dung bị báo cáo (bài đăng / comment), lý do báo cáo, thông tin người báo cáo.
   4) Admin xem xét nội dung và quyết định:
      - Nếu **vi phạm**: nhấn "Xóa nội dung + Giải quyết". Hệ thống gọi API DELETE /admin/posts/{postId} (soft delete: UPDATE deleted_at = NOW()), sau đó gọi API POST /admin/reports/{id}/resolve cập nhật Bảng reports SET status = RESOLVED, reviewed_by = admin_id.
      - Nếu **không vi phạm**: nhấn "Từ chối". Hệ thống gọi API PUT /admin/reports/{reportId} cập nhật status = DISMISSED.
   5) (Tùy chọn) Admin ghi chú admin_note và gửi cảnh báo cho người vi phạm.
   6) Hệ thống cập nhật danh sách reports. Use case kết thúc.

   3.2. Các luồng rẽ nhánh
   1) Tại bước 2, nếu không có báo cáo nào đang chờ xử lý, hệ thống hiển thị "Không có báo cáo nào cần xử lý".
   2) Tại bước 4, nếu nội dung bị báo cáo đã bị xóa trước đó, hệ thống hiển thị "Nội dung đã được xử lý" và cho phép đóng report.

4. **Các yêu cầu đặc biệt:**
   - Xóa bài đăng sử dụng soft delete, không xóa vĩnh viễn khỏi database.
5. **Tiền điều kiện:** Người dùng đã đăng nhập với vai trò Admin.
6. **Hậu điều kiện:** Báo cáo được xử lý (RESOLVED hoặc DISMISSED), nội dung vi phạm bị xóa nếu có.
7. **Điểm mở rộng:** Không có.

---

### 2.2.4.44. Mô tả Use Case "Thống kê hệ thống"

1. **Tên Use Case:** Thống kê hệ thống.
2. **Mô tả vắn tắt:**
   Use case này cho phép Quản trị viên (Admin) xem dashboard thống kê tổng quan về hoạt động của hệ thống InstaGallery.
3. **Luồng các sự kiện**

   3.1. Luồng cơ bản
   1) Use case này bắt đầu khi Admin truy cập trang Dashboard trên trang quản lý.
   2) Hệ thống gọi API GET /admin/dashboard/stats, truy vấn các Bảng users, posts, bookings, reports để tính tổng số liệu.
   3) Hệ thống hiển thị số liệu tổng quan: tổng số người dùng (users), tổng số bài đăng (posts), tổng số booking, số báo cáo đang chờ xử lý (reports PENDING).
   4) Hệ thống gọi API GET /admin/dashboard/growth?period=30days truy vấn dữ liệu tăng trưởng theo thời gian.
   5) Hệ thống hiển thị biểu đồ tăng trưởng: users mới / posts mới theo ngày/tuần/tháng.
   6) Hệ thống hiển thị thống kê chi tiết: phân bố vai trò (Client/Photographer/Admin), phân bố trạng thái bookings, danh sách top users hoạt động tích cực nhất. Use case kết thúc.

   3.2. Các luồng rẽ nhánh
   1) Tại bước 4, Admin có thể thay đổi khoảng thời gian thống kê (7 ngày / 30 ngày / 90 ngày / 1 năm), hệ thống tải lại dữ liệu tương ứng.
   2) Tại bước 2, nếu lỗi truy vấn database, hệ thống hiển thị "Không thể tải dữ liệu thống kê, vui lòng thử lại".

4. **Các yêu cầu đặc biệt:** Không có.
5. **Tiền điều kiện:** Người dùng đã đăng nhập với vai trò Admin.
6. **Hậu điều kiện:** Dữ liệu thống kê hệ thống được hiển thị trên Dashboard.
7. **Điểm mở rộng:** Không có.
