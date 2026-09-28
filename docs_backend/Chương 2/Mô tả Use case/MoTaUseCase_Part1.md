# MÔ TẢ CHI TIẾT CÁC USE CASE HỆ THỐNG INSTAGALLERY

## --- Nhóm: Xác thực & Quản lý tài khoản ---

### 2.2.4.1. Mô tả Use Case "Đăng ký tài khoản"

1. **Tên Use Case:** Đăng ký tài khoản.
2. **Mô tả vắn tắt:**
   Use case này cho phép người dùng mới tạo tài khoản trên hệ thống InstaGallery bằng cách cung cấp thông tin cá nhân cơ bản, lựa chọn loại tài khoản (Client hoặc Photographer) để sử dụng các chức năng của ứng dụng.
3. **Luồng các sự kiện**

   3.1. Luồng cơ bản
   1) Use case này bắt đầu khi người dùng mở ứng dụng InstaGallery và kích chọn nút "Đăng ký" trên màn hình chào mừng. Hệ thống hiển thị màn hình đăng ký.
   2) Hệ thống hiển thị form đăng ký gồm các trường: Username, Email, Mật khẩu, Xác nhận mật khẩu, và lựa chọn loại tài khoản (Client / Photographer).
   3) Người dùng nhập đầy đủ thông tin vào các trường và nhấn nút "Đăng ký".
   4) Hệ thống kiểm tra tính hợp lệ của dữ liệu đầu vào: email đúng định dạng, mật khẩu tối thiểu 8 ký tự, mật khẩu xác nhận khớp với mật khẩu.
   5) Hệ thống kiểm tra trong Bảng users xem email và username đã tồn tại hay chưa. Nếu chưa tồn tại, hệ thống tiếp tục xử lý.
   6) Hệ thống mã hóa mật khẩu bằng thuật toán bcrypt (cost = 12) và lưu thông tin tài khoản mới vào Bảng users.
   7) Hệ thống tạo JWT Access Token (thời hạn 15 phút) và Refresh Token (thời hạn 30 ngày), lưu phiên đăng nhập vào Bảng user_sessions.
   8) Hệ thống trả về thông tin người dùng và tokens, lưu tokens qua TokenStore trên thiết bị và chuyển người dùng đến màn hình bật Vị trí (Location). Use case kết thúc.

   3.2. Các luồng rẽ nhánh
   1) Tại bước 4, nếu email không đúng định dạng, hệ thống hiển thị thông báo "Email không hợp lệ" và yêu cầu nhập lại.
   2) Tại bước 4, nếu mật khẩu dưới 8 ký tự, hệ thống hiển thị thông báo "Mật khẩu phải có ít nhất 8 ký tự" và yêu cầu nhập lại.
   3) Tại bước 4, nếu mật khẩu xác nhận không khớp, hệ thống hiển thị thông báo "Mật khẩu xác nhận không khớp" và yêu cầu nhập lại.
   4) Tại bước 5, nếu email đã tồn tại trong Bảng users, hệ thống hiển thị thông báo "Email đã được sử dụng" và yêu cầu nhập email khác.
   5) Tại bước 5, nếu username đã tồn tại trong Bảng users, hệ thống hiển thị thông báo "Username đã được sử dụng" và yêu cầu nhập username khác.
   6) Tại bất kỳ bước nào, nếu xảy ra lỗi kết nối mạng hoặc lỗi server, hệ thống hiển thị thông báo lỗi "Đã xảy ra lỗi, vui lòng thử lại" và kết thúc use case.

4. **Các yêu cầu đặc biệt:**
   - Mật khẩu phải được mã hóa bằng bcrypt trước khi lưu vào database, không lưu dạng plaintext.
   - Hệ thống phải giới hạn tối đa 3 lần đăng ký trong 1 giờ từ cùng một IP (rate limiting).
5. **Tiền điều kiện:** Người dùng chưa có tài khoản trên hệ thống và đang ở màn hình đăng ký.
6. **Hậu điều kiện:** Tài khoản mới được tạo thành công trong Bảng users, phiên đăng nhập được tạo trong Bảng user_sessions, người dùng được chuyển đến màn hình Location.
7. **Điểm mở rộng:** Không có.

