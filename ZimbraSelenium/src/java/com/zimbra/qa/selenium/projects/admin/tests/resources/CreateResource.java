package com.zimbra.qa.selenium.projects.admin.tests.resources;



import org.testng.annotations.Test;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAdminAccount;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.items.ResourceItem;
import com.zimbra.qa.selenium.projects.admin.ui.WizardCreateResource;

public class CreateResource extends AdminCommonTest {
	public CreateResource() {
		logger.info("New " + CreateResource.class.getCanonicalName());

		//Every test starts at Resource page
		super.startingPage=app.zPageManageResources;
	}


	/**
	 * Testcase : Create a basic resource.
	 * Steps :
	 * 1. Create a resource from GUI.
	 * 2. Verify resource is created using SOAP.
	 * @throws HarnessException
	 */
	@Test(	description = "Create a basic resource",
			groups = { "obsolete" })
			public void CreateResource_01() throws HarnessException {

		// Create a new resource in the Admin Console
		ResourceItem resource = new ResourceItem();


		// Click "New"
		WizardCreateResource wizard = 
			(WizardCreateResource)app.zPageManageResources.zToolbarPressButton(Button.B_NEW);

		// Fill out the wizard and click Finish
		wizard.zCompleteWizard(resource);


		// Verify the resource exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetCalendarResourceRequest xmlns='urn:zimbraAdmin'>"
				+ 		"<calresource by='name'>" +  resource.getEmailAddress() + "</calresource>"  
				+		"</GetCalendarResourceRequest>");

		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetCalendarResourceResponse/admin:calresource", 1); 
		ZAssert.assertNotNull(response, "Verify the RESOURCE is created successfully");

	}

	/**
	 * Testcase : Create a basic resource.
	 * Steps :
	 * 1. Create a resource from GUI i.e. New --> Resource.
	 * 2. Verify resource is created using SOAP.
	 * @throws HarnessException
	 */
	@Test(	description = "Create a basic resource using New --> Resource",
			groups = { "sanity" })
			public void CreateResource_02() throws HarnessException {

		// Create a new resource in the Admin Console
		ResourceItem resource = new ResourceItem();


		// Click "New --> Resources"
		WizardCreateResource wizard = 
			(WizardCreateResource)app.zPageManageResources.zToolbarPressPulldown(Button.B_GEAR_BOX, Button.O_NEW);

		// Fill out the wizard and click Finish
		wizard.zCompleteWizard(resource);


		// Verify the resource exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetCalendarResourceRequest xmlns='urn:zimbraAdmin'>"
				+ 		"<calresource by='name'>" +  resource.getEmailAddress() + "</calresource>"  
				+		"</GetCalendarResourceRequest>");

		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetCalendarResourceResponse/admin:calresource", 1); 
		ZAssert.assertNotNull(response, "Verify the RESOURCE is created successfully");

	}

	/**
	 * Testcase : Create a basic resource.
	 * Steps :
	 * 1. Create a Location resource from GUI.
	 * 2. Verify resource is created using SOAP.
	 * @throws HarnessException
	 */
	@Test(	description = "Create a basic resource",
			groups = { "sanity" })
			public void CreateResource_03() throws HarnessException {

		// Create a new resource in the Admin Console
		ResourceItem resource = new ResourceItem();


		// Click "New"
		WizardCreateResource wizard = 
			(WizardCreateResource)app.zPageManageResources.zToolbarPressPulldown(Button.B_GEAR_BOX, Button.O_NEW);

		// Fill out the wizard and click Finish
		wizard.setResourceType(WizardCreateResource.Locators.LOCATION);
		wizard.zCompleteWizard(resource);


		// Verify the resource exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetCalendarResourceRequest xmlns='urn:zimbraAdmin'>"
				+ 		"<calresource by='name'>" +  resource.getEmailAddress() + "</calresource>"  
				+		"</GetCalendarResourceRequest>");

		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetCalendarResourceResponse/admin:calresource", 1); 
		ZAssert.assertNotNull(response, "Verify the RESOURCE is created successfully");

	}
	
	/**
	 * Testcase : Create a basic resource.
	 * Steps :
	 * 1. Create a Equipment resource from GUI.
	 * 2. Verify resource is created using SOAP.
	 * @throws HarnessException
	 */
	@Test(	description = "Create a basic resource",
			groups = { "sanity" })
			public void CreateResource_04() throws HarnessException {

		// Create a new resource in the Admin Console
		ResourceItem resource = new ResourceItem();


		// Click "New"
		WizardCreateResource wizard = 
			(WizardCreateResource)app.zPageManageResources.zToolbarPressPulldown(Button.B_GEAR_BOX, Button.O_NEW);

		// Fill out the wizard and click Finish
		wizard.setResourceType(WizardCreateResource.Locators.EQUIPMENT);
		wizard.zCompleteWizard(resource);


		// Verify the resource exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetCalendarResourceRequest xmlns='urn:zimbraAdmin'>"
				+ 		"<calresource by='name'>" +  resource.getEmailAddress() + "</calresource>"  
				+		"</GetCalendarResourceRequest>");

		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetCalendarResourceResponse/admin:calresource", 1); 
		ZAssert.assertNotNull(response, "Verify the RESOURCE is created successfully");

	}



}

