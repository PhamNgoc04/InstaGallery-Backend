# 09. Kỹ năng và kỹ thuật backend đang dùng

## Kotlin
- `data class`, `object`, enum
- nullable handling
- extension style code
- coroutine-friendly repository/service

## Ktor
- `Application.module()`
- plugin configuration
- routing REST
- JWT authentication
- `StatusPages`
- WebSocket
- CORS
- rate limiting

## Gradle Kotlin DSL
- quản lý dependency bằng `build.gradle.kts`
- plugin Kotlin/Ktor/shadow
- test task với JUnit Platform

## JWT
- tạo access token bằng HMAC256
- validate token qua Ktor auth
- dùng claim `userId`, `email`, `role`

## Session management
- refresh token theo thiết bị
- lưu DB
- logout từng session hoặc toàn bộ session

## Password hashing
- BCrypt cost 12
- verify hash khi login/change password

## Database
- Exposed table DSL
- Exposed transaction
- HikariCP pool
- MySQL JDBC

## Repository pattern
- mỗi domain có repository riêng
- tách query ra khỏi route

## REST API design
- versioning `/api/v1`
- chia endpoint theo domain
- dùng method đúng tương đối: GET/POST/PUT/DELETE

## Pagination
- feed, comment, booking, notification, rating, follow list
- dùng `page`, `limit`
- có `meta`

## Realtime backend
- WebSocket chat
- connection manager
- push event `NEW_MESSAGE`

## Config management
- `application.conf`
- env override cho JWT và database

## Logging và error handling
- logback
- status pages
- response wrapper chuẩn

## Testing
- unit test service
- integration test route
- H2 test DB
- MockK

## Kỹ thuật học được từ project này
- xây backend nhiều module trong Ktor
- tổ chức theo layered architecture
- kết hợp auth + refresh token + session DB
- làm CRUD + pagination + moderation + realtime trong cùng một project
