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
