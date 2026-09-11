# InstaGallery — Danh Sách API Backend Chi Tiết

Tài liệu này bao gồm toàn bộ các route HTTP và WebSocket hiện có trong codebase Ktor của InstaGallery Backend.

## 1. Thông Số Tổng Quan (Metrics)

- **Tổng số bảng cơ sở dữ liệu (MySQL Table count):** **33**
- **Tổng số REST endpoints (`/api/v1`):** **139**
- **Tổng số system / debug HTTP routes:** **5**
- **WebSocket endpoints:** **1**
- **Tổng cộng (Total endpoints):** **145**

## 2. Chi Tiết Từng Route Cụ Thể

### 2.1. Cụm Xác Thực & Đăng Nhập (Auth)
Tất cả các route này bắt đầu bằng `/api/v1/auth`.

| Phương thức | Endpoint | Access | Mô tả |
| :--- | :--- | :---: | :--- |
| POST | `/register` | Public | Đăng ký tài khoản người dùng mới (Client/Photographer) |
| POST | `/login` | Public | Đăng nhập và nhận cặp JWT token (Access & Refresh) |
| POST | `/refresh` | Public | Đổi Refresh Token lấy Access Token mới |
| POST | `/forgot-password` | Public | Yêu Cầu mã OTP qua email để đặt lại mật khẩu |
| POST | `/reset-password` | Public | Xác nhận mật khẩu mới bằng OTP |
| POST | `/google` | Public | Đăng nhập/Đăng ký nhanh bằng mạng xã hội Google |
| POST | `/2fa/verify-login`| Public | Xác thực mã đăng nhập khi tài khoản bật 2FA |
| POST | `/logout` | Authenticated | Đăng xuất, vô hiệu hóa token hiện tại |
| PUT | `/change-password` | Authenticated | Đổi mật khẩu tài khoản trực tiếp |
| POST | `/2fa/setup` | Authenticated | Khởi tạo thông tin thiết lập bảo mật 2FA (QR/Secret) |
| POST | `/2fa/enable` | Authenticated | Kích hoạt chức năng đăng nhập 2 lớp 2FA |

### 2.2. Hồ Sơ Người Dùng (Users & Social)
Tất cả các route này bắt đầu bằng `/api/v1/users` (hoặc `/api/v1/users/me`).

| Phương thức | Endpoint | Access | Mô tả |
| :--- | :--- | :---: | :--- |
| GET | `/me` | Authenticated | Lấy chi tiết thông tin cá nhân hiện dùng |
| PUT | `/me` | Authenticated | Cập nhật thông tin hồ sơ (full name, phone, bio...) |
| GET | `/me/sessions` | Authenticated | Xem danh sách các phiên đăng nhập đang hoạt động |
| DELETE | `/me/sessions/{id}`| Authenticated | Đóng một phiên làm việc cụ thể từ xa |
| POST | `/me/deactivate` | Authenticated | Khóa tài khoản tạm thời (Vô hiệu hóa) |
| PUT | `/me/avatar` | Authenticated | Lưu thông tin cập nhật ảnh đại diện mới |
| PUT | `/me/privacy` | Authenticated | Thay đổi quyền riêng tư tài khoản (Private/Public) |
| GET | `/me/saved-posts` | Authenticated | Xem danh sách lưu trữ bài viết của cá nhân |
| GET | `/me/liked-posts` | Authenticated | Xem danh sách các bài viết cá nhân đã thích |
| GET | `/me/tagged-posts` | Authenticated | Xem các bài đăng được tag tên bản thân |
| GET | `/me/comments` | Authenticated | Xem lịch sử các bình luận đã thực hiện |
| GET | `/me/activity-log` | Authenticated | Truy xuất lịch sử hoạt động audit của tài khoản |
| GET | `/me/blocked` | Authenticated | Xem danh sách người dùng đang chặn |
| GET | `/suggestions` | Authenticated | Lấy danh sách gợi ý người dùng để theo dõi |
| GET | `/me/follow-requests`| Authenticated | Danh sách các yêu cầu theo dõi đang chờ duyệt |
| POST| `/me/follow-requests/{followerId}/{action}`| Authenticated | Chấp nhận (accept) hoặc Từ chối (reject) yêu cầu theo dõi |
| GET | `/{id}` | Public | Xem thông tin hồ sơ chi tiết của người dùng khác |
| POST | `/{id}/follow` | Authenticated | Theo dõi hoặc Hủy theo dõi người dùng cụ thể |
| POST | `/{id}/block` | Authenticated | Chặn hoặc Bỏ chặn người dùng cụ thể |
| POST | `/{id}/mute` | Authenticated | Tắt tiếng hoặc Bật tiếng nội dung của người dùng |
| GET | `/{id}/followers` | Public | Xem danh sách người theo dõi của người dùng cụ thể |
| GET | `/{id}/following` | Public | Xem danh sách người đang được người dùng cụ thể theo dõi |

