# 11. Hướng dẫn học từ project này

## Nên học gì trước

1. Kotlin cơ bản
2. HTTP và REST API
3. JWT là gì
4. SQL cơ bản và quan hệ bảng
5. Ktor routing/plugin cơ bản
6. Exposed table/query cơ bản

## Thứ tự học nên đi

### Bước 1: vào từ `Application.kt`
Mục tiêu:
- hiểu app khởi động thế nào
- plugin nào được cài trước plugin nào

### Bước 2: đọc `application.conf`
Mục tiêu:
- hiểu config JWT/DB
- hiểu env override

### Bước 3: đọc `plugins/`
Nên theo thứ tự:
1. `Serialization.kt`
2. `Security.kt`
3. `StatusPages.kt`
4. `DependencyInjection.kt`
5. `Routing.kt`

### Bước 4: đọc `models/`
Mục tiêu:
- phân biệt request, response, common DTO
- đọc `Enums.kt` trước để hiểu domain

### Bước 5: chọn 1 flow hoàn chỉnh để học xuyên tầng
Nên học theo thứ tự này:

1. `register/login`
2. `get me/update profile`
3. `create post/get feed`
4. `like/comment`
5. `booking`

Với mỗi flow, đi theo đường:

```text
route -> service -> repository -> table
```

## File nào nên học kỹ

### `AuthService.kt`
Học:
- validation
- hash password
- token/session flow

### `PostRepository.kt`
Học:
- insert parent-child
- pagination
- join nhiều bảng

### `InteractionRepository.kt`
Học:
- toggle pattern
- comment tree
- counter update

### `BookingService.kt`
Học:
- business rule
- state machine đơn giản
- authorization theo vai trò nghiệp vụ

### `StatusPages.kt`
Học:
- exception handling tập trung

## Cách đọc hiệu quả cho người mới

### Không đọc hết một lượt
Thay vào đó, chọn 1 API rồi lần:
- route
- request DTO
- service
- repository
- table
- response DTO

### Ví dụ nên thực hành
- `POST /api/v1/auth/register`
- `GET /api/v1/users/me`
- `POST /api/v1/posts`
- `POST /api/v1/posts/{id}/like`
- `POST /api/v1/bookings`

## Cần lưu ý khi học từ project này
- Đây là project học tập/prototype khá thật, không phải production mẫu hoàn hảo.
- Vì vậy nên học cả:
  - cái đang làm đúng
  - và cả lỗi thiết kế/bảo mật để tránh lặp lại sau này

## Bài học quan trọng rút ra
- Tách tầng giúp project lớn vẫn đọc được.
- JWT chỉ là một phần của auth; session refresh token cũng rất quan trọng.
- DTO công khai phải tách khỏi dữ liệu nhạy cảm.
- Nếu route public mà repo còn TODO thì API contract sẽ nhanh chóng bị vỡ.

## Lộ trình học đề xuất trong 7 ngày

1. Ngày 1: `Application.kt`, plugin, config
2. Ngày 2: models + tables
3. Ngày 3: auth flow
4. Ngày 4: user + post flow
5. Ngày 5: interaction + notification + search
6. Ngày 6: booking + portfolio + rating
7. Ngày 7: admin + report + review lại các điểm chưa tốt
