## --- Nhóm: Chat ---

### 2.2.5.37. Use Case Nhắn tin (Chat)

**- Biểu đồ lớp phân tích:**

```mermaid
classDiagram
    class ChatUI {
        <<boundary>>
        +hienThiDanhSachConversation()
        +hienThiLichSuChat()
        +nhapTinNhan()
        +clickGuiTinNhan()
        +hienThiTinNhanMoi()
        +hienThiUnreadBadge()
    }

    class ChatController {
        <<control>>
        +kiemTraConversationTonTai()
        +taoConversationMoi()
        +layLichSuTinNhan()
        +guiTinNhan()
        +ketNoiWebSocket()
        +nhanTinNhanRealtime()
        +capNhatLastReadAt()
    }

    class Conversation {
        <<entity>>
        +id : BIGINT
        +type : ENUM
        +updated_at : TIMESTAMP
        +created_at : TIMESTAMP
        +getId()
        +setId()
        +getType()
        +setType()
        +getUpdatedAt()
        +setUpdatedAt()
    }

    class ConversationMember {
        <<entity>>
        +conversation_id : BIGINT
        +user_id : BIGINT
        +last_read_at : TIMESTAMP
        +is_muted : BOOLEAN
        +getConversationId()
        +getUserId()
        +getLastReadAt()
        +setLastReadAt()
    }

    class Message {
        <<entity>>
        +id : BIGINT
        +conversation_id : BIGINT
        +sender_id : BIGINT
        +content : TEXT
        +message_type : ENUM
        +created_at : TIMESTAMP
        +getId()
        +getConversationId()
        +getSenderId()
        +getContent()
        +setContent()
        +getMessageType()
        +setMessageType()
    }

    class ICsdl {
        <<boundary>>
        +truyVanBangConversations()
        +themBangConversations()
        +themBangConversationMembers()
        +truyVanBangMessages()
        +themBangMessages()
        +capNhatBangConversations()
        +capNhatBangConversationMembers()
    }

    ChatUI "1" -- "1" ChatController
    ChatController "1" -- "*" Conversation
    ChatController "1" -- "*" ConversationMember
    ChatController "1" -- "*" Message
    ChatController "1" -- "*" ICsdl
```

**- Biểu đồ trình tự:**

```mermaid
sequenceDiagram
    actor U1 as Người gửi
    participant UI as ChatUI
    participant CT as ChatController
    participant CV as Conversation
    participant CM as ConversationMember
    participant M as Message
    participant DB as ICsdl

    U1->>UI: click "Nhắn tin" trên profile
    UI->>CT: kiemTraConversationTonTai(userId)
    CT->>DB: truyVanBangConversations()
    alt Chưa có conversation
        CT->>CV: createConversation(DIRECT)
        CV->>DB: themBangConversations()
        CT->>CM: createMembers()
        CM->>DB: themBangConversationMembers()
    else Đã có
        DB-->>CT: conversation hiện có
    end

    UI->>CT: layLichSuTinNhan(convId)
    CT->>DB: truyVanBangMessages(convId)
    DB-->>M: danh sách tin nhắn
    CT-->>UI: hienThiLichSuChat()
    UI-->>U1: hiển thị lịch sử chat

    CT->>CT: ketNoiWebSocket()

    U1->>UI: nhapTinNhan()
    U1->>UI: clickGuiTinNhan()
    UI->>CT: guiTinNhan(content)
    CT->>M: createMessage(TEXT)
    M->>DB: themBangMessages()
    CT->>DB: capNhatBangConversations(updated_at)
    CT->>CT: broadcastWebSocket()
    CT-->>UI: hienThiTinNhanMoi()
    UI-->>U1: bubble phải (tin nhắn đã gửi)
    Note over UI: Người nhận nhận tin qua WebSocket
```

---

## --- Nhóm: Quản trị ---

### 2.2.5.42. Use Case Quản lý người dùng

**- Biểu đồ lớp phân tích:**

