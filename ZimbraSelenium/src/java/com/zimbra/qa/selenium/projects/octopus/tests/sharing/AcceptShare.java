package com.zimbra.qa.selenium.projects.octopus.tests.sharing;

import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
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

		ownerAccount = new ZimbraAccount();
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

		ownerAccount.soapSend("<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+ "<folder name='" + ownerFoldername + "' l='"
				+ ownerBriefcaseRootFolder.getId() + "'/>"
				+ "</CreateFolderRequest>");

		// Verify the share folder exists on the server
		FolderItem ownerFolderItem = FolderItem.importFromSOAP(ownerAccount,
				ownerFoldername);

		ZAssert.assertNotNull(ownerFolderItem,
				"Verify the owner share folder exists");

		ZimbraAccount currentAccount = app.zGetActiveAccount();

		ownerAccount.soapSend("<FolderActionRequest xmlns='urn:zimbraMail'>"
				+ "<action id='" + ownerFolderItem.getId() + "' op='grant'>"
				+ "<grant d='" + currentAccount.EmailAddress
				+ "' gt='usr' perm='r'/>" + "</action>"
				+ "</FolderActionRequest>");

		// Owner sends share notification to the current user
		/*
		 * ownerAccount.soapSend("<SendShareNotificationRequest xmlns='urn:zimbraMail'>"
		 * + "<share l='" + ownerFolder.getId() + "' gt='usr' zid='" +
		 * currentAccount.ZimbraId + "' name='" + currentAccount.EmailAddress +
		 * "'><notes _content='test'/></share>" +
		 * "</SendShareNotificationRequest>");
		 */
		ownerAccount
				.soapSend("<SendShareNotificationRequest xmlns='urn:zimbraMail'>"
						+ "<share l='"
						+ ownerFolderItem.getId()
						+ "' gt='usr'"
						+ " zid='"
						+ currentAccount.ZimbraId
						+ "'"
						+ " name='"
						+ currentAccount.EmailAddress
						+ "'/>"
						+ "</SendShareNotificationRequest>");

		// Currrent user gets share notification
		String getShareNotifcationRequest = "<GetShareNotificationsRequest xmlns='urn:zimbraMail'/>";
		
		app.zPageOctopus.waitForResponse(currentAccount, getShareNotifcationRequest, ownerFoldername, 5);

		currentAccount.soapSend(getShareNotifcationRequest);
		
		// Open Sharing tab
		app.zPageOctopus
				.zToolbarPressButton(Button.B_TAB_SHARING);

		ZAssert
				.assertTrue(app.zPageSharing.zWaitForElementPresent(
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
		ZAssert
				.assertTrue(
						app.zPageOctopus.zIsItemInCurentListView(ownerFolderItem.getName()),
						"Verify the accepted shared folder appears in My Files list view");
	}
}
