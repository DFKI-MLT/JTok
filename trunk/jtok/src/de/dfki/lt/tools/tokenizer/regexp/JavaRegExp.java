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

package de.dfki.lt.tools.tokenizer.regexp;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import de.dfki.lt.tools.tokenizer.exceptions.InitializationException;

/**
 * <code>JavaRegExp</code> implements the {@link RegExp} interface for
 * regular expressions of the java.util.regex package.
 *
 * @author Joerg Steffen
 * @version $Id: JavaRegExp.java,v 1.4 2005-04-12 08:47:37 steffen Exp $ */

public class JavaRegExp implements RegExp {

  /**
   * This contains an instance of a regular expression in the
   * java.util.regex package. */
  private Pattern javaRE;


  /**
   * This creates a new instance of <code>JavaRegExp</code>. Not to be
   * used outside this class. */
  private JavaRegExp() {}


  /**
   * This creates a new instance of <code>JavaRegExp</code> for a
   * <code>String</code> containing a regular expression.
   *
   * @param regExpString a <code>String</code> with a regular expression
   * @exception InitializationException if regular expression is not
   * well formed */
  public JavaRegExp(String regExpString)
    throws InitializationException {
    try {
      this.setJavaRE(Pattern.compile(regExpString));
    } catch (PatternSyntaxException pse) {
      throw new InitializationException(pse.toString());
    }
  }


  /**
   * This returns the field {@link #javaRE}.
   *
   * @return a <code>Pattern</code> */
  private Pattern getJavaRE() {
    return this.javaRE;
  }

  /**
   * This sets the field {@link #javaRE} to
   * <code>aJavaRE</code>.
   *
   * @param aJavaRE a <code>Pattern</code> */
  private void setJavaRE(Pattern aJavaRE){
    this.javaRE = aJavaRE;
  }


  /**
   * This returns an array of all {@link Match}es for the regular
   * expression in <code>input</code>.
   *
   * @param input the <code>String</code> where to look for matches
   * @return a <code>List</code> of {@link Match}es */
  public List getAllMatches(String input) {

    // create Matcher for input
    Matcher javaMatch = this.getJavaRE().matcher(input);
    // convert matches to JavaMatches and collect them in a list
    List matches = new ArrayList();
    while (javaMatch.find()) {
      matches.add(new Match(javaMatch.start(),
                            javaMatch.end(),
                            javaMatch.group()));
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

    // create Matcher for input
    Matcher javaMatch = this.getJavaRE().matcher(input);
    return javaMatch.matches();
  }


  /**
   * This checks if the input contains a match for the regular
   * expression. If yes, a {@link Match} is returned,
   * <code>null</code> otherwise.
   *
   * @param input the <code>String</code> to check
   * @return a {@link Match} or <code>null</code> */
  public Match contains(String input) {

    // create Matcher for input
    Matcher javaMatch = this.getJavaRE().matcher(input);
    if (javaMatch.find()) {
      return new Match(javaMatch.start(),
                       javaMatch.end(),
                       javaMatch.group());
    }

    return null;
  }
}
