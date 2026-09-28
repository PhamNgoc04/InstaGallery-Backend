## --- Nhóm: Ảnh & Tương tác (tiếp) ---

### 2.2.4.11. Mô tả Use Case "Chỉnh sửa bài đăng"

1. **Tên Use Case:** Chỉnh sửa bài đăng.
2. **Mô tả vắn tắt:**
   Use case này cho phép chủ bài đăng chỉnh sửa caption và chế độ hiển thị của bài đăng đã tạo trên hệ thống.
3. **Luồng các sự kiện**

   3.1. Luồng cơ bản
   1) Use case này bắt đầu khi người dùng vào màn hình chi tiết bài đăng của mình, kích chọn biểu tượng menu "⋮" và chọn "Sửa".
   2) Hệ thống gọi API GET /posts/{postId} truy vấn Bảng posts để lấy thông tin bài đăng, kiểm tra quyền sở hữu (user_id = current user).
   3) Hệ thống hiển thị form chỉnh sửa với dữ liệu pre-fill: caption, location, visibility (Public/Private/Friends Only). Ảnh chỉ hiển thị xem trước, không cho phép thay đổi.
   4) Người dùng chỉnh sửa caption, thay đổi chế độ hiển thị hoặc vị trí.
   5) Người dùng nhấn nút "Lưu".
   6) Hệ thống gọi API PUT /posts/{postId}, xác thực quyền sở hữu, cập nhật caption, visibility, updated_at trong Bảng posts.
   7) Hệ thống hiển thị thông báo "Đã cập nhật bài đăng" và cập nhật giao diện. Use case kết thúc.

   3.2. Các luồng rẽ nhánh
   1) Tại bước 2, nếu người dùng không phải là chủ bài đăng, hệ thống hiển thị "Bạn không có quyền chỉnh sửa bài đăng này" và kết thúc use case.
   2) Tại bước 5, nếu người dùng nhấn "Hủy", hệ thống không lưu thay đổi và quay lại màn hình chi tiết bài đăng.
   3) Tại bất kỳ bước nào, nếu xảy ra lỗi server, hệ thống hiển thị thông báo lỗi và kết thúc use case.

4. **Các yêu cầu đặc biệt:**
   - Không cho phép thay đổi ảnh/video đã upload trong bài đăng.
5. **Tiền điều kiện:** Người dùng đã đăng nhập và là chủ sở hữu bài đăng.
6. **Hậu điều kiện:** Bài đăng được cập nhật thành công trong Bảng posts.
7. **Điểm mở rộng:** Không có.

---

### 2.2.4.19. Mô tả Use Case "Like / Unlike bài đăng"

1. **Tên Use Case:** Like / Unlike.
2. **Mô tả vắn tắt:**
   Use case này cho phép người dùng (Client, Photographer) bày tỏ sự yêu thích (like) hoặc bỏ thích (unlike) đối với một bài đăng trên hệ thống.
3. **Luồng các sự kiện**

   3.1. Luồng cơ bản — Like
   1) Người dùng nhấn nút ❤️ trên bài đăng trong Feed, Explore hoặc PostDetail.
   2) Hệ thống cập nhật giao diện ngay lập tức (Optimistic UI): icon chuyển sang đỏ, số like tăng 1.
   3) Hệ thống gọi API POST /posts/{postId}/like, INSERT vào Bảng likes. Trigger trong database tự động tăng like_count trong Bảng posts.
   4) Hệ thống INSERT thông báo vào Bảng notifications (type = NEW_LIKE) và gửi cho chủ bài đăng. Use case kết thúc.

   3.1b. Luồng cơ bản — Unlike
   1) Người dùng nhấn nút ❤️ lần nữa trên bài đã like.
   2) Hệ thống gọi API DELETE /posts/{postId}/like, xóa bản ghi trong Bảng likes. Trigger tự động giảm like_count trong Bảng posts.
   3) Giao diện cập nhật: icon trở về trắng, số like giảm 1. Use case kết thúc.

   3.2. Các luồng rẽ nhánh
   1) Tại luồng Like bước 3, nếu người dùng đã like bài này trước đó (trùng UNIQUE user_id + post_id), server trả về lỗi và UI rollback.
   2) Tại bất kỳ bước nào, nếu xảy ra lỗi mạng, hệ thống rollback Optimistic UI và hiển thị thông báo lỗi.

