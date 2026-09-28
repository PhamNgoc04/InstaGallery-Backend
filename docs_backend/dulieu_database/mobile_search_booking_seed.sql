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
(101, 'ngonganha', 'ngonganha@gmail.com', '$2a$10$XFtMU6tpoOfu9xYz/w9YWehAWtayIWnrHTZ2lmP.grGQRjKeubSx.', 'Ngô Ngân Hà', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e', 'Landscape photographer based in Ha Noi.', 'Ha Noi', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 3280, 214, 3),
(102, 'duongnganha', 'duongnganha@gmail.com', '$2a$10$x7YDWy3JkG00jZKBXeuiJeU56uks85rVASTIMJvUCiEmq8nXsg/lW', 'Dương Ngân Hà', 'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d', 'Street and editorial photographer.', 'Da Nang', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 2450, 188, 2),
(103, 'lynganha', 'lynganha@gmail.com', '$2a$10$cxOVg.gTd0O8zdcsQHykAeSY//p1NgEGKPp3AH3kaA6/93e0yCtmG', 'Lý Ngân Hà', 'https://images.unsplash.com/photo-1494790108377-be9c29b29330', 'Travel and lifestyle photographer.', 'Hoi An', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1890, 160, 2),
(104, 'nguyenhoanglong', 'nguyenhoanglong@gmail.com', '$2a$10$39JP1ZiT8gWHWJvJIyy3kuds.t1Jar3Je/h53VNF7an9m7Fcmhc1.', 'Nguyễn Hoàng Long', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d', 'Wedding photographer for natural moments.', 'Ha Noi', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 4120, 201, 3),
(105, 'tranhoanglong', 'tranhoanglong@gmail.com', '$2a$10$AS2ussFjiUB1lbBwUzrnMubTNTTv2t6C/5zFTa/23bE/hhh19FLPu', 'Trần Hoàng Long', 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80', 'Portrait photographer with soft natural light.', 'Ha Noi', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 2760, 143, 2),
(106, 'lehoanglong', 'lehoanglong@gmail.com', '$2a$10$gCSHdq3cpeGubHGAs.8jQe7J1YOtmtVmd2aDcaDhtH8mE/HPZg9di', 'Lê Hoàng Long', 'https://images.unsplash.com/photo-1507101105822-7472b28e22ac', 'Architecture and night city photographer.', 'Ho Chi Minh City', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 2210, 119, 2),
(107, 'phamhoanglong', 'phamhoanglong@gmail.com', '$2a$10$ZagvntrANBMsh66NK5X8L..euElZoaWieSpaI9HMwQZUEXrPejZnS', 'Phạm Hoàng Long', 'https://images.unsplash.com/photo-1544005313-94ddf0286df2', 'Couple and elopement photographer.', 'Da Lat', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 3570, 181, 2),
(108, 'hoangminhlong', 'hoangminhlong@gmail.com', '$2a$10$XFWCWJBGQl3jhuMgU0msOumpKWPgsEuHVQSowuHRkWEZoYlYLPw1G', 'Hoàng Minh Long', 'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d', 'Fine art travel photography.', 'Paris', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 2980, 137, 2)
ON DUPLICATE KEY UPDATE
    username = VALUES(username),
    email = VALUES(email),
    password_hash = VALUES(password_hash),
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
(109, 'huynhhoanglong', 'huynhhoanglong@gmail.com', '$2a$10$edpGPUQlaBa83BjRwyRKUe4Mugoz52dwO4SYOYx2dzGDXjDyOrJmS', 'Huỳnh Hoàng Long', 'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d', 'Outdoor portrait and event photographer.', 'Ha Noi', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1540, 96, 0),
(110, 'phanhoanglong', 'phanhoanglong@gmail.com', '$2a$10$6udIU3XlPn/vWfnv9oY4oOkPPZFj0mNaiANejRFQWJ8U4HLY06oei', 'Phan Hoàng Long', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e', 'Wedding and couple photographer.', 'Ho Chi Minh City', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1730, 112, 0),
(111, 'vuhoanglong', 'vuhoanglong@gmail.com', '$2a$10$O18.anSwe.l5gondkDYGGuJgvruLtToUf2EtJuQc89HKyc9Rybs..', 'Vũ Hoàng Long', 'https://images.unsplash.com/photo-1494790108377-be9c29b29330', 'Portrait photographer with clean natural tones.', 'Da Nang', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1688, 105, 0),
(112, 'vohoanglong', 'vohoanglong@gmail.com', '$2a$10$JflHaKO8KYq0dHC9qF9ZU.fjp/wF1oYRt3UBXJ4xJBPTxvD7JKRfy', 'Võ Hoàng Long', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d', 'Street, fashion and editorial photographer.', 'Hue', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1420, 87, 0),
(113, 'danghoanglong', 'danghoanglong@gmail.com', '$2a$10$vueMJGK8of4Qpwik68r.guVMhI9wM6t5.F7y53n7aSeUgppzdTmYS', 'Đặng Hoàng Long', 'https://images.unsplash.com/photo-1507101105822-7472b28e22ac', 'Travel photographer for personal brands.', 'Nha Trang', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1980, 133, 0),
(114, 'buihoanglong', 'buihoanglong@gmail.com', '$2a$10$1wL98nfHIhbPbqacXzvl8eCaIrd3OSMklNrkLSHyyDbv2zGoqet4a', 'Bùi Hoàng Long', 'https://images.unsplash.com/photo-1544005313-94ddf0286df2', 'Family and lifestyle photographer.', 'Can Tho', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1290, 74, 0),
(115, 'dohoanglong', 'dohoanglong@gmail.com', '$2a$10$Dwwk6uxxUJmMMBR.ouohBumUuPEqjAPNg9OHSmyTuqkS8teDoTJ/i', 'Đỗ Hoàng Long', 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde', 'Landscape and drone photographer.', 'Da Lat', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 2210, 151, 0),
(116, 'hohoanglong', 'hohoanglong@gmail.com', '$2a$10$S1C4XCMWvdvnKUn9b9B6GOz0EA62yhvf8g.OhKAGa85A/HwyMR5yK', 'Hồ Hoàng Long', 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80', 'Beauty, portrait and studio photographer.', 'Ha Noi', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1875, 126, 0),
(117, 'ngohoanglong', 'ngohoanglong@gmail.com', '$2a$10$scf3Q6C2dS9P0x7Nyg638OC0YLmgUjAZ3HYualNTr/i.JlCFl3L6K', 'Ngô Hoàng Long', 'https://images.unsplash.com/photo-1527980965255-d3b416303d12', 'Documentary wedding photographer.', 'Hai Phong', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1644, 101, 0),
(118, 'duonghoanglong', 'duonghoanglong@gmail.com', '$2a$10$nQ5KTb0xk3n9z2lAOGhikOtnYlMAOY7dcKJDWYi20hBBdxnBRo1ra', 'Dương Hoàng Long', 'https://images.unsplash.com/photo-1508214751196-bcfd4ca60f91', 'Street portrait photographer.', 'Hoi An', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1495, 92, 0),
(119, 'lyhoanglong', 'lyhoanglong@gmail.com', '$2a$10$lEiE/yxUKTs21H0XYvfE8e2Kr14st/xw8EkwZv64qE8m6MrrI2w.e', 'Lý Hoàng Long', 'https://images.unsplash.com/photo-1504593811423-6dd665756598', 'Architecture and interior photographer.', 'Ho Chi Minh City', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1368, 89, 0),
(120, 'nguyenkimanh', 'nguyenkimanh@gmail.com', '$2a$10$dGjwLweZbo2mMfY5ckvwe.65DbMNiYHJv6yV9zvRAxg4/72/GaEFC', 'Nguyễn Kim Anh', 'https://images.unsplash.com/photo-1547425260-76bcadfb4f2c', 'Fashion and lookbook photographer.', 'Da Nang', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 2112, 141, 0),
(121, 'trankimanh', 'trankimanh@gmail.com', '$2a$10$iJpLI.XRlBrrSoq4V6Gbk.M6gKp8rOI2OeTimNnj4y7YLoU0cgvcm', 'Trần Kim Anh', 'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d', 'Event and concert photographer.', 'Ha Noi', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1742, 118, 0),
(122, 'lekimanh', 'lekimanh@gmail.com', '$2a$10$NtWfSBoGS1WuNiLGQFEUx.vtqARTKweDNRT5hr5dqvyntOgoJ/7wi', 'Lê Kim Anh', 'https://images.unsplash.com/photo-1494790108377-be9c29b29330', 'Fine art portrait photographer.', 'Hue', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1960, 121, 0),
(123, 'phamkimanh', 'phamkimanh@gmail.com', '$2a$10$PLi26siO.WJSjqQZbb5ijeukP87U5dHjVMgijFe9RSiGFq/P4yYa2', 'Phạm Kim Anh', 'https://images.unsplash.com/photo-1517841905240-472988babdf9', 'Minimal product and portrait photographer.', 'Can Tho', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1308, 77, 0),
(124, 'hoangkimanh', 'hoangkimanh@gmail.com', '$2a$10$iMBl1KnQE6Q5pAcn81mGzeiwUY.swesfmOtUXOvEqN.jnBPZfwFSa', 'Hoàng Kim Anh', 'https://images.unsplash.com/photo-1504257432389-52343af06ae3', 'Travel wedding photographer.', 'Da Lat', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 2320, 154, 0),
(125, 'huynhkimanh', 'huynhkimanh@gmail.com', '$2a$10$B2NoPPI.hDrZNkQ0nheaVOHG262HttgeBwIko/xpxoGc1M5CPI0sy', 'Huỳnh Kim Anh', 'https://images.unsplash.com/photo-1544005313-94ddf0286df2', 'Studio portrait and beauty photographer.', 'Ha Noi', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 2050, 134, 0),
(126, 'phankimanh', 'phankimanh@gmail.com', '$2a$10$oUVyhejF.V/Y9Nt2CZIMM.QkPbAcX3VtEkSmVB0M8eHRAphCCiqlK', 'Phan Kim Anh', 'https://images.unsplash.com/photo-1519345182560-3f2917c472ef', 'Mountain and outdoor photographer.', 'Sa Pa', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 2480, 166, 0),
(127, 'vukimanh', 'vukimanh@gmail.com', '$2a$10$dLgbrHSBHoFpsY8FUqukQ.tIrFzIlsHho034jmMfqEuXr1aMu8eP.', 'Vũ Kim Anh', 'https://images.unsplash.com/photo-1534528741775-53994a69daeb', 'Wedding detail and bridal portrait photographer.', 'Ninh Binh', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 2170, 148, 0),
(128, 'vokimanh', 'vokimanh@gmail.com', '$2a$10$Xb0tPHkOhQgNFK/MH4xtLeER3w5MeLXAyloEIP3zg/uVvQA5J1suO', 'Võ Kim Anh', 'https://images.unsplash.com/photo-1524504388940-b1c1722653e1', 'Lifestyle and travel portrait photographer.', 'Phu Quoc', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1860, 117, 0)
ON DUPLICATE KEY UPDATE
    username = VALUES(username),
    email = VALUES(email),
    password_hash = VALUES(password_hash),
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
(129, 'tranlinhchi', 'tranlinhchi@gmail.com', '$2a$10$GwK1mM1X30tAwa0MpWE5r.Sq2CRIOPfF7Cduzw4rSAHwU0V46WKEC', 'Trần Linh Chi', 'https://images.unsplash.com/photo-1494790108377-be9c29b29330', 'Thích ảnh du lịch và những góc quán cà phê yên tĩnh.', 'Ha Noi', 'USER', 'CLIENT', 1, 1, 'LOCAL', 0, 320, 180, 0),
(130, 'buikimanh', 'buikimanh@gmail.com', '$2a$10$69TuSl4c47rPeJyQ9iF5luy/M3tKk52Q8gmLsCDz7hmQVeGTnVZN6', 'Bùi Kim Anh', 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80', 'Hay lưu lại concept chân dung, beauty và studio tối giản.', 'Da Nang', 'USER', 'CLIENT', 1, 1, 'LOCAL', 0, 280, 165, 0),
(131, 'phamtuananh', 'phamtuananh@gmail.com', '$2a$10$jNNmG2U/9RaNEMLSt/mmxugbIsq/txVeYdSStsc3y5PpwbsJaTnV.', 'Phạm Tuấn Anh', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d', 'Quan tâm ảnh đường phố, xe cộ và đời sống đô thị.', 'Ho Chi Minh City', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 190, 142, 0),
(132, 'hoangbaotran', 'hoangbaotran@gmail.com', '$2a$10$6bcFTqk7yS8SiFOuMue12OSuxezbGdIMd1jgufi/4Aobms0OdqBB6', 'Hoàng Bảo Trân', 'https://images.unsplash.com/photo-1544005313-94ddf0286df2', 'Đang tìm nhiếp ảnh gia chụp ảnh gia đình và lifestyle.', 'Can Tho', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 210, 128, 0),
(133, 'hoanglong', 'hoanglong@gmail.com', '$2a$10$U9/sEitb6NJPzvZXWNpNkeeMHdKvTM2Apqmui6sJDvH28nl3iv3uS', 'Hoàng Long', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e', 'Mê ảnh phong cảnh, trekking và các chuyến đi nhiều mây.', 'Sa Pa', 'USER', 'CLIENT', 1, 1, 'LOCAL', 0, 410, 205, 0),
(134, 'phanthuylinh', 'phanthuylinh@gmail.com', '$2a$10$tSdNTCLq5hEJ4CTA3x.V4uOmxW7Gjshi4vYVjDGHbe4a/THBLc.DS', 'Phan Thùy Linh', 'https://images.unsplash.com/photo-1517841905240-472988babdf9', 'Thích lookbook, thời trang và ảnh profile cá nhân.', 'Hue', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 260, 156, 0),
(135, 'vudangquang', 'vudangquang@gmail.com', '$2a$10$JA4pUdsoWO2sFNk3uLE21Oiu/xcgBGahhq7y5q95ZcF4Cj8DFGAfu', 'Vũ Đăng Quang', 'https://images.unsplash.com/photo-1519345182560-3f2917c472ef', 'Theo dõi ảnh kiến trúc, nội thất và không gian tối giản.', 'Ho Chi Minh City', 'USER', 'CLIENT', 1, 1, 'LOCAL', 0, 175, 120, 0),
(136, 'vongochan', 'vongochan@gmail.com', '$2a$10$hGTnMJuQ/zkgLx5.NZuRnefRf2WVvH7WyMYE9T8zrKLykKx5aMTLu', 'Võ Ngọc Hân', 'https://images.unsplash.com/photo-1534528741775-53994a69daeb', 'Hay đặt lịch chụp ảnh beauty và portrait nhẹ nhàng.', 'Ha Noi', 'USER', 'CLIENT', 1, 1, 'LOCAL', 0, 345, 190, 0),
(137, 'dangvietanh', 'dangvietanh@gmail.com', '$2a$10$cQU6JeGw2XzFWnkxGvkOCey01a9Je0DxNSE3AyRMmrg.2M8gl5HHi', 'Đặng Việt Anh', 'https://images.unsplash.com/photo-1527980965255-d3b416303d12', 'Thích ảnh sự kiện, sân khấu nhỏ và những khung hình nhiều cảm xúc.', 'Hai Phong', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 150, 88, 0),
(138, 'legiabao', 'legiabao@gmail.com', '$2a$10$BJRsvV8nVTMDzU0MnD8VB.e5Kz0W7MJ3zKQkpWbInMhrdhr9/.hmy', 'Lê Gia Bảo', 'https://images.unsplash.com/photo-1508214751196-bcfd4ca60f91', 'Lưu lại ý tưởng chụp ảnh cưới, du lịch và cặp đôi.', 'Da Lat', 'USER', 'CLIENT', 1, 1, 'LOCAL', 0, 390, 210, 0),
(139, 'phamson', 'phamson@gmail.com', '$2a$10$s.uwd3XG./qZfhvZLlfEhuKrHeGoA2MIfD6xIXceXKGLDWFigc5ZG', 'Phạm Sơn', 'https://images.unsplash.com/photo-1504593811423-6dd665756598', 'Quan tâm ảnh sản phẩm, thương mại và bố cục sạch.', 'Nha Trang', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 188, 111, 0),
(140, 'holanhuong', 'holanhuong@gmail.com', '$2a$10$db4zNuG4y2lIh3eCkjFYZ.Ub231mOW8vrQyM8SSI3Gzm.jRWFoEia', 'Hồ Lan Hương', 'https://images.unsplash.com/photo-1524504388940-b1c1722653e1', 'Thích ảnh biển, lifestyle và những bộ ảnh có màu trong trẻo.', 'Phu Quoc', 'USER', 'CLIENT', 1, 1, 'LOCAL', 0, 420, 230, 0),
(141, 'huynhgiabao', 'huynhgiabao@gmail.com', '$2a$10$8SLROsFEJzo08uAG3gjHweN5gPsX5K4m/tA/721AH2hjv68nR6gCO', 'Huỳnh Gia Bảo', 'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d', 'Nhiếp ảnh gia ảnh cưới, sự kiện và cặp đôi tại Hà Nội.', 'Ha Noi', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 2360, 154, 0),
(142, 'phangiabao', 'phangiabao@gmail.com', '$2a$10$YswT2R1Ewv8SD2jjtwwFAOx8UC2hnRSiR0kOBkOYmCuLD8KH9.9AW', 'Phan Gia Bảo', 'https://images.unsplash.com/photo-1494790108377-be9c29b29330', 'Chuyên chân dung nữ, beauty và ảnh profile tự nhiên.', 'Da Nang', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 2180, 132, 0),
(143, 'vugiabao', 'vugiabao@gmail.com', '$2a$10$64p.cSu.2biZBEWk0HQhj.hqawQcHjn4hFKCAZaSZM.xljLDen5Sq', 'Vũ Gia Bảo', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d', 'Chụp street, night city và ảnh editorial đường phố.', 'Ho Chi Minh City', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1840, 118, 0),
(144, 'vogiabao', 'vogiabao@gmail.com', '$2a$10$cwyzavAFmdZnlYcQkaZFn./HWhZkzjECNK6FG0Zikot2rphuwT0zu', 'Võ Gia Bảo', 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80', 'Chụp gia đình, newborn và lifestyle tại miền Tây.', 'Can Tho', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1660, 97, 0),
(145, 'danggiabao', 'danggiabao@gmail.com', '$2a$10$INrjKzcJnYXX5WBqTHrNW.qEsceHW7ngCdAuXn4XnRVquvmo4NIzG', 'Đặng Gia Bảo', 'https://images.unsplash.com/photo-1527980965255-d3b416303d12', 'Chuyên ảnh sản phẩm, lookbook và thương mại điện tử.', 'Ho Chi Minh City', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1920, 126, 0),
(146, 'buigiabao', 'buigiabao@gmail.com', '$2a$10$Q2GzfkyTbSvEKTCJyEFv2.BEnb8YUtiKKHVjBRaJWk7JzC.K2e0cK', 'Bùi Gia Bảo', 'https://images.unsplash.com/photo-1544005313-94ddf0286df2', 'Chụp ảnh du lịch, resort và lifestyle biển.', 'Nha Trang', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 2260, 145, 0),
(147, 'dogiabao', 'dogiabao@gmail.com', '$2a$10$/jcuOkldkzm9UOGN7qsMlujBkGisMrAWhFyGb3tV1hYNuDoijh5.u', 'Đỗ Gia Bảo', 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde', 'Chụp phong cảnh, flycam và outdoor cho các chuyến đi xa.', 'Da Lat', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 2540, 168, 0),
(148, 'hogiabao', 'hogiabao@gmail.com', '$2a$10$es/ecBHjTQIy.8/AwJtCZumRS69UCTS5nwFt6I3cw/Ohe2Iifsq0G', 'Hồ Gia Bảo', 'https://images.unsplash.com/photo-1517841905240-472988babdf9', 'Chụp bridal, fine art portrait và ảnh cưới tối giản.', 'Hue', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 2070, 139, 0)
ON DUPLICATE KEY UPDATE
    username = VALUES(username),
    email = VALUES(email),
    password_hash = VALUES(password_hash),
    full_name = VALUES(full_name),
    profile_picture_url = VALUES(profile_picture_url),
    bio = VALUES(bio),
    location = VALUES(location),
    user_type = VALUES(user_type),
    follower_count = VALUES(follower_count),
    following_count = VALUES(following_count),
    post_count = VALUES(post_count);


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

-- Users 149 and 151-200. Password is username + 123!
INSERT INTO users (
    id, username, email, password_hash, full_name, profile_picture_url, bio,
    location, role, user_type, is_active, is_verified, provider, is_private,
    follower_count, following_count, post_count
) VALUES
(149, 'vominhchau', 'vominhchau@gmail.com', '$2a$10$tgslYfVOqjEdAA1E4m9.P.19/qtYx5pE6ohXlKh6sNoX/HaxwAt8O', 'Võ Minh Châu', 'https://images.unsplash.com/photo-1508214751196-bcfd4ca60f91', 'Hay tìm nhiếp ảnh gia cho ảnh gia đình và chuyến đi cuối tuần.', 'Phu Quoc', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 337, 183, 0),
(151, 'buiminhchau', 'buiminhchau@gmail.com', '$2a$10$/tbiDpRPsUCvHzEjRX0ZLuoIjpntAvgmFFgseinGGB4q.JwYqQ2Va', 'Bùi Minh Châu', 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80', 'Muốn lưu lại ảnh cưới tối giản và khoảnh khắc đời thường.', 'Da Nang', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 83, 197, 0),
(152, 'dangkimanh', 'dangkimanh@gmail.com', '$2a$10$kedXbPimHrn5h8fS2GGHH.r/Yoeu2eFtXCp.N9lXT5OZifWFHyruu', 'Đặng Kim Anh', 'https://images.unsplash.com/photo-1544005313-94ddf0286df2', 'Thích ảnh đường phố, quán cà phê và những buổi chiều có nắng.', 'Ho Chi Minh City', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 96, 204, 0),
(153, 'hominhchau', 'hominhchau@gmail.com', '$2a$10$QUUZEhkGgaQvQ9ehMH8cfOhTuHrN1loPnh6gYnQZfgU0L8uJ1L3La', 'Hồ Minh Châu', 'https://images.unsplash.com/photo-1534528741775-53994a69daeb', 'Hay tìm nhiếp ảnh gia cho ảnh gia đình và chuyến đi cuối tuần.', 'Hue', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 109, 211, 0),
(154, 'dokimanh', 'dokimanh@gmail.com', '$2a$10$WvNYQD8SbBfW.HFZf40/7.ID/LQOJSYdCLXo0sxUFuwDuBG87nhSq', 'Đỗ Kim Anh', 'https://images.unsplash.com/photo-1524504388940-b1c1722653e1', 'Mê ảnh phong cảnh, biển và các bộ ảnh màu trong trẻo.', 'Da Lat', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 122, 218, 0),
(155, 'hokimanh', 'hokimanh@gmail.com', '$2a$10$KWc8u9sAS022Vh/ZsV4P4eT807FXZf6O9VkJ/6vvw9lvcW/zr2MyK', 'Hồ Kim Anh', 'https://images.unsplash.com/photo-1508214751196-bcfd4ca60f91', 'Muốn lưu lại ảnh cưới tối giản và khoảnh khắc đời thường.', 'Nha Trang', 'USER', 'CLIENT', 1, 1, 'LOCAL', 0, 135, 45, 0),
(156, 'ngokimanh', 'ngokimanh@gmail.com', '$2a$10$cSe63tGLKFGiJ2nKM1UC2.cXcSPqeQ6EEmFbHjZpF0y866c7wm4cO', 'Ngô Kim Anh', 'https://images.unsplash.com/photo-1494790108377-be9c29b29330', 'Thích ảnh đường phố, quán cà phê và những buổi chiều có nắng.', 'Hoi An', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 148, 52, 0),
(157, 'duongkimanh', 'duongkimanh@gmail.com', '$2a$10$GcavwmYdXmdzxeiooyRmTO3GKpfOgzxwKPNRez3SyqDLgCSAX8oY2', 'Dương Kim Anh', 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80', 'Hay tìm nhiếp ảnh gia cho ảnh gia đình và chuyến đi cuối tuần.', 'Can Tho', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 161, 59, 0),
(158, 'lykimanh', 'lykimanh@gmail.com', '$2a$10$VuTY2B/Bu68eLmRddrFc4eu1Mjpf.wOjdjt0vp0C.cXzxIvqJvyBa', 'Lý Kim Anh', 'https://images.unsplash.com/photo-1544005313-94ddf0286df2', 'Mê ảnh phong cảnh, biển và các bộ ảnh màu trong trẻo.', 'Sa Pa', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 174, 66, 0),
(159, 'nguyengiabao', 'nguyengiabao@gmail.com', '$2a$10$vG0rIOXBPJzUgPo/0nMRg.THHtXpTZ0X6ULZ2j4Ys35qJonkxbUha', 'Nguyễn Gia Bảo', 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde', 'Muốn lưu lại ảnh cưới tối giản và khoảnh khắc đời thường.', 'Phu Quoc', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 187, 73, 0),
(160, 'trangiabao', 'trangiabao@gmail.com', '$2a$10$ButA.N/b9btWn5cqKypuJOfpWcDVnh9GHGFLMSjN.jaXQTdUaPQ7e', 'Trần Gia Bảo', 'https://images.unsplash.com/photo-1527980965255-d3b416303d12', 'Thích ảnh đường phố, quán cà phê và những buổi chiều có nắng.', 'Ha Noi', 'USER', 'CLIENT', 1, 1, 'LOCAL', 0, 200, 80, 0),
(161, 'phamminhchau', 'phamminhchau@gmail.com', '$2a$10$jzAbp5/iBQjjKzNbNypil.fL.VXCES5j.f9p/uqggdxmZUNZ5QkRm', 'Phạm Minh Châu', 'https://images.unsplash.com/photo-1508214751196-bcfd4ca60f91', 'Hay tìm nhiếp ảnh gia cho ảnh gia đình và chuyến đi cuối tuần.', 'Da Nang', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 213, 87, 0),
(162, 'phamgiabao', 'phamgiabao@gmail.com', '$2a$10$W6MV8wRkC3xDiW7BLLyeL.fdf1buqaEQKhBR87AWpckLkXtid5lR6', 'Phạm Gia Bảo', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e', 'Mê ảnh phong cảnh, biển và các bộ ảnh màu trong trẻo.', 'Ho Chi Minh City', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 226, 94, 0),
(163, 'hoanggiabao', 'hoanggiabao@gmail.com', '$2a$10$kEz8lydAz0bYa6J47nPevuRMGDLPzyzZxrdrWOUJPlAy8dxehIPci', 'Hoàng Gia Bảo', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d', 'Muốn lưu lại ảnh cưới tối giản và khoảnh khắc đời thường.', 'Hue', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 239, 101, 0),
(164, 'phanminhchau', 'phanminhchau@gmail.com', '$2a$10$wTaWLUZS8q7GsjgTZuq8POsgobJZbtOVCAAnS2AIO2ICJTqjompoO', 'Phan Minh Châu', 'https://images.unsplash.com/photo-1544005313-94ddf0286df2', 'Thích ảnh đường phố, quán cà phê và những buổi chiều có nắng.', 'Da Lat', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 252, 108, 0),
(165, 'vuminhchau', 'vuminhchau@gmail.com', '$2a$10$F1gIccuF1CfGWd5FglGIiOsXfcE/28dGwh06qrJ3MVDEeJ5xbgtTO', 'Vũ Minh Châu', 'https://images.unsplash.com/photo-1534528741775-53994a69daeb', 'Hay tìm nhiếp ảnh gia cho ảnh gia đình và chuyến đi cuối tuần.', 'Nha Trang', 'USER', 'CLIENT', 1, 1, 'LOCAL', 0, 265, 115, 0),
(166, 'dangtuankiet', 'dangtuankiet@gmail.com', '$2a$10$n.M.za4UVmeJGVkBCoeRaOkgXRVdEMmz/8haW4sgpZiZZk43bV2dS', 'Đặng Tuấn Kiệt', 'https://images.unsplash.com/photo-1527980965255-d3b416303d12', 'Mê ảnh phong cảnh, biển và các bộ ảnh màu trong trẻo.', 'Hoi An', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 278, 122, 0),
(167, 'dangminhchau', 'dangminhchau@gmail.com', '$2a$10$qvKguUEgbU9XG1hzZZhgu.Rm62NTT9i1jCbYF7pstSEs/TbputmTS', 'Đặng Minh Châu', 'https://images.unsplash.com/photo-1508214751196-bcfd4ca60f91', 'Muốn lưu lại ảnh cưới tối giản và khoảnh khắc đời thường.', 'Can Tho', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 291, 129, 0),
(168, 'dotuankiet', 'dotuankiet@gmail.com', '$2a$10$qTtHyylm2QlgVWv3RSQL.eCn5SxnLfaa6Thsa9RF9j0QVoilH34/S', 'Đỗ Tuấn Kiệt', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e', 'Thích ảnh đường phố, quán cà phê và những buổi chiều có nắng.', 'Sa Pa', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 304, 136, 0),
(169, 'dominhchau', 'dominhchau@gmail.com', '$2a$10$8.wcMSPjdeebAsM1C412/OHXl4.hKOf2wJz6UxnSGlzltPSUMfHIK', 'Đỗ Minh Châu', 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80', 'Hay tìm nhiếp ảnh gia cho ảnh gia đình và chuyến đi cuối tuần.', 'Phu Quoc', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 317, 143, 0),
(170, 'ngotuankiet', 'ngotuankiet@gmail.com', '$2a$10$wMWQUX0b.FDdF1dyUBreauwnRczAxvfdcJ39sjrKYS668uRIC7kDi', 'Ngô Tuấn Kiệt', 'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d', 'Mê ảnh phong cảnh, biển và các bộ ảnh màu trong trẻo.', 'Ha Noi', 'USER', 'CLIENT', 1, 1, 'LOCAL', 0, 330, 150, 0),
(171, 'ngominhchau', 'ngominhchau@gmail.com', '$2a$10$7wR1hr.6Uq0YZHV8ZP4Mt.qEVQaoD.Nf3ky.zAykxHuviHhhYrV/2', 'Ngô Minh Châu', 'https://images.unsplash.com/photo-1534528741775-53994a69daeb', 'Muốn lưu lại ảnh cưới tối giản và khoảnh khắc đời thường.', 'Da Nang', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 343, 157, 0),
(172, 'ngogiabao', 'ngogiabao@gmail.com', '$2a$10$xW3ExsWrXnCQfwYOFfXR8.5HHC4jse7TkwpvwRxtKFpFgeii.kUK6', 'Ngô Gia Bảo', 'https://images.unsplash.com/photo-1527980965255-d3b416303d12', 'Thích ảnh đường phố, quán cà phê và những buổi chiều có nắng.', 'Ho Chi Minh City', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 356, 164, 0),
(173, 'duonggiabao', 'duonggiabao@gmail.com', '$2a$10$C0AwjtqQJqMJ5/pbfikUpes/rZvS.UeTS.LkaID7BBsOg6sAluOO.', 'Dương Gia Bảo', 'https://images.unsplash.com/photo-1547425260-76bcadfb4f2c', 'Hay tìm nhiếp ảnh gia cho ảnh gia đình và chuyến đi cuối tuần.', 'Hue', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 89, 171, 0),
(174, 'lygiabao', 'lygiabao@gmail.com', '$2a$10$6W34slkCk9Rg3bD9eNCmS.bR/bY89v5JBw/cqtb1e3nwAmFRKZsoC', 'Lý Gia Bảo', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e', 'Mê ảnh phong cảnh, biển và các bộ ảnh màu trong trẻo.', 'Da Lat', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 102, 178, 0),
(175, 'nguyenminhchau', 'nguyenminhchau@gmail.com', '$2a$10$h98zJLz8v5s0kSGk9Jjlh.AKu//IDHETxCWA9qr8gmALuGDtyF88a', 'Nguyễn Minh Châu', 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80', 'Muốn lưu lại ảnh cưới tối giản và khoảnh khắc đời thường.', 'Nha Trang', 'USER', 'CLIENT', 1, 1, 'LOCAL', 0, 115, 185, 0),
(176, 'tranminhchau', 'tranminhchau@gmail.com', '$2a$10$ZU/P5sFmw84zKxIV/onSEuNQYoDZ.kqeTflX5gv7vwL9r0khMV7Vi', 'Trần Minh Châu', 'https://images.unsplash.com/photo-1544005313-94ddf0286df2', 'Thích ảnh đường phố, quán cà phê và những buổi chiều có nắng.', 'Hoi An', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 128, 192, 0),
(177, 'leminhchau', 'leminhchau@gmail.com', '$2a$10$VjVPP/FMtJiStQXVyXtkjOmpQRJUIpO.qyUV0R/Oc7vwg3ZdJ24ea', 'Lê Minh Châu', 'https://images.unsplash.com/photo-1534528741775-53994a69daeb', 'Hay tìm nhiếp ảnh gia cho ảnh gia đình và chuyến đi cuối tuần.', 'Can Tho', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 141, 199, 0),
(178, 'hoangtuankiet', 'hoangtuankiet@gmail.com', '$2a$10$wPkQvSG8nB47en/o0MHfNOCQjTYOhKio2HKDYhAj3U71sm.EuVOiW', 'Hoàng Tuấn Kiệt', 'https://images.unsplash.com/photo-1527980965255-d3b416303d12', 'Mê ảnh phong cảnh, biển và các bộ ảnh màu trong trẻo.', 'Sa Pa', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 154, 206, 0),
(179, 'hoangminhchau', 'hoangminhchau@gmail.com', '$2a$10$qNBImdNv7d1LKa7JTuOOXuHG.VcDfnmqN3yvmsmqYfjsWv16CdM6e', 'Hoàng Minh Châu', 'https://images.unsplash.com/photo-1508214751196-bcfd4ca60f91', 'Muốn lưu lại ảnh cưới tối giản và khoảnh khắc đời thường.', 'Phu Quoc', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0, 167, 213, 0),
(180, 'huynhminhchau', 'huynhminhchau@gmail.com', '$2a$10$0RLwbxaAGWJovHFIzysTKOQkycRgPlVRiIJNBSop59EVpIf6AcsMW', 'Huỳnh Minh Châu', 'https://images.unsplash.com/photo-1494790108377-be9c29b29330', 'Thích ảnh đường phố, quán cà phê và những buổi chiều có nắng.', 'Ha Noi', 'USER', 'CLIENT', 1, 1, 'LOCAL', 0, 180, 40, 0),
(181, 'vutuankiet', 'vutuankiet@gmail.com', '$2a$10$RLv7evZIemG23VXqCAkXLuY4IjkQ6WTxDi/XEBxMYnHarJJ8w62Vy', 'Vũ Tuấn Kiệt', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d', 'Chụp ảnh cưới, lễ hỏi và các buổi gặp mặt thân mật.', 'Da Nang', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1777, 47, 0),
(182, 'votuankiet', 'votuankiet@gmail.com', '$2a$10$CmocdkhRo3bsCMkbvpP7x.HcF1oem94aWOts8mMF/eqAvLsI2sGTm', 'Võ Tuấn Kiệt', 'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d', 'Chuyên ảnh du lịch, lifestyle và chân dung ngoài trời.', 'Ho Chi Minh City', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1794, 54, 0),
(183, 'buiphuongvy', 'buiphuongvy@gmail.com', '$2a$10$ED3Q2h6oX6QR1qvSX/lpu.rwV8.GUWKHSASNS7CIiVQQ52cYuokO.', 'Bùi Phương Vy', 'https://images.unsplash.com/photo-1534528741775-53994a69daeb', 'Chụp sự kiện, lookbook và những câu chuyện đời thường.', 'Hue', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1811, 61, 0),
(184, 'buituankiet', 'buituankiet@gmail.com', '$2a$10$Ko7rpU.x3NyjGj70unD/H.zXsp8PBmMCRllsLK4XrDoj0MevUNUo.', 'Bùi Tuấn Kiệt', 'https://images.unsplash.com/photo-1527980965255-d3b416303d12', 'Nhận chụp chân dung tự nhiên, ảnh cặp đôi và kỷ niệm gia đình.', 'Da Lat', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1828, 68, 0),
(185, 'hophuongvy', 'hophuongvy@gmail.com', '$2a$10$wY3WQcn/q91Zg1PYWxy3FescRaM3A1/2Brgs0RlotqE6HvJQKjaNK', 'Hồ Phương Vy', 'https://images.unsplash.com/photo-1508214751196-bcfd4ca60f91', 'Chụp ảnh cưới, lễ hỏi và các buổi gặp mặt thân mật.', 'Nha Trang', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1845, 75, 0),
(186, 'hotuankiet', 'hotuankiet@gmail.com', '$2a$10$GXUpVi5kjguI.JqV6Uly5eBAZBAh0nYz3.io/GJXGW2jeTafPSgqG', 'Hồ Tuấn Kiệt', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e', 'Chuyên ảnh du lịch, lifestyle và chân dung ngoài trời.', 'Hoi An', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1862, 82, 0),
(187, 'duongphuongvy', 'duongphuongvy@gmail.com', '$2a$10$SFaycpdAeD04h1zVb0dPG.tGgn8M3bz5BtBUafLqhE8vDrataLdcC', 'Dương Phương Vy', 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80', 'Chụp sự kiện, lookbook và những câu chuyện đời thường.', 'Can Tho', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1879, 89, 0),
(188, 'duongtuankiet', 'duongtuankiet@gmail.com', '$2a$10$iK94HxItvuPzfO5h.yX0Je9i4g9g6/krEcTq49GZc9IvXEkyoJiJC', 'Dương Tuấn Kiệt', 'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d', 'Nhận chụp chân dung tự nhiên, ảnh cặp đôi và kỷ niệm gia đình.', 'Sa Pa', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1896, 96, 0),
(189, 'duongminhchau', 'duongminhchau@gmail.com', '$2a$10$ggVG4yVhIFb6ueLx0vshlOxPC.axVjzvjyUX.5b1exG90VT7WeE66', 'Dương Minh Châu', 'https://images.unsplash.com/photo-1534528741775-53994a69daeb', 'Chụp ảnh cưới, lễ hỏi và các buổi gặp mặt thân mật.', 'Phu Quoc', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1913, 103, 0),
(190, 'nguyenphuongvy', 'nguyenphuongvy@gmail.com', '$2a$10$w2bmZ/lI4VXDOTtqQvsd1.9i/w17zae4zfkpBSppJ9Lq0h/NI5RVa', 'Nguyễn Phương Vy', 'https://images.unsplash.com/photo-1524504388940-b1c1722653e1', 'Chuyên ảnh du lịch, lifestyle và chân dung ngoài trời.', 'Ha Noi', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1930, 110, 0),
(191, 'nguyentuankiet', 'nguyentuankiet@gmail.com', '$2a$10$sbe7jCKueXHqC24Z9n6KBuKFYUr2S/8p82BtmhlwXJ6Coin/Z67uu', 'Nguyễn Tuấn Kiệt', 'https://images.unsplash.com/photo-1547425260-76bcadfb4f2c', 'Chụp sự kiện, lookbook và những câu chuyện đời thường.', 'Da Nang', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1947, 117, 0),
(192, 'trantuankiet', 'trantuankiet@gmail.com', '$2a$10$pQWhcrBVoC4IHs8tNVuNs.ykeMeg6HiKwFy/2Hwg/4lAZG79vt5dq', 'Trần Tuấn Kiệt', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e', 'Nhận chụp chân dung tự nhiên, ảnh cặp đôi và kỷ niệm gia đình.', 'Ho Chi Minh City', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1964, 124, 0),
(193, 'letuankiet', 'letuankiet@gmail.com', '$2a$10$dAlVyxzZ/d9GGBg9N5YYIOYpHHXhRTr2zkmkmu/1d3/W0F5jTFKFq', 'Lê Tuấn Kiệt', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d', 'Chụp ảnh cưới, lễ hỏi và các buổi gặp mặt thân mật.', 'Hue', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1981, 131, 0),
(194, 'phamtuankiet', 'phamtuankiet@gmail.com', '$2a$10$WbxOMDYhXYems.rs7c4lPewzfyNzLdVaYRerqHZJbwrm36a2dxOby', 'Phạm Tuấn Kiệt', 'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d', 'Chuyên ảnh du lịch, lifestyle và chân dung ngoài trời.', 'Da Lat', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 1998, 138, 0),
(195, 'huynhphuongvy', 'huynhphuongvy@gmail.com', '$2a$10$0SnNPDr7RS6VUkER6SoJ3Os7MtVwJjeGoDkpassjQRuFyo6kO3DnC', 'Huỳnh Phương Vy', 'https://images.unsplash.com/photo-1534528741775-53994a69daeb', 'Chụp sự kiện, lookbook và những câu chuyện đời thường.', 'Nha Trang', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 2015, 145, 0),
(196, 'huynhtuankiet', 'huynhtuankiet@gmail.com', '$2a$10$Q8yxScsdHwdwdm4QD42uiOIh0DuvUF5VUmjR/2Z7/w9a6ke6jtiyC', 'Huỳnh Tuấn Kiệt', 'https://images.unsplash.com/photo-1527980965255-d3b416303d12', 'Nhận chụp chân dung tự nhiên, ảnh cặp đôi và kỷ niệm gia đình.', 'Hoi An', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 2032, 152, 0),
(197, 'phantuankiet', 'phantuankiet@gmail.com', '$2a$10$H/HZEK7G6Uyal.DLzxL7IO3bylT4yxWuy18JooSLwjBGBpDTrT.0C', 'Phan Tuấn Kiệt', 'https://images.unsplash.com/photo-1547425260-76bcadfb4f2c', 'Chụp ảnh cưới, lễ hỏi và các buổi gặp mặt thân mật.', 'Can Tho', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 2049, 159, 0),
(198, 'vophuongvy', 'vophuongvy@gmail.com', '$2a$10$4VNyRJ2FoExZKyZiboay/.xK5YMp96HB8rtndXRRtx2r1MUtdSZMC', 'Võ Phương Vy', 'https://images.unsplash.com/photo-1494790108377-be9c29b29330', 'Chuyên ảnh du lịch, lifestyle và chân dung ngoài trời.', 'Sa Pa', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 2066, 166, 0),
(199, 'dangphuongvy', 'dangphuongvy@gmail.com', '$2a$10$akCN.pntkxVyTq6WL6hU7uLFGDnZh92DDankrKXmB9oiU.fCwFvjC', 'Đặng Phương Vy', 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80', 'Chụp sự kiện, lookbook và những câu chuyện đời thường.', 'Phu Quoc', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 2083, 173, 0),
(200, 'dohuuphuc', 'dohuuphuc@gmail.com', '$2a$10$3Mdeo2cu3maGUEqOdv.Xue1XYdVi89.3uUh9mDbQ1wWtZ5dKlIm4q', 'Đỗ Hữu Phúc', 'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d', 'Nhận chụp chân dung tự nhiên, ảnh cặp đôi và kỷ niệm gia đình.', 'Ha Noi', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0, 2100, 180, 0)
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
(181, 181, 'Ảnh của Vũ Tuấn Kiệt', 'Nhận chụp chân dung, cặp đôi và kỷ niệm tại Da Nang.', '["Portrait","Lifestyle","Outdoor"]', 750000.00, 'VND', 'Da Nang', 1, 4.80, 12),
(182, 182, 'Ảnh của Võ Tuấn Kiệt', 'Nhận chụp chân dung, cặp đôi và kỷ niệm tại Ho Chi Minh City.', '["Portrait","Lifestyle","Outdoor"]', 800000.00, 'VND', 'Ho Chi Minh City', 1, 4.80, 12),
(183, 183, 'Ảnh của Bùi Phương Vy', 'Nhận chụp chân dung, cặp đôi và kỷ niệm tại Hue.', '["Portrait","Lifestyle","Outdoor"]', 850000.00, 'VND', 'Hue', 1, 4.80, 12),
(184, 184, 'Ảnh của Bùi Tuấn Kiệt', 'Nhận chụp chân dung, cặp đôi và kỷ niệm tại Da Lat.', '["Portrait","Lifestyle","Outdoor"]', 500000.00, 'VND', 'Da Lat', 1, 4.80, 12),
(185, 185, 'Ảnh của Hồ Phương Vy', 'Nhận chụp chân dung, cặp đôi và kỷ niệm tại Nha Trang.', '["Portrait","Lifestyle","Outdoor"]', 550000.00, 'VND', 'Nha Trang', 1, 4.80, 12),
(186, 186, 'Ảnh của Hồ Tuấn Kiệt', 'Nhận chụp chân dung, cặp đôi và kỷ niệm tại Hoi An.', '["Portrait","Lifestyle","Outdoor"]', 600000.00, 'VND', 'Hoi An', 1, 4.80, 12),
(187, 187, 'Ảnh của Dương Phương Vy', 'Nhận chụp chân dung, cặp đôi và kỷ niệm tại Can Tho.', '["Portrait","Lifestyle","Outdoor"]', 650000.00, 'VND', 'Can Tho', 1, 4.80, 12),
(188, 188, 'Ảnh của Dương Tuấn Kiệt', 'Nhận chụp chân dung, cặp đôi và kỷ niệm tại Sa Pa.', '["Portrait","Lifestyle","Outdoor"]', 700000.00, 'VND', 'Sa Pa', 1, 4.80, 12),
(189, 189, 'Ảnh của Dương Minh Châu', 'Nhận chụp chân dung, cặp đôi và kỷ niệm tại Phu Quoc.', '["Portrait","Lifestyle","Outdoor"]', 750000.00, 'VND', 'Phu Quoc', 1, 4.80, 12),
(190, 190, 'Ảnh của Nguyễn Phương Vy', 'Nhận chụp chân dung, cặp đôi và kỷ niệm tại Ha Noi.', '["Portrait","Lifestyle","Outdoor"]', 800000.00, 'VND', 'Ha Noi', 1, 4.80, 12),
(191, 191, 'Ảnh của Nguyễn Tuấn Kiệt', 'Nhận chụp chân dung, cặp đôi và kỷ niệm tại Da Nang.', '["Portrait","Lifestyle","Outdoor"]', 850000.00, 'VND', 'Da Nang', 1, 4.80, 12),
(192, 192, 'Ảnh của Trần Tuấn Kiệt', 'Nhận chụp chân dung, cặp đôi và kỷ niệm tại Ho Chi Minh City.', '["Portrait","Lifestyle","Outdoor"]', 500000.00, 'VND', 'Ho Chi Minh City', 1, 4.80, 12),
(193, 193, 'Ảnh của Lê Tuấn Kiệt', 'Nhận chụp chân dung, cặp đôi và kỷ niệm tại Hue.', '["Portrait","Lifestyle","Outdoor"]', 550000.00, 'VND', 'Hue', 1, 4.80, 12),
(194, 194, 'Ảnh của Phạm Tuấn Kiệt', 'Nhận chụp chân dung, cặp đôi và kỷ niệm tại Da Lat.', '["Portrait","Lifestyle","Outdoor"]', 600000.00, 'VND', 'Da Lat', 1, 4.80, 12),
(195, 195, 'Ảnh của Huỳnh Phương Vy', 'Nhận chụp chân dung, cặp đôi và kỷ niệm tại Nha Trang.', '["Portrait","Lifestyle","Outdoor"]', 650000.00, 'VND', 'Nha Trang', 1, 4.80, 12),
(196, 196, 'Ảnh của Huỳnh Tuấn Kiệt', 'Nhận chụp chân dung, cặp đôi và kỷ niệm tại Hoi An.', '["Portrait","Lifestyle","Outdoor"]', 700000.00, 'VND', 'Hoi An', 1, 4.80, 12),
(197, 197, 'Ảnh của Phan Tuấn Kiệt', 'Nhận chụp chân dung, cặp đôi và kỷ niệm tại Can Tho.', '["Portrait","Lifestyle","Outdoor"]', 750000.00, 'VND', 'Can Tho', 1, 4.80, 12),
(198, 198, 'Ảnh của Võ Phương Vy', 'Nhận chụp chân dung, cặp đôi và kỷ niệm tại Sa Pa.', '["Portrait","Lifestyle","Outdoor"]', 800000.00, 'VND', 'Sa Pa', 1, 4.80, 12),
(199, 199, 'Ảnh của Đặng Phương Vy', 'Nhận chụp chân dung, cặp đôi và kỷ niệm tại Phu Quoc.', '["Portrait","Lifestyle","Outdoor"]', 850000.00, 'VND', 'Phu Quoc', 1, 4.80, 12),
(200, 200, 'Ảnh của Đỗ Hữu Phúc', 'Nhận chụp chân dung, cặp đôi và kỷ niệm tại Ha Noi.', '["Portrait","Lifestyle","Outdoor"]', 500000.00, 'VND', 'Ha Noi', 1, 4.80, 12)
ON DUPLICATE KEY UPDATE
    user_id = VALUES(user_id),
    title = VALUES(title),
    description = VALUES(description);

SET FOREIGN_KEY_CHECKS = 1;
