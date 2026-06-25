# InstaGallery - AI Context & API Integration Guide for Android

File nay la context prompt cho Android developer/AI assistant. Noi dung da dong bo voi source Ktor hien tai.

## 1. Network configuration

### Base URL

- Android Emulator: `http://10.0.2.2:8080`
- Physical device cung WiFi: `http://<IP-MAY-TINH>:8080`
- API base path: `/api/v1`
- WebSocket chat: `ws://10.0.2.2:8080/api/v1/ws/chat?token=<JWT>`

### Cleartext HTTP

Moi truong local chua co HTTPS. Android 9+ can khai bao trong `AndroidManifest.xml`:

```xml
<application
    ...
    android:usesCleartextTraffic="true">
```

### Localhost image trap

Neu backend tra link mock dang `http://localhost/...` hoac `http://127.0.0.1/...`, Android Emulator phai map ve `10.0.2.2` truoc khi dua vao Coil/AsyncImage.

## 2. Current backend metrics

- Database tables: **30**.
- REST endpoints under `/api/v1`: **107**.
- System/debug HTTP endpoints outside `/api/v1`: **5**.
- Total HTTP endpoints: **112**.
- WebSocket endpoints: **1**.
- Total including WebSocket: **113**.

Source of truth:

- API list: [Backend_APIs.md](Backend_APIs.md)
- Database schema: [database_schema.md](database_schema.md)

## 3. Auth integration rules

- Public endpoints: login/register/forgot password/reset password/google/2FA verify/explore/search public/profile public/ratings public.
- Protected endpoints: gui `Authorization: Bearer <accessToken>`.
- Refresh flow: goi `POST /api/v1/auth/refresh`.
- Neu `/auth/refresh` cung tra `401`, clear local token va dua user ve login.
- Logout co the can them `X-Refresh-Token`.
- WebSocket chat truyen token bang query param: `/api/v1/ws/chat?token=<JWT>`.

## 4. Response wrapper

```kotlin
@Serializable
data class BaseResponse<T>(
    val status: String,
    val message: String? = null,
    val data: T? = null,
    val errorCode: String? = null
)
```

## 5. Core DTO hints

```kotlin
@Serializable
data class User(
    val id: Long,
    val username: String,
    val email: String,
    val fullName: String,
    val profilePictureUrl: String? = null,
    val bio: String? = null,
    val role: String,
    val userType: String? = null,
    val isVerified: Boolean = false,
    val followerCount: Int = 0,
    val followingCount: Int = 0,
    val postCount: Int = 0
)

@Serializable
data class Post(
    val id: Long,
    val userId: Long,
    val caption: String? = null,
    val location: String? = null,
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val shareCount: Int = 0,
    val createdAt: String? = null
)

@Serializable
data class Conversation(
    val id: Long,
    val title: String? = null,
    val type: String,
    val lastMessage: Message? = null
)

@Serializable
data class Message(
    val id: Long,
    val conversationId: Long,
    val senderId: Long,
    val content: String? = null,
    val messageType: String = "TEXT",
    val createdAt: String? = null
)
```

## 6. Current API map

Dung `BASE_URL = http://10.0.2.2:8080/api/v1`. Cac endpoint ben duoi khong lap lai tien to `/api/v1`.

### Auth

| Method | Endpoint | Auth |
|---|---|:---:|
| POST | `/auth/register` | No |
| POST | `/auth/login` | No |
| POST | `/auth/refresh` | No |
| POST | `/auth/forgot-password` | No |
| POST | `/auth/reset-password` | No |
| POST | `/auth/google` | No |
| POST | `/auth/2fa/verify-login` | No |
| POST | `/auth/logout` | Yes |
| PUT | `/auth/change-password` | Yes |
| POST | `/auth/2fa/setup` | Yes |
| POST | `/auth/2fa/enable` | Yes |

### Users

| Method | Endpoint | Auth |
|---|---|:---:|
| GET | `/users/me` | Yes |
| PUT | `/users/me` | Yes |
| GET | `/users/me/sessions` | Yes |
| DELETE | `/users/me/sessions/{id}` | Yes |
| POST | `/users/me/deactivate` | Yes |
| PUT | `/users/me/avatar` | Yes |
| PUT | `/users/me/privacy` | Yes |
| GET | `/users/me/saved-posts` | Yes |
| GET | `/users/me/liked-posts` | Yes |
| GET | `/users/me/tagged-posts` | Yes |
| GET | `/users/me/activity-log` | Yes |
| GET | `/users/me/blocked` | Yes |
| GET | `/users/suggestions` | Yes |
| GET | `/users/me/follow-requests` | Yes |
| POST | `/users/me/follow-requests/{followerId}/{action}` | Yes |
| GET | `/users/{id}` | No |
| POST | `/users/{id}/follow` | Yes |
| POST | `/users/{id}/block` | Yes |
| POST | `/users/{id}/mute` | Yes |
| GET | `/users/{id}/followers` | No |
| GET | `/users/{id}/following` | No |

### Posts, media and interactions

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
| POST | `/media/presigned-url` | Yes |
| POST | `/posts/{postId}/media` | Yes |
| DELETE | `/posts/media/{mediaId}` | Yes |
| PUT | `/posts/{postId}/media/reorder` | Yes |

### Albums, explore and search

| Method | Endpoint | Auth |
|---|---|:---:|
| POST | `/albums` | Yes |
| GET | `/albums` | Yes |
| GET | `/albums/{id}` | Yes |
| PUT | `/albums/{id}` | Yes |
| DELETE | `/albums/{id}` | Yes |
| POST | `/albums/{id}/media` | Yes |
| DELETE | `/albums/{id}/media/{mediaId}` | Yes |
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

### Chat, notifications, reports and admin

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

## 7. Prompt mau cho AI Android

> Toi dang xay dung Android app cho InstaGallery. Backend Ktor hien co 30 bang, 107 REST endpoints duoi `/api/v1`, 5 system HTTP routes, va 1 WebSocket `/api/v1/ws/chat`. Hay tao Network Module bang Ktor Client/Koin, xu ly Bearer token + refresh token, dung `10.0.2.2` cho emulator, va chi sinh API service theo endpoint map trong file nay.
