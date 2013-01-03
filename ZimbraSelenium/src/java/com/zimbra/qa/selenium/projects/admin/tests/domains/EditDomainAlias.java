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
import com.zimbra.qa.selenium.projects.admin.ui.PageMain;
import com.zimbra.qa.selenium.projects.admin.ui.PageManageDomains;
import com.zimbra.qa.selenium.projects.admin.ui.PageSearchResults;
import com.zimbra.qa.selenium.projects.admin.ui.WizardCreateDomainAlias;

public class EditDomainAlias extends AdminCommonTest {
	public EditDomainAlias() {
		logger.info("New "+ EditDomainAlias.class.getCanonicalName());

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
			public void EditDomainAlias_01() throws HarnessException {
		
		
		String targetDomain = ZimbraSeleniumProperties.getStringProperty("testdomain");
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetDomainRequest xmlns='urn:zimbraAdmin'>"
				+	"<domain by='name'>" + targetDomain + "</domain>"
				+	"</GetDomainRequest>");

		String targetDomainID=ZimbraAdminAccount.AdminConsoleAdmin().soapSelectValue("//admin:GetDomainResponse/admin:domain", "id").toString();
		
		
		// Create a new domain alias in the Admin Console using SOAP
		DomainItem alias = new DomainItem();
		String domainAliasName=alias.getName();
		
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<CreateDomainRequest xmlns='urn:zimbraAdmin'>"
				+ "<name>"+domainAliasName+"</name>"
				+ "<a n='zimbraDomainType'>alias</a>"
				+ "<a n='zimbraDomainAliasTargetId'>"+targetDomainID+"</a>"
				+ "<a n='description'>"+"domain alias of zqa-062.eng.vmware.com"+"</a>"
				+ "<a n='zimbraMailCatchAllAddress'>@"+domainAliasName+"</a>" 
				+ "<a  n='zimbraMailCatchAllForwardingAddress'>@"+targetDomain+"</a>"
				+ "</CreateDomainRequest>");
				
		// Refresh the domain list
		app.zPageManageDomains.sClickAt(PageMain.Locators.REFRESH_BUTTON, "");

		// Click on account to be edited.
		app.zPageManageDomains.zListItem(Action.A_LEFTCLICK, domainAliasName);
		
		//Set object type and initialize creatdomainalias wizard
		app.zPageManageDomains.setType(PageManageDomains.TypeOfObject.DOMAIN_ALIAS);
		WizardCreateDomainAlias wizard = (WizardCreateDomainAlias) app.zPageManageDomains.zToolbarPressPulldown(Button.B_GEAR_BOX, Button.O_EDIT);
		
		wizard.zSetTargetDomain(alias);
		
