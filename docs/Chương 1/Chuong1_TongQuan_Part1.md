# CHƯƠNG 1: TỔNG QUAN VỀ CÔNG NGHỆ VÀ CƠ SỞ LÝ THUYẾT

---

## 1.1. Tổng quan đề tài

### 1.1.1. Đặt vấn đề

Trong thời đại số hóa hiện nay, việc chụp và chia sẻ ảnh đã trở thành một phần không thể thiếu trong cuộc sống hàng ngày. Sự phát triển mạnh mẽ của điện thoại thông minh với camera chất lượng cao đã biến nhiếp ảnh từ một lĩnh vực chuyên nghiệp thành một hoạt động phổ biến của mọi người. Theo thống kê, mỗi ngày có hơn **100 triệu bức ảnh** được đăng tải lên Instagram và hàng tỷ bức ảnh được chia sẻ trên các nền tảng mạng xã hội khác nhau.

Tuy nhiên, các nền tảng mạng xã hội phổ biến hiện nay như Facebook, Instagram hay TikTok đều được thiết kế phục vụ mục đích **đa chức năng, đại chúng**, dẫn đến một số hạn chế nghiêm trọng đối với cộng đồng nhiếp ảnh chuyên nghiệp và nghiệp dư:

**Các vấn đề cụ thể bao gồm:**

1. **Chất lượng ảnh bị giảm nghiêm trọng khi đăng tải:** Các nền tảng phổ biến thường nén ảnh để tiết kiệm băng thông và dung lượng lưu trữ. Instagram nén ảnh xuống còn khoảng 1080px chiều rộng, Facebook giảm chất lượng JPEG xuống mức thấp — khiến các tác phẩm nhiếp ảnh mất đi độ sắc nét và chi tiết vốn có.

2. **Thiếu kênh kết nối chuyên nghiệp giữa thợ ảnh và khách hàng:** Khách hàng có nhu cầu chụp ảnh (cưới, sự kiện, chân dung) gặp khó khăn trong việc tìm kiếm thợ ảnh phù hợp dựa trên phong cách, khu vực hoạt động và đánh giá thực tế. Hiện tại, việc kết nối chủ yếu thông qua các nhóm Facebook hoặc giới thiệu truyền miệng — thiếu tính hệ thống và minh bạch.

3. **Thiếu tính năng chuyên biệt cho nhiếp ảnh gia:** Các nền tảng hiện tại không cung cấp công cụ quản lý portfolio chuyên nghiệp, hệ thống đặt lịch chụp, hay cơ chế đánh giá dịch vụ — những tính năng thiết yếu cho hoạt động kinh doanh của nhiếp ảnh gia.

4. **Nguy cơ vi phạm bản quyền ảnh:** Ảnh được chia sẻ trên mạng xã hội rất dễ bị sao chép, tải về và sử dụng trái phép mà không có cơ chế bảo vệ hiệu quả.

Do đó, việc xây dựng hệ thống **InstaGallery — Ứng dụng Giao lưu Ảnh Android** là cần thiết để tạo ra một **môi trường chuyên biệt, chất lượng cao**, giúp kết nối cộng đồng nhiếp ảnh và khách hàng một cách hiệu quả.

**Bảng 1.1 — Ưu điểm của hệ thống InstaGallery:**

| Ưu điểm | Mô tả |
|----------|-------|
| **Chất lượng ảnh cao** | Nền tảng tập trung hiển thị ảnh chất lượng cao, áp dụng thuật toán nén thông minh để giữ nguyên độ sắc nét tối đa |
| **Bảo mật đa tầng** | Hệ thống bảo mật 7 lớp (Defense in Depth): JWT Authentication, HTTPS, Signed URLs, Rate Limiting, Input Validation, TokenStore/SecureTokenStore, RBAC |
| **Cộng đồng chuyên biệt** | Không gian giao lưu dành riêng cho người có cùng đam mê nhiếp ảnh, tạo môi trường tương tác chất lượng |
| **Kết nối trực tiếp** | Khách hàng dễ dàng tìm kiếm, đánh giá và đặt lịch chụp với thợ ảnh phù hợp thông qua hệ thống Portfolio và Booking tích hợp |
| **Chat Realtime** | Hỗ trợ nhắn tin trực tiếp qua WebSocket, giúp thợ ảnh và khách hàng trao đổi yêu cầu nhanh chóng |

**Bảng 1.2 — Thách thức và giải pháp đề xuất:**

