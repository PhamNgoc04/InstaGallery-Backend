import os
import re

routes_dir = r"d:\InstaGallery\instagallery-backend\src\main\kotlin\com\instagallery\routes"

for filename in os.listdir(routes_dir):
    if filename.endswith(".kt"):
        filepath = os.path.join(routes_dir, filename)
        with open(filepath, "r", encoding="utf-8") as f:
            content = f.read()
        
        # Replace the global getKoin import with the ktor ext import
        content = content.replace("import org.koin.java.KoinJavaComponent.getKoin", "import org.koin.ktor.ext.getKoin")
        
        # Replace 'val serviceName = getKoin().get<ServiceClass>()' -> 'val serviceName = application.getKoin().get<ServiceClass>()'
        content = re.sub(r"val\s+([a-zA-Z0-9_]+)\s*=\s*getKoin\(\)\.get<([^>]+)>\(\)", r"val \1 = application.getKoin().get<\2>()", content)
        
        with open(filepath, "w", encoding="utf-8") as f:
            f.write(content)
        
        print(f"Fixed {filename}")
