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
import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.dfki.lt.tools.tokenizer.exceptions.InitializationException;
import de.dfki.lt.tools.tokenizer.regexp.RegExp;

/**
 * Manages the content of a abbreviation description file.
 *
 * @author Joerg Steffen, DFKI
 */
public class AbbrevDescription
    extends Description {

  /**
   * The name of the all abbreviation rule.
   */
  protected static final String ALL_RULE = "ALL_RULE";


  /**
   * Contains the most common terms that only start with a capital letter when
   * they are at the beginning of a sentence.
   */
  private Set<String> nonCapTerms;


  /**
   * Creates a new instance of {@link AbbrevDescription} for the abbreviation
   * description contained in the given DOM document.
   *
   * @param abbrDescr
   *          a DOM document with the abbreviation description
   * @param classes
   *          a set with the defined classes, used for validation
   * @param resourceDir
   *          the name of the resource directory
   * @exception InitializationException
   *              if an error occurs
   */
  public AbbrevDescription(
      Document abbrDescr, Set<String> classes, String resourceDir) {

    super.setDefinitionsMap(new HashMap<String, RegExp>());
    super.setRulesMap(new HashMap<String, RegExp>());
    super.setRegExpMap(new HashMap<RegExp, String>());
    super.setClassMembersMap(new HashMap<String, Set<String>>());

    // build the lists map
    super.loadLists(abbrDescr, classes, resourceDir);
    // build the classes matcher map
    super.loadDefinitions(abbrDescr, classes);
    // build the rules matcher map
    super.loadRules(abbrDescr);
    this.createAllAbbreviationsRule(abbrDescr);

    // load list of terms that only start with a capital letter when they are
    // at the beginning of a sentence.
    try {
      BufferedReader in =
        new BufferedReader(
          new InputStreamReader(
            FileTools.openResourceFileAsStream(
              resourceDir + "/nonCapTerms.txt"),
            "utf-8"));
      // init set where to store the terms
      this.nonCapTerms = new HashSet<String>();

      String line;
      while ((line = in.readLine()) != null) {
        line = line.trim();
        // ignore lines starting with #
        if (line.startsWith("#") || (line.length() == 0)) {
          continue;
        }
        // extract the term and add it to the set
        int end = line.indexOf('#');
        if (-1 != end) {
          line = line.substring(0, end).trim();
          if (line.length() == 0) {
            continue;
          }
        }

        // convert first letter to upper case to make runtime comparison more
        // efficient
        char firstChar = line.charAt(0);
        firstChar = Character.toUpperCase(firstChar);
        this.nonCapTerms.add(firstChar + line.substring(1));
        // also add a version completely in upper case letters
        this.nonCapTerms.add(line.toUpperCase());
      }

      in.close();
    } catch (IOException ioe) {
      throw new InitializationException(ioe.getLocalizedMessage(), ioe);
    }
  }


  /**
   * Returns the set of the most common terms that only start with a capital
   * letter when they are at the beginning of a sentence.
   *
   * @return a set with the terms
   */
  protected Set<String> getNonCapTerms() {

    return this.nonCapTerms;
  }


  /**
   * Create a rule that matches ALL abbreviations for which there are
   * definitions.
   */
  private void createAllAbbreviationsRule(Document abbrDescr) {

    // get list of definitions
    NodeList defs =
      this.getChild(abbrDescr.getDocumentElement(), DEFS).getChildNodes();

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
