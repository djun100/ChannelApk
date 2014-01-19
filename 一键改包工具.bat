@echo off
java -jar test.jar unpack
java -jar test.jar repandpack
call sign
java -jar test.jar zipalign
java -jar test.jar clear