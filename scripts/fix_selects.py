import os
import re

dir_path = r'd:\InstaGallery\instagallery-backend\src\main\kotlin\com\instagallery'
repo_path = os.path.join(dir_path, 'repositories')
service_path = os.path.join(dir_path, 'services')

def fix_file(file_path):
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    original_content = content

    # Fix .slice(...).select { ... } -> .select(...).where { ... }
    # Since they might be on multiple lines, we can do a robust regex
    # Match `.slice(any)` followed optionally by whitespace/newlines then `.select {`
    # We replace `.slice` with `.select` and `.select {` with `.where {`
    
    # regex 1: slice to select
    content = re.sub(r'\.slice\(', '.select(', content)
    
    # regex 2: .select { ... } to .selectAll().where { ... }
    # BUT if it's immediately after a .select(...) because of the previous slice replacement, we need .where {
    # It's easier to just do: Table.select { -> Table.selectAll().where {
    # BUT wait, how to distinguish?
    # If it is preceded by ), \n  it was chained.
    # Let's just do a manual pass line by line or simple regex.
    res = []
    lines = content.split('\n')
    for i, line in enumerate(lines):
        # MediaTagsTable.tagName -> MediaTagsTable.name
        line = line.replace('MediaTagsTable.tagName', 'MediaTagsTable.name')
        
        # AdminRepository .slice to .select and .select { to .where {
        # Actually, let's just do text replacements for known patterns.
        
        # If line has .select {
        if '.select {' in line or '.select{' in line:
            # Check if this is a chained call after a slice/select(...)
            # A chained call in this codebase usually looks like: \s+ .select {
            # Let's look at previous non-empty line
            prev_line = lines[i-1].strip() if i > 0 else ""
            if prev_line.endswith(')') and '.select(' in prev_line:
                # It was chained to .select(...)
                line = line.replace('.select {', '.where {').replace('.select{', '.where {')
            elif prev_line.startswith('.select('):
                 line = line.replace('.select {', '.where {').replace('.select{', '.where {')
            else:
                 line = line.replace('.select {', '.selectAll().where {').replace('.select{', '.selectAll().where {')
                 
        res.append(line)
        
    content = '\n'.join(res)
    
    # Fix MediaDto -> FeedMediaDto in PostRepository
    if 'PostRepository.kt' in file_path:
        content = content.replace('MutableList<MediaDto>', 'MutableList<FeedMediaDto>')
        content = content.replace('      MediaDto(', '      FeedMediaDto(')
        # Also fix the import if needed, but they are in same package com.instagallery.models.common.*
        
    if 'UserRepository.kt' in file_path:
        if 'import org.jetbrains.exposed.sql.and' not in content:
            content = content.replace('import org.jetbrains.exposed.sql.*', 'import org.jetbrains.exposed.sql.*\nimport org.jetbrains.exposed.sql.and')
            
    if 'ChatService.kt' in file_path:
        if 'import kotlinx.coroutines.isActive' not in content:
            content = content.replace('import kotlinx.serialization.encodeToString', 'import kotlinx.coroutines.isActive\nimport kotlinx.serialization.encodeToString')

    if content != original_content:
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(content)
        print(f"Fixed {os.path.basename(file_path)}")

for root, _, files in os.walk(dir_path):
    for name in files:
        if name.endswith('.kt'):
            fix_file(os.path.join(root, name))

print("All files processed.")
