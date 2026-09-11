USE instagallery;
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- Full demo seed for app-like data.
-- Keep existing data. All generated demo IDs use 1001+.
-- Recommended import from CMD to preserve Vietnamese text:
-- cmd /c "type docs_backend\app_demo_full_seed.sql | docker exec -i instagallery-backend-mysql-1 mysql --default-character-set=utf8mb4 -uig_user -p123456789 instagallery"

SET FOREIGN_KEY_CHECKS = 1;

-- 1) Filters for media editing/demo previews.
INSERT INTO filters (id, name, description, config_json, preview_url, is_active) VALUES
(1001, 'Natural Warm', 'Tông ấm tự nhiên cho ảnh chân dung và lifestyle.', '{"temperature":12,"contrast":4,"saturation":6}', 'https://images.unsplash.com/photo-1496440737103-cd596325d314', 1),
(1002, 'Clean Portrait', 'Làm dịu da, giữ màu sáng và sạch cho ảnh chân dung.', '{"brightness":8,"clarity":-4,"skinSoftness":10}', 'https://images.unsplash.com/photo-1494790108377-be9c29b29330', 1),
(1003, 'Moody Street', 'Tăng chiều sâu cho ảnh đường phố, tối nhẹ vùng shadow.', '{"contrast":14,"shadows":-10,"grain":8}', 'https://images.unsplash.com/photo-1518005020951-eccb494ad742', 1),
(1004, 'Film Wedding', 'Màu phim nhẹ cho ảnh cưới và couple ngoại cảnh.', '{"fade":9,"temperature":6,"highlights":-7}', 'https://images.unsplash.com/photo-1520854221256-17451cc331bf', 1),
(1005, 'Landscape Pop', 'Tăng xanh trời, xanh lá và độ trong cho phong cảnh.', '{"vibrance":14,"dehaze":8,"sharpen":5}', 'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee', 1),
(1006, 'Night Blue', 'Tông xanh lạnh cho ảnh đêm, kiến trúc và thành phố.', '{"temperature":-12,"contrast":10,"highlights":-5}', 'https://images.unsplash.com/photo-1518005020951-eccb494ad742', 1),
(1007, 'Soft Studio', 'Ánh sáng mềm cho ảnh studio, beauty và profile.', '{"brightness":10,"contrast":-2,"saturation":-3}', 'https://images.unsplash.com/photo-1517841905240-472988babdf9', 1),
(1008, 'Travel Sun', 'Màu nắng trong trẻo cho ảnh du lịch và resort.', '{"temperature":10,"vibrance":10,"highlights":-3}', 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e', 1),
(1009, 'Black White Fine', 'Đen trắng tương phản vừa cho ảnh fine art.', '{"monochrome":1,"contrast":16,"grain":6}', 'https://images.unsplash.com/photo-1524504388940-b1c1722653e1', 1),
(1010, 'Minimal Product', 'Tông sạch, ít nhiễu cho ảnh sản phẩm và lookbook.', '{"clarity":8,"saturation":-4,"whiteBalance":3}', 'https://images.unsplash.com/photo-1542291026-7eec264c27ff', 1),
(1011, 'Vintage Cafe', 'Màu hoài niệm cho ảnh quán cà phê và đời sống.', '{"fade":12,"temperature":8,"grain":10}', 'https://images.unsplash.com/photo-1500534314209-a25ddb2bd429', 1),
(1012, 'Cinematic Green', 'Màu điện ảnh hơi xanh cho ảnh outdoor.', '{"tint":-8,"contrast":12,"saturation":4}', 'https://images.unsplash.com/photo-1464822759023-fed622ff2c3b', 1)
ON DUPLICATE KEY UPDATE
    description = VALUES(description),
    config_json = VALUES(config_json),
    preview_url = VALUES(preview_url),
    is_active = VALUES(is_active);

UPDATE post_media
SET filter_id = 1001 + MOD(id, 12)
WHERE id BETWEEN 501 AND 564
  AND filter_id IS NULL;

-- 2) Social graph.
INSERT IGNORE INTO followers (follower_id, following_id, created_at)
WITH clients AS (
    SELECT id FROM users
    WHERE user_type = 'CLIENT' AND id IN (
        1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,
        21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,
        37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,
        53,54,55,56,57,58,59,60,61,62,63,64,65,129,130,
        131,132,133,134,135,136,137,138,139,140
    )
),
photographers AS (
    SELECT id FROM users
    WHERE user_type = 'PHOTOGRAPHER' AND (
        id BETWEEN 66 AND 100 OR id BETWEEN 101 AND 108 OR id BETWEEN 141 AND 148
    )
),
pairs AS (
    SELECT clients.id AS follower_id, photographers.id AS following_id
    FROM clients
    JOIN photographers
    WHERE MOD((clients.id * 13) + photographers.id, 17) IN (0, 1)
)
SELECT
    follower_id,
    following_id,
    DATE_SUB('2026-07-03 12:00:00', INTERVAL MOD((follower_id * 3) + following_id, 90) DAY)
FROM pairs;

INSERT IGNORE INTO followers (follower_id, following_id, created_at) VALUES
(6, 101, '2026-07-01 08:00:00'), (6, 102, '2026-07-01 08:05:00'),
(6, 103, '2026-07-01 08:10:00'), (6, 104, '2026-07-01 08:15:00'),
(6, 105, '2026-07-01 08:20:00'), (6, 106, '2026-07-01 08:25:00'),
(6, 107, '2026-07-01 08:30:00'), (6, 108, '2026-07-01 08:35:00'),
(10, 66, '2026-07-01 09:00:00'), (20, 70, '2026-07-01 09:10:00'),
(30, 80, '2026-07-01 09:20:00'), (40, 90, '2026-07-01 09:30:00'),
(50, 100, '2026-07-01 09:40:00');

