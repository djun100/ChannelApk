@echo off
java -jar main.jar unpack
java -jar main.jar repmanifest_pack
call sign
java -jar main.jar zipalign
java -jar main.jar clear