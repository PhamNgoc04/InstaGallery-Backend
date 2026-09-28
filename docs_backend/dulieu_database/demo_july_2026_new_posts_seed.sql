USE instagallery;
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- =========================================================================
-- RICH ENRICHMENT DEMO SEED - JULY 2026 (UPDATED WITH 30+ HIGH-ENGAGEMENT POSTS)
-- Adds new client users, new photographers with portfolios, 
-- and 30+ highly-recent social posts across all categories.
-- High likes ensure they rank in Page 1 (top 80 explore posts) for client-side filtering.
-- =========================================================================

SET FOREIGN_KEY_CHECKS = 0;

-- 1) INSERT NEW CLIENT USERS (IDs 201 - 205)
INSERT IGNORE INTO users (
    id, username, email, password_hash, full_name, profile_picture_url, bio,
    location, role, user_type, is_active, is_verified, provider, is_private,
    follower_count, following_count, post_count
) VALUES
(201, 'hoangnam', 'hoangnam@gmail.com', '$2a$12$1tPMR06e8N/p.r03cWpXpOS0VpePshq4w1bHq0Y.xJ4V8z1z1z1z1', 'Hoàng Nam', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150', 'Thích chụp ảnh đường phố Hà Nội, thích cà phê vỉa hè và lưu giữ góc phố thân quen.', 'Ha Noi', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 180, 95, 0),
(202, 'bichthuy', 'bichthuy@gmail.com', '$2a$12$1tPMR06e8N/p.r03cWpXpOS0VpePshq4w1bHq0Y.xJ4V8z1z1z1z1', 'Bích Thủy', 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150', 'Quan tâm ảnh chân dung nghệ thuật, muốn tìm thợ ảnh chụp album cá nhân.', 'Ha Noi', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 240, 120, 0),
(203, 'phuonganh', 'phuonganh@gmail.com', '$2a$12$1tPMR06e8N/p.r03cWpXpOS0VpePshq4w1bHq0Y.xJ4V8z1z1z1z1', 'Phương Anh', 'https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=150', 'Mê du lịch và chụp ảnh couple outdoor ở Đà Lạt. Cần tìm thợ ảnh có tâm.', 'Da Lat', 'USER', 'CLIENT', 1, 1, 'LOCAL', 0, 310, 155, 0),
(204, 'quocanh', 'quocanh@gmail.com', '$2a$12$1tPMR06e8N/p.r03cWpXpOS0VpePshq4w1bHq0Y.xJ4V8z1z1z1z1', 'Quốc Anh', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150', 'Sáng lập local brand tại Sài Gòn, chuyên tìm photographer chụp lookbook sản phẩm.', 'Ho Chi Minh City', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 140, 78, 0),
(205, 'lanhuong', 'lanhuong@gmail.com', '$2a$12$1tPMR06e8N/p.r03cWpXpOS0VpePshq4w1bHq0Y.xJ4V8z1z1z1z1', 'Lan Hương', 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150', 'Thích lưu giữ những khoảnh khắc gia đình nhỏ ấm áp và bình dị.', 'Da Nang', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 215, 110, 0)
ON DUPLICATE KEY UPDATE
    full_name = VALUES(full_name),
    bio = VALUES(bio),
    location = VALUES(location);

-- 2) INSERT NEW PHOTOGRAPHERS (IDs 206 - 210)
INSERT IGNORE INTO users (
    id, username, email, password_hash, full_name, profile_picture_url, bio,
    location, role, user_type, is_active, is_verified, provider, is_private,
    follower_count, following_count, post_count
) VALUES
(206, 'photoducduy', 'photoducduy@gmail.com', '$2a$12$1tPMR06e8N/p.r03cWpXpOS0VpePshq4w1bHq0Y.xJ4V8z1z1z1z1', 'Đức Duy Photo', 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150', 'Nhiếp ảnh gia chuyên chân dung tự nhiên, phóng sự đường phố và ảnh thu Hà Nội trầm mặc.', 'Ha Noi', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1520, 180, 20),
(207, 'photothutrang', 'photothutrang@gmail.com', '$2a$12$1tPMR06e8N/p.r03cWpXpOS0VpePshq4w1bHq0Y.xJ4V8z1z1z1z1', 'Thu Trang Photo', 'https://images.unsplash.com/photo-1508214751196-bcfd4ca60f91?w=150', 'Nhận chụp couple outdoor Đà Lạt, phong cách nhẹ nhàng, thơ mộng, lãng mạn.', 'Da Lat', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1840, 210, 20),
(208, 'photominhquan', 'photominhquan@gmail.com', '$2a$12$1tPMR06e8N/p.r03cWpXpOS0VpePshq4w1bHq0Y.xJ4V8z1z1z1z1', 'Minh Quân Studio', 'https://images.unsplash.com/photo-1500048993953-d23a436266cf?w=150', 'Studio chuyên nghiệp cho local brand, lookbook sản phẩm và ảnh thời trang tại Sài Gòn.', 'Ho Chi Minh City', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 2150, 230, 20),
(209, 'phototruclam', 'phototruclam@gmail.com', '$2a$12$1tPMR06e8N/p.r03cWpXpOS0VpePshq4w1bHq0Y.xJ4V8z1z1z1z1', 'Trúc Lâm FineArt', 'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=150', 'Kể câu chuyện văn hóa, nghệ thuật cố đô qua các bộ ảnh phục cổ và fine art tại Huế.', 'Hue', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1310, 140, 20),
(210, 'photophuongvy', 'photophuongvy@gmail.com', '$2a$12$1tPMR06e8N/p.r03cWpXpOS0VpePshq4w1bHq0Y.xJ4V8z1z1z1z1', 'Phương Vy Baby', 'https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=150', 'Lưu giữ nụ cười trẻ thơ và khoảnh khắc gia đình nhỏ đón bình minh trên biển Đà Nẵng.', 'Da Nang', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1690, 175, 20)
ON DUPLICATE KEY UPDATE
    full_name = VALUES(full_name),
    bio = VALUES(bio),
    location = VALUES(location);

-- 3) INSERT PORTFOLIOS FOR NEW PHOTOGRAPHERS (IDs 206 - 210)
INSERT IGNORE INTO portfolios (
    id, user_id, title, description, specialties, hourly_rate, currency,
    service_area, is_available, rating_avg, review_count
) VALUES
(206, 206, 'Chân Dung Và Đường Phố Hà Nội', 'Gói chụp chân dung nghệ thuật ngoài trời, kết hợp lưu trữ góc phố cổ kính.', '["Portrait","Street","Outdoor"]', 600000.00, 'VND', 'Ha Noi', 1, 4.85, 24),
(207, 207, 'Đà Lạt Couple Story', 'Gói chụp đôi lãng mạn tại các đồi thông, hồ nước và quán cà phê Đà Lạt.', '["Couple","Wedding","Outdoor"]', 850000.00, 'VND', 'Da Lat', 1, 4.90, 31),
(208, 208, 'Fashion Lookbook & Studio', 'Gói chụp thương mại thời trang, lookbook local brand tại studio chuyên nghiệp.', '["Fashion","Product","Studio"]', 1200000.00, 'VND', 'Ho Chi Minh City', 1, 4.75, 42),
(209, 209, 'Cổ Đô Huế FineArt', 'Chụp ảnh phục cổ, nghệ thuật cung đình tại lăng tẩm và Đại Nội Huế.', '["FineArt","Landscape","Architecture"]', 900000.00, 'VND', 'Hue', 1, 4.88, 19),
(210, 210, 'Gia Đình Và Bé Yêu Đà Nẵng', 'Gói chụp ảnh gia đình nhỏ, chụp bé đón bình minh tại bãi biển Mỹ Khê.', '["Family","Outdoor","Lifestyle"]', 700000.00, 'VND', 'Da Nang', 1, 4.92, 15)
ON DUPLICATE KEY UPDATE
    title = VALUES(title),
    description = VALUES(description),
    hourly_rate = VALUES(hourly_rate),
    specialties = VALUES(specialties);

-- 4) INSERT FRESH NEW POSTS (IDs 1301 - 1342) WITH RECENT TIMESTAMPS & HIGH LIKES
INSERT INTO posts (
    id, user_id, caption, location, visibility, comment_visibility,
    like_count, comment_count, share_count, created_at
) VALUES
-- ================= TODAY SECTION (Hôm nay, < 24 hours ago) =================
(1301, 206, 'Hà Nội sớm mùa thu, phố cổ bình yên đến lạ thường. Ánh nắng ban mai xuyên qua tán lá cổ thụ... #portrait #outdoor #hanoi', 'Ha Noi', 'PUBLIC', 'ALLOW_ALL', 4900, 3, 15, CURRENT_TIMESTAMP - INTERVAL 5 MINUTE),
(1302, 207, 'Một ngày nhiều sương mù và ngập tràn tiếng cười của cặp đôi nhỏ giữa đồi thông Đà Lạt thơ mộng. #couple #wedding #dalat', 'Da Lat', 'PUBLIC', 'ALLOW_ALL', 4850, 2, 28, CURRENT_TIMESTAMP - INTERVAL 12 MINUTE),
(1303, 208, 'Lookbook mới thực hiện cho một local brand thời trang thiết kế Việt Nam. Tone màu tối giản và cá tính. #fashion #lookbook #studio', 'Ho Chi Minh City', 'PUBLIC', 'ALLOW_ALL', 4800, 1, 22, CURRENT_TIMESTAMP - INTERVAL 25 MINUTE),
(1304, 209, 'Vẻ đẹp cổ kính trầm mặc của Đại Nội Huế dưới cơn mưa chiều thu... #fineart #landscape #hue', 'Hue', 'PUBLIC', 'ALLOW_ALL', 4750, 1, 41, CURRENT_TIMESTAMP - INTERVAL 45 MINUTE),
(1305, 210, 'Nụ cười hồn nhiên của bé yêu giữa bãi cát vàng biển Mỹ Khê Đà Nẵng. Gia đình nhỏ ngập tràn niềm vui. #family #outdoor #danang', 'Da Nang', 'PUBLIC', 'ALLOW_ALL', 4700, 0, 10, CURRENT_TIMESTAMP - INTERVAL 1 HOUR),
(1306, 206, 'Góc cà phê đường tàu Hà Nội - nơi nhịp sống Thủ đô vẫn trôi chầm chậm bên những thanh ray cũ kỹ. #street #hanoi', 'Ha Noi', 'PUBLIC', 'ALLOW_ALL', 4650, 0, 18, CURRENT_TIMESTAMP - INTERVAL 2 HOUR),
(1307, 207, 'Sunset at Tuyen Lam Lake. Khi hoàng hôn buông xuống, Đà Lạt bỗng hóa thơ mộng hơn bao giờ hết. #landscape #travel #dalat', 'Da Lat', 'PUBLIC', 'ALLOW_ALL', 4600, 0, 31, CURRENT_TIMESTAMP - INTERVAL 3 HOUR),
(1308, 208, 'Street style năng động chụp tại khu vực Nhà hát Thành phố - nhịp sống Sài Gòn trẻ trung và phóng khoáng. #lifestyle #street #hcmc', 'Ho Chi Minh City', 'PUBLIC', 'ALLOW_ALL', 4550, 0, 14, CURRENT_TIMESTAMP - INTERVAL 4 HOUR),
(1309, 209, 'Nắng vàng đổ bóng bên những bức tường thành cũ kỹ... Một góc nhìn bình yên khác của mảnh đất Cố đô. #architecture #fineart #hue', 'Hue', 'PUBLIC', 'ALLOW_ALL', 4500, 0, 19, CURRENT_TIMESTAMP - INTERVAL 5 HOUR),
(1310, 210, 'Khoảnh khắc cả gia đình cùng nhau đón bình minh ấm áp trên biển Mỹ Khê. Cảm xúc thật trọn vẹn. #lifestyle #family #danang', 'Da Nang', 'PUBLIC', 'ALLOW_ALL', 4450, 0, 25, CURRENT_TIMESTAMP - INTERVAL 6 HOUR),
(1311, 206, 'Chân dung bạn mẫu chụp trong buổi chiều thu nhiều gió tại bãi đá sông Hồng Hà Nội. #portrait #outdoor #hanoi', 'Ha Noi', 'PUBLIC', 'ALLOW_ALL', 4400, 0, 9, CURRENT_TIMESTAMP - INTERVAL 8 HOUR),
(1312, 207, 'Đà Lạt những ngày này se lạnh sớm, nhưng những bức ảnh vẫn ấm áp nhờ tình yêu chân thành của họ. #couple #wedding #dalat', 'Da Lat', 'PUBLIC', 'ALLOW_ALL', 4350, 0, 37, CURRENT_TIMESTAMP - INTERVAL 10 HOUR),
(1321, 206, 'Góc phố Phan Đình Phùng rợp bóng lá vàng bay sớm nay. Mùa thu Hà Nội chạm ngõ rồi mọi người ơi. #portrait #street #hanoi', 'Ha Noi', 'PUBLIC', 'ALLOW_ALL', 4300, 0, 19, CURRENT_TIMESTAMP - INTERVAL 30 MINUTE),
(1322, 207, 'Ánh bình minh đầu ngày chiếu qua làn sương mỏng trên đồi cỏ hồng mộng mơ. #landscape #travel #dalat', 'Da Lat', 'PUBLIC', 'ALLOW_ALL', 4250, 0, 29, CURRENT_TIMESTAMP - INTERVAL 15 MINUTE),
(1323, 208, 'Bộ ảnh cưới tối giản tinh tế nhưng đong đầy cảm xúc chụp tại không gian bảo tàng Mỹ thuật. #wedding #couple #hcmc', 'Ho Chi Minh City', 'PUBLIC', 'ALLOW_ALL', 4200, 0, 34, CURRENT_TIMESTAMP - INTERVAL 50 MINUTE),
(1324, 209, 'Nhịp sống lao động bình dị của người dân chài lưới trên đầm Lập An lúc bình minh lên. #street #landscape #hue', 'Hue', 'PUBLIC', 'ALLOW_ALL', 4150, 0, 42, CURRENT_TIMESTAMP - INTERVAL 1 HOUR),
(1325, 210, 'Chụp bé con chập chững bước đi trên bãi biển Non Nước vào một buổi chiều lộng gió. #family #outdoor #danang', 'Da Nang', 'PUBLIC', 'ALLOW_ALL', 4100, 0, 18, CURRENT_TIMESTAMP - INTERVAL 2 HOUR),

-- ================= WEEK SECTION (Tuần này, 1 - 7 days ago) =================
(1313, 206, 'Một buổi chiều lang thang chụp ảnh chân dung phim cổ điển quanh hồ Gươm thơ mộng. #portrait #street #hanoi', 'Ha Noi', 'PUBLIC', 'ALLOW_ALL', 4500, 0, 45, CURRENT_TIMESTAMP - INTERVAL 3 DAY),
(1314, 207, 'Đám cưới nhỏ ngoài trời ngập nắng, một cái kết trọn vẹn cho tình yêu 5 năm đi qua sóng gió. #wedding #couple #dalat', 'Da Lat', 'PUBLIC', 'ALLOW_ALL', 4400, 0, 52, CURRENT_TIMESTAMP - INTERVAL 4 DAY),
(1315, 208, 'Góc phố Sài Gòn rực rỡ nắng vàng lung linh qua lăng kính nhiếp ảnh đường phố. #street #landscape #hcmc', 'Ho Chi Minh City', 'PUBLIC', 'ALLOW_ALL', 4300, 0, 30, CURRENT_TIMESTAMP - INTERVAL 5 DAY),
(1316, 209, 'Màu xanh ngút ngàn của rừng thông cổ thụ Đà Lạt chụp lúc sương sớm ban mai chưa tan. #landscape #travel #dalat', 'Da Lat', 'PUBLIC', 'ALLOW_ALL', 4200, 0, 28, CURRENT_TIMESTAMP - INTERVAL 6 DAY),
(1331, 206, 'Chân dung thiếu nữ Hà Thành e ấp bên những đóa hoa sen Tây Hồ chớm nở mùa hạ. #portrait #outdoor #hanoi', 'Ha Noi', 'PUBLIC', 'ALLOW_ALL', 4500, 0, 15, CURRENT_TIMESTAMP - INTERVAL 1 DAY),
(1332, 207, 'Ảnh cưới lãng mạn mang đậm chất Hàn Quốc chụp giữa rừng thông Đà Lạt. #wedding #couple #dalat', 'Da Lat', 'PUBLIC', 'ALLOW_ALL', 4400, 0, 21, CURRENT_TIMESTAMP - INTERVAL 2 DAY),
(1333, 208, 'Khoảnh khắc street life mộc mạc và nhộn nhịp quanh chợ Bến Thành chiều muộn. #street #hcmc', 'Ho Chi Minh City', 'PUBLIC', 'ALLOW_ALL', 4300, 0, 12, CURRENT_TIMESTAMP - INTERVAL 2 DAY),
(1334, 209, 'Hoàng hôn lấp lánh phản chiếu rực rỡ trên dòng sông Hương lững lờ trôi chiều nay. #landscape #hue', 'Hue', 'PUBLIC', 'ALLOW_ALL', 4200, 0, 26, CURRENT_TIMESTAMP - INTERVAL 3 DAY),
(1335, 210, 'Album ảnh kỷ niệm đầy ắp tiếng cười giòn tan của bé con tại công viên trung tâm Đà Nẵng. #family #portrait', 'Da Nang', 'PUBLIC', 'ALLOW_ALL', 4100, 0, 8, CURRENT_TIMESTAMP - INTERVAL 4 DAY),
(1336, 206, 'Một góc nhỏ phố cổ lung linh sắc màu dưới ánh đèn dầu ảo diệu về đêm. #street #night #hanoi', 'Ha Noi', 'PUBLIC', 'ALLOW_ALL', 4000, 0, 14, CURRENT_TIMESTAMP - INTERVAL 4 DAY),
(1337, 207, 'Một thoáng mộng mơ ngọt ngào của Đà Lạt qua những nhành hoa dã quỳ nở rộ bên đường đèo. #landscape #travel', 'Da Lat', 'PUBLIC', 'ALLOW_ALL', 3950, 0, 17, CURRENT_TIMESTAMP - INTERVAL 5 DAY),
(1338, 208, 'Bộ ảnh chụp couple phong cách vintage nhẹ nhàng, lưu giữ thanh xuân đôi ta. #couple #outdoor', 'Ho Chi Minh City', 'PUBLIC', 'ALLOW_ALL', 3900, 0, 31, CURRENT_TIMESTAMP - INTERVAL 6 DAY),

-- ================= MONTH SECTION (Tuần trước/Tháng này, 8 - 25 days ago) =================
(1317, 210, 'Ảnh chụp bé yêu cười đùa tinh nghịch trong studio có bố cục tối giản sang trọng. #family #portrait #studio #baby #danang', 'Da Nang', 'PUBLIC', 'ALLOW_ALL', 3800, 0, 15, CURRENT_TIMESTAMP - INTERVAL 9 DAY),
(1341, 206, 'Chân dung studio góc nghiêng nghệ thuật tôn lên nét thanh tú của bạn mẫu. #portrait #studio', 'Ha Noi', 'PUBLIC', 'ALLOW_ALL', 4500, 0, 22, CURRENT_TIMESTAMP - INTERVAL 8 DAY),
(1342, 207, 'Khoảnh khắc thiêng liêng và xúc động khi cô dâu bước vào lễ đường ngoài trời ngập hoa. #wedding #event', 'Da Lat', 'PUBLIC', 'ALLOW_ALL', 4400, 0, 35, CURRENT_TIMESTAMP - INTERVAL 9 DAY),
(1343, 208, 'Nhịp sống hối hả ngược xuôi của dòng người Sài Gòn nhìn từ flycam trên cao. #street #landscape', 'Ho Chi Minh City', 'PUBLIC', 'ALLOW_ALL', 4300, 0, 47, CURRENT_TIMESTAMP - INTERVAL 10 DAY),
(1344, 209, 'Cầu Tràng Tiền rực rỡ sắc màu phản chiếu huyền ảo xuống mặt nước sông Hương yên ả. #landscape #night', 'Hue', 'PUBLIC', 'ALLOW_ALL', 4200, 0, 32, CURRENT_TIMESTAMP - INTERVAL 12 DAY),
(1345, 210, 'Nụ cười hồn nhiên của bé yêu bên chiếc bánh kem sinh nhật đầu đời bên gia đình. #family #baby', 'Da Nang', 'PUBLIC', 'ALLOW_ALL', 4100, 0, 11, CURRENT_TIMESTAMP - INTERVAL 14 DAY),
(1346, 206, 'Bộ ảnh chân dung ngoại cảnh chụp lúc hoàng hôn buông lãng mạn bên hồ Tây nhiều gió. #portrait #outdoor', 'Ha Noi', 'PUBLIC', 'ALLOW_ALL', 4000, 0, 19, CURRENT_TIMESTAMP - INTERVAL 15 DAY),
(1347, 207, 'Nụ hôn ngọt ngào ấm áp dưới làn sương mờ ảo tại quảng trường Lâm Viên buổi sớm. #couple #wedding', 'Da Lat', 'PUBLIC', 'ALLOW_ALL', 3950, 0, 28, CURRENT_TIMESTAMP - INTERVAL 18 DAY),
(1348, 208, 'Lookbook giới thiệu dòng sản phẩm túi xách da thời trang cao cấp phong cách tối giản. #fashion #product', 'Ho Chi Minh City', 'PUBLIC', 'ALLOW_ALL', 3900, 0, 16, CURRENT_TIMESTAMP - INTERVAL 20 DAY),
(1349, 209, 'Cổng Ngọ Môn lộng lẫy và uy nghiêm dưới bầu trời Huế xanh ngắt đầy nắng. #architecture #hue', 'Hue', 'PUBLIC', 'ALLOW_ALL', 3850, 0, 24, CURRENT_TIMESTAMP - INTERVAL 22 DAY),
(1350, 210, 'Khoảnh khắc cả gia đình cùng nhau quây quần ấm áp chúc thọ ông bà dịp cuối tuần. #family #lifestyle', 'Da Nang', 'PUBLIC', 'ALLOW_ALL', 3800, 0, 29, CURRENT_TIMESTAMP - INTERVAL 25 DAY)
ON DUPLICATE KEY UPDATE
    caption = VALUES(caption),
    location = VALUES(location),
    like_count = VALUES(like_count),
    created_at = VALUES(created_at);

-- 5) INSERT POST MEDIA FOR NEW POSTS (IDs 2301 - 2350)
INSERT IGNORE INTO post_media (
    id, post_id, media_file_url, thumbnail_url, media_type, position, width, height
) VALUES
(2301, 1301, 'https://images.unsplash.com/photo-1502602898657-3e91760cbb34?w=1200', 'https://images.unsplash.com/photo-1502602898657-3e91760cbb34?w=600', 'IMAGE', 1, 1200, 800),
(2302, 1302, 'https://images.unsplash.com/photo-1520854221256-17451cc331bf?w=1200', 'https://images.unsplash.com/photo-1520854221256-17451cc331bf?w=600', 'IMAGE', 1, 1200, 800),
(2303, 1303, 'https://images.unsplash.com/photo-1509631179647-0177331693ae?w=1200', 'https://images.unsplash.com/photo-1509631179647-0177331693ae?w=600', 'IMAGE', 1, 1200, 800),
(2304, 1304, 'https://images.unsplash.com/photo-1555939594-58d7cb561ad1?w=1200', 'https://images.unsplash.com/photo-1555939594-58d7cb561ad1?w=600', 'IMAGE', 1, 1200, 800),
(2305, 1305, 'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?w=1200', 'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?w=600', 'IMAGE', 1, 1200, 800),
(2306, 1306, 'https://images.unsplash.com/photo-1518005020951-eccb494ad742?w=1200', 'https://images.unsplash.com/photo-1518005020951-eccb494ad742?w=600', 'IMAGE', 1, 1200, 800),
(2307, 1307, 'https://images.unsplash.com/photo-1475924156734-496f6cac6ec1?w=1200', 'https://images.unsplash.com/photo-1475924156734-496f6cac6ec1?w=600', 'IMAGE', 1, 1200, 800),
(2308, 1308, 'https://images.unsplash.com/photo-1496440737103-cd596325d314?w=1200', 'https://images.unsplash.com/photo-1496440737103-cd596325d314?w=600', 'IMAGE', 1, 1200, 800),
(2309, 1309, 'https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?w=1200', 'https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?w=600', 'IMAGE', 1, 1200, 800),
(2310, 1310, 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=1200', 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=600', 'IMAGE', 1, 1200, 800),
(2311, 1311, 'https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=1200', 'https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=600', 'IMAGE', 1, 1200, 800),
(2312, 1312, 'https://images.unsplash.com/photo-1517841905240-472988babdf9?w=1200', 'https://images.unsplash.com/photo-1517841905240-472988babdf9?w=600', 'IMAGE', 1, 1200, 800),
(2313, 1313, 'https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=1200', 'https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=600', 'IMAGE', 1, 1200, 800),
(2314, 1314, 'https://images.unsplash.com/photo-1520854221256-17451cc331bf?w=1200', 'https://images.unsplash.com/photo-1520854221256-17451cc331bf?w=600', 'IMAGE', 1, 1200, 800),
(2315, 1315, 'https://images.unsplash.com/photo-1502602898657-3e91760cbb34?w=1200', 'https://images.unsplash.com/photo-1502602898657-3e91760cbb34?w=600', 'IMAGE', 1, 1200, 800),
(2316, 1316, 'https://images.unsplash.com/photo-1475924156734-496f6cac6ec1?w=1200', 'https://images.unsplash.com/photo-1475924156734-496f6cac6ec1?w=600', 'IMAGE', 1, 1200, 800),
(2317, 1317, 'https://images.unsplash.com/photo-1517841905240-472988babdf9?w=1200', 'https://images.unsplash.com/photo-1517841905240-472988babdf9?w=600', 'IMAGE', 1, 1200, 800),
-- New Batch (1321 - 1350)
(2321, 1321, 'https://images.unsplash.com/photo-1517841905240-472988babdf9?w=1200', 'https://images.unsplash.com/photo-1517841905240-472988babdf9?w=600', 'IMAGE', 1, 1200, 800),
(2322, 1322, 'https://images.unsplash.com/photo-1475924156734-496f6cac6ec1?w=1200', 'https://images.unsplash.com/photo-1475924156734-496f6cac6ec1?w=600', 'IMAGE', 1, 1200, 800),
(2323, 1323, 'https://images.unsplash.com/photo-1520854221256-17451cc331bf?w=1200', 'https://images.unsplash.com/photo-1520854221256-17451cc331bf?w=600', 'IMAGE', 1, 1200, 800),
(2324, 1324, 'https://images.unsplash.com/photo-1502602898657-3e91760cbb34?w=1200', 'https://images.unsplash.com/photo-1502602898657-3e91760cbb34?w=600', 'IMAGE', 1, 1200, 800),
(2325, 1325, 'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?w=1200', 'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?w=600', 'IMAGE', 1, 1200, 800),
(2326, 1326, 'https://images.unsplash.com/photo-1518005020951-eccb494ad742?w=1200', 'https://images.unsplash.com/photo-1518005020951-eccb494ad742?w=600', 'IMAGE', 1, 1200, 800),
(2327, 1327, 'https://images.unsplash.com/photo-1475924156734-496f6cac6ec1?w=1200', 'https://images.unsplash.com/photo-1475924156734-496f6cac6ec1?w=600', 'IMAGE', 1, 1200, 800),
(2328, 1328, 'https://images.unsplash.com/photo-1509631179647-0177331693ae?w=1200', 'https://images.unsplash.com/photo-1509631179647-0177331693ae?w=600', 'IMAGE', 1, 1200, 800),
(2329, 1329, 'https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?w=1200', 'https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?w=600', 'IMAGE', 1, 1200, 800),
(2330, 1330, 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=1200', 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=600', 'IMAGE', 1, 1200, 800),
(2331, 1331, 'https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=1200', 'https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=600', 'IMAGE', 1, 1200, 800),
(2332, 1332, 'https://images.unsplash.com/photo-1520854221256-17451cc331bf?w=1200', 'https://images.unsplash.com/photo-1520854221256-17451cc331bf?w=600', 'IMAGE', 1, 1200, 800),
(2333, 1333, 'https://images.unsplash.com/photo-1496440737103-cd596325d314?w=1200', 'https://images.unsplash.com/photo-1496440737103-cd596325d314?w=600', 'IMAGE', 1, 1200, 800),
(2334, 1334, 'https://images.unsplash.com/photo-1475924156734-496f6cac6ec1?w=1200', 'https://images.unsplash.com/photo-1475924156734-496f6cac6ec1?w=600', 'IMAGE', 1, 1200, 800),
(2335, 1335, 'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?w=1200', 'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?w=600', 'IMAGE', 1, 1200, 800),
(2336, 1336, 'https://images.unsplash.com/photo-1518005020951-eccb494ad742?w=1200', 'https://images.unsplash.com/photo-1518005020951-eccb494ad742?w=600', 'IMAGE', 1, 1200, 800),
(2337, 1337, 'https://images.unsplash.com/photo-1475924156734-496f6cac6ec1?w=1200', 'https://images.unsplash.com/photo-1475924156734-496f6cac6ec1?w=600', 'IMAGE', 1, 1200, 800),
(2338, 1338, 'https://images.unsplash.com/photo-1517841905240-472988babdf9?w=1200', 'https://images.unsplash.com/photo-1517841905240-472988babdf9?w=600', 'IMAGE', 1, 1200, 800),
(2339, 1339, 'https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?w=1200', 'https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?w=600', 'IMAGE', 1, 1200, 800),
(2340, 1340, 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=1200', 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=600', 'IMAGE', 1, 1200, 800),
(2341, 1341, 'https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=1200', 'https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=600', 'IMAGE', 1, 1200, 800),
(2342, 1342, 'https://images.unsplash.com/photo-1520854221256-17451cc331bf?w=1200', 'https://images.unsplash.com/photo-1520854221256-17451cc331bf?w=600', 'IMAGE', 1, 1200, 800),
(2343, 1343, 'https://images.unsplash.com/photo-1496440737103-cd596325d314?w=1200', 'https://images.unsplash.com/photo-1496440737103-cd596325d314?w=600', 'IMAGE', 1, 1200, 800),
(2344, 1344, 'https://images.unsplash.com/photo-1475924156734-496f6cac6ec1?w=1200', 'https://images.unsplash.com/photo-1475924156734-496f6cac6ec1?w=600', 'IMAGE', 1, 1200, 800),
(2345, 1345, 'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?w=1200', 'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?w=600', 'IMAGE', 1, 1200, 800),
(2346, 1346, 'https://images.unsplash.com/photo-1518005020951-eccb494ad742?w=1200', 'https://images.unsplash.com/photo-1518005020951-eccb494ad742?w=600', 'IMAGE', 1, 1200, 800),
(2347, 1347, 'https://images.unsplash.com/photo-1517841905240-472988babdf9?w=1200', 'https://images.unsplash.com/photo-1517841905240-472988babdf9?w=600', 'IMAGE', 1, 1200, 800),
(2348, 1348, 'https://images.unsplash.com/photo-1509631179647-0177331693ae?w=1200', 'https://images.unsplash.com/photo-1509631179647-0177331693ae?w=600', 'IMAGE', 1, 1200, 800),
(2349, 1349, 'https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?w=1200', 'https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?w=600', 'IMAGE', 1, 1200, 800),
(2350, 1350, 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=1200', 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=600', 'IMAGE', 1, 1200, 800)
ON DUPLICATE KEY UPDATE
    media_file_url = VALUES(media_file_url),
    thumbnail_url = VALUES(thumbnail_url);

-- 5.5) LINK NEW POST MEDIA TO TAGS (post_media_tags)
INSERT IGNORE INTO post_media_tags (media_id, tag_id) VALUES
(2301, 301), (2301, 302), (2301, 311), -- Post 1301: trending, portrait, lifestyle
(2302, 301), (2302, 303), (2302, 307), -- Post 1302: trending, wedding, couple
(2303, 301), (2303, 314), (2303, 309), -- Post 1303: trending, fashion, studio
(2304, 301), (2304, 304), (2304, 321), -- Post 1304: trending, landscape, fineart
(2305, 315), (2305, 318), (2305, 304), -- Post 1305: family, outdoor, landscape
(2306, 305), (2306, 301),             -- Post 1306: street, trending
(2307, 304), (2307, 306), (2307, 301), -- Post 1307: landscape, travel, trending
(2308, 305), (2308, 311), (2308, 301), -- Post 1308: street, lifestyle, trending
(2309, 312), (2309, 321),             -- Post 1309: architecture, fineart
(2310, 311), (2310, 315),             -- Post 1310: lifestyle, family
(2311, 302), (2311, 318),             -- Post 1311: portrait, outdoor
(2312, 303), (2312, 307),             -- Post 1312: wedding, couple
-- This week's posts (Tuần này)
(2313, 301), (2313, 302), (2313, 305), -- Post 1313: trending, portrait, street
(2314, 301), (2314, 303), (2314, 307), -- Post 1314: trending, wedding, couple
(2315, 301), (2315, 305), (2315, 304), -- Post 1315: trending, street, landscape
(2316, 301), (2316, 304), (2316, 306), -- Post 1316: trending, landscape, travel
-- Last week's posts (Tuần trước)
(2317, 301), (2317, 315), (2317, 302), -- Post 1317: trending, family, portrait

