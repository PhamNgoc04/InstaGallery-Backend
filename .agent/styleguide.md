# Ktor Backend Styleguide & Conventions

**1. Architecture Paradigm**
- Tuân thủ cực đoan cấu trúc **BSRP** (Routing -> Service -> Repository).
- Không bao giờ gọi DB từ Routing. Không bao giờ validate JSON từ Repository.

**2. Error Handling**
- Sử dụng Custom Exception: `ValidationException`, `AuthException`, `NotFoundException`.
- Không bọc `try-catch` lồng nhau ở Route. Quăng Exception cho Ktor `StatusPages` plugin xử lý tự động thành JSON BaseResponse chuẩn.

**3. Database Interaction (Exposed)**
- Chỉ dùng **DSL API** (bọc trong object), hạn chế tối đa DAO (Class-based).
- Mọi hàm thay đổi DB (Insert/Update) bắt buộc phải bọc trong `dbQuery { ... }` (suspend transaction) từ `DatabaseFactory`.

**4. Code Style & Naming**
- DTO (Request/Response): Đặt ở `models/request` hoặc `models/response` với hậu tố rõ ràng (Ví dụ: `LoginRequest`, `UserDto`).
- Table Schema: Đặt hậu tố `Table` (Ví dụ: `UsersTable`).

**5. Clean Code**
- Đảm bảo tính Immutable: Dùng `val` ở mọi nơi có thể.
- Tuyệt đối không hardcode mật khẩu hay secret, đọc từ `environment.config`.
