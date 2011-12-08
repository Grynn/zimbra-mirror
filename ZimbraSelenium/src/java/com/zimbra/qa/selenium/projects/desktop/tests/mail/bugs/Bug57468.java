package com.zimbra.qa.selenium.projects.desktop.tests.mail.bugs;

import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.desktop.tests.mail.mail.GetMail;

public class Bug57468 extends AjaxCommonTest {

	/**
	 * @throws HarnessException
	 */
	@AfterClass( groups = { "always" } )
	public void bug57468AfterClass() throws HarnessException {
		logger.info("bug57468AfterClass: start");
		
		// Since we collapsed the folder tree, it may cause problems for other tests
		// Rest the ZWC user
		ZimbraAccount.ResetAccountZDC();
		
		logger.info("bug57468AfterClass: finish");
	}

	public Bug57468() {
		logger.info("New "+ GetMail.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageMail;

		// Make sure we are using an account with message view
		super.startingAccountPreferences = null;

	}

	@Bugs( ids = "57468")
	@Test(	description = "Verify collapsed folders remain collapsed when getting mail",
			groups = { "functional" })
	public void Bug57468_01() throws HarnessException {

		// Create a subfolder in Inbox
		FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox);
		ZAssert.assertNotNull(inbox, "Verify the inbox is available");

		String foldername = "folder" + ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
                	"<folder name='"+ foldername +"' l='"+ inbox.getId() +"'/>" +
                "</CreateFolderRequest>");

		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
		app.zPageMain.zWaitForDesktopLoadingSpinner(5000);

		// Collapse all the mail folder trees
		app.zTreeMail.zCollapseAll();

		// Send a message to the test account
		ZimbraAccount.AccountA().soapSend(
					"<SendMsgRequest xmlns='urn:zimbraMail'>" +
						"<m>" +
							"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
							"<su>"+ "subject" + ZimbraSeleniumProperties.getUniqueString() +"</su>" +
							"<mp ct='text/plain'>" +
								"<content>content" + ZimbraSeleniumProperties.getUniqueString() +"</content>" +
							"</mp>" +
						"</m>" +
					"</SendMsgRequest>");

		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Verify the inbox remains collapsed
		List<FolderItem> folders = app.zTreeMail.zListGetFolders();
		FolderItem found = null;

		for (FolderItem f : folders) {
		   // FolderItem id is in this format: b79df08f-e04e-4645-957b-4d773562f509:2
		   // Thus, need to split the String with ";" and pick the second item to compare
		   // with the folder Item ID obtained from SOAP
		   if ( f.getId().split(":")[1].equals(inbox.getId()) ) {
				found = f;
				break;
			}
		}
		ZAssert.assertNotNull(found, "Verify the inbox is in the folder tree");
		
		ZAssert.assertTrue(app.zTreeMail.isCollapsed(),
		      "Verify that the mail folder tree is collapsed");
		
		
	}

}
