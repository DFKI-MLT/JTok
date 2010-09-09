JTok V1.6
written by Joerg Steffen
Email: steffen@dfki.de
(c) DFKI, 2003-2010

This package provides a tokenizer that identifies paragraphs,
sentences and tokens of an input text. Non-word tokens are further
classified into abbreviations, numbers, punctuation and clitics.

The package currently supports English, German and Italian, but can be
extended easily to other languages.

The output of the tokenizer is an instance of
de.dfki.lt.tools.tokenizer.annotate.AnnotatedString, but there are
methods that translate an AnnotatedString into an XML representation
or into instances of Paragraph, TextUnit and Token classes.

The configuration of the tokenizer is done via XML configuration files
in the conf directory. For each language, there are configuration
files that describe the token type hierarchy and definition and rules
for matching the different token types. For details, see documentation
in config files. With this approach, the tokenizer can easily be
modified without recompiling the Java sources.


Installation:
=============
- Unpack the JTok archive to target folder using "gunzip" and "tar -xvf".

Testing:
========
- Execute "test-tokenizer" in the "bin" folder: This starts the tokenizer
  and processes some german and english text from the /data directory. 

Content:
========
/bin 
     test-tokenizer: This sends some test data to the tokenizer as defined 
     in conf/testJTok.cfg and returns the result as pretty-printed
     Paragraphs, TextUnits and Tokens instances.
 
     tokenize: a simple script for playing around with JTok. It takes
     a file name and its language as argument and returns the tokenized
     document as pretty-printed Paragraphs, TextUnits and Tokens instances.
     Make sure you runs this script in the "bin" folder to make sure that
     the JVM classpath is set correctly.

/conf 
     This contains the JTok configuration files, especially the language 
     descriptions. For each supported language there is a subdirectory
     that contains several language description files in the XML format.
     For further details, see commments in the XML files.
     Some config files contain pathnames. These are searched in the
     classpath of the JVM.

/data
     This contains some test data for german and english and a directory
     with some test files for specific stages of the tokenization process. 

/doc 
     This contains the API documentation of the JTok system created by
     Javadoc. 

/lib 
     This contains .jar archives of packages the system uses.

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



