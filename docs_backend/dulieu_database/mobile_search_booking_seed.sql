USE instagallery;
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- Mobile Search + Booking seed.
-- Run after the normal schema/database has been created.
-- Photographer passwords follow: email name + 123!
-- Example: photohoan@gmail.com -> photohoan123!

SET FOREIGN_KEY_CHECKS = 0;

INSERT INTO users (
    id, username, email, password_hash, full_name, profile_picture_url, bio,
    location, role, user_type, is_active, is_verified, provider, is_private,
    follower_count, following_count, post_count
) VALUES
(101, 'photo_tranquang', 'tranquang.photo@instagallery.local', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TsphxXK', 'Tran Quang Photo', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e', 'Landscape photographer based in Ha Noi.', 'Ha Noi', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 3280, 214, 3),
(102, 'photo_nguyentu', 'nguyentu.photo@instagallery.local', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TsphxXK', 'Nguyen Tu Photo', 'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d', 'Street and editorial photographer.', 'Da Nang', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 2450, 188, 2),
(103, 'photo_lekhanh', 'lekhanh.photo@instagallery.local', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TsphxXK', 'Le Khanh Photo', 'https://images.unsplash.com/photo-1494790108377-be9c29b29330', 'Travel and lifestyle photographer.', 'Hoi An', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1890, 160, 2),
(104, 'photo_hoangnam', 'hoangnam.photo@instagallery.local', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TsphxXK', 'Hoang Nam Photo', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d', 'Wedding photographer for natural moments.', 'Ha Noi', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 4120, 201, 3),
(105, 'photo_thuha', 'thuha.photo@instagallery.local', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TsphxXK', 'Thu Ha Photo', 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80', 'Portrait photographer with soft natural light.', 'Ha Noi', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 2760, 143, 2),
(106, 'photo_minhtam', 'minhtam.photo@instagallery.local', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TsphxXK', 'Minh Tam Photo', 'https://images.unsplash.com/photo-1507101105822-7472b28e22ac', 'Architecture and night city photographer.', 'Ho Chi Minh City', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 2210, 119, 2),
(107, 'photo_thaovy', 'thaovy.photo@instagallery.local', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TsphxXK', 'Thao Vy Photo', 'https://images.unsplash.com/photo-1544005313-94ddf0286df2', 'Couple and elopement photographer.', 'Da Lat', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 3570, 181, 2),
(108, 'photo_kai', 'kai.photo@instagallery.local', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TsphxXK', 'Kai Nguyen Photo', 'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d', 'Fine art travel photography.', 'Paris', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 2980, 137, 2)
ON DUPLICATE KEY UPDATE
    full_name = VALUES(full_name),
    profile_picture_url = VALUES(profile_picture_url),
    bio = VALUES(bio),
    location = VALUES(location),
    user_type = VALUES(user_type),
    follower_count = VALUES(follower_count),
    following_count = VALUES(following_count),
    post_count = VALUES(post_count);

INSERT INTO users (
    id, username, email, password_hash, full_name, profile_picture_url, bio,
    location, role, user_type, is_active, is_verified, provider, is_private,
    follower_count, following_count, post_count
) VALUES
(109, 'photohoan', 'photohoan@gmail.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TsphxXK', 'Hoan Photo', 'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d', 'Outdoor portrait and event photographer.', 'Ha Noi', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1540, 96, 0),
(110, 'phototong', 'phototong@gmail.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TsphxXK', 'Tong Photo', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e', 'Wedding and couple photographer.', 'Ho Chi Minh City', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1730, 112, 0),
(111, 'photolinh', 'photolinh@gmail.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TsphxXK', 'Linh Photo', 'https://images.unsplash.com/photo-1494790108377-be9c29b29330', 'Portrait photographer with clean natural tones.', 'Da Nang', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1688, 105, 0),
(112, 'photoduy', 'photoduy@gmail.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TsphxXK', 'Duy Photo', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d', 'Street, fashion and editorial photographer.', 'Hue', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1420, 87, 0),
(113, 'photonam', 'photonam@gmail.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TsphxXK', 'Nam Photo', 'https://images.unsplash.com/photo-1507101105822-7472b28e22ac', 'Travel photographer for personal brands.', 'Nha Trang', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1980, 133, 0),
(114, 'photoan', 'photoan@gmail.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TsphxXK', 'An Photo', 'https://images.unsplash.com/photo-1544005313-94ddf0286df2', 'Family and lifestyle photographer.', 'Can Tho', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1290, 74, 0),
(115, 'photobao', 'photobao@gmail.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TsphxXK', 'Bao Photo', 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde', 'Landscape and drone photographer.', 'Da Lat', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 2210, 151, 0),
(116, 'photochi', 'photochi@gmail.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TsphxXK', 'Chi Photo', 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80', 'Beauty, portrait and studio photographer.', 'Ha Noi', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1875, 126, 0),
(117, 'photodan', 'photodan@gmail.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TsphxXK', 'Dan Photo', 'https://images.unsplash.com/photo-1527980965255-d3b416303d12', 'Documentary wedding photographer.', 'Hai Phong', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1644, 101, 0),
(118, 'photogiang', 'photogiang@gmail.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TsphxXK', 'Giang Photo', 'https://images.unsplash.com/photo-1508214751196-bcfd4ca60f91', 'Street portrait photographer.', 'Hoi An', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1495, 92, 0),
(119, 'photohai', 'photohai@gmail.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TsphxXK', 'Hai Photo', 'https://images.unsplash.com/photo-1504593811423-6dd665756598', 'Architecture and interior photographer.', 'Ho Chi Minh City', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1368, 89, 0),
(120, 'photokhanh', 'photokhanh@gmail.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TsphxXK', 'Khanh Photo', 'https://images.unsplash.com/photo-1547425260-76bcadfb4f2c', 'Fashion and lookbook photographer.', 'Da Nang', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 2112, 141, 0),
(121, 'photolong', 'photolong@gmail.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TsphxXK', 'Long Photo', 'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d', 'Event and concert photographer.', 'Ha Noi', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1742, 118, 0),
(122, 'photomai', 'photomai@gmail.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TsphxXK', 'Mai Photo', 'https://images.unsplash.com/photo-1494790108377-be9c29b29330', 'Fine art portrait photographer.', 'Hue', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1960, 121, 0),
(123, 'photonhi', 'photonhi@gmail.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TsphxXK', 'Nhi Photo', 'https://images.unsplash.com/photo-1517841905240-472988babdf9', 'Minimal product and portrait photographer.', 'Can Tho', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1308, 77, 0),
(124, 'photophuc', 'photophuc@gmail.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TsphxXK', 'Phuc Photo', 'https://images.unsplash.com/photo-1504257432389-52343af06ae3', 'Travel wedding photographer.', 'Da Lat', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 2320, 154, 0),
(125, 'photoquynh', 'photoquynh@gmail.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TsphxXK', 'Quynh Photo', 'https://images.unsplash.com/photo-1544005313-94ddf0286df2', 'Studio portrait and beauty photographer.', 'Ha Noi', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 2050, 134, 0),
(126, 'photoson', 'photoson@gmail.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TsphxXK', 'Son Photo', 'https://images.unsplash.com/photo-1519345182560-3f2917c472ef', 'Mountain and outdoor photographer.', 'Sa Pa', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 2480, 166, 0),
(127, 'phototrang', 'phototrang@gmail.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TsphxXK', 'Trang Photo', 'https://images.unsplash.com/photo-1534528741775-53994a69daeb', 'Wedding detail and bridal portrait photographer.', 'Ninh Binh', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 2170, 148, 0),
(128, 'photoyen', 'photoyen@gmail.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TsphxXK', 'Yen Photo', 'https://images.unsplash.com/photo-1524504388940-b1c1722653e1', 'Lifestyle and travel portrait photographer.', 'Phu Quoc', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1860, 117, 0)
ON DUPLICATE KEY UPDATE
    username = VALUES(username),
    email = VALUES(email),
    full_name = VALUES(full_name),
    profile_picture_url = VALUES(profile_picture_url),
    bio = VALUES(bio),
    location = VALUES(location),
    user_type = VALUES(user_type),
    follower_count = VALUES(follower_count),
    following_count = VALUES(following_count),
    post_count = VALUES(post_count);

INSERT INTO users (
    id, username, email, password_hash, full_name, profile_picture_url, bio,
    location, role, user_type, is_active, is_verified, provider, is_private,
    follower_count, following_count, post_count
) VALUES
(129, 'userlinhnguyen', 'userlinhnguyen@gmail.com', '$2a$12$uVtENvbETcWhGArrJb2BRuzk7mzKj07h0EiXhKxon0ffqAGcq/zZm', 'Linh Nguyễn', 'https://images.unsplash.com/photo-1494790108377-be9c29b29330', 'Thích ảnh du lịch và những góc quán cà phê yên tĩnh.', 'Ha Noi', 'USER', 'CLIENT', 1, 1, 'LOCAL', 0, 320, 180, 0),
(130, 'userminhanh', 'userminhanh@gmail.com', '$2a$12$N9p05oUKBiJ7Pw1o3wm8AuYGWws6OYSMakGRoNWkIqFX3obHTRuy.', 'Minh Anh', 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80', 'Hay lưu lại concept chân dung, beauty và studio tối giản.', 'Da Nang', 'USER', 'CLIENT', 1, 1, 'LOCAL', 0, 280, 165, 0),
(131, 'usertuananh', 'usertuananh@gmail.com', '$2a$12$nFBz2XX6TLKf5guFRr4TOe6Ag4koV1yJD0r5mPssuuxQSJuYBwMQm', 'Tuấn Anh', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d', 'Quan tâm ảnh đường phố, xe cộ và đời sống đô thị.', 'Ho Chi Minh City', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 190, 142, 0),
(132, 'userbaotran', 'userbaotran@gmail.com', '$2a$12$H8U4JMcjRnJKT8.JBYhA2.PGpgTq6i9RjgKAhfQOegzFxaeySZ.tG', 'Bảo Trân', 'https://images.unsplash.com/photo-1544005313-94ddf0286df2', 'Đang tìm nhiếp ảnh gia chụp ảnh gia đình và lifestyle.', 'Can Tho', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 210, 128, 0),
(133, 'userhoanglong', 'userhoanglong@gmail.com', '$2a$12$0DvOHv/YxE0oJYpgYnLG3edp2VS9UuIM8jIRzaHyL5xgHFVRSqSFS', 'Hoàng Long', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e', 'Mê ảnh phong cảnh, trekking và các chuyến đi nhiều mây.', 'Sa Pa', 'USER', 'CLIENT', 1, 1, 'LOCAL', 0, 410, 205, 0),
(134, 'userthuylinh', 'userthuylinh@gmail.com', '$2a$12$Ch8e0Q6WDTw7poHe.sT3aeN8FWdp0ZxULMnPLzEpbcwjGyLZ0Z6zu', 'Thùy Linh', 'https://images.unsplash.com/photo-1517841905240-472988babdf9', 'Thích lookbook, thời trang và ảnh profile cá nhân.', 'Hue', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 260, 156, 0),
(135, 'userdangquang', 'userdangquang@gmail.com', '$2a$12$ranqJnzu1tlQ0Kf.HMwJBOWoWOcGqn50LT/clqu/382JTZugcl7FG', 'Đăng Quang', 'https://images.unsplash.com/photo-1519345182560-3f2917c472ef', 'Theo dõi ảnh kiến trúc, nội thất và không gian tối giản.', 'Ho Chi Minh City', 'USER', 'CLIENT', 1, 1, 'LOCAL', 0, 175, 120, 0),
(136, 'userngochan', 'userngochan@gmail.com', '$2a$12$7tLF4jGFBKuNjyeuGctG9Ok/wPNNeYXyCTFwmhi6IhINv5UuJ8g8G', 'Ngọc Hân', 'https://images.unsplash.com/photo-1534528741775-53994a69daeb', 'Hay đặt lịch chụp ảnh beauty và portrait nhẹ nhàng.', 'Ha Noi', 'USER', 'CLIENT', 1, 1, 'LOCAL', 0, 345, 190, 0),
(137, 'uservietanh', 'uservietanh@gmail.com', '$2a$12$FIgbivES.iyjsVdjPIMpF.hdXQeY/FJGaEZ5EuM4Mp7rvSXZLbBgK', 'Việt Anh', 'https://images.unsplash.com/photo-1527980965255-d3b416303d12', 'Thích ảnh sự kiện, sân khấu nhỏ và những khung hình nhiều cảm xúc.', 'Hai Phong', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 150, 88, 0),
(138, 'userthanhmai', 'userthanhmai@gmail.com', '$2a$12$4zCDt6Pu3YeATAnmmVYkc.yODDn2ieTmA0Z4m/0CLylO3xDK7o5l.', 'Thanh Mai', 'https://images.unsplash.com/photo-1508214751196-bcfd4ca60f91', 'Lưu lại ý tưởng chụp ảnh cưới, du lịch và cặp đôi.', 'Da Lat', 'USER', 'CLIENT', 1, 1, 'LOCAL', 0, 390, 210, 0),
(139, 'userphamson', 'userphamson@gmail.com', '$2a$12$yfWFzbcWtt/1nXApzbVgkO8N3Uvsw6W2F21ZAdvFThoX.y5s0vR66', 'Phạm Sơn', 'https://images.unsplash.com/photo-1504593811423-6dd665756598', 'Quan tâm ảnh sản phẩm, thương mại và bố cục sạch.', 'Nha Trang', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 188, 111, 0),
(140, 'userlanhuong', 'userlanhuong@gmail.com', '$2a$12$/U.3MjyxyNdgU7atBGvOc.twaw/0fPKlEDbnICd4pxIp/c5kJPFUS', 'Lan Hương', 'https://images.unsplash.com/photo-1524504388940-b1c1722653e1', 'Thích ảnh biển, lifestyle và những bộ ảnh có màu trong trẻo.', 'Phu Quoc', 'USER', 'CLIENT', 1, 1, 'LOCAL', 0, 420, 230, 0),
(141, 'photohung', 'photohung@gmail.com', '$2a$12$P3tvFt72jRZhU4nGdm1XqObLhBFnsPpjWZcRDTs3YemqujppGnCtG', 'Hùng Photo', 'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d', 'Nhiếp ảnh gia ảnh cưới, sự kiện và cặp đôi tại Hà Nội.', 'Ha Noi', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 2360, 154, 0),
(142, 'photovy', 'photovy@gmail.com', '$2a$12$KxLTQdjfAd7aMldkFlDvtuyeuOjSrIDfj8byWu1qiJ9laIUNYlQoO', 'Vy Photo', 'https://images.unsplash.com/photo-1494790108377-be9c29b29330', 'Chuyên chân dung nữ, beauty và ảnh profile tự nhiên.', 'Da Nang', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 2180, 132, 0),
(143, 'photodat', 'photodat@gmail.com', '$2a$12$uzqUBbVhuNPlmA2cY8VYn.N.VrwiD8oGAJ6n2oASoiaGr/NtDmlTK', 'Đạt Photo', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d', 'Chụp street, night city và ảnh editorial đường phố.', 'Ho Chi Minh City', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1840, 118, 0),
(144, 'phototram', 'phototram@gmail.com', '$2a$12$502jd0u7TTXYryoP4dOoSOavEwlt2g/DWYeppMpUQ65kbbDsGYymq', 'Trâm Photo', 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80', 'Chụp gia đình, newborn và lifestyle tại miền Tây.', 'Can Tho', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1660, 97, 0),
(145, 'photokiet', 'photokiet@gmail.com', '$2a$12$9G6WDe.YSdiLQC1iPcksR.ya6Rh67otYWJLXLM.ExPLptIM1FuAQy', 'Kiệt Photo', 'https://images.unsplash.com/photo-1527980965255-d3b416303d12', 'Chuyên ảnh sản phẩm, lookbook và thương mại điện tử.', 'Ho Chi Minh City', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1920, 126, 0),
(146, 'photohuyen', 'photohuyen@gmail.com', '$2a$12$mV1SNrO/9FB2soszbUyXaeS890jLdmmxwgx3l6Wh1vLozIb5R/dem', 'Huyền Photo', 'https://images.unsplash.com/photo-1544005313-94ddf0286df2', 'Chụp ảnh du lịch, resort và lifestyle biển.', 'Nha Trang', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 2260, 145, 0),
(147, 'photothinh', 'photothinh@gmail.com', '$2a$12$hAdBGgEeeH4cAKHuoi6ZT.GLZv/ONMkr7/v/BTRII8IyNqCIEv/hS', 'Thịnh Photo', 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde', 'Chụp phong cảnh, flycam và outdoor cho các chuyến đi xa.', 'Da Lat', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 2540, 168, 0),
(148, 'photomy', 'photomy@gmail.com', '$2a$12$WkHocOmiVSoZheDUnnFNauG5cCH7Mow4SaOomU8gt3sYtnzHvpjFq', 'My Photo', 'https://images.unsplash.com/photo-1517841905240-472988babdf9', 'Chụp bridal, fine art portrait và ảnh cưới tối giản.', 'Hue', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 2070, 139, 0)
ON DUPLICATE KEY UPDATE
    username = VALUES(username),
    email = VALUES(email),
    full_name = VALUES(full_name),
    profile_picture_url = VALUES(profile_picture_url),
    bio = VALUES(bio),
    location = VALUES(location),
    user_type = VALUES(user_type),
    follower_count = VALUES(follower_count),
    following_count = VALUES(following_count),
    post_count = VALUES(post_count);

UPDATE users SET password_hash = CASE username
    WHEN 'photo_tranquang' THEN '$2a$12$i0kOGQby4ze/ivyEphdjZeXO/QVtgTVYc1PvBrjM2iePcA8Oh7LbK' -- tranquang.photo123!
    WHEN 'photo_nguyentu' THEN '$2a$12$JYHGa7IhMP4NIbEIiN41ZOTWZ/Ug60qFUxwv0mEepYvx5o8xDSNY2' -- nguyentu.photo123!
    WHEN 'photo_lekhanh' THEN '$2a$12$6hdorVJgtD.5Oc/3aIHRoeCys4bi6NZrfD4Hj5xSWG2wevYUipG/2' -- lekhanh.photo123!
    WHEN 'photo_hoangnam' THEN '$2a$12$vUltx95.pZYPeczb9EPWjuRj2Ych8AbrZgIULusGXrbXZniqSetMO' -- hoangnam.photo123!
    WHEN 'photo_thuha' THEN '$2a$12$93e6p6aGAu.Nus2cokhsRurbZq/j/BL0b1emObctYP08c2MtXLdg.' -- thuha.photo123!
    WHEN 'photo_minhtam' THEN '$2a$12$JNLumv9xXsGG8r6TOTyYtuoWN.TJk3pyivHETWYj89iYUOWYeBXKa' -- minhtam.photo123!
    WHEN 'photo_thaovy' THEN '$2a$12$uwr00KegXo8qeQT5uF6ZuexmRZGXijcYf8pSxdkZ9m8vXN6x5BNhe' -- thaovy.photo123!
    WHEN 'photo_kai' THEN '$2a$12$Qzepsk33rVHu37JjwqQ1e.mSl0ZSIajVstmUjEnXSzAo8kMHgOsCW' -- kai.photo123!
    WHEN 'photohoan' THEN '$2a$12$OiJwmUieckNssHz8WsKC/.lxzMBOJfKp5UsQita0XhGeH97G6LSUK' -- photohoan123!
    WHEN 'phototong' THEN '$2a$12$KcUnDTq.NLAVxZMcMxhY5.sxT06x3HGc0axqxvX.YlwHi/OSti4CK' -- phototong123!
    WHEN 'photolinh' THEN '$2a$12$iYz3/ocuzwxFPGGM6G9wiuKbeYITyu6FYGOJDzVK71s4TmUqCSYey' -- photolinh123!
    WHEN 'photoduy' THEN '$2a$12$rmzHLZhVrlgRt.qI7pQlT.A/gg0JieeDSGFMmDNMNFa.V0Z6d82tG' -- photoduy123!
    WHEN 'photonam' THEN '$2a$12$P4OkXSVNbq.k4d6RDRxjjO6YKgO4Z9Fw5F7MBY/37gPn7kpphFd6y' -- photonam123!
    WHEN 'photoan' THEN '$2a$12$j9zk9wChUD5VZ578uNxsZ.hfrwGI.FaXj4CMzQuzs4Wl33ciTjRly' -- photoan123!
    WHEN 'photobao' THEN '$2a$12$8PA07cOzB9Q9cewgYbL9Y.PUUxpznZ.elJa5XTi.FN3h.tA8U9tmC' -- photobao123!
    WHEN 'photochi' THEN '$2a$12$.DmPr4uZlUcdOu7NuXhwc.Qz4hvuLKyh/i.d0rj2qh.ta6XaVP1E.' -- photochi123!
    WHEN 'photodan' THEN '$2a$12$5/Y7CGeosDAy27XprbMCoONJ3htYccp4qsE/hNYjDKP/MKyNQ6WQ6' -- photodan123!
    WHEN 'photogiang' THEN '$2a$12$GTxJjdrnm8mReSfb/FShn.qMxRWbwCDs4Wfr1Ft6carHqTHqdsew6' -- photogiang123!
    WHEN 'photohai' THEN '$2a$12$WzpQ524fPvvAN.j0ktAlQ.h.Bx0zvSRp8QvWMkteFqOCBjvrzrA9G' -- photohai123!
    WHEN 'photokhanh' THEN '$2a$12$3Ob49wZ8xADkSImb.6uSZecKIq.gJ8/jDFt3vK/U9zb2w6VxBgiZ6' -- photokhanh123!
    WHEN 'photolong' THEN '$2a$12$R3Vt/Kdp5Lbxg.svXf0clexm8YfiwWxdT97L4j3tfRurHAGwO/2qy' -- photolong123!
    WHEN 'photomai' THEN '$2a$12$YlZ7/XAokXd8PcsghCYEZOZCkFd9uXpw84LY.kXtwMLbBLUDKRP8W' -- photomai123!
    WHEN 'photonhi' THEN '$2a$12$CcJHIjmKZiqfq.gRMwoD8OTa0tnZ3FNPIBP9iJ549S7t/qL0YFjbO' -- photonhi123!
    WHEN 'photophuc' THEN '$2a$12$um9IiMGMKz3pfuWc6v6CVOV4IT.8vhOznsV9ATeIDI3i4.cZFQVGi' -- photophuc123!
    WHEN 'photoquynh' THEN '$2a$12$HKscELYu/aYkmfTqmG4WS.UGop3H2i4JF5ISDd4W19doqyeFWUr6O' -- photoquynh123!
    WHEN 'photoson' THEN '$2a$12$45n2aDgLTjPLjEgpNCgiIO1aX.0MLkcXEP4agNFvPtbkSxa00A.fq' -- photoson123!
    WHEN 'phototrang' THEN '$2a$12$G3UBoBrTW1mEoRBTyS92ceMGctfdVPPyhKvQtHBKxKUNhs0dMRXgS' -- phototrang123!
    WHEN 'photoyen' THEN '$2a$12$Ag0QH5gHNn5UJRjx2NxYu.bVIC5ilBfNJxF6ul9Oqt8H3WtXrp8Wu' -- photoyen123!
    WHEN 'userlinhnguyen' THEN '$2a$12$uVtENvbETcWhGArrJb2BRuzk7mzKj07h0EiXhKxon0ffqAGcq/zZm' -- userlinhnguyen123!
    WHEN 'userminhanh' THEN '$2a$12$N9p05oUKBiJ7Pw1o3wm8AuYGWws6OYSMakGRoNWkIqFX3obHTRuy.' -- userminhanh123!
    WHEN 'usertuananh' THEN '$2a$12$nFBz2XX6TLKf5guFRr4TOe6Ag4koV1yJD0r5mPssuuxQSJuYBwMQm' -- usertuananh123!
    WHEN 'userbaotran' THEN '$2a$12$H8U4JMcjRnJKT8.JBYhA2.PGpgTq6i9RjgKAhfQOegzFxaeySZ.tG' -- userbaotran123!
    WHEN 'userhoanglong' THEN '$2a$12$0DvOHv/YxE0oJYpgYnLG3edp2VS9UuIM8jIRzaHyL5xgHFVRSqSFS' -- userhoanglong123!
    WHEN 'userthuylinh' THEN '$2a$12$Ch8e0Q6WDTw7poHe.sT3aeN8FWdp0ZxULMnPLzEpbcwjGyLZ0Z6zu' -- userthuylinh123!
    WHEN 'userdangquang' THEN '$2a$12$ranqJnzu1tlQ0Kf.HMwJBOWoWOcGqn50LT/clqu/382JTZugcl7FG' -- userdangquang123!
    WHEN 'userngochan' THEN '$2a$12$7tLF4jGFBKuNjyeuGctG9Ok/wPNNeYXyCTFwmhi6IhINv5UuJ8g8G' -- userngochan123!
    WHEN 'uservietanh' THEN '$2a$12$FIgbivES.iyjsVdjPIMpF.hdXQeY/FJGaEZ5EuM4Mp7rvSXZLbBgK' -- uservietanh123!
    WHEN 'userthanhmai' THEN '$2a$12$4zCDt6Pu3YeATAnmmVYkc.yODDn2ieTmA0Z4m/0CLylO3xDK7o5l.' -- userthanhmai123!
    WHEN 'userphamson' THEN '$2a$12$yfWFzbcWtt/1nXApzbVgkO8N3Uvsw6W2F21ZAdvFThoX.y5s0vR66' -- userphamson123!
    WHEN 'userlanhuong' THEN '$2a$12$/U.3MjyxyNdgU7atBGvOc.twaw/0fPKlEDbnICd4pxIp/c5kJPFUS' -- userlanhuong123!
    WHEN 'photohung' THEN '$2a$12$P3tvFt72jRZhU4nGdm1XqObLhBFnsPpjWZcRDTs3YemqujppGnCtG' -- photohung123!
    WHEN 'photovy' THEN '$2a$12$KxLTQdjfAd7aMldkFlDvtuyeuOjSrIDfj8byWu1qiJ9laIUNYlQoO' -- photovy123!
    WHEN 'photodat' THEN '$2a$12$uzqUBbVhuNPlmA2cY8VYn.N.VrwiD8oGAJ6n2oASoiaGr/NtDmlTK' -- photodat123!
    WHEN 'phototram' THEN '$2a$12$502jd0u7TTXYryoP4dOoSOavEwlt2g/DWYeppMpUQ65kbbDsGYymq' -- phototram123!
    WHEN 'photokiet' THEN '$2a$12$9G6WDe.YSdiLQC1iPcksR.ya6Rh67otYWJLXLM.ExPLptIM1FuAQy' -- photokiet123!
    WHEN 'photohuyen' THEN '$2a$12$mV1SNrO/9FB2soszbUyXaeS890jLdmmxwgx3l6Wh1vLozIb5R/dem' -- photohuyen123!
    WHEN 'photothinh' THEN '$2a$12$hAdBGgEeeH4cAKHuoi6ZT.GLZv/ONMkr7/v/BTRII8IyNqCIEv/hS' -- photothinh123!
    WHEN 'photomy' THEN '$2a$12$WkHocOmiVSoZheDUnnFNauG5cCH7Mow4SaOomU8gt3sYtnzHvpjFq' -- photomy123!
    ELSE password_hash
END
WHERE username IN (
    'photo_tranquang', 'photo_nguyentu', 'photo_lekhanh', 'photo_hoangnam',
    'photo_thuha', 'photo_minhtam', 'photo_thaovy', 'photo_kai',
    'photohoan', 'phototong', 'photolinh', 'photoduy', 'photonam',
    'photoan', 'photobao', 'photochi', 'photodan', 'photogiang',
    'photohai', 'photokhanh', 'photolong', 'photomai', 'photonhi',
    'photophuc', 'photoquynh', 'photoson', 'phototrang', 'photoyen',
    'userlinhnguyen', 'userminhanh', 'usertuananh', 'userbaotran',
    'userhoanglong', 'userthuylinh', 'userdangquang', 'userngochan',
    'uservietanh', 'userthanhmai', 'userphamson', 'userlanhuong',
    'photohung', 'photovy', 'photodat', 'phototram',
    'photokiet', 'photohuyen', 'photothinh', 'photomy'
);

INSERT INTO portfolios (
    id, user_id, title, description, specialties, hourly_rate, currency,
    service_area, is_available, rating_avg, review_count
) VALUES
(101, 101, 'Landscape Photo Trips', 'Gói chụp bình minh, ruộng bậc thang, núi rừng và các chuyến ảnh phong cảnh.', '["Landscape","Travel","Outdoor"]', 650000.00, 'VND', 'Ha Noi, Sa Pa, Ha Giang', 1, 4.90, 42),
(102, 102, 'Street Stories', 'Chụp ảnh đường phố, chân dung đô thị và câu chuyện đời thường tự nhiên.', '["Street","Portrait","Editorial"]', 550000.00, 'VND', 'Da Nang, Hoi An', 1, 4.80, 36),
(103, 103, 'Travel Memories', 'Bộ ảnh du lịch lifestyle cho cặp đôi, creator và những chuyến đi đáng nhớ.', '["Travel","Lifestyle","Portrait"]', 600000.00, 'VND', 'Hoi An, Da Nang', 1, 4.70, 28),
(104, 104, 'Wedding Documentary', 'Chụp phóng sự cưới tự nhiên với màu ảnh trong trẻo và cảm xúc thật.', '["Wedding","Couple","Event"]', 900000.00, 'VND', 'Ha Noi', 1, 4.95, 64),
(105, 105, 'Soft Portrait Studio', 'Chụp chân dung tối giản bằng ánh sáng tự nhiên, phù hợp profile và beauty.', '["Portrait","Studio","Beauty"]', 500000.00, 'VND', 'Ha Noi', 1, 4.85, 47),
(106, 106, 'City Night Frames', 'Photo walk kiến trúc, phố đêm và ánh sáng đô thị cho bộ ảnh cá tính.', '["Street","Architecture","Night"]', 520000.00, 'VND', 'Ho Chi Minh City', 1, 4.75, 31),
(107, 107, 'Da Lat Couple Shoots', 'Bộ ảnh cặp đôi tại Đà Lạt với màu ấm, đồi thông và ánh sáng núi rừng.', '["Wedding","Couple","Landscape"]', 720000.00, 'VND', 'Da Lat', 1, 4.90, 53),
(108, 108, 'Paris Fine Art', 'Ảnh du lịch fine art và editorial cho thương hiệu cá nhân, portfolio và kỷ niệm.', '["Travel","Portrait","Editorial"]', 850000.00, 'VND', 'Paris', 1, 4.80, 39)
ON DUPLICATE KEY UPDATE
    title = VALUES(title),
    description = VALUES(description),
    specialties = VALUES(specialties),
    hourly_rate = VALUES(hourly_rate),
    service_area = VALUES(service_area),
    is_available = VALUES(is_available),
    rating_avg = VALUES(rating_avg),
    review_count = VALUES(review_count);

INSERT INTO portfolios (
    id, user_id, title, description, specialties, hourly_rate, currency,
    service_area, is_available, rating_avg, review_count
) VALUES
(109, 109, 'Hoan Outdoor Photo', 'Gói chụp chân dung ngoài trời, sự kiện nhỏ và ảnh cá nhân tại Hà Nội.', '["Portrait","Outdoor","Event"]', 520000.00, 'VND', 'Ha Noi', 1, 4.70, 22),
(110, 110, 'Tong Wedding Photo', 'Chụp ảnh cưới và cặp đôi với màu ảnh ấm, nhẹ nhàng và giàu cảm xúc.', '["Wedding","Couple","Event"]', 780000.00, 'VND', 'Ho Chi Minh City', 1, 4.80, 31),
(111, 111, 'Linh Portrait Studio', 'Chụp chân dung bằng ánh sáng tự nhiên, phù hợp profile, beauty và lifestyle.', '["Portrait","Beauty","Studio"]', 480000.00, 'VND', 'Da Nang', 1, 4.75, 26),
(112, 112, 'Duy Street Editorial', 'Photo walk đường phố, thời trang và editorial trong những góc phố Huế.', '["Street","Fashion","Editorial"]', 560000.00, 'VND', 'Hue', 1, 4.65, 19),
(113, 113, 'Nam Travel Photo', 'Bộ ảnh du lịch cho creator, cặp đôi và thương hiệu cá nhân bên biển.', '["Travel","Lifestyle","Landscape"]', 620000.00, 'VND', 'Nha Trang', 1, 4.85, 34),
(114, 114, 'An Family Lifestyle', 'Chụp gia đình, em bé và lifestyle đời thường với cảm giác gần gũi.', '["Family","Lifestyle","Portrait"]', 450000.00, 'VND', 'Can Tho', 1, 4.60, 18),
(115, 115, 'Bao Drone Landscape', 'Chụp phong cảnh và flycam cho chuyến đi, resort, homestay và du lịch.', '["Landscape","Drone","Travel"]', 700000.00, 'VND', 'Da Lat', 1, 4.90, 44),
(116, 116, 'Chi Beauty Portrait', 'Chụp beauty, profile và chân dung studio với retouch mềm, màu da tự nhiên.', '["Portrait","Beauty","Studio"]', 530000.00, 'VND', 'Ha Noi', 1, 4.80, 29),
(117, 117, 'Dan Documentary Wedding', 'Phóng sự cưới, lễ gia tiên và tiệc cưới theo phong cách tự nhiên.', '["Wedding","Documentary","Event"]', 820000.00, 'VND', 'Hai Phong', 1, 4.78, 27),
(118, 118, 'Giang Street Portrait', 'Chụp chân dung đường phố tại phố cổ, quán cà phê và không gian du lịch.', '["Street","Portrait","Travel"]', 500000.00, 'VND', 'Hoi An', 1, 4.66, 21),
(119, 119, 'Hai Architecture Photo', 'Chụp nội thất, khách sạn, quán cà phê và kiến trúc thương mại.', '["Architecture","Interior","Street"]', 650000.00, 'VND', 'Ho Chi Minh City', 1, 4.72, 24),
(120, 120, 'Khanh Lookbook Studio', 'Chụp lookbook thời trang, chân dung thương hiệu và editorial studio.', '["Fashion","Portrait","Studio"]', 690000.00, 'VND', 'Da Nang', 1, 4.88, 39),
(121, 121, 'Long Event Frames', 'Chụp sự kiện, concert, nightlife và khoảnh khắc sân khấu giàu năng lượng.', '["Event","Street","Night"]', 570000.00, 'VND', 'Ha Noi', 1, 4.69, 23),
(122, 122, 'Mai Fine Art Portrait', 'Chụp chân dung fine art với ánh sáng mềm, màu ảnh tinh tế và retouch nhẹ.', '["Portrait","Fine Art","Beauty"]', 610000.00, 'VND', 'Hue', 1, 4.84, 33),
(123, 123, 'Nhi Minimal Studio', 'Chụp sản phẩm nhỏ, chân dung tối giản và bố cục sạch cho cửa hàng.', '["Product","Portrait","Studio"]', 470000.00, 'VND', 'Can Tho', 1, 4.58, 17),
(124, 124, 'Phuc Travel Wedding', 'Gói cưới du lịch, elopement và cặp đôi tại Đà Lạt cùng các điểm đến.', '["Wedding","Travel","Couple"]', 880000.00, 'VND', 'Da Lat', 1, 4.92, 48),
(125, 125, 'Quynh Studio Beauty', 'Chụp beauty, profile công việc và headshot chuyên nghiệp trong studio.', '["Portrait","Beauty","Business"]', 540000.00, 'VND', 'Ha Noi', 1, 4.81, 35),
(126, 126, 'Son Mountain Photo', 'Chụp bình minh vùng núi, trekking và bộ ảnh outdoor phiêu lưu.', '["Landscape","Outdoor","Travel"]', 720000.00, 'VND', 'Sa Pa', 1, 4.87, 37),
(127, 127, 'Trang Bridal Details', 'Chụp chân dung cô dâu, chi tiết váy cưới và khoảnh khắc ngày cưới.', '["Wedding","Portrait","Event"]', 760000.00, 'VND', 'Ninh Binh', 1, 4.83, 32),
(128, 128, 'Yen Lifestyle Travel', 'Chụp lifestyle du lịch, chân dung ngoài trời và bộ ảnh nghỉ dưỡng biển.', '["Lifestyle","Travel","Portrait"]', 590000.00, 'VND', 'Phu Quoc', 1, 4.73, 25)
ON DUPLICATE KEY UPDATE
    title = VALUES(title),
    description = VALUES(description),
    specialties = VALUES(specialties),
    hourly_rate = VALUES(hourly_rate),
    service_area = VALUES(service_area),
    is_available = VALUES(is_available),
    rating_avg = VALUES(rating_avg),
    review_count = VALUES(review_count);

INSERT INTO portfolios (
    id, user_id, title, description, specialties, hourly_rate, currency,
    service_area, is_available, rating_avg, review_count
) VALUES
(141, 141, 'Hung Wedding Moments', 'Chụp ảnh cưới, lễ ăn hỏi và sự kiện gia đình với màu ảnh ấm, tự nhiên.', '["Wedding","Couple","Event"]', 790000.00, 'VND', 'Ha Noi, Ninh Binh', 1, 4.86, 34),
(142, 142, 'Vy Natural Portrait', 'Chụp chân dung nữ, beauty và profile cá nhân bằng ánh sáng mềm.', '["Portrait","Beauty","Studio"]', 560000.00, 'VND', 'Da Nang, Hoi An', 1, 4.82, 28),
(143, 143, 'Dat City Stories', 'Photo walk đường phố, phố đêm và editorial đô thị cho thương hiệu cá nhân.', '["Street","Night","Editorial"]', 580000.00, 'VND', 'Ho Chi Minh City', 1, 4.74, 22),
(144, 144, 'Tram Family Lifestyle', 'Chụp gia đình, newborn và lifestyle đời thường trong không gian gần gũi.', '["Family","Lifestyle","Portrait"]', 490000.00, 'VND', 'Can Tho, Vinh Long', 1, 4.70, 19),
(145, 145, 'Kiet Product Studio', 'Chụp sản phẩm, lookbook và ảnh thương mại điện tử với bố cục sạch.', '["Product","Fashion","Studio"]', 640000.00, 'VND', 'Ho Chi Minh City', 1, 4.79, 25),
(146, 146, 'Huyen Beach Lifestyle', 'Chụp lifestyle biển, resort, du lịch và ảnh thương hiệu cá nhân.', '["Travel","Lifestyle","Beach"]', 690000.00, 'VND', 'Nha Trang, Phu Quoc', 1, 4.88, 37),
(147, 147, 'Thinh Drone Outdoor', 'Chụp phong cảnh, flycam và outdoor cho trekking, homestay và du lịch.', '["Landscape","Drone","Outdoor"]', 730000.00, 'VND', 'Da Lat, Sa Pa', 1, 4.91, 41),
(148, 148, 'My Bridal Fine Art', 'Chụp bridal, fine art portrait và bộ ảnh cưới tối giản, tinh tế.', '["Wedding","Bridal","Fine Art"]', 820000.00, 'VND', 'Hue, Da Nang', 1, 4.84, 30)
ON DUPLICATE KEY UPDATE
    title = VALUES(title),
    description = VALUES(description),
    specialties = VALUES(specialties),
    hourly_rate = VALUES(hourly_rate),
    service_area = VALUES(service_area),
    is_available = VALUES(is_available),
    rating_avg = VALUES(rating_avg),
    review_count = VALUES(review_count);

INSERT INTO posts (
    id, user_id, caption, location, visibility, comment_visibility,
    like_count, comment_count, share_count, created_at
) VALUES
(201, 101, 'Đức Anh Bùi sớm ở Mù Cang Chải #landscape #terrace #vietnam', 'Mù Cang Chải, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 892, 54, 123, CURRENT_TIMESTAMP - INTERVAL 1 HOUR),
(202, 101, 'Ngắm núi Phú Sĩ lúc bình minh #mountfuji #japan #travel', 'Yamanashi, Nhật Bản', 'PUBLIC', 'ALLOW_ALL', 2400, 112, 260, CURRENT_TIMESTAMP - INTERVAL 2 HOUR),
(203, 107, 'Hoàng hôn trên đồi cỏ Đà Lạt #dalat #sunset #couple', 'Đà Lạt, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 1800, 89, 140, CURRENT_TIMESTAMP - INTERVAL 3 HOUR),
(204, 108, 'Một góc Paris thơ mộng sau khung cửa cũ #paris #france #travel', 'Paris, Pháp', 'PUBLIC', 'ALLOW_ALL', 1600, 76, 118, CURRENT_TIMESTAMP - INTERVAL 4 HOUR),
(205, 106, 'Đường phố Brooklyn trong buổi sáng mùa thu #newyork #brooklyn #street', 'New York, Hoa Kỳ', 'PUBLIC', 'ALLOW_ALL', 1200, 54, 98, CURRENT_TIMESTAMP - INTERVAL 5 HOUR),
(206, 105, 'Chân dung trong ánh sáng cửa sổ rất dịu #portrait #beauty', 'Hà Nội, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 980, 43, 62, CURRENT_TIMESTAMP - INTERVAL 6 HOUR),
(207, 102, 'Đôi sneaker cũ trên nền đen tối giản #street #minimal', 'Đà Nẵng, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 860, 38, 51, CURRENT_TIMESTAMP - INTERVAL 7 HOUR),
(208, 106, 'Cây cầu đêm và ánh xanh thành phố #architecture #street #night', 'Thành phố Hồ Chí Minh, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 790, 31, 44, CURRENT_TIMESTAMP - INTERVAL 8 HOUR),
(209, 104, 'Ngày cưới giữa rừng thông, mọi khoảnh khắc đều thật nhẹ #wedding #couple #forest', 'Sóc Sơn, Hà Nội', 'PUBLIC', 'ALLOW_ALL', 1520, 67, 101, CURRENT_TIMESTAMP - INTERVAL 9 HOUR),
(210, 103, 'Cung đường biển đẹp nhất miền Trung trong nắng sớm #travel #landscape #roadtrip', 'Nha Trang, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 710, 29, 35, CURRENT_TIMESTAMP - INTERVAL 10 HOUR),
(211, 101, 'Dãy núi trong sương sớm, chỉ nghe tiếng gió qua thung lũng #landscape #mountain #travel', 'Sa Pa, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 1320, 58, 88, CURRENT_TIMESTAMP - INTERVAL 11 HOUR),
(212, 104, 'Lễ cưới nhỏ bên hồ, ánh hoàng hôn vừa đủ ấm #wedding #couple #sunset', 'Hồ Tây, Hà Nội', 'PUBLIC', 'ALLOW_ALL', 1110, 49, 72, CURRENT_TIMESTAMP - INTERVAL 12 HOUR),
(213, 109, 'Một buổi chụp ngoài trời ở bãi đá sông Hồng #portrait #outdoor #hanoi', 'Bãi đá sông Hồng, Hà Nội', 'PUBLIC', 'ALLOW_ALL', 640, 24, 31, CURRENT_TIMESTAMP - INTERVAL 13 HOUR),
(214, 110, 'Bộ ảnh cưới tối giản với váy trắng và nắng chiều #wedding #couple #minimal', 'Quận 2, Thành phố Hồ Chí Minh', 'PUBLIC', 'ALLOW_ALL', 1180, 52, 77, CURRENT_TIMESTAMP - INTERVAL 14 HOUR),
(215, 111, 'Chân dung nàng thơ trong quán cà phê nhỏ #portrait #lifestyle #danang', 'Đà Nẵng, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 730, 28, 36, CURRENT_TIMESTAMP - INTERVAL 15 HOUR),
(216, 112, 'Một góc phố Huế sau cơn mưa chiều #street #hue #vietnam', 'Huế, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 690, 21, 29, CURRENT_TIMESTAMP - INTERVAL 16 HOUR),
(217, 113, 'Sắc xanh biển Nha Trang trong chuyến đi mùa hạ #travel #beach #landscape', 'Nha Trang, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 940, 37, 46, CURRENT_TIMESTAMP - INTERVAL 17 HOUR),
(218, 114, 'Khoảnh khắc gia đình bên hiên nhà miền Tây #family #lifestyle #portrait', 'Cần Thơ, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 520, 19, 22, CURRENT_TIMESTAMP - INTERVAL 18 HOUR),
(219, 115, 'Đồi thông Đà Lạt nhìn từ flycam lúc sáng sớm #landscape #drone #dalat', 'Đà Lạt, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 1270, 49, 80, CURRENT_TIMESTAMP - INTERVAL 19 HOUR),
(220, 116, 'Ảnh beauty với nền sáng và tông màu trong trẻo #portrait #beauty #studio', 'Hà Nội, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 860, 34, 41, CURRENT_TIMESTAMP - INTERVAL 20 HOUR),
(221, 117, 'Câu chuyện cưới kể bằng những ánh nhìn rất thật #wedding #documentary #event', 'Hải Phòng, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 980, 42, 58, CURRENT_TIMESTAMP - INTERVAL 21 HOUR),
(222, 118, 'Phố cổ Hội An lên đèn và một nụ cười rất nhẹ #street #portrait #hoian', 'Hội An, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 760, 27, 33, CURRENT_TIMESTAMP - INTERVAL 22 HOUR),
(223, 119, 'Không gian khách sạn với ánh sáng tự nhiên buổi sớm #architecture #interior #hotel', 'Thành phố Hồ Chí Minh, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 610, 18, 25, CURRENT_TIMESTAMP - INTERVAL 23 HOUR),
(224, 120, 'Lookbook mùa mới với những gam màu trung tính #fashion #portrait #studio', 'Đà Nẵng, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 1040, 39, 54, CURRENT_TIMESTAMP - INTERVAL 24 HOUR),
(225, 121, 'Đêm nhạc nhỏ, sân khấu gần và cảm xúc rất lớn #event #night #street', 'Hà Nội, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 700, 23, 38, CURRENT_TIMESTAMP - INTERVAL 25 HOUR),
(226, 122, 'Bức chân dung fine art trong lớp ánh sáng mềm #portrait #fineart #beauty', 'Huế, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 930, 36, 49, CURRENT_TIMESTAMP - INTERVAL 26 HOUR),
(227, 123, 'Sản phẩm nhỏ trên nền trắng, mọi chi tiết đều rõ ràng #product #minimal #studio', 'Cần Thơ, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 430, 12, 18, CURRENT_TIMESTAMP - INTERVAL 27 HOUR),
(228, 124, 'Cặp đôi chạy qua vườn hoa lúc trời vừa tắt nắng #wedding #travel #couple', 'Đà Lạt, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 1210, 48, 76, CURRENT_TIMESTAMP - INTERVAL 28 HOUR),
(229, 125, 'Headshot công việc với ánh sáng gọn và nền sạch #portrait #business #studio', 'Hà Nội, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 590, 16, 21, CURRENT_TIMESTAMP - INTERVAL 29 HOUR),
(230, 126, 'Bình minh trên đỉnh Fansipan, mây trôi dưới chân #landscape #mountain #travel', 'Sa Pa, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 1380, 55, 90, CURRENT_TIMESTAMP - INTERVAL 30 HOUR),
(231, 127, 'Chi tiết váy cưới và bó hoa trong căn phòng nhỏ #wedding #bridal #portrait', 'Ninh Bình, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 870, 33, 47, CURRENT_TIMESTAMP - INTERVAL 31 HOUR),
(232, 128, 'Một buổi chiều Phú Quốc với màu biển rất trong #travel #lifestyle #beach', 'Phú Quốc, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 810, 30, 42, CURRENT_TIMESTAMP - INTERVAL 32 HOUR),
(233, 102, 'Chợ đêm Hội An sau cơn mưa, ánh đèn phản chiếu rất điện ảnh #street #travel #night', 'Hội An, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 920, 34, 58, CURRENT_TIMESTAMP - INTERVAL 33 HOUR),
(234, 103, 'Một buổi sáng lang thang ở phố cổ, mọi thứ chậm và rất thơ #travel #lifestyle #hoian', 'Hội An, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 880, 27, 46, CURRENT_TIMESTAMP - INTERVAL 34 HOUR),
(235, 105, 'Chân dung profile với nền tối giản và ánh nắng nghiêng qua rèm #portrait #studio #minimal', 'Hà Nội, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 760, 24, 33, CURRENT_TIMESTAMP - INTERVAL 35 HOUR),
(236, 106, 'Quán cà phê nhỏ với đường nét kiến trúc sạch và ánh sáng rất mềm #architecture #interior #minimal', 'Thành phố Hồ Chí Minh, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 690, 18, 29, CURRENT_TIMESTAMP - INTERVAL 36 HOUR),
(237, 109, 'Bộ ảnh ngoài trời lúc nắng vừa dịu, màu da và cây cỏ lên rất tự nhiên #portrait #outdoor #hanoi', 'Hà Nội, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 830, 31, 44, CURRENT_TIMESTAMP - INTERVAL 37 HOUR),
(238, 110, 'Lễ đính hôn ấm cúng trong sân nhà, từng ánh nhìn đều đáng giữ lại #wedding #couple #event', 'Thành phố Hồ Chí Minh, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 1340, 62, 95, CURRENT_TIMESTAMP - INTERVAL 38 HOUR),
(239, 115, 'Đường bay qua thung lũng Đà Lạt, mây và đồi thông mở ra rất rộng #landscape #drone #travel', 'Đà Lạt, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 1460, 57, 102, CURRENT_TIMESTAMP - INTERVAL 39 HOUR),
(240, 120, 'Editorial thời trang với phông xám, dáng đứng gọn và ánh sáng studio #fashion #portrait #studio', 'Đà Nẵng, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 1010, 36, 51, CURRENT_TIMESTAMP - INTERVAL 40 HOUR),
(241, 121, 'Backstage đêm nhạc, ánh đèn sân khấu và những khoảnh khắc phía sau màn #event #night #street', 'Hà Nội, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 780, 26, 39, CURRENT_TIMESTAMP - INTERVAL 41 HOUR),
(242, 123, 'Bộ ảnh sản phẩm gốm trên nền trắng, giữ đúng chất liệu và đường nét thủ công #product #studio #minimal', 'Cần Thơ, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 560, 14, 22, CURRENT_TIMESTAMP - INTERVAL 42 HOUR),
(243, 126, 'Trekking Sa Pa trong sương, mỗi khúc cua đều có một lớp núi mới #landscape #outdoor #mountain', 'Sa Pa, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 1250, 49, 86, CURRENT_TIMESTAMP - INTERVAL 43 HOUR),
(244, 128, 'Bãi biển Phú Quốc lúc sớm, nước trong và trời gần như không gợn mây #travel #beach #lifestyle', 'Phú Quốc, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 970, 33, 60, CURRENT_TIMESTAMP - INTERVAL 44 HOUR),
(245, 129, 'Một góc quán cà phê có nắng xiên qua ô cửa nhỏ #lifestyle #minimal #hanoi', 'Hà Nội, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 420, 12, 18, CURRENT_TIMESTAMP - INTERVAL 45 HOUR),
(246, 130, 'Moodboard chân dung với nền kem, ánh sáng dịu và màu da tự nhiên #portrait #beauty #studio', 'Đà Nẵng, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 510, 16, 24, CURRENT_TIMESTAMP - INTERVAL 46 HOUR),
(247, 131, 'Sài Gòn giờ tan tầm, xe chạy qua lớp nắng cuối ngày #street #city #vietnam', 'Thành phố Hồ Chí Minh, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 610, 21, 31, CURRENT_TIMESTAMP - INTERVAL 47 HOUR),
(248, 132, 'Bữa cơm gia đình cuối tuần, những điều nhỏ mà rất đáng nhớ #family #lifestyle #portrait', 'Cần Thơ, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 390, 10, 15, CURRENT_TIMESTAMP - INTERVAL 48 HOUR),
(249, 133, 'Tầng mây mở ra sau con dốc dài ở Sa Pa #landscape #travel #outdoor', 'Sa Pa, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 840, 32, 52, CURRENT_TIMESTAMP - INTERVAL 49 HOUR),
(250, 134, 'Lookbook áo linen trong phố cổ, màu nhẹ và chuyển động rất mềm #fashion #portrait #travel', 'Huế, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 730, 25, 38, CURRENT_TIMESTAMP - INTERVAL 50 HOUR),
(251, 135, 'Không gian làm việc nhỏ với ánh sáng trắng và đường nét gọn #architecture #interior #minimal', 'Thành phố Hồ Chí Minh, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 460, 13, 19, CURRENT_TIMESTAMP - INTERVAL 51 HOUR),
(252, 136, 'Beauty shot với layout sạch, giữ lại cảm giác rất tự nhiên #portrait #beauty #studio', 'Hà Nội, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 690, 27, 35, CURRENT_TIMESTAMP - INTERVAL 52 HOUR),
(253, 137, 'Một khung hình phía sau sân khấu trước giờ biểu diễn #event #night #documentary', 'Hải Phòng, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 580, 20, 30, CURRENT_TIMESTAMP - INTERVAL 53 HOUR),
(254, 138, 'Hai người đi qua đồi cỏ, chiều Đà Lạt vừa đủ lạnh #couple #wedding #dalat', 'Đà Lạt, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 960, 41, 66, CURRENT_TIMESTAMP - INTERVAL 54 HOUR),
(255, 139, 'Sản phẩm chăm sóc da trên nền đá sáng, chi tiết sạch và rõ #product #studio #minimal', 'Nha Trang, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 440, 11, 17, CURRENT_TIMESTAMP - INTERVAL 55 HOUR),
(256, 140, 'Sáng biển Phú Quốc, tóc còn ướt và trời xanh rất nhẹ #beach #lifestyle #travel', 'Phú Quốc, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 720, 26, 43, CURRENT_TIMESTAMP - INTERVAL 56 HOUR),
(257, 141, 'Lễ ăn hỏi nhỏ trong sân nhà, màu hoa và tiếng cười giữ lại thật vừa vặn #wedding #event #couple', 'Hà Nội, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 1190, 50, 82, CURRENT_TIMESTAMP - INTERVAL 57 HOUR),
(258, 142, 'Chân dung nàng thơ với nắng cửa sổ và lớp nền rất mềm #portrait #beauty #studio', 'Đà Nẵng, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 870, 34, 48, CURRENT_TIMESTAMP - INTERVAL 58 HOUR),
(259, 143, 'Đèn đường sau cơn mưa, phố đêm Sài Gòn lên màu rất điện ảnh #street #night #architecture', 'Thành phố Hồ Chí Minh, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 790, 29, 46, CURRENT_TIMESTAMP - INTERVAL 59 HOUR),
(260, 144, 'Một buổi chụp gia đình trong vườn, trẻ con chạy quanh đầy nắng #family #lifestyle #portrait', 'Cần Thơ, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 650, 22, 32, CURRENT_TIMESTAMP - INTERVAL 60 HOUR),
(261, 145, 'Ảnh sản phẩm túi vải và set lookbook tối giản cho thương hiệu mới #product #fashion #studio', 'Thành phố Hồ Chí Minh, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 810, 31, 47, CURRENT_TIMESTAMP - INTERVAL 61 HOUR),
(262, 146, 'Resort bên biển lúc trời trong, mọi khung hình đều có mùi mùa hè #travel #beach #lifestyle', 'Nha Trang, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 1020, 43, 70, CURRENT_TIMESTAMP - INTERVAL 62 HOUR),
(263, 147, 'Flycam qua đồi cỏ và con đường nhỏ dẫn vào thung lũng #drone #landscape #outdoor', 'Đà Lạt, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 1360, 59, 98, CURRENT_TIMESTAMP - INTERVAL 63 HOUR),
(264, 148, 'Bridal portrait trong căn phòng trắng, mọi chi tiết đều nhẹ và tinh tế #bridal #fineart #wedding', 'Huế, Việt Nam', 'PUBLIC', 'ALLOW_ALL', 940, 37, 55, CURRENT_TIMESTAMP - INTERVAL 64 HOUR)
ON DUPLICATE KEY UPDATE
    caption = VALUES(caption),
    location = VALUES(location),
    visibility = VALUES(visibility),
    comment_visibility = VALUES(comment_visibility),
    like_count = VALUES(like_count),
    comment_count = VALUES(comment_count),
    share_count = VALUES(share_count),
    created_at = VALUES(created_at);

UPDATE users
SET post_count = (
    SELECT COUNT(*)
    FROM posts
    WHERE posts.user_id = users.id
)
WHERE id BETWEEN 101 AND 148;

INSERT INTO post_media (
    id, post_id, media_file_url, thumbnail_url, media_type, position, width, height
) VALUES
(501, 201, 'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee', NULL, 'IMAGE', 1, 1200, 800),
(502, 202, 'https://images.unsplash.com/photo-1490806843957-31f4c9a91c65', NULL, 'IMAGE', 1, 1200, 800),
(503, 203, 'https://images.unsplash.com/photo-1519741497674-611481863552', NULL, 'IMAGE', 1, 1200, 800),
(504, 204, 'https://images.unsplash.com/photo-1502602898657-3e91760cbb34', NULL, 'IMAGE', 1, 1200, 800),
(505, 205, 'https://images.unsplash.com/photo-1494526585095-c41746248156', NULL, 'IMAGE', 1, 1200, 800),
(506, 206, 'https://images.unsplash.com/photo-1496440737103-cd596325d314', NULL, 'IMAGE', 1, 900, 1200),
(507, 207, 'https://images.unsplash.com/photo-1542291026-7eec264c27ff', NULL, 'IMAGE', 1, 1200, 900),
(508, 208, 'https://images.unsplash.com/photo-1518005020951-eccb494ad742', NULL, 'IMAGE', 1, 1200, 900),
(509, 209, 'https://images.unsplash.com/photo-1520854221256-17451cc331bf', NULL, 'IMAGE', 1, 1200, 800),
(510, 210, 'https://images.unsplash.com/photo-1500534314209-a25ddb2bd429', NULL, 'IMAGE', 1, 1200, 800),
(511, 211, 'https://images.unsplash.com/photo-1464822759023-fed622ff2c3b', NULL, 'IMAGE', 1, 1200, 800),
(512, 212, 'https://images.unsplash.com/photo-1469371670807-013ccf25f16a', NULL, 'IMAGE', 1, 1200, 800),
(513, 213, 'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee', NULL, 'IMAGE', 1, 1200, 800),
(514, 214, 'https://images.unsplash.com/photo-1519741497674-611481863552', NULL, 'IMAGE', 1, 1200, 800),
(515, 215, 'https://images.unsplash.com/photo-1496440737103-cd596325d314', NULL, 'IMAGE', 1, 900, 1200),
(516, 216, 'https://images.unsplash.com/photo-1518005020951-eccb494ad742', NULL, 'IMAGE', 1, 1200, 900),
(517, 217, 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e', NULL, 'IMAGE', 1, 1200, 800),
(518, 218, 'https://images.unsplash.com/photo-1511895426328-dc8714191300', NULL, 'IMAGE', 1, 1200, 800),
(519, 219, 'https://images.unsplash.com/photo-1464822759023-fed622ff2c3b', NULL, 'IMAGE', 1, 1200, 800),
(520, 220, 'https://images.unsplash.com/photo-1534528741775-53994a69daeb', NULL, 'IMAGE', 1, 900, 1200),
(521, 221, 'https://images.unsplash.com/photo-1520854221256-17451cc331bf', NULL, 'IMAGE', 1, 1200, 800),
(522, 222, 'https://images.unsplash.com/photo-1500534314209-a25ddb2bd429', NULL, 'IMAGE', 1, 1200, 800),
(523, 223, 'https://images.unsplash.com/photo-1494526585095-c41746248156', NULL, 'IMAGE', 1, 1200, 800),
(524, 224, 'https://images.unsplash.com/photo-1503342217505-b0a15ec3261c', NULL, 'IMAGE', 1, 900, 1200),
(525, 225, 'https://images.unsplash.com/photo-1501386761578-eac5c94b800a', NULL, 'IMAGE', 1, 1200, 800),
(526, 226, 'https://images.unsplash.com/photo-1524504388940-b1c1722653e1', NULL, 'IMAGE', 1, 900, 1200),
(527, 227, 'https://images.unsplash.com/photo-1542291026-7eec264c27ff', NULL, 'IMAGE', 1, 1200, 900),
(528, 228, 'https://images.unsplash.com/photo-1520854221256-17451cc331bf', NULL, 'IMAGE', 1, 1200, 800),
(529, 229, 'https://images.unsplash.com/photo-1547425260-76bcadfb4f2c', NULL, 'IMAGE', 1, 900, 1200),
(530, 230, 'https://images.unsplash.com/photo-1500534314209-a25ddb2bd429', NULL, 'IMAGE', 1, 1200, 800),
(531, 231, 'https://images.unsplash.com/photo-1469371670807-013ccf25f16a', NULL, 'IMAGE', 1, 1200, 800),
(532, 232, 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e', NULL, 'IMAGE', 1, 1200, 800),
(533, 233, 'https://images.unsplash.com/photo-1528127269322-539801943592', NULL, 'IMAGE', 1, 1200, 800),
(534, 234, 'https://images.unsplash.com/photo-1528127269322-539801943592', NULL, 'IMAGE', 1, 1200, 800),
(535, 235, 'https://images.unsplash.com/photo-1496440737103-cd596325d314', NULL, 'IMAGE', 1, 900, 1200),
(536, 236, 'https://images.unsplash.com/photo-1497366754035-f200968a6e72', NULL, 'IMAGE', 1, 1200, 800),
(537, 237, 'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee', NULL, 'IMAGE', 1, 1200, 800),
(538, 238, 'https://images.unsplash.com/photo-1520854221256-17451cc331bf', NULL, 'IMAGE', 1, 1200, 800),
(539, 239, 'https://images.unsplash.com/photo-1500534314209-a25ddb2bd429', NULL, 'IMAGE', 1, 1200, 800),
(540, 240, 'https://images.unsplash.com/photo-1503342217505-b0a15ec3261c', NULL, 'IMAGE', 1, 900, 1200),
(541, 241, 'https://images.unsplash.com/photo-1501386761578-eac5c94b800a', NULL, 'IMAGE', 1, 1200, 800),
(542, 242, 'https://images.unsplash.com/photo-1523275335684-37898b6baf30', NULL, 'IMAGE', 1, 1200, 900),
(543, 243, 'https://images.unsplash.com/photo-1464822759023-fed622ff2c3b', NULL, 'IMAGE', 1, 1200, 800),
(544, 244, 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e', NULL, 'IMAGE', 1, 1200, 800),
(545, 245, 'https://images.unsplash.com/photo-1495474472287-4d71bcdd2085', NULL, 'IMAGE', 1, 1200, 800),
(546, 246, 'https://images.unsplash.com/photo-1496440737103-cd596325d314', NULL, 'IMAGE', 1, 900, 1200),
(547, 247, 'https://images.unsplash.com/photo-1494526585095-c41746248156', NULL, 'IMAGE', 1, 1200, 800),
(548, 248, 'https://images.unsplash.com/photo-1511895426328-dc8714191300', NULL, 'IMAGE', 1, 1200, 800),
(549, 249, 'https://images.unsplash.com/photo-1464822759023-fed622ff2c3b', NULL, 'IMAGE', 1, 1200, 800),
(550, 250, 'https://images.unsplash.com/photo-1503342217505-b0a15ec3261c', NULL, 'IMAGE', 1, 900, 1200),
(551, 251, 'https://images.unsplash.com/photo-1497366754035-f200968a6e72', NULL, 'IMAGE', 1, 1200, 800),
(552, 252, 'https://images.unsplash.com/photo-1534528741775-53994a69daeb', NULL, 'IMAGE', 1, 900, 1200),
(553, 253, 'https://images.unsplash.com/photo-1501386761578-eac5c94b800a', NULL, 'IMAGE', 1, 1200, 800),
(554, 254, 'https://images.unsplash.com/photo-1519741497674-611481863552', NULL, 'IMAGE', 1, 1200, 800),
(555, 255, 'https://images.unsplash.com/photo-1523275335684-37898b6baf30', NULL, 'IMAGE', 1, 1200, 900),
(556, 256, 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e', NULL, 'IMAGE', 1, 1200, 800),
(557, 257, 'https://images.unsplash.com/photo-1520854221256-17451cc331bf', NULL, 'IMAGE', 1, 1200, 800),
(558, 258, 'https://images.unsplash.com/photo-1496440737103-cd596325d314', NULL, 'IMAGE', 1, 900, 1200),
(559, 259, 'https://images.unsplash.com/photo-1518005020951-eccb494ad742', NULL, 'IMAGE', 1, 1200, 900),
(560, 260, 'https://images.unsplash.com/photo-1511895426328-dc8714191300', NULL, 'IMAGE', 1, 1200, 800),
(561, 261, 'https://images.unsplash.com/photo-1503342217505-b0a15ec3261c', NULL, 'IMAGE', 1, 900, 1200),
(562, 262, 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e', NULL, 'IMAGE', 1, 1200, 800),
(563, 263, 'https://images.unsplash.com/photo-1500534314209-a25ddb2bd429', NULL, 'IMAGE', 1, 1200, 800),
(564, 264, 'https://images.unsplash.com/photo-1469371670807-013ccf25f16a', NULL, 'IMAGE', 1, 1200, 800)
ON DUPLICATE KEY UPDATE
    media_file_url = VALUES(media_file_url),
    media_type = VALUES(media_type),
    position = VALUES(position),
    width = VALUES(width),
    height = VALUES(height);

INSERT INTO media_tags (id, name, description, usage_count) VALUES
(301, 'trending', 'Các bài ảnh đang được yêu thích nhất', 64),
(302, 'portrait', 'Ảnh chân dung cá nhân, beauty và profile', 26),
(303, 'wedding', 'Ảnh cưới, đính hôn và khoảnh khắc cặp đôi', 16),
(304, 'landscape', 'Ảnh phong cảnh, núi rừng, biển và du lịch', 18),
(305, 'street', 'Ảnh đường phố, đời sống đô thị và khoảnh khắc tự nhiên', 13),
(306, 'travel', 'Ảnh du lịch, địa điểm và trải nghiệm khám phá', 21),
(307, 'couple', 'Ảnh cặp đôi, pre-wedding và câu chuyện tình yêu', 12),
(308, 'beauty', 'Ảnh beauty, makeup và ánh sáng studio', 8),
(309, 'studio', 'Ảnh chụp trong studio với bố cục sạch', 15),
(310, 'event', 'Ảnh sự kiện, sân khấu và khoảnh khắc đông người', 10),
(311, 'lifestyle', 'Ảnh lifestyle đời thường, gia đình và thương hiệu cá nhân', 14),
(312, 'architecture', 'Ảnh kiến trúc, nội thất và không gian đô thị', 7),
(313, 'drone', 'Ảnh flycam, góc nhìn trên cao và cảnh quan rộng', 4),
(314, 'fashion', 'Ảnh thời trang, lookbook và editorial', 6),
(315, 'family', 'Ảnh gia đình, trẻ em và khoảnh khắc đời thường', 4),
(316, 'product', 'Ảnh sản phẩm, thương mại và bố cục tối giản', 5),
(317, 'night', 'Ảnh đêm, sân khấu và ánh sáng thành phố', 6),
(318, 'outdoor', 'Ảnh ngoài trời với ánh sáng tự nhiên', 4),
(319, 'interior', 'Ảnh nội thất, khách sạn, quán cà phê và không gian sống', 3),
(320, 'minimal', 'Ảnh tối giản, nền sạch và bố cục ít chi tiết', 8),
(321, 'fineart', 'Ảnh nghệ thuật với màu sắc và cảm xúc được xử lý tinh tế', 3),
(322, 'business', 'Ảnh profile công việc, thương hiệu cá nhân và headshot', 2),
(323, 'beach', 'Ảnh biển, nghỉ dưỡng và du lịch mùa hè', 5),
(324, 'documentary', 'Ảnh phóng sự, câu chuyện thật và khoảnh khắc tự nhiên', 3),
(325, 'bridal', 'Ảnh cô dâu, váy cưới và chi tiết lễ cưới', 3)
ON DUPLICATE KEY UPDATE
    description = VALUES(description),
    usage_count = VALUES(usage_count);

INSERT IGNORE INTO post_media_tags (media_id, tag_id) VALUES
(501, 301), (501, 304), (501, 306),
(502, 301), (502, 304), (502, 306),
(503, 301), (503, 303), (503, 307),
(504, 301), (504, 306),
(505, 301), (505, 305),
(506, 302),
(507, 305),
(508, 305),
(509, 303), (509, 307),
(510, 304), (510, 306),
(511, 304), (511, 306),
(512, 303), (512, 307),
(513, 301), (513, 302), (513, 304),
(514, 301), (514, 303), (514, 307),
(515, 301), (515, 302), (515, 311),
(516, 301), (516, 305),
(517, 301), (517, 304), (517, 306),
(518, 311), (518, 302),
(519, 301), (519, 304), (519, 306),
(520, 302), (520, 308), (520, 309),
(521, 301), (521, 303), (521, 310),
(522, 301), (522, 305), (522, 302),
(523, 312), (523, 305),
(524, 301), (524, 302), (524, 309),
(525, 310), (525, 305),
(526, 302), (526, 308),
(527, 309),
(528, 301), (528, 303), (528, 306), (528, 307),
(529, 302), (529, 309),
(530, 301), (530, 304), (530, 306),
(531, 303), (531, 302),
(532, 301), (532, 306), (532, 311),
(513, 318),
(518, 315),
(519, 313),
(521, 324),
(523, 319),
(524, 314),
(525, 317),
(526, 321),
(527, 316), (527, 320),
(529, 322),
(531, 325),
(532, 323),
(533, 301), (533, 305), (533, 306), (533, 317),
(534, 301), (534, 306), (534, 311),
(535, 302), (535, 309), (535, 320),
(536, 312), (536, 319), (536, 320),
(537, 301), (537, 302), (537, 318),
(538, 301), (538, 303), (538, 307), (538, 310),
(539, 301), (539, 304), (539, 306), (539, 313),
(540, 301), (540, 302), (540, 309), (540, 314),
(541, 310), (541, 317), (541, 305),
(542, 309), (542, 316), (542, 320),
(543, 301), (543, 304), (543, 306), (543, 318),
(544, 301), (544, 306), (544, 311), (544, 323),
(545, 311), (545, 320),
(546, 302), (546, 308), (546, 309),
(547, 301), (547, 305),
(548, 315), (548, 311), (548, 302),
(549, 301), (549, 304), (549, 306), (549, 318),
(550, 302), (550, 314), (550, 306),
(551, 312), (551, 319), (551, 320),
(552, 302), (552, 308), (552, 309),
(553, 310), (553, 317), (553, 324),
(554, 301), (554, 307), (554, 303),
(555, 316), (555, 309), (555, 320),
(556, 301), (556, 323), (556, 311), (556, 306),
(557, 301), (557, 303), (557, 310), (557, 307),
(558, 302), (558, 308), (558, 309),
(559, 301), (559, 305), (559, 317), (559, 312),
(560, 315), (560, 311), (560, 302),
(561, 316), (561, 314), (561, 309),
(562, 301), (562, 306), (562, 323), (562, 311),
(563, 301), (563, 313), (563, 304), (563, 318),
(564, 325), (564, 321), (564, 303);

-- Some existing dev databases already have "travel" and "night" under older IDs.
-- Add these mappings by tag name, then remove mobile mappings that point to missing tag IDs.
INSERT IGNORE INTO post_media_tags (media_id, tag_id)
SELECT mapped.media_id, media_tags.id
FROM (
    SELECT 501 AS media_id, 'travel' AS tag_name UNION ALL
    SELECT 502, 'travel' UNION ALL
    SELECT 504, 'travel' UNION ALL
    SELECT 510, 'travel' UNION ALL
    SELECT 511, 'travel' UNION ALL
    SELECT 517, 'travel' UNION ALL
    SELECT 519, 'travel' UNION ALL
    SELECT 528, 'travel' UNION ALL
    SELECT 530, 'travel' UNION ALL
    SELECT 532, 'travel' UNION ALL
    SELECT 533, 'travel' UNION ALL
    SELECT 534, 'travel' UNION ALL
    SELECT 539, 'travel' UNION ALL
    SELECT 543, 'travel' UNION ALL
    SELECT 544, 'travel' UNION ALL
    SELECT 549, 'travel' UNION ALL
    SELECT 550, 'travel' UNION ALL
    SELECT 556, 'travel' UNION ALL
    SELECT 562, 'travel' UNION ALL
    SELECT 525, 'night' UNION ALL
    SELECT 533, 'night' UNION ALL
    SELECT 541, 'night' UNION ALL
    SELECT 553, 'night' UNION ALL
    SELECT 559, 'night'
) AS mapped
INNER JOIN media_tags ON media_tags.name = mapped.tag_name;

DELETE post_media_tags
FROM post_media_tags
LEFT JOIN media_tags ON media_tags.id = post_media_tags.tag_id
WHERE media_tags.id IS NULL
  AND post_media_tags.media_id BETWEEN 501 AND 564;

SET FOREIGN_KEY_CHECKS = 1;
