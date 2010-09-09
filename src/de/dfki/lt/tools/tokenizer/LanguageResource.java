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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
 * <code>LanguageResource</code> class manages the language-specific
 * information needed by the  tokenizer to process a document of that
 * language.
 *
 * @author Joerg Steffen, DFKI
 * @version $Id: LanguageResource.java,v 1.7 2010-04-30 09:24:48 steffen Exp $ */

public class LanguageResource {

  /**
   * This contains the name suffix of the resource file with the
   * classes hierarchy. */
  private static final String CLASSES_HIERARCHY = "_classes.xml";

  /**
   * This contains the name suffix of the resource file with the
   * punctuation description. */
  private static final String PUNCT_DESCR = "_punct.xml";

  /**
   * This contains the name suffix of the resource file with the
   * clitic description. */
  private static final String CLITIC_DESCR = "_clitics.xml";

  /**
   * This contains the name suffix of the resource file with the
   * abbreviations description. */
  private static final String ABBREV_DESCR = "_abbrev.xml";

  /**
   * This contains the name suffix of the resource file with the
   * numbers description. */
  private static final String NUMB_DESCR = "_numbers.xml";

  /**
   * This is the name of the tag attribute in the class definition. */
  private static final String CLASS_TAG = "tag";


  /**
   * This contains the name of the language for which this class
   * contains the resources. */
  private String language;

  /**
   * This contains root Element of the classes hierarchy. */
  private Element classesRoot;

  /**
   * This contains a <code>HashMap</code> that maps class names to
   * a <code>List</code> of class names that are ancestors of this
   * class. */
  private HashMap ancestorsMap;

  /**
   * This contains a <code>HashMap</code> that maps class names to
   * their tags as defined in the class definition file. */
  private HashMap tagsMap;

  /**
   * This contains a <code>HashMap</code> that maps tags to their
   * class names as defined in the class definition file. */
  private HashMap classesMap;

  /**
   * This contains the punctuation description. */
  private PunctDescription punctDescr;

  /**
   * This contains the clitics description. */
  private CliticsDescription clitDescr;

  /**
   * This contains the abbreviations description. */
  private AbbrevDescription abbrevDescr;

  /**
   * This contains the numbers description. */
  private NumbersDescription numbDescr;


  /**
   * This creates a new instance of <code>LanguageResource</code>.
   * Not to be used outside this class. */
  private LanguageResource() {
    this.setClassesRoot(null);
    this.setAncestorsMap(new HashMap());
    this.setTagsMap(new HashMap());
    this.setClassesMap(new HashMap());
    this.setPunctDescr(null);
    this.setClitDescr(null);
    this.setAbbrevDescr(null);
    this.setNumbDescr(null);
  }

