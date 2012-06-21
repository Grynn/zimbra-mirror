package com.zimbra.qa.selenium.projects.octopus.tests.sharing;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.octopus.tests.history.HistoryCommonTest;
import com.zimbra.qa.selenium.projects.octopus.ui.PageHistory.GetText;
import com.zimbra.qa.selenium.projects.octopus.ui.PageMyFiles;
import com.zimbra.qa.selenium.projects.octopus.ui.PageSharing.Locators;

public class ShareNotification extends HistoryCommonTest
{
	private ZimbraAccount granteeAccount = null;
	private ZimbraAccount owner = null;

	public ShareNotification(){
		logger.info("New " + ShareNotification.class.getCanonicalName());

		// Test starts at the Octopus page
		super.startingPage = app.zPageMyFiles;
		super.startingAccountPreferences = null;
		granteeAccount = new ZimbraAccount();
		granteeAccount.provision();
		granteeAccount.authenticate();
	}

	@Test(description = "Accept the share by clicking on 'Add to My files' button.Verify the shared folder and files.", groups = { "smoke" })
	public void AcceptShareByNotification() throws HarnessException
	{
		String ownerFolderName = "ownerFolder"+ ZimbraSeleniumProperties.getUniqueString();
		shareFolder(ownerFolderName,PPT_FILE);

		// Login with grantee's Credential's.
		app.zPageLogin.zLogin(granteeAccount);

		// If there is a busy overlay, wait for that to finish
		app.zPageOctopus.zWaitForBusyOverlayOctopus();

		//Verify sharing notification
		ZAssert.assertTrue(app.zPageOctopus.sIsElementPresent(Locators.zNotificationCount.locator),
				"Verify Sharing notification recived to grantee");

		//Click on Notification count.
		app.zPageSharing.sClickAt(Locators.zNotificationCount.locator,"0,0");

		//Click on 'Add to My files' button from notification.
		app.zPageSharing.sClickAt(Locators.zAddToMyFiles.locator,"0,0");

		// If there is a busy overlay, wait for that to finish
		//app.zPageOctopus.zWaitForBusyOverlayOctopus();

		// Verify shared mount point folder gets added in My files list view by clicking on Add to My files button from the notification.
		ZAssert.assertTrue(app.zPageMyFiles.zWaitForElementPresent(
				PageMyFiles.Locators.zMyFilesListViewItems.locator
				+ ":contains(" + ownerFolderName + ")", "3000"),
				"Verify the shared mount point folder is displayed in the My Files list view");

		// click on mount point folder
		app.zPageMyFiles.zListItem(Action.A_LEFTCLICK, ownerFolderName);

		// If there is a busy overlay, wait for that to finish
		//app.zPageOctopus.zWaitForBusyOverlayOctopus();

		// Verify the file present in the shared folder
		ZAssert.assertTrue(app.zPageMyFiles.zWaitForElementPresent(
				PageMyFiles.Locators.zMyFilesListView.locator
				+ ":contains("+ PPT_FILE + ")", "3000"),
				"Verify the file present in mount point folder.");
	}

	@Test(description = "Verify Share invitation has 'Add to my files' and 'Ignore' button.", groups = { "smoke" })
	public void VerifyShareNotificationMenu() throws HarnessException
	{
		String ownerFolderName = "ownerFolder"+ ZimbraSeleniumProperties.getUniqueString();
		shareFolder(ownerFolderName,PPT_FILE);

		// Login with grantee's Credential's.
		app.zPageLogin.zLogin(granteeAccount);

		// If there is a busy overlay, wait for that to finish
		app.zPageOctopus.zWaitForBusyOverlayOctopus();

		//Verify sharing notification
		ZAssert.assertTrue(app.zPageOctopus.sIsElementPresent(Locators.zNotificationCount.locator),
				"Verify Sharing notification recived to grantee");

		//Click on Notification count.
		app.zPageSharing.sClickAt(Locators.zNotificationCount.locator,"0,0");

		// If there is a busy overlay, wait for that to finish
		app.zPageOctopus.zWaitForBusyOverlayOctopus();

		// Verify Add to My files button in share Notification.
		ZAssert.assertTrue(app.zPageOctopus.sIsElementPresent(Locators.zAddToMyFiles.locator),
				"Verify Add to My files button is visible.");

		// If there is a busy overlay, wait for that to finish
		//app.zPageOctopus.zWaitForBusyOverlayOctopus();

		// Verify Ignore button in share Notification.
		ZAssert.assertTrue(app.zPageOctopus.sIsElementPresent(Locators.zIgnoreShare.locator),
				"Verify Ignore button is visible .");
	}

