# 2.4.3. CÁC SƠ ĐỒ TUẦN TỰ (SEQUENCE DIAGRAMS)

Để bảo vệ Đồ án trước hội đồng xuất sắc nhất, bên cạnh UML Class/Use Case, **Sơ đồ Tuần tự (Sequence Diagram)** là minh chứng mạnh mẽ nhất thể hiện việc bạn nắm cực kỳ sâu mô hình Client-Server. Dưới đây là 3 sơ đồ "sát thủ" xử lý 3 luồng phức tạp nhất của InstaGallery: **Đăng nhập**, **Đăng bài (Xử lý Cloud Media)**, và **Máy trạng thái Đặt Lịch (Booking State Machine)**.

---

### 1. Luồng Xác Thực (Authentication & Login Flow)
Sơ đồ này mô tả cách JWT Access Token và Refresh Token được cấp phát và bảo mật qua Local Cache (Redis) cùng DB (MySQL).

```mermaid
sequenceDiagram
    autonumber
    actor User as Người dùng
    participant App as Android App<br/>(XML/AppCompat + MVVM)
    participant AuthCtrl as Auth Controller<br/>(API Layer)
    participant AuthSvc as Auth Service<br/>(Business Logic)
    participant DB as MySQL Database
    participant Cache as Redis Cache

    User->>App: Nhập Email & Mật khẩu
    App->>AuthCtrl: POST /api/v1/auth/login
    AuthCtrl->>AuthSvc: validateCredentials(email, pwd)
    
    %% Truy vấn User
    AuthSvc->>DB: SELECT * FROM users WHERE email = ?
    DB-->>AuthSvc: Trả về User Entity (hoặc Null)
    
    alt Nếu Người dùng hợp lệ & Mật khẩu đúng
        AuthSvc->>AuthSvc: Khởi tạo JWT Access Token (15m)<br/>& Refresh Token (30d)
        
        %% Lưu lại phiên đăng nhập
        AuthSvc->>Cache: Lưu Refresh Token (Hỗ trợ Device/Blacklist)
        AuthSvc->>DB: INSERT INTO user_sessions
        
        AuthSvc-->>AuthCtrl: TokenResponse DTO
        AuthCtrl-->>App: HTTP 200 OK + JWT Tokens
        
        App->>App: Lưu Token qua TokenStore
        App-->>User: Chuyển hướng vào màn hình Feed
    else Nếu Sai Thông Tin
        AuthSvc-->>AuthCtrl: ném BadCredentialsException
        AuthCtrl-->>App: HTTP 401 Unauthorized
        App-->>User: Hiển thị lỗi "Sai email hoặc mật khẩu"
    end
```

---

### 2. Luồng Đăng Bài Tích Hợp Đa Phương Tiện (Upload Post & Media Flow)
Khác với ứng dụng thuần Text, InstaGallery phải xử lý file Ảnh/Video. Sơ đồ này chứng minh cơ chế Upload trực tiếp từ Client lên Storage Bucket thay vì dồn rác qua Server, giúp tối ưu băng thông (Performance).

