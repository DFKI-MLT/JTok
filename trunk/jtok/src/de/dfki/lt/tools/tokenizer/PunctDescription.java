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
 * <code>PunctDescription</code> extends {@link Description}. It
 * manages the content of a punctuation description file.
 *
 * @author Joerg Steffen, DFKI
 * @version $Id: PunctDescription.java,v 1.7 2010-08-18 13:20:39 steffen Exp $ */

public class PunctDescription
  extends Description {

  /**
   * This is the name of the all punctuation rule. */
  protected static final String ALL_RULE =
    "ALL_PUNCT_RULE";

  /**
   * This is the name of the clitic punctuation rule. */
  protected static final String CLITIC_RULE =
    "CLITIC_PUNCT_RULE";

  /**
   * This is the name of the internal punctuation rule. */
  protected static final String INTERNAL_RULE =
    "INTERNAL_PUNCT_RULE";

  /**
   * This is the name of the non-breaking left punctuation rule. */
  protected static final String NON_BREAK_LEFT_RULE =
    "NON_BREAKING_LEFT_PUNCT_RULE";

  /**
   * This is the name of the non-breaking right punctuation rule. */
  protected static final String NON_BREAK_RIGHT_RULE =
    "NON_BREAKING_RIGHT_PUNCT_RULE";

  /**
   * This is the name of the sentence internal punctuation rule. */
  protected static final String INTERNAL_TU_RULE =
    "INTERNAL_TU_PUNCT_RULE";

  /**
   * This is the class name for ambiguous open/close punctuation. */
  protected static final String OPEN_CLOSE_PUNCT =
    "OPENCLOSE_PUNCT";

  /**
   * This is the class name for opening punctuation. */
  public static final String OPEN_PUNCT =
    "OPEN_PUNCT";

  /**
   * This is the class name for closing punctuation. */
  public static final String CLOSE_PUNCT =
    "CLOSE_PUNCT";

  /**
   * This is the class name for opening brackets. */
  public static final String OPEN_BRACKET =
    "OPEN_BRACKET";

  /**
   * This is the class name for closing brackets. */
  public static final String CLOSE_BRACKET =
    "CLOSE_BRACKET";


  /**
   * This creates a new instance of <code>PunctDescription</code>. Not
   * to be used outside this class. */
  private PunctDescription() {
    super.setDefinitionsMap(new HashMap());
    super.setRulesMap(new HashMap());
    super.setRegExpMap(new HashMap());
  }


  /**
   * This creates a new instance of <code>PunctDescription</code> for
   * the punctuation description contained in the dom
   * <code>Document punctDescr</code>.
   *
   * @param punctDescr a dom <code>Document</code> with the
   * punctuation description
   * @param classes a <code>Set</code> with the defined classes, used
   * for validation
   * @exception InitializationException if an error occurs */
  public PunctDescription(Document punctDescr,
                          Set classes) {

    this();
    // build the classes matcher map
    super.loadDefinitions(punctDescr, classes);
    // build the rules matcher map
    super.loadRules(punctDescr);
  }
}
