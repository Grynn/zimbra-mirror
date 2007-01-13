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
 * An XML declaration.
 * @version $Revision: 1.3 $ $Date: 1998/02/17 04:24:35 $
 */
public class XmlDecl extends TextDecl {
  private boolean standalone;

  /**
   * Returns true if the XML declaration specified
   * <code>standalone="yes"</code>.
   */
  public boolean isStandalone() {
    return standalone;
  }
  
  /**
   * Creates an <code>XMLDecl</code> from the specified byte subarray.
   * The specified encoding is used to convert bytes to characters.
   * The byte subarray should be a <code>TOK_XML_DECL</code> token
   * returned from Encoding.tokenizeProlog or Encoding.tokenizeContent,
   * starting with <code>&lt;?</code> and ending with <code>?&gt;</code>.
   * @exception InvalidTokenException if the specified byte subarray
   * is not a legal XML declaration.
   */
  public XmlDecl(Encoding enc, byte[] buf, int off, int end)
       throws InvalidTokenException {
    standalone = init(true, enc, buf, off, end);
  }
      
}
