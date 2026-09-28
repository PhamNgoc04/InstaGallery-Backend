USE instagallery;
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =========================================================================
-- SEED POSTS FOR NGOCPB04 (ID 4) & THAOPHAM (ID 2)
-- =========================================================================

-- 1) Bài viết cho Phạm Ngọc, user 4 (phamngoc@gmail.com) - 8 bài
INSERT INTO posts (
    id, user_id, caption, location, visibility, comment_visibility,
    like_count, comment_count, share_count, created_at, updated_at
) VALUES
(1401, 4, 'Một buổi chiều hoàng hôn rực rỡ bên Hồ Tây. Mặt trời đỏ rực lặn dần sau những rặng cây 🌅 #sunset #hanoi #taylake #chill', 'Hồ Tây, Hà Nội', 'PUBLIC', 'ALLOW_ALL', 158, 4, 18, CURRENT_TIMESTAMP - INTERVAL 3 HOUR, CURRENT_TIMESTAMP - INTERVAL 3 HOUR),
(1402, 4, 'Góc quán cà phê sách quen thuộc trong con ngõ nhỏ phố cổ. Cảm giác bình yên giữa lòng thủ đô tấp nập ☕📖 #cafe #hanoi #vintage #lifestyle', 'Phố cổ Hà Nội', 'PUBLIC', 'ALLOW_ALL', 95, 3, 8, CURRENT_TIMESTAMP - INTERVAL 8 HOUR, CURRENT_TIMESTAMP - INTERVAL 8 HOUR),
(1403, 4, 'Chuyến trekking Sapa săn mây đầu mùa. Những thửa ruộng bậc thang ngút ngàn đẹp đến ngỡ ngàng! ⛰️🌾 #sapa #travel #landscape #vietnam', 'Sa Pa, Lào Cai', 'PUBLIC', 'ALLOW_ALL', 230, 5, 32, CURRENT_TIMESTAMP - INTERVAL 1 DAY, CURRENT_TIMESTAMP - INTERVAL 1 DAY),
(1404, 4, 'Thu Hà Nội với những gánh hoa rực rỡ trên phố Phan Đình Phùng. Mùa đẹp nhất trong năm đã về 🍂🌼 #autumn #hanoi #street #flowers', 'Phan Đình Phùng, Hà Nội', 'PUBLIC', 'ALLOW_ALL', 184, 4, 21, CURRENT_TIMESTAMP - INTERVAL 2 DAY, CURRENT_TIMESTAMP - INTERVAL 2 DAY),
(1405, 4, 'Bình minh trên biển Mỹ Khê - một sớm mùa hè trong veo và ngập tràn năng lượng 🌊🌤️ #danang #beach #sunrise #travel', 'Bãi biển Mỹ Khê, Đà Nẵng', 'PUBLIC', 'ALLOW_ALL', 142, 2, 14, CURRENT_TIMESTAMP - INTERVAL 3 DAY, CURRENT_TIMESTAMP - INTERVAL 3 DAY),
(1406, 4, 'Đêm Hội An lung linh sắc màu đèn lồng. Đi bộ dọc bờ sông Hoài nghe nhịp thở của thời gian 🏮✨ #hoian #night #heritage #travel', 'Phố cổ Hội An', 'PUBLIC', 'ALLOW_ALL', 176, 3, 19, CURRENT_TIMESTAMP - INTERVAL 4 DAY, CURRENT_TIMESTAMP - INTERVAL 4 DAY),
(1407, 4, 'Một góc check-in siêu xinh tại quán cafe phong cách Địa Trung Hải cuối tuần này 🌿🤍 #lifestyle #coffee #weekend #chill', 'Hà Nội', 'PUBLIC', 'ALLOW_ALL', 112, 2, 9, CURRENT_TIMESTAMP - INTERVAL 5 DAY, CURRENT_TIMESTAMP - INTERVAL 5 DAY),
(1408, 4, 'Sài Gòn những chiều tan tầm, ánh nắng vàng hắt lên những tòa nhà cao tầng thật hiện đại 🏙️🌇 #saigon #street #citylight #architecture', 'Quận 1, TP. Hồ Chí Minh', 'PUBLIC', 'ALLOW_ALL', 128, 3, 15, CURRENT_TIMESTAMP - INTERVAL 6 DAY, CURRENT_TIMESTAMP - INTERVAL 6 DAY)
ON DUPLICATE KEY UPDATE
    caption = VALUES(caption),
    location = VALUES(location),
    like_count = VALUES(like_count),
    comment_count = VALUES(comment_count),
    share_count = VALUES(share_count);

