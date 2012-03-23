package com.zimbra.qa.selenium.projects.ajax.tests.mail.feeds;

import java.net.*;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DialogCreateFolder;

public class CreateFeed extends PrefGroupMailByMessageTest {

	public CreateFeed() {
		logger.info("New "+ CreateFeed.class.getCanonicalName());

		
		
	}


	@Test(	description = "Create a new feed by clicking 'new folder' on folder tree",
			groups = { "functional" })
			public void CreateFeed_01() throws HarnessException, MalformedURLException {

		String foldername = "folder" + ZimbraSeleniumProperties.getUniqueString();

		// feed.rss=http://zqa-tms.eng.vmware.com/files/Service/RSS/Basic/basic.xml
		String feed = ZimbraSeleniumProperties.getStringProperty("feed.rss");


		// Click on the "new folder" button
		DialogCreateFolder createFolderDialog = (DialogCreateFolder)app.zTreeMail.zPressButton(Button.B_TREE_NEWFOLDER);

		createFolderDialog.zEnterFolderName(foldername);
		createFolderDialog.zClickSubscribeFeed(true);
		createFolderDialog.zEnterFeedURL(new URL(feed));

		createFolderDialog.zClickButton(Button.B_OK);

		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());

		// Make sure the folder was created on the ZCS server
		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(), foldername);
		ZAssert.assertNotNull(folder, "Verify the feed exists on the server");

		ZAssert.assertEquals(folder.getName(), foldername, "Verify the server and client feed names match");

		// getUrl() doesn't seem to be implemented in Helix
		//	   ZAssert.assertEquals(folder.getUrl(), feed.toString(), "Verify the server and client feed URLs match");

	}


}
