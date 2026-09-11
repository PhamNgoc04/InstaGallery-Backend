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
# D:\Android\SDK\platform-tools\adb.exe -s emulator-5554 reverse tcp:8080 tcp:8080
```

## 2. Khởi chạy backend bằng Docker Compose

```powershell
cd D:\InstaGallery\instagallery-backend

# Tắt MySQL local nếu đang chạy chiếm cổng (với quyền Administrator)
net stop MySQL80

# Mở terminal trong VS Code và chạy:
docker compose ps
docker compose down
docker compose up -d

docker compose up --build -d
```

Lệnh này sẽ khởi chạy cả 3 dịch vụ chính:

- Backend Ktor: `http://localhost:8080`
- MySQL: `localhost:3306`
- Redis: `localhost:6379`

### Thư mục lưu trữ tập tin đa phương tiện (Media Upload Local):

- Các tệp tin hình ảnh được tải lên từ ứng dụng Android sẽ được lưu trữ tại thư mục `D:\InstaGallery\instagallery-backend\uploads`.
- Tệp cấu hình `docker-compose.yml` sẽ gắn kết mục dữ liệu (mount) `./uploads` trên máy vật lý vào `/app/uploads` bên trong container chạy backend.
- Cơ sở dữ liệu chỉ lưu trữ đường dẫn URL truy xuất của tệp. Nếu xóa thư mục `uploads` trên máy vật lý, các thông tin bài viết cũ vẫn được lưu trong DB nhưng hình ảnh hiển thị sẽ bị lỗi `MEDIA_NOT_FOUND`.
- Việc chạy các lệnh `docker compose down`, `docker compose up --build -d`, khởi động lại máy tính, hoặc tái thiết lập container backend sẽ không làm mất tệp tin hình ảnh trong thư mục `uploads`.

Sau khi khởi chạy Docker Compose thành công lần đầu tiên, các container sẽ tự động chạy ngầm ở chế độ `restart: unless-stopped` mỗi khi khởi động Docker Desktop.

Nếu bạn chỉ chỉnh sửa mã nguồn backend Ktor và muốn biên dịch xây dựng lại image, hãy sử dụng lại lệnh khởi chạy ở trên.

## 3. Các cổng kết nối sử dụng (Ports)

- Backend Ktor: `http://localhost:8080`
- MySQL Docker: `localhost:3306`
- Redis Docker: `localhost:6379`

## 4. Kiểm tra Đăng nhập trên Postman (Auth Session)

**Endpoint:**
```http
POST http://localhost:8080/api/v1/auth/login
```

**Body JSON:**
```json
{
  "usernameOrEmail": "ngocpb04@gmail.com",
  "password": "ngoc123!"
}
```

**Lưu ý:**
- Điểm tiếp nhận API hiện tại đang hỗ trợ tìm kiếm tài khoản đăng nhập trực tiếp qua Email.
- Nếu hệ thống phản hồi lỗi `USER_NOT_FOUND`, điều này có nghĩa tài khoản tương ứng chưa tồn tại trong cơ sở dữ liệu.

## 5. Quy trình Đăng ký tài khoản mới (Register account)

Nếu chưa khởi tạo tài khoản, vui lòng đăng ký trước tại:

**Endpoint:**
```http
POST http://localhost:8080/api/v1/auth/register
```

**Body JSON:**
```json
{
  "email": "ngocpb04@gmail.com",
  "username": "ngocpham",
  "password": "ngoc123!",
  "fullName": "Ngoc Pham"
}
```

Sau khi đăng ký thành công, hãy sử dụng địa chỉ email và mật khẩu tương ứng để thực hiện thao tác đăng nhập.

## 6. Khởi tạo dữ liệu mẫu (Database Seeding)

Để nạp trước dữ liệu mẫu vào MySQL, hãy sử dụng lệnh sau:

```powershell
Get-Content database_seed.sql | docker exec -i instagallery-backend-mysql-1 mysql -uig_user -p123456789 instagallery
```

**Lưu ý:**
- Cú pháp trên yêu cầu container MySQL của dự án phải đang ở trạng thái chạy hoạt động.
- Nếu mật khẩu mã hóa trong file seed không trùng khớp hoặc bị lỗi mã hóa, phương án tối ưu nhất vẫn là tự khởi tạo các tài khoản mới thông qua API.

## 7. Chẩn đoán nhanh sự cố (Troubleshooting)

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

## 8. Dừng hệ thống

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
