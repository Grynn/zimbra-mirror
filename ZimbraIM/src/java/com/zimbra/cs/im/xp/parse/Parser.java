package com.zimbra.cs.im.xp.parse;

import java.io.IOException;
import java.util.Locale;

/**
 * An XML Parser.
 * @see Application
 * @see EntityManager
 * @version $Revision: 1.1 $ $Date: 1998/05/08 06:38:15 $
 */
public interface Parser {

  /**
   * Sets the <code>EntityManager</code> which will be used
   * to access external entities referenced in the document.
   */
  void setEntityManager(EntityManager entityManager);

  /**
   * Sets the <code>Application</code>, which will receive information
   * about the XML document.
   */
  void setApplication(Application application);

  /**
   * Sets the locale to be used for error messages.
   */
  void setLocale(Locale locale);

  /**
   * Parses an XML document.  The current <code>EntityManager</code>,
   * <code>Application</code> and <code>Locale</code> will be used
   * for the entire parse. If no <code>EntityManager</code> has been
   * set, a default <code>EntityManager</code> will be used.
   * If no <code>Locale</code> has been set, the default <code>Locale</code>
   * as returned by <code>Locale.getDefault</code> will be used.
   * If no <code>Application</code> has been set, no information about
   * the document will be reported, but an exception will be thrown if
   * the document is not well-formed.
   *
   * @param entity the document entity of the XML document; the InputStream
   * of the document entity will be closed after parsing
   * @exception NotWellFormedException if the document is not well-formed
   * @exception IOException if an IO error occurs
   * @exception ApplicationException if any of the <code>Application</code>
   * methows throw an Exception
   */
  void parseDocument(OpenEntity entity) throws ApplicationException, IOException;
}
