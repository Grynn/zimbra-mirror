package com.zimbra.qa.selenium.projects.admin.tests.accounts;


import org.testng.annotations.Test;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAdminAccount;
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
	 * Testcase : Delete a basic account
	 * Steps :
	 * 1. Create an account using SOAP.
	 * 2. Delete the account using GUI.
	 * @throws HarnessException
	 */
	@Test(	description = "Delete a basic account",
			groups = { "smoke" })
			public void DeleteAccount_01() throws HarnessException {

		// Create a new account in the Admin Console using SOAP
		AccountItem account = new AccountItem();
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
	 * Testcase : Delete a basic account
	 * Steps :
	 * 1. Create an account using SOAP.
	 * 2. Delete the account using right click in GUI.
	 * @throws HarnessException
	 */
	@Test(	description = "Delete a basic account",
			groups = { "smoke" })
			public void DeleteAccount_02() throws HarnessException {

		// Create a new account in the Admin Console using SOAP
		AccountItem account = new AccountItem();
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
	 * Testcase : Delete a basic account
	 * Steps :
	 * 1. Create an account using SOAP.
	 * 2. Delete the account using GUI.
	 * @throws HarnessException
	 */
	@Test(	description = "Delete a basic account",
			groups = { "functional" })
			public void DeleteAccount_03() throws HarnessException {

		// Create a new account in the Admin Console using SOAP
		AccountItem account = new AccountItem();
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
	 * Testcase : Right click menu : Delete a basic account
	 * Steps :
	 * 1. Create an account using SOAP.
	 * 2. Right click on account to be deleted.
	 * 3. Delete account.
	 * @throws HarnessException
	 */
	@Test(	description = "Right click menu : Delete a basic account",
			groups = { "functional" })
			public void DeleteAccount_04() throws HarnessException {

		// Create a new account in the Admin Console using SOAP
		AccountItem account = new AccountItem();
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