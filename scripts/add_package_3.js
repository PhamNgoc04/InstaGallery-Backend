const fs = require('fs');
const path = require('path');

const filePath = path.join(__dirname, 'InstaGallery_Local.postman_collection.json');
const data = fs.readFileSync(filePath, 'utf8');
const collection = JSON.parse(data);

function createRequest(name, method, urlPath, queryParams = []) {
  const req = {
    "name": name,
    "request": {
      "auth": {
        "type": "bearer",
        "bearer": [
          {
            "key": "token",
            "value": "{{JWT_TOKEN}}",
            "type": "string"
          }
        ]
      },
      "method": method,
      "header": [],
      "url": {
        "raw": "{{BASE_URL}}" + urlPath,
        "host": [
          "{{BASE_URL}}"
        ],
        "path": urlPath.split('?')[0].split('/').filter(p => !!p)
      }
    },
    "response": []
  };

  if (urlPath.includes('?')) {
      const queryStr = urlPath.split('?')[1];
      req.request.url.query = queryStr.split('&').map(q => {
          const [k,v] = q.split('=');
          return { key: k, value: v };
      });
  }

  return req;
}

function createWsRequest(name, urlPath) {
    return {
        "name": name,
        "protocolProfileBehavior": {
            "disableBodyPruning": true
        },
        "request": {
            "method": "SOCKET", // WebSockets in Postman
            "header": [],
            "url": {
                "raw": "ws://localhost:8080" + urlPath,
                "protocol": "ws",
                "host": [
                    "localhost"
                ],
                "port": "8080",
                "path": urlPath.split('?')[0].split('/').filter(p => !!p),
                "query": urlPath.split('?')[1]?.split('&').map(q => {
                    const [k,v] = q.split('=');
                    return { key: k, value: v };
                }) || []
            }
        },
        "response": []
    };
}


// Create 7. Chat Folder
let chatFolder = collection.item.find(i => i.name.includes("7. Chat"));

if (!chatFolder) {
  chatFolder = { "name": "7. Chat & Messages (Tin nhắn)", "item": [] };
  collection.item.push(chatFolder);
}

const requestsToAdd = [];
requestsToAdd.push(createRequest("GET Conversations List", "GET", "/api/v1/chat/conversations"));
requestsToAdd.push(createRequest("GET Messages in Conversation", "GET", "/api/v1/chat/conversations/1/messages?page=1&limit=50"));

// Note: Postman Collection JSON format for WebSockets is slightly different. 
// Standard HTTP requests are enough to document the REST endpoints.
// WebSockets in Postman are handled in a separate workspace UI, but we can 
// add a documentation text block or a standard GET request that fails gracefully to show the URL.

// We will add a mock Request to store the WS URL
requestsToAdd.push({
    "name": "WS Real-time Connection (Note: Use Postman WebSocket Request type)",
    "request": {
      "method": "GET",
      "header": [],
      "url": {
        "raw": "ws://localhost:8080/api/v1/ws/chat?token={{JWT_TOKEN}}",
        "protocol": "ws",
        "host": ["localhost"],
        "port": "8080",
        "path": ["api", "v1", "ws", "chat"],
        "query": [{ "key": "token", "value": "{{JWT_TOKEN}}" }]
      },
      "description": "BẠN HÃY TẢI POSTMAN MỚI NHẤT -> BẤM VÀO NÚT 'NEW' -> CHỌN 'WEBSOCKET REQUEST' ĐỂ KẾT NỐI VÀO ĐƯỜNG DẪN NÀY LÀ ĐƯỢC NHÉ!"
    },
    "response": []
});


requestsToAdd.forEach(req => {
    if (!chatFolder.item.find(i => i.name === req.name)) {
        chatFolder.item.push(req);
    }
});

fs.writeFileSync(filePath, JSON.stringify(collection, null, 4));
console.log("Package 3 (Chat & WebSockets) injected successfully.");
