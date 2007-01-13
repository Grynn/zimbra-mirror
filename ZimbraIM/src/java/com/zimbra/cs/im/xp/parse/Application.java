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
 * This interface is used by the parser to report information to the
 * application.
 * In all cases the event argument is valid only until the function
 * returns;
 * the parser may reuse the event object to report subsequent events.
 *
 * @see Parser
 * @version $Revision: 1.2 $ $Date: 1998/06/10 09:43:55 $
 */
public interface Application {
  /**
   * Reports the start of the document.
   * This is called once per well-formed document before any other methods.
   */
  void startDocument() throws Exception;
  /**
   * Reports the end of the prolog.
   * Called before the start of the first element.
   */
  void endProlog(EndPrologEvent event) throws Exception;

  /**
   * Reports the start of an element.
   * This includes both start-tags and empty elements.
   */
  void startElement(StartElementEvent event) throws Exception;
  /**
   * Reports character data.
   */
  void characterData(CharacterDataEvent event) throws Exception;
  /**
   * Reports the end of a element.
   * This includes both end-tags and empty elements.
   */
  void endElement(EndElementEvent event) throws Exception;

  /**
   * Reports a processing instruction.
   * Note that processing instructions can occur before or after the
   * document element.
   */
  void processingInstruction(ProcessingInstructionEvent event) throws Exception;

  /**
   * Reports the end of the document.
   * Called once per well-formed document, after all other methods.
   * Not called if the document is not well-formed.
   */
  void endDocument() throws Exception;

  /**
   * Reports a comment.
   * Note that comments can occur before or after the
   * document element.
   */
  void comment(CommentEvent event) throws Exception;

  /**
   * Reports the start of a CDATA section.
   */
  void startCdataSection(StartCdataSectionEvent event) throws Exception;
  
  /**
   * Reports the end of a CDATA section.
   */
  void endCdataSection(EndCdataSectionEvent event) throws Exception;

  /**
   * Reports the start of an entity reference.
   * This event will be followed by the result of parsing
   * the entity's replacement text.
   * This is not called for entity references in attribute values.
   */
  void startEntityReference(StartEntityReferenceEvent event) throws Exception;

  /**
   * Reports the start of an entity reference.
   * This event follow's the result of parsing
   * the entity's replacement text.
   * This is not called for entity references in attribute values.
   */
  void endEntityReference(EndEntityReferenceEvent event) throws Exception;

  /**
   * Reports the start of the document type declaration.
   */
  void startDocumentTypeDeclaration(StartDocumentTypeDeclarationEvent event) throws Exception;

  /**
   * Reports the end of the document type declaration.
   */
  void endDocumentTypeDeclaration(EndDocumentTypeDeclarationEvent event) throws Exception;

  /**
   * Reports a markup declaration.
   */
  void markupDeclaration(MarkupDeclarationEvent event) throws Exception;
}
