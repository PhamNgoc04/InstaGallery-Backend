# 📘 BỘ CHUẨN TOÀN DIỆN ASH 2.0 DÀNH CHO BACKEND (KTOR)
# Agents + Skills + Hooks + Context Engineering + Brainstorming + OpenSpec + BSRP

> **Đây là biên bản "Nhập gia tùy tục" của ASH 2.0** — Dành riêng cho hệ thống Backend Ktor của InstaGallery, nhưng vẫn giữ nguyên "Linh hồn" 8 trụ cột của triết lý AI Coding hiện đại (Agents, Skills, Hooks...).
> BSRP (Backend Architecture Standard: Service & Repository Pattern) giờ đây không đứng độc lập mà là **Trái tim** của phần OpenSpec và Skills trong chuẩn ASH.

---

# MỤC LỤC
1. [NỀN TẢNG (ASH 1.0) cho Backend](#-phần-1-nền-tảng-ash-10-cho-backend)
2. [CỘT MỐC MỚI (ASH 2.0) cho Ktor](#-phần-2-cột-mốc-mới-ash-20-cho-ktor)
3. [KIẾN TRÚC LÕI (BSRP) Tích Hợp Vào SKILLS](#-phần-3-kiến-trúc-lõi-bsrp-tích-hợp-vào-skills)
4. [QUY TẮC BẢO MẬT & API (HOOKS)](#-phần-4-quy-tắc-bảo-mật--api-hooks)

---

# 🧠 PHẦN 1: NỀN TẢNG (ASH 1.0) CHO BACKEND

## 1.1. AGENTS — Bộ Não Ktor
Giống như Android, Backend cũng cần "Hiến Pháp". Mọi AI Agent khi làm việc trong thư mục `instagallery-backend` **BẮT BUỘC** phải tuân thủ:

- **Công nghệ lõi:** Kotlin 2.0, Ktor 3.0.2, Exposed (ORM), HikariCP.
- **Quy tắc tuyệt đối (DO):**
  - LUÔN LUÔN xử lý Exception tập trung tại StatusPages.
  - LUÔN LUÔN chia file theo chức năng (Auth, Posts, Users) chứ không để phình to `Application.kt`.
  - Áp dụng triệt để mô hình BSRP: `Route` -> `Service` -> `Repository`.
- **Cấm kỵ (DON'T):**
  - KHÔNG được gọi cơ sở dữ liệu trực tiếp trong tầng `Route`.
  - KHÔNG trả về lỗi 500 do NullPointerException (`!!`).
  - KHÔNG để lộ Error Query SQL ra ngoài màn hình API (Tránh SQL Injection & Leak config).

## 1.2. SKILLS — Kỹ Năng Code Ktor Chuẩn Xác
Trong ASH 2.0, bạn không gõ *"Viết tính năng đăng nhập đi"*. Mà bạn sẽ gọi SKILL: **`create-backend-feature`**.

**Luồng chạy chuẩn của Skill Backend:**
- **Bước 1 (Repository):** Tạo `Exposed Table Object` (VD: `UsersTable`) -> Tạo DAO -> Viết các hàm query DB bảo mật.
- **Bước 2 (Service):** Tạo class `AuthService`. Hash/Verify Password bằng BCrypt. Kiểm tra Logic kinh doanh (VD: Email đã tồn tại chưa?).
- **Bước 3 (Route):** Mở API Endpoint (RESTful). Parse DTO Request JSON từ ngoài vào. Hứng kết quả từ Service và đẩy ra JSON chuẩn JSend (luôn có status, message, data).

## 1.3. HOOKS — Cản Thiệp & Bảo Vệ DB
Các Hooks không cho phép AI làm càn trên Database:
- **Tool Hook:** Chặn AI tự ý gõ lệnh `rm -rf` hoặc `DROP DATABASE` trong Terminal.
- **Submit Hook:** Bất cứ khi nào tôi code chức năng sửa xóa dữ liệu, Submit Hook buộc tôi phải tự kiểm tra xem code đó có được bọc trong vòng `transaction { ... }` của Exposed hay chưa (tránh Data loss do crash server giữa chừng).

---

# 🧪 PHẦN 2: CỘT MỐC MỚI (ASH 2.0) CHO KTOR

## 2.1. CONTEXT ENGINEERING (Thiết kế bối cảnh)
Đồ án của bạn có 33 bảng CSDL. AI không thể tự nghĩ ra.
**Chiến lược Context:** Trước khi tôi code bất cứ cụm API nào, CẤM tôi nhảy vào code luôn mà bắt buộc phải:
1. Đọc file `instagallery_complete_system.md` từ ổ C:\ OneDrive của bạn.
2. Nắm rõ liên kết Khóa chính - Khóa ngoại (1-N hay N-N).
3. Sau đó mới thiết kế Entity DB. 

*Tuyệt chiêu này giúp Backend của bạn ăn khớp 100% với tài liệu Báo cáo Word.*

## 2.2. OPENSPEC (Thiết kế API trước, gõ code sau)
AI không tự ý cắm đầu vào IDEA gõ code ngay.
- Phải vẽ ra tài liệu Đặc tả API (`api-spec.md`) trước mặt bạn. 
- Bao gồm: Method (GET/POST), URL (`/api/v1/...`), Body truy vấn mẫu (JSON), Response trả về mẫu (JSON).
- Bạn duyệt (Ok) -> AI mới tạo thư mục code.

---

# 🏛 PHẦN 3: KIẾN TRÚC LÕI (BSRP) TÍCH HỢP VÀO SKILLS

BSRP (Backend Architecture Standard: Service & Repository Pattern) là quy tắc bắt buộc áp dụng cho mọi đoạn code AI sinh ra:

### Pillar 1: Routing Layer (`routes/`)
- Nhận HTTP Request từ Mobile/Web, Parse JSON.
- Cấm chứa Business logic (cấm kiểm tra email trùng, cấm gọi DB).
- Phải ủy quyền (delegate) xử lý cho `Service`.

### Pillar 2: Service Layer (`services/`)
- Mạch máu của ứng dụng (Ví dụ: `AuthService`).
- Kiểm tra quyền, check tồn tại, mã hóa mật khẩu.
- Kết nối nhiều Repositories nếu logic phức tạp (Vừa gọi Redis verify OTP, vừa gọi MySQL insert Data).

### Pillar 3: Repository Layer (`repositories/`)
- Nơi **duy nhất** được gọi `transaction` và mã SQL Exposed.
- CRUD thuần túy. Trả dữ liệu Data Class sạch về cho Service.

---

# 🛡 PHẦN 4: QUY TẮC BẢO MẬT & API (HOOKS MỞ RỘNG)

1. **JSend Standard:** Chuẩn hóa toàn bộ phản hồi HTTP 200/201:
   ```json
   {
     "success": true,
     "message": "Chi tiết bằng tiếng việt (tuỳ chọn)",
     "data": { ... }
   }
   ```
2. **Never Trust User Input:** Validation tầng Route là Bắt buộc (Bỏ trống string, Email sai định dạng...).
3. **Soft Delete:** Không viết lệnh `delete` phần tử mang tính "tài sản" như Post, User ở Repository. LUÔN thêm trường `is_deleted = true`.
4. **JWT Flow:** Việc sinh token (genToken) xử lý tại Util / Service riêng biệt. Header gởi về có Bearer Token.
