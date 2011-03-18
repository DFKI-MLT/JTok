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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.List;


/**
 * <code>FileTools</code> provides static methods to work on files and
 * stream.
 *
 * @author Joerg Steffen, DFKI
 * @version $Id: FileTools.java,v 1.7 2010-01-22 16:33:23 steffen Exp $ */

public class FileTools {

  /**
   * <code>FileTools</code> cannot be instantiated. */
  private FileTools() {}


  /**
   * Write an input stream to a file. Fail, if filename already exists.
   *
   * @param inputStream some stream to be saved
   * @param file the target file
   * @exception IOException when file can't be saved */
  public static void saveStream(InputStream inputStream, File file)
    throws IOException {

    int i;
    byte ab[] = new byte[4096];
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(file);
      while ((i = inputStream.read(ab)) > 0)
        fos.write(ab, 0, i);
    } finally { //always close
      if (null != fos) {
        fos.close();
      }
    }
  }


  /**
   * Read a url content to a string.
   *
   * @param url some url
   * @return the content as a string
   * @exception IOException thrown when ressource cannot
   * be opened for reading */
  public static String readUrlToString(URL url)
    throws IOException {

    URLConnection con = url.openConnection();
    con.connect();
    InputStream is = null;
    ByteArrayOutputStream bos = null;

    // initialize size
    int len = con.getContentLength(); // is this really necessary?
    if (-1 == len) {
      len = 10000;
    }

    try {
      bos = new ByteArrayOutputStream(len);
      is = con.getInputStream();
      readInputStream(bos, is);
    } finally { // always close
      if (null != is) {
        is.close();
      }
      if (null != bos) {
        bos.close();
      }
    }
    return bos.toString();
  }


  /**
   * Read a url content to a byte array.
   *
   * @param url some url
   * @return the content as a byte array
   * @exception IOException thrown when ressource cannot
   * be opened for reading */
  public static byte[] readUrlToByteArray(URL url)
    throws IOException {

    URLConnection con = url.openConnection();
    con.connect();
    InputStream is = null;
    ByteArrayOutputStream bos = null;

    // initialize size
    int len = con.getContentLength(); // is this really necessary?
    if (-1 == len) {
      len = 10000;
    }

    try {
      bos = new ByteArrayOutputStream(len);
      is = con.getInputStream();
      readInputStream(bos, is);
    } finally { // always close
      if (null != is) {
        is.close();
      }
      if (null != bos) {
        bos.close();
      }
    }
    return bos.toByteArray();
  }


  /**
   * Reads some input stream and writes it into an output stream. The
   * method applies some efficient buffering in byte arrays and is the
   * basis for all read...-methods in this class.
   *
   * @param os some output stream.
   * @param is some input stream.
   * @exception java.io.IOException thrown when reading or
   * writing fails */
  public static void readInputStream(OutputStream os, InputStream is)
    throws java.io.IOException {

    byte[] buffer = new byte[4096];
    int readb;
    while (true) {
      readb = is.read(buffer);
      if (readb == -1) break;
      os.write(buffer,0,readb);
    }
  }


  /**
   * Read some input stream and return its content as a string.
   *
   * @param is the input stream
   * @return the content of the stream as string
   * @exception java.io.IOException */
  public static String readInputStream(InputStream is)
    throws java.io.IOException {

    ByteArrayOutputStream bos = null;
    try {
      bos = new ByteArrayOutputStream();
      readInputStream(bos,is);
    } finally { // always close
      if (null != bos) {
        bos.close();
      }
    }
    return bos.toString();
  }


  /**
   * Read some input stream and return its content as byte array.
   *
   * @param is the input stream
   * @return the content of the stream as byte array
   * @exception java.io.IOException */
  public static byte[] readInputStreamToByteArray(InputStream is)
    throws java.io.IOException {

    ByteArrayOutputStream bos = null;
    try {
      bos = new ByteArrayOutputStream();
      readInputStream(bos,is);
    } finally { // always close
      if (null != bos) {
        bos.close();
      }
    }
    return bos.toByteArray();
  }


  /**
   * This recursivly collects all filenames in the directory
   * <code>aDirectory</code> with suffix <code>aSuffix</code> and
   * returns them in a <code>List</code>.
   *
   * @param aDirectory a <code>String</code> with the directory name
   * @param aSuffix a <code>String</code> with a filename suffix
   * @return a <code>List</code> with the filenames */
  public static List getFilesFromDir(String aDirectory,
				     String aSuffix) {

    // initialize result list
    List fileNames = new ArrayList();

    // add file separator to directory if necessary
    if (!aDirectory.endsWith(File.separator)) {
      aDirectory = aDirectory + File.separator;
    }

    // check if input is an directory
    File directory = new File(aDirectory);
    if (!directory.isDirectory()) {
      return fileNames;
    }

    // iterate over files in directory
    File[] filesInDir = directory.listFiles();
    for (int i = 0; i < filesInDir.length; i++) {
      // if file is a directory, collect recursivly
      if (filesInDir[i].isDirectory()) {
        fileNames.addAll(getFilesFromDir
                         (filesInDir[i].getAbsolutePath(), aSuffix));
      }
      else if (filesInDir[i].getName()
               .endsWith(aSuffix))
        // otherwise, add filename with matching suffix to result list
        fileNames.add(filesInDir[i].getAbsolutePath());
    }
    return fileNames;
  }


  /**
   * This simply copies a source file to a target file.
   *
   * @param source the source <code>File</code> to copy
   * @param target the target <code>File</code> */
  public static void copyFile(File source, File target)
    throws java.io.IOException {
    FileInputStream fis  = new FileInputStream(source);
    FileOutputStream fos = new FileOutputStream(target);
    byte[] buf = new byte[1024];
    int i = 0;
    while((i = fis.read(buf)) != -1) {
      fos.write(buf, 0, i);
    }
    fis.close();
    fos.close();
  }


  /**
   * New NIO based method to read the contents of a file as byte[] array.
   * Only files up to size Integer.MAX_INT can be read.
   * The ByteBuffer is rewinded when returned.
   */
  public static ByteBuffer readFile(File aFile)
    throws FileNotFoundException, IOException {

    FileInputStream fis = new FileInputStream(aFile);
    FileChannel fc = fis.getChannel();
    // for some reason, the buffer must be 1 byte bigger than the file
    ByteBuffer readBuffer = ByteBuffer.allocate((int)fc.size());
    fc.read(readBuffer);
    fis.close(); // also closes channel
    readBuffer.rewind();
    return readBuffer;
  }


  /**
   * New NIO based method to read a file as a String with the given
   * charset encoding.
   * @param aCharSet the charset to use for conversion, if null
   * ISO-8859-15 is used */
  public static String readFileAsString(File aFile, String aCharSet)
    throws FileNotFoundException, IOException, UnsupportedCharsetException {
    ByteBuffer buffer = readFile(aFile);
     if (null == aCharSet) {
       aCharSet = "ISO-8859-15";
     }
     String converted = new String(buffer.array(), aCharSet);
     return converted;
  }


  /**
   * This returns an input stream for the given resource file.
   *
   * @param resourceFileName a <code>String with the resource name
   * @return an <code>InputStream</code> where to read from the content of the
   * resource file
   * @throws FileNotFoundException if there is an error when reading the
   * resource
   */
  public static InputStream openResourceFileAsStream(String resourceFileName)
    throws FileNotFoundException {

    InputStream is = ClassLoader.getSystemResourceAsStream(resourceFileName);
    if (null == is) {
      // try local loader with absolute path
      is = FileTools.class.getResourceAsStream("/" + resourceFileName);
      if (null == is) {
        // try local loader, relative, just in case
        is = FileTools.class.getResourceAsStream(resourceFileName);
        if (null == is) {
          // can't find it on classpath, so try relative to current directory
          // this will throw security exception under and applet but there's
          // no other choice left
          is = new FileInputStream(resourceFileName);
        }
      }
    }

    return is;
  }
}


