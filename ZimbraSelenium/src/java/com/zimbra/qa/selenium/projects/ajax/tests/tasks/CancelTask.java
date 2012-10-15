package com.zimbra.qa.selenium.projects.ajax.tests.tasks;

import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;

import java.util.HashMap;
import java.util.List;
import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.TaskItem;
import com.zimbra.qa.selenium.framework.ui.AbsDialog;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.ui.Shortcut;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogWarning;
import com.zimbra.qa.selenium.projects.ajax.ui.tasks.FormTaskNew;
import com.zimbra.qa.selenium.projects.ajax.ui.tasks.FormTaskNew.Field;
import com.zimbra.qa.selenium.projects.ajax.ui.tasks.PageTasks.Locators;

public class CancelTask extends AjaxCommonTest {
	@SuppressWarnings("serial")
   public CancelTask() {
		logger.info("New " + CancelTask.class.getCanonicalName());

		super.startingPage = app.zPageTasks;

		super.startingAccountPreferences = new HashMap<String , String>() {{
			put("zimbraPrefComposeFormat", "text");
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
		SleepUtil.sleepSmall();
		
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
		SleepUtil.sleepSmall();
		
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
	/*
	 * @steps
	 * 1.Login to Web client
	 * 2.Go to Task
	 * 3.Click New Task
	 * 4.Click Attach button from toolbar
	 * 5.Click Cancel
	 * 6.Again click New Task
	 * 7.Click Attach button from toolbar
	 * 8.Hit Cancel
	 * Expected:
	 * Compose task with empty attachment should get cancelled every time whenever
	 * user click on cancel 
	 */
	@Bugs(ids = "74670")
	@Test(description = "cancelling empty attachment task in 2nd attempt", groups = { "functional" })
	public void Bug_74670() throws HarnessException {

		//1st attempt
		//Click NEW button
		app.zPageTasks.zClickAt(Locators.zNewTask,"");
		SleepUtil.sleepMedium();
		app.zPageTasks.zClickAt(Locators.zAttachButton, "");
		SleepUtil.sleepMedium();
		ZAssert.assertTrue(app.zPageTasks.sIsElementPresent(Locators.zAttachmentInputBox),"Verify Attachment input box ");		 
		app.zPageTasks.zClickAt(FormTaskNew.Locators.zCancelTask,"");
		ZAssert.assertTrue(app.zPageTasks.sGetEval("window.appCtxt.getCurrentViewType()").equalsIgnoreCase("TKL"),"Verify List view is open");

		//2nd attempt
		app.zPageTasks.zClickAt(Locators.zNewTask,"");
		SleepUtil.sleepMedium();
		app.zPageTasks.zClickAt(Locators.zAttachButton, "");	
		SleepUtil.sleepMedium();
		ZAssert.assertTrue(app.zPageTasks.sIsElementPresent(Locators.zAttachmentInputBox),"Verify Attachment input box ");		 
		app.zPageTasks.zClickAt(FormTaskNew.Locators.zCancelTask,"");
		ZAssert.assertTrue(app.zPageTasks.sGetEval("window.appCtxt.getCurrentViewType()").equalsIgnoreCase("TKL"),"Verify List view is open");		 
	}
}
