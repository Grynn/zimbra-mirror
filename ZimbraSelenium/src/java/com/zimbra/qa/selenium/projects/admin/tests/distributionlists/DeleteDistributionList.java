package com.zimbra.qa.selenium.projects.admin.tests.distributionlists;

import org.testng.annotations.Test;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAdminAccount;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.items.DistributionListItem;
import com.zimbra.qa.selenium.projects.admin.ui.DialogForDeleteOperation;
import com.zimbra.qa.selenium.projects.admin.ui.PageMain;

public class DeleteDistributionList extends AdminCommonTest {

	public DeleteDistributionList() {
		logger.info("New "+ DeleteDistributionList.class.getCanonicalName());

		// All tests start at the "Distribution Lists" page
		super.startingPage = app.zPageManageDistributionList;
	}

	/**
	 * Testcase : Verify delete operation for DL - Manage distribution list view.
	 * Steps :
	 * 1. Create an dl using SOAP.
	 * 2. Select dl to be deleted in manage distribution list view.
	 * 3. Click on Delete button.
	 * 4. Verify list is deleted using SOAP
	 * @throws HarnessException
	 */
	@Test(	description = "Verify delete operation for distribution list - Manage distribution list view",
			groups = { "smoke" })
			public void DeleteDistributionList_01() throws HarnessException {

		// Create a new dl in the Admin Console using SOAP
		DistributionListItem dl = new DistributionListItem();
		String dlEmailAddress=dl.getEmailAddress();

		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<CreateDistributionListRequest xmlns='urn:zimbraAdmin'>"
				+			"<name>" + dlEmailAddress + "</name>"
				+		"</CreateDistributionListRequest>");

		// Refresh list to populate account.
		app.zPageManageDistributionList.sClickAt(PageMain.Locators.REFRESH_BUTTON, "");

		// Click on account to be deleted.
		app.zPageManageDistributionList.zListItem(Action.A_LEFTCLICK, dl.getEmailAddress());

		// Click on Delete button
		DialogForDeleteOperation dialog = (DialogForDeleteOperation) app.zPageManageDistributionList.zToolbarPressPulldown(Button.B_GEAR_BOX, Button.O_DELETE);

		// Click Yes in Confirmation dialog
		dialog.zClickButton(Button.B_YES);

		// Click Ok on "Delete Items" dialog
		dialog.zClickButton(Button.B_OK);


