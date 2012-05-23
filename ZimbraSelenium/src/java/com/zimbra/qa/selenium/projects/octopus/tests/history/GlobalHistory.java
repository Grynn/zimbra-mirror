package com.zimbra.qa.selenium.projects.octopus.tests.history;

import java.util.ArrayList;

import org.testng.annotations.*;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.HistoryItem;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.octopus.core.OctopusCommonTest;
import com.zimbra.qa.selenium.projects.octopus.ui.DialogFileShare;
import com.zimbra.qa.selenium.projects.octopus.ui.PageMyFiles;
import com.zimbra.qa.selenium.projects.octopus.ui.PageHistory.GetText;

public class GlobalHistory extends OctopusCommonTest {

	private String fileName=TEXT_FILE;

	public GlobalHistory() {
		logger.info("New " + GlobalHistory.class.getCanonicalName());

		// test starts at the History tab
		super.startingPage = app.zPageHistory;
		super.startingAccountPreferences = null;
	}

	//Verify found history matches with required history.
	private void VerifyHistory(String requiredHistory) throws HarnessException
	{
		boolean found = false;
		ArrayList<HistoryItem> historyItems = new ArrayList<HistoryItem>();
		historyItems = app.zPageHistory.zListItem();
		for (HistoryItem historyItem : historyItems)
		{
			if(historyItem.getHistoryText().contains(requiredHistory))
			{
				found = true;
				break;
			}
		}
		ZAssert.assertTrue(found, "Verify if "+ requiredHistory +" history found");
	}

	@Test(description = "Check gloabl history for comments action", groups = { "smoke" })
	public void CheckGlobalHistoryForComment() throws HarnessException 
	{
		String  fileId = uploadFileViaSoap(app.zGetActiveAccount(), fileName);  

		//make comment via soap
		String comment = "Comment" + ZimbraSeleniumProperties.getUniqueString();
		makeCommentViaSoap(app.zGetActiveAccount(), fileId, comment);

		//Click on History tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_HISTORY);

		//Assert if found history matches with comment file history
		VerifyHistory(GetText.comment(fileName));
	}

	@Test(description = "Check gloabl history for rename action", groups = { "smoke2" })
	public void CheckGlobalHistoryForRename() throws HarnessException 
	{
		//Click on the My Files tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);

		String  fileId = uploadFileViaSoap(app.zGetActiveAccount(), fileName);  

		//rename via soap
		String newName = "New Name " + ZimbraSeleniumProperties.getUniqueString() +
				fileName.substring(fileName.indexOf("."),fileName.length());
		renameViaSoap(app.zGetActiveAccount(), fileId, newName);

		//Click on History tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_HISTORY);

		//Assert if found history matches with Rename file history
		VerifyHistory(GetText.rename(fileName, newName));
	}

	@Test(description = "Check gloabl history for favorites action", groups = { "smoke" })
	public void CheckGlobalHistoryForFavorites() throws HarnessException 
	{
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

	@Test(description = "Check gloabl history for file upload action", groups = { "smoke" })
	public void CheckGlobalHistoryForFileUpload() throws HarnessException 
	{
		// click on the My Files tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);

		uploadFileViaSoap(app.zGetActiveAccount(), fileName);  

		//Click on History tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_HISTORY);

		//Assert if found history matches with upload file history
		VerifyHistory(GetText.newVersion(fileName));
	}

	@Test(description = "Check gloabl history for file delete action", groups = { "smoke" })
	public void CheckGlobalHistoryForDeleteAction() throws HarnessException 
	{
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

	@Test(description = "Check gloabl history for file Share action", groups = { "smoke" })
	public void CheckGlobalHistoryForShareAction() throws HarnessException 
	{
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

}


