# API Đặc Tả: Hệ Thống Nhắn Tin Thời Gian Thực (Chat & Conversations)

*(Module kết hợp giữa REST API cho lịch sử tin nhắn và WebSocket cho giao tiếp Real-time)*

---

## PHẦN 1: REST API - QUẢN LÝ CUỘC TRÒ CHUYỆN

### 1. Lấy danh sách Hội Thoại (Get My Conversations)
- **Cụm:** `Chat`
- **Endpoint:** `GET /api/v1/chat/conversations`
- **Access:** Bắt buộc có JWT Token
- **Mô tả:** Lấy danh sách các cuộc trò chuyện gần đây của User hiện tại (sắp xếp theo tin nhắn mới nhất).

**Response Thành Công (200 OK):**
```json
{
  "success": true,
  "data": {
    "conversations": [
      {
        "id": 1,
        "title": "Nhóm Chụp Ảnh Kỷ Yếu" // Hoặc null nếu là Direct Message 1-1
        "type": "GROUP",
        "last_message": "Chốt giờ nhé mọi người!",
        "last_message_time": "2026-05-20T10:05:00Z",
        "unread_count": 2 
      }
    ]
  }
}
```

### 2. Lấy Lịch Sử Tin Nhắn Của Hành Trình (Get Messages in Conversation)
- **Cụm:** `Chat`
- **Endpoint:** `GET /api/v1/chat/conversations/{id}/messages`
- **Access:** Bắt buộc có JWT Token (Và phải là thành viên nhóm)
- **Hỗ trợ Query:** `?page=1&limit=50`

**Response Thành Công (200 OK):**
```json
{
  "success": true,
  "data": {
    "messages": [
      {
        "message_id": 105,
        "sender_id": 2,
        "sender_name": "Phó Nháy Pro",
        "content": "Gửi bạn báo giá nhé.",
        "type": "TEXT",
        "created_at": "..."
      }
    ],
    "meta": { "current_page": 1, "has_next": false }
  }
}
```

---

## PHẦN 2: WEBSOCKET - REAL-TIME MESSAGING

*(Đây là mạch đập chính của mạng xã hội, nơi kết nối trực tiếp hai chiều)*

- **Đường dẫn:** `ws://[host]/api/v1/ws/chat`
- **Giao thức:** `ws` hoặc `wss`
- **Xác thực:** Gửi Token JWT qua tham số (Ví dụ: `?token=eyJ...`) hoặc Frame khởi tạo, vì WebSocket Web chưa hỗ trợ ghim Header Authorization chuẩn. 

### Quy Tắc Mã Hóa Khung Truyền:
Mọi tin nhắn đi qua Ws phải là JSON String chuẩn. Hệ thống Backend sẽ `Receive` và `Send` dạng Object.

**1. Hành Động Mạng: Gửi Tin Nhắn Mới (Client -> Server)**
```json
{
  "action": "SEND_MESSAGE",
  "conversation_id": 1,
  "content": "Chào bạn, mình muốn đặt lịch",
  "message_type": "TEXT" // Enum: TEXT, IMAGE
}
```

**2. Phản Hồi Từ Ws Phân Phối Ra Các Máy Khác (Server -> Clients in Room)**
*(Backend tự động kiểm tra ai đang online trong hộp thoại và Push Broadcast)*
```json
{
  "event": "NEW_MESSAGE",
  "data": {
    "message_id": 106,
    "conversation_id": 1,
    "sender_id": 3,
    "sender_name": "Nguyen Khach",
    "content": "Chào bạn, mình muốn đặt lịch",
    "created_at": "...",
    "is_me": false // Tiện để UI render bong bóng chat
  }
}
```

**3. Sự Kiện Error (Server -> Client lỗi)**
```json
{
  "event": "ERROR",
  "message": "Không có quyền nhắn tin trong nhóm này."
}
```

---

*(Thiết kế này kết nối chặt chẽ giữa logic Lưu Database (Exposed) và phát sóng sự kiện liên tục bằng Channel của Ktor).*
