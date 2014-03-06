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

package de.dfki.lt.tools.tokenizer.annotate;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import org.junit.Test;

/**
 * Test class for {@link FastAnnotatedString}.
 * 
 * @author Joerg Steffen, DFKI
 */
public class TestFastAnnotatedString {

  /**
   * Tests annotated Strings.
   * 
   * @throws IOException
   *           if there is an error when reading the result file
   */
  @Test
  public void testFastAnnotatedString()
      throws IOException {

    AnnotatedString input1 =
      new FastAnnotatedString("This is a test.");
    // 0123456789012345
    input1.annotate("type", "tok", 0, 4);
    input1.annotate("type", "tok", 5, 7);
    input1.annotate("type", "tok", 8, 9);
    input1.annotate("type", "tok", 10, 14);
    input1.annotate("type", "punct", 14, 15);
    compareResults(input1, "expected-results/annotated-string-expected-1.txt");

    AnnotatedString input2 = new FastAnnotatedString("sdfslkdflsdfsldfksdf");
    input2.annotate("type", "tok", 5, 15);
    assertEquals("kdflsdfsld\t5-15\ttok", input2.toString("type").trim());

    input2.annotate("type", "mid", 9, 12);
    compareResults(input2, "expected-results/annotated-string-expected-2.txt");
  }


  /**
   * Compares the string representation of the given annotation and the expected
   * result as read from the given file name.
   * 
   * @param input
   *          the annotated string
   * @param resFileName
   *          the result file name
   * @throws IOException
   *           if there is an error when reading the result file
   */
  private void compareResults(
      AnnotatedString input, String resFileName)
      throws IOException {

    BufferedReader resReader = new BufferedReader(
      new InputStreamReader(
        getClass().getClassLoader().getResourceAsStream(
          resFileName),
        "utf-8"));
    BufferedReader inputReader =
      new BufferedReader(new StringReader(input.toString("type")));
    // compare line by line with expected result
    int lineCount = 1;
    String resLine;
    while ((resLine = resReader.readLine()) != null) {
      String inputLine = inputReader.readLine();
      assertNotNull(inputLine);
      assertEquals(resFileName + ": line " + lineCount, resLine, inputLine);
      lineCount++;
    }
  }
}