-- 2) Bài viết cho Phạm Phương Thảo, user 2 (phamphuongthao@gmail.com) - 8 bài
INSERT INTO posts (
    id, user_id, caption, location, visibility, comment_visibility,
    like_count, comment_count, share_count, created_at, updated_at
) VALUES
(1409, 2, 'Concept nàng thơ mùa thu Hà Nội cùng tà áo dài trắng tinh khôi dưới nắng sớm 🍂🤍 Trọn bộ ảnh vừa hoàn thiện cho bạn mẫu xinh đẹp! #portrait #nangtho #hanoi #lookbook', 'Hoàng thành Thăng Long, Hà Nội', 'PUBLIC', 'ALLOW_ALL', 320, 6, 45, CURRENT_TIMESTAMP - INTERVAL 2 HOUR, CURRENT_TIMESTAMP - INTERVAL 2 HOUR),
(1410, 2, 'Bộ ảnh Pre-wedding nhẹ nhàng tại vườn thông Ba Vì. Tình yêu đôi khi chỉ cần những cái nắm tay thật chặt 🌲💍 #wedding #couple #outdoor #bavi', 'Rừng thông Ba Vì, Hà Nội', 'PUBLIC', 'ALLOW_ALL', 285, 5, 38, CURRENT_TIMESTAMP - INTERVAL 6 HOUR, CURRENT_TIMESTAMP - INTERVAL 6 HOUR),
(1411, 2, 'Beauty shoot trong studio với ánh sáng tự nhiên mềm mại. Tôn vinh nét đẹp trong trẻo thuần khiết ✨💄 #beauty #portrait #studio #makeup', 'Thảo Studio, Hà Nội', 'PUBLIC', 'ALLOW_ALL', 210, 4, 25, CURRENT_TIMESTAMP - INTERVAL 12 HOUR, CURRENT_TIMESTAMP - INTERVAL 12 HOUR),
(1412, 2, 'Concept Retro Vintage thập niên 90 tại khu tập thể cũ. Tone màu film hoài niệm và nhiều cảm xúc 📻🎞️ #vintage #retro #film #portrait', 'Khu tập thể Nghĩa Tân, Hà Nội', 'PUBLIC', 'ALLOW_ALL', 198, 3, 22, CURRENT_TIMESTAMP - INTERVAL 1 DAY, CURRENT_TIMESTAMP - INTERVAL 1 DAY),
(1413, 2, 'Lookbook thời trang hè thu cho Local Brand. Chất vải linen bay bổng và ánh sáng tự nhiên ngoài trời 👗🌿 #fashion #lookbook #editorial #street', 'Phố đi bộ Hà Nội', 'PUBLIC', 'ALLOW_ALL', 260, 4, 30, CURRENT_TIMESTAMP - INTERVAL 2 DAY, CURRENT_TIMESTAMP - INTERVAL 2 DAY),
(1414, 2, 'Bộ ảnh đôi kẹo ngọt lãng mạn giữa vườn hoa cẩm tú cầu Đà Lạt 🌸💕 Cảm ơn hai bạn đã tin tưởng dịch vụ chụp ảnh của Thảo! #couple #dalat #flowers #love', 'Vườn hoa cẩm tú cầu, Đà Lạt', 'PUBLIC', 'ALLOW_ALL', 340, 7, 52, CURRENT_TIMESTAMP - INTERVAL 3 DAY, CURRENT_TIMESTAMP - INTERVAL 3 DAY),
(1415, 2, 'Chân dung đen trắng (Black & White) - khi mọi chi tiết thừa bị lược bỏ, chỉ còn lại chiều sâu ánh mắt và cảm xúc 🖤🤍 #bnw #portrait #fineart #moody', 'Studio Hà Nội', 'PUBLIC', 'ALLOW_ALL', 175, 3, 16, CURRENT_TIMESTAMP - INTERVAL 4 DAY, CURRENT_TIMESTAMP - INTERVAL 4 DAY),
(1416, 2, 'Góc phố ẩm thực đêm Hà Nội qua lăng kính phóng sự đời thường. Nụ cười rạng rỡ của người lao động bình dị 🍜🏮 #streetphotography #documentary #hanoi #life', 'Phố ẩm thực Tống Duy Tân, Hà Nội', 'PUBLIC', 'ALLOW_ALL', 225, 4, 28, CURRENT_TIMESTAMP - INTERVAL 5 DAY, CURRENT_TIMESTAMP - INTERVAL 5 DAY)
ON DUPLICATE KEY UPDATE
    caption = VALUES(caption),
    location = VALUES(location),
    like_count = VALUES(like_count),
    comment_count = VALUES(comment_count),
    share_count = VALUES(share_count);

