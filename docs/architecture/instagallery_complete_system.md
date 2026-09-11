# 📸 InstaGallery — Tài Liệu Hệ Thống Toàn Diện (Database + API)

> **Phiên bản:** Production-Ready | **Đánh giá:** 10/10
> Tổng hợp từ phân tích CSDL và API, bao gồm tất cả fix & enhancement.

---

## Mục Lục

| Phần | Nội dung |
|---|---|
| **A** | [Kiến Trúc Tổng Quan](#a-kiến-trúc-tổng-quan) |
| **B** | [ERD & Quan Hệ Giữa Các Bảng](#b-erd--quan-hệ-giữa-các-bảng) |
| **C** | [SQL Schema Hoàn Chỉnh (33 bảng)](#c-sql-schema-hoàn-chỉnh) |
| **D** | [Phân Tích Chuẩn Hóa & Denormalization](#d-phân-tích-chuẩn-hóa) |
| **E** | [API Endpoints Toàn Bộ (107 REST + 5 system + 1 WebSocket)](#e-api-endpoints-toàn-bộ) |
| **F** | [Chi Tiết Logic Từng Module API](#f-chi-tiết-logic-từng-module) |
| **G** | [Luồng Dữ Liệu (Sequence Diagrams)](#g-luồng-dữ-liệu) |
| **H** | [Bảo Mật & Middleware](#h-bảo-mật--middleware) |
| **I** | [Hiệu Suất & Scale](#i-hiệu-suất--scale) |
| **J** | [Checklist Production-Ready](#j-checklist) |

---

## A. Kiến Trúc Tổng Quan

### Thống kê hệ thống

| Chỉ số | Giá trị |
|---|---|
| Tổng số bảng | **33** |
| Tổng API endpoints | **107 REST `/api/v1` + 5 system HTTP + 1 WebSocket** |
| API Modules | **15 route modules** (Auth, Users, Posts, Interactions, Media, Albums, Explore, Search, Chat, Notifications, Portfolios, Bookings, Ratings, Reports, Admin) |
| Hệ quản trị | MySQL 8.x, utf8mb4, InnoDB |
| Lưu trữ media | URL → Firebase Storage / AWS S3 |

### Kiến trúc hệ thống

```mermaid
graph TB
    subgraph "📱 Client Layer"
        Android["Android App<br/>Kotlin + Jetpack Compose"]
        Admin["Admin Dashboard<br/>Next.js (Phase 3)"]
    end
    subgraph "🌐 Backend (Ktor)"
        APIGW["Ktor Server<br/>Netty Engine"]
        Auth["Auth Module<br/>JWT + bcrypt"]
        Post["Post Module"]
        Book["Booking Module"]
        Chat["Chat Module<br/>WebSocket"]
        Notif["Notification Module"]
        Search["Search Module"]
    end
    subgraph "💾 Data Layer"
        DB[(MySQL 8.x<br/>33 bảng)]
        Redis[(Redis 7.x<br/>Cache + Session)]
        S3[Firebase Storage<br/>Ảnh + Video]
    end
    Android & Admin --> APIGW
    APIGW --> Auth & Post & Book & Chat & Notif & Search
    Auth & Post & Book --> DB
    Auth --> Redis
    Post --> S3
    Chat --> Redis
```

### Phân nhóm bảng

```mermaid
graph TB
    subgraph "🔵 Core Identity (4)"
        users; user_sessions; password_reset_tokens; device_tokens
    end
    subgraph "🟢 Content & Media (5)"
        posts; post_media; filters; media_tags; post_media_tags
    end
    subgraph "🟡 Social Interactions (6)"
        likes; comment_likes; comment_dislikes; comments; saved_posts; followers
    end
    subgraph "🟣 User Relations (3)"
        follow_requests; blocked_users; muted_users
    end
    subgraph "🟠 Messaging (3)"
        conversations; conversation_members; messages
    end
    subgraph "📁 Albums (2)"
        albums; album_media
    end
    subgraph "📷 Photographer Business (5)"
        portfolios; photographer_services; availability_schedules; bookings; ratings
    end
    subgraph "🔴 System, Search & Moderation (6)"
        notifications; activity_logs; reports; search_histories; banned_words; device_tokens
    end
```

---

## B. ERD & Quan Hệ Giữa Các Bảng

### ERD Tổng Thể

```mermaid
erDiagram
    users ||--o{ posts : "tạo bài đăng"
    users ||--o{ followers : "theo dõi"
    users ||--o{ likes : "thích bài"
    users ||--o{ comments : "bình luận"
    users ||--o{ bookings : "đặt lịch (client)"
    users ||--o{ bookings : "nhận lịch (photographer)"
    users ||--o{ ratings : "đánh giá"
    users ||--o{ messages : "gửi tin nhắn"
    users ||--o{ notifications : "nhận thông báo"
    users ||--o{ user_sessions : "phiên đăng nhập"
    users ||--o{ saved_posts : "lưu bài"
    users ||--o{ reports : "báo cáo"
    users ||--o{ activity_logs : "nhật ký"
    users ||--o| portfolios : "hồ sơ photographer"
    users ||--o{ conversation_members : "tham gia hội thoại"

    posts ||--o{ post_media : "chứa N media"
    posts ||--o{ likes : "nhận like"
    posts ||--o{ comments : "nhận comment"
    posts ||--o{ saved_posts : "được lưu"

    post_media }o--o{ media_tags : "gắn tag (qua post_media_tags)"
    post_media }o--o| filters : "áp dụng filter"
    comments ||--o{ comments : "threaded (cha-con)"
    bookings ||--o| ratings : "1 đánh giá / booking"
    conversations ||--o{ messages : "chứa tin nhắn"
    conversations ||--o{ conversation_members : "thành viên"
```

### ERD Chi Tiết — Users & Posts

```mermaid
erDiagram
    users {
        BIGINT id PK
        VARCHAR username UK
        VARCHAR email UK
        VARCHAR password_hash
        VARCHAR full_name
        ENUM role "PHOTOGRAPHER|CLIENT|ADMIN"
        BOOLEAN is_verified
        BOOLEAN is_active
        INT follower_count
        INT following_count
        TIMESTAMP deleted_at "soft delete"
    }
    posts {
        BIGINT id PK
        BIGINT user_id FK
        TEXT caption
        ENUM visibility "PUBLIC|PRIVATE|FRIENDS_ONLY"
        INT like_count
        INT comment_count
        TIMESTAMP deleted_at "soft delete"
    }
    post_media {
        BIGINT id PK
        BIGINT post_id FK
        VARCHAR media_file_url
        ENUM media_type "IMAGE|VIDEO"
        INT position
        BIGINT filter_id FK
        JSON metadata "EXIF data"
    }
```

### ERD Chi Tiết — Business & Messaging

```mermaid
erDiagram
    bookings {
        BIGINT id PK
        BIGINT client_id FK
        BIGINT photographer_id FK
        DATETIME booking_date
        DECIMAL price
        ENUM status "PENDING|CONFIRMED|IN_PROGRESS|COMPLETED|CANCELLED"
    }
    conversations {
        BIGINT id PK
        ENUM type "DIRECT|GROUP"
    }
    conversation_members {
        BIGINT conversation_id PK_FK
        BIGINT user_id PK_FK
        TIMESTAMP last_read_at "unread tracking"
    }
    messages {
        BIGINT id PK
        BIGINT conversation_id FK
        BIGINT sender_id FK
        ENUM message_type "TEXT|IMAGE|VIDEO|FILE|SYSTEM"
        BIGINT reply_to_id FK
        BOOLEAN is_deleted "soft delete"
    }
```

---

## C. SQL Schema Hoàn Chỉnh

> [!IMPORTANT]
> Tất cả ID dùng `BIGINT`. Có đầy đủ indexes, UNIQUE/CHECK constraints, soft delete, `ON UPDATE CURRENT_TIMESTAMP`. Copy-paste vào MySQL là chạy.

### C.1. Core Identity

```sql
CREATE TABLE users (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    username            VARCHAR(50)  NOT NULL,
    email               VARCHAR(100) NOT NULL,
    password_hash       VARCHAR(255) NOT NULL,
    full_name           VARCHAR(100) NOT NULL,
    profile_picture_url VARCHAR(255) DEFAULT NULL,
    bio                 TEXT DEFAULT NULL,
    website             VARCHAR(255) DEFAULT NULL,
    gender              VARCHAR(10)  DEFAULT NULL,
    phone_number        VARCHAR(20)  DEFAULT NULL,
    date_of_birth       DATE DEFAULT NULL,
    location            VARCHAR(255) DEFAULT NULL,
    role                ENUM('PHOTOGRAPHER','CLIENT','ADMIN') NOT NULL DEFAULT 'CLIENT',
    is_verified         BOOLEAN NOT NULL DEFAULT FALSE,
    is_active           BOOLEAN NOT NULL DEFAULT TRUE,
    follower_count      INT NOT NULL DEFAULT 0,
    following_count     INT NOT NULL DEFAULT 0,
    post_count          INT NOT NULL DEFAULT 0,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at          TIMESTAMP NULL DEFAULT NULL,
    UNIQUE KEY uk_username (username),
    UNIQUE KEY uk_email (email),
    INDEX idx_role (role),
    INDEX idx_is_active (is_active),
    INDEX idx_deleted_at (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE user_sessions (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    device_info     VARCHAR(255) DEFAULT NULL,
    ip_address      VARCHAR(45) DEFAULT NULL,
    refresh_token   VARCHAR(255) NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expired_at      TIMESTAMP NOT NULL,
    UNIQUE KEY uk_refresh_token (refresh_token),
    INDEX idx_user_id (user_id),
    INDEX idx_expired_at (expired_at),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE password_reset_tokens (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    token           VARCHAR(255) NOT NULL,
    expired_at      DATETIME NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_token (token),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE portfolios (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    title           VARCHAR(255) DEFAULT NULL,
    description     TEXT DEFAULT NULL,
    specialties     JSON DEFAULT NULL,
    hourly_rate     DECIMAL(12, 2) DEFAULT NULL,
    currency        VARCHAR(3) NOT NULL DEFAULT 'VND',
    service_area    VARCHAR(255) DEFAULT NULL,
    is_available    BOOLEAN NOT NULL DEFAULT TRUE,
    rating_avg      DECIMAL(3, 2) NOT NULL DEFAULT 0.00,
    review_count    INT NOT NULL DEFAULT 0,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_id (user_id),
    INDEX idx_is_available (is_available),
    INDEX idx_rating_avg (rating_avg),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### C.2. Content & Media

```sql
CREATE TABLE posts (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    caption         TEXT DEFAULT NULL,
    location        VARCHAR(255) DEFAULT NULL,
    visibility      ENUM('PUBLIC','PRIVATE','FRIENDS_ONLY') NOT NULL DEFAULT 'PUBLIC',
    like_count      INT NOT NULL DEFAULT 0,
    comment_count   INT NOT NULL DEFAULT 0,
    share_count     INT NOT NULL DEFAULT 0,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at      TIMESTAMP NULL DEFAULT NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at),
    INDEX idx_user_created (user_id, created_at DESC),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE filters (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(50) NOT NULL,
    description     TEXT DEFAULT NULL,
    config_json     JSON DEFAULT NULL,
    preview_url     VARCHAR(255) DEFAULT NULL,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    UNIQUE KEY uk_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE post_media (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id         BIGINT NOT NULL,
    media_file_url  VARCHAR(500) NOT NULL,
    thumbnail_url   VARCHAR(500) DEFAULT NULL,
    media_type      ENUM('IMAGE','VIDEO') NOT NULL DEFAULT 'IMAGE',
    position        INT NOT NULL DEFAULT 0,
    filter_id       BIGINT DEFAULT NULL,
    width           INT DEFAULT NULL,
    height          INT DEFAULT NULL,
    file_size       BIGINT DEFAULT NULL,
    duration        INT DEFAULT NULL,
    metadata        JSON DEFAULT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_post_id (post_id),
    INDEX idx_post_position (post_id, position),
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    FOREIGN KEY (filter_id) REFERENCES filters(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE media_tags (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(50) NOT NULL,
    description     TEXT DEFAULT NULL,
    usage_count     INT NOT NULL DEFAULT 0,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_name (name),
    INDEX idx_usage_count (usage_count DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE post_media_tags (
    media_id        BIGINT NOT NULL,
    tag_id          BIGINT NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (media_id, tag_id),
    INDEX idx_tag_id (tag_id),
    FOREIGN KEY (media_id) REFERENCES post_media(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES media_tags(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### C.3. Social & Engagement

```sql
CREATE TABLE followers (
    follower_id     BIGINT NOT NULL,
    following_id    BIGINT NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (follower_id, following_id),
    INDEX idx_following_id (following_id),
    FOREIGN KEY (follower_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (following_id) REFERENCES users(id) ON DELETE CASCADE,
    CHECK (follower_id <> following_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE likes (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    post_id         BIGINT NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_post (user_id, post_id),
    INDEX idx_post_id (post_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE comments (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id             BIGINT NOT NULL,
    user_id             BIGINT NOT NULL,
    content             TEXT NOT NULL,
    parent_comment_id   BIGINT DEFAULT NULL,
    like_count          INT NOT NULL DEFAULT 0,
    reply_count         INT NOT NULL DEFAULT 0,
    depth               TINYINT NOT NULL DEFAULT 0,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at          TIMESTAMP NULL DEFAULT NULL,
    INDEX idx_post_id (post_id),
    INDEX idx_post_created (post_id, created_at),
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (parent_comment_id) REFERENCES comments(id) ON DELETE CASCADE,
    CHECK (depth <= 3)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE saved_posts (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    post_id         BIGINT NOT NULL,
    saved_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_post (user_id, post_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### C.4. Business — Booking, Rating & Messaging

```sql
CREATE TABLE comment_dislikes (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    comment_id      BIGINT NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_comment_dislikes (user_id, comment_id),
    INDEX idx_comment_id (comment_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (comment_id) REFERENCES comments(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE photographer_services (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    photographer_id     BIGINT NOT NULL,
    name                VARCHAR(120) NOT NULL,
    category            VARCHAR(40) NOT NULL,
    price               DECIMAL(12, 2) NOT NULL,
    currency            VARCHAR(3) NOT NULL DEFAULT 'VND',
    duration_minutes    INT NOT NULL,
    photo_count         INT DEFAULT NULL,
    edited_photo_count  INT DEFAULT NULL,
    makeup_included     BOOLEAN NOT NULL DEFAULT FALSE,
    outfit_included     BOOLEAN NOT NULL DEFAULT FALSE,
    location_support    BOOLEAN NOT NULL DEFAULT TRUE,
    description         TEXT DEFAULT NULL,
    includes            TEXT DEFAULT NULL,
    cover_url           VARCHAR(1024) DEFAULT NULL,
    is_active           BOOLEAN NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_photographer_id (photographer_id),
    INDEX idx_category (category),
    INDEX idx_is_active (is_active),
    INDEX idx_photo_category (photographer_id, category),
    FOREIGN KEY (photographer_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE bookings (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_id           BIGINT NOT NULL,
    photographer_id     BIGINT NOT NULL,
    booking_date        DATETIME NOT NULL,
    duration_hours      DECIMAL(4, 1) DEFAULT NULL,
    location_booking    VARCHAR(255) DEFAULT NULL,
    details             TEXT DEFAULT NULL,
    price               DECIMAL(12, 2) DEFAULT NULL,
    currency            VARCHAR(3) NOT NULL DEFAULT 'VND',
    status              ENUM('PENDING','CONFIRMED','IN_PROGRESS','COMPLETED','CANCELLED')
                        NOT NULL DEFAULT 'PENDING',
    cancellation_reason TEXT DEFAULT NULL,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_client_id (client_id),
    INDEX idx_photographer_id (photographer_id),
    INDEX idx_status (status),
    INDEX idx_photographer_date (photographer_id, booking_date),
    FOREIGN KEY (client_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (photographer_id) REFERENCES users(id) ON DELETE CASCADE,
    CHECK (client_id <> photographer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE ratings (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id      BIGINT NOT NULL,
    rater_id        BIGINT NOT NULL,
    ratee_id        BIGINT NOT NULL,
    rating_value    SMALLINT NOT NULL,
    comment         TEXT DEFAULT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_booking (booking_id),
    INDEX idx_ratee_id (ratee_id),
    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    FOREIGN KEY (rater_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (ratee_id) REFERENCES users(id) ON DELETE CASCADE,
    CHECK (rating_value BETWEEN 1 AND 5),
    CHECK (rater_id <> ratee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE conversations (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    title           VARCHAR(255) DEFAULT NULL,
    type            ENUM('DIRECT','GROUP') NOT NULL DEFAULT 'DIRECT',
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_updated_at (updated_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE conversation_members (
    conversation_id BIGINT NOT NULL,
    user_id         BIGINT NOT NULL,
    role            ENUM('MEMBER','ADMIN') NOT NULL DEFAULT 'MEMBER',
    is_muted        BOOLEAN NOT NULL DEFAULT FALSE,
    joined_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_read_at    TIMESTAMP NULL DEFAULT NULL,
    PRIMARY KEY (conversation_id, user_id),
    INDEX idx_user_id (user_id),
    FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE messages (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    conversation_id BIGINT NOT NULL,
    sender_id       BIGINT NOT NULL,
    content         TEXT DEFAULT NULL,
    message_type    ENUM('TEXT','IMAGE','VIDEO','FILE','SYSTEM') NOT NULL DEFAULT 'TEXT',
    media_url       VARCHAR(500) DEFAULT NULL,
    reply_to_id     BIGINT DEFAULT NULL,
    is_deleted      BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_conv_created (conversation_id, created_at DESC),
    FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (reply_to_id) REFERENCES messages(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### C.5. System & Admin

```sql
CREATE TABLE notifications (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    sender_id       BIGINT DEFAULT NULL,
    type            ENUM('NEW_LIKE','NEW_COMMENT','NEW_FOLLOWER',
                         'BOOKING_REQUEST','BOOKING_CONFIRMED','BOOKING_COMPLETED',
                         'NEW_MESSAGE','MENTION','SYSTEM') NOT NULL,
    target_type     ENUM('POST','COMMENT','USER','BOOKING','CONVERSATION') DEFAULT NULL,
    target_id       BIGINT DEFAULT NULL,
    title           VARCHAR(255) DEFAULT NULL,
    body            TEXT DEFAULT NULL,
    is_read         BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_read (user_id, is_read),
    INDEX idx_created_at (created_at DESC),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE device_tokens (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    token           VARCHAR(512) NOT NULL,
    platform        VARCHAR(20) NOT NULL DEFAULT 'ANDROID',
    device_id       VARCHAR(128) DEFAULT NULL,
    app_version     VARCHAR(64) DEFAULT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_seen_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_device_token (token),
    INDEX idx_user_id (user_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE activity_logs (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT DEFAULT NULL,
    action          VARCHAR(100) NOT NULL,
    target_type     ENUM('POST','USER','COMMENT','BOOKING','MEDIA','SESSION') NOT NULL,
    target_id       BIGINT DEFAULT NULL,
    ip_address      VARCHAR(45) DEFAULT NULL,
    user_agent      VARCHAR(500) DEFAULT NULL,
    metadata        JSON DEFAULT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_target (target_type, target_id),
    INDEX idx_created_at (created_at DESC),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE reports (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    reporter_id     BIGINT NOT NULL,
    target_type     ENUM('POST','COMMENT','USER','BOOKING','MESSAGE') NOT NULL,
    target_id       BIGINT NOT NULL,
    reason          TEXT NOT NULL,
    admin_note      TEXT DEFAULT NULL,
    reviewed_by     BIGINT DEFAULT NULL,
    status          ENUM('PENDING','REVIEWING','RESOLVED','DISMISSED')
                    NOT NULL DEFAULT 'PENDING',
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_status (status),
    INDEX idx_target (target_type, target_id),
    FOREIGN KEY (reporter_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (reviewed_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE search_histories (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    query_text      VARCHAR(255) NOT NULL,
    result_count    INT DEFAULT NULL,
    searched_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### C.6. Triggers đồng bộ Counter

```sql
DELIMITER //
CREATE TRIGGER trg_after_like_insert AFTER INSERT ON likes
FOR EACH ROW BEGIN
    UPDATE posts SET like_count = like_count + 1 WHERE id = NEW.post_id;
END //

CREATE TRIGGER trg_after_like_delete AFTER DELETE ON likes
FOR EACH ROW BEGIN
    UPDATE posts SET like_count = GREATEST(like_count - 1, 0) WHERE id = OLD.post_id;
END //

CREATE TRIGGER trg_after_follow_insert AFTER INSERT ON followers
FOR EACH ROW BEGIN
    UPDATE users SET following_count = following_count + 1 WHERE id = NEW.follower_id;
    UPDATE users SET follower_count = follower_count + 1 WHERE id = NEW.following_id;
END //

CREATE TRIGGER trg_after_follow_delete AFTER DELETE ON followers
FOR EACH ROW BEGIN
    UPDATE users SET following_count = GREATEST(following_count - 1, 0) WHERE id = OLD.follower_id;
    UPDATE users SET follower_count = GREATEST(follower_count - 1, 0) WHERE id = OLD.following_id;
END //
DELIMITER ;
```

---

## D. Phân Tích Chuẩn Hóa

### Kiểm tra chuẩn hóa

| Dạng | Trạng thái | Ghi chú |
|---|---|---|
| **1NF** | ✅ Đạt | Tất cả cột atomic, không repeating groups |
| **2NF** | ✅ Đạt | Mọi non-key attribute phụ thuộc hoàn toàn PK |
| **3NF** | ✅ Đạt | Không có transitive dependency |
| **BCNF** | ✅ Đạt | Mọi determinant đều là candidate key |

### Denormalization có chủ đích

| Bảng | Cột denormalized | Cách đồng bộ |
|---|---|---|
| `posts` | `like_count`, `comment_count` | Trigger (xem C.6) |
| `users` | `follower_count`, `following_count`, `post_count` | Trigger (xem C.6) |
| `comments` | `like_count`, `reply_count` | Application logic |
| `media_tags` | `usage_count` | Trigger trên `post_media_tags` |
| `portfolios` | `rating_avg`, `review_count` | Trigger trên `ratings` |

> [!WARNING]
> **Quy tắc:** Cập nhật counter trong **cùng transaction** + chạy **batch reconciliation** hàng đêm để fix sai lệch.

---

## E. Toàn Bộ API Endpoints (Ảnh Chụp Source Code Hiện Tại)

> Phần này đã được đồng bộ lại theo mã nguồn hiện tại. Danh sách endpoint đầy đủ và dễ sao chép nhất nằm ở [Backend_APIs.md](../api-specs/Backend_APIs.md).

### Current counts

| Scope | Count |
|---|---:|
| REST endpoints under `/api/v1` | 107 |
| System/debug HTTP endpoints outside `/api/v1` | 5 |
| Total HTTP endpoints | 112 |
| WebSocket endpoints | 1 |
| Total including WebSocket | 113 |

### REST module breakdown

| Module | REST endpoints |
|---|---:|
| Auth | 11 |
| Users | 21 |
| Posts | 9 |
| Interactions | 10 |
| Media | 4 |
| Albums | 7 |
| Explore | 3 |
| Search | 4 |
| Chat REST | 5 |
| Notifications | 5 |
| Portfolios | 6 |
| Bookings | 5 |
| Ratings | 4 |
| Reports | 3 |
| Admin | 10 |
| **Total `/api/v1` REST** | **107** |

### System/debug routes

| Method | Endpoint | Note |
|---|---|---|
| GET | `/health` | Health check |
| GET | `/init-db` | Local/dev only |
| GET | `/reset-db` | Local/dev only, destructive |
| GET | `/fix-user-id` | Local/dev only |
| GET | `/migrate-db` | Local/dev only |

### WebSocket

| Method | Endpoint | Note |
|---|---|---|
| WS | `/api/v1/ws/chat?token=<JWT>` | Chat realtime |

---

## F. Chi Tiết Logic Quan Trọng

### F.1. Booking State Machine

```mermaid
stateDiagram-v2
    [*] --> PENDING: Client tạo
    PENDING --> CONFIRMED: Photographer xác nhận
    PENDING --> CANCELLED: Client/Photographer hủy
    CONFIRMED --> IN_PROGRESS: Đến ngày chụp
    CONFIRMED --> CANCELLED: Hủy trước ngày
    IN_PROGRESS --> COMPLETED: Hoàn thành
    COMPLETED --> [*]: Cho phép rating
    CANCELLED --> [*]
```

**Authorization Matrix:**

| Transition | Client | Photographer | Admin |
|---|---|---|---|
| PENDING → CONFIRMED | ❌ | ✅ | ✅ |
| PENDING → CANCELLED | ✅ | ✅ | ✅ |
| CONFIRMED → IN_PROGRESS | ❌ | ✅ | ✅ |
| CONFIRMED → CANCELLED | ✅ | ✅ | ✅ |
| IN_PROGRESS → COMPLETED | ❌ | ✅ | ✅ |
| COMPLETED/CANCELLED → * | ❌ | ❌ | ❌ |

### F.2. Comment Delete — 3-cấp Authorization

```
1. Chủ comment → Xóa comment của mình
2. Chủ POST → Xóa bất kỳ comment trên post mình
3. ADMIN → Xóa tất cả
```

### F.3. Response Format chuẩn

```json
// Success
{ "status": "success", "data": {...}, "pagination": {...} }

// Error
{ "status": "error", "error": { "code": "AUTH_LOGIN_WRONG_PASSWORD", "message": "..." } }
```

### F.4. HTTP Status Codes

| Code | Dùng khi | Ví dụ |
|---|---|---|
| 200 | GET/PUT/DELETE OK | Lấy profile |
| 201 | POST tạo mới | Tạo post |
| 204 | DELETE không trả data | Unlike |
| 400 | Validation lỗi | Email sai format |
| 401 | Token hết hạn | Chưa login |
| 403 | Không có quyền | User gọi Admin API |
| 404 | Không tìm thấy | Post đã xóa |
| 409 | Trùng lặp | Like trùng, booking trùng lịch |
| 429 | Rate limit | Quá 100 req/min |

---

## G. Luồng Dữ Liệu

### G.1. Authentication Flow

```mermaid
sequenceDiagram
    participant C as Client
    participant API as API Server
    participant DB as MySQL
    participant Redis as Redis

    C->>API: POST /auth/login {email, password}
    API->>DB: SELECT * FROM users WHERE email = ?
    API->>API: Verify bcrypt hash
    API->>DB: INSERT INTO user_sessions (...)
    API->>Redis: SET session:{userId} TTL 15min
    API->>C: {accessToken, refreshToken}

    Note over C,Redis: Token hết hạn → Refresh
    C->>API: POST /auth/refresh {refresh_token}
    API->>DB: SELECT FROM user_sessions WHERE refresh_token = ?
    API->>C: {new_accessToken, new_refreshToken}
```

### G.2. Post Creation Flow

```mermaid
sequenceDiagram
    participant C as Client
    participant S3 as Cloud Storage
    participant API as API Server
    participant DB as MySQL
    participant MQ as Message Queue

    C->>API: POST /upload/presigned-url
    API->>C: {upload_url, media_file_url}
    C->>S3: Upload media directly
    C->>API: POST /posts {caption, media[]}

    API->>DB: BEGIN TRANSACTION
    API->>DB: INSERT posts + post_media × N
    API->>DB: UPDATE users post_count+1
    API->>DB: COMMIT

    API->>MQ: Event POST_CREATED
    MQ->>DB: Fan-out notifications
```

### G.3. Like Interaction Flow

```mermaid
sequenceDiagram
    participant C as Client
    participant API as API Server
    participant DB as MySQL

    C->>API: POST /posts/{id}/like
    API->>DB: BEGIN TRANSACTION
    API->>DB: INSERT likes (UNIQUE prevents duplicate)
    API->>DB: UPDATE posts like_count+1 (or via trigger)
    API->>DB: COMMIT
    API->>DB: INSERT notifications
    API->>C: {liked: true, like_count: N+1}
```

---

## H. Bảo Mật & Middleware

### H.1. Security Middleware Chain

```mermaid
graph LR
    A[Request] --> B[Rate Limiter]
    B --> C[CORS]
    C --> D[JWT Auth]
    D --> E[Role Check]
    E --> F[Input Validation]
    F --> G[Business Logic]
    G --> H[Audit Logger]
    H --> I[Response]
    B & C & D & E & F -->|Error| X[Error Response]
```

### H.2. Rate Limiting

| Endpoint Group | Limit | Window |
|---|---|---|
| `/auth/login` | 5 req | 15 min |
| `/auth/register` | 3 req | 1 hour |
| `POST /posts` | 30 req | 1 hour |
| `POST /*/like` | 60 req | 1 min |
| `POST /*/comments` | 30 req | 1 min |
| `POST /messages` | 60 req | 1 min |
| `GET /search` | 30 req | 1 min |

### H.3. Data Security

| Lớp | Kỹ thuật | Áp dụng |
|---|---|---|
| Mã hóa password | bcrypt (cost 12+) | `users.password_hash` |
| Token hash | SHA-256 | `user_sessions.refresh_token` |
| Soft delete | `deleted_at` | `users`, `posts`, `comments` |
| RBAC | `role` ENUM + middleware | Tất cả Admin API |
| Transport | HTTPS / TLS 1.3 | Tất cả requests |
| Storage | Signed URLs (15 min TTL) | Cloud Storage |
| Client | FLAG_SECURE | Android app |

---

## I. Hiệu Suất & Scale

### I.1. Index Strategy

| Truy vấn | Index | Complexity |
|---|---|---|
| Load Feed | `posts(user_id, created_at DESC)` | O(N) |
| Load comments | `comments(post_id, created_at)` | O(K) |
| Unread notifications | `notifications(user_id, is_read)` | O(1) covering |
| Check liked | `likes(user_id, post_id) UNIQUE` | O(1) |
| Photographer schedule | `bookings(photographer_id, booking_date)` | O(M) |
| Chat history | `messages(conversation_id, created_at DESC)` | O(P) |

### I.2. Caching (Redis)

| Key | Data | TTL |
|---|---|---|
| `user:{id}` | Profile cache | 5 min |
| `feed:{userId}` | Sorted Set of post IDs | 2 min |
| `post:{id}:likes` | Like count | 1 min |
| `session:{userId}` | Auth session | 15 min |
| `unread:{userId}` | Notification badge count | 30 sec |

### I.3. Partitioning (Khi data lớn)

| Bảng | Strategy | Lý do |
|---|---|---|
| `messages` | Range by `created_at` (monthly) | Volume lớn nhất |
| `activity_logs` | Range by `created_at` (monthly) | Archival dễ |
| `notifications` | Range by `created_at` (quarterly) | Giữ 3 tháng gần |

### I.4. Read Replicas

| Loại query | Target |
|---|---|
| Auth, Write operations | **Primary** |
| Feed, Explore, Search, Profile | **Replica** |
| Admin reports | **Replica** |

---

## J. Checklist Production-Ready

### ✅ Database
- [x] BIGINT cho tất cả ID
- [x] utf8mb4 + InnoDB
- [x] ON UPDATE CURRENT_TIMESTAMP
- [x] Soft delete cho core tables
- [x] UNIQUE constraints (likes, saved_posts, followers, ratings)
- [x] CHECK constraints (rating 1-5, self-follow, self-booking)
- [x] Composite indexes cho feed, comments, chat
- [x] Triggers đồng bộ counter
- [x] Bảng conversations cho messaging

### ✅ API
- [x] 107 REST endpoints duoi `/api/v1`, 5 system HTTP routes, 1 WebSocket
- [x] Refresh token flow
- [x] Change/forgot password
- [x] Cursor-based pagination
- [x] Booking state machine + authorization matrix
- [x] Comment 3-cấp authorization
- [x] Conversation-based messaging (thay direct message)
- [x] Presigned URL upload
- [x] Admin dashboard stats

### ✅ Security
- [x] bcrypt password hashing
- [x] JWT + Refresh Token rotation
- [x] Rate limiting per endpoint group
- [x] 8-layer middleware chain
- [x] Signed URLs cho media
- [x] RBAC (USER/ADMIN/PHOTOGRAPHER)
- [x] Input validation tất cả endpoints
- [x] Audit logging

### ✅ Scale
- [x] Redis caching strategy
- [x] Read replica plan
- [x] Table partitioning plan
- [x] Message queue cho async operations
- [x] Denormalization + triggers
