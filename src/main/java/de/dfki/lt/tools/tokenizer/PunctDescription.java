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
 * Manages the content of a punctuation description file.
 *
 * @author Joerg Steffen, DFKI
 */
public class PunctDescription
    extends Description {

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
   * Creates a new instance of {@link PunctDescription} for the punctuation
   * description contained in the given config file.
   *
   * @param punctDescrPath
   *          path to the config file
   * @exception InitializationException
   *              if an error occurs
   */
  public PunctDescription(String punctDescrPath) {

    super.setDefinitionsMap(new HashMap<String, RegExp>());
    super.setRulesMap(new HashMap<String, RegExp>());
    super.setRegExpMap(new HashMap<RegExp, String>());

    // read config file
    try {
      BufferedReader in =
        new BufferedReader(
          new InputStreamReader(
            FileTools.openResourceFileAsStream(punctDescrPath.toString()),
            "utf-8"));
      String line;
      Map<String, String> defsMap = new HashMap<>();
      while ((line = in.readLine()) != null) {
        line = line.trim();
        if (line.length() == 0 || line.startsWith("#")) {
          continue;
        }
        if (line.equals(DEFS_MARKER)) {
          defsMap = super.loadDefinitions(in);
          // when loadDefs returns the reader has reached the rules section
          super.loadRules(in, defsMap);
        }
      }
      getRulesMap().put(ALL_RULE, createAllRule(defsMap));
    } catch (IOException ioe) {
      throw new InitializationException(ioe.getLocalizedMessage(), ioe);
    }
  }
}
