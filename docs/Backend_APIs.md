# InstaGallery Backend - API Overview

Dự án InstaGallery Backend được viết bằng Kotlin với Framework Ktor. Dưới đây là bài đánh giá (review) tổng quan và danh sách tất cả các API đang có trong hệ thống.

## 1. Đánh giá Backend (Review)
- **Công nghệ cốt lõi**: Kotlin, Ktor (Netty engine).
- **Cơ sở dữ liệu**: MySQL, thư viện ORM là JetBrains Exposed. Các bảng dữ liệu (30 bảng) đã được định nghĩa rất chi tiết bao gồm Users, Posts, Media, Ratings, Bookings, v.v.
- **Bảo mật & Xác thực (Auth)**: Sử dụng JWT (JSON Web Token) chia làm Access Token & Refresh Token. Hỗ trợ xác thực 2 bước (2FA), đăng nhập qua Google OAuth, cơ chế Block/Mute người dùng.
- **Kiến trúc**: Sử dụng DI container là Koin để tiêm các Service (`UserService`, `AuthService`, `PostService`,...) vào các layer Route. Code tách biệt routing theo từng tính năng (Feature-based routing).
- **Tính năng nổi bật**:
  - Hỗ trợ đầy đủ RESTful API cho một mạng xã hội hình ảnh (Posts, Like, Comment, Save).
  - Có Flow Admin để quản trị nội dung.
  - Tích hợp WebSocket (`/api/v1/ws/chat`) cho tính năng Chat Realtime.
  - Hỗ trợ Booking/Rating cho các Nhiếp Ảnh Gia (Photographers).

Dưới đây là danh sách toàn bộ **85 API (endpoints)** đã được đăng ký và phân loại định tuyến trong hệ thống.

## 2. Danh sách toàn bộ Endpoint API

### 🛠 System / Setup APIs (Public)
| Method | Endpoint | Bắt buộc Token | Mô tả |
|---|---|:---:|---|
| GET | `/health` | ❌ | Kiểm tra server hoạt động |
| GET | `/init-db` | ❌ | Khởi tạo 30 bảng CSDL |
| GET | `/reset-db` | ❌ | Xóa sạch và khởi tạo lại toàn bộ CSDL |
| GET | `/fix-user-id` | ❌ | Sửa lỗi Auto Increment cho ID=1 |
| GET | `/migrate-db` | ❌ | Migrate các cột bị thiếu vào CSDL mà không mất dữ liệu |

### 🔐 Auth APIs (Đăng nhập & Xác thực)
| Method | Endpoint | Bắt buộc Token | Mô tả |
|---|---|:---:|---|
| POST | `/api/v1/auth/register` | ❌ | Đăng ký tài khoản mới |
| POST | `/api/v1/auth/login` | ❌ | Đăng nhập mảng (trả về Access & Refresh Token) |
| POST | `/api/v1/auth/refresh` | ❌ | Lấy Access Token mới bằng Refresh Token |
| POST | `/api/v1/auth/forgot-password` | ❌ | Yêu cầu khôi phục mật khẩu |
| POST | `/api/v1/auth/reset-password` | ❌ | Đặt lại mật khẩu |
| POST | `/api/v1/auth/google` | ❌ | Đăng nhập thông qua Google |
| POST | `/api/v1/auth/2fa/verify-login` | ❌ | Xác thực mã OTP 2FA để đăng nhập |
| POST | `/api/v1/auth/logout` | ✅ | Đăng xuất (xóa Session/Refresh Token) |
| PUT | `/api/v1/auth/change-password` | ✅ | Đổi mật khẩu |
| POST | `/api/v1/auth/2fa/setup` | ✅ | Cấu hình 2FA ban đầu |
| POST | `/api/v1/auth/2fa/enable` | ✅ | Kích hoạt 2FA |

### 👤 User APIs (Người dùng & Hồ sơ)
| Method | Endpoint | Bắt buộc Token | Mô tả |
|---|---|:---:|---|
| GET | `/api/v1/users/me` | ✅ | Xem hồ sơ cá nhân hiện tại |
| PUT | `/api/v1/users/me` | ✅ | Cập nhật thông tin cá nhân |
| GET | `/api/v1/users/me/sessions` | ✅ | Lấy danh sách các phiên đăng nhập (Thiết bị) |
| DELETE | `/api/v1/users/me/sessions/{id}` | ✅ | Xóa/Đăng xuất một thiết bị cụ thể |
| POST | `/api/v1/users/me/deactivate` | ✅ | Tạm vô hiệu hóa tài khoản |
| PUT | `/api/v1/users/me/avatar` | ✅ | Cập nhật ảnh đại diện |
| GET | `/api/v1/users/suggestions` | ✅ | Gợi ý kết bạn / người dùng |
| GET | `/api/v1/users/{id}` | ❌ | Xem hồ sơ public của user khác |

