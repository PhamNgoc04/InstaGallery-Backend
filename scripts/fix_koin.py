import os
import re

routes_dir = r"d:\InstaGallery\instagallery-backend\src\main\kotlin\com\instagallery\routes"

for filename in os.listdir(routes_dir):
    if filename.endswith(".kt"):
        filepath = os.path.join(routes_dir, filename)
        with open(filepath, "r", encoding="utf-8") as f:
            content = f.read()
        
        # Replace import
        content = content.replace("import org.koin.ktor.ext.inject", "import org.koin.java.KoinJavaComponent.getKoin")
        
        # Replace 'val serviceName: ServiceClass by inject()' -> 'val serviceName = getKoin().get<ServiceClass>()'
        content = re.sub(r"val\s+([a-zA-Z0-9_]+)\s*:\s*([a-zA-Z0-9_]+)\s+by\s+inject\(\)", r"val \1 = getKoin().get<\2>()", content)
        
        with open(filepath, "w", encoding="utf-8") as f:
            f.write(content)
        
        print(f"Fixed {filename}")
