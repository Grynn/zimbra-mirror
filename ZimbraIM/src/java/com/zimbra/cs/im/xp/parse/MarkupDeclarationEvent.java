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

/**
 * Information about a markup declaration.
 * @see com.zimbra.cs.im.xp.parse.Application#markupDeclaration
 * @version $Revision: 1.2 $ $Date: 1998/06/25 04:41:34 $
 */
public interface MarkupDeclarationEvent {
  static int ATTRIBUTE = 0;
  static int ELEMENT = 1;
  static int GENERAL_ENTITY = 2;
  static int PARAMETER_ENTITY = 3;
  static int NOTATION = 4;
  int getType();
  String getName();
  String getAttributeName();
  DTD getDTD();
}
