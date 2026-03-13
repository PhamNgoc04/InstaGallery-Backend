---
description: Quy trình Build & Run cho Ktor
---
# Ktor Build & Test Protocol

> Tính năng: Chạy an toàn và xác nhận module Ktor đã được viết đúng cú pháp.

Các lệnh chuẩn (Phải chạy ở thư mục gốc chứa `build.gradle.kts`):

1. **Kiểm tra Cú pháp (Compile Check):**
`./gradlew build --no-daemon -x test`
*(Chạy cái này trước mỗi khi bạn tạo xong 1 loạt các Model hoặc Route mới để chắc chắn Kotlin không báo đỏ lỗi import hay Type mismatch).*

2. **Chạy Ktor cục bộ (Dev Server):**
`./gradlew run`
*(Ktor sẽ mặc định khởi chạy ở port 8080 theo `application.conf`)*

3. **Gặp lỗi Port xung đột (Error 98):**
Chạy `netstat -ano | findstr :8080` (trên Windows) để tìm PID đang giữ port, sau đó báo cho User để họ kill. Khuyến cáo không tự tiện dùng `taskkill` do vi phạm an toàn.
