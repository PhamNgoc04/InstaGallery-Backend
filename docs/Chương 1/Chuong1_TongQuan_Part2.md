## 1.2. Kotlin

### 1.2.1. Giới thiệu về Kotlin

Kotlin là ngôn ngữ lập trình **mã nguồn mở, kiểu tĩnh (statically-typed)** được phát triển bởi **JetBrains** — công ty đứng sau các IDE nổi tiếng như IntelliJ IDEA và PyCharm. Kotlin được thiết kế để chạy trên **JVM (Java Virtual Machine)**, có khả năng **tương thích hoàn toàn 100% với Java** — nghĩa là có thể gọi code Java từ Kotlin và ngược lại, đồng thời sử dụng toàn bộ hệ sinh thái thư viện Java sẵn có.

Kể từ phiên bản Android Studio 3.0, Kotlin đã được Google hỗ trợ đầy đủ trong việc phát triển ứng dụng Android và được nhúng trực tiếp vào gói cài đặt của IDE. Đến **Google I/O 2019**, Google chính thức tuyên bố Kotlin là **ngôn ngữ ưu tiên số 1 (Kotlin-first)** cho nền tảng Android. Hiện nay, hơn 95% trong top 1000 ứng dụng trên Google Play sử dụng Kotlin.

Trong dự án InstaGallery, Kotlin được sử dụng cho **cả hai phía**: ứng dụng Android (Client) và máy chủ Backend (Ktor Server), tạo nên một hệ thống **full-stack Kotlin** thống nhất về ngôn ngữ.

### 1.2.2. Sơ lược lịch sử của Kotlin

| Mốc thời gian | Sự kiện |
|---------------|---------|
| **7/2011** | JetBrains công bố dự án Kotlin |
| **2/2016** | **Kotlin 1.0** — phiên bản ổn định đầu tiên chính thức phát hành |
| **5/2017** | Google công bố **hỗ trợ chính thức Kotlin** cho Android tại Google I/O 2017 |
| **11/2017** | **Kotlin 1.2** — hỗ trợ chia sẻ mã nguồn giữa JVM và JavaScript (Multiplatform) |
| **10/2018** | **Kotlin 1.3** — Coroutines API chính thức ổn định, hỗ trợ xử lý bất đồng bộ |
| **5/2019** | Google tuyên bố Kotlin là **ngôn ngữ ưu tiên số 1** (Kotlin-first) cho Android |
| **5/2021** | **Kotlin 1.5** — hỗ trợ JVM Records, Sealed interfaces |
| **5/2024** | **Kotlin 2.0** — K2 compiler chính thức, hiệu suất biên dịch nhanh gấp 2 lần |

### 1.2.3. Ưu điểm của Kotlin

- **Cấu trúc code ngắn gọn:** Giảm đáng kể lượng boilerplate code so với Java. Ví dụ, một `data class` trong Kotlin chỉ cần 1 dòng, tương đương khoảng 50-80 dòng Java (bao gồm getter/setter/equals/hashCode/toString).

```kotlin
// Kotlin: 1 dòng = đầy đủ chức năng
data class User(val id: Int, val username: String, val email: String)
```

- **Null Safety (An toàn Null):** Kotlin phân biệt rõ ràng kiểu nullable (`String?`) và non-nullable (`String`) ngay tại thời điểm biên dịch, loại bỏ hoàn toàn lỗi `NullPointerException` — lỗi phổ biến nhất trong Java.

```kotlin
var name: String = "InstaGallery"  // Không thể null
var bio: String? = null            // Có thể null
val displayBio = bio ?: "Chưa cập nhật"  // Elvis operator — giá trị mặc định
```

- **Dễ tiếp cận:** Cú pháp hiện đại, trực quan, người biết Java có thể học Kotlin nhanh chóng.
- **Biên dịch linh hoạt:** Có thể biên dịch thành Java bytecode (JVM), JavaScript, hoặc Native code.
- **Tương thích Java:** Sử dụng tất cả thư viện Java thông qua interop, không cần viết lại.
- **Mã nguồn mở:** Phát hành theo giấy phép Apache 2.0, hoàn toàn miễn phí.
- **Coroutines:** Hỗ trợ xử lý bất đồng bộ hiệu quả mà không cần callback phức tạp.

