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


/**
 * A default implementation of <code>Application</code>.
 * All methods do nothing.
 * @version $Revision: 1.2 $ $Date: 1998/06/10 09:43:55 $
 */
public class ApplicationImpl implements Application {
  public void startDocument() throws Exception { }
  public void endDocument() throws Exception { }
  public void startElement(StartElementEvent event) throws Exception { }
  public void characterData(CharacterDataEvent event) throws Exception { }
  public void endElement(EndElementEvent event) throws Exception { }
  public void processingInstruction(ProcessingInstructionEvent event) throws Exception { }
  public void endProlog(EndPrologEvent event) throws Exception { }
  public void comment(CommentEvent event) throws Exception { }
  public void startCdataSection(StartCdataSectionEvent event) throws Exception { }
  public void endCdataSection(EndCdataSectionEvent event) throws Exception { }
  public void startEntityReference(StartEntityReferenceEvent event) throws Exception { }
  public void endEntityReference(EndEntityReferenceEvent event) throws Exception { }
  public void startDocumentTypeDeclaration(StartDocumentTypeDeclarationEvent event) throws Exception { }
  public void endDocumentTypeDeclaration(EndDocumentTypeDeclarationEvent event) throws Exception { }
  public void markupDeclaration(MarkupDeclarationEvent event) throws Exception { }
}
