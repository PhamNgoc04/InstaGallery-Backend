## --- Nhóm: Ảnh & Tương tác ---

### 2.2.5.15. Use Case Xem chi tiết bài đăng

**- Biểu đồ lớp phân tích:**

```mermaid
classDiagram
    class ChiTietAnhUI {
        <<boundary>>
        +hienThiCarouselAnh()
        +hienThiCaptionInfo()
        +hienThiDanhSachComment()
        +clickLike()
        +clickComment()
        +clickSave()
        +clickAvatar()
    }

    class ChiTietAnhController {
        <<control>>
        +layChiTietBaiDang()
        +layDanhSachComment()
        +kiemTraTrangThaiLike()
        +kiemTraTrangThaiSave()
        +chuyenManHinh()
    }

    class Post {
        <<entity>>
        +id : BIGINT
        +user_id : BIGINT
        +caption : TEXT
        +location : VARCHAR
        +visibility : ENUM
        +like_count : INT
        +comment_count : INT
        +created_at : TIMESTAMP
        +getId()
        +getUserId()
        +getCaption()
        +getVisibility()
        +getLikeCount()
        +getCommentCount()
    }

    class PostMedia {
        <<entity>>
        +id : BIGINT
        +post_id : BIGINT
        +media_file_url : VARCHAR
        +media_type : ENUM
        +position : INT
        +getMediaFileUrl()
        +getMediaType()
        +getPosition()
    }

    class Comment {
        <<entity>>
        +id : BIGINT
        +post_id : BIGINT
        +user_id : BIGINT
        +content : TEXT
        +parent_comment_id : BIGINT
        +depth : TINYINT
        +created_at : TIMESTAMP
        +getContent()
        +getDepth()
        +getParentCommentId()
    }

    class ICsdl {
        <<boundary>>
        +truyVanBangPosts()
        +truyVanBangPostMedia()
        +truyVanBangComments()
        +truyVanBangLikes()
        +truyVanBangSavedPosts()
    }

    ChiTietAnhUI "1" -- "1" ChiTietAnhController
    ChiTietAnhController "1" -- "1" Post
    ChiTietAnhController "1" -- "*" PostMedia
    ChiTietAnhController "1" -- "*" Comment
    ChiTietAnhController "1" -- "*" ICsdl
```

**- Biểu đồ trình tự:**

```mermaid
sequenceDiagram
    actor KH as Người dùng
    participant UI as ChiTietAnhUI
    participant CT as ChiTietAnhController
    participant P as Post
    participant PM as PostMedia
    participant C as Comment
    participant DB as ICsdl

    KH->>UI: clickBaiDang()
    UI->>CT: layChiTietBaiDang(postId)
    CT->>DB: truyVanBangPosts(postId)
    DB-->>P: thông tin bài đăng
    CT->>DB: truyVanBangPostMedia(postId)
    DB-->>PM: danh sách ảnh/video
    CT->>DB: truyVanBangLikes(userId, postId)
    DB-->>CT: trạng thái is_liked
    CT->>DB: truyVanBangSavedPosts(userId, postId)
    DB-->>CT: trạng thái is_saved
    CT-->>UI: hienThiCarouselAnh()
    UI-->>KH: hiển thị carousel + caption + info

    UI->>CT: layDanhSachComment(postId)
    CT->>DB: truyVanBangComments(postId)
    DB-->>C: danh sách comments (threaded)
    CT-->>UI: hienThiDanhSachComment()
    UI-->>KH: hiển thị danh sách bình luận
```

---

### 2.2.5.10. Use Case Upload ảnh / Đăng bài

**- Biểu đồ lớp phân tích:**

