import os
dir_path = r'd:\InstaGallery\instagallery-backend\src\main\kotlin\com\instagallery'
count = 0
for root, _, files in os.walk(dir_path):
    for f in files:
        if f.endswith('.kt'):
            path = os.path.join(root, f)
            with open(path, 'r', encoding='utf-8') as file:
                text = file.read()
            orig = text
            text = text.replace('.slice(', '.select(')
            text = text.replace('.select {', '.selectAll().where {')
            text = text.replace('.select{', '.selectAll().where {')
            text = text.replace('.select(UsersTable.createdAt.date(), UsersTable.id.count()).selectAll().where {', '.select(UsersTable.createdAt.date(), UsersTable.id.count()).where {')
            text = text.replace('.select(PostsTable.createdAt.date(), PostsTable.id.count()).selectAll().where {', '.select(PostsTable.createdAt.date(), PostsTable.id.count()).where {')
            text = text.replace('.select(BookingsTable.createdAt.date(), BookingsTable.id.count()).selectAll().where {', '.select(BookingsTable.createdAt.date(), BookingsTable.id.count()).where {')
            
            text = text.replace('MediaTagsTable.tagName', 'MediaTagsTable.name')
            if 'PostRepository.kt' in f:
                text = text.replace('MutableList<MediaDto>', 'MutableList<FeedMediaDto>')
                text = text.replace('MediaDto(', 'FeedMediaDto(')
            
            if 'UserRepository.kt' in f and 'import org.jetbrains.exposed.sql.and' not in text:
                text = text.replace('import org.jetbrains.exposed.sql.*', 'import org.jetbrains.exposed.sql.*\nimport org.jetbrains.exposed.sql.and')
            
            if 'ChatService.kt' in f and 'import kotlinx.coroutines.isActive' not in text:
                text = text.replace('import io.ktor.websocket.*', 'import io.ktor.websocket.*\nimport kotlinx.coroutines.isActive')

            if text != orig:
                with open(path, 'w', encoding='utf-8') as file:
                    file.write(text)
                count += 1
                print("Updated", f)
print("Total updated:", count)
