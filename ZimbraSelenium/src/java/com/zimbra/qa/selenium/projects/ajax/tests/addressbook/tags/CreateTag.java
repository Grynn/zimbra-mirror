package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.tags;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties.AppType;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
//import com.zimbra.qa.selenium.projects.ajax.ui.mail.*;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogTag;

public class CreateTag extends AjaxCommonTest {

	public CreateTag() {
		logger.info("New "+ CreateTag.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageAddressbook;
		super.startingAccountPreferences = null;
		
	}
	
	@Test(	description = "Create a new tag by clicking 'new tag' on folder tree",
			groups = { "sanity" })
	public void CreateTagByClickTagsOnFolderTree() throws HarnessException {
				
		// Set the new tag name
		String name = "tag" + ZimbraSeleniumProperties.getUniqueString();
		
		DialogTag dialog = null;
		dialog = (DialogTag)app.zTreeContacts.zPressButton(Button.B_TREE_NEWTAG);
	
		ZAssert.assertNotNull(dialog, "Verify the new dialog opened");
		
		// Fill out the form with the basic details
		dialog.zSubmit(name);

		// Make sure the tag was created on the server
		TagItem tag = app.zPageAddressbook.zGetTagItem(app.zGetActiveAccount(), name);
		ZAssert.assertNotNull(tag, "Verify the new tag was created");
		
		ZAssert.assertEquals(tag.getName(), name, "Verify the server and client tag names match");
		
	}

	
	
	@Test(	description = "Create a new tag using keyboard shortcuts nt",
			groups = { "functional" })
	public void CreateTagByClickShortcutnt() throws HarnessException {
								
		// Set the new tag name
		String name = "tag" + ZimbraSeleniumProperties.getUniqueString();
		
		DialogTag dialog = (DialogTag)app.zPageAddressbook.zKeyboardShortcut(Shortcut.S_NEWTAG);
		ZAssert.assertNotNull(dialog, "Verify the new dialog opened");
		
		// Fill out the form with the basic details
		dialog.zSubmit(name);

		// Make sure the tag was created on the server
		TagItem tag = app.zPageAddressbook.zGetTagItem(app.zGetActiveAccount(), name);
		ZAssert.assertNotNull(tag, "Verify the new tag was created");
		
		ZAssert.assertEquals(tag.getName(), name, "Verify the server and client tag names match");
				
	}

	@Test(	description = "Create a new tag using context menu from a tag",
			groups = { "functional" })
	public void CreateTagByClickNewTagOnTagsContextmenu() throws HarnessException {
		
		// Set the new tag name
		String name1 = "tag" + ZimbraSeleniumProperties.getUniqueString();
		String name2 = "tag" + ZimbraSeleniumProperties.getUniqueString();
		
		// Create a tag to right click on
		app.zGetActiveAccount().soapSend(
				"<CreateTagRequest xmlns='urn:zimbraMail'>" +
            		"<tag name='"+ name2 +"' color='1' />" +
            	"</CreateTagRequest>");

		// Get the tag
		TagItem tag2 = app.zPageAddressbook.zGetTagItem(app.zGetActiveAccount(), name2);

		
		 // Refresh the view, to pick up the new tag
	    FolderItem contactFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), "Contacts");	      
	    app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, contactFolder);
	    		
		// Create a new tag using the context menu + New Tag
		DialogTag dialog = (DialogTag)app.zTreeContacts.zTreeItem(Action.A_RIGHTCLICK, Button.B_TREE_NEWTAG, tag2);
		ZAssert.assertNotNull(dialog, "Verify the new dialog opened");
		
		// Fill out the form with the basic details
		dialog.zSubmit(name1);

		// Make sure the tag was created on the server
		TagItem tag1 = app.zPageAddressbook.zGetTagItem(app.zGetActiveAccount(), name1);

		ZAssert.assertNotNull(tag1, "Verify the new tag was created");
		
		ZAssert.assertEquals(tag1.getName(), name1, "Verify the server and client tag names match");
		
	}

	@Test(	description = "Create a new tag using addressbook app New -> New Tag",
			groups = { "functional" })
	public void CreateTagByClickNew_NewTag() throws HarnessException {
				
		// Set the new folder name
		String name = "tag" + ZimbraSeleniumProperties.getUniqueString();
				
		DialogTag dialog = (DialogTag)app.zPageAddressbook.zToolbarPressPulldown(Button.B_NEW, Button.O_NEW_TAG);
		ZAssert.assertNotNull(dialog, "Verify the new dialog opened");
		
		// TODO: does a folder in the tree need to be selected?
		dialog.zSubmit(name);
		
		// Make sure the folder was created on the server
		TagItem tag = app.zPageAddressbook.zGetTagItem(app.zGetActiveAccount(), name);
		ZAssert.assertNotNull(tag, "Verify the new tag was created");
		
		ZAssert.assertEquals(tag.getName(), name, "Verify the server and client tag names match");
		
	}


}
