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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * This is a test class to play with the java.util.regex package. 
 *
 * @author Joerg Steffen, DFKI
 * @version $Id: TestJava.java,v 1.4 2005-04-12 08:47:38 steffen Exp $ */

public class TestJava {
  
  public static void main(String[] args) {
    
    try {
      Pattern pat = Pattern.compile("a|ab|abc");
      Matcher ma = pat.matcher("abcabc");
      while (ma.find()) {
        System.out.println(ma.start());
        System.out.println(ma.end());
        System.out.println(ma.group());
      }

      ma = pat.matcher("abc");
      System.out.println(ma.matches());
    } catch (PatternSyntaxException pse) {
      System.err.println(pse.toString());
    }
  }
}