-- 3) MEDIA CHO 16 BÀI VIẾT TRÊN
INSERT INTO post_media (
    id, post_id, media_file_url, thumbnail_url, media_type, position, width, height
) VALUES
-- Posts user 4
(2401, 1401, 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e', 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=500', 'IMAGE', 1, 1200, 800),
(2402, 1402, 'https://images.unsplash.com/photo-1495474472287-4d71bcdd2085', 'https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=500', 'IMAGE', 1, 1200, 800),
(2403, 1403, 'https://images.unsplash.com/photo-1464822759023-fed622ff2c3b', 'https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?w=500', 'IMAGE', 1, 1200, 800),
(2404, 1404, 'https://images.unsplash.com/photo-1508214751196-bcfd4ca60f91', 'https://images.unsplash.com/photo-1508214751196-bcfd4ca60f91?w=500', 'IMAGE', 1, 900, 1200),
(2405, 1405, 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e', 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=500', 'IMAGE', 1, 1200, 800),
(2406, 1406, 'https://images.unsplash.com/photo-1518005020951-eccb494ad742', 'https://images.unsplash.com/photo-1518005020951-eccb494ad742?w=500', 'IMAGE', 1, 1200, 900),
(2407, 1407, 'https://images.unsplash.com/photo-1517841905240-472988babdf9', 'https://images.unsplash.com/photo-1517841905240-472988babdf9?w=500', 'IMAGE', 1, 900, 1200),
(2408, 1408, 'https://images.unsplash.com/photo-1494526585095-c41746248156', 'https://images.unsplash.com/photo-1494526585095-c41746248156?w=500', 'IMAGE', 1, 1200, 800),

