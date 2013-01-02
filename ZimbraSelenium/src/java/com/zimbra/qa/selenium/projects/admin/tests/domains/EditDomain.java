package com.zimbra.qa.selenium.projects.admin.tests.domains;

import org.testng.annotations.Test;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAdminAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.items.DomainItem;
import com.zimbra.qa.selenium.projects.admin.ui.FormEditDomain;
import com.zimbra.qa.selenium.projects.admin.ui.PageMain;
import com.zimbra.qa.selenium.projects.admin.ui.PageSearchResults;

public class EditDomain extends AdminCommonTest {
	public EditDomain() {
		logger.info("New "+ EditDomain.class.getCanonicalName());

		// All tests start at the "Accounts" page
		super.startingPage = app.zPageManageDomains;

	}

	/**
	 * Testcase : Verify delete domain operation --  Manage Domain List View
	 * Steps :
	 * 1. Create a domain using SOAP.
	 * 2. Select a domain.
	 * 4. Edit an domain using edit button in Gear box menu.
	 * 5. Verify domain is edited using SOAP.
	 * @throws HarnessException
	 */
	@Test(	description = "Verify delete domain operation --  Manage Domain List View",
			groups = { "smoke" })
			public void EditDomain_01() throws HarnessException {
	
		// Create a new domain in the Admin Console using SOAP
		DomainItem domain = new DomainItem();
		String domainName=domain.getName();
	
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<CreateDomainRequest xmlns='urn:zimbraAdmin'>"
				+			"<name>" + domainName + "</name>"
				+		"</CreateDomainRequest>");
	
	
		// Refresh the domain list
		app.zPageManageDomains.sClickAt(PageMain.Locators.REFRESH_BUTTON, "");
	
		// Click on account to be deleted.
		app.zPageManageDomains.zListItem(Action.A_LEFTCLICK, domain.getName());
		
		FormEditDomain form = (FormEditDomain) app.zPageManageDomains.zToolbarPressPulldown(Button.B_GEAR_BOX, Button.O_EDIT);
		
		//Click on General Information tab.
		form.zClickTreeItem(FormEditDomain.TreeItem.GENERAL_INFORMATION);

		//Edit the name.
		String description = "editeddomain_" + ZimbraSeleniumProperties.getUniqueString();
		form.setName(description);
		
		//Submit the form.
		form.zSubmit();
		
