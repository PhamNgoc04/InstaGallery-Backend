$ErrorActionPreference = "Stop"
try {
    .\gradlew run --stacktrace 2>&1 | Out-File -FilePath gradle_real_log.txt -Encoding utf8
} catch {
    Write-Host "Gradle run failed"
}