		// Verify the dl does not exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetDistributionListRequest xmlns='urn:zimbraAdmin'>" +
				"<dl by='name'>"+dlEmailAddress+"</dl>"+
		"</GetDistributionListRequest>");

		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetDistributionListResponse/admin:dl", 1);
		ZAssert.assertNull(response, "Verify the distribution list is deleted successfully");

	}
	
	/**
	 * Testcase : Verify delete operation for DL - Manage distribution list view/Right Click Menu.
	 * Steps :
	 * 1. Create an dl using SOAP.
	 * 2. Right click on dl to be deleted in manage distribution list view.
	 * 3. Click on Delete button.
	 * 4. Verify list is deleted using SOAP
	 * @throws HarnessException
	 */
	@Test(	description = "Verify delete operation for distribution list -- Manage distribution list/Right Click Menu",
			groups = { "smoke" })
			public void DeleteDistributionList_02() throws HarnessException {

		// Create a new dl in the Admin Console using SOAP
		DistributionListItem dl = new DistributionListItem();
		String dlEmailAddress=dl.getEmailAddress();

		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<CreateDistributionListRequest xmlns='urn:zimbraAdmin'>"
				+			"<name>" + dlEmailAddress + "</name>"
				+		"</CreateDistributionListRequest>");

		// Refresh list to populate account.
		app.zPageManageDistributionList.sClickAt(PageMain.Locators.REFRESH_BUTTON, "");

		// Click on account to be deleted.
		app.zPageManageDistributionList.zListItem(Action.A_RIGHTCLICK, dl.getEmailAddress());

		// Click on Delete button
		DialogForDeleteOperation dialog = (DialogForDeleteOperation) app.zPageManageDistributionList.zToolbarPressButton(Button.B_TREE_DELETE);

		// Click Yes in Confirmation dialog
		dialog.zClickButton(Button.B_YES);

		// Click Ok on "Delete Items" dialog
		dialog.zClickButton(Button.B_OK);


		// Verify the dl does not exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetDistributionListRequest xmlns='urn:zimbraAdmin'>" +
				"<dl by='name'>"+dlEmailAddress+"</dl>"+
		"</GetDistributionListRequest>");

		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetDistributionListResponse/admin:dl", 1);
		ZAssert.assertNull(response, "Verify the distribution list is deleted successfully");

	}

	
	/**
	 * Testcase : Verify delete operation for DL - Search distribution list view.
	 * Steps :
	 * 1. Create an dl using SOAP.
	 * 2. Delete dl present in the search result.
	 * 3. Verify list is deleted using SOAP
	 * @throws HarnessException
	 */
	@Test(	description = "Verify delete operation for distribution list - Search distribution list view",
			groups = { "smoke" })
			public void DeleteDistributionList_03() throws HarnessException {

		// Create a new dl in the Admin Console using SOAP
		DistributionListItem dl = new DistributionListItem();
		String dlEmailAddress=dl.getEmailAddress();

		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<CreateDistributionListRequest xmlns='urn:zimbraAdmin'>"
				+			"<name>" + dlEmailAddress + "</name>"
				+		"</CreateDistributionListRequest>");

		// Enter the search string to find the dl
		app.zPageSearchResults.zAddSearchQuery(dlEmailAddress);

		// Click search
		app.zPageSearchResults.zToolbarPressButton(Button.B_SEARCH);


		// Right Click on distribution list to be deleted.
		app.zPageSearchResults.zListItem(Action.A_LEFTCLICK, dl.getEmailAddress());

		// Click on Delete button
		DialogForDeleteOperation dialog = (DialogForDeleteOperation) app.zPageSearchResults.zToolbarPressButton(Button.B_TREE_DELETE);

		// Click Yes in Confirmation dialog
		dialog.zClickButton(Button.B_YES);

		// Click Ok on "Delete Items" dialog
		dialog.zClickButton(Button.B_OK);


		// Verify the dl does not exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetDistributionListRequest xmlns='urn:zimbraAdmin'>" +
				"<dl by='name'>"+dlEmailAddress+"</dl>"+
		"</GetDistributionListRequest>");

		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetDistributionListResponse/admin:dl", 1);
		ZAssert.assertNull(response, "Verify the distribution list is deleted successfully");

	}

	
	/**
	 * Testcase : Verify delete operation for DL - Search distribution list view/Right Click Menu.
	 * Steps :
	 * 1. Create an dl using SOAP.
	 * 2. Right Click and Delete dl present in the search result.
	 * 3. Verify list is deleted using SOAP
	 * @throws HarnessException
	 */
	@Test(	description = "Verify delete operation for distribution list - Search distribution list view/Right Click Menu.",
			groups = { "smoke" })
			public void DeleteDistributionList_04() throws HarnessException {

		// Create a new dl in the Admin Console using SOAP
		DistributionListItem dl = new DistributionListItem();
		String dlEmailAddress=dl.getEmailAddress();

		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<CreateDistributionListRequest xmlns='urn:zimbraAdmin'>"
				+			"<name>" + dlEmailAddress + "</name>"
				+		"</CreateDistributionListRequest>");

		// Enter the search string to find the dl
		app.zPageSearchResults.zAddSearchQuery(dlEmailAddress);

		// Click search
		app.zPageSearchResults.zToolbarPressButton(Button.B_SEARCH);


		// Right Click on distribution list to be deleted.
		app.zPageSearchResults.zListItem(Action.A_RIGHTCLICK, dl.getEmailAddress());

		// Click on Delete button
		DialogForDeleteOperation dialog = (DialogForDeleteOperation) app.zPageSearchResults.zToolbarPressButton(Button.B_TREE_DELETE);

		// Click Yes in Confirmation dialog
		dialog.zClickButton(Button.B_YES);

		// Click Ok on "Delete Items" dialog
		dialog.zClickButton(Button.B_OK);


		// Verify the dl does not exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetDistributionListRequest xmlns='urn:zimbraAdmin'>" +
				"<dl by='name'>"+dlEmailAddress+"</dl>"+
		"</GetDistributionListRequest>");

		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetDistributionListResponse/admin:dl", 1);
		ZAssert.assertNull(response, "Verify the distribution list is deleted successfully");

	}
}
