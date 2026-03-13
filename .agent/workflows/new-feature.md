---
description: Quy trình Phát triển Tính năng Mới (OpenSpec First)
---
# Mệnh Lệnh OpenSpec (Bắt Buộc)

> **Khi User yêu cầu:** "Làm cho tôi tính năng A" hoặc "Tạo API B". AI không bao giờ được phép lao vào code (Dùng thư mục `routes`, `services`, `repositories`) ngay lập tức. Đây là hành vi bị cấm.

## Quy Trình 2 Bươc:

**BƯỚC 1: BRAINSTORM & OPENSPEC (Lý Thuyết Trước)**
1. AI phải vào thư mục `docs/specs/` (tạo nếu chưa có).
2. Tạo một file Markdown mới, ví dụ: `api-tao-booking.md`.
3. Phác thảo **OpenSpec** (Cấu trúc API RESTful) bằng tiếng Việt rõ ràng, bao gồm:
   - Endpoint: `POST /api/v1/xyz`
   - Role truy cập: `USER`, `ADMIN`, hay `PUBLIC`?
   - Request Body: JSON mẫu (nếu POST/PUT) kèm lời giải thích validation.
   - Response Thành Công (200 OK): Cấu trúc JSON JSend mẫu cự thể lấy dữ liệu từ những schema bảng nào đã có? (Tham chiếu `instagallery_db_analysis.md`).
   - Response Lỗi (4xx, 5xx): Liet kê danh sách các Error Code (Ví dụ: `USER_NOT_FOUND`).
4. DỪNG LẠI (`notify_user`). Trình bày file Markdown vừa tạo cho người dùng và hỏi: *"Bạn có duyệt thiết kế API này không?"*

**BƯỚC 2: THỰC THI (Code Sau Cùng - BSRP Framework)**
1. NHẤT QUYẾT chờ đến khi User nói "Đồng ý" hoặc "Ok duyệt spec".
2. Bật workflow `create-api-feature` để chính thức đổ code theo quy chuẩn 4 Bước của ASH 2.0 (Table -> Repo -> Service -> Route).
