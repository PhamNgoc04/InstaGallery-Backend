# Hướng Dẫn Chạy Backend

Tài liệu này là danh sách kiểm tra (checklist) ngắn gọn để khởi chạy lại backend InstaGallery trên máy tính hiện tại.

## 1. Điều kiện cần có

- Docker Desktop đang mở
- JDK 17 đã được cài đặt
- Đang ở thư mục gốc của dự án: `D:\InstaGallery\instagallery-backend`

```powershell
cd /d D:\InstaGallery\instagallery-android-kotlin
gradlew.bat :app:installDebug

& "D:\Android\SDK\platform-tools\adb.exe" devices -l

# Chạy trên máy ảo và điện thoại thật
# Dùng trên máy thật (Trước khi chạy thì phải chạy đoạn này trên terminal):
D:\Android\SDK\platform-tools\adb.exe devices

# Máy Samsung
D:\Android\SDK\platform-tools\adb.exe -s R58N85Y4BMZ reverse tcp:8080 tcp:8080

# Máy Realme C33
D:\Android\SDK\platform-tools\adb.exe -s 2812292110EA14PS reverse tcp:8080 tcp:8080

# Lệnh cho Máy Ảo:
D:\Android\SDK\platform-tools\adb.exe -s emulator-5554 reverse tcp:8080 tcp:8080
```

## 2. Khởi chạy Backend trực tiếp trên Terminal (Khuyến nghị cho Antigravity / Local Dev)

Đây là cách **nhanh nhất**, khởi động chỉ trong 3-5 giây, tốn ít RAM và xem log trực tiếp trên cửa sổ Terminal của Antigravity mà không cần bật Docker Desktop.

### Điều kiện:
- Dịch vụ MySQL cục bộ (`MySQL80`) đang chạy trên cổng 3306 (mặc định đã chạy sẵn trên máy).
- **Cách kiểm tra và bật MySQL:**
  - 👉 **Nếu dùng Terminal CMD (Command Prompt):**
    ```cmd
    # Kiểm tra trạng thái:
    sc query MySQL80
    # Nếu trạng thái là STOPPED, bật lại bằng:
    net start MySQL80
    ```
    *(Lưu ý: Lệnh `Get-Service` là của PowerShell. Nếu gõ trong CMD sẽ bị lỗi `'Get-Service' is not recognized`).*
  - 👉 **Nếu dùng Terminal PowerShell:**
    ```powershell
    # Kiểm tra trạng thái:
    Get-Service MySQL80
    # Nếu Stopped, bật lại bằng:
    Start-Service MySQL80
    ```


### 2.1 Cấu hình JAVA_HOME (Bắt buộc nếu bị lỗi `JAVA_HOME is set to an invalid directory`):

Nếu terminal báo lỗi:
```text
ERROR: JAVA_HOME is set to an invalid directory: D:\Android\Android_Studio\jbr
```
Nguyên nhân do đường dẫn cũ không tồn tại. Thư mục JDK 21 thực tế trên máy bạn là:
`D:\Android\Android_Studio_2025\android-studio\jbr`

**Cách khắc phục:**
- **Nếu đang dùng CMD** (như trong terminal Antigravity mặc định):
  ```cmd
  set JAVA_HOME=D:\Android\Android_Studio_2025\android-studio\jbr
  ```
- **Nếu đang dùng PowerShell**:
  ```powershell
  $env:JAVA_HOME = "D:\Android\Android_Studio_2025\android-studio\jbr"
  ```
*(Hệ thống cũng đã lưu vĩnh viễn biến này vào tài khoản Windows của bạn, bạn có thể mở tab Terminal mới để nhận tự động).*

### 2.2 Biến môi trường trước khi chạy

`application.conf` không chứa mật khẩu hay JWT secret. Máy local có thể chạy thẳng nếu MySQL user là `root` và password là `123456789`. Muốn chỉ định rõ:

```powershell
$env:DB_PASSWORD = "123456789"
$env:JWT_SECRET = "mot-secret-dai-hon-32-ky-tu-chi-dung-local"
$env:EXPOSE_DEBUG_RESET_TOKEN = "true"
.\gradlew.bat run
```

