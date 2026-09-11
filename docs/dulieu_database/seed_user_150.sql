USE instagallery;
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 1) Tạo 15 bài viết cho user 150 (bocbatho1909)
INSERT INTO posts (
    id, user_id, caption, location, visibility, comment_visibility,
    like_count, comment_count, share_count, created_at, updated_at
) VALUES
(1201, 150, 'Hoàng hôn buông xuống trên Hồ Tây, Hà Nội. Ánh nắng rực rỡ cuối ngày lấp lánh trên mặt nước. #hanoi #taylake #sunset #photography', 'Hồ Tây, Hà Nội', 'PUBLIC', 'ALLOW_ALL', 88, 0, 12, CURRENT_TIMESTAMP - INTERVAL 2 HOUR, CURRENT_TIMESTAMP - INTERVAL 2 HOUR),
(1202, 150, 'Một buổi sáng sớm bình yên tại Sapa. Sương mù giăng kín lối đi và những ruộng bậc thang xanh ngút ngàn. #sapa #misty #landscape #vietnam', 'Sa Pa, Lào Cai', 'PUBLIC', 'ALLOW_ALL', 124, 0, 15, CURRENT_TIMESTAMP - INTERVAL 5 HOUR, CURRENT_TIMESTAMP - INTERVAL 5 HOUR),
(1203, 150, 'Góc nhỏ quán cà phê quen thuộc tại Đà Lạt. Không khí se lạnh cùng ly cà phê nóng ấm lòng. #dalat #cafe #vintage #chill', 'Đà Lạt, Lâm Đồng', 'PUBLIC', 'ALLOW_ALL', 65, 0, 8, CURRENT_TIMESTAMP - INTERVAL 9 HOUR, CURRENT_TIMESTAMP - INTERVAL 9 HOUR),
(1204, 150, 'Bình minh trên biển Mỹ Khê, Đà Nẵng. Những vạt nắng đầu tiên chiếu rọi bãi cát trắng mịn. #danang #beach #sunrise #landscape', 'Mỹ Khê, Đà Nẵng', 'PUBLIC', 'ALLOW_ALL', 142, 0, 18, CURRENT_TIMESTAMP - INTERVAL 12 HOUR, CURRENT_TIMESTAMP - INTERVAL 12 HOUR),
(1205, 150, 'Góc phố cổ Hội An rực rỡ sắc màu đèn lồng về đêm. Vẻ đẹp hoài cổ đắm say lòng người. #hoian #lanterns #night #heritage', 'Phố cổ Hội An', 'PUBLIC', 'ALLOW_ALL', 110, 0, 14, CURRENT_TIMESTAMP - INTERVAL 18 HOUR, CURRENT_TIMESTAMP - INTERVAL 18 HOUR),
(1206, 150, 'Street photography tại chợ Đồng Xuân. Những nhịp sống hối hả chân thực của người Tràng An. #streetphotography #hanoi #people #life', 'Chợ Đồng Xuân, Hà Nội', 'PUBLIC', 'ALLOW_ALL', 76, 0, 6, CURRENT_TIMESTAMP - INTERVAL 1 DAY, CURRENT_TIMESTAMP - INTERVAL 1 DAY),
(1207, 150, 'Buổi chụp chân dung nghệ thuật ngoài trời với ánh sáng tự nhiên nhẹ nhàng. #portrait #beauty #outdoor #fashion', 'Công viên Thống Nhất, Hà Nội', 'PUBLIC', 'ALLOW_ALL', 95, 0, 10, CURRENT_TIMESTAMP - INTERVAL 2 DAY, CURRENT_TIMESTAMP - INTERVAL 2 DAY),
(1208, 150, 'Kiến trúc hiện đại của cầu Rồng Đà Nẵng lúc lên đèn. Một biểu tượng đầy kiêu hãnh của thành phố. #danang #dragonbridge #architecture #citylight', 'Cầu Rồng, Đà Nẵng', 'PUBLIC', 'ALLOW_ALL', 153, 0, 22, CURRENT_TIMESTAMP - INTERVAL 3 DAY, CURRENT_TIMESTAMP - INTERVAL 3 DAY),
(1209, 150, 'Vẻ đẹp tĩnh lặng của Tràng An Ninh Bình giữa mây trời non nước. #ninhbinh #trangan #travelvietnam #nature', 'Tràng An, Ninh Bình', 'PUBLIC', 'ALLOW_ALL', 188, 0, 31, CURRENT_TIMESTAMP - INTERVAL 4 DAY, CURRENT_TIMESTAMP - INTERVAL 4 DAY),
(1210, 150, 'Chân dung đen trắng biểu đạt chiều sâu cảm xúc qua đôi mắt. #blackandwhite #portrait #fineart #bnw', 'Studio, Hà Nội', 'PUBLIC', 'ALLOW_ALL', 59, 0, 4, CURRENT_TIMESTAMP - INTERVAL 5 DAY, CURRENT_TIMESTAMP - INTERVAL 5 DAY),
(1211, 150, 'Vệt nắng xiên qua ô cửa sổ nhỏ trong một chiều mùa thu Hà Nội nhẹ nhàng. #autumn #hanoi #lightandshadow #minimalism', 'Phố Phan Đình Phùng, Hà Nội', 'PUBLIC', 'ALLOW_ALL', 82, 0, 7, CURRENT_TIMESTAMP - INTERVAL 6 DAY, CURRENT_TIMESTAMP - INTERVAL 6 DAY),
(1212, 150, 'Ảnh chụp couple kỷ niệm tình yêu ngập tràn hạnh phúc tại vườn hoa cẩm tú cầu. #couple #love #dalat #wedding', 'Đà Lạt, Lâm Đồng', 'PUBLIC', 'ALLOW_ALL', 119, 0, 13, CURRENT_TIMESTAMP - INTERVAL 7 DAY, CURRENT_TIMESTAMP - INTERVAL 7 DAY),
(1213, 150, 'Vẻ đẹp hoang sơ của đồi chè Cầu Đất chìm trong sương sớm. #dalat #caudat #teahills #morning', 'Đồi chè Cầu Đất, Đà Lạt', 'PUBLIC', 'ALLOW_ALL', 130, 0, 11, CURRENT_TIMESTAMP - INTERVAL 8 DAY, CURRENT_TIMESTAMP - INTERVAL 8 DAY),
(1214, 150, 'Nhịp sống đêm nhộn nhịp tại phố đi bộ Tạ Hiện. Điểm hẹn giao lưu văn hóa đầy sắc màu. #hanoi #tahien #nightlife #street', 'Tạ Hiện, Hà Nội', 'PUBLIC', 'ALLOW_ALL', 79, 0, 5, CURRENT_TIMESTAMP - INTERVAL 9 DAY, CURRENT_TIMESTAMP - INTERVAL 9 DAY),
(1215, 150, 'Khám phá bản sắc văn hóa dân tộc tại bản Cát Cát, Sa Pa. Những nụ cười hồn nhiên của trẻ em vùng cao. #sapa #catcat #culture #portrait', 'Bản Cát Cát, Sa Pa', 'PUBLIC', 'ALLOW_ALL', 104, 0, 9, CURRENT_TIMESTAMP - INTERVAL 10 DAY, CURRENT_TIMESTAMP - INTERVAL 10 DAY)
ON DUPLICATE KEY UPDATE
    user_id = VALUES(user_id),
    caption = VALUES(caption),
    location = VALUES(location),
    visibility = VALUES(visibility),
    comment_visibility = VALUES(comment_visibility),
    like_count = VALUES(like_count),
    share_count = VALUES(share_count),
    created_at = VALUES(created_at);

