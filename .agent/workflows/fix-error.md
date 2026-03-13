---
description: Sửa lỗi & Debugging theo Tri thức (Knowledge Base)
---
# Fix Error Protocol (Học hỏi & Rà soát)

Khi AI code sai một lệnh, hoặc User copy dán báo một Exception dài (VD: `MethodNotFound`, `Cannot parse json...`):

1. **KHÔNG HOẢNG LOẠN TRA GOOGLE HOẶC TỰ ĐOÁN ĐIÊN CUỒNG.**
2. Đầu tiên, gọi tool đọc file `docs/knowledge-base/error-log.md` để xem dự án này đã bao giờ gặp lỗi tương tự và có cách giải quyết (Workaround) đã được chốt trước đó chưa.
   - Ví dụ: Lỗi Ktor Serialize Class — Kiểm tra xem data class đã có annotation `@Serializable` chưa?
   - Ví dụ: Lỗi Date formatting của Ktor — Đã có TypeAdapter chưa?
3. Nếu CÓ trong Knowledge Base -> Bê nguyên cách giải quyết ra.
4. Nếu CHƯA CÓ trong Knowledge Base -> Tự mò tìm và sửa lỗi. Sau khi được User xác nhận là CHẠY ĐƯỢC -> **BẮT BUỘC GHI NHẬN LẠI VÀO `error-log.md`** để làm giàu "Bản sắc dự án".
