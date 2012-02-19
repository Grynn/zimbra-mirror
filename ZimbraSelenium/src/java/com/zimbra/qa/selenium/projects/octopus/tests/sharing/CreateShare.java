package com.zimbra.qa.selenium.projects.octopus.tests.sharing;

import org.testng.annotations.*;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.octopus.ui.DialogFolderShare;
import com.zimbra.qa.selenium.projects.octopus.core.OctopusCommonTest;

public class CreateShare extends OctopusCommonTest {

	private ZimbraAccount granteeAccount = null;
	private boolean _folderIsCreated = false;
	private String _folderName = null;
	private boolean _fileAttached = false;
	private String _fileId = null;

	@BeforeMethod(groups = { "always" })
	public void testReset() {
		_folderName = null;
		_folderIsCreated = false;
		_fileId = null;
		_fileAttached = false;
	}

	public CreateShare() {
		logger.info("New " + CreateShare.class.getCanonicalName());

		// Test starts at the Octopus page
		super.startingPage = app.zPageMyFiles;
		super.startingAccountPreferences = null;
		granteeAccount = new ZimbraAccount();
		granteeAccount.provision();
		granteeAccount.authenticate();
	}

	@Test(description = "Create share using SOAP - verify view permissions in Share info using SOAP", groups = { "sanity" })
	public void CreateShare_01() throws HarnessException {
		ZimbraAccount currentOwnerAccount = app.zGetActiveAccount();

		FolderItem ownerBriefcaseRootFolder = FolderItem.importFromSOAP(
				currentOwnerAccount, SystemFolder.Briefcase);

		ZAssert.assertNotNull(ownerBriefcaseRootFolder,
				"Verify the owner Briefcase root folder exists");

		// Owner creates a folder, shares it with current user
		String ownerFoldername = "ownerFolder"
				+ ZimbraSeleniumProperties.getUniqueString();

		currentOwnerAccount
				.soapSend("<CreateFolderRequest xmlns='urn:zimbraMail'>"
						+ "<folder name='" + ownerFoldername + "' l='"
						+ ownerBriefcaseRootFolder.getId()
						+ "' view='document'/>" + "</CreateFolderRequest>");

		// Verify the folder exists on the server
		FolderItem ownerFolderItem = FolderItem.importFromSOAP(
				currentOwnerAccount, ownerFoldername);

		ZAssert.assertNotNull(ownerFolderItem, "Verify the owner folder exists");

		_folderIsCreated = true;
		_folderName = ownerFoldername;

		currentOwnerAccount
				.soapSend("<FolderActionRequest xmlns='urn:zimbraMail'>"
						+ "<action id='" + ownerFolderItem.getId()
						+ "' op='grant'>" + "<grant d='"
						+ granteeAccount.EmailAddress + "' gt='usr' perm='r'/>"
						+ "</action>" + "</FolderActionRequest>");

		currentOwnerAccount
				.soapSend("<SendShareNotificationRequest xmlns='urn:zimbraMail'>"
						+ "<item id='"
						+ ownerFolderItem.getId()
						+ "'/>"
						+ "<e a='"
						+ granteeAccount.EmailAddress
						+ "'/>"
						+ "<notes _content='I invite you to share the folder'/>"
						+ "</SendShareNotificationRequest>");

		// grantee verifies notification message
		String getShareNotifcationRequest = "<GetShareNotificationsRequest xmlns='urn:zimbraMail'/>";
		
		app.zPageOctopus.waitForResponse(granteeAccount, getShareNotifcationRequest, ownerFoldername, 5);

		granteeAccount
				.soapSend(getShareNotifcationRequest);
		
		ZAssert.assertTrue(granteeAccount.soapMatch(
				"//mail:GetShareNotificationsResponse//mail:link", "name",
				ownerFoldername),
				"Verify link to the folder in the notification message");

		ZAssert.assertTrue(
				granteeAccount.soapMatch(
						"//mail:GetShareNotificationsResponse//mail:link",
						"perm", "r"),
				"Verify granted permissions for the folder are matched");

	}

