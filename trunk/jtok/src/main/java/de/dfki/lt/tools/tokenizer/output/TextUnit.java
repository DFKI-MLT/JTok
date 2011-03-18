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

package de.dfki.lt.tools.tokenizer.output;

import java.util.ArrayList;
import java.util.List;

/**
 * This represents a text unit with its tokens.
 *
 * @author Joerg Steffen, DFKI
 * @version $Id: TextUnit.java,v 1.5 2010-06-07 13:41:28 steffen Exp $ */

public class TextUnit {

  /**
   * This contains the start index of the text unit. */
  private int startIndex;

  /**
   * This contains the end index of the text unit. */
  private int endIndex;

  /**
   * This contains a <code>List</code> with the {@link Token}s of the
   * text unit. */
  private List<Token> tokens;


  /**
   * This creates a new instance of <code>TextUnit</code>. */
  public TextUnit() {
    this.setStartIndex(0);
    this.setEndIndex(0);
    this.setTokens(new ArrayList<Token>());
  }


  /**
   * This creates a new instance of <code>TextUnit</code> that
   * contains the given tokens.
   *
   * @param someTokens a <code>List</code> with {@link Token}s */
  public TextUnit(List<Token> someTokens) {
    this.setTokens(someTokens);
  }


  /**
   * This returns the start index of the text unit.
   *
   * @return an <code>int</code> */
  public int getStartIndex() {
    return this.startIndex;
  }

  /**
   * This sets the start index of the text unit to
   * <code>aStartIndex</code>.
   *
   * @param aStartIndex an <code>int</code> */
  public void setStartIndex(int aStartIndex) {
    this.startIndex = aStartIndex;
  }


  /**
   * This returns the end index of the text unit.
   *
   * @return an <code>int</code> */
  public int getEndIndex() {
    return this.endIndex;
  }

  /**
   * This sets the end index of the text unit to
   * <code>anEndIndex</code>.
   *
   * @param anEndIndex an <code>int</code> */
  public void setEndIndex(int anEndIndex) {
    this.endIndex = anEndIndex;
  }


  /**
   * This returns the list with the tokens of the text unit.
   *
   * @return a <code>List</code> */
  public List<Token> getTokens() {
    return this.tokens;
  }

  /**
   * This sets the tokens of the text unit to
   * <code>someTokens</code>. As a side effect, it adjusts the start
   * index and end index of the text unit to the start index of the
   * first token and the end index of the last token.
   *
   * @param someTokens a <code>List</code> */
  public void setTokens(List<Token> someTokens) {
    this.tokens = someTokens;
    if (someTokens.size() > 0) {
      this.setStartIndex
        (someTokens.get(0).getStartIndex());
      this.setEndIndex
        (someTokens.get(someTokens.size() - 1).getEndIndex());
    }
    else {
      this.setStartIndex(0);
      this.setEndIndex(0);
    }
  }


  /**
   * This returns a string representation of the text unit.
   *
   * @return a <code>String</code> */
  public String toString() {

    StringBuffer result = new StringBuffer();
    String newline = System.getProperty("line.separator");

    result.append("  Text Unit Start: ")
      .append(this.getStartIndex())
      .append(newline)
      .append("  Text Unit End: ")
      .append(this.getEndIndex())
      .append(newline);

    // add tokens
    for (Token oneToken : this.getTokens()) {
      result.append(oneToken.toString());
    }

    return result.toString();
  }
}

