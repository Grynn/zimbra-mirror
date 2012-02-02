package com.zimbra.qa.selenium.projects.octopus.tests.search;

import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.IOctListViewItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.octopus.core.OctopusCommonTest;
import com.zimbra.qa.selenium.projects.octopus.ui.DialogFileShare;

public class SharePublicly extends OctopusCommonTest {

	public SharePublicly() {
		logger.info("New " + SharePublicly.class.getCanonicalName());

		// test starts at the Search tab
		super.startingPage = app.zPageSearch;
		super.startingAccountPreferences = null;
		
	}

	@Test(
			description = "Share a file from the search results", 
			groups = { "smoke" })
	public void SharePublicly_01() throws HarnessException {

		String filename = "filename"+ ZimbraSeleniumProperties.getUniqueString() +".txt";
		String filePath = ZimbraSeleniumProperties.getBaseDirectory()
				+ "/data/public/documents/doc01/plaintext.txt";
	
		

		// Upload file to server through RestUtil
		String attachmentId = app.zGetActiveAccount().uploadFile(filePath);

		// Save uploaded file through SOAP
		FolderItem briefcaseRootFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Briefcase);

		app.zGetActiveAccount().soapSend(
					"<SaveDocumentRequest xmlns='urn:zimbraMail'>"
				+		"<doc name='"+ filename +"' l='" + briefcaseRootFolder.getId() + "'>"
				+			"<upload id='" + attachmentId + "'/>"
				+		"</doc>"
				+	"</SaveDocumentRequest>");
		String documentId = app.zGetActiveAccount().soapSelectValue("//mail:doc", "id");


		
		// Sync up
//		app.zPageOctopus.zToolbarPressButton(Button.B_GETMAIL);


		// Search for the message
		app.zPageSearch.zExecuteSearchQuery(filename);
		
		// Get all the messages in the view
		// Verify the uploaded file exists
		IOctListViewItem found = null;
		List<IOctListViewItem> items = app.zPageSearch.zGetListViewItems();
		for (IOctListViewItem item : items) {
			if ( item.getListViewName().equalsIgnoreCase(filename) ) {
				// found it
				found = item;
				break;
			}
		}
				
		ZAssert.assertNotNull(found, "Verify the item is found in the list after searching");

		
		// Share the file
		DialogFileShare dialog = (DialogFileShare)app.zPageSearch.zToolbarPressPulldown(
														Button.B_MY_FILES_LIST_ITEM, 
														Button.O_FILE_SHARE, 
														found.getListViewName());
		
		
		// Click through the dialog
		dialog.zClickButton(Button.B_CLOSE);
		SleepUtil.sleepMedium();
		
		// Verify the document is shared
		app.zGetActiveAccount().soapSend(
				"<GetItemRequest xmlns='urn:zimbraMail'>"
			+		"<item id='"+ documentId +"'/>"
			+	"</GetItemRequest>");

		String perm = app.zGetActiveAccount().soapSelectValue("//mail:grant[@gt='pub']", "perm");
		
		ZAssert.assertEquals(perm, "r", "Verify there is a public share for read-only on the document");
		

	}


}
