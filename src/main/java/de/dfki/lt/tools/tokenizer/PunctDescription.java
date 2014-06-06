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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import de.dfki.lt.tools.tokenizer.exceptions.InitializationException;
import de.dfki.lt.tools.tokenizer.regexp.RegExp;

/**
 * Manages the content of a punctuation description file.
 *
 * @author Joerg Steffen, DFKI
 */
public class PunctDescription
    extends Description {

  /**
   * Class name for opening punctuation.
   */
  public static final String OPEN_PUNCT = "OPEN_PUNCT";

  /**
   * Class name for closing punctuation.
   */
  public static final String CLOSE_PUNCT = "CLOSE_PUNCT";

  /**
   * Class name for opening brackets.
   */
  public static final String OPEN_BRACKET = "OPEN_BRACKET";

  /**
   * Class name for closing brackets.
   */
  public static final String CLOSE_BRACKET = "CLOSE_BRACKET";

  /**
   * Class name for terminal punctuation.
   */
  public static final String TERM_PUNCT = "TERM_PUNCT";

  /**
   * Class name for possible terminal punctuation.
   */
  public static final String TERM_PUNCT_P = "TERM_PUNCT_P";

  /**
   * Name of the all punctuation rule.
   */
  protected static final String ALL_RULE = "ALL_PUNCT_RULE";

  /**
   * Name of the internal punctuation rule.
   */
  protected static final String INTERNAL_RULE = "INTERNAL_PUNCT_RULE";

  /**
   * Name of the sentence internal punctuation rule.
   */
  protected static final String INTERNAL_TU_RULE = "INTERNAL_TU_PUNCT_RULE";

  /**
   * Class name for ambiguous open/close punctuation.
   */
  protected static final String OPEN_CLOSE_PUNCT = "OPENCLOSE_PUNCT";

  /**
   * Contains the name suffix of the resource file with the punctuation
   * description.
   */
  private static final String PUNCT_DESCR = "_punct.cfg";

  /**
   * Contains the logger.
   */
  private static final Logger LOG = Logger.getLogger(PunctDescription.class);

  /**
   * Creates a new instance of {@link PunctDescription} for the given language.
   *
   * @param resourceDir
   *          path to the folder with the language resources
   * @param lang
   *          the language
   * @param macrosMap
   *          a map of macro names to regular expression strings
   * @throws IOException
   *           if there is an error when reading the configuration
   */
  public PunctDescription(
      String resourceDir, String lang, Map<String, String> macrosMap)
      throws IOException {

    super.setDefinitionsMap(new HashMap<String, RegExp>());
    super.setRulesMap(new HashMap<String, RegExp>());
    super.setRegExpMap(new HashMap<RegExp, String>());

    Path commonDescrPath =
        Paths.get("jtok").resolve(COMMON)
          .resolve(COMMON + PUNCT_DESCR);
    Path punctDescrPath =
      Paths.get(resourceDir).resolve(lang + PUNCT_DESCR);

    // open both the common config file and the language specific one
    BufferedReader commonIn = null;
    BufferedReader langIn = null;
    try {
      commonIn = new BufferedReader(
        new InputStreamReader(
          FileTools.openResourceFileAsStream(commonDescrPath.toString()),
          "utf-8"));
      LOG.info("loading common punctuation description...");
    } catch (FileNotFoundException fne) {
      // do nothing, commonIn is still null
    }
    try {
      langIn = new BufferedReader(
        new InputStreamReader(
          FileTools.openResourceFileAsStream(punctDescrPath.toString()),
          "utf-8"));
      LOG.info(
        String.format("loading punctuation description for %s...", lang));
    } catch (FileNotFoundException fne) {
      // do nothing, langIn is still null
    }

    // at least one configuration must be found
    if (null == commonIn && null == langIn) {
      throw new InitializationException(
        String.format(
          "missing punctuation description for language %s", lang));
    }

    // read both config files to definitions start
    readToDefinitions(commonIn);
    readToDefinitions(langIn);

    // read definitions
    Map<String, String> defsMap = new HashMap<>();
    super.loadDefinitions(commonIn, macrosMap, defsMap);
    super.loadDefinitions(langIn, macrosMap, defsMap);

    // when loadDefinitions returns the reader has reached the rules section;
    // read rules
    super.loadRules(commonIn, defsMap, macrosMap);
    super.loadRules(langIn, defsMap, macrosMap);

    getRulesMap().put(ALL_RULE, createAllRule(defsMap));

    if (null != commonIn) {
      commonIn.close();
    }
    if (null != langIn) {
      langIn.close();
    }
  }
}