| Thách thức | Giải pháp đề xuất |
|-----------|-------------------|
| Cạnh tranh với các mạng xã hội lớn (Instagram, Facebook) | Tập trung vào **thị trường ngách** — cộng đồng nhiếp ảnh chuyên biệt với tính năng Booking + Portfolio mà các MXH lớn không có |
| Bảo mật dữ liệu và bản quyền ảnh phức tạp | Áp dụng **bảo mật đa tầng** (Defense in Depth): Signed URLs cho media, JWT với rotation, bcrypt cho mật khẩu, TokenStore trên thiết bị và SecureTokenStore cho production |
| Quản lý và kiểm duyệt nội dung | Cơ chế kiểm duyệt **kết hợp người dùng báo cáo + Admin xử lý** qua Dashboard quản trị với activity logging |
| Đảm bảo hiệu năng khi lượng dữ liệu lớn | Sử dụng **Redis Cache** cho feed, pagination theo API backend (`page`, `limit`), và **Object Storage/CDN** qua presigned URL cho phân phối ảnh |

---

### 1.1.2. Mục tiêu đề tài

Đề tài hướng đến xây dựng một **hệ thống chia sẻ ảnh chuyên biệt dành cho cộng đồng nhiếp ảnh** trên nền tảng Android, với các mục tiêu cụ thể:

**a) Mục tiêu chính:**

1. **Xây dựng ứng dụng Android** cho phép người dùng đăng tải, chia sẻ và tương tác với các bộ sưu tập ảnh chất lượng cao.
2. **Phát triển hệ thống Backend** xử lý nghiệp vụ, quản lý dữ liệu và đảm bảo bảo mật thông tin người dùng.
3. **Tạo hệ thống kết nối Thợ ảnh — Khách hàng** thông qua tính năng Portfolio (hồ sơ năng lực) và Booking (đặt lịch chụp).

**b) Mục tiêu kỹ thuật:**

| # | Mục tiêu | Chỉ số đo lường |
|---|---------|-----------------|
| 1 | Kiến trúc ứng dụng Android theo chuẩn MVVM + Clean Architecture | Phân tách rõ 3 lớp: Data, Domain, Presentation |
| 2 | Backend RESTful API + WebSocket cho chat realtime | Thời gian phản hồi API < 500ms |
| 3 | Bảo mật đa tầng | JWT Access (15p) + Refresh (30d), bcrypt(12), Rate Limiting |
| 4 | Cơ sở dữ liệu quan hệ với đầy đủ ràng buộc | 30 bảng, Exposed ORM, counter cache, soft delete |
| 5 | Giao diện người dùng hiện đại, mượt mà | XML Layout, AppCompat, Material Components, Dark Mode, Optimistic UI |

**c) Các chức năng chính mà hệ thống cung cấp:**

- **Chia sẻ ảnh:** Đăng tải ảnh/carousel với caption, vị trí, chế độ hiển thị (Public/Private/Friends Only).
- **Tương tác xã hội:** Like, Comment (threaded), Follow/Unfollow, Save bài đăng.
- **Tìm kiếm & Khám phá:** Full-text search người dùng, bài đăng, hashtag với autocomplete.
- **Portfolio & Booking:** Nhiếp ảnh gia tạo hồ sơ năng lực, khách hàng đặt lịch chụp và đánh giá.
- **Chat Realtime:** Nhắn tin trực tiếp qua WebSocket.
- **Thông báo:** Thông báo trong app cho like, comment, follow, booking; push notification để sau MVP.
- **Quản trị:** Dashboard cho Admin quản lý người dùng, kiểm duyệt nội dung, xem thống kê.

---

### 1.1.3. Phạm vi đề tài (Scope)

Đề tài tập trung xây dựng hệ thống chia sẻ ảnh **InstaGallery** với phạm vi được xác định rõ ràng như sau:

**a) Phạm vi hệ thống:**

| Thành phần | Mô tả | Công nghệ |
|-----------|-------|-----------|
| **Ứng dụng di động** | Android native, hỗ trợ Android 8.0 (API 26) trở lên | Kotlin, XML + AppCompat + Material + ConstraintLayout, MVVM, lightweight Clean Architecture, Koin DI |
| **Backend Server** | RESTful API + WebSocket server | Ktor (Kotlin), JWT Authentication |
| **Cơ sở dữ liệu** | Cơ sở dữ liệu quan hệ | MySQL 8.x (InnoDB, utf8mb4) |
| **Cache** | In-memory cache cho session, rate limiting, feed | Redis |
| **Lưu trữ media** | Object storage hoặc local/static storage cho ảnh/video | Tạm thời không dùng Firebase; Android upload qua backend presigned URL, provider storage quyết định sau MVP |

**b) Phạm vi chức năng — 3 nhóm tác nhân:**

