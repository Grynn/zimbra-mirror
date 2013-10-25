/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.projects.ajax.tests.tasks.undo;

import java.util.HashMap;
import java.util.List;
import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.Toaster;



public class UndoMoveTask extends AjaxCommonTest {

	@SuppressWarnings("serial")
	public UndoMoveTask() {
		logger.info("Move " + UndoMoveTask.class.getCanonicalName());

		// All tests start at the login page
		super.startingPage = app.zPageTasks;

		super.startingAccountPreferences = new HashMap<String , String>() {{
			put("zimbraPrefShowSelectionCheckbox", "TRUE");
			put("zimbraPrefTasksReadingPaneLocation", "bottom");
		}};
	}

	@Test(description = "Undo moved task", groups = { "smoke" })
	public void UndoMoveTask_01() throws HarnessException {

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

		app.zPageTasks.zToolbarPressPulldown(Button.B_MOVE, subFolder);

		// Click undo
		Toaster toaster = app.zPageMain.zGetToaster();
		toaster.zClickUndo();

		// refresh tasks page
		app.zTreeTasks.zTreeItem(Action.A_LEFTCLICK, taskFolder);

		//Verify Task come back to Tasks folder

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

		ZAssert.assertNotNull(found,"Verify the  task is come back to Task folder");


	}
}