INSERT IGNORE INTO follow_requests (id, follower_id, following_id, status, created_at, updated_at) VALUES
(1001, 6, 17, 'PENDING', '2026-07-02 09:00:00', '2026-07-02 09:00:00'),
(1002, 7, 34, 'PENDING', '2026-07-02 09:10:00', '2026-07-02 09:10:00'),
(1003, 8, 51, 'ACCEPTED', '2026-06-20 10:00:00', '2026-06-21 08:00:00'),
(1004, 9, 68, 'REJECTED', '2026-06-18 12:00:00', '2026-06-19 18:00:00'),
(1005, 10, 85, 'PENDING', '2026-07-02 10:05:00', '2026-07-02 10:05:00'),
(1006, 11, 100, 'ACCEPTED', '2026-06-15 15:00:00', '2026-06-16 09:00:00'),
(1007, 12, 129, 'PENDING', '2026-07-02 11:00:00', '2026-07-02 11:00:00'),
(1008, 13, 130, 'REJECTED', '2026-06-28 16:00:00', '2026-06-29 08:00:00'),
(1009, 14, 131, 'PENDING', '2026-07-02 13:00:00', '2026-07-02 13:00:00'),
(1010, 15, 132, 'ACCEPTED', '2026-06-22 09:30:00', '2026-06-22 18:00:00'),
(1011, 16, 133, 'PENDING', '2026-07-01 20:00:00', '2026-07-01 20:00:00'),
(1012, 18, 134, 'PENDING', '2026-07-02 21:00:00', '2026-07-02 21:00:00');

INSERT IGNORE INTO blocked_users (id, blocker_id, blocked_id, reason, created_at) VALUES
(1001, 6, 58, 'Không muốn nhận bình luận quảng cáo.', '2026-06-10 09:00:00'),
(1002, 12, 73, 'Tương tác không phù hợp trong tin nhắn.', '2026-06-11 10:00:00'),
(1003, 22, 89, 'Tài khoản gửi nhiều nội dung spam.', '2026-06-12 11:00:00'),
(1004, 33, 102, 'Ẩn khỏi trang cá nhân demo.', '2026-06-13 12:00:00'),
(1005, 44, 140, 'Tắt tương tác không mong muốn.', '2026-06-14 13:00:00');

INSERT IGNORE INTO muted_users (id, muter_id, muted_id, created_at) VALUES
(1001, 6, 70, '2026-06-21 08:00:00'),
(1002, 10, 80, '2026-06-21 08:10:00'),
(1003, 20, 90, '2026-06-21 08:20:00'),
(1004, 30, 100, '2026-06-21 08:30:00'),
(1005, 40, 101, '2026-06-21 08:40:00'),
(1006, 50, 102, '2026-06-21 08:50:00'),
(1007, 60, 103, '2026-06-21 09:00:00'),
(1008, 129, 104, '2026-06-21 09:10:00');

-- 3) Interactions: likes, comments, replies, comment likes and saves.
INSERT IGNORE INTO likes (id, user_id, post_id, created_at)
WITH demo_users AS (
    SELECT id FROM users WHERE id BETWEEN 1 AND 65 OR id BETWEEN 129 AND 140
),
demo_posts AS (
    SELECT id FROM posts WHERE id BETWEEN 201 AND 264
),
pairs AS (
    SELECT
        ROW_NUMBER() OVER (ORDER BY demo_users.id, demo_posts.id) AS rn,
        demo_users.id AS user_id,
        demo_posts.id AS post_id
    FROM demo_users
    JOIN demo_posts
    WHERE MOD((demo_users.id * 29) + demo_posts.id, 19) IN (0, 1, 2)
)
SELECT
    1000 + rn AS id,
    user_id,
    post_id,
    DATE_SUB('2026-07-03 12:00:00', INTERVAL MOD((user_id * 5) + post_id, 45) HOUR)
FROM pairs
WHERE rn <= 450;

INSERT IGNORE INTO comments (
    id, post_id, user_id, content, parent_comment_id, like_count, reply_count, depth, created_at, updated_at
)
WITH ranked_posts AS (
    SELECT id AS post_id, ROW_NUMBER() OVER (ORDER BY id) AS rn
    FROM posts
    WHERE id BETWEEN 201 AND 264
),
seed_comments AS (
    SELECT
        rn,
        post_id,
        ELT(MOD(rn - 1, 21) + 1, 1,2,3,4,5,6,7,8,9,10,20,30,40,50,60,129,130,131,132,133,134) AS user_id
    FROM ranked_posts
    WHERE rn <= 60
)
SELECT
    1000 + rn AS id,
    post_id,
    user_id,
    ELT(MOD(rn - 1, 10) + 1,
        'Màu ảnh rất đẹp, nhìn có cảm giác tự nhiên.',
        'Concept này hợp để lưu lại cho buổi chụp sắp tới.',
        'Góc máy và ánh sáng đều rất cuốn.',
        'Ảnh này nhìn như một khung hình trong phim.',
        'Địa điểm đẹp quá, cho mình xin thêm thông tin với.',
        'Bố cục sạch và cảm xúc rất thật.',
        'Tone màu này lên feed chắc rất nổi bật.',
        'Mình thích cách bắt khoảnh khắc ở ảnh này.',
        'Có thể dùng concept này cho chụp couple không?',
        'Bài viết rất truyền cảm hứng, lưu lại ngay.'
    ) AS content,
    NULL AS parent_comment_id,
    0 AS like_count,
    0 AS reply_count,
    0 AS depth,
    DATE_SUB('2026-07-03 11:30:00', INTERVAL rn HOUR) AS created_at,
    DATE_SUB('2026-07-03 11:30:00', INTERVAL rn HOUR) AS updated_at
