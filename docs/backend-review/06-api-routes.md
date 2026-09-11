# 06. API Routes hiện có

## Ghi chú chung

- Base API chính là `/api/v1`.
- Auth protected dùng `authenticate("jwt")`.
- Response thường theo `ApiResponse`.
- Source of truth chi tiết: [../Backend_APIs.md](../api-specs/Backend_APIs.md).

- Tổng hợp theo source hiện tại: **139 REST endpoints dưới `/api/v1`**, **5 system HTTP routes**, **1 WebSocket**.
- Một số route đã expose nhưng logic phía dưới vẫn còn TODO/mock, cần review kỹ trước khi gọi là production-ready.

## System

| Method | Endpoint | Chức năng | Ghi chú |
|---|---|---|---|
| GET | `/health` | Health check | OK |
| GET | `/init-db` | Tạo schema DB | Nguy hiểm, public |
| GET | `/reset-db` | Drop và tạo lại DB | Cực nguy hiểm, public |
| GET | `/fix-user-id` | Chạy SQL sửa user ID | Chỉ dùng local |
| GET | `/migrate-db` | Tạo bảng/cột còn thiếu | Không nên public |

## Module summary

| Module | REST endpoints |
|---|---:|
| Auth | 11 |
| Users | 22 |
| Posts | 9 |
| Interactions | 11 |
| Media | 6 |
| Albums | 7 |
| Explore | 3 |
| Search | 4 |
| Chat REST | 6 |
| Notifications | 5 |
| Portfolios | 7 |
| Photographer services | 7 |
| Bookings | 6 |
| Ratings | 4 |
| Reports | 3 |
| Devices | 2 |
| Admin | 26 |
| **Total `/api/v1` REST** | **139** |

## Auth

| Method | Endpoint | Ghi chú |
|---|---|---|
| POST | `/api/v1/auth/register` | Public |
| POST | `/api/v1/auth/login` | Public |
| POST | `/api/v1/auth/refresh` | Public |
| POST | `/api/v1/auth/forgot-password` | Public |
| POST | `/api/v1/auth/reset-password` | Public |
| POST | `/api/v1/auth/google` | Public |
| POST | `/api/v1/auth/2fa/verify-login` | TODO/simple stub |
| POST | `/api/v1/auth/logout` | Protected |
| PUT | `/api/v1/auth/change-password` | Protected |
| POST | `/api/v1/auth/2fa/setup` | TODO/simple stub |
| POST | `/api/v1/auth/2fa/enable` | TODO/simple stub |

## Users

| Method | Endpoint | Ghi chú |
|---|---|---|
| GET | `/api/v1/users/me` | Protected |
| PUT | `/api/v1/users/me` | Protected |
| GET | `/api/v1/users/me/sessions` | Protected |
| DELETE | `/api/v1/users/me/sessions/{id}` | Protected |
| POST | `/api/v1/users/me/deactivate` | Protected |
| PUT | `/api/v1/users/me/avatar` | Mock upload |
| PUT | `/api/v1/users/me/privacy` | Protected |
| GET | `/api/v1/users/me/saved-posts` | Protected |
| GET | `/api/v1/users/me/liked-posts` | Protected |
| GET | `/api/v1/users/me/tagged-posts` | Protected |
| GET | `/api/v1/users/me/comments` | Protected |
| GET | `/api/v1/users/me/activity-log` | Protected |
| GET | `/api/v1/users/me/blocked` | Protected |
| POST | `/api/v1/users/{id}/follow` | Toggle follow |
| GET | `/api/v1/users/suggestions` | Protected |
| GET | `/api/v1/users/me/follow-requests` | TODO/skeleton |
| POST | `/api/v1/users/me/follow-requests/{followerId}/{action}` | TODO/skeleton |
| POST | `/api/v1/users/{id}/block` | TODO/skeleton |
| POST | `/api/v1/users/{id}/mute` | TODO/skeleton |
| GET | `/api/v1/users/{id}` | Public profile |
| GET | `/api/v1/users/{id}/followers` | Public |
| GET | `/api/v1/users/{id}/following` | Public |

## Posts và interactions

