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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.dfki.lt.tools.tokenizer.exceptions.InitializationException;
import de.dfki.lt.tools.tokenizer.exceptions.ProcessingException;
import de.dfki.lt.tools.tokenizer.regexp.RegExp;

/**
 * Manages the language-specific information needed by the tokenizer to process
 * a document of that language.
 *
 * @author Joerg Steffen, DFKI
 */
public class LanguageResource {

  /**
   * Contains the name suffix of the resource file with the classes hierarchy.
   */
  private static final String CLASSES_HIERARCHY = "_class_hierarchy.xml";

  /**
   * Contains the name suffix of the config file with the macros.
   */
  private static final String MACRO_CFG = "_macros.cfg";


  /**
   * Contains the name of the language for which this class contains the
   * resources.
   */
  private String language;

  /**
   * Contains root element of the classes hierarchy.
   */
  private Element classesRoot;

  /**
   * Contains the name of the root element of the classes hierarchy.
   */
  private String classesRootName;

  /**
   * Contains a map from class names to a lists of class names that are
   * ancestors of this class.
   */
  private Map<String, List<String>> ancestorsMap;

  /**
   * Contains the punctuation description.
   */
  private PunctDescription punctDescr;

  /**
   * Contains the clitics description.
   */
  private CliticsDescription clitDescr;

  /**
   * Contains the abbreviations description.
   */
  private AbbrevDescription abbrevDescr;

  /**
   * Contains the token classes description.
   */
  private TokenClassesDescription classesDescr;


  /**
   * Creates a new instance of {@link LanguageResource} for teh given language
   * using the resource description files in the given resource directory.
   *
   * @param lang
   *          the name of the language for which this class contains the
   *          resources
   * @param resourceDir
   *          the name of the resource directory
   * @exception InitializationException
   *              if an error occurs
   */
  public LanguageResource(String lang, String resourceDir) {

    // init stuff
    this.setAncestorsMap(new HashMap<String, List<String>>());
    this.setPunctDescr(null);
    this.setClitDescr(null);
    this.setAbbrevDescr(null);
    this.setClassseDescr(null);
    this.language = lang;
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    try {
      // create builder for parsing xml
      DocumentBuilder builder = factory.newDocumentBuilder();
      // load classes hierarchy
      Document doc = null;
      // try to load common classes hierarchy
      try {
        doc = builder.parse(
          FileTools.openResourceFileAsStream(
            Paths.get("jtok").resolve(Description.COMMON)
              .resolve(Description.COMMON + CLASSES_HIERARCHY).toString()));
      } catch (FileNotFoundException fne) {
        // do nothing
      }
      // try to load language specific classes hierarchy,
      // overwriting common one
      try {
        doc = builder.parse(
          FileTools.openResourceFileAsStream(
            Paths.get(resourceDir)
              .resolve(lang + CLASSES_HIERARCHY).toString()));
      } catch (FileNotFoundException fne) {
        // do nothing
      }
      // at least one classes hierarchy must have been loaded
      if (null == doc) {
        throw new InitializationException(
          String.format("missing class hierarchy for language %s", lang));
      }

      // set hierarchy root
      this.setClassesRoot(doc.getDocumentElement());
      // map class names to dom elements
      this.mapSingleClass(this.getClassesRoot());
      this.mapClasses(this.getClassesRoot().getChildNodes());

      // load macros
      Map<String, String> macrosMap = new HashMap<>();
      Description.loadMacros(
        Paths.get("jtok").resolve(Description.COMMON)
          .resolve(Description.COMMON + MACRO_CFG),
        macrosMap);
      Description.loadMacros(
        Paths.get(resourceDir).resolve(lang + MACRO_CFG),
        macrosMap);

      // load punctuation description
      this.setPunctDescr(
        new PunctDescription(resourceDir, lang, macrosMap));

      // load clitics description
      this.setClitDescr(
        new CliticsDescription(resourceDir, lang, macrosMap));

      // load abbreviation description
      this.setAbbrevDescr(
        new AbbrevDescription(resourceDir, lang, macrosMap));

      // load token classes description document
      this.setClassseDescr(
        new TokenClassesDescription(resourceDir, lang, macrosMap));

    } catch (SAXException spe) {
      throw new InitializationException(spe.getLocalizedMessage(), spe);
    } catch (ParserConfigurationException pce) {
      throw new InitializationException(pce.getLocalizedMessage(), pce);
    } catch (IOException ioe) {
      throw new InitializationException(ioe.getLocalizedMessage(), ioe);
    }
  }


