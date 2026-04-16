@echo off
./gradlew run --stacktrace > gradle_log.txt 2>&1
echo DONE >> gradle_log.txt
