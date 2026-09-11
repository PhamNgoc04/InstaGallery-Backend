-- =========================================================================
-- INSTAGALLERY MOCK DATA SEED SCRIPT (Dành cho Database Mới Tinh)
-- =========================================================================
-- Lưu ý: Mật khẩu của tất cả User dưới đây đều rỗng hoặc giả lập Hash. 
-- Bạn nên đăng nhập bằng Google hoặc xài API /reset-password nếu hash không khớp.
-- =========================================================================

USE instagallery;

-- 1. Xóa dữ liệu cũ để tránh lỗi Duplicate Key (nếu chạy lại)
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE users;
TRUNCATE TABLE portfolios;
TRUNCATE TABLE posts;
TRUNCATE TABLE post_media;
TRUNCATE TABLE comments;
TRUNCATE TABLE albums;
TRUNCATE TABLE album_media;
SET FOREIGN_KEY_CHECKS = 1;



-- ==========================================
-- BẢNG 1: NGƯỜI DÙNG (USERS)
-- ==========================================
INSERT INTO users (id, username, email, password_hash, full_name, profile_picture_url, role, user_type, is_active, is_verified, provider, is_private) VALUES
(1, 'superadmin', 'admin@instagallery.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TsphxXK', 'Hệ Thống Quản Trị', 'https://cdn-icons-png.flaticon.com/512/3135/3135715.png', 'ADMIN', 'CLIENT', 1, 1, 'LOCAL', 0),
(2, 'thaopham', 'thaopham02@gmail.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TsphxXK', 'Phạm Phương Thảo', 'https://i.pinimg.com/736x/89/90/48/899048ab0cc455154006fdb9676964b3.jpg', 'USER', 'PHOTOGRAPHER', 1, 1, 'LOCAL', 0),
(3, 'hienpham', 'hienpham89@gmail.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TsphxXK', 'Phạm Văn Hiền', NULL, 'USER', 'CLIENT', 1, 0, 'LOCAL', 1),
(4, 'ngocpb04', 'ngocpb04@gmail.com', '$2a$12$/PCBsTE.KHqz.VMYZVzLcOsebL1nHQL6mfpY1cSwjGQWzstfklXHa', 'Phạm Ngọc', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e', 'USER', 'CLIENT', 1, 1, 'LOCAL', 0),
(5, 'nhitt83', 'nhitt83@gmail.com', '$2a$12$.qiH4qO3ZdGiqm500mv6sex1YDbEItrj1i0jf7pWG.O1NXOfqO7ES', 'Trần Nhi', 'https://images.unsplash.com/photo-1494790108377-be9c29b29330', 'USER', 'CLIENT', 1, 0, 'LOCAL', 0);

-- ==========================================
-- BẢNG 2: HỒ SƠ THỢ CHỤP (PORTFOLIOS)
-- ==========================================
INSERT INTO portfolios (id, user_id, description, specialties, hourly_rate, service_area, is_available) VALUES
(1, 2, 'Photographer tự do chuyên chụp nàng thơ, lookbook tại Hà Nội.', '["Chân dung", "Nghệ thuật"]', 500000.0, 'Hà Nội', 1);

-- ==========================================
-- BẢNG 3: BÀI ĐĂNG (POSTS)
-- ==========================================
INSERT INTO posts (id, user_id, caption, location, visibility, comment_visibility) VALUES
(1, 2, 'Một buổi chiều hoàng hôn Hồ Tây lộng gió 🌅🌿\n#hanoi #sunset', 'Hồ Tây, Hà Nội', 'PUBLIC', 'ALLOW_ALL'),
(2, 2, 'Concept rùng rợn Halloween tháng 10. Makeup siêu đỉnh! 🎃🖤', 'Studio A', 'PUBLIC', 'ALLOW_ALL'),
(3, 3, 'My first post hi everyone!', 'Sài Gòn', 'PUBLIC', 'FOLLOWERS_ONLY');

-- ==========================================
-- BẢNG 4: HÌNH ẢNH BÀI ĐĂNG (POST_MEDIA)
-- ==========================================
INSERT INTO post_media (id, post_id, media_file_url, media_type, position) VALUES
(1, 1, 'https://tse2.mm.bing.net/th/id/OIP.ybq_2H5aKIZStGW4VwVTbgHaEe?rs=1&pid=ImgDetMain&o=7&rm=3', 'IMAGE', 1),
(2, 2, 'https://th.bing.com/th/id/OIP.ZyptoLIGPFoUTsaUBArYjQHaEK?o=7rm=3&rs=1&pid=ImgDetMain&o=7&rm=3', 'IMAGE', 1),
(3, 2, 'https://tse2.mm.bing.net/th/id/OIP.vkuxXHB0meCfO7V_NWClCQHaEo?rs=1&pid=ImgDetMain&o=7&rm=3', 'IMAGE', 2),
(4, 3, 'https://i.pinimg.com/originals/0d/8d/fb/0d8dfb79ba66f4c412aae69abd1dd2ea.jpg', 'IMAGE', 1);

-- ==========================================
-- BẢNG 5: BỘ SƯU TẬP (ALBUMS) & ĐÍNH KÈM GÓC CHỤP 
-- ==========================================
INSERT INTO albums (id, user_id, title, description, cover_image_url, is_private) VALUES
(1, 2, 'Fall Collection 2026', 'Các bộ ảnh chụp ngoại cảnh Mùa Thu', 'https://tse3.mm.bing.net/th/id/OIP.ltA6ZlfEeiqSosscT0gxOQHaEK?rs=1&pid=ImgDetMain&o=7&rm=3', 0);

INSERT INTO album_media (id, album_id, post_id) VALUES
(1, 1, 1);

-- ==========================================
-- BẢNG 6: BÌNH LUẬN (COMMENTS)
-- ==========================================
INSERT INTO comments (id, post_id, user_id, content) VALUES
(1, 1, 3, 'Hình đẹp quá chị ơi!! Nhận lịch cuối tuần không ạ?'),
(2, 1, 2, 'Dạ còn nhé b, inbox m nhen ❤️');

-- HOÀN TẤT SEED DATA! 
-- Bây giờ hãy Restart Ứng dụng Backend Ktor.
