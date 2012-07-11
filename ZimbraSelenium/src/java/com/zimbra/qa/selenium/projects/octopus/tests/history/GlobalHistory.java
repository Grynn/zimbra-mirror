package com.zimbra.qa.selenium.projects.octopus.tests.history;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.octopus.ui.DialogFileShare;
import com.zimbra.qa.selenium.projects.octopus.ui.DisplayFilePreview;
import com.zimbra.qa.selenium.projects.octopus.ui.PageHistory.GetText;
import com.zimbra.qa.selenium.projects.octopus.ui.PageMyFiles;

public class GlobalHistory extends HistoryCommonTest {

	private FolderItem folder = null;
	private ZimbraAccount granteeAccount = null;

	public GlobalHistory()
	{
		logger.info("New " + GlobalHistory.class.getCanonicalName());

		// test starts at the History tab
		super.startingPage = app.zPageHistory;
		super.startingAccountPreferences = null;
		granteeAccount = new ZimbraAccount();
		granteeAccount.provision();
		granteeAccount.authenticate();
	}

	@Test(description = "Check global history for comments action", groups = { "smoke" })
	public void CheckGlobalHistoryForComment() throws HarnessException
	{
		String fileName=TEXT_FILE;

		//Click on the My Files tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);
		String  fileId = uploadFileViaSoap(app.zGetActiveAccount(), fileName);

		// Select file in the list view
		DisplayFilePreview filePreview = (DisplayFilePreview) app.zPageMyFiles.zListItem(Action.A_LEFTCLICK, fileName);

		//make comment via soap
		String comment = "Comment" + ZimbraSeleniumProperties.getUniqueString();
		makeCommentViaSoap(app.zGetActiveAccount(), fileId, comment);

		//Click on History tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_HISTORY);

		//Assert if found history matches with comment file history
		VerifyHistory(GetText.comment(fileName));
	}

	@Test(description = "Check global history for rename action", groups = { "smoke" })
	public void CheckGlobalHistoryForRename() throws HarnessException
	{
		String fileName=PPT_FILE;

		//Click on the My Files tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);

		String  fileId = uploadFileViaSoap(app.zGetActiveAccount(), fileName);

		//rename via soap
		String newName = "NewName " + ZimbraSeleniumProperties.getUniqueString() +
		fileName.substring(fileName.indexOf("."),fileName.length());
		renameViaSoap(app.zGetActiveAccount(), fileId, newName);

		//To get file rename  history ,user needs to do refresh first -Workaround
		app.zPageMyFiles.zRefresh();

		//Click on History tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_HISTORY);

		//Assert if found history matches with Rename file history
		VerifyHistory(GetText.rename(fileName, newName));
	}

	@Test(description = "Check global history for favorites action", groups = { "smoke" })
	public void CheckGlobalHistoryForFavorites() throws HarnessException
	{
		String fileName=EXCEL_FILE;
		//click on the My Files tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);

		String  fileId = uploadFileViaSoap(app.zGetActiveAccount(), fileName);

		//mark file as favorite via soap
		markFileFavoriteViaSoap(app.zGetActiveAccount(), fileId);

		//To get favorite  history ,user needs to do refresh first -Workaround
		app.zPageOctopus.zRefresh();

		//Click on History tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_HISTORY);

		//Assert if found history matches with favorites file history
		VerifyHistory(GetText.favorite(fileName));

		//Click on the My Files tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);

		//Un-mark file as favorite via soap
		unMarkFileFavoriteViaSoap(app.zGetActiveAccount(), fileId);

		//To get history ,user needs to do refresh first -Workaround//
		app.zPageOctopus.zRefresh();

		//Click on History tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_HISTORY);

		//Assert if found history matches with No favorites file history
		VerifyHistory(GetText.favorite(fileName));
	}

