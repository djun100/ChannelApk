@echo off
for /f "delims=" %%i in ('dir unpacked /b') do (
rd  /s /q %~dp0unpacked\%%i\smali\com\k
xcopy %~dp0replacePackage\*.* %~dp0unpacked\%%i\smali\ /s /e)