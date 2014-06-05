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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
   * Class name for breaking abbreviation.
   */
  public static final String B_ABBREVIATION = "B_ABBREVIATION";

  /**
   * The name of the all abbreviation rule.
   */
  protected static final String ALL_RULE = "ALL_RULE";

  /**
   * Contains the name suffix of the resource file with the abbreviations
   * description.
   */
  private static final String ABBREV_DESCR = "_abbrev.cfg";

  /**
   * Contains the most common terms that only start with a capital letter when
   * they are at the beginning of a sentence.
   */
  private Set<String> nonCapTerms;


  /**
   * Creates a new instance of {@link AbbrevDescription} for the given language.
   *
   * @param resourceDir
   *          path to the folder with the language resources
   * @param lang
   *          the language
   * @param macrosMap
   *          a map of macro names to regular expression strings
   * @throws IOException
   *           if there is an error when reading the configuration
   */
  public AbbrevDescription(
      String resourceDir, String lang, Map<String, String> macrosMap)
      throws IOException {

    super.setDefinitionsMap(new HashMap<String, RegExp>());
    super.setRulesMap(new HashMap<String, RegExp>());
    super.setRegExpMap(new HashMap<RegExp, String>());
    super.setClassMembersMap(new HashMap<String, Set<String>>());

    Path commonDescrPath =
        Paths.get("jtok").resolve(COMMON)
          .resolve(COMMON + ABBREV_DESCR);
    Path abbrDescrPath =
      Paths.get(resourceDir).resolve(lang + ABBREV_DESCR);

    // open both the common config file and the language specific one
    BufferedReader commonIn = null;
    BufferedReader langIn = null;
    try {
      commonIn = new BufferedReader(
        new InputStreamReader(
          FileTools.openResourceFileAsStream(commonDescrPath.toString()),
          "utf-8"));
    } catch (FileNotFoundException fne) {
      // do nothing, commonIn is still null
    }
    try {
      langIn = new BufferedReader(
        new InputStreamReader(
          FileTools.openResourceFileAsStream(abbrDescrPath.toString()),
          "utf-8"));
    } catch (FileNotFoundException fne) {
      // do nothing, langIn is still null
    }

    // at least one configuration must be found
    if (null == commonIn && null == langIn) {
      throw new InitializationException(
        String.format(
          "missing abbreviation description for language %s", lang));
    }

    // read both config files to lists start
    readToLists(commonIn);
    readToLists(langIn);

    // read lists
    super.loadLists(commonIn, Paths.get("jtok").resolve(COMMON).toString());
    super.loadLists(langIn, resourceDir);

    // read definitions
    Map<String, String> defsMap = new HashMap<>();
    super.loadDefinitions(commonIn, macrosMap, defsMap);
    super.loadDefinitions(langIn, macrosMap, defsMap);

    getRulesMap().put(ALL_RULE, createAllRule(defsMap));

    if (null != commonIn) {
      commonIn.close();
    }
    if (null != langIn) {
      langIn.close();
    }

    // load list of terms that only start with a capital letter when they are
    // at the beginning of a sentence
    Path commonNonCapTerms =
      Paths.get(resourceDir).resolve("nonCapTerms.txt");
    Path langNonCapTerms =
      Paths.get("jtok").resolve(COMMON).resolve("nonCapTerms.txt");
    commonIn = null;
    langIn = null;
    try {
      commonIn =
        new BufferedReader(
          new InputStreamReader(
            FileTools.openResourceFileAsStream(commonNonCapTerms.toString()),
              "utf-8"));
    } catch (FileNotFoundException fne) {
      // do nothing, commonIn is still null
    }
    try {
      langIn =
        new BufferedReader(
          new InputStreamReader(
            FileTools.openResourceFileAsStream(langNonCapTerms.toString()),
              "utf-8"));
    } catch (FileNotFoundException fne) {
      // do nothing, langIn is still null
    }

    // at least one configuration must be found
    if (null == commonIn && null == langIn) {
      throw new InitializationException(
        String.format(
          "missing non-capital term list in abbreviation description of "
          + "language %s", lang));
    }

    readNonCapTerms(commonIn);
    readNonCapTerms(langIn);

    if (null != commonIn) {
      commonIn.close();
    }
    if (null != langIn) {
      langIn.close();
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
   * Reads the list of terms that only start with a capital letter when they are
   * at the beginning of a sentence from the given reader.<br>
   * Immediately returns if the reader is {@code null}.
   *
   * @param in
   *          the reader
   * @throws IOException
   *           if there is an error when reading
   */
  private void readNonCapTerms(BufferedReader in)
      throws IOException {

    if (null == in) {
      return;
    }

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
  }
}