4. **Các yêu cầu đặc biệt:**
   - Like sử dụng Optimistic UI (cập nhật giao diện trước khi server phản hồi) để trải nghiệm mượt mà.
5. **Tiền điều kiện:** Người dùng đã đăng nhập vào hệ thống.
6. **Hậu điều kiện:** Like/Unlike được ghi nhận trong Bảng likes, like_count được cập nhật, chủ bài đăng nhận thông báo (khi like).
7. **Điểm mở rộng:** Không có.

---

### 2.2.4.20. Mô tả Use Case "Bình luận (Comment)"

1. **Tên Use Case:** Bình luận (Comment).
2. **Mô tả vắn tắt:**
   Use case này cho phép người dùng (Client, Photographer) viết bình luận và trả lời bình luận (threaded comments) trên bài đăng trong hệ thống.
3. **Luồng các sự kiện**

   3.1. Luồng cơ bản — Viết bình luận
   1) Người dùng nhấn biểu tượng 💬 hoặc cuộn xuống phần bình luận trên PostDetailScreen.
   2) Người dùng nhập nội dung bình luận vào ô nhập và nhấn "Gửi".
   3) Hệ thống gọi API POST /posts/{postId}/comments, INSERT vào Bảng comments, cập nhật comment_count trong Bảng posts.
   4) Hệ thống INSERT thông báo vào Bảng notifications (type = NEW_COMMENT) gửi cho chủ bài đăng.
   5) Bình luận mới hiển thị trên giao diện. Use case kết thúc.

   3.1b. Luồng cơ bản — Trả lời bình luận (Reply)
   1) Người dùng nhấn "Trả lời" trên một bình luận.
   2) Người dùng nhập nội dung trả lời và nhấn "Gửi".
   3) Hệ thống lưu bình luận với parent_comment_id trỏ đến comment gốc, depth tăng 1 (tối đa 3 cấp). Use case kết thúc.

   3.2. Các luồng rẽ nhánh
   1) Tại bước 2, nếu nội dung bình luận rỗng, nút "Gửi" bị vô hiệu hóa.
   2) Tại luồng Reply bước 3, nếu depth ≥ 3, hệ thống không cho phép trả lời thêm (flatten vào cấp 3).
   3) Tại bất kỳ bước nào, nếu xảy ra lỗi mạng, hệ thống hiển thị thông báo lỗi.

4. **Các yêu cầu đặc biệt:**
   - Threaded comments hỗ trợ tối đa 3 cấp.
5. **Tiền điều kiện:** Người dùng đã đăng nhập vào hệ thống.
6. **Hậu điều kiện:** Bình luận được ghi nhận trong Bảng comments, comment_count được cập nhật, chủ bài đăng nhận thông báo.
7. **Điểm mở rộng:** Không có.

---

### 2.2.4.23. Mô tả Use Case "Tìm kiếm ảnh / Thợ ảnh / Hashtag"

1. **Tên Use Case:** Tìm kiếm ảnh / Thợ ảnh.
2. **Mô tả vắn tắt:**
   Use case này cho phép người dùng (Client, Photographer) tìm kiếm người dùng, bài đăng và hashtag trên hệ thống.
