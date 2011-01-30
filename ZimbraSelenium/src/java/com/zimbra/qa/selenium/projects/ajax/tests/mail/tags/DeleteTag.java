package com.zimbra.qa.selenium.projects.ajax.tests.mail.tags;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.TagItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogWarning;


public class DeleteTag extends AjaxCommonTest {

	public DeleteTag() {
		logger.info("New "+ DeleteTag.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageMail;
		super.startingAccountPreferences = null;
		
	}
	
	@Test(	description = "Delete a tag - Right click, Delete",
			groups = { "smoke" })
	public void DeleteTag_01() throws HarnessException {
		
		

		
		// Create the tag to delete
		String name = "tag" + ZimbraSeleniumProperties.getUniqueString();
		
		app.zGetActiveAccount().soapSend(
				"<CreateTagRequest xmlns='urn:zimbraMail'>" +
                	"<tag name='"+ name +"' color='1' />" +
                "</CreateTagRequest>");

		TagItem tag = TagItem.importFromSOAP(app.zGetActiveAccount(), name);
		ZAssert.assertNotNull(tag, "Verify the tag was created");
		
		
		// Click on Get Mail to refresh the folder list
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Delete the tag using context menu
		DialogWarning dialog = (DialogWarning) app.zTreeMail.zTreeItem(Action.A_RIGHTCLICK, Button.B_DELETE, tag);
		ZAssert.assertNotNull(dialog, "Verify the warning dialog opened");
		
		// Click "Yes" to confirm
		dialog.zClickButton(Button.B_YES);

		
		// Verify the tag is no longer found
		tag = TagItem.importFromSOAP(app.zGetActiveAccount(), name);
		ZAssert.assertNull(tag, "Verify the tag is deleted");

		
	}

	


}
