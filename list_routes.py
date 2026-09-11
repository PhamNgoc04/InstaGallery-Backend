import os
import re

routes_dir = r"d:\InstaGallery\instagallery-backend\src\main\kotlin\com\instagallery\routes"
pattern = re.compile(r'\b(get|post|put|delete|patch|options|head|webSocket)\s*\(\s*"([^"]+)"')

endpoints = []

for file in os.listdir(routes_dir):
    if file.endswith(".kt"):
        filepath = os.path.join(routes_dir, file)
        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()
        
        matches = pattern.findall(content)
        for method, path in matches:
            endpoints.append((file, method.upper(), path))

print(f"Total endpoints scanned: {len(endpoints)}")
# Group by file
from collections import defaultdict
grouped = defaultdict(list)
for file, method, path in endpoints:
    grouped[file].append((method, path))

for file, items in sorted(grouped.items()):
    print(f"\nFile: {file} ({len(items)} endpoints)")
    for method, path in items:
        print(f"  {method:<6} {path}")
