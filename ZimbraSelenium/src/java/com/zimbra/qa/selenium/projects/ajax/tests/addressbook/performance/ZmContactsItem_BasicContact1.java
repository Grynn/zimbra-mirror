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

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.ContactItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.performance.PerfKey;
import com.zimbra.qa.selenium.framework.util.performance.PerfMetrics;
import com.zimbra.qa.selenium.framework.util.performance.PerfToken;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;

public class ZmContactsItem_BasicContact1 extends AjaxCommonTest{

   public ZmContactsItem_BasicContact1() {
      logger.info("New " + ZmContactsItem_BasicContact1.class.getCanonicalName());

      // All tests start at the login page
      super.startingPage = app.zPageAddressbook;

      // Make sure we are using an account with message view
      super.startingAccountPreferences = null;
   }

   @Test(description="Measure the time to view Basic contact item",
         groups={"performance"})
   public void ZmContactsItem_01() throws HarnessException {
      // Create 2 contacts via Soap because by default the first one will be selected
      // therefore measuring the performance of loading the second one
      ContactItem.createContactItem(app.zGetActiveAccount());
      ContactItem contactItem = ContactItem.createContactItem(app.zGetActiveAccount());

      FolderItem contactFolder = FolderItem.importFromSOAP(
            app.zGetActiveAccount(), "Contacts");

      // Refresh the contact list
      app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, contactFolder);

      PerfToken token = PerfMetrics.startTimestamp(PerfKey.ZmContactsItem,
            "Load the basic contact view");

      // Select the contact
   //  app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, contactItem.fileAs);;       
      app.zPageAddressbook.zClickAt("css=div[id='zv__CNS-main'] li[id^='zli__CNS-main'] div[id$='__fileas']:contains('"+contactItem.fileAs+"')","");
      PerfMetrics.waitTimestamp(token);

   }
}