```mermaid
classDiagram
    class UploadAnhUI {
        <<boundary>>
        +moGallery()
        +hienThiPreviewAnh()
        +chonFilter()
        +nhapCaption()
        +chonViTri()
        +chonVisibility()
        +clickDangBai()
        +hienThiThongBao()
    }

    class UploadAnhController {
        <<control>>
        +validateSoLuongAnh()
        +layPresignedUrl()
        +uploadAnhLenStorage()
        +taoBaiDangMoi()
        +guiNotification()
        +chuyenManHinh()
    }

    class Post {
        <<entity>>
        +id : BIGINT
        +user_id : BIGINT
        +caption : TEXT
        +location : VARCHAR
        +visibility : ENUM
        +created_at : TIMESTAMP
        +setCaption()
        +setLocation()
        +setVisibility()
    }

    class PostMedia {
        <<entity>>
        +id : BIGINT
        +post_id : BIGINT
        +media_file_url : VARCHAR
        +media_type : ENUM
        +position : INT
        +filter_id : BIGINT
        +setMediaFileUrl()
        +setPosition()
        +setFilterId()
    }

    class User {
        <<entity>>
        +id : BIGINT
        +post_count : INT
        +getPostCount()
        +setPostCount()
    }

    class ICsdl {
        <<boundary>>
        +layPresignedUrl()
        +uploadObjectStorage()
        +themBangPosts()
        +themBangPostMedia()
        +capNhatBangUsers()
        +themBangNotifications()
    }

    UploadAnhUI "1" -- "1" UploadAnhController
    UploadAnhController "1" -- "1" Post
    UploadAnhController "1" -- "*" PostMedia
    UploadAnhController "1" -- "1" User
    UploadAnhController "1" -- "*" ICsdl
```

**- Biểu đồ trình tự:**

```mermaid
sequenceDiagram
    actor KH as Người dùng
    participant UI as UploadAnhUI
    participant CT as UploadAnhController
    participant P as Post
    participant PM as PostMedia
    participant U as User
    participant DB as ICsdl

    KH->>UI: click nút "+"
    UI->>UI: moGallery()
    KH->>UI: chọn ảnh (tối đa 10)
    UI->>CT: validateSoLuongAnh()
    UI->>UI: hienThiPreviewAnh()
    KH->>UI: nhapCaption(), chonViTri(), chonVisibility()
    KH->>UI: clickDangBai()
    loop Mỗi ảnh (1..N)
        UI->>CT: layPresignedUrl()
        CT->>DB: layPresignedUrl()
        DB-->>CT: upload_url
        CT->>DB: uploadObjectStorage()
        DB-->>CT: media_url
    end
    CT->>P: createPost()
    P->>DB: themBangPosts()
    DB-->>CT: post_id
    CT->>PM: createPostMedia()
    PM->>DB: themBangPostMedia()
    CT->>U: updatePostCount()
    U->>DB: capNhatBangUsers()
    CT->>DB: themBangNotifications()
    CT->>UI: hienThiThongBao("Đăng bài thành công")
    UI-->>KH: chuyển về Feed
```

---

### 2.2.5.11. Use Case Chỉnh sửa bài đăng

**- Biểu đồ lớp phân tích:**

```mermaid
classDiagram
    class ChinhSuaBaiDangUI {
        <<boundary>>
        +clickMenuSua()
        +hienThiFormChinhSua()
        +nhapCaptionMoi()
        +chonVisibilityMoi()
        +clickLuu()
        +clickHuy()
        +hienThiThongBao()
    }

    class ChinhSuaBaiDangController {
        <<control>>
        +layThongTinBaiDang()
        +kiemTraQuyenSoHuu()
        +validateDuLieu()
        +capNhatBaiDang()
    }

    class Post {
        <<entity>>
        +id : BIGINT
        +user_id : BIGINT
        +caption : TEXT
        +location : VARCHAR
        +visibility : ENUM
        +updated_at : TIMESTAMP
        +getId()
        +getUserId()
        +getCaption()
        +setCaption()
        +getVisibility()
        +setVisibility()
        +getLocation()
        +setLocation()
    }

    class ICsdl {
        <<boundary>>
        +truyVanBangPosts()
        +capNhatBangPosts()
    }

    ChinhSuaBaiDangUI "1" -- "1" ChinhSuaBaiDangController
    ChinhSuaBaiDangController "1" -- "1" Post
    ChinhSuaBaiDangController "1" -- "*" ICsdl
```