FROM seed_comments;

INSERT IGNORE INTO comments (
    id, post_id, user_id, content, parent_comment_id, like_count, reply_count, depth, created_at, updated_at
)
WITH root_comments AS (
    SELECT id, post_id, ROW_NUMBER() OVER (ORDER BY id) AS rn
    FROM comments
    WHERE id BETWEEN 1001 AND 1060
),
reply_seed AS (
    SELECT
        rn,
        id AS parent_comment_id,
        post_id,
        ELT(MOD(rn + 4, 21) + 1, 1,2,3,4,5,6,7,8,9,10,20,30,40,50,60,129,130,131,132,133,134) AS user_id
    FROM root_comments
    WHERE rn <= 40
)
SELECT
    1100 + rn AS id,
    post_id,
    user_id,
    ELT(MOD(rn - 1, 8) + 1,
        'Đúng rồi, ánh sáng ở đây rất mềm.',
        'Mình cũng đang tìm photographer theo style này.',
        'Concept này chụp buổi sáng chắc sẽ đẹp hơn nữa.',
        'Mình đã lưu lại để tham khảo.',
        'Nếu đi nhóm nhỏ thì góc này rất hợp.',
        'Tone này lên ảnh cưới cũng ổn đó.',
        'Có cảm giác rất gần gũi, không bị dàn dựng.',
        'Mình thích phần hậu kỳ nhẹ như vậy.'
    ) AS content,
    parent_comment_id,
    0 AS like_count,
    0 AS reply_count,
    1 AS depth,
    DATE_SUB('2026-07-03 10:30:00', INTERVAL rn HOUR) AS created_at,
    DATE_SUB('2026-07-03 10:30:00', INTERVAL rn HOUR) AS updated_at
FROM reply_seed;

INSERT IGNORE INTO comment_likes (id, user_id, comment_id, created_at)
WITH demo_users AS (
    SELECT id FROM users WHERE id BETWEEN 1 AND 65
),
demo_comments AS (
    SELECT id FROM comments WHERE id BETWEEN 1001 AND 1140
),
pairs AS (
    SELECT
        ROW_NUMBER() OVER (ORDER BY demo_users.id, demo_comments.id) AS rn,
        demo_users.id AS user_id,
        demo_comments.id AS comment_id
    FROM demo_users
    JOIN demo_comments
    WHERE MOD((demo_users.id * 11) + demo_comments.id, 41) IN (0, 1)
)
SELECT
    1000 + rn AS id,
    user_id,
    comment_id,
    DATE_SUB('2026-07-03 12:00:00', INTERVAL MOD(rn, 72) HOUR)
FROM pairs
WHERE rn <= 220;

INSERT IGNORE INTO saved_posts (id, user_id, post_id, saved_at)
WITH demo_users AS (
    SELECT id FROM users WHERE id BETWEEN 1 AND 65 OR id BETWEEN 129 AND 140
),
demo_posts AS (
    SELECT id FROM posts WHERE id BETWEEN 201 AND 264
),
pairs AS (
    SELECT
        ROW_NUMBER() OVER (ORDER BY demo_users.id, demo_posts.id) AS rn,
        demo_users.id AS user_id,
        demo_posts.id AS post_id
    FROM demo_users
    JOIN demo_posts
    WHERE MOD((demo_users.id * 17) + demo_posts.id, 23) IN (0, 1)
)
SELECT
    1000 + rn AS id,
    user_id,
    post_id,
    DATE_SUB('2026-07-03 12:00:00', INTERVAL MOD(rn, 80) HOUR)
FROM pairs
WHERE rn <= 260;

-- 4) Albums and album contents.
INSERT IGNORE INTO albums (
    id, user_id, title, description, cover_image_url, is_private, created_at, updated_at
)
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 24
)
SELECT
    1000 + n AS id,
    ELT(MOD(n - 1, 24) + 1, 6,10,20,30,40,50,60,66,70,80,90,100,101,102,103,104,105,106,107,108,129,130,141,142) AS user_id,
    ELT(MOD(n - 1, 8) + 1,
        'Ý tưởng chụp chân dung',
        'Concept cưới ngoài trời',
        'Phong cảnh muốn ghé',
        'Ảnh đường phố yêu thích',
        'Lookbook và sản phẩm',
        'Gia đình và lifestyle',
        'Du lịch mùa hè',
        'Ảnh đêm thành phố'
    ) AS title,
    'Album demo gom những bài viết đẹp để người dùng có dữ liệu xem, lưu và quản lý bộ sưu tập.' AS description,
    ELT(MOD(n - 1, 12) + 1,
        'https://images.unsplash.com/photo-1496440737103-cd596325d314',
        'https://images.unsplash.com/photo-1520854221256-17451cc331bf',
        'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee',
        'https://images.unsplash.com/photo-1518005020951-eccb494ad742',
        'https://images.unsplash.com/photo-1542291026-7eec264c27ff',
        'https://images.unsplash.com/photo-1438761681033-6461ffad8d80',
        'https://images.unsplash.com/photo-1507525428034-b723cf961d3e',
        'https://images.unsplash.com/photo-1464822759023-fed622ff2c3b',
        'https://images.unsplash.com/photo-1502602898657-3e91760cbb34',
        'https://images.unsplash.com/photo-1494526585095-c41746248156',
        'https://images.unsplash.com/photo-1519741497674-611481863552',
        'https://images.unsplash.com/photo-1500534314209-a25ddb2bd429'
    ) AS cover_image_url,
    CASE WHEN MOD(n, 7) = 0 THEN 1 ELSE 0 END AS is_private,
    DATE_SUB('2026-07-03 12:00:00', INTERVAL n DAY) AS created_at,
    DATE_SUB('2026-07-03 12:00:00', INTERVAL n DAY) AS updated_at
