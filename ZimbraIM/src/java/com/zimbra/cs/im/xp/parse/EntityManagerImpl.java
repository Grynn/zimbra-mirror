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

import java.net.URL;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * A default implementation of <code>EntityManager</code>.
 * @see EntityManager
 * @version $Revision: 1.4 $ $Date: 1998/02/17 04:20:35 $
 */
public class EntityManagerImpl implements EntityManager {
  public OpenEntity open(String systemId, URL baseURL, String publicId)
       throws IOException {
    URL useURL = new URL(baseURL, systemId);
    return new OpenEntity(useURL.openStream(),
			  useURL.toString(),
			  useURL,
			  null);
  }

  /**
   * Creates an OpenEntity from a file name.
   */
  static public OpenEntity openFile(String name) throws IOException {
    File file = new File(name);
    return new OpenEntity(new FileInputStream(file),
			  name,
			  fileToURL(file),
			  null);
  }

  /**
   * Creates an OpenEntity for the standard input.
   */
  static public OpenEntity openStandardInput() throws IOException {
    return new OpenEntity(new FileInputStream(FileDescriptor.in),
			  "<standard input>",
			  userDirURL(),
			  null);
  }

  /**
   * Generates a <code>URL</code> from a <code>File</code>.
   */
  static public URL fileToURL(File file) {
    String path = file.getAbsolutePath();
    String fSep = System.getProperty("file.separator");
    if (fSep != null && fSep.length() == 1)
      path = path.replace(fSep.charAt(0), '/');
    if (path.length() > 0 && path.charAt(0) != '/')
      path = '/' + path;
    try {
      return new URL("file", null, path);
    }
    catch (java.net.MalformedURLException e) {
      /* According to the spec this could only happen if the file
	 protocol were not recognized. */
      throw new Error("unexpected MalformedURLException");
    }
  }
  
  /**
   * Generates a URL for the current working directory.
   */
  static public URL userDirURL() {
    return fileToURL(new File(System.getProperty("user.dir")
                              + System.getProperty("file.separator")));
  }

}

