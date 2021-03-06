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

package de.dfki.lt.tools.tokenizer.exceptions;

/**
 * {@link ProcessingException} is thrown when the processing of input data causes an error.
 *
 * @author Joerg Steffen, DFKI
 */
public class ProcessingException extends RuntimeException {

  /**
   * Creates a new instance of {@link ProcessingException} with null as its detail message. The
   * cause is not initialized.
   */
  public ProcessingException() {

    super();
  }


  /**
   * Creates a new instance of {@link ProcessingException} with the given detail message. The cause
   * is not initialized.
   *
   * @param message
   *          the detail message
   */
  public ProcessingException(String message) {

    super(message);
  }


  /**
   * Creates a new instance of {@link ProcessingException} with the specified cause and a detail
   * message of (cause==null ? null : cause.toString()) (which typically contains the class and
   * detail message of cause).
   *
   * @param cause
   *          a throwable with the cause of the exception (which is saved for later retrieval by the
   *          {@link #getCause()} method). (A {@code null} value is permitted, and indicates that
   *          the cause is nonexistent or unknown.)
   */
  public ProcessingException(Throwable cause) {

    super(cause);
  }


  /**
   * Creates a new instance of {@link ProcessingException} with the given detail message and the
   * given cause.
   *
   * @param message
   *          the detail message
   * @param cause
   *          a throwable with the cause of the exception (which is saved for later retrieval by the
   *          {@link #getCause()} method). (A {@code null} value is permitted, and indicates that
   *          the cause is nonexistent or unknown.)
   */
  public ProcessingException(String message, Throwable cause) {

    super(message, cause);
  }
}
