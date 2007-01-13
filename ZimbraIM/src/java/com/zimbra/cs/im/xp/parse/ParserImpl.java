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

import java.io.IOException;

/**
 *
 * @version $Revision: 1.1 $ $Date: 1998/05/08 06:38:14 $
 */
public class ParserImpl extends ParserBase implements Parser {
  private Application application = new ApplicationImpl();
  
  public void setApplication(Application application) {
    if (application == null)
      throw new NullPointerException();
    this.application = application;
  }

  /**
   * Parses an XML document.
   * If no <code>EntityManager</code> has been specified with
   * <code>setEntityManager</code>, then <code>EntityManagerImpl</code>
   * will be used.
   *
   * @param entity the document entity of the XML document
   * @exception NotWellFormedException if the document is not well-formed
   * @exception IOException if an IO error occurs
   * @see EntityManagerImpl
   */
  public void parseDocument(OpenEntity entity) throws ApplicationException, IOException {
    DocumentParser.parse(entity, entityManager, application, locale);
  }
}
