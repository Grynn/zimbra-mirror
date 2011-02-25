package com.zimbra.qa.selenium.projects.ajax.tests.tasks;

import java.util.List;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.AppAjaxClient;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogMove;


public class MoveTask extends AjaxCommonTest {

	public MoveTask() {
		logger.info("Move " + EditTask.class.getCanonicalName());

		// All tests start at the login page
		super.startingPage = app.zPageTasks;

		super.startingAccountPreferences = null;
	}

	@Test(description = "Create document through SOAP - move & verify through GUI", groups = { "smoke" })
	public void MoveTask_01() throws HarnessException {

		FolderItem taskFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Tasks);

		String name = "TaskFolder" + ZimbraSeleniumProperties.getUniqueString();

		// Create a subfolder to move the message into i.e. tasks/subfolder
		String taskFolderID = taskFolder.getId();

		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+		"<folder name='" + name + "' l='" + taskFolderID + "'/>"
				+	"</CreateFolderRequest>");

		FolderItem subFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), name);

		// refresh task page
		app.zTreeTasks.zTreeItem(Action.A_LEFTCLICK, taskFolder);

		String subject = "task" + ZimbraSeleniumProperties.getUniqueString();

		app.zGetActiveAccount().soapSend(
				"<CreateTaskRequest xmlns='urn:zimbraMail'>"
				+		"<m >"
				+			"<inv>"
				+				"<comp name='" + subject + "'>"
				+					"<or a='"+ app.zGetActiveAccount().EmailAddress + "'/>"
				+				"</comp>" 
				+			"</inv>"
				+			"<su>" + subject + "</su>"
				+			"<mp ct='text/plain'>"
				+				"<content>content" + ZimbraSeleniumProperties.getUniqueString() + "</content>"
				+			"</mp>"
				+		"</m>"
				+	"</CreateTaskRequest>");

		TaskItem task = TaskItem.importFromSOAP(app.zGetActiveAccount(),subject);
		ZAssert.assertNotNull(task, "Verify the task is created");

		// Refresh the tasks view
		app.zTreeTasks.zTreeItem(Action.A_LEFTCLICK, taskFolder);

		// Select the item
		app.zPageTasks.zListItem(Action.A_LEFTCLICK, subject);

		// Click on Move selected item icon in toolbar
		DialogMove dialogmove = (DialogMove) app.zPageTasks.zToolbarPressButton(Button.B_MOVE);
		ZAssert.assertTrue(dialogmove.zIsActive(), "Verify that the dialog is active or not");

		// Click OK on Confirmation dialog
		dialogmove.zClickTreeFolder(subFolder);
		dialogmove.zClickButton(Button.B_OK);

		// refresh tasks page
		app.zTreeTasks.zTreeItem(Action.A_LEFTCLICK, taskFolder);

		List<TaskItem> tasks = app.zPageTasks.zGetTasks();
		TaskItem found = null;
		for (TaskItem t : tasks) {
			logger.info("Task: looking for " + subject + " found: "
					+ t.gSubject);
			if (subject.equals(t.gSubject)) {
				// Found it!
				found = t;
				break;
			}
		}

		ZAssert.assertNull(found,"Verify the  task no longer  present in the task list");

		// click on subfolder in tree view
		app.zTreeTasks.zTreeItem(Action.A_LEFTCLICK, subFolder);
		List<TaskItem> tasks1 = app.zPageTasks.zGetTasks();

		TaskItem movetask = null;
		for (TaskItem t : tasks1) {
			logger.info("Task: looking for " + subject + " found: "
					+ t.gSubject);
			if (subject.equals(t.gSubject)) {
				// Found it!
				movetask = t;
				break;
			}
		}
		ZAssert.assertNotNull(movetask,	"Verify the task is moved to the selected folder");
	}

	@AfterMethod(groups = { "always" })
	public void afterMethod() throws HarnessException {
		logger.info("Checking for the Move Dialog ...");

		// Check if the "Move Dialog is still open
		DialogMove dialog = new DialogMove(app, ((AppAjaxClient)app).zPageTasks);
		if (dialog.zIsActive()) {
			logger.warn(dialog.myPageName()
					+ " was still active.  Cancelling ...");
			dialog.zClickButton(Button.B_CANCEL);
		}

	}

}
