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
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.dfki.lt.tools.tokenizer.exceptions.InitializationException;
import de.dfki.lt.tools.tokenizer.regexp.DkBricsRegExpFactory;
import de.dfki.lt.tools.tokenizer.regexp.Match;
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
   * Single line in descriptions that marks the start of the lists section.
   */
  protected static String LISTS_MARKER = "LISTS:";

  /**
   * Single line in descriptions that marks the start of the definitions
   * section.
   */
  protected static String DEFS_MARKER = "DEFINITIONS:";

  /**
   * Single line in descriptions that marks the start of the rules section.
   */
  protected static String RULES_MARKER = "RULES:";

  /**
   * Regular expression for matching references used in regular expressions of
   * config files.
   */
  private static RegExp REF_MATCHER =
    FACTORY.createRegExp("\\<[A-Za-z0-9_]+\\>");

  /**
   * Contains the logger object for logging.
   */
  private static final Logger LOG = LoggerFactory.getLogger(Description.class);


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
  protected Element getChild(Element ele, String childName) {

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
   * Reads the macro configuration from the given path.
   *
   * @param macroPath
   *          path to the config file
   * @return a map of macro names to regular expression strings
   */
  protected static Map<String, String> loadMacros(String macroPath) {

    Map<String, String> macroMap = new HashMap<>();

    // read config file
    try {
      BufferedReader in =
        new BufferedReader(
          new InputStreamReader(
            FileTools.openResourceFileAsStream(macroPath.toString()),
            "utf-8"));
      String line;
      while ((line = in.readLine()) != null) {
        line = line.trim();
        if (line.length() == 0 || line.startsWith("#")) {
          continue;
        }
        int sep = line.indexOf(":");
        if (sep == -1) {
          LOG.error(String.format(
            "missing separator in macros configuration line %s", line));
        }
        String macroName = line.substring(0, sep).trim();
        String regExpString = line.substring(sep + 1).trim();

        // expand possible macros
        regExpString = replaceReferences(regExpString, macroMap);

        macroMap.put(macroName, regExpString);
      }
    } catch (IOException ioe) {
      throw new InitializationException(ioe.getLocalizedMessage(), ioe);
    }

    return macroMap;
  }


  /**
   * Reads the definitions section from the given reader to map each token class
   * from the definitions to a regular expression that matches all tokens of
   * that class. Also creates and returns the definitions map.
   *
   * @param in
   *          the reader
   * @param macrosMap
   *          a map of macro names to regular expression strings
   * @return a map of definition names to regular expression strings
   * @throws IOException
   *           if there is an error during reading
   */
  protected Map<String, String> loadDefinitions(
    BufferedReader in, Map<String, String> macrosMap)
      throws IOException {

    // init temporary map where to store the regular expression string
    // for each class
    Map<String, StringBuilder> tempMap = new HashMap<>();

    // this maps the definition names to their regular expression strings
    Map<String, String> defMap = new LinkedHashMap<>();

    String line;
    while ((line = in.readLine()) != null) {
      line = line.trim();
      if (line.length() == 0 || line.startsWith("#")) {
        continue;
      }
      if (line.equals(RULES_MARKER)) {
        break;
      }

      int firstSep = line.indexOf(":");
      int secondSep = line.lastIndexOf(":");
      if (firstSep == -1 || secondSep == firstSep) {
        LOG.error(
          String.format(
            "missing separator in definitions section line %s", line));
        continue;
      }
      String defName = line.substring(0, firstSep).trim();
      String regExpString = line.substring(firstSep + 1, secondSep).trim();
      String className = line.substring(secondSep + 1).trim();

      // expand possible macros
      regExpString = replaceReferences(regExpString, macrosMap);

      // extend class matcher:
      // get old entry
      StringBuilder oldRegExpr = tempMap.get(className);
      // if there is no old entry create a new one
      if (null == oldRegExpr) {
        StringBuilder newRegExpr = new StringBuilder(regExpString);
        tempMap.put(className, newRegExpr);
      }
      else {
        // extend regular expression with another disjunct
        oldRegExpr.append("|" + regExpString);
      }

      // save definition
      if (defMap.get(defName) != null) {
        LOG.error(
          String.format("duplicate definition %s: %s", defName, regExpString));
        continue;
      }
      defMap.put(defName, regExpString);
    }

    // create regular expressions from regular expression strings and store them
    // under their class name in definitions map
    for (Map.Entry<String, StringBuilder> oneEntry : tempMap.entrySet()) {
      getDefinitionsMap().put(
        oneEntry.getKey(),
        FACTORY.createRegExp(oneEntry.getValue().toString()));
    }

    return defMap;
  }


  /**
   * Reads the rules section from the given reader to map each rules to a
   * regular expression that matches all tokens of that rule.
   *
   * each rule from the description to a regular expression that matches all
   * tokens from that rule.
   *
   * @param in
   *          the reader
   * @param defsMap
   *          a map of definition names to regular expression strings
   * @param macrosMap
   *          a map of macro names to regular expression strings
   * @throws IOException
   *           if there is an error during reading
   */
  protected void loadRules(
      BufferedReader in, Map<String, String> defsMap,
      Map<String, String> macrosMap)
      throws IOException {

    String line;
    while ((line = in.readLine()) != null) {
      line = line.trim();
      if (line.length() == 0 || line.startsWith("#")) {
        continue;
      }
      int firstSep = line.indexOf(":");
      int secondSep = line.lastIndexOf(":");
      if (firstSep == -1 || secondSep == firstSep) {
        LOG.error(
          String.format("missing separator in rules section line %s", line));
        continue;
      }
      String ruleName = line.substring(0, firstSep).trim();
      String regExpString = line.substring(firstSep + 1, secondSep).trim();
      String className = line.substring(secondSep + 1).trim();

      // expand definitions
      regExpString = replaceReferences(regExpString, defsMap);
      // expand possible macros
      regExpString = replaceReferences(regExpString, macrosMap);

      // add rule to map
      RegExp regExp = FACTORY.createRegExp(regExpString);
      getRulesMap().put(ruleName, regExp);
      // if rule has a class, add regular expression to regular expression map
      if (className.length() > 0) {
        getRegExpMap().put(regExp, className);
      }
    }
  }


  /**
   * Reads the lists section from the given reader to map each token class from
   * the lists to a set that contains all members of that class.
   *
   * Uses the lists section in a description file to map each token class from
   * the lists to a set that contains all members of that class.
   *
   * @param in
   *          the reader
   * @param resourceDir
   *          the resource directory
   * @throws IOException
   *           if there is an error during reading
   */
  protected void loadLists(BufferedReader in, String resourceDir)
      throws IOException {

    String line;
    while ((line = in.readLine()) != null) {
      line = line.trim();
      if (line.length() == 0 || line.startsWith("#")) {
        continue;
      }
      if (line.equals(DEFS_MARKER)) {
        break;
      }

      int sep = line.indexOf(":");
      if (sep == -1) {
        LOG.error(String.format(
          "missing separator in lists section line %s", line));
      }
      String listFileName = line.substring(0, sep).trim();
      String className = line.substring(sep + 1).trim();
      this.loadList(Paths.get(resourceDir).resolve(listFileName), className);
    }
  }


  /**
   * Loads the abbreviations list from the given path and stores its items under
   * the given class name
   *
   * @param listPath
   *          the abbreviations list path
   * @param className
   *          the class name
   * @throws IOException
   *           if there is an error when reading the list
   */
  private void loadList(Path listPath, String className)
      throws IOException {

    BufferedReader in =
      new BufferedReader(
        new InputStreamReader(
          FileTools.openResourceFileAsStream(listPath.toString()),
          StandardCharsets.UTF_8));
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
  }


  /**
   * Replaces references in the given regular expression string using the given
   * reference map.
   *
   * @param regExpString
   *          the regular expression string with possible references
   * @param refMap
   *          a map of reference name to regular expression strings
   * @return the modified regular expression string
   */
  private static String replaceReferences(
      String regExpString, Map<String, String> refMap) {

    String result = regExpString;

    List<Match> references = REF_MATCHER.getAllMatches(regExpString);

    for (Match oneRef : references) {
      // get reference name by removing opening and closing angle brackets
      String refName =
        oneRef.getImage().substring(1, oneRef.getImage().length() - 1);
      String refRegExpr = refMap.get(refName);
      if (null == refRegExpr) {
        LOG.error(
          String.format("unknown reference %s in regular expression %s",
            refName, regExpString));
        continue;
      }
      result = result.replaceFirst(
        oneRef.getImage(), Matcher.quoteReplacement(refRegExpr));
    }

    return result;
  }


  /**
   * Creates a rule that matches ALL definitions.
   *
   * @param defsMap
   *          the definitions map
   * @return a regular expression matching all definitions
   */
  protected static RegExp createAllRule(Map<String, String> defsMap) {

    StringBuilder ruleRegExpr = new StringBuilder();

    // iterate over definitions
    List<String> defsList = new ArrayList<>(defsMap.values());
    for (int i = 0, iMax = defsList.size(); i < iMax; i++) {
      String regExpr = defsList.get(i);
      // extend regular expression with another disjunct
      ruleRegExpr.append(regExpr);
      if (i < iMax - 1) {
        ruleRegExpr.append("|");
      }
    }
    return FACTORY.createRegExp(ruleRegExpr.toString());
  }
}
