# InstaGallery API Collection - Frontend Spec

Tai lieu nay danh cho Mobile/Web frontend va da dong bo voi source Ktor hien tai.

## Base URLs

- Android Emulator: `http://10.0.2.2:8080/api/v1`
- Physical device cung WiFi: `http://<IP-MAY-TINH>:8080/api/v1`
- WebSocket Android Emulator: `ws://10.0.2.2:8080/api/v1/ws/chat?token=<JWT>`
- WebSocket physical device: `ws://<IP-MAY-TINH>:8080/api/v1/ws/chat?token=<JWT>`

## Project metrics

- Database tables: **30 tables**.
- REST API duoi `/api/v1`: **107 endpoints**.
- System/debug HTTP routes ngoai `/api/v1`: **5 endpoints**.
- Total HTTP endpoints: **112 endpoints**.
- WebSocket: **1 endpoint**.
- Total including WebSocket: **113 endpoints**.

> Full backend route list nam o [Backend_APIs.md](Backend_APIs.md). File nay tap trung vao endpoint frontend can goi va cac luu y tich hop.

## Response wrapper

Tat ca API nen duoc frontend parse theo wrapper chung:

```json
{
  "status": "SUCCESS",
  "message": "...",
  "data": {}
}
```

Khi co loi, backend tra ve `status = "ERROR"` va co the kem `errorCode`.

## Auth and token rules

- Public endpoints khong can `Authorization`.
- Protected endpoints gui header: `Authorization: Bearer <accessToken>`.
- Refresh token flow goi `POST /auth/refresh`.
- Mot so auth actions, vi du logout, co the can them header `X-Refresh-Token`.
- WebSocket chat truyen token qua query: `/ws/chat?token=<JWT>`.

## Endpoint map by frontend domain

### Auth - base `/auth`

| Method | Endpoint | Auth |
|---|---|:---:|
| POST | `/register` | No |
| POST | `/login` | No |
| POST | `/refresh` | No |
| POST | `/forgot-password` | No |
| POST | `/reset-password` | No |
| POST | `/google` | No |
| POST | `/2fa/verify-login` | No |
| POST | `/logout` | Yes |
| PUT | `/change-password` | Yes |
| POST | `/2fa/setup` | Yes |
| POST | `/2fa/enable` | Yes |

### Users - base `/users`

| Method | Endpoint | Auth |
|---|---|:---:|
| GET | `/me` | Yes |
| PUT | `/me` | Yes |
| GET | `/me/sessions` | Yes |
| DELETE | `/me/sessions/{id}` | Yes |
| POST | `/me/deactivate` | Yes |
| PUT | `/me/avatar` | Yes |
| PUT | `/me/privacy` | Yes |
| GET | `/me/saved-posts` | Yes |
| GET | `/me/liked-posts` | Yes |
| GET | `/me/tagged-posts` | Yes |
| GET | `/me/activity-log` | Yes |
| GET | `/me/blocked` | Yes |
| GET | `/suggestions` | Yes |
| GET | `/me/follow-requests` | Yes |
| POST | `/me/follow-requests/{followerId}/{action}` | Yes |
| GET | `/{id}` | No |
| POST | `/{id}/follow` | Yes |
| POST | `/{id}/block` | Yes |
| POST | `/{id}/mute` | Yes |
| GET | `/{id}/followers` | No |
| GET | `/{id}/following` | No |

### Posts and interactions

| Method | Endpoint | Auth |
|---|---|:---:|
| POST | `/posts` | Yes |
| GET | `/posts/feed` | Yes |
| GET | `/posts/{id}` | Yes |
| PUT | `/posts/{id}` | Yes |
| DELETE | `/posts/{id}` | Yes |
| GET | `/posts/users/{userId}/posts` | Yes |
| POST | `/posts/{id}/tags` | Yes |
| DELETE | `/posts/{id}/tags/{taggedUserId}` | Yes |
| PUT | `/posts/{id}/comment-settings` | Yes |
| POST | `/posts/{id}/like` | Yes |
| GET | `/posts/{id}/likes` | Yes |
| POST | `/posts/{id}/save` | Yes |
| POST | `/posts/{id}/comments` | Yes |
| GET | `/posts/{id}/comments` | Yes |
| POST | `/posts/{id}/share` | Yes |
| GET | `/posts/{id}/shares` | Yes |
| PUT | `/comments/{commentId}` | Yes |
| DELETE | `/comments/{commentId}` | Yes |
| POST | `/comments/{commentId}/like` | Yes |

