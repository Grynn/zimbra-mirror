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
 * Thrown to indicate that the subarray being tokenized is not the
 * complete encoding of one or more characters, but might be if
 * more bytes were added.
 * @version $Revision: 1.2 $ $Date: 1998/02/17 04:24:11 $
 */
public class PartialCharException extends PartialTokenException {
  private int leadByteIndex;
  PartialCharException(int leadByteIndex) {
    this.leadByteIndex = leadByteIndex;
  }
  /**
   * Returns the index of the first byte that is not part of the complete
   * encoding of a character.
   */
  public int getLeadByteIndex() {
    return leadByteIndex;
  }
}
