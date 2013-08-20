/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2012, 2013 Zimbra Software, LLC.
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
package com.zimbra.qa.selenium.projects.admin.tests.cos;

import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAdminAccount;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.items.AccountItem;
import com.zimbra.qa.selenium.projects.admin.items.CosItem;
import com.zimbra.qa.selenium.projects.admin.ui.PageMain;

public class GetCos extends AdminCommonTest {
	
	public GetCos() {
		logger.info("New " + GetCos.class.getCanonicalName());
		
		//All tests starts at "Cos" page
		super.startingPage=app.zPageManageCOS;
	}
	
	/**
	 * Testcase : Verify created cos is displayed in UI - Search list view.
	 * Steps :
	 * 1. Create a cos using SOAP.
	 * 2. Search cos created in Step-1
	 * 3. Verify cos is present in the list.
	 * @throws HarnessException
	 */
	@Test(	description = "Verify created cos is displayed in UI - Manage COS list view.",
			groups = { "smoke" })
			public void GetCos_01() throws HarnessException {

		// Create a new cos in the Admin Console using SOAP
		CosItem cos = new CosItem();
		String cosName=cos.getName();

		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<CreateCosRequest xmlns='urn:zimbraAdmin'>"
				+			"<name>" + cosName + "</name>"
				+		"</CreateCosRequest>");

		// Refresh the account list
		app.zPageManageCOS.sClickAt(PageMain.Locators.REFRESH_BUTTON, "");

		// Get the list of displayed accounts
		List<CosItem> cosList = app.zPageManageCOS.zListGetCos();
		
		ZAssert.assertNotNull(cosList, "Verify the cos list is returned");

		CosItem found = null;
		for (CosItem a : cosList) {
			logger.info("Looking for cos "+ cosName + " found: "+ a.getName());
			if ( cosName.equals(a.getName()) ) {
				found = a;
				break;
			}
		}
		ZAssert.assertNotNull(found, "Verify the cos is returned correctly");

	}

	/**
	 * Testcase : Verify created cos is displayed in UI - Search list view.
	 * Steps :
	 * 1. Create a cos using SOAP.
	 * 2. Search cos created in Step-1
	 * 3. Verify cos is present in the list.
	 * @throws HarnessException
	 */
	@Test(	description = "Verify created cos is displayed in UI - Search list view.",
			groups = { "smoke" })
			public void GetCos_02() throws HarnessException {

		// Create a new cos in the Admin Console using SOAP
		CosItem cos = new CosItem();
		String cosName=cos.getName();

		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
						"<CreateCosRequest xmlns='urn:zimbraAdmin'>"
				+			"<name>" + cosName + "</name>"
				+		"</CreateCosRequest>");
		
		
		SleepUtil.sleepSmall();

		// Enter the search string to find the account
		app.zPageSearchResults.zAddSearchQuery(cosName);

		// Click search
		app.zPageSearchResults.zToolbarPressButton(Button.B_SEARCH);

		// Get the list of displayed accounts
		List<AccountItem> accounts = app.zPageSearchResults.zListGetAccounts();
		ZAssert.assertNotNull(accounts, "Verify the cos list is returned");

		AccountItem found = null;
		for (AccountItem a : accounts) {
			logger.info("Looking for cos "+ cosName + " found: "+ a.getGEmailAddress());
			if ( cosName.equals(a.getGEmailAddress()) ) {
				found = a;
				break;
			}
		}
		ZAssert.assertNotNull(found, "Verify the cos is returned correctly");

	}
}
