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

package de.dfki.lt.tools.tokenizer.regexp;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import de.dfki.lt.tools.tokenizer.exceptions.InitializationException;

/**
 * Implements the {@link RegExp} interface for regular expressions of the
 * java.util.regex package.
 *
 * @author Joerg Steffen
 */
public class JavaRegExp implements RegExp {

  /**
   * Contains an instance of a regular expression in the java.util.regex
   * package.
   */
  private Pattern javaRE;


  /**
   * Creates a new instance of {@link JavaRegExp} for the given regular
   * expression string.
   *
   * @param regExpString
   *          a regular expression string
   * @exception InitializationException
   *              if regular expression is not well formed
   */
  public JavaRegExp(String regExpString)
      throws InitializationException {

    try {
      this.javaRE = Pattern.compile(regExpString);
    } catch (PatternSyntaxException pse) {
      throw new InitializationException(pse.getLocalizedMessage(), pse);
    }
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public List<Match> getAllMatches(String input) {

    // create Matcher for input
    Matcher javaMatch = this.javaRE.matcher(input);
    // convert matches to JavaMatches and collect them in a list
    List<Match> matches = new ArrayList<>();
    while (javaMatch.find()) {
      matches.add(new Match(javaMatch.start(),
        javaMatch.end(),
        javaMatch.group()));
    }
    // return result
    return matches;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean matches(String input) {

    // create Matcher for input
    Matcher javaMatch = this.javaRE.matcher(input);
    return javaMatch.matches();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Match contains(String input) {

    // create Matcher for input
    Matcher javaMatch = this.javaRE.matcher(input);
    if (javaMatch.find()) {
      return new Match(javaMatch.start(),
        javaMatch.end(),
        javaMatch.group());
    }

    return null;
  }
}
