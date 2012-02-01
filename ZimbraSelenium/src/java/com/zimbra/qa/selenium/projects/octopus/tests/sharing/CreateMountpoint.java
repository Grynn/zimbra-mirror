package com.zimbra.qa.selenium.projects.octopus.tests.sharing;

import java.util.List;
import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderMountpointItem;
import com.zimbra.qa.selenium.framework.items.IOctListViewItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.octopus.core.OctopusCommonTest;
import com.zimbra.qa.selenium.projects.octopus.ui.PageMyFiles;
public class CreateMountpoint extends OctopusCommonTest {

	private ZimbraAccount ownerAccount = null;

	public CreateMountpoint() {
		logger.info("New " + CreateMountpoint.class.getCanonicalName());

		// Test starts at the Octopus page
		super.startingPage = app.zPageSharing;
		super.startingAccountPreferences = null;

		ownerAccount = new ZimbraAccount();
		ownerAccount.provision();
		ownerAccount.authenticate();
	}

	@Test(description = "Create a mountpoint to a shared folder using SOAP", groups = { "functional" })
	public void CreateMountpoint_01() throws HarnessException {
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

		ZAssert.assertEquals(folderMountpointItem.getName(), folderMountpointName,
				"Verify the server mountpoint name");
		
		// click on My Files tab
		PageMyFiles pageMyFiles = (PageMyFiles) app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);
				
		// Verify the mountpoint exists in the list view
		List<IOctListViewItem> items = app.zPageOctopus.zGetListViewItems();
		
		ZAssert.assertNotNull(items,  "Verify list view is not empty");
		
		//ZAssert.assertContains(items, folderMountpointName,
		//"Verify list view contains mountpoint folder");	
		
		ZAssert
		.assertTrue(pageMyFiles.zWaitForElementPresent(
				PageMyFiles.Locators.zMyFilesListViewItems.locator
						+ ":contains(" + folderMountpointName
						+ ")", "3000"),
				"Verify the mountpoint folder is displayed in the My Files list view");
	}	
}