### Media and albums

| Method | Endpoint | Auth |
|---|---|:---:|
| POST | `/media/presigned-url` | Yes |
| POST | `/posts/{postId}/media` | Yes |
| DELETE | `/posts/media/{mediaId}` | Yes |
| PUT | `/posts/{postId}/media/reorder` | Yes |
| POST | `/albums` | Yes |
| GET | `/albums` | Yes |
| GET | `/albums/{id}` | Yes |
| PUT | `/albums/{id}` | Yes |
| DELETE | `/albums/{id}` | Yes |
| POST | `/albums/{id}/media` | Yes |
| DELETE | `/albums/{id}/media/{mediaId}` | Yes |

### Explore and search

| Method | Endpoint | Auth |
|---|---|:---:|
| GET | `/explore` | No |
| GET | `/explore/trending` | No |
| GET | `/explore/hashtags/{tag}` | No |
| GET | `/search` | Optional |
| GET | `/search/history` | Yes |
| DELETE | `/search/history` | Yes |
| GET | `/search/trending` | No |

### Portfolio, booking and rating

| Method | Endpoint | Auth |
|---|---|:---:|
| GET | `/portfolios` | No |
| GET | `/portfolios/users/{userId}` | No |
| GET | `/portfolios/me` | Yes |
| PUT | `/portfolios/me` | Yes |
| POST | `/portfolios/me/availability` | Yes |
| GET | `/portfolios/me/availability` | Yes |
| POST | `/bookings` | Yes |
| GET | `/bookings` | Yes |
| GET | `/bookings/{id}` | Yes |
| PUT | `/bookings/{id}/status` | Yes |
| DELETE | `/bookings/{id}` | Yes |
| GET | `/users/{photographerId}/ratings` | No |
| POST | `/users/{photographerId}/ratings` | Yes |
| DELETE | `/ratings/{ratingId}` | Yes |
| PUT | `/ratings/{ratingId}` | Yes |

### Chat and notifications

| Method | Endpoint | Auth |
|---|---|:---:|
| POST | `/chat/conversations` | Yes |
| GET | `/chat/conversations` | Yes |
| GET | `/chat/conversations/{id}/messages` | Yes |
| POST | `/chat/conversations/{id}/messages` | Yes |
| DELETE | `/chat/conversations/{id}` | Yes |
| WS | `/ws/chat?token=<JWT>` | Token query |
| GET | `/notifications` | Yes |
| GET | `/notifications/unread-count` | Yes |
| PUT | `/notifications/{id}/read` | Yes |
| PUT | `/notifications/read-all` | Yes |
| DELETE | `/notifications/{id}` | Yes |

### Reports and admin

| Method | Endpoint | Auth |
|---|---|:---:|
| POST | `/reports` | Yes |
| GET | `/admin/reports` | Admin |
| PUT | `/admin/reports/{reportId}` | Admin |
| GET | `/admin/stats` | Admin |
| GET | `/admin/stats/growth` | Admin |
| PUT | `/admin/users/{userId}/ban` | Admin |
| DELETE | `/admin/posts/{postId}` | Admin |
| DELETE | `/admin/comments/{commentId}` | Admin |
| GET | `/admin/users` | Admin |
| GET | `/admin/users/{userId}` | Admin |
| GET | `/admin/banned-keywords` | Admin |
| POST | `/admin/banned-keywords` | Admin |
| DELETE | `/admin/banned-keywords/{id}` | Admin |

## System routes

Nhung route nay khong nam trong base `/api/v1` va khong nen goi tu app production:

| Method | Endpoint |
|---|---|
| GET | `/health` |
| GET | `/init-db` |
| GET | `/reset-db` |
| GET | `/fix-user-id` |
| GET | `/migrate-db` |
