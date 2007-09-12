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