	@Test(description = "Create share using Context Menu - verify Message text using GUI", groups = { "smoke" })
	public void CreateShare_02() throws HarnessException {
		ZimbraAccount currentOwnerAccount = app.zGetActiveAccount();

		FolderItem ownerBriefcaseRootFolder = FolderItem.importFromSOAP(
				currentOwnerAccount, SystemFolder.Briefcase);

		ZAssert.assertNotNull(ownerBriefcaseRootFolder,
				"Verify the owner Briefcase root folder exists");

		// Owner creates a folder, shares it with current user
		String ownerFoldername = "ownerFolder"
				+ ZimbraSeleniumProperties.getUniqueString();

		currentOwnerAccount
				.soapSend("<CreateFolderRequest xmlns='urn:zimbraMail'>"
						+ "<folder name='" + ownerFoldername + "' l='"
						+ ownerBriefcaseRootFolder.getId()
						+ "' view='document'/>" + "</CreateFolderRequest>");

		// Verify the folder exists on the server
		FolderItem ownerFolderItem = FolderItem.importFromSOAP(
				currentOwnerAccount, ownerFoldername);

		ZAssert.assertNotNull(ownerFolderItem, "Verify the owner folder exists");

		_folderIsCreated = true;
		_folderName = ownerFoldername;

		// click on My Files tab
		// app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);

		// Owner selects Share option from the Context menu
		DialogFolderShare dialogShare = (DialogFolderShare) app.zPageMyFiles
				.zToolbarPressPulldown(Button.B_MY_FILES_LIST_ITEM,
						Button.O_FOLDER_SHARE, ownerFoldername);

		// Click on Permissions input field
		dialogShare.zClick(DialogFolderShare.Locators.zViewAndEditInput.locator);

		// Provide input into Permissions field
		dialogShare.zTypeInput(
				DialogFolderShare.Locators.zViewEditAndShareInput,
				granteeAccount.EmailAddress);

		// Click on Show Message link
		dialogShare.zClickButton(Button.B_SHOW_MESSAGE);

		// Provide input into Message field
		dialogShare.zTypeInput(DialogFolderShare.Locators.zMessageInput,
				ownerFoldername);

		// Click on Share button
		dialogShare.zClickButton(Button.B_SHARE);

		SleepUtil.sleepSmall();

		// Select Share option from the Context menu
		dialogShare = (DialogFolderShare) app.zPageMyFiles
				.zToolbarPressPulldown(Button.B_MY_FILES_LIST_ITEM,
						Button.O_FOLDER_SHARE, ownerFoldername);

		// Verify the Message text appears in the Dialog
		String msg = dialogShare.zRetrieveText(DialogFolderShare.Locators.zMessageInput.locator);

		ZAssert.assertNotNull(msg, "Verify Message Text appears in the Dialog");

		ZAssert.assertStringContains(msg, ownerFoldername,
				"Verify Message Text contains shared folder name");

		dialogShare.zClickButton(Button.B_CANCEL);
	}

	@Test(description = "Create share using Context Menu - verify Share title using GUI", groups = { "functional" })
	public void CreateShare_03() throws HarnessException {
		ZimbraAccount currentOwnerAccount = app.zGetActiveAccount();

		FolderItem ownerBriefcaseRootFolder = FolderItem.importFromSOAP(
				currentOwnerAccount, SystemFolder.Briefcase);

		ZAssert.assertNotNull(ownerBriefcaseRootFolder,
				"Verify the owner Briefcase root folder exists");

		// Owner creates a folder, shares it with current user
		String ownerFoldername = "ownerFolder"
				+ ZimbraSeleniumProperties.getUniqueString();

		currentOwnerAccount
				.soapSend("<CreateFolderRequest xmlns='urn:zimbraMail'>"
						+ "<folder name='" + ownerFoldername + "' l='"
						+ ownerBriefcaseRootFolder.getId()
						+ "' view='document'/>" + "</CreateFolderRequest>");

		// Verify the folder exists on the server
		FolderItem ownerFolderItem = FolderItem.importFromSOAP(
				currentOwnerAccount, ownerFoldername);

		ZAssert.assertNotNull(ownerFolderItem, "Verify the owner folder exists");

		_folderIsCreated = true;
		_folderName = ownerFoldername;

		// click on My Files tab
		// app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);

		// Owner selects Share option from the Context menu
		DialogFolderShare dialogShare = (DialogFolderShare) app.zPageMyFiles
				.zToolbarPressPulldown(Button.B_MY_FILES_LIST_ITEM,
						Button.O_FOLDER_SHARE, ownerFoldername);

		// Click on Permissions input field
		dialogShare.zClick(DialogFolderShare.Locators.zViewAndEditInput.locator);

		// Provide input into Permissions field
		dialogShare.zTypeInput(
				DialogFolderShare.Locators.zViewEditAndShareInput,
				granteeAccount.EmailAddress);

		// Click on Share button
		dialogShare.zClickButton(Button.B_SHARE);

		SleepUtil.sleepSmall();

		// Select Share option from the Context menu
		dialogShare = (DialogFolderShare) app.zPageMyFiles
				.zToolbarPressPulldown(Button.B_MY_FILES_LIST_ITEM,
						Button.O_FOLDER_SHARE, ownerFoldername);

		String infoTitle = "already sharing this folder";
		ZAssert.assertTrue(app.zPageSharing.zWaitForElementPresent(
				DialogFolderShare.Locators.zShareInfoTitle.locator
						+ ":contains(" + infoTitle + ")", "4000"), "");

		dialogShare.zClickButton(Button.B_CANCEL);
	}

