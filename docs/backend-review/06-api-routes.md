# 06. API Routes hiện có

## Ghi chú chung
- Base API chủ yếu là `/api/v1`
- Auth protected dùng `authenticate("jwt")`
- Response thường theo `ApiResponse`
- Một số route đã public nhưng logic còn TODO/mock, tôi đánh dấu rõ bên dưới

## System

| Method | Endpoint | Chức năng | Ghi chú |
|---|---|---|---|
| GET | `/health` | Health check | OK |
| GET | `/init-db` | Tạo schema DB | Nguy hiểm, public |
| GET | `/reset-db` | Drop và tạo lại DB | Cực nguy hiểm, public |
| GET | `/fix-user-id` | Chạy SQL sửa user ID | Chỉ dùng local |
| GET | `/migrate-db` | Tạo bảng/cột còn thiếu | Không nên public |

## Auth

| Method | Endpoint | Request | Response | Ghi chú |
|---|---|---|---|---|
| POST | `/api/v1/auth/register` | `RegisterRequest` | `RegisterResponse` | Đăng ký |
| POST | `/api/v1/auth/login` | `LoginRequest` | `LoginResponse` | Đăng nhập |
| POST | `/api/v1/auth/refresh` | `RefreshTokenRequest` | `TokenRefreshResponse` | Refresh access token |
| POST | `/api/v1/auth/forgot-password` | `ForgotPasswordRequest` | `null` | Mock email reset |
| POST | `/api/v1/auth/reset-password` | `ResetPasswordRequest` | `null` | Đặt lại mật khẩu |
| POST | `/api/v1/auth/google` | chưa chuẩn hóa | `null` | TODO |
| POST | `/api/v1/auth/2fa/verify-login` | chưa chuẩn hóa | `null` | TODO |
| POST | `/api/v1/auth/logout` | Header `X-Refresh-Token` | `null` | Protected |
| PUT | `/api/v1/auth/change-password` | `ChangePasswordRequest` | `null` | Protected |
| POST | `/api/v1/auth/2fa/setup` | chưa chuẩn hóa | `null` | TODO |
| POST | `/api/v1/auth/2fa/enable` | chưa chuẩn hóa | `null` | TODO |

## Users

| Method | Endpoint | Chức năng | Ghi chú |
|---|---|---|---|
| GET | `/api/v1/users/me` | Lấy profile hiện tại | Protected |
| PUT | `/api/v1/users/me` | Cập nhật profile | Protected |
| GET | `/api/v1/users/me/sessions` | Danh sách session | Protected |
| DELETE | `/api/v1/users/me/sessions/{id}` | Revoke 1 session | Protected |
| POST | `/api/v1/users/me/deactivate` | Vô hiệu hóa tài khoản | Protected |
| PUT | `/api/v1/users/me/avatar` | Cập nhật avatar | Mock |
| PUT | `/api/v1/users/me/privacy` | Bật/tắt private account | Protected |
| GET | `/api/v1/users/me/saved-posts` | Bài đã lưu | Hiện repo còn TODO |
| GET | `/api/v1/users/me/liked-posts` | Bài đã like | Hiện repo còn TODO |
| GET | `/api/v1/users/me/tagged-posts` | Bài được tag | Hiện repo còn TODO |
| GET | `/api/v1/users/me/activity-log` | Nhật ký hoạt động | Hiện repo còn TODO |
| GET | `/api/v1/users/me/blocked` | Danh sách block | Hiện repo còn TODO |
| POST | `/api/v1/users/{id}/follow` | Follow/unfollow | Protected |
| GET | `/api/v1/users/suggestions` | Gợi ý user | Protected |
| GET | `/api/v1/users/me/follow-requests` | Danh sách follow request | TODO |
| POST | `/api/v1/users/me/follow-requests/{followerId}/{action}` | Accept/reject follow request | TODO |
| POST | `/api/v1/users/{id}/block` | Block/unblock user | TODO |
| POST | `/api/v1/users/{id}/mute` | Mute/unmute user | TODO |
| GET | `/api/v1/users/{id}` | Public profile | Có nguy cơ lộ `passwordHash` |
| GET | `/api/v1/users/{id}/followers` | Danh sách followers | Public |
| GET | `/api/v1/users/{id}/following` | Danh sách following | Public |

