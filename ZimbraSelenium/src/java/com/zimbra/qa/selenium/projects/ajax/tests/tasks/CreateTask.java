package com.zimbra.qa.selenium.projects.ajax.tests.tasks;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.TaskItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.tasks.TaskNew;

public class CreateTask extends AjaxCommonTest {

	public CreateTask() {
		// logger.info("New " + CreateDocument.class.getCanonicalName());
		logger.info("New " + CreateTask.class.getCanonicalName());

		super.startingPage = app.zPageTasks;

		super.startingAccountPreferences = null;
	}

	@Test(description = "Create Simple task through GUI - verify through GUI", groups = { "sanity" })
	public void CreateTask_01() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem taskFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Tasks);

		// Create task item
		TaskItem task = new TaskItem();
		String sub = task.gettaskSubject();
		String body = task.gettaskBody();
		TaskNew taskNew = (TaskNew) app.zPageTasks
				.zToolbarPressButton(Button.B_NEW);

		taskNew.zFill(task);

		taskNew.zSubmit();

		app.zTreeTasks.zTreeItem(Action.A_LEFTCLICK, taskFolder);

		boolean present = app.zPageTasks.isPresent(sub);
		ZAssert.assertTrue(present, "Verify subject through GUI");

	}

}
