# Thiết Kế Chi Tiết Hệ Thống Quy Mô Thấp (Low-Level Design & API Specs)

Tài liệu này bao gồm chi tiết kỹ thuật hệ thống backend ở mức cấu trúc và đặc tả thành phần.

## 1. Nền Tảng Kỹ Thuật (Architecture Overview)

Hệ thống được thiết kế theo kiến trúc Layered Modular Monolith (Route -> Service -> Repository), sử dụng Ktor Framework làm cổng giao tiếp chính.

- **Phiên bản Ktor:** 3.0.2
- **Tổng số bảng cơ sở dữ liệu (MySQL Tables):** **33**
- **Tổng số API Endpoints chính thức:** 139 REST `/api/v1`
- **Kết nối thông tin thời gian thực:** 1 WebSocket chat channel
- **Bảo mật:** JWT Authentication (quản lý phân quyền qua đối tượng JWT Principal)

## 2. Phân Tích Thực Thể Bảng Mới & Cấu Trúc Bổ Sung (New Schema Context)

Theo cập nhật mới nhất, hệ thống đã tích hợp thêm 3 bảng chính:

1. **`comment_dislikes`**: Cho phép ghi nhận tương tác tiêu cực (dislike) của người dùng đối với các bình luận trên bài viết. Đồng hành cùng `comment_likes` nhằm duy trì dữ liệu tương tác cân bằng.
2. **`device_tokens`**: Quản lý lưu trữ Firebase Cloud Messaging (FCM) tokens cho từng người dùng, hỗ trợ đẩy thông báo đẩy (push notification) trên Android.
3. **`photographer_services`**: Danh sách gói dịch vụ chụp ảnh cụ thể của từng Photographer, lưu trữ giá cả, danh mục chụp và các chi tiết hậu cần đi kèm.

## 3. Bản Đồ Modules Mã Nguồn (Directory Structure Schema)

Tất cả logic nghiệp vụ chính được tổ chức vào các tầng chuyên biệt:

- `com.instagallery.routes`: Định nghĩa endpoints, kiểm soát dữ liệu đầu vào (HTTP validation) và trả phản hồi.
- `com.instagallery.services`: Nơi tập trung toàn bộ Business Logic, giao dịch phân quyền, và xác thực.
- `com.instagallery.repositories`: Thực hiện các câu lệnh truy vấn dữ liệu thô Exposed DSL.

## 4. Mô Tả Chi Tiết RESTful API Endpoints (LLD Specifications)

Chi tiết sơ đồ ánh xạ endpoint và phản hồi mẫu được hệ thống hóa trong tài liệu [Backend_APIs.md](../api-specs/Backend_APIs.md) và đặc tả frontend tại [Frontend_API_Specs.md](../api-specs/Frontend_API_Specs.md).

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
