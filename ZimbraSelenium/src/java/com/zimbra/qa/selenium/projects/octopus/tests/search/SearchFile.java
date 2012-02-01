package com.zimbra.qa.selenium.projects.octopus.tests.search;

import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.octopus.core.OctopusCommonTest;

public class SearchFile extends OctopusCommonTest {

	public SearchFile() {
		logger.info("New " + SearchFile.class.getCanonicalName());

		// test starts at the Search tab
		super.startingPage = app.zPageSearch;
		super.startingAccountPreferences = null;
		
	}

	@Test(
			description = "Search for a file by filename", 
			groups = { "sanity" })
	public void SearchFile_01() throws HarnessException {

		String filename = "filename"+ ZimbraSeleniumProperties.getUniqueString() +".xls";
		String filePath = ZimbraSeleniumProperties.getBaseDirectory()
				+ "/data/public/other/testexcelfile.xls";
		
		

		// Upload file to server through RestUtil
		String attachmentId = app.zGetActiveAccount().uploadFile(filePath);

		// Save uploaded file through SOAP
		FolderItem briefcaseRootFolder = FolderItem.importFromSOAP(
				app.zGetActiveAccount(), 
				SystemFolder.Briefcase);

		app.zGetActiveAccount().soapSend(
					"<SaveDocumentRequest xmlns='urn:zimbraMail'>"
				+		"<doc name='"+ filename +"' l='" + briefcaseRootFolder.getId() + "'>"
				+			"<upload id='" + attachmentId + "'/>"
				+		"</doc>"
				+	"</SaveDocumentRequest>");


		
		// Sync up
//		app.zPageOctopus.zToolbarPressButton(Button.B_GETMAIL);


		// Search for the message
		app.zPageSearch.zExecuteSearchQuery(filename);
		
		app.zPageSearch.zWaitForBusyOverlayOctopus();
		
		// Get all the messages in the view
		// Verify the uploaded file exists
		boolean found = false;
		List<String> items = app.zPageSearch.zGetListViewItems();
		for (String i : items) {
			if ( i.equals(filename)) {
				// found it
				found = true;
				break;
			}
		}
				
		ZAssert.assertTrue(found, "Verify the item is found after searching");

	}

	@Test(
			description = "Search for a text file by content", 
			groups = { "sanity" })
	public void SearchFile_02() throws HarnessException {
	}
	
	@Test(
			description = "Search for a binary (spreadsheet) file by content", 
			groups = { "sanity" })
	public void SearchFile_03() throws HarnessException {
	}
	
	@Test(description = "Search for a file in trash", 
			groups = { "smoke" })
	public void SearchFile_04() throws HarnessException {
	}

}
