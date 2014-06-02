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

import java.util.HashMap;

import org.w3c.dom.Document;

import de.dfki.lt.tools.tokenizer.exceptions.InitializationException;
import de.dfki.lt.tools.tokenizer.regexp.RegExp;

/**
 * Manages the content of a clitics description file.
 *
 * @author Joerg Steffen, DFKI
 */
public class CliticsDescription
    extends Description {

  /**
   * Name of the proclitic rule.
   */
  protected static final String PROCLITIC_RULE =
    "PROCLITIC_RULE";

  /**
   * Name of the enclitic rule.
   */
  protected static final String ENCLITIC_RULE =
    "ENCLITIC_RULE";


  /**
   * Creates a new instance of {@link CliticsDescription} for the clitics
   * description contained in the given DOM document.
   *
   * @param clitDescr
   *          a DOM document with the clitics description
   * @exception InitializationException
   *              if an error occurs
   */
  public CliticsDescription(Document clitDescr) {

    super.setDefinitionsMap(new HashMap<String, RegExp>());
    super.setRulesMap(new HashMap<String, RegExp>());
    super.setRegExpMap(new HashMap<RegExp, String>());

    // build the classes matcher map
    super.loadDefinitions(clitDescr);
    // build the rules matcher map
    super.loadRules(clitDescr);
  }
}
