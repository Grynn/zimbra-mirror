/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.projects.ajax.tests.tasks.performance;

import java.io.File;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.RestUtil;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.framework.util.performance.PerfKey;
import com.zimbra.qa.selenium.framework.util.performance.PerfMetrics;
import com.zimbra.qa.selenium.framework.util.performance.PerfToken;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;

public class ZmTasksApp_InList_Task1 extends AjaxCommonTest {

   public ZmTasksApp_InList_Task1() {
      logger.info("New " + ZmTasksApp_InList_Task1.class.getCanonicalName());

      // All tests start at the login page
      super.startingPage = app.zPageMail;

      // Make sure we are using an account with message view
      super.startingAccountPreferences = null;
   }

   @DataProvider(name = "DataProvider_LoadingApp_1Task")
   public Object[][] DataProvideNewMessageShortcuts() {
     return new Object[][] {
           new Object[] { "Load (initial) the Tasks app, 1 task in list"},
           new Object[] { "Load (from cache) the Tasks app, 1 task in list"}
     };
   }

   @Test(description = "Measure the time to load Tasks page with 1 task",
         groups = {"performance"}, dataProvider = "DataProvider_LoadingApp_1Task")
   public void ZmTasksApp_01(String logMessage) throws HarnessException {

      String subject = "task"+ ZimbraSeleniumProperties.getUniqueString();

      app.zGetActiveAccount().soapSend(
            "<CreateTaskRequest xmlns='urn:zimbraMail'>" +
            "<m >" +
            "<inv>" +
            "<comp name='"+ subject +"'>" +
            "<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
            "</comp>" +
            "</inv>" +
            "<su>"+ subject +"</su>" +
            "<mp ct='text/plain'>" +
            "<content>content"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
            "</mp>" +
            "</m>" +
      "</CreateTaskRequest>");

      PerfToken token = PerfMetrics.startTimestamp(PerfKey.ZmTasksApp,
            logMessage);

     // app.zPageTasks.zNavigateTo();//id=zb__App__Tasks_title
      app.zPageAddressbook.zClickAt("css=td[id='zb__App__Tasks_title']","");

      PerfMetrics.waitTimestamp(token);

      // Wait for the app to load
      app.zPageTasks.zWaitForActive();
   }

   @Test(description="Measure the time to load Tasks page with 100 tasks",
         groups={"performance"})
   public void ZmTasksApp_02() throws HarnessException {

      // Import 100 appointments using Tasks.ics and REST to speed up the setup
	   String filename = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/ics/100tasks.ics";

	   RestUtil rest = new RestUtil();
	   rest.setAuthentication(app.zGetActiveAccount());
	   rest.setPath("/service/home/~/Tasks");
	   rest.setQueryParameter("fmt", "ics");
	   rest.setUploadFile(new File(filename));
	   rest.doPost();

	   PerfToken token = PerfMetrics.startTimestamp(PerfKey.ZmTasksApp,
			   "Load the Tasks app, 100 tasks in list");

	   // app.zPageTasks.zNavigateTo();
	   app.zPageAddressbook.zClickAt("css=td[id='zb__App__Tasks_title']","");

	   PerfMetrics.waitTimestamp(token);

	   // Wait for the app to load
	   app.zPageTasks.zWaitForActive();
   }
}
