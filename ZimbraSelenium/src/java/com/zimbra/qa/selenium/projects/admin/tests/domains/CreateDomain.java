package com.zimbra.qa.selenium.projects.admin.tests.domains;

import org.testng.annotations.Test;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAdminAccount;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.items.DomainItem;
import com.zimbra.qa.selenium.projects.admin.ui.WizardCreateDomain;

public class CreateDomain extends AdminCommonTest {
	
	
	public CreateDomain() {
		logger.info("New " + CreateDomain.class.getName());
		
		super.startingPage=app.zPageManageDomains;
	}
	
	/**
	 * Testcase : Create a simple domain
	 * Steps :
	 * 1. Create a domain from GUI
	 * 2. Verify domain is created using SOAP.
	 * @throws HarnessException
	 */
	@Test(	description = "Create a simple domain",
			groups = { "sanity" })
			public void CreateDomain_01() throws HarnessException {

		// Create a new domain in the Admin Console
		DomainItem domain = new DomainItem();



		// Click "New" -> "Domain"
		WizardCreateDomain wizard = 
			(WizardCreateDomain)app.zPageManageDomains.zToolbarPressPulldown(Button.B_GEAR_BOX, Button.O_NEW);

		// Fill out the wizard and click Finish
		wizard.zCompleteWizard(domain);

		// Verify the domain exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetDomainRequest xmlns='urn:zimbraAdmin'>"
			+	"<domain by='name'>" + domain.getName() + "</domain>"
			+	"</GetDomainRequest>");


		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetDomainResponse/admin:domain", 1);
		ZAssert.assertNotNull(response, "Verify the domain is created successfully");
	}
}
