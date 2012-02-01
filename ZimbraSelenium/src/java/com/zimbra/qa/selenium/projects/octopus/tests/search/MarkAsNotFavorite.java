package com.zimbra.qa.selenium.projects.octopus.tests.search;

import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.IOctListViewItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.octopus.core.OctopusCommonTest;

public class MarkAsNotFavorite extends OctopusCommonTest {

	public MarkAsNotFavorite() {
		logger.info("New " + MarkAsNotFavorite.class.getCanonicalName());

		// test starts at the Search tab
		super.startingPage = app.zPageSearch;
		super.startingAccountPreferences = null;
		
	}

	@Test(
			description = "Mark a file as not favorite from the search results", 
			groups = { "smoke" })
	public void MarkAsNotFavorite_01() throws HarnessException {

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


		// Mark it a favorite
		app.zGetActiveAccount().soapSend(
				"<DocumentActionRequest xmlns='urn:zimbraMail'>"
			+		"<action op='watch' id='" + documentId + "'/>"
			+	"</DocumentActionRequest>");

		
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
		app.zPageSearch.zToolbarPressPulldown(Button.B_MY_FILES_LIST_ITEM, 
														Button.O_NOT_FAVORITE, 
														found.getListViewName());

		
		// Verify the document is shared
		app.zGetActiveAccount().soapSend(
				"<GetWatchingItemsRequest xmlns='urn:zimbraMail'/>");

		Element[] nodes = app.zGetActiveAccount().soapSelectNodes("//mail:target[@email='"+ app.zGetActiveAccount().EmailAddress +"']//mail:item[@id='"+ documentId + "']");
		ZAssert.assertEquals(nodes.length, 0, "Verify the item does not appear in the watch list");		
		

	}


}
