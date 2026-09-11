# 🚀 InstaGallery Backend — Hướng Dẫn Cài Đặt & Chạy Dự Án

> **Tech Stack**: Ktor 3.0.2 · Kotlin 2.0.0 · MySQL 8.0 · Redis 7 · Gradle 8.10.2
>
> Thời gian cài đặt ước tính: **~15–20 phút** (lần đầu)

---

## 📑 Mục lục

1. [Yêu cầu môi trường](#1-yêu-cầu-môi-trường)
2. [Clone dự án](#2-clone-dự-án)
3. [Cài đặt MySQL & tạo database](#3-cài-đặt-mysql--tạo-database)
4. [Cấu hình ứng dụng](#4-cấu-hình-ứng-dụng)
5. [Chạy dự án bằng IntelliJ IDEA](#5-chạy-dự-án-bằng-intellij-idea)
6. [Chạy dự án bằng Command Line](#6-chạy-dự-án-bằng-command-line)
7. [Chạy bằng Docker Compose (Nhanh nhất)](#7-chạy-bằng-docker-compose-nhanh-nhất)
8. [Nạp dữ liệu mẫu (Seed)](#8-nạp-dữ-liệu-mẫu-seed)
9. [Kiểm tra server hoạt động](#9-kiểm-tra-server-hoạt-động)
10. [Xử lý lỗi thường gặp](#10-xử-lý-lỗi-thường-gặp)

---

## 1. Yêu cầu môi trường

Đảm bảo máy bạn đã cài đặt các công cụ sau trước khi bắt đầu:

| Công cụ | Phiên bản tối thiểu | Kiểm tra |
|---------|---------------------|----------|
| **JDK** | 17 hoặc 21 (LTS) | `java -version` |
| **Git** | Bất kỳ | `git --version` |
| **MySQL** | 8.0+ | `mysql --version` |
| **IntelliJ IDEA** | 2023.x+ (Community OK) | — |

> **Gradle KHÔNG cần cài thủ công** — dự án đã có `gradlew` (Gradle Wrapper) tích hợp sẵn, phiên bản **8.10.2** sẽ tự tải về khi build lần đầu.

> **Redis là tùy chọn** — server vẫn chạy được nếu không có Redis. Redis chỉ cần khi dùng tính năng caching nâng cao.

---

## 2. Clone dự án

Mở terminal (PowerShell, CMD, hoặc Git Bash) và chạy:

```bash
git clone https://github.com/PhamNgoc04/InstaGallery-Backend.git
cd InstaGallery-Backend
```

Cấu trúc thư mục sau khi clone:

```
InstaGallery-Backend/
├── src/main/kotlin/com/instagallery/   # Source code Kotlin
│   ├── database/                        # DatabaseFactory + 30 Tables
│   ├── models/                          # Data models & DTOs
│   ├── routes/                          # API route handlers
│   └── Application.kt                  # Entry point
├── src/main/resources/
│   ├── application.conf                 # ⚙️ Cấu hình chính (DB, JWT, Port)
│   └── logback.xml                      # Cấu hình logging
├── docker-compose.yml                   # MySQL + Redis qua Docker
├── database_seed.sql                    # Dữ liệu mẫu
├── build.gradle.kts                     # Dependencies
└── gradlew / gradlew.bat                # Gradle Wrapper
```

---

## 3. Cài đặt MySQL & tạo database

Bạn có **2 lựa chọn**:

### Lựa chọn A — MySQL cài thủ công (có sẵn trên máy)

**Bước 1:** Đăng nhập vào MySQL với quyền root:
```bash
mysql -u root -p
```

**Bước 2:** Tạo database và user:
```sql
-- Tạo database
CREATE DATABASE IF NOT EXISTS instagallery
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- Tạo user (nếu không muốn dùng root)
CREATE USER IF NOT EXISTS 'ig_user'@'localhost' IDENTIFIED BY '123456789';
GRANT ALL PRIVILEGES ON instagallery.* TO 'ig_user'@'localhost';
FLUSH PRIVILEGES;

-- Xác nhận thành công
SHOW DATABASES;
```

**Bước 3:** Thoát MySQL:
```sql
EXIT;
```

> ⚠️ Lần đầu chạy ứng dụng, Ktor sẽ **tự động tạo toàn bộ 30 bảng** thông qua Exposed `SchemaUtils.create(...)`. Bạn **không cần** chạy SQL tạo bảng thủ công.

---

### Lựa chọn B — Docker (không cần cài MySQL/Redis trên máy)

> Xem **[Mục 7](#7-chạy-bằng-docker-compose-nhanh-nhất)** để chạy toàn bộ bằng Docker Compose.

---

## 4. Cấu hình ứng dụng

File cấu hình chính: `src/main/resources/application.conf`

```hocon
ktor {
    deployment {
        port = 8080          # Server chạy trên cổng này
    }
    application {
        modules = [ com.instagallery.ApplicationKt.module ]
    }
}

jwt {
    secret = "my-super-secret-key-for-instagallery-app-which-is-at-least-32-bytes"
    secret = ${?JWT_SECRET}   # Override bằng env variable
    issuer = "http://localhost:8080/"
    audience = "http://localhost:8080/api/v1"
    realm = "InstaGallery"
}

database {
    url      = "jdbc:mysql://localhost:3306/instagallery?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC"
    url      = ${?DB_URL}       # Override bằng env variable
    user     = "root"
    user     = ${?DB_USER}      # Override bằng env variable
    password = "123456789"
    password = ${?DB_PASSWORD}  # Override bằng env variable
}
```

### Chỉnh sửa nếu cần

Nếu MySQL của bạn dùng user/password khác, sửa trực tiếp các giá trị mặc định trong file này:

| Trường | Giá trị mặc định | Ý nghĩa |
|--------|-----------------|---------|
| `database.url` | `jdbc:mysql://localhost:3306/instagallery` | URL kết nối MySQL |
| `database.user` | `root` | Username MySQL |
| `database.password` | `123456789` | Password MySQL |
| `jwt.secret` | `my-super-secret-key...` | Secret ký JWT token |
| `deployment.port` | `8080` | Port server lắng nghe |

> 💡 **Không commit** password thật lên Git. Trong production, dùng biến môi trường `DB_PASSWORD`, `JWT_SECRET`, `DB_USER`.

---

## 5. Chạy dự án bằng IntelliJ IDEA

Đây là cách **được khuyến nghị** khi phát triển.

### Bước 1: Mở dự án

1. Mở **IntelliJ IDEA**
2. Chọn **File → Open...**
3. Điều hướng đến thư mục `InstaGallery-Backend/` vừa clone về
4. Nhấn **OK** → Chọn **Trust Project** nếu được hỏi

### Bước 2: Chờ Gradle Sync

- IntelliJ sẽ tự động nhận diện `build.gradle.kts` và bắt đầu tải dependencies
- Quá trình này mất **3–10 phút** lần đầu (tùy tốc độ mạng)
- Theo dõi tiến trình ở thanh **Build** phía dưới

> Nếu Gradle Sync không tự chạy: **File → Sync Project with Gradle Files** (hoặc nhấn icon voi ở góc phải)

### Bước 3: Cấu hình Run Configuration

1. Nhấn **Add Configuration...** hoặc vào **Run → Edit Configurations...**
2. Nhấn **+** → chọn **Application**
3. Điền các thông tin:

| Trường | Giá trị |
|--------|---------|
| **Name** | `InstaGallery` |
| **Main class** | `io.ktor.server.netty.EngineMain` |
| **Module** | `instagallery-backend.main` |
| **Program arguments** | _(để trống)_ |
| **Working directory** | `$MODULE_WORKING_DIR$` |

4. Nhấn **Apply** → **OK**

### Bước 4: Chạy

- Nhấn nút **▶ Run** (hoặc `Shift + F10`)
- Server khởi động thành công khi terminal hiển thị:

```
[main] INFO  Application - Application started in X.XXX seconds.
[main] INFO  Application - Responding at http://0.0.0.0:8080
```

---

## 6. Chạy dự án bằng Command Line

Không cần IntelliJ, chỉ cần JDK.

### Windows (PowerShell hoặc CMD)

```powershell
# Di chuyển vào thư mục dự án
cd InstaGallery-Backend

# Build và chạy (lần đầu sẽ tải Gradle + dependencies)
.\gradlew.bat run
```

### macOS / Linux

```bash
cd InstaGallery-Backend

# Cấp quyền thực thi (chỉ cần 1 lần)
chmod +x gradlew

# Chạy
./gradlew run
```

### Chế độ Development (hot reload)

```bash
# Windows
.\gradlew.bat run -t

# macOS/Linux
./gradlew run -t
```

> `-t` (continuous build) sẽ theo dõi thay đổi file và tự restart server.

### Build JAR để deploy

```bash
# Tạo fat JAR (shadow jar) — output: build/libs/instagallery-backend-0.0.1-all.jar
./gradlew shadowJar

# Chạy JAR đã build
java -jar build/libs/instagallery-backend-0.0.1-all.jar
```

---

## 7. Chạy bằng Docker Compose (Nhanh nhất)

Cách này chỉ cần cài **Docker Desktop**, không cần MySQL hay Redis trên máy.

### Bước 1: Khởi động MySQL + Redis

```bash
# Đứng trong thư mục gốc dự án
docker-compose up -d
```

Lệnh này sẽ tự tải và khởi động:
- **MySQL 8.0** trên port `3306` — database: `instagallery`, user: `ig_user`, password: `123456789`
- **Redis 7** trên port `6379`

### Bước 2: Kiểm tra container đang chạy

```bash
docker-compose ps
```

Output mong đợi:
```
NAME                      STATUS
instagallery-mysql-1      Up (healthy)
instagallery-redis-1      Up
```

### Bước 3: Cập nhật application.conf

Vì Docker dùng user `ig_user` (không phải `root`), cập nhật `application.conf`:

```hocon
database {
    url      = "jdbc:mysql://localhost:3306/instagallery?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC"
    user     = "ig_user"
    password = "123456789"
}
```

### Bước 4: Chạy backend Ktor như bình thường

```bash
./gradlew run
# hoặc chạy từ IntelliJ IDEA
```

### Dừng Docker khi xong

```bash
docker-compose down
# Giữ lại dữ liệu MySQL; thêm -v nếu muốn xóa luôn volume:
# docker-compose down -v
```

---

## 8. Nạp dữ liệu mẫu (Seed)

> **Chạy sau khi server đã khởi động lần đầu** để các bảng được tạo tự động.

File seed: `database_seed.sql` — chứa 3 users mẫu, posts, albums, comments.

### Cách 1: Dùng MySQL command line

```bash
mysql -u root -p instagallery < database_seed.sql
```

### Cách 2: Dùng MySQL Workbench

1. Mở **MySQL Workbench** → kết nối vào database `instagallery`
2. Vào **File → Open SQL Script...** → chọn `database_seed.sql`
3. Nhấn **⚡ Execute All** (hoặc `Ctrl + Shift + Enter`)

### Cách 3: Paste trực tiếp vào MySQL CLI

```sql
mysql -u root -p
USE instagallery;
-- paste nội dung file database_seed.sql vào đây
```

### Tài khoản test sau khi seed

| Username | Email | Password | Role |
|----------|-------|----------|------|
| `superadmin` | admin@instagallery.com | `Admin@123` | ADMIN |
| `thaopham` | thaopham02@gmail.com | `Admin@123` | USER / PHOTOGRAPHER |
| `hienpham` | hienpham89@gmail.com | `Admin@123` | USER |

> 💡 Password hash trong seed file: `$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TsphxXK`  
> Đây là bcrypt hash của `Admin@123`.

---

## 9. Kiểm tra server hoạt động

Sau khi server chạy thành công trên port `8080`, test bằng các cách sau:

### Dùng trình duyệt

Mở: `http://localhost:8080`

### Dùng curl

```bash
# Health check
curl http://localhost:8080

# Đăng nhập để lấy JWT token
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "admin@instagallery.com", "password": "Admin@123"}'
```

### Dùng Postman

Dự án có sẵn collection Postman tại thư mục gốc:
```
InstaGallery_Local.postman_collection.json
```

1. Mở **Postman** → **Import** → chọn file `InstaGallery_Local.postman_collection.json`
2. Collection sẽ tự cấu hình `baseUrl = http://localhost:8080`
3. Bắt đầu với request **POST /auth/login** để lấy token

---

## 10. Xử lý lỗi thường gặp

### ❌ `Communications link failure` — Không kết nối được MySQL

**Nguyên nhân:** MySQL chưa chạy hoặc sai credentials.

```bash
# Kiểm tra MySQL đang chạy (Windows)
Get-Service MySQL*

# Khởi động MySQL (Windows)
net start MySQL80

# Kiểm tra port 3306
netstat -ano | findstr :3306
```

Kiểm tra lại `application.conf`:
- `database.user` và `database.password` có khớp với MySQL của bạn không?
- `database.url` có đúng tên database là `instagallery` không?

---

### ❌ `Table 'instagallery.xxx' doesn't exist`

**Nguyên nhân:** Bảng chưa được tạo do `SchemaUtils.create()` bị comment out.

**Giải pháp:** Mở `DatabaseFactory.kt` và bỏ comment đoạn này:

```kotlin
transaction {
    SchemaUtils.create(
        UsersTable, UserSessionsTable, PortfoliosTable,
        PostsTable, FiltersTable, PostMediaTable, MediaTagsTable, PostMediaTagsTable,
        FollowersTable, LikesTable, CommentsTable, CommentLikesTable, SavedPostsTable,
        BookingsTable, RatingsTable, ConversationsTable, ConversationMembersTable, MessagesTable,
        NotificationsTable, ActivityLogsTable, ReportsTable, SearchHistoriesTable,
        AlbumsTable, AlbumMediaTable, BlockedUsersTable, MutedUsersTable,
        FollowRequestsTable, SearchHistoriesTable, BannedWordsTable,
        AvailabilitySchedulesTable, PasswordResetTokensTable, ActivityLogsTable
    )
}
```

Restart server — Exposed sẽ tự tạo toàn bộ các bảng còn thiếu.

---

### ❌ `Port 8080 already in use`

**Nguyên nhân:** Có process khác đang chiếm port 8080.

```powershell
# Windows: Tìm process đang dùng port 8080
netstat -ano | findstr :8080

# Dừng process theo PID (thay 12345 bằng PID thực)
taskkill /PID 12345 /F
```

Hoặc đổi port trong `application.conf`:
```hocon
ktor {
    deployment {
        port = 8090  # Đổi sang port khác
    }
}
```

---

### ❌ Gradle build lỗi `Could not resolve...`

**Nguyên nhân:** Mạng không ổn định khi tải dependencies lần đầu.

```bash
# Xóa cache Gradle và build lại
./gradlew clean build --refresh-dependencies
```

---

### ❌ `java.lang.UnsupportedClassVersionError`

**Nguyên nhân:** JDK đang dùng quá cũ (thấp hơn JDK 17).

```bash
# Kiểm tra version
java -version

# Phải là 17 hoặc 21
# openjdk version "17.x.x" hoặc "21.x.x"
```

Tải JDK 21 tại: https://adoptium.net/

---

### ❌ IntelliJ không nhận ra Kotlin / Gradle

1. **File → Invalidate Caches... → Invalidate and Restart**
2. Sau khi IntelliJ khởi động lại: **File → Sync Project with Gradle Files**

---

## 📌 Tổng kết nhanh (TL;DR)

```bash
# 1. Clone
git clone https://github.com/PhamNgoc04/InstaGallery-Backend.git
cd InstaGallery-Backend

# 2. Tạo database (MySQL đã cài sẵn)
mysql -u root -p -e "CREATE DATABASE instagallery CHARACTER SET utf8mb4;"

# 3. Kiểm tra application.conf (đúng user/password MySQL)
#    src/main/resources/application.conf

# 4. Chạy
./gradlew run          # macOS/Linux
.\gradlew.bat run      # Windows

# 5. Seed data (sau khi server chạy lần đầu)
mysql -u root -p instagallery < database_seed.sql

# ✅ Server sẵn sàng tại http://localhost:8080
```
