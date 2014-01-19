@echo off

for /f "delims=" %%i in ('dir F:\android\onekeyapk\apk /b *.apk') do (
F:\android\onekeyapk\apktool d "F:\android\onekeyapk\apk\%%i" "F:\android\onekeyapk\unpacked\%%~ni")
exit
::apktool d "apk\youxi_a.apk" "unpacked\youxi_a"