		// Verify the domain exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetDomainRequest xmlns='urn:zimbraAdmin'>"
				+	"<domain by='name'>" + domainName + "</domain>"
				+	"</GetDomainRequest>");


		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetDomainResponse/admin:domain/admin:a[@n='description']", 1);
		ZAssert.assertStringContains(response.toString(), description, "Verify description is edited correctly");
	
	}

	/**
	 * Testcase : Verify delete domain operation  -- Manage Domain List View/Right Click Menu
	 * Steps :
	 * 1. Create a domain using SOAP.
	 * 2. Right click on domain.
	 * 3. Delete a domain using delete button in right click menu.
	 * 4. Verify domain is deleted using SOAP..
	 * @throws HarnessException
	 */
	@Test(	description = "Verify delete domain operation",
			groups = { "functional" })
			public void EditDomain_02() throws HarnessException {
	
		// Create a new domain in the Admin Console using SOAP
		DomainItem domain = new DomainItem();
		String domainName=domain.getName();
	
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<CreateDomainRequest xmlns='urn:zimbraAdmin'>"
				+			"<name>" + domainName + "</name>"
				+		"</CreateDomainRequest>");
	
	
		// Refresh the domain list
		app.zPageManageDomains.sClickAt(PageMain.Locators.REFRESH_BUTTON, "");
	
		// Click on account to be deleted.
		app.zPageManageDomains.zListItem(Action.A_RIGHTCLICK, domain.getName());
		
		FormEditDomain form = (FormEditDomain) app.zPageManageDomains.zToolbarPressButton(Button.B_TREE_EDIT);
		
		//Click on General Information tab.
		form.zClickTreeItem(FormEditDomain.TreeItem.GENERAL_INFORMATION);

		//Edit the name.
		String description = "editeddomain_" + ZimbraSeleniumProperties.getUniqueString();
		form.setName(description);
		
		//Submit the form.
		form.zSubmit();
		
		// Verify the domain exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetDomainRequest xmlns='urn:zimbraAdmin'>"
				+	"<domain by='name'>" + domainName + "</domain>"
				+	"</GetDomainRequest>");

		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetDomainResponse/admin:domain/admin:a[@n='description']", 1);
		ZAssert.assertStringContains(response.toString(), description, "Verify description is edited correctly");
	}

	/**
	 * Testcase : Edit domain name  - Search list view
	 * Steps :
	 * 1. Create an domain using SOAP.
	 * 2. Go to search domain View
	 * 3. Select a domain.
	 * 4. Edit an domain using edit button in Gear box menu.
	 * 5. Verify domain is edited using SOAP.
	 * @throws HarnessException
	 */
	@Test(	description = "Edit domain name  - Search list View",
			groups = { "functional" })
			public void Editdomain_03() throws HarnessException {

		// Create a new domain in the Admin Console using SOAP
		DomainItem domain = new DomainItem();
		String domainName=domain.getName();

		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<CreateDomainRequest xmlns='urn:zimbraAdmin'>"
		+			"<name>" + domainName + "</name>"
		+		"</CreateDomainRequest>");
		
		// Enter the search string to find the account
		app.zPageSearchResults.zAddSearchQuery(domainName);

		// Click search
		app.zPageSearchResults.zToolbarPressButton(Button.B_SEARCH);
		
		// Click on domain to be deleted.
		app.zPageSearchResults.zListItem(Action.A_LEFTCLICK, domain.getName());

		// Click on Edit button
		app.zPageSearchResults.setType(PageSearchResults.TypeOfObject.DOMAIN);
		FormEditDomain form = (FormEditDomain) app.zPageSearchResults.zToolbarPressPulldown(Button.B_GEAR_BOX, Button.O_EDIT);
		
		//Click on General Information tab.
		form.zClickTreeItem(FormEditDomain.TreeItem.GENERAL_INFORMATION);

		//Edit the name.
		String description = "editeddomain_" + ZimbraSeleniumProperties.getUniqueString();
		form.setName(description);
		
		//Submit the form.
		form.zSubmit();
		
		// Verify the domain exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetDomainRequest xmlns='urn:zimbraAdmin'>"
				+	"<domain by='name'>" + domainName + "</domain>"
				+	"</GetDomainRequest>");


		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetDomainResponse/admin:domain/admin:a[@n='description']", 1);
		ZAssert.assertStringContains(response.toString(), description, "Verify description is edited correctly");
		
	}
	
	/**
	 * Testcase : Edit domain name -- right click 
	 * Steps :
	 * 1. Create an domain using SOAP.
	 * 2. Edit the domain name using UI Right Click.
	 * 3. Verify domain name is changed using SOAP.
	 * @throws HarnessException
	 */
	@Test(	description = "Edit domain name -- right click",
			groups = { "functional" })
			public void Editdomain_04() throws HarnessException {
		// Create a new domain in the Admin Console using SOAP
		DomainItem domain = new DomainItem();
		String domainName=domain.getName();

		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<CreateDomainRequest xmlns='urn:zimbraAdmin'>"
		+			"<name>" + domainName + "</name>"
		+		"</CreateDomainRequest>");

		// Enter the search string to find the account
		app.zPageSearchResults.zAddSearchQuery(domainName);

		// Click search
		app.zPageSearchResults.zToolbarPressButton(Button.B_SEARCH);
		
		// Click on domain to be deleted.
		app.zPageSearchResults.zListItem(Action.A_RIGHTCLICK, domain.getName());

		// Click on Edit button
		app.zPageSearchResults.setType(PageSearchResults.TypeOfObject.DOMAIN);
		FormEditDomain form = (FormEditDomain) app.zPageSearchResults.zToolbarPressButton(Button.B_TREE_EDIT);
		
		//Click on General Information tab.
		form.zClickTreeItem(FormEditDomain.TreeItem.GENERAL_INFORMATION);

		//Edit the name.
		String editedName = "editeddomain_" + ZimbraSeleniumProperties.getUniqueString();
		form.setName(editedName);
		
		//Submit the form.
		form.zSubmit();
		
		// Verify the domain exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetDomainRequest xmlns='urn:zimbraAdmin'>"
				+	"<domain by='name'>" + domainName + "</domain>"
				+	"</GetDomainRequest>");


		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetDomainResponse/admin:domain", 1);
		ZAssert.assertNull(response, "https://bugzilla.zimbra.com/show_bug.cgi?id=79304");
	}

}