---

### 2.2.4.2. Mô tả Use Case "Đăng nhập"

1. **Tên Use Case:** Đăng nhập.
2. **Mô tả vắn tắt:**
   Use case này cho phép người dùng (Client, Photographer, Admin) xác thực tài khoản để truy cập vào hệ thống InstaGallery.
3. **Luồng các sự kiện**

   3.1. Luồng cơ bản
   1) Use case này bắt đầu khi người dùng mở ứng dụng InstaGallery. Hệ thống kiểm tra token đăng nhập trong bộ nhớ cục bộ thông qua TokenStore.
   2) Nếu không có token, hệ thống hiển thị màn hình Đăng nhập gồm các trường: Email, Mật khẩu và nút "Đăng nhập".
   3) Người dùng nhập email và mật khẩu, sau đó nhấn nút "Đăng nhập".
   4) Hệ thống kiểm tra tính hợp lệ của dữ liệu đầu vào (email đúng định dạng, mật khẩu không rỗng) và gửi yêu cầu đến API.
   5) Hệ thống kiểm tra giới hạn đăng nhập (rate limit): tối đa 5 lần trong 15 phút từ cùng một IP qua Redis Cache.
   6) Hệ thống truy vấn Bảng users theo email, kiểm tra tài khoản tồn tại.
   7) Hệ thống so sánh mật khẩu nhập vào với password_hash đã lưu bằng thuật toán bcrypt.
   8) Hệ thống kiểm tra trạng thái tài khoản (is_active = true) trong Bảng users.
   9) Hệ thống tạo Access Token (15 phút) và Refresh Token (30 ngày), lưu phiên đăng nhập vào Bảng user_sessions và cache vào Redis.
   10) Hệ thống trả về tokens và thông tin người dùng, lưu tokens qua TokenStore, chuyển người dùng đến màn hình Feed. Use case kết thúc.

   3.2. Các luồng rẽ nhánh
   1) Tại bước 1, nếu có token hợp lệ (chưa hết hạn) trong bộ nhớ cục bộ, hệ thống tự động chuyển người dùng vào màn hình Feed mà không cần đăng nhập lại. Use case kết thúc.
   2) Tại bước 1, nếu Access Token hết hạn nhưng Refresh Token còn hợp lệ, hệ thống gọi API POST /auth/refresh để lấy Access Token mới. Nếu thành công, chuyển vào Feed. Nếu thất bại, hiển thị màn hình Đăng nhập.
   3) Tại bước 5, nếu vượt quá giới hạn rate limit (5 lần/15 phút), hệ thống hiển thị thông báo "Vui lòng thử lại sau 15 phút" và kết thúc use case.
   4) Tại bước 6, nếu email không tồn tại trong Bảng users, hệ thống hiển thị "Email hoặc mật khẩu không đúng" (không tiết lộ cụ thể) và kết thúc use case.
   5) Tại bước 7, nếu mật khẩu không khớp, hệ thống hiển thị "Email hoặc mật khẩu không đúng" và kết thúc use case.
   6) Tại bước 8, nếu tài khoản bị khóa (is_active = false), hệ thống hiển thị "Tài khoản đã bị vô hiệu hóa, vui lòng liên hệ admin" và kết thúc use case.

4. **Các yêu cầu đặc biệt:**
   - Tokens phải được truy cập qua TokenStore; giai đoạn dev/MVP có thể dùng DataStoreTokenStore, production nên dùng SecureTokenStore.
   - Hệ thống phải áp dụng rate limiting: tối đa 5 lần đăng nhập trong 15 phút.
5. **Tiền điều kiện:** Người dùng đã có tài khoản hợp lệ trên hệ thống.
6. **Hậu điều kiện:** Người dùng đăng nhập thành công, phiên đăng nhập được tạo, được chuyển đến màn hình Feed.
7. **Điểm mở rộng:** Không có.

---

