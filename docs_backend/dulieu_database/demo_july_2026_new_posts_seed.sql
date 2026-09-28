USE instagallery;
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- =========================================================================
-- RICH ENRICHMENT DEMO SEED - JULY 2026 (UPDATED WITH 30+ HIGH-ENGAGEMENT POSTS)
-- Adds new client users, new photographers with portfolios, 
-- and 30+ highly-recent social posts across all categories.
-- High likes ensure they rank in Page 1 (top 80 explore posts) for client-side filtering.
-- Password is username + 123!. Admin is the only account that uses Admin@123.
-- =========================================================================

SET FOREIGN_KEY_CHECKS = 0;

-- 1) INSERT NEW CLIENT USERS (IDs 201 - 205)
INSERT IGNORE INTO users (
    id, username, email, password_hash, full_name, profile_picture_url, bio,
    location, role, user_type, is_active, is_verified, provider, is_private,
    follower_count, following_count, post_count
) VALUES
(201, 'hoangnam', 'hoangnam@gmail.com', '$2a$10$i7h0X/VeB7QGAV5dx1X5BObm20vn9WAYWyjXU51KRM8EqF3z9I/Hm', 'Hoàng Nam', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150', 'Thích chụp ảnh đường phố Hà Nội, thích cà phê vỉa hè và lưu giữ góc phố thân quen.', 'Ha Noi', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 180, 95, 0),
(202, 'buibichthuy', 'buibichthuy@gmail.com', '$2a$10$AeAuQe2JJD/Qy952T7j/meU2FI/AzMYAIiyXAXi0Ii0Ft7m2YZYyW', 'Bùi Bích Thủy', 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150', 'Quan tâm ảnh chân dung nghệ thuật, muốn tìm thợ ảnh chụp album cá nhân.', 'Ha Noi', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 240, 120, 0),
(203, 'dophuonganh', 'dophuonganh@gmail.com', '$2a$10$2AFbK4oBdgHd6qTbybvfb.iC/2GmPPKONadWF4mUVufFRKcjOWZvS', 'Đỗ Phương Anh', 'https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=150', 'Mê du lịch và chụp ảnh couple outdoor ở Đà Lạt. Cần tìm thợ ảnh có tâm.', 'Da Lat', 'USER', 'CLIENT', 1, 1, 'LOCAL', 0, 310, 155, 0),
(204, 'hoquocanh', 'hoquocanh@gmail.com', '$2a$10$XsDryFr3TlRjem8OsVkPtuv6rtp9030FGN3nQkgtT5WCuTtLvJv0i', 'Hồ Quốc Anh', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150', 'Sáng lập local brand tại Sài Gòn, chuyên tìm photographer chụp lookbook sản phẩm.', 'Ho Chi Minh City', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 140, 78, 0),
(205, 'ngolanhuong', 'ngolanhuong@gmail.com', '$2a$10$WFxXnHITxuWJl429JQzUrOuBqkZBeLy0rpioGSczV98/ni6K.Wu8a', 'Ngô Lan Hương', 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150', 'Thích lưu giữ những khoảnh khắc gia đình nhỏ ấm áp và bình dị.', 'Da Nang', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 215, 110, 0)
ON DUPLICATE KEY UPDATE
    username = VALUES(username),
    email = VALUES(email),
    password_hash = VALUES(password_hash),
    full_name = VALUES(full_name),
    bio = VALUES(bio),
    location = VALUES(location);

-- 2) INSERT NEW PHOTOGRAPHERS (IDs 206 - 210)
INSERT IGNORE INTO users (
    id, username, email, password_hash, full_name, profile_picture_url, bio,
    location, role, user_type, is_active, is_verified, provider, is_private,
    follower_count, following_count, post_count
) VALUES
(206, 'duongducduy', 'duongducduy@gmail.com', '$2a$10$ydwVZ4Nx62HIyMBYDR9CZubQGzLX9rrYw9SL2IJkWli/An6fmuwQq', 'Dương Đức Duy', 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150', 'Nhiếp ảnh gia chuyên chân dung tự nhiên, phóng sự đường phố và ảnh thu Hà Nội trầm mặc.', 'Ha Noi', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1520, 180, 20),
(207, 'vuhuuphuc', 'vuhuuphuc@gmail.com', '$2a$10$Je349boCeLFmwVzGj5echub1yGQ8Ly13xZs55yqS0dEt0ebs94fHK', 'Vũ Hữu Phúc', 'https://images.unsplash.com/photo-1508214751196-bcfd4ca60f91?w=150', 'Nhận chụp couple outdoor Đà Lạt, phong cách nhẹ nhàng, thơ mộng, lãng mạn.', 'Da Lat', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1840, 210, 20),
(208, 'nguyenminhquan', 'nguyenminhquan@gmail.com', '$2a$10$Khg57tPWL8DQh4jKNCNTHe14BsFY8As4.aOSHbW3eK3pRg9HTK78G', 'Nguyễn Minh Quân', 'https://images.unsplash.com/photo-1500048993953-d23a436266cf?w=150', 'Studio chuyên nghiệp cho local brand, lookbook sản phẩm và ảnh thời trang tại Sài Gòn.', 'Ho Chi Minh City', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 2150, 230, 20),
(209, 'trantruclam', 'trantruclam@gmail.com', '$2a$10$ztPHZFGPBAQjiXNAvE26We8mSko43AHw43qn1D99VBKVUZszrjHhi', 'Trần Trúc Lâm', 'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=150', 'Kể câu chuyện văn hóa, nghệ thuật cố đô qua các bộ ảnh phục cổ và fine art tại Huế.', 'Hue', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1310, 140, 20),
(210, 'lephuongvy', 'lephuongvy@gmail.com', '$2a$10$2jXqd/gbNFcLsi21OFYqRugFRGtGqhKXRJr63qsAuUVIgVFINtSoq', 'Lê Phương Vy', 'https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=150', 'Lưu giữ nụ cười trẻ thơ và khoảnh khắc gia đình nhỏ đón bình minh trên biển Đà Nẵng.', 'Da Nang', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1690, 175, 20)
ON DUPLICATE KEY UPDATE
    username = VALUES(username),
    email = VALUES(email),
    password_hash = VALUES(password_hash),
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

-- Users 211-250. Password is username + 123!
INSERT INTO users (
    id, username, email, password_hash, full_name, profile_picture_url, bio,
    location, role, user_type, is_active, is_verified, provider, is_private,
    follower_count, following_count, post_count
) VALUES
(211, 'hoangphuongvy', 'hoangphuongvy@gmail.com', '$2a$10$JewhntoaCoXwD18VvD0YAeBbDDCdYWE9Tcm3uwaBDUV4xxsRbuw4.', 'Hoàng Phương Vy', 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80', 'Muốn lưu lại ảnh cưới tối giản và khoảnh khắc đời thường.', 'Da Nang', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 303, 77, 0),
(212, 'phanhuuphuc', 'phanhuuphuc@gmail.com', '$2a$10$9IYzMDo7GxdcrKCyCPm1ZOy2ycJ3JjXOO1Q6icuNVuFkOTnwF/Jui', 'Phan Hữu Phúc', 'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d', 'Thích ảnh đường phố, quán cà phê và những buổi chiều có nắng.', 'Ho Chi Minh City', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 316, 84, 0),
(213, 'phanphuongvy', 'phanphuongvy@gmail.com', '$2a$10$PpgiIkyt.r09a3CgYDdbeei0KJ1Tq2Z6W.0RpKx9xDeCperm8bzJu', 'Phan Phương Vy', 'https://images.unsplash.com/photo-1534528741775-53994a69daeb', 'Hay tìm nhiếp ảnh gia cho ảnh gia đình và chuyến đi cuối tuần.', 'Hue', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 329, 91, 0),
(214, 'vuphuongvy', 'vuphuongvy@gmail.com', '$2a$10$oVBCF/xmV3wtH7RGa.8kpeFqyJcvtIhuFn05hgtcL9oluWue31Y1O', 'Vũ Phương Vy', 'https://images.unsplash.com/photo-1524504388940-b1c1722653e1', 'Mê ảnh phong cảnh, biển và các bộ ảnh màu trong trẻo.', 'Da Lat', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 342, 98, 0),
(215, 'danghuuphuc', 'danghuuphuc@gmail.com', '$2a$10$sLLrYbY7yJoN.QrkbljPeugKxl3VLzmSG0/qgl0R3DyiO2hG5WdmC', 'Đặng Hữu Phúc', 'https://images.unsplash.com/photo-1547425260-76bcadfb4f2c', 'Muốn lưu lại ảnh cưới tối giản và khoảnh khắc đời thường.', 'Nha Trang', 'USER', 'CLIENT', 1, 1, 'LOCAL', 0, 355, 105, 0),
(216, 'buihuuphuc', 'buihuuphuc@gmail.com', '$2a$10$p84qFkysYMBOmCptfMK.6uZYHRSbUocVVJ5C/4RRImlKsCV5VVDVG', 'Bùi Hữu Phúc', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e', 'Thích ảnh đường phố, quán cà phê và những buổi chiều có nắng.', 'Hoi An', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 88, 112, 0),
(217, 'hohaianh', 'hohaianh@gmail.com', '$2a$10$0J7qMQaJj62MzBh0esP48unJ7gTJv6crYTZV6NNbOusYrEaLH3KSy', 'Hồ Hải Anh', 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80', 'Hay tìm nhiếp ảnh gia cho ảnh gia đình và chuyến đi cuối tuần.', 'Can Tho', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 101, 119, 0),
(218, 'dophuongvy', 'dophuongvy@gmail.com', '$2a$10$yRb818Np33n/gmGxeMcb1uNOb7cIlS5NIQl8u12Ca514cTl2mdty6', 'Đỗ Phương Vy', 'https://images.unsplash.com/photo-1544005313-94ddf0286df2', 'Mê ảnh phong cảnh, biển và các bộ ảnh màu trong trẻo.', 'Sa Pa', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 114, 126, 0),
(219, 'ngohuuphuc', 'ngohuuphuc@gmail.com', '$2a$10$wEJxmvd0YE8FdQkUYV/KI.B/HuqF9AG3tef7K9nE/7gL1I5VsFLFO', 'Ngô Hữu Phúc', 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde', 'Muốn lưu lại ảnh cưới tối giản và khoảnh khắc đời thường.', 'Phu Quoc', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 127, 133, 0),
(220, 'ngophuongvy', 'ngophuongvy@gmail.com', '$2a$10$5dgWvfHInBlGqLkCYnqJPO6QY5NihDlKAP6Ki/2sk8B.4NFwSg9hO', 'Ngô Phương Vy', 'https://images.unsplash.com/photo-1524504388940-b1c1722653e1', 'Thích ảnh đường phố, quán cà phê và những buổi chiều có nắng.', 'Ha Noi', 'USER', 'CLIENT', 1, 1, 'LOCAL', 0, 140, 140, 0),
(221, 'lyhuuphuc', 'lyhuuphuc@gmail.com', '$2a$10$fz5z.IODr0CiSnqlpZYEG.0I5QQjA6buBYnh5bO32vbmBGwTTDi.O', 'Lý Hữu Phúc', 'https://images.unsplash.com/photo-1547425260-76bcadfb4f2c', 'Hay tìm nhiếp ảnh gia cho ảnh gia đình và chuyến đi cuối tuần.', 'Da Nang', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 153, 147, 0),
(222, 'lyphuongvy', 'lyphuongvy@gmail.com', '$2a$10$2frSURNJZulIDiT1kJePKObbdklXg6aEXiFSRE8GnskMb.M2YyUQG', 'Lý Phương Vy', 'https://images.unsplash.com/photo-1494790108377-be9c29b29330', 'Mê ảnh phong cảnh, biển và các bộ ảnh màu trong trẻo.', 'Ho Chi Minh City', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 166, 154, 0),
(223, 'lytuankiet', 'lytuankiet@gmail.com', '$2a$10$oGBuEuRzrs8JjY.3L8ns1eAAbGNNmnphEXRYI7Mqx4wT/KExKZfB6', 'Lý Tuấn Kiệt', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d', 'Muốn lưu lại ảnh cưới tối giản và khoảnh khắc đời thường.', 'Hue', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 179, 161, 0),
(224, 'tranhuuphuc', 'tranhuuphuc@gmail.com', '$2a$10$tilJRFyCuzc8xkbKuigdD.hkIuhVVSw.ua/Cgox3AIgyvdL7uNEY2', 'Trần Hữu Phúc', 'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d', 'Thích ảnh đường phố, quán cà phê và những buổi chiều có nắng.', 'Da Lat', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 192, 168, 0),
(225, 'lehuuphuc', 'lehuuphuc@gmail.com', '$2a$10$p32d0DHqRxWxC.LDpLxQtuy0B6eiVeK5psGP3RyyQKvkiutAL8rzG', 'Lê Hữu Phúc', 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde', 'Hay tìm nhiếp ảnh gia cho ảnh gia đình và chuyến đi cuối tuần.', 'Nha Trang', 'USER', 'CLIENT', 1, 1, 'LOCAL', 0, 205, 175, 0),
(226, 'phamhuuphuc', 'phamhuuphuc@gmail.com', '$2a$10$1xPsJ1Y83A3JqbdhLQydmezAl0/Iim7yfwi/gkBrWz9Dr.27EM8OW', 'Phạm Hữu Phúc', 'https://images.unsplash.com/photo-1527980965255-d3b416303d12', 'Mê ảnh phong cảnh, biển và các bộ ảnh màu trong trẻo.', 'Hoi An', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 218, 182, 0),
(227, 'phamphuongvy', 'phamphuongvy@gmail.com', '$2a$10$NRiXXPSGH3hYxmjKFrTRx.WGbEVioZQCLLSG3OtwvpTuywQP4zbKS', 'Phạm Phương Vy', 'https://images.unsplash.com/photo-1508214751196-bcfd4ca60f91', 'Muốn lưu lại ảnh cưới tối giản và khoảnh khắc đời thường.', 'Can Tho', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 231, 189, 0),
(228, 'huynhhuuphuc', 'huynhhuuphuc@gmail.com', '$2a$10$iAmGM1pN8rRTBJjJP9RDRO4bMj7LpQY9TPYPfZyqG/lvwvM4L99cK', 'Huỳnh Hữu Phúc', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e', 'Thích ảnh đường phố, quán cà phê và những buổi chiều có nắng.', 'Sa Pa', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 244, 196, 0),
(229, 'vuhaianh', 'vuhaianh@gmail.com', '$2a$10$LqFqtB7oGzUnV1venzonOORyk6C4Hui5jQePGbvZpCi5B/21HMqpm', 'Vũ Hải Anh', 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80', 'Hay tìm nhiếp ảnh gia cho ảnh gia đình và chuyến đi cuối tuần.', 'Phu Quoc', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 257, 203, 0),
(230, 'vohaianh', 'vohaianh@gmail.com', '$2a$10$fcaV58J.WPhDprag9xxyU.WWFO8i/.i4YLAhmQBuIO4LjuhJCf69W', 'Võ Hải Anh', 'https://images.unsplash.com/photo-1544005313-94ddf0286df2', 'Mê ảnh phong cảnh, biển và các bộ ảnh màu trong trẻo.', 'Ha Noi', 'USER', 'CLIENT', 1, 1, 'LOCAL', 0, 270, 210, 0),
(231, 'vohuuphuc', 'vohuuphuc@gmail.com', '$2a$10$lstotTS9Cfxgdizd159ByOxMdqdYQJ6pm5tEZmOZwmTNC7d7KErku', 'Võ Hữu Phúc', 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde', 'Muốn lưu lại ảnh cưới tối giản và khoảnh khắc đời thường.', 'Da Nang', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 283, 217, 0),
(232, 'buihaianh', 'buihaianh@gmail.com', '$2a$10$X5sfTVMRSGuh7u./8Xk1b.n631Jo44Q4OD2F7txbuyV7x4B3YeT2C', 'Bùi Hải Anh', 'https://images.unsplash.com/photo-1524504388940-b1c1722653e1', 'Thích ảnh đường phố, quán cà phê và những buổi chiều có nắng.', 'Ho Chi Minh City', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 296, 44, 0),
(233, 'dohaianh', 'dohaianh@gmail.com', '$2a$10$wBYmHcPZzu0LKMdJztnOVe.X4YqRWgYVl7d/RDTONYfysMEls2fWe', 'Đỗ Hải Anh', 'https://images.unsplash.com/photo-1508214751196-bcfd4ca60f91', 'Hay tìm nhiếp ảnh gia cho ảnh gia đình và chuyến đi cuối tuần.', 'Hue', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 309, 51, 0),
(234, 'ngoquocbao', 'ngoquocbao@gmail.com', '$2a$10$qs1v1GCQuv/aK94nTY/DFe8meXd9/SCJvDdFem/RXxrqxCAsg.8ES', 'Ngô Quốc Bảo', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e', 'Mê ảnh phong cảnh, biển và các bộ ảnh màu trong trẻo.', 'Da Lat', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 322, 58, 0),
(235, 'hohuuphuc', 'hohuuphuc@gmail.com', '$2a$10$qCVq1IXe8bfYznY.xjcsR.u04gMH9Ji9p7sIPyKkbIPpxUNhkX3eu', 'Hồ Hữu Phúc', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d', 'Muốn lưu lại ảnh cưới tối giản và khoảnh khắc đời thường.', 'Nha Trang', 'USER', 'CLIENT', 1, 1, 'LOCAL', 0, 335, 65, 0),
(236, 'duonghaianh', 'duonghaianh@gmail.com', '$2a$10$131dYg3PMHLDRJojVcwTyeqIV/4xTPIhLKUQgK9Ir9FK/MgzPT1xS', 'Dương Hải Anh', 'https://images.unsplash.com/photo-1544005313-94ddf0286df2', 'Nhận chụp chân dung tự nhiên, ảnh cặp đôi và kỷ niệm gia đình.', 'Hoi An', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 2712, 72, 0),
(237, 'duonghuuphuc', 'duonghuuphuc@gmail.com', '$2a$10$OvAk2pv4Vpl5j9xPAbukA.46CO8CUubdM7jIRNp6Si9RwILPLah2e', 'Dương Hữu Phúc', 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde', 'Chụp ảnh cưới, lễ hỏi và các buổi gặp mặt thân mật.', 'Can Tho', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 2729, 79, 0),
(238, 'tranthaonguyen', 'tranthaonguyen@gmail.com', '$2a$10$11dk.Irvalo4kTp5Ur.KKurZtRJDZKhycfGfkEDO0ZhR.YUUA4TxC', 'Trần Thảo Nguyên', 'https://images.unsplash.com/photo-1524504388940-b1c1722653e1', 'Chuyên ảnh du lịch, lifestyle và chân dung ngoài trời.', 'Sa Pa', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 2746, 86, 0),
(239, 'nguyenhaianh', 'nguyenhaianh@gmail.com', '$2a$10$e73vcnxBMrXOVJrI2KA.k.JdZ09pWE94SbCEfwPYmBToEJmkGMvIq', 'Nguyễn Hải Anh', 'https://images.unsplash.com/photo-1508214751196-bcfd4ca60f91', 'Chụp sự kiện, lookbook và những câu chuyện đời thường.', 'Phu Quoc', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 2763, 93, 0),
(240, 'nguyenhuuphuc', 'nguyenhuuphuc@gmail.com', '$2a$10$d/EeIp9pntirwP6KVWxic.MvaRlOVoSug2L0Y1uECto05sbvbHHm.', 'Nguyễn Hữu Phúc', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e', 'Nhận chụp chân dung tự nhiên, ảnh cặp đôi và kỷ niệm gia đình.', 'Ha Noi', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 2780, 100, 0),
(241, 'phamquocbao', 'phamquocbao@gmail.com', '$2a$10$6v69DKmEhPHkkGTR2LW9yu9qo2v2OcZPRIfAbvWRXf01GYIbEY.NO', 'Phạm Quốc Bảo', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d', 'Chụp ảnh cưới, lễ hỏi và các buổi gặp mặt thân mật.', 'Da Nang', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 2797, 107, 0),
(242, 'phamhaianh', 'phamhaianh@gmail.com', '$2a$10$Rdc2.EF4MtAvJxWlr8NgGO5LhXJr7IHnT.XWGIYfgJ.cPKwQpHHvW', 'Phạm Hải Anh', 'https://images.unsplash.com/photo-1544005313-94ddf0286df2', 'Chuyên ảnh du lịch, lifestyle và chân dung ngoài trời.', 'Ho Chi Minh City', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 2814, 114, 0),
(243, 'hoanghaianh', 'hoanghaianh@gmail.com', '$2a$10$MezCVk0R15EKiFuFprGWXOJT5//FGj.t.vrDNlYfz8gPlN7HYmSMe', 'Hoàng Hải Anh', 'https://images.unsplash.com/photo-1534528741775-53994a69daeb', 'Chụp sự kiện, lookbook và những câu chuyện đời thường.', 'Hue', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 2831, 121, 0),
(244, 'hoanghuuphuc', 'hoanghuuphuc@gmail.com', '$2a$10$RMXVf8lgylII.woFSxEnCu2InQ0gXMu2VHhtc5RVbYa/EQ.GVIH1C', 'Hoàng Hữu Phúc', 'https://images.unsplash.com/photo-1527980965255-d3b416303d12', 'Nhận chụp chân dung tự nhiên, ảnh cặp đôi và kỷ niệm gia đình.', 'Da Lat', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 2848, 128, 0),
(245, 'phanhaianh', 'phanhaianh@gmail.com', '$2a$10$rCEPwi72vT4XucV5.pig/uqkylmQzc2josFXziuaPCNXbqRv6eGlG', 'Phan Hải Anh', 'https://images.unsplash.com/photo-1508214751196-bcfd4ca60f91', 'Chụp ảnh cưới, lễ hỏi và các buổi gặp mặt thân mật.', 'Nha Trang', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 2865, 135, 0),
(246, 'voquocbao', 'voquocbao@gmail.com', '$2a$10$jDYiTp2KLL9jCvzwLQ0vmerZb1322cqrHaVYy1H323PGVY3/sWKC.', 'Võ Quốc Bảo', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e', 'Chuyên ảnh du lịch, lifestyle và chân dung ngoài trời.', 'Hoi An', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 2882, 142, 0),
(247, 'dangquocbao', 'dangquocbao@gmail.com', '$2a$10$mLIpBhoo15ppjD3W4tmIxO5M/Nmt1KUjvWZzApryp.GCAaqd6SHJK', 'Đặng Quốc Bảo', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d', 'Chụp sự kiện, lookbook và những câu chuyện đời thường.', 'Can Tho', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 2899, 149, 0),
(248, 'danghaianh', 'danghaianh@gmail.com', '$2a$10$WRJXvWtbVzBvztZNf5J8XugicrsUIK5wQfuXG.BzdCxMORrZOTsfS', 'Đặng Hải Anh', 'https://images.unsplash.com/photo-1544005313-94ddf0286df2', 'Nhận chụp chân dung tự nhiên, ảnh cặp đôi và kỷ niệm gia đình.', 'Sa Pa', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 2916, 156, 0),
(249, 'doquocbao', 'doquocbao@gmail.com', '$2a$10$kFl2a/CGkJ0pKTwY.Hn9meHZ6Y0P.WBc6OxR0kvyy3iX0ISwy8LeK', 'Đỗ Quốc Bảo', 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde', 'Chụp ảnh cưới, lễ hỏi và các buổi gặp mặt thân mật.', 'Phu Quoc', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 2933, 163, 0),
(250, 'hoquocbao', 'hoquocbao@gmail.com', '$2a$10$w4JiG937nNXo5DUnBNUsiumcIzPfHfiW8OYw0rWeCjgdqVkLAUAj6', 'Hồ Quốc Bảo', 'https://images.unsplash.com/photo-1527980965255-d3b416303d12', 'Chuyên ảnh du lịch, lifestyle và chân dung ngoài trời.', 'Ha Noi', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 2950, 170, 0)
ON DUPLICATE KEY UPDATE
    username = VALUES(username),
    email = VALUES(email),
    password_hash = VALUES(password_hash),
    full_name = VALUES(full_name),
    bio = VALUES(bio),
    location = VALUES(location),
    user_type = VALUES(user_type);

INSERT INTO portfolios (
    id, user_id, title, description, specialties, hourly_rate, currency,
    service_area, is_available, rating_avg, review_count
) VALUES
(236, 236, 'Ảnh của Dương Hải Anh', 'Nhận chụp chân dung, cặp đôi và kỷ niệm tại Hoi An.', '["Portrait","Lifestyle","Outdoor"]', 700000.00, 'VND', 'Hoi An', 1, 4.80, 12),
(237, 237, 'Ảnh của Dương Hữu Phúc', 'Nhận chụp chân dung, cặp đôi và kỷ niệm tại Can Tho.', '["Portrait","Lifestyle","Outdoor"]', 750000.00, 'VND', 'Can Tho', 1, 4.80, 12),
(238, 238, 'Ảnh của Trần Thảo Nguyên', 'Nhận chụp chân dung, cặp đôi và kỷ niệm tại Sa Pa.', '["Portrait","Lifestyle","Outdoor"]', 800000.00, 'VND', 'Sa Pa', 1, 4.80, 12),
(239, 239, 'Ảnh của Nguyễn Hải Anh', 'Nhận chụp chân dung, cặp đôi và kỷ niệm tại Phu Quoc.', '["Portrait","Lifestyle","Outdoor"]', 850000.00, 'VND', 'Phu Quoc', 1, 4.80, 12),
(240, 240, 'Ảnh của Nguyễn Hữu Phúc', 'Nhận chụp chân dung, cặp đôi và kỷ niệm tại Ha Noi.', '["Portrait","Lifestyle","Outdoor"]', 500000.00, 'VND', 'Ha Noi', 1, 4.80, 12),
(241, 241, 'Ảnh của Phạm Quốc Bảo', 'Nhận chụp chân dung, cặp đôi và kỷ niệm tại Da Nang.', '["Portrait","Lifestyle","Outdoor"]', 550000.00, 'VND', 'Da Nang', 1, 4.80, 12),
(242, 242, 'Ảnh của Phạm Hải Anh', 'Nhận chụp chân dung, cặp đôi và kỷ niệm tại Ho Chi Minh City.', '["Portrait","Lifestyle","Outdoor"]', 600000.00, 'VND', 'Ho Chi Minh City', 1, 4.80, 12),
(243, 243, 'Ảnh của Hoàng Hải Anh', 'Nhận chụp chân dung, cặp đôi và kỷ niệm tại Hue.', '["Portrait","Lifestyle","Outdoor"]', 650000.00, 'VND', 'Hue', 1, 4.80, 12),
(244, 244, 'Ảnh của Hoàng Hữu Phúc', 'Nhận chụp chân dung, cặp đôi và kỷ niệm tại Da Lat.', '["Portrait","Lifestyle","Outdoor"]', 700000.00, 'VND', 'Da Lat', 1, 4.80, 12),
(245, 245, 'Ảnh của Phan Hải Anh', 'Nhận chụp chân dung, cặp đôi và kỷ niệm tại Nha Trang.', '["Portrait","Lifestyle","Outdoor"]', 750000.00, 'VND', 'Nha Trang', 1, 4.80, 12),
(246, 246, 'Ảnh của Võ Quốc Bảo', 'Nhận chụp chân dung, cặp đôi và kỷ niệm tại Hoi An.', '["Portrait","Lifestyle","Outdoor"]', 800000.00, 'VND', 'Hoi An', 1, 4.80, 12),
(247, 247, 'Ảnh của Đặng Quốc Bảo', 'Nhận chụp chân dung, cặp đôi và kỷ niệm tại Can Tho.', '["Portrait","Lifestyle","Outdoor"]', 850000.00, 'VND', 'Can Tho', 1, 4.80, 12),
(248, 248, 'Ảnh của Đặng Hải Anh', 'Nhận chụp chân dung, cặp đôi và kỷ niệm tại Sa Pa.', '["Portrait","Lifestyle","Outdoor"]', 500000.00, 'VND', 'Sa Pa', 1, 4.80, 12),
(249, 249, 'Ảnh của Đỗ Quốc Bảo', 'Nhận chụp chân dung, cặp đôi và kỷ niệm tại Phu Quoc.', '["Portrait","Lifestyle","Outdoor"]', 550000.00, 'VND', 'Phu Quoc', 1, 4.80, 12),
(250, 250, 'Ảnh của Hồ Quốc Bảo', 'Nhận chụp chân dung, cặp đôi và kỷ niệm tại Ha Noi.', '["Portrait","Lifestyle","Outdoor"]', 600000.00, 'VND', 'Ha Noi', 1, 4.80, 12)
ON DUPLICATE KEY UPDATE
    user_id = VALUES(user_id),
    title = VALUES(title),
    description = VALUES(description);

SET FOREIGN_KEY_CHECKS = 1;
