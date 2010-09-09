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
 * <code>CliticsDescription</code> extends {@link Description}. It
 * manages the content of a clitics description file.
 *
 * @author Joerg Steffen, DFKI
 * @version $Id: CliticsDescription.java,v 1.5 2005-04-12 08:47:37 steffen Exp $ */

public class CliticsDescription 
  extends Description {

  /**
   * This is the name of the proclitic rule. */
  protected static final String PROCLITIC_RULE = 
    "PROCLITIC_RULE";

  /**
   * This is the name of the enclitic rule. */
  protected static final String ENCLITIC_RULE = 
    "ENCLITIC_RULE";


  /**
   * This creates a new instance of <code>CliticsDescription</code>. Not
   * to be used outside this class. */
  private CliticsDescription() {
    super.setDefinitionsMap(new HashMap());
    super.setRulesMap(new HashMap());
    super.setRegExpMap(new HashMap());
  }
  

  /**
   * This creates a new instance of <code>CliticsDescription</code> for
   * the clitics description contained in the dom
   * <code>Document clitDescr</code>.
   *
   * @param clitDescr a dom <code>Document</code> with the
   * clitics description 
   * @param classes a <code>Set</code> with the defined classes, used
   * for validation   
   * @exception InitializationException if an error occurs */
  public CliticsDescription(Document clitDescr,
                            Set classes) {
    
    this();
    // build the classes matcher map
    super.loadDefinitions(clitDescr, classes);
    // build the rules matcher map
    super.loadRules(clitDescr);
  }
}
