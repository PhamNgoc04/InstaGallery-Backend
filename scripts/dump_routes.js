const fs = require('fs');
const path = require('path');
const routesDir = 'd:/InstaGallery/instagallery-backend/src/main/kotlin/com/instagallery/routes';

const files = fs.readdirSync(routesDir).filter(f => f.endsWith('.kt'));
const implemented = [];

files.forEach(file => {
    const content = fs.readFileSync(path.join(routesDir, file), 'utf8');
    let basePath = '';
    const routeMatch = content.match(/route\("(\/api\/v1\/[^"]+)"\)/);
    if (routeMatch) {
         basePath = routeMatch[1];
    } else {
        const fallbackMatch = content.match(/route\("(\/[^"]+)"\)/);
        if (fallbackMatch) basePath = fallbackMatch[1];
    }
    
    // Catch get("/...")
    const methodRegex1 = /(get|post|put|delete|patch|webSocket)\("([^"]*)"\)/g;
    let match;
    while ((match = methodRegex1.exec(content)) !== null) {
        let httpMethod = match[1].toUpperCase();
        let relativePath = match[2];
        let fullPath = basePath;
        if (relativePath && relativePath !== '/') {
             fullPath = (`${basePath}/${relativePath}`).replace(/\/+/g, '/');
        }
        implemented.push(`${httpMethod} ${fullPath}`);
    }
    
    // Catch get { ... }
    const methodRegex2 = /(get|post|put|delete|patch|webSocket)\s*\{/g;
    while ((match = methodRegex2.exec(content)) !== null) {
        let httpMethod = match[1].toUpperCase();
        // Check if there's a negative lookbehind for string matching, just crude check:
        // usually it's `get {` on its own line after indentation
        implemented.push(`${httpMethod} ${basePath}`);
    }
});

implemented.sort();
fs.writeFileSync('d:/InstaGallery/instagallery-backend/implemented_routes.txt', implemented.join('\n'));
console.log('Total routes found:', implemented.length);
