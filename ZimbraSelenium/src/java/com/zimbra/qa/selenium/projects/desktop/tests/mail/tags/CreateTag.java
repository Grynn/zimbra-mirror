package com.zimbra.qa.selenium.projects.desktop.tests.mail.tags;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
//import com.zimbra.qa.selenium.projects.ajax.ui.mail.*;
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.desktop.ui.DialogTag;

public class CreateTag extends AjaxCommonTest {

	public CreateTag() {
		logger.info("New "+ CreateTag.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageMail;
		super.startingAccountPreferences = null;
		
	}

	@Test(	description = "Create a new tag using keyboard shortcuts",
			groups = { "functional" })
	public void CreateTag_02() throws HarnessException {
		
		Shortcut shortcut = Shortcut.S_NEWTAG;

		// Set the new tag name
		String name = "tag" + ZimbraSeleniumProperties.getUniqueString();
		
		DialogTag dialog = (DialogTag)app.zPageMail.zKeyboardShortcut(shortcut);
		ZAssert.assertNotNull(dialog, "Verify the new dialog opened");
		
		// Fill out the form with the basic details
		dialog.zSubmit(name);

		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
		app.zPageMain.zWaitForDesktopLoadingSpinner(5000);

		// Make sure the tag was created on the server
		TagItem tag = app.zPageMail.zGetTagItem(app.zGetActiveAccount(), name);
		ZAssert.assertNotNull(tag, "Verify the new folder was created");
		
		ZAssert.assertEquals(tag.getName(), name, "Verify the server and client tag names match");

	}

	@Test(	description = "Create a new tag using context menu from a tag",
			groups = { "functional" })
	public void CreateTag_03() throws HarnessException {
		
		FolderItem inboxFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox);
		// Set the new tag name
		String name1 = "tag" + ZimbraSeleniumProperties.getUniqueString();
		String name2 = "tag" + ZimbraSeleniumProperties.getUniqueString();
		
		// Create a tag to right click on
		app.zGetActiveAccount().soapSend(
				"<CreateTagRequest xmlns='urn:zimbraMail'>" +
            		"<tag name='"+ name2 +"' color='1' />" +
            	"</CreateTagRequest>");

		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
		app.zPageMain.zWaitForDesktopLoadingSpinner(5000);

		// Get the tag
		TagItem tag2 = app.zPageMail.zGetTagItem(app.zGetActiveAccount(), name2);

		app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, inboxFolder);
		// Create a new tag using the context menu + New Tag
		DialogTag dialog = (DialogTag)app.zTreeMail.zTreeItem(Action.A_RIGHTCLICK, Button.B_TREE_NEWTAG, tag2);
		ZAssert.assertNotNull(dialog, "Verify the new dialog opened");
		
		// Fill out the form with the basic details
		dialog.zSubmit(name1);

		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
		app.zPageMain.zWaitForDesktopLoadingSpinner(5000);

		// Make sure the folder was created on the server
		TagItem tag1 = app.zPageMail.zGetTagItem(app.zGetActiveAccount(), name1);

		ZAssert.assertNotNull(tag1, "Verify the new tag was created");
		
		ZAssert.assertEquals(tag1.getName(), name1, "Verify the server and client tag names match");
		
	}

	@Test(	description = "Create a new tag using mail app New -> New Tag",
			groups = { "sanity" })
	public void CreateTag_04() throws HarnessException {
		
		
		// Set the new folder name
		String name = "tag" + ZimbraSeleniumProperties.getUniqueString();
				
		// Create a new folder in the inbox
		// using the context menu + New Folder
		DialogTag dialog = (DialogTag)app.zPageMail.zToolbarPressPulldown(Button.B_NEW, Button.O_NEW_TAG);
		ZAssert.assertNotNull(dialog, "Verify the new dialog opened");
		
		// Fill out the form with the basic details
		// TODO: does a folder in the tree need to be selected?
		dialog.zSubmit(name);

		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
		app.zPageMain.zWaitForDesktopLoadingSpinner(5000);

		// Make sure the folder was created on the server
		TagItem tag = app.zPageMail.zGetTagItem(app.zGetActiveAccount(), name);
		ZAssert.assertNotNull(tag, "Verify the new tag was created");
		
		ZAssert.assertEquals(tag.getName(), name, "Verify the server and client tag names match");
		
	}


}
