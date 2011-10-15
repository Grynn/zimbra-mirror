package com.zimbra.qa.selenium.projects.ajax.tests.preferences.sharing;

import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.PagePreferences.ShareItem;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.TreePreferences.TreeItem;


public class ShowMyShares extends AjaxCommonTest {

	
	
	public ShowMyShares() {
		
		super.startingPage = app.zPagePreferences;
		super.startingAccountPreferences = null;
		
		
	}


	@Test(
			description = "View the sharing preference page",
			groups = { "functional" }
			)
	public void ShowMyShares_01() throws HarnessException {

		//*** Test Data
		ZimbraAccount delegate = new ZimbraAccount();
		delegate.provision();
		delegate.authenticate();

		
		String foldername = "folder"+ ZimbraSeleniumProperties.getUniqueString();
		
		FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Inbox);
		ZAssert.assertNotNull(inbox, "Verify the new owner folder exists");

		app.zGetActiveAccount().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+		"<folder name='" + foldername +"' l='" + inbox.getId() +"'/>"
				+	"</CreateFolderRequest>");
		
		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(), foldername);
		ZAssert.assertNotNull(folder, "Verify the new owner folder exists");
		
		app.zGetActiveAccount().soapSend(
					"<FolderActionRequest xmlns='urn:zimbraMail'>"
				+		"<action id='"+ folder.getId() +"' op='grant'>"
				+			"<grant d='" + delegate.EmailAddress + "' gt='usr' perm='r'/>"
				+		"</action>"
				+	"</FolderActionRequest>");
		

		
		
		//*** Test Steps
		
		// Refresh
		app.zPageMain.zToolbarPressButton(Button.B_REFRESH);
		
		// Navigate to preferences -> notifications
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.Sharing);


		
		
		//*** Test Verification
		ShareItem found = null;
		List<ShareItem> items = app.zPagePreferences.zSharesGetSharedByMe();
		for (ShareItem item : items) {
			if (delegate.EmailAddress.contains(item.with)) {
				found = item;
				break;
			}
		}
		

		ZAssert.assertNotNull(found, "verify the shared item appears in the list");
		ZAssert.assertStringContains(found.item, foldername, "Verify the owner foldername");

		
	}
}