3. **Luồng các sự kiện**

   3.1. Luồng cơ bản
   1) Use case này bắt đầu khi người dùng kích chọn tab "Explore" trên thanh Bottom Navigation và nhấn vào thanh tìm kiếm.
   2) Hệ thống gọi API GET /search/history truy vấn Bảng search_histories để lấy lịch sử tìm kiếm gần đây (tối đa 10 mục), hiển thị danh sách kèm nút xóa từng mục.
   3) Người dùng bắt đầu gõ từ khóa. Hệ thống gọi API GET /search/autocomplete gợi ý người dùng và hashtag khớp từ Bảng users và Bảng media_tags.
   4) Người dùng nhấn nút tìm hoặc chọn một gợi ý từ dropdown.
   5) Hệ thống gọi API GET /search với keyword, thực hiện full-text search trên Bảng users, Bảng posts, Bảng media_tags. Kết quả trả về chia thành 3 tab: Users / Posts / Tags.
   6) Hệ thống lưu lịch sử tìm kiếm vào Bảng search_histories.
   7) Người dùng nhấn vào kết quả để chuyển đến trang Profile hoặc PostDetail. Use case kết thúc.

   3.2. Các luồng rẽ nhánh
   1) Tại bước 5, nếu không tìm thấy kết quả nào, hệ thống hiển thị "Không tìm thấy kết quả cho từ khóa này".
   2) Tại bước 2, nếu người dùng nhấn "Xóa tất cả", hệ thống xóa toàn bộ lịch sử tìm kiếm trong Bảng search_histories.

4. **Các yêu cầu đặc biệt:**
   - Autocomplete phải phản hồi trong 500ms để trải nghiệm mượt mà.
5. **Tiền điều kiện:** Người dùng đã đăng nhập vào hệ thống.
6. **Hậu điều kiện:** Kết quả tìm kiếm được hiển thị, lịch sử tìm kiếm được lưu lại.
7. **Điểm mở rộng:** Không có.

---

### 2.2.4.22. Mô tả Use Case "Follow / Unfollow"

1. **Tên Use Case:** Follow / Unfollow.
2. **Mô tả vắn tắt:**
   Use case này cho phép người dùng (Client, Photographer) theo dõi hoặc bỏ theo dõi người dùng khác trên hệ thống.
3. **Luồng các sự kiện**

   3.1. Luồng cơ bản — Follow
   1) Use case này bắt đầu khi người dùng xem trang Profile của người dùng khác và nhấn nút "Follow".
   2) Hệ thống gọi API POST /users/{userId}/follow, INSERT bản ghi mới vào Bảng followers (follower_id = người follow, following_id = người được follow).
   3) Trigger trong database tự động tăng following_count của người follow và follower_count của người được follow trong Bảng users.
   4) Hệ thống INSERT thông báo vào Bảng notifications (type = NEW_FOLLOWER): "X đã bắt đầu follow bạn".
   5) Giao diện cập nhật: nút chuyển từ "Follow" thành "Following". Use case kết thúc.

   3.1b. Luồng cơ bản — Unfollow
   1) Người dùng nhấn nút "Following" trên profile người đang follow.
   2) Hệ thống gọi API DELETE /users/{userId}/follow, xóa bản ghi trong Bảng followers.
   3) Trigger giảm following_count và follower_count tương ứng.
   4) Giao diện cập nhật: nút chuyển từ "Following" về "Follow". Use case kết thúc.

   3.2. Các luồng rẽ nhánh
   1) Tại bước 1 (Follow), nếu người dùng cố follow chính mình, hệ thống hiển thị "Bạn không thể follow chính mình".
   2) Tại bất kỳ bước nào, nếu xảy ra lỗi mạng, hệ thống hiển thị thông báo lỗi.

4. **Các yêu cầu đặc biệt:** Không có.
5. **Tiền điều kiện:** Người dùng đã đăng nhập vào hệ thống.
6. **Hậu điều kiện:** Quan hệ follow được tạo/xóa trong Bảng followers, các counter được cập nhật trong Bảng users.
7. **Điểm mở rộng:** Không có.

---

## --- Nhóm: Portfolio & Booking ---

### 2.2.4.34. Mô tả Use Case "Xem Portfolio nhiếp ảnh gia"

1. **Tên Use Case:** Xem Portfolio nhiếp ảnh gia.
2. **Mô tả vắn tắt:**
   Use case này cho phép người dùng (Client, Photographer) xem hồ sơ năng lực, tác phẩm và đánh giá của nhiếp ảnh gia trên hệ thống.
