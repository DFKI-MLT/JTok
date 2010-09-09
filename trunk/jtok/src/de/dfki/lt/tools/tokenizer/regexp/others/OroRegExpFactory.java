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

package de.dfki.lt.tools.tokenizer.regexp.others;

import de.dfki.lt.tools.tokenizer.exceptions.InitializationException;
import de.dfki.lt.tools.tokenizer.regexp.RegExp;
import de.dfki.lt.tools.tokenizer.regexp.RegExpFactory;

/**
 * <code>OroRegExpFactory</code> extends {@link RegExpFactory} for
 * regular expressions of the org.apache.oro.text.regex package. 
 * 
 * @author Joerg Steffen, DFKI
 * @version $Id: OroRegExpFactory.java,v 1.4 2005-04-12 08:47:38 steffen Exp $ */

public class OroRegExpFactory 
  extends RegExpFactory {

  /**
   * This creates a new instance of <code>OroRegExpFactory</code>. */
  public OroRegExpFactory() {
    // nothing to do
  }


  /**
   * This creates a regular expression object from an input string.
   *
   * @param regExpString a <code>String</code> with a regular
   * expression
   * @return a {@link RegExp} build from the input string
   * @exception InitializationException if regular expression is not
   * well formed */
  public RegExp createRegExp(String regExpString) 
    throws InitializationException {
    
    return new OroRegExp(regExpString);
  }
}
