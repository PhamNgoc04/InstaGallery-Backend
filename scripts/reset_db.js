const fs = require('fs');
const mysql = require('mysql2/promise');
const path = require('path');

async function resetDatabase() {
    const seedFilePath = path.join(__dirname, '..', 'database_seed.sql');
    try {
        const sqlContent = fs.readFileSync(seedFilePath, 'utf8');
        
        const connection = await mysql.createConnection({
            host: 'localhost',
            user: 'root',
            password: '123456789',
            database: 'instagallery',
            multipleStatements: true
        });

        console.log('Đang thực thi database_seed.sql...');
        await connection.query(sqlContent);
        
        console.log('✅ Reset Database thành công!');
        await connection.end();
    } catch (error) {
        console.error('❌ Lỗi khi reset database:', error.message);
    }
}

resetDatabase();
