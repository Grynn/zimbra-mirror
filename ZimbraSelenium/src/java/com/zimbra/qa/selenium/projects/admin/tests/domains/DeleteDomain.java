package com.zimbra.qa.selenium.projects.admin.tests.domains;

import org.testng.annotations.Test;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAdminAccount;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.items.DomainItem;
import com.zimbra.qa.selenium.projects.admin.ui.DialogForDeleteOperation;

public class DeleteDomain extends AdminCommonTest {
	public DeleteDomain() {
		logger.info("New" + DeleteDomain.class.getCanonicalName());

		//All tests starts from domain page
		this.startingPage=app.zPageManageDomains;
	}

	/**
	 * Testcase : Verify delete domain operation --  Search List View
	 * Steps :
	 * 1. Create a domain using SOAP.
	 * 2. Search domain.
	 * 3. Select a domain.
	 * 4. Delete a domain using delete button in Gear box menu.
	 * 5. Verify domain is deleted using SOAP.
	 * @throws HarnessException
	 */
	@Test(	description = "Verify delete domain operation --  Search List View",
			groups = { "smoke" })
			public void DeleteDomain_01() throws HarnessException {

		// Create a new domain in the Admin Console using SOAP
		DomainItem domain = new DomainItem();

		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<CreateDomainRequest xmlns='urn:zimbraAdmin'>"
				+			"<name>" + domain.getName() + "</name>"
				+		"</CreateDomainRequest>");


		// Enter the search string to find the domain
		app.zPageSearchResults.zAddSearchQuery(domain.getName());

		// Click search
		app.zPageSearchResults.zToolbarPressButton(Button.B_SEARCH);

		// Click on domain to be deleted.
		app.zPageSearchResults.zListItem(Action.A_LEFTCLICK, domain.getName());

		// Click on Delete button
		DialogForDeleteOperation dialog = (DialogForDeleteOperation) app.zPageSearchResults.zToolbarPressPulldown(Button.B_GEAR_BOX, Button.O_DELETE);

		// Click Yes in Confirmation dialog.
		dialog.zClickButton(Button.B_YES);

		// Click Ok on "Delete Items" dialog
		dialog.zClickButton(Button.B_OK);

		// Verify the domain do not exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetDomainRequest xmlns='urn:zimbraAdmin'>"
				+	"<domain by='name'>" + domain.getName() + "</domain>"
				+	"</GetDomainRequest>");


		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetDomainResponse/admin:domain", 1);
		ZAssert.assertNull(response, "Verify the domain is deleted successfully");

	}
	
	/**
	 * Testcase : Verify delete domain operation  -- Search List View/Right Click Menu
	 * Steps :
	 * 1. Create a domain using SOAP.
	 * 2. Search domain.
	 * 3. Right click on domain.
	 * 4. Delete a domain using delete button in right click menu.
	 * 5. Verify domain is deleted using SOAP..
	 * @throws HarnessException
	 */
	@Test(	description = "Verify delete domain operation",
			groups = { "functional" })
			public void DeleteDomain_02() throws HarnessException {

		// Create a new domain in the Admin Console using SOAP
		DomainItem domain = new DomainItem();

		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<CreateDomainRequest xmlns='urn:zimbraAdmin'>"
				+			"<name>" + domain.getName() + "</name>"
				+		"</CreateDomainRequest>");


		// Enter the search string to find the domain
		app.zPageSearchResults.zAddSearchQuery(domain.getName());

		// Click search
		app.zPageSearchResults.zToolbarPressButton(Button.B_SEARCH);

		// Right Click on domain to be deleted.
		app.zPageSearchResults.zListItem(Action.A_RIGHTCLICK, domain.getName());

		// Click on Delete button
		DialogForDeleteOperation dialog = (DialogForDeleteOperation) app.zPageSearchResults.zToolbarPressButton(Button.B_TREE_DELETE);

		// Click Yes in Confirmation dialog.
		dialog.zClickButton(Button.B_YES);

		// Click Ok on "Delete Items" dialog
		dialog.zClickButton(Button.B_OK);

		// Verify the domain do not exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetDomainRequest xmlns='urn:zimbraAdmin'>"
				+	"<domain by='name'>" + domain.getName() + "</domain>"
				+	"</GetDomainRequest>");


		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetDomainResponse/admin:domain", 1);
		ZAssert.assertNull(response, "Verify the domain is deleted successfully");

	}



}
