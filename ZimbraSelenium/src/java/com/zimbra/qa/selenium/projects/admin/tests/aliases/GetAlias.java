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
package com.zimbra.qa.selenium.projects.admin.tests.aliases;

import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAdminAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.items.AccountItem;
import com.zimbra.qa.selenium.projects.admin.items.AliasItem;
import com.zimbra.qa.selenium.projects.admin.ui.PageMain;

public class GetAlias extends AdminCommonTest {

	public GetAlias() {
		logger.info("New " + GetAlias.class.getCanonicalName());
		
		//All test starts at alias page
		super.startingPage=app.zPageManageAliases;		
	}
	
	/**
	 * Testcase : Verify delete alias operation  -- Manage alias View
	 * Steps :
	 * 1. Create an alias using SOAP.
	 * 2. Go to Manage alias View.
	 * 3. Select an alias.
	 * 4. Delete an alias using delete button in Gear box menu.
	 * 5. Verify alias is deleted using SOAP.
	 * @throws HarnessException
	 */
	@Test(	description = "Verify delete alias operation  -- Manage alias View",
			groups = { "smoke" })
			public void GetAlias_01() throws HarnessException {
	
		AccountItem target = new AccountItem("email" + ZimbraSeleniumProperties.getUniqueString(),ZimbraSeleniumProperties.getStringProperty("testdomain"));
		AccountItem.createUsingSOAP(target);
	
	
		// Create a new account in the Admin Console using SOAP
		AliasItem alias = new AliasItem();
		String aliasEmailAddress=alias.getEmailAddress();
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<AddAccountAliasRequest xmlns='urn:zimbraAdmin'>"
				+			"<id>" + target.getID() + "</id>"
				+			"<alias>" + aliasEmailAddress + "</alias>"
				+		"</AddAccountAliasRequest>");
	
		// Refresh the account list
		app.zPageManageAliases.sClickAt(PageMain.Locators.REFRESH_BUTTON, "");
	
	
		// Get the list of displayed accounts
		List<AccountItem> accounts = app.zPageManageAliases.zListGetAccounts();
		ZAssert.assertNotNull(accounts, "Verify the alias list is returned");
	
		AccountItem found = null;
		for (AccountItem a : accounts) {
			logger.info("Looking for alias "+ aliasEmailAddress + " found: "+ a.getGEmailAddress());
			if ( aliasEmailAddress.equals(a.getGEmailAddress()) ) {
				found = a;
				break;
			}
		}
		ZAssert.assertNotNull(found, "Verify alias is deleted successfully");
	}

	
	/**
	 * Testcase : Verify created alias is displayed in UI.
	 * Steps :
	 * 1. Create an alias using SOAP.
	 * 2. Verify alias is present in the list.
	 * @throws HarnessException
	 */
	@Test(	description = "Verify created alias is present in the list view",
			groups = { "smoke" })
			public void GetAlias_02() throws HarnessException {

		AccountItem target = new AccountItem("email" + ZimbraSeleniumProperties.getUniqueString(),ZimbraSeleniumProperties.getStringProperty("testdomain"));
		AccountItem.createUsingSOAP(target);
		
		
		// Create a new account in the Admin Console using SOAP
		AliasItem alias = new AliasItem();
		String aliasEmailAddress=alias.getEmailAddress();
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
						"<AddAccountAliasRequest xmlns='urn:zimbraAdmin'>"
				+			"<id>" + target.getID() + "</id>"
				+			"<alias>" + aliasEmailAddress + "</alias>"
				+		"</AddAccountAliasRequest>");

		// Enter the search string to find the account
		app.zPageSearchResults.zAddSearchQuery(aliasEmailAddress);

		// Click search
		app.zPageSearchResults.zToolbarPressButton(Button.B_SEARCH);

		// Get the list of displayed accounts
		List<AccountItem> accounts = app.zPageSearchResults.zListGetAccounts();
		ZAssert.assertNotNull(accounts, "Verify the alias list is returned");

		AccountItem found = null;
		for (AccountItem a : accounts) {
			logger.info("Looking for alias "+ aliasEmailAddress + " found: "+ a.getGEmailAddress());
			if ( aliasEmailAddress.equals(a.getGEmailAddress()) ) {
				found = a;
				break;
			}
		}
		ZAssert.assertNotNull(found, "See http://bugzilla.zimbra.com/show_bug.cgi?id=4704");

	}
	
}
