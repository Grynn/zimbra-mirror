package com.zimbra.qa.selenium.projects.admin.tests.resources;

import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAdminAccount;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.items.AccountItem;
import com.zimbra.qa.selenium.projects.admin.items.ResourceItem;
import com.zimbra.qa.selenium.projects.admin.ui.DialogForDeleteOperation;
import com.zimbra.qa.selenium.projects.admin.ui.PageMain;

public class DeleteResource extends AdminCommonTest {

	public DeleteResource() {
		logger.info("New "+ DeleteResource.class.getCanonicalName());

		// All tests start at the "Resources" page
		super.startingPage = app.zPageManageResources;

	}
	
	/**
	 * Testcase : Verify delete resource operation -- Manage resource View -- Location
	 * Steps :
	 * 1. Create a resource using SOAP.
	 * 2. Go to Manage resource View.
	 * 3. Select a resource.
	 * 4. Delete a resource using delete button in Gear box menu.
	 * 5. Verify resource is deleted using SOAP.
	 * @throws HarnessException
	 */
	@Test(	description = "Verify delete resource operation -- Manage resource View -- Location",
			groups = { "smoke" })
	public void DeleteResource_01() throws HarnessException {

		// Create a new resource in the Admin Console using SOAP
		ResourceItem resource = new ResourceItem();
		
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
						"<CreateCalendarResourceRequest xmlns='urn:zimbraAdmin'>"
				 		+ "<name>" + resource.getEmailAddress() + "</name>"
				 		+ "<a n=\"displayName\">" + resource.getName() + "</a>"
				 		+ "<a n=\"zimbraCalResType\">" + "Location" + "</a>"
				 		+ "<password>test123</password>"
				 		+ "</CreateCalendarResourceRequest>");
		
		// Refresh list to populate account.
		app.zPageManageResources.sClickAt(PageMain.Locators.REFRESH_BUTTON, "");

		// Click on account to be deleted.
		app.zPageManageResources.zListItem(Action.A_LEFTCLICK, resource.getEmailAddress());

		// Click on Delete button
		DialogForDeleteOperation dialog = (DialogForDeleteOperation) app.zPageManageResources.zToolbarPressPulldown(Button.B_GEAR_BOX, Button.O_DELETE);

		// Click Yes in Confirmation dialog
		dialog.zClickButton(Button.B_YES);

		// Click Ok on "Delete Items" dialog
		dialog.zClickButton(Button.B_OK);
		
		// Get the list of displayed accounts
		List<AccountItem> accounts = app.zPageManageResources.zListGetAccounts();
		ZAssert.assertNotNull(accounts, "Verify the resource list is returned");
		