```kotlin
// Gọi API bất đồng bộ — code tuần tự nhưng chạy non-blocking
viewModelScope.launch {
    val user = userRepository.login(email, password)  // Suspend function
    _uiState.value = UiState.Success(user)
}
```

- **Extension Functions:** Mở rộng chức năng của class hiện có mà không cần kế thừa.
- **Sealed Classes:** Biểu diễn tập hợp giới hạn các kiểu con, phù hợp cho State management trong MVVM.

### 1.2.4. Ứng dụng của Kotlin trong Android

Hiện nay Kotlin là **ngôn ngữ số 1** được hỗ trợ trên nền tảng Android nhờ tính ngắn gọn, an toàn và biểu đạt rõ ràng hơn Java. Google đã xây dựng nhiều thư viện Jetpack (ViewModel, Navigation, Room, DataStore, Lifecycle,...) với **Kotlin-first API** — nghĩa là API được thiết kế tối ưu cho Kotlin trước, Java thứ hai.

Tuy nhiên, Kotlin không thay thế Java hoàn toàn trong quá trình xây dựng ứng dụng — Kotlin vẫn cần JVM (Java Virtual Machine) để biên dịch và chạy trên thiết bị Android. Cả hai ngôn ngữ có thể tồn tại song song trong cùng một dự án.

Trong InstaGallery, Kotlin được sử dụng cho:

| Thành phần | Vai trò |
|-----------|---------|
| **Android App** | UI XML + AppCompat/Material/ConstraintLayout, Fragment/ViewBinding, ViewModel, Repository |
| **Backend Server (Ktor)** | Toàn bộ Routes, Services, Repositories phía server |

---

## 1.3. Android Studio

### 1.3.1. Giới thiệu về Android Studio

**Android Studio** là môi trường phát triển tích hợp (IDE) chính thức của Google dành cho phát triển ứng dụng Android. Phiên bản đầu tiên được phát hành vào **tháng 5/2013**, xây dựng trên nền tảng IntelliJ IDEA của JetBrains và hoàn toàn miễn phí theo giấy phép Apache License 2.0.

Android Studio cung cấp đầy đủ công cụ cần thiết cho quy trình phát triển ứng dụng Android:

- **Gradle Build System:** Hệ thống build linh hoạt, quản lý dependencies và build variants (debug/release) tự động.
- **Android Emulator:** Giả lập thiết bị Android để chạy thử ứng dụng trên nhiều phiên bản Android và kích thước màn hình khác nhau.
- **Layout Inspector & Profiler:** Công cụ phân tích hiệu năng, phát hiện memory leak, theo dõi CPU/Network/Battery usage.
- **Layout Editor & Preview:** Xem trước giao diện XML, ConstraintLayout và Material Components ngay trong IDE.
- **Layout Inspector, Network Inspector & Profiler:** Hỗ trợ kiểm tra giao diện, request mạng, CPU, bộ nhớ và hiệu năng ứng dụng.
- **Tích hợp Git/GitHub:** Quản lý mã nguồn, commit, push, pull request trực tiếp trong IDE.

### 1.3.2. Ứng dụng của Android Studio

Trong dự án InstaGallery, Android Studio đóng vai trò:

- Là IDE chính để phát triển **ứng dụng InstaGallery trên nền tảng Android**.
- Xây dựng giao diện bằng **XML Layout + AppCompat + Material + ConstraintLayout**, sử dụng Layout Editor/Preview để kiểm tra từng màn hình.
- Phát triển ứng dụng từ project Kotlin, xây dựng giao diện, business logic, kết nối API Backend.
- Kết nối ứng dụng với **backend Ktor** qua REST API/WebSocket, dùng Ktor Client để gọi API và Coil để hiển thị ảnh.
- Chạy và debug ứng dụng trên **Android Emulator** hoặc thiết bị thật qua USB/Wi-Fi.
- Quản lý mã nguồn với tính năng **tích hợp GitHub**.

---

## 1.4. XML Layout, AppCompat và Material Components

### 1.4.1. Giới thiệu về UI stack Android đang sử dụng

