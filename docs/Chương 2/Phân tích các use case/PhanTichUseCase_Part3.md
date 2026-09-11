## --- Nhóm: Tương tác (tiếp) ---

### 2.2.5.22. Use Case Follow / Unfollow

**- Biểu đồ lớp phân tích:**

```mermaid
classDiagram
    class FollowUI {
        <<boundary>>
        +hienThiNutFollow()
        +hienThiNutFollowing()
        +clickFollow()
        +clickUnfollow()
        +capNhatGiaoDien()
    }

    class FollowController {
        <<control>>
        +xuLyFollow()
        +xuLyUnfollow()
        +kiemTraSelfFollow()
        +guiNotification()
    }

    class Follower {
        <<entity>>
        +follower_id : BIGINT
        +following_id : BIGINT
        +created_at : TIMESTAMP
        +getFollowerId()
        +setFollowerId()
        +getFollowingId()
        +setFollowingId()
        +getCreatedAt()
    }

    class User {
        <<entity>>
        +id : BIGINT
        +follower_count : INT
        +following_count : INT
        +getFollowerCount()
        +setFollowerCount()
        +getFollowingCount()
        +setFollowingCount()
    }

    class Notification {
        <<entity>>
        +id : BIGINT
        +user_id : BIGINT
        +type : ENUM
        +setType()
        +setUserId()
    }

    class ICsdl {
        <<boundary>>
        +themBangFollowers()
        +xoaBangFollowers()
        +capNhatBangUsers()
        +themBangNotifications()
    }

    FollowUI "1" -- "1" FollowController
    FollowController "1" -- "*" Follower
    FollowController "1" -- "1" User
    FollowController "1" -- "*" Notification
    FollowController "1" -- "*" ICsdl
```

**- Biểu đồ trình tự:**

```mermaid
sequenceDiagram
    actor KH as Người dùng
    participant UI as FollowUI
    participant CT as FollowController
    participant F as Follower
    participant U as User
    participant N as Notification
    participant DB as ICsdl

    Note over KH,DB: === FOLLOW ===
    KH->>UI: clickFollow()
    UI->>CT: xuLyFollow(targetUserId)
    CT->>CT: kiemTraSelfFollow()
    CT->>F: createFollower()
    F->>DB: themBangFollowers()
    Note over DB: Trigger: following_count +1, follower_count +1
    CT->>N: createNotification(NEW_FOLLOWER)
    N->>DB: themBangNotifications()
    DB-->>CT: return kết quả
    CT-->>UI: capNhatGiaoDien()
    UI-->>KH: nút chuyển thành "Following"

    Note over KH,DB: === UNFOLLOW ===
    KH->>UI: clickUnfollow()
    UI->>CT: xuLyUnfollow(targetUserId)
    CT->>DB: xoaBangFollowers()
    Note over DB: Trigger: giảm counters
    DB-->>CT: return kết quả
    CT-->>UI: capNhatGiaoDien()
    UI-->>KH: nút chuyển về "Follow"
```

---

## --- Nhóm: Portfolio & Booking ---

### 2.2.5.34. Use Case Xem Portfolio nhiếp ảnh gia

**- Biểu đồ lớp phân tích:**

