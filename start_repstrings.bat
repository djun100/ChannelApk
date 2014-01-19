@echo off
java -jar doapk.jar unpack
java -jar doapk.jar repstrings_pack
call sign
java -jar doapk.jar zipalign
java -jar doapk.jar clear