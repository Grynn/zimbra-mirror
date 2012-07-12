package com.zimbra.qa.selenium.projects.octopus.tests.sharing;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.OctopusAccount;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.octopus.core.OctopusCommonTest;
import com.zimbra.qa.selenium.projects.octopus.ui.DialogFolderShare;

public class CreateShare extends OctopusCommonTest {

	private ZimbraAccount granteeAccount = null;
	private boolean _folderIsCreated = false;
	private String _folderName = null;
	private boolean _fileAttached = false;
	private String _fileId = null;
	private ZimbraAccount owner = null;

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
		granteeAccount = new OctopusAccount();
		granteeAccount.provision();
		granteeAccount.authenticate();
	}

	@Test(description = "Create share using SOAP - verify view permissions in Share info using SOAP", groups = { "functional" })
	public void CreateShare_01() throws HarnessException {
		ZimbraAccount currentOwnerAccount = app.zGetActiveAccount();

		FolderItem ownerBriefcaseRootFolder = FolderItem.importFromSOAP(
				currentOwnerAccount, SystemFolder.Briefcase);

		ZAssert.assertNotNull(ownerBriefcaseRootFolder,
				"Verify the owner Briefcase root folder exists");

		// Owner creates a folder, shares it with current user
		String ownerFoldername = "ownerFolder"
				+ ZimbraSeleniumProperties.getUniqueString();

		// Verify the folder exists on the server
		FolderItem ownerFolderItem = createFolderViaSoap(currentOwnerAccount, ownerFoldername,ownerBriefcaseRootFolder);

		ZAssert.assertNotNull(ownerFolderItem, "Verify the owner folder exists");

		_folderIsCreated = true;
		_folderName = ownerFoldername;

		shareFolderViaSoap(currentOwnerAccount, granteeAccount, ownerFolderItem, SHARE_AS_READ);

		// grantee verifies notification message
		String getShareNotifcationRequest = "<GetShareNotificationsRequest xmlns='urn:zimbraMail'/>";

		app.zPageOctopus.waitForResponse(granteeAccount, getShareNotifcationRequest, ownerFoldername, 5);

		granteeAccount.soapSend(getShareNotifcationRequest);

		ZAssert.assertTrue(granteeAccount.soapMatch(
				"//mail:GetShareNotificationsResponse//mail:link", "name",
				ownerFoldername),
				"Verify link to the folder in the notification message");

		ZAssert.assertTrue(granteeAccount.soapMatch(
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

		// Verify the folder exists on the server
		FolderItem ownerFolderItem =createFolderViaSoap(currentOwnerAccount, ownerFoldername,ownerBriefcaseRootFolder);

		ZAssert.assertNotNull(ownerFolderItem, "Verify the owner folder exists");

		_folderIsCreated = true;
		_folderName = ownerFoldername;

		// Owner selects Share option from the Context menu
		DialogFolderShare dialogShare = (DialogFolderShare) app.zPageMyFiles
				.zToolbarPressPulldown(Button.B_MY_FILES_LIST_ITEM,
						Button.O_FOLDER_SHARE, ownerFoldername);

		// Click on Permissions input field
		dialogShare.zClick(DialogFolderShare.Locators.zViewAndEditInput.locator);

		// Provide input into Permissions field
		dialogShare.zTypeInput(DialogFolderShare.Locators.zViewEditAndShareInput,
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

		// Verify the folder exists on the server
		FolderItem ownerFolderItem = createFolderViaSoap(currentOwnerAccount, ownerFoldername,ownerBriefcaseRootFolder);

		ZAssert.assertNotNull(ownerFolderItem, "Verify the owner folder exists");

		_folderIsCreated = true;
		_folderName = ownerFoldername;

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

		// Verify the folder exists on the server
		FolderItem ownerFolderItem = createFolderViaSoap(currentOwnerAccount, ownerFoldername,ownerBriefcaseRootFolder);

		ZAssert.assertNotNull(ownerFolderItem, "Verify the owner folder exists");

		_folderIsCreated = true;
		_folderName = ownerFoldername;

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

		// Verify the folder exists on the server
		FolderItem ownerFolderItem = createFolderViaSoap(currentOwnerAccount, ownerFoldername,ownerBriefcaseRootFolder);

		ZAssert.assertNotNull(ownerFolderItem, "Verify the owner folder exists");

		_folderIsCreated = true;
		_folderName = ownerFoldername;

		shareFolderViaSoap(currentOwnerAccount, granteeAccount, ownerFolderItem, SHARE_AS_READWRITE);

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

		// Verify the folder exists on the server
		FolderItem ownerFolderItem = createFolderViaSoap(currentOwnerAccount, ownerFoldername,ownerBriefcaseRootFolder);

		ZAssert.assertNotNull(ownerFolderItem, "Verify the owner folder exists");

		_folderIsCreated = true;
		_folderName = ownerFoldername;

		shareFolderViaSoap(currentOwnerAccount, granteeAccount, ownerFolderItem, SHARE_AS_ADMIN);

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

	@Bugs(ids = "71149")
	@Test(description = "History shows 'undefined' if user has specified wrong email address while sharing folder", groups = { "functional" })
	public void CreateShareWithInvalidEmailFormat() throws HarnessException {
		ZimbraAccount currentOwnerAccount = app.zGetActiveAccount();

		FolderItem ownerBriefcaseRootFolder = FolderItem.importFromSOAP(
				currentOwnerAccount, SystemFolder.Briefcase);

		ZAssert.assertNotNull(ownerBriefcaseRootFolder,
				"Verify the owner Briefcase root folder exists");

		// Owner creates a folder, shares it with current user
		String ownerFoldername = "ownerFolder"
				+ ZimbraSeleniumProperties.getUniqueString();

		// Verify the folder exists on the server
		FolderItem ownerFolderItem = createFolderViaSoap(currentOwnerAccount, ownerFoldername,ownerBriefcaseRootFolder);

		ZAssert.assertNotNull(ownerFolderItem, "Verify the owner folder exists");

		_folderIsCreated = true;
		_folderName = ownerFoldername;

		// Owner selects Share option from the Context menu
		DialogFolderShare dialogShare = (DialogFolderShare) app.zPageMyFiles
				.zToolbarPressPulldown(Button.B_MY_FILES_LIST_ITEM,
						Button.O_FOLDER_SHARE, ownerFoldername);

		// Click on Permissions input field
		dialogShare.zClick(DialogFolderShare.Locators.zViewAndEditInput.locator);

		String invalidEmailFormat="invalidemailaddress";

		// Provide input into Permissions field
		dialogShare.zTypeInput(DialogFolderShare.Locators.zViewEditAndShareInput,invalidEmailFormat);

		// Verify error Message text appears in the Share Dialog for invalid email format.
		ZAssert.assertTrue(app.zPageOctopus.sIsElementPresent(DialogFolderShare.Locators.zInavlidEmailFormatMessage.locator),
				"Verify message Text appears in the Dialog for invalid email format.");

		dialogShare.zClickButton(Button.B_SHARE);

		// Verify Toast message Text appears for sharing invalid email format.
		/*ZAssert.assertTrue(app.zPageOctopus.sIsElementPresent(DialogFolderShare.Locators.zSharingFailedToastMessage.locator),
				"Verify Toast message Text appears for sharing invalid email format.");*/

		ZAssert.assertTrue(app.zPageOctopus.zWaitForElementPresent
				(DialogFolderShare.Locators.zSharingFailedToastMessage.locator, "30000"),
				"Verify Toast message Text appears for sharing invalid email format.");
	}

	@Test(description = "Check share dialog", groups = { "smoke" })
	public void CheckShareDialog() throws HarnessException {
		ZimbraAccount currentOwnerAccount = app.zGetActiveAccount();

		FolderItem ownerBriefcaseRootFolder = FolderItem.importFromSOAP(
				currentOwnerAccount, SystemFolder.Briefcase);

		ZAssert.assertNotNull(ownerBriefcaseRootFolder,
				"Verify the owner Briefcase root folder exists");

		// Owner creates a folder, shares it with current user
		String ownerFoldername = "ownerFolder"
				+ ZimbraSeleniumProperties.getUniqueString();

		createFolderViaSoap(currentOwnerAccount, ownerFoldername,ownerBriefcaseRootFolder);

		_folderIsCreated = true;
		_folderName = ownerFoldername;

		// Owner selects Share option from the Context menu
		DialogFolderShare dialogShare = (DialogFolderShare) app.zPageMyFiles
				.zToolbarPressPulldown(Button.B_MY_FILES_LIST_ITEM,
						Button.O_FOLDER_SHARE, ownerFoldername);

		// Verify Show Message link in share dialog.
		ZAssert.assertTrue(app.zPageOctopus.sIsElementPresent(DialogFolderShare.Locators.zShowMessageLink.locator),
				"Verify Show Message link is visible on sharing dialog invalid.");

		// Click on Show Message link
		dialogShare.zClickButton(Button.B_SHOW_MESSAGE);

		// Verify Hide Message link in share dialog.
		ZAssert.assertTrue(app.zPageOctopus.sIsElementPresent(DialogFolderShare.Locators.zHideMessageLink.locator),
				"Verify Show Message link is visible on sharing dialog invalid.");

		// Verify input label present in share dialog.
		ZAssert.assertTrue(app.zPageOctopus.sIsElementPresent(DialogFolderShare.Locators.zPermissionLabel.locator),
				"Verify Show Message link is visible on sharing dialog .");

		// Verify share button in share dialog.
		ZAssert.assertTrue(app.zPageOctopus.sIsElementPresent(DialogFolderShare.Locators.zShareBtn.locator),
				"Verify share button is visible on sharing dialog .");

		// Verify cancel button in share dialog.
		ZAssert.assertTrue(app.zPageOctopus.sIsElementPresent(DialogFolderShare.Locators.zCancelBtn.locator),
				"Verify cancel button is visible on sharing dialog .");

		// Click on Cancel button
		dialogShare.zClickButton(Button.B_CANCEL);
	}

	@Test(description = "Basic folder sharing with view right. Check permission", groups = { "smoke" })
	public void CheckViewPermissionOfFolder() throws HarnessException
	{
		//Create folder using Soap
		String ownerFolderName = "ownerFolder"+ ZimbraSeleniumProperties.getUniqueString();
		CreateFolder(ownerFolderName,PPT_FILE);

		_folderIsCreated = true;
		_folderName = ownerFolderName;

		FolderItem ownerFolder = FolderItem.importFromSOAP(owner, ownerFolderName);

		//upload file to folder
		uploadFileViaSoap(app.zGetActiveAccount(),PPT_FILE, ownerFolder);

		// Grantee user creates the mountpoint that points to the share
		FolderItem granteeBrifcase = FolderItem.importFromSOAP(granteeAccount, SystemFolder.Briefcase);

		String mountPointFolderName = "mountFolderRead";
		mountFolderViaSoap(owner, granteeAccount, ownerFolder, SHARE_AS_READ, granteeBrifcase, mountPointFolderName);

		// Logout owner
		app.zPageOctopus.zLogout();

		// Login with grantee's Credential's.
		app.zPageLogin.zLogin(granteeAccount);

		// Navigate to folder
		app.zPageMyFiles.zListItem(Action.A_LEFTCLICK, mountPointFolderName);

		//Assert if option Upload is disabled for View permission.
		ZAssert.assertTrue(app.zPageOctopus.zIsContextMenuOptionDisabled(Button.B_MY_FILES, OPTION_UPLOAD, mountPointFolderName),"Verify that Upload option is disabled");

		//Assert if option New Folder is disabled for View permission.
		ZAssert.assertTrue(app.zPageOctopus.zIsContextMenuOptionDisabled(Button.B_MY_FILES, OPTION_NEW_FOLDER, mountPointFolderName),"Verify that New folder option is disabled");
	}

	@Test(description = "Basic folder sharing with edit right. Check permission.", groups = { "smoke" })
	public void CheckViewEditPermissionOfFolder() throws HarnessException
	{
		//Create folder using Soap
		String ownerFolderName = "ownerFolder"+ ZimbraSeleniumProperties.getUniqueString();
		CreateFolder(ownerFolderName,PPT_FILE);

		_folderIsCreated = true;
		_folderName = ownerFolderName;

		FolderItem ownerFolder = FolderItem.importFromSOAP(owner, ownerFolderName);

		//upload file to folder
		uploadFileViaSoap(app.zGetActiveAccount(),PPT_FILE, ownerFolder);

		FolderItem granteeBrifcase = FolderItem.importFromSOAP(granteeAccount, SystemFolder.Briefcase);
		String mountPointFolderName = "mountFolderEdit";

		mountFolderViaSoap(owner, granteeAccount, ownerFolder, SHARE_AS_READWRITE, granteeBrifcase, mountPointFolderName);

		// Logout owner
		app.zPageOctopus.zLogout();

		// Login with grantee's Credential's.
		app.zPageLogin.zLogin(granteeAccount);

		//Assert if option Share is not present.
		ZAssert.assertTrue(app.zPageOctopus.zIsContextMenuOptionPresent(Button.B_MY_FILES_LIST_ITEM, OPTION_SHARE, mountPointFolderName),"Verify that Share option is not present");

		// Navigate to folder
		app.zPageMyFiles.zListItem(Action.A_LEFTCLICK, mountPointFolderName);

		//Assert if option Upload is present for View ,Edit permission.
		ZAssert.assertTrue(app.zPageOctopus.zIsContextMenuOptionPresent(Button.B_MY_FILES, OPTION_UPLOAD, mountPointFolderName),"Verify that Upload option is present");

		//Assert if option New Folder is present for View ,Edit permission.
		ZAssert.assertTrue(app.zPageOctopus.zIsContextMenuOptionPresent(Button.B_MY_FILES, OPTION_NEW_FOLDER, mountPointFolderName),"Verify that New folder option is present");
	}

	@Test(description = "Basic folder sharing with admin right. Check permission.", groups = { "smoke" })
	public void CheckAdminPermissionOfFolder() throws HarnessException
	{
		//Create folder using Soap
		String ownerFolderName = "ownerFolder"+ ZimbraSeleniumProperties.getUniqueString();
		CreateFolder(ownerFolderName,PPT_FILE);

		_folderIsCreated = true;
		_folderName = ownerFolderName;

		FolderItem ownerFolder = FolderItem.importFromSOAP(owner, ownerFolderName);

		//upload file to folder
		uploadFileViaSoap(app.zGetActiveAccount(),PPT_FILE, ownerFolder);

		FolderItem granteeBrifcase = FolderItem.importFromSOAP(granteeAccount, SystemFolder.Briefcase);

		String mountPointFolderName = "mountFolderAdmin";
		mountFolderViaSoap(owner, granteeAccount, ownerFolder, SHARE_AS_ADMIN, granteeBrifcase, mountPointFolderName);

		// Logout owner
		app.zPageOctopus.zLogout();

		// Login with grantee's Credential's.
		app.zPageLogin.zLogin(granteeAccount);

		//Assert if option Share is present.
		ZAssert.assertTrue(app.zPageOctopus.zIsContextMenuOptionPresent(Button.B_MY_FILES_LIST_ITEM, OPTION_SHARE, mountPointFolderName),"Verify that Share option is present");
	}

	/*Common code to create folder using Soap request*/
	public void CreateFolder(String ownerFolderName,String fileInFolder) throws HarnessException
	{
		// Get current active account as owner
		owner = app.zGetActiveAccount();

		// Get the root folder of Owner
		FolderItem ownerBriefcase = FolderItem.importFromSOAP(owner, SystemFolder.Briefcase);

		//Create folder Using SOAP under Owner root folder.
		owner.soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>"
						+"<folder name='" + ownerFolderName + "' l='" + ownerBriefcase.getId() + "' view='document'/>"
						+"</CreateFolderRequest>");
	}

	@AfterMethod(groups = { "always" })
	public void testCleanup() {
		if (_fileAttached && _fileId != null) {
			try {
				// Delete it from Server
				app.zPageOctopus.deleteItemUsingSOAP(_fileId,
						app.zGetActiveAccount());
			} catch (Exception e) {
				logger.info("Failed while deleting the file", e);
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
				logger.info("Failed while removing the folder.", e);
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
			logger.info("Failed while emptying Trash", e);
		}
	}

}
