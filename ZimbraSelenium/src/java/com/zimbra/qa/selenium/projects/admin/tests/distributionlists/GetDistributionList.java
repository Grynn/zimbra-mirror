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
package com.zimbra.qa.selenium.projects.admin.tests.distributionlists;

import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAdminAccount;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.items.AccountItem;
import com.zimbra.qa.selenium.projects.admin.items.DistributionListItem;
import com.zimbra.qa.selenium.projects.admin.ui.PageMain;

public class GetDistributionList extends AdminCommonTest {

	public GetDistributionList() {
		logger.info("New "+ GetDistributionList.class.getCanonicalName());

		// All tests start at the "Distribution Lists" page
		super.startingPage = app.zPageManageDistributionList;
	}

	/**
	 * Testcase : Verify created dl is displayed in UI  -- Manage dl View
	 * Steps :
	 * 1. Create a dl using SOAP.
	 * 2. Go to Manage dl View.
	 * 3. Verify dl is present in the list
	 * @throws HarnessException
	 */
	@Test(	description = "Verify created dl is present in the list view",
			groups = { "smoke" })
			public void GetDistributionList_01() throws HarnessException {

		// Create a new dl in the Admin Console using SOAP
		DistributionListItem dl = new DistributionListItem();
		String dlEmailAddress=dl.getEmailAddress();

		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
						"<CreateDistributionListRequest xmlns='urn:zimbraAdmin'>"
				+			"<name>" + dlEmailAddress + "</name>"
				+		"</CreateDistributionListRequest>");

		// Refresh the list
		app.zPageManageDistributionList.sClickAt(PageMain.Locators.REFRESH_BUTTON, "");
		
		// Get the list of displayed accounts
		List<AccountItem> accounts = app.zPageManageDistributionList.zListGetAccounts();
		ZAssert.assertNotNull(accounts, "Verify the dl list is returned");

		AccountItem found = null;
		for (AccountItem a : accounts) {
			logger.info("Looking for dl "+ dlEmailAddress + " found: "+ a.getGEmailAddress());
			if ( dlEmailAddress.equals(a.getGEmailAddress()) ) {
				found = a;
				break;
			}
		}
		ZAssert.assertNotNull(found, "Verify the dl is returned correctly");
	}
		
	/**
	 * Testcase : Verify created admin dl is displayed in UI  -- Manage dl View
	 * Steps :
	 * 1. Create a admin dl using SOAP.
	 * 2. Go to Manage dl View.
	 * 3. Verify dl is present in the list
	 * @throws HarnessException
	 */
	@Test(	description = "Verify created admin dl is present in the list view",
			groups = { "functional" })
			public void GetDistributionList_02() throws HarnessException {

		// Create a new dl in the Admin Console using SOAP
		DistributionListItem dl = new DistributionListItem();
		String dlEmailAddress=dl.getEmailAddress();

		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<CreateDistributionListRequest xmlns='urn:zimbraAdmin'>"
				+			"<name>" + dlEmailAddress + "</name>"
				+			"<a xmlns='' n='zimbraIsAdminGroup'>TRUE</a>"
				+		"</CreateDistributionListRequest>");

		// Refresh the list
		app.zPageManageDistributionList.sClickAt(PageMain.Locators.REFRESH_BUTTON, "");
		
		// Get the list of displayed accounts
		List<AccountItem> accounts = app.zPageManageDistributionList.zListGetAccounts();
		ZAssert.assertNotNull(accounts, "Verify the dl list is returned");

		AccountItem found = null;
		for (AccountItem a : accounts) {
			logger.info("Looking for dl "+ dlEmailAddress + " found: "+ a.getGEmailAddress());
			if ( dlEmailAddress.equals(a.getGEmailAddress()) ) {
				found = a;
				break;
			}
		}
		ZAssert.assertNotNull(found, "Verify the dl is returned correctly");
	}

	/**
	 * Testcase : Verify created dynamic admin dl is displayed in UI  -- Manage dl View
	 * Steps :
	 * 1. Create a dynamic admin dl using SOAP.
	 * 2. Go to Manage dl View.
	 * 3. Verify dl is present in the list
	 * @throws HarnessException
	 */
	@Test(	description = "Verify created dynamic admin dl is present in the list view",
			groups = { "functional" })
			public void GetDistributionList_03() throws HarnessException {

		// Create a new dl in the Admin Console using SOAP
		DistributionListItem dl = new DistributionListItem();
		String dlEmailAddress=dl.getEmailAddress();

		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<CreateDistributionListRequest xmlns='urn:zimbraAdmin' dynamic='1'>"
				+			"<name>" + dlEmailAddress + "</name>"
				+			"<a xmlns='' n='zimbraIsAdminGroup'>TRUE</a>"
				+			"<a xmlns='' n='zimbraIsACLGroup'>TRUE</a>"
				+		"</CreateDistributionListRequest>");

		// Refresh the list
		app.zPageManageDistributionList.sClickAt(PageMain.Locators.REFRESH_BUTTON, "");
		
		// Get the list of displayed accounts
		List<AccountItem> accounts = app.zPageManageDistributionList.zListGetAccounts();
		ZAssert.assertNotNull(accounts, "Verify the dl list is returned");

		AccountItem found = null;
		for (AccountItem a : accounts) {
			logger.info("Looking for dl "+ dlEmailAddress + " found: "+ a.getGEmailAddress());
			if ( dlEmailAddress.equals(a.getGEmailAddress()) ) {
				found = a;
				break;
			}
		}
		ZAssert.assertNotNull(found, "Verify the dl is returned correctly");
	}

	/**
	 * Testcase : Verify created dl is displayed in UI - Search view.
	 * Steps :
	 * 1. Create an dl using SOAP.
	 * 2. Search list
	 * 2. Verify dl is present in the search list.
	 * @throws HarnessException
	 */
	@Test(	description = "Verify created dl is present in the list view  - Search view",
			groups = { "functional" })
			public void GetDistributionList_04() throws HarnessException {

		// Create a new dl in the Admin Console using SOAP
		DistributionListItem dl = new DistributionListItem();
		String dlEmailAddress=dl.getEmailAddress();

		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
						"<CreateDistributionListRequest xmlns='urn:zimbraAdmin'>"
				+			"<name>" + dlEmailAddress + "</name>"
				+		"</CreateDistributionListRequest>");

		// Enter the search string to find the account
		app.zPageSearchResults.zAddSearchQuery(dlEmailAddress);

		// Click search
		app.zPageSearchResults.zToolbarPressButton(Button.B_SEARCH);

		// Get the list of displayed accounts
		List<AccountItem> accounts = app.zPageSearchResults.zListGetAccounts();
		ZAssert.assertNotNull(accounts, "Verify the dl list is returned");

		AccountItem found = null;
		for (AccountItem a : accounts) {
			logger.info("Looking for dl "+ dlEmailAddress + " found: "+ a.getGEmailAddress());
			if ( dlEmailAddress.equals(a.getGEmailAddress()) ) {
				found = a;
				break;
			}
		}
		ZAssert.assertNotNull(found, "Verify the dl is returned correctly");

	}


}
