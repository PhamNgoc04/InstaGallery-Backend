const mysql = require('mysql2/promise');

async function createMockConversation() {
    const connection = await mysql.createConnection({
        host: 'localhost',
        user: 'root',
        password: '123456789',
        database: 'instagallery'
    });

    try {
        console.log("Connected to the database. Spawning a test conversation...");
        
        // Check if users 1 and 2 exist (Assuming they do based on earlier steps)
        
        // 1. Insert into Conversations table
        const [convResult] = await connection.execute(
            `INSERT INTO conversations (type, created_at, updated_at) VALUES ('DIRECT', NOW(), NOW())`
        );
        const conversationId = convResult.insertId;
        
        // 2. Insert into ConversationMembers table
        await connection.execute(
            `INSERT INTO conversation_members (conversation_id, user_id, joined_at) VALUES (?, ?, NOW())`,
            [conversationId, 1]
        );
        
        await connection.execute(
            `INSERT INTO conversation_members (conversation_id, user_id, joined_at) VALUES (?, ?, NOW())`,
            [conversationId, 2]
        );

        console.log(`Successfully created Conversation ID = ${conversationId} between User 1 and User 2.`);
    } catch (e) {
        console.error("Error creating conversation. Maybe DB config is different?", e.message);
    } finally {
        await connection.end();
    }
}

createMockConversation();
