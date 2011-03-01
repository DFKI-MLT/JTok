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

package de.dfki.lt.tools.tokenizer.regexp;

import java.util.List;

/**
 * <code>RegExp</code> defines an interface for regular expression
 * patterns.
 *
 * @author Joerg Steffen, DFKI
 * @version $Id: RegExp.java,v 1.4 2005-04-12 08:47:37 steffen Exp $ */
public interface RegExp {

  /**
   * This specifies a method signature that returns a
   * <code>List</code> with all {@link Match}es for the regular
   * expression in  <code>input</code>.
   *
   * @param input the <code>String</code> where to look for matches
   * @return a <code>List</code> of {@link Match}es */
  public List getAllMatches(String input);

  /**
   * This specifies a method signature that checks if the regular
   * expression matches the input in its entirety.
   *
   * @param input the <code>String</code> to check
   * @return a <code>boolean</code> */
  public boolean matches(String input);

  /**
   * This specifies a method signature that checks if the input
   * contains a match for the regular expression. If yes, a {@link
   * Match} is returned, <code>null</code> otherwise.
   *
   * @param input the <code>String</code> to check
   * @return a {@link Match} or <code>null</code> */
  public Match contains(String input);
}
