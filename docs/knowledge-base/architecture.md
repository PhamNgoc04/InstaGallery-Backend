# Sổ Tay Kiến Trúc Ktor (InstaGallery Backend)

> Nơi chứa những "Chân lý" (Source of Truth) về các quyết định Frame/Library của dự án. Không thay đổi trừ khi có lệnh Explicit từ Admin/User.

## 1. Hệ Sinh Thái Sinh Tồn

1. **Web Framework:** Ktor 3.0.0 (Netty Engine). Lí do: Gọn nhẹ, hỗ trợ Coroutine Native, không dính Annotations lằng nhằng như Spring Boot. Tuân thủ **BSRP** architecture tuyệt đối.
2. **Database Engine:** MySQL 8+ & Redis 7. 
3. **ORM:** Jetbrains Exposed 0.55+. Lí do: DSL Kotlin thân thiện, kiểm soát SQL tốt, Type-safe. Chối bỏ Hibernate/JPA.
4. **Connection Pool:** HikariCP. Quản lý 10 threads. Được khởi tạo 1 lần tại `DatabaseFactory.kt`.
5. **Dependency Injection:** Koin (Sẽ dùng cho việc inject các Repositories vào Services).
6. **Authentication:** JWT (JSON Web Token) kết hợp băm mật khẩu `jBCrypt`.

## 2. Các Tiêu Chuẩn Hiện Tại 
- Dữ liệu trả về Frontend BẮT BUỘC tuân theo class `ApiResponse.kt` tại `models/common/ApiResponse.kt`. JSON JSend Style.
- Toàn bộ Code Base tuân thủ `styleguide.md` và `ash-2.0-backend-comprehensive.md`.

## 3. Database Constraints
*(Xem chi tiết bảng tại `instagallery_db_analysis.md`)*
Nỗ lực tối đa hóa performance bằng việc **Denormalization** có chủ đích (VD: Lưu sẵn `like_count`, `follower_count` trong bảng `users` và `posts`). Chấp nhận tốn công Trigger/Cập nhật để đổi lấy sức mạnh Select.