	@Test(description = "Create share using Context Menu - verify grantee name using GUI", groups = { "functional" })
	public void CreateShare_04() throws HarnessException {
		ZimbraAccount currentOwnerAccount = app.zGetActiveAccount();

		FolderItem ownerBriefcaseRootFolder = FolderItem.importFromSOAP(
				currentOwnerAccount, SystemFolder.Briefcase);

		ZAssert.assertNotNull(ownerBriefcaseRootFolder,
				"Verify the owner Briefcase root folder exists");

		// Owner creates a folder, shares it with current user
		String ownerFoldername = "ownerFolder"
				+ ZimbraSeleniumProperties.getUniqueString();

		currentOwnerAccount
				.soapSend("<CreateFolderRequest xmlns='urn:zimbraMail'>"
						+ "<folder name='" + ownerFoldername + "' l='"
						+ ownerBriefcaseRootFolder.getId()
						+ "' view='document'/>" + "</CreateFolderRequest>");

		// Verify the folder exists on the server
		FolderItem ownerFolderItem = FolderItem.importFromSOAP(
				currentOwnerAccount, ownerFoldername);

		ZAssert.assertNotNull(ownerFolderItem, "Verify the owner folder exists");

		_folderIsCreated = true;
		_folderName = ownerFoldername;

		// click on My Files tab
		// app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);

		// Owner selects Share option from the Context menu
		DialogFolderShare dialogShare = (DialogFolderShare) app.zPageMyFiles
				.zToolbarPressPulldown(Button.B_MY_FILES_LIST_ITEM,
						Button.O_FOLDER_SHARE, ownerFoldername);
		
		// Click on Permissions input field
		dialogShare.zClick(DialogFolderShare.Locators.zViewEditAndShareInput.locator);

		dialogShare.zTypeInput(
				DialogFolderShare.Locators.zViewEditAndShareInput,
				granteeAccount.EmailAddress);

		// Click on Share button
		dialogShare.zClickButton(Button.B_SHARE);

		SleepUtil.sleepSmall();

		// Select Share option from the Context menu
		dialogShare = (DialogFolderShare) app.zPageMyFiles
				.zToolbarPressPulldown(Button.B_MY_FILES_LIST_ITEM,
						Button.O_FOLDER_SHARE, ownerFoldername);

		// Expand grantee address bubble
		if (app.zPageSharing.zWaitForElementPresent(
				// DialogFolderShare.Locators.zShareInfoTitle.locator
				// + ":contains(" + (char)9658 + ")", "3000")
				// + ":contains(\u25BA)", "3000")
				DialogFolderShare.Locators.zShareInfoExpandAddrBubble.locator,
				"3000")) {
			dialogShare.zClickButton(Button.B_EXPAND);
		}
		
		ZAssert.assertTrue(app.zPageSharing.zWaitForElementPresent(
				DialogFolderShare.Locators.zShareInfoAddrBubble.locator
						+ ":contains(" + granteeAccount.EmailAddress + ")",
				"3000"), "Verify address bubble contains grantee name");

		// Collapse grantee name address bubble
		if (app.zPageSharing
				.zWaitForElementPresent(
						// DialogFolderShare.Locators.zShareInfoTitle.locator
						// + ":contains(" + (char) 9660 + ")", "1000")
						// + ":contains(\u25BC)", "1000")
						DialogFolderShare.Locators.zShareInfoCollapseAddrBubble.locator,
						"1000")) {
			dialogShare.zClickButton(Button.B_COLLAPSE);
		}

		dialogShare.zClickButton(Button.B_CANCEL);
	}