### 2.2.4.5. Mô tả Use Case "Quên mật khẩu"

1. **Tên Use Case:** Quên mật khẩu.
2. **Mô tả vắn tắt:**
   Use case này cho phép người dùng (Client, Photographer) khôi phục mật khẩu thông qua email khi quên mật khẩu đăng nhập.
3. **Luồng các sự kiện**

   3.1. Luồng cơ bản
   1) Use case này bắt đầu khi người dùng nhấn liên kết "Quên mật khẩu?" trên màn hình Đăng nhập. Hệ thống hiển thị màn hình nhập email.
   2) Người dùng nhập email đã đăng ký và nhấn nút "Gửi link đặt lại".
   3) Hệ thống kiểm tra email có tồn tại trong Bảng users hay không.
   4) Nếu email tồn tại, hệ thống tạo reset token (hết hạn sau 1 giờ) và gửi email chứa link đặt lại mật khẩu cho người dùng.
   5) Hệ thống hiển thị thông báo "Nếu email tồn tại, link đặt lại đã được gửi. Vui lòng kiểm tra email".
   6) Người dùng mở email và nhấn vào link đặt lại mật khẩu. Ứng dụng mở màn hình Đặt lại mật khẩu qua deep link.
   7) Hệ thống hiển thị form gồm: Mật khẩu mới, Xác nhận mật khẩu mới.
   8) Người dùng nhập mật khẩu mới và xác nhận, sau đó nhấn nút "Đặt lại mật khẩu".
   9) Hệ thống xác thực reset token chưa hết hạn, mã hóa mật khẩu mới bằng bcrypt, cập nhật password_hash trong Bảng users.
   10) Hệ thống xóa tất cả phiên đăng nhập cũ trong Bảng user_sessions của người dùng.
   11) Hệ thống hiển thị thông báo "Đặt lại mật khẩu thành công!" và chuyển về màn hình Đăng nhập. Use case kết thúc.

   3.2. Các luồng rẽ nhánh
   1) Tại bước 3, nếu email không tồn tại trong Bảng users, hệ thống vẫn hiển thị thông báo "Nếu email tồn tại, link đã được gửi" (để bảo mật, không tiết lộ email có tồn tại hay không). Use case kết thúc.
   2) Tại bước 9, nếu reset token đã hết hạn (quá 1 giờ), hệ thống hiển thị thông báo "Link đã hết hạn, vui lòng gửi lại" và kết thúc use case.
   3) Tại bước 8, nếu mật khẩu mới và xác nhận không khớp, hệ thống hiển thị thông báo lỗi và yêu cầu nhập lại.

4. **Các yêu cầu đặc biệt:**
   - Reset token phải có thời hạn 1 giờ và chỉ sử dụng được 1 lần.
   - Hệ thống luôn trả phản hồi giống nhau cho dù email có tồn tại hay không (bảo mật).
5. **Tiền điều kiện:** Người dùng đã có tài khoản với email hợp lệ trên hệ thống.
6. **Hậu điều kiện:** Mật khẩu được đặt lại thành công, tất cả phiên đăng nhập cũ bị xóa, người dùng cần đăng nhập lại.
7. **Điểm mở rộng:** Không có.

---

### 2.2.4.8. Mô tả Use Case "Cập nhật hồ sơ cá nhân"

1. **Tên Use Case:** Cập nhật thông tin cá nhân.
2. **Mô tả vắn tắt:**
   Use case này cho phép người dùng (Client, Photographer, Admin) chỉnh sửa các thông tin hồ sơ cá nhân của mình trên hệ thống InstaGallery.