FROM seq;

INSERT IGNORE INTO album_media (id, album_id, post_id, added_at)
WITH album_rows AS (
    SELECT id FROM albums WHERE id BETWEEN 1001 AND 1024
),
post_rows AS (
    SELECT id FROM posts WHERE id BETWEEN 201 AND 264
),
pairs AS (
    SELECT
        ROW_NUMBER() OVER (ORDER BY album_rows.id, post_rows.id) AS rn,
        album_rows.id AS album_id,
        post_rows.id AS post_id
    FROM album_rows
    JOIN post_rows
    WHERE MOD((album_rows.id * 7) + post_rows.id, 17) IN (0, 1)
)
SELECT
    1000 + rn AS id,
    album_id,
    post_id,
    DATE_SUB('2026-07-03 12:00:00', INTERVAL MOD(rn, 120) HOUR)
FROM pairs
WHERE rn <= 120;

-- 5) Photographer availability, bookings and ratings.
INSERT IGNORE INTO availability_schedules (
    id, portfolio_id, type, day_of_week, specific_date, start_time, end_time, is_booked, created_at, updated_at
)
WITH portfolios_scope AS (
    SELECT id AS portfolio_id
    FROM portfolios
    WHERE user_id BETWEEN 66 AND 148
),
slots AS (
    SELECT 1 AS slot_no UNION ALL
    SELECT 2 UNION ALL
    SELECT 3 UNION ALL
    SELECT 4
),
seed AS (
    SELECT
        ROW_NUMBER() OVER (ORDER BY portfolios_scope.portfolio_id, slots.slot_no) AS rn,
        portfolios_scope.portfolio_id,
        slots.slot_no
    FROM portfolios_scope
    JOIN slots
)
SELECT
    1000 + rn AS id,
    portfolio_id,
    CASE WHEN slot_no = 4 THEN 'SPECIFIC_DATE' ELSE 'RECURRING' END AS type,
    CASE slot_no
        WHEN 1 THEN 'MONDAY'
        WHEN 2 THEN 'WEDNESDAY'
        WHEN 3 THEN 'SATURDAY'
        ELSE NULL
    END AS day_of_week,
    CASE WHEN slot_no = 4 THEN DATE_ADD('2026-07-10', INTERVAL MOD(portfolio_id, 28) DAY) ELSE NULL END AS specific_date,
    CASE slot_no
        WHEN 1 THEN '08:00'
        WHEN 2 THEN '13:30'
        WHEN 3 THEN '15:00'
        ELSE '09:00'
    END AS start_time,
    CASE slot_no
        WHEN 1 THEN '11:30'
        WHEN 2 THEN '17:00'
        WHEN 3 THEN '18:30'
        ELSE '12:00'
    END AS end_time,
    CASE WHEN slot_no = 4 AND MOD(portfolio_id, 5) = 0 THEN 1 ELSE 0 END AS is_booked,
    '2026-07-03 08:00:00' AS created_at,
    '2026-07-03 08:00:00' AS updated_at
FROM seed;

