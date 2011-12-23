package com.zimbra.qa.selenium.projects.ajax.tests.mail.feeds;

import java.net.*;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;

public class DragAndDropFeed extends PrefGroupMailByMessageTest{

	public DragAndDropFeed(){
		logger.info("New "+ DragAndDropFeed.class.getCanonicalName());

		
		
		

	}

	@Test(	description = "Drag one folder and Drop into other",
			groups = { "smoke" })
	public void DragAndDropFeed_01() throws HarnessException, MalformedURLException {

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

		// Create two subfolders in the inbox
		// One folder to Drag
		// Another folder to drop into
		String name1 = "folder" + ZimbraSeleniumProperties.getUniqueString();

		app.zGetActiveAccount().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+		"<folder name='"+ name1 +"' l='"+ inbox.getId() +"'/>"
				+	"</CreateFolderRequest>");


		FolderItem subfolder1 = FolderItem.importFromSOAP(app.zGetActiveAccount(), name1);
		ZAssert.assertNotNull(subfolder1, "Verify the first subfolder is available");



		// Click on Get Mail to refresh the folder list
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Bug 65234
		// Sometimes the folder tree is rendered slowly.  sleep a bit
		SleepUtil.sleepVerySmall();
		
		app.zPageMail.zDragAndDrop(
				"css=td[id='zti__main_Mail__" + feed.getId() + "_textCell']",
				"css=td[id='zti__main_Mail__" + subfolder1.getId() + "_textCell']");
				


		// Verify the folder is now in the other subfolder
		feed = FolderItem.importFromSOAP(app.zGetActiveAccount(), feedname);
		ZAssert.assertNotNull(feed, "Verify the subfolder is again available");
		ZAssert.assertEquals(subfolder1.getId(), feed.getParentId(), "Verify the subfolder's parent is now the other subfolder");


	}

}
