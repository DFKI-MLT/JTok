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

import java.util.HashMap;
import java.util.Set;

import org.w3c.dom.Document;

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
  protected static final String ALL_RULE =
    "ALL_PUNCT_RULE";

  /**
   * Name of the internal punctuation rule.
   */
  protected static final String INTERNAL_RULE =
    "INTERNAL_PUNCT_RULE";

  /**
   * Name of the non-breaking left punctuation rule.
   */
  protected static final String NON_BREAK_LEFT_RULE =
    "NON_BREAKING_LEFT_PUNCT_RULE";

  /**
   * Name of the non-breaking right punctuation rule.
   */
  protected static final String NON_BREAK_RIGHT_RULE =
    "NON_BREAKING_RIGHT_PUNCT_RULE";

  /**
   * Name of the sentence internal punctuation rule.
   */
  protected static final String INTERNAL_TU_RULE =
    "INTERNAL_TU_PUNCT_RULE";

  /**
   * Class name for ambiguous open/close punctuation.
   */
  protected static final String OPEN_CLOSE_PUNCT =
    "OPENCLOSE_PUNCT";

  /**
   * Class name for opening punctuation.
   */
  public static final String OPEN_PUNCT =
    "OPEN_PUNCT";

  /**
   * Class name for closing punctuation.
   */
  public static final String CLOSE_PUNCT =
    "CLOSE_PUNCT";

  /**
   * Class name for opening brackets.
   */
  public static final String OPEN_BRACKET =
    "OPEN_BRACKET";

  /**
   * Class name for closing brackets.
   */
  public static final String CLOSE_BRACKET =
    "CLOSE_BRACKET";


  /**
   * Creates a new instance of {@link PunctDescription} for the punctuation
   * description contained in the given DOM document.
   *
   * @param punctDescr
   *          a DOM document with the punctuation description
   * @param classes
   *          a set with the defined classes, used for validation
   * @exception InitializationException
   *              if an error occurs
   */
  public PunctDescription(Document punctDescr, Set<String> classes) {

    super.setDefinitionsMap(new HashMap<String, RegExp>());
    super.setRulesMap(new HashMap<String, RegExp>());
    super.setRegExpMap(new HashMap<RegExp, String>());

    // build the classes matcher map
    super.loadDefinitions(punctDescr, classes);
    // build the rules matcher map
    super.loadRules(punctDescr);
  }
}
