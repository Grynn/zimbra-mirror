package com.zimbra.qa.selenium.projects.admin.tests.aliases;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.items.*;
import com.zimbra.qa.selenium.projects.admin.ui.WizardCreateAlias;


public class CreateAlias extends AdminCommonTest {
	
	public CreateAlias() {
		logger.info("New "+ CreateAlias.class.getCanonicalName());
		
		// All tests start at the "Aliases" page
		super.startingPage=app.zPageManageAliases;
	}
	
	/**
	 * Testcase : Create a basic alias.
	 * 1. Create a alias with GUI.
	 * 2. Verify alias is created using SOAP.
	 * @throws HarnessException
	 */
	
	@Test(	description = "Create a basic alias",
			groups = { "sanity" })
	public void CreateAlias_01() throws HarnessException {
		
		// Create a new account to associate the alias with
		AccountItem account = new AccountItem();
		AccountItem.createUsingSOAP(account);
		
		// Create a new account in the Admin Console
		AliasItem alias = new AliasItem();		// Create a new account in the Admin Console using SOAP
		alias.setTargetAccountEmail(account.getEmailAddress());


		// Click "New"
		WizardCreateAlias wizard = 
			(WizardCreateAlias)app.zPageManageAliases.zToolbarPressButton(Button.B_NEW);
		
		// Fill out the wizard	
		wizard.zCompleteWizard(alias);

		// Verify the alias exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
						"<GetAccountRequest xmlns='urn:zimbraAdmin'>"
				+			"<account by='name'>"+ alias.getEmailAddress() +"</account>"
				+		"</GetAccountRequest>");
		String email = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectValue("//admin:account", "name");
		ZAssert.assertEquals(email, account.getEmailAddress(), "Verify the alias is associated with the correct account");
				
		
	}


}