### 🤝 Social Network APIs (Follow, Block, Mute)
| Method | Endpoint | Bắt buộc Token | Mô tả |
|---|---|:---:|---|
| POST | `/api/v1/users/{id}/follow` | ✅ | Theo dõi (Follow/Unfollow) người dùng |
| GET | `/api/v1/users/{id}/followers` | ❌ | Lấy danh sách Followers của người dùng |
| GET | `/api/v1/users/{id}/following` | ❌ | Lấy danh sách Following của người dùng |
| GET | `/api/v1/users/me/follow-requests` | ✅ | Lấy danh sách yêu cầu theo dõi (Private) |
| POST | `/api/v1/users/me/follow-requests/{followerId}/{action}`| ✅ | Chấp nhận/Từ chối request theo dõi |
| POST | `/api/v1/users/{id}/block` | ✅ | Chặn (Block/Unblock) người dùng |
| POST | `/api/v1/users/{id}/mute` | ✅ | Ẩn/Tắt tiếng (Mute/Unmute) người dùng |

### 🖼 Post APIs (Bài viết)
| Method | Endpoint | Bắt buộc Token | Mô tả |
|---|---|:---:|---|
| POST | `/api/v1/posts` | ✅ | Đăng bài viết mới |
| GET | `/api/v1/posts/feed` | ✅ | Lấy dòng thời gian (Feed) |
| GET | `/api/v1/posts/{id}` | ✅ | Lấy chi tiết bài viết |
| PUT | `/api/v1/posts/{id}` | ✅ | Cập nhật nội dung bài viết |
| DELETE | `/api/v1/posts/{id}` | ✅ | Xóa bài viết |

### ❤️ Interaction APIs (Tương tác: Like, Comment, Save)
| Method | Endpoint | Bắt buộc Token | Mô tả |
|---|---|:---:|---|
| POST | `/api/v1/posts/{id}/like` | ✅ | Thả tim / Bỏ tim bài viết |
| POST | `/api/v1/posts/{id}/save` | ✅ | Lưu / Bỏ lưu bài viết (Bookmark) |
| POST | `/api/v1/posts/{id}/comments` | ✅ | Viết bình luận |
| GET | `/api/v1/posts/{id}/comments` | ✅ | Lấy danh sách bình luận của bài viết |
| PUT | `/api/v1/comments/{commentId}` | ✅ | Sửa bình luận |
| DELETE | `/api/v1/comments/{commentId}` | ✅ | Xóa bình luận |
| POST | `/api/v1/comments/{commentId}/like`| ✅ | Thích / Bỏ thích bình luận |

### 📁 Media APIs (Quản lý File & S3 Mock)
| Method | Endpoint | Bắt buộc Token | Mô tả |
|---|---|:---:|---|
| POST | `/api/v1/media/presigned-url` | ✅ | Lấy S3 Presigned URL để upload ảnh/video |
| POST | `/api/v1/posts/{postId}/media`| ✅ | Thêm Media vào Bài viết |
| DELETE | `/api/v1/posts/media/{mediaId}`| ✅ | Xóa Media khỏi Bài viết |
| PUT | `/api/v1/posts/{postId}/media/reorder`|✅ | Sắp xếp lại thứ tự Media trong bài |

### 🗂 Album APIs
| Method | Endpoint | Bắt buộc Token | Mô tả |
|---|---|:---:|---|
| POST | `/api/v1/albums` | ✅ | Tạo Album mới |
| GET | `/api/v1/albums` | ✅ | Lấy danh sách Albums |
| GET | `/api/v1/albums/{id}` | ✅ | Xem chi tiết Album |
| PUT | `/api/v1/albums/{id}` | ✅ | Sửa thông tin Album |
| DELETE | `/api/v1/albums/{id}` | ✅ | Xóa Album |
| POST | `/api/v1/albums/{id}/media` | ✅ | Thêm ảnh/bài viết vào Album |
| DELETE | `/api/v1/albums/{id}/media/{mediaId}`| ✅ | Xóa ảnh/bài viết khỏi Album |

