import os
import re

dir_path = r'd:\InstaGallery\instagallery-backend\src\main\kotlin\com\instagallery'
repo_path = os.path.join(dir_path, 'repositories')
service_path = os.path.join(dir_path, 'services')

def fix_file(file_path):
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()

    orig = content
    
    # Simple search and replace for type issues
    content = content.replace("MediaTagsTable.tagName", "MediaTagsTable.name")
    if 'PostRepository.kt' in file_path:
        content = content.replace('MutableList<MediaDto>', 'MutableList<FeedMediaDto>')
        content = content.replace('MediaDto(', 'FeedMediaDto(')
    if 'UserRepository.kt' in file_path:
        if 'import org.jetbrains.exposed.sql.and' not in content:
             content = content.replace('import org.jetbrains.exposed.sql.*', 'import org.jetbrains.exposed.sql.*\nimport org.jetbrains.exposed.sql.and')
    if 'ChatService.kt' in file_path:
        if 'import kotlinx.coroutines.isActive' not in content:
            content = content.replace('import io.ktor.websocket.*', 'import io.ktor.websocket.*\nimport kotlinx.coroutines.isActive')

    # Regex replacements for Exposed API
    # 1. Any `.slice(...)` -> `.select(...)`
    # Warning: .select(...) on its own is fine.
    content = re.sub(r'\.slice\(', r'.select(', content)
    
    # 2. `.select {` -> either `.where {` or `.selectAll().where {`
    lines = content.split('\n')
    new_lines = []
    for i, line in enumerate(lines):
        if '.select {' in line or '.select{' in line:
            # check if it is chained to `.select(...)` which was formerly `.slice(...)`
            prev_line = lines[i-1].strip() if i > 0 else ""
            if prev_line.endswith(')') and '.select(' in prev_line:
                line = line.replace('.select {', '.where {').replace('.select{', '.where {')
            elif prev_line.startswith('.select('):
                line = line.replace('.select {', '.where {').replace('.select{', '.where {')
            elif '.select(' in line and ')' in line.split('.select')[0]: 
                # e.g., UsersTable.select(A).select { B } -> UsersTable.select(A).where { B }
                # But it's usually spanning lines. If it's on the same line:
                if line.find('.select {') > line.find('.select('):
                    line = line.replace('.select {', '.where {').replace('.select{', '.where {')
                else:
                    line = line.replace('.select {', '.selectAll().where {').replace('.select{', '.selectAll().where {')
            else:
                line = line.replace('.select {', '.selectAll().where {').replace('.select{', '.selectAll().where {')
        new_lines.append(line)
        
    content = '\n'.join(new_lines)
    
    if content != orig:
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(content)
        print(f"Fixed {file_path}")

for root, _, files in os.walk(dir_path):
    for f in files:
        if f.endswith('.kt'):
            fix_file(os.path.join(root, f))
print('Done!')
