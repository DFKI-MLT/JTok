JTok V1.10
written by Joerg Steffen
Email: steffen@dfki.de
(c) DFKI, 2003-2012

This product is licensed to you under the GNU Lesser General Public License, Version 2.1.
You may not use this product except in compliance with the license.

JTok provides a tokenizer that identifies paragraphs,
sentences and tokens of an input text. Non-word tokens are further
classified into abbreviations, numbers, punctuation and clitics.

The package currently supports English, German and Italian, but can be
extended easily to other languages.

The output of JTok is an instance of
de.dfki.lt.tools.tokenizer.annotate.AnnotatedString, but there are
methods that translate an AnnotatedString into an XML representation
or into instances of Paragraph, TextUnit and Token classes.


Installation:
=============
JTok uses the Maven build tool (http://maven.apache.org/).

Execute "mvn install" to create target/jtok-core-1.10.jar that contains the
core tokenizer classes with all required resources. This includes a default
configuration as found in src/main/resources. It is used when no configuration
is found elsewhere on the classpath. To use your own configuration, make sure
to add your configuration folder in front of jtok-core-1.10.jar in the classpath.

Additionally, the API documentation of JTok can be found in target/apidocs. 

Execute "mvn package assembly:single" to create a binary distribution of JTok
in target/jtok-core-1.10-bin.zip that also contains all 3rd party libraries.


Testing:
========
Execute "mvn test" to run the unit tests. They use the files in src/test/resources.


Content:
========
src/main/assembly
     The assembly descriptor and readme file of the binary distribution.

src/main/java
     The Java sources.

src/main/resources
     The JTok configuration files, especially the language descriptions.
     For each supported language there is a subdirectory that contains
     several language description files in the XML format that describe
     the token type hierarchy and definition and rules for matching the
     different token types. For further details, see commments in the
     XML files.
     After modifying configuration files, execute "mvn compile" to make
     them available to the runtime system or add src/main/resources to
     the JVM classpath in front of jtok-core-1.10.jar.
     Another option for user specific JTok configuration is to put it
     into a folder 'jtok-user' in the classpath. This location is
     searched first for any JTok configuration. The expected folder
     structure in 'jtok-user' is the same as in 'conf', so don't forget
     the 'jtok' directly under 'jtok-user'.

src/main/scripts
     tokenize: a simple script for playing around with JTok. It takes
     a file name and its language as argument and returns the tokenized
     document as pretty-printed Paragraphs, TextUnits and Tokens instances.
     tokenixe: same as tokenize, but creates an XMl output format.

src/test/java
     The Java sources of the test classes.

src/test/resources
     Some test data for German and English and a directory with some test
     files for specific stages of the tokenization process. 
     These files are used in the unit tests. The expected results are located
     in the expected-results directory.


Penn Treebank Token Replacements:
=================================
JTok additionally provides its tokens in PTB format. So some tokens have 
an alternative surface string by applying the following replacements:
- '/' with '\/'
- '*' with '\*'
- '(' with '-LRB-'
- ')' with '-RRB-'
- '[' with '-LSB-'
- ']' with '-RSB-'
- '{' with '-LCB-'
- '}' with '-RCB-'
- all opening punctuation with '``'
- all closing punctuation with ''''



