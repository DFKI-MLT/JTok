@echo off
set TOKENIZER_CLASSPATH=..\conf;..\classes;..\lib\jtok.jar;..\lib\log4j.jar

if NOT "%3"=="" (
java -classpath %TOKENIZER_CLASSPATH% de.dfki.lt.tools.tokenizer.output.XMLOutputter %1 %2 %3
exit 0
)

if NOT "%2"=="" (
java -classpath %TOKENIZER_CLASSPATH% de.dfki.lt.tools.tokenizer.output.XMLOutputter %1 %2
exit 0
)

echo "usage: tokenixe <filename> <language> [encoding]"
