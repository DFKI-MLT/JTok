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

package de.dfki.lt.tools.tokenizer.annotate;

import java.util.HashMap;

/**
 * <code>FastAnnotatedString</code> is a fast implementation of
 * the {@link AnnotatedString} interface. It reserves an array of
 * objects and an array of booleans for each newly introduced
 * annotation key. This provides fast access at the cost of memory. So
 * only introduce new annotation keys if neccessary.
 *
 * @author Joerg Steffen, DFKI
 * @version $Id: FastAnnotatedString.java,v 1.4 2005-04-12 08:47:37 steffen Exp $ */

public class FastAnnotatedString 
  implements AnnotatedString {

  /**
   * This contains the current index within the string. */
  private int index;

  /**
   * This contains the index position at the end of the string. */
  private int endIndex;

  /**
   * This contains the content of the string as a character array. */
  private char[] content;

  /**
   * This contains a <code>HashMap</code> that maps annotation keys to
   * arrays of <code>Object</code>s holding the annotation values. 
   * The object at a certain index in the array is the annotation
   * value of the corresponding character in the annotated string. */
  private HashMap annotations;

  /**
   * This contains a <code>HashMap</code> that maps annotation keys to
   * arrays of <code>boolean</code>s holding annotation borders. */
  private HashMap borders;

  /**
   * This contains the last annotation key used. */
  private String currentKey;

  /**
   * This contains the last value array used. */
  private Object[] currentValues;

  /**
   * This containts the last border array used. */
  private boolean[] currentBorders;


  /**
   * This creates a new instance of <code>FastAnnotatedString</code>. 
   * Not to be used outside this class */
  private FastAnnotatedString() {}

  /**
   * This create a new instance of <code>FastAnnotatedString</code>
   * for a text in <code>inputString</code>.
   *
   * @param anInputText a <code>String</code> with the text to
   * annotate */
  public FastAnnotatedString(String anInputText) {
    // check if there is a string
    if (anInputText == null)
      throw new NullPointerException();
    // initialization
    endIndex = anInputText.length();
    content = anInputText.toCharArray();
    annotations = new HashMap(5);
    borders = new HashMap(5);
    currentKey = null;
    currentBorders = null;
    currentValues = null;
    index = 0;
  }


  /**
   * This sets the position to getBeginIndex() and returns the
   * character at that position.
   *
   * @return the first character in the text, or DONE if the text is
   * empty */
  public char first() {
    index = 0;
    return current();
  }

  
  /**
   * This sets the position to getEndIndex()-1 (getEndIndex() if the
   * text is empty) and returns the character at that position
   *
   * @return the last character in the text, or DONE if the text is
   * empty */ 
  public char last() {
    if (0 != endIndex)
      index = endIndex - 1;
    else
      index = endIndex;
    return current();
  }


  /**
   * This gets the character at the current position (as returned by
   * getIndex()). 
   *
   * @return the character at the current position or DONE if the current
   * position is off the end of the text */
  public char current() {
    if (index >= 0 && index < endIndex) { 
      return content[index];
    }
    return DONE;
  }


  /**
   * This increments the index by one and returns the character
   * at the new index. If the resulting index is greater or equal
   * to getEndIndex(), the current index is reset to getEndIndex() and
   * a value of DONE is returned.
   *
   * @return the character at the new position or DONE if the new
   * position is off the end of the text range */
  public char next() {
    if (index < endIndex - 1) {
      index++;
      return content[index];
    }
    index = endIndex;
    return DONE;
  }

  
  /**
   * This decrements the index by one and returns the character
   * at the new index. If the current index is getBeginIndex(), the index
   * remains at getBeginIndex() and a value of DONE is returned.
   *
   * @return the character at the new position or DONE if the current
   * position is equal to getBeginIndex() */
  public char previous() {
    if (index > 0) {
      index--;
      return content[index];
    }
    return DONE;
  }

  
  /**
   * This returns the start index of the text.
   *
   * @return the index at which the text begins
   */
  public int getBeginIndex() {
    return 0;
  }

  
  /**
   * This returns the end index of the text. This index is the index
   * following the last character of the text.
   *
   * @return the index after the last character in the text */
  public int getEndIndex() {
    return endIndex;
  }

  
  /**
   * This returns the current index.
   *
   * @return the current index */
  public int getIndex() {
    return index;
  }
  

  /**
   * This sets the position to the specified position in the text and
   * returns that character.
   *
   * @param anIndex the index within the text; valid values range from
   * getBeginIndex() to getEndIndex(); an IllegalArgumentException is thrown
   * if an invalid value is supplied
   * @return the character at the specified position or DONE if the
   * specified position is equal to getEndIndex() */
  public char setIndex(int anIndex) {
    if (anIndex < 0 || anIndex > endIndex)
      throw new IllegalArgumentException("Invalid index");
    index = anIndex;
    return current();
  }

  
  /**
   * This create a copy of this object.
   *
   * @return a copy of this */
  public Object clone() {
    try {
      FastAnnotatedString other
        = (FastAnnotatedString)super.clone();
      return other;
    } catch (CloneNotSupportedException cnse) {
      throw new InternalError();
    }
  }


  /**
   * This returns the character from the specified position without
   * changing the index.
   *
   * @param charIndex the index within the text; valid values range from
   * getBeginIndex() to getEndIndex(); an IllegalArgumentException is thrown
   * if an invalid value is supplied
   * @return the character at the specified position or DONE if the
   * specified position is equal to getEndIndex() */
  public char charAt(int charIndex) {
    if (charIndex < 0 || charIndex > endIndex)
      throw new IllegalArgumentException("Invalid index");
    if (charIndex >= 0 && charIndex < endIndex) {
      return content[charIndex];
    }
    return DONE;
  }


  /**
   * This returns the substring between the specified indices.
   *
   * @param aBeginIndex an <code>int</code> with the index of the first
   * character of the range
   * @param anEndIndex an <code>int</code> with the index of the
   * character following the last character of the range
   * @return a <code>String</code> with the substring
   * @exception IllegalArgumentException if aBeginIndex is less then 0,
   * anEndIndex is greater than the length of the string, or aBeginIndex
   * and anEndIndex together don't define a non-empty subrange of the
   * string */
  public String substring(int aBeginIndex, int anEndIndex) {
    if (aBeginIndex < 0 ||
        anEndIndex > endIndex ||
        aBeginIndex > anEndIndex)
      throw new IllegalArgumentException("Invalid substring range");
    return new String(content, aBeginIndex, anEndIndex - aBeginIndex);
  }


  /**
   * Adds an annotation to a subrange of the string.
   *
   * @param key a <code>String</code> with the annotation key
   * @param value a <code>Object</code> with the annotation value
   * @param aBeginIndex an <code>int</code> with the index of the first
   * character of the range
   * @param anEndIndex an <code>int</code> with the index of the
   * character following the last character of the range
   * @exception IllegalArgumentException if aBeginIndex is less then 0,
   * anEndIndex is greater than the length of the string, or aBeginIndex
   * and anEndIndex together don't define a non-empty subrange of the
   * string  */
  public void annotate(String key, Object value, 
                       int aBeginIndex, int anEndIndex) {
    // check if range is legal
    if (aBeginIndex < 0 ||
        anEndIndex > endIndex ||
        aBeginIndex >= anEndIndex)
      throw new IllegalArgumentException("Invalid substring range");
    
    if (!key.equals(currentKey)) {
      // update currents
      Object probe = annotations.get(key);
      if (null == probe) {
        //create new arrays for this key
        currentValues = new Object[endIndex];
        currentBorders = new boolean[endIndex];
        currentKey = key;
        // if string is not empty, the first character is already a border 
        if (endIndex > 0)
          currentBorders[0] = true;
        // store arrays
        annotations.put(key, currentValues);
        borders.put(key, currentBorders);
      }
      else {
        currentValues = (Object[])probe;
        currentBorders = (boolean[])borders.get(key);
        currentKey = key;
      }
    }

    // annotate
    for (int i = aBeginIndex; i < anEndIndex; i++) {
      currentValues[i] = value;
      currentBorders[i] = false;
    }
    // set border for current annotation and the implicit next
    // annotation (if there is one)
    currentBorders[aBeginIndex] = true;
    if (anEndIndex < endIndex)
      currentBorders[anEndIndex] = true;
  }

  
  /**
   * This returns the annotation value of the string at the current
   * index for a given key.
   *
   * @param key a <code>String</code> with the annotation key
   * @return an <code>Object</code> with the annotation value or
   * <code>null</code> if there is no annotation with the given key at
   * that position */ 
  public Object getAnnotation(String key) {
    if (index >= 0 && index < endIndex) {
      if (!key.equals(currentKey)) {
        // update currents
        Object probe = annotations.get(key);
        if (null != probe) { 
          currentKey = key;
          currentValues = (Object[])probe;
          currentBorders = (boolean[])borders.get(key);
        }
        else 
          return null;
      }
      
      // get annotation value
      return currentValues[index];
    }

    return null;
  }


  /**
   * This returns the index of the first character of the run
   * with respect to the given annotation key containing the current
   * character. 
   *
   * @param key <code>String</code> with an annotation key
   * @return an <code>int</code> with the index */
  public int getRunStart(String key) {
    if (!key.equals(currentKey)) {
      // update currents
      Object probe = borders.get(key);
      if (null != probe) {
        currentKey = key;
        currentValues = (Object[])annotations.get(key);
        currentBorders = (boolean[])probe;
      }
      else
        return 0;
    }
    // search border
    for (int i = index; i >= 0; i--) {
      if (currentBorders[i])
        return i;
    }
    return 0;
  }


  /**
   * This returns the index of the first character following the run
   * with respect to the given annotation key containing the current
   * character. 
   *
   * @param key <code>String</code> with an attribute key
   * @return an <code>int</code> with the index */
  public int getRunLimit(String key) {
    if (!key.equals(currentKey)) {
      // update currents
      Object probe = borders.get(key);
      if (null != probe) {
        currentKey = key;
        currentValues = (Object[])annotations.get(key);
        currentBorders = (boolean[])probe;
      }
      else
        return endIndex;
    }
    // search border
    for (int i = index + 1; i < endIndex; i++) {
      if (currentBorders[i])
        return i;
    }
    return endIndex;
  }

  
  /**
   * This returns the index of the first character annotated with the
   * given annotation key following the run containing the current
   * character with respect to the given annotation key.
   *
   * @param key a <code>String</code> with the annotation key
   * @return an <code>int</code> with the index */
  public int findNextAnnotation(String key) {
    if (!key.equals(currentKey)) {
      // update currents
      Object probe = annotations.get(key);
      if (null != probe) {
        currentKey = key;
        currentValues = (Object[])probe;
        currentBorders = (boolean[])borders.get(key);
      }
      else
        return endIndex;
    }
        
    // search next annotation
    int i;
    for (i = index + 1; i < endIndex; i++) {
      if (currentBorders[i]) {
        for (int j = i; j < endIndex; j++) {
          if (null != currentValues[j])
            return j;
        }
        return endIndex;
      }
    }
    return endIndex;
  }
  
  
  /**
   * This returns a string representation of the annotated string with the
   * annotation for the given attribute key.
   *
   * @param key <code>String</code> with an attribute key
   * @return a <code>String</code> */
  public String toString(String key) {
    // init result
    StringBuffer result = new StringBuffer();
    // make a backup of the current index
    int bakupIndex = index;
    // iterate over string
    index = 0;
    while (index < endIndex) {
      int endAnno = this.getRunLimit(key);
      if (null != getAnnotation(key)) 
        result.append(substring(index, endAnno) + "\t" +
                      index + "-" + endAnno + "\t" +
                      getAnnotation(key) + 
                      System.getProperty("line.separator"));
      index = endAnno;
    }
    // restore index
    index = bakupIndex;
    // return result
    return result.toString();
  }


  /**
   * This returns the surface string of the annotated string.
   *
   * @return a <code>String</code> */
  public String toString() {
    return new String(content);
  }
}
