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
  private static final String CLASSES_HIERARCHY = "_classes.xml";

  /**
   * Contains the name suffix of the resource file with the punctuation
   * description.
   */
  private static final String PUNCT_DESCR = "_punct.xml";

  /**
   * Contains the name suffix of the resource file with the clitic description.
   */
  private static final String CLITIC_DESCR = "_clitics.xml";

  /**
   * Contains the name suffix of the resource file with the abbreviations
   * description.
   */
  private static final String ABBREV_DESCR = "_abbrev.xml";

  /**
   * Contains the name suffix of the resource file with the numbers description.
   */
  private static final String NUMB_DESCR = "_numbers.xml";

  /**
   * The name of the tag attribute in the class definition.
   */
  private static final String CLASS_TAG = "tag";

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
   * Contains a map from class names to their tags as defined in the class
   * definition file.
   */
  private Map<String, String> tagsMap;

  /**
   * Contains a map from tags to their class names as defined in the class
   * definition file.
   */
  private Map<String, String> classesMap;

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
   * Contains the numbers description.
   */
  private NumbersDescription numbDescr;


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
    this.setTagsMap(new HashMap<String, String>());
    this.setClassesMap(new HashMap<String, String>());
    this.setPunctDescr(null);
    this.setClitDescr(null);
    this.setAbbrevDescr(null);
    this.setNumbDescr(null);
    this.language = lang;
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    try {
      // create builder for parsing xml
      DocumentBuilder builder = factory.newDocumentBuilder();
      // load classes hierarchy
      Document doc = builder.parse(
        FileTools.openResourceFileAsStream(
          Paths.get(resourceDir).resolve(lang + CLASSES_HIERARCHY).toString()));
      // set hierarchy root
      this.setClassesRoot(doc.getDocumentElement());
      // map class names to dom elements and tag
      this.mapSingleClass(this.getClassesRoot());
      this.mapClasses(this.getClassesRoot().getChildNodes());

      // load punctuation description document
      doc = builder.parse(
        FileTools.openResourceFileAsStream(
          Paths.get(resourceDir).resolve(lang + PUNCT_DESCR).toString()));
      this.setPunctDescr
        (new PunctDescription(doc, this.getTagsMap().keySet()));

      // load clitics description document
      doc = builder.parse(
        FileTools.openResourceFileAsStream(
          Paths.get(resourceDir).resolve(lang + CLITIC_DESCR).toString()));
      this.setClitDescr
        (new CliticsDescription(doc, this.getTagsMap().keySet()));

      // load abbreviation description document
      doc = builder.parse(
        FileTools.openResourceFileAsStream(
          Paths.get(resourceDir).resolve(lang + ABBREV_DESCR).toString()));
      this.setAbbrevDescr
        (new AbbrevDescription(doc,
          this.getTagsMap().keySet(),
          resourceDir));

      // load numbers description document
      doc = builder.parse(
        FileTools.openResourceFileAsStream(
          Paths.get(resourceDir).resolve(lang + NUMB_DESCR).toString()));
      this.setNumbDescr
        (new NumbersDescription(doc,
          this.getTagsMap().keySet()));

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
   * Returns the map from class names to their tags as defined in the class
   * definition file. The class names are used as annotations in the annotated
   * string. The tags could be used as XML tags when creating an XML structure
   * from the annotated string.
   *
   * @return the tags map
   */
  public Map<String, String> getTagsMap() {

    return this.tagsMap;
  }


  /**
   * Sets the tags map to the given parameter.
   *
   * @param tagsMap
   *          the tags map
   */
  void setTagsMap(Map<String, String> tagsMap) {

    this.tagsMap = tagsMap;
  }


  /**
   * Returns the classes map.
   *
   * @return the classes map
   */
  Map<String, String> getClassesMap() {

    return this.classesMap;
  }


  /**
   * Sets the the classes map to the given parameter.
   *
   * @param classesMap
   *          the classes map
   */
  void setClassesMap(Map<String, String> classesMap) {

    this.classesMap = classesMap;
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
   * Returns the numbers description.
   *
   * @return the numbers description
   */
  NumbersDescription getNumbDescr() {

    return this.numbDescr;
  }


  /**
   * Sets the numbers description to the given parameter.
   *
   * @param numbDescr
   *          a numbers description
   */
  void setNumbDescr(NumbersDescription numbDescr) {

    this.numbDescr = numbDescr;
  }


  /**
   * Iterates recursively over a list of class elements and adds each elements
   * ancestors to ancestors map using the name of the element as key. It also
   * adds each class tag to the tags map.
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
   * Creates mappings for the given class in the tags, classes and ancestor
   * maps.
   *
   * @param ele
   *          a class element
   */
  private void mapSingleClass(Element ele) {

    String key = ele.getTagName();
    // add tag to tags map
    String tag = ele.getAttribute(CLASS_TAG);
    this.getTagsMap().put(key, tag);
    // add class to classes map
    this.getClassesMap().put(tag, key);
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
      throw new ProcessingException(
        String.format("undefined token class %s", class2));
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
   * Returns the matcher for non-breaking right punctuation from the punctuation
   * description.
   *
   * @return a regular expression
   */
  RegExp getNbrMatcher() {

    return this.getPunctDescr().getRulesMap()
      .get(PunctDescription.NON_BREAK_RIGHT_RULE);
  }


  /**
   * Returns the matcher for non-breaking left punctuation from the punctuation
   * description.
   *
   * @return a regular expression
   */
  RegExp getNblMatcher() {

    return this.getPunctDescr().getRulesMap()
      .get(PunctDescription.NON_BREAK_LEFT_RULE);
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
   * Returns the matcher for clitic punctuation from the punctuation
   * description.
   *
   * @return a regular expression
   */
  RegExp getCliticsMatcher() {

    return this.getPunctDescr().getRulesMap()
      .get(PunctDescription.CLITIC_RULE);
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
   * Returns the matcher for the abbreviations.
   *
   * @return a regular expression
   */
  RegExp getAbbrevMatcher() {

    return this.getAbbrevDescr().getRulesMap()
      .get(AbbrevDescription.ABBREV_RULE);
  }


  /**
   * Returns the matcher for the mid name initials.
   *
   * @return a regular expression
   */
  RegExp getInitialMatcher() {

    return this.getAbbrevDescr().getRulesMap()
      .get(AbbrevDescription.INITIAL_RULE);
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
   * Returns the matcher for simple digits.
   *
   * @return a regular expression
   */
  RegExp getSimpleDigitsMatcher() {

    return this.getNumbDescr().getRulesMap()
      .get(NumbersDescription.SIMPLE_DIGITS_RULE);
  }


  /**
   * Returns the matcher for ordinal numbers.
   *
   * @return a regular expression
   */
  RegExp getOrdinalMatcher() {

    return this.getNumbDescr().getRulesMap()
      .get(NumbersDescription.ORDINAL_RULE);
  }


  /**
   * Returns the matcher for the digits.
   *
   * @return a regular expression
   */
  RegExp getDigitsMatcher() {

    return this.getNumbDescr().getRulesMap()
      .get(NumbersDescription.DIGITS_RULE);
  }
}
