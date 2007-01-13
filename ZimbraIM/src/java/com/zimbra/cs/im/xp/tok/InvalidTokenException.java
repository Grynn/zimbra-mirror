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
 * Thrown to indicate that the byte subarray being tokenized does not start
 * with a legal XML token and cannot start one if more bytes are added.
 * @version $Revision: 1.3 $ $Date: 1998/02/17 04:24:08 $
 */
public class InvalidTokenException extends TokenException {
  private /* final */ int offset;

  /**
   * The character or byte at the specified offset is not allowed
   * at that point.
   */
  public static final byte ILLEGAL_CHAR = 0;
  /**
   * The target of a processing instruction was XML.
   */
  public static final byte XML_TARGET = 1;
  /**
   * A duplicate attribute was specified.
   */
  public static final byte DUPLICATE_ATTRIBUTE = 2;

  private /* final */ byte type;
  
  InvalidTokenException(int offset, byte type) {
    this.offset = offset;
    this.type = type;
  }

  InvalidTokenException(int offset) {
    this.offset = offset;
    this.type = ILLEGAL_CHAR;
  }

  /**
   * Returns the offset after the longest initial subarray
   * which could start a legal XML token.
   */
  public final int getOffset() {
    return offset;
  }
  public final byte getType() {
    return type;
  }
}
