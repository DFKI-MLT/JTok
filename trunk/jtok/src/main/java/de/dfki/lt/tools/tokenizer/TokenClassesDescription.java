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
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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
   * Creates a new instance of {@link TokenClassesDescription} for the token
   * classes description contained in the given DOM document.
   *
   * @param tokClassesDescr
   *          a DOM document with the token classes description
   * @param classes
   *          a set with the defined classes, used for validation
   * @exception InitializationException
   *              if an error occurs
   */
  public TokenClassesDescription(Document tokClassesDescr, Set<String> classes) {

    super.setDefinitionsMap(new HashMap<String, RegExp>());
    super.setRulesMap(new HashMap<String, RegExp>());
    super.setRegExpMap(new HashMap<RegExp, String>());

    // build the classes matcher map
    super.loadDefinitions(tokClassesDescr, classes);
    // build the rules matcher map
    super.loadRules(tokClassesDescr);
    this.createAllClassesRule(tokClassesDescr);
  }


  /**
   * Create a rule that matches ALL token classes for which there are
   * definitions.
   */
  private void createAllClassesRule(Document classesDescr) {

    // get list of definitions
    NodeList defs =
      this.getChild(classesDescr.getDocumentElement(), DEFS).getChildNodes();

    StringBuilder ruleRegExpr = new StringBuilder();

    // iterate over definitions
    for (int i = 0, iMax = defs.getLength(); i < iMax; i++) {
      // get definition element
      Object oneObj = defs.item(i);
      if (!(oneObj instanceof Element)) {
        continue;
      }
      Element oneDef = (Element)oneObj;
      // get regular expression string
      String regExpr = oneDef.getAttribute(DEF_REGEXP);
      // extend regular expression with another disjunct
      ruleRegExpr.append(regExpr);
      if (i < iMax - 2) {
        ruleRegExpr.append("|");
      }
    }
    // add rule to map
    RegExp regExp = FACTORY.createRegExp(ruleRegExpr.toString());
    getRulesMap().put(ALL_RULE, regExp);
  }
}
