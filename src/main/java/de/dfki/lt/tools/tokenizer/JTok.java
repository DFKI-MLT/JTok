/*
 * JTok
 * A configurable tokenizer implemented in Java
 *
 * (C) 2003 - 2014  DFKI Language Technology Lab http://www.dfki.de/lt
 *   Author: Joerg Steffen, steffen@dfki.de
 *
 *   This program is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU Lesser General Public
 *   License as published by the Free Software Foundation; either
 *   version 2.1 of the License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *   Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package de.dfki.lt.tools.tokenizer;

import java.io.File;
import java.io.IOException;
import java.text.CharacterIterator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dfki.lt.tools.tokenizer.annotate.AnnotatedString;
import de.dfki.lt.tools.tokenizer.annotate.FastAnnotatedString;
import de.dfki.lt.tools.tokenizer.exceptions.InitializationException;
import de.dfki.lt.tools.tokenizer.exceptions.LanguageNotSupportedException;
import de.dfki.lt.tools.tokenizer.exceptions.ProcessingException;
import de.dfki.lt.tools.tokenizer.output.Paragraph;
import de.dfki.lt.tools.tokenizer.output.ParagraphOutputter;
import de.dfki.lt.tools.tokenizer.regexp.Match;
import de.dfki.lt.tools.tokenizer.regexp.RegExp;

/**
 * Tokenizer tool that recognizes paragraphs, sentences, tokens, punctuation,
 * numbers, abbreviations, etc.
 *
 * @author Joerg Steffen, DFKI
 */
public class JTok {

  /**
   * Contains the logger object for logging.
   */
  private static final Logger LOG = LoggerFactory.getLogger(JTok.class);

  /**
   * Property for the languages in the config.
   */
  private static final String LANGUAGES_PROP = "languages";

  /**
   * Annotation key for the token class.
   */
  public static final String CLASS_ANNO = "class";

  /**
   * Annotation key for sentences and paragraph borders.
   */
  public static final String BORDER_ANNO = "border";

  /**
   * Annotation value for text unit borders.
   */
  public static final String TU_BORDER = "tu";

  /**
   * Annotation value for paragraph borders.
   */
  public static final String P_BORDER = "p";

  /**
   * Maps each supported language to a language resource.
   */
  private Map<String, LanguageResource> langResources;


  /**
   * Creates a new instance of {@link JTok}.
   *
   * @throws IOException
   *           if there is an error reading the configuration
   */
  public JTok()
      throws IOException {

    // load tokenizer configuration
    Properties props = new Properties();
    props.load(FileTools.openResourceFileAsStream("jtok/jtok.cfg"));
    this.init(props);
  }


  /**
   * Creates a new instance of {@link JTok} using the given properties.
   *
   * @param configProps
   *          properties that contain data about the supported languages
   * @exception InitializationException
   *              if initialization fails
   */
  public JTok(Properties configProps) {

    this.init(configProps);
  }


  /**
   * Initializes the tokenizer.
   *
   * @param configProps
   *          properties with the the configuration
   */
  private void init(Properties configProps) {

    this.langResources = new HashMap<>();

    // get list of languages
    String languages = configProps.getProperty(LANGUAGES_PROP);

    // iterate over languages
    StringTokenizer st = new StringTokenizer(languages, ",");

    while (st.hasMoreTokens()) {
      // get language
      String oneLanguage = st.nextToken();
      // add language resources for that language
      String langDir = configProps.getProperty(oneLanguage);
      LOG.info(String.format("loading language resources for %s from %s",
        oneLanguage, langDir));

      this.langResources.put(
        oneLanguage, new LanguageResource(oneLanguage, langDir));
    }
  }


  /**
   * Returns the language resource for the given language if available.
   *
   * @param lang
   *          the language
   * @return the language resource
   * @exception LanguageNotSupportedException
   *              if no language resource is available for this language
   */
  public LanguageResource getLanguageResource(String lang)
      throws LanguageNotSupportedException {

    Object probe = this.langResources.get(lang);
    if (null != probe) {
      return (LanguageResource)probe;
    }
    throw new LanguageNotSupportedException(
      String.format("language %s not supported", lang));
  }


