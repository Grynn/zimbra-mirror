/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.projects.ajax.tests.mail.feeds;

import java.net.*;

import org.testng.annotations.*;

import com.zimbra.common.soap.*;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.*;
import com.zimbra.qa.selenium.projects.ajax.ui.*;


public class RenameFeed extends PrefGroupMailByMessageTest {

	public RenameFeed() {
		logger.info("New "+ RenameFeed.class.getCanonicalName());
		
		
		
		
		
	}
	
	@Test(	description = "Rename a feed folder - Context menu -> Rename",
			groups = { "smoke" })
	public void RenameFeed_01() throws HarnessException, MalformedURLException {
		
		FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox);
		ZAssert.assertNotNull(inbox, "Verify the inbox is available");
				
		// Create a subfolder in Inbox
		String feedname = "feed" + ZimbraSeleniumProperties.getUniqueString();
		URL feedurl = new URL("http", "rss.news.yahoo.com", 80, "/rss/topstories");

		app.zGetActiveAccount().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+		"<folder name='"+ feedname +"' l='"+ inbox.getId() +"' url='"+ feedurl.toString() +"'/>"
				+	"</CreateFolderRequest>");

		FolderItem feed = FolderItem.importFromSOAP(app.zGetActiveAccount(), feedname);
		ZAssert.assertNotNull(feed, "Verify the subfolder is available");
		
		
		// Click on Get Mail to refresh the folder list
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Rename the folder using context menu
		DialogRenameFolder dialog = (DialogRenameFolder)app.zTreeMail.zTreeItem(Action.A_RIGHTCLICK, Button.B_RENAME, feed);
		ZAssert.assertNotNull(dialog, "Verify the dialog opened");
		
		// Set the name, click OK
		String name2 = "feed" + ZimbraSeleniumProperties.getUniqueString();
		dialog.zSetNewName(name2);
		dialog.zClickButton(Button.B_OK);

		
		// Get all the folders and verify the new name appears and the old name disappears
		app.zGetActiveAccount().soapSend("<GetFolderRequest xmlns = 'urn:zimbraMail'/>");
		
		Element[] eFolder1 = app.zGetActiveAccount().soapSelectNodes("//mail:folder[@name='"+ feedname +"']");
		ZAssert.assertEquals(eFolder1.length, 0, "Verify the old folder name no longer exists");
		
		Element[] eFolder2 = app.zGetActiveAccount().soapSelectNodes("//mail:folder[@name='"+ name2 +"']");
		ZAssert.assertEquals(eFolder2.length, 1, "Verify the new folder name exists");
		
		
	}

	

}
