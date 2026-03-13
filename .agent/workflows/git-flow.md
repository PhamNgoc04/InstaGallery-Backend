---
description: Quy trình Quản lý Nhánh Git chuyên nghiệp (Git Flow) cho InstaGallery
---
# Git Flow Workflow - InstaGallery Backend

Dự án này áp dụng mô hình phân nhánh **Git Flow** tiêu chuẩn thiết kế cho Production. Đây là cách các công ty công nghệ lớn quản lý mã nguồn, giảm rủi ro conflict và đảm bảo tính ổn định.

## Cấu trúc Nhánh (Branches)

1. **`main`**: Nhánh Production. Chứa code đã được live/deploy. Mã nguồn ở đây LUÔN CHẠY ĐƯỢC và ổn định 100%. Không bao giờ code trực tiếp vào đây.
2. **`develop`**: Nhánh Integration (Phát triển chính). Mọi tính năng mới sẽ được merge vào đây trước. Khi `develop` đã ổn định, nó sẽ được merge sang `main` qua một Release.
3. **`feature/*`**: Dùng để phát triển tính năng mới. Tách ra từ `develop` và merge ngược lại vào `develop`.
   - Ví dụ: `feature/login-google`, `feature/payment-gateway`
4. **`hotfix/*`**: Dùng để sửa lỗi KHẨN CẤP trên môi trường Production. Tách ra từ `main`, và phải được merge vào cả `main` lẫn `develop`.
   - Ví dụ: `hotfix/fix-null-pointer-login`
5. **`release/*`** (Tùy chọn): Dùng để đóng gói version chuẩn bị đưa lên Production. Tách ra từ `develop`.

## Quy trình làm việc hàng ngày (Daily Workflow)

### 1. Khi bắt đầu làm một tính năng mới (Ví dụ: Thêm API Upload Avatar)

**Bước 1: Chuyển sang nhánh develop và lấy code mới nhất**
```bash
git checkout develop
git pull origin develop
```

**Bước 2: Tạo nhánh feature mới từ develop**
```bash
git checkout -b feature/upload-avatar
```

**Bước 3: Code, Test và Commit thường xuyên**
```bash
git add .
git commit -m "feat: Add avatar upload API endpoints"
```
*Lưu ý: Bạn có thể tham khảo quy tắc Commit Conventional (feat, fix, chore, docs, refactor).*

**Bước 4: Đẩy nhánh này lên GitHub**
```bash
git push -u origin feature/upload-avatar
```

**Bước 5: Tạo Pull Request (PR) / Merge Request (MR)**
- Lên GitHub, tạo một Pull Request từ nhánh `feature/upload-avatar` gộp vào nhánh `develop`.
- (Tự review hoặc nhờ team review) -> Merge!

**Bước 6: Dọn dẹp**
Sau khi PR đã được merge, bạn có thể xóa nhánh feature:
```bash
git branch -d feature/upload-avatar
```
(Và lặp lại từ Bước 1 cho tính năng tiếp theo!)

### 2. Khi chuẩn bị đưa Code lên Production (Release)
- Mở một Pull Request từ nhánh `develop` sang nhánh `main`.
- Merge! Đẩy (Tag) version nếu cần.

### 3. Quy ước Đặt Tên Commit (Conventional Commits)
- `feat:` Thêm tính năng mới
- `fix:` Sửa lỗi (bug)
- `docs:` Cập nhật tài liệu (README, swagger)
- `style:` Sửa định dạng code (khoảng trắng, dấu phẩy, v.v., không ảnh hưởng logic)
- `refactor:` Đổi cấu trúc code nhưng không thay đổi tính năng
- `test:` Thêm hoặc sửa Test case
- `chore:` Các tác vụ cấu hình build (gradle), dependency, gitignore

// turbo
## Lệnh tiện ích: Khởi tạo nhánh Develop lần đầu
(Không cần chạy nếu Assistant đã tạo giúp bạn)
```bash
git pull
git checkout -b develop
git push -u origin develop
```
