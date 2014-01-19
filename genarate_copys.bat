@echo off
for /f "delims=" %%i in ('dir unpacked /b') do (

xcopy %~dp0unpacked\%%i\*.* %~dp0unpacked\%%i1\ /s /e)