3. **Luồng các sự kiện**

   3.1. Luồng cơ bản
   1) Use case này bắt đầu khi người dùng kích chọn tab "Profile" trên thanh Bottom Navigation. Hệ thống hiển thị trang hồ sơ cá nhân với thông tin lấy từ Bảng users.
   2) Người dùng kích chọn nút "Chỉnh sửa hồ sơ". Hệ thống gọi API GET /users/me lấy thông tin hiện tại từ Bảng users và hiển thị form chỉnh sửa gồm: Avatar, Tên hiển thị, Username, Bio, Email, Website.
   3) Người dùng chỉnh sửa các thông tin cần thay đổi trên form.
   4) (Tùy chọn) Nếu người dùng muốn đổi avatar, người dùng nhấn vào ảnh avatar hiện tại, hệ thống mở Gallery cho phép chọn ảnh mới. Ảnh được upload qua API/avatar flow của backend hoặc presigned URL, sau đó URL ảnh mới được cập nhật vào Bảng users.
   5) Người dùng nhấn nút "Lưu".
   6) Hệ thống kiểm tra tính hợp lệ của dữ liệu (username không trùng, bio không quá dài), cập nhật thông tin vào Bảng users.
   7) Hệ thống hiển thị thông báo "Cập nhật thành công" và cập nhật giao diện với thông tin mới. Use case kết thúc.

   3.2. Các luồng rẽ nhánh
   1) Tại bước 6, nếu username mới đã tồn tại trong Bảng users, hệ thống hiển thị thông báo "Username đã tồn tại" và yêu cầu nhập username khác.
   2) Tại bước 4, nếu upload avatar thất bại do lỗi mạng, hệ thống hiển thị "Lỗi upload ảnh, vui lòng thử lại" và giữ avatar cũ.
   3) Tại bất kỳ bước nào, nếu xảy ra lỗi server, hệ thống hiển thị thông báo lỗi và kết thúc use case.

4. **Các yêu cầu đặc biệt:** Không có.
5. **Tiền điều kiện:** Người dùng đã đăng nhập vào hệ thống.
6. **Hậu điều kiện:** Thông tin cá nhân của người dùng được cập nhật thành công trong Bảng users.
7. **Điểm mở rộng:** Không có.

---

## --- Nhóm: Ảnh & Tương tác ---

### 2.2.4.13. Mô tả Use Case "Duyệt Feed ảnh"

1. **Tên Use Case:** Duyệt Feed ảnh.
2. **Mô tả vắn tắt:**
   Use case này cho phép người dùng (Client, Photographer) xem danh sách bài đăng từ những người mà mình theo dõi trên hệ thống.
3. **Luồng các sự kiện**

   3.1. Luồng cơ bản
   1) Use case này bắt đầu khi người dùng mở ứng dụng hoặc kích chọn tab "Home" trên thanh Bottom Navigation.
   2) Hệ thống gọi API `GET /api/v1/posts/feed` với tham số `page` và `limit`, truy vấn Bảng posts JOIN Bảng followers để lấy bài đăng từ những người mà người dùng đang follow, sắp xếp theo thời gian mới nhất. Hệ thống kiểm tra Redis Cache trước, nếu cache miss thì truy vấn database.
   3) Hệ thống hiển thị danh sách bài đăng, mỗi bài gồm: avatar tác giả, username, ảnh/carousel, caption, nút like/comment/share/save, số lượt like, thời gian đăng.
   4) Người dùng cuộn xuống để xem thêm bài. Khi gần cuối danh sách, hệ thống tự động gọi API tải thêm bài (load more) với `page` tiếp theo.
   5) Người dùng kéo xuống từ đầu trang (pull-to-refresh), hệ thống invalidate cache và tải lại feed mới nhất. Use case kết thúc.

   3.2. Các luồng rẽ nhánh
   1) Tại bước 2, nếu người dùng chưa follow ai, hệ thống hiển thị màn hình Feed trống (Empty State) với gợi ý "Khám phá người dùng để theo dõi" kèm nút chuyển sang trang Explore.
   2) Tại bước 2, nếu lỗi kết nối mạng, hệ thống hiển thị thông báo "Không có kết nối mạng" và cho phép thử lại.

4. **Các yêu cầu đặc biệt:**
   - Feed phải sử dụng pagination theo backend contract hiện tại: `page` và `limit`.
   - Hệ thống sử dụng Redis cache với TTL 5 phút cho feed data.
