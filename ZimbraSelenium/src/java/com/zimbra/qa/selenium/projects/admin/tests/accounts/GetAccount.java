package com.zimbra.qa.selenium.projects.admin.tests.accounts;

import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAdminAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.items.AccountItem;
import com.zimbra.qa.selenium.projects.admin.ui.PageMain;


public class GetAccount extends AdminCommonTest {

	public GetAccount() {
		logger.info("New "+ GetAccount.class.getCanonicalName());

		// All tests start at the "Accounts" page
		super.startingPage = app.zPageManageAccounts;

	}
	

	/**
	 * Testcase : Verify created account is displayed in UI -- Manage Account View.
	 * Steps :
	 * 1. Create an account using SOAP.
	 * 2. Verify account is present in the list.
	 * @throws HarnessException
	 */
	@Test(	description = "Verify created account is displayed in UI -- Manage Account View.",
			groups = { "smoke" })
	public void GetAccount_01() throws HarnessException {

		// Create a new account in the Admin Console using SOAP
		AccountItem account = new AccountItem("email" + ZimbraSeleniumProperties.getUniqueString(),ZimbraSeleniumProperties.getStringProperty("testdomain"));
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
						"<CreateAccountRequest xmlns='urn:zimbraAdmin'>"
				+			"<name>" + account.getEmailAddress() + "</name>"
				+			"<password>test123</password>"
				+		"</CreateAccountRequest>");

		
		// Refresh the account list
		app.zPageManageAccounts.sClickAt(PageMain.Locators.REFRESH_BUTTON, "");

		
		// Get the list of displayed accounts
		List<AccountItem> accounts = app.zPageManageAccounts.zListGetAccounts();
		ZAssert.assertNotNull(accounts, "Verify the account list is returned");
		
		AccountItem found = null;
		for (AccountItem a : accounts) {
			logger.info("Looking for account "+ account.getEmailAddress() + " found: "+ a.getGEmailAddress());
			if ( account.getEmailAddress().equals(a.getGEmailAddress()) ) {
				found = a;
				break;
			}
		}
		ZAssert.assertNotNull(found, "Verify the account is found");

	}



	/**
	 * Testcase : Verify created account is displayed in UI -- Search list view.
	 * Steps :
	 * 1. Create an account using SOAP.
	 * 2. Search account
	 * 3. Verify account is present in the list.
	 * @throws HarnessException
	 */
	@Test(	description = "Verify created account is displayed in UI -- Search list view",
			groups = { "functional" })
	public void GetAccount_02() throws HarnessException {

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
		
		
		
		// Get the list of displayed accounts
		List<AccountItem> accounts = app.zPageSearchResults.zListGetAccounts();
		ZAssert.assertNotNull(accounts, "Verify the account list is returned");
		
		AccountItem found = null;
		for (AccountItem a : accounts) {
			logger.info("Looking for account "+ account.getEmailAddress() + " found: "+ a.getGEmailAddress());
			if ( account.getEmailAddress().equals(a.getGEmailAddress()) ) {
				found = a;
				break;
			}
		}
		ZAssert.assertNotNull(found, "Verify the account is found");

	}



}