-- New Batch mappings (1321 - 1350)
-- Today batch tags
(2321, 301), (2321, 302), (2321, 305), -- Post 1321: trending, portrait, street
(2322, 301), (2322, 304),             -- Post 1322: trending, landscape
(2323, 301), (2323, 303), (2323, 307), -- Post 1323: trending, wedding, couple
(2324, 301), (2324, 305), (2324, 304), -- Post 1324: trending, street, landscape
(2325, 315), (2325, 318),             -- Post 1325: family, outdoor
(2326, 301), (2326, 305),             -- Post 1326: trending, street
(2327, 301), (2327, 304),             -- Post 1327: trending, landscape
(2328, 301), (2328, 314), (2328, 309), -- Post 1328: trending, fashion, studio
(2329, 312),                           -- Post 1329: architecture
(2330, 315), (2330, 311),             -- Post 1330: family, lifestyle

-- This week batch tags
(2331, 301), (2331, 302),             -- Post 1331: trending, portrait
(2332, 301), (2332, 303), (2332, 307), -- Post 1332: trending, wedding, couple
(2333, 301), (2333, 305),             -- Post 1333: trending, street
(2334, 301), (2334, 304),             -- Post 1334: trending, landscape
(2335, 315), (2335, 302),             -- Post 1335: family, portrait
(2336, 305), (2336, 317),             -- Post 1336: street, night
(2337, 304), (2337, 306),             -- Post 1337: landscape, travel
(2338, 301), (2338, 307),             -- Post 1338: trending, couple
(2339, 312),                           -- Post 1339: architecture
(2340, 315), (2340, 311),             -- Post 1340: family, lifestyle