-- 2) Tạo media cho 15 bài viết này
INSERT INTO post_media (
    id, post_id, media_file_url, thumbnail_url, media_type, position, width, height
) VALUES
(2201, 1201, 'https://picsum.photos/seed/instagallery-post-1201/1200/800', 'https://picsum.photos/seed/instagallery-post-1201-thumb/600/400', 'IMAGE', 1, 1200, 800),
(2202, 1202, 'https://picsum.photos/seed/instagallery-post-1202/1200/800', 'https://picsum.photos/seed/instagallery-post-1202-thumb/600/400', 'IMAGE', 1, 1200, 800),
(2203, 1203, 'https://picsum.photos/seed/instagallery-post-1203/1200/800', 'https://picsum.photos/seed/instagallery-post-1203-thumb/600/400', 'IMAGE', 1, 1200, 800),
(2204, 1204, 'https://picsum.photos/seed/instagallery-post-1204/1200/800', 'https://picsum.photos/seed/instagallery-post-1204-thumb/600/400', 'IMAGE', 1, 1200, 800),
(2205, 1205, 'https://picsum.photos/seed/instagallery-post-1205/1200/800', 'https://picsum.photos/seed/instagallery-post-1205-thumb/600/400', 'IMAGE', 1, 1200, 800),
(2206, 1206, 'https://picsum.photos/seed/instagallery-post-1206/1200/800', 'https://picsum.photos/seed/instagallery-post-1206-thumb/600/400', 'IMAGE', 1, 1200, 800),
(2207, 1207, 'https://picsum.photos/seed/instagallery-post-1207/1200/800', 'https://picsum.photos/seed/instagallery-post-1207-thumb/600/400', 'IMAGE', 1, 1200, 800),
(2208, 1208, 'https://picsum.photos/seed/instagallery-post-1208/1200/800', 'https://picsum.photos/seed/instagallery-post-1208-thumb/600/400', 'IMAGE', 1, 1200, 800),
(2209, 1209, 'https://picsum.photos/seed/instagallery-post-1209/1200/800', 'https://picsum.photos/seed/instagallery-post-1209-thumb/600/400', 'IMAGE', 1, 1200, 800),
(2210, 1210, 'https://picsum.photos/seed/instagallery-post-1210/1200/800', 'https://picsum.photos/seed/instagallery-post-1210-thumb/600/400', 'IMAGE', 1, 1200, 800),
(2211, 1211, 'https://picsum.photos/seed/instagallery-post-1211/1200/800', 'https://picsum.photos/seed/instagallery-post-1211-thumb/600/400', 'IMAGE', 1, 1200, 800),
(2212, 1212, 'https://picsum.photos/seed/instagallery-post-1212/1200/800', 'https://picsum.photos/seed/instagallery-post-1212-thumb/600/400', 'IMAGE', 1, 1200, 800),
(2213, 1213, 'https://picsum.photos/seed/instagallery-post-1213/1200/800', 'https://picsum.photos/seed/instagallery-post-1213-thumb/600/400', 'IMAGE', 1, 1200, 800),
(2214, 1214, 'https://picsum.photos/seed/instagallery-post-1214/1200/800', 'https://picsum.photos/seed/instagallery-post-1214-thumb/600/400', 'IMAGE', 1, 1200, 800),
(2215, 1215, 'https://picsum.photos/seed/instagallery-post-1215/1200/800', 'https://picsum.photos/seed/instagallery-post-1215-thumb/600/400', 'IMAGE', 1, 1200, 800)
ON DUPLICATE KEY UPDATE
    post_id = VALUES(post_id),
    media_file_url = VALUES(media_file_url),
    thumbnail_url = VALUES(thumbnail_url);