  /**
   * Tokenizes the given text in the given language. Returns an annotated string
   * containing the identified paragraphs with their text units and tokens.<br>
   * This method is thread-safe.
   *
   * @param inputText
   *          the text to tokenize
   * @param lang
   *          the language of the text
   * @return an annotated string
   * @exception ProcessingException
   *              if input data causes an error e.g. if language is not
   *              supported
   */
  public AnnotatedString tokenize(String inputText, String lang) {

    // get language resource for language
    LanguageResource langRes = this.getLanguageResource(lang);

    // init attributed string for annotation
    AnnotatedString input = new FastAnnotatedString(inputText);

    // identify tokens
    this.identifyTokens(input, langRes);

    // identify punctuation
    this.identifyPunct(input, langRes);

    // identify clitics
    this.identifyClitics(input, langRes);

    // identify numbers
    this.identifyNumbers(input, langRes);

    // identify abbreviations
    this.identifyAbbrev(input, langRes);

    // identify sentences and paragraphs
    this.identifyTus(input, langRes);

    // return result
    return input;
  }


  /**
   * Identifies tokens and annotates them. Tokens are sequences of
   * non-whitespaces.
   *
   * @param input
   *          an annotated string
   * @param langRes
   *          the language resource to use
   */
  private void identifyTokens(
      AnnotatedString input, LanguageResource langRes) {

    // init token start index
    int tokenStart = 0;
    // flag for indicating if new token was found
    boolean tokenFound = false;

    // get classes root annotation
    String rootClass =
      langRes.getClassesRoot().getTagName();

    // iterate over input
    for (char c = input.first(); c != CharacterIterator.DONE; c = input.next()) {
      if (Character.isWhitespace(c) || (c == '\u00a0')) {
        if (tokenFound) {
          // annotate newly identified token
          input.annotate(CLASS_ANNO, rootClass,
            tokenStart, input.getIndex());
          tokenFound = false;
        }
      }
      else if (!tokenFound) {
        // a new token starts here, after some whitespaces
        tokenFound = true;
        tokenStart = input.getIndex();
      }
    }
    // annotate last token
    if (tokenFound) {
      input.annotate(CLASS_ANNO, rootClass,
        tokenStart, input.getIndex());
    }
  }


  /**
   * Identifies punctuations in the annotated tokens of the given annotated
   * string
   *
   * @param input
   *          an annotated string
   * @param langRes
   *          the language resource to use
   * @exception ProcessingException
   *              if an error occurs
   */
  private void identifyPunct(
      AnnotatedString input, LanguageResource langRes) {

    // get the matchers needed
    RegExp allPunctMatcher = langRes.getAllPunctMatcher();
    RegExp internalMatcher = langRes.getInternalMatcher();
    RegExp nbrMatcher = langRes.getNbrMatcher();
    RegExp nblMatcher = langRes.getNblMatcher();

    // iterate over tokens
    char c = input.setIndex(0);
    // move to first non-whitespace
    if (null == input.getAnnotation(CLASS_ANNO)) {
      c = input.setIndex(input.findNextAnnotation(CLASS_ANNO));
    }
    while (c != CharacterIterator.DONE) {

      // get the end index of the token c belongs to
      int tokenEnd = input.getRunLimit(CLASS_ANNO);

      // only check tokens
      if (null == input.getAnnotation(CLASS_ANNO)) {
        c = input.setIndex(input.findNextAnnotation(CLASS_ANNO));
        continue;
      }

      // get class of token
      String tokClass = (String)input.getAnnotation(CLASS_ANNO);
      // get the start index of the token
      int tokenStart = input.getIndex();
      // set iterator to next non-whitespace token
      c = input.setIndex(input.findNextAnnotation(CLASS_ANNO));

      // get the token content
      String image = input.substring(tokenStart, tokenEnd);
      // use the all rule to split image in parts consisting of
      // punctuation and non-punctuation
      List<Match> matches = allPunctMatcher.getAllMatches(image);
      // if there is no punctuation just continue
      if (0 == matches.size()) {
        continue;
      }

      // this is the relative start position of current token within
      // the image
      int index = 0;
      // iterator over matches
      for (int i = 0; i < matches.size(); i++) {
        // get next match
        Match oneMatch = matches.get(i);

        // check if we have some non-punctuation before the current
        // punctuation
        if (index != oneMatch.getStartIndex()) {

          // check for internal punctuation:
          if (internalMatcher.matches(oneMatch.getImage())) {
            // punctuation is internal;
            // check for right context
            if (this.isRightContextEnd(oneMatch, matches, image, i)) {
              // token not complete yet
              continue;
            }
          }

          // check for non-breaking right punctuation:
          if (nbrMatcher.matches(oneMatch.getImage())) {
            // punctuation is non-breaking right, so annotate new
            // token
            input.annotate(CLASS_ANNO, tokClass,
              tokenStart + index,
              tokenStart + oneMatch.getEndIndex());
            index = oneMatch.getEndIndex();
            continue;
          }

          // we have a breaking punctuation; create token for
          // non-punctuation before the current punctuation
          input.annotate(CLASS_ANNO, tokClass,
            tokenStart + index,
            tokenStart + oneMatch.getStartIndex());
          index = oneMatch.getStartIndex();
        }
        else {
          // there is no non-punctuation before this punctuation

          // check for non-breaking left punctuation:
          if (nblMatcher.matches(oneMatch.getImage())) {
            // punctuation is non-breaking left
            // check for right context
            if (this.isRightContextEnd(oneMatch, matches, image, i)) {
              // token not complete yet
              continue;
            }
          }
        }

        // punctuation is not internal and not non-breaking left- or
        // right: get the class of the punctuation and create token
        // for it
        String punctClass =
          this.identifyPunctClass(
            oneMatch, null, image, langRes);
        input.annotate(CLASS_ANNO, punctClass,
          tokenStart + index,
          tokenStart + oneMatch.getEndIndex());
        index = oneMatch.getEndIndex();
      }

      // cleanup after all matches have been processed
      if (index != image.length()) {
        // create a token from rest of image
        input.annotate(CLASS_ANNO, tokClass,
          tokenStart + index,
          tokenStart + image.length());
      }
    }
  }