## Posts

| Method | Endpoint | Chức năng | Ghi chú |
|---|---|---|---|
| POST | `/api/v1/posts` | Tạo post | Protected |
| GET | `/api/v1/posts/feed` | Feed theo followings | Protected |
| GET | `/api/v1/posts/{id}` | Chi tiết post | Protected |
| PUT | `/api/v1/posts/{id}` | Sửa post | Protected |
| DELETE | `/api/v1/posts/{id}` | Xóa mềm post | Protected |
| GET | `/api/v1/posts/users/{userId}/posts` | Danh sách post của user | Protected theo code hiện tại |
| POST | `/api/v1/posts/{id}/tags` | Tag user vào post | TODO ở repo |
| DELETE | `/api/v1/posts/{id}/tags/{taggedUserId}` | Gỡ tag user | TODO ở repo |
| PUT | `/api/v1/posts/{id}/comment-settings` | Đổi comment visibility | Protected |

## Interactions

| Method | Endpoint | Chức năng |
|---|---|---|
| POST | `/api/v1/posts/{id}/like` | Like/unlike post |
| GET | `/api/v1/posts/{id}/likes` | Danh sách user đã like |
| POST | `/api/v1/posts/{id}/save` | Save/unsave post |
| POST | `/api/v1/posts/{id}/comments` | Tạo comment |
| GET | `/api/v1/posts/{id}/comments` | Lấy comment + replies |
| POST | `/api/v1/posts/{id}/share` | Tăng share count |
| GET | `/api/v1/posts/{id}/shares` | Lấy share count |
| PUT | `/api/v1/comments/{commentId}` | Sửa comment |
| DELETE | `/api/v1/comments/{commentId}` | Xóa comment |
| POST | `/api/v1/comments/{commentId}/like` | Like comment |

## Booking

| Method | Endpoint | Chức năng |
|---|---|---|
| POST | `/api/v1/bookings` | Tạo booking |
| GET | `/api/v1/bookings` | Danh sách booking của tôi |
| GET | `/api/v1/bookings/{id}` | Chi tiết booking |
| PUT | `/api/v1/bookings/{id}/status` | Đổi trạng thái booking |
| DELETE | `/api/v1/bookings/{id}` | Hủy booking |

## Chat

| Method | Endpoint | Chức năng | Ghi chú |
|---|---|---|---|
| POST | `/api/v1/chat/conversations` | Tạo hoặc lấy conversation trực tiếp | Body parse tay |
| GET | `/api/v1/chat/conversations` | Danh sách conversation | Protected |
| GET | `/api/v1/chat/conversations/{id}/messages` | Lịch sử tin nhắn | Protected |
| POST | `/api/v1/chat/conversations/{id}/messages` | Gửi tin nhắn HTTP | Body parse tay |
| DELETE | `/api/v1/chat/conversations/{id}` | Ẩn conversation | Protected |
| WS | `/api/v1/ws/chat?token=...` | Realtime chat | Auth WS đang chưa verify JWT đúng cách |

## Notifications

| Method | Endpoint | Chức năng |
|---|---|---|
| GET | `/api/v1/notifications` | Danh sách notification |
| GET | `/api/v1/notifications/unread-count` | Số chưa đọc |
| PUT | `/api/v1/notifications/{id}/read` | Mark read |
| PUT | `/api/v1/notifications/read-all` | Mark read all |
| DELETE | `/api/v1/notifications/{id}` | Xóa notification |

## Search

| Method | Endpoint | Chức năng | Ghi chú |
|---|---|---|---|
| GET | `/api/v1/search` | Search user/post | Optional auth đang `decode` JWT |
| GET | `/api/v1/search/history` | Lịch sử search | Protected |
| DELETE | `/api/v1/search/history` | Xóa lịch sử search | Protected |
| GET | `/api/v1/search/trending` | Trending keywords | Public |

## Portfolio

