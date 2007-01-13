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
 * Information about the start of an element.
 * @see com.zimbra.cs.im.xp.parse.Application#startElement
 * @version $Revision: 1.9 $ $Date: 1998/12/28 08:12:30 $
 */
public interface StartElementEvent extends LocatedEvent {
  /**
   * Returns the element type name.
   */
  String getName();

  /**
   * Returns the number of attributes.
   * Both specified and defaulted attributes are included.
   * Implied attributes are not included.
   */
  int getAttributeCount();
  
  /**
   * Returns the name of the attribute with index <code>i</code>.
   * <code>i</code> must be greater than or equal to 0
   * and less that the number of attributes returned
   * by <code>getAttributeCount</code>.
   */
  String getAttributeName(int i);

  /**
   * Returns the value of the attribute with index <code>i</code>.
   * <code>i</code> must be greater than or equal to 0
   * and less that the number of attributes returned
   * by <code>getAttributeCount</code>.
   * The value does not include the surrounding quotes.
   */
  String getAttributeValue(int i);

  /**
   * Returns the value of the attribute with the specified name,
   * Returns null if there is no such attribute, or if the
   * value of the attribute was implied.
   */
  String getAttributeValue(String name);

  /**
   * Returns the number of attributes which were specified.
   * The specified attributes have indices less than the
   * defaulted attributes.
   */
  int getAttributeSpecifiedCount();

  /**
   * Returns the value of the specified attribute with index <code>i</code>
   * before normalization.
   */
  String getAttributeUnnormalizedValue(int i);

  /**
   * Returns the index of the ID attribute, or -1 if there is no ID
   * attribute.
   */
  int getIdAttributeIndex();
}