	@Test(description = "Create share using SOAP - verify view and edit permissions message using SOAP", groups = { "functional" })
	public void CreateShare_05() throws HarnessException {
		ZimbraAccount currentOwnerAccount = app.zGetActiveAccount();

		FolderItem ownerBriefcaseRootFolder = FolderItem.importFromSOAP(
				currentOwnerAccount, SystemFolder.Briefcase);

		ZAssert.assertNotNull(ownerBriefcaseRootFolder,
				"Verify the owner Briefcase root folder exists");

		// Owner creates a folder, shares it with current user
		String ownerFoldername = "ownerFolder"
				+ ZimbraSeleniumProperties.getUniqueString();

		currentOwnerAccount
				.soapSend("<CreateFolderRequest xmlns='urn:zimbraMail'>"
						+ "<folder name='" + ownerFoldername + "' l='"
						+ ownerBriefcaseRootFolder.getId()
						+ "' view='document'/>" + "</CreateFolderRequest>");

		// Verify the folder exists on the server
		FolderItem ownerFolderItem = FolderItem.importFromSOAP(
				currentOwnerAccount, ownerFoldername);

		ZAssert.assertNotNull(ownerFolderItem, "Verify the owner folder exists");

		_folderIsCreated = true;
		_folderName = ownerFoldername;

		currentOwnerAccount
				.soapSend("<FolderActionRequest xmlns='urn:zimbraMail'>"
						+ "<action id='" + ownerFolderItem.getId()
						+ "' op='grant'>" + "<grant d='"
						+ granteeAccount.EmailAddress
						+ "' gt='usr' perm='rwidx'/>" + "</action>"
						+ "</FolderActionRequest>");

		currentOwnerAccount
				.soapSend("<SendShareNotificationRequest xmlns='urn:zimbraMail'>"
						+ "<item id='"
						+ ownerFolderItem.getId()
						+ "'/>"
						+ "<e a='"
						+ granteeAccount.EmailAddress
						+ "'/>"
						+ "<notes _content='I invite you to share the folder'/>"
						+ "</SendShareNotificationRequest>");

		
		// grantee verifies view and edit permissions in the notification message
		String getShareNotifcationRequest = "<GetShareNotificationsRequest xmlns='urn:zimbraMail'/>";
				
		app.zPageOctopus.waitForResponse(granteeAccount, getShareNotifcationRequest, ownerFoldername, 5);

		granteeAccount.soapSend(getShareNotifcationRequest);

		ZAssert.assertTrue(granteeAccount.soapMatch(
				"//mail:GetShareNotificationsResponse//mail:link", "name",
				ownerFoldername),
				"Verify link to the folder in the notification message");

		ZAssert.assertTrue(granteeAccount.soapMatch(
				"//mail:GetShareNotificationsResponse//mail:link", "perm",
				"rwidx"),
				"Verify granted permissions for the folder are matched");
	}