		AccountItem found = null;
		for (AccountItem a : accounts) {
			logger.info("Looking for resource "+ resource.getEmailAddress() + " found: "+ a.getGEmailAddress());
			if ( resource.getEmailAddress().equals(a.getGEmailAddress()) ) {
				found = a;
				break;
			}
		}
		ZAssert.assertNull(found, "Verify the resource is deleted successfully");

	}
	
	/**
	 * Testcase : Verify delete resource operation -- Manage resource View -- Equipment
	 * Steps :
	 * 1. Create a resource using SOAP.
	 * 2. Go to Manage resource View.
	 * 3. Select a resource.
	 * 4. Delete a resource using delete button in Gear box menu.
	 * 5. Verify resource is deleted using SOAP.
	 * @throws HarnessException
	 */
	@Test(	description = "Verify delete resource operation -- Manage resource View -- Equipment",
			groups = { "smoke" })
	public void DeleteResource_02() throws HarnessException {

		// Create a new resource in the Admin Console using SOAP
		ResourceItem resource = new ResourceItem();
		
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
						"<CreateCalendarResourceRequest xmlns='urn:zimbraAdmin'>"
				 		+ "<name>" + resource.getEmailAddress() + "</name>"
				 		+ "<a n=\"displayName\">" + resource.getName() + "</a>"
				 		+ "<a n=\"zimbraCalResType\">" + "Equipment" + "</a>"
				 		+ "<password>test123</password>"
				 		+ "</CreateCalendarResourceRequest>");
		
		// Refresh list to populate account.
		app.zPageManageResources.sClickAt(PageMain.Locators.REFRESH_BUTTON, "");

		// Click on account to be deleted.
		app.zPageManageResources.zListItem(Action.A_LEFTCLICK, resource.getEmailAddress());

		// Click on Delete button
		DialogForDeleteOperation dialog = (DialogForDeleteOperation) app.zPageManageResources.zToolbarPressPulldown(Button.B_GEAR_BOX, Button.O_DELETE);

		// Click Yes in Confirmation dialog
		dialog.zClickButton(Button.B_YES);

		// Click Ok on "Delete Items" dialog
		dialog.zClickButton(Button.B_OK);
		
		// Get the list of displayed accounts
		List<AccountItem> accounts = app.zPageManageResources.zListGetAccounts();
		ZAssert.assertNotNull(accounts, "Verify the resource list is returned");
		
		AccountItem found = null;
		for (AccountItem a : accounts) {
			logger.info("Looking for resource "+ resource.getEmailAddress() + " found: "+ a.getGEmailAddress());
			if ( resource.getEmailAddress().equals(a.getGEmailAddress()) ) {
				found = a;
				break;
			}
		}
		ZAssert.assertNull(found, "Verify the resource is deleted successfully");

	}
	
	/**
	 * Testcase : Verify delete resource operation -- Manage resource View/Right Click Menu -- Location
	 * Steps :
	 * 1. Create a resource using SOAP.
	 * 2. Go to Manage resource View.
	 * 3. Right Click on a resource.
	 * 4. Delete a resource using delete button in right click menu.
	 * 5. Verify resource is deleted using SOAP.
	 * @throws HarnessException
	 */
	@Test(	description = "Verify delete resource operation -- Manage resource View/Right Click Menu -- Location",
			groups = { "smoke" })
	public void DeleteResource_03() throws HarnessException {

		// Create a new resource in the Admin Console using SOAP
		ResourceItem resource = new ResourceItem();
		
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
						"<CreateCalendarResourceRequest xmlns='urn:zimbraAdmin'>"
				 		+ "<name>" + resource.getEmailAddress() + "</name>"
				 		+ "<a n=\"displayName\">" + resource.getName() + "</a>"
				 		+ "<a n=\"zimbraCalResType\">" + "Location" + "</a>"
				 		+ "<password>test123</password>"
				 		+ "</CreateCalendarResourceRequest>");
		
		// Refresh list to populate account.
		app.zPageManageResources.sClickAt(PageMain.Locators.REFRESH_BUTTON, "");

		// Right Click on resource to be deleted.
		app.zPageManageResources.zListItem(Action.A_RIGHTCLICK, resource.getEmailAddress());

		// Click on Delete button
		DialogForDeleteOperation dialog = (DialogForDeleteOperation) app.zPageManageResources.zToolbarPressButton(Button.B_TREE_DELETE);

		// Click Yes in Confirmation dialog
		dialog.zClickButton(Button.B_YES);

		// Click Ok on "Delete Items" dialog
		dialog.zClickButton(Button.B_OK);
		
		// Get the list of displayed accounts
		List<AccountItem> accounts = app.zPageManageResources.zListGetAccounts();
		ZAssert.assertNotNull(accounts, "Verify the resource list is returned");
		
		AccountItem found = null;
		for (AccountItem a : accounts) {
			logger.info("Looking for resource "+ resource.getEmailAddress() + " found: "+ a.getGEmailAddress());
			if ( resource.getEmailAddress().equals(a.getGEmailAddress()) ) {
				found = a;
				break;
			}
		}
		ZAssert.assertNull(found, "Verify the resource is deleted successfully");

	}
	
	/**
	 * Testcase : Verify delete resource operation -- Manage resource View/Right Click Menu -- Equipment
	 * Steps :
	 * 1. Create a resource using SOAP.
	 * 2. Go to Manage resource View.
	 * 3. Right Click on a resource.
	 * 4. Delete a resource using delete button in right click menu.
	 * 5. Verify resource is deleted using SOAP.
	 * @throws HarnessException
	 */
	@Test(	description = "Verify delete resource operation -- Manage resource View/Right Click Menu -- Equipment",
			groups = { "smoke" })
	public void DeleteResource_04() throws HarnessException {

		// Create a new resource in the Admin Console using SOAP
		ResourceItem resource = new ResourceItem();
		
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
						"<CreateCalendarResourceRequest xmlns='urn:zimbraAdmin'>"
				 		+ "<name>" + resource.getEmailAddress() + "</name>"
				 		+ "<a n=\"displayName\">" + resource.getName() + "</a>"
				 		+ "<a n=\"zimbraCalResType\">" + "Equipment" + "</a>"
				 		+ "<password>test123</password>"
				 		+ "</CreateCalendarResourceRequest>");
		
		// Refresh list to populate account.
		app.zPageManageResources.sClickAt(PageMain.Locators.REFRESH_BUTTON, "");

		// Right Click on resource to be deleted.
		app.zPageManageResources.zListItem(Action.A_RIGHTCLICK, resource.getEmailAddress());

		// Click on Delete button
		DialogForDeleteOperation dialog = (DialogForDeleteOperation) app.zPageManageResources.zToolbarPressButton(Button.B_TREE_DELETE);

		// Click Yes in Confirmation dialog
		dialog.zClickButton(Button.B_YES);

		// Click Ok on "Delete Items" dialog
		dialog.zClickButton(Button.B_OK);
		
		// Get the list of displayed accounts
		List<AccountItem> accounts = app.zPageManageResources.zListGetAccounts();
		ZAssert.assertNotNull(accounts, "Verify the resource list is returned");
		
		AccountItem found = null;
		for (AccountItem a : accounts) {
			logger.info("Looking for resource "+ resource.getEmailAddress() + " found: "+ a.getGEmailAddress());
			if ( resource.getEmailAddress().equals(a.getGEmailAddress()) ) {
				found = a;
				break;
			}
		}
		ZAssert.assertNull(found, "Verify the resource is deleted successfully");

	}


	/**
	 * Testcase : Verify delete resource operation -- Search List View -- Location
	 * Steps :
	 * 1. Create a resource using SOAP.
	 * 2. Search resource.
	 * 3. Select a resource.
	 * 4. Delete a resource using delete button in Gear box menu.
	 * 5. Verify resource is deleted using SOAP.
	 * @throws HarnessException
	 */
	@Test(	description = "Verify delete resource operation -- Search List View -- Location",
			groups = { "functional" })
	public void DeleteResource_05() throws HarnessException {

		// Create a new resource in the Admin Console using SOAP
		ResourceItem resource = new ResourceItem();
		
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
						"<CreateCalendarResourceRequest xmlns='urn:zimbraAdmin'>"
				 		+ "<name>" + resource.getEmailAddress() + "</name>"
				 		+ "<a n=\"displayName\">" + resource.getName() + "</a>"
				 		+ "<a n=\"zimbraCalResType\">" + "Location" + "</a>"
				 		+ "<password>test123</password>"
				 		+ "</CreateCalendarResourceRequest>");
		
		// Enter the search string to find the account
		app.zPageSearchResults.zAddSearchQuery(resource.getEmailAddress());
		
		// Click search
		app.zPageSearchResults.zToolbarPressButton(Button.B_SEARCH);
		
		// Click on resource to be deleted.
		app.zPageSearchResults.zListItem(Action.A_LEFTCLICK, resource.getEmailAddress());

		// Click on Delete button
		DialogForDeleteOperation dialog = (DialogForDeleteOperation) app.zPageSearchResults.zToolbarPressPulldown(Button.B_GEAR_BOX, Button.O_DELETE);

		// Click Yes in Confirmation dialog
		dialog.zClickButton(Button.B_YES);

		// Click Ok on "Delete Items" dialog
		dialog.zClickButton(Button.B_OK);
		
		// Get the list of displayed accounts
		List<AccountItem> accounts = app.zPageSearchResults.zListGetAccounts();
		ZAssert.assertNotNull(accounts, "Verify the resource list is returned");
		
		AccountItem found = null;
		for (AccountItem a : accounts) {
			logger.info("Looking for resource "+ resource.getEmailAddress() + " found: "+ a.getGEmailAddress());
			if ( resource.getEmailAddress().equals(a.getGEmailAddress()) ) {
				found = a;
				break;
			}
		}
		ZAssert.assertNull(found, "Verify the resource is deleted successfully");

	}
	
	/**
	 * Testcase : Verify delete resource operation -- Search List View -- Equipment
	 * Steps :
	 * 1. Create a resource using SOAP.
	 * 2. Search resource.
	 * 3. Select a resource.
	 * 4. Delete a resource using delete button in Gear box menu.
	 * 5. Verify resource is deleted using SOAP.
	 * @throws HarnessException
	 */
	@Test(	description = "Verify delete resource operation -- Search List View -- Equipment",
			groups = { "functional" })
	public void DeleteResource_06() throws HarnessException {

		// Create a new resource in the Admin Console using SOAP
		ResourceItem resource = new ResourceItem();
		
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
						"<CreateCalendarResourceRequest xmlns='urn:zimbraAdmin'>"
				 		+ "<name>" + resource.getEmailAddress() + "</name>"
				 		+ "<a n=\"displayName\">" + resource.getName() + "</a>"
				 		+ "<a n=\"zimbraCalResType\">" + "Equipment" + "</a>"
				 		+ "<password>test123</password>"
				 		+ "</CreateCalendarResourceRequest>");
		
		// Enter the search string to find the account
		app.zPageSearchResults.zAddSearchQuery(resource.getEmailAddress());
		
		// Click search
		app.zPageSearchResults.zToolbarPressButton(Button.B_SEARCH);
		
		// Click on resource to be deleted.
		app.zPageSearchResults.zListItem(Action.A_LEFTCLICK, resource.getEmailAddress());

		// Click on Delete button
		DialogForDeleteOperation dialog = (DialogForDeleteOperation) app.zPageSearchResults.zToolbarPressPulldown(Button.B_GEAR_BOX, Button.O_DELETE);

		// Click Yes in Confirmation dialog
		dialog.zClickButton(Button.B_YES);

		// Click Ok on "Delete Items" dialog
		dialog.zClickButton(Button.B_OK);
		
		// Get the list of displayed accounts
		List<AccountItem> accounts = app.zPageSearchResults.zListGetAccounts();
		ZAssert.assertNotNull(accounts, "Verify the resource list is returned");
		
		AccountItem found = null;
		for (AccountItem a : accounts) {
			logger.info("Looking for resource "+ resource.getEmailAddress() + " found: "+ a.getGEmailAddress());
			if ( resource.getEmailAddress().equals(a.getGEmailAddress()) ) {
				found = a;
				break;
			}
		}
		ZAssert.assertNull(found, "Verify the resource is deleted successfully");

	}
	
	/**
	 * Testcase : Verify delete resource operation -- Search List View/Right Click Menu -- Location
	 * Steps :
	 * 1. Create a resource using SOAP.
	 * 2. Search resource.
	 * 3. Right click on resource.
	 * 4. Delete a resource using delete button in right click menu.
	 * 5. Verify resource is deleted using SOAP.
	 * @throws HarnessException
	 */
	@Test(	description = "Verify delete resource operation -- Search List View/Right Click Menu -- Location",
			groups = { "functional" })
	public void DeleteResource_07() throws HarnessException {

		// Create a new resource in the Admin Console using SOAP
		ResourceItem resource = new ResourceItem();
		
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
						"<CreateCalendarResourceRequest xmlns='urn:zimbraAdmin'>"
				 		+ "<name>" + resource.getEmailAddress() + "</name>"
				 		+ "<a n=\"displayName\">" + resource.getName() + "</a>"
				 		+ "<a n=\"zimbraCalResType\">" + "Location" + "</a>"
				 		+ "<password>test123</password>"
				 		+ "</CreateCalendarResourceRequest>");
		
		// Enter the search string to find the account
		app.zPageSearchResults.zAddSearchQuery(resource.getEmailAddress());
		
		// Click search
		app.zPageSearchResults.zToolbarPressButton(Button.B_SEARCH);
		
		// Right Click on resource to be deleted.
		app.zPageSearchResults.zListItem(Action.A_RIGHTCLICK, resource.getEmailAddress());

		// Click on Delete button
		DialogForDeleteOperation dialog = (DialogForDeleteOperation) app.zPageSearchResults.zToolbarPressButton(Button.B_TREE_DELETE);

		// Click Yes in Confirmation dialog
		dialog.zClickButton(Button.B_YES);

		// Click Ok on "Delete Items" dialog
		dialog.zClickButton(Button.B_OK);
		
		// Get the list of displayed accounts
		List<AccountItem> accounts = app.zPageSearchResults.zListGetAccounts();
		ZAssert.assertNotNull(accounts, "Verify the resource list is returned");
		
		AccountItem found = null;
		for (AccountItem a : accounts) {
			logger.info("Looking for resource "+ resource.getEmailAddress() + " found: "+ a.getGEmailAddress());
			if ( resource.getEmailAddress().equals(a.getGEmailAddress()) ) {
				found = a;
				break;
			}
		}
		ZAssert.assertNull(found, "Verify the resource is deleted successfully");

	}
	
	/**
	 * Testcase : Verify delete resource operation -- Search List View/Right Click Menu -- Equipment
	 * Steps :
	 * 1. Create a resource using SOAP.
	 * 2. Search resource.
	 * 3. Right click on resource.
	 * 4. Delete a resource using delete button in right click menu.
	 * 5. Verify resource is deleted using SOAP.
	 * @throws HarnessException
	 */
	@Test(	description = "Verify delete resource operation -- Search List View/Right Click Menu -- Equipment",
			groups = { "functional" })
	public void DeleteResource_08() throws HarnessException {

		// Create a new resource in the Admin Console using SOAP
		ResourceItem resource = new ResourceItem();
		
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
						"<CreateCalendarResourceRequest xmlns='urn:zimbraAdmin'>"
				 		+ "<name>" + resource.getEmailAddress() + "</name>"
				 		+ "<a n=\"displayName\">" + resource.getName() + "</a>"
				 		+ "<a n=\"zimbraCalResType\">" + "Equipment" + "</a>"
				 		+ "<password>test123</password>"
				 		+ "</CreateCalendarResourceRequest>");
		
		// Enter the search string to find the account
		app.zPageSearchResults.zAddSearchQuery(resource.getEmailAddress());
		
		// Click search
		app.zPageSearchResults.zToolbarPressButton(Button.B_SEARCH);
		
		// Click on resource to be deleted.
		app.zPageSearchResults.zListItem(Action.A_RIGHTCLICK, resource.getEmailAddress());

		// Click on Delete button
		DialogForDeleteOperation dialog = (DialogForDeleteOperation) app.zPageSearchResults.zToolbarPressButton(Button.B_TREE_DELETE);

		// Click Yes in Confirmation dialog
		dialog.zClickButton(Button.B_YES);

		// Click Ok on "Delete Items" dialog
		dialog.zClickButton(Button.B_OK);
		
		// Get the list of displayed accounts
		List<AccountItem> accounts = app.zPageSearchResults.zListGetAccounts();
		ZAssert.assertNotNull(accounts, "Verify the resource list is returned");
		
		AccountItem found = null;
		for (AccountItem a : accounts) {
			logger.info("Looking for resource "+ resource.getEmailAddress() + " found: "+ a.getGEmailAddress());
			if ( resource.getEmailAddress().equals(a.getGEmailAddress()) ) {
				found = a;
				break;
			}
		}
		ZAssert.assertNull(found, "Verify the resource is deleted successfully");

	}

}
