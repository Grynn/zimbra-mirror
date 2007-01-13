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

import java.util.Enumeration;

/**
 * Information about the definition of an Attribute.
 *
 * @see ElementType#getAttributeDefinition
 * @version $Revision: 1.1 $ $Date: 1998/06/25 04:41:53 $
 */

public interface AttributeDefinition {
  /**
   * Returns the normalized default value
   * or null if no default value was specified.
   */
  String getDefaultValue();

  /**
   * Returns the unnormalized default value
   * or null if no default value was specified.
   */
  String getDefaultUnnormalizedValue();

  /**
   * Returns true if the attribute was #REQUIRED or #FIXED.
   */
  boolean isRequired();

  static byte UNDECLARED = -1;
  static byte CDATA = 0;
  static byte ID = 1;
  static byte IDREF = 2;
  static byte IDREFS = 3;
  static byte ENTITY = 4;
  static byte ENTITIES = 5; 
  static byte NMTOKEN = 6;
  static byte NMTOKENS = 7;
  static byte ENUM = 8;
  static byte NOTATION = 9;
  /**
   * Returns an integer corresponding to the type of the attribute.
   */
  byte getType();

  /**
   * Returns an enumeration over the allowed values
   * if this was declared as an enumerated type, and null otherwise.
   */
  Enumeration allowedValues();
}
