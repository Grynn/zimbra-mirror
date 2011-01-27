package com.zimbra.qa.selenium.projects.desktop.ui;

import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.projects.desktop.ui.mail.PageMail;
import com.zimbra.qa.selenium.projects.desktop.ui.mail.TreeMail;


public class AppDesktopClient extends AbsApplication {
   public PageMain zPageMain = null;
   public PageAccounts zPageAccounts = null;
   public PageMail zPageMail = null;
   public TreeMail zTreeMail = null;

   public AppDesktopClient(ZimbraAccount account) throws HarnessException {
      super();
      logger.info("new " + AppDesktopClient.class.getCanonicalName());

      zSetActiveAcount(account);

      // Main page
      zPageMain = new PageMain(this);
      pages.put(zPageMain.myPageName(), zPageMain);
      
      // Accounts page
      zPageAccounts = new PageAccounts(this);
      pages.put(zPageAccounts.myPageName(), zPageAccounts);

      // Mail apge
      zPageMail = new PageMail(this);
      pages.put(zPageMail.myPageName(), zPageMail);

      // Tree Mail
      zTreeMail = new TreeMail(this);
      trees.put(zTreeMail.myPageName(), zTreeMail);
   }

   @Override
   public String myApplicationName() {
      return "Desktop";
   }

   @Override
   public boolean zIsLoaded() throws HarnessException {
      // Verifying if the main and account status pages are active
      if (this.zPageMain.zIsActive() ||
            this.zPageAccounts.zIsActive()) {
         return true;
      } else {
         return false;
      }
   }
}
