package com.zimbra.qa.selenium.projects.ajax.tests.tasks.bugs;

import java.util.HashMap;

import org.testng.annotations.Test;


import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderMountpointItem;
import com.zimbra.qa.selenium.framework.items.TaskItem;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.tasks.PageTasks.Locators;



public class Bug_73226 extends AjaxCommonTest {

	@SuppressWarnings("serial")
	public Bug_73226() {
		logger.info("New "+ Bug_73226.class.getCanonicalName());
		super.startingPage = app.zPageTasks;
		super.startingAccountPreferences = new HashMap<String, String>() {
			{
				put("zimbraPrefReadingPaneLocation", "bottom");
				put("zimbraPrefTasksReadingPaneLocation", "off");
				put("zimbraPrefGroupMailBy", "message");
				put("zimbraPrefShowSelectionCheckbox", "TRUE");
			}
		};			

	}
	/* 
	 * 1.Login to web client
	 * 2.Share task folder with user2.
	 * 3.Login with user2 >> Accept share
	 * 4.Go to Tasks Tab
	 * 5.Click on mount point
	 * 6.List view shows all the task
	 * 7.Go to view drop down and select Reading Pane Off option
	 * 8.select any task and do double click
	 * 9.Task gets open in same tab
	 * 
	 * Expected:Tool bar should show close button.
	 * 
	 */
	@Bugs(	ids = "73226")
	@Test(	description = "Verify Close button on Share Task Window and its functionality",	groups = { "functional" })
	public void VerifyCloseButton_ShareTaskWindow() throws HarnessException {

		String foldername = "tasklist" + ZimbraSeleniumProperties.getUniqueString();
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String mountpointname = "mountpoint" + ZimbraSeleniumProperties.getUniqueString();

		FolderItem task = FolderItem.importFromSOAP(ZimbraAccount.AccountA(), FolderItem.SystemFolder.Tasks );

		// Create a folder to share
		ZimbraAccount.AccountA().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+		"<folder name='" + foldername + "' l='" + task.getId() + "'/>"
				+	"</CreateFolderRequest>");

		FolderItem folder = FolderItem.importFromSOAP(ZimbraAccount.AccountA(), foldername);

		// Share it
		ZimbraAccount.AccountA().soapSend(
				"<FolderActionRequest xmlns='urn:zimbraMail'>"
				+		"<action id='"+ folder.getId() +"' op='grant'>"
				+			"<grant d='"+ app.zGetActiveAccount().EmailAddress +"' gt='usr' perm='r'/>"
				+		"</action>"
				+	"</FolderActionRequest>");

		// Add a task to it
		ZimbraAccount.AccountA().soapSend(
				"<CreateTaskRequest xmlns='urn:zimbraMail'>" +
				"<m l='"+ folder.getId() +"' >" +
				"<inv>" +
				"<comp name='"+ subject +"'>" +
				"<or a='"+ ZimbraAccount.AccountA().EmailAddress +"'/>" +
				"</comp>" +
				"</inv>" +
				"<su>"+ subject +"</su>" +
				"<mp ct='text/plain'>" +
				"<content>content"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
				"</mp>" +
				"</m>" +
		"</CreateTaskRequest>");


		TaskItem task1 = TaskItem.importFromSOAP(ZimbraAccount.AccountA(), subject);
		ZAssert.assertNotNull(task1, "Verify the task added");

		// Mount it
		app.zGetActiveAccount().soapSend(
				"<CreateMountpointRequest xmlns='urn:zimbraMail'>"
				+		"<link l='1' name='"+ mountpointname +"' view='task' rid='"+ folder.getId() +"' zid='"+ ZimbraAccount.AccountA().ZimbraId +"'/>"
				+	"</CreateMountpointRequest>");

		FolderMountpointItem mountpoint = FolderMountpointItem.importFromSOAP(app.zGetActiveAccount(), mountpointname);

		// Refresh the tasks view
		app.zPageTasks.zToolbarPressButton(Button.B_REFRESH);
		app.zTreeTasks.zTreeItem(Action.A_LEFTCLICK, task);

		// Click on the mountpoint
		app.zTreeTasks.zTreeItem(Action.A_LEFTCLICK, mountpoint);

		// Select the item
		app.zPageTasks.zListItem(Action.A_LEFTCLICK, subject);
		app.zPageTasks.zListItem(Action.A_DOUBLECLICK, subject);
		
		SleepUtil.sleepMedium();		
		//Verify Full task view along with Close button
		ZAssert.assertTrue(app.zPageTasks.sGetEval("window.appCtxt.getCurrentViewType()").equalsIgnoreCase("TKV"),"Verify Full Pane view is open");
		ZAssert.assertTrue(app.zPageTasks.sIsElementPresent(Locators.zCloseButton), "Verify Close button is there");

		app.zPageTasks.zToolbarPressButton(Button.B_CLOSE);

		//After closing Task list view should show.
		ZAssert.assertTrue(app.zPageTasks.sGetEval("window.appCtxt.getCurrentViewType()").equalsIgnoreCase("TKL"),"Verify List view is open");

	}

}
