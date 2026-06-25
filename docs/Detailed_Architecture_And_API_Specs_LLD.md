# Detailed Architecture and API Specs LLD

**Project:** InstaGallery Backend System
**Framework:** Ktor 3.0.2 / Kotlin 2.0.0 / Koin / Exposed SQL
**Updated:** 2026-06-25
**Purpose:** Low-level architecture reference for Android/Web integration.

> Full endpoint source of truth: [Backend_APIs.md](Backend_APIs.md)
> Full database source of truth: [database_schema.md](database_schema.md)

---

## 1. Architecture Model

Backend hien tai la **Layered Modular Monolith** theo luong:

```text
Route -> Service -> Repository -> Database/Table
```

Trong do:

| Layer | Vai tro | Vi du |
|---|---|---|
| Ktor Plugins | Cai dat routing, auth, CORS, status pages, DI, DB, WebSocket | `plugins/Routing.kt`, `plugins/Security.kt` |
| Routes | Nhan request, parse path/query/body, check auth, goi service | `routes/AuthRoutes.kt`, `routes/PostRoutes.kt` |
| Services | Xu ly business logic va orchestration | `AuthService`, `PostService`, `AdminService` |
| Repositories | Truy cap database qua Exposed | `UserRepository`, `PostRepository`, `BookingRepository` |
| Database Tables | Khai bao schema Exposed | `database/tables/*Table.kt` |

Day co huong Clean Architecture nhung **chua phai Clean Architecture strict**, vi service/repository van phu thuoc truc tiep vao Exposed table va `DatabaseFactory`.

---

## 2. Current Metrics

| Scope | Count |
|---|---:|
| Database tables | 30 |
| REST endpoints under `/api/v1` | 107 |
| System/debug HTTP endpoints outside `/api/v1` | 5 |
| Total HTTP endpoints | 112 |
| WebSocket endpoints | 1 |
| Total including WebSocket | 113 |

---

## 3. Database Schema Overview

He thong RDBMS MySQL hien co **30 tables**, chia thanh cac nhom sau:

| Group | Tables |
|---|---|
| Auth & Identity | `users`, `user_sessions`, `password_reset_tokens` |
| Content & Media | `posts`, `post_media`, `filters`, `media_tags`, `post_media_tags` |
| Social Interactions | `likes`, `comment_likes`, `comments`, `saved_posts` |
| User Relations | `followers`, `follow_requests`, `blocked_users`, `muted_users` |
| Messaging | `conversations`, `conversation_members`, `messages` |
| Albums | `albums`, `album_media` |
| Photographer Business | `portfolios`, `availability_schedules`, `bookings`, `ratings` |
| System, Search & Moderation | `notifications`, `activity_logs`, `reports`, `search_histories`, `banned_words` |

Important notes:

- `users`, `posts`, `comments`, `albums` co soft delete fields.
- `posts`, `users`, `comments`, `media_tags`, `portfolios` co counter/cache fields.
- Messaging dung `conversations` + `conversation_members` + `messages`.
- Booking/rating nam trong nhom photographer marketplace.

---

## 4. API Endpoint Map

Base path chinh cho app la:

```text
http://<host>:8080/api/v1
```

### REST module breakdown

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

### System/debug routes

| Method | Endpoint | Note |
|---|---|---|
| GET | `/health` | Health check |
| GET | `/init-db` | Local/dev only |
| GET | `/reset-db` | Local/dev only, destructive |
| GET | `/fix-user-id` | Local/dev only |
| GET | `/migrate-db` | Local/dev only |

### WebSocket

| Method | Endpoint | Note |
|---|---|---|
| WS | `/api/v1/ws/chat?token=<JWT>` | Realtime chat |

---

## 5. High-Impact Endpoint Corrections

Mot so endpoint cu trong tai lieu truoc day **khong dung voi source hien tai**. Frontend nen dung cac endpoint duoi day:

| Use case | Correct endpoint |
|---|---|
| Home feed | `GET /api/v1/posts/feed` |
| User posts | `GET /api/v1/posts/users/{userId}/posts` |
| Presigned upload URL | `POST /api/v1/media/presigned-url` |
| Booking list | `GET /api/v1/bookings` |
| Booking status update | `PUT /api/v1/bookings/{id}/status` |
| Create rating | `POST /api/v1/users/{photographerId}/ratings` |
| Search trending | `GET /api/v1/search/trending` |
| Explore hashtag | `GET /api/v1/explore/hashtags/{tag}` |
| Chat messages | `GET /api/v1/chat/conversations/{id}/messages` |
| Send chat over REST | `POST /api/v1/chat/conversations/{id}/messages` |
| Admin users | `GET /api/v1/admin/users` |
| Admin reports | `GET /api/v1/admin/reports` |

Khong sinh client cho cac endpoint cu sau neu chi dua vao backend hien tai:

```text
GET /api/v1/posts
GET /api/v1/feed
GET /api/v1/photographers
GET /api/v1/search/autocomplete
GET /api/v1/tags/{tagName}/posts
POST /api/v1/ratings
GET /api/v1/bookings/me
POST /api/v1/upload/presigned-url
POST /api/v1/auth/verify-email
POST /api/v1/auth/resend-verification
```

---

## 6. Mobile Integration Notes

### Android emulator

```text
BASE_URL = "http://10.0.2.2:8080/api/v1"
WS_URL   = "ws://10.0.2.2:8080/api/v1/ws/chat?token=<JWT>"
```

### Physical device

```text
BASE_URL = "http://<LAPTOP_IPV4>:8080/api/v1"
WS_URL   = "ws://<LAPTOP_IPV4>:8080/api/v1/ws/chat?token=<JWT>"
```

Android 9+ local HTTP can khai bao:

```xml
<application
    ...
    android:usesCleartextTraffic="true">
```

### Token flow

1. Login/register nhan access token va refresh token.
2. Protected request gui `Authorization: Bearer <accessToken>`.
3. Khi gap `401`, goi `POST /api/v1/auth/refresh`.
4. Neu refresh fail, clear token va dua user ve login.
5. WebSocket chat gui JWT qua query param `token`.

### Localhost image trap

Neu backend tra URL dang `localhost` hoac `127.0.0.1`, Android Emulator khong tai duoc. Can map ve `10.0.2.2` hoac IP LAN cua laptop truoc khi dua vao Coil/AsyncImage.

---

## 7. Suggested Android Build Order

1. Auth: `/auth/register`, `/auth/login`, `/auth/refresh`, `/auth/logout`.
2. Profile: `/users/me`, `/users/{id}`, avatar/privacy/session routes.
3. Feed: `/posts/feed`, `/posts/{id}`, interactions like/save/comment.
4. Search/explore: `/explore`, `/explore/trending`, `/search`, `/search/history`.
5. Media/albums: `/media/presigned-url`, `/posts/{postId}/media`, `/albums`.
6. Booking/portfolio/rating: `/portfolios`, `/bookings`, `/users/{photographerId}/ratings`.
7. Chat: REST conversation endpoints + WebSocket `/ws/chat`.
8. Notifications and reports.
