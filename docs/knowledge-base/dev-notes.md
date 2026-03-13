# Quyết Định Kỹ Thuật (Architecture Decision Records - ADR)

> Ghi nhận lại các màn "Brainstorming" đã đi đến phương án chốt tóm lược để có History.

## QĐ 01: Cách xử lý Cascade Delete trong Ktor Exposed (2026-03-13)
- **Vấn đề:** Khi Drop bảng hoặc Xóa 1 Khách hàng, bài đăng của họ bị mồ côi.
- **Quyết định:** Gắn thẳng chế độ của Engine SQL: `onDelete = ReferenceOption.CASCADE` trong Jetbrains Exposed khai báo. Nếu là xóa mềm, dùng trường cấu trúc `deleted_at: timestamp`.

## QĐ 02: BSRP Flow Enforcer (2026-03-13)
- **Vấn đề:** AI hay code gộp SQL trực tiếp vào endpoint Routes.
- **Quyết định:** Áp dụng Workflow `.agent/create-api-feature.md` & `new-feature.md` của ASH 2.0. Phạt cách ly SQL khỏi Route layer. Mọi DB action thông qua transaction suspend trong repo.
