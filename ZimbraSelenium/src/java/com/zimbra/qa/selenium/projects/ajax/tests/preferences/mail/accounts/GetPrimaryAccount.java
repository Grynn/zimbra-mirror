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
package com.zimbra.qa.selenium.projects.ajax.tests.preferences.mail.accounts;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.TreePreferences.TreeItem;


public class GetPrimaryAccount extends AjaxCommonTest {

	public GetPrimaryAccount() {
		
		super.startingPage = app.zPagePreferences;
		super.startingAccountPreferences = null;
		
	}


	@Test(
			description = "View the accounts preference page shows the default primary account",
			groups = { "functional" }
			)
	public void GetPrimaryAccount_01() throws HarnessException {

		
		// Navigate to preferences -> notifications
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.MailAccounts);

		// TODO: Maybe this list should be abstracted?
		String tableLocator = "css=div[id='zli__ACCT__"+ app.zGetActiveAccount().ZimbraId +"']";
		ZAssert.assertTrue(app.zPagePreferences.sIsElementPresent(tableLocator), "Verify the zimbra default account is in the preferences list");
		
		String accountName = app.zPagePreferences.sGetText(tableLocator + " td[id$='__na']");
		String accountStatus = app.zPagePreferences.sGetText(tableLocator + " td[id$='__st']");
		String accountEmail = app.zPagePreferences.sGetText(tableLocator + " td[id$='__em']");
		String accountType = app.zPagePreferences.sGetText(tableLocator + " td[id$='__ty']");
		
		ZAssert.assertEquals(accountName,	"Primary Account",	"Verify the Primary account name");
		ZAssert.assertEquals(accountStatus, "OK",				"Verify the Primary account status");
		ZAssert.assertEquals(accountEmail,	app.zGetActiveAccount().EmailAddress,	
																"Verify the Primary account email");
		ZAssert.assertEquals(accountType,	"Primary",			"Verify the Primary account type");
		
	}
}
