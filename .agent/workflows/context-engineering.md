---
description: context-engineering
---
# Skill: Context Engineering (ASH 2.0)

Context Engineering là nghệ thuật thiết kế ngữ cảnh hệ thống trước khi bắt tay làm nhiệm vụ. Ở Backend Ktor do tính chất RDBMs phức tạp, AI bắt buộc phải có ngữ cảnh rõ ràng về database và use-case.

Trước khi thực hiện một chức năng lớn, AI BẮT BUỘC PHẢI nạp và đọc các tài liệu sau nếu chức năng liên quan đến Database và Business Logic mới:
1. `C:\Users\Ngoc Pham\OneDrive - nien.edu.vn\Documents\Đồ án tốt nghiệp\Nội dung Backend\instagallery_db_analysis.md` (Để phân tích cấu trúc Column, Type, Index, Quan hệ của các bảng liên quan).
2. `C:\Users\Ngoc Pham\OneDrive - nien.edu.vn\Documents\Đồ án tốt nghiệp\Nội dung Backend\instagallery_complete_system.md` (Để phân tích Workflow Logic, Use case của tính năng đang cần code).

Chỉ bắt đầu gõ code Kotlin sau khi đã đọc và tóm tắt rành mạch liên kết giữa các bảng Cơ Sở Dữ Liệu ở file `instagallery_db_analysis.md`. Thái độ làm việc là **Luôn Tư Duy Dữ Liệu (Brainstorming Data Flow)** trước khi mở file viết Code.
