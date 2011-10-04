package com.zimbra.qa.selenium.projects.admin.tests.distributionlists;

import org.testng.annotations.Test;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAdminAccount;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.items.DistributionListItem;
import com.zimbra.qa.selenium.projects.admin.ui.FormNewDistributionList;

public class CreateDistributionList extends AdminCommonTest {
	public CreateDistributionList() {
		logger.info("New "+ CreateDistributionList.class.getCanonicalName());

		// All tests start at the "Distribution Lists" page
		super.startingPage = app.zPageManageDistributionList;
	}
	
	/**
	 * Testcase : Create a basic DL
	 * Steps :
	 * 1. Create a DL from GUI.
	 * 2. Verify DL is created using SOAP.
	 * @throws HarnessException
	 */
	@Test(	description = "Create a basic DL",
			groups = { "sanity" })
			public void CreateDistributionList_01() throws HarnessException {

		// Create a new dl in the Admin Console
		DistributionListItem dl = new DistributionListItem();

		// Click "New"
		FormNewDistributionList form =(FormNewDistributionList) app.zPageManageDistributionList.zToolbarPressButton(Button.B_NEW);
	
		// Fill out the necessary input fields and submit
		form.zComplete(dl);
		
		// Verify the dl exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
		"<GetDistributionListRequest xmlns='urn:zimbraAdmin'>" +
		                     "<dl by='name'>"+dl.getEmailAddress()+"</dl>"+
		                   "</GetDistributionListRequest>");
		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetDistributionListResponse/admin:dl", 1);
		ZAssert.assertNotNull(response, "Verify the distribution list is created successfully");
	}
	
	
	/**
	 * 1. Create DL from UI. i.e. using New --> DL
	 * 2. Verify DL is created using soap.
	 * @throws HarnessException
	 */
	@Test(	description = "Create a basic DL. New --> DL",
			groups = { "sanity" })
			public void CreateDistributionList_02() throws HarnessException {

		// Create a new dl in the Admin Console
		DistributionListItem dl = new DistributionListItem();

		// Click "New"
		FormNewDistributionList form =(FormNewDistributionList) app.zPageManageDistributionList.zToolbarPressPulldown(Button.B_NEW, Button.O_DISTRIBUTIUONLISTS_DISTRIBUTIONLIST);
	
		// Fill out the necessary input fields and submit
		form.zComplete(dl);
		
		// Verify the dl exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
		"<GetDistributionListRequest xmlns='urn:zimbraAdmin'>" +
		                     "<dl by='name'>"+dl.getEmailAddress()+"</dl>"+
		                   "</GetDistributionListRequest>");
		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetDistributionListResponse/admin:dl", 1);
		ZAssert.assertNotNull(response, "Verify the distribution list is created successfully");
	}


}
