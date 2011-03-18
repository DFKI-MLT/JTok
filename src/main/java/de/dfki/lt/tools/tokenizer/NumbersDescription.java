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

import java.util.HashMap;
import java.util.Set;

import org.w3c.dom.Document;

import de.dfki.lt.tools.tokenizer.exceptions.InitializationException;

/**
 * <code>NumbersDescription</code> extends {@link Description}. It
 * manages the content of a numbers description file.
 *
 * @author Joerg Steffen, DFKI
 * @version $Id: NumbersDescription.java,v 1.5 2005-04-12 08:47:37 steffen Exp $ */

public class NumbersDescription
  extends Description {

  /**
   * This is the name of the digits probe rule. */
  protected static final String SIMPLE_DIGITS_RULE =
    "SIMPLE_DIGITS_RULE";

  /**
   * This is the name of the ordinal number rule. */
  protected static final String ORDINAL_RULE =
    "ORDINAL_RULE";

  /**
   * This is the name of the digits rule. */
  protected static final String DIGITS_RULE =
    "DIGITS_RULE";


  /**
   * This creates a new instance of <code>NumbersDescription</code>. Not
   * to be used outside this class. */
  private NumbersDescription() {
    super.setDefinitionsMap(new HashMap());
    super.setRulesMap(new HashMap());
    super.setRegExpMap(new HashMap());
  }


  /**
   * This creates a new instance of <code>NumbersDescription</code> for
   * the numbers description contained in the dom
   * <code>Document numbDescr</code>.
   *
   * @param numbDescr a dom <code>Document</code> with the
   * numbers description
   * @param classes a <code>Set</code> with the defined classes, used
   * for validation
   * @exception InitializationException if an error occurs */
  public NumbersDescription(Document numbDescr,
                            Set classes) {

    this();
    // build the classes matcher map
    super.loadDefinitions(numbDescr, classes);
    // build the rules matcher map
    super.loadRules(numbDescr);
  }
}
