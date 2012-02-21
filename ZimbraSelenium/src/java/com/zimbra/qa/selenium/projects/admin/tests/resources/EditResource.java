package com.zimbra.qa.selenium.projects.admin.tests.resources;

import org.testng.annotations.Test;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAdminAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.items.ResourceItem;
import com.zimbra.qa.selenium.projects.admin.ui.FormEditResource;
import com.zimbra.qa.selenium.projects.admin.ui.PageMain;

public class EditResource extends AdminCommonTest {
	public EditResource() {
		logger.info("New "+ EditResource.class.getCanonicalName());

		// All tests start at the "Resources" page
		super.startingPage = app.zPageManageResources;

	}

	/**
	 * Testcase : Edit Resource name  -- Manage resource View -- Location
	 * Steps :
	 * 1. Create a resource using SOAP.
	 * 2. Go to Manage resource View.
	 * 3. Select a resource.
	 * 4. Edit a resource using edit button in Gear box menu.
	 * 5. Verify resource is deleted using SOAP.
	 * @throws HarnessException
	 */
	@Test(	description = " Edit Resource name  -- Manage resource View -- Location",
			groups = { "smoke" })
			public void EditResource_01() throws HarnessException {

		// Create a new Resource in the Admin Console using SOAP
		ResourceItem resource = new ResourceItem();
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<CreateCalendarResourceRequest xmlns='urn:zimbraAdmin'>"
		 		+ "<name>" + resource.getEmailAddress() + "</name>"
		 		+ "<a n=\"displayName\">" + resource.getName() + "</a>"
		 		+ "<a n=\"zimbraCalResType\">" + "Location" + "</a>"
		 		+ "<password>test123</password>"
		 		+ "</CreateCalendarResourceRequest>");

		// Refresh the Resource list
		app.zPageManageResources.sClickAt(PageMain.Locators.REFRESH_BUTTON, "");
		
		// Click on Resource to be Edited.
		app.zPageManageResources.zListItem(Action.A_LEFTCLICK, resource.getEmailAddress());
		
		// Click on Edit button
		FormEditResource form = (FormEditResource) app.zPageManageResources.zToolbarPressPulldown(Button.B_GEAR_BOX, Button.O_EDIT);
		
		//Click on General Information tab.
		form.zClickTreeItem(FormEditResource.TreeItem.RESOURCE_PROPERTIES);

		//Edit the name.
		String editedName = "editedResource_" + ZimbraSeleniumProperties.getUniqueString();
		form.setName(editedName);
		
		//Submit the form.
		form.zSubmit();
		
