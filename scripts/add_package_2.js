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

// 1. Create 5. Explore Folder
let exploreFolder = collection.item.find(i => i.name.includes("5. Explore"));

if (!exploreFolder) {
  exploreFolder = { "name": "5. Explore (Khám phá)", "item": [] };
  collection.item.push(exploreFolder);
}

const exploreRequestsToAdd = [];
exploreRequestsToAdd.push(createRequest("GET Trending Hashtags", "GET", "/api/v1/explore/trending?limit=10"));
exploreRequestsToAdd.push(createRequest("GET Explore Feed", "GET", "/api/v1/explore?page=1&limit=20&tag=nature"));

exploreRequestsToAdd.forEach(req => {
    if (!exploreFolder.item.find(i => i.name === req.name)) {
        exploreFolder.item.push(req);
    }
});


// 2. Create 6. Search Folder
let searchFolder = collection.item.find(i => i.name.includes("6. Search"));

if (!searchFolder) {
  searchFolder = { "name": "6. Search (Tìm kiếm)", "item": [] };
  collection.item.push(searchFolder);
}

const searchRequestsToAdd = [];
searchRequestsToAdd.push(createRequest("GET Global Search", "GET", "/api/v1/search?q=ngoc&type=ALL&limit=20"));
searchRequestsToAdd.push(createRequest("GET Search History", "GET", "/api/v1/search/history"));
searchRequestsToAdd.push(createRequest("DELETE Clear Search History", "DELETE", "/api/v1/search/history"));

searchRequestsToAdd.forEach(req => {
    if (!searchFolder.item.find(i => i.name === req.name)) {
        searchFolder.item.push(req);
    }
});

fs.writeFileSync(filePath, JSON.stringify(collection, null, 4));
console.log("Package 2 (Explore & Search) injected successfully.");
