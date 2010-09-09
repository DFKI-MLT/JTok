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
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.Document;

import de.dfki.lt.tools.tokenizer.exceptions.InitializationException;

/**
 * <code>AbbrevDescription</code> extends {@link Description}. It
 * manages the content of a abbreviation description file.
 *
 * @author Joerg Steffen, DFKI
 * @version $Id: AbbrevDescription.java,v 1.6 2010-04-30 09:26:46 steffen Exp $ */

public class AbbrevDescription
  extends Description {

  /**
   * This is the name of the abbreviation rule. */
  protected static final String ABBREV_RULE =
    "ABBREV_RULE";

  /**
   * This is the name of the mid name initial rule. */
  protected static final String INITIAL_RULE =
    "INITIAL_RULE";

  /**
   * This contains the most common terms that only start with a capital letter
   * when they are at the beginning of a sentence.
   */
  private Set<String> nonCapTerms;

  /**
   * This creates a new instance of <code>AbbrevDescription</code>. Not
   * to be used outside this class. */
  private AbbrevDescription() {
    super.setDefinitionsMap(new HashMap());
    super.setRulesMap(new HashMap());
    super.setRegExpMap(new HashMap());
    super.setListsMap(new HashMap());
  }


  /**
   * This creates a new instance of <code>AbbrevDescription</code> for
   * the abbreviation description contained in the dom
   * <code>Document abbrDescr</code>.
   *
   * @param abbrDescr a dom <code>Document</code> with the
   * abbreviation description
   * @param classes a <code>Set</code> with the defined classes, used
   * for validation
   * @param aResourceDir a <code>String</code> with the name of the
   * resource directory
   * @exception InitializationException if an error occurs */
  public AbbrevDescription(Document abbrDescr,
                           Set classes,
                           String aResourceDir) {

    this();
    // build the lists map
    super.loadLists(abbrDescr, classes, aResourceDir);
    // build the classes matcher map
    super.loadDefinitions(abbrDescr, classes);
    // build the rules matcher map
    super.loadRules(abbrDescr);

    // load list of terms that only start with a capital letter when they are
    // at the beginning of a sentence.
    try {
      BufferedReader in =
        new BufferedReader(
            new InputStreamReader(
                FileTools.openResourceFileAsStream(
                    aResourceDir + "/nonCapTerms.txt"),
                "utf-8"));
      // init set where to store the terms
      this.nonCapTerms = new HashSet<String>();

      String str;
      while ((str = in.readLine()) != null) {
        str = str.trim();
        if (str.startsWith("#")) {
          continue;
        }
        // extract the term and add it to the set
        int end = str.indexOf('#');
        if (-1 != end) {
          str = str.substring(0, end);
        }
        str = str.trim();
        if (str.length() == 0) {
          continue;
        }
        // convert first letter to upper case to make runtime comparison more
        // efficient
        char firstChar = str.charAt(0);
        firstChar = Character.toUpperCase(firstChar);
        this.nonCapTerms.add(firstChar + str.substring(1));
        // also add a version completely in upper case letters
        this.nonCapTerms.add(str.toUpperCase());
      }

      in.close();
    } catch (IOException ioe) {
      throw new InitializationException(ioe.toString());
    }
  }


  /**
   * This returns the set of the most common terms that only start with a
   * capital letter when they are at the beginning of a sentence.
   *
   * @return a <code>Set</code> of <code>String</code>s with the terms
   */
  protected Set<String> getNonCapTerms() {

    return this.nonCapTerms;
  }
}
