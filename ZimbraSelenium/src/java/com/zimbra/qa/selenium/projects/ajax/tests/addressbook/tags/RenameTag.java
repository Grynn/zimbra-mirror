package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.tags;

import org.testng.annotations.Test;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.TagItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.*;


public class RenameTag extends AjaxCommonTest {

	public RenameTag() {
		logger.info("New "+ RenameTag.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageAddressbook;
		super.startingAccountPreferences = null;
		
	}
	
	@Test(	description = "Rename a tag - Right click, Rename",
			groups = { "smoke" })
	public void RightClickSelectRename() throws HarnessException {		
		// Set the new tag name
		String name1 = "tag" + ZimbraSeleniumProperties.getUniqueString();
		
		// Create a tag to right click on
		app.zGetActiveAccount().soapSend(
				"<CreateTagRequest xmlns='urn:zimbraMail'>" +
            		"<tag name='"+ name1 +"' color='1' />" +
            	"</CreateTagRequest>");
		// Get the tag
		TagItem tag1 = app.zPageAddressbook.zGetTagItem(app.zGetActiveAccount(), name1);
		
		 // Refresh the view, to pick up the new tag
	    FolderItem contactFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), "Contacts");	      
	    app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, contactFolder);
	    		
		// Create a new tag using the context menu + New Tag
		DialogRenameTag dialog = (DialogRenameTag)app.zTreeContacts.zTreeItem(Action.A_RIGHTCLICK,  Button.B_RENAME, tag1);
		ZAssert.assertNotNull(dialog, "Verify the new dialog opened");
		
		// Create the tag to rename
		String name2 = "tag" + ZimbraSeleniumProperties.getUniqueString();
	
		// Rename the tag using the context menu
		ZAssert.assertNotNull(dialog, "Verify the warning dialog opened");
		
		// Set the new name, click OK
		dialog.zSetNewName(name2);
		dialog.zClickButton(Button.B_OK);

		app.zPageAddressbook.zWaitForBusyOverlay();
	
		// Verify the tag is no longer found
		app.zGetActiveAccount().soapSend("<GetTagRequest xmlns='urn:zimbraMail'/>");
		
		Element[] eTag1 = app.zGetActiveAccount().soapSelectNodes("//mail:tag[@name='"+ name1 +"']");
		ZAssert.assertEquals(eTag1.length, 0, "Verify the old tag name no longer exists");
		
		Element[] eTag2 = app.zGetActiveAccount().soapSelectNodes("//mail:tag[@name='"+ name2 +"']");
		ZAssert.assertEquals(eTag2.length, 1, "Verify the new tag name exists");

		
	}

	


}