  /**
   * Returns {@code true} if there is a right context after the punctuation
   * matched by the given match or {@code false} when there is no right context.
   *
   * @param oneMatch
   *          a match matching a punctuation
   * @param matches
   *          a list of all punctuation matching matches
   * @param i
   *          the index of the match in the matches list
   * @param image
   *          the string on which the punctuation matchers have been applied
   * @return a flag indicating if there is a right context
   */
  private boolean isRightContextEnd(
      Match oneMatch, List<Match> matches, String image, int i) {

    if (i < (matches.size() - 1)) {
      // there is another punctuation later in the image
      Match nextMatch = matches.get(i + 1);
      if (nextMatch.getStartIndex() != oneMatch.getEndIndex()) {
        // there is some right context and punctuation
        // following the internal punctuation
        return true;
      }
      return false;
    }
    else if (oneMatch.getEndIndex() != image.length()) {
      // there is right context after the internal
      // punctuation
      return true;
    }
    else {
      return false;
    }
  }


  /**
   * Checks the class of a punctuation and returns the corresponding class name
   * for annotation.
   *
   * @param punct
   *          the match for which to find the class name
   * @param regExp
   *          the regular expression that found the punctuation as a match,
   *          {@code null} if punctuation wasn't found via a regular expression
   * @param image
   *          a string with the input
   * @param langRes
   *          a language resource that contains everything needed for
   *          identifying the class
   * @return the class name
   * @exception ProcessingException
   *              if class of punctuation can't be identified
   */
  private String identifyPunctClass(
      Match punct, RegExp regExp, String image, LanguageResource langRes) {

    String oneClass =
      this.identifyClass(punct.getImage(), regExp, langRes.getPunctDescr());
    // check if we have an ambiguous open/close punctuation; if
    // yes, resolve it
    if (oneClass.equals(PunctDescription.OPEN_CLOSE_PUNCT)) {

      int nextIndex = punct.getEndIndex();
      if ((nextIndex >= image.length())
        || !Character.isLetter(image.charAt(nextIndex))) {
        oneClass = PunctDescription.CLOSE_PUNCT;
      }
      else {
        int prevIndex = punct.getStartIndex() - 1;
        if ((prevIndex < 0)
          || !Character.isLetter(image.charAt(prevIndex))) {
          oneClass = PunctDescription.OPEN_PUNCT;
        }
      }
    }
    // return class name
    return oneClass;
  }


