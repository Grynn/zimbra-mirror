package com.zimbra.qa.selenium.projects.ajax.tests.briefcase.folders;

import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;

public class DragAndDropFolder extends AjaxCommonTest {

	public DragAndDropFolder() {
		logger.info("New " + DragAndDropFolder.class.getCanonicalName());

		// All tests start at the Briefcase page
		super.startingPage = app.zPageBriefcase;
		super.startingAccountPreferences = null;
	}

	@Test(description = "Drag one briefcase sub-folder and Drop into other", groups = { "functional" })
	public void DragAndDropFolder_01() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseRootFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		ZAssert.assertNotNull(briefcaseRootFolder,
				"Verify the Briefcase root folder is available");

		// Create two briefcase sub-folders:One folder to Drag & Another folder
		// to drop into
		String briefcaseSubFolderName1 = "folder1"
				+ ZimbraSeleniumProperties.getUniqueString();
		String briefcaseSubFolderName2 = "folder2"
				+ ZimbraSeleniumProperties.getUniqueString();

		account.soapSend("<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+ "<folder name='" + briefcaseSubFolderName1 + "' l='"
				+ briefcaseRootFolder.getId() + "'/>"
				+ "</CreateFolderRequest>");

		FolderItem briefcaseSubFolder1 = FolderItem.importFromSOAP(account,
				briefcaseSubFolderName1);
		ZAssert.assertNotNull(briefcaseSubFolder1,
				"Verify the first subfolder is available");

		account.soapSend("<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+ "<folder name='" + briefcaseSubFolderName2 + "' l='"
				+ briefcaseRootFolder.getId() + "'/>"
				+ "</CreateFolderRequest>");

		FolderItem briefcaseSubFolder2 = FolderItem.importFromSOAP(account,
				briefcaseSubFolderName2);
		ZAssert.assertNotNull(briefcaseSubFolder2,
				"Verify the second subfolder is available");

		// refresh the Briefcase tree folder list
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseRootFolder,
				false);

		// Perform DND action
		app.zPageBriefcase.zDragAndDrop("css=td#zti__main_Briefcase__"
				+ briefcaseSubFolder1.getId() + "_textCell:contains("
				+ briefcaseSubFolder1.getName() + ")",
				"css=td#zti__main_Briefcase__" + briefcaseSubFolder2.getId()
						+ "_textCell:contains(" + briefcaseSubFolder2.getName()
						+ ")");

		// Verify the folder is now in the other sub-folder
		briefcaseSubFolder1 = FolderItem.importFromSOAP(account,
				briefcaseSubFolderName1);
		ZAssert.assertNotNull(briefcaseSubFolder1,
				"Verify the subfolder is still available");
		ZAssert.assertEquals(briefcaseSubFolder2.getId(), briefcaseSubFolder1
				.getParentId(),
				"Verify the subfolder's parent is now the other subfolder");
	}
}
