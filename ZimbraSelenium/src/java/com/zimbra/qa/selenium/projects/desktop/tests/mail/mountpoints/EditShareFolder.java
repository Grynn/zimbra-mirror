package com.zimbra.qa.selenium.projects.desktop.tests.mail.mountpoints;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount.SOAP_DESTINATION_HOST_TYPE;
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.desktop.ui.DialogShare;
import com.zimbra.qa.selenium.projects.desktop.ui.DialogShare.ShareRole;
import com.zimbra.qa.selenium.projects.desktop.ui.mail.DialogEditFolder;

public class EditShareFolder extends AjaxCommonTest{
	public EditShareFolder() {
		logger.info("New "+ ShareFolder.class.getCanonicalName());

		// All tests start at the login page
		super.startingPage = app.zPageMail;
		super.startingAccountPreferences = null;

	}


	@Test(	description = "Share and edit folder with admin rights",
			groups = { "smoke" })
			public void EditShareFolder_01() throws HarnessException {

		FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Inbox);
		String foldername = "folder" + ZimbraSeleniumProperties.getUniqueString();


		// Create a subfolder in Inbox
		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+		"<folder name='" + foldername +"' l='" + inbox.getId() +"'/>"
				+	"</CreateFolderRequest>");

		//Need to do Refresh by clicking on getmail button to see folder in the list 
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Make sure the folder was created on the server
		FolderItem subfolder = FolderItem.importFromSOAP(
		      app.zGetActiveAccount(),
		      foldername,
		      SOAP_DESTINATION_HOST_TYPE.CLIENT,
		      app.zGetActiveAccount().EmailAddress);
		ZAssert.assertNotNull(subfolder, "Verify the folder exists on the server");


		// Right click on folder, select "Share"
		DialogShare sharedialog = (DialogShare)app.zTreeMail.zTreeItem(Action.A_RIGHTCLICK, Button.B_SHARE, subfolder);
		ZAssert.assertNotNull(sharedialog, "Verify the sharing dialog pops up");

		// Use defaults for all options
		sharedialog.zSetEmailAddress(ZimbraAccount.AccountA().EmailAddress);

		// Send it
		sharedialog.zClickButton(Button.B_OK);

		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
		app.zPageMain.zWaitForDesktopLoadingSpinner(5000);

		// Make sure that AccountA now has the share
		ZimbraAccount.AccountA().soapSend(
				"<GetShareInfoRequest xmlns='urn:zimbraAccount'>"
				+		"<grantee type='usr'/>"
				+		"<owner by='name'>"+ app.zGetActiveAccount().EmailAddress +"</owner>"
				+	"</GetShareInfoRequest>");

		String ownerEmail = ZimbraAccount.AccountA().soapSelectValue("//acct:GetShareInfoResponse//acct:share[@folderPath='/Inbox/"+ foldername +"']", "ownerEmail");
		ZAssert.assertEquals(ownerEmail, app.zGetActiveAccount().EmailAddress, "Verify the owner of the shared folder");

		String rights = ZimbraAccount.AccountA().soapSelectValue("//acct:GetShareInfoResponse//acct:share[@folderPath='/Inbox/"+ foldername +"']", "rights");
		ZAssert.assertEquals(rights, "r", "Verify the rights are 'read only'");

		String granteeType = ZimbraAccount.AccountA().soapSelectValue("//acct:GetShareInfoResponse//acct:share[@folderPath='/Inbox/"+ foldername +"']", "granteeType");
		ZAssert.assertEquals(granteeType, "usr", "Verify the grantee type is 'user'");


		//Edit

		//Right click folder, click Edit Properties
		DialogEditFolder editdialog = (DialogEditFolder)app.zTreeMail.zTreeItem(Action.A_RIGHTCLICK, Button.B_TREE_EDIT, subfolder);
		ZAssert.assertNotNull(editdialog, "Verify the sharing dialog pops up");

		//Click Edit link on Edit properties dialog
		editdialog.zClickButton(Button.O_EDIT_LINK);

		ZAssert.assertTrue(sharedialog.zIsActive(), "Verify that the Share dialog is active ");

		//Select Admin radio button
		sharedialog.zSetRole(ShareRole.Admin);

		//click ok
		sharedialog.zClickButton(Button.B_OK);

		//Verify Edit properties  dialog is active
		ZAssert.assertTrue(editdialog.zIsActive(), "Verify that the Edit Folder Properties dialog is active ");

		//click ok button from edit Folder properties dialog
		editdialog.zClickButton(Button.B_OK);

		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
      app.zPageMain.zWaitForDesktopLoadingSpinner(5000);

      ZimbraAccount.AccountA().soapSend(
				"<GetShareInfoRequest xmlns='urn:zimbraAccount'>"
				+		"<grantee type='usr'/>"
				+		"<owner by='name'>"+ app.zGetActiveAccount().EmailAddress +"</owner>"
				+	"</GetShareInfoRequest>");

		String adminrights = ZimbraAccount.AccountA().soapSelectValue("//acct:GetShareInfoResponse//acct:share[@folderPath='/Inbox/"+ foldername +"']", "rights");

		//verify admin rights 	
		ZAssert.assertEquals(adminrights, "rwidxa", "Verify the rights are admin");

	}

}
