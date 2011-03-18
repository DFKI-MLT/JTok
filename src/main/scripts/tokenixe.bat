@echo off

if NOT "%3"=="" (
mvn exec:java -Dexec.mainClass=de.dfki.lt.tools.tokenizer.output.XMLOutputter -Dexec.args="%1 %2 %3"
exit 0
)

if NOT "%2"=="" (
mvn exec:java -Dexec.mainClass=de.dfki.lt.tools.tokenizer.output.XMLOutputter -Dexec.args="%1 %2"
exit 0
)

echo "usage: tokenixe <filename> <language> [encoding]"