  /**
   * Identifies clitics and splits them from the annotated tokens of the given
   * annotated string.
   *
   * @param input
   *          an annotated string
   * @param langRes
   *          the language resource to use
   * @exception ProcessingException
   *              if an error occurs
   */
  private void identifyClitics(
      AnnotatedString input, LanguageResource langRes) {

    // get matchers needed for clitics recognition
    RegExp proclitMatcher = langRes.getProcliticsMatcher();
    RegExp enclitMatcher = langRes.getEncliticsMatcher();
    RegExp nbrMatcher = langRes.getNbrMatcher();
    RegExp nblMatcher = langRes.getNblMatcher();

    // get the class of the root element of the class hierarchy;
    // only tokens with this type are further examined
    String rootClass =
      langRes.getClassesRoot().getTagName();

    // iterate over tokens
    char c = input.setIndex(0);
    // move to first non-whitespace
    if (null == input.getAnnotation(CLASS_ANNO)) {
      c = input.setIndex(input.findNextAnnotation(CLASS_ANNO));
    }
    while (c != CharacterIterator.DONE) {

      // get the end index of the token c belongs to
      int tokenEnd = input.getRunLimit(CLASS_ANNO);
      // get class of token
      String tokClass = (String)input.getAnnotation(CLASS_ANNO);
      // only check tokens with the most general class
      if (tokClass != rootClass) {
        c = input.setIndex(input.findNextAnnotation(CLASS_ANNO));
        continue;
      }

      // get the start index of the token
      int tokenStart = input.getIndex();
      // set iterator to next non-whitespace token
      c = input.setIndex(input.findNextAnnotation(CLASS_ANNO));

      // get the token content
      String image = input.substring(tokenStart, tokenEnd);

      // keep track of the start and end index of the non-clitic part
      // of the image
      int startIndex = 0;
      int endIndex = image.length();

      // proclitics:
      // check for non-breaking left punctuation; these
      // have to be cut before proclitics check
      Match nbl = nblMatcher.starts(image);
      Match proclit = null;
      if (null != nbl) {
        proclit = proclitMatcher.starts(
          image.substring(nbl.getEndIndex(), endIndex));
      }
      else {
        proclit = proclitMatcher.starts(image);
      }

      // if there is a proclitic, create a token for possible
      // non-breaking left punctuation
      if ((null != nbl) && (null != proclit)) {
        String punctClass =
          this.identifyPunctClass(nbl, nblMatcher, image, langRes);
        input.annotate(CLASS_ANNO, punctClass,
          tokenStart + nbl.getStartIndex(),
          tokenStart + nbl.getEndIndex());
        startIndex = nbl.getEndIndex();
      }

      // create tokens for proclitics
      while (null != proclit) {
        String clitClass =
          this.identifyClass(
            proclit.getImage(), proclitMatcher, langRes.getClitDescr());
        input.annotate(CLASS_ANNO, clitClass,
          tokenStart + startIndex + proclit.getStartIndex(),
          tokenStart + startIndex + proclit.getEndIndex());
        startIndex = startIndex + proclit.getEndIndex();
        proclit =
          proclitMatcher.starts(image.substring(
            startIndex, image.length()));
      }

      // enclitics:
      // check for non-breaking right punctuation; these
      // have to be cut before enclitics check
      Match nbr = nbrMatcher.ends(image);
      Match enclit = null;
      if (null != nbr) {
        enclit = enclitMatcher.ends(
          image.substring(startIndex, nbr.getStartIndex()));
      }
      else {
        enclit = enclitMatcher.ends(
          image.substring(startIndex, endIndex));
      }

      // if there is an enclitic, create a token for possible
      // non-breaking right punctuation
      if ((null != nbr) && (null != enclit)) {
        String punctClass =
          this.identifyPunctClass(nbr, nbrMatcher, image, langRes);
        input.annotate(CLASS_ANNO, punctClass,
          tokenStart + nbr.getStartIndex(),
          tokenStart + nbr.getEndIndex());
      }
      // create tokens for enclitics
      while (null != enclit) {
        String clitClass =
          this.identifyClass(
            enclit.getImage(), enclitMatcher, langRes.getClitDescr());
        input.annotate(CLASS_ANNO, clitClass,
          tokenStart + startIndex + enclit.getStartIndex(),
          tokenStart + startIndex + enclit.getEndIndex());
        endIndex = startIndex + enclit.getStartIndex();
        enclit =
          enclitMatcher.ends(image.substring(startIndex, endIndex));
      }

      // create token for remaining stuff between pro- and enclitics
      if (startIndex != endIndex) {
        input.annotate(CLASS_ANNO, rootClass,
          tokenStart + startIndex,
          tokenStart + endIndex);
      }
    }
  }


