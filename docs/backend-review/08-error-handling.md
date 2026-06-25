# 08. Error handling

## Cơ chế đang dùng
Project dùng `StatusPages` trong [plugins/StatusPages.kt](/d:/InstaGallery/instagallery-backend/src/main/kotlin/com/instagallery/plugins/StatusPages.kt:11).

Các exception custom:
- `AuthException`
- `ValidationException`
- `NotFoundException`
- `ForbiddenException`

Mapping hiện tại:
- `AuthException` -> `401 Unauthorized`
- `ValidationException` -> `400 Bad Request`
- `NotFoundException` -> `404`
- `ForbiddenException` -> `403`
- `Throwable` -> `500`

## Điểm tốt
- Có một nơi tập trung để map exception -> response.
- Format lỗi thống nhất qua `ApiResponse.error(...)`.
- Có log lỗi unhandled.

## Vấn đề hiện tại

## 1. `AuthException` đang ôm quá nhiều meaning
Ví dụ:
- sai password
- user không tồn tại
- forbidden xem post private
- unauthorized action

Nhưng tất cả đều map về `401`.

Điều này chưa chuẩn HTTP:
- thiếu quyền trên resource nên là `403`
- resource không tồn tại nên là `404`
- dữ liệu sai nên là `400`

## 2. Vẫn còn nhiều `throw Exception(...)`
Ví dụ:
- [AuthService.kt](/d:/InstaGallery/instagallery-backend/src/main/kotlin/com/instagallery/services/AuthService.kt:45)
- [PostService.kt](/d:/InstaGallery/instagallery-backend/src/main/kotlin/com/instagallery/services/PostService.kt:30)
- [MediaService.kt](/d:/InstaGallery/instagallery-backend/src/main/kotlin/com/instagallery/services/MediaService.kt:50)
- [PortfolioService.kt](/d:/InstaGallery/instagallery-backend/src/main/kotlin/com/instagallery/services/PortfolioService.kt:51)

Hệ quả:
- bị đẩy thành `500 Internal Server Error`
- frontend khó phân biệt lỗi nghiệp vụ với lỗi hệ thống

## 3. Nhiều route tự `call.respond(...)` lỗi ngay trong route
Điều này không sai, nhưng làm logic lỗi bị phân tán.

Ví dụ:
- parse `id` hỏng thì route trả ngay `INVALID_ID`
- role admin check cũng trả ngay ở route

Nên chuẩn hóa hơn bằng helper hoặc exception.

## 4. Một số service/repository fail im lặng
Ví dụ `NotificationService.markAsRead()` không báo nếu ID không tồn tại hoặc không thuộc user.
Về UX có thể chấp nhận, nhưng nếu muốn API chặt thì nên trả rõ.

## Response format hiện tại

```json
{
  "status": "ERROR",
  "error": {
    "code": "INVALID_ID",
    "message": "ID bài viết không hợp lệ."
  }
}
```

Format này ổn và nên giữ.

## Gợi ý cải thiện

### Phân loại exception rõ hơn

```kotlin
sealed class AppException(message: String) : RuntimeException(message)
class BadRequestException(message: String) : AppException(message)
class UnauthorizedException(message: String) : AppException(message)
class ForbiddenException(message: String) : AppException(message)
class ResourceNotFoundException(message: String) : AppException(message)
class ConflictException(message: String) : AppException(message)
```

### Không dùng `Exception` chung chung cho lỗi nghiệp vụ
- thay bằng `ConflictException`, `ValidationException`, `NotFoundException`...

### Tạo helper lấy principal
- tránh lặp `principal?.payload?.getClaim("userId")?.asLong()`

### Dùng request validation plugin
- đưa lỗi body/query về một chuẩn thống nhất hơn

## Đánh giá chung
Error handling có nền tảng khá tốt, nhưng mới dừng ở mức “đủ chạy”. Muốn chuyên nghiệp hơn cần:
- phân loại exception chuẩn hơn
- bỏ `throw Exception`
- giảm lỗi trả thủ công rải rác ở route