  /**
   * This creates a new instance of <code>LanguageResource</code> for
   * <code>aLanguage</code> by using the resource description files in
   * <code>aResourceDir</code>.
   *
   * @param aLanguage a <code>String</code> with the name of the
   * language for which this class contains the resources
   * @param aResourceDir a <code>String</code> with the name of the
   * resource directory
   * @exception InitializationException if an error occurs */
  public LanguageResource(String aLanguage,
                          String aResourceDir) {

    // init stuff
    this();
    this.setLanguage(aLanguage);
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    try {
      // create builder for parsing xml
      DocumentBuilder builder = factory.newDocumentBuilder();
      // load classes hierarchy
      Document doc = builder.parse(
        FileTools.openResourceFileAsStream(
          aResourceDir + "/" + aLanguage + CLASSES_HIERARCHY));
      // set hierarchy root
      this.setClassesRoot(doc.getDocumentElement());
      // map class names to dom elements and tag
      this.mapSingleClass(this.getClassesRoot());
      this.mapClasses(this.getClassesRoot().getChildNodes());

      // load punctuation description document
      doc = builder.parse(
        FileTools.openResourceFileAsStream(
          aResourceDir + "/" + aLanguage + PUNCT_DESCR));
      this.setPunctDescr
        (new PunctDescription(doc, this.getTagsMap().keySet()));

      // load clitics description document
      doc = builder.parse(
        FileTools.openResourceFileAsStream(
          aResourceDir + "/" + aLanguage + CLITIC_DESCR));
      this.setClitDescr
        (new CliticsDescription(doc, this.getTagsMap().keySet()));

      // load abbreviation description document
      doc = builder.parse(
        FileTools.openResourceFileAsStream(
          aResourceDir + "/" + aLanguage + ABBREV_DESCR));
      this.setAbbrevDescr
        (new AbbrevDescription(doc,
                               this.getTagsMap().keySet(),
                               aResourceDir));

      // load numbers description document
      doc = builder.parse(
        FileTools.openResourceFileAsStream(
          aResourceDir + "/" + aLanguage + NUMB_DESCR));
      this.setNumbDescr
        (new NumbersDescription(doc,
                                this.getTagsMap().keySet()));

    } catch (SAXException spe) {
      throw new InitializationException(spe.getMessage());
    } catch (ParserConfigurationException pce) {
      throw new InitializationException(pce.getMessage());
    } catch (IOException ioe) {
      throw new InitializationException(ioe.getMessage());
    }
  }


  /**
   * This returns the language of this language resource.
   *
   * @return a <code>String</code> with the language */
  public String getLanguage() {
    return this.language;
  }

  /**
   * This sets the field {@link #language} to
   * <code>aLanguage</code>.
   *
   * @param aLanguage a <code>String</code> */
  private void setLanguage(String aLanguage){
    this.language = aLanguage;
  }


  /**
   * This returns the field {@link #classesRoot}.
   *
   * @return a <code>Element</code> */
  Element getClassesRoot() {
    return this.classesRoot;
  }

  /**
   * This sets the field {@link #classesRoot} to
   * <code>aClassesRoot</code>.
   *
   * @param aClassesRoot a <code>Element</code> */
  void setClassesRoot(Element aClassesRoot) {
    this.classesRoot = aClassesRoot;
  }


  /**
   * This returns the field {@link #ancestorsMap}.
   *
   * @return a <code>HashMap</code> */
  HashMap getAncestorsMap() {
    return this.ancestorsMap;
  }

  /**
   * This sets the field {@link #ancestorsMap} to
   * <code>anAncestorsMap</code>.
   *
   * @param anAncestorsMap a <code>HashMap</code> */
  void setAncestorsMap(HashMap anAncestorsMap){
    this.ancestorsMap = anAncestorsMap;
  }


  /**
   * This returns a <code>HashMap</code> that maps class names to
   * their tags as defined in the class definition file. The class
   * names are used as annotations in the annotated string. The tags
   * could be used as xml tags when creating an xml structure from the
   * annotated string.
   *
   * @return a <code>HashMap</code> */
  public HashMap getTagsMap() {
    return this.tagsMap;
  }

  /**
   * This sets the field {@link #tagsMap} to
   * <code>aTagsMap</code>.
   *
   * @param aTagsMap a <code>HashMap</code> */
  void setTagsMap(HashMap aTagsMap){
    this.tagsMap = aTagsMap;
  }


  /**
   * This returns the field {@link #classesMap}.
   *
   * @return a <code>HashMap</code> */
  HashMap getClassesMap() {
    return this.classesMap;
  }

  /**
   * This sets the field {@link #classesMap} to
   * <code>aClassesMap</code>.
   *
   * @param aClassesMap a <code>HashMap</code> */
  void setClassesMap(HashMap aClassesMap){
    this.classesMap = aClassesMap;
  }


  /**
   * This returns the field {@link #punctDescr}.
   *
   * @return a {@link PunctDescription} */
  PunctDescription getPunctDescr() {
    return this.punctDescr;
  }

