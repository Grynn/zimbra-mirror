/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.projects.ajax.tests.mail.feeds;

import java.net.*;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.*;
import com.zimbra.qa.selenium.projects.ajax.ui.*;


public class EmptyFeed extends PrefGroupMailByMessageTest {

	public EmptyFeed() {
		logger.info("New "+ EmptyFeed.class.getCanonicalName());

		

	}

	@Test(description = "Empty a feed folder (context menu)", groups = { "smoke" })
	public void EmptyFeed_01() throws HarnessException, MalformedURLException {

		FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(),
				FolderItem.SystemFolder.Inbox);

		// Create a subfolder in Inbox
		String feedname = "feed" + ZimbraSeleniumProperties.getUniqueString();
		// feed.rss=http://server/files/Service/RSS/Basic/basic.xml
		URL feedurl = new URL(ZimbraSeleniumProperties.getStringProperty("feed.rss"));

		app.zGetActiveAccount().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+		"<folder name='"+ feedname +"' l='"+ inbox.getId() +"' url='"+ feedurl.toString() +"'/>"
				+	"</CreateFolderRequest>");

		FolderItem feed = FolderItem.importFromSOAP(app.zGetActiveAccount(), feedname);
		ZAssert.assertNotNull(feed, "Verify the subfolder is available");

		
		// Click on Get Mail to refresh the folder list
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
		
		
		// Right click on folder, select "Mark all as read"
		DialogWarning dialog = (DialogWarning) app.zTreeMail.zTreeItem(Action.A_RIGHTCLICK, Button.B_TREE_FOLDER_EMPTY, feed);
		ZAssert.assertNotNull(dialog,"Verify the warning dialog pops up - Are you sure you want to delete all items?");

		// Dismiss it
		dialog.zClickButton(Button.B_OK);


	}
}
