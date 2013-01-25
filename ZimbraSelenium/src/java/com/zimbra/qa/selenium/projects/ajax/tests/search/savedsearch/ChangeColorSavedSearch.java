package com.zimbra.qa.selenium.projects.ajax.tests.search.savedsearch;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.SavedSearchFolderItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DialogEditFolder;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DialogEditFolder.FolderColor;

public class ChangeColorSavedSearch extends PrefGroupMailByMessageTest {

	public ChangeColorSavedSearch() {
		logger.info("New " + ChangeColorSavedSearch.class.getCanonicalName());

		
		
		

	}

	@Test(
			description = "Edit a folder, change the color (Context menu -> Edit)",
			groups = { "functional" }
			)
	public void ChangeColorSavedSearch_01() throws HarnessException {

		
		//-- DATA setup
		//

		// Create the message data to be sent
		String name = "search" + ZimbraSeleniumProperties.getUniqueString();
		String query = "subject:(" + ZimbraSeleniumProperties.getUniqueString() + ")";

		app.zGetActiveAccount().soapSend(
				"<CreateSearchFolderRequest xmlns='urn:zimbraMail'>" +
					"<search name='"+ name +"' query='"+ query +"' l='1'/>" +
				"</CreateSearchFolderRequest>");

		// Get the item
		SavedSearchFolderItem item = SavedSearchFolderItem.importFromSOAP(app.zGetActiveAccount(), name);

		
		
		//-- GUI Actions
		//
		
		
		// Click on Get Mail to refresh the folder list
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Right click on the search, select edit
		// TODO: can the folder rename dialog be reused?  Or, do we need DialogRenameSavedSearchFolder class?
		DialogEditFolder dialog = (DialogEditFolder) app.zTreeMail.zTreeItem(Action.A_RIGHTCLICK, Button.B_EDIT, item);
		
		// Change the color, click OK
		dialog.zSetNewColor(FolderColor.Gray);
		dialog.zClickButton(Button.B_OK);

		
		//-- VERIFICATION
		//
		
		// Check the color
		app.zGetActiveAccount().soapSend(
				"<GetFolderRequest xmlns='urn:zimbraMail'>"
			+		"<folder id='" + item.getId() + "'/>"
			+	"</GetFolderRequest>");

		String color = app.zGetActiveAccount().soapSelectValue("//mail:search[@name='" + item.getName() + "']", "color");
		ZAssert.assertEquals(color, "8", "Verify the color of the folder is set to gray (8)");
	}

}
