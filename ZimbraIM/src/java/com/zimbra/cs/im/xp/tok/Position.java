/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * 
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Server.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005, 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s): 
 * 
 * ***** END LICENSE BLOCK *****
 */
 
package com.zimbra.cs.im.xp.tok;

/**
 * Represents a position in an entity.
 * A position can be modified by <code>Encoding.movePosition</code>.
 * @see Encoding#movePosition
 * @version $Revision: 1.2 $ $Date: 1998/02/17 04:24:15 $
 */
public final class Position implements Cloneable {
  int lineNumber;
  int columnNumber;

  /**
   * Creates a position for the start of an entity: the line number is
   * 1 and the column number is 0.
   */
  public Position() {
    lineNumber = 1;
    columnNumber = 0;
  }

  /**
   * Returns the line number.
   * The first line number is 1.
   */
  public int getLineNumber() {
    return lineNumber;
  }

  /**
   * Returns the column number.
   * The first column number is 0.
   * A tab character is not treated specially.
   */
  public int getColumnNumber() {
    return columnNumber;
  }

  /**
   * Returns a copy of this position.
   */
  public Object clone() {
    try {
      return super.clone();
    }
    catch (CloneNotSupportedException e) {
      throw new InternalError();
    }
  }
}
