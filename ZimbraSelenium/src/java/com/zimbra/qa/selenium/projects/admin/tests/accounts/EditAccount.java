/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012 VMware, Inc.
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
import com.zimbra.qa.selenium.projects.admin.ui.FormEditAccount;
import com.zimbra.qa.selenium.projects.admin.ui.PageMain;
import com.zimbra.qa.selenium.projects.admin.ui.PageSearchResults;

public class EditAccount extends AdminCommonTest {
	public EditAccount() {
		logger.info("New "+ EditAccount.class.getCanonicalName());

		// All tests start at the "Accounts" page
		super.startingPage = app.zPageManageAccounts;
	}

	/**
	 * Testcase : Edit account name  - Manage Account View
	 * Steps :
	 * 1. Create an account using SOAP.
	 * 2. Go to Manage Account View
	 * 3. Select an Account.
	 * 4. Edit an account using edit button in Gear box menu.
	 * 5. Verify account is edited using SOAP.
	 * @throws HarnessException
	 */
	@Test(	description = "Edit Account name  - Manage Account View",
			groups = { "smoke" })
			public void EditAccount_01() throws HarnessException {

		// Create a new account in the Admin Console using SOAP
		AccountItem account = new AccountItem("email" + ZimbraSeleniumProperties.getUniqueString(),ZimbraSeleniumProperties.getStringProperty("testdomain"));
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<CreateAccountRequest xmlns='urn:zimbraAdmin'>"
				+			"<name>" + account.getEmailAddress() + "</name>"
				+			"<password>test123</password>"
				+		"</CreateAccountRequest>");

		// Refresh the account list
		app.zPageManageAccounts.sClickAt(PageMain.Locators.REFRESH_BUTTON, "");
		
		// Click on account to be Edited.
		app.zPageManageAccounts.zListItem(Action.A_LEFTCLICK, account.getEmailAddress());
		
		// Click on Edit button
		FormEditAccount form = (FormEditAccount) app.zPageManageAccounts.zToolbarPressPulldown(Button.B_GEAR_BOX, Button.O_EDIT);
		
		//Click on General Information tab.
		form.zClickTreeItem(FormEditAccount.TreeItem.GENERAL_INFORMATION);

		//Edit the name.
		String editedName = "editedAccount_" + ZimbraSeleniumProperties.getUniqueString();
		form.setName(editedName);
		
		//Submit the form.
		form.zSubmit();
		
