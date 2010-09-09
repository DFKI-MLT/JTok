/*
 * JTok
 * A configurable tokenizer implemented in Java
 *
 * (C) 2003 - 2005  DFKI Language Technology Lab http://www.dfki.de/lt
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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import de.dfki.lt.tools.tokenizer.exceptions.InitializationException;
import de.dfki.lt.tools.tokenizer.regexp.JavaRegExpFactory;
import de.dfki.lt.tools.tokenizer.regexp.RegExp;
import de.dfki.lt.tools.tokenizer.regexp.RegExpFactory;

/**
 * <code>Description</code> is an abstract class that provides common
 * methods to manage the content of description files.
 *
 * @author Joerg Steffen, DFKI
 * @version $Id: Description.java,v 1.8 2010-04-30 09:25:45 steffen Exp $ */

public abstract class Description {

  /**
   * This is the name of the element with the definitions in the
   * description files. */
  protected static final String DEFS = "DEFINITIONS";

  /**
   * This is the attribute of a definition element that contains the
   * regular expression. */
  protected static final String DEF_REGEXP = "regexp";

  /**
   * This is the attribute of a definition or list element that
  contains the class name. */
  protected static final String DEF_CLASS = "class";

  /**
   * This is the name of the element with the lists in the description
   * files. */
  protected static final String LISTS = "LISTS";

  /**
   * This is the attribute of a list element that point to the list
   * file. */
  protected static final String LIST_FILE = "file";

  /**
   * This is the attribute of a list element that contains the encoding
   * of the list file.
   */
  protected static final String LIST_ENCODING = "encoding";

  /**
   * This is the name of the element with the rules in the
   * description files. */
  protected static final String RULES = "RULES";

  /**
   * This is the factory for creating regular expressions. */
  protected static RegExpFactory FACTORY = new JavaRegExpFactory();


  /**
   * This maps a class to a regular expression that matches all
   * tokens of this class. The regular expression is build as a
   * disjunction of the regular expressions used in the
   * definitions. If a rule matches expressions from more than one
   * class, this is used to identify the class. */
  protected HashMap definitionsMap;

  /**
   * This maps the rule names to regular expressions that match the
   * tokens as described by the rule. */
  protected HashMap rulesMap;

  /**
   * This maps regular expressions of rules to class names of the
   * matched expression. This is used for rules that only match
   * expressions that all have the same class. */
  protected HashMap regExpMap;

  /**
   * This maps a class to a hash map that contains members of this
   * class. */
  protected HashMap listsMap;


  /**
   * This returns the field {@link #definitionsMap}.
   *
   * @return a <code>HashMap</code> */
  protected HashMap getDefinitionsMap() {
    return this.definitionsMap;
  }

  /**
   * This sets the field {@link #definitionsMap} to
   * <code>aDefinitionsMap</code>.
   *
   * @param aDefinitionsMap a <code>HashMap</code> */
  protected void setDefinitionsMap(HashMap aDefinitionsMap){
    this.definitionsMap = aDefinitionsMap;
  }


  /**
   * This returns the field {@link #rulesMap}.
   *
   * @return a <code>HashMap</code> */
  protected HashMap getRulesMap() {
    return this.rulesMap;
  }

  /**
   * This sets the field {@link #rulesMap} to
   * <code>aRulesMap</code>.
   *
   * @param aRulesMap a <code>HashMap</code> */
  protected void setRulesMap(HashMap aRulesMap){
    this.rulesMap = aRulesMap;
  }


  /**
   * This returns the field {@link #regExpMap}.
   *
   * @return a <code>HashMap</code> */
  protected HashMap getRegExpMap() {
    return this.regExpMap;
  }

  /**
   * This sets the field {@link #regExpMap} to
   * <code>aRegExpMap</code>.
   *
   * @param aRegExpMap a <code>HashMap</code> */
  protected void setRegExpMap(HashMap aRegExpMap){
    this.regExpMap = aRegExpMap;
  }


  /**
   * This returns the field {@link #listsMap}.
   *
   * @return a <code>HashMap</code> */
  protected HashMap getListsMap() {
    return this.listsMap;
  }

  /**
   * This sets the field {@link #listsMap} to
   * <code>aListsMap</code>.
   *
   * @param aListsMap a <code>HashMap</code> */
  protected void setListsMap(HashMap aListsMap){
    this.listsMap = aListsMap;
  }


  /**
   * This returns the first child element within the given element with the
   * given name. If no elements exist for the specified name, <code>null</code>
   * is returned.
   *
   * @param anEle an <code>Element</code> value
   * @param aName a <code>String</code> value
   * @return a <code>List</code> of <code>Element</code>s
   */
  private Element getChild(Element anEle, String aName) {
    NodeList children = anEle.getChildNodes();
    for (int i = 0, iMax = children.getLength(); i < iMax; i++) {
      Node oneChild = children.item(i);
      if ((oneChild instanceof Element) &&
          ((Element)oneChild).getTagName().equals(aName)) {
        return (Element)oneChild;
      }
    }
    return null;
  }