-- Posts user 2
(2409, 1409, 'https://images.unsplash.com/photo-1534528741775-53994a69daeb', 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500', 'IMAGE', 1, 900, 1200),
(2410, 1410, 'https://images.unsplash.com/photo-1519741497674-611481863552', 'https://images.unsplash.com/photo-1519741497674-611481863552?w=500', 'IMAGE', 1, 1200, 800),
(2411, 1411, 'https://images.unsplash.com/photo-1494790108377-be9c29b29330', 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=500', 'IMAGE', 1, 900, 1200),
(2412, 1412, 'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d', 'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=500', 'IMAGE', 1, 900, 1200),
(2413, 1413, 'https://images.unsplash.com/photo-1503342217505-b0a15ec3261c', 'https://images.unsplash.com/photo-1503342217505-b0a15ec3261c?w=500', 'IMAGE', 1, 900, 1200),
(2414, 1414, 'https://images.unsplash.com/photo-1520854221256-17451cc331bf', 'https://images.unsplash.com/photo-1520854221256-17451cc331bf?w=500', 'IMAGE', 1, 1200, 800),
(2415, 1415, 'https://images.unsplash.com/photo-1524504388940-b1c1722653e1', 'https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=500', 'IMAGE', 1, 900, 1200),
(2416, 1416, 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=500', 'IMAGE', 1, 1200, 800)
ON DUPLICATE KEY UPDATE
    media_file_url = VALUES(media_file_url),
    thumbnail_url = VALUES(thumbnail_url);

-- 4) GẮN TAG CHO MEDIA
INSERT IGNORE INTO post_media_tags (media_id, tag_id) VALUES
(2401, 301), (2401, 304),
(2402, 311), (2402, 320),
(2403, 301), (2403, 304), (2403, 306),
(2404, 301), (2404, 305),
(2405, 304), (2405, 323),
(2406, 301), (2406, 305), (2406, 317),
(2407, 311), (2407, 319),
(2408, 305), (2408, 312),
(2409, 301), (2409, 302), (2409, 314),
(2410, 301), (2410, 303), (2410, 307),
(2411, 302), (2411, 308), (2411, 309),
(2412, 302), (2412, 305), (2412, 321),
(2413, 301), (2413, 314), (2413, 309),
(2414, 301), (2414, 303), (2414, 307),
(2415, 302), (2415, 321),
(2416, 305), (2416, 324);

-- 5) BÌNH LUẬN MẪU (COMMENTS)
INSERT IGNORE INTO comments (id, post_id, user_id, content, created_at) VALUES
(1401, 1401, 2, 'Hoàng hôn Hồ Tây lúc nào cũng chill hết nấc anh ạ! 🌅', CURRENT_TIMESTAMP - INTERVAL 2 HOUR),
(1402, 1401, 5, 'Màu trời đẹp quá bạn ơi 😍', CURRENT_TIMESTAMP - INTERVAL 1 HOUR),
(1403, 1402, 129, 'Quán cafe này ở ngõ nào vậy bạn? Nhìn góc ngồi mê quá!', CURRENT_TIMESTAMP - INTERVAL 5 HOUR),
(1404, 1402, 4, 'Ở ngõ 36 Hàng Trống nha bạn ơi ☕', CURRENT_TIMESTAMP - INTERVAL 4 HOUR),
(1405, 1403, 101, 'Góc chụp góc ruộng bậc thang Sapa này canh ánh sáng đỉnh quá!', CURRENT_TIMESTAMP - INTERVAL 18 HOUR),
(1406, 1404, 2, 'Mùa thu Hà Nội là mùa đẹp nhất để chụp ảnh luôn nè ❤️', CURRENT_TIMESTAMP - INTERVAL 1 DAY),
(1407, 1409, 4, 'Ảnh chị Thảo chụp lúc nào tone màu cũng trong trẻo, nàng thơ quá chị ơi! 🥰', CURRENT_TIMESTAMP - INTERVAL 1 HOUR),
(1408, 1409, 5, 'Bộ này xuất sắc quá, makeup và ánh sáng đều đỉnh!', CURRENT_TIMESTAMP - INTERVAL 1 HOUR),
(1409, 1410, 8, 'Concept pre-wedding rừng thông lãng mạn quá, cho em xin báo giá gói này nhé chị', CURRENT_TIMESTAMP - INTERVAL 4 HOUR),
(1410, 1410, 2, 'Dạ em inbox chi tiết gói cho bạn qua tin nhắn rồi nhé ạ!', CURRENT_TIMESTAMP - INTERVAL 3 HOUR),
(1411, 1414, 130, 'Vườn cẩm tú cầu nở rộ nhìn như cổ tích vậy trời ơi 🌸', CURRENT_TIMESTAMP - INTERVAL 2 DAY),
(1412, 1416, 104, 'Street photo góc này bắt khoảnh khắc tự nhiên rất có hồn!', CURRENT_TIMESTAMP - INTERVAL 3 DAY);

-- 6) LƯỢT THẢ TIM (LIKES)
INSERT IGNORE INTO likes (user_id, post_id) VALUES
(1, 1401), (2, 1401), (3, 1401), (5, 1401), (6, 1401), (8, 1401), (10, 1401),
(1, 1402), (2, 1402), (5, 1402), (129, 1402), (130, 1402),
(1, 1403), (2, 1403), (101, 1403), (104, 1403), (105, 1403), (8, 1403),
(1, 1404), (2, 1404), (5, 1404), (6, 1404), (7, 1404),
(1, 1409), (4, 1409), (5, 1409), (6, 1409), (7, 1409), (8, 1409), (101, 1409),
(1, 1410), (4, 1410), (8, 1410), (9, 1410), (10, 1410), (129, 1410),
(1, 1414), (4, 1414), (5, 1414), (130, 1414), (131, 1414), (132, 1414);

-- 7) CẬP NHẬT LẠI SỐ LƯỢNG POST_COUNT VÀ TƯƠNG TÁC
UPDATE users u
SET post_count = (
    SELECT COUNT(*) FROM posts p WHERE p.user_id = u.id AND p.deleted_at IS NULL
)
WHERE u.id IN (2, 4);

UPDATE posts p
SET 
    comment_count = (SELECT COUNT(*) FROM comments c WHERE c.post_id = p.id),
    like_count = (SELECT COUNT(*) FROM likes l WHERE l.post_id = p.id)
WHERE p.id BETWEEN 1401 AND 1416;

SET FOREIGN_KEY_CHECKS = 1;