		// Verify the Resource exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetCalendarResourceRequest xmlns='urn:zimbraAdmin'>"
				+ 		"<calresource by='name'>" +  editedName+"@"+resource.getDomainName() + "</calresource>"  
				+		"</GetCalendarResourceRequest>");
		
		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetCalendarResourceResponse/admin:calresource", 1); 
		ZAssert.assertNotNull(response, "Verify the Resource is edited successfully");
	}
	
	/**
	 * Testcase : Edit Resource name  -- Manage resource View -- Equipment
	 * Steps :
	 * 1. Create a resource using SOAP.
	 * 2. Go to Manage resource View.
	 * 3. Select a resource.
	 * 4. Edit a resource using edit button in Gear box menu.
	 * 5. Verify resource is deleted using SOAP.
	 * @throws HarnessException
	 */
	@Test(	description = " Edit Resource name  -- Manage resource View -- Location",
			groups = { "functional" })
			public void EditResource_02() throws HarnessException {

		// Create a new Resource in the Admin Console using SOAP
		ResourceItem resource = new ResourceItem();
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<CreateCalendarResourceRequest xmlns='urn:zimbraAdmin'>"
		 		+ "<name>" + resource.getEmailAddress() + "</name>"
		 		+ "<a n=\"displayName\">" + resource.getName() + "</a>"
		 		+ "<a n=\"zimbraCalResType\">" + "Equipment" + "</a>"
		 		+ "<password>test123</password>"
		 		+ "</CreateCalendarResourceRequest>");

		// Refresh the Resource list
		app.zPageManageResources.sClickAt(PageMain.Locators.REFRESH_BUTTON, "");
		
		// Click on Resource to be Edited.
		app.zPageManageResources.zListItem(Action.A_LEFTCLICK, resource.getEmailAddress());
		
		// Click on Edit button
		FormEditResource form = (FormEditResource) app.zPageManageResources.zToolbarPressPulldown(Button.B_GEAR_BOX, Button.O_EDIT);
		
		//Click on General Information tab.
		form.zClickTreeItem(FormEditResource.TreeItem.RESOURCE_PROPERTIES);

		//Edit the name.
		String editedName = "editedResource_" + ZimbraSeleniumProperties.getUniqueString();
		form.setName(editedName);
		
		//Submit the form.
		form.zSubmit();
		
		// Verify the Resource exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetCalendarResourceRequest xmlns='urn:zimbraAdmin'>"
				+ 		"<calresource by='name'>" +  editedName+"@"+resource.getDomainName() + "</calresource>"  
				+		"</GetCalendarResourceRequest>");
		
		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetCalendarResourceResponse/admin:calresource", 1); 
		ZAssert.assertNotNull(response, "Verify the Resource is edited successfully");
	}

	
	/**
	 * Testcase : Edit Resource name -- Manage resource View/Right Click Menu -- Location
	 * Steps :
	 * 1. Create a resource using SOAP.
	 * 2. Go to Manage resource View.
	 * 3. Right Click on a resource.
	 * 4. Edit a resource using edit button in right click menu.
	 * 5. Verify resource is deleted using SOAP.
	 * @throws HarnessException
	 */
	@Test(	description = "Edit Resource name -- Manage resource View/Right Click Menu -- Location",
			groups = { "functional" })
			public void EditResource_03() throws HarnessException {

		// Create a new Resource in the Admin Console using SOAP
		ResourceItem resource = new ResourceItem();
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<CreateCalendarResourceRequest xmlns='urn:zimbraAdmin'>"
		 		+ "<name>" + resource.getEmailAddress() + "</name>"
		 		+ "<a n=\"displayName\">" + resource.getName() + "</a>"
		 		+ "<a n=\"zimbraCalResType\">" + "Location" + "</a>"
		 		+ "<password>test123</password>"
		 		+ "</CreateCalendarResourceRequest>");

		// Refresh the Resource list
		app.zPageManageResources.sClickAt(PageMain.Locators.REFRESH_BUTTON, "");
		
		// Right Click on Resource to be Edited.
		app.zPageManageResources.zListItem(Action.A_RIGHTCLICK, resource.getEmailAddress());
		
		// Click on Edit button
		FormEditResource form = (FormEditResource) app.zPageManageResources.zToolbarPressButton(Button.B_TREE_EDIT);
		
		//Click on General Information tab.
		form.zClickTreeItem(FormEditResource.TreeItem.RESOURCE_PROPERTIES);

		//Edit the name.
		String editedName = "editedResource_" + ZimbraSeleniumProperties.getUniqueString();
		form.setName(editedName);
		
		//Submit the form.
		form.zSubmit();
		
		// Verify the Resource exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetCalendarResourceRequest xmlns='urn:zimbraAdmin'>"
				+ 		"<calresource by='name'>" +  editedName+"@"+resource.getDomainName() + "</calresource>"  
				+		"</GetCalendarResourceRequest>");
		
		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetCalendarResourceResponse/admin:calresource", 1); 
		ZAssert.assertNotNull(response, "Verify the Resource is edited successfully");
	}
	
	/**
	 * Testcase : Edit Resource name -- Manage resource View/Right Click Menu -- Equipment
	 * Steps :
	 * 1. Create a resource using SOAP.
	 * 2. Go to Manage resource View.
	 * 3. Right Click on a resource.
	 * 4. Edit a resource using edit button in right click menu.
	 * 5. Verify resource is deleted using SOAP.
	 * @throws HarnessException
	 */
	@Test(	description = "Edit Resource name -- Manage resource View/Right Click Menu -- Equipment",
			groups = { "functional" })
			public void EditResource_04() throws HarnessException {

		// Create a new Resource in the Admin Console using SOAP
		ResourceItem resource = new ResourceItem();
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<CreateCalendarResourceRequest xmlns='urn:zimbraAdmin'>"
		 		+ "<name>" + resource.getEmailAddress() + "</name>"
		 		+ "<a n=\"displayName\">" + resource.getName() + "</a>"
		 		+ "<a n=\"zimbraCalResType\">" + "Equipment" + "</a>"
		 		+ "<password>test123</password>"
		 		+ "</CreateCalendarResourceRequest>");

		// Refresh the Resource list
		app.zPageManageResources.sClickAt(PageMain.Locators.REFRESH_BUTTON, "");
		
		// Right Click on Resource to be Edited.
		app.zPageManageResources.zListItem(Action.A_RIGHTCLICK, resource.getEmailAddress());
		
		// Click on Edit button
		FormEditResource form = (FormEditResource) app.zPageManageResources.zToolbarPressButton(Button.B_TREE_EDIT);
		
		//Click on General Information tab.
		form.zClickTreeItem(FormEditResource.TreeItem.RESOURCE_PROPERTIES);

		//Edit the name.
		String editedName = "editedResource_" + ZimbraSeleniumProperties.getUniqueString();
		form.setName(editedName);
		
		//Submit the form.
		form.zSubmit();
		
		// Verify the Resource exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetCalendarResourceRequest xmlns='urn:zimbraAdmin'>"
				+ 		"<calresource by='name'>" +  editedName+"@"+resource.getDomainName() + "</calresource>"  
				+		"</GetCalendarResourceRequest>");
		
		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetCalendarResourceResponse/admin:calresource", 1); 
		ZAssert.assertNotNull(response, "Verify the Resource is edited successfully");
	}


}
