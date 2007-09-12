package com.zimbra.cs.im.xp.parse;

/**
 * Information about a markup declaration.
 * @see com.zimbra.cs.im.xp.parse.Application#markupDeclaration
 * @version $Revision: 1.2 $ $Date: 1998/06/25 04:41:34 $
 */
public interface MarkupDeclarationEvent {
  static int ATTRIBUTE = 0;
  static int ELEMENT = 1;
  static int GENERAL_ENTITY = 2;
  static int PARAMETER_ENTITY = 3;
  static int NOTATION = 4;
  int getType();
  String getName();
  String getAttributeName();
  DTD getDTD();
}
