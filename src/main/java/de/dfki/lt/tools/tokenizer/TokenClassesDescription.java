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

import de.dfki.lt.tools.tokenizer.exceptions.InitializationException;
import de.dfki.lt.tools.tokenizer.regexp.RegExp;

/**
 * Manages the content of a token classes description file.
 *
 * @author Joerg Steffen, DFKI
 */
public class TokenClassesDescription
    extends Description {

  /**
   * Name of the all classes rule.
   */
  protected static final String ALL_RULE = "ALL_CLASSES_RULE";

  /**
   * Contains the name suffix of the resource file with the token classes
   * description.
   */
  private static final String CLASS_DESCR = "_classes.cfg";


  /**
   * Creates a new instance of {@link TokenClassesDescription} for the given
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
  public TokenClassesDescription(
      String resourceDir, String lang, Map<String, String> macrosMap)
      throws IOException {

    super.setDefinitionsMap(new HashMap<String, RegExp>());
    super.setRulesMap(new HashMap<String, RegExp>());
    super.setRegExpMap(new HashMap<RegExp, String>());

    Path commonDescrPath =
        Paths.get("jtok").resolve(COMMON)
          .resolve(COMMON + CLASS_DESCR);
    Path tokClassesDescrPath =
      Paths.get(resourceDir).resolve(lang + CLASS_DESCR);

    // open both the common config file and the language specific one
    BufferedReader commonIn = null;
    BufferedReader langIn = null;
    try {
      commonIn = new BufferedReader(
        new InputStreamReader(
          FileTools.openResourceFileAsStream(commonDescrPath.toString()),
          "utf-8"));
    } catch (FileNotFoundException fne) {
      // do nothing, commonIn is still null
    }
    try {
      langIn = new BufferedReader(
        new InputStreamReader(
          FileTools.openResourceFileAsStream(tokClassesDescrPath.toString()),
          "utf-8"));
    } catch (FileNotFoundException fne) {
      // do nothing, langIn is still null
    }

    // at least one configuration must be found
    if (null == commonIn && null == langIn) {
      throw new InitializationException(
        String.format(
          "missing token classes description for language %s", lang));
    }

    // read both config files to definitions start
    readToDefinitions(commonIn);
    readToDefinitions(langIn);

    // read definitions
    Map<String, String> defsMap = new HashMap<>();
    super.loadDefinitions(commonIn, macrosMap, defsMap);
    super.loadDefinitions(langIn, macrosMap, defsMap);

    getRulesMap().put(ALL_RULE, createAllRule(defsMap));

    if (null != commonIn) {
      commonIn.close();
    }
    if (null != langIn) {
      langIn.close();
    }
  }
}
