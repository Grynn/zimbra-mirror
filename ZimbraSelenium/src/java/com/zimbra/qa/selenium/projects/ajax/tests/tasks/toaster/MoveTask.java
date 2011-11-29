package com.zimbra.qa.selenium.projects.ajax.tests.tasks.toaster;

import java.util.HashMap;
import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.Toaster;



public class MoveTask extends AjaxCommonTest {

	@SuppressWarnings("serial")
	public MoveTask() {
		logger.info("Move " + MoveTask.class.getCanonicalName());

		// All tests start at the login page
		super.startingPage = app.zPageTasks;

		super.startingAccountPreferences = new HashMap<String , String>() {{
			put("zimbraPrefTasksReadingPaneLocation", "bottom");
		}};
	}
	
	@Test(description = "Verify Toaster message on moving  Task", groups = { "functional" })
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

		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());

		TaskItem task = TaskItem.importFromSOAP(app.zGetActiveAccount(),subject);
		ZAssert.assertNotNull(task, "Verify the task is created");

		// Refresh the tasks view
		app.zTreeTasks.zTreeItem(Action.A_LEFTCLICK, taskFolder);
		
		app.zPageTasks.zToolbarPressPulldown(Button.B_MOVE, subFolder);

		// Verifying the toaster message
		Toaster toast = app.zPageMain.zGetToaster();
	   String toastMsg = toast.zGetToastMessage();
	   ZAssert.assertStringContains(toastMsg, "1 task moved to", "Verify toast message: 1 task moved to");
	}
}