```mermaid
classDiagram
    class PortfolioUI {
        <<boundary>>
        +hienThiThongTinPortfolio()
        +hienThiGalleryAnh()
        +hienThiDanhSachDanhGia()
        +clickDatLichChup()
        +clickNhanTin()
    }

    class PortfolioController {
        <<control>>
        +layThongTinPortfolio()
        +layDanhSachDanhGia()
        +layGalleryAnh()
        +chuyenManHinh()
    }

    class Portfolio {
        <<entity>>
        +id : BIGINT
        +user_id : BIGINT
        +title : VARCHAR
        +specialties : JSON
        +hourly_rate : DECIMAL
        +service_area : VARCHAR
        +is_available : BOOLEAN
        +rating_avg : DECIMAL
        +review_count : INT
        +getTitle()
        +getSpecialties()
        +getHourlyRate()
        +getServiceArea()
        +getIsAvailable()
        +getRatingAvg()
        +getReviewCount()
    }

    class Rating {
        <<entity>>
        +id : BIGINT
        +rating_value : SMALLINT
        +comment : TEXT
        +created_at : TIMESTAMP
        +getRatingValue()
        +getComment()
    }

    class ICsdl {
        <<boundary>>
        +truyVanBangPortfolios()
        +truyVanBangRatings()
        +truyVanBangPosts()
    }

    PortfolioUI "1" -- "1" PortfolioController
    PortfolioController "1" -- "1" Portfolio
    PortfolioController "1" -- "*" Rating
    PortfolioController "1" -- "*" ICsdl
```

**- Biểu đồ trình tự:**

```mermaid
sequenceDiagram
    actor KH as Người dùng
    participant UI as PortfolioUI
    participant CT as PortfolioController
    participant PF as Portfolio
    participant R as Rating
    participant DB as ICsdl

    KH->>UI: click "Xem Portfolio"
    UI->>CT: layThongTinPortfolio(userId)
    CT->>DB: truyVanBangPortfolios(userId)
    DB-->>PF: thông tin portfolio
    CT-->>UI: hienThiThongTinPortfolio()
    UI-->>KH: hiển thị chuyên môn, giá, khu vực

    UI->>CT: layDanhSachDanhGia(userId)
    CT->>DB: truyVanBangRatings(userId)
    DB-->>R: danh sách đánh giá
    CT-->>UI: hienThiDanhSachDanhGia()

    UI->>CT: layGalleryAnh(userId)
    CT->>DB: truyVanBangPosts(userId)
    DB-->>CT: danh sách ảnh
    CT-->>UI: hienThiGalleryAnh()
    UI-->>KH: hiển thị đầy đủ Portfolio
```

---

### 2.2.5.35. Use Case Đặt lịch chụp

**- Biểu đồ lớp phân tích:**

```mermaid
classDiagram
    class DatLichUI {
        <<boundary>>
        +hienThiCalendar()
        +chonNgayChup()
        +nhapYeuCau()
        +hienThiGiaDuKien()
        +clickDatLich()
        +hienThiThongBao()
    }

    class DatLichController {
        <<control>>
        +layLichTrong()
        +kiemTraNgayTrong()
        +tinhGiaDuKien()
        +taoBooking()
        +guiNotification()
    }

    class Booking {
        <<entity>>
        +id : BIGINT
        +client_id : BIGINT
        +photographer_id : BIGINT
        +booking_date : DATETIME
        +duration_hours : DECIMAL
        +details : TEXT
        +price : DECIMAL
        +status : ENUM
        +created_at : TIMESTAMP
        +setClientId()
        +setPhotographerId()
        +setBookingDate()
        +setDetails()
        +setPrice()
        +setStatus()
        +getStatus()
    }

    class Portfolio {
        <<entity>>
        +hourly_rate : DECIMAL
        +is_available : BOOLEAN
        +getHourlyRate()
        +getIsAvailable()
    }

    class ICsdl {
        <<boundary>>
        +truyVanBangBookings()
        +truyVanBangPortfolios()
        +themBangBookings()
        +themBangNotifications()
    }

    DatLichUI "1" -- "1" DatLichController
    DatLichController "1" -- "1" Booking
    DatLichController "1" -- "1" Portfolio
    DatLichController "1" -- "*" ICsdl
```

**- Biểu đồ trình tự:**

