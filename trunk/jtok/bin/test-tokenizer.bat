@echo off
set TOKENIZER_CLASSPATH=..\conf;..\classes;..\lib\jtok.jar;..\lib\log4j.jar

java -classpath %TOKENIZER_CLASSPATH% de.dfki.lt.tools.tokenizer.TestJTok


