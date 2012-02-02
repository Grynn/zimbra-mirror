package com.zimbra.qa.selenium.projects.octopus.tests.search;

import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.IOctListViewItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.octopus.core.OctopusCommonTest;

public class RenameFile extends OctopusCommonTest {

	public RenameFile() {
		logger.info("New " + RenameFile.class.getCanonicalName());

		// test starts at the Search tab
		super.startingPage = app.zPageSearch;
		super.startingAccountPreferences = null;
		
	}

	@Test(
			description = "Rename a file from the search results", 
			groups = { "smoke" })
	public void RenameFile_01() throws HarnessException {

		String extension = ".txt";
		String name1 = "filename"+ ZimbraSeleniumProperties.getUniqueString();
		String name2 = "filename"+ ZimbraSeleniumProperties.getUniqueString();
		String filePath = ZimbraSeleniumProperties.getBaseDirectory()
				+ "/data/public/documents/doc01/plaintext.txt";
	
		

		// Upload file to server through RestUtil
		String attachmentId = app.zGetActiveAccount().uploadFile(filePath);

		// Save uploaded file through SOAP
		FolderItem briefcaseRootFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Briefcase);

		app.zGetActiveAccount().soapSend(
					"<SaveDocumentRequest xmlns='urn:zimbraMail'>"
				+		"<doc name='"+ name1 + extension +"' l='" + briefcaseRootFolder.getId() + "'>"
				+			"<upload id='" + attachmentId + "'/>"
				+		"</doc>"
				+	"</SaveDocumentRequest>");
		String documentId = app.zGetActiveAccount().soapSelectValue("//mail:doc", "id");


		
		// Sync up
//		app.zPageOctopus.zToolbarPressButton(Button.B_GETMAIL);


		// Search for the message
		app.zPageSearch.zExecuteSearchQuery(name1);
		
		// Get all the messages in the view
		// Verify the uploaded file exists
		IOctListViewItem found = null;
		List<IOctListViewItem> items = app.zPageSearch.zGetListViewItems();
		for (IOctListViewItem item : items) {
			if ( item.getListViewName().equalsIgnoreCase(name1 + extension) ) {
				// found it
				found = item;
				break;
			}
		}
				
		ZAssert.assertNotNull(found, "Verify the item is found in the list after searching");

		
		// Share the file
		app.zPageSearch.zToolbarPressPulldown(Button.B_MY_FILES_LIST_ITEM, 
														Button.O_RENAME, 
														found.getListViewName());
		app.zPageSearch.rename(name2);

		
		// Verify the document is renamed
		app.zGetActiveAccount().soapSend(
				"<GetItemRequest xmlns='urn:zimbraMail'>"
			+		"<item id='"+ documentId +"'/>"
			+	"</GetItemRequest>");

		String n = app.zGetActiveAccount().soapSelectValue("//mail:doc", "name");
		
		ZAssert.assertEquals(n, name2 + extension, "Verify the file is renamed to the new filename ");
		

	}


}