```mermaid
classDiagram
    class QuanLyNguoiDungUI {
        <<boundary>>
        +hienThiDanhSachUser()
        +timKiemUser()
        +hienThiChiTietUser()
        +clickKhoaTaiKhoan()
        +clickMoKhoa()
        +clickXacMinh()
        +clickXoa()
        +hienThiThongBao()
    }

    class QuanLyNguoiDungController {
        <<control>>
        +layDanhSachUser()
        +timKiemUser()
        +khoaTaiKhoan()
        +moKhoaTaiKhoan()
        +xacMinhTaiKhoan()
        +xoaTaiKhoan()
        +ghiLogHoatDong()
    }

    class User {
        <<entity>>
        +id : BIGINT
        +username : VARCHAR
        +email : VARCHAR
        +role : ENUM
        +is_active : BOOLEAN
        +is_verified : BOOLEAN
        +deleted_at : TIMESTAMP
        +created_at : TIMESTAMP
        +getIsActive()
        +setIsActive()
        +getIsVerified()
        +setIsVerified()
        +getDeletedAt()
        +setDeletedAt()
    }

    class UserSession {
        <<entity>>
        +id : BIGINT
        +user_id : BIGINT
        +getUserId()
    }

    class ActivityLog {
        <<entity>>
        +id : BIGINT
        +user_id : BIGINT
        +action : VARCHAR
        +target_type : ENUM
        +target_id : BIGINT
        +ip_address : VARCHAR
        +created_at : TIMESTAMP
        +setAction()
        +setTargetType()
        +setTargetId()
    }

    class ICsdl {
        <<boundary>>
        +truyVanBangUsers()
        +capNhatBangUsers()
        +xoaBangUserSessions()
        +themBangActivityLogs()
    }

    QuanLyNguoiDungUI "1" -- "1" QuanLyNguoiDungController
    QuanLyNguoiDungController "1" -- "*" User
    QuanLyNguoiDungController "1" -- "*" UserSession
    QuanLyNguoiDungController "1" -- "*" ActivityLog
    QuanLyNguoiDungController "1" -- "*" ICsdl
```

**- Biểu đồ trình tự:**

```mermaid
sequenceDiagram
    actor A as Admin
    participant UI as QuanLyNguoiDungUI
    participant CT as QuanLyNguoiDungController
    participant U as User
    participant US as UserSession
    participant AL as ActivityLog
    participant DB as ICsdl

    A->>UI: truy cập "Quản lý người dùng"
    UI->>CT: layDanhSachUser()
    CT->>DB: truyVanBangUsers()
    DB-->>U: danh sách users
    CT-->>UI: hienThiDanhSachUser()
    UI-->>A: hiển thị danh sách (filter, phân trang)

    A->>UI: chọn user → click "Khóa"
    UI->>CT: khoaTaiKhoan(userId)
    CT->>U: setIsActive(FALSE)
    U->>DB: capNhatBangUsers()
    CT->>US: xoaPhienDangNhap(userId)
    US->>DB: xoaBangUserSessions()
    CT->>AL: createLog(BAN_USER)
    AL->>DB: themBangActivityLogs()
    DB-->>CT: return kết quả
    CT-->>UI: hienThiThongBao("User đã bị khóa")
    UI-->>A: cập nhật giao diện
```

---

### 2.2.5.43. Use Case Kiểm duyệt nội dung

**- Biểu đồ lớp phân tích:**

```mermaid
classDiagram
    class KiemDuyetUI {
        <<boundary>>
        +hienThiDanhSachReport()
        +hienThiChiTietReport()
        +clickXoaNoiDung()
        +clickGiaiQuyet()
        +clickTuChoi()
        +nhapGhiChu()
        +hienThiThongBao()
    }

    class KiemDuyetController {
        <<control>>
        +layDanhSachReport()
        +layChiTietReport()
        +xoaNoiDungViPham()
        +giaiQuyetReport()
        +tuChoiReport()
    }

    class Report {
        <<entity>>
        +id : BIGINT
        +reporter_id : BIGINT
        +target_type : ENUM
        +target_id : BIGINT
        +reason : TEXT
        +admin_note : TEXT
        +reviewed_by : BIGINT
        +status : ENUM
        +created_at : TIMESTAMP
        +getId()
        +getTargetType()
        +getTargetId()
        +getReason()
        +getStatus()
        +setStatus()
        +setAdminNote()
        +setReviewedBy()
    }

    class Post {
        <<entity>>
        +id : BIGINT
        +deleted_at : TIMESTAMP
        +setDeletedAt()
    }

    class Comment {
        <<entity>>
        +id : BIGINT
        +deleted_at : TIMESTAMP
        +setDeletedAt()
    }

    class ICsdl {
        <<boundary>>
        +truyVanBangReports()
        +capNhatBangReports()
        +capNhatBangPosts()
        +capNhatBangComments()
    }

    KiemDuyetUI "1" -- "1" KiemDuyetController
    KiemDuyetController "1" -- "*" Report
    KiemDuyetController "1" -- "*" Post
    KiemDuyetController "1" -- "*" Comment
    KiemDuyetController "1" -- "*" ICsdl
```

**- Biểu đồ trình tự:**

