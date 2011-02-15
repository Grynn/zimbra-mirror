package com.zimbra.qa.selenium.projects.ajax.tests.tasks;

import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.TaskItem;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.tasks.FormTaskNew;
import com.zimbra.qa.selenium.projects.ajax.ui.tasks.FormTaskNew.Field;

public class CreateTask extends AjaxCommonTest {

	public CreateTask() {
		// logger.info("New " + CreateDocument.class.getCanonicalName());
		logger.info("New " + CreateTask.class.getCanonicalName());

		super.startingPage = app.zPageTasks;

		super.startingAccountPreferences = null;
	}

	@Test(	description = "Create Simple task through GUI - verify through GUI",
			groups = { "sanity" })
	public void CreateTask_01() throws HarnessException {

		String subject = "task" + ZimbraSeleniumProperties.getUniqueString();
		String body = "taskbody" + ZimbraSeleniumProperties.getUniqueString();

		// Click NEW button
		FormTaskNew taskNew = (FormTaskNew) app.zPageTasks.zToolbarPressButton(Button.B_NEW);
		
		// Fill out the resulting form
		taskNew.zFillField(Field.Subject, subject);
		taskNew.zFillField(Field.Body, body);
		taskNew.zSubmit();
		
		// Get the list of tasks in the view
		List<TaskItem> tasks = app.zPageTasks.zGetTasks();
		ZAssert.assertNotNull(tasks, "Verify the list of tasks exists");
		
		// Iterate over the task list, looking for the new task
		TaskItem found = null;
		for (TaskItem t : tasks ) {
			logger.info("Task: looking for "+ subject +" found: "+ t.gSubject);
			if ( subject.equals(t.gSubject) ) {
				// Found it!
				found = t;
			}
		}
		
		ZAssert.assertNotNull(found, "Verify the new task is in the task list");

	}

}