  /**
   * This uses the definitions section in a description file
   * to map each token class from the definitions to a regular
   * expression that matches all tokens of that class.
   *
   * @param aDescr a dom <code>Document</code> with a description
   * @param classes a <code>Set</code> with the defined classes, used
   * for validation
   * @exception InitializationException if definitions description
   * contains illegal regular expression or undefined classes */
  protected void loadDefinitions(Document aDescr,
                                 Set classes) {

    // get list of definitions
    NodeList defs =
      this.getChild(aDescr.getDocumentElement(), DEFS).getChildNodes();

    // init temporary hashmap where to store the regexp string for
    // each class
    HashMap tempMap = new HashMap();

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
      if (className.length() == 0)
        continue;
      // throw exception if class is unknown
      if (!classes.contains(className))
        throw new InitializationException
          ("undefined class " + className + " in definitions");
      // get regexpr string
      String regExpr = oneDef.getAttribute(DEF_REGEXP);
      // extend class matcher:
      // get old entry
      StringBuffer oldRegExpr = (StringBuffer)tempMap.get(className);
      // if there is no old entry create a new one
      if (null == oldRegExpr) {
        StringBuffer newRegExpr = new StringBuffer(regExpr);
        tempMap.put(className,newRegExpr);
      }
      else
        // extend regular expression with another disjunct
        oldRegExpr.append("|".intern() + regExpr);
    }

    // create regular expressions from regexpr strings and store them
    // under their class name in definitions map
    for (Iterator it = tempMap.keySet().iterator(); it.hasNext();) {
      String key = (String)it.next();
      StringBuffer val = (StringBuffer)tempMap.get(key);
      getDefinitionsMap().put(key, FACTORY.createRegExp(val.toString()));
    }
  }


  /**
   * This maps each rule from the description to a regular
   * expression that matches all tokens from that rule.
   *
   * @param aDescr a dom <code>Document</code> with the description
   * @exception InitializationException if rules description
   * contains illegal regular expression */
  protected void loadRules(Document aDescr) {

    // get root of definitions
    Element defRoot = this.getChild(aDescr.getDocumentElement(), DEFS);

    // get list of rules
    NodeList rules =
      this.getChild(aDescr.getDocumentElement(), RULES).getChildNodes();

    // iterate over rules
    for (int i = 0, iMax = rules.getLength(); i < iMax; i++) {
      // get rules element
      Object oneObj = rules.item(i);
      if (!(oneObj instanceof Element)) {
        continue;
      }
      Element oneRule = (Element)oneObj;

      // initialize string buffer for regular expression
      StringBuffer ruleRegExpr = new StringBuffer();
      // create dummy matcher for empty rules; dummy matcher matches
      // a blank
      if (oneRule.getChildNodes().getLength() == 0) {
        ruleRegExpr.append(" ".intern());
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
            if (null == regExprEle)
              throw new InitializationException
                ("undefined definition " + ruleEleName + " in rule");
            // extend regular expression
            ruleRegExpr.append(regExprEle.getAttribute(DEF_REGEXP));
          }
        }
      }
      // add rule to map
      RegExp aRegExp = FACTORY.createRegExp(ruleRegExpr.toString());
      getRulesMap().put(oneRule.getTagName(), aRegExp);
      // check if rule has a class attribute, if yes, add regexp to
      // regexp map
      String className = oneRule.getAttribute(DEF_CLASS);
      if (className.length() > 0)
        getRegExpMap().put(aRegExp, className);
    }
  }


  /**
   * This uses the lists section in a description file
   * to map each token class from the lists to a hashmap that contains
   * all members of that class.
   *
   * @param aDescr a dom <code>Document</code> with a description
   * @param classes a <code>Set</code> with the defined classes, used
   * for validation
   * @param aResourceDir a <code>String</code> with the name of the
   * resource directory
   * @exception InitializationException if lists description contains
   * undefined classes or file */
  protected void loadLists(Document aDescr,
                           Set classes,
                           String aResourceDir) {

    // get list of list elements
    NodeList lists =
      this.getChild(aDescr.getDocumentElement(), LISTS).getChildNodes();

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
      if (!classes.contains(className))
        throw new InitializationException
          ("undefined class " + className + " in lists");
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
                      aResourceDir + "/" + fileName), fileEncoding));
        // init set where to store the abbreviations
        Set<String> items = new HashSet<String>();
        // iterate over lines of file
        String line;
        while ((line = in.readLine()) != null) {
          // ignore lines starting with #
          if (line.startsWith("#".intern()))
            continue;
          // extract the abbreviation and add it to the set
          int end = line.indexOf('#');
          if (-1 != end)
            line = line.substring(0, end);
          line = line.trim();
          if (!line.equals("")) {
            items.add(line);
            // also add the upper case version
            items.add(line.toUpperCase());
          }
        }
        in.close();
        // add set to lists map
        this.getListsMap().put(className, items);
      } catch (IOException ioe) {
        throw new InitializationException(ioe.toString());
      }
    }
  }
}
