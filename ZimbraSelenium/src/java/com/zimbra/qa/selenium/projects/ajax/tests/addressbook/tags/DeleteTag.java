package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.tags;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.TagItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogWarning;


public class DeleteTag extends AjaxCommonTest {

	public DeleteTag() {
		logger.info("New "+ DeleteTag.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageAddressbook;
		super.startingAccountPreferences = null;
		
	}
	
	@Test(	description = "Delete a tag - Right click, Delte",
			groups = { "smoke" })
	public void RightClickSelectDelete() throws HarnessException {
						
		// Create the tag to delete
		String name = "tag" + ZimbraSeleniumProperties.getUniqueString();
		
		app.zGetActiveAccount().soapSend(
				"<CreateTagRequest xmlns='urn:zimbraMail'>" +
                	"<tag name='"+ name +"' color='1' />" +
                "</CreateTagRequest>");

		TagItem tag = TagItem.importFromSOAP(app.zGetActiveAccount(), name);
		ZAssert.assertNotNull(tag, "Verify the tag was created");
				
		 // Refresh the view, to pick up the new tag
	    FolderItem contactFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), "Contacts");	      
	    app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, contactFolder);
	
		// Delete the tag using context menu
		DialogWarning dialog = (DialogWarning) app.zTreeContacts.zTreeItem(Action.A_RIGHTCLICK, Button.B_DELETE, tag);
		ZAssert.assertNotNull(dialog, "Verify the warning dialog opened");
						
		// Click "Yes" to confirm
		dialog.zClickButton(Button.B_YES);
		app.zPageAddressbook.zWaitForBusyOverlay();
		
		
		// To check whether deleted tag is exist
		app.zGetActiveAccount().soapSend("<GetTagRequest xmlns='urn:zimbraMail'/>");

		String tagname = app.zGetActiveAccount().soapSelectValue("//mail:GetTagResponse//mail:tag[@name='" + name + "']","name");
		ZAssert.assertNull(tagname, "Verify the tag is deleted");
		
	}

	


}
