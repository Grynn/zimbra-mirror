package com.zimbra.cs.im.xp.parse;

import java.net.URL;

/**
 * Information about an entity or notation.
 *
 * @see DTD#getEntity
 * @version $Revision: 1.1 $ $Date: 1998/06/25 04:41:53 $
 */

public interface Entity {
  /**
   * Returns the system identifier, or null if no system identifier
   * was specified.
   * A relative URL is not automatically resolved into an absolute URL;
   * <code>getBase</code> can be used to do this.
   *
   * @see #getBase
   */
  String getSystemId();
  /**
   * Returns the URL that should be used for resolving the system identifier
   * if the system identifier is relative.
   * Returns null if no URL is available.
   */
  URL getBase();
  /**
   * Returns the public identifier, or null if no public identifier
   * was specified.
   */
  String getPublicId();
  /**
   * Returns the replacement text or null if this is not an internal
   * entity.
   */
  String getReplacementText();
  /**
   * Returns the notation name of the entity, of null if this is
   * not an unparsed entity.
   */
  String getNotationName();
}
