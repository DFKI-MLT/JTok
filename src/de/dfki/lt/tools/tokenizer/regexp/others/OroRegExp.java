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

import java.util.ArrayList;
import java.util.List;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.PatternMatcherInput;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

import de.dfki.lt.tools.tokenizer.exceptions.InitializationException;
import de.dfki.lt.tools.tokenizer.regexp.Match;
import de.dfki.lt.tools.tokenizer.regexp.RegExp;

/**
 * <code>OroRegExp</code> implements the {@link RegExp} interface for
 * regular expressions of the  org.apache.oro.text.regex package.
 *
 * @author Joerg Steffen
 * @version $Id: OroRegExp.java,v 1.4 2005-04-12 08:47:37 steffen Exp $ */

public class OroRegExp implements RegExp {

  /**
   * This contains the compiler for Perl5 regular expressions. */
  private static PatternCompiler comp = new Perl5Compiler();

  /**
   * This contains the matcher for Perl5 patterns. */
  private static PatternMatcher matcher = new Perl5Matcher();


  /**
   * This contains an instance of a regular expression in the
   *  org.apache.oro.text.regex package. */
  private Pattern oroPattern;


  /**
   * This creates a new instance of <code>OroRegExp</code>. Not to be
   * used outside this class. */
  private OroRegExp() {}

  
  /**
   * This creates a new instance of <code>OroRegExp</code> for a
   * <code>String</code> containing a regular expression.
   *
   * @param regExpString a <code>String</code> with a regular expression
   * @exception InitializationException if regular expression is not
   * well formed */
  public OroRegExp(String regExpString) 
    throws InitializationException {
    try {
      this.setOroPattern(comp.compile(regExpString));
    } catch (MalformedPatternException mpe) {
      throw new InitializationException(mpe.toString());
    }
  }

  
  /**
   * This returns the field {@link #oroPattern}.
   *
   * @return a <code>Pattern</code> */
  private Pattern getOroPattern() {
    return this.oroPattern;
  }
  
  /**
   * This sets the field {@link #oroPattern} to
   * <code>anOroPattern</code>.
   *
   * @param anOroPattern a <code>Pattern</code> */
  private void setOroPattern(Pattern anOroPattern) {
    this.oroPattern = anOroPattern;
  }
  

  /**
   * This returns an array of all {@link Match}es for the regular
   * expression in <code>input</code>. 
   *
   * @param input the <code>String</code> where to look for matches 
   * @return a <code>List</code> of {@link Match}es */
  public List getAllMatches(String input) {
    
    PatternMatcherInput maInput = new PatternMatcherInput(input);
    // convert matches to OroMatches and collect them in a list
    List matches = new ArrayList();
    while(matcher.contains(maInput, this.getOroPattern())) {
      MatchResult oneMatch = matcher.getMatch();
      matches.add(new Match(oneMatch.beginOffset(0),
                            oneMatch.endOffset(0),
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
    Match aMatch = this.contains(input);
    if (null == aMatch) {
      return false;
    }
    
    return (
        aMatch.getEndIndex() - aMatch.getStartIndex() == input.length());
  }

  
  /**
   * This checks if the input contains a match for the regular
   * expression. If yes, a {@link Match} is returned,
   * <code>null</code> otherwise.
   * 
   * @param input the <code>String</code> to check
   * @return a {@link Match} or <code>null</code> */
  public Match contains(String input) { 
    
    if (matcher.contains(input, this.getOroPattern())) {
      MatchResult oneMatch = matcher.getMatch();
      return new Match(oneMatch.beginOffset(0),
                       oneMatch.endOffset(0),
                       oneMatch.toString());
    }
    
    return null;
  }
}