INSERT IGNORE INTO bookings (
    id, client_id, photographer_id, booking_date, duration_hours, location_booking,
    details, price, currency, status, cancellation_reason, created_at, updated_at
)
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 64
),
booking_seed AS (
    SELECT
        n,
        ELT(MOD(n - 1, 25) + 1, 1,2,3,4,5,6,7,8,9,10,20,30,40,50,60,129,130,131,132,133,134,135,136,137,138) AS client_id,
        ELT(MOD(n - 1, 24) + 1, 66,67,68,69,70,75,80,85,90,95,100,101,102,103,104,105,106,107,108,141,142,143,144,145) AS photographer_id,
        ELT(MOD(n - 1, 5) + 1, 'PENDING', 'CONFIRMED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED') AS status
    FROM seq
)
SELECT
    1000 + n AS id,
    client_id,
    photographer_id,
    CASE
        WHEN status IN ('COMPLETED', 'CANCELLED') THEN DATE_ADD('2026-05-20 08:00:00', INTERVAL n DAY)
        ELSE DATE_ADD('2026-07-10 08:00:00', INTERVAL n DAY)
    END AS booking_date,
    CAST(2.0 + (MOD(n, 5) * 0.5) AS DECIMAL(4,1)) AS duration_hours,
    ELT(MOD(n - 1, 12) + 1,
        'Hồ Tây, Hà Nội', 'Bãi biển Mỹ Khê, Đà Nẵng', 'Quận 1, TP. Hồ Chí Minh',
        'Đại Nội Huế', 'Đồi chè Cầu Đất, Đà Lạt', 'Bãi Dài, Nha Trang',
        'Phố cổ Hội An', 'Bản Cát Cát, Sa Pa', 'Bến Ninh Kiều, Cần Thơ',
        'Sunset Town, Phú Quốc', 'Tràng An, Ninh Bình', 'Nhà hát lớn Hải Phòng'
    ) AS location_booking,
    ELT(MOD(n - 1, 8) + 1,
        'Chụp chân dung cá nhân, ưu tiên ánh sáng tự nhiên và màu ảnh nhẹ.',
        'Chụp couple ngoại cảnh, cần tư vấn trang phục và địa điểm.',
        'Chụp ảnh cưới nhỏ, phong cách tự nhiên, ít tạo dáng cứng.',
        'Chụp lookbook sản phẩm mới, cần ảnh vuông và dọc cho mạng xã hội.',
        'Chụp sự kiện gia đình, cần bắt khoảnh khắc và ảnh nhóm.',
        'Chụp du lịch trong ngày, ưu tiên các góc có nắng sớm.',
        'Chụp profile công việc, cần hậu kỳ sạch và chuyên nghiệp.',
        'Chụp kỷ yếu nhóm nhỏ, cần concept vui và nhiều ảnh candid.'
    ) AS details,
    900000.00 + (MOD(n, 9) * 250000.00) AS price,
    'VND' AS currency,
    status,
    CASE WHEN status = 'CANCELLED' THEN 'Khách đổi lịch cá nhân, sẽ đặt lại vào tuần sau.' ELSE NULL END AS cancellation_reason,
    DATE_SUB('2026-07-03 08:00:00', INTERVAL n DAY) AS created_at,
    DATE_SUB('2026-07-03 08:00:00', INTERVAL MOD(n, 20) DAY) AS updated_at
FROM booking_seed;

INSERT IGNORE INTO ratings (id, booking_id, rater_id, ratee_id, rating_value, comment, created_at)
WITH completed_bookings AS (
    SELECT
        id AS booking_id,
        client_id,
        photographer_id,
        ROW_NUMBER() OVER (ORDER BY id) AS rn
    FROM bookings
    WHERE id BETWEEN 1001 AND 1064
      AND status IN ('COMPLETED', 'CANCELLED')
)
SELECT
    1000 + rn AS id,
    booking_id,
    client_id AS rater_id,
    photographer_id AS ratee_id,
    ELT(MOD(rn - 1, 5) + 1, 5, 5, 4, 5, 4) AS rating_value,
    ELT(MOD(rn - 1, 8) + 1,
        'Nhiếp ảnh gia đúng giờ, tư vấn concept kỹ và giao ảnh rất ổn.',
        'Ảnh đẹp, màu tự nhiên, cả nhóm đều hài lòng.',
        'Buổi chụp thoải mái, biết cách hướng dẫn tạo dáng.',
        'Giao tiếp dễ chịu, hậu kỳ sạch, sẽ đặt tiếp.',
        'Chất lượng tốt so với giá, địa điểm được tư vấn hợp lý.',
        'Ảnh nhiều khoảnh khắc tự nhiên, không bị gượng.',
        'Làm việc chuyên nghiệp, gửi ảnh preview rất nhanh.',
        'Concept phù hợp yêu cầu, màu ảnh nhẹ và sang.'
    ) AS comment,
    DATE_ADD('2026-06-01 12:00:00', INTERVAL rn DAY) AS created_at
FROM completed_bookings
WHERE rn <= 30;

-- 6) Chat conversations and messages.
INSERT IGNORE INTO conversations (id, title, type, created_at, updated_at)
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 20
)
SELECT
    1000 + n AS id,
    CASE WHEN n <= 14 THEN NULL ELSE CONCAT('Nhóm chụp demo ', LPAD(n - 14, 2, '0')) END AS title,
    CASE WHEN n <= 14 THEN 'DIRECT' ELSE 'GROUP' END AS type,
    DATE_SUB('2026-07-03 10:00:00', INTERVAL n DAY) AS created_at,
    DATE_SUB('2026-07-03 12:00:00', INTERVAL MOD(n, 48) HOUR) AS updated_at
FROM seq;

INSERT IGNORE INTO conversation_members (
    conversation_id, user_id, role, nickname, is_muted, joined_at, last_read_at
)
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 20
),
conv_seed AS (
    SELECT
        1000 + n AS conversation_id,
        n,
        ELT(MOD(n - 1, 19) + 1, 1,2,3,4,5,6,7,8,9,10,20,30,40,50,60,129,130,131,132) AS client_id,
        ELT(MOD(n - 1, 14) + 1, 66,70,75,80,85,90,95,100,101,102,103,104,105,106) AS photographer_id
    FROM seq
)
SELECT conversation_id, client_id, 'MEMBER', NULL, 0, '2026-07-01 08:00:00', '2026-07-03 08:00:00'
FROM conv_seed
UNION ALL
SELECT conversation_id, photographer_id, CASE WHEN n > 14 THEN 'ADMIN' ELSE 'MEMBER' END, NULL, 0, '2026-07-01 08:00:00', '2026-07-03 08:00:00'
FROM conv_seed
UNION ALL
SELECT conversation_id, 133 + MOD(n, 8), 'MEMBER', NULL, CASE WHEN MOD(n, 5) = 0 THEN 1 ELSE 0 END, '2026-07-01 09:00:00', NULL
FROM conv_seed
WHERE n > 14
UNION ALL
SELECT conversation_id, 141 + MOD(n, 6), 'MEMBER', NULL, 0, '2026-07-01 09:30:00', NULL
FROM conv_seed
WHERE n > 14;