		// Verify the account exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetAccountRequest xmlns='urn:zimbraAdmin'>"
				+			"<account by='name'>"+ editedName+"@"+account.getDomainName() +"</account>"
				+		"</GetAccountRequest>");
		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetAccountResponse/admin:account", 1);
		ZAssert.assertNotNull(response, "Verify the account is edited successfully");
	}
	
	/**
	 * Testcase : Edit account name -- right click 
	 * Steps :
	 * 1. Create an account using SOAP.
	 * 2. Edit the account name using UI Right Click.
	 * 3. Verify account name is changed using SOAP.
	 * @throws HarnessException
	 */
	@Test(	description = "Edit account name -- right click",
			groups = { "functional" })
			public void EditAccount_02() throws HarnessException {

		// Create a new account in the Admin Console using SOAP
		AccountItem account = new AccountItem("email" + ZimbraSeleniumProperties.getUniqueString(),ZimbraSeleniumProperties.getStringProperty("testdomain"));
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<CreateAccountRequest xmlns='urn:zimbraAdmin'>"
				+			"<name>" + account.getEmailAddress() + "</name>"
				+			"<password>test123</password>"
				+		"</CreateAccountRequest>");

		// Refresh the account list
		app.zPageManageAccounts.sClickAt(PageMain.Locators.REFRESH_BUTTON, "");
		
		// Right Click on account to be Edited.
		app.zPageManageAccounts.zListItem(Action.A_RIGHTCLICK, account.getEmailAddress());
		
		// Click on Edit button
		FormEditAccount form = (FormEditAccount) app.zPageManageAccounts.zToolbarPressButton(Button.B_TREE_EDIT);
		
		//Click on General Information tab.
		form.zClickTreeItem(FormEditAccount.TreeItem.GENERAL_INFORMATION);

		//Edit the name.
		String editedName = "editedAccount_" + ZimbraSeleniumProperties.getUniqueString();
		form.setName(editedName);
		
		//Submit the form.
		form.zSubmit();
		
		// Verify the account exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetAccountRequest xmlns='urn:zimbraAdmin'>"
				+			"<account by='name'>"+ editedName+"@"+account.getDomainName() +"</account>"
				+		"</GetAccountRequest>");
		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetAccountResponse/admin:account", 1);
		ZAssert.assertNotNull(response, "https://bugzilla.zimbra.com/show_bug.cgi?id=74487");
	}
	
	/**
	 * Testcase : Edit delegated admin account name  - Manage Account View
	 * Steps :
	 * 1. Create an delegated admin account using SOAP.
	 * 2. Go to Manage Account View
	 * 3. Select an Account.
	 * 4. Edit an account using edit button in Gear box menu.
	 * 5. Verify account is edited using SOAP.
	 * @throws HarnessException
	 */
	@Test(	description = "Edit delegated admin account name  - Manage Account View",
			groups = { "functional" })
			public void EditAccount_03() throws HarnessException {

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
		
		// Click on account to be Edited.
		app.zPageManageAccounts.zListItem(Action.A_LEFTCLICK, account.getEmailAddress());
		
		// Click on Edit button
		FormEditAccount form = (FormEditAccount) app.zPageManageAccounts.zToolbarPressPulldown(Button.B_GEAR_BOX, Button.O_EDIT);
		
		//Click on General Information tab.
		form.zClickTreeItem(FormEditAccount.TreeItem.GENERAL_INFORMATION);

		//Edit the name.
		String editedName = "editedAccount_" + ZimbraSeleniumProperties.getUniqueString();
		form.setName(editedName);
		
		//Submit the form.
		form.zSubmit();
		
		// Verify the account exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetAccountRequest xmlns='urn:zimbraAdmin'>"
				+			"<account by='name'>"+ editedName+"@"+account.getDomainName() +"</account>"
				+		"</GetAccountRequest>");
		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetAccountResponse/admin:account", 1);
		ZAssert.assertNotNull(response, "Verify the account is edited successfully");
	}

	/**
	 * Testcase : Edit global admin account name  - Manage Account View
	 * Steps :
	 * 1. Create an global admin account using SOAP.
	 * 2. Go to Manage Account View
	 * 3. Select an Account.
	 * 4. Edit an account using edit button in Gear box menu.
	 * 5. Verify account is edited using SOAP.
	 * @throws HarnessException
	 */
	@Test(	description = "Edit global admin Account name  - Manage Account View",
			groups = { "smoke" })
			public void EditAccount_04() throws HarnessException {

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
		
		// Click on account to be Edited.
		app.zPageManageAccounts.zListItem(Action.A_LEFTCLICK, account.getEmailAddress());
		
		// Click on Edit button
		FormEditAccount form = (FormEditAccount) app.zPageManageAccounts.zToolbarPressPulldown(Button.B_GEAR_BOX, Button.O_EDIT);
		
		//Click on General Information tab.
		form.zClickTreeItem(FormEditAccount.TreeItem.GENERAL_INFORMATION);

		//Edit the name.
		String editedName = "editedAccount_" + ZimbraSeleniumProperties.getUniqueString();
		form.setName(editedName);
		
		//Submit the form.
		form.zSubmit();
		
		// Verify the account exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetAccountRequest xmlns='urn:zimbraAdmin'>"
				+			"<account by='name'>"+ editedName+"@"+account.getDomainName() +"</account>"
				+		"</GetAccountRequest>");
		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetAccountResponse/admin:account", 1);
		ZAssert.assertNotNull(response, "Verify the account is edited successfully");
	}

	
	/**
	 * Testcase : Edit a basic account -- Search List View
	 * Steps :
	 * 1. Create an account using SOAP.
	 * 2. Search account.
	 * 3. Select an Account.
	 * 4. Edit an account using edit button in Gear box menu.
	 * 5. Verify account is edited using SOAP.
	 * 
	 * @throws HarnessException
	 */
	@Test(	description = "Edit a basic account - Search List View",
			groups = { "functional" })
			public void EditAccount_05() throws HarnessException {

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
		app.zPageSearchResults.setType(PageSearchResults.TypeOfObject.ACCOUNT);
		FormEditAccount form = (FormEditAccount) app.zPageSearchResults.zToolbarPressPulldown(Button.B_GEAR_BOX, Button.O_EDIT);

		//Click on General Information tab.
		form.zClickTreeItem(FormEditAccount.TreeItem.GENERAL_INFORMATION);

		//Edit the name.
		String editedName = "editedAccount_" + ZimbraSeleniumProperties.getUniqueString();
		form.setName(editedName);
		
		//Submit the form.
		form.zSubmit();
		
		// Verify the account exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetAccountRequest xmlns='urn:zimbraAdmin'>"
				+			"<account by='name'>"+ editedName+"@"+account.getDomainName() +"</account>"
				+		"</GetAccountRequest>");
		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetAccountResponse/admin:account", 1);
		ZAssert.assertNotNull(response, "https://bugzilla.zimbra.com/show_bug.cgi?id=74487");

	}
	
	/**
	 * Testcase : Edit a basic account -- Search List View
	 * Steps :
	 * 1. Create an account using SOAP.
	 * 2. Search account.
	 * 3. Select an Account.
	 * 4. Edit an account using edit button in Gear box menu.
	 * 5. Verify account is edited using SOAP.
	 * 
	 * @throws HarnessException
	 */
	@Test(	description = "Edit a basic account - Search List View",
			groups = { "functional" })
			public void EditAccount_06() throws HarnessException {


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
		app.zPageSearchResults.zListItem(Action.A_RIGHTCLICK, account.getEmailAddress());

		
		// Click on Delete button
		app.zPageSearchResults.setType(PageSearchResults.TypeOfObject.ACCOUNT);
		FormEditAccount form = (FormEditAccount) app.zPageSearchResults.zToolbarPressButton(Button.B_TREE_EDIT);

		//Click on General Information tab.
		form.zClickTreeItem(FormEditAccount.TreeItem.GENERAL_INFORMATION);

		//Edit the name.
		String editedName = "editedAccount_" + ZimbraSeleniumProperties.getUniqueString();
		form.setName(editedName);
		
		//Submit the form.
		form.zSubmit();
		
		// Verify the account exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetAccountRequest xmlns='urn:zimbraAdmin'>"
				+			"<account by='name'>"+ editedName+"@"+account.getDomainName() +"</account>"
				+		"</GetAccountRequest>");
		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetAccountResponse/admin:account", 1);
		ZAssert.assertNotNull(response, "https://bugzilla.zimbra.com/show_bug.cgi?id=74487");

	}



}
