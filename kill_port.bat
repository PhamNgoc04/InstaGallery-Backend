@echo off
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8080') do (
    echo KIlling PID %%a
    taskkill /f /pid %%a
)
echo Done!
