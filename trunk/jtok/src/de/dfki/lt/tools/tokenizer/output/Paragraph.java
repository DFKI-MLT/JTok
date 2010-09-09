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
 * This represents a paragraph with its text units.
 *
 * @author Joerg Steffen, DFKI
 * @version $Id: Paragraph.java,v 1.5 2010-06-07 13:41:28 steffen Exp $ */

public class Paragraph {

  /**
   * This contains the start index of the paragraph. */
  private int startIndex;

  /**
   * This contains the end index of the paragraph. */
  private int endIndex;

  /**
   * This contains a <code>List</code> with the {@link TextUnit}s of
   * the paragraph. */
  private List<TextUnit> textUnits;


  /**
   * This creates a new instance of <code>Paragraph</code>. */
  public Paragraph() {
    this.setStartIndex(0);
    this.setEndIndex(0);
    this.setTextUnits(new ArrayList<TextUnit>());
  }


  /**
   * This creates a new instance of <code>Paragraph</code> that
   * contains the given text units.
   *
   * @param someTextUnits a <code>List</code> with {@link TextUnit}s */
  public Paragraph(List<TextUnit> someTextUnits) {
    this.setTextUnits(someTextUnits);
  }


  /**
   * This returns the start index of the paragraph.
   *
   * @return an <code>int</code> */
  public int getStartIndex() {
    return this.startIndex;
  }

  /**
   * This sets the start index of the paragraph to
   * <code>aStartIndex</code>.
   *
   * @param aStartIndex an <code>int</code> */
  public void setStartIndex(int aStartIndex) {
    this.startIndex = aStartIndex;
  }


  /**
   * This returns the end index of the paragraph.
   *
   * @return an <code>int</code> */
  public int getEndIndex() {
    return this.endIndex;
  }

  /**
   * This sets the end index of the paragraph to
   * <code>anEndIndex</code>.
   *
   * @param anEndIndex an <code>int</code> */
  public void setEndIndex(int anEndIndex) {
    this.endIndex = anEndIndex;
  }


  /**
   * This returns the list with the text units of the paragraph.
   *
   * @return a <code>List</code> */
  public List<TextUnit> getTextUnits() {
    return this.textUnits;
  }

  /**
   * This sets the text units of the paragraph to
   * <code>someTextUnits</code>. As a side effect, it adjusts the start
   * index and end index of the paragraph to the start index of the
   * first text unit and the end index of the last text unit.
   *
   * @param someTextUnits a <code>List</code> */
  public void setTextUnits(List<TextUnit> someTextUnits) {
    this.textUnits = someTextUnits;
    if (someTextUnits.size() > 0) {
      this.setStartIndex
        (someTextUnits.get(0).getStartIndex());
      this.setEndIndex
        (someTextUnits.get(someTextUnits.size() - 1)
         .getEndIndex());
    }
    else {
      this.setStartIndex(0);
      this.setEndIndex(0);
    }
  }


  /**
   * This returns a string representation of the paragraph.
   *
   * @return a <code>String</code> */
  public String toString() {

    StringBuffer result = new StringBuffer();
    String newline = System.getProperty("line.separator");

    result.append("Paragraph Start: ")
      .append(this.getStartIndex())
      .append(newline)
      .append("Paragraph End: ")
      .append(this.getEndIndex())
      .append(newline);

    // add text units
    for (TextUnit oneTU : this.getTextUnits()) {
      result.append(oneTU.toString());
    }

    return result.toString();
  }
}

