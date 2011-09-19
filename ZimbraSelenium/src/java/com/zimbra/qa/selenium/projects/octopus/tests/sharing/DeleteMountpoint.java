package com.zimbra.qa.selenium.projects.octopus.tests.sharing;

import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderMountpointItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.octopus.core.OctopusCommonTest;

public class DeleteMountpoint extends OctopusCommonTest {

	private ZimbraAccount ownerAccount = null;

	public DeleteMountpoint() {
		logger.info("New " + DeleteMountpoint.class.getCanonicalName());

		// Test starts at the Octopus page
		super.startingPage = app.zPageOctopus;
		super.startingAccountPreferences = null;

		ownerAccount = new ZimbraAccount();
		ownerAccount.provision();
		ownerAccount.authenticate();
	}

	@Test(description = "Delete a mountpoint to a shared folder using pull down list", groups = { "functional" })
	public void DeleteMountpoint_01() throws HarnessException {
		FolderItem ownerBriefcaseRootFolder = FolderItem.importFromSOAP(
				ownerAccount, SystemFolder.Briefcase);

		ZAssert.assertNotNull(ownerBriefcaseRootFolder,
				"Verify the owner Briefcase root folder exists");

		FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(),
				SystemFolder.Trash);
		ZAssert.assertNotNull(trash, "Verify the trash is available");
		
		// Owner creates a folder, shares it with current user
		String ownerFoldername = "ownerFolder"
				+ ZimbraSeleniumProperties.getUniqueString();

		ownerAccount.soapSend("<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+ "<folder name='" + ownerFoldername + "' l='"
				+ ownerBriefcaseRootFolder.getId() + "'/>"
				+ "</CreateFolderRequest>");

		// Verify the share folder exists on the server
		FolderItem ownerFolder = FolderItem.importFromSOAP(ownerAccount,
				ownerFoldername);

		ZAssert
				.assertNotNull(ownerFolder,
						"Verify the owner share folder exists");

		ZimbraAccount currentAccount = app.zGetActiveAccount();

		ownerAccount.soapSend("<FolderActionRequest xmlns='urn:zimbraMail'>"
				+ "<action id='" + ownerFolder.getId() + "' op='grant'>"
				+ "<grant d='" + currentAccount.EmailAddress
				+ "' gt='usr' perm='r'/>" + "</action>"
				+ "</FolderActionRequest>");

		// Current user creates the mountpoint that points to the share
		FolderItem currentAccountRootFolder = FolderItem.importFromSOAP(
				currentAccount, SystemFolder.Briefcase);

		String folderMountpointName = "mountpoint"
				+ ZimbraSeleniumProperties.getUniqueString();

		currentAccount
				.soapSend("<CreateMountpointRequest xmlns='urn:zimbraMail'>"
						+ "<link l='" + currentAccountRootFolder.getId()
						+ "' name='" + folderMountpointName
						+ "' view='document' rid='" + ownerFolder.getId()
						+ "' zid='" + ownerAccount.ZimbraId + "'/>"
						+ "</CreateMountpointRequest>");

		// Verify the mountpoint exists on the server
		FolderMountpointItem folderMountpointItem = FolderMountpointItem
				.importFromSOAP(currentAccount, folderMountpointName);
		ZAssert.assertNotNull(folderMountpointItem,
				"Verify the mountpoint is available");

		// refresh Octopus page
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);

		SleepUtil.sleepVerySmall();
		
		// Delete the mountpoint folder using drop down list option
		app.zPageOctopus.zToolbarPressPulldown(Button.B_MY_FILES_LIST_ITEM, Button.O_DELETE, folderMountpointItem);

		// Verify the mountpoint folder is now in the trash
		for(int i = 0; i<5; i++){
			folderMountpointItem = FolderMountpointItem.importFromSOAP(currentAccount,
					folderMountpointName);
			if(folderMountpointItem != null && trash.getId().contentEquals(folderMountpointItem.getParentId()))
				break;
			SleepUtil.sleepVerySmall();
		}
		
		ZAssert.assertNotNull(folderMountpointItem,
				"Verify the mountpoint is again available");
		ZAssert.assertEquals(trash.getId(), folderMountpointItem.getParentId(),
				"Verify the mountpoint's parent is now the trash folder");
	}
}
