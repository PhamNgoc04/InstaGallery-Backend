# InstaGallery Backend - API Overview

Tai lieu nay duoc cap nhat theo source hien tai trong `src/main/kotlin/com/instagallery/routes` va `src/main/kotlin/com/instagallery/plugins/Routing.kt`.

## 1. Tong quan hien tai

- Database tables: **30 bang**.
- REST API duoi `/api/v1`: **107 endpoints**.
- System/debug HTTP routes ngoai `/api/v1`: **5 endpoints**.
- Tong HTTP endpoints: **112 endpoints**.
- WebSocket: **1 endpoint** (`/api/v1/ws/chat`).
- Tong neu tinh ca WebSocket: **113 endpoints**.

> Luu y: cac route `/init-db`, `/reset-db`, `/fix-user-id`, `/migrate-db` chi nen dung local/dev vi co tac dong truc tiep den database.

## 2. System / Setup APIs

| Method | Endpoint | Auth | Mo ta |
|---|---|:---:|---|
| GET | `/health` | No | Health check |
| GET | `/init-db` | No | Tao 30 bang CSDL |
| GET | `/reset-db` | No | Drop va tao lai CSDL |
| GET | `/fix-user-id` | No | Sua nhanh user id local |
| GET | `/migrate-db` | No | Tao bang/cot con thieu |

## 3. REST APIs duoi `/api/v1`

### Auth APIs - 11 endpoints

| Method | Endpoint | Auth |
|---|---|:---:|
| POST | `/api/v1/auth/register` | No |
| POST | `/api/v1/auth/login` | No |
| POST | `/api/v1/auth/refresh` | No |
| POST | `/api/v1/auth/forgot-password` | No |
| POST | `/api/v1/auth/reset-password` | No |
| POST | `/api/v1/auth/google` | No |
| POST | `/api/v1/auth/2fa/verify-login` | No |
| POST | `/api/v1/auth/logout` | Yes |
| PUT | `/api/v1/auth/change-password` | Yes |
| POST | `/api/v1/auth/2fa/setup` | Yes |
| POST | `/api/v1/auth/2fa/enable` | Yes |

### User APIs - 21 endpoints

| Method | Endpoint | Auth |
|---|---|:---:|
| GET | `/api/v1/users/me` | Yes |
| PUT | `/api/v1/users/me` | Yes |
| GET | `/api/v1/users/me/sessions` | Yes |
| DELETE | `/api/v1/users/me/sessions/{id}` | Yes |
| POST | `/api/v1/users/me/deactivate` | Yes |
| PUT | `/api/v1/users/me/avatar` | Yes |
| PUT | `/api/v1/users/me/privacy` | Yes |
| GET | `/api/v1/users/me/saved-posts` | Yes |
| GET | `/api/v1/users/me/liked-posts` | Yes |
| GET | `/api/v1/users/me/tagged-posts` | Yes |
| GET | `/api/v1/users/me/activity-log` | Yes |
| GET | `/api/v1/users/me/blocked` | Yes |
| POST | `/api/v1/users/{id}/follow` | Yes |
| GET | `/api/v1/users/suggestions` | Yes |
| GET | `/api/v1/users/me/follow-requests` | Yes |
| POST | `/api/v1/users/me/follow-requests/{followerId}/{action}` | Yes |
| POST | `/api/v1/users/{id}/block` | Yes |
| POST | `/api/v1/users/{id}/mute` | Yes |
| GET | `/api/v1/users/{id}` | No |
| GET | `/api/v1/users/{id}/followers` | No |
| GET | `/api/v1/users/{id}/following` | No |

### Post APIs - 9 endpoints

| Method | Endpoint | Auth |
|---|---|:---:|
| POST | `/api/v1/posts` | Yes |
| GET | `/api/v1/posts/feed` | Yes |
| GET | `/api/v1/posts/{id}` | Yes |
| PUT | `/api/v1/posts/{id}` | Yes |
| DELETE | `/api/v1/posts/{id}` | Yes |
| GET | `/api/v1/posts/users/{userId}/posts` | Yes |
| POST | `/api/v1/posts/{id}/tags` | Yes |
| DELETE | `/api/v1/posts/{id}/tags/{taggedUserId}` | Yes |
| PUT | `/api/v1/posts/{id}/comment-settings` | Yes |

### Interaction APIs - 10 endpoints

| Method | Endpoint | Auth |
|---|---|:---:|
| POST | `/api/v1/posts/{id}/like` | Yes |
| GET | `/api/v1/posts/{id}/likes` | Yes |
| POST | `/api/v1/posts/{id}/save` | Yes |
| POST | `/api/v1/posts/{id}/comments` | Yes |
| GET | `/api/v1/posts/{id}/comments` | Yes |
| POST | `/api/v1/posts/{id}/share` | Yes |
| GET | `/api/v1/posts/{id}/shares` | Yes |
| PUT | `/api/v1/comments/{commentId}` | Yes |
| DELETE | `/api/v1/comments/{commentId}` | Yes |
| POST | `/api/v1/comments/{commentId}/like` | Yes |

### Media APIs - 4 endpoints

