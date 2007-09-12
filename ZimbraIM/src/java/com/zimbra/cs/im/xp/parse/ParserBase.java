package com.zimbra.cs.im.xp.parse;

import java.util.Locale;

/**
 *
 * @version $Revision: 1.1 $ $Date: 1998/06/25 10:52:26 $
 */
public class ParserBase {
  protected EntityManager entityManager = new EntityManagerImpl();
  protected Locale locale = Locale.getDefault();

  public void setEntityManager(EntityManager entityManager) {
    if (entityManager == null)
      throw new NullPointerException();
    this.entityManager = entityManager;
  }

  public void setLocale(Locale locale) {
    if (locale == null)
      throw new NullPointerException();
    this.locale = locale;
  }
}
