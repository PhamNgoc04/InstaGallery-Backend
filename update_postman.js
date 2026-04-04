const fs = require('fs');

const p = 'd:/InstaGallery/instagallery-backend/InstaGallery_Local.postman_collection.json';
const data = JSON.parse(fs.readFileSync(p, 'utf8'));

const adminGroup = {
    "name": "7. Admin Dashboard",
    "item": [
        {
            "name": "GET Admin Stats",
            "request": {
                "auth": { "type": "bearer", "bearer": [ { "key": "token", "value": "{{JWT_TOKEN}}", "type": "string" } ] },
                "method": "GET",
                "header": [],
                "url": {
                    "raw": "{{BASE_URL}}/api/v1/admin/stats",
                    "host": [ "{{BASE_URL}}" ],
                    "path": [ "api", "v1", "admin", "stats" ]
                }
            },
            "response": []
        },
        {
            "name": "PUT Ban User",
            "request": {
                "auth": { "type": "bearer", "bearer": [ { "key": "token", "value": "{{JWT_TOKEN}}", "type": "string" } ] },
                "method": "PUT",
                "header": [],
                "body": {
                    "mode": "raw",
                    "raw": "{\n    \"isBanned\": true,\n    \"reason\": \"Vi phạm quy tắc cộng đồng\"\n}",
                    "options": { "raw": { "language": "json" } }
                },
                "url": {
                    "raw": "{{BASE_URL}}/api/v1/admin/users/1/ban",
                    "host": [ "{{BASE_URL}}" ],
                    "path": [ "api", "v1", "admin", "users", "1", "ban" ]
                }
            },
            "response": []
        }
    ]
};

const albumGroup = {
    "name": "5. Albums & Gallery",
    "item": [
        {
            "name": "POST Create Album",
            "request": {
                "auth": { "type": "bearer", "bearer": [ { "key": "token", "value": "{{JWT_TOKEN}}", "type": "string" } ] },
                "method": "POST",
                "header": [],
                "body": {
                    "mode": "raw",
                    "raw": "{\n    \"title\": \"Mùa Thu Hà Nội\",\n    \"isPrivate\": false\n}",
                    "options": { "raw": { "language": "json" } }
                },
                "url": {
                    "raw": "{{BASE_URL}}/api/v1/albums",
                    "host": [ "{{BASE_URL}}" ],
                    "path": [ "api", "v1", "albums" ]
                }
            },
            "response": []
        }
    ]
};

const bookingGroup = {
    "name": "6. Portfolio & Bookings",
    "item": [
        {
            "name": "POST Update Availability",
            "request": {
                "auth": { "type": "bearer", "bearer": [ { "key": "token", "value": "{{JWT_TOKEN}}", "type": "string" } ] },
                "method": "POST",
                "header": [],
                "body": {
                    "mode": "raw",
                    "raw": "[\n    {\n        \"dayOfWeek\": \"MONDAY\",\n        \"startTime\": \"08:00:00\",\n        \"endTime\": \"17:00:00\"\n    }\n]",
                    "options": { "raw": { "language": "json" } }
                },
                "url": {
                    "raw": "{{BASE_URL}}/api/v1/portfolios/me/availability",
                    "host": [ "{{BASE_URL}}" ],
                    "path": [ "api", "v1", "portfolios", "me", "availability" ]
                }
            },
            "response": []
        }
    ]
};

data.item.push(albumGroup);
data.item.push(bookingGroup);
data.item.push(adminGroup);

fs.writeFileSync(p, JSON.stringify(data, null, 4));
console.log("Successfully updated Postman collection!");