3. **Luồng các sự kiện**

   3.1. Luồng cơ bản
   1) Use case này bắt đầu khi người dùng truy cập trang Profile của một Photographer và nhấn nút "Xem Portfolio".
   2) Hệ thống gọi API GET /users/{userId}/portfolio truy vấn Bảng portfolios lấy thông tin hồ sơ năng lực.
   3) Hệ thống hiển thị: ảnh bìa, chuyên môn (tags), khu vực hoạt động, giá/giờ (hourly_rate), trạng thái sẵn sàng (is_available).
   4) Hệ thống gọi API GET /users/{userId}/ratings truy vấn Bảng ratings lấy danh sách đánh giá.
   5) Hệ thống hiển thị: điểm trung bình (rating_avg), từng nhận xét gồm sao, nội dung và tên client.
   6) Hệ thống gọi API GET /users/{username}/posts truy vấn Bảng posts lấy gallery ảnh đẹp nhất.
   7) Hệ thống hiển thị gallery ảnh và các nút "Đặt lịch chụp" và "Nhắn tin". Use case kết thúc.

   3.2. Các luồng rẽ nhánh
   1) Tại bước 2, nếu Photographer chưa tạo portfolio, hệ thống hiển thị "Nhiếp ảnh gia chưa tạo hồ sơ năng lực".
   2) Tại bước 4, nếu chưa có đánh giá nào, hệ thống hiển thị "Chưa có đánh giá".

4. **Các yêu cầu đặc biệt:** Không có.
5. **Tiền điều kiện:** Người dùng đã đăng nhập vào hệ thống.
6. **Hậu điều kiện:** Thông tin Portfolio đầy đủ được hiển thị trên màn hình.
7. **Điểm mở rộng:** Không có.

---

### 2.2.4.35. Mô tả Use Case "Đặt lịch chụp"

1. **Tên Use Case:** Đặt lịch chụp (Booking).
2. **Mô tả vắn tắt:**
   Use case này cho phép khách hàng (Client) đặt lịch chụp ảnh với nhiếp ảnh gia (Photographer) trên hệ thống.
3. **Luồng các sự kiện**

   3.1. Luồng cơ bản
   1) Use case này bắt đầu khi Client nhấn nút "Đặt lịch chụp" trên trang Portfolio hoặc Profile của Photographer.
   2) Hệ thống gọi API GET /photographers/{id}/availability truy vấn Bảng bookings lấy các ngày đã có lịch (status IN ('CONFIRMED', 'IN_PROGRESS')).
   3) Hệ thống hiển thị Calendar, các ngày đã đặt bị disable (không chọn được).
   4) Client chọn ngày chụp trên Calendar. Hệ thống kiểm tra ngày đó còn trống.
   5) Client nhập yêu cầu chi tiết, chọn thời lượng, xem giá dự kiến (tính từ hourly_rate trong Bảng portfolios).
   6) Client nhấn nút "Đặt lịch".
   7) Hệ thống gọi API POST /bookings, INSERT vào Bảng bookings với status = PENDING.
   8) Hệ thống INSERT thông báo vào Bảng notifications gửi cho Photographer: "Có yêu cầu đặt lịch mới".
   9) Hệ thống hiển thị "Đặt lịch thành công! Đang chờ xác nhận từ thợ ảnh". Use case kết thúc.

   3.2. Các luồng rẽ nhánh
   1) Tại bước 4, nếu ngày chọn đã có lịch (trùng booking CONFIRMED/IN_PROGRESS), hệ thống hiển thị "Ngày này đã được đặt, vui lòng chọn ngày khác".
   2) Tại bước 2, nếu Photographer không sẵn sàng (is_available = false), hệ thống hiển thị "Nhiếp ảnh gia hiện không nhận lịch" và kết thúc.
   3) Tại bước 6, nếu thiếu thông tin bắt buộc, nút "Đặt lịch" bị vô hiệu hóa.

4. **Các yêu cầu đặc biệt:** Không có.
5. **Tiền điều kiện:** Người dùng đã đăng nhập với vai trò Client. Photographer có portfolio và đang ở trạng thái sẵn sàng.
6. **Hậu điều kiện:** Booking mới được tạo trong Bảng bookings với status = PENDING, Photographer nhận được thông báo.
7. **Điểm mở rộng:** Không có.