Trong phiên bản Android hiện tại của InstaGallery, giao diện được xây dựng bằng **XML Layout + AppCompat + Material Components + ConstraintLayout**. Đây là stack ổn định, phổ biến trong Android native, phù hợp với project đang có `MainActivity`, `activity_main.xml` và các dependency AppCompat/Material/ConstraintLayout sẵn trong Gradle.

XML Layout tách phần mô tả giao diện khỏi code Kotlin xử lý logic. Activity/Fragment dùng ViewBinding để truy cập view an toàn, ViewModel quản lý trạng thái qua StateFlow, còn Fragment chỉ render state và gửi user action vào ViewModel.

```xml
<!-- Ví dụ: nút Like trong layout XML -->
<com.google.android.material.button.MaterialButton
    android:id="@+id/likeButton"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="@string/like"
    app:icon="@drawable/ic_favorite_border" />
```

```kotlin
// Fragment chỉ render state, không chứa business logic.
binding.likeButton.setOnClickListener {
    viewModel.onLikeClicked(postId)
}
```

### 1.4.2. Lý do chọn XML/AppCompat cho InstaGallery

| Tiêu chí | Lý do |
|----------|------|
| Phù hợp codebase hiện tại | Project đang là XML/AppCompat skeleton, không cần migrate UI stack ngay từ đầu |
| Dễ triển khai theo từng màn hình | Activity/Fragment + ViewBinding phù hợp với vertical slice nhỏ |
| Ổn định và dễ debug | Layout Inspector, Preview, ConstraintLayout tooling hỗ trợ tốt |
| Tương thích thư viện UI | Material Components, RecyclerView, Navigation Fragment, Coil ImageView dùng tốt với XML |
| Giảm rủi ro đồ án | Tập trung vào backend integration, auth, feed, booking thay vì đổi framework UI |

Compose vẫn có thể là hướng mở rộng trong tương lai, nhưng không phải lựa chọn triển khai chính của phiên bản hiện tại.

---

## 1.5. Lưu trữ media qua Presigned URL

### 1.5.1. Nhu cầu lưu trữ media

InstaGallery là ứng dụng chia sẻ ảnh, vì vậy hệ thống cần cơ chế lưu trữ media tách khỏi cơ sở dữ liệu quan hệ. MySQL chỉ lưu metadata như `media_file_url`, `media_type`, `position`, `post_id`; file ảnh/video thực tế được lưu ở một storage provider riêng.

Trong bản thiết kế hiện tại, Android không phụ thuộc trực tiếp vào một SDK storage cụ thể. Ứng dụng gọi backend Ktor để lấy **presigned URL**, sau đó upload file lên storage qua URL tạm thời đó. Giai đoạn MVP tạm thời **không dùng Firebase**; storage provider sẽ được quyết định sau, có thể là storage nội bộ/local dev, S3-compatible storage hoặc một dịch vụ cloud khác.

### 1.5.2. Quy trình upload media

| Bước | Mô tả |
|------|------|
| 1 | Android App gọi `POST /api/v1/media/presigned-url` để xin URL upload |
| 2 | Backend Ktor kiểm tra JWT, quyền upload và tạo presigned URL có thời hạn |
| 3 | Android App upload file trực tiếp lên `uploadUrl` bằng HTTP PUT/POST |
| 4 | Sau khi upload thành công, Android gọi API tạo bài đăng hoặc gắn media vào bài đăng |
| 5 | Backend lưu URL/metadata vào MySQL, ví dụ bảng `post_media` |

**Quy trình tổng quát:**

```text
Android App
-> POST /api/v1/media/presigned-url
-> nhận uploadUrl
-> upload file lên object storage
-> POST /api/v1/posts hoặc POST /api/v1/posts/{postId}/media
-> backend lưu metadata vào MySQL
```

### 1.5.3. Lợi ích của presigned URL

