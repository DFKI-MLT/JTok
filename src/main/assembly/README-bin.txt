JTok V2.14
written by Joerg Steffen
Email: steffen@dfki.de
(c) DFKI, 2003-2015

This product is licensed to you under the GNU Lesser General Public License, 
Version 2.11. You may not use this product except in compliance with the
license.

JTok provides a configurable tokenizer that identifies paragraphs,
sentences and tokens of an input text. Tokens can be further
classified into abbreviations, numbers, punctuation, etc.

The package currently supports English, German and Italian, but also comes with
a default configuration that can be used for other languages.

The output of JTok is an instance of
de.dfki.lt.tools.tokenizer.annotate.AnnotatedString, but there are
methods available that transform an AnnotatedString into an XML representation
or into instances of Paragraph, TextUnit and Token classes.


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
     several language description files that describe the token class hierarchy 
     and definition and rules for matching the different token classes. For 
     further details, see comments in the configuration files.
     A default configuration is provided in the 'default' folder.
     After modifying configuration files, execute "mvn compile" to make
     them available to the runtime system or add src/main/resources to
     the JVM classpath in front of jtok-core-2.14.jar.
     Another option for user specific JTok configuration is to put it
     into a folder 'jtok-user' in the classpath. This location is
     searched first for any JTok configuration. The expected folder
     structure in 'jtok-user' is the same as in 'conf', so don't forget
     the 'jtok' directly under 'jtok-user'.

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



