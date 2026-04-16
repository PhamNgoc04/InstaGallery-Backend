const fs = require('fs');
const path = require('path');

const routesDir = 'd:/InstaGallery/instagallery-backend/src/main/kotlin/com/instagallery/routes';
const postmanFile = 'd:/InstaGallery/instagallery-backend/InstaGallery_Local.postman_collection.json';

// Read existing postman collection
const postmanRaw = fs.readFileSync(postmanFile, 'utf8');
const collection = JSON.parse(postmanRaw);

const files = fs.readdirSync(routesDir).filter(f => f.endsWith('.kt'));

function generateItem(name, method, urlPath) {
    const rawUrl = `{{BASE_URL}}${urlPath}`;
    const host = ["{{BASE_URL}}"];
    const pathParts = urlPath.split('/').filter(p => p !== '');
    
    // Convert path params like {id} to Postman syntax :id
    const finalParts = pathParts.map(p => p.startsWith('{') && p.endsWith('}') ? `:${p.substring(1, p.length - 1)}` : p);
    
    const request = {
        auth: {
            "type": "bearer",
            "bearer": [
                {
                    "key": "token",
                    "value": "{{JWT_TOKEN}}",
                    "type": "string"
                }
            ]
        },
        method: method,
        header: [],
        url: {
            raw: rawUrl,
            host: host,
            path: finalParts,
            variable: pathParts.filter(p => p.startsWith('{') && p.endsWith('}')).map(p => ({
                key: p.substring(1, p.length - 1),
                value: "1"
            }))
        }
    };
    
    // Add default JSON headers and raw body for non GET/DELETE
    if (method === 'POST' || method === 'PUT' || method === 'PATCH') {
        request.header.push({
            key: "Content-Type",
            value: "application/json",
            type: "text"
        });
        request.body = {
            mode: "raw",
            raw: "{\n    // TODO: Add payload \n}",
            options: {
                raw: { language: "json" }
            }
        };
    }
    
    return {
        name: name,
        request: request,
        response: []
    };
}

// Keep only the first 4 folders which user configured manually (Auth, Users, Interactions, Posts)
collection.item = collection.item.slice(0, 4);
let folderIndex = 5;

files.forEach(file => {
    // Check if we should skip because it was already defined in the first 4 folders
    if (file === 'AuthRoutes.kt' || file === 'UserRoutes.kt' || file === 'InteractionRoutes.kt' || file === 'PostRoutes.kt') {
        return; 
    }

    const content = fs.readFileSync(path.join(routesDir, file), 'utf8');
    
    let basePath = '';
    const routeMatch = content.match(/route\("(\/api\/v1\/[^"]+)"\)/);
    if (routeMatch) {
         basePath = routeMatch[1];
    } else {
        const fallbackMatch = content.match(/route\("(\/[^"]+)"\)/);
        if (fallbackMatch) basePath = fallbackMatch[1];
    }
    
    const nameStr = file.replace('Routes.kt', '');
    const folderName = `${folderIndex}. ${nameStr} (${nameStr})`;
    
    const items = [];
    
    // 1. Catch get("...")
    const methodRegex1 = /(get|post|put|delete|patch|webSocket)\("([^"]*)"\)/g;
    let match;
    
    while ((match = methodRegex1.exec(content)) !== null) {
        let httpMethod = match[1].toUpperCase();
        let relativePath = match[2];
        
        let fullPath = basePath;
        if (relativePath && relativePath !== '/') {
             fullPath = (`${basePath}/${relativePath}`).replace(/\/+/g, '/');
        }
        
        // Skip websockets or convert them if necessary, but Postman allows them so we just keep them as GET
        if (httpMethod === 'WEBSOCKET') httpMethod = "GET"; 
        
        const itemName = `${httpMethod} ${fullPath}`;
        items.push(generateItem(itemName, httpMethod, fullPath));
    }
    
    // 2. Catch get { ... }
    const methodRegex2 = /(get|post|put|delete|patch|webSocket)\s*\{/g;
    while ((match = methodRegex2.exec(content)) !== null) {
        let httpMethod = match[1].toUpperCase();
        if (httpMethod === 'WEBSOCKET') httpMethod = "GET";
        
        const itemName = `${httpMethod} ${basePath}`;
        items.push(generateItem(itemName, httpMethod, basePath));
    }
    
    if (items.length > 0) {
        // Sort items by url and then by method
        items.sort((a,b) => a.name.localeCompare(b.name));
        
        // Remove exact duplicates
        const uniqueItems = items.filter((v,i,a)=>a.findIndex(v2=>(v2.name===v.name))===i);

        collection.item.push({
            name: folderName,
            item: uniqueItems
        });
        folderIndex++;
    }
});

// Update the file
fs.writeFileSync(postmanFile, JSON.stringify(collection, null, 4));
console.log('Postman collection updated successfully, including block routes.');
