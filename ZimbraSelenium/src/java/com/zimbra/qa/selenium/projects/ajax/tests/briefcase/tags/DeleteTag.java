package com.zimbra.qa.selenium.projects.ajax.tests.briefcase.tags;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.TagItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogWarning;

public class DeleteTag extends AjaxCommonTest {

	public DeleteTag() {
		logger.info("New " + DeleteTag.class.getCanonicalName());

		// All tests start at the Briefcase page
		super.startingPage = app.zPageBriefcase;
		super.startingAccountPreferences = null;

	}

	@Test(description = "Delete a tag - Right click, Delete", groups = { "functional" })
	public void DeleteTag_01() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		// Create the tag to delete
		String name = "tag" + ZimbraSeleniumProperties.getUniqueString();

		account.soapSend("<CreateTagRequest xmlns='urn:zimbraMail'>"
				+ "<tag name='" + name + "' color='1' />"
				+ "</CreateTagRequest>");

		// Get the tag
		TagItem tag = TagItem.importFromSOAP(app.zGetActiveAccount(), name);
		ZAssert.assertNotNull(tag, "Verify the tag was created");

		// refresh briefcase page
		app.zTreeBriefcase
				.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, false);

		// Delete the tag using context menu
		DialogWarning dialog = (DialogWarning) app.zTreeBriefcase.zTreeItem(
				Action.A_RIGHTCLICK, Button.B_TREE_DELETE, tag);
		ZAssert.assertNotNull(dialog, "Verify the warning dialog opened");

		// Click "Yes" to confirm
		dialog.zClickButton(Button.B_YES);

		// refresh briefcase page
		app.zTreeBriefcase
				.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, false);

		// Verify the tag is deleted
		account.soapSend("<GetTagRequest xmlns='urn:zimbraMail'/>");

		String tagname = account
				.soapSelectValue("//mail:GetTagResponse//mail:tag[@name='"
						+ name + "']", "name");
		ZAssert.assertNull(tagname, "Verify the tag is deleted");
	}
}