	@Test(description = "Ignore the share by clicking on 'Ignore' button.Verify the shared folder is not visible under My Files directory.", groups = { "smoke" })
	public void IgnoreShareUsingNotification() throws HarnessException
	{
		String ownerFolderName = "ownerFolder"+ ZimbraSeleniumProperties.getUniqueString();
		shareFolder(ownerFolderName,PPT_FILE);

		// Login with grantee's Credential's.
		app.zPageLogin.zLogin(granteeAccount);

		// If there is a busy overlay, wait for that to finish
		app.zPageOctopus.zWaitForBusyOverlayOctopus();

		//Verify sharing notification
		ZAssert.assertTrue(app.zPageOctopus.sIsElementPresent(Locators.zNotificationCount.locator),
				"Verify Sharing notification recived to grantee");

		//Click on Notification count.
		app.zPageSharing.sClickAt(Locators.zNotificationCount.locator,"0,0");

		//Click on 'Ignore' button from notification.
		app.zPageSharing.sClickAt(Locators.zIgnoreShare.locator,"0,0");

		// If there is a busy overlay, wait for that to finish
		app.zPageOctopus.zWaitForBusyOverlayOctopus();

		// Verify mount point folder gets added in My files list view.
		ZAssert.assertFalse(app.zPageMyFiles.zWaitForElementPresent(
				PageMyFiles.Locators.zMyFilesListViewItems.locator
				+ ":contains(" + ownerFolderName + ")", "3000"),
				"Verify the mount point folder is not visible in the My Files list view");

		// If there is a busy overlay, wait for that to finish
		//app.zPageOctopus.zWaitForBusyOverlayOctopus();

		// Verify the ignored folder doesn't appear in My Files list view
		ZAssert.assertFalse(app.zPageOctopus.zIsItemInCurentListView(ownerFolderName),
				"Verify the ignored share folder doesn't appears in My Files list view");
	}

	@Test(description = "Revoking permission does not allow folder access.", groups = { "smoke" })
	public void FolderNotAccesibleAfterRevoke() throws HarnessException
	{
		// share folder via soap
		FolderItem folder = createFolderViaSoap(app.zGetActiveAccount());
		shareFolderViaSoap(app.zGetActiveAccount(), granteeAccount, folder,SHARE_AS_READWRITE);

		String mountPointFolderName = "mountFolder";
		FolderItem granteeBrifcase = FolderItem.importFromSOAP(granteeAccount, SystemFolder.Briefcase);
		// Create a mount point of shared folder in grantee's account
		granteeAccount.soapSend(
				"<CreateMountpointRequest xmlns='urn:zimbraMail'>"
						+ "<link l='" + granteeBrifcase.getId()+"' name='" + mountPointFolderName
						+ "' view='document' rid='" + folder.getId()
						+ "' zid='" + app.zGetActiveAccount().ZimbraId + "'/>"
						+"</CreateMountpointRequest>");

		// revoke folder via soap
		revokeShareFolderViaSoap(app.zGetActiveAccount(), granteeAccount, folder);

		// Click on History tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_HISTORY);
		app.zPageOctopus.zWaitForBusyOverlayOctopus();

		// Verify revoked history for owner.
		VerifyHistory(GetText.revoke(SHARE_PERMISSION.SHARE_AS_READWRITE,folder.getName(),granteeAccount));

		// Logout grantee
		app.zPageOctopus.zLogout();

		// Login with grantee's Credential's.
		app.zPageLogin.zLogin(granteeAccount);

		// click on Shared mount point folder
		app.zPageMyFiles.zListItem(Action.A_LEFTCLICK, mountPointFolderName);

		// If there is a busy overlay, wait for that to finish
		//app.zPageOctopus.zWaitForBusyOverlayOctopus();

		// Verify the file present in the shared mount point folder
		ZAssert.assertFalse(app.zPageMyFiles.zWaitForElementPresent(
				PageMyFiles.Locators.zMyFilesListView.locator
				+ ":contains("+ PPT_FILE + ")", "3000"),
				"Verify the file is not availble in shared mount point folder.");
	}

	//Common code to share folder using Soap request
	public void shareFolder(String ownerFolderName,String fileInFolder) throws HarnessException
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

		FolderItem ownerFolder = FolderItem.importFromSOAP(owner, ownerFolderName);

		//upload file to folder
		uploadFileViaSoap(app.zGetActiveAccount(),fileInFolder, ownerFolder);

		//Share folder with grantee using Edit access
		shareFolderViaSoap(owner, granteeAccount, ownerFolder, SHARE_AS_READWRITE);

		// Logout owner
		app.zPageOctopus.zLogout();
	}

}
