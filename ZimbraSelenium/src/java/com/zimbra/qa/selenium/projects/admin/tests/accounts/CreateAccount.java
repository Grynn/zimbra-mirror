package com.zimbra.qa.selenium.projects.admin.tests.accounts;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAdminAccount;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.items.AccountItem;
import com.zimbra.qa.selenium.projects.admin.ui.PageManageAccounts;
import com.zimbra.qa.selenium.projects.admin.ui.WizardCreateAccount;


public class CreateAccount extends AdminCommonTest {

	public CreateAccount() {
		logger.info("New "+ CreateAccount.class.getCanonicalName());

		// All tests start at the "Accounts" page
		super.startingPage = app.zPageManageAccounts;

	}


	/**
	 * Testcase : Create a basic account
	 * Steps :
	 * 1. Create an account from GUI.
	 * 2. Verify account is created using SOAP.
	 * @throws HarnessException
	 */
	@Test(	description = "Create a basic account",
			groups = { "sanity" })
			public void CreateAccount_01() throws HarnessException {

		// Create a new account in the Admin Console
		AccountItem account = new AccountItem();

		// Use the default Create Account method
		app.zPageManageAccounts.createAccount(account);

		// Verify the account exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetAccountRequest xmlns='urn:zimbraAdmin'>" +
				"<account by='name'>"+ account.getEmailAddress() +"</account>" +
		"</GetAccountRequest>");
		com.zimbra.common.soap.Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetAccountResponse/admin:account", 1); 
		ZAssert.assertNotNull(response, "Verify the account is created successfully");
	}


	/**
	 * Testcase : Verify created account is displayed in UI.
	 * Steps :
	 * 1. Create an account using SOAP.
	 * 2. Verify account is present in the list.
	 * @throws HarnessException
	 */
	@Test(	description = "Verify created account is present in the account list view",
			groups = { "sanity" })
			public void CreateAccount_02() throws HarnessException {

		// Create a new account in the Admin Console using SOAP
		AccountItem account = new AccountItem();
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<CreateAccountRequest xmlns='urn:zimbraAdmin'> <name>"+account.getEmailAddress()+"</name>"+
				"<password>test123</password>" +
		"</CreateAccountRequest>");

		Boolean accountPresent = app.zPageSearchResults.getSearchResults(account.getEmailAddress());
		ZAssert.assertTrue(accountPresent, "Verify the new account appears in the list");
	}


	/**
	 * Testcase : Create a basic account
	 * Steps :
	 * 1. Create an account from GUI i.e. New -> Account.
	 * 2. Verify account is created using SOAP.
	 * @throws HarnessException
	 */
	@Test(	description = "Create a basic account using New->Account",
			groups = { "sanity" })
			public void CreateAccount_03() throws HarnessException {

		// Create a new account in the Admin Console
		AccountItem account = new AccountItem();

		WizardCreateAccount wizard = 
			app.zPageManageAccounts.getNewAccountWizard(PageManageAccounts.Locators.zmi__ACLV__NEW_WIZARD_title);
		wizard.zCompleteWizard(account);

		// Verify the account exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetAccountRequest xmlns='urn:zimbraAdmin'>" +
				"<account by='name'>"+ account.getEmailAddress() +"</account>" +
		"</GetAccountRequest>");
		com.zimbra.common.soap.Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetAccountResponse/admin:account", 1); 
		ZAssert.assertNotNull(response, "Verify the account is created successfully");


	}


}
