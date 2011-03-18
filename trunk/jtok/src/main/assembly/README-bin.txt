JTok V1.9
written by Joerg Steffen
Email: steffen@dfki.de
(c) DFKI, 2003-2011

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

The JTok source code is available at
http://heartofgold.opendfki.de/browser/trunk/jtok

Content:
========
bin
     tokenize: a simple script for playing around with JTok. It takes
     a file name and its language as argument and returns the tokenized
     document as pretty-printed Paragraphs, TextUnits and Tokens instances.
     tokenixe: same as tokenize, but creates an XML output format.

conf
     The JTok configuration files, especially the language descriptions.
     For each supported language there is a subdirectory that contains
     several language description files in the XML format that describe
     the token type hierarchy and definition and rules for matching the
     different token types. For further details, see commments in the
     XML files. Make sure to include this directory in front of
     jtok-core-1.9.jar in the JVM classpath, otherwise the default
     configuration included in the jar will be used.

docs
     The API documentation of JTok as created by Javadoc. 

lib
     The JTok library and 3rd party libraries used by JTok.


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



