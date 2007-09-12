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
