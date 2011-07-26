package com.zimbra.qa.selenium.projects.ajax.tests.mail.folders.showremaining;

import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;


public class ShowRemaining extends AjaxCommonTest {

	public ShowRemaining() {
		logger.info("New "+ ShowRemaining.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageMail;
		super.startingAccountPreferences = null;
		
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
		ZAssert.assertNotNull(found, "Verify the folder was in the tree");

		
	}	


}
