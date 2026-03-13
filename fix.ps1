$dir = "d:\InstaGallery\instagallery-backend\src\main\kotlin\com\instagallery"
$files = Get-ChildItem -Path $dir -Recurse -Filter *.kt

foreach ($file in $files) {
    $path = $file.FullName
    $text = [System.IO.File]::ReadAllText($path)
    $orig = $text

    $text = $text -replace '\.slice\(', '.select('
    $text = $text -replace '\.select\s*\{', '.selectAll().where {'
    # fix the chaining issue: .select(A, B).selectAll().where { -> .select(A, B).where {
    $text = $text -replace '\.select\s*\(([^)]+)\)\s*\.selectAll\(\)\.where\s*\{', '.select($1).where {'

    $text = $text.Replace("MediaTagsTable.tagName", "MediaTagsTable.name")
    
    if ($path -match "PostRepository.kt") {
        $text = $text.Replace("MutableList<MediaDto>", "MutableList<FeedMediaDto>")
        $text = $text.Replace("     MediaDto(", "     FeedMediaDto(")
        $text = $text.Replace("MediaDto(", "FeedMediaDto(")
    }
    
    if ($path -match "UserRepository.kt" -and $text -notmatch "import org.jetbrains.exposed.sql.and") {
        $text = $text.Replace("import org.jetbrains.exposed.sql.*", "import org.jetbrains.exposed.sql.*`nimport org.jetbrains.exposed.sql.and")
    }

    if ($path -match "ChatService.kt" -and $text -notmatch "import kotlinx.coroutines.isActive") {
        $text = $text.Replace("import io.ktor.websocket.*", "import io.ktor.websocket.*`nimport kotlinx.coroutines.isActive")
    }

    if ($text -cne $orig) {
        # Enforce UTF-8 without BOM so Kotlin compiler is happy
        $utf8NoBom = New-Object System.Text.UTF8Encoding($False)
        [System.IO.File]::WriteAllText($path, $text, $utf8NoBom)
        Write-Host "Updated $($file.Name)"
    }
}
Write-Host "Done Script"
