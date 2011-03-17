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

package de.dfki.lt.tools.tokenizer;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;

import de.dfki.lt.tools.tokenizer.output.Paragraph;
import de.dfki.lt.tools.tokenizer.output.ParagraphOutputter;

/**
 * This is a test class for {@link JTok}.
 *
 * @author Joerg Steffen, DFKI
 * @version $Id: TestJTok.java,v 1.8 2010-08-31 09:56:50 steffen Exp $ */

public class TestJTok {

  /**
   * This contains the tokenizer.
   */
  private static JTok tokenizer;


  /**
   * This initializes the tokenizer.
   *
   * @throws IOException if there is an error during initialization
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
   * This tests the {@link JTok#tokenize(String, String)} method.
   *
   * @throws IOException
   *           if there is an error when reading files
   */
  @Test
  public void testTokenize()
      throws IOException {

    // German
    this.compareResults(
      "german/amazon.txt", "de",
      "expected-results/german/amazon-expected.txt");
    this.compareResults(
      "german/german.txt", "de",
      "expected-results/german/german-expected.txt");

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

    // Other
    this.compareResults(
      "test/cliticsTest.txt", "en",
      "expected-results/test/cliticsTest-expected.txt");
    this.compareResults(
      "test/misc.txt", "en",
      "expected-results/test/misc-expected.txt");
    this.compareResults(
      "test/numbersTest.txt", "en",
      "expected-results/test/numbersTest-expected.txt");
    this.compareResults(
      "test/paragraphTest.txt", "en",
      "expected-results/test/paragraphTest-expected.txt");
    this.compareResults(
      "test/punctuationTest.txt", "en",
      "expected-results/test/punctuationTest-expected.txt");
    this.compareResults(
      "test/specialCharactersTest.txt", "en",
      "expected-results/test/specialCharactersTest-expected.txt");
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

    // tokenize input file
    InputStream in =
      getClass().getClassLoader().getResourceAsStream(inputFileName);
    String input =
      new String(FileTools.readInputStreamToByteArray(in), "utf-8");
    StringBuilder result = new StringBuilder();
    String newline = System.getProperty("line.separator");
    // print result as paragraphs with text units and tokens
    Iterator<Paragraph> it = ParagraphOutputter.createParagraphs
      (tokenizer.tokenize(input, lang)).iterator();
    while (it.hasNext()) {
      result.append(it.next().toString());
      result.append(newline);
    }

    /*
    PrintWriter out = new PrintWriter(
      new BufferedWriter(
        new OutputStreamWriter(
          new FileOutputStream("data/" + resFileName), "utf-8")));
    out.print(result.toString());
    out.close();
    */

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