`EXPOSE_DEBUG_RESET_TOKEN=true` chỉ dùng local để nhận mã đặt lại mật khẩu trong response. Server không in mã đó ra log. Không đặt `KTOR_ENV=production` trên máy dev nếu muốn server tự tạo bảng còn thiếu.

### 2.3 Các lệnh chạy Backend (Chỉ chọn 1 trong các lệnh tùy theo nhu cầu của bạn):

> 💡 **Lưu ý:** Bạn **KHÔNG cần chạy lần lượt**. Thông thường bạn chỉ cần chạy **duy nhất lệnh số 1** (hoặc số 2).

#### 👉 Dành cho Terminal CMD (Command Prompt):
- **Lựa chọn 1 (Chạy server thông thường - Khuyên dùng):**
  ```cmd
  gradlew.bat run
  ```
- **Lựa chọn 2 (Chạy server với Hot-reload - tự khởi động lại khi sửa code):**
  ```cmd
  gradlew.bat run -t
  ```
- **Lựa chọn 3 (Chỉ kiểm tra lỗi code, KHÔNG bật server):**
  ```cmd
  gradlew.bat build --no-daemon -x test
  ```

#### 👉 Dành cho Terminal PowerShell:
- **Lựa chọn 1 (Chạy server thông thường - Khuyên dùng):**
  ```powershell
  .\gradlew.bat run
  ```
- **Lựa chọn 2 (Chạy server với Hot-reload - tự khởi động lại khi sửa code):**
  ```powershell
  .\gradlew.bat run -t
  ```
- **Lựa chọn 3 (Chỉ kiểm tra lỗi code, KHÔNG bật server):**
  ```powershell
  .\gradlew.bat build --no-daemon -x test
  ```

> **Cách dừng server:** Nhấn `Ctrl + C` trên terminal.

### 2.4 Khắc phục lỗi thường gặp khi chạy Local:

#### 🔴 Lỗi 1: Trùng cổng 8080 (`Address already in use: bind` / `Task :run FAILED (exit value 1)`)
- **Dấu hiệu:** Chạy `gradlew.bat run` bị dừng với thông báo:
  ```text
  Exception in thread "main" java.net.BindException: Address already in use: bind
  ...
  Execution failed for task ':run'.
  > Process '...java.exe' finished with non-zero exit value 1
  ```
- **Nguyên nhân:** Có một tiến trình `java.exe` cũ của backend chưa tắt hẳn và đang chiếm giữ cổng 8080.
- **Cách sửa nhanh:**
  - **Trên CMD (nhanh nhất):**
    ```cmd
    kill_port.bat
    ```
    Hoặc lệnh một dòng tự động tìm và tắt PID:
    ```cmd
    for /f "tokens=5" %a in ('netstat -aon ^| findstr :8080') do taskkill /f /pid %a
    ```
  - **Trên PowerShell:**
    ```powershell
    .\kill_port.bat
    # Hoặc:
    Stop-Process -Id (Get-NetTCPConnection -LocalPort 8080).OwningProcess -Force
    ```

#### 🔴 Lỗi 2: `'Get-Service' is not recognized as an internal or external command`
- **Nguyên nhân:** Bạn đang gõ lệnh `Get-Service` trong cửa sổ **Command Prompt (CMD)**.
- **Cách sửa:**
  - Trong CMD: dùng lệnh `sc query MySQL80` (hoặc `net start MySQL80`).
  - Nếu muốn dùng `Get-Service`: chuyển terminal sang tab **PowerShell**.

---

## 3. Khởi chạy trọn gói bằng Docker Compose

Dùng khi bạn muốn môi trường cô lập trong Docker (chạy cả Backend, MySQL và Redis trong container):

```powershell
cd D:\InstaGallery\instagallery-backend

# Tắt MySQL local nếu đang chạy để tránh đụng cổng 3306 (chạy PowerShell Admin):
Stop-Service MySQL80

# Khởi chạy các container ngầm:
docker compose up -d

# Hoặc build lại image và chạy:
docker compose up --build -d
```

## 4. Các cổng kết nối sử dụng (Ports)

- Backend Ktor: `http://localhost:8080`
- MySQL (Local / Docker): `localhost:3306`
- Redis (Docker): `localhost:6379`

