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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.CharacterIterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.dfki.lt.tools.tokenizer.FileTools;
import de.dfki.lt.tools.tokenizer.JTok;
import de.dfki.lt.tools.tokenizer.annotate.AnnotatedString;
import de.dfki.lt.tools.tokenizer.exceptions.ProcessingException;


/**
 * <code>XMLOutputter</code> provides static methods that return an
 * XML presentation of a {@link
 * de.dfki.lt.tools.tokenizer.annotate.AnnotatedString}.
 *
 * @author Joerg Steffen, DFKI
 * @version $Id: XMLOutputter.java,v 1.12 2010-09-02 07:42:10 steffen Exp $ */

public class XMLOutputter {

  /**
   * This contains the logger object for logging. */
  private static final Logger LOG = LoggerFactory.getLogger(XMLOutputter.class);

  /**
   * This is the name of XML elements in the result that describe a
   * document. */
  public static final String XML_DOCUMENT = "Document";

  /**
   * This is the name of XML elements in the result that describe a
   * paragraph. */
  public static final String XML_PARAGRAPH = "p";

  /**
   * This is the name of XML elements in the result that describe a
   * text unit. Text units are contained in paragraphs. */
  public static final String XML_TEXT_UNIT = "tu";

  /**
   * This is the name of the XML attribute in
   * <code>XML_TEXT_UNIT</code> that contains the text unit id. */
  public static final String ID_ATT = "id";

  /**
   * This is the name of XML elements in the result that describe a
   * token. Tokens are contained in text units. */
  public static final String XML_TOKEN = "Token";

  /**
   * This is the name of the XML attribute in <code>XML_TOKEN</code>
   * that contains the token image. */
  public static final String IMAGE_ATT = "string";

  /**
   * This the name of the XML attribute in <code>XML_TOKEN</code>
   * that contains the Penn Treebank token image if it is any different
   * than the regular surface string. */
  public static final String PTB_ATT = "ptb";

  /**
   * This is the name of the XML attribute in <code>XML_TOKEN</code>
   * that contains the token type. */
  public static final String TOK_TYPE_ATT = "type";

  /**
   * This is the name of the XML attribute in <code>XML_TOKEN</code>
   * that contains the token offset. */
  public static final String OFFSET_ATT = "offset";

  /**
   * This is the name of the XML attribute in <code>XML_TOKEN</code>
   * that contains the token length. */
  public static final String LENGTH_ATT = "length";


  /**
   * This creates an XML document from an annotated
   * <code>input</code>.
   *
   * @param input an {@link
   * de.dfki.lt.tools.tokenizer.annotate.AnnotatedString}
   * @return  a <code>Document</code>
   * @exception ProcessingException if an error occurs */
  public static Document createXMLDocument(AnnotatedString input) {

    // create result document
    Document doc = null;
    try {
      DocumentBuilderFactory factory =
        DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      doc = builder.newDocument();
    } catch (ParserConfigurationException pce) {
      throw new ProcessingException(pce.getMessage());
    }

    // create root element
    Element root = doc.createElement(XML_DOCUMENT);
    doc.appendChild(root);

    // init text unit counter
    int tuId = 0;

    // create paragraph element
    Element p = doc.createElement(XML_PARAGRAPH);
    // create text unit element
    Element tu = doc.createElement(XML_TEXT_UNIT);
    tu.setAttribute(ID_ATT, tuId + "");

    // iterate over tokens and create XML elements
    char c = input.setIndex(0);
    while (c != CharacterIterator.DONE) {

      int tokenStart = input.getRunStart(JTok.CLASS_ANNO);
      int tokenEnd = input.getRunLimit(JTok.CLASS_ANNO);
      // check if c belongs to a token
      if (null != input.getAnnotation(JTok.CLASS_ANNO)) {
        // get tag
        String type =
          (String)input.getAnnotation(JTok.CLASS_ANNO);
        if (null == type)
          throw new ProcessingException
            ("undefined class " + input.getAnnotation(JTok.CLASS_ANNO));
        // create new element
        Element xmlToken = doc.createElement(XML_TOKEN);
        // set attributes
        String image = input.substring(tokenStart, tokenEnd);
        xmlToken.setAttribute(IMAGE_ATT, image);
        String ptbImage = Token.applyPtbFormat(image, type);
        if (null != ptbImage) {
          xmlToken.setAttribute(PTB_ATT, ptbImage);
        }
        xmlToken.setAttribute(TOK_TYPE_ATT, type);
        xmlToken.setAttribute(OFFSET_ATT, tokenStart + "");
        xmlToken.setAttribute(LENGTH_ATT, image.length() + "");

        // check if token is first token of a paragraph or text unit
        if (null != input.getAnnotation(JTok.BORDER_ANNO)) {
          // add current text unit to paragraph and create new one
          if (tu.hasChildNodes()) {
            p.appendChild(tu);
            tu = doc.createElement(XML_TEXT_UNIT);
            tuId++;
            tu.setAttribute(ID_ATT, tuId + "");
          }
        }

        // check if token is first token of a paragraph
        if (input.getAnnotation(JTok.BORDER_ANNO) == JTok.P_BORDER) {
          // add current paragraph to document and create new one
          if (p.hasChildNodes()) {
            root.appendChild(p);
            p = doc.createElement(XML_PARAGRAPH);
          }
        }

        // add token to text unit
        tu.appendChild(xmlToken);
      }
      // set iterator to next token
      c = input.setIndex(tokenEnd);
    }
    // add last text units to paragraph
    if (tu.hasChildNodes()) {
      p.appendChild(tu);
    }
    // add last paragraph element to document
    if (p.hasChildNodes()) {
      root.appendChild(p);
    }

    // return document
    return doc;
  }


