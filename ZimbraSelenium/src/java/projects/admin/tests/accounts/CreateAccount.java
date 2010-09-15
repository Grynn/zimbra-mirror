package projects.admin.tests.accounts;

import java.util.List;

import org.testng.annotations.Test;

import projects.admin.clients.AccountItem;
import projects.admin.clients.Item;
import projects.admin.tests.CommonTest;
import projects.admin.ui.CreateAccountWizard;
import projects.admin.ui.ManageAccountsPage;

import com.zimbra.common.soap.Element;

import framework.util.HarnessException;
import framework.util.ZAssert;
import framework.util.ZimbraAdminAccount;

public class CreateAccount extends CommonTest {
	
	public CreateAccount() {
		logger.info("New "+ CreateAccount.class.getCanonicalName());
		
		// All tests start at the "Accounts" page
		super.startingPage = app.zManageAccountsPage;

	}
	
	@Test(	description = "Create a basic account",
			groups = { "sanity" })
	public void CreateAccount_01() throws HarnessException {

		// Create a new account in the Admin Console
		AccountItem account = new AccountItem();
		
		// Use the default Create Account method
		app.zManageAccountsPage.createAccount(account);
		
		// Verify the account exists in the ZCS
		ZimbraAdminAccount.GlobalAdmin().soapSend(
				"<GetAccountRequest xmlns='urn:zimbraAdmin'>" +
					"<account by='name'>"+ account.EmailAddress +"</account>" +
				"</GetAccountRequest>");
		Element response = ZimbraAdminAccount.GlobalAdmin().soapSelectNode("//admin:GetAccountResponse", 1);
		ZAssert.assertNotNull(response, "Verify the account is created successfully");
		
		// Verify the account exists in the Account list
		List<Item> accounts = app.zSearchResultsPage.getSearchResults(account.EmailAddress);
		ZAssert.assertTrue(accounts.contains(account), "Verify the new account appears in the list");

	}

	@Test(	description = "Create a basic account using New",
			groups = { "smoke" })
	public void CreateAccount_02() throws HarnessException {

		// Create a new account in the Admin Console
		AccountItem account = new AccountItem();
				
		CreateAccountWizard wizard = 
			app.zManageAccountsPage.getNewAccountWizard(ManageAccountsPage.zb__ACLV__NEW_MENU_title);
		wizard.completeWizard(account);
		
		// Verify the account exists in the ZCS
		ZimbraAdminAccount.GlobalAdmin().soapSend(
				"<GetAccountRequest xmlns='urn:zimbraAdmin'>" +
					"<account by='name'>"+ account.EmailAddress +"</account>" +
				"</GetAccountRequest>");
		Element response = ZimbraAdminAccount.GlobalAdmin().soapSelectNode("//admin:GetAccountResponse", 1);
		ZAssert.assertNotNull(response, "Verify the account is created successfully");

		// Verify the account exists in the Account list
		List<Item> accounts = app.zSearchResultsPage.getSearchResults(account.EmailAddress);
		ZAssert.assertTrue(accounts.contains(account), "Verify the new account appears in the list");

	}

	@Test(	description = "Create a basic account using New->Account",
			groups = { "smoke" })
	public void CreateAccount_03() throws HarnessException {

		// Create a new account in the Admin Console
		AccountItem account = new AccountItem();
				
		CreateAccountWizard wizard = 
			app.zManageAccountsPage.getNewAccountWizard(ManageAccountsPage.zmi__ACLV__NEW_WIZARD_title);
		wizard.completeWizard(account);

		// Verify the account exists in the ZCS
		ZimbraAdminAccount.GlobalAdmin().soapSend(
				"<GetAccountRequest xmlns='urn:zimbraAdmin'>" +
					"<account by='name'>"+ account.EmailAddress +"</account>" +
				"</GetAccountRequest>");
		Element response = ZimbraAdminAccount.GlobalAdmin().soapSelectNode("//admin:GetAccountResponse", 1);
		ZAssert.assertNotNull(response, "Verify the account is created successfully");

		// Verify the account exists in the Account list
		List<Item> accounts = app.zSearchResultsPage.getSearchResults(account.EmailAddress);
		ZAssert.assertTrue(accounts.contains(account), "Verify the new account appears in the list");

	}


}