### 🔍 Explore & Search APIs (Khám phá & Tìm kiếm)
| Method | Endpoint | Bắt buộc Token | Mô tả |
|---|---|:---:|---|
| GET | `/api/v1/explore/trending` | ❌ | Lấy các thẻ/Hastag thịnh hành |
| GET | `/api/v1/explore` | ❌ | Lấy bài viết gợi ý Explore |
| GET | `/api/v1/search` | Tùy chọn | Tìm kiếm Global |
| GET | `/api/v1/search/history` | ✅ | Lấy lịch sử tìm kiếm |
| DELETE | `/api/v1/search/history` | ✅ | Xóa lịch sử tìm kiếm |

### 💬 Chat APIs (Trò chuyện trực tiếp)
| Method | Endpoint | Bắt buộc Token | Mô tả |
|---|---|:---:|---|
| GET | `/api/v1/chat/conversations` | ✅ | Lấy danh sách hội thoại |
| GET | `/api/v1/chat/conversations/{id}/messages`| ✅ | Lấy lịch sử tin nhắn trong phòng chat |
| WS  | `/api/v1/ws/chat?token=xxx` | ✅ | Kết nối WebSocket để chat Realtime |

### 📸 Portfolio, Booking & Rating APIs (Nhiếp Ảnh Gia)
| Method | Endpoint | Bắt buộc Token | Mô tả |
|---|---|:---:|---|
| GET | `/api/v1/portfolios` | ❌ | Tìm kiếm Nhiếp Ảnh Gia theo kỹ năng/vị trí |
| GET | `/api/v1/portfolios/users/{userId}`| ❌ | Xem hồ sơ Nhiếp Ảnh Gia công khai |
| GET | `/api/v1/portfolios/me` | ✅ | Quản lý Portfolio của tôi |
| PUT | `/api/v1/portfolios/me` | ✅ | Cập nhật Portfolio cá nhân hiện tại |
| POST | `/api/v1/portfolios/me/availability`| ✅ | Cập nhật lịch trống (Availability) |
| GET | `/api/v1/portfolios/me/availability`| ✅ | Xem lịch làm việc rảnh/bận của tôi |
| POST | `/api/v1/bookings` | ✅ | Tạo một yêu cầu đặt lịch (Booking) |
| GET | `/api/v1/bookings` | ✅ | Lấy danh sách lịch đã đặt / được đặt |
| PUT | `/api/v1/bookings/{id}/status` | ✅ | Cập nhật trạng thái Booking |
| GET | `/api/v1/users/{photographerId}/ratings` | ❌ | Lấy danh sách đánh giá từ khách hàng |
| POST | `/api/v1/users/{photographerId}/ratings` | ✅ | Viết đánh giá cho Photographer |
| DELETE | `/api/v1/ratings/{ratingId}`| ✅ | Gỡ bài đánh giá |

### 🔔 Notification APIs (Thông báo)
| Method | Endpoint | Bắt buộc Token | Mô tả |
|---|---|:---:|---|
| GET | `/api/v1/notifications` | ✅ | Lấy danh sách thông báo |
| PUT | `/api/v1/notifications/{id}/read` | ✅ | Đánh dấu 1 thông báo đã đọc |
| PUT | `/api/v1/notifications/read-all`| ✅ | Đánh dấu đọc tất cả |

### 🚩 Report & Admin APIs (Báo cáo & Quản trị Hệ thống)
| Method | Endpoint | Bắt buộc Token | Mô tả |
|---|---|:---:|---|
| POST | `/api/v1/reports` | ✅ | Người dùng: Báo cáo nội dung vi phạm |
| GET | `/api/v1/admin/stats` | ✅ (AD) | Lấy thông số tổng quan hệ thống |
| GET | `/api/v1/admin/stats/growth`| ✅ (AD) | Lấy biểu đồ tăng trưởng |
| PUT | `/api/v1/admin/users/{userId}/ban`| ✅ (AD) | Khóa / Mở khóa người dùng |
| DELETE | `/api/v1/admin/posts/{postId}`| ✅ (AD) | Admin xóa Bài viết vi phạm |
| DELETE | `/api/v1/admin/comments/{commentId}`| ✅ (AD) | Admin xóa Bình luận vi phạm |
| GET | `/api/v1/admin/reports` | ✅ (AD) | Lấy danh sách các báo cáo vi phạm |
| PUT | `/api/v1/admin/reports/{reportId}`| ✅ (AD) | Xử lý báo cáo vi phạm |

*Ghi chú: (AD) - Yêu cầu role là ADMIN.*
