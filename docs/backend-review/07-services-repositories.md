# 07. Service và Repository

## Vai trò lý thuyết

### Service nên làm gì
- Validate nghiệp vụ
- Kiểm tra quyền
- Điều phối nhiều repository
- Không nên chứa SQL trực tiếp

### Repository nên làm gì
- Query database
- Map dữ liệu DB -> DTO/entity
- Không nên chứa business rule phức tạp

## Thực tế project hiện tại

## Auth

### `AuthService`
Làm khá đúng vai trò service:
- validate email/password
- kiểm tra duplicate
- hash password
- generate access token
- tạo refresh token session
- đổi mật khẩu/reset mật khẩu

Repository liên quan:
- `UserRepository`
- `SessionRepository`
- `PasswordResetRepository`

Đây là module có mức hoàn thiện cao nhất.

## User

### `UserService`
Các hàm chính:
- `getCurrentUser`
- `updateProfile`
- `deactivateAccount`
- `getSuggestedUsers`
- `updatePrivacy`

Vấn đề:
- `deactivateAccount()` và `updatePrivacy()` đang update thẳng `UsersTable` thay vì đi qua repository.
- Điều này làm service vừa làm business vừa làm data access.

## Post

### `PostService`
Các trách nhiệm chính:
- validate số media
- đọc feed
- lấy chi tiết post
- update/delete post
- explore/trending
- comment setting

### `PostRepository`
Chứa khá nhiều query thật:
- create post + media
- feed pagination
- post detail
- explore
- trending tags

Vấn đề:
- `tagUserInPost` và `removeTagFromPost` vẫn TODO.
- `getFeedPosts()` hiện chỉ lấy post `PUBLIC`, kể cả post của chính user. Về sản phẩm, thường nên cho phép thấy post private của chính mình.

## Interaction

### `InteractionService`
Phần validate khá rõ:
- check post/comment tồn tại
- check nội dung comment rỗng
- check self-follow

### `InteractionRepository`
Phần đã có:
- like/unlike post
- save/unsave
- create/get/update/delete comment
- like comment
- follow/unfollow
- followers/following
- share count

Phần còn thiếu:
- saved posts
- liked posts
- tagged posts
- activity log
- blocked users

Hiện các hàm này trả `Any` hoặc map rỗng. Nghĩa là API đã mở nhưng contract chưa chín.

## Booking

### `BookingService`
Đây là service có business rule tốt:
- cấm self booking
- parse ngày
- chặn ngày quá khứ
- chặn state transition sai
- phân quyền client/photographer khi update status

### `BookingRepository`
- insert booking
- get booking
- update status
- pagination booking list

Điểm chưa tối ưu:
- `getBookingsList()` bị N+1 query khi lấy partner name.

## Chat

### `ChatService`
Làm 2 việc:
- REST chat history / send message
- realtime push qua WebSocket

Điểm tốt:
- check membership conversation trước khi đọc/gửi tin nhắn
- push message tới member đang online

Điểm yếu:
- auth WebSocket chưa an toàn
- `ConnectionManager` chỉ giữ 1 session/user, không hỗ trợ nhiều thiết bị cùng online

## Portfolio, Rating, Report, Notification, Search, Media, Admin

### `PortfolioService`
- validate role photographer
- validate range giá
- upsert portfolio

### `RatingService`
- kiểm tra score
- kiểm tra booking hợp lệ
- chống duplicate rating

### `ReportService`
- khá mỏng, chủ yếu bọc repository

### `NotificationService`
- rất mỏng, gần như pass-through

### `SearchService`
- validate query rỗng
- save search history nếu có userId

### `MediaService`
- validate file extension
- validate media count
- presigned URL hiện là mock

### `AdminService`
- bọc logic admin stats/moderation
- nhưng nhiều kết quả vẫn là `Any`

## Đánh giá clean architecture

## Đúng hướng
- Route không gọi SQL trực tiếp.
- Hầu hết module có đủ route/service/repository.
- Business rule quan trọng của auth, booking, rating đã đi vào service.

## Chưa đúng clean architecture
- Service còn đụng DB trực tiếp.
- Repository trả DTO public thay vì entity/domain model.
- Thiếu interface cho repository.
- Thiếu use case layer riêng.
- Nhiều hàm trả `Any`.

## Kết luận
Hiện tại project đang ở mức “layered architecture dùng được”. Nếu mục tiêu là production hoặc mở rộng team, cần chuẩn hóa:

```text
Route -> UseCase -> Repository Interface -> Repository Impl -> DB
```

và tách:
- `InternalUser`
- `UserResponse`
- mapper giữa DB row, domain model, response DTO
