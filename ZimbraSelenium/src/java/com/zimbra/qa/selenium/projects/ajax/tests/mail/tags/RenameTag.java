package com.zimbra.qa.selenium.projects.ajax.tests.mail.tags;

import org.testng.annotations.Test;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.items.TagItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.*;


public class RenameTag extends AjaxCommonTest {

	public RenameTag() {
		logger.info("New "+ RenameTag.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageMail;
		super.startingAccountPreferences = null;
		
	}
	
	@Test(	description = "Rename a tag - Right click, Rename",
			groups = { "smoke" })
	public void RenameTag_01() throws HarnessException {
		
		// Create the tag to rename
		String name1 = "tag" + ZimbraSeleniumProperties.getUniqueString();
		String name2 = "tag" + ZimbraSeleniumProperties.getUniqueString();
		
		app.zGetActiveAccount().soapSend(
				"<CreateTagRequest xmlns='urn:zimbraMail'>" +
                	"<tag name='"+ name1 +"' color='1' />" +
                "</CreateTagRequest>");

		TagItem tag = TagItem.importFromSOAP(app.zGetActiveAccount(), name1);
		ZAssert.assertNotNull(tag, "Verify the tag was created");
		
		
		// Click on Get Mail to refresh the folder list
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Rename the tag using the context menu
		DialogRenameTag dialog = (DialogRenameTag) app.zTreeMail.zTreeItem(Action.A_RIGHTCLICK, Button.B_RENAME, tag);
		ZAssert.assertNotNull(dialog, "Verify the warning dialog opened");
		
		// Set the new name, click OK
		dialog.zSetNewName(name2);
		dialog.zClickButton(Button.B_OK);

		
		// Verify the tag is no longer found
		app.zGetActiveAccount().soapSend("<GetTagRequest xmlns='urn:zimbraMail'/>");
		
		Element[] eTag1 = app.zGetActiveAccount().soapSelectNodes("//mail:tag[@name='"+ name1 +"']");
		ZAssert.assertEquals(eTag1.length, 0, "Verify the old tag name no longer exists");
		
		Element[] eTag2 = app.zGetActiveAccount().soapSelectNodes("//mail:tag[@name='"+ name2 +"']");
		ZAssert.assertEquals(eTag2.length, 1, "Verify the new tag name exists");

		
	}

	


}