| Method | Endpoint | Ghi chú |
|---|---|---|
| POST | `/api/v1/posts` | Protected |
| GET | `/api/v1/posts/feed` | Protected |
| GET | `/api/v1/posts/{id}` | Protected |
| PUT | `/api/v1/posts/{id}` | Protected |
| DELETE | `/api/v1/posts/{id}` | Protected |
| GET | `/api/v1/posts/users/{userId}/posts` | Protected |
| POST | `/api/v1/posts/{id}/tags` | Protected |
| DELETE | `/api/v1/posts/{id}/tags/{taggedUserId}` | Protected |
| PUT | `/api/v1/posts/{id}/comment-settings` | Protected |
| POST | `/api/v1/posts/{id}/like` | Protected |
| GET | `/api/v1/posts/{id}/likes` | Protected |
| POST | `/api/v1/posts/{id}/save` | Protected |
| POST | `/api/v1/posts/{id}/comments` | Protected |
| GET | `/api/v1/posts/{id}/comments` | Protected |
| POST | `/api/v1/posts/{id}/share` | Protected |
| GET | `/api/v1/posts/{id}/shares` | Protected |
| PUT | `/api/v1/comments/{commentId}` | Protected |
| DELETE | `/api/v1/comments/{commentId}` | Protected |
| POST | `/api/v1/comments/{commentId}/like` | Protected |
| POST | `/api/v1/comments/{commentId}/dislike` | Protected |

## Media, albums, explore, search

| Method | Endpoint | Ghi chú |
|---|---|---|
| PUT | `/api/v1/media/local-upload/{folder}/{fileName}` | Public local/dev route |
| GET | `/api/v1/media/local-files/{folder}/{fileName}` | Public local/dev route |
| POST | `/api/v1/media/presigned-url` | Protected, mock/local behavior |
| POST | `/api/v1/posts/{postId}/media` | Protected |
| DELETE | `/api/v1/posts/media/{mediaId}` | Protected |
| PUT | `/api/v1/posts/{postId}/media/reorder` | Protected |
| POST | `/api/v1/albums` | TODO/skeleton |
| GET | `/api/v1/albums` | TODO/skeleton |
| GET | `/api/v1/albums/{id}` | TODO/skeleton |
| PUT | `/api/v1/albums/{id}` | TODO/skeleton |
| DELETE | `/api/v1/albums/{id}` | TODO/skeleton |
| POST | `/api/v1/albums/{id}/media` | TODO/skeleton |
| DELETE | `/api/v1/albums/{id}/media/{mediaId}` | TODO/skeleton |
| GET | `/api/v1/explore` | Public |
| GET | `/api/v1/explore/trending` | Public |
| GET | `/api/v1/explore/hashtags/{tag}` | Public |
| GET | `/api/v1/search` | Optional auth, currently decodes JWT manually |
| GET | `/api/v1/search/history` | Protected |
| DELETE | `/api/v1/search/history` | Protected |
| GET | `/api/v1/search/trending` | Public |

## Portfolio, services, booking, ratings

| Method | Endpoint | Ghi chú |
|---|---|---|
| GET | `/api/v1/portfolios` | Public |
| GET | `/api/v1/portfolios/users/{userId}` | Public |
| GET | `/api/v1/portfolios/users/{userId}/availability` | Public |
| GET | `/api/v1/portfolios/me` | Protected |
| PUT | `/api/v1/portfolios/me` | Protected |
| POST | `/api/v1/portfolios/me/availability` | Protected |
| GET | `/api/v1/portfolios/me/availability` | Protected |
| GET | `/api/v1/users/{photographerId}/services` | Public |
| GET | `/api/v1/photographer/services` | Protected |
| POST | `/api/v1/photographer/services` | Protected |
| GET | `/api/v1/photographer/services/{id}` | Protected |
| PUT | `/api/v1/photographer/services/{id}` | Protected |
| PUT | `/api/v1/photographer/services/{id}/status` | Protected |
| DELETE | `/api/v1/photographer/services/{id}` | Protected |
| POST | `/api/v1/bookings` | Protected |
| GET | `/api/v1/bookings` | Protected |
| GET | `/api/v1/bookings/{id}` | Protected |
| PUT | `/api/v1/bookings/{id}/status` | Protected |
| POST | `/api/v1/bookings/{id}/review` | Protected |
| DELETE | `/api/v1/bookings/{id}` | Protected |
| GET | `/api/v1/users/{photographerId}/ratings` | Public |
| POST | `/api/v1/users/{photographerId}/ratings` | Protected |
| DELETE | `/api/v1/ratings/{ratingId}` | Protected |
| PUT | `/api/v1/ratings/{ratingId}` | Protected |

