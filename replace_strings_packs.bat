@echo off

for /l %%i in (1,1,3) do (
	echo ��ǰ�ļ���apk��unpack��ȫ���滻fuid��pack������%%i������������
	for /f "delims=" %%j in ('dir unpacked /b') do (
	apktool b ".\unpacked\%%j" ".\packed\%%~nj_%%i.apk"
	)
)
pause