5. **Tiền điều kiện:** Người dùng đã đăng nhập vào hệ thống.
6. **Hậu điều kiện:** Danh sách bài đăng mới nhất từ những người đang theo dõi được hiển thị trên màn hình.
7. **Điểm mở rộng:** Không có.

---

### 2.2.4.14. Mô tả Use Case "Khám phá (Explore)"

1. **Tên Use Case:** Khám phá (Explore).
2. **Mô tả vắn tắt:**
   Use case này cho phép người dùng (Client, Photographer) duyệt ảnh trending và khám phá nội dung mới từ toàn hệ thống, kể cả từ những người mình chưa theo dõi.
3. **Luồng các sự kiện**

   3.1. Luồng cơ bản
   1) Use case này bắt đầu khi người dùng kích chọn tab "🔍 Explore" trên thanh Bottom Navigation.
   2) Hệ thống gọi API GET /explore truy vấn Bảng posts JOIN Bảng users JOIN Bảng post_media, sắp xếp theo thuật toán trending (like_count × recency_weight). Hệ thống kiểm tra Redis Cache trước (TTL 10 phút), nếu cache miss thì truy vấn database.
   3) Hệ thống gọi API GET /search/trending truy vấn Bảng media_tags lấy danh sách trending tags (sắp xếp theo usage_count DESC, lấy top 10).
   4) Hệ thống hiển thị: thanh Search ở trên cùng, danh sách trending tags (horizontal chips), grid ảnh 3 cột (staggered layout).
   5) Người dùng nhấn vào một chip tag → hệ thống lọc bài đăng theo tag đó.
   6) Người dùng cuộn xuống → hệ thống tải thêm bài bằng `page`/`limit`.
   7) Người dùng nhấn vào một bức ảnh → hệ thống chuyển đến PostDetailScreen. Use case kết thúc.

   3.2. Các luồng rẽ nhánh
   1) Tại bước 2, nếu chưa có bài đăng nào trên hệ thống, hệ thống hiển thị Empty State "Chưa có nội dung" với gợi ý follow người dùng.
   2) Tại bước 1, nếu người dùng nhấn vào thanh Search → hệ thống chuyển sang SearchScreen (UC Tìm kiếm ảnh / Thợ ảnh).
   3) Tại bước 2, nếu lỗi kết nối mạng, hệ thống hiển thị "Không có kết nối mạng" và cho phép thử lại.

4. **Các yêu cầu đặc biệt:**
   - Explore sử dụng Redis cache với TTL 10 phút cho dữ liệu trending.
   - Thuật toán trending cân bằng giữa số lượt like và độ mới của bài.
5. **Tiền điều kiện:** Người dùng đã đăng nhập vào hệ thống.
6. **Hậu điều kiện:** Danh sách bài đăng trending và trending tags được hiển thị trên màn hình.
7. **Điểm mở rộng:** Không có.

---

### 2.2.4.15. Mô tả Use Case "Xem chi tiết bài đăng"

1. **Tên Use Case:** Xem chi tiết ảnh.
2. **Mô tả vắn tắt:**
   Use case này cho phép người dùng (Client, Photographer) xem đầy đủ thông tin của một bài đăng bao gồm ảnh, caption và danh sách bình luận.
3. **Luồng các sự kiện**

   3.1. Luồng cơ bản
   1) Use case này bắt đầu khi người dùng kích chọn một bài đăng từ Feed, Explore hoặc Profile.
   2) Hệ thống gọi API GET /posts/{postId}, truy vấn Bảng posts JOIN Bảng post_media JOIN Bảng users lấy chi tiết bài đăng, đồng thời kiểm tra trạng thái is_liked và is_saved của người dùng hiện tại.
   3) Hệ thống hiển thị: carousel ảnh (vuốt qua lại), avatar và username tác giả (nhấn vào xem profile), caption, vị trí, thời gian đăng.
   4) Hệ thống gọi API GET /posts/{postId}/comments truy vấn Bảng comments lấy danh sách bình luận (hỗ trợ threaded comments, hiển thị 2 cấp mặc định).
   5) Hệ thống hiển thị danh sách comments với: avatar, username, nội dung, thời gian, nút trả lời, nút like comment.
   6) Người dùng có thể thực hiện: like bài, viết comment, save bài, nhấn avatar để xem profile tác giả. Use case kết thúc.

   3.2. Các luồng rẽ nhánh
   1) Tại bước 2, nếu bài đăng không tồn tại hoặc đã bị xóa (deleted_at IS NOT NULL), hệ thống hiển thị "Bài đăng không tồn tại hoặc đã bị xóa" và quay lại trang trước.
   2) Tại bước 2, nếu bài đăng có visibility = Private và người dùng không phải chủ bài, hệ thống hiển thị "Bạn không có quyền xem bài đăng này".