  /**
   * This sets the field {@link #punctDescr} to
   * <code>aPunctDescr</code>.
   *
   * @param aPunctDescr a {@link PunctDescription} */
  void setPunctDescr(PunctDescription aPunctDescr){
    this.punctDescr = aPunctDescr;
  }


  /**
   * This returns the field {@link #clitDescr}.
   *
   * @return a <code>CliticsDescription</code> */
  CliticsDescription getClitDescr() {
    return this.clitDescr;
  }

  /**
   * This sets the field {@link #clitDescr} to
   * <code>aClitDescr</code>.
   *
   * @param aClitDescr a <code>CliticsDescription</code> */
  void setClitDescr(CliticsDescription aClitDescr){
    this.clitDescr = aClitDescr;
  }


  /**
   * This returns the field {@link #abbrevDescr}.
   *
   * @return an <code>AbbrevDescription</code> */
  AbbrevDescription getAbbrevDescr() {
    return this.abbrevDescr;
  }

  /**
   * This sets the field {@link #abbrevDescr} to
   * <code>anAbbrevDescr</code>.
   *
   * @param anAbbrevDescr an <code>AbbrevDescription</code> */
  void setAbbrevDescr(AbbrevDescription anAbbrevDescr){
    this.abbrevDescr = anAbbrevDescr;
  }


  /**
   * This returns the field {@link #numbDescr}.
   *
   * @return a <code>NumbersDescription</code> */
  NumbersDescription getNumbDescr() {
    return this.numbDescr;
  }

  /**
   * This sets the field {@link #numbDescr} to
   * <code>aNumbDescr</code>.
   *
   * @param aNumbDescr a <code>NumbersDescription</code> */
  void setNumbDescr(NumbersDescription aNumbDescr){
    this.numbDescr = aNumbDescr;
  }


  /**
   * This iterates recursively over a <code>List</code> of class
   * <code>Element</code>s and adds each elements ancestors to {@link
   * #ancestorsMap} using the name of the element as key. It also
   * adds each class tag to {@link #tagsMap}.
   *
   * @param elementList a <code>NodeList</code> of class elements */
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
   * This creates some mappings for the given class.
   *
   * @param oneEle a class <code>Element</code> */
  private void mapSingleClass(Element oneEle) {
    String key = oneEle.getTagName();
    // add tag to tags map
    String tag = oneEle.getAttribute(CLASS_TAG);
    this.getTagsMap().put(key, tag);
    // add class to classes map
    this.getClassesMap().put(tag, key);
    // collect anchestors of element
    List ancestors = new ArrayList();
    // each element is also its own ancestor
    ancestors.add(oneEle.getTagName());
    Node directAncestor = oneEle.getParentNode();
    while (null != directAncestor && (directAncestor instanceof Element)) {
      ancestors.add(((Element)directAncestor).getTagName());
      directAncestor = directAncestor.getParentNode();
    }
    // add list to ancestors map
    this.getAncestorsMap().put(key, ancestors);
  }


  /**
   * This checks if <code>class1</code> is anestor in the class
   * hierarchy of <code>class2</code> or equals <code>class2</code>.
   *
   * @param class1 a <code>String</code> with a token class name
   * @param class2 a <code>String</code> with a token class name
   * @return a <code>boolen</code>
   * @exception ProcessingException if <code>class2</code> is not a
   * defined class */
  boolean isAncestor(String class1, String class2)
    throws ProcessingException {

    List ancestors = (List)this.getAncestorsMap().get(class2);
    if (null == ancestors) {
      throw new ProcessingException("undefined token class " + class2);
    }
    return ancestors.contains(class1);
  }


  /**
   * This returns the matcher for all punctuation from the punctuation
   * description.
   *
   * @return a {@link RegExp} */
  RegExp getAllPunctMatcher() {
    return (RegExp)this.getPunctDescr().getRulesMap()
      .get(PunctDescription.ALL_RULE);
  }

