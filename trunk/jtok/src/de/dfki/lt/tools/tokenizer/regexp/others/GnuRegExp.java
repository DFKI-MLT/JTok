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

import gnu.regexp.RE;
import gnu.regexp.REException;
import gnu.regexp.REMatch;

import java.util.ArrayList;
import java.util.List;

import de.dfki.lt.tools.tokenizer.exceptions.InitializationException;
import de.dfki.lt.tools.tokenizer.regexp.Match;
import de.dfki.lt.tools.tokenizer.regexp.RegExp;

/**
 * <code>GnuRegExp</code> implements the {@link RegExp} interface for
 * regular expressions of the gnu.regexp package.
 *
 * @author Joerg Steffen
 * @version $Id: GnuRegExp.java,v 1.4 2005-04-12 08:47:38 steffen Exp $ */

public class GnuRegExp implements RegExp {

  /**
   * This contains an instance of a regular expression in the
   * gnu.regexp package. */
  private RE gnuRE;


  /**
   * This creates a new instance of <code>GnuRegExp</code>. Not to be
   * used outside this class. */
  private GnuRegExp() {}

  
  /**
   * This creates a new instance of <code>GnuRegExp</code> for a
   * <code>String</code> containing a regular expression.
   *
   * @param regExpString a <code>String</code> with a regular expression
   * @exception InitializationException if regular expression is not
   * well formed */
  public GnuRegExp(String regExpString) 
    throws InitializationException {
    try {
      this.setGnuRE(new RE(regExpString));
    } catch (REException ree) {
      throw new InitializationException(ree.toString());
    }
  }

  
  /**
   * This returns the field {@link #gnuRE}.
   *
   * @return a <code>RE</code> */
  private RE getGnuRE() {
    return this.gnuRE;
  }
  
  /**
   * This sets the field {@link #gnuRE} to
   * <code>aGnuRE</code>.
   *
   * @param aGnuRE a <code>RE</code> */
  private void setGnuRE(RE aGnuRE){
    this.gnuRE = aGnuRE;
  }
  

  /**
   * This returns an array of all {@link Match}es for the regular
   * expression in <code>input</code>. 
   *
   * @param input the <code>String</code> where to look for matches 
   * @return a <code>List</code> of {@link Match}es */
  public List getAllMatches(String input) {
    
    // get array of REMatches
    REMatch[] reMatches = this.getGnuRE().getAllMatches(input);
    // convert REMatches to GnuMatches and collect them in a list
    List matches = new ArrayList(reMatches.length);
    for (int i = 0; i < reMatches.length; i++) {
      REMatch oneMatch = reMatches[i];
      matches.add(new Match(oneMatch.getStartIndex(),
                            oneMatch.getEndIndex(),
                            oneMatch.toString()));
    }
    //return result
    return matches;
  }

  
  /**
   * This checks if the regular expression matches the input in its
   * entirety.  
   * 
   * @param input the <code>String</code> to check
   * @return a <code>boolean</code> */
  public boolean matches(String input) {
    return this.getGnuRE().isMatch(input);
  }


  /**
   * This checks if the input contains a match for the regular
   * expression. If yes, a {@link Match} is returned,
   * <code>null</code> otherwise.
   * 
   * @param input the <code>String</code> to check
   * @return a {@link Match} or <code>null</code> */
  public Match contains(String input) { 
    REMatch oneMatch = this.getGnuRE().getMatch(input);
    if (null != oneMatch) {
      return new Match(oneMatch.getStartIndex(),
                       oneMatch.getEndIndex(),
                       oneMatch.toString());
    }
    
    return null;
  }
}
