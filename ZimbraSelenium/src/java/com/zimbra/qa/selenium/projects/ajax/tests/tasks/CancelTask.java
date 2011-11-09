package com.zimbra.qa.selenium.projects.ajax.tests.tasks;

import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;

import java.util.HashMap;
import java.util.List;
import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.TaskItem;
import com.zimbra.qa.selenium.framework.ui.AbsDialog;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.ui.Shortcut;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogWarning;
import com.zimbra.qa.selenium.projects.ajax.ui.tasks.FormTaskNew;
import com.zimbra.qa.selenium.projects.ajax.ui.tasks.FormTaskNew.Field;

public class CancelTask extends AjaxCommonTest {
	@SuppressWarnings("serial")
   public CancelTask() {
		logger.info("New " + CancelTask.class.getCanonicalName());

		super.startingPage = app.zPageTasks;

		super.startingAccountPreferences = new HashMap<String , String>() {{
       // put("zimbraPrefComposeFormat", "html");
			put("zimbraPrefTasksReadingPaneLocation", "bottom");
      }};
	}
	/**
	 * Test Case: CancelTask_01
	 * Open new Task 
	 * Enter Subject and body
	 * Press Cancel button
	 * Waring dialog should pop up and press no
	 * Task should not show in list 
	 * @throws HarnessException
	 */
	@Test(description = "Cancel composing of new task through GUI", groups = { "functional" })
	public void CancelTask_01() throws HarnessException {

		String subject = "task" + ZimbraSeleniumProperties.getUniqueString();
		String body = "taskbody" + ZimbraSeleniumProperties.getUniqueString();
		
		//Click NEW button
		FormTaskNew taskNew = (FormTaskNew) app.zPageTasks.zToolbarPressButton(Button.B_NEW);
		
		//Fill out resulting form
		taskNew.zFillField(Field.Subject, subject);
		taskNew.zFillField(Field.Body, body);
		
		//Click Cancel , to cancel the compose
		AbsDialog warning = (AbsDialog) taskNew.zToolbarPressButton(Button.B_CANCEL);
		ZAssert.assertNotNull(warning, "Verify the dialog is returned");

		//Click No button of warning dialog
		warning.zClickButton(Button.B_NO);

		List<TaskItem> tasks = app.zPageTasks.zGetTasks();

		TaskItem found = null;
		for (TaskItem t : tasks) {
			logger.info("Subject: looking for " + subject + " found: "
					+ t.gSubject);
			if (subject.equals(t.gSubject)) {
				found = t;
				break;
			}
		}

		ZAssert.assertNull(found, "Verify the task is no longer present in task list");

	}
	@Test(description = "Cancel composing of new task using Esc shortcut", groups = { "functional" })
	public void CancelTask_02() throws HarnessException {

		Shortcut shortcut = Shortcut.S_ESCAPE;
		String subject = "task" + ZimbraSeleniumProperties.getUniqueString();
		String body = "taskbody" + ZimbraSeleniumProperties.getUniqueString();
		
		//Click NEW button
		FormTaskNew taskNew = (FormTaskNew) app.zPageTasks.zToolbarPressButton(Button.B_NEW);
		
		//Fill out resulting form		
		taskNew.zFillField(Field.Subject, subject);
		taskNew.zFillField(Field.Body, body);
				
		//Click Escape shortcut 'Esc'	
		DialogWarning warning =(DialogWarning)app.zPageTasks.zKeyboardShortcut(shortcut);
		ZAssert.assertNotNull(warning, "Verify the dialog is opened");

		//Click No button of warning dialog
		warning.zClickButton(Button.B_NO);

		List<TaskItem> tasks = app.zPageTasks.zGetTasks();

		TaskItem found = null;
		for (TaskItem t : tasks) {
			logger.info("Subject: looking for " + subject + " found: "
					+ t.gSubject);
			if (subject.equals(t.gSubject)) {
				found = t;
				break;
			}
		}

		ZAssert.assertNull(found, "Verify the task is no longer present in task list");

	}
}
