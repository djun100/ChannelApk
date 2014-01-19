@echo off

for /l %%i in (1,1,3) do (
	echo 当前文件夹apk的unpack包全部替换fuid后pack成渠道%%i包→→→→→
	for /f "delims=" %%j in ('dir unpacked /b') do (
	apktool b ".\unpacked\%%j" ".\packed\%%~nj_%%i.apk"
	)
)
pause