  /**
   * Identifies numbers in the annotated token of the given annotated string.
   *
   * @param input
   *          an annotated string
   * @param langRes
   *          the language resource to use
   * @exception ProcessingException
   *              if an error occurs
   */
  private void identifyNumbers(
      AnnotatedString input, LanguageResource langRes) {

    // get matcher needed for number recognition
    RegExp simpleDigitsMatcher = langRes.getSimpleDigitsMatcher();
    RegExp ordinalMatcher = langRes.getOrdinalMatcher();
    RegExp digitsMatcher = langRes.getDigitsMatcher();

    // get the class of the root element of the class hierarchy;
    // only tokens with this class are further examined
    String rootClass =
      langRes.getClassesRoot().getTagName();

    // iterate over tokens
    char c = input.setIndex(0);
    // move to first non-whitespace
    if (null == input.getAnnotation(CLASS_ANNO)) {
      c = input.setIndex(input.findNextAnnotation(CLASS_ANNO));
    }
    while (c != CharacterIterator.DONE) {

      // get the end index of the token c belongs to
      int tokenEnd = input.getRunLimit(CLASS_ANNO);
      // get class of token
      String tokClass = (String)input.getAnnotation(CLASS_ANNO);
      // only check tokens with the most general class
      if (tokClass != rootClass) {
        c = input.setIndex(input.findNextAnnotation(CLASS_ANNO));
        continue;
      }

      // get the start index of the token
      int tokenStart = input.getIndex();
      // set iterator to next non-whitespace token
      c = input.setIndex(input.findNextAnnotation(CLASS_ANNO));

      // get the token content
      String image = input.substring(tokenStart, tokenEnd);

      // check if token contains digits
      if (null != simpleDigitsMatcher.contains(image)) {
        // initialize flag that indicates if a period was found at the
        // end of the image
        boolean periodFlag = false;
        // check if token is candidate for an ordinal number
        if ('.' == image.charAt(image.length() - 1)) {
          periodFlag = true;
          // check if token is ordinal number
          if (ordinalMatcher.matches(image)) {
            String ordClass =
              this.identifyClass(image, ordinalMatcher, langRes.getNumbDescr());
            input.annotate(CLASS_ANNO, ordClass, tokenStart, tokenEnd);
            continue;
          }

          // cut period from image
          image = image.substring(0, image.length() - 1);
          tokenEnd--;
        }

        // check if token is a digit
        if (digitsMatcher.matches(image)) {
          String numbClass =
            this.identifyClass(image, digitsMatcher, langRes.getNumbDescr());
          input.annotate(CLASS_ANNO, numbClass, tokenStart, tokenEnd);
          // if period was cut off, annotate it now
          if (periodFlag) {
            String punctClass =
              this.identifyClass(".", null, langRes.getPunctDescr());
            input.annotate(CLASS_ANNO, punctClass,
              tokenEnd, tokenEnd + 1);
          }
        }
      }
    }
  }