	@Test(description = "Create share using SOAP - verify view, edit and share permissions message using SOAP", groups = { "functional" })
	public void CreateShare_06() throws HarnessException {
		ZimbraAccount currentOwnerAccount = app.zGetActiveAccount();

		FolderItem ownerBriefcaseRootFolder = FolderItem.importFromSOAP(
				currentOwnerAccount, SystemFolder.Briefcase);

		ZAssert.assertNotNull(ownerBriefcaseRootFolder,
				"Verify the owner Briefcase root folder exists");

		// Owner creates a folder, shares it with another user
		String ownerFoldername = "ownerFolder"
				+ ZimbraSeleniumProperties.getUniqueString();

		currentOwnerAccount
				.soapSend("<CreateFolderRequest xmlns='urn:zimbraMail'>"
						+ "<folder name='" + ownerFoldername + "' l='"
						+ ownerBriefcaseRootFolder.getId()
						+ "' view='document'/>" + "</CreateFolderRequest>");

		// Verify the folder exists on the server
		FolderItem ownerFolderItem = FolderItem.importFromSOAP(
				currentOwnerAccount, ownerFoldername);

		ZAssert.assertNotNull(ownerFolderItem, "Verify the owner folder exists");

		_folderIsCreated = true;
		_folderName = ownerFoldername;

		currentOwnerAccount
				.soapSend("<FolderActionRequest xmlns='urn:zimbraMail'>"
						+ "<action id='" + ownerFolderItem.getId()
						+ "' op='grant'>" + "<grant d='"
						+ granteeAccount.EmailAddress
						+ "' gt='usr' perm='rwidxa'/>" + "</action>"
						+ "</FolderActionRequest>");

		currentOwnerAccount
				.soapSend("<SendShareNotificationRequest xmlns='urn:zimbraMail'>"
						+ "<item id='"
						+ ownerFolderItem.getId()
						+ "'/>"
						+ "<e a='"
						+ granteeAccount.EmailAddress
						+ "'/>"
						+ "<notes _content='I invite you to share the folder'/>"
						+ "</SendShareNotificationRequest>");

		SleepUtil.sleepSmall();

		// grantee verifies view, edit and share permissions
		String getShareNotifcationRequest = "<GetShareNotificationsRequest xmlns='urn:zimbraMail'/>";
		
		app.zPageOctopus.waitForResponse(granteeAccount, getShareNotifcationRequest, ownerFoldername, 5);

		granteeAccount.soapSend(getShareNotifcationRequest);

		ZAssert.assertTrue(granteeAccount.soapMatch(
				"//mail:GetShareNotificationsResponse//mail:link", "name",
				ownerFoldername),
				"Verify link to the folder in the notification message");

		ZAssert.assertTrue(granteeAccount.soapMatch(
				"//mail:GetShareNotificationsResponse//mail:link", "perm",
				"rwidxa"),
				"Verify granted permissions for the folder are matched");
	}

	@AfterMethod(groups = { "always" })
	public void testCleanup() {
		if (_fileAttached && _fileId != null) {
			try {
				// Delete it from Server
				app.zPageOctopus.deleteItemUsingSOAP(_fileId,
						app.zGetActiveAccount());
			} catch (Exception e) {
				logger.info("Failed while deleting the file");
				e.printStackTrace();
			} finally {
				_fileId = null;
				_fileAttached = false;
			}
		}
		if (_folderIsCreated) {
			try {
				// Delete it from Server
				FolderItem
						.deleteUsingSOAP(app.zGetActiveAccount(), _folderName);
			} catch (Exception e) {
				logger.info("Failed while removing the folder.");
				e.printStackTrace();
			} finally {
				_folderName = null;
				_folderIsCreated = false;
			}
		}
		try {
			// Refresh view
			// ZimbraAccount account = app.zGetActiveAccount();
			// FolderItem item =
			// FolderItem.importFromSOAP(account,SystemFolder.Briefcase);
			// account.soapSend("<GetFolderRequest xmlns='urn:zimbraMail'><folder l='1' recursive='0'/>"
			// + "</GetFolderRequest>");
			// account.soapSend("<GetFolderRequest xmlns='urn:zimbraMail' requestId='folders' depth='1' tr='true' view='document'><folder l='"
			// + item.getId() + "'/></GetFolderRequest>");
			// account.soapSend("<GetActivityStreamRequest xmlns='urn:zimbraMail' id='16'/>");
			// app.zGetActiveAccount().accountIsDirty = true;
			// app.zPageOctopus.sRefresh();

			// Empty trash
			app.zPageTrash.emptyTrashUsingSOAP(app.zGetActiveAccount());

			app.zPageOctopus.zLogout();
		} catch (Exception e) {
			logger.info("Failed while emptying Trash");
			e.printStackTrace();
		}
	}
}