	@Test(description = "Check global history for file upload action", groups = { "smoke" })
	public void CheckGlobalHistoryForFileUpload() throws HarnessException
	{
		String fileName=JPG_FILE;

		// click on the My Files tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);

		uploadFileViaSoap(app.zGetActiveAccount(), fileName);

		//Click on History tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_HISTORY);

		//Assert if found history matches with upload file history
		VerifyHistory(GetText.newVersion(fileName));
	}

	@Test(description = "Check global history for file delete action", groups = { "smoke" })
	public void CheckGlobalHistoryForDeleteAction() throws HarnessException
	{
		String fileName=TEXT_FILE;

		//click on the My Files tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);

		uploadFileViaSoap(app.zGetActiveAccount(), fileName);

		//delete file using right click context menu
		app.zPageMyFiles.zToolbarPressPulldown(Button.B_MY_FILES_LIST_ITEM,
				Button.O_DELETE, fileName);

		//To get delete history ,user needs to do refresh first-Workaround
		app.zPageOctopus.zRefresh();

		//Click on History tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_HISTORY);

		//Assert if found history matches with Delete file history
		VerifyHistory(GetText.deleteFile(fileName));
	}

	@Test(description = "Check global history for file Share action", groups = { "smoke" })
	public void CheckGlobalHistoryForShareAction() throws HarnessException
	{
		String fileName=EXCEL_FILE;

		//click on the My Files tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);

		//Upload file via Soap
		uploadFileViaSoap(app.zGetActiveAccount(), fileName);

		// Click on Share option in the file Context menu
		DialogFileShare dialogFileShare = (DialogFileShare) app.zPageMyFiles
		.zToolbarPressPulldown(Button.B_MY_FILES_LIST_ITEM,
				Button.O_FILE_SHARE, fileName);

		// Click on Close button
		dialogFileShare.zClickButton(Button.B_CLOSE);

		// If there is a busy overlay, wait for that to finish
		app.zPageOctopus.zWaitForBusyOverlayOctopus();

		// Verify the file share icon is displayed
		ZAssert.assertTrue(app.zPageMyFiles.zWaitForElementPresent(
				PageMyFiles.Locators.zMyFilesListViewItems.locator
				+ " img[src*='shared_badge.png']", "3000"),
		"Verify the file share icon is displayed");

		//Click on History tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_HISTORY);

		//Assert if found history matches with share file history
		VerifyHistory(GetText.shareFile(fileName));
	}

	@Test(description = "Check global history for folder creation", groups = { "smoke" })
	public void CheckGlobalHistoryForFolderCreation() throws HarnessException
	{
		//click on the My Files tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);

		app.zPageMyFiles.zToolbarPressPulldown(Button.B_MY_FILES,
				Button.O_NEW_FOLDER);

		//To get folder creation history ,user needs to do refresh first -Workaround
		app.zPageMyFiles.zRefresh();

		//Click on History tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_HISTORY);

		//Assert if found history matches with share file history
		VerifyHistory(GetText.createFolder());
	}

	@Test(description = "Check global history for folder deletion", groups = { "smoke" })
	public void CheckGlobalHistoryForFolderDeletion() throws HarnessException
	{
		//click on the My Files tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);

		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseRootFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);
		// Create the sub-folder
		String subFolderName = "folder"
			+ ZimbraSeleniumProperties.getUniqueString();

		// Verify the sub-folder exists on the server
		FolderItem subFolder = createFolderViaSoap(account, briefcaseRootFolder);

		// delete folder using SOAP
		deleteFolderViaSoap(account,subFolder);

		//To get file folder deletion history ,user needs to do refresh first -Workaround
		app.zPageMyFiles.zRefresh();

		//Click on History tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_HISTORY);

