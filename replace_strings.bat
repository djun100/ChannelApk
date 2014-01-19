@echo off

setlocal enabledelayedexpansion

for /f "delims=" %%i in ('type test.txt') do (
    
set str=%%i
    
set str=!str:111=2222!
    
echo !str!>>tmp.txt

pause
)

echo 循环完成了

pause
