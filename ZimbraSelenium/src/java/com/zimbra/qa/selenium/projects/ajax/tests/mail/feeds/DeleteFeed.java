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

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;

public class DeleteFeed extends PrefGroupMailByMessageTest {

	public DeleteFeed() {
		logger.info("New "+ DeleteFeed.class.getCanonicalName());





	}

	@Test(	description = "Delete a feed folder - Right click, Delete",
			groups = { "smoke" })
	public void DeleteFeed_01() throws HarnessException, MalformedURLException {

		FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash);
		FolderItem root = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.UserRoot);

		String feedname = "feed" + ZimbraSeleniumProperties.getUniqueString();
		URL feedurl = new URL("http", "rss.news.yahoo.com", 80, "/rss/topstories");

		app.zGetActiveAccount().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+		"<folder name='"+ feedname +"' l='"+ root.getId() +"' url='"+ feedurl.toString() +"'/>"
				+	"</CreateFolderRequest>");

		FolderItem feed = FolderItem.importFromSOAP(app.zGetActiveAccount(), feedname);
		ZAssert.assertNotNull(feed, "Verify the subfolder is available");


		// Click on Get Mail to refresh the folder list
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Delete the folder using context menu
		app.zTreeMail.zTreeItem(Action.A_RIGHTCLICK, Button.B_DELETE, feed);

		// Verify the folder is now in the trash
		feed = FolderItem.importFromSOAP(app.zGetActiveAccount(), feedname);
		ZAssert.assertNotNull(feed, "Verify the subfolder is again available");
		ZAssert.assertEquals(trash.getId(), feed.getParentId(), "Verify the subfolder's parent is now the trash folder ID");

	}

}
