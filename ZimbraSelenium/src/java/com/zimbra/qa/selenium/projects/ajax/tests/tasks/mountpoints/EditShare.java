package com.zimbra.qa.selenium.projects.ajax.tests.tasks.mountpoints;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogShare;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogShare.ShareRole;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DialogEditFolder;

public class EditShare extends AjaxCommonTest {
	
	@SuppressWarnings("serial")
	public EditShare() {
		logger.info("New "+ EditShare.class.getCanonicalName());
		logger.info("New " + CreateShare.class.getCanonicalName());

		super.startingPage = app.zPageTasks;
		super.startingAccountPreferences = new HashMap<String, String>() {
			{
				put("zimbraPrefTasksReadingPaneLocation", "bottom");
			}
		};

	}


	@Test(	description = "Share and edit folder with admin rights",
			groups = { "smoke" })
	public void EditShare_01() throws HarnessException {

		FolderItem taskFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Tasks);

		// Create the subTaskList
		String name = "taskList" + ZimbraSeleniumProperties.getUniqueString();

		app.zGetActiveAccount()
		.soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+ "<folder name='" + name + "' l='"
				+ taskFolder.getId() + "'/>"
				+ "</CreateFolderRequest>");

		FolderItem subTaskList = FolderItem.importFromSOAP(app.zGetActiveAccount(), name);
		ZAssert.assertNotNull(subTaskList, "Verify the subfolder is available");

		// Refresh the tasks view
		app.zTreeTasks.zTreeItem(Action.A_LEFTCLICK, taskFolder);

		// Right click on folder, select "Share"
		DialogShare dialog = (DialogShare) app.zTreeTasks.zTreeItem(Action.A_RIGHTCLICK, Button.B_SHARE, subTaskList);
		ZAssert.assertNotNull(dialog, "Verify the sharing dialog pops up");

		// Use defaults for all options
		dialog.zSetEmailAddress(ZimbraAccount.AccountA().EmailAddress);

		// Send it
		dialog.zClickButton(Button.B_OK);

		// Make sure that AccountA now has the share
		ZimbraAccount.AccountA().soapSend(
				"<GetShareInfoRequest xmlns='urn:zimbraAccount'>"
				+ "<grantee type='usr'/>" + "<owner by='name'>"
				+ app.zGetActiveAccount().EmailAddress + "</owner>"
				+ "</GetShareInfoRequest>");

		String ownerEmail = ZimbraAccount.AccountA().soapSelectValue("//acct:GetShareInfoResponse//acct:share[@folderPath='/Tasks/"	+ name + "']", "ownerEmail");
		ZAssert.assertEquals(ownerEmail, app.zGetActiveAccount().EmailAddress,
				"Verify the owner of the shared folder");

		String rights = ZimbraAccount.AccountA().soapSelectValue("//acct:GetShareInfoResponse//acct:share[@folderPath='/Tasks/"	+ name + "']", "rights");
		ZAssert.assertEquals(rights, "r", "Verify the rights are 'read only'");

		String granteeType = ZimbraAccount.AccountA().soapSelectValue("//acct:GetShareInfoResponse//acct:share[@folderPath='/Tasks/"+ name + "']", "granteeType");
		ZAssert.assertEquals(granteeType, "usr","Verify the grantee type is 'user'");		

		//Need to do Refresh by clicking on getmail button to see folder in the list 
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		//Edit
		//Right click folder, click Edit Properties
		DialogEditFolder editdialog = (DialogEditFolder)app.zTreeTasks.zTreeItem(Action.A_RIGHTCLICK, Button.B_TREE_EDIT, subTaskList);
		ZAssert.assertNotNull(editdialog, "Verify the sharing dialog pops up");

		//Click Edit link on Edit properties dialog
		DialogShare sharedialog = (DialogShare)editdialog.zClickButton(Button.O_EDIT_LINK);
		ZAssert.assertTrue(sharedialog.zIsActive(), "Verify that the Share dialog is active ");

		//Select Admin radio button
		sharedialog.zSetRole(ShareRole.Admin);

		//click ok
		sharedialog.zClickButton(Button.B_OK);

		//Verify Edit properties  dialog is active
		ZAssert.assertTrue(editdialog.zIsActive(), "Verify that the Edit Folder Properties dialog is active ");

		//click ok button from edit Folder properties dialog
		editdialog.zClickButton(Button.B_OK);		
		
		ZimbraAccount.AccountA().soapSend(
				"<GetShareInfoRequest xmlns='urn:zimbraAccount'>"
				+		"<grantee type='usr'/>"
				+		"<owner by='name'>"+ app.zGetActiveAccount().EmailAddress +"</owner>"
				+	"</GetShareInfoRequest>");

		String adminrights = ZimbraAccount.AccountA().soapSelectValue("//acct:GetShareInfoResponse//acct:share[@folderPath='/Tasks/"+ name +"']", "rights");

		//verify admin rights 	
		ZAssert.assertEquals(adminrights, "rwidxa", "Verify the rights are admin");

	}

}
