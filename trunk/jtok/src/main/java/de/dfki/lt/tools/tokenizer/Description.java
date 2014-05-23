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
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import de.dfki.lt.tools.tokenizer.exceptions.InitializationException;
import de.dfki.lt.tools.tokenizer.regexp.DkBricsRegExpFactory;
import de.dfki.lt.tools.tokenizer.regexp.RegExp;
import de.dfki.lt.tools.tokenizer.regexp.RegExpFactory;

/**
 * Abstract class that provides common methods to manage the content of
 * description files.
 *
 * @author Joerg Steffen, DFKI
 */
public abstract class Description {

  /**
   * Name of the element with the definitions in the description files.
   */
  protected static final String DEFS = "DEFINITIONS";

  /**
   * Attribute of a definition element that contains the regular expression.
   */
  protected static final String DEF_REGEXP = "regexp";

  /**
   * Attribute of a definition or list element that contains the class name.
   */
  protected static final String DEF_CLASS = "class";

  /**
   * Name of the element with the lists in the description files.
   */
  protected static final String LISTS = "LISTS";

  /**
   * Attribute of a list element that point to the list file.
   */
  protected static final String LIST_FILE = "file";

  /**
   * Attribute of a list element that contains the encoding of the list file.
   */
  protected static final String LIST_ENCODING = "encoding";

  /**
   * Name of the element with the rules in the description files.
   */
  protected static final String RULES = "RULES";

  /**
   * Factory for creating regular expressions.
   */
  protected static RegExpFactory FACTORY = new DkBricsRegExpFactory();

  /**
   * Maps a class name to a regular expression that matches all tokens of this
   * class. The regular expression is build as a disjunction of the regular
   * expressions used in the definitions. If a rule matches expressions from
   * more than one class, this is used to identify the class.
   */
  protected Map<String, RegExp> definitionsMap;

  /**
   * Maps the rule names to regular expressions that match the tokens as
   * described by the rule.
   */
  protected Map<String, RegExp> rulesMap;

  /**
   * Maps regular expressions of rules to class names of the matched expression.
   * This is used for rules that only match expressions that all have the same
   * class.
   */
  protected Map<RegExp, String> regExpMap;

  /**
   * Maps a class to a set containing members of this class.
   */
  protected Map<String, Set<String>> classMembersMap;


  /**
   * Returns the definitions map.
   *
   * @return the definitions map
   */
  protected Map<String, RegExp> getDefinitionsMap() {

    return this.definitionsMap;
  }


  /**
   * Sets the definitions map to the given parameter.
   *
   * @param definitionsMap
   *          a definitions map
   */
  protected void setDefinitionsMap(Map<String, RegExp> definitionsMap) {

    this.definitionsMap = definitionsMap;
  }


  /**
   * Returns the rules map.
   *
   * @return the rules map
   */
  protected Map<String, RegExp> getRulesMap() {

    return this.rulesMap;
  }


  /**
   * Sets the rules map to the given parameter.
   *
   * @param rulesMap
   *          a rules map
   */
  protected void setRulesMap(Map<String, RegExp> rulesMap) {

    this.rulesMap = rulesMap;
  }


  /**
   * Returns the regular expressions map.
   *
   * @return the regular expressions map
   */
  protected Map<RegExp, String> getRegExpMap() {

    return this.regExpMap;
  }


  /**
   * Sets the regular expressions map to the given parameter.
   *
   * @param regExpMap
   *          a regular expressions map
   */
  protected void setRegExpMap(Map<RegExp, String> regExpMap) {

    this.regExpMap = regExpMap;
  }


  /**
   * Returns the class members map.
   *
   * @return the class members map
   */
  protected Map<String, Set<String>> getClassMembersMap() {

    return this.classMembersMap;
  }


  /**
   * Sets the class members map to the given parameter.
   *
   * @param classMembersMap
   *          a class members map
   */
  protected void setClassMembersMap(Map<String, Set<String>> classMembersMap) {

    this.classMembersMap = classMembersMap;
  }


