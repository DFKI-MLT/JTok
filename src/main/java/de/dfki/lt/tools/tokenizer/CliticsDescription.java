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
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

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
   * Creates a new instance of {@link CliticsDescription} for the clitics
   * description contained in the given config file.
   *
   * @param clitDescrPath
   *          path to the config file
   * @param macrosMap
   *          a map of macro names to regular expression strings
   * @exception InitializationException
   *              if an error occurs
   */
  public CliticsDescription(
      String clitDescrPath, Map<String, String> macrosMap) {

    super.setDefinitionsMap(new HashMap<String, RegExp>());
    super.setRulesMap(new HashMap<String, RegExp>());
    super.setRegExpMap(new HashMap<RegExp, String>());

    // read config file
    try {
      BufferedReader in =
        new BufferedReader(
          new InputStreamReader(
            FileTools.openResourceFileAsStream(clitDescrPath.toString()),
            "utf-8"));
      String line;
      Map<String, String> defsMap = new HashMap<>();
      while ((line = in.readLine()) != null) {
        line = line.trim();
        if (line.length() == 0 || line.startsWith("#")) {
          continue;
        }
        if (line.equals(DEFS_MARKER)) {
          defsMap = super.loadDefinitions(in, macrosMap);
          // when loadDefs returns the reader has reached the rules section
          super.loadRules(in, defsMap, macrosMap);
        }
      }
    } catch (IOException ioe) {
      throw new InitializationException(ioe.getLocalizedMessage(), ioe);
    }
  }
}