4. **Các yêu cầu đặc biệt:** Không có.
5. **Tiền điều kiện:** Người dùng đã đăng nhập vào hệ thống.
6. **Hậu điều kiện:** Thông tin chi tiết bài đăng và danh sách comments được hiển thị đầy đủ.
7. **Điểm mở rộng:** Không có.

---

### 2.2.4.10. Mô tả Use Case "Upload ảnh / Đăng bài"

1. **Tên Use Case:** Upload ảnh (Đăng bài).
2. **Mô tả vắn tắt:**
   Use case này cho phép người dùng (Client, Photographer) tạo bài đăng mới với ảnh/video kèm caption, vị trí và chế độ hiển thị.
3. **Luồng các sự kiện**

   3.1. Luồng cơ bản
   1) Use case này bắt đầu khi người dùng kích chọn nút "+" trên thanh Bottom Navigation.
   2) Hệ thống mở Gallery trên thiết bị, cho phép chọn nhiều ảnh/video (multi-select, tối đa 10 file).
   3) Người dùng chọn ảnh/video xong, ứng dụng tự động nén ảnh (resize + compress quality 80%) để tiết kiệm bandwidth. Hệ thống hiển thị preview các ảnh đã chọn, cho phép sắp xếp lại thứ tự bằng kéo thả.
   4) (Tùy chọn) Người dùng chọn filter để áp dụng cho ảnh.
   5) Người dùng viết caption, (tùy chọn) thêm vị trí và chọn chế độ hiển thị (Public / Private / Friends Only).
   6) Người dùng nhấn nút "Đăng bài".
   7) Hệ thống gọi API `POST /api/v1/media/presigned-url` cho từng ảnh để lấy URL upload, sau đó upload ảnh lên object storage qua URL đó.
   8) Sau khi upload ảnh xong, hệ thống gọi API POST /posts để tạo bài đăng. Server thực hiện: INSERT vào Bảng posts, INSERT vào Bảng post_media cho mỗi ảnh, UPDATE post_count trong Bảng users.
   9) Hệ thống gửi notification cho các followers của người đăng bài.
   10) Hệ thống chuyển về màn hình Feed và hiển thị bài đăng mới ở đầu danh sách. Use case kết thúc.

   3.2. Các luồng rẽ nhánh
   1) Tại bước 2, nếu người dùng chọn quá 10 ảnh, hệ thống hiển thị thông báo "Tối đa 10 ảnh cho mỗi bài đăng" và không cho chọn thêm.
   2) Tại bước 7, nếu lỗi mạng khi upload ảnh, hệ thống hiển thị "Lỗi upload, vui lòng thử lại" và cho phép thử lại.
   3) Tại bước 6, nếu người dùng chưa viết caption và chưa chọn ảnh nào, nút "Đăng bài" bị vô hiệu hóa.

4. **Các yêu cầu đặc biệt:**
   - Mỗi ảnh tối đa 10MB.
   - Upload sử dụng presigned URL để tránh gửi file qua API server (tối ưu hiệu suất).
5. **Tiền điều kiện:** Người dùng đã đăng nhập vào hệ thống.
6. **Hậu điều kiện:** Bài đăng mới được tạo trong Bảng posts và Bảng post_media, post_count của người dùng tăng lên 1, followers nhận được thông báo.
7. **Điểm mở rộng:** Không có.