## Chat, devices, notifications, reports

| Method | Endpoint | Ghi chú |
|---|---|---|
| POST | `/api/v1/chat/conversations` | Protected, body parse bằng tay |
| GET | `/api/v1/chat/conversations` | Protected |
| GET | `/api/v1/chat/conversations/{id}/messages` | Protected |
| PUT | `/api/v1/chat/conversations/{id}/read` | Protected |
| POST | `/api/v1/chat/conversations/{id}/messages` | Protected, body parse bằng tay |
| DELETE | `/api/v1/chat/conversations/{id}` | Protected |
| WS | `/api/v1/ws/chat?token=...` | JWT decode, chưa verify đúng cách |
| POST | `/api/v1/devices/fcm-token` | Protected |
| DELETE | `/api/v1/devices/fcm-token` | Protected |
| GET | `/api/v1/notifications` | Protected |
| GET | `/api/v1/notifications/unread-count` | Protected |
| PUT | `/api/v1/notifications/{id}/read` | Protected |
| PUT | `/api/v1/notifications/read-all` | Protected |
| DELETE | `/api/v1/notifications/{id}` | Protected |
| POST | `/api/v1/reports` | Protected |
| GET | `/api/v1/admin/reports` | Admin |
| PUT | `/api/v1/admin/reports/{reportId}` | Admin |

## Admin

| Method | Endpoint | Ghi chú |
|---|---|---|
| GET | `/api/v1/admin/stats` | Admin |
| GET | `/api/v1/admin/stats/growth` | Admin |
| PUT | `/api/v1/admin/users/{userId}/ban` | Admin |
| DELETE | `/api/v1/admin/posts/{postId}` | Admin |
| DELETE | `/api/v1/admin/comments/{commentId}` | Admin |
| GET | `/api/v1/admin/users` | Admin |
| GET | `/api/v1/admin/users/{userId}` | Admin |
| PUT | `/api/v1/admin/users/{userId}/verification` | Admin |
| PUT | `/api/v1/admin/users/{userId}/featured` | Admin |
| GET | `/api/v1/admin/posts` | Admin |
| GET | `/api/v1/admin/posts/{postId}` | Admin |
| PUT | `/api/v1/admin/posts/{postId}/status` | Admin |
| GET | `/api/v1/admin/bookings` | Admin |
| GET | `/api/v1/admin/bookings/{bookingId}` | Admin |
| PUT | `/api/v1/admin/bookings/{bookingId}/status` | Admin |
| GET | `/api/v1/admin/ratings` | Admin |
| PUT | `/api/v1/admin/ratings/{ratingId}/status` | Admin |
| DELETE | `/api/v1/admin/ratings/{ratingId}` | Admin |
| GET | `/api/v1/admin/media` | Admin |
| DELETE | `/api/v1/admin/media/{mediaId}` | Admin |
| GET | `/api/v1/admin/notifications` | Admin |
| POST | `/api/v1/admin/notifications` | Admin |
| GET | `/api/v1/admin/banned-keywords` | Admin |
| POST | `/api/v1/admin/banned-keywords` | Admin |
| DELETE | `/api/v1/admin/banned-keywords/{id}` | Admin |
| GET | `/api/v1/admin/activity-logs` | Admin |

## Nhận xét tổng quan về API

- Bề rộng chức năng rất tốt.
- Depth chưa đồng đều: nhiều endpoint hoàn chỉnh, nhiều endpoint mới là skeleton/mock.
- Các route system/debug đang public và có tác động DB, cần khóa trong production.
- WebSocket và optional auth ở search đang decode token, cần verify chữ ký và hạn dùng.
- Contract response nên được chuẩn hóa chặt hơn, tránh `Any` và `Map<String, Any>` ở các lớp service/DTO.
