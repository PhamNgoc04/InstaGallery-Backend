# 05. Auth và Security

## Cơ chế auth hiện tại

### Access token
- JWT được cấu hình trong [plugins/Security.kt](/d:/InstaGallery/instagallery-backend/src/main/kotlin/com/instagallery/plugins/Security.kt:11)
- Claim chính:
  - `userId`
  - `email`
  - `role`
- Tạo token trong [utils/JwtManager.kt](/d:/InstaGallery/instagallery-backend/src/main/kotlin/com/instagallery/utils/JwtManager.kt:15)

### Refresh token
- Là UUID random.
- Được lưu vào `user_sessions`.
- Dùng cho:
  - refresh access token
  - logout theo session
  - quản lý multi-device

### Password hashing
- Dùng BCrypt cost 12 trong [PasswordHasher.kt](/d:/InstaGallery/instagallery-backend/src/main/kotlin/com/instagallery/utils/PasswordHasher.kt:5)
- Đây là lựa chọn tốt.

## Quy trình login/register

### Register
- Validate email/password
- Check email/username duplicate
- Hash password
- Insert user
- Tạo access token + refresh token
- Lưu session

### Login
- Tìm user theo email
- Check `isActive`
- Verify password hash
- Tạo access token + refresh token mới
- Lưu session

## Điểm làm tốt
- Có refresh token riêng.
- Có invalidate session khi đổi mật khẩu/reset mật khẩu.
- Có list session và revoke từng session.
- Có thiết kế chỗ cho OAuth/2FA dù chưa hoàn thiện.

## Lỗ hổng và vấn đề nghiêm trọng

## 1. `UserDto` lộ `passwordHash`
Nguồn:
- [UserDto.kt](/d:/InstaGallery/instagallery-backend/src/main/kotlin/com/instagallery/models/common/UserDto.kt:6)
- [UserRepository.kt](/d:/InstaGallery/instagallery-backend/src/main/kotlin/com/instagallery/repositories/UserRepository.kt:66)

Vấn đề:
- `passwordHash` đang nằm trong DTO có thể trả ra API.
- Các API như `/users/me`, `/users/{id}` đang có nguy cơ trả hash mật khẩu cho client.

Hướng sửa:
- Tách `UserEntity/InternalUser` và `UserResponse`.
- Không bao giờ serialize password hash ra ngoài.

Ví dụ:

```kotlin
@Serializable
data class UserResponse(
    val id: Long,
    val username: String,
    val email: String,
    val fullName: String,
    val profilePictureUrl: String?,
    val role: Role,
    val userType: UserType,
    val isActive: Boolean,
    val isVerified: Boolean
)
```

## 2. WebSocket auth chỉ `decode` token, không verify
Nguồn: [ChatRoutes.kt](/d:/InstaGallery/instagallery-backend/src/main/kotlin/com/instagallery/routes/ChatRoutes.kt:104)

Hiện tại:

```kotlin
val decodedJwt = try { JWT.decode(token) } catch (e: Exception) { null }
```

`decode` chỉ đọc payload, không kiểm tra:
- chữ ký
- issuer
- audience
- expiration

Nghĩa là token giả mạo có thể qua cửa nếu format đúng.

Hướng sửa:
- Dùng `JwtManager.verifyTokenSync(token)`

Ví dụ:

```kotlin
val jwtManager = application.getKoin().get<JwtManager>()
val verified = jwtManager.verifyTokenSync(token)
    ?: return@webSocket close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Token invalid"))
val userId = verified.getClaim("userId").asLong()
```

## 3. Search optional auth cũng chỉ `decode`
Nguồn: [SearchRoutes.kt](/d:/InstaGallery/instagallery-backend/src/main/kotlin/com/instagallery/routes/SearchRoutes.kt:26)

Rủi ro giống WebSocket: client có thể giả token để gán lịch sử tìm kiếm cho user khác.

## 4. Secret JWT và DB password bị hardcode fallback
Nguồn:
- [application.conf](/d:/InstaGallery/instagallery-backend/src/main/resources/application.conf:10)
- [DatabaseFactory.kt](/d:/InstaGallery/instagallery-backend/src/main/kotlin/com/instagallery/database/DatabaseFactory.kt:17)

Vấn đề:
- Nếu môi trường thiếu env, app vẫn chạy với secret mặc định dễ đoán.

## 5. CORS đang `anyHost()`
Nguồn: [plugins/CORS.kt](/d:/InstaGallery/instagallery-backend/src/main/kotlin/com/instagallery/plugins/CORS.kt:8)

Đây là cấu hình chỉ nên dùng local.

## 6. Ban user chưa thật sự khóa đăng nhập
Nguồn: [AdminRepository.kt](/d:/InstaGallery/instagallery-backend/src/main/kotlin/com/instagallery/repositories/AdminRepository.kt:76)

Hiện tại admin ban user bằng cách set `deletedAt`, nhưng auth login lại check `isActive`, không check `deletedAt`.
Kết quả:
- user bị “ban” vẫn có thể đăng nhập nếu `isActive = true`.

Hướng sửa:
- Dùng cột `isActive` hoặc tạo `isBanned`, `banReason`, `bannedAt`.
- Tất cả query user công khai cũng nên lọc `deletedAt.isNull()`.

## 7. Route DB công khai là rủi ro bảo mật cấp cao
Nguồn: [plugins/Routing.kt](/d:/InstaGallery/instagallery-backend/src/main/kotlin/com/instagallery/plugins/Routing.kt:29)

## Mức độ sẵn sàng production
- Password hashing: khá ổn
- JWT chuẩn HTTP: khá ổn
- Session refresh: tốt
- Secret management: chưa ổn
- WebSocket security: chưa ổn
- Exposure bề mặt tấn công: chưa ổn

## Kết luận
Auth phần cơ bản làm đúng hướng, nhưng security tổng thể vẫn chưa đủ an toàn cho production vì còn lộ hash, verify token chưa đúng ở một số luồng và còn endpoint phá hủy dữ liệu mở công khai.
