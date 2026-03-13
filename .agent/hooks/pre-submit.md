# 🛡️ HOOK: Bức Tường Lửa Tự Động Đầu Ra (Pre-Submit)

> **Mô tả:** Hook này là Check-list TỰ ĐỘNG yêu cầu AI rà soát code của mình trước khi coi là hoàn thành task và push lên nhánh/kết thúc workflow.

## 1. Scan Mã Hóa & Lộ Lọt Dữ Liệu
AI bắt buộc trả lời các câu hỏi sau với chính mình:
- [ ] Tôi có lỡ gán `password_hash` vào Data Transfer Object (DTO) Response trả về Client không? *(Nếu có: Sửa ngay lập tức, sử dụng hàm `.toDto()` để loại bỏ trường này).*
- [ ] Tôi có `println(token)` JWT hay `println(secretKey)` ra system logs không? *(Nếu có: Phải xóa sạch statement log debug).*

## 2. Scan BSRP Layer Breach (Phá vỡ cấu trúc)
- [ ] Có câu lệnh `transaction {}` hay `UsersTable.insert {}` nào đang nằm trong thư mục `routes/` hay trong `services/` không? *(Nếu có: Refactor đẩy nó xuống `repositories/`).*

## 3. Quét SQL Injection (SQL Guard)
- [ ] Có bất kỳ câu lệnh SQL raw nào dùng dạng nối chuỗi `.exec("SELECT * FROM users WHERE username = '" + username + "'")` không? *(Bắt buộc dùng tính năng gán parameter của Exposed hoặc DSL query `.eq()`).*

---
**Quy tắc Cưỡng chế:** AI tuyệt đối không được Notify cho User rằng Task Hoàn Thành nếu chưa thầm lặng thực thi quá trình kiểm tra 3 điểm trên.
