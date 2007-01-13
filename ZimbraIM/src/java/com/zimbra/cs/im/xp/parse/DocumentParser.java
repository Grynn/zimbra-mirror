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
import java.util.Locale;

/**
 * A parser for XML documents.
 * @version $Revision: 1.1 $ $Date: 1998/05/08 06:37:11 $
 */
public class DocumentParser {
  /**
   * Parses an XML document.
   *
   * @param entity the document entity of the XML document; the InputStream
   * of the document entity will be closed after parsing
   * @param entityManager the EntityManager to be used to access external
   * entities referenced in the document
   * @param application the Application which will receive information
   * about the XML document
   * @param locale the Locale to be used for error messages
   *
   * @exception NotWellFormedException if the document is not well-formed
   * @exception IOException if an IO error occurs
   * @exception ApplicationException if any of the <code>Application</code>
   * methods throw an Exception
   */
  public static void parse(OpenEntity entity,
			   EntityManager entityManager,
			   Application application,
			   Locale locale)
   throws ApplicationException, IOException {
     new EntityParser(entity, entityManager, application, locale, null)
         .parseDocumentEntity();
  }
}
