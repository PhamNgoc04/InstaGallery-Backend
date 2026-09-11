# 2.2.5. Phân tích các Use Case

## --- Nhóm: Xác thực & Quản lý tài khoản ---

### 2.2.5.1. Use Case Đăng ký

**- Biểu đồ lớp phân tích:**

```mermaid
classDiagram
    class DangKyUI {
        <<boundary>>
        +clickNutDangKy()
        +clickNutQuayLai()
        +hienThiManHinhDangKy()
        +nhapThongTinDangKy()
        +hienThiThongBaoLoi()
    }

    class DangKyController {
        <<control>>
        +validateDuLieu()
        +kiemTraTrungEmail()
        +kiemTraTrungUsername()
        +taoTaiKhoanMoi()
        +maHoaMatKhau()
        +taoTokens()
        +chuyenManHinh()
    }

    class User {
        <<entity>>
        +id : BIGINT
        +username : VARCHAR
        +email : VARCHAR
        +password_hash : VARCHAR
        +full_name : VARCHAR
        +role : ENUM
        +is_active : BOOLEAN
        +created_at : TIMESTAMP
        +getId()
        +setId()
        +getUsername()
        +setUsername()
        +getEmail()
        +setEmail()
        +getPasswordHash()
        +setPasswordHash()
        +getFullName()
        +setFullName()
        +getRole()
        +setRole()
        +getIsActive()
        +setIsActive()
        +getCreatedAt()
        +setCreatedAt()
    }

    class UserSession {
        <<entity>>
        +id : BIGINT
        +user_id : BIGINT
        +refresh_token : VARCHAR
        +expired_at : TIMESTAMP
        +created_at : TIMESTAMP
        +getId()
        +setId()
        +getUserId()
        +setUserId()
        +getRefreshToken()
        +setRefreshToken()
        +getExpiredAt()
        +setExpiredAt()
    }

    class ICsdl {
        <<boundary>>
        +themBangUsers()
        +kiemTraBangUsers()
        +themBangUserSessions()
    }

    DangKyUI "1" -- "1" DangKyController
    DangKyController "1" -- "1" User
    DangKyController "1" -- "1" UserSession
    DangKyController "1" -- "*" ICsdl
```

**- Biểu đồ trình tự:**

```mermaid
sequenceDiagram
    actor KH as Người dùng
    participant UI as DangKyUI
    participant CT as DangKyController
    participant U as User
    participant US as UserSession
    participant DB as ICsdl

    KH->>UI: clickNutDangKy()
    UI->>UI: hienThiManHinhDangKy()
    KH->>UI: nhapThongTinDangKy()
    KH->>UI: clickNutDangKy()
    UI->>CT: validateDuLieu()
    CT->>DB: kiemTraBangUsers(email)
    DB-->>CT: không tồn tại
    CT->>DB: kiemTraBangUsers(username)
    DB-->>CT: không tồn tại
    CT->>CT: maHoaMatKhau()
    CT->>U: createUser()
    U->>DB: themBangUsers()
    DB-->>CT: user_id
    CT->>US: createSession()
    US->>DB: themBangUserSessions()
    DB-->>CT: return kết quả
    CT->>UI: hienThiDangKyThanhCong()
    UI-->>KH: chuyển đến màn hình Location
```

---

### 2.2.5.2. Use Case Đăng nhập

**- Biểu đồ lớp phân tích:**

```mermaid
classDiagram
    class DangNhapUI {
        <<boundary>>
        +hienThiManHinhDangNhap()
        +nhapEmailMatKhau()
        +clickNutDangNhap()
        +hienThiThongBaoLoi()
        +chuyenDenFeed()
    }

    class DangNhapController {
        <<control>>
        +kiemTraTokenLocal()
        +validateDuLieu()
        +kiemTraRateLimit()
        +xacThucTaiKhoan()
        +soSanhMatKhau()
        +kiemTraTrangThai()
        +taoTokens()
        +chuyenManHinh()
    }

    class User {
        <<entity>>
        +id : BIGINT
        +email : VARCHAR
        +password_hash : VARCHAR
        +is_active : BOOLEAN
        +role : ENUM
        +getEmail()
        +setEmail()
        +getPasswordHash()
        +getIsActive()
        +getRole()
    }

    class UserSession {
        <<entity>>
        +id : BIGINT
        +user_id : BIGINT
        +refresh_token : VARCHAR
        +device_info : VARCHAR
        +ip_address : VARCHAR
        +expired_at : TIMESTAMP
        +getUserId()
        +setUserId()
        +getRefreshToken()
        +setRefreshToken()
    }

    class ICsdl {
        <<boundary>>
        +truyVanBangUsers()
        +themBangUserSessions()
        +kiemTraRateLimit()
    }

    DangNhapUI "1" -- "1" DangNhapController
    DangNhapController "1" -- "1" User
    DangNhapController "1" -- "1" UserSession
    DangNhapController "1" -- "*" ICsdl
```

