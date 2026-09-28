USE instagallery;
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- Create the four demo packages for every photographer that does not already have them.
INSERT INTO photographer_services (
    photographer_id, name, category, price, currency, duration_minutes,
    description, includes, is_active
)
SELECT
    u.id,
    pkg.name,
    pkg.category,
    pkg.price,
    'VND',
    pkg.duration_minutes,
    pkg.description,
    pkg.includes,
    1
FROM users u
JOIN (
    SELECT 'Chân Dung Cơ Bản' AS name, 'PORTRAIT' AS category, 1500000.00 AS price, 120 AS duration_minutes,
           'Gói chụp chân dung gọn nhẹ cho ảnh cá nhân, profile và concept đơn giản.' AS description,
           '["2 giờ chụp","50 ảnh gốc","20 ảnh chỉnh sửa","Tư vấn tạo dáng"]' AS includes
    UNION ALL
    SELECT 'Chân Dung Nghệ Thuật', 'PORTRAIT', 2800000.00, 240,
           'Gói chụp chân dung cao cấp với concept, trang phục và trang điểm cơ bản.',
           '["4 giờ chụp","100 ảnh gốc","40 ảnh chỉnh sửa","Trang điểm cơ bản","Tư vấn concept"]'
    UNION ALL
    SELECT 'Chụp Cưới Tiêu Chuẩn', 'WEDDING', 8000000.00, 480,
           'Gói chụp phóng sự cưới và ảnh cặp đôi trong ngày cưới.',
           '["8 giờ chụp","300 ảnh gốc","80 ảnh chỉnh sửa","2 thợ chụp"]'
    UNION ALL
    SELECT 'Chụp Ảnh Sự Kiện', 'EVENT', 3500000.00, 180,
           'Gói chụp sự kiện, hội nghị, tiệc và bàn giao ảnh nhanh (recap).',
           '["3 giờ chụp","150 ảnh gốc","30 ảnh chỉnh sửa","Ảnh recap trong 72 giờ"]'
) pkg
WHERE u.user_type = 'PHOTOGRAPHER'
  AND u.deleted_at IS NULL
  AND NOT EXISTS (
      SELECT 1
      FROM photographer_services existing
      WHERE existing.photographer_id = u.id
        AND existing.name = pkg.name
  );

UPDATE photographer_services 
SET name = 'Chân Dung Cơ Bản',
    description = 'Gói chụp chân dung gọn nhẹ cho ảnh cá nhân, profile và concept đơn giản.',
    includes = '["2 giờ chụp","50 ảnh gốc","20 ảnh chỉnh sửa","Tư vấn tạo dáng"]'
WHERE name = 'Portrait Basic' OR name = 'Chân Dung Cơ Bản';

UPDATE photographer_services 
SET name = 'Chân Dung Nghệ Thuật',
    description = 'Gói chụp chân dung cao cấp với concept, trang phục và trang điểm cơ bản.',
    includes = '["4 giờ chụp","100 ảnh gốc","40 ảnh chỉnh sửa","Trang điểm cơ bản","Tư vấn concept"]'
WHERE name = 'Portrait Premium' OR name = 'Chân Dung Nghệ Thuật';

UPDATE photographer_services 
SET name = 'Chụp Cưới Tiêu Chuẩn',
    description = 'Gói chụp phóng sự cưới và ảnh cặp đôi trong ngày cưới.',
    includes = '["8 giờ chụp","300 ảnh gốc","80 ảnh chỉnh sửa","2 thợ chụp"]'
WHERE name = 'Wedding Standard' OR name = 'Chụp Cưới Tiêu Chuẩn';

UPDATE photographer_services 
SET name = 'Chụp Ảnh Sự Kiện',
    description = 'Gói chụp sự kiện, hội nghị, tiệc và bàn giao ảnh nhanh (recap).',
    includes = '["3 giờ chụp","150 ảnh gốc","30 ảnh chỉnh sửa","Ảnh recap trong 72 giờ"]'
WHERE name = 'Event Coverage' OR name = 'Chụp Ảnh Sự Kiện';
