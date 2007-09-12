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