  /**
   * This returns the matcher for non-breaking right punctuation from
   * the punctuation description.
   *
   * @return a {@link RegExp} */
  RegExp getNbrMatcher() {
    return (RegExp)this.getPunctDescr().getRulesMap()
      .get(PunctDescription.NON_BREAK_RIGHT_RULE);
  }

  /**
   * This returns the matcher for non-breaking left punctuation from
   * the punctuation description.
   *
   * @return a {@link RegExp} */
  RegExp getNblMatcher() {
    return (RegExp)this.getPunctDescr().getRulesMap()
      .get(PunctDescription.NON_BREAK_LEFT_RULE);
  }


  /**
   * This returns the matcher for internal punctuation from the punctuation
   * description.
   *
   * @return a {@link RegExp} */
  RegExp getInternalMatcher() {
    return (RegExp)this.getPunctDescr().getRulesMap()
      .get(PunctDescription.INTERNAL_RULE);
  }


  /**
   * This returns the matcher for sentence internal punctuation from
   * the punctuation description.
   *
   * @return a {@link RegExp} */
  RegExp getInternalTuMatcher() {
    return (RegExp)this.getPunctDescr().getRulesMap()
      .get(PunctDescription.INTERNAL_TU_RULE);
  }


  /**
   * This returns the matcher for clitic punctuation from the punctuation
   * description.
   *
   * @return a {@link RegExp} */
  RegExp getCliticsMatcher() {
    return (RegExp)this.getPunctDescr().getRulesMap()
      .get(PunctDescription.CLITIC_RULE);
  }


  /**
   * This returns the matcher for proclitics from the clitics
   * description.
   *
   * @return a {@link RegExp} */
  RegExp getProcliticsMatcher() {
    return (RegExp)this.getClitDescr().getRulesMap()
      .get(CliticsDescription.PROCLITIC_RULE);
  }


  /**
   * This returns the matcher for enclitics from the clitics
   * description.
   *
   * @return a {@link RegExp} */
  RegExp getEncliticsMatcher() {
    return (RegExp)this.getClitDescr().getRulesMap()
      .get(CliticsDescription.ENCLITIC_RULE);
  }


  /**
   * This returns the hashmap with the abbreviation lists.
   *
   * @return a <code>HashMap</code> */
  HashMap getAbbrevLists() {
    return this.getAbbrevDescr().getListsMap();
  }


  /**
   * This returns the matcher for the abbreviations.
   *
   * @return a {@link RegExp} */
  RegExp getAbbrevMatcher() {
    return (RegExp)this.getAbbrevDescr().getRulesMap()
      .get(AbbrevDescription.ABBREV_RULE);
  }


  /**
   * This returns the matcher for the mid name initials.
   *
   * @return a {@link RegExp} */
  RegExp getInitialMatcher() {
    return (RegExp)this.getAbbrevDescr().getRulesMap()
    .get(AbbrevDescription.INITIAL_RULE);
  }


  /**
   * This returns the set of the most common terms that only start with a
   * capital letter when they are at the beginning of a sentence.
   *
   * @return a <code>Set</code> of <code>String</code>s with the terms
   */
  Set<String> getNonCapTerms() {
    return this.getAbbrevDescr().getNonCapTerms();
  }


  /**
   * This returns the matcher for simple digits.
   *
   * @return a {@link RegExp} */
  RegExp getSimpleDigitsMatcher() {
    return (RegExp)this.getNumbDescr().getRulesMap()
      .get(NumbersDescription.SIMPLE_DIGITS_RULE);
  }


  /**
   * This returns the matcher for ordinal numbers.
   *
   * @return a {@link RegExp} */
  RegExp getOrdinalMatcher() {
    return (RegExp)this.getNumbDescr().getRulesMap()
      .get(NumbersDescription.ORDINAL_RULE);
  }


  /**
   * This returns the matcher for the digits.
   *
   * @return a {@link RegExp} */
  RegExp getDigitsMatcher() {
    return (RegExp)this.getNumbDescr().getRulesMap()
      .get(NumbersDescription.DIGITS_RULE);
  }
}
