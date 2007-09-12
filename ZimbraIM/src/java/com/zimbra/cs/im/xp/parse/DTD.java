package com.zimbra.cs.im.xp.parse;

import java.util.Enumeration;

/**
 * Information about a DTD.
 * @version $Revision: 1.6 $ $Date: 1998/06/25 04:41:33 $
 */
public interface DTD {
  /**
   * Returns the document type name or null if there was no DOCTYPE
   * declaration.
   */
  String getDocumentTypeName();
  
  /**
   * Indicates a general entity.
   *
   * @see #getEntity
   * @see #entityNames
   */
  static byte GENERAL_ENTITY = 0;
  /**
   * Indicates a parameter entity.
   *
   * @see #getEntity
   * @see #entityNames
   */
  static byte PARAMETER_ENTITY = 1;
  /**
   * Indicates an entity declared with a NOTATION declaration.
   *
   * @see #getEntity
   * @see #entityNames
   */
  static byte NOTATION = 2;

  /**
   * Returns information about the entity with the specified name
   * and type or null if there was no such entity declared.
   * @see #GENERAL_ENTITY
   * @see #PARAMETER_ENTITY
   * @see #NOTATION
   */
  Entity getEntity(byte entityType, String name);

  /**
   * Returns an enumeration over the names of entities of the
   * specified type.
   *
   * @see #GENERAL_ENTITY
   * @see #PARAMETER_ENTITY
   * @see #NOTATION
   */
  Enumeration entityNames(byte entityType);

  /**
   * The external subset declared in the document type declaration
   * is modelled as a parameter entity with this name.
   * This will not be included in the names enumerated by
   * <code>entityNames</code>.
   * If there is an external subset then its contents will
   * be preceded by a StartEntityReferenceEvent with this name,
   * and followed by an EndEntityReferenceEvent.
   */
  static String EXTERNAL_SUBSET_NAME ="#DOCTYPE";

  /**
   * Returns information about the element type with the specified name,
   * or null if there was neither an ELEMENT nor an ATTLIST declaration.
   */
  ElementType getElementType(String name);

  /**
   * Returns an enumeration over the names of element types which were
   * declared in the DTD or for which attributes were declared in the DTD.
   */
  Enumeration elementTypeNames();

  /**
   * Returns true if the complete DTD was processed.
   */
  boolean isComplete();

  /**
   * Returns true if <code>standalone="yes"</code> was specified in the
   * XML declaration.
   */
  boolean isStandalone();
}