- **Không khóa chặt vào một provider:** Android chỉ biết upload URL do backend trả về, không cần biết storage cụ thể là local, S3-compatible, Cloudinary hay provider khác.
- **Giảm tải backend:** File lớn được upload trực tiếp lên storage, backend không phải nhận toàn bộ binary.
- **An toàn hơn:** URL upload có thời hạn, chỉ cấp cho người dùng đã xác thực.
- **Phù hợp app media-heavy:** Feed, profile, portfolio và album có thể dùng URL media đã lưu để hiển thị ảnh qua Coil.
- **Dễ mở rộng:** Khi cần CDN hoặc đổi storage provider, thay đổi chủ yếu nằm ở backend/config.

---

## 1.6. MySQL

### 1.6.1. Giới thiệu về MySQL

**MySQL** là hệ quản trị cơ sở dữ liệu quan hệ (RDBMS) **mã nguồn mở** phổ biến nhất thế giới, hiện thuộc sở hữu của Oracle Corporation. MySQL sử dụng ngôn ngữ truy vấn **SQL (Structured Query Language)** để quản lý dữ liệu có cấu trúc.

Đặc điểm nổi bật của MySQL:

| Đặc điểm | Mô tả |
|----------|-------|
| **Hiệu suất cao** | Xử lý hàng triệu truy vấn/giây, phù hợp với hệ thống có nhiều người dùng đồng thời |
| **ACID Compliance** | Đảm bảo tính Atomicity (nguyên tử), Consistency (nhất quán), Isolation (cô lập), Durability (bền vững) cho mọi transaction |
| **InnoDB Engine** | Storage engine mặc định, hỗ trợ transaction, foreign key, row-level locking |
| **Bảo mật tốt** | Hỗ trợ SSL/TLS encryption, xác thực người dùng, phân quyền chi tiết |
| **Hỗ trợ đa người dùng** | Nhiều người truy cập và thao tác dữ liệu đồng thời mà không ảnh hưởng hiệu suất |
| **Quan hệ phức tạp** | Hỗ trợ mô hình dữ liệu quan hệ với Foreign Key, JOIN, Subquery — phù hợp cho User ↔ Post ↔ Booking ↔ Rating |

### 1.6.2. Ứng dụng MySQL trong hệ thống InstaGallery

Trong hệ thống InstaGallery, **MySQL 8.x** (InnoDB, utf8mb4) được sử dụng làm cơ sở dữ liệu chính, lưu trữ toàn bộ dữ liệu có cấu trúc của ứng dụng. Cơ sở dữ liệu backend mới gồm **30 bảng** chia thành 10 nhóm:

| Nhóm | Các bảng | Mục đích |
|------|---------|----------|
| **Auth & Identity** | users, user_sessions, password_reset_tokens | Quản lý tài khoản, phiên đăng nhập, khôi phục mật khẩu |
| **Content & Media** | posts, post_media, filters, media_tags, post_media_tags | Quản lý bài đăng, media, filter và tag |
| **Interaction** | likes, comment_likes, comments, saved_posts | Tương tác với bài đăng và bình luận |
| **User Relationship** | followers, follow_requests, blocked_users, muted_users | Theo dõi, yêu cầu theo dõi, chặn, tắt tiếng |
| **Messaging** | conversations, conversation_members, messages | Chat và tin nhắn |
| **Album** | albums, album_media | Quản lý album cá nhân |
| **Photography Service** | portfolios, availability_schedules, bookings, ratings | Portfolio, lịch khả dụng, đặt lịch, đánh giá |
| **Notification** | notifications | Thông báo hệ thống |
| **Search** | search_histories | Lịch sử tìm kiếm |
| **Moderation & Audit** | reports, banned_words, activity_logs | Báo cáo vi phạm, từ khóa cấm, nhật ký hệ thống |

MySQL đảm bảo tính toàn vẹn dữ liệu thông qua **khóa chính (PK)**, **khóa ngoại (FK)**, **CHECK constraints** và **UNIQUE constraints**. Ngoài ra, hệ thống sử dụng:

- **Trigger** để tự động đồng bộ counter (like_count, follower_count) khi dữ liệu thay đổi.
- **Soft Delete** (cột `deleted_at`) để xóa mềm thay vì xóa vĩnh viễn dữ liệu quan trọng.
- **Denormalized Counters** để tối ưu hiệu suất truy vấn đếm trên dữ liệu lớn.
- **Character set utf8mb4** để hỗ trợ lưu trữ emoji 🎉📸❤️ trong bài đăng và bình luận.

