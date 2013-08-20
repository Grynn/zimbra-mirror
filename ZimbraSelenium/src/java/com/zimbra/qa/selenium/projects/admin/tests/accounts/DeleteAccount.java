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
package com.zimbra.qa.selenium.projects.admin.tests.accounts;


import org.testng.annotations.Test;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAdminAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.items.AccountItem;
import com.zimbra.qa.selenium.projects.admin.ui.DialogForDeleteOperation;
import com.zimbra.qa.selenium.projects.admin.ui.PageMain;

public class DeleteAccount extends AdminCommonTest {

	public DeleteAccount() {
		logger.info("New "+ DeleteAccount.class.getCanonicalName());

		// All tests start at the "Accounts" page
		super.startingPage = app.zPageManageAccounts;

	}
	
	/**
	 * Testcase : Delete a basic account -- Manage Account View
	 * Steps :
	 * 1. Create an account using SOAP.
	 * 2. Go to Manage Account View.
	 * 3. Select an Account.
	 * 4. Delete an account using delete button in Gear box menu.
	 * 5. Verify account is deleted using SOAP.
	 * 
	 * @throws HarnessException
	 */
	@Test(	description = "Delete a basic account -- Manage Account View",
			groups = { "smoke" })
			public void DeleteAccount_01() throws HarnessException {

		// Create a new account in the Admin Console using SOAP
		AccountItem account = new AccountItem("email" + ZimbraSeleniumProperties.getUniqueString(),ZimbraSeleniumProperties.getStringProperty("testdomain"));
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<CreateAccountRequest xmlns='urn:zimbraAdmin'>"
				+			"<name>" + account.getEmailAddress() + "</name>"
				+			"<password>test123</password>"
				+		"</CreateAccountRequest>");



		// Refresh the account list
		app.zPageManageAccounts.sClickAt(PageMain.Locators.REFRESH_BUTTON, "");

		// Click on account to be deleted.
		app.zPageManageAccounts.zListItem(Action.A_LEFTCLICK, account.getEmailAddress());
		

		// Click on Delete button
		DialogForDeleteOperation dialog = (DialogForDeleteOperation) app.zPageManageAccounts.zToolbarPressPulldown(Button.B_GEAR_BOX, Button.O_DELETE);

		// Click Yes in Confirmation dialog
		dialog.zClickButton(Button.B_YES);

		// Click Ok on "Delete Items" dialog
		dialog.zClickButton(Button.B_OK);


