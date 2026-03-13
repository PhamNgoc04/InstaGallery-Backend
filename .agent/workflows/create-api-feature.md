---
description: create-api-feature
---
# Skill: Create API Feature (ASH 2.0 Ktor Standard)

Khi được yêu cầu tạo một tính năng API mới, bạn BẮT BUỘC phải tuân thủ nghiêm ngặt BSRP (Backend Architecture Standard: Service & Repository Pattern) và thực hiện 4 bước sau:

### Bước 1: Table Definition (`database/tables/`)
* Mở thư mục `src/main/kotlin/com/instagallery/database/tables`. 
* Tạo `Table Object` map 1:1 với Schema bằng Jetbrains Exposed (thừa kế `Table` hoặc `IdTable`).
* Chú ý thiết lập đúng các ràng buộc Foreign Key và Unique Index.

### Bước 2: Repository (`repositories/`)
* Tạo lớp Repository tương ứng trong `src/main/kotlin/com/instagallery/repositories/`.
* **Nguyên Tắc Sống Còn:** Mọi hàm thay đổi dữ liệu (Insert/Update/Delete) phải được bọc trong vòng `newSuspendedTransaction(Dispatchers.IO) { ... }` của Exposed.
* Tầng này chỉ được phép trả ra Model, Entity hoặc DTO, KHÔNG được throw HTTP Exception.

### Bước 3: Service (`services/`)
* Tạo lớp Service tương ứng trong `src/main/kotlin/com/instagallery/services/`.
* Thực hiện Validation nghiệp vụ (VD: Kiểm tra tính tồn tại, logic dòng tiền).
* Chỉ ném các Custom Exception quy định sẵn ở `StatusPages` (`AuthException`, `ValidationException`, `NotFoundException`, `ForbiddenException`).
* Gọi Repository để xử lý dữ liệu.

### Bước 4: Route (`routes/`)
* Tạo file Route trong `src/main/kotlin/com/instagallery/routes/`.
* Parse JSON request payload bằng `call.receive()`.
* Chặn xử lý logic kinh doanh ở tầng này. Gọi trực tiếp hàm từ Service.
* Trả kết quả bọc chung trong `ApiResponse.success(result)` hoặc để `StatusPages` tự động bắt lổi trả về `ApiResponse.error()`.