-- 3) Thêm Followings cho user 150 (user 150 theo dõi 20 người khác)
-- Dùng INSERT IGNORE để tránh trùng lặp nếu chạy lại
INSERT IGNORE INTO followers (follower_id, following_id, created_at) VALUES
(150, 1, CURRENT_TIMESTAMP - INTERVAL 10 DAY),
(150, 2, CURRENT_TIMESTAMP - INTERVAL 10 DAY),
(150, 3, CURRENT_TIMESTAMP - INTERVAL 10 DAY),
(150, 4, CURRENT_TIMESTAMP - INTERVAL 10 DAY),
(150, 5, CURRENT_TIMESTAMP - INTERVAL 10 DAY),
(150, 6, CURRENT_TIMESTAMP - INTERVAL 10 DAY),
(150, 7, CURRENT_TIMESTAMP - INTERVAL 9 DAY),
(150, 8, CURRENT_TIMESTAMP - INTERVAL 9 DAY),
(150, 9, CURRENT_TIMESTAMP - INTERVAL 8 DAY),
(150, 10, CURRENT_TIMESTAMP - INTERVAL 8 DAY),
(150, 20, CURRENT_TIMESTAMP - INTERVAL 7 DAY),
(150, 30, CURRENT_TIMESTAMP - INTERVAL 7 DAY),
(150, 40, CURRENT_TIMESTAMP - INTERVAL 6 DAY),
(150, 50, CURRENT_TIMESTAMP - INTERVAL 6 DAY),
(150, 60, CURRENT_TIMESTAMP - INTERVAL 5 DAY),
(150, 66, CURRENT_TIMESTAMP - INTERVAL 5 DAY),
(150, 70, CURRENT_TIMESTAMP - INTERVAL 4 DAY),
(150, 75, CURRENT_TIMESTAMP - INTERVAL 4 DAY),
(150, 80, CURRENT_TIMESTAMP - INTERVAL 3 DAY),
(150, 85, CURRENT_TIMESTAMP - INTERVAL 3 DAY),
(150, 90, CURRENT_TIMESTAMP - INTERVAL 2 DAY),
(150, 95, CURRENT_TIMESTAMP - INTERVAL 2 DAY),
(150, 100, CURRENT_TIMESTAMP - INTERVAL 1 DAY);

