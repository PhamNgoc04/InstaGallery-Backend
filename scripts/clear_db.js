const mysql = require('mysql2/promise');

async function clearDatabase() {
    try {
        const connection = await mysql.createConnection({
            host: 'localhost',
            user: 'root',
            password: '123456789',
            database: 'instagallery',
            multipleStatements: true
        });

        console.log('Đang tiến hành dọn sạch toàn bộ dữ liệu trong database...');
        
        // Tắt kiểm tra khóa ngoại
        await connection.query('SET FOREIGN_KEY_CHECKS = 0;');
        
        // Lấy danh sách tất cả các bảng trong database instagallery
        const [rows] = await connection.query(`
            SELECT table_name 
            FROM information_schema.tables 
            WHERE table_schema = 'instagallery'
        `);

        // Quét qua và xoá sạch từng bảng một
        for (let row of rows) {
            const tableName = row.TABLE_NAME || row.table_name;
            await connection.query(`TRUNCATE TABLE \`${tableName}\``);
            console.log(`- Đã làm sạch bảng: ${tableName}`);
        }

        // Bật lại kiểm tra khóa ngoại
        await connection.query('SET FOREIGN_KEY_CHECKS = 1;');

        console.log('✅ Xóa thành công. Database hiện tại đã trống rỗng hoàn toàn, chỉ giữ lại cấu trúc bảng!');
        await connection.end();
    } catch (error) {
        console.error('❌ Lỗi khi xóa trắng database:', error.message);
    }
}

clearDatabase();
