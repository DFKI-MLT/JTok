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

import de.dfki.lt.tools.tokenizer.PunctDescription;

/**
 * This represents a token with its type and surface image.
 *
 * @author Joerg Steffen, DFKI
 * @version $Id: Token.java,v 1.8 2010-08-31 09:56:51 steffen Exp $ */

public class Token {

  /**
   * These contain the Penn Treebank replacements for brackets
   */
  private static final String LRB = "-LRB-";
  private static final String RRB = "-RRB-";
  private static final String LSB = "-LSB-";
  private static final String RSB = "-RSB-";
  private static final String LCB = "-LCB-";
  private static final String RCB = "-RCB-";

  /**
   * This contains the start index of the token. */
  private int startIndex;

  /**
   * This contains the end index of the token. */
  private int endIndex;

  /**
   * This contains the type of the token. */
  private String type;

  /**
   * This contains the surface image of the token. */
  private String image;


  /**
   * This creates a new instance of <code>Token</code>. */
  public Token() {
    this.setStartIndex(0);
    this.setEndIndex(0);
    this.setType(new String());
    this.setImage(new String());
  }


  /**
   * This creates a new instance of <code>Token</code> with the given
   * start index, end index, type and surface image of the token.
   *
   * @param aStartIndex a <code>int</code> with the start index
   * @param anEndIndex a <code>int</code> with the end index
   * @param aType a <code>String</code> with the type
   * @param anImage a <code>String</code> with the surface image */
  public Token(int aStartIndex,
               int anEndIndex,
               String aType,
               String anImage) {
    this.setStartIndex(aStartIndex);
    this.setEndIndex(anEndIndex);
    this.setType(aType);
    this.setImage(anImage);
  }


  /**
   * This returns the start index of the token.
   *
   * @return an <code>int</code> */
  public int getStartIndex() {
    return this.startIndex;
  }

  /**
   * This sets the start index of the token to
   * <code>aStartIndex</code>.
   *
   * @param aStartIndex an <code>int</code> */
  public void setStartIndex(int aStartIndex) {
    this.startIndex = aStartIndex;
  }


  /**
   * This returns the end index of the token.
   *
   * @return an <code>int</code> */
  public int getEndIndex() {
    return this.endIndex;
  }

  /**
   * This sets the end index of the token to
   * <code>anEndIndex</code>.
   *
   * @param anEndIndex an <code>int</code> */
  public void setEndIndex(int anEndIndex) {
    this.endIndex = anEndIndex;
  }


  /**
   * This returns the type of the token.
   *
   * @return a <code>String</code> */
  public String getType() {
    return this.type;
  }

  /**
   * This sets the type of the token to <code>aType</code>.
   *
   * @param aType a <code>String</code> */
  public void setType(String aType) {
    this.type = aType;
  }


  /**
   * This returns the surface image of the token.
   *
   * @return a <code>String</code> */
  public String getImage() {

    return this.image;
  }


  /**
   * This sets the surface image of the token to
   * <code>anImage</code>.
   *
   * @param anImage a <code>String</code> */
  public void setImage(String anImage) {
    this.image = anImage;
  }


  /**
   * This returns the Penn Treebank surface image of the token if a Penn
   * Treebank replacement took place, <code>null</code> otherwise.
   *
   * @return a <code>String</code> with the surface image as the
   * result of the Penn Treebank token replacement or <code>null</code>
   */
  public String getPtbImage() {

    return applyPtbFormat(this.image, this.type);
  }


  /**
   * This returns a string representation of the token.
   *
   * @return a <code>String</code> */
  public String toString() {

    StringBuffer result = new StringBuffer();
    String newline = System.getProperty("line.separator");

    result.append("    Token: ")
      .append(filledStringLeft(this.getImage(), 15))
      .append("\tType: ")
      .append(this.getType())
      .append("\tStart: ")
      .append(this.getStartIndex())
      .append("\tEnd: ")
      .append(this.getEndIndex());
    String ptbImage = applyPtbFormat(this.image, this.type);
    if (null != ptbImage) {
      result.append("\tPTB: \"").append(ptbImage).append("\"");
    }
    result.append(newline);

    return result.toString();
  }


  /**
   * This returns a <code>String</code> of the given length that
   * starts with the given string. If that string is shorter the rest
   * id filled with blanks.
   *
   * @param aString a <code>String</code>
   * @param aLength a <code>int</code> with the length
   * @return a <code>String</code> */
  private static String filledStringLeft(String aString,
                                         int aLength) {
    StringBuffer result = new StringBuffer("\"");
    result.append(aString)
      .append("\"");
    while (result.length() < aLength) {
      result.append(" ".intern());
    }
    return result.toString();
  }


  /**
   * This applies some replacements used in the Penn Treebank format to the
   * given token image of the given type.
   *
   * @param image a <code>String</code>
   * @param type a <code>String</code>
   * @return a modified <code>String</code> or <code>null,/code> if no
   * replacement took place
   */
  public static String applyPtbFormat(String image, String type) {

    String result = null;

    if (type.equals(PunctDescription.OPEN_BRACKET)) {

      if (image.equals("(")) {
        result = LRB;
      }
      else if (image.equals("[")) {
        result = LSB;
      }
      else if (image.equals("{")) {
        result = LCB;
      }
    }

    else if (type.equals(PunctDescription.CLOSE_BRACKET)) {

      if (image.equals(")")) {
        result = RRB;
      }
      else if (image.equals("]")) {
        result = RSB;
      }
      else if (image.equals("}")) {
        result = RCB;
      }
    }

    else if (type.equals(PunctDescription.OPEN_PUNCT)) {
      result = "``";
    }

    else if (type.equals(PunctDescription.CLOSE_PUNCT)) {
      result = "''";
    }

    else if (image.contains("/")) {
      result = image.replace("/", "\\/");
    }
    else if (image.contains("*")) {
      result = image.replace("*", "\\*");
    }

    return result;
  }
}