**- Biểu đồ trình tự:**

```mermaid
sequenceDiagram
    actor KH as Người dùng
    participant UI as ChinhSuaBaiDangUI
    participant CT as ChinhSuaBaiDangController
    participant P as Post
    participant DB as ICsdl

    KH->>UI: clickMenuSua()
    UI->>CT: layThongTinBaiDang(postId)
    CT->>DB: truyVanBangPosts(postId)
    DB-->>P: thông tin bài đăng
    CT->>CT: kiemTraQuyenSoHuu()
    CT-->>UI: hienThiFormChinhSua()
    UI-->>KH: hiển thị form (pre-fill caption, visibility)

    KH->>UI: nhapCaptionMoi()
    KH->>UI: chonVisibilityMoi()
    KH->>UI: clickLuu()
    UI->>CT: capNhatBaiDang()
    CT->>CT: validateDuLieu()
    CT->>P: setCaption(), setVisibility()
    P->>DB: capNhatBangPosts()
    DB-->>CT: return kết quả
    CT->>UI: hienThiThongBao("Đã cập nhật bài đăng")
    UI-->>KH: cập nhật giao diện
```

---

### 2.2.5.19 & 2.2.5.20. Use Case Like & Comment

**- Biểu đồ lớp phân tích:**

```mermaid
classDiagram
    class LikeCommentUI {
        <<boundary>>
        +clickNutLike()
        +clickNutUnlike()
        +nhapNoiDungComment()
        +clickGuiComment()
        +clickTraLoiComment()
        +capNhatSoLike()
        +hienThiCommentMoi()
    }

    class LikeCommentController {
        <<control>>
        +xuLyLike()
        +xuLyUnlike()
        +taoComment()
        +taoReplyComment()
        +kiemTraDepth()
        +guiNotification()
    }

    class Like {
        <<entity>>
        +id : BIGINT
        +user_id : BIGINT
        +post_id : BIGINT
        +created_at : TIMESTAMP
        +getUserId()
        +setUserId()
        +getPostId()
        +setPostId()
    }

    class Comment {
        <<entity>>
        +id : BIGINT
        +post_id : BIGINT
        +user_id : BIGINT
        +content : TEXT
        +parent_comment_id : BIGINT
        +depth : TINYINT
        +created_at : TIMESTAMP
        +getContent()
        +setContent()
        +getDepth()
        +setDepth()
        +getParentCommentId()
        +setParentCommentId()
    }

    class Notification {
        <<entity>>
        +id : BIGINT
        +user_id : BIGINT
        +type : ENUM
        +target_id : BIGINT
        +setType()
        +setTargetId()
    }

    class ICsdl {
        <<boundary>>
        +themBangLikes()
        +xoaBangLikes()
        +themBangComments()
        +capNhatBangPosts()
        +themBangNotifications()
    }

    LikeCommentUI "1" -- "1" LikeCommentController
    LikeCommentController "1" -- "*" Like
    LikeCommentController "1" -- "*" Comment
    LikeCommentController "1" -- "*" Notification
    LikeCommentController "1" -- "*" ICsdl
```

**- Biểu đồ trình tự:**