INSERT IGNORE INTO messages (
    id, conversation_id, sender_id, content, message_type, media_url, reply_to_id, is_deleted, created_at
)
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 120
),
conv_seed AS (
    SELECT
        1000 + conv_no AS conversation_id,
        conv_no,
        ELT(MOD(conv_no - 1, 19) + 1, 1,2,3,4,5,6,7,8,9,10,20,30,40,50,60,129,130,131,132) AS client_id,
        ELT(MOD(conv_no - 1, 14) + 1, 66,70,75,80,85,90,95,100,101,102,103,104,105,106) AS photographer_id
    FROM (
        SELECT 1 AS conv_no UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5
        UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL SELECT 10
        UNION ALL SELECT 11 UNION ALL SELECT 12 UNION ALL SELECT 13 UNION ALL SELECT 14 UNION ALL SELECT 15
        UNION ALL SELECT 16 UNION ALL SELECT 17 UNION ALL SELECT 18 UNION ALL SELECT 19 UNION ALL SELECT 20
    ) c
),
message_seed AS (
    SELECT
        seq.n,
        conv_seed.conversation_id,
        CASE WHEN MOD(seq.n, 2) = 0 THEN conv_seed.client_id ELSE conv_seed.photographer_id END AS sender_id
    FROM seq
    JOIN conv_seed ON conv_seed.conv_no = MOD(seq.n - 1, 20) + 1
)
SELECT
    1000 + n AS id,
    conversation_id,
    sender_id,
    ELT(MOD(n - 1, 12) + 1,
        'Chào bạn, mình muốn hỏi thêm về gói chụp cuối tuần này.',
        'Mình còn lịch trống vào buổi sáng, bạn muốn chụp khoảng mấy giờ?',
        'Concept mình thích là tự nhiên, nhẹ nhàng, không cần tạo dáng quá nhiều.',
        'Được nhé, mình sẽ gửi moodboard và bảng giá chi tiết.',
        'Địa điểm này có cần xin phép trước không bạn?',
        'Mình đã từng chụp ở đó, ánh sáng đẹp nhất là khoảng 7 giờ sáng.',
        'Bạn cho mình xem vài ảnh tham khảo tone màu được không?',
        'Mình gửi trước vài ảnh, bạn xem có hợp style không nhé.',
        'Gói này bao gồm chỉnh màu toàn bộ ảnh và retouch 10 ảnh chọn lọc.',
        'Mình đặt lịch giữ slot trước nhé, nếu đổi giờ mình báo sớm.',
        'Cảm ơn bạn, mình sẽ chuẩn bị trang phục theo gợi ý.',
        'Hẹn bạn đúng giờ, mình sẽ đến sớm để khảo sát ánh sáng.'
    ) AS content,
    'TEXT' AS message_type,
    NULL AS media_url,
    NULL AS reply_to_id,
    0 AS is_deleted,
    DATE_SUB('2026-07-03 12:00:00', INTERVAL (120 - n) MINUTE) AS created_at
FROM message_seed;

-- 7) Notifications.
INSERT IGNORE INTO notifications (
    id, user_id, sender_id, type, target_type, target_id, title, body, is_read, created_at
)
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 90
),
seed AS (
    SELECT
        n,
        ELT(MOD(n - 1, 25) + 1, 1,2,3,4,5,6,7,8,9,10,20,30,40,50,60,66,70,80,90,100,129,130,131,132,133) AS user_id,
        ELT(MOD(n + 3, 20) + 1, 101,102,103,104,105,106,107,108,66,70,80,90,100,141,142,143,6,10,20,30) AS sender_id,
        ELT(MOD(n - 1, 9) + 1, 'NEW_LIKE', 'NEW_COMMENT', 'NEW_FOLLOWER', 'BOOKING_REQUEST', 'BOOKING_CONFIRMED', 'BOOKING_COMPLETED', 'NEW_MESSAGE', 'MENTION', 'SYSTEM') AS notification_type
    FROM seq
)
SELECT
    1000 + n AS id,
    user_id,
    CASE WHEN notification_type = 'SYSTEM' THEN NULL ELSE sender_id END AS sender_id,
    notification_type AS type,
    CASE
        WHEN notification_type IN ('NEW_LIKE', 'NEW_COMMENT', 'MENTION') THEN 'POST'
        WHEN notification_type = 'NEW_FOLLOWER' THEN 'USER'
        WHEN notification_type IN ('BOOKING_REQUEST', 'BOOKING_CONFIRMED', 'BOOKING_COMPLETED') THEN 'BOOKING'
        WHEN notification_type = 'NEW_MESSAGE' THEN 'CONVERSATION'
        ELSE NULL
    END AS target_type,
    CASE
        WHEN notification_type IN ('NEW_LIKE', 'NEW_COMMENT', 'MENTION') THEN 201 + MOD(n, 64)
        WHEN notification_type = 'NEW_FOLLOWER' THEN sender_id
        WHEN notification_type IN ('BOOKING_REQUEST', 'BOOKING_CONFIRMED', 'BOOKING_COMPLETED') THEN 1001 + MOD(n, 64)
        WHEN notification_type = 'NEW_MESSAGE' THEN 1001 + MOD(n, 20)
        ELSE NULL
    END AS target_id,
    ELT(MOD(n - 1, 9) + 1,
        'Lượt thích mới',
        'Bình luận mới',
        'Người theo dõi mới',
        'Yêu cầu đặt lịch',
        'Lịch chụp đã xác nhận',
        'Buổi chụp đã hoàn tất',
        'Tin nhắn mới',
        'Bạn được nhắc đến',
        'Cập nhật hệ thống'
    ) AS title,
    ELT(MOD(n - 1, 9) + 1,
        'Có người vừa thích bài viết của bạn.',
        'Bài viết của bạn có bình luận mới.',
        'Một người dùng mới vừa bắt đầu theo dõi bạn.',
        'Bạn có một yêu cầu đặt lịch chụp mới.',
        'Lịch chụp của bạn đã được xác nhận.',
        'Buổi chụp đã hoàn tất, bạn có thể gửi đánh giá.',
        'Bạn vừa nhận được tin nhắn mới.',
        'Có người nhắc đến bạn trong một bài viết.',
        'InstaGallery đã cập nhật dữ liệu demo mới.'
    ) AS body,
    CASE WHEN MOD(n, 4) = 0 THEN 1 ELSE 0 END AS is_read,
    DATE_SUB('2026-07-03 12:00:00', INTERVAL n HOUR) AS created_at
