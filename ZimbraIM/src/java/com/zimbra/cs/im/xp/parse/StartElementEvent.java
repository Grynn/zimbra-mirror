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
