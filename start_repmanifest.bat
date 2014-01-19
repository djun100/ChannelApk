@echo off
java -jar doapk.jar unpack
java -jar doapk.jar repmanifest_pack
call sign
java -jar doapk.jar zipalign
java -jar doapk.jar clear