		// Verify the domain exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetDomainRequest xmlns='urn:zimbraAdmin'>"
				+	"<domain by='name'>" + domainAliasName + "</domain>"
				+	"</GetDomainRequest>");


		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetDomainResponse/admin:domain/admin:a[@n='zimbraMailCatchAllForwardingAddress']", 1);
		ZAssert.assertStringContains(response.toString(), ZimbraSeleniumProperties.getStringProperty("server.host"), "Verify description is edited correctly");
	
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
			public void EditDomainAlias_02() throws HarnessException {
		
		
		String targetDomain = ZimbraSeleniumProperties.getStringProperty("testdomain");
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetDomainRequest xmlns='urn:zimbraAdmin'>"
				+	"<domain by='name'>" + targetDomain + "</domain>"
				+	"</GetDomainRequest>");

		String targetDomainID=ZimbraAdminAccount.AdminConsoleAdmin().soapSelectValue("//admin:GetDomainResponse/admin:domain", "id").toString();
		
		
		// Create a new domain alias in the Admin Console using SOAP
		DomainItem alias = new DomainItem();
		String domainAliasName=alias.getName();
		
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<CreateDomainRequest xmlns='urn:zimbraAdmin'>"
				+ "<name>"+domainAliasName+"</name>"
				+ "<a n='zimbraDomainType'>alias</a>"
				+ "<a n='zimbraDomainAliasTargetId'>"+targetDomainID+"</a>"
				+ "<a n='description'>"+"domain alias of zqa-062.eng.vmware.com"+"</a>"
				+ "<a n='zimbraMailCatchAllAddress'>@"+domainAliasName+"</a>" 
				+ "<a  n='zimbraMailCatchAllForwardingAddress'>@"+targetDomain+"</a>"
				+ "</CreateDomainRequest>");
				
		// Refresh the domain list
		app.zPageManageDomains.sClickAt(PageMain.Locators.REFRESH_BUTTON, "");

		// Click on account to be edited.
		app.zPageManageDomains.zListItem(Action.A_RIGHTCLICK, domainAliasName);
		
		//Set object type and initialize creatdomainalias wizard
		app.zPageManageDomains.setType(PageManageDomains.TypeOfObject.DOMAIN_ALIAS);
		WizardCreateDomainAlias wizard = (WizardCreateDomainAlias) app.zPageManageDomains.zToolbarPressButton(Button.B_TREE_EDIT);
		
		wizard.zSetTargetDomain(alias);
		
		// Verify the domain exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetDomainRequest xmlns='urn:zimbraAdmin'>"
				+	"<domain by='name'>" + domainAliasName + "</domain>"
				+	"</GetDomainRequest>");


		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetDomainResponse/admin:domain/admin:a[@n='zimbraMailCatchAllForwardingAddress']", 1);
		ZAssert.assertStringContains(response.toString(), ZimbraSeleniumProperties.getStringProperty("server.host"), "Verify description is edited correctly");
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
			public void EditdomainAlias_03() throws HarnessException {
		
		
		String targetDomain = ZimbraSeleniumProperties.getStringProperty("testdomain");
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetDomainRequest xmlns='urn:zimbraAdmin'>"
				+	"<domain by='name'>" + targetDomain + "</domain>"
				+	"</GetDomainRequest>");

		String targetDomainID=ZimbraAdminAccount.AdminConsoleAdmin().soapSelectValue("//admin:GetDomainResponse/admin:domain", "id").toString();
		
		
		// Create a new domain alias in the Admin Console using SOAP
		DomainItem alias = new DomainItem();
		String domainAliasName=alias.getName();
		
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<CreateDomainRequest xmlns='urn:zimbraAdmin'>"
				+ "<name>"+domainAliasName+"</name>"
				+ "<a n='zimbraDomainType'>alias</a>"
				+ "<a n='zimbraDomainAliasTargetId'>"+targetDomainID+"</a>"
				+ "<a n='description'>"+"domain alias of zqa-062.eng.vmware.com"+"</a>"
				+ "<a n='zimbraMailCatchAllAddress'>@"+domainAliasName+"</a>" 
				+ "<a  n='zimbraMailCatchAllForwardingAddress'>@"+targetDomain+"</a>"
				+ "</CreateDomainRequest>");
				
		// Enter the search string to find the alias
		app.zPageSearchResults.zAddSearchQuery(domainAliasName);

		// Click search
		app.zPageSearchResults.zToolbarPressButton(Button.B_SEARCH);

		// Click on alias to be edited.
		app.zPageSearchResults.zListItem(Action.A_LEFTCLICK, domainAliasName);


		// Click on Edit button
		app.zPageSearchResults.setType(PageSearchResults.TypeOfObject.DOMAIN_ALIAS);
		WizardCreateDomainAlias wizard = (WizardCreateDomainAlias) app.zPageSearchResults.zToolbarPressPulldown(Button.B_GEAR_BOX, Button.O_EDIT);

		wizard.zSetTargetDomain(alias);
		
		// Verify the domain exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetDomainRequest xmlns='urn:zimbraAdmin'>"
				+	"<domain by='name'>" + domainAliasName + "</domain>"
				+	"</GetDomainRequest>");


		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetDomainResponse/admin:domain/admin:a[@n='zimbraMailCatchAllForwardingAddress']", 1);
		ZAssert.assertStringContains(response.toString(), ZimbraSeleniumProperties.getStringProperty("server.host"), "http://bugzilla.zimbra.com/show_bug.cgi?id=79446");
		
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
			public void EditdomainAlias_04() throws HarnessException {
		
		
		String targetDomain = ZimbraSeleniumProperties.getStringProperty("testdomain");
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetDomainRequest xmlns='urn:zimbraAdmin'>"
				+	"<domain by='name'>" + targetDomain + "</domain>"
				+	"</GetDomainRequest>");

		String targetDomainID=ZimbraAdminAccount.AdminConsoleAdmin().soapSelectValue("//admin:GetDomainResponse/admin:domain", "id").toString();
		
		
		// Create a new domain alias in the Admin Console using SOAP
		DomainItem alias = new DomainItem();
		String domainAliasName=alias.getName();
		
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<CreateDomainRequest xmlns='urn:zimbraAdmin'>"
				+ "<name>"+domainAliasName+"</name>"
				+ "<a n='zimbraDomainType'>alias</a>"
				+ "<a n='zimbraDomainAliasTargetId'>"+targetDomainID+"</a>"
				+ "<a n='description'>"+"domain alias of zqa-062.eng.vmware.com"+"</a>"
				+ "<a n='zimbraMailCatchAllAddress'>@"+domainAliasName+"</a>" 
				+ "<a  n='zimbraMailCatchAllForwardingAddress'>@"+targetDomain+"</a>"
				+ "</CreateDomainRequest>");
				
		// Enter the search string to find the alias
		app.zPageSearchResults.zAddSearchQuery(domainAliasName);

		// Click search
		app.zPageSearchResults.zToolbarPressButton(Button.B_SEARCH);

		// Click on alias to be edited.
		app.zPageSearchResults.zListItem(Action.A_LEFTCLICK, domainAliasName);


		// Click on Edit button
		app.zPageSearchResults.setType(PageSearchResults.TypeOfObject.DOMAIN_ALIAS);
		WizardCreateDomainAlias wizard = (WizardCreateDomainAlias) app.zPageSearchResults.zToolbarPressButton(Button.B_TREE_EDIT);

		wizard.zSetTargetDomain(alias);
		
		// Verify the domain exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetDomainRequest xmlns='urn:zimbraAdmin'>"
				+	"<domain by='name'>" + domainAliasName + "</domain>"
				+	"</GetDomainRequest>");

		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetDomainResponse/admin:domain/admin:a[@n='zimbraMailCatchAllForwardingAddress']", 1);
		ZAssert.assertStringContains(response.toString(), ZimbraSeleniumProperties.getStringProperty("server.host"), "http://bugzilla.zimbra.com/show_bug.cgi?id=79446");
		
	}

}
