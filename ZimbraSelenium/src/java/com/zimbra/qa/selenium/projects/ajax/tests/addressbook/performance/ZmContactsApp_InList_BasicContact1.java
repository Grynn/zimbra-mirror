/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2012 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.performance;

import java.io.File;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.ContactItem;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.RestUtil;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
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

   @DataProvider(name = "DataProvider_LoadingApp_1Contact")
   public Object[][] DataProvideNewMessageShortcuts() {
     return new Object[][] {
           new Object[] { "Load (initial) the Address Book app, 1 contact in list"},
           new Object[] { "Load (from cache) the Address Book app, 1 contact in list"}
     };
   }
   @Test(description = "Measure the time to load address book page with 1 contact item",
         groups = {"performance"}, dataProvider = "DataProvider_LoadingApp_1Contact")
   public void ZmContactsApp_01(String logMessage) throws HarnessException {
      ContactItem.createContactItem(app.zGetActiveAccount());

      PerfToken token = PerfMetrics.startTimestamp(PerfKey.ZmContactsApp,
            logMessage);
      app.zPageAddressbook.zNavigateTo();

      PerfMetrics.waitTimestamp(token);

      // Wait for the app to load
      app.zPageAddressbook.zWaitForActive();
   }

   @Test(description = "Measure the time to load address book page with 100 contact items",
         groups = {"performance"})
   public void ZmContactsApp_02() throws HarnessException {

      // Loading csv file that has information for 100 contacts to speed up the setup
      String filename = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/csv/100contacts.csv";

      RestUtil rest = new RestUtil();
      rest.setAuthentication(app.zGetActiveAccount());
      rest.setPath("/service/home/~/Contacts");
      rest.setQueryParameter("fmt", "csv");
      rest.setUploadFile(new File(filename));
      rest.doPost();

      PerfToken token = PerfMetrics.startTimestamp(PerfKey.ZmContactsApp,
            "Load the Address Book app, 100 contacts in list");
      app.zPageAddressbook.zNavigateTo();

      PerfMetrics.waitTimestamp(token);

      // Wait for the app to load
      app.zPageAddressbook.zWaitForActive();
   }
}