**- Biểu đồ trình tự:**

```mermaid
sequenceDiagram
    actor KH as Người dùng
    participant UI as DangNhapUI
    participant CT as DangNhapController
    participant U as User
    participant US as UserSession
    participant DB as ICsdl

    KH->>UI: mở ứng dụng
    UI->>CT: kiemTraTokenLocal()
    CT-->>UI: không có token
    UI->>UI: hienThiManHinhDangNhap()
    KH->>UI: nhapEmailMatKhau()
    KH->>UI: clickNutDangNhap()
    UI->>CT: xacThucTaiKhoan()
    CT->>DB: kiemTraRateLimit()
    DB-->>CT: chưa vượt giới hạn
    CT->>DB: truyVanBangUsers(email)
    DB-->>U: thông tin user
    CT->>CT: soSanhMatKhau()
    CT->>CT: kiemTraTrangThai()
    CT->>US: createSession()
    US->>DB: themBangUserSessions()
    DB-->>CT: return tokens
    CT->>UI: chuyenDenFeed()
    UI-->>KH: hiển thị màn hình Feed
```

---

### 2.2.5.5. Use Case Quên mật khẩu

**- Biểu đồ lớp phân tích:**

```mermaid
classDiagram
    class QuenMatKhauUI {
        <<boundary>>
        +hienThiFormNhapEmail()
        +nhapEmail()
        +clickGuiLink()
        +hienThiFormDatLaiMK()
        +nhapMatKhauMoi()
        +clickDatLaiMatKhau()
        +hienThiThongBao()
    }

    class QuenMatKhauController {
        <<control>>
        +kiemTraEmailTonTai()
        +taoResetToken()
        +guiEmailReset()
        +xacThucResetToken()
        +maHoaMatKhauMoi()
        +capNhatMatKhau()
        +xoaPhienDangNhapCu()
        +chuyenManHinh()
    }

    class User {
        <<entity>>
        +id : BIGINT
        +email : VARCHAR
        +password_hash : VARCHAR
        +getEmail()
        +setEmail()
        +getPasswordHash()
        +setPasswordHash()
    }

    class UserSession {
        <<entity>>
        +id : BIGINT
        +user_id : BIGINT
        +getUserId()
    }

    class ICsdl {
        <<boundary>>
        +truyVanBangUsers()
        +capNhatBangUsers()
        +xoaBangUserSessions()
    }

    QuenMatKhauUI "1" -- "1" QuenMatKhauController
    QuenMatKhauController "1" -- "1" User
    QuenMatKhauController "1" -- "1" UserSession
    QuenMatKhauController "1" -- "*" ICsdl
```

**- Biểu đồ trình tự:**

```mermaid
sequenceDiagram
    actor KH as Người dùng
    participant UI as QuenMatKhauUI
    participant CT as QuenMatKhauController
    participant U as User
    participant US as UserSession
    participant DB as ICsdl

    KH->>UI: click "Quên mật khẩu?"
    UI->>UI: hienThiFormNhapEmail()
    KH->>UI: nhapEmail()
    KH->>UI: clickGuiLink()
    UI->>CT: kiemTraEmailTonTai()
    CT->>DB: truyVanBangUsers(email)
    DB-->>CT: tìm thấy user
    CT->>CT: taoResetToken()
    CT->>CT: guiEmailReset()
    CT-->>UI: hienThiThongBao("Kiểm tra email")

    Note over KH: Mở email, bấm link reset

    KH->>UI: nhapMatKhauMoi()
    KH->>UI: clickDatLaiMatKhau()
    UI->>CT: xacThucResetToken()
    CT->>CT: maHoaMatKhauMoi()
    CT->>U: setPasswordHash()
    U->>DB: capNhatBangUsers()
    CT->>US: xoaPhienCu()
    US->>DB: xoaBangUserSessions()
    DB-->>CT: return kết quả
    CT->>UI: hienThiThongBao("Thành công")
    UI-->>KH: chuyển về màn hình Đăng nhập
```

---

### 2.2.5.8. Use Case Cập nhật hồ sơ cá nhân

**- Biểu đồ lớp phân tích:**