### 2.3. Bài đăng & Tương Tác (Posts & Comments)
Tất cả các route này bắt đầu bằng `/api/v1/posts` hoặc các tiền tố `/api/v1/comments` tương ứng.

| Phương thức | Endpoint | Access | Mô tả |
| :--- | :--- | :---: | :--- |
| POST | `/posts` | Authenticated | Tạo một bài viết cá nhân mới (POST) |
| GET | `/posts/feed` | Authenticated | Tải bảng tin trang chủ (News Feed) cá nhân |
| GET | `/posts/{id}` | Authenticated | Xem chi tiết thông tin một bài đăng |
| PUT | `/posts/{id}` | Authenticated | Chỉnh sửa mô tả bài đăng |
| DELETE | `/posts/{id}` | Authenticated | Xóa bài viết cá nhân (Soft Delete) |
| GET | `/posts/users/{userId}/posts`| Authenticated | Tải danh sách bài đăng của một người dùng cụ thể |
| POST | `/posts/{id}/tags` | Authenticated | Gắn thẻ người dùng cụ thể vào bài đăng |
| DELETE | `/posts/{id}/tags/{taggedUserId}`| Authenticated | Gỡ bỏ thẻ tag người dùng khỏi bài đăng |
| PUT| `/posts/{id}/comment-settings`| Authenticated | Bật/tắt hoặc điều chỉnh quyền bình luận bài viết |
| POST | `/posts/{id}/like` | Authenticated | Nhấn thích hoặc Bỏ thích bài đăng |
| GET | `/posts/{id}/likes` | Authenticated | Xem danh sách những người dùng đã thích bài đăng |
| POST | `/posts/{id}/save` | Authenticated | Lưu bài đăng vào bộ sưu tập cá nhân hoặc gỡ lưu |
| POST| `/posts/{id}/comments`| Authenticated | Đăng bình luận mới dưới bài viết (hỗ trợ comment cha-con) |
| GET| `/posts/{id}/comments`| Authenticated | Lấy danh sách bình luận dưới dạng cây phân cấp |
| POST | `/posts/{id}/share` | Authenticated | Chia sẻ bài viết |
| GET | `/posts/{id}/shares` | Authenticated | Xem danh sách hoặc số lượng lượt chia sẻ |
| PUT| `/comments/{commentId}`| Authenticated | Cập nhật nội dung bình luận cá nhân |
| DELETE| `/comments/{commentId}`| Authenticated | Xóa bình luận cá nhân |
| POST| `/comments/{commentId}/like`| Authenticated | Nhấn thích bình luận |
| POST| `/comments/{commentId}/dislike`| Authenticated | Nhấn không thích bình luận (dislike) |

### 2.4. Lưu Trữ Media & Album (Media & Albums)
Cấu trúc quản lý định dạng file tải lên và quản trị Album cá nhân.

