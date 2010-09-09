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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import de.dfki.lt.tools.tokenizer.output.ParagraphOutputter;

/**
 * This is a test class for {@link JTok}.
 *
 * @author Joerg Steffen, DFKI
 * @version $Id: TestJTok.java,v 1.8 2010-08-31 09:56:50 steffen Exp $ */

public class TestJTok {

  /**
   * This is the path to the tokenizer root directory.
  private static final String ROOT =
    System.getProperty("tokenizer.root");
    */

  /**
   * This is the system local newline string. */
  private static final String LINE_SEPARATOR =
    System.getProperty("line.separator");

  /**
   * This is the property prefix for the languages in the
   * config. */
  private static final String LANG_PREFIX = "lang.";

  /**
   * This is the property for the tokenizer config in the
   * config. */
  private static final String CONFIG_PROP = "tokenizer.config";

  /**
   * This is the property for the encoding in the config. */
  private static final String ENCODING_PROP = "encoding";


  /**
   * This contains a <code>Properties</code> object with the
   * test description. */
  private Properties config;


  /**
   * This creates a new instance of <code>TestJTok</code> using a default test
   * description.
   */
  public TestJTok() {

    try {
      // load default test description
      Properties props = new Properties();
      props.load(FileTools.openResourceFileAsStream("testJTok.cfg"));
      this.setConfig(props);
    } catch (IOException ioe) {
      System.err.println("could not open file: " + ioe.getMessage());
      System.exit(1);
    }
  }

  /**
   * This creates a new instance of <code>TestJTok</code> using
   * the test description in the file <code>configFile</code>.
   *
   * @param configFile a <code>String</code> with the file name of a
   * properties file with the test description */
  public TestJTok(String configFile) {

    // load properties
    try {
      InputStream in =
        FileTools.openResourceFileAsStream(configFile);
      this.getConfig().load(in);
      in.close();
    } catch (IOException ioe) {
      System.err.println("could not open file: " + ioe.getMessage());
      System.exit(1);
    }
  }


  /**
   * This returns the field {@link #config}.
   *
   * @return a <code>Properties</code> */
  private Properties getConfig() {
    return this.config;
  }

  /**
   * This sets the field {@link #config} to
   * <code>aConfig</code>.
   *
   * @param aConfig a <code>Properties</code> */
  private void setConfig(Properties aConfig){
    this.config = aConfig;
  }


  /**
   * This starts the test. */
  public void start() {

    // get name of properties file for tokenizer
    String tokPropsVal = this.getConfig().getProperty(CONFIG_PROP);
    if (null == tokPropsVal) {
      System.err.println
        ("no tokenizer config file found in properties.");
      System.exit(1);
    }
    // load tokenizer properties
    /**
    File testDir = new File(tokPropsVal);
    if (!testDir.isAbsolute())
      tokPropsVal = ROOT + File.separator + tokPropsVal;
      */
    Properties tokProps = new Properties();
    try {
      InputStream in =
        FileTools.openResourceFileAsStream(tokPropsVal);
      tokProps.load(in);
      in.close();
    } catch (IOException ioe) {
      System.err.println(ioe.getMessage());
      System.exit(1);
    }

    // get encoding
    String encoding = this.getConfig().getProperty(ENCODING_PROP);
    if (null == encoding) {
      encoding = "ISO-8859-1";
    }

    // create new instance of JTok
    JTok testTok = new JTok(tokProps);

    // take time
    long time = System.currentTimeMillis();

    // iterate over keys in properties and use those starting with
    // lang
    Enumeration props = this.getConfig().propertyNames();
    while (props.hasMoreElements()) {
      String oneProp = (String)props.nextElement();
      if (!oneProp.startsWith(LANG_PREFIX))
        continue;

      // get Language
      String lang = oneProp.substring(LANG_PREFIX.length());

      // collect all filenames from the directories for language
      List allFiles = new ArrayList();
      // iterate over directories
      StringTokenizer dirs =
        new StringTokenizer
          (this.getConfig().getProperty(oneProp).trim(),",".intern());
      while (dirs.hasMoreElements()) {
        String oneDir = dirs.nextToken();
        /**
        testDir = new File(oneDir);
        if (!testDir.isAbsolute())
          oneDir = ROOT + File.separator + oneDir;
        */
        allFiles.addAll(FileTools.getFilesFromDir(oneDir, ".txt"));
      }

      // iterate over files
      for (int i = 0; i < allFiles.size(); i++) {
        String fileName = (String)allFiles.get(i);
        System.out.println
          ("tokenize " + (i + 1) + ". of " +
           allFiles.size() + " " + lang + " text..." +
           LINE_SEPARATOR + fileName);
        // get text from file
        String text = null;
        try {
          // get text from file
          text = FileTools.readFileAsString(new File(fileName), encoding);
        } catch (IOException ioe) {
          System.err.println(ioe.toString());
        }

        /*
        // print result as XML string
        System.out.println
          (XMLOutputter.createXMLString
           (testTok.tokenize(text, lang)));
        */

        // print result as paragraphs with text units and tokens
        Iterator it = ParagraphOutputter.createParagraphs
          (testTok.tokenize(text, lang)).iterator();
        while (it.hasNext()) {
          System.out.println(it.next());
        }
      }
    }

    // time needed for tokenization
    System.out.println("Time: " +
                       (System.currentTimeMillis() - time)/1000.0);
  }


  /**
   * This main method gets as argument the name of a test description
   * file and creates a new instance of <code>TestJTok</code>
   * with it.
   *
   * @param args a <code>String</code> array that can contain a
   * <code>String</code> with the name of the test description file;
   * if no description file is provided, a default test description is used */
  public static void main(String[] args) {

    // start test
    if (args.length > 0) {
      new TestJTok(args[0]).start();
    }
    else {
      new TestJTok().start();
    }
  }
}


