# Thiết Kế Chi Tiết Hệ Thống Quy Mô Thấp (Low-Level Design & API Specs)

Tài liệu này bao gồm chi tiết kỹ thuật hệ thống backend ở mức cấu trúc và đặc tả thành phần.

## 1. Nền Tảng Kỹ Thuật (Architecture Overview)

Hệ thống được thiết kế theo kiến trúc Layered Modular Monolith (Route -> Service -> Repository), sử dụng Ktor Framework làm cổng giao tiếp chính.

- **Phiên bản Ktor:** 3.0.2
- **Tổng số bảng cơ sở dữ liệu (MySQL Tables):** **36**
- **Tổng số API Endpoints chính thức:** 139 REST `/api/v1`
- **Kết nối thông tin thời gian thực:** 1 WebSocket chat channel
- **Bảo mật:** JWT Authentication (quản lý phân quyền qua đối tượng JWT Principal)

## 2. Phân Tích Thực Thể Bảng Mở Rộng (New Schema Context)

Theo cập nhật mã nguồn thực tế mới nhất, hệ thống đã chuẩn hóa toàn bộ 36 bảng cơ sở dữ liệu với các bảng mở rộng quan trọng:

1. **`post_tagged_users`**: Quản lý việc gắn thẻ (tag) người dùng vào bài viết (`POST /api/v1/posts/{id}/tags`), gỡ thẻ (`DELETE /api/v1/posts/{id}/tags/{taggedUserId}`) và xem danh sách bài viết được gắn thẻ (`GET /api/v1/users/me/tagged-posts`).
2. **`comment_reactions`**: Bảng đang dùng cho like và dislike bình luận. Mỗi user một dòng trên một comment. `comment_likes` và `comment_dislikes` chỉ để backfill lúc khởi động.
3. **`post_shares`**: Khóa chính `(user_id, post_id)`. `POST /api/v1/posts/{id}/share` lần hai không tăng `posts.share_count`. `GET /api/v1/posts/{id}/shares` đọc bộ đếm đó.
4. **`device_tokens`**: Quản lý lưu trữ Firebase Cloud Messaging (FCM) tokens cho từng người dùng (`POST /api/v1/devices/fcm-token`), hỗ trợ đẩy thông báo (push notification) trên Android.
5. **`photographer_services`**: Danh sách gói dịch vụ chụp ảnh cụ thể của từng Photographer (`/api/v1/photographer/services`), lưu trữ giá cả, danh mục chụp và các chi tiết hậu cần đi kèm.

## 3. Bản Đồ Modules Mã Nguồn (Directory Structure Schema)

Tất cả logic nghiệp vụ chính được tổ chức vào các tầng chuyên biệt:

- `com.instagallery.routes`: Định nghĩa endpoints, kiểm soát dữ liệu đầu vào (HTTP validation) và trả phản hồi.
- `com.instagallery.services`: Nơi tập trung toàn bộ Business Logic, giao dịch phân quyền, và xác thực.
- `com.instagallery.repositories`: Thực hiện các câu lệnh truy vấn dữ liệu thô Exposed DSL.

## 4. Mô Tả Chi Tiết RESTful API Endpoints (LLD Specifications)

Chi tiết sơ đồ ánh xạ endpoint và phản hồi mẫu được hệ thống hóa trong tài liệu [Backend_APIs.md](Backend_APIs.md) và đặc tả frontend tại [Frontend_API_Specs.md](Frontend_API_Specs.md).

### 4.1. Quy chuẩn Response Payload (JSend Standard)

Hệ thống trả về JSend Wrapper đồng nhất:

```json
{
  "status": "SUCCESS",
  "message": "Thông tin xử lý thành công.",
  "data": {
    "key": "value"
  }
}
```

Trong trường hợp lỗi nghiệp vụ hoặc kỹ thuật:

```json
{
  "status": "ERROR",
  "message": "Chi tiết lỗi mô tả.",
  "errorCode": "LỖI_MỤC_TIÊU"
}
```

## 5. Hướng Dẫn Tích Hợp Android (Mobile Integration Core)

- Sử dụng địa chỉ IP đặc biệt `10.0.2.2:8080` khi chạy ứng dụng trên Android Emulator để truy cập vào Ktor Local Server.
- Cấu hình Cleartext Traffic do backend chạy giao thức HTTP thuần túy cho môi trường local.
- Hãy tham khảo chi tiết [AI_Android_Integration_Context.md](../api-specs/AI_Android_Integration_Context.md) để biết thêm các DTO mẫu và cấu trúc hàm tích hợp.