| Phương thức | Endpoint | Access | Mô tả |
| :--- | :--- | :---: | :--- |
| PUT| `/media/local-upload/{folder}/{fileName}`| Public | Hỗ trợ đăng tải trực tiếp file lên máy chủ cục bộ |
| GET| `/media/local-files/{folder}/{fileName}`| Public | Truy xuất hình ảnh tĩnh từ máy chủ local |
| POST | `/media/presigned-url` | Authenticated | Lấy đường dẫn tải ảnh lên S3/Firebase bảo mật |
| POST | `/posts/{postId}/media`| Authenticated | Bổ sung hình ảnh hoặc video vào bài đăng có sẵn |
| DELETE | `/posts/media/{mediaId}`| Authenticated | Gỡ hình ảnh ra khỏi bài đăng |
| PUT| `/posts/{postId}/media/reorder`| Authenticated | Sắp xếp lại thứ tự ảnh hiển thị (Carousel) |
| POST | `/albums` | Authenticated | Tạo nhóm Album tuyển tập ảnh cá nhân mới |
| GET | `/albums` | Authenticated | Xem danh sách các album của tài khoản hiện tại |
| GET | `/albums/{id}` | Authenticated | Chi tiết dữ liệu và danh sách ảnh thuộc Album |
| PUT | `/albums/{id}` | Authenticated | Chỉnh sửa tên hoặc thông tin cơ bản Album |
| DELETE | `/albums/{id}` | Authenticated | Xóa Album cá nhân |
| POST | `/albums/{id}/media` | Authenticated | Thêm hình ảnh mới vào Album |
| DELETE | `/albums/{id}/media/{mediaId}`| Authenticated | Gỡ hình ảnh ra khỏi Album |

### 2.5. Khám Phá & Tìm Kiếm (Explore & Search)
Các tìm kiếm thông minh thông qua thẻ Hashtag hoặc xu hướng hiển thị.

| Phương thức | Endpoint | Access | Mô tả |
| :--- | :--- | :---: | :--- |
| GET | `/explore` | Public | Trang khám phá, gợi ý thông minh dựa trên độ phổ biến |
| GET | `/explore/trending` | Public | Danh sách bài viết đang thịnh hành (nhiều tương tác) |
| GET | `/explore/hashtags/{tag}`| Public | Lọc danh sách bài đăng theo thẻ Hashtag cụ thể |
| GET | `/search` | Optional | Tìm kiếm người dùng, bài đăng, từ khóa tổng hợp |
| GET | `/search/history` | Authenticated | Xem danh sách các từ khóa gợi ý lịch sử truy vấn |
| DELETE | `/search/history` | Authenticated | Xóa lịch sử tìm kiếm cá nhân |
| GET | `/search/trending` | Public | Danh sách chủ đề xu hướng tìm kiếm nhiều nhất |

### 2.6. Nhiếp Ảnh & Đặt Lịch Chụp (Portfolio & Business)
Phục vụ trực tiếp việc kinh doanh, lên lịch chụp hình của photographer.

| Phương thức | Endpoint | Access | Mô tả |
| :--- | :--- | :---: | :--- |
| GET | `/portfolios` | Public | Lấy danh sách hồ sơ các nhiếp ảnh gia |
| GET | `/portfolios/users/{userId}`| Public | Chi tiết hồ sơ tác nghiệp và portfolio của photographer |
| GET| `/portfolios/users/{userId}/availability`| Public | Lịch trình rảnh/bận trong tuần của photographer |
| GET | `/portfolios/me` | Authenticated | Lấy chi tiết portfolio cá nhân (nếu là photographer) |
| PUT | `/portfolios/me` | Authenticated | Cập nhật giá chụp cố định, giới thiệu và chuyên môn |
| POST| `/portfolios/me/availability`| Authenticated | Thiết lập chu kỳ rảnh chụp cá nhân hàng tuần |
| GET| `/portfolios/me/availability`| Authenticated | Lấy thông tin chu kỳ rảnh của bản thân |
| GET| `/users/{photographerId}/services`| Public | Danh sách dịch vụ/gói chụp phục vụ công khai |
| GET | `/photographer/services`| Authenticated | Photographer lấy danh sách các dịch vụ của mình |
| POST | `/photographer/services`| Authenticated | Tạo một gói dịch vụ chụp ảnh mới |
| GET| `/photographer/services/{id}`| Authenticated | Chi tiết thông tin gói dịch vụ chụp ảnh |
| PUT| `/photographer/services/{id}`| Authenticated | Cập nhật gói dịch vụ chụp ảnh |
| PUT| `/photographer/services/{id}/status`| Authenticated | Bật hoặc tắt gói dịch vụ (hoạt động/ngưng nhận khách) |
| DELETE| `/photographer/services/{id}`| Authenticated | Xóa một gói dịch vụ cụ thể |
| POST | `/bookings` | Authenticated | Khách hàng thực hiện đặt lịch chụp mới |
| GET | `/bookings` | Authenticated | Danh sách các đơn đặt lịch hẹn (Khách/Nhiếp ảnh) |
| GET | `/bookings/{id}` | Authenticated | Xem chi tiết thông số và trạng thái đơn đặt lịch chụp |
| PUT | `/bookings/{id}/status`| Authenticated | Thay đổi trạng thái đặt lịch (Hủy, Xác nhận, Hoàn thành) |
| POST | `/bookings/{id}/review`| Authenticated | Đánh giá của khách hàng sau khi hoàn tất buổi chụp hình |
| DELETE | `/bookings/{id}` | Authenticated | Xóa lịch hẹn đã hủy bỏ |
| GET| `/users/{photographerId}/ratings`| Public | Xem danh sách đánh giá của các khách hàng |
| POST| `/users/{photographerId}/ratings`| Authenticated | Đánh giá nhiếp ảnh gia trực tiếp |
| DELETE | `/ratings/{ratingId}` | Authenticated | Hủy bỏ hoặc ẩn đánh giá của bản thân |
| PUT | `/ratings/{ratingId}` | Authenticated | Cập nhật nội dung đánh giá của bản thân |