  /**
   * Returns the language of this language resource.
   *
   * @return the language
   */
  public String getLanguage() {

    return this.language;
  }


  /**
   * Returns the classes root element.
   *
   * @return the classes root element
   */
  Element getClassesRoot() {

    return this.classesRoot;
  }


  /**
   * Sets the the classes root element to the given parameter.
   *
   * @param classesRoot
   *          the classes root element
   */
  void setClassesRoot(Element classesRoot) {

    this.classesRoot = classesRoot;
    this.classesRootName = classesRoot.getTagName();
  }


  /**
   * Returns the ancestor map.
   *
   * @return the ancestor map
   */
  Map<String, List<String>> getAncestorsMap() {

    return this.ancestorsMap;
  }


  /**
   * Sets the the ancestor map to the given parameter.
   *
   * @param ancestorMap
   *          the ancestor map
   */
  void setAncestorsMap(Map<String, List<String>> ancestorMap) {

    this.ancestorsMap = ancestorMap;
  }


  /**
   * Returns the punctuation description.
   *
   * @return the punctuation description
   */
  PunctDescription getPunctDescr() {

    return this.punctDescr;
  }


  /**
   * Sets the punctuation description to the given parameter.
   *
   * @param punctDescr
   *          a punctuation description
   */
  void setPunctDescr(PunctDescription punctDescr) {

    this.punctDescr = punctDescr;
  }


  /**
   * Returns the clitics description.
   *
   * @return the clitics description
   */
  CliticsDescription getClitDescr() {

    return this.clitDescr;
  }


  /**
   * Sets the clitics description to the given parameter.
   *
   * @param clitDescr
   *          a clitics description
   */
  void setClitDescr(CliticsDescription clitDescr) {

    this.clitDescr = clitDescr;
  }


  /**
   * Returns the abbreviations description.
   *
   * @return the abbreviations description
   */
  AbbrevDescription getAbbrevDescr() {

    return this.abbrevDescr;
  }


  /**
   * Sets the abbreviations description to the given parameter.
   *
   * @param abbrevDescr
   *          a abbreviations description
   */
  void setAbbrevDescr(AbbrevDescription abbrevDescr) {

    this.abbrevDescr = abbrevDescr;
  }


  /**
   * Returns the token classes description.
   *
   * @return the token classes description
   */
  TokenClassesDescription getClassesDescr() {

    return this.classesDescr;
  }


  /**
   * Sets the token classes description to the given parameter.
   *
   * @param classesDescr
   *          a token classes description
   */
  void setClassseDescr(TokenClassesDescription classesDescr) {

    this.classesDescr = classesDescr;
  }


  /**
   * Iterates recursively over a list of class elements and adds each elements
   * ancestors to ancestors map using the name of the element as key.
   *
   * @param elementList
   *          node list of class elements
   */
  private void mapClasses(NodeList elementList) {

    // iterate over elements
    for (int i = 0, iMax = elementList.getLength(); i < iMax; i++) {
      Object oneObj = elementList.item(i);
      if (!(oneObj instanceof Element)) {
        continue;
      }
      Element oneEle = (Element)oneObj;
      this.mapSingleClass(oneEle);
      // add children of element to maps
      if (oneEle.getChildNodes().getLength() > 0) {
        this.mapClasses(oneEle.getChildNodes());
      }
    }
  }


