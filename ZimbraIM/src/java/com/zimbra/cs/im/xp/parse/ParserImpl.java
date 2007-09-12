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
