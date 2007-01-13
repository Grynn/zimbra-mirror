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
 * Represents information returned by the tokenizing methods in
 * <code>Encoding</code>.
 *
 * @see Encoding#tokenizeContent
 * @see Encoding#tokenizeProlog
 * @see Encoding#tokenizeAttributeValue
 * @see Encoding#tokenizeEntityValue
 * @version $Revision: 1.4 $ $Date: 1998/06/16 03:56:35 $
 */
public class Token {
  int tokenEnd = -1;
  int nameEnd = -1;
  char refChar1 = 0;
  char refChar2 = 0;

  public final int getTokenEnd() {
    return tokenEnd;
  }

  protected final void setTokenEnd(int i) {
    tokenEnd = i;
  }

  public final int getNameEnd() {
    return nameEnd;
  }

  protected final void setNameEnd(int i) {
    nameEnd = i;
  }

  public final char getRefChar() {
    return refChar1;
  }

  public final void getRefCharPair(char[] ch, int off) {
    ch[off] = refChar1;
    ch[off + 1] = refChar2;
  }
}