## 5. Kiểm tra Đăng nhập trên Postman (Auth Session)

**Endpoint:**
```http
POST http://localhost:8080/api/v1/auth/login
```

**Body JSON:**
```json
{
  "usernameOrEmail": "phamphuongthao@gmail.com",
  "password": "phamphuongthao123!"
}
```

**Lưu ý:**
- Điểm tiếp nhận API hỗ trợ tìm kiếm tài khoản đăng nhập trực tiếp qua Email hoặc Username.
- Tài khoản Admin: `admin@instagallery.com` / `Admin@123`
- Các tài khoản demo khác: mật khẩu là tên đăng nhập cộng `123!`. Ví dụ nhiếp ảnh gia Phạm Phương Thảo: `phamphuongthao@gmail.com` / `phamphuongthao123!`

## 6. Quy trình Đăng ký tài khoản mới (Register account)

Nếu chưa khởi tạo tài khoản, có thể đăng ký tại:

**Endpoint:**
```http
POST http://localhost:8080/api/v1/auth/register
```

**Body JSON:**
```json
{
  "email": "phamngoc.demo@gmail.com",
  "username": "phamngocdemo",
  "password": "phamngocdemo123!",
  "fullName": "Phạm Ngọc",
  "userType": "CLIENT"
}
```

## 7. Khởi tạo dữ liệu mẫu (Database Seeding)

### Cách 1: Nạp vào MySQL cục bộ (Local MySQL Server 8.0)

Chạy sau khi backend đã khởi động một lần để Exposed tạo bảng. User local là `root`, password `123456789`, database `instagallery`. Bộ demo có 250 user (id 1–250). Admin dùng `Admin@123`. User khác dùng tên đăng nhập cộng `123!`.

Giữ đúng thứ tự file trong `docs_backend\dulieu_database`:

```powershell
$mysql = "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
$files = @(
  "docs_backend\dulieu_database\demo_fill_user_ids_1_100_seed.sql",
  "docs_backend\dulieu_database\mobile_search_booking_seed.sql",
  "docs_backend\dulieu_database\demo_more_posts_comments_seed.sql",
  "docs_backend\dulieu_database\demo_july_2026_new_posts_seed.sql",
  "docs_backend\dulieu_database\seed_posts_user_ngoc_thao.sql",
  "docs_backend\dulieu_database\seed_user_150.sql",
  "docs_backend\dulieu_database\app_demo_full_seed.sql",
  "docs_backend\dulieu_database\update_services.sql"
)
foreach ($file in $files) {
  cmd /c "`"$mysql`" --default-character-set=utf8mb4 -uroot -p123456789 instagallery < $file"
}
```

`database_seed.sql` ở thư mục gốc chỉ là bộ mẫu nhỏ, không thay bộ 8 file trên.

### Cách 2: Nạp vào Docker MySQL

Chỉ dùng khi backend chạy trong Docker. User trong container là `ig_user`, không phải `root` của MySQL cài trên máy.

---

## 8. Chẩn đoán nhanh sự cố (Troubleshooting)

### Backend không khởi chạy thành công
Xem dòng ghi nhật ký (logs) của container backend:
```powershell
docker compose logs backend --tail 100
```

### Cơ sở dữ liệu MySQL chưa sẵn sàng nhận kết nối
Tra cứu log của container MySQL:
```powershell
docker logs instagallery-backend-mysql-1 --tail 50
```
Khi nhật ký xuất hiện dòng thông báo `ready for connections`, cơ sở dữ liệu đã sẵn sàng hoạt động.

### Kiểm tra các container hiện hành
```powershell
docker-compose ps
```

## 9. Dừng hệ thống

Khi cần dừng toàn bộ container hệ thống:
```powershell
docker-compose down
```

Nếu muốn dọn dẹp sạch toàn bộ dữ liệu lưu trữ local của MySQL (xóa Volume):
```powershell
docker-compose down -v
```

> [!NOTE]
> Lệnh dừng kèm tham số `-v` ở trên không xóa thư mục `uploads` vì thư mục này sử dụng cơ chế bind mount. Chỉ xóa thư mục `uploads` thủ công khi bạn thực sự muốn làm sạch toàn bộ ảnh/video demo.