| Tác nhân | Số lượng Use Case | Các chức năng đại diện |
|----------|-------------------|----------------------|
| **Khách hàng (Client)** | 21 UC | Đăng ký, Feed, Upload ảnh, Like/Comment, Đặt lịch chụp, Chat |
| **Nhiếp ảnh gia (Photographer)** | 22 UC | Tất cả chức năng Client + Tạo Portfolio, Quản lý Booking |
| **Quản trị viên (Admin)** | 4 UC | Quản lý người dùng, Kiểm duyệt nội dung, Thống kê |

**c) Phạm vi ngoài đề tài (Out of Scope):**

- Ứng dụng iOS (chỉ phát triển Android).
- Tích hợp thanh toán trực tuyến (VNPay, Momo) — để dành cho hướng phát triển tương lai.
- Hệ thống gợi ý bằng AI/Machine Learning.
- Tính năng Stories/Reels dạng video ngắn.

---

### 1.1.4. Khảo sát các sản phẩm tương tự

Trước khi thiết kế hệ thống, nhóm tác giả đã khảo sát và phân tích các nền tảng chia sẻ ảnh phổ biến hiện nay để rút ra bài học kinh nghiệm và xác định khoảng trống thị trường.

#### a) Instagram

- **Điểm mạnh:** Giao diện trực quan, dễ sử dụng; cộng đồng người dùng hơn 2 tỷ tài khoản; hệ sinh thái quảng cáo mạnh mẽ; tích hợp Stories, Reels, và Shopping.
- **Hạn chế:** Nén ảnh rất nặng (giảm xuống 1080px, JPEG chất lượng thấp); không có tính năng đặt lịch chụp hay quản lý portfolio cho nhiếp ảnh gia; thuật toán ưu tiên nội dung viral thay vì chất lượng nghệ thuật.

#### b) 500px

- **Điểm mạnh:** Chất lượng ảnh cao, cộng đồng nhiếp ảnh chuyên nghiệp; hệ thống Licensing cho phép bán ảnh; tính năng Discover/Explore tối ưu cho ảnh nghệ thuật.
- **Hạn chế:** Không có tính năng đặt lịch chụp (booking); giao diện mobile chưa tối ưu bằng phiên bản web; tính năng nhắn tin hạn chế.

#### c) VSCO

- **Điểm mạnh:** Bộ lọc màu (preset) chất lượng cao, công cụ chỉnh sửa ảnh chuyên nghiệp; cộng đồng sáng tạo với không gian chia sẻ cảm hứng.
- **Hạn chế:** Không có các tính năng thương mại (đặt lịch, portfolio); mô hình trả phí (VSCO Membership) cho các preset cao cấp; tính năng tương tác xã hội hạn chế (không có like, comment).

#### d) Flickr

- **Điểm mạnh:** Nền tảng lâu đời (từ 2004), cho phép lưu trữ ảnh chất lượng gốc với dung lượng lớn (1TB miễn phí); cộng đồng nhiếp ảnh lớn; hỗ trợ metadata EXIF chi tiết.
- **Hạn chế:** Giao diện phức tạp, chưa tối ưu cho di động; chưa có tính năng booking/portfolio; tốc độ phát triển chậm so với các nền tảng mới.

**Bảng 1.3 — So sánh tổng hợp các nền tảng:**

| Tiêu chí | Instagram | 500px | VSCO | Flickr | **InstaGallery** |
|----------|-----------|-------|------|--------|:----------------:|
| Chất lượng ảnh | ⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | **⭐⭐⭐⭐** |
| Cộng đồng nhiếp ảnh | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ | **⭐⭐⭐⭐** |
| Đặt lịch chụp (Booking) | ❌ | ❌ | ❌ | ❌ | **✅** |
| Portfolio nhiếp ảnh gia | ❌ | ⭐⭐⭐ | ❌ | ⭐⭐ | **⭐⭐⭐⭐** |
| Đánh giá thợ ảnh | ❌ | ❌ | ❌ | ❌ | **✅** |
| Chat realtime | ✅ | ❌ | ❌ | ❌ | **✅** |
| Ứng dụng Android native | ✅ | ✅ | ✅ | ✅ | **✅** |
| Bảo mật bản quyền | ⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐ | **⭐⭐⭐⭐** |

> **Nhận xét:** Nhìn chung, các nền tảng hiện tại đều có những điểm mạnh riêng nhưng **chưa có ứng dụng nào thực sự kết hợp đầy đủ** các yếu tố: chất lượng ảnh cao, cộng đồng nhiếp ảnh chuyên biệt, tính năng đặt lịch chụp, đánh giá thợ ảnh, và bảo mật bản quyền hiệu quả. **InstaGallery** được thiết kế để lấp đầy khoảng trống này — tạo ra một **hệ sinh thái khép kín** cho cộng đồng nhiếp ảnh từ chia sẻ tác phẩm, kết nối khách hàng, đến quản lý dịch vụ chụp ảnh.