```mermaid
sequenceDiagram
    actor KH as Người dùng
    participant UI as LikeCommentUI
    participant CT as LikeCommentController
    participant L as Like
    participant C as Comment
    participant N as Notification
    participant DB as ICsdl

    Note over KH,DB: === LIKE ===
    KH->>UI: clickNutLike()
    UI->>UI: capNhatSoLike(+1)
    UI->>CT: xuLyLike(postId)
    CT->>L: createLike()
    L->>DB: themBangLikes()
    Note over DB: Trigger: like_count + 1
    CT->>N: createNotification(NEW_LIKE)
    N->>DB: themBangNotifications()
    DB-->>CT: return kết quả

    Note over KH,DB: === COMMENT ===
    KH->>UI: nhapNoiDungComment()
    KH->>UI: clickGuiComment()
    UI->>CT: taoComment(postId, content)
    CT->>C: createComment()
    C->>DB: themBangComments()
    CT->>DB: capNhatBangPosts(comment_count + 1)
    CT->>N: createNotification(NEW_COMMENT)
    N->>DB: themBangNotifications()
    DB-->>CT: return kết quả
    CT-->>UI: hienThiCommentMoi()
    UI-->>KH: hiển thị comment mới
```

---

### 2.2.5.23. Use Case Tìm kiếm ảnh / Thợ ảnh / Hashtag

**- Biểu đồ lớp phân tích:**

```mermaid
classDiagram
    class TimKiemUI {
        <<boundary>>
        +hienThiThanhTimKiem()
        +hienThiLichSuTimKiem()
        +nhapTuKhoa()
        +hienThiGoiY()
        +clickTimKiem()
        +hienThiKetQua()
        +clickKetQua()
    }

    class TimKiemController {
        <<control>>
        +layLichSuTimKiem()
        +goiYAutocomplete()
        +thucHienTimKiem()
        +luuLichSuTimKiem()
        +xoaLichSu()
        +chuyenManHinh()
    }

    class SearchHistory {
        <<entity>>
        +id : BIGINT
        +user_id : BIGINT
        +query_text : VARCHAR
        +result_count : INT
        +searched_at : TIMESTAMP
        +getQueryText()
        +setQueryText()
        +getResultCount()
        +setResultCount()
    }

    class MediaTag {
        <<entity>>
        +id : BIGINT
        +name : VARCHAR
        +usage_count : INT
        +getName()
        +getUsageCount()
    }

    class ICsdl {
        <<boundary>>
        +truyVanBangSearchHistories()
        +truyVanBangUsers()
        +truyVanBangMediaTags()
        +truyVanBangPosts()
        +themBangSearchHistories()
        +xoaBangSearchHistories()
    }

    TimKiemUI "1" -- "1" TimKiemController
    TimKiemController "1" -- "*" SearchHistory
    TimKiemController "1" -- "*" MediaTag
    TimKiemController "1" -- "*" ICsdl
```

**- Biểu đồ trình tự:**

```mermaid
sequenceDiagram
    actor KH as Người dùng
    participant UI as TimKiemUI
    participant CT as TimKiemController
    participant SH as SearchHistory
    participant MT as MediaTag
    participant DB as ICsdl

    KH->>UI: click thanh tìm kiếm
    UI->>CT: layLichSuTimKiem()
    CT->>DB: truyVanBangSearchHistories(userId)
    DB-->>SH: danh sách lịch sử
    CT-->>UI: hienThiLichSuTimKiem()
    UI-->>KH: hiển thị lịch sử tìm kiếm

    KH->>UI: nhapTuKhoa("wedding")
    UI->>CT: goiYAutocomplete("wedding")
    CT->>DB: truyVanBangUsers(LIKE "wedding%")
    CT->>DB: truyVanBangMediaTags(LIKE "wedding%")
    DB-->>MT: gợi ý tags
    CT-->>UI: hienThiGoiY()
    UI-->>KH: hiển thị dropdown gợi ý

    KH->>UI: clickTimKiem()
    UI->>CT: thucHienTimKiem("wedding")
    CT->>DB: truyVanBangUsers() + truyVanBangPosts() + truyVanBangMediaTags()
    DB-->>CT: kết quả tìm kiếm
    CT->>SH: createSearchHistory()
    SH->>DB: themBangSearchHistories()
    CT-->>UI: hienThiKetQua()
    UI-->>KH: hiển thị kết quả (tabs: Users/Posts/Tags)
```