| Method | Endpoint | Chức năng |
|---|---|---|
| GET | `/api/v1/portfolios` | Tìm photographer |
| GET | `/api/v1/portfolios/users/{userId}` | Portfolio của user |
| GET | `/api/v1/portfolios/me` | Portfolio của tôi |
| PUT | `/api/v1/portfolios/me` | Upsert portfolio |
| POST | `/api/v1/portfolios/me/availability` | TODO availability |
| GET | `/api/v1/portfolios/me/availability` | TODO availability |

## Ratings

| Method | Endpoint | Chức năng |
|---|---|---|
| GET | `/api/v1/users/{photographerId}/ratings` | Xem rating của photographer |
| POST | `/api/v1/users/{photographerId}/ratings` | Tạo rating |
| DELETE | `/api/v1/ratings/{ratingId}` | Xóa rating |
| PUT | `/api/v1/ratings/{ratingId}` | Sửa rating |

## Media

| Method | Endpoint | Chức năng | Ghi chú |
|---|---|---|---|
| POST | `/api/v1/media/presigned-url` | Tạo presigned URL | Mock S3 |
| POST | `/api/v1/posts/{postId}/media` | Thêm media vào post | Protected |
| DELETE | `/api/v1/posts/media/{mediaId}` | Xóa media | Protected |
| PUT | `/api/v1/posts/{postId}/media/reorder` | Đổi thứ tự media | Protected |

## Explore

| Method | Endpoint | Chức năng |
|---|---|---|
| GET | `/api/v1/explore/trending` | Trending hashtag |
| GET | `/api/v1/explore` | Explore feed |
| GET | `/api/v1/explore/hashtags/{tag}` | Browse theo hashtag |

## Reports

| Method | Endpoint | Chức năng |
|---|---|---|
| POST | `/api/v1/reports` | User gửi report |
| GET | `/api/v1/admin/reports` | Admin xem reports |
| PUT | `/api/v1/admin/reports/{reportId}` | Admin update report |

## Admin

| Method | Endpoint | Chức năng | Ghi chú |
|---|---|---|---|
| GET | `/api/v1/admin/stats` | Dashboard stats | Protected + admin |
| GET | `/api/v1/admin/stats/growth` | Growth chart | Protected + admin |
| PUT | `/api/v1/admin/users/{userId}/ban` | Ban/unban user | Logic ban hiện chưa chuẩn |
| DELETE | `/api/v1/admin/posts/{postId}` | Admin xóa post | Soft delete |
| DELETE | `/api/v1/admin/comments/{commentId}` | Admin xóa comment | Soft delete |
| GET | `/api/v1/admin/users` | Danh sách user | `status` query chưa dùng |
| GET | `/api/v1/admin/users/{userId}` | Chi tiết user | Map động |
| GET | `/api/v1/admin/banned-keywords` | Danh sách từ khóa cấm | TODO |
| POST | `/api/v1/admin/banned-keywords` | Thêm từ khóa cấm | TODO giả |
| DELETE | `/api/v1/admin/banned-keywords/{id}` | Xóa từ khóa cấm | TODO giả |

## Albums

| Method | Endpoint | Chức năng | Ghi chú |
|---|---|---|---|
| POST | `/api/v1/albums` | Tạo album | TODO |
| GET | `/api/v1/albums` | Danh sách album | TODO |
| GET | `/api/v1/albums/{id}` | Chi tiết album | TODO |
| PUT | `/api/v1/albums/{id}` | Sửa album | TODO |
| DELETE | `/api/v1/albums/{id}` | Xóa album | TODO |
| POST | `/api/v1/albums/{id}/media` | Thêm media vào album | TODO |
| DELETE | `/api/v1/albums/{id}/media/{mediaId}` | Xóa media khỏi album | TODO |

## Nhận xét tổng quan về API
- Bề rộng chức năng rất tốt.
- Depth chưa đồng đều: nhiều endpoint hoàn chỉnh, nhiều endpoint mới là skeleton.
- Một số endpoint public/protected chưa hợp lý hoàn toàn.
- Contract response nên được chuẩn hóa chặt hơn, tránh `Any` và `Map<String, Any>`.
