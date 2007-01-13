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
 
package com.zimbra.cs.im.xp.parse;

import java.net.URL;

/**
 * Information about the location of the parse of an XML document.
 * @version $Revision: 1.1 $ $Date: 1998/05/25 03:40:30 $
 */
public interface ParseLocation {

  /**
   * Returns the location of the external entity being
   * parsed in a form suitable for use in a message.
   * Returns null if no location is available.
   * This is typically a URI or a filename.
   */
  String getEntityLocation();

  /**
   * Returns the URL to use as the base URL for resolving relative URLs
   * contained in the entity being parsed.
   */
  public URL getEntityBase();

  /**
   * Returns the line number of the character being parsed
   * or -1 if no line number is available.
   * The number of the first line is 1.
   */
  int getLineNumber();

  /**
   * Returns the column number of the character being parsed
   * or -1 if no column number is available.
   * The number of the first column in a line is 0.
   * A tab character is not treated specially.
   */
  int getColumnNumber();

  /**
   * Returns the byte index of the first byte of the character being parsed
   * or -1 if no byte index is available.
   * The index of the first byte is 0.
   */
  public long getByteIndex();

}
