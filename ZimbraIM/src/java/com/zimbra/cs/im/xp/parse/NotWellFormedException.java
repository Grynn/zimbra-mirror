package com.zimbra.cs.im.xp.parse;

import java.net.URL;

/**
 * Thrown when an XML document is not well-formed.
 * @version $Revision: 1.5 $ $Date: 1998/05/25 03:39:22 $
 */
public class NotWellFormedException extends java.io.IOException implements ParseLocation {
  private String messageWithoutLocation;
  private String entityLocation;
  private URL entityBase;
  private int lineNumber;
  private int columnNumber;
  private long byteIndex;

  NotWellFormedException(String message,
			 String messageWithoutLocation,
			 String entityLocation,
			 URL entityBase,
			 int lineNumber,
			 int columnNumber,
			 long byteIndex) {
    super(message);
    this.messageWithoutLocation = messageWithoutLocation;
    this.entityLocation = entityLocation;
    this.entityBase = entityBase;
    this.lineNumber = lineNumber;
    this.columnNumber = columnNumber;
    this.byteIndex = byteIndex;
  }
  /**
   * Returns the location of the external entity where the
   * the error occurred in a form suitable for use in an error message.
   * This is typically a URI or a filename.
   */
  public final String getEntityLocation() {
    return entityLocation;
  }

  /**
   * Returns the URL used as the base URL for resolving relative URLs
   * contained in the entity where the error occurred.
   */
  public final URL getEntityBase() {
    return entityBase;
  }

  /**
   * Returns the line number where the error occured.
   * The first line has number 1.
   */
  public final int getLineNumber() {
    return lineNumber;
  }

  /**
   * Returns the column number where the error occurred.
   * The first column has number 0.
   */
  public final int getColumnNumber() {
    return columnNumber;
  }

  /**
   * Returns the index of the byte in the entity where the error occurred.
   * The first byte has offset 0.
   */
  public final long getByteIndex() {
    return byteIndex;
  }

  /**
   * Returns a description of the error that does not include
   * the location of the error.
   * <code>getMessage</code> returns a description of the error
   * that does include the location.
   */
  public final String getMessageWithoutLocation() {
    return messageWithoutLocation;
  }
}
