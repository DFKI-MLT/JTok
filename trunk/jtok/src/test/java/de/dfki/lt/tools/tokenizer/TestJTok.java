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

package de.dfki.lt.tools.tokenizer;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;

import de.dfki.lt.tools.tokenizer.output.Paragraph;
import de.dfki.lt.tools.tokenizer.output.Outputter;

/**
 * Test class for {@link JTok}.
 *
 * @author Joerg Steffen, DFKI
 */
public class TestJTok {

  /**
   * Contains the tokenizer.
   */
  private static JTok tokenizer;


  /**
   * Initializes the tokenizer.
   *
   * @throws IOException
   *           if there is an error during initialization
   */
  @BeforeClass
  public static void oneTimeSetUp()
      throws IOException {

    Properties tokProps = new Properties();
    InputStream in = FileTools.openResourceFileAsStream("jtok/jtok.cfg");
    tokProps.load(in);
    in.close();
    // create new instance of JTok
    tokenizer = new JTok(tokProps);
  }


  /**
   * Tests the method {@link JTok#tokenize(String, String)}.
   *
   * @throws IOException
   *           if there is an error when reading files
   */
  @Test
  public void testGerman()
      throws IOException {

    // German
    this.compareResults(
      "german/amazon.txt", "de",
      "expected-results/german/amazon-expected.txt");
    this.compareResults(
      "german/german.txt", "de",
      "expected-results/german/german-expected.txt");
  }


  /**
   * Tests the method {@link JTok#tokenize(String, String)}.
   *
   * @throws IOException
   *           if there is an error when reading files
   */
  @Test
  public void testEnglish()
      throws IOException {

    // English
    this.compareResults(
      "english/amazon-coleman.txt", "en",
      "expected-results/english/amazon-coleman-expected.txt");
    this.compareResults(
      "english/english.txt", "en",
      "expected-results/english/english-expected.txt");
    this.compareResults(
      "english/randomhouse-hertsgaard.txt", "en",
      "expected-results/english/randomhouse-hertsgaard-expected.txt");
  }


  /**
   * Tests the method {@link JTok#tokenize(String, String)}.
   *
   * @throws IOException
   *           if there is an error when reading files
   */
  @Test
  public void testClitics()
      throws IOException {

    // Other
    this.compareResults(
      "test/cliticsTest.txt", "en",
      "expected-results/test/cliticsTest-expected.txt");
  }


  /**
   * Tests the method {@link JTok#tokenize(String, String)}.
   *
   * @throws IOException
   *           if there is an error when reading files
   */
  @Test
  public void testMisc()
      throws IOException {

    this.compareResults(
      "test/misc.txt", "en",
      "expected-results/test/misc-expected.txt");
  }


  /**
   * Tests the method {@link JTok#tokenize(String, String)}.
   *
   * @throws IOException
   *           if there is an error when reading files
   */
  @Test
  public void testNumbers()
      throws IOException {

    this.compareResults(
      "test/numbersTest.txt", "de",
      "expected-results/test/numbersTest-expected.txt");
  }


  /**
   * Tests the method {@link JTok#tokenize(String, String)}.
   *
   * @throws IOException
   *           if there is an error when reading files
   */
  @Test
  public void testParagraphs()
      throws IOException {

    this.compareResults(
      "test/paragraphTest.txt", "en",
      "expected-results/test/paragraphTest-expected.txt");
  }


  /**
   * Tests the method {@link JTok#tokenize(String, String)}.
   *
   * @throws IOException
   *           if there is an error when reading files
   */
  @Test
  public void testPunctuation()
      throws IOException {

    this.compareResults(
      "test/punctuationTest.txt", "en",
      "expected-results/test/punctuationTest-expected.txt");
  }


  /**
   * Tests the method {@link JTok#tokenize(String, String)}.
   *
   * @throws IOException
   *           if there is an error when reading files
   */
  @Test
  public void testSpecialCharacters()
      throws IOException {

    this.compareResults(
      "test/specialCharactersTest.txt", "de",
      "expected-results/test/specialCharactersTest-expected.txt");
  }

  /**
   * Tests the method {@link JTok#tokenize(String, String)}.
   *
   * @throws IOException
   *           if there is an error when reading files
   */
  @Test
  public void testTextUnits()
      throws IOException {

    this.compareResults(
      "test/tuTest.txt", "de",
      "expected-results/test/tuTest-expected.txt");
  }


  /**
   * Compares the tokenization result of the given input with the result as read
   * from the given file name.
   *
   * @param inputFileName
   *          the input file to tokenize
   * @param lang
   *          the language of the input file
   * @param resFileName
   *          the result file name
   * @throws IOException
   *           if there is an error when reading the result file
   */
  private void compareResults(
      String inputFileName, String lang, String resFileName)
      throws IOException {

    System.out.println(inputFileName);
    // tokenize input file
    InputStream in =
      getClass().getClassLoader().getResourceAsStream(inputFileName);
    String input =
      new String(FileTools.readInputStreamToByteArray(in), "utf-8");
    StringBuilder result = new StringBuilder();
    String newline = System.getProperty("line.separator");
    // print result as paragraphs with text units and tokens
    for (Paragraph onePara : Outputter.createParagraphs
      (tokenizer.tokenize(input, lang))) {
      result.append(onePara.toString());
      result.append(newline);
    }

    // compare line by line with expected result
    BufferedReader resReader = new BufferedReader(
      new InputStreamReader(
        getClass().getClassLoader().getResourceAsStream(
          resFileName),
        "utf-8"));
    BufferedReader inputReader =
      new BufferedReader(new StringReader(result.toString()));
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
