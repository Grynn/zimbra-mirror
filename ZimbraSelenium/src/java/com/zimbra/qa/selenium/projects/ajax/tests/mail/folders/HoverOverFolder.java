package com.zimbra.qa.selenium.projects.ajax.tests.mail.folders;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.TooltipFolder;


public class HoverOverFolder extends PrefGroupMailByMessageTest {

	public HoverOverFolder() {
		logger.info("New "+ HoverOverFolder.class.getCanonicalName());
		
	}
	
	@Test(	description = "Hover over a folder to show the tooltip",
			groups = { "functional" })
	public void TooltipFolder_01() throws HarnessException {
		
		//-- Data
		
		// Create a subfolder with a message in it		
		String foldername = "folder" + ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
                	"<folder name='"+ foldername +"' l='"+ FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox).getId() +"'/>" +
                "</CreateFolderRequest>");
		FolderItem subfolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), foldername);

		// Add a message
		app.zGetActiveAccount().soapSend(
				"<AddMsgRequest xmlns='urn:zimbraMail'>"
    		+		"<m l='"+ subfolder.getId() +"' f='u'>"
        	+			"<content>From: foo@foo.com\n"
        	+				"To: foo@foo.com \n"
        	+				"Subject: subject"+ ZimbraSeleniumProperties.getUniqueString() +"\n"
        	+				"MIME-Version: 1.0 \n"
        	+				"Content-Type: text/plain; charset=utf-8 \n"
        	+				"Content-Transfer-Encoding: 7bit\n"
        	+				"\n"
        	+				"simple text string in the body\n"
        	+			"</content>"
        	+		"</m>"
			+	"</AddMsgRequest>");
		
		
		//-- GUI
		
		// Click on Get Mail to refresh the folder list
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Hover over the folder
		TooltipFolder tooltip = (TooltipFolder)app.zTreeMail.zTreeItem(Action.A_HOVEROVER, subfolder);
		
		
		//-- Verification
		
		ZAssert.assertTrue(tooltip.zIsActive(), "Verify the tooltip shows");

	}	

	@Test(	description = "Hover over a folder - Verify contents",
			groups = { "functional", "matt" })
	public void TooltipFolder_02() throws HarnessException {
		
		//-- Data
		
		// Create a subfolder with a message in it		
		String foldername = "folder" + ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
                	"<folder name='"+ foldername +"' l='"+ FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox).getId() +"'/>" +
                "</CreateFolderRequest>");
		FolderItem subfolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), foldername);

		// Add a message
		app.zGetActiveAccount().soapSend(
				"<AddMsgRequest xmlns='urn:zimbraMail'>"
    		+		"<m l='"+ subfolder.getId() +"' f='u'>"
        	+			"<content>From: foo@foo.com\n"
        	+				"To: foo@foo.com \n"
        	+				"Subject: subject"+ ZimbraSeleniumProperties.getUniqueString() +"\n"
        	+				"MIME-Version: 1.0 \n"
        	+				"Content-Type: text/plain; charset=utf-8 \n"
        	+				"Content-Transfer-Encoding: 7bit\n"
        	+				"\n"
        	+				"simple text string in the body\n"
        	+			"</content>"
        	+		"</m>"
			+	"</AddMsgRequest>");
		
		
		//-- GUI
		
		// Click on Get Mail to refresh the folder list
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Hover over the folder
		TooltipFolder tooltip = (TooltipFolder)app.zTreeMail.zTreeItem(Action.A_HOVEROVER, subfolder);
		
		
		//-- Verification
		
		// Verify the tooltip appears
		ZAssert.assertTrue(tooltip.zIsActive(), "Verify the tooltip shows");

		String actual;
		
		// Verify the folder name appears
		actual = tooltip.zGetField(TooltipFolder.Field.Foldername);
		ZAssert.assertEquals(actual, foldername, "Verify the correct foldername is shown");

		// Verify 1 total message
		actual = tooltip.zGetField(TooltipFolder.Field.TotalMessages);
		ZAssert.assertEquals(actual, "1", "Verify the correct foldername is shown");

		// Verify 1 unread message
		actual = tooltip.zGetField(TooltipFolder.Field.UnreadMessages);
		ZAssert.assertEquals(actual, "1", "Verify the correct foldername is shown");

		// Verify 198 total message
		actual = tooltip.zGetField(TooltipFolder.Field.Size);
		ZAssert.assertStringContains(actual, "198", "Verify size contains the message size");

	}	

	@Test(	description = "Hover over an empty folder",
			groups = { "functional" })
	public void TooltipFolder_03() throws HarnessException {
		
		//-- Data
		
		// Create a subfolder with a message in it		
		String foldername = "folder" + ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
                	"<folder name='"+ foldername +"' l='"+ FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox).getId() +"'/>" +
                "</CreateFolderRequest>");
		FolderItem subfolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), foldername);

	
		
		//-- GUI
		
		// Click on Get Mail to refresh the folder list
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Hover over the folder
		TooltipFolder tooltip = (TooltipFolder)app.zTreeMail.zTreeItem(Action.A_HOVEROVER, subfolder);
		
		
		//-- Verification
		
		ZAssert.assertTrue(tooltip.zIsActive(), "Verify the tooltip shows");

	}	

	@DataProvider(name = "DataProviderSystemFolders")
	public Object[][] DataProviderSystemFolders() {
	  return new Object[][] {
			    new Object[] { "Inbox", SystemFolder.Inbox },
			    new Object[] { "Drafts", SystemFolder.Drafts },
			    new Object[] { "Sent", SystemFolder.Sent },
			    new Object[] { "Trash", SystemFolder.Trash },
			    new Object[] { "Junk", SystemFolder.Junk },
	  };
	}
	
	@Test(	description = "Hover over the system folders",
			groups = { "functional" },
			dataProvider = "DataProviderSystemFolders")
	public void TooltipFolder_10(String foldername, SystemFolder foldertype) throws HarnessException {
		
		//-- Data
		
		FolderItem subfolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), foldertype);

		if ( foldertype == SystemFolder.Drafts ) {
			
			app.zGetActiveAccount().soapSend(
					"<SaveDraftRequest xmlns='urn:zimbraMail'>"
	    		+		"<m >"
	        	+			"<e t='t' a='"+ ZimbraAccount.AccountA().EmailAddress +"'/>"
	        	+			"<su>subject"+ ZimbraSeleniumProperties.getUniqueString() +"</su>"
				+			"<mp ct='text/plain'>"
				+				"<content>content</content>"
				+			"</mp>"
	        	+		"</m>"
				+	"</SaveDraftRequest>");

		} else {
			
			// Add a message
			app.zGetActiveAccount().soapSend(
					"<AddMsgRequest xmlns='urn:zimbraMail'>"
	    		+		"<m l='"+ subfolder.getId() +"' f='u'>"
	        	+			"<content>From: foo@foo.com\n"
	        	+				"To: foo@foo.com \n"
	        	+				"Subject: subject"+ ZimbraSeleniumProperties.getUniqueString() +"\n"
	        	+				"MIME-Version: 1.0 \n"
	        	+				"Content-Type: text/plain; charset=utf-8 \n"
	        	+				"Content-Transfer-Encoding: 7bit\n"
	        	+				"\n"
	        	+				"simple text string in the body\n"
	        	+			"</content>"
	        	+		"</m>"
				+	"</AddMsgRequest>");
		
		}
		
		
		//-- GUI
		
		// Click on Get Mail to refresh the folder list
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Hover over the folder
		TooltipFolder tooltip = (TooltipFolder)app.zTreeMail.zTreeItem(Action.A_HOVEROVER, subfolder);
		
		
		//-- Verification
		
		ZAssert.assertTrue(tooltip.zIsActive(), "Verify the tooltip shows");

	}	


}