---

## 1.7. Kiến trúc phần mềm

### 1.7.1. Clean Architecture

**Clean Architecture** là kiến trúc phần mềm được đề xuất bởi **Robert C. Martin (Uncle Bob)**, chia ứng dụng thành các lớp đồng tâm với quy tắc **phụ thuộc hướng vào trong (Dependency Rule)** — lớp bên trong không bao giờ biết về lớp bên ngoài:

```
┌─────────────────────────────────────────────────────┐
│  FRAMEWORKS & DRIVERS (Ngoài cùng)                  │
│  XML/AppCompat, Ktor, MySQL, Object Storage, Redis  │
│  ┌─────────────────────────────────────────────┐    │
│  │  INTERFACE ADAPTERS                         │    │
│  │  ViewModel, RepositoryImpl, API Controllers │    │
│  │  ┌─────────────────────────────────────┐    │    │
│  │  │  USE CASES (Logic ứng dụng)         │    │    │
│  │  │  GetFeedUseCase, CreateBookingUC    │    │    │
│  │  │  ┌─────────────────────────────┐    │    │    │
│  │  │  │  ENTITIES (Lõi nghiệp vụ)  │    │    │    │
│  │  │  │  User, Post, Booking        │    │    │    │
│  │  │  └─────────────────────────────┘    │    │    │
│  │  └─────────────────────────────────────┘    │    │
│  └─────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────┘
        ← Dependency Rule: phụ thuộc hướng vào trong →
```

Clean Architecture đảm bảo **business logic độc lập** với framework bên ngoài — có thể thay đổi UI (ví dụ sau này chuyển từ XML sang Compose nếu có quyết định riêng), local cache (Room hoặc giải pháp khác), hoặc network library (Ktor Client sang thư viện khác) mà **không ảnh hưởng đến lõi nghiệp vụ**.

Trong Android InstaGallery, Clean Architecture được triển khai qua **3 layer**:

| Layer | Package | Chức năng | Ví dụ |
|-------|---------|----------|-------|
| **Presentation/UI** | `feature/<name>/ui/` | Fragment/Activity + ViewModel — hiển thị state và xử lý sự kiện người dùng | `FeedFragment.kt`, `FeedViewModel.kt` |
| **Domain** | `feature/<name>/domain/` | Domain model + Repository Interface + UseCase optional | `FeedPost.kt`, `FeedRepository.kt` |
| **Data** | `feature/<name>/data/` | RepositoryImpl + RemoteDataSource + API + DTO + Mapper | `FeedRepositoryImpl.kt`, `FeedApi.kt` |

### 1.7.2. Mô hình MVVM

**MVVM (Model – View – ViewModel)** là mô hình kiến trúc UI được **Google khuyến nghị chính thức** cho phát triển Android. Mô hình chia ứng dụng thành 3 thành phần tách biệt:

```
┌──────────────┐     quan sát      ┌──────────────────┐     gọi        ┌──────────────┐
│              │ ◄──────────────── │                  │ ──────────────►│              │
│     VIEW     │    StateFlow /    │   VIEW MODEL     │   UseCase /    │    MODEL     │
│ Fragment/View│    StateFlow      │  Koin ViewModel  │   Repository   │  (Data Layer)│
│              │                   │                  │                │              │
│              │ ──────────────── ►│                  │ ◄──────────────│              │
└──────────────┘  sự kiện user     └──────────────────┘    dữ liệu     └──────────────┘
```

| Thành phần | Vai trò | Ví dụ trong InstaGallery |
|-----------|---------|-------------------------|
| **Model** | Các domain model, data source, repository — quản lý dữ liệu. Không ràng buộc với View hay ViewModel, có thể tái sử dụng. | `FeedRepository`, `User`, `FeedPost` |
| **View** | Fragment/Activity hiển thị giao diện, nhận sự kiện người dùng. Liên kết với ViewModel thông qua **StateFlow** để quan sát thay đổi trạng thái. **Không chứa business logic.** | `FeedFragment`, `LoginFragment`, `PostDetailFragment` |
| **ViewModel** | Cầu nối giữa View và Model. Gọi Repository/UseCase, quản lý **UiState** rồi truyền kết quả cho View qua **StateFlow** và event một lần qua **SharedFlow**. | `FeedViewModel`, `LoginViewModel`, `BookingViewModel` |

