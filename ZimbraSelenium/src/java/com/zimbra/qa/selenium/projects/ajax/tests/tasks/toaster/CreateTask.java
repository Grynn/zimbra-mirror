package com.zimbra.qa.selenium.projects.ajax.tests.tasks.toaster;

import java.util.HashMap;
import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.Toaster;
import com.zimbra.qa.selenium.projects.ajax.ui.tasks.FormTaskNew;
import com.zimbra.qa.selenium.projects.ajax.ui.tasks.FormTaskNew.Field;

public class CreateTask extends AjaxCommonTest {

	@SuppressWarnings("serial")
	public CreateTask() {

		logger.info("New " + CreateTask.class.getCanonicalName());
		super.startingPage = app.zPageTasks;

		super.startingAccountPreferences = new HashMap<String, String>() {
			{
				// put("zimbraPrefComposeFormat", "html");
				put("zimbraPrefTasksReadingPaneLocation", "bottom");
			}
		};
	}

	@Test(description = "Verify Toaster message on Create Task", groups = { "functional" })
	public void CreateTask_01() throws HarnessException {

		String subject = "task" + ZimbraSeleniumProperties.getUniqueString();
		String body = "taskbody" + ZimbraSeleniumProperties.getUniqueString();

		// Click NEW button
		FormTaskNew taskNew = (FormTaskNew) app.zPageTasks.zToolbarPressButton(Button.B_NEW);

		// Fill out the resulting form
		taskNew.zFillField(Field.Subject, subject);
		taskNew.zFillField(Field.Body, body);
		taskNew.zSubmit();

		// Verifying the toaster message
		Toaster toast = app.zPageMain.zGetToaster();
		String toastMsg = toast.zGetToastMessage();
		ZAssert.assertStringContains(toastMsg, "Task Saved","Verify toast message: Task Saved");

	}

}