  /**
   * This creates an XML file from an annotated <code>input</code>.
   *
   * @param input an {@link
   * de.dfki.lt.tools.tokenizer.annotate.AnnotatedString}
   * @param anEncoding a <code>String</code> with the encoding to use
   * @param aFileName a <code>String</code> with the name of the XML file
   * @exception ProcessingException if an error occurs
   */
  public static void createXMLFile(AnnotatedString input,
                                   String anEncoding,
                                   String aFileName) {
    // tokenize text
    Document doc = createXMLDocument(input);

    try {
      // init writer for result
      Writer out = new OutputStreamWriter(
          new FileOutputStream(aFileName), anEncoding);
      // use a transformer for output
      Transformer transformer =
        TransformerFactory.newInstance().newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty(OutputKeys.ENCODING, anEncoding);
      DOMSource source = new DOMSource(doc);
      StreamResult result = new StreamResult(out);
      transformer.transform(source, result);
      out.close();
    } catch (TransformerException te) {
      throw new ProcessingException(te.getMessage());
    } catch (IOException ioe) {
      throw new ProcessingException(ioe.getMessage());
    }
  }


  /**
   * This creates an XML string from an annotated
   * <code>input</code>. Correctly works with Unicode
   * (uschaefer 2006-06-09).
   * @param input an {@link
   * de.dfki.lt.tools.tokenizer.annotate.AnnotatedString}
   * @return  an XML String
   * @exception ProcessingException if an error occurs */
  public static String createXMLString(AnnotatedString input) {
    // tokenize text
    Document doc =
      createXMLDocument(input);

    // init output writer for result
    StringWriter out = new StringWriter();

    // use a transformer for output
    try {
      Transformer transformer =
        TransformerFactory.newInstance().newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      DOMSource source = new DOMSource(doc);
      StreamResult result = new StreamResult(out);
      transformer.transform(source, result);
    } catch (TransformerException te) {
      throw new ProcessingException(te.getMessage());
    }

    // return result
    return out.toString();
  }


  /**
   * This main method must be used with two or three arguments:
   * - a file name for the document to tokenize
   * - the language of the document
   * - an optional encoding to use (default is ISO-8859-1)
   * Supported languages are: de, en, it
   *
   * @param args an array of <code>String</code>s with the arguments */
  public static void main(String[] args) {

    // check for correct arguments
    if (args.length != 2 &&
        args.length != 3) {
      System.out.println
        ("This method needs two arguments:\n" +
         "- a file name for the document to tokenize\n" +
         "- the language of the document\n" +
         "- an optional encoding to use (default is ISO-8859-1)\n" +
         "Supported languages are: de, en, it");
      System.exit(1);
    }

    // check encoding
    String encoding = "ISO-8859-1";
    if (args.length == 3) {
      encoding = args[2];
    }

    String text = null;
    try {
      // get text from file
      text = FileTools.readFileAsString(new File(args[0]), encoding);
    } catch (IOException ioe) {
      System.err.println(ioe.toString());
      System.exit(1);
    }

    try {
      // create new instance of JTok
      JTok testTok = new JTok();

      // tokenize text
      AnnotatedString result = testTok.tokenize(text, args[1]);

      // print result
      System.out.println(XMLOutputter.createXMLString(result));

    } catch (IOException e) {
      LOG.error(e.getLocalizedMessage(), e);
    }
  }

}
