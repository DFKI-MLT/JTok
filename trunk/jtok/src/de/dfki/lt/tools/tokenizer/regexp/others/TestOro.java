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

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.PatternMatcherInput;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

/**
 * This is a test class to play with the org.apache.oro.text.regex
 * package. 
 *
 * @author Joerg Steffen, DFKI
 * @version $Id: TestOro.java,v 1.4 2005-04-12 08:47:38 steffen Exp $ */

public class TestOro {
  
  public static void main(String[] args) {
    
    try {
      PatternCompiler comp = new Perl5Compiler();
      PatternMatcher matcher = new Perl5Matcher();
      Pattern pat = comp.compile("abc|ab|a");
      PatternMatcherInput input = new PatternMatcherInput("abcabc");
      while (matcher.contains(input, pat)) {
        MatchResult res = matcher.getMatch();
        System.out.println(res.toString());
        System.out.println(res.beginOffset(0));
        System.out.println(res.endOffset(0));
      }

      System.out.println(matcher.matches("abc", pat));

    } catch (MalformedPatternException mpe) {
      System.err.println(mpe.toString());
    }
  }
}