		//Assert if found history matches with share file history
		VerifyHistory(GetText.deleteFolder(subFolderName));
	}

	@Test(description = "Check global history for rename folder", groups = { "smoke" })
	public void CheckGlobalHistoryForFolderRename() throws HarnessException
	{
		//click on the My Files tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);

		//Create folder via soap
		folder = createFolderViaSoap(app.zGetActiveAccount());

		//rename folder via soap
		String folderOldName = folder.getName();
		String folderNewName = "RenameFolder " +  app.zGetActiveAccount().getPref("displayName");
		renameViaSoap(app.zGetActiveAccount(), folder.getId(), folderNewName);

		//updated 'folder' object
		folder = FolderItem.importFromSOAP(app.zGetActiveAccount(), folderNewName);

		//Click on History tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_HISTORY);

		//Assert if found history matches with share file history
		VerifyHistory(GetText.rename(folderOldName, folderNewName,"1"));
	}

	@Test(description = "Check global history for move folder", groups = { "smoke" })
	public void CheckGlobalHistoryForFolderMove() throws HarnessException
	{
		//click on the My Files tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseRootFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		// Create two briefcase sub-folders:
		// One folder to Move & Another folder to move into
		String subFolderName1 = "folder1"
			+ ZimbraSeleniumProperties.getUniqueString();
		String subFolderName2 = "folder2"
			+ ZimbraSeleniumProperties.getUniqueString();

		// Verify the sub-folder exists on the server
		FolderItem subFolderItem1 = createFolderViaSoap(account,subFolderName1,briefcaseRootFolder);

		// Verify the destination sub-folder exists on the server
		FolderItem subFolderItem2 = createFolderViaSoap(account,subFolderName2,briefcaseRootFolder);

		// move folder using SOAP
		app.zPageOctopus.moveItemUsingSOAP(subFolderItem1.getId(),
				subFolderItem2.getId(), account);

		// click on destination sub-folder
		app.zPageMyFiles.zListItem(Action.A_LEFTCLICK, subFolderName2);

		// If there is a busy overlay, wait for that to finish
		app.zPageOctopus.zWaitForBusyOverlayOctopus();

		// Verify the first sub-folder is now in the destination folder
		ZAssert.assertTrue(app.zPageOctopus.zIsItemInCurentListView(subFolderName1),
		"Verify the first sub-folder was moved to the destination folder");

		//To get folder move history ,user needs to do refresh first -Workaround
		app.zPageMyFiles.zRefresh();

		//Click on History tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_HISTORY);

		//Assert if found history matches with share file history
		VerifyHistory(GetText.moveFolder(subFolderName1, subFolderName2));

	}

	@Test(description = "Check global history for share folder", groups = { "smoke" })
	public void CheckGlobalHistoryForFolderShare() throws HarnessException
	{
		//click on the My Files tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);

		// share folder via soap
		FolderItem folder = createFolderViaSoap(app.zGetActiveAccount());
		shareFolderViaSoap(app.zGetActiveAccount(), granteeAccount, folder,SHARE_AS_READWRITE);

		//To get folder share history ,user needs to do refresh first -Workaround
		app.zPageMyFiles.zRefresh();

		//Click on History tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_HISTORY);

		//Assert if found history matches with share file history
		VerifyHistory(GetText.share(SHARE_PERMISSION.SHARE_AS_READWRITE, folder.getName(), granteeAccount));
	}

	@Test(description = "Check global history for revoke folder", groups = { "smoke" })
	public void CheckGlobalHistoryForFolderRevoke() throws HarnessException
	{
		//click on the My Files tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);

		// share folder via soap
		FolderItem folder = createFolderViaSoap(app.zGetActiveAccount());
		shareFolderViaSoap(app.zGetActiveAccount(), granteeAccount, folder,SHARE_AS_READWRITE);

		// revoke folder via soap
		revokeShareFolderViaSoap(app.zGetActiveAccount(), granteeAccount, folder);

		//To get folder revoke history ,user needs to do refresh first -Workaround
		app.zPageMyFiles.zRefresh();

		// Click on History tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_HISTORY);

		// Verify revoked history for owner.
		VerifyHistory(GetText.revoke(SHARE_PERMISSION.SHARE_AS_READWRITE,folder.getName(),granteeAccount));
	}

}


