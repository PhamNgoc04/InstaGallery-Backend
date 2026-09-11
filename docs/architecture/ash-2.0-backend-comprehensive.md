# 📘 BỘ CHUẨN TOÀN DIỆN ASH 2.0 — PHIÊN BẢN BACKEND KTOR
# Agents + Skills + Hooks + Context Engineering + Brainstorming + OpenSpec + MCP
## Phiên Bản Đặc Thù Ktor • Cập Nhật: 2026-03-13

> **Bản Tuyên Ngôn Kỹ Thuật cho Ktor Backend** — Đây không chỉ là tài liệu hướng dẫn, đây là hệ thống "cấy ghép" tư duy cho AI. Dành riêng cho hệ thống Máy chủ Ktor, nơi bảo mật, tốc độ và tính toàn vẹn dữ liệu (Data Integrity) là sinh mạng.

---

# MỤC LỤC

**NỀN TẢNG KIẾN TRÚC MÁY CHỦ:**
1. [Tổng quan ASH 2.0 Backend](#-phần-0-tổng-quan-ash-20-backend)
2. [AGENTS — Bộ Não Máy Chủ (BSRP Lõi)](#-phần-1-agents--bộ-não-máy-chủ)
3. [SKILLS — Kỹ Năng Ktor Chuẩn Mực](#-phần-2-skills--kỹ-năng-chuyên-biệt)
4. [HOOKS — Bức Tường Lửa Bảo Vệ DB](#-phần-3-hooks--bảo-vệ--tự-động-hóa)

**TRÍ TUỆ NHÂN TẠO NÂNG CAO:**
5. [CONTEXT ENGINEERING — Thiết Kế Nhận Thức DB](#-phần-4-context-engineering--thiết-kế-nhận-thức-db)
6. [BRAINSTORMING — Thiết Kế Luồng Dữ Liệu](#-phần-5-brainstorming--bão-não-luồng-dữ-liệu)
7. [OPENSPEC — Đặc Tả API Tiêu Chuẩn](#-phần-6-openspec--phát-triển-theo-đặc-tả-api)
8. [MCP INTEGRATION — Giao Tiếp Ngoại Vi](#-phần-7-mcp--kết-nối-cơ-sở-dữ-liệu)

**QUY CHUẨN KỸ THUẬT:**
9. [Cấu trúc Thư Mục Vàng](#-phần-8-cấu-trúc-thư-mục-vàng-ktor)

---
---

# 🔷 PHẦN 0: TỔNG QUAN ASH 2.0 BACKEND

## Tại sao Backend cần ASH?
Frontend tập trung vào State (Trạng thái) và UI (Giao diện). Backend tập trung vào **Quy trình (Workflow), Dữ liệu (State of Truth) và Bảo mật (Security).** 
Việc thả rông AI code Backend mà không có ASH sẽ dẫn đến những thảm họa: Mất đồng bộ dữ liệu, SQL Injection, Logic rò rỉ ở tầng API.

## Kiến Trúc Lõi Của Agent (BSRP)
ASH Backend xoanh quanh một triết lý thiết kế tối cao duy nhất: **BSRP (Backend Architecture Standard: Service & Repository Pattern)**. Mọi thứ Agent làm phải xoay quanh 3 tầng vững chắc này.

---
---

# 🧠 PHẦN 1: AGENTS — Bộ Não Máy Chủ

> **AGENTS = Tập hợp các giới luật sinh tử mà AI PHẢI tuân theo khi chạm vào Backend.**

## 1.1. Core Tech Stack (Hệ Sinh Thái Ktor)
- **Framework Lõi:** Ktor 3.0+ (Gọn nhẹ, Coroutine-based).
- **ORM & Database:** Jetbrains Exposed (Kotlin DSL), MySQL, HikariCP Connection Pool.
- **Cache & Session:** Redis (Jedis).
- **Security:** JWT Authentication, BCrypt Hashing.
- **Serialization:** kotlinx.serialization (JSON).

## 1.2. The 3 Pillars of BSRP (Ba Trụ Cột Tuyệt Đối)

AI **bắt buộc** phải phân loại code vào 3 tầng này, cấm lai tạp:

### 🚫 Tầng 1: Routing Layer (`routes/`)
- **Vai trò:** Vệ sĩ cửa khẩu. Chỉ nhận HTTP, parse JSON, và bắt lỗi (Error Handling).
- **Tuyệt đối cấm:** KHÔNG gọi Database, KHÔNG băm mật khẩu, KHÔNG xử lý logic phức tạp. 
- **Quy tắc JSON:** Luôn trả về định dạng chuẩn JSend:
  `{ "success": true/false, "data": { ... }, "message": "Thông báo" }`

### 🧠 Tầng 2: Service Layer (`services/`)
- **Vai trò:** Khối óc kinh doanh (Business Logic). 
- **Nhiệm vụ:** Validate nghiệp vụ (Email này tồn tại chưa?), mã hóa mật khẩu, kiểm tra quyền truy cập, tính toán tiền bạc.

### 🗄️ Tầng 3: Repository Layer (`repositories/`)
- **Vai trò:** Công nhân hầm mỏ. Nơi DUY NHẤT được chạm vào SQL.
- **Tuyệt đối cấm:** Tầng này chỉ chứa các câu lệnh `Exposed` (insert, select, update...). Không quăng lỗi HTTP 404 ở tầng này. Trả dữ liệu sạch rách về chữ `Model` cho Service.

---
---

# 🎯 PHẦN 2: SKILLS — Kỹ Năng Ktor Chuyên Biệt

> **SKILLS = Cách AI từ chối "đoán mò" và code theo quy trình chuẩn.**

Khi được yêu cầu "Code API X", AI sẽ tự động kích hoạt Skill **`create-api-feature`** với 4 bước thép:

## Quy Trình 4 Bước Tạo 1 API Lõi:

### Bước 1: Table Definition (Tầng Đáy)
- Mở `database/tables/`. Tạo `Table Object` map 1:1 với Schema bằng Exposed.
- Ví dụ: `object UsersTable : Table("users") { val id = integer("id").autoIncrement() ... }`
- Chú ý ràng buộc Foreign Key chuẩn xác từ tài liệu thiết kế.

### Bước 2: Repository (Tầng Tương Tác CSDL)
- Tạo `UserRepository.kt`.
- **Nguyên Tắc Sống Còn:** Mọi hàm thay đổi dữ liệu (Insert/Update/Delete) phải được bọc trong vòng `transaction { ... }` của Exposed.

### Bước 3: Service (Tầng Xử Lý)
- Tạo `AuthService.kt`. Khởi tạo các DTOs Validation (LoginRequest, LoginResponse).
- Thực thi băm mật khẩu: `BCrypt.checkpw(request.password, user.passwordHash)`.

### Bước 4: Route (Tầng Giao Diện RESTful)
- Tạo `AuthRoutes.kt`. Đăng ký vào Application Module.
- Bọc toàn bộ route vào một khối `try/catch` hoặc cấu hình `StatusPages` ở `Application.kt` để bắt lỗi toàn cầu, tránh server sập khi crash 1 API.

---
---

# 🛡️ PHẦN 3: HOOKS — Bức Tường Lửa Bảo Vệ DB

> **HOOKS = Can thiệp cưỡng chế. Ranh giới giữa Code sạch và Sập Database.**

## 3.1. Submit Hook: SQL & Logging Guard
Khi AI sinh code, Hook tự động kiểm tra code xem có vi phạm:
- Có hàm `query` trực tiếp ID mà không qua Filter không? (Nguy cơ SQL Injection).
- Có in thẳng `PasswordHash` ra ngoài mảng `Response JSON` không? CẤM ĐỂ LỘ.
- Có log ra Token JWT của user ra Terminal không? CẤM.

## 3.2. Tool Hook: Chặn Lệnh Phá Hoại
Tệp `settings.json` của Agent luôn chứa rào cản:
- `Deny: Bash(rm -rf src/main/kotlin/*)`
- `Deny: Bash(mysql drop database)`
- `Allow: Bash(./gradlew build)`

## 3.3. Workflow Hook: Test Before Deploy
Không bao giờ push thẳng code lên `main` nếu chưa vượt qua Unit Test cho Service Layer.

---
---

# 🧪 PHẦN 4: CONTEXT ENGINEERING — Thiết Kế Nhận Thức DB

> **CONTEXT = Bạn phải cho AI nhìn thấy "Chiến Trường" trước khi ra lệnh nổ súng.**

**Vấn Đề Ở Backend:** Có quá nhiều bảng cơ sở dữ liệu (Ví dụ: Đồ án hiện có 33 bảng). AI không thể đoán được bảng `users` nối với bảng `posts` kiểu gì nếu không có Context.

## Chiến Lược Tiêm Ngữ Cảnh Chuyên Sâu (Context Injection):

Trước khi code tính năng X, AI **bắt buộc** phải nạp 3 file sau từ thư mục tài liệu dự án:
1. `instagallery_db_analysis.md` (Để biết bảng có cột gì, kiểu dữ liệu gì).
2. `instagallery_complete_system.md` (Để nắm luồng hoạt động Use Case FR-XX liên quan).
3. `instagallery_ktor_roadmap.md` (Tài liệu Roadmap kiến trúc Ktor).

**Công thức Prompt (The Trigger):**
*"AI, chuẩn bị code API Tạo Đơn Đặt Lịch (Booking). Hãy đọc file sơ đồ CSDL để hiểu bảng `bookings` nối với `photographers` và `users` ra sao trước khi viết Repository."*

---
---

# 💡 PHẦN 5: BRAINSTORMING — Bão Não Luồng Dữ Liệu

> **BRAINSTORMING = Hãy tư duy Dữ Liệu, đừng tư duy Giao Diện.**

Ở Backend, khi đối mặt với một Logic mới. Bắt buộc phải Brainstorm về State Machine (Máy trạng thái) và Dòng Cầm Máu Dữ liệu (ACID).

**Kỹ Thuật Brainstorming State Machine:**
Ví dụ với Tính năng Booking Nhiếp ảnh gia, AI phải vẽ ra giấy các trạng thái:
`PENDING -> ACCEPTED -> COMPLETED -> REVIEWED`
Và `PENDING -> REJECTED`.
- Câu hỏi đặt ra: "Nếu Photogapher bị Khóa tài khoản khi Đơn đang ở `PENDING` thì xử lý sao?"
- AI và Người cùng thảo luận -> Chốt Logic -> Mới bước sang phần OpenSpec.

---
---

# 📋 PHẦN 6: OPENSPEC — Đặc Tả API Tiêu Chuẩn

> **OPENSPEC = Chưa có hợp đồng (Spec), thì chưa được đổ móng (Code).**

Mọi API phải được chốt thiết kế bằng Markdown RESTful Spec trước khi được gõ file `.kt` đầu tiên.

### Template OpenSpec Chuẩn Mực (`specs/api-login.md`):

```markdown
# API Định Nghĩa: Đăng Nhập Hệ Thống

**1. Thông tin chung**
- Cụm: `Auth`
- Endpoint: `POST /api/v1/auth/login`
- Access: Public
- Mô tả: Mở khóa truy cập bằng Email và Mật khẩu, trả về JWT.

**2. Request (JSON)**
```json
{
  "email": "user@example.com",
  "password": "Mật khẩu thuần (sẽ tự mã hóa dưới ngầm)"
}
```

**3. Response Thành Công (200 OK)**
```json
{
  "success": true,
  "message": "Đăng nhập thành công",
  "data": {
    "token": "eyJhbGci... (Bearer JWT)",
    "expires_at": 1718000000,
    "user_id": 1,
    "role": "MEMBER"
  }
}
```

**4. Response Lỗi (400 / 401)**
- `USER_NOT_FOUND`
- `WRONG_PASSWORD`
- `ACCOUNT_LOCKED`
```
*(Chỉ khi nào OpenSpec này được duyệt, AI mới tiến hành SKILL 4 bước)*

---
---

# 🔌 PHẦN 7: MCP INTEGRATION — Giao Tiếp Ngoại Vi

Backend thường xuyên kết nối với Storage (AWS S3) và Cache (Redis). Sử dụng MCP File System để thao tác với các dịch vụ mock (giả lập) ở Local môi trường nhằm kiểm tra.
- Redis Connection: Kiểm tra mã OTP lưu tạm thời.
- SQL Log: Kiểm tra các truy vấn Exposed SQL Logging xem có hiện tượng "N+1 Query Issue" khi lấy danh sách Bài Viết kèm bình luận hay không.

---
---

# 📁 PHẦN 8: CẤU TRÚC THƯ MỤC VÀNG KTOR (ASH Compliant)

```text
instagallery-backend/
├── docs/                     
│   ├── ash-2.0-backend-comprehensive.md  ← [TÀI LIỆU NÀY] Bạn đang ở đây
│   └── specs/                            ← [OPENSPEC] Chứa đặc tả Markdown JSON API
│
├── .agent/                               ← Thư mục AI Agent (Hooks, Skills JSON)
│
├── src/main/kotlin/com/instagallery/
│   ├── Application.kt                    ← Entry Point cấu hình Netty Server
│   ├── plugins/                          ← Setup JWT, CORS, Database, JSON, Routing, RateLimiting, StatusPages
│   ├── routes/                           ← [PILLAR 1] Controllers (REST API endpoints)
│   ├── services/                         ← [PILLAR 2] Business Logic / Validation / Auth
│   ├── repositories/                     ← [PILLAR 3] Nhận tham số từ Service, DB Queries (kết nối với Table)
│   ├── database/                         
│   │   └── tables/                       ← [CONTEXT] Cấu trúc bảng SQL bằng Jetbrains Exposed
│   ├── models/                           
│   │   ├── request/                      ← Request DTOs
│   │   ├── response/                     ← Response DTOs
│   │   └── common/                       ← Common classes (VD: ApiResponse)
│   └── utils/                            ← Hàm Helper tiện ích mã hóa, JWT Manager
│
└── build.gradle.kts                      ← Chứa danh sách Dependency.
```

---
**Đây là Sách Giáo Khoa tối thượng của Antigravity khi code Backend Ktor. Code như Kỹ sư Enterprise, bảo mật như Ngân hàng!**