		// Verify the account exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetAccountRequest xmlns='urn:zimbraAdmin'>"
				+			"<account by='name'>"+ account.getEmailAddress() +"</account>"
				+		"</GetAccountRequest>");
		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetAccountResponse/admin:account", 1); 
		ZAssert.assertNull(response, "Verify the account is deleted successfully");


	}
	
	/**
	 * Testcase : Delete a basic account -- Manage Account View/Right Click Menu
	 * Steps :
	 * 1. Create an account using SOAP.
	 * 2. Go to Manage Account View.
	 * 3. Right Click on an Account.
	 * 4. Delete an account using delete button in right click menu.
	 * 5. Verify account is deleted using SOAP.
	 * 
	 * @throws HarnessException
	 */
	@Test(	description = "Delete a basic account - Manage Account View/Right Click Menu",
			groups = { "functional" })
			public void DeleteAccount_02() throws HarnessException {

		// Create a new account in the Admin Console using SOAP
		AccountItem account = new AccountItem("email" + ZimbraSeleniumProperties.getUniqueString(),ZimbraSeleniumProperties.getStringProperty("testdomain"));
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<CreateAccountRequest xmlns='urn:zimbraAdmin'>"
				+			"<name>" + account.getEmailAddress() + "</name>"
				+			"<password>test123</password>"
				+		"</CreateAccountRequest>");



		// Refresh the account list
		app.zPageManageAccounts.sClickAt(PageMain.Locators.REFRESH_BUTTON, "");

		// Right Click on account to be deleted.
		app.zPageManageAccounts.zListItem(Action.A_RIGHTCLICK, account.getEmailAddress());
		

		// Click on Delete button
		DialogForDeleteOperation dialog = (DialogForDeleteOperation) app.zPageManageAccounts.zToolbarPressButton(Button.B_TREE_DELETE);

		// Click Yes in Confirmation dialog
		dialog.zClickButton(Button.B_YES);

		// Click Ok on "Delete Items" dialog
		dialog.zClickButton(Button.B_OK);


		// Verify the account exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetAccountRequest xmlns='urn:zimbraAdmin'>"
				+			"<account by='name'>"+ account.getEmailAddress() +"</account>"
				+		"</GetAccountRequest>");
		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetAccountResponse/admin:account", 1); 
		ZAssert.assertNull(response, "Verify the account is deleted successfully");


	}

	/**
	 * Testcase : Delete a delegated admin account -- Manage Account View
	 * Steps :
	 * 1. Create a delegated admin account using SOAP.
	 * 2. Go to Manage Account View.
	 * 3. Select an Account.
	 * 4. Delete an account using delete button in Gear box menu.
	 * 5. Verify account is deleted using SOAP.
	 * 
	 * @throws HarnessException
	 */
	@Test(	description = "Delete a delegated admin account -- Manage Account View",
			groups = { "functional" })
			public void DeleteAccount_03() throws HarnessException {

		// Create a new account in the Admin Console using SOAP
		AccountItem account = new AccountItem("delegated_admin" + ZimbraSeleniumProperties.getUniqueString(),ZimbraSeleniumProperties.getStringProperty("testdomain"));
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<CreateAccountRequest xmlns='urn:zimbraAdmin'>"
				+			"<name>" + account.getEmailAddress() + "</name>"
				+			"<password>test123</password>"
				+			"<a xmlns='' n='zimbraIsDelegatedAdminAccount'>TRUE</a>"
				+		"</CreateAccountRequest>");



		// Refresh the account list
		app.zPageManageAccounts.sClickAt(PageMain.Locators.REFRESH_BUTTON, "");

		// Click on account to be deleted.
		app.zPageManageAccounts.zListItem(Action.A_LEFTCLICK, account.getEmailAddress());
		

		// Click on Delete button
		DialogForDeleteOperation dialog = (DialogForDeleteOperation) app.zPageManageAccounts.zToolbarPressPulldown(Button.B_GEAR_BOX, Button.O_DELETE);

		// Click Yes in Confirmation dialog
		dialog.zClickButton(Button.B_YES);

		// Click Ok on "Delete Items" dialog
		dialog.zClickButton(Button.B_OK);


		// Verify the account exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetAccountRequest xmlns='urn:zimbraAdmin'>"
				+			"<account by='name'>"+ account.getEmailAddress() +"</account>"
				+		"</GetAccountRequest>");
		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetAccountResponse/admin:account", 1); 
		ZAssert.assertNull(response, "Verify the account is deleted successfully");
	}

	/**
	 * Testcase : Delete a global admin account -- Manage Account View
	 * Steps :
	 * 1. Create a global admin account using SOAP.
	 * 2. Go to Manage Account View.
	 * 3. Select an Account.
	 * 4. Delete an account using delete button in Gear box menu.
	 * 5. Verify account is deleted using SOAP.
	 * 
	 * @throws HarnessException
	 */
	@Test(	description = "Delete a global admin account -- Manage Account View",
			groups = { "functional" })
			public void DeleteAccount_04() throws HarnessException {

		// Create a new account in the Admin Console using SOAP
		AccountItem account = new AccountItem("global_admin" + ZimbraSeleniumProperties.getUniqueString(),ZimbraSeleniumProperties.getStringProperty("testdomain"));
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<CreateAccountRequest xmlns='urn:zimbraAdmin'>"
				+			"<name>" + account.getEmailAddress() + "</name>"
				+			"<password>test123</password>"
				+			"<a xmlns='' n='zimbraIsAdminAccount'>TRUE</a>"
				+		"</CreateAccountRequest>");



		// Refresh the account list
		app.zPageManageAccounts.sClickAt(PageMain.Locators.REFRESH_BUTTON, "");

		// Click on account to be deleted.
		app.zPageManageAccounts.zListItem(Action.A_LEFTCLICK, account.getEmailAddress());
		

		// Click on Delete button
		DialogForDeleteOperation dialog = (DialogForDeleteOperation) app.zPageManageAccounts.zToolbarPressPulldown(Button.B_GEAR_BOX, Button.O_DELETE);

		// Click Yes in Confirmation dialog
		dialog.zClickButton(Button.B_YES);

		// Click Ok on "Delete Items" dialog
		dialog.zClickButton(Button.B_OK);


		// Verify the account exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetAccountRequest xmlns='urn:zimbraAdmin'>"
				+			"<account by='name'>"+ account.getEmailAddress() +"</account>"
				+		"</GetAccountRequest>");
		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetAccountResponse/admin:account", 1); 
		ZAssert.assertNull(response, "Verify the account is deleted successfully");
	}




	/**
	 * Testcase : Delete a basic account -- Search List View
	 * Steps :
	 * 1. Create an account using SOAP.
	 * 2. Search account.
	 * 3. Select an Account.
	 * 4. Delete an account using delete button in Gear box menu.
	 * 5. Verify account is deleted using SOAP.
	 * 
	 * @throws HarnessException
	 */
	@Test(	description = "Delete a basic account - Search List View",
			groups = { "smoke" })
			public void DeleteAccount_05() throws HarnessException {

		// Create a new account in the Admin Console using SOAP
		AccountItem account = new AccountItem("email" + ZimbraSeleniumProperties.getUniqueString(),ZimbraSeleniumProperties.getStringProperty("testdomain"));
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<CreateAccountRequest xmlns='urn:zimbraAdmin'>"
				+			"<name>" + account.getEmailAddress() + "</name>"
				+			"<password>test123</password>"
				+		"</CreateAccountRequest>");



		// Enter the search string to find the account
		app.zPageSearchResults.zAddSearchQuery(account.getEmailAddress());

		// Click search
		app.zPageSearchResults.zToolbarPressButton(Button.B_SEARCH);

		// Click on account to be deleted.
		app.zPageSearchResults.zListItem(Action.A_LEFTCLICK, account.getEmailAddress());

		// Click on Delete button
		DialogForDeleteOperation dialog = (DialogForDeleteOperation) app.zPageSearchResults.zToolbarPressPulldown(Button.B_GEAR_BOX, Button.O_DELETE);

		// Click Yes in Confirmation dialog
		dialog.zClickButton(Button.B_YES);

		// Click Ok on "Delete Items" dialog
		dialog.zClickButton(Button.B_OK);


		// Verify the account exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetAccountRequest xmlns='urn:zimbraAdmin'>"
				+			"<account by='name'>"+ account.getEmailAddress() +"</account>"
				+		"</GetAccountRequest>");
		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetAccountResponse/admin:account", 1); 
		ZAssert.assertNull(response, "Verify the account is deleted successfully");


	}
	
	/**
	 * Testcase : Delete a basic account -- Search List View/Right Click Menu
	 * Steps :
	 * 1. Create an account using SOAP.
	 * 2. Search account.
	 * 3. Right click on Account.
	 * 4. Delete an account using delete button in right click menu.
	 * 5. Verify account is deleted using SOAP.
	 * @throws HarnessException
	 */
	@Test(	description = "Delete a basic account - Search List View/Right Click Menu",
			groups = { "functional" })
			public void DeleteAccount_06() throws HarnessException {

		// Create a new account in the Admin Console using SOAP
		AccountItem account = new AccountItem("email" + ZimbraSeleniumProperties.getUniqueString(),ZimbraSeleniumProperties.getStringProperty("testdomain"));
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<CreateAccountRequest xmlns='urn:zimbraAdmin'>"
				+			"<name>" + account.getEmailAddress() + "</name>"
				+			"<password>test123</password>"
				+		"</CreateAccountRequest>");



		// Enter the search string to find the account
		app.zPageSearchResults.zAddSearchQuery(account.getEmailAddress());

		// Click search
		app.zPageSearchResults.zToolbarPressButton(Button.B_SEARCH);

		// Right Click on account to be deleted.
		app.zPageSearchResults.zListItem(Action.A_RIGHTCLICK, account.getEmailAddress());

		// Click on Delete button
		DialogForDeleteOperation dialog = (DialogForDeleteOperation) app.zPageSearchResults.zToolbarPressButton(Button.B_TREE_DELETE);

		// Click Yes in Confirmation dialog
		dialog.zClickButton(Button.B_YES);

		// Click Ok on "Delete Items" dialog
		dialog.zClickButton(Button.B_OK);


		// Verify the account exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetAccountRequest xmlns='urn:zimbraAdmin'>"
				+			"<account by='name'>"+ account.getEmailAddress() +"</account>"
				+		"</GetAccountRequest>");
		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetAccountResponse/admin:account", 1); 
		ZAssert.assertNull(response, "Verify the account is deleted successfully");


	}
	
}