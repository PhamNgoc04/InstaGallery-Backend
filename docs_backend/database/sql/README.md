# Thư mục chứa Script & Dữ liệu mẫu SQL (Database Seeds)

Thư mục này tập hợp các tệp tin script SQL phục vụ cho việc khởi tạo, nạp dữ liệu mẫu (seed data), và cập nhật cấu trúc cơ sở dữ liệu của hệ thống **InstaGallery**.

---

## 📁 Danh sách các tệp tin

| Tên file | Mô tả |
|---|---|
| [`demo_july_2026_new_posts_seed.sql`](file:///d:/InstaGallery/instagallery-android-kotlin/docs_backend/sql/demo_july_2026_new_posts_seed.sql) | Dữ liệu mẫu bài đăng (posts), hình ảnh (media), lượt thích, bình luận và hashtag mới phục vụ chạy demo. |
| [`app_demo_full_seed.sql`](file:///d:/InstaGallery/instagallery-android-kotlin/docs_backend/sql/app_demo_full_seed.sql) | Script tổng hợp toàn bộ dữ liệu mẫu ban đầu cho toàn hệ thống. |
| [`demo_fill_user_ids_6_100_seed.sql`](file:///d:/InstaGallery/instagallery-android-kotlin/docs_backend/sql/demo_fill_user_ids_6_100_seed.sql) | Script tạo danh sách người dùng mẫu (User IDs từ 6 đến 100). |
| [`seed_user_150.sql`](file:///d:/InstaGallery/instagallery-android-kotlin/docs_backend/sql/seed_user_150.sql) | Script tạo người dùng mẫu mở rộng (đến 150 users). |
| [`demo_more_posts_comments_seed.sql`](file:///d:/InstaGallery/instagallery-android-kotlin/docs_backend/sql/demo_more_posts_comments_seed.sql) | Dữ liệu mẫu bổ sung thêm bài viết và chuỗi bình luận tương tác. |
| [`mobile_search_booking_seed.sql`](file:///d:/InstaGallery/instagallery-android-kotlin/docs_backend/sql/mobile_search_booking_seed.sql) | Dữ liệu mẫu cho phân hệ tìm kiếm (khám phá) và lịch đặt chụp ảnh mẫu. |
| [`update_services.sql`](file:///d:/InstaGallery/instagallery-android-kotlin/docs_backend/sql/update_services.sql) | Script cập nhật các gói dịch vụ và bảng giá của nhiếp ảnh gia. |

---

## 🚀 Hướng dẫn nạp dữ liệu (Import SQL)

### Cách 1: Nạp qua Docker (Khuyên dùng)
```powershell
Get-Content docs_backend/sql/demo_july_2026_new_posts_seed.sql | docker exec -i instagallery-backend-mysql-1 mysql -uig_user -p123456789 instagallery
```

### Cách 2: Nạp qua MySQL CLI trực tiếp
```bash
mysql -u root -p instagallery < docs_backend/sql/demo_july_2026_new_posts_seed.sql
```

### Cách 3: Nạp qua MySQL Workbench / DBeaver / Navicat
1. Mở công cụ quản lý cơ sở dữ liệu và kết nối vào DB `instagallery`.
2. Mở file `.sql` trong thư mục `docs_backend/sql/`.
3. Nhấn **Execute / Run** để nạp dữ liệu vào các bảng tương ứng.
