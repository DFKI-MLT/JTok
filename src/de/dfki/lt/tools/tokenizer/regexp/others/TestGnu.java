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

/**
 * This is a test class to play with the gnu.regexp package. 
 *
 * @author Joerg Steffen, DFKI
 * @version $Id: TestGnu.java,v 1.4 2005-04-12 08:47:38 steffen Exp $ */

public class TestGnu {
  
  public static void main(String[] args) {
    
    try {
      RE re = new RE("a|ab|abc");
      REMatch[] matches = re.getAllMatches("abcabc");
      for (int i = 0; i < matches.length; i++) {
        System.out.println(matches[i].getStartIndex());
        System.out.println(matches[i].getEndIndex());
        System.out.println(matches[i].toString());
      }
      System.out.println(re.isMatch("abc"));
    } catch (REException ree) {
      System.err.println(ree.toString());
    }
  }
}
