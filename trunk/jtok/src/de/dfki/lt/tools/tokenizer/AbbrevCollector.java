package de.dfki.lt.tools.tokenizer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.dfki.lt.tools.tokenizer.regexp.RegExp;

/**
 * {@link AbbrevCollector} provides methods to collect abbreviations from
 * corpora containing a single sentence per line.
 *
 * @author Joerg Steffen, DFKI
 * @version $Id: AbbrevCollector.java,v 1.1 2010-04-30 10:05:47 steffen Exp $
 */
public class AbbrevCollector {

  /**
   * This scans the given directory recursively for files with the given suffix.
   * It is assumed that each of these files contains one sentence per line. It
   * extracts all abbreviations from these files and stores them under the given
   * result file name using UTF-8 encoding.
   *
   * @param dir
   *          a <code>String</code> with the directory to scan
   * @param suffix
   *          a <code>String</code> with the file name suffix
   * @param encoding
   *          a <code>String</code> with the encoding of the files
   * @param resultFileName
   *          a <code>String</code> with the result file name
   * @param lang
   *          a <code>String</code> with the language of the files
   * @throws IOException
   *           if there is a problem when reading or writing the files
   */
  public static void collect(
      String dir, String suffix, String encoding,
      String resultFileName, String lang)
      throws IOException {

    // init tokenizer and get the relevant language resource
    JTok jtok = new JTok();
    LanguageResource langRes = jtok.getLanguageResource(lang);

    // get matchers and lists used to filter the abbreviations

    // the lists contains known abbreviations and titles
    HashMap abbrevLists = langRes.getAbbrevLists();

    // this contains the word that only start with a capital letter at
    // the beginning of a sentence; we want to avoid to extract abbreviations
    // consisting of such a word followed by a punctuation
    Set<String> nonCapTerms = langRes.getNonCapTerms();

    // this are the matcher for abbreviations
    RegExp abbrevMatcher = langRes.getAbbrevMatcher();
    RegExp initialMatcher = langRes.getInitialMatcher();

    Set<String> abbrevs = new HashSet<String>();

    // get all training files
    List<String> trainingFiles =
      FileTools.getFilesFromDir(dir, suffix);

    // iterate over corpus files
    for (String oneFileName : trainingFiles) {
      System.out.println("processing " + oneFileName + " ...");

      // init reader
      BufferedReader in =
        new BufferedReader(
        new InputStreamReader(
        new FileInputStream(oneFileName), encoding));
      String sent;
      // read lines from file
      while ((sent = in.readLine()) != null) {

        // split the sentence using as separator whitespaces and
        // ... .. ' ` \ \\ |
        String[] tokens = sent.split(" |\\.\\.\\.|\\.\\.|'|`|\\(|\\)|[|]");

        for (int i = 0; i < tokens.length - 1; i++) {
          // we skip the last token with the final sentence punctuation
          String oneTok = tokens[i];
          if (oneTok.length() > 1 && oneTok.endsWith(".")) {

            // if the abbreviation contains a hyphen, it's sufficient to check
            // the part after the hyphen
            int hyphenPos = oneTok.lastIndexOf("-");
            if (hyphenPos != -1) {
              oneTok = oneTok.substring(hyphenPos + 1);
            }

            // check with matchers
            if (abbrevMatcher.matches(oneTok)
              || initialMatcher.matches(oneTok)) {
              continue;
            }

            // check with lists
            boolean found = false;
            Iterator it = abbrevLists.keySet().iterator();
            while (it.hasNext()) {
              String abbrevClass = (String)it.next();
              Set oneList = (Set)abbrevLists.get(abbrevClass);
              if (oneList.contains(oneTok)) {
                found = true;
                break;
              }
            }
            if (found) {
              continue;
            }

            // check with terms;
            // convert first letter to upper case because this is the format of
            // the terms in the list and remove the punctuation
            char firstChar = oneTok.charAt(0);
            firstChar = Character.toUpperCase(firstChar);
            String tempTok =
              firstChar + oneTok.substring(1, oneTok.length() - 1);
            if (nonCapTerms.contains(tempTok)) {
              continue;
            }

            // we found a new abbreviation
            abbrevs.add(oneTok);
          }
        }
      }
      in.close();
    }

    // sort collected abbreviations
    List<String> sortedAbbrevs = new ArrayList<String>(abbrevs);
    Collections.sort(sortedAbbrevs);

    // save results
    PrintWriter out = null;
    try {
      out = new PrintWriter(
        new BufferedWriter(
        new OutputStreamWriter(
        new FileOutputStream(resultFileName), "utf-8")));
      for (String oneAbbrev : sortedAbbrevs) {
        out.println(oneAbbrev);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (null != out) {
        out.close();
      }
    }
  }


  /**
   * This is the main method. It requires 5 arguments:
   * <ul>
   * <li>the parent folder of the corpus
   * <li>the file extension of the corpus files to use
   * <li>the file encoding
   * <li>the result file name
   * <li>the language of the corpus
   * </ul>
   *
   * @param args
   *          an array with the arguments
   */
  public static void main(String[] args) {

    if (args.length != 5) {
      System.err.println("wrong number of arguments");
      System.exit(1);
    }

    try {
      AbbrevCollector.collect(args[0], args[1], args[2], args[3], args[4]);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