FROM seed;

-- 8) Moderation/system demo data.
INSERT INTO banned_words (id, word_or_regex, is_regex, added_by_admin_id, created_at) VALUES
(1001, 'spam', 0, NULL, '2026-06-01 08:00:00'),
(1002, 'scam', 0, NULL, '2026-06-01 08:05:00'),
(1003, 'lừa đảo', 0, NULL, '2026-06-01 08:10:00'),
(1004, 'link kiếm tiền', 0, NULL, '2026-06-01 08:15:00'),
(1005, 'mua follow', 0, NULL, '2026-06-01 08:20:00'),
(1006, 'free\\s+money', 1, NULL, '2026-06-01 08:25:00'),
(1007, 'click\\s+ngay', 1, NULL, '2026-06-01 08:30:00'),
(1008, 'fake booking', 0, NULL, '2026-06-01 08:35:00')
ON DUPLICATE KEY UPDATE
    is_regex = VALUES(is_regex),
    added_by_admin_id = VALUES(added_by_admin_id);

INSERT IGNORE INTO reports (
    id, reporter_id, target_type, target_id, reason, admin_note, reviewed_by, status, created_at, updated_at
)
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 20
)
SELECT
    1000 + n AS id,
    ELT(MOD(n - 1, 17) + 1, 1,2,3,4,5,6,7,8,9,10,20,30,40,50,60,129,130) AS reporter_id,
    ELT(MOD(n - 1, 5) + 1, 'POST', 'COMMENT', 'USER', 'BOOKING', 'MESSAGE') AS target_type,
    CASE MOD(n - 1, 5)
        WHEN 0 THEN 201 + MOD(n, 64)
        WHEN 1 THEN 1001 + MOD(n, 80)
        WHEN 2 THEN 66 + MOD(n, 35)
        WHEN 3 THEN 1001 + MOD(n, 64)
        ELSE 1001 + MOD(n, 120)
    END AS target_id,
    ELT(MOD(n - 1, 6) + 1,
        'Nội dung có dấu hiệu quảng cáo quá mức.',
        'Bình luận không liên quan đến bài viết.',
        'Tài khoản gửi nhiều tin nhắn giống nhau.',
        'Thông tin đặt lịch cần được kiểm tra lại.',
        'Tin nhắn có nội dung nghi ngờ spam.',
        'Người dùng báo cáo để đội ngũ kiểm duyệt xem xét.'
    ) AS reason,
    CASE WHEN MOD(n, 3) = 0 THEN 'Đã kiểm tra demo, chưa cần xử lý thêm.' ELSE NULL END AS admin_note,
    CASE WHEN MOD(n, 3) = 0 THEN 1 ELSE NULL END AS reviewed_by,
    ELT(MOD(n - 1, 4) + 1, 'PENDING', 'REVIEWING', 'RESOLVED', 'DISMISSED') AS status,
    DATE_SUB('2026-07-03 12:00:00', INTERVAL n DAY) AS created_at,
    DATE_SUB('2026-07-03 12:00:00', INTERVAL MOD(n, 8) DAY) AS updated_at
FROM seq;

INSERT IGNORE INTO password_reset_tokens (id, user_id, token, expired_at, created_at) VALUES
(1001, 6, 'demo-reset-token-userdemo06', '2026-07-04 08:00:00', '2026-07-03 08:00:00'),
(1002, 10, 'demo-reset-token-userdemo10', '2026-07-04 08:10:00', '2026-07-03 08:10:00'),
(1003, 20, 'demo-reset-token-userdemo20', '2026-07-04 08:20:00', '2026-07-03 08:20:00'),
(1004, 30, 'demo-reset-token-userdemo30', '2026-07-04 08:30:00', '2026-07-03 08:30:00'),
(1005, 66, 'demo-reset-token-photodemo66', '2026-07-04 08:40:00', '2026-07-03 08:40:00'),
(1006, 70, 'demo-reset-token-photodemo70', '2026-07-04 08:50:00', '2026-07-03 08:50:00'),
(1007, 101, 'demo-reset-token-photo-tranquang', '2026-07-04 09:00:00', '2026-07-03 09:00:00'),
(1008, 141, 'demo-reset-token-photohung', '2026-07-04 09:10:00', '2026-07-03 09:10:00');