```mermaid
sequenceDiagram
    actor C as Client
    participant UI as DatLichUI
    participant CT as DatLichController
    participant B as Booking
    participant PF as Portfolio
    participant DB as ICsdl

    C->>UI: click "Đặt lịch chụp"
    UI->>CT: layLichTrong(photographerId)
    CT->>DB: truyVanBangBookings(photographerId)
    DB-->>CT: ngày đã đặt
    CT->>DB: truyVanBangPortfolios(photographerId)
    DB-->>PF: thông tin portfolio
    CT-->>UI: hienThiCalendar()
    UI-->>C: Calendar (ngày đã đặt bị disable)

    C->>UI: chonNgayChup()
    UI->>CT: kiemTraNgayTrong()
    CT-->>UI: ngày trống OK
    C->>UI: nhapYeuCau()
    UI->>CT: tinhGiaDuKien()
    CT-->>UI: hienThiGiaDuKien()

    C->>UI: clickDatLich()
    UI->>CT: taoBooking()
    CT->>B: createBooking(status=PENDING)
    B->>DB: themBangBookings()
    CT->>DB: themBangNotifications()
    DB-->>CT: return kết quả
    CT-->>UI: hienThiThongBao("Đặt lịch thành công!")
    UI-->>C: hiển thị thông báo chờ xác nhận
```

---

### 2.2.5.40. Use Case Quản lý Booking

**- Biểu đồ lớp phân tích:**

```mermaid
classDiagram
    class QuanLyBookingUI {
        <<boundary>>
        +hienThiDanhSachBooking()
        +locTheoTrangThai()
        +hienThiChiTietBooking()
        +clickXacNhan()
        +clickBatDau()
        +clickHoanThanh()
        +clickHuy()
        +hienThiThongBao()
    }

    class QuanLyBookingController {
        <<control>>
        +layDanhSachBooking()
        +layChiTietBooking()
        +validateStateMachine()
        +capNhatTrangThai()
        +guiNotification()
    }

    class Booking {
        <<entity>>
        +id : BIGINT
        +client_id : BIGINT
        +photographer_id : BIGINT
        +booking_date : DATETIME
        +details : TEXT
        +price : DECIMAL
        +status : ENUM
        +updated_at : TIMESTAMP
        +getId()
        +getStatus()
        +setStatus()
        +getClientId()
        +getPhotographerId()
        +getBookingDate()
        +getDetails()
        +getPrice()
    }

    class Notification {
        <<entity>>
        +id : BIGINT
        +user_id : BIGINT
        +type : ENUM
        +target_id : BIGINT
        +setType()
        +setUserId()
        +setTargetId()
    }

    class ICsdl {
        <<boundary>>
        +truyVanBangBookings()
        +capNhatBangBookings()
        +themBangNotifications()
    }

    QuanLyBookingUI "1" -- "1" QuanLyBookingController
    QuanLyBookingController "1" -- "*" Booking
    QuanLyBookingController "1" -- "*" Notification
    QuanLyBookingController "1" -- "*" ICsdl
```

**- Biểu đồ trình tự:**

```mermaid
sequenceDiagram
    actor P as Photographer
    participant UI as QuanLyBookingUI
    participant CT as QuanLyBookingController
    participant B as Booking
    participant N as Notification
    participant DB as ICsdl

    P->>UI: mở "Đơn booking"
    UI->>CT: layDanhSachBooking(photographerId)
    CT->>DB: truyVanBangBookings(photographerId)
    DB-->>B: danh sách bookings
    CT-->>UI: hienThiDanhSachBooking()
    UI-->>P: hiển thị danh sách (lọc theo status)

    P->>UI: click booking PENDING
    UI->>CT: layChiTietBooking(bookingId)
    CT-->>UI: hienThiChiTietBooking()

    P->>UI: clickXacNhan()
    UI->>CT: capNhatTrangThai(CONFIRMED)
    CT->>CT: validateStateMachine(PENDING → CONFIRMED)
    CT->>B: setStatus(CONFIRMED)
    B->>DB: capNhatBangBookings()
    CT->>N: createNotification(BOOKING_CONFIRMED)
    N->>DB: themBangNotifications()
    DB-->>CT: return kết quả
    CT-->>UI: hienThiThongBao("Đã xác nhận")

    Note over P: Đến ngày chụp...
    P->>UI: clickBatDau()
    UI->>CT: capNhatTrangThai(IN_PROGRESS)
    CT->>B: setStatus(IN_PROGRESS)
    B->>DB: capNhatBangBookings()

    Note over P: Chụp xong...
    P->>UI: clickHoanThanh()
    UI->>CT: capNhatTrangThai(COMPLETED)
    CT->>B: setStatus(COMPLETED)
    B->>DB: capNhatBangBookings()
    CT->>N: createNotification(BOOKING_COMPLETED)
    N->>DB: themBangNotifications()
    CT-->>UI: hienThiThongBao("Hoàn thành")
```

