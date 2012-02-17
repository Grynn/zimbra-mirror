package com.zimbra.qa.selenium.projects.admin.tests.distributionlists;

import org.testng.annotations.Test;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAdminAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.items.DistributionListItem;
import com.zimbra.qa.selenium.projects.admin.ui.FormEditDistributionList;
import com.zimbra.qa.selenium.projects.admin.ui.PageMain;

public class EditDistributionList extends AdminCommonTest {
	public EditDistributionList() {
		logger.info("New "+ EditDistributionList.class.getCanonicalName());

		// All tests start at the "Accounts" page
		super.startingPage = app.zPageManageDistributionList;

	}

	/**
	 * Testcase : Edit Distribution List name - Manage Distribution List view
	 * Steps :
	 * 1. Create a dl using SOAP.
	 * 2. Edit the dl name using UI.
	 * 3. Verify dl name is changed using SOAP.
	 * @throws HarnessException
	 */
	@Test(	description = "Edit Distribution List name - Manage Distribution List view",
			groups = { "smoke" })
			public void EditDistributionList_01() throws HarnessException {

		// Create a new dl in the Admin Console using SOAP
		DistributionListItem dl = new DistributionListItem();
		String dlEmailAddress=dl.getEmailAddress();

		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<CreateDistributionListRequest xmlns='urn:zimbraAdmin'>"
				+			"<name>" + dlEmailAddress + "</name>"
				+		"</CreateDistributionListRequest>");

		// Refresh the list
		app.zPageManageDistributionList.sClickAt(PageMain.Locators.REFRESH_BUTTON, "");
		
		// Click on distribution list to be deleted.
		app.zPageManageDistributionList.zListItem(Action.A_LEFTCLICK, dl.getEmailAddress());
		
		// Click on Edit button
		FormEditDistributionList form = (FormEditDistributionList) app.zPageManageDistributionList.zToolbarPressPulldown(Button.B_GEAR_BOX,Button.O_EDIT);
		//FormEditDistributionList form = (FormEditDistributionList) app.zPageManageDistributionList.zToolbarPressPulldown(Button.B_GEAR_BOX, Button.O_EDIT);
		
		//Click on General Information tab.
		form.zClickTreeItem(FormEditDistributionList.TreeItem.MEMBERS);

		//Edit the name.
		String editedName = "editedDL_" + ZimbraSeleniumProperties.getUniqueString();
		form.setName(editedName);
		
		//Submit the form.
		form.zSubmit();
		
		// Verify the dl exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetDistributionListRequest xmlns='urn:zimbraAdmin'>" +
				"<dl by='name'>"+editedName+"@"+dl.getDomainName()+	"</dl>"+
		"</GetDistributionListRequest>");

		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetDistributionListResponse/admin:dl", 1);
		ZAssert.assertNotNull(response, "Verify the distribution list is edited successfully");
	}
	
	/**
	 * Testcase : Edit Distribution List name - Manage Distribution List view + Right Click Menu
	 * Steps :
	 * 1. Create a dl using SOAP.
	 * 2. Right Click and Edit the dl name using UI.
	 * 3. Verify dl name is changed using SOAP.
	 * @throws HarnessException
	 */
	@Test(	description = "Edit Distribution List name - Manage Distribution List view + Right Click Menu",
			groups = { "functional" })
			public void EditDistributionList_02() throws HarnessException {

		// Create a new dl in the Admin Console using SOAP
		DistributionListItem dl = new DistributionListItem();
		String dlEmailAddress=dl.getEmailAddress();

		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<CreateDistributionListRequest xmlns='urn:zimbraAdmin'>"
				+			"<name>" + dlEmailAddress + "</name>"
				+		"</CreateDistributionListRequest>");

		// Refresh the list
		app.zPageManageDistributionList.sClickAt(PageMain.Locators.REFRESH_BUTTON, "");
		
		// Right Click on distribution list to be deleted.
		app.zPageManageDistributionList.zListItem(Action.A_RIGHTCLICK, dl.getEmailAddress());
		
		// Click on Edit button
		FormEditDistributionList form = (FormEditDistributionList) app.zPageManageDistributionList.zToolbarPressButton(Button.B_TREE_EDIT);
		
		//Click on General Information tab.
		form.zClickTreeItem(FormEditDistributionList.TreeItem.MEMBERS);

		//Edit the name.
		String editedName = "editedDL_" + ZimbraSeleniumProperties.getUniqueString();
		form.setName(editedName);
		
		//Submit the form.
		form.zSubmit();
		
		// Verify the dl exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetDistributionListRequest xmlns='urn:zimbraAdmin'>" +
				"<dl by='name'>"+editedName+"@"+dl.getDomainName()+	"</dl>"+
		"</GetDistributionListRequest>");

		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetDistributionListResponse/admin:dl", 1);
		ZAssert.assertNotNull(response, "Verify the distribution list is edited successfully");
	}

}
