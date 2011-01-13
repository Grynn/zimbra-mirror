package com.zimbra.qa.selenium.projects.desktop.ui;

import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.util.HarnessException;


public class AppDesktopClient extends AbsApplication {
   public PageMain zPageMain = null;
   public PageAccounts zPageAccounts = null;

   public AppDesktopClient() {
      super();
      logger.info("new " + AppDesktopClient.class.getCanonicalName());
      
      // Login page
      
      zPageMain = new PageMain(this);
      pages.put(zPageMain.myPageName(), zPageMain);
      
      // Main page
      zPageAccounts = new PageAccounts(this);
      pages.put(zPageAccounts.myPageName(), zPageAccounts);
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
