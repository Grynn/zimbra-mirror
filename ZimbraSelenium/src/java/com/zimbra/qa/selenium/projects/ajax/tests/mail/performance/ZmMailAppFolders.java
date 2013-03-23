/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012, 2013 VMware, Inc.
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
package com.zimbra.qa.selenium.projects.ajax.tests.mail.performance;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.framework.util.performance.PerfKey;
import com.zimbra.qa.selenium.framework.util.performance.PerfMetrics;
import com.zimbra.qa.selenium.framework.util.performance.PerfToken;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.PageLogin.Locators;



public class ZmMailAppFolders extends AjaxCommonTest {
	
	public ZmMailAppFolders() {
		logger.info("New "+ ZmMailAppFolders.class.getCanonicalName());
		
		
		super.startingPage = app.zPageLogin;
		super.startingAccountPreferences = new HashMap<String, String>() {
			private static final long serialVersionUID = 7525760124523255182L;
		{
			put("zimbraPrefGroupMailBy", "message");
			put("zimbraPrefMessageViewHtmlPreferred", "TRUE");
		}};
		
	}
	
	@Test(	description = "Measure the time to load the mail app, message view, 1 folder",
			groups = { "performance" })
	public void ZmMailAppFolder_01() throws HarnessException {

		// Create a folder
		FolderItem root = FolderItem.importFromSOAP(ZimbraAccount.AccountZWC(), FolderItem.SystemFolder.UserRoot);
		ZimbraAccount.AccountZWC().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
	                	"<folder name='folder"+ ZimbraSeleniumProperties.getUniqueString() + "' view='message' l='"+ root.getId() +"'/>" +
	                "</CreateFolderRequest>");


		// Fill out the login page
		app.zPageLogin.zSetLoginName(ZimbraAccount.AccountZWC().EmailAddress);
		app.zPageLogin.zSetLoginPassword(ZimbraAccount.AccountZWC().Password);

		PerfToken token = PerfMetrics.startTimestamp(PerfKey.ZmMailAppOverviewPanel, "Load the mail app, message view, 1 folder");

		// Click the Login button
		app.zPageLogin.sClick(Locators.zBtnLogin);

		PerfMetrics.waitTimestamp(token);
				
		// Wait for the app to load
		app.zPageMain.zWaitForActive();
		
		
	}

	@Test(	description = "Measure the time to load the mail app, message view, 100 folders",
			groups = { "performance" })
	public void ZmMailAppFolder_02() throws HarnessException {

		// Create 100 folders
		FolderItem root = FolderItem.importFromSOAP(ZimbraAccount.AccountZWC(), FolderItem.SystemFolder.UserRoot);
		for (int i = 0; i < 100; i++) {
			ZimbraAccount.AccountZWC().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
							"<folder name='folder"+ ZimbraSeleniumProperties.getUniqueString() + "' view='message' l='"+ root.getId() +"'/>" +
					"</CreateFolderRequest>");
		}


		// Fill out the login page
		app.zPageLogin.zSetLoginName(ZimbraAccount.AccountZWC().EmailAddress);
		app.zPageLogin.zSetLoginPassword(ZimbraAccount.AccountZWC().Password);

		PerfToken token = PerfMetrics.startTimestamp(PerfKey.ZmMailAppOverviewPanel, "Load the mail app, message view, 100 folders");

		// Click the Login button
		app.zPageLogin.sClick(Locators.zBtnLogin);

		PerfMetrics.waitTimestamp(token);

		// Wait for the app to load
		app.zPageMain.zWaitForActive();


	}


}
