const fs = require('fs');
const path = require('path');

const filePath = path.join(__dirname, '..', 'InstaGallery_Local.postman_collection.json');
const data = fs.readFileSync(filePath, 'utf8');
const collection = JSON.parse(data);

function createRequest(name, method, urlPath, bodyContent = null, customHeaders = []) {
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
      "header": customHeaders,
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

  if (bodyContent) {
      req.request.body = {
          "mode": "raw",
          "raw": JSON.stringify(bodyContent, null, 4),
          "options": {
              "raw": {
                  "language": "json"
              }
          }
      };
  }

  return req;
}

// 1. Add to Auth Folder
const authFolder = collection.item.find(i => i.name.includes("1. Auth"));
if (authFolder) {
    const authRequestsToAdd = [
        createRequest("POST Refresh Token", "POST", "/api/v1/auth/refresh", null, [
            { "key": "Authorization", "value": "Bearer {{YOUR_REFRESH_TOKEN_HERE}}", "type": "text" }
        ]),
        createRequest("POST Logout", "POST", "/api/v1/auth/logout", null, [
            { "key": "X-Refresh-Token", "value": "{{YOUR_REFRESH_TOKEN_HERE}}", "type": "text" }
        ]),
        createRequest("POST Forgot Password", "POST", "/api/v1/auth/forgot-password", { "email": "test@example.com" }),
        createRequest("PUT Change Password", "PUT", "/api/v1/auth/change-password", { "oldPassword": "old", "newPassword": "new" })
    ];

    authRequestsToAdd.forEach(req => {
        if (!authFolder.item.find(i => i.name === req.name)) {
            authFolder.item.push(req);
        }
    });
}

// 2. Add to Users Folder
const userFolder = collection.item.find(i => i.name.includes("2. Users"));
if (userFolder) {
    const userRequestsToAdd = [
        createRequest("PUT Update Profile", "PUT", "/api/v1/users/me", { "fullName": "New Name", "bio": "New Bio updated via API", "website": "https://newwebsite.com" }),
        createRequest("PUT Update Avatar (Mock)", "PUT", "/api/v1/users/me/avatar"),
        createRequest("GET My Sessions", "GET", "/api/v1/users/me/sessions", null, [
             { "key": "X-Refresh-Token", "value": "{{YOUR_REFRESH_TOKEN_HERE}}", "type": "text" }
        ]),
        createRequest("DELETE Revoke Session", "DELETE", "/api/v1/users/me/sessions/1")
    ];

    userRequestsToAdd.forEach(req => {
        if (!userFolder.item.find(i => i.name === req.name)) {
            userFolder.item.push(req);
        }
    });
}

fs.writeFileSync(filePath, JSON.stringify(collection, null, 4));
console.log("Package 4 (Extended Auth & Profile) injected successfully.");