**Lợi ích khi kết hợp MVVM + Clean Architecture:**
- **Separation of Concerns:** Mỗi lớp chỉ chịu trách nhiệm một việc duy nhất.
- **Testability:** ViewModel và UseCase có thể unit test bằng JUnit mà không cần thiết bị Android.
- **Lifecycle-aware:** ViewModel sống sót qua configuration change (xoay màn hình) — dữ liệu không bị mất.
- **Scalability:** Thêm tính năng mới (ví dụ: thêm module Chat) không ảnh hưởng đến tính năng cũ.

---

## 1.8. Bảo mật hệ thống

### 1.8.1. JWT Authentication

**JWT (JSON Web Token)** là cơ chế xác thực **stateless** — server không cần lưu trạng thái session trong bộ nhớ, mọi thông tin xác thực được chứa trong token đã ký số. JWT có cấu trúc gồm 3 phần: `Header.Payload.Signature`.

```
Header:    { "alg": "HS256", "typ": "JWT" }
Payload:   { "userId": 123, "role": "CLIENT", "exp": 1234567890 }
Signature: HMAC-SHA256(base64(header) + "." + base64(payload), secretKey)
```

Trong InstaGallery, JWT được triển khai theo mô hình **Access Token + Refresh Token**:

| Token | Thời hạn | Lưu trữ | Mục đích |
|-------|----------|---------|----------|
| **Access Token** | 15 phút | TokenStore trên Android | Gắn vào header `Authorization: Bearer <token>` mỗi API request |
| **Refresh Token** | 30 ngày | TokenStore + MySQL (`user_sessions`) | Gia hạn Access Token khi hết hạn, không cần đăng nhập lại |

Khi Access Token hết hạn (server trả về lỗi 401 Unauthorized), ứng dụng tự động gọi `POST /auth/refresh` kèm Refresh Token để lấy Access Token mới — giúp người dùng không bị đăng xuất đột ngột. Luồng này được xử lý trong `core.network.TokenRefreshHandler` của Ktor Client, có single-flight để tránh nhiều request refresh chạy song song.

Mật khẩu người dùng được hash bằng thuật toán **bcrypt** (cost factor = 12) trước khi lưu vào database — không bao giờ lưu plaintext.

### 1.8.2. Signed URLs và Watermark

**Signed URLs** là các đường dẫn truy cập tạm thời được ký số (thường bằng HMAC hoặc JWT), sử dụng để giới hạn quyền truy cập vào tài nguyên media (ảnh, video). Các liên kết này có thể bị giới hạn theo:
- **Thời gian:** URL chỉ hợp lệ trong X phút, sau đó tự động hết hạn.
- **Địa chỉ IP:** Chỉ cho phép truy cập từ IP đã xác định.

Trong InstaGallery, Signed URLs được áp dụng cho quá trình upload ảnh lên object storage thông qua backend — đảm bảo chỉ người dùng đã xác thực mới có thể upload, và URL upload không thể bị tái sử dụng sau khi hết hạn.

**Watermark** là phương pháp thêm logo, văn bản hoặc mã QR ẩn/hiện vào hình ảnh trước khi hiển thị, nhằm hạn chế việc sao chép và phân phối trái phép nội dung nhiếp ảnh. Đây là tính năng quan trọng cho cộng đồng nhiếp ảnh chuyên nghiệp, giúp bảo vệ bản quyền tác phẩm.

Việc kết hợp Signed URLs và Watermark tạo nên **lớp bảo vệ kép** cho nội dung media nhạy cảm trong ứng dụng chia sẻ ảnh.

### 1.8.3. Mã hóa dữ liệu (TLS 1.3, AES-256)

Hệ thống InstaGallery áp dụng mã hóa ở **hai tầng**:

**a) Tầng đường truyền — TLS 1.3:**

