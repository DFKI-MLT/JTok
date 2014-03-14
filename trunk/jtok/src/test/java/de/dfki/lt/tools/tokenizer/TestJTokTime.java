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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test class for {@link JTok}.
 *
 * @author Joerg Steffen, DFKI
 */
public class TestJTokTime {

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


  @Test
  public void testTime() throws IOException {

    long start = System.currentTimeMillis();
    for (int i = 1; i <= 30; i++) {
      System.out.println(i);
      String input = new String(
        Files.readAllBytes(Paths.get(String.format("test-%02d.txt", i))),
        StandardCharsets.UTF_8);
      tokenizer.tokenize(input, "de");
    }
    long stop = System.currentTimeMillis();

    System.out.println("Time: " + (stop - start) / 1000.0);

  }
}