  /**
   * Creates mappings for the given class in the ancestor maps.
   *
   * @param ele
   *          a class element
   */
  private void mapSingleClass(Element ele) {

    String key = ele.getTagName();
    // collect ancestors of element
    List<String> ancestors = new ArrayList<>();
    Node directAncestor = ele.getParentNode();
    while ((null != directAncestor)
        && (directAncestor instanceof Element)
        && (directAncestor != this.classesRoot)) {
      ancestors.add(((Element)directAncestor).getTagName());
      directAncestor = directAncestor.getParentNode();
    }
    // add list to ancestors map
    this.getAncestorsMap().put(key, ancestors);
  }


  /**
   * Checks if the first given class is ancestor in the class hierarchy of the
   * second given class> or equals the second given class.
   *
   * @param class1
   *          the first class name
   * @param class2
   *          the second class name
   * @return a flag indicating the ancestor relation
   * @exception ProcessingException
   *              if the second class name is not a defined class
   */
  boolean isAncestor(String class1, String class2)
      throws ProcessingException {

    if (class1.equals(this.classesRootName) || class1.equals(class2)) {
      return true;
    }

    List<String> ancestors = this.getAncestorsMap().get(class2);
    if (null == ancestors) {
      // if there is explicit entry in the classes hierarchy, it is assumed
      // that the token is a direct child of the root
      return class1.equals(this.classesRootName);
    }
    return ancestors.contains(class1);
  }


  /**
   * Returns the matcher for all punctuation from the punctuation description.
   *
   * @return a regular expression
   */
  RegExp getAllPunctMatcher() {

    return this.getPunctDescr().getRulesMap()
      .get(PunctDescription.ALL_RULE);
  }


  /**
   * Returns the matcher for internal punctuation from the punctuation
   * description.
   *
   * @return a regular expression
   */
  RegExp getInternalMatcher() {

    return this.getPunctDescr().getRulesMap()
      .get(PunctDescription.INTERNAL_RULE);
  }


  /**
   * Returns the matcher for sentence internal punctuation from the punctuation
   * description.
   *
   * @return a regular expression
   */
  RegExp getInternalTuMatcher() {

    return this.getPunctDescr().getRulesMap()
      .get(PunctDescription.INTERNAL_TU_RULE);
  }


  /**
   * Returns the matcher for proclitics from the clitics description.
   *
   * @return a regular expression
   */
  RegExp getProcliticsMatcher() {

    return this.getClitDescr().getRulesMap()
      .get(CliticsDescription.PROCLITIC_RULE);
  }


  /**
   * Returns the matcher for enclitics from the clitics description.
   *
   * @return a regular expression
   */
  RegExp getEncliticsMatcher() {

    return this.getClitDescr().getRulesMap()
      .get(CliticsDescription.ENCLITIC_RULE);
  }


  /**
   * Returns the map with the abbreviation lists.
   *
   * @return the map with the abbreviation lists
   */
  Map<String, Set<String>> getAbbrevLists() {

    return this.getAbbrevDescr().getClassMembersMap();
  }


  /**
   * Returns the matcher for the all abbreviations from the abbreviations
   * description.
   *
   * @return a regular expression
   */
  RegExp getAllAbbrevMatcher() {

    return this.getAbbrevDescr().getRulesMap()
      .get(AbbrevDescription.ALL_RULE);
  }


  /**
   * Returns the set of the most common terms that only start with a capital
   * letter when they are at the beginning of a sentence.
   *
   * @return a set with the terms
   */
  Set<String> getNonCapTerms() {

    return this.getAbbrevDescr().getNonCapTerms();
  }


  /**
   * Returns the matcher for all token classes from the token classes
   * description.
   *
   * @return a regular expression
   */
  RegExp getAllClassesMatcher() {

    return this.getClassesDescr().getRulesMap()
      .get(TokenClassesDescription.ALL_RULE);
  }
}