-- 4) Thêm Followers cho user 150 (25 người khác theo dõi user 150)
INSERT IGNORE INTO followers (follower_id, following_id, created_at) VALUES
(1, 150, CURRENT_TIMESTAMP - INTERVAL 10 DAY),
(2, 150, CURRENT_TIMESTAMP - INTERVAL 10 DAY),
(3, 150, CURRENT_TIMESTAMP - INTERVAL 10 DAY),
(4, 150, CURRENT_TIMESTAMP - INTERVAL 10 DAY),
(5, 150, CURRENT_TIMESTAMP - INTERVAL 10 DAY),
(6, 150, CURRENT_TIMESTAMP - INTERVAL 10 DAY),
(7, 150, CURRENT_TIMESTAMP - INTERVAL 10 DAY),
(8, 150, CURRENT_TIMESTAMP - INTERVAL 9 DAY),
(9, 150, CURRENT_TIMESTAMP - INTERVAL 9 DAY),
(10, 150, CURRENT_TIMESTAMP - INTERVAL 8 DAY),
(11, 150, CURRENT_TIMESTAMP - INTERVAL 8 DAY),
(12, 150, CURRENT_TIMESTAMP - INTERVAL 7 DAY),
(13, 150, CURRENT_TIMESTAMP - INTERVAL 7 DAY),
(14, 150, CURRENT_TIMESTAMP - INTERVAL 6 DAY),
(15, 150, CURRENT_TIMESTAMP - INTERVAL 6 DAY),
(16, 150, CURRENT_TIMESTAMP - INTERVAL 5 DAY),
(17, 150, CURRENT_TIMESTAMP - INTERVAL 5 DAY),
(18, 150, CURRENT_TIMESTAMP - INTERVAL 4 DAY),
(19, 150, CURRENT_TIMESTAMP - INTERVAL 4 DAY),
(20, 150, CURRENT_TIMESTAMP - INTERVAL 3 DAY),
(21, 150, CURRENT_TIMESTAMP - INTERVAL 3 DAY),
(22, 150, CURRENT_TIMESTAMP - INTERVAL 2 DAY),
(23, 150, CURRENT_TIMESTAMP - INTERVAL 2 DAY),
(24, 150, CURRENT_TIMESTAMP - INTERVAL 1 DAY),
(25, 150, CURRENT_TIMESTAMP - INTERVAL 1 DAY),
(26, 150, CURRENT_TIMESTAMP),
(27, 150, CURRENT_TIMESTAMP),
(28, 150, CURRENT_TIMESTAMP),
(29, 150, CURRENT_TIMESTAMP),
(30, 150, CURRENT_TIMESTAMP);

SET FOREIGN_KEY_CHECKS = 1;

-- 5) Cập nhật lại counter cache cho toàn bộ users (bao gồm 150 và các user khác)
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
    u.follower_count = COALESCE(fc.total_followers, 0),
    u.following_count = COALESCE(fgc.total_following, 0),
    u.post_count = COALESCE(pc.total_posts, 0);