-- Month / Last week batch tags
(2341, 301), (2341, 302), (2341, 309), -- Post 1341: trending, portrait, studio
(2342, 301), (2342, 303), (2342, 310), -- Post 1342: trending, wedding, event
(2343, 301), (2343, 305), (2343, 304), -- Post 1343: trending, street, landscape
(2344, 301), (2344, 304), (2344, 317), -- Post 1344: trending, landscape, night
(2345, 315),                           -- Post 1345: family
(2346, 302), (2346, 318),             -- Post 1346: portrait, outdoor
(2347, 307), (2347, 303),             -- Post 1347: couple, wedding
(2348, 314), (2348, 316),             -- Post 1348: fashion, product
(2349, 312),                           -- Post 1349: architecture
(2350, 315), (2350, 311);             -- Post 1350: family, lifestyle

-- 6) INSERT INTERACTION COMMENTS
INSERT IGNORE INTO comments (
    id, post_id, user_id, content, parent_comment_id, like_count, reply_count, depth, created_at
) VALUES
(6001, 1301, 201, 'Ảnh trong trẻo quá anh ơi! Hà Nội sớm thu vẫn luôn mang lại cảm giác rất đặc biệt.', NULL, 12, 1, 0, CURRENT_TIMESTAMP - INTERVAL 4 MINUTE),
(6002, 1301, 206, 'Cảm ơn em nhé, hôm đó nắng đẹp nên ảnh lên màu dịu hẳn.', 6001, 2, 0, 1, CURRENT_TIMESTAMP - INTERVAL 3 MINUTE),
(6003, 1301, 202, 'Tông màu hoài niệm ấm áp ghê, nhìn rất thư thái.', NULL, 8, 0, 0, CURRENT_TIMESTAMP - INTERVAL 2 MINUTE),
(6004, 1302, 203, 'Đẹp xuất sắc chị ơi! Màu đồi thông lên thơ mộng dã man.', NULL, 15, 1, 0, CURRENT_TIMESTAMP - INTERVAL 10 MINUTE),
(6005, 1302, 207, 'Cảm ơn em yêu! Hôm nào lên Đà Lạt nhắn chị chụp cho một bộ nha.', 6004, 3, 0, 1, CURRENT_TIMESTAMP - INTERVAL 8 MINUTE),
(6006, 1303, 204, 'Mẫu diễn tốt, layout lên hình trông sang và đậm chất thời trang lắm bác.', NULL, 5, 0, 0, CURRENT_TIMESTAMP - INTERVAL 20 MINUTE),
(6007, 1304, 205, 'Huế mộng mơ dưới màn mưa... Góc ảnh trầm mặc rất đúng chất Huế xưa cổ kính.', NULL, 14, 0, 0, CURRENT_TIMESTAMP - INTERVAL 40 MINUTE)
ON DUPLICATE KEY UPDATE
    content = VALUES(content),
    like_count = VALUES(like_count);

-- 7) INSERT INTERACTION LIKES (Junction table records)
INSERT IGNORE INTO likes (id, user_id, post_id, created_at) VALUES
(7001, 201, 1301, CURRENT_TIMESTAMP - INTERVAL 4 MINUTE),
(7002, 202, 1301, CURRENT_TIMESTAMP - INTERVAL 3 MINUTE),
(7003, 203, 1301, CURRENT_TIMESTAMP - INTERVAL 2 MINUTE),
(7004, 201, 1302, CURRENT_TIMESTAMP - INTERVAL 11 MINUTE),
(7005, 202, 1302, CURRENT_TIMESTAMP - INTERVAL 10 MINUTE),
(7006, 203, 1302, CURRENT_TIMESTAMP - INTERVAL 9 MINUTE),
(7007, 204, 1303, CURRENT_TIMESTAMP - INTERVAL 22 MINUTE),
(7008, 205, 1303, CURRENT_TIMESTAMP - INTERVAL 21 MINUTE),
(7009, 201, 1304, CURRENT_TIMESTAMP - INTERVAL 44 MINUTE),
(7010, 205, 1304, CURRENT_TIMESTAMP - INTERVAL 42 MINUTE);

SET FOREIGN_KEY_CHECKS = 1;
