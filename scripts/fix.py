import glob
import os

dir_path = r'd:\InstaGallery\instagallery-backend\src\main\kotlin\com\instagallery\repositories'
for file_path in glob.glob(os.path.join(dir_path, '*.kt')):
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()

    new_content = content.replace('.select {', '.selectAll().where {')
    new_content = new_content.replace('.select{', '.selectAll().where {')
    new_content = new_content.replace('.slice(', '.select(')
    
    if new_content != content:
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(new_content)
        print(f"Fixed {file_path}")
print("Done")