---

### 2.2.5.36. Use Case Đánh giá thợ ảnh

**- Biểu đồ lớp phân tích:**

```mermaid
classDiagram
    class DanhGiaUI {
        <<boundary>>
        +hienThiNutDanhGia()
        +hienThiFormDanhGia()
        +chonSao()
        +nhapNhanXet()
        +clickGuiDanhGia()
        +hienThiThongBao()
    }

    class DanhGiaController {
        <<control>>
        +kiemTraBookingCompleted()
        +kiemTraDaDanhGia()
        +validateDanhGia()
        +luuDanhGia()
        +capNhatRatingAvg()
    }

    class Rating {
        <<entity>>
        +id : BIGINT
        +booking_id : BIGINT
        +rater_id : BIGINT
        +ratee_id : BIGINT
        +rating_value : SMALLINT
        +comment : TEXT
        +created_at : TIMESTAMP
        +setBookingId()
        +setRaterId()
        +setRateeId()
        +setRatingValue()
        +setComment()
        +getRatingValue()
    }

    class Booking {
        <<entity>>
        +id : BIGINT
        +status : ENUM
        +getStatus()
    }

    class Portfolio {
        <<entity>>
        +rating_avg : DECIMAL
        +review_count : INT
        +setRatingAvg()
        +setReviewCount()
    }

    class ICsdl {
        <<boundary>>
        +truyVanBangBookings()
        +truyVanBangRatings()
        +themBangRatings()
        +capNhatBangPortfolios()
    }

    DanhGiaUI "1" -- "1" DanhGiaController
    DanhGiaController "1" -- "1" Rating
    DanhGiaController "1" -- "1" Booking
    DanhGiaController "1" -- "1" Portfolio
    DanhGiaController "1" -- "*" ICsdl
```

**- Biểu đồ trình tự:**

```mermaid
sequenceDiagram
    actor C as Client
    participant UI as DanhGiaUI
    participant CT as DanhGiaController
    participant R as Rating
    participant B as Booking
    participant PF as Portfolio
    participant DB as ICsdl

    C->>UI: mở booking COMPLETED
    UI->>UI: hienThiNutDanhGia()
    C->>UI: clickNutDanhGia()
    UI->>CT: kiemTraBookingCompleted(bookingId)
    CT->>DB: truyVanBangBookings(bookingId)
    DB-->>B: status = COMPLETED ✓
    CT->>CT: kiemTraDaDanhGia()
    CT->>DB: truyVanBangRatings(bookingId)
    DB-->>CT: chưa có rating ✓
    CT-->>UI: hienThiFormDanhGia()

    C->>UI: chonSao(4)
    C->>UI: nhapNhanXet("Chụp rất đẹp!")
    C->>UI: clickGuiDanhGia()
    UI->>CT: luuDanhGia()
    CT->>CT: validateDanhGia()
    CT->>R: createRating()
    R->>DB: themBangRatings()
    CT->>PF: capNhatRatingAvg()
    PF->>DB: capNhatBangPortfolios()
    DB-->>CT: return kết quả
    CT-->>UI: hienThiThongBao("Đánh giá đã được gửi!")
    UI-->>C: hiển thị thông báo cảm ơn
```
