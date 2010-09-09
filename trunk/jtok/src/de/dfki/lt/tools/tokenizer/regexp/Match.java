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

/**
 * <code>Match</code> holds the result of matching an input string
 * with a regular expression.
 *
 * @author Joerg Steffen, DFKI
 * @version $Id: Match.java,v 1.3 2005-04-12 08:47:37 steffen Exp $ */

public class Match {
  
  /**
   * This contains the index within the input text where the match in
   * its entirety began. */
  private int startIndex;

  /**
   * This contains the index within the input string where the match
   * in its entirety ends. The return value is the next position after
   * the end of the string.  */
  private int endIndex;
  

  /**
   * This contains the <code>String</code> matching the regular
   * expression pattern. */
  private String image;


  /**
   * This creates a new instance of <code>Match</code>. Not to be used
   * outside this class. */
  private Match() {}


  /**
   * This creates a new instance of <code>Match</code> using the given
   * parameters. 
   *
   * @param aStart an <code>int</code> with the start index
   * @param anEnd an <code>int</code> with the end index
   * @param anImage a <code>String</code> with the match */
  public Match(int aStart, int anEnd, String anImage) {
    this.setStartIndex(aStart);
    this.setEndIndex(anEnd);
    this.setImage(anImage);
  }


  /**
   * This returns the index within the input text where the match in
   * its entirety began.
   *
   * @return a <code>int</code> */
  public int getStartIndex() {
    return this.startIndex;
  }
  
  /**
   * This sets the field {@link #startIndex} to
   * <code>aStartIndex</code>.
   *
   * @param aStartIndex a <code>int</code> */
  private void setStartIndex(int aStartIndex){
    this.startIndex = aStartIndex;
  }
  

  /**
   * This returns the index within the input string where the match in
   * its entirety ends. The return value is the next position after
   * the end of the string.
   *
   * @return an <code>int</code> with the index */
  public int getEndIndex() {
    return this.endIndex;
  }
  
  /**
   * This sets the field {@link #endIndex} to
   * <code>aEndIndex</code>.
   *
   * @param aEndIndex an <code>int</code> with the index */
  private void setEndIndex(int aEndIndex){
    this.endIndex = aEndIndex;
  }
  

  /**
   * This returns the field {@link #image}.
   *
   * @return a <code>String</code> */
  private String getImage() {
    return this.image;
  }
  
  /**
   * This sets the field {@link #image} to
   * <code>aImage</code>.
   *
   * @param aImage a <code>String</code> */
  private void setImage(String aImage){
    this.image = aImage;
  }
  

  /**
   * This returns the <code>String</code> matching the regular
   * expression pattern. 
   * 
   * @return the matching  <code>String</code> */
  public String toString() {
    return this.getImage();
  }
}
