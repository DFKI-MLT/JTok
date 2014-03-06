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
 * Manages the content of a numbers description file.
 *
 * @author Joerg Steffen, DFKI
 */
public class NumbersDescription
    extends Description {

  /**
   * Name of the digits probe rule.
   */
  protected static final String SIMPLE_DIGITS_RULE =
    "SIMPLE_DIGITS_RULE";

  /**
   * Name of the ordinal number rule.
   */
  protected static final String ORDINAL_RULE =
    "ORDINAL_RULE";

  /**
   * Name of the digits rule.
   */
  protected static final String DIGITS_RULE =
    "DIGITS_RULE";


  /**
   * Creates a new instance of {@link NumbersDescription}for the numbers
   * description contained in the given DOM document.
   *
   * @param numbDescr
   *          a DOM document with the numbers description
   * @param classes
   *          a set with the defined classes, used for validation
   * @exception InitializationException
   *              if an error occurs
   */
  public NumbersDescription(Document numbDescr, Set<String> classes) {

    super.setDefinitionsMap(new HashMap<String, RegExp>());
    super.setRulesMap(new HashMap<String, RegExp>());
    super.setRegExpMap(new HashMap<RegExp, String>());

    // build the classes matcher map
    super.loadDefinitions(numbDescr, classes);
    // build the rules matcher map
    super.loadRules(numbDescr);
  }
}