  /**
   * Returns the first child element of the given element with the given name.
   * If no such child exists, returns {@code null}.
   *
   * @param ele
   *          the parent element
   * @param childName
   *          the child name
   * @return the first child element with the specified name or {@code null} if
   *         no such child exists
   */
  private Element getChild(Element ele, String childName) {

    NodeList children = ele.getChildNodes();
    for (int i = 0, iMax = children.getLength(); i < iMax; i++) {
      Node oneChild = children.item(i);
      if ((oneChild instanceof Element) &&
        ((Element)oneChild).getTagName().equals(childName)) {
        return (Element)oneChild;
      }
    }
    return null;
  }


  /**
   * Uses the definitions section in a description file to map each token class
   * from the definitions to a regular expression that matches all tokens of
   * that class.
   *
   * @param descrDoc
   *          a DOM document with a description
   * @param classes
   *          a set with the defined classes, used for validation
   * @exception InitializationException
   *              if definitions description contains illegal regular expression
   *              or undefined classes
   */
  protected void loadDefinitions(Document descrDoc, Set<String> classes) {

    // get list of definitions
    NodeList defs =
      this.getChild(descrDoc.getDocumentElement(), DEFS).getChildNodes();

    // init temporary map where to store the regular expression string
    // for each class
    Map<String, StringBuilder> tempMap = new HashMap<>();

    // iterate over definitions
    for (int i = 0, iMax = defs.getLength(); i < iMax; i++) {
      // get definition element
      Object oneObj = defs.item(i);
      if (!(oneObj instanceof Element)) {
        continue;
      }
      Element oneDef = (Element)oneObj;
      // get class
      String className =
        oneDef.getAttribute(DEF_CLASS);
      // if there is no class for a definition, ignore it
      if (className.length() == 0) {
        continue;
      }
      // throw exception if class is unknown
      if (!classes.contains(className)) {
        throw new InitializationException(
          String.format("undefined class %s in definitions", className));
      }
      // get regular expression string
      String regExpr = oneDef.getAttribute(DEF_REGEXP);
      // extend class matcher:
      // get old entry
      StringBuilder oldRegExpr = tempMap.get(className);
      // if there is no old entry create a new one
      if (null == oldRegExpr) {
        StringBuilder newRegExpr = new StringBuilder(regExpr);
        tempMap.put(className, newRegExpr);
      }
      else {
        // extend regular expression with another disjunct
        oldRegExpr.append("|" + regExpr);
      }
    }

    // create regular expressions from regular expression strings and store them
    // under their class name in definitions map
    for (Map.Entry<String, StringBuilder> oneEntry : tempMap.entrySet()) {
      getDefinitionsMap().put(
        oneEntry.getKey(),
        FACTORY.createRegExp(oneEntry.getValue().toString()));
    }
  }