```mermaid
classDiagram
    class CapNhatThongTinUI {
        <<boundary>>
        +hienThiTrangProfile()
        +clickChinhSuaHoSo()
        +hienThiFormChinhSua()
        +nhapThongTinMoi()
        +chonAnhAvatar()
        +clickLuu()
        +hienThiThongBao()
    }

    class CapNhatThongTinController {
        <<control>>
        +layThongTinHienTai()
        +validateDuLieu()
        +kiemTraTrungUsername()
        +uploadAvatar()
        +capNhatThongTin()
        +chuyenManHinh()
    }

    class User {
        <<entity>>
        +id : BIGINT
        +username : VARCHAR
        +full_name : VARCHAR
        +email : VARCHAR
        +bio : TEXT
        +profile_picture_url : VARCHAR
        +website : VARCHAR
        +gender : VARCHAR
        +phone_number : VARCHAR
        +date_of_birth : DATE
        +updated_at : TIMESTAMP
        +getId()
        +getUsername()
        +setUsername()
        +getFullName()
        +setFullName()
        +getBio()
        +setBio()
        +getProfilePictureUrl()
        +setProfilePictureUrl()
        +getWebsite()
        +setWebsite()
        +getGender()
        +setGender()
        +getPhoneNumber()
        +setPhoneNumber()
        +getDateOfBirth()
        +setDateOfBirth()
    }

    class ICsdl {
        <<boundary>>
        +truyVanBangUsers()
        +capNhatBangUsers()
        +uploadObjectStorage()
    }

    CapNhatThongTinUI "1" -- "1" CapNhatThongTinController
    CapNhatThongTinController "1" -- "1" User
    CapNhatThongTinController "1" -- "*" ICsdl
```

**- Biểu đồ trình tự:**

```mermaid
sequenceDiagram
    actor KH as Người dùng
    participant UI as CapNhatThongTinUI
    participant CT as CapNhatThongTinController
    participant U as User
    participant DB as ICsdl

    KH->>UI: clickChinhSuaHoSo()
    UI->>CT: layThongTinHienTai()
    CT->>DB: truyVanBangUsers(userId)
    DB-->>U: thông tin user hiện tại
    U-->>CT: return User
    CT-->>UI: hienThiFormChinhSua()
    UI-->>KH: hiển thị form với dữ liệu hiện tại

    KH->>UI: nhapThongTinMoi()
    opt Đổi avatar
        KH->>UI: chonAnhAvatar()
        UI->>CT: uploadAvatar()
        CT->>DB: uploadObjectStorage()
        DB-->>CT: avatar_url
    end
    KH->>UI: clickLuu()
    UI->>CT: capNhatThongTin()
    CT->>CT: validateDuLieu()
    CT->>U: setFullName(), setBio(), setWebsite()
    U->>DB: capNhatBangUsers()
    DB-->>CT: return kết quả
    CT->>UI: hienThiThongBao("Cập nhật thành công")
    UI-->>KH: cập nhật giao diện
```

---

### 2.2.5.13. Use Case Duyệt Feed ảnh

**- Biểu đồ lớp phân tích:**

```mermaid
classDiagram
    class FeedUI {
        <<boundary>>
        +hienThiDanhSachBaiDang()
        +cuonXuongLoadMore()
        +keoXuongRefresh()
        +clickBaiDang()
        +hienThiFeedTrong()
    }

    class FeedController {
        <<control>>
        +layDanhSachFeed()
        +kiemTraCache()
        +taiThemBaiDang()
        +lamMoiFeed()
        +chuyenManHinh()
    }

    class Post {
        <<entity>>
        +id : BIGINT
        +user_id : BIGINT
        +caption : TEXT
        +location : VARCHAR
        +like_count : INT
        +comment_count : INT
        +created_at : TIMESTAMP
        +getId()
        +setId()
        +getUserId()
        +getCaption()
        +getLocation()
        +getLikeCount()
        +getCommentCount()
        +getCreatedAt()
    }

    class Follower {
        <<entity>>
        +follower_id : BIGINT
        +following_id : BIGINT
        +getFollowerId()
        +getFollowingId()
    }

    class ICsdl {
        <<boundary>>
        +truyVanBangPosts()
        +truyVanBangFollowers()
        +kiemTraRedisCache()
        +luuRedisCache()
    }

    FeedUI "1" -- "1" FeedController
    FeedController "1" -- "*" Post
    FeedController "1" -- "*" Follower
    FeedController "1" -- "*" ICsdl
```

**- Biểu đồ trình tự:**

```mermaid
sequenceDiagram
    actor KH as Người dùng
    participant UI as FeedUI
    participant CT as FeedController
    participant P as Post
    participant F as Follower
    participant DB as ICsdl

    KH->>UI: mở tab Home
    UI->>CT: layDanhSachFeed()
    CT->>DB: kiemTraRedisCache()
    alt Cache hit
        DB-->>CT: cached feed data
    else Cache miss
        CT->>DB: truyVanBangFollowers(userId)
        DB-->>F: danh sách following
        CT->>DB: truyVanBangPosts(followingIds)
        DB-->>P: danh sách bài đăng
        CT->>DB: luuRedisCache()
    end
    CT-->>UI: hienThiDanhSachBaiDang()
    UI-->>KH: hiển thị feed

    KH->>UI: cuonXuongLoadMore()
    UI->>CT: taiThemBaiDang(page + 1, limit)
    CT->>DB: truyVanBangPosts(page, limit)
    DB-->>P: bài đăng tiếp theo
    CT-->>UI: hienThiDanhSachBaiDang()
    UI-->>KH: thêm bài vào danh sách
```