```mermaid
sequenceDiagram
    autonumber
    actor User as Người dùng
    participant App as Android App
    participant Cloud as Object Storage/CDN<br/>(provider interchangeable)
    participant PostCtrl as Post Controller
    participant PostSvc as Post Service
    participant DB as MySQL Database

    User->>App: Chọn ảnh (Gallery), Áp dụng Filter & Nhập Caption
    User->>App: Nhấn "Đăng Bài"

    %% File Upload Phase
    rect rgb(0, 0, 0, 0.05)
    Note right of App: Giai đoạn 1: Upload tài nguyên tĩnh
    App->>PostCtrl: POST /api/v1/media/presigned-url
    PostCtrl->>Cloud: Tạo presigned URL
    Cloud-->>PostCtrl: upload_url + media_file_url
    PostCtrl-->>App: Trả về upload_url + media_file_url
    App->>Cloud: PUT file lên upload_url
    Cloud-->>App: Upload thành công
    end
    
    %% Create Post Phase
    rect rgb(0, 0, 0, 0.08)
    Note right of App: Giai đoạn 2: Tạo Bài Đăng (Data Insert)
    App->>PostCtrl: POST /api/v1/posts {caption, media_file_url}
    PostCtrl->>PostSvc: createPost(userId, PostRequestDTO)
    
    %% Thực thi Transaction DB
    Note right of PostSvc: BEGIN TRANSACTION
    PostSvc->>DB: INSERT INTO posts (caption, user_id)
    DB-->>PostSvc: Mới tạo: post_id
    PostSvc->>DB: INSERT INTO post_media (post_id, media_file_url)
    PostSvc->>DB: UPDATE users SET post_count = post_count + 1
    Note right of PostSvc: COMMIT TRANSACTION
    end

    PostSvc-->>PostCtrl: Trả về thông tin Post vừa tạo
    PostCtrl-->>App: HTTP 201 Created
    App-->>User: Thêm bài lên đầu Feed & Ẩn loading
```

---

### 3. Luồng Giao Dịch Đặt Lịch Chụp (Booking State Machine Flow)
Đây là nghiệp vụ độc đáo nhất (USP) của InstaGallery. Sơ đồ này biểu diễn tương tác chéo giữa 2 tác nhân thông qua notification module. Giai đoạn MVP chỉ cần thông báo trong app; push notification để sau.

```mermaid
sequenceDiagram
    autonumber
    actor Client as Khách Hàng (Client)
    actor Photog as Nhiếp Ảnh Gia (Photographer)
    participant App as Android App
    participant BookCtrl as Booking Controller
    participant BookSvc as Booking Service
    participant DB as MySQL DB
    participant Notif as Notification Module<br/>(in-app, push deferred)

    %% Client tạo Request
    Client->>App: Chốt Ngày, Giờ & Nội dung chụp
    App->>BookCtrl: POST /api/v1/bookings
    BookCtrl->>BookSvc: createBooking(clientId, photogId, date)
    
    %% Validation
    BookSvc->>DB: Kiểm tra bảng 'availabilities' (Lịch trống)
    
    alt Ngày CÒN TRỐNG
        BookSvc->>DB: INSERT INTO bookings (Status="PENDING")
        BookSvc->>Notif: Bắn Event "Booking_Created"
        Notif-->>Photog: Ting Ting: "Bạn có lời mời đặt lịch mới!"
        BookSvc-->>BookCtrl: Success
        BookCtrl-->>App: HTTP 201 Created
        App-->>Client: UI: "Đang chờ thợ ảnh xác nhận..."
    else KÍN LỊCH (Trùng lịch)
        BookSvc-->>BookCtrl: ném DateConflictException
        BookCtrl-->>App: HTTP 409 Conflict
        App-->>Client: UI: Báo lỗi "Lịch chụp đã bị người khác đặt"
    end

    %% Photographer Action (Bất đồng bộ sau một lúc)
    Note over Photog, App: Thợ ảnh mở App xem đơn PENDING
    Photog->>App: Nhấn nút [Chấp Nhận Lịch]
    App->>BookCtrl: PUT /api/v1/bookings/{id}/status (CONFIRMED)
    BookCtrl->>BookSvc: updateBookingStatus(id, CONFIRMED)
    
    BookSvc->>DB: UPDATE bookings SET status='CONFIRMED'
    BookSvc->>Notif: Bắn Event "Booking_Confirmed"
    Notif-->>Client: Ting Ting: "Lịch chụp của bạn đã được Thợ ảnh xác nhận!"
    
    BookSvc-->>BookCtrl: Trả về trạng thái Booking
    BookCtrl-->>App: HTTP 200 OK
    App-->>Photog: UI: Đơn hàng chuyển sang tab Tiến Hành
```