  /**
   * Maps each rule from the description to a regular expression that matches
   * all tokens from that rule.
   *
   * @param descrDoc
   *          a DOM document with a description
   * @exception InitializationException
   *              if rules description contains illegal regular expression
   */
  protected void loadRules(Document descrDoc) {

    // get root of definitions
    Element defRoot = this.getChild(descrDoc.getDocumentElement(), DEFS);

    // get list of rules
    NodeList rules =
      this.getChild(descrDoc.getDocumentElement(), RULES).getChildNodes();

    // iterate over rules
    for (int i = 0, iMax = rules.getLength(); i < iMax; i++) {
      // get rules element
      Object oneObj = rules.item(i);
      if (!(oneObj instanceof Element)) {
        continue;
      }
      Element oneRule = (Element)oneObj;

      // initialize string buffer for regular expression
      StringBuilder ruleRegExpr = new StringBuilder();
      // create dummy matcher for empty rules; dummy matcher matches
      // a blank
      if (oneRule.getChildNodes().getLength() == 0) {
        ruleRegExpr.append(" ");
      }
      else {
        // iterate over rule content
        NodeList ruleEles = oneRule.getChildNodes();
        for (int j = 0, jMax = ruleEles.getLength(); j < jMax; j++) {
          Object oneRuleEle = ruleEles.item(j);
          // check if we have a text
          if (oneRuleEle instanceof Text) {
            ruleRegExpr.append(((Text)oneRuleEle).getData().trim());
          }
          else if (oneRuleEle instanceof Element) {
            // get rule element name
            String ruleEleName = ((Element)oneRuleEle).getTagName();
            // get regular expression for rule element
            Element regExprEle = this.getChild(defRoot, ruleEleName);
            if (null == regExprEle) {
              throw new InitializationException(
                String.format("undefined definition %s in rule", ruleEleName));
            }
            // extend regular expression
            ruleRegExpr.append(regExprEle.getAttribute(DEF_REGEXP));
          }
        }
      }
      // add rule to map
      RegExp regExp = FACTORY.createRegExp(ruleRegExpr.toString());
      getRulesMap().put(oneRule.getTagName(), regExp);
      // check if rule has a class attribute, if yes, add regular expression to
      // regular expression map
      String className = oneRule.getAttribute(DEF_CLASS);
      if (className.length() > 0) {
        getRegExpMap().put(regExp, className);
      }
    }
  }


  /**
   * Uses the lists section in a description file to map each token class from
   * the lists to a set that contains all members of that class.
   *
   * @param descrDoc
   *          a DOM document with a description
   * @param classes
   *          a set with the defined classes, used for validation
   * @param resourceDir
   *          the name of the resource directory
   * @exception InitializationException
   *              if lists description contains undefined classes or file
   */
  protected void loadLists(
      Document descrDoc, Set<String> classes, String resourceDir) {

    // get list of list elements
    NodeList lists =
      this.getChild(descrDoc.getDocumentElement(), LISTS).getChildNodes();

    // iterate over lists
    for (int i = 0, iMax = lists.getLength(); i < iMax; i++) {
      // get list element
      Object oneObj = lists.item(i);
      if (!(oneObj instanceof Element)) {
        continue;
      }
      Element oneList = (Element)oneObj;
      // get class
      String className = oneList.getAttribute(DEF_CLASS);
      // throw exception if class is unknown
      if (!classes.contains(className)) {
        throw new InitializationException(
          String.format("undefined class %s in lists", className));
      }
      // get file location
      String fileName = oneList.getAttribute(LIST_FILE);
      // get file encoding
      String fileEncoding = oneList.getAttribute(LIST_ENCODING);
      // open file to read abbreviations
      try {
        BufferedReader in =
          new BufferedReader(
            new InputStreamReader(
              FileTools.openResourceFileAsStream(
                Paths.get(resourceDir).resolve(fileName).toString()),
              fileEncoding));
        // init set where to store the abbreviations
        Set<String> items = new HashSet<String>();
        // iterate over lines of file
        String line;
        while ((line = in.readLine()) != null) {
          line = line.trim();
          // ignore lines starting with #
          if (line.startsWith("#") || (line.length() == 0)) {
            continue;
          }
          // extract the abbreviation and add it to the set
          int end = line.indexOf('#');
          if (-1 != end) {
            line = line.substring(0, end).trim();
            if (line.length() == 0) {
              continue;
            }
          }
          items.add(line);
          // also add the upper case version
          items.add(line.toUpperCase());
          // also add a version with the first letter in
          // upper case (if required)
          char firstChar = line.charAt(0);
          if (Character.isLowerCase(firstChar)) {
            firstChar = Character.toUpperCase(firstChar);
            items.add(firstChar + line.substring(1));
          }
        }
        in.close();
        // add set to lists map
        this.getClassMembersMap().put(className, items);
      } catch (IOException ioe) {
        throw new InitializationException(ioe.getLocalizedMessage(), ioe);
      }
    }
  }
}