INSERT IGNORE INTO activity_logs (
    id, user_id, action, target_type, target_id, ip_address, user_agent, metadata, created_at
)
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 80
)
SELECT
    1000 + n AS id,
    ELT(MOD(n - 1, 25) + 1, 1,2,3,4,5,6,7,8,9,10,20,30,40,50,60,66,70,80,90,100,129,130,141,142,143) AS user_id,
    ELT(MOD(n - 1, 8) + 1, 'LOGIN', 'VIEW_POST', 'LIKE_POST', 'SAVE_POST', 'CREATE_BOOKING', 'SEND_MESSAGE', 'MARK_NOTIFICATION_READ', 'UPDATE_PROFILE') AS action,
    ELT(MOD(n - 1, 6) + 1, 'SESSION', 'POST', 'POST', 'POST', 'BOOKING', 'MEDIA') AS target_type,
    CASE MOD(n - 1, 6)
        WHEN 0 THEN NULL
        WHEN 1 THEN 201 + MOD(n, 64)
        WHEN 2 THEN 201 + MOD(n, 64)
        WHEN 3 THEN 201 + MOD(n, 64)
        WHEN 4 THEN 1001 + MOD(n, 64)
        ELSE 501 + MOD(n, 64)
    END AS target_id,
    CONCAT('192.168.1.', 20 + MOD(n, 80)) AS ip_address,
    'InstaGallery Android Demo/1.0' AS user_agent,
    CONCAT('{"source":"demo_seed","sequence":', n, '}') AS metadata,
    DATE_SUB('2026-07-03 12:00:00', INTERVAL n HOUR) AS created_at
FROM seq;

-- 9) Counter refresh with GREATEST so existing large demo counters are not reduced.
UPDATE posts p
LEFT JOIN (
    SELECT post_id, COUNT(*) AS total_likes
    FROM likes
    GROUP BY post_id
) lc ON lc.post_id = p.id
LEFT JOIN (
    SELECT post_id, COUNT(*) AS total_comments
    FROM comments
    WHERE deleted_at IS NULL
    GROUP BY post_id
) cc ON cc.post_id = p.id
SET
    p.like_count = GREATEST(p.like_count, COALESCE(lc.total_likes, 0)),
    p.comment_count = GREATEST(p.comment_count, COALESCE(cc.total_comments, 0)),
    p.share_count = GREATEST(p.share_count, 10 + MOD(p.id, 90))
WHERE p.id BETWEEN 201 AND 264;

UPDATE comments c
LEFT JOIN (
    SELECT comment_id, COUNT(*) AS total_likes
    FROM comment_likes
    GROUP BY comment_id
) cl ON cl.comment_id = c.id
LEFT JOIN (
    SELECT parent_comment_id, COUNT(*) AS total_replies
    FROM comments
    WHERE parent_comment_id IS NOT NULL
      AND deleted_at IS NULL
    GROUP BY parent_comment_id
) replies ON replies.parent_comment_id = c.id
SET
    c.like_count = GREATEST(c.like_count, COALESCE(cl.total_likes, 0)),
    c.reply_count = GREATEST(c.reply_count, COALESCE(replies.total_replies, 0))
WHERE c.id BETWEEN 1001 AND 1140;

UPDATE users u
LEFT JOIN (
    SELECT following_id AS user_id, COUNT(*) AS total_followers
    FROM followers
    GROUP BY following_id
) fc ON fc.user_id = u.id
LEFT JOIN (
    SELECT follower_id AS user_id, COUNT(*) AS total_following
    FROM followers
    GROUP BY follower_id
) fgc ON fgc.user_id = u.id
LEFT JOIN (
    SELECT user_id, COUNT(*) AS total_posts
    FROM posts
    WHERE deleted_at IS NULL
    GROUP BY user_id
) pc ON pc.user_id = u.id
SET
    u.follower_count = GREATEST(u.follower_count, COALESCE(fc.total_followers, 0)),
    u.following_count = GREATEST(u.following_count, COALESCE(fgc.total_following, 0)),
    u.post_count = GREATEST(u.post_count, COALESCE(pc.total_posts, 0))
WHERE u.id BETWEEN 1 AND 148;

UPDATE portfolios p
LEFT JOIN (
    SELECT ratee_id AS user_id, AVG(rating_value) AS avg_rating, COUNT(*) AS total_reviews
    FROM ratings
    GROUP BY ratee_id
) r ON r.user_id = p.user_id
SET
    p.rating_avg = CASE
        WHEN r.avg_rating IS NULL THEN p.rating_avg
        ELSE GREATEST(p.rating_avg, CAST(r.avg_rating AS DECIMAL(3,2)))
    END,
    p.review_count = GREATEST(p.review_count, COALESCE(r.total_reviews, 0))
WHERE p.user_id BETWEEN 66 AND 148;

UPDATE media_tags t
LEFT JOIN (
    SELECT tag_id, COUNT(*) AS total_usage
    FROM post_media_tags
    GROUP BY tag_id
) usage_counts ON usage_counts.tag_id = t.id
SET t.usage_count = GREATEST(t.usage_count, COALESCE(usage_counts.total_usage, 0));
