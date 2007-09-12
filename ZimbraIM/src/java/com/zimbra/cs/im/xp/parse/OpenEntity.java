package com.zimbra.cs.im.xp.parse;

import java.io.InputStream;
import java.net.URL;

/**
 * Information about an open external entity.
 * This is used to by <code>EntityManager</code> to return
 * information about an external entity that is has opened.
 * @see EntityManager
 * @version $Revision: 1.4 $ $Date: 1998/02/17 04:20:47 $
 */
public class OpenEntity {
  private InputStream inputStream;
  private String encoding;
  private URL base;
  private String location;

  /**
   * Creates and initializes an <code>OpenEntity</code> which uses
   * an externally specified encoding.
   */
  public OpenEntity(InputStream inputStream, String location, URL base, String encoding) {
    this.inputStream = inputStream;
    this.location = location;
    this.base = base;
    this.encoding = encoding;
  }

  /**
   * Creates and initializes an <code>OpenEntity</code> which uses
   * the encoding specified in the entity.
   */
  public OpenEntity(InputStream inputStream, String location, URL base) {
    this(inputStream, location, base, null);
  }

  /**
   * Returns an InputStream containing the entity's bytes.
   * If this is called more than once on the same
   * OpenEntity, it will return the same InputStream.
   */
  public final InputStream getInputStream() {
    return inputStream;
  }

  /**
   * Returns the name of the encoding to be used to convert the entity's
   * bytes into characters, or null if this should be determined from
   * the entity itself using XML's rules.
   */
  public final String getEncoding() {
    return encoding;
  }

  /**
   * Returns the URL to use as the base URL for resolving relative URLs
   * contained in the entity.
   */
  public final URL getBase() {
    return base;
  }

  /**
   * Returns a string representation of the location of the entity
   * suitable for use in error messages.
   */
  public final String getLocation() {
    return location;
  }

}
