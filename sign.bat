@echo off
for /f "delims=" %%i in ('dir packed\*.apk /b') do (
jarsigner -verbose -keystore android_1234.p12 -storepass 1234 -signedjar signed\%%i packed\%%i 1 -digestalg SHA1 -sigalg MD5withRSA -storetype pkcs12
)