  /**
   * Identifies abbreviations in the annotated token of the given annotated
   * string. Candidates are tokens with a non-breaking right punctuation that
   * starts with a period. If the token with that period is identified as an
   * abbreviation, the rest of the punctuation is split of.
   *
   * @param input
   *          an annotated string
   * @param langRes
   *          the language resource to use
   * @exception ProcessingException
   *              if an error occurs
   */
  private void identifyAbbrev(
      AnnotatedString input, LanguageResource langRes) {

    // get matchers needed for abbreviation recognition
    RegExp nbrMatcher = langRes.getNbrMatcher();
    RegExp abbrevMatcher = langRes.getAbbrevMatcher();
    RegExp initialMatcher = langRes.getInitialMatcher();

    // get map with abbreviation lists
    Map<String, Set<String>> abbrevLists =
      langRes.getAbbrevLists();

    // get the class of the root element of the class hierarchy;
    // only tokens with this class are further examined
    String rootClass =
      langRes.getClassesRoot().getTagName();

    // iterate over tokens
    char c = input.setIndex(0);
    // move to first non-whitespace
    if (null == input.getAnnotation(CLASS_ANNO)) {
      c = input.setIndex(input.findNextAnnotation(CLASS_ANNO));
    }
    while (c != CharacterIterator.DONE) {

      // get the end index of the token c belongs to
      int tokenEnd = input.getRunLimit(CLASS_ANNO);
      // get class of token
      String tokClass = (String)input.getAnnotation(CLASS_ANNO);
      // only check tokens with the most general class
      if (tokClass != rootClass) {
        c = input.setIndex(input.findNextAnnotation(CLASS_ANNO));
        continue;
      }

      // get the start index of the token
      int tokenStart = input.getIndex();
      // set iterator to next non-whitespace token
      c = input.setIndex(input.findNextAnnotation(CLASS_ANNO));

      // get the token content
      String image = input.substring(tokenStart, tokenEnd);
      // check if token contains a non-breaking right punctuation that
      // consists of a single period
      Match nbr = nbrMatcher.ends(image);
      if ((null != nbr)
          && ((nbr.getEndIndex() - nbr.getStartIndex()) == 1)
          && (input.charAt(tokenStart + nbr.getStartIndex()) == '.')) {
        // found an abbreviation candidate

        // if the abbreviation contains a hyphen, it's sufficient to check
        // the part after the hyphen
        int hyphenPos = image.lastIndexOf("-");
        if (hyphenPos != -1) {
          String afterHyphen = image.substring(hyphenPos + 1);
          if (afterHyphen.matches("[^0-9]{2,}")) {
            image = afterHyphen;
          }
        }

        // check if token is in abbreviation lists
        boolean found = false;
        for (Map.Entry<String, Set<String>> oneEntry : abbrevLists.entrySet()) {
          String abbrevClass = oneEntry.getKey();
          Set<String> oneList = oneEntry.getValue();
          if (oneList.contains(image)) {
            // annotate abbreviation
            input.annotate(CLASS_ANNO, abbrevClass,
              tokenStart,
              tokenStart + nbr.getEndIndex());
            // stop looking for this abbreviation
            found = true;
            break;
          }
        }
        if (found) {
          continue;
        }

        // check if token is matched by mid name initial matcher
        if ((initialMatcher != null) && initialMatcher.matches(image)) {
          String initialClass =
            this.identifyClass(image, initialMatcher, langRes.getAbbrevDescr());
          input.annotate(CLASS_ANNO, initialClass,
            tokenStart,
            tokenStart + nbr.getEndIndex());
          continue;
        }

        // check if token is matched by abbreviation matcher
        if (abbrevMatcher.matches(image)) {
          String abbrevClass =
            this.identifyClass(image,
              abbrevMatcher,
              langRes.getAbbrevDescr());
          input.annotate(CLASS_ANNO, abbrevClass,
            tokenStart,
            tokenStart + nbr.getEndIndex());
          continue;
        }

        // if token is no abbreviation, split non-breaking right
        // punctuation
        String punctClass =
          this.identifyPunctClass(nbr, nbrMatcher, image, langRes);
        // annotate
        input.annotate(CLASS_ANNO, punctClass,
          tokenStart + nbr.getStartIndex(),
          tokenStart + nbr.getEndIndex());
      }
    }
  }