TLS 1.3 (Transport Layer Security) là giao thức bảo mật mới nhất cho HTTPS, hỗ trợ các bộ mã hóa mạnh như `TLS_AES_256_GCM_SHA384`. So với TLS 1.2, phiên bản 1.3 giảm số bước handshake từ 2 xuống 1 (1-RTT), tăng tốc độ kết nối đáng kể. Android app dùng Ktor Client để giao tiếp với Ktor Server qua HTTPS ở môi trường production/staging, đảm bảo **toàn bộ dữ liệu** truyền giữa App và Server đều được mã hóa.

**b) Tầng lưu trữ local — AES-256-GCM:**

**AES-256** (Advanced Encryption Standard) là tiêu chuẩn mã hóa dùng để bảo vệ dữ liệu nhạy cảm trên thiết bị. Trong kiến trúc Android hiện tại, token được truy cập qua interface `TokenStore`: giai đoạn dev/MVP có thể dùng `DataStoreTokenStore`, còn bản production nên dùng `SecureTokenStore` dựa trên Android Keystore hoặc giải pháp mã hóa tương đương.

| Tầng | Công nghệ | Dữ liệu được bảo vệ |
|------|----------|---------------------|
| Đường truyền | TLS 1.3 (HTTPS) | Toàn bộ request/response giữa App ↔ Server |
| Lưu trữ local | TokenStore; production dùng SecureTokenStore/Android Keystore | JWT tokens, thông tin người dùng nhạy cảm |
| Media access | Signed URLs (HMAC) | URL truy cập ảnh có thời hạn |

---

## 1.9. Một số thư viện khác

### 1.9.1. Ktor Client (HTTP Client)

**Ktor Client** là thư viện HTTP client đa nền tảng trong hệ sinh thái Kotlin. Vì backend InstaGallery cũng dùng Ktor/Kotlin, Android chọn Ktor Client để đồng bộ tư duy về request/response, serialization, plugin và coroutine.

```kotlin
class FeedApi(
    private val client: HttpClient,
    private val baseUrl: String,
) {
    suspend fun getFeed(page: Int, size: Int): BaseResponse<FeedResponseDto> =
        client.get("$baseUrl/posts/feed") {
            parameter("page", page)
            parameter("size", size)
        }.body()

    suspend fun likePost(postId: Long): BaseResponse<Unit> =
        client.post("$baseUrl/posts/$postId/like").body()
}
```

Ktor Client kết hợp với `ContentNegotiation` và `kotlinx.serialization` để parse JSON, gắn `Authorization: Bearer <token>` qua app-level network layer, xử lý refresh token trong `TokenRefreshHandler`, và hỗ trợ `suspend function` tự nhiên với Kotlin Coroutines.

### 1.9.2. Coil (Image Loading)

**Coil** (Coroutine Image Loader) là thư viện tải ảnh hiện đại, được thiết kế đặc biệt cho **Kotlin**. Với Android XML, Coil có thể load ảnh trực tiếp vào `ImageView`, phù hợp cho feed, profile, portfolio và album của InstaGallery:

```kotlin
// Hiển thị ảnh bài đăng trong ImageView XML
binding.postImage.load(imageUrlRewriter.rewrite(post.imageUrl)) {
    crossfade(true)
    placeholder(R.drawable.placeholder)
    error(R.drawable.error_image)
}
```

Coil hỗ trợ **memory cache** và **disk cache** tự động, kỹ thuật **downsampling** (giảm kích thước ảnh theo view size) để tiết kiệm bộ nhớ, và hỗ trợ định dạng GIF, SVG, video frames. Với emulator, app cần rewrite URL ảnh từ `localhost`/`127.0.0.1` sang `10.0.2.2` trước khi truyền vào Coil.

### 1.9.3. Kotlin Coroutines & Flow

**Kotlin Coroutines** giúp xử lý các tác vụ bất đồng bộ (gọi API, truy vấn database) thông qua `suspend function` — viết code theo phong cách tuần tự nhưng chạy **non-blocking**. Cơ chế **structured concurrency** đảm bảo coroutine tự động hủy khi ViewModel bị destroy, tránh memory leak:

