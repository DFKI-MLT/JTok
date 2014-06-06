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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dfki.lt.tools.tokenizer.exceptions.InitializationException;
import de.dfki.lt.tools.tokenizer.regexp.RegExp;

/**
 * Manages the content of a clitics description file.
 *
 * @author Joerg Steffen, DFKI
 */
public class CliticsDescription
    extends Description {

  /**
   * Name of the proclitic rule.
   */
  protected static final String PROCLITIC_RULE =
    "PROCLITIC_RULE";

  /**
   * Name of the enclitic rule.
   */
  protected static final String ENCLITIC_RULE =
    "ENCLITIC_RULE";

  /**
   * Contains the name suffix of the resource file with the clitic description.
   */
  private static final String CLITIC_DESCR = "_clitics.cfg";

  /**
   * Contains the logger.
   */
  private static final Logger LOG =
    LoggerFactory.getLogger(CliticsDescription.class);


  /**
   * Creates a new instance of {@link CliticsDescription} for the given
   * language.
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
  public CliticsDescription(
      String resourceDir, String lang, Map<String, String> macrosMap)
      throws IOException {

    super.setDefinitionsMap(new HashMap<String, RegExp>());
    super.setRulesMap(new HashMap<String, RegExp>());
    super.setRegExpMap(new HashMap<RegExp, String>());

    Path commonDescrPath =
        Paths.get("jtok").resolve(COMMON)
          .resolve(COMMON + CLITIC_DESCR);
    Path clitDescrPath =
      Paths.get(resourceDir).resolve(lang + CLITIC_DESCR);

    // open both the common config file and the language specific one
    BufferedReader commonIn = null;
    BufferedReader langIn = null;
    try {
      commonIn = new BufferedReader(
        new InputStreamReader(
          FileTools.openResourceFileAsStream(commonDescrPath.toString()),
          "utf-8"));
      LOG.info("loading common clitics description");
    } catch (FileNotFoundException fne) {
      // do nothing, commonIn is still null
    }
    try {
      langIn = new BufferedReader(
        new InputStreamReader(
          FileTools.openResourceFileAsStream(clitDescrPath.toString()),
          "utf-8"));
      LOG.info(
        String.format("loading clitics description for %s...", lang));
    } catch (FileNotFoundException fne) {
      // do nothing, langIn is still null
    }

    // at least one configuration must be found
    if (null == commonIn && null == langIn) {
      throw new InitializationException(
        String.format(
          "missing clitics description for language %s", lang));
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

    if (null != commonIn) {
      commonIn.close();
    }
    if (null != langIn) {
      langIn.close();
    }
  }
}