  /**
   * Identifies text units and paragraphs in the given annotated string and
   * annotates them under the annotation key BORDER_ANNO.
   *
   * @param input
   *          an annotated string
   * @param langRes
   *          the language resource to use
   * @exception ProcessingException
   *              if an undefined class name is found
   */
  private void identifyTus(
      AnnotatedString input, LanguageResource langRes) {

    // get matcher needed for text unit identification
    RegExp intPunctMatcher = langRes.getInternalTuMatcher();

    // init end-of-sentence-mode flag; when in this mode, every token
    // that is not PTERM, PTERM_P, CLOSE_PUNCT or CLOSE_BRACKET initiates the
    // annotation of a new text unit.
    boolean eosMode = false;
    boolean abbrevMode = false;

    // iterate over tokens
    char c = input.setIndex(0);
    while (c != CharacterIterator.DONE) {

      int tokenStart = input.getRunStart(CLASS_ANNO);
      int tokenEnd = input.getRunLimit(CLASS_ANNO);
      // check if c belongs to a token
      if (null != input.getAnnotation(CLASS_ANNO)) {
        // check if we are in end-of-sentence mode
        if (eosMode) {
          // if we find terminal punctuation or closing bracets,
          // continue with the current sentence
          if (langRes.isAncestor(
              "TERM_PUNCT", (String)input.getAnnotation(CLASS_ANNO))
              || langRes.isAncestor(
                "TERM_PUNCT_P", (String)input.getAnnotation(CLASS_ANNO))
              || langRes.isAncestor(
                "CLOSE_PUNCT", (String)input.getAnnotation(CLASS_ANNO))
              || langRes.isAncestor(
                "CLOSE_BRACKET", (String)input.getAnnotation(CLASS_ANNO))) {
            // do nothing
          }
          // if we find a lower case letter or a punctuation that can
          // only appear within a text unit, it was wrong alert, the
          // sentence hasn't ended yet
          else if (Character.isLowerCase(c)
            || intPunctMatcher.matches(
              input.substring(input.getIndex(), input.getIndex() + 1))) {
            eosMode = false;
          }
          // otherwise, we just found the first element of the next
          // sentence
          else {
            input.annotate(BORDER_ANNO, TU_BORDER, tokenStart, tokenStart + 1);
            eosMode = false;
          }
        }
        else if (abbrevMode) {
          String image = input.substring(tokenStart, tokenEnd);
          if (langRes.getNonCapTerms().contains(image)
            || langRes.isAncestor(
              "OPEN_PUNCT", (String)input.getAnnotation(CLASS_ANNO))) {
            // there is a term that only starts with a capital letter at the
            // beginning of a sentence OR
            // an opening punctuation;
            // so we just found the first element of the next sentence
            input.annotate(BORDER_ANNO, TU_BORDER, tokenStart, tokenStart + 1);
          }
          abbrevMode = false;
          // continue without going to the next token;
          // it's possible that after an abbreviation follows a
          // end-of-sentence marker
          continue;
        }
        else {
          // check if token is a end-of-sentence marker
          if (langRes.isAncestor
              ("TERM_PUNCT", (String)input.getAnnotation(CLASS_ANNO))
              || langRes.isAncestor(
                "TERM_PUNCT_P", (String)input.getAnnotation(CLASS_ANNO))) {
            eosMode = true;
          }
          // check if token is a breaking abbreviation
          else if (langRes.isAncestor
            ("B_ABBREVIATION",
              (String)input.getAnnotation(CLASS_ANNO))) {
            abbrevMode = true;
          }
        }
        // set iterator to next token
        c = input.setIndex(tokenEnd);
      }
      else {
        // check for paragraph change in whitespace sequence
        if (this.isParagraphChange(input.substring(tokenStart, tokenEnd))) {
          eosMode = false;
          abbrevMode = false;
          // set iterator to next token
          c = input.setIndex(tokenEnd);
          // next token starts a new paragraph
          if (c != CharacterIterator.DONE) {
            input.annotate(BORDER_ANNO, P_BORDER,
              input.getIndex(), input.getIndex() + 1);
          }
        }
        else {
          // just set iterator to next token
          c = input.setIndex(tokenEnd);
        }
      }
    }
  }


  /**
   * Called with a sequence of whitespaces. It returns a flag indicating if the
   * sequence contains a paragraph change. A paragraph change is defined as a
   * sequence of whitespaces that contains two line breaks.
   *
   * @param wSpaces
   *          a string consisting only of whitespaces
   * @return a flag indicating a paragraph change
   */
  private boolean isParagraphChange(String wSpaces) {

    int len = wSpaces.length();
    for (int i = 0; i < len; i++) {
      char c = wSpaces.charAt(i);
      if (('\n' == c) || ('\r' == c)) {
        // possible continuations for a paragraph change:
        // - another \n -> paragraph change in Unix or Windows
        // the second \n must no be the next character!
        // this way we catch \n\n for Unix and \r\n\r\n for Windows
        // - another \r -> paragraph change in MacOs or Windows
        // the second \r must no be the next character!
        // this way we catch \r\r for MacOs and \r\n\r\n for Windows
        // we just look for a second occurrence of the c just found
        for (int j = i + 1; j < len; j++) {
          if (c == wSpaces.charAt(j)) {
            return true;
          }
        }
      }
    }
    return false;
  }