### 2.7. Giao Tiếp Nhóm & Đẩy Đơn (Chat & Devices)
Tương tác thời gian thực thông qua Websocket và đẩy thông báo.

| Phương thức | Endpoint | Access | Mô tả |
| :--- | :--- | :---: | :--- |
| POST | `/chat/conversations`| Authenticated | Khởi tạo cuộc hội thoại trực tiếp hoặc nhóm chát |
| GET | `/chat/conversations`| Authenticated | Danh sách phòng chát hiển thị tin nhắn mới nhất |
| GET| `/chat/conversations/{id}/messages`| Authenticated | Tải lịch sử tin nhắn trong đoạn hội thoại |
| PUT| `/chat/conversations/{id}/read`| Authenticated | Đánh dấu đã đọc toàn bộ tin nhắn thuộc phòng |
| POST| `/chat/conversations/{id}/messages`| Authenticated | Gửi tin nhắn mới |
| DELETE| `/chat/conversations/{id}`| Authenticated | Xóa cuộc hội thoại cá nhân |
| WS | `/ws/chat` | Authenticated | Giao tiếp Websocket thời gian thực (tin nhắn/trực tuyến) |
| POST | `/devices/fcm-token` | Authenticated | Đăng ký mã thông báo thiết bị Firebase FCM để nhận notification |
| DELETE | `/devices/fcm-token` | Authenticated | Hủy token thiết bị khi đăng xuất |
| GET | `/notifications` | Authenticated | Xem toàn bộ các thông báo nhận được |
| GET | `/notifications/unread-count`| Authenticated | Số lượng thông báo chưa xem trên ứng dụng |
| PUT | `/notifications/{id}/read`| Authenticated | Đánh dấu đã đọc thông báo cụ thể |
| PUT | `/notifications/read-all`| Authenticated | Xem tất cả các thông báo hiện dùng |
| DELETE | `/notifications/{id}`| Authenticated | Xóa hẳn thông báo ra khỏi màn hình |

### 2.8. Hệ Thống & Quản Trị Quản Lý (Admin & Moderation)
Dành riêng cho công cụ quản trị hệ thống của bộ phận Admin.

