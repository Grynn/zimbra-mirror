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
import java.io.IOException;

/**
 * This interface is used by the parser to access external entities.
 * @see Parser
 * @version $Revision: 1.4 $ $Date: 1998/02/17 04:20:32 $
 */
public interface EntityManager {
  /**
   * Opens an external entity.
   * @param systemId the system identifier specified in the entity declaration
   * @param baseURL the base URL relative to which the system identifier
   * should be resolved; null if no base URL is available
   * @param publicId the public identifier specified in the entity declaration;
   * null if no public identifier was specified
   */
  OpenEntity open(String systemId, URL baseURL, String publicId) throws IOException;
}