  /**
   * Identifies the class of the given string and returns the corresponding
   * class name for annotation.
   *
   * @param string
   *          the string for which to find the class name
   * @param regExp
   *          the regular expression that found the string as a match,
   *          {@code null} if string wasn't found via a regular expression
   * @param descr
   *          a description that contains everything needed for identifying the
   *          class
   * @return the class name
   * @exception ProcessingException
   *              if class of string can't be identified
   */
  private String identifyClass(
      String string, RegExp regExp, Description descr) {

    // first try to identify class via the regular expression
    if (null != regExp) {
      Map<RegExp, String> regExpMap = descr.getRegExpMap();
      String oneClass = regExpMap.get(regExp);
      if (null != oneClass) {
        return oneClass;
      }
    }

    // get hash map with classes
    Map<String, RegExp> definitionsMap = descr.getDefinitionsMap();
    // iterate over classes
    for (Map.Entry<String, RegExp> oneEntry : definitionsMap.entrySet()) {
      // check if string is of that class
      String oneClass = oneEntry.getKey();
      RegExp oneRE = oneEntry.getValue();
      if (oneRE.matches(string)) {
        // return class name
        return oneClass;
      }
    }
    // throw exception if no class for string was found
    throw new ProcessingException(
      String.format("could not find class for %s", string));
  }


  /**
   * Checks if the class of a token with the first given tag is ancestor in the
   * class hierarchy of the class of a token with the second given tag or if the
   * token classes are equal in the token class hierarchy for the given
   * language.
   *
   * @param tag1
   *          the first token class tag
   * @param tag2
   *          the second token class tag
   * @param lang
   *          the language
   * @return a flag indicating the ancestor relationship
   * @exception ProcessingException
   *              if tags cannot be mapped to a token class
   */
  public boolean isAncestor(String tag1, String tag2, String lang)
      throws ProcessingException {

    // get language resource for language
    LanguageResource langRes = this.getLanguageResource(lang);

    // get class for tag 1
    String class1 = langRes.getClassesMap().get(tag1);
    if (null == class1) {
      throw new ProcessingException(
        String.format("undefined token class tag %s", tag1));
    }
    // get class for tag 2
    String class2 = langRes.getClassesMap().get(tag2);
    if (null == class2) {
      throw new ProcessingException(
        String.format("undefined token class tag %s", tag2));
    }

    // return result for token class hierarchy
    return langRes.isAncestor(class1, class2);
  }


  /**
   * This main method must be used with two or three arguments:
   * <ul>
   * <li>a file name for the document to tokenize
   * <li>the language of the document
   * <li>an optional encoding to use (default is ISO-8859-1)
   * </ul>
   * Supported languages are: de, en, it
   *
   * @param args
   *          the arguments
   */
  public static void main(String[] args) {

    // check for correct arguments
    if ((args.length != 2) && (args.length != 3)) {
      System.out.format(
        "This method needs two arguments:%n"
          + "- a file name for the document to tokenize%n"
          + "- the language of the document%n"
          + "- an optional encoding to use (default is ISO-8859-1)%n"
          + "Supported languages are: de, en, it");
      System.exit(1);
    }

    // check encoding
    String encoding = "ISO-8859-1";
    if (args.length == 3) {
      encoding = args[2];
    }

    String text = null;
    try {
      // get text from file
      text = FileTools.readFileAsString(new File(args[0]), encoding);
    } catch (IOException ioe) {
      ioe.printStackTrace();
      System.exit(1);
    }

    try {
      // create new instance of JTok
      JTok testTok = new JTok();

      // tokenize text
      AnnotatedString result = testTok.tokenize(text, args[1]);

      // print result
      for (Paragraph onePara : ParagraphOutputter.createParagraphs(result)) {
        System.out.println(onePara);
      }
    } catch (IOException e) {
      LOG.error(e.getLocalizedMessage(), e);
    }
  }
}
