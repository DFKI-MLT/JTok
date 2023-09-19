# JTok

[![mvn build](https://github.com/DFKI-MLT/JTok/actions/workflows/maven.yml/badge.svg)](https://github.com/DFKI-MLT/JTok/actions/workflows/maven.yml)

V2.1.21

written by Jörg Steffen

Email: steffen@dfki.de

(c) DFKI, 2003-2023

This product is licensed to you under the GNU Lesser General Public License, Version 2.11. You may not use this product except in compliance with the license.

JTok provides a Java-based configurable tokenizer that identifies paragraphs, sentences and tokens of an input text. Tokens can be further classified into abbreviations, numbers, punctuation, etc.

JTok currently supports English, German and Italian, but also comes with a default configuration that can be used for other languages.

The output of JTok is an instance of `de.dfki.lt.tools.tokenizer.annotate.AnnotatedString`, but there are methods available that transform an AnnotatedString into an XML representation or into instances of `Paragraph`, `TextUnit` and `Token` classes.


## Installation
JTok uses the [Maven](https://maven.apache.org/) build tool.

Execute `mvn install` to create `target/jtok-core-X.Y.Z.jar` that contains the core tokenizer classes with all required resources. This includes a default configuration as found in `src/main/resources`. It is used when no configuration is found elsewhere on the classpath. To use your own configuration, make sure to add your configuration folder in front of `jtok-core-X.Y.Z.jar` in the classpath.

Additionally, the API documentation of JTok can be found in `target/apidocs`. 

Execute `mvn package assembly:single` to create a binary distribution of JTok in `target/jtok-core-X.Y.Z-bin.zip` that also contains all 3rd party libraries.


## Testing
Execute `mvn test` to run the unit tests. They use the files in `src/test/resources`.


## Content
* `src/main/assembly`: The assembly descriptor and readme file of the binary distribution

* `src/main/java`: The Java sources

* `src/main/resources`: The JTok configuration files, especially the language descriptions

   For each supported language there is a subdirectory that contains several language description files that describe the token class hierarchy and definitions and rules for matching the different token classes. For further details, see the [Tokenization](#tokenization) section below and the comments in the configuration files. 

 A default configuration is provided in the `default` folder. After modifying configuration files, execute `mvn compile` to make them available to the runtime system or add `src/main/resources` to the JVM classpath in front of `jtok-core-X.Y.Z.jar`. Another option for user specific JTok configuration is to put it into a folder `jtok-user` in the classpath. This location is searched first for any JTok configuration. The expected folder structure in `jtok-user` is the same as in `conf`, so don't forget the `jtok` directly under `jtok-user`.

* `src/main/scripts`:
   * `tokenize`: a simple script for playing around with JTok. It takes a file name and its language as arguments and returns the tokenized document as pretty-printed Paragraphs, TextUnits and Tokens instances.
   * `tokenixe`: same as tokenize, but creates an XML output format

* `src/test/java`: The Java sources of the test classes

* `src/test/resources`: 

   Some test data for German and English and a directory with some test files for specific stages of the tokenization process. These files are used in the unit tests. The expected results are located in the `expected-results` directory.


## Tokenization

JTok is based on regular expressions. In order to make the regular expressions more readable, it is possible to assign regular expressions to macros and use them in the definition of more complex regular expressions. Macros are defined in the configuration file `LANG_macros.cfg`.

JTok does not only identify tokens but also assigns them a class. Token classes are defined via regular expressions using the format 

`<definition name> : <regular expression> : <class name>` 

The definition name can be used in the same way as a macro.

The token class definitions are grouped in different configuration files based on their type:
* `LANG_abbrev.cfg` defines abbreviation tokens
* `LANG_clitics.cfg` defines clitic tokens
* `LANG_punct.cfg` defines punctuation tokens
* `LANG_classes.cfg` defines general tokens that don't fit into any of the above types 

Token classes are arranged in a hierarchy defined in the configuration file `LANG_class_hierarchy.xml`. A token having a specific class (represented by a node in the hierarchy) inherits the classes of its parent nodes in the hierarchy. JTok uses the hierarchy to efficiently verify if a token belongs to a more general class, e.g. if a token is a punctuation.

JTok identifies and classifies tokens in several stages. At each stage, JTok tries to assign each newly identified token to a class. If it succeeds the token is excluded from further processing, i.e. it is not split into smaller tokens in subsequent stages. The stages are:

* **Whitespace separation:** Tokens are identified as sequences of non-whitespaces.

* **Punctuation and clitics:** Tokens are checked for punctuation at the beginning and at the end as well as for internal punctuation, as defined in `LANG_punct.cfg`. They are also checked for special prefixes (proclitics) and suffixes (enclitics), as defined in `LANG_clitics.cfg`. If punctuation or clitics are found, the token is split into one or more new tokens. Please note: it is possible to define punctuation as 'internal' in which case no splitting takes place.

* **Abbreviations:** Tokens followed by a period are checked as potential abbreviations. The check uses the regular expressions defined in `LANG_abbrev.cfg` as well as the abbreviations list in `LANG_b-abbreev.txt`. If an abbreviation is identified, the period is merged again with the preceding token.

*  **Text units and paragraphs:** In this last stage, tokens are grouped into text units (mostly sentences, but also headings etc.) and text units are grouped into paragraphs. Text units are identified by looking for terminal and possible terminal punctuation as well as closing punctuation and closing brackets, as defined in the token classes hierarchy. JTok tries to solve possible ambiguities between abbreviations and tokens followed by an end-of-sentence period by checking if the following token belongs to a list of words that only start with a capital letter at the beginning of a sentence, as defined in `LANG_nonCapTerms.txt`.

   Paragraphs are identified by two or more consecutive line breaks.


## Penn Treebank Token Replacements
JTok additionally provides its tokens in PTB format. So some tokens have an alternative surface string by applying the following replacements:
* `/` with `\/`
* `*` with `\*`
* `(` with `-LRB-`
* `)` with `-RRB-`
* `[` with `-LSB-`
* `]` with `-RSB-`
* `{` with `-LCB-`
* `}` with `-RCB-`
* all opening punctuation with `` `` ``
* all closing punctuation with `''`