| Phương thức | Endpoint | Access | Mô tả |
| :--- | :--- | :---: | :--- |
| POST | `/reports` | Authenticated | Gửi yêu cầu báo cáo bài viết/người dùng vi phạm |
| GET | `/admin/reports` | Admin | Quản trị viên lấy danh sách các đơn báo cáo vi phạm |
| PUT | `/admin/reports/{reportId}`| Admin | Xử lý hoặc thay đổi trạng thái các báo cáo vi phạm |
| GET | `/admin/stats` | Admin | Số liệu thống kê tổng các mảng hiển thị dashboard |
| GET | `/admin/stats/growth` | Admin | Thông kê tăng trưởng lượng người dùng, bài viết theo ngày |
| PUT | `/admin/users/{userId}/ban`| Admin | Vô hiệu hóa hoặc khóa tài khoản vi phạm vĩnh viễn |
| DELETE | `/admin/posts/{postId}`| Admin | Xóa bỏ cưỡng chế bài đăng vi phạm khỏi hệ thống |
| DELETE| `/admin/comments/{commentId}`| Admin | Xóa bình luận vi phạm khỏi hệ thống |
| GET | `/admin/users` | Admin | Danh sách toàn bộ tài khoản người dùng trên trang admin |
| GET | `/admin/users/{userId}`| Admin | Xem chi tiết thông số tài khoản và lịch sử của user |
| PUT| `/admin/users/{userId}/verification`| Admin | Cấp hoặc hủy tích xanh định danh của người dùng |
| PUT| `/admin/users/{userId}/featured`| Admin | Quyết định tuyển chọn photographer lên trang nổi bật |
| GET | `/admin/posts` | Admin | Tra cứu toàn bộ bài viết trong cơ sở dữ liệu |
| GET | `/admin/posts/{postId}`| Admin | Thông tin chi tiết, liên kết của bài viết |
| PUT| `/admin/posts/{postId}/status`| Admin | Khóa hoặc chuyển trạng thái hiển thị của bài đăng |
| GET | `/admin/bookings` | Admin | Tra cứu danh sách các đơn đặt lịch trong toàn hệ thống |
| GET| `/admin/bookings/{bookingId}`| Admin | Lấy thông số đơn đặt lịch chụp hình chi tiết |
| PUT| `/admin/bookings/{bookingId}/status`| Admin | Xử lý hoặc sửa đổi giao dịch đặt lịch chụp hình |
| GET | `/admin/ratings` | Admin | Xem toàn bộ đánh giá xuất hiện trên ứng dụng |
| PUT| `/admin/ratings/{ratingId}/status`| Admin | Ẩn tạm thời hoặc khôi phục hiển thị đánh giá |
| DELETE| `/admin/ratings/{ratingId}`| Admin | Xóa hẳn đánh giá khỏi hệ thống |
| GET | `/admin/media` | Admin | Quản trị danh sách file hình ảnh/video hệ thống |
| DELETE| `/admin/media/{mediaId}`| Admin | Xóa tệp khỏi máy chủ |
| GET | `/admin/notifications`| Admin | Quản lý thông báo đã phát từ quản trị |
| POST | `/admin/notifications`| Admin | Định dạng nội dung phát đi thông báo chung hệ thống |
| GET | `/admin/banned-keywords`| Admin | Lọc danh sách từ khóa cấm đã lập |
| POST | `/admin/banned-keywords`| Admin | Thiết lập từ khóa cấm hoặc regex mới |
| DELETE| `/admin/banned-keywords/{id}`| Admin | Xóa từ khóa cấm khỏi danh sách |
| GET | `/admin/activity-logs`| Admin | Nhật ký hoạt động chung của hệ thống |

---

## 3. Các Endpoint Hệ Thống (System Endpoints)

Đây là các endpoint đặc biệt hỗ trợ phát triển, cài đặt không cấu hình tiền tố nhóm `/api/v1` và tuyệt đối không tích hợp trực tiếp lên phiên bản ứng dụng chính thức:

- `GET /health`: Kiểm tra sức khỏe hệ thống (Trả về trạng thái hoạt động của Server/DB).
- `GET /init-db`: Khởi tạo cấu trúc các bảng Exposed Tables mới.
- `GET /reset-db`: Hoàn trả và làm sạch toàn bộ dữ liệu cấu trúc bảng CSDL.
- `GET /migrate-db`: Nâng cấp hoặc áp dụng các file SQL Schema mới vào DB.
- `GET /fix-user-id`: Một số route debug tối ưu hóa đồng dạng định danh ID người dùng.
