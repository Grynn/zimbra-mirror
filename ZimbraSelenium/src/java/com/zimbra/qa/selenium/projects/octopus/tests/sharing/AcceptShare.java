package com.zimbra.qa.selenium.projects.octopus.tests.sharing;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.OctopusAccount;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.octopus.core.OctopusCommonTest;
import com.zimbra.qa.selenium.projects.octopus.ui.PageSharing;

public class AcceptShare extends OctopusCommonTest {

	private ZimbraAccount ownerAccount = null;

	public AcceptShare() {
		logger.info("New " + AcceptShare.class.getCanonicalName());

		// Test starts at the Octopus page
		super.startingPage = app.zPageSharing;
		super.startingAccountPreferences = null;

		ownerAccount = new OctopusAccount();
		ownerAccount.provision();
		ownerAccount.authenticate();
	}

	@Test(description = "Accept share invitation clicking on Add To My Files button", groups = { "smoke" })
	public void AcceptShare_01() throws HarnessException {
		FolderItem ownerBriefcaseRootFolder = FolderItem.importFromSOAP(
				ownerAccount, SystemFolder.Briefcase);

		ZAssert.assertNotNull(ownerBriefcaseRootFolder,
				"Verify the owner Briefcase root folder exists");

		// Owner creates a folder, shares it with current user
		String ownerFoldername = "ownerFolder"
				+ ZimbraSeleniumProperties.getUniqueString();

		// Verify the share folder exists on the server
		FolderItem ownerFolderItem = createFolderViaSoap(ownerAccount, ownerFoldername,ownerBriefcaseRootFolder);

		ZAssert.assertNotNull(ownerFolderItem,"Verify the owner share folder exists");

		ZimbraAccount granteeAccount = app.zGetActiveAccount();

		shareFolderViaSoap(ownerAccount, granteeAccount, ownerFolderItem, SHARE_AS_READ);

		// Currrent/grantee user gets share notification
		String getShareNotifcationRequest = "<GetShareNotificationsRequest xmlns='urn:zimbraMail'/>";

		app.zPageOctopus.waitForResponse(granteeAccount, getShareNotifcationRequest, ownerFoldername, 5);

		granteeAccount.soapSend(getShareNotifcationRequest);

		// Open Sharing tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_SHARING);

		ZAssert.assertTrue(app.zPageSharing.zWaitForElementPresent(
				PageSharing.Locators.zShareNotificationListView.locator
				+ ":contains(" + ownerFolderItem.getName()
				+ ")", "9000"),
				"Verify the owner share folder is displayed in the Share Invitation view");

		// click on Add To My Files button
		app.zPageSharing.zToolbarPressButton(Button.B_ADD_TO_MY_FILES,
				ownerFolderItem);

		ZAssert.assertTrue(app.zPageSharing.zWaitForElementPresent(
				PageSharing.Locators.zSharedItemsView.locator + ":contains("
						+ ownerFolderItem.getName() + ")", "3000"),
				"Verify item appears in the Shared Items View");

		// Click on My Files tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);

		// Make sure the accepted shared folder appears in My Files list view
		ZAssert.assertTrue(
				app.zPageOctopus.zIsItemInCurentListView(ownerFolderItem.getName()),
				"Verify the accepted shared folder appears in My Files list view");
	}
}
