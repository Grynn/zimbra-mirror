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
 * Thrown to indicate that the byte subarray being tokenized is a legal XML
 * token, but that subsequent bytes in the same entity could be part of
 * the token.  For example, <code>Encoding.tokenizeProlog</code>
 * would throw this if the byte subarray consists of a legal XML name.
 * @version $Revision: 1.3 $ $Date: 1998/02/17 04:24:06 $
 */
public class ExtensibleTokenException extends TokenException {
  private int tokType;

  ExtensibleTokenException(int tokType) {
    this.tokType = tokType;
  }

  /**
   * Returns the type of token in the byte subarrary.
   */
  public int getTokenType() {
    return tokType;
  }
}
