package com.zimbra.cs.im.xp.parse;

import java.util.Enumeration;

/**
 * Information about an element type.
 *
 * @see DTD#getElementType
 * @version $Revision: 1.7 $ $Date: 1998/06/25 04:41:33 $
 */

public interface ElementType {
  static byte UNDECLARED_CONTENT = -1;
  static byte ANY_CONTENT = 0;
  static byte MIXED_CONTENT = 1;
  static byte EMPTY_CONTENT = 2;
  static byte ELEMENT_CONTENT = 3;
  /**
   * Returns an integer corresponding to the content specified for
   * an element type.
   */
  byte getContentType();
  /**
   * Returns the <code>contentspec</code> for the element type;
   * the <code>contentspec</code> is the part of the element type declaration
   * following the element type name.
   * The <code>contentspec</code> will have parameter
   * entity references expanded and whitespace removed.
   * Returns null if the element type was not declared.
   */
  String getContentSpec();
  /**
   * Returns an enumeration over the names of attributes defined
   * for an element type.
   */
  Enumeration attributeNames();
  /**
   * Returns the definition of the specified attribute or
   * null if no such attribute is defined.
   */
  AttributeDefinition getAttributeDefinition(String name);
}