| Method | Endpoint | Auth |
|---|---|:---:|
| POST | `/api/v1/media/presigned-url` | Yes |
| POST | `/api/v1/posts/{postId}/media` | Yes |
| DELETE | `/api/v1/posts/media/{mediaId}` | Yes |
| PUT | `/api/v1/posts/{postId}/media/reorder` | Yes |

### Album APIs - 7 endpoints

| Method | Endpoint | Auth |
|---|---|:---:|
| POST | `/api/v1/albums` | Yes |
| GET | `/api/v1/albums` | Yes |
| GET | `/api/v1/albums/{id}` | Yes |
| PUT | `/api/v1/albums/{id}` | Yes |
| DELETE | `/api/v1/albums/{id}` | Yes |
| POST | `/api/v1/albums/{id}/media` | Yes |
| DELETE | `/api/v1/albums/{id}/media/{mediaId}` | Yes |

### Explore APIs - 3 endpoints

| Method | Endpoint | Auth |
|---|---|:---:|
| GET | `/api/v1/explore/trending` | No |
| GET | `/api/v1/explore` | No |
| GET | `/api/v1/explore/hashtags/{tag}` | No |

### Search APIs - 4 endpoints

| Method | Endpoint | Auth |
|---|---|:---:|
| GET | `/api/v1/search` | Optional |
| GET | `/api/v1/search/history` | Yes |
| DELETE | `/api/v1/search/history` | Yes |
| GET | `/api/v1/search/trending` | No |

### Chat APIs - 5 REST endpoints + 1 WebSocket

| Method | Endpoint | Auth |
|---|---|:---:|
| POST | `/api/v1/chat/conversations` | Yes |
| GET | `/api/v1/chat/conversations` | Yes |
| GET | `/api/v1/chat/conversations/{id}/messages` | Yes |
| POST | `/api/v1/chat/conversations/{id}/messages` | Yes |
| DELETE | `/api/v1/chat/conversations/{id}` | Yes |
| WS | `/api/v1/ws/chat?token={jwt}` | Token query |

### Notification APIs - 5 endpoints

| Method | Endpoint | Auth |
|---|---|:---:|
| GET | `/api/v1/notifications` | Yes |
| GET | `/api/v1/notifications/unread-count` | Yes |
| PUT | `/api/v1/notifications/{id}/read` | Yes |
| PUT | `/api/v1/notifications/read-all` | Yes |
| DELETE | `/api/v1/notifications/{id}` | Yes |

### Portfolio APIs - 6 endpoints

| Method | Endpoint | Auth |
|---|---|:---:|
| GET | `/api/v1/portfolios` | No |
| GET | `/api/v1/portfolios/users/{userId}` | No |
| GET | `/api/v1/portfolios/me` | Yes |
| PUT | `/api/v1/portfolios/me` | Yes |
| POST | `/api/v1/portfolios/me/availability` | Yes |
| GET | `/api/v1/portfolios/me/availability` | Yes |

### Booking APIs - 5 endpoints

| Method | Endpoint | Auth |
|---|---|:---:|
| POST | `/api/v1/bookings` | Yes |
| GET | `/api/v1/bookings` | Yes |
| GET | `/api/v1/bookings/{id}` | Yes |
| PUT | `/api/v1/bookings/{id}/status` | Yes |
| DELETE | `/api/v1/bookings/{id}` | Yes |

### Rating APIs - 4 endpoints

| Method | Endpoint | Auth |
|---|---|:---:|
| GET | `/api/v1/users/{photographerId}/ratings` | No |
| POST | `/api/v1/users/{photographerId}/ratings` | Yes |
| DELETE | `/api/v1/ratings/{ratingId}` | Yes |
| PUT | `/api/v1/ratings/{ratingId}` | Yes |

### Report APIs - 3 endpoints

| Method | Endpoint | Auth |
|---|---|:---:|
| POST | `/api/v1/reports` | Yes |
| GET | `/api/v1/admin/reports` | Admin |
| PUT | `/api/v1/admin/reports/{reportId}` | Admin |

### Admin APIs - 10 endpoints

| Method | Endpoint | Auth |
|---|---|:---:|
| GET | `/api/v1/admin/stats` | Admin |
| GET | `/api/v1/admin/stats/growth` | Admin |
| PUT | `/api/v1/admin/users/{userId}/ban` | Admin |
| DELETE | `/api/v1/admin/posts/{postId}` | Admin |
| DELETE | `/api/v1/admin/comments/{commentId}` | Admin |
| GET | `/api/v1/admin/users` | Admin |
| GET | `/api/v1/admin/users/{userId}` | Admin |
| GET | `/api/v1/admin/banned-keywords` | Admin |
| POST | `/api/v1/admin/banned-keywords` | Admin |
| DELETE | `/api/v1/admin/banned-keywords/{id}` | Admin |

## 4. Module count

| Module | REST endpoints |
|---|---:|
| Auth | 11 |
| Users | 21 |
| Posts | 9 |
| Interactions | 10 |
| Media | 4 |
| Albums | 7 |
| Explore | 3 |
| Search | 4 |
| Chat REST | 5 |
| Notifications | 5 |
| Portfolios | 6 |
| Bookings | 5 |
| Ratings | 4 |
| Reports | 3 |
| Admin | 10 |
| **Total `/api/v1` REST** | **107** |