```kotlin
viewModelScope.launch {
    _uiState.value = UiState.Loading
    try {
        val posts = postRepository.getFeed()      // Suspend — non-blocking
        _uiState.value = UiState.Success(posts)
    } catch (e: Exception) {
        _uiState.value = UiState.Error(e.message)
    }
}
```

**Flow** là dạng stream dữ liệu bất đồng bộ, phát ra nhiều giá trị theo thời gian. Trong Android, Flow thường được sử dụng để quản lý **UiState** trong ViewModel thông qua `StateFlow`, và được thu thập trong Fragment theo vòng đời:

```kotlin
// ViewModel
private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
val uiState: StateFlow<UiState> = _uiState.asStateFlow()

// Fragment — collect state theo lifecycle
viewLifecycleOwner.lifecycleScope.launch {
    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.uiState.collect { state ->
            render(state)
        }
    }
}
```

### 1.9.4. Koin (Dependency Injection)

**Koin** là thư viện Dependency Injection nhẹ cho Kotlin, phù hợp với project Android đang ở giai đoạn fresh skeleton. Koin không cần annotation processor phức tạp, dễ đọc và dễ cấu hình theo module:

```kotlin
val networkModule = module {
    single { AppHttpClient.create(tokenStore = get()) }
    single { FeedApi(client = get(), baseUrl = BuildConfig.API_BASE_URL) }
}

val feedModule = module {
    single<FeedRepository> { FeedRepositoryImpl(remoteDataSource = get()) }
    viewModel { FeedViewModel(repository = get()) }
}
```

Koin hỗ trợ singleton, factory và ViewModel DSL, giúp chia dependency theo `core/di` hoặc theo feature khi project lớn hơn.

---

## 1.10. Tổng kết chương

Chương 1 đã trình bày toàn diện nền tảng lý thuyết và công nghệ cho việc xây dựng hệ thống **InstaGallery** — ứng dụng chia sẻ ảnh chuyên biệt dành cho cộng đồng nhiếp ảnh, hướng tới giải quyết các hạn chế của nền tảng hiện có về chất lượng ảnh, bảo mật bản quyền và hỗ trợ đặt lịch chụp.

**Bảng 1.4 — Tổng hợp công nghệ sử dụng trong InstaGallery:**

| Tầng | Công nghệ | Vai trò |
|------|----------|---------|
| **Ngôn ngữ** | Kotlin | Ngôn ngữ chính cho cả Android Client và Backend Server |
| **IDE** | Android Studio, IntelliJ IDEA | Phát triển ứng dụng Android và Backend |
| **UI Framework** | XML Layout, AppCompat, Material Components, ConstraintLayout | Giao diện người dùng Android native, hỗ trợ Dark Mode |
| **Backend** | Ktor Framework | RESTful API + WebSocket Server |
| **Database** | MySQL 8.x (InnoDB) | Cơ sở dữ liệu quan hệ - 30 bảng |
| **Cache** | Redis | Session cache, Rate Limiting, Feed cache |
| **Storage** | Object Storage qua presigned URL | Lưu trữ và phân phối media (ảnh), provider có thể thay đổi phía backend |
| **Bảo mật** | JWT, bcrypt, TLS 1.3, AES-256 | Bảo vệ xác thực, mật khẩu, đường truyền, lưu trữ local |
| **Thư viện** | Ktor Client, kotlinx.serialization, Coil, Coroutines/Flow, Koin, DataStore | HTTP client, JSON, image loading, async/state, DI, session storage |
| **Kiến trúc** | Clean Architecture + MVVM | Phân tách code rõ ràng, dễ bảo trì và kiểm thử |

Các kiến trúc **Clean Architecture kết hợp MVVM** giúp hệ thống có cấu trúc rõ ràng, dễ bảo trì, kiểm thử và mở rộng. Các cơ chế bảo mật như JWT, Signed URLs, Watermark, TLS và AES được áp dụng đa tầng để bảo vệ dữ liệu người dùng và nội dung nhiếp ảnh.

Những cơ sở lý thuyết và công nghệ này tạo nền tảng vững chắc cho việc **phân tích thiết kế** (Chương 2) và **triển khai hệ thống** (Chương 3) trong các chương tiếp theo.
