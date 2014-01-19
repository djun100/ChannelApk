@echo off

for /f "delims=" %%i in ('dir unpacked /b') do (
apktool b ".\unpacked\%%i" ".\packed\%%~ni.apk")
exit