```mermaid
sequenceDiagram
    actor A as Admin
    participant UI as KiemDuyetUI
    participant CT as KiemDuyetController
    participant R as Report
    participant P as Post
    participant DB as ICsdl

    A->>UI: mở "Kiểm duyệt nội dung"
    UI->>CT: layDanhSachReport()
    CT->>DB: truyVanBangReports(status=PENDING)
    DB-->>R: danh sách reports
    CT-->>UI: hienThiDanhSachReport()
    UI-->>A: hiển thị danh sách (PENDING ưu tiên)

    A->>UI: click report → xem chi tiết
    UI->>CT: layChiTietReport(reportId)
    CT-->>UI: hienThiChiTietReport()
    UI-->>A: nội dung bị báo cáo, lý do, reporter

    alt Nội dung vi phạm
        A->>UI: clickXoaNoiDung()
        UI->>CT: xoaNoiDungViPham(postId)
        CT->>P: setDeletedAt(NOW)
        P->>DB: capNhatBangPosts()
        A->>UI: clickGiaiQuyet()
        UI->>CT: giaiQuyetReport(reportId)
        CT->>R: setStatus(RESOLVED)
        R->>DB: capNhatBangReports()
    else Không vi phạm
        A->>UI: clickTuChoi()
        UI->>CT: tuChoiReport(reportId)
        CT->>R: setStatus(DISMISSED)
        R->>DB: capNhatBangReports()
    end
    DB-->>CT: return kết quả
    CT-->>UI: hienThiThongBao("Đã xử lý")
    UI-->>A: cập nhật danh sách
```

---

### 2.2.5.44. Use Case Thống kê hệ thống

**- Biểu đồ lớp phân tích:**

```mermaid
classDiagram
    class ThongKeUI {
        <<boundary>>
        +hienThiDashboard()
        +hienThiSoLieuTong()
        +hienThiBieuDoTangTruong()
        +hienThiThongKeChiTiet()
        +chonKhoangThoiGian()
    }

    class ThongKeController {
        <<control>>
        +laySoLieuTong()
        +layDuLieuTangTruong()
        +layThongKeChiTiet()
        +tinhPhanBoRole()
        +tinhTrangThaiBooking()
    }

    class User {
        <<entity>>
        +id : BIGINT
        +role : ENUM
        +created_at : TIMESTAMP
        +getRole()
        +getCreatedAt()
    }

    class Post {
        <<entity>>
        +id : BIGINT
        +created_at : TIMESTAMP
        +getCreatedAt()
    }

    class Booking {
        <<entity>>
        +id : BIGINT
        +status : ENUM
        +created_at : TIMESTAMP
        +getStatus()
        +getCreatedAt()
    }

    class Report {
        <<entity>>
        +id : BIGINT
        +status : ENUM
        +getStatus()
    }

    class ICsdl {
        <<boundary>>
        +demBangUsers()
        +demBangPosts()
        +demBangBookings()
        +demBangReports()
        +truyVanTangTruongUsers()
        +truyVanTangTruongPosts()
    }

    ThongKeUI "1" -- "1" ThongKeController
    ThongKeController "1" -- "*" User
    ThongKeController "1" -- "*" Post
    ThongKeController "1" -- "*" Booking
    ThongKeController "1" -- "*" Report
    ThongKeController "1" -- "*" ICsdl
```

**- Biểu đồ trình tự:**

```mermaid
sequenceDiagram
    actor A as Admin
    participant UI as ThongKeUI
    participant CT as ThongKeController
    participant U as User
    participant P as Post
    participant B as Booking
    participant R as Report
    participant DB as ICsdl

    A->>UI: truy cập Dashboard
    UI->>CT: laySoLieuTong()
    CT->>DB: demBangUsers()
    DB-->>U: tổng users
    CT->>DB: demBangPosts()
    DB-->>P: tổng posts
    CT->>DB: demBangBookings()
    DB-->>B: tổng bookings
    CT->>DB: demBangReports(PENDING)
    DB-->>R: reports đang chờ
    CT-->>UI: hienThiSoLieuTong()
    UI-->>A: hiển thị tổng users, posts, bookings, reports

    UI->>CT: layDuLieuTangTruong(30days)
    CT->>DB: truyVanTangTruongUsers()
    DB-->>CT: users mới theo ngày
    CT->>DB: truyVanTangTruongPosts()
    DB-->>CT: posts mới theo ngày
    CT-->>UI: hienThiBieuDoTangTruong()
    UI-->>A: hiển thị biểu đồ tăng trưởng

    UI->>CT: layThongKeChiTiet()
    CT->>CT: tinhPhanBoRole()
    CT->>CT: tinhTrangThaiBooking()
    CT-->>UI: hienThiThongKeChiTiet()
    UI-->>A: phân bố role, status bookings, top users
```
