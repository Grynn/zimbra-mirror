package com.zimbra.qa.selenium.projects.ajax.tests.mail.folders.showremaining;

import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;


public class ShowRemaining extends PrefGroupMailByMessageTest {

	public ShowRemaining() {
		logger.info("New "+ ShowRemaining.class.getCanonicalName());
		
		
		
		
	}
	
	@Test(	description = "Click on 'show remaining folders'",
			groups = { "functional" })
	public void ShowRemaining_01() throws HarnessException {
		
		String name = "";
		
		// Create 500 subfolders

		for ( int i = 0; i < 125; i++ ) {
			name = "folder" + ZimbraSeleniumProperties.getUniqueString();
			
			app.zGetActiveAccount().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
	                	"<folder name='"+ name +"' l='1'/>" +
	                "</CreateFolderRequest>");
		}

		// Need to logout/login for changes to take effect
		ZimbraAccount active = app.zGetActiveAccount();
		app.zPageMain.zLogout();
		app.zPageLogin.zLogin(active);
		if ( !startingPage.zIsActive() ) {
			startingPage.zNavigateTo();
		}
		if ( !startingPage.zIsActive() ) {
			throw new HarnessException("Unable to navigate to "+ startingPage.myPageName());
		}
		
		
		// Click on Get Mail to refresh the folder list
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Click on the "Show Remaining"
		app.zTreeMail.zPressButton(Button.B_TREE_SHOW_REMAINING_FOLDERS);
		
		// Wait again
		SleepUtil.sleep(10000);

		// Verify the last folder now appears in the list
		List<FolderItem> folders = app.zTreeMail.zListGetFolders();
		
		FolderItem found = null;
		for (FolderItem f : folders) {
			if ( name.equals(f.getName()) ) {
				found = f;
				break;
			}
		}
		ZAssert.assertNotNull(found, "Verify the folder "+ name + " was in the tree");

		
	}	


}
