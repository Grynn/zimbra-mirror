package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.performance;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.ContactItem;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.performance.PerfKey;
import com.zimbra.qa.selenium.framework.util.performance.PerfMetrics;
import com.zimbra.qa.selenium.framework.util.performance.PerfToken;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;

public class ZmContactsApp_InList_BasicContact1 extends AjaxCommonTest {

   public ZmContactsApp_InList_BasicContact1() {
      logger.info("New " + ZmContactsApp_InList_BasicContact1.class.getCanonicalName());

      // All tests start at the login page
      super.startingPage = app.zPageMail;

      // Make sure we are using an account with message view
      super.startingAccountPreferences = null;
   }

   @Test(description="Measure the time to load address book page with 1 contact item",
         groups={"performance"})
   public void ZmContactsApp_01() throws HarnessException {
      ContactItem.createUsingSOAP(app);

      PerfToken token = PerfMetrics.startTimestamp(PerfKey.ZmContactsApp,
            "Load the Address Book app, 1 contact in list");
      app.zPageAddressbook.zNavigateTo();

      PerfMetrics.waitTimestamp(token);

      // Wait for the app to load
      app.zPageAddressbook.zWaitForActive();
   }

   @Test(description="Measure the time to load address book page with 100 contact items",
         groups={"performance"})
   public void ZmContactsApp_02() throws HarnessException {
      //Create 100 contact items
      for (int i = 0; i < 100; i++) {
         ContactItem.createUsingSOAP(app);
      }

      PerfToken token = PerfMetrics.startTimestamp(PerfKey.ZmContactsApp,
            "Load the Address Book app, 100 contacts in list");
      app.zPageAddressbook.zNavigateTo();

      PerfMetrics.waitTimestamp(token);

      // Wait for the app to load
      app.zPageAddressbook.zWaitForActive();
   }
}
