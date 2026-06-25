# 12. Chạy và debug project

## Cách chạy backend

### Cách 1: chạy bằng Gradle wrapper
Từ README hiện tại, lệnh chính là:

```powershell
.\gradlew.bat run
```

### Cách 2: chạy test

```powershell
.\gradlew.bat test
```

Trong môi trường review này, `test` hiện chạy thành công.

## Chuẩn bị database

### Cấu hình trong `application.conf`
- `DB_URL`
- `DB_USER`
- `DB_PASSWORD`
- `JWT_SECRET`

Ví dụ local:

```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/instagallery?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC"
$env:DB_USER="root"
$env:DB_PASSWORD="123456789"
$env:JWT_SECRET="your-strong-secret"
.\gradlew.bat run
```

### Nếu dùng Docker Compose
Project có `docker-compose.yml`, nên kiểm tra trước:

```powershell
docker compose up -d
```

Sau đó chạy backend.

## API test nhanh

### Health check

```http
GET http://localhost:8080/health
```

### Register

```http
POST http://localhost:8080/api/v1/auth/register
Content-Type: application/json

{
  "email": "demo@example.com",
  "username": "demo",
  "password": "12345678",
  "fullName": "Demo User",
  "userType": "CLIENT"
}
```

### Login

```http
POST http://localhost:8080/api/v1/auth/login
Content-Type: application/json

{
  "usernameOrEmail": "demo@example.com",
  "password": "12345678"
}
```

## Dùng Postman/Insomnia
Project đã có file:
- `InstaGallery_Local.postman_collection.json`

Bạn có thể import trực tiếp để test nhanh hơn.

## Debug lỗi thường gặp

## 1. Không kết nối được MySQL
Triệu chứng:
- app crash lúc startup
- lỗi JDBC/Hikari

Kiểm tra:
- MySQL đã chạy chưa
- đúng `DB_URL`, `DB_USER`, `DB_PASSWORD` chưa
- database `instagallery` đã tồn tại chưa

## 2. JWT luôn báo invalid
Kiểm tra:
- `JWT_SECRET` giữa lúc generate và verify có cùng giá trị không
- token có bị cắt mất prefix `Bearer ` không
- token đã hết hạn chưa

## 3. Route protected trả 401
Kiểm tra:
- có gửi `Authorization: Bearer <token>` không
- route có cần thêm `X-Refresh-Token` không, ví dụ logout/session

## 4. Lỗi body parse
Một số route đang parse body thủ công bằng `receiveText()`:
- privacy
- comment settings
- chat create conversation
- chat send message
- admin banned keyword

Nếu gửi sai key JSON, route sẽ lỗi `MISSING_FIELD` hoặc parse exception.

## 5. API có endpoint nhưng trả dữ liệu rỗng
Đây có thể không phải bug runtime mà là TODO trong code, ví dụ:
- saved posts
- liked posts
- tagged posts
- activity log
- blocked users
- follow requests
- albums
- banned keywords admin

## Kiểm tra database trực tiếp
Bạn có thể kiểm tra nhanh bằng MySQL client:

```sql
SELECT * FROM users;
SELECT * FROM user_sessions;
SELECT * FROM posts;
SELECT * FROM bookings;
SELECT * FROM notifications;
```

## Lưu ý khi debug local
- Không dùng các route `/reset-db` hoặc `/fix-user-id` trên môi trường có dữ liệu thật.
- `SchemaUtils.create(...)` sẽ tự tạo bảng khi app lên, nhưng không thay thế migration chuẩn.
- Nếu thấy response tiếng Việt bị lỗi encoding, kiểm tra terminal/editor encoding trước khi nghi ngờ logic.

## Quy trình debug khuyên dùng

1. Xác nhận config env
2. Chạy `.\gradlew.bat test`
3. Chạy `.\gradlew.bat run`
4. Test `GET /health`
5. Register -> login -> copy JWT
6. Test dần từng module
7. Nếu lỗi logic, lần theo `route -> service -> repository -> table`

## Gợi ý nâng cấp trải nghiệm debug
- thêm OpenAPI/Swagger
- thêm request logging
- thêm SQL logging có chọn lọc
- thêm profile `dev` và `prod`
