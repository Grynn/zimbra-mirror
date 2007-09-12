package com.zimbra.cs.im.xp.parse;

/**
 * Information about the start of a document type declaration.
 * @see com.zimbra.cs.im.xp.parse.Application#startDocumentTypeDeclaration
 * @version $Revision: 1.1 $ $Date: 1998/06/10 09:45:12 $
 */
public interface StartDocumentTypeDeclarationEvent {
  /**
   * Returns the DTD being declared.
   */
  DTD getDTD();
}
