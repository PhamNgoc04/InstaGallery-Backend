# InstaGallery Error Log (Sổ Tay Gỡ Rỗi)

> Nơi đúc kết kinh nghiệm xương máu của AI và User. Gặp lỗi -> Tìm cách giải quyết -> Ghi vào đây để không bao giờ vấp lại lần 2.

### 📅 YYYY-MM-DD | Lỗi Ví Dụ Mẫu
- **Hiện tượng:** Ktor Exception lúc parse JSON HTTP POST Body. `Cannot load plugin...`
- **Nguyên nhân:** Quên cài plugin Serialization `install(ContentNegotiation) { json() }` trong file Route hoặc Application.
- **Cách khắc phục:** Đã gắn block Content Negotiation ở Plugin layer. Từ nay mọi Route POST đều tự động serialize data class sang JSON.

*(Khối lỗi tiếp theo sẽ được bổ sung vào đây tự động)*

### 📅 2026-03-13 | Mismatch Plugin Capability Shadow Gradle 9
- **Hiện tượng:** Gradle 9 báo lỗi `Cannot select module with conflict on capability io.github.goooler.shadow` hoặc `Could not create task ':startShadowScripts' ... propertyName=mainClassName`.
- **Nguyên nhân:** Ktor 3.0.2 sử dụng `com.gradleup.shadow:8.3.5` ngầm. Tuy nhiên, build process lại báo lỗi mất thuộc tính `mainClassName` ở phase `startShadowScripts` do Gradle 9 đã xóa thuộc tính này trên Project object.
- **Cách khắc phục:** Ta sử dụng đúng plugin mặc định của Ktor 3. Nhưng add thêm scope thủ công để bơm trực tiếp thuộc tính này vào task đóng gói:
```kotlin
tasks.named("startShadowScripts") {
    setProperty("mainClassName", "io.ktor.server.netty.EngineMain")
}
```
