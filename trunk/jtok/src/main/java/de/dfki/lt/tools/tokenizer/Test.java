package de.dfki.lt.tools.tokenizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * {@link Test}
 *
 * @author Joerg Steffen, DFKI
 */
public class Test {

  /**
   * The main method; requires no arguments.
   *
   * @param args
   *          the arguments; not used here
   */
  public static void main(String[] args) {

    try (BufferedReader in = Files.newBufferedReader(
      Paths.get("test.txt"), Charset.forName("UTF-8"))) {
      String line;
      int count = 0;
      int fileCount = 1;
      PrintWriter out = new PrintWriter(Files.newBufferedWriter(
        Paths.get(String.format("test-%02d.txt", fileCount)),
        Charset.forName("UTF-8")));
      while ((line = in.readLine()) != null) {
        String[] splitLine = line.split("\t");
        line = splitLine[1];
        out.println(line);
        count++;
        if (count % 10000 == 0) {
          fileCount++;
          out.close();
          out = new PrintWriter(Files.newBufferedWriter(
            Paths.get(String.format("test-%02d.txt", fileCount)),
            Charset.forName("UTF-8")));
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
