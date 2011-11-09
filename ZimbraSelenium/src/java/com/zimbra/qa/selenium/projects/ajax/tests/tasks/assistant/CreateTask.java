package com.zimbra.qa.selenium.projects.ajax.tests.tasks.assistant;



import java.util.HashMap;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.TaskItem;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.ui.Shortcut;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogAssistant;


public class CreateTask extends AjaxCommonTest {

	@SuppressWarnings("serial")
	public CreateTask() {
		logger.info("New " + CreateTask.class.getCanonicalName());
		
		super.startingPage = app.zPageTasks;
		super.startingAccountPreferences = new HashMap<String , String>() {{
			put("zimbraPrefTasksReadingPaneLocation", "bottom");
		}};

	}
/**
 * Test case: Create Task using assistant
 * 1.Go to Tasks
 * 2.Press '`' or backquote
 * 3.Zimbra Assistant dialog should get open
 * 4.Put Task related commands like subject and body
 * 5.Press OK.
 * 6.Task should get created
 * @throws HarnessException
 */
	@Test(description = "Create Task using assistant", groups = { "functional" })
	public void CreateTask_01() throws HarnessException {
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String command = "task \"" + subject + "\" Notes(hello)";
		// Click Get Mail button
		app.zPageTasks.zToolbarPressButton(Button.B_REFRESH);
		
		//Click on shortcut '`' or backquote
		DialogAssistant assistant = (DialogAssistant) app.zPageTasks.zKeyboardShortcut(Shortcut.S_ASSISTANT);
		assistant.zEnterCommand(command);
		//Click OK button
		assistant.zClickButton(Button.B_OK);
		
		//Verify created task
		TaskItem task = TaskItem.importFromSOAP(app.zGetActiveAccount(), subject);
		ZAssert.assertEquals(task.getName(), subject, "Verify task subject");

	}
	/**
	 * Test case: Create Task using assistant and save it
	 * 1.Go to Tasks
	 * 2.Press '`' or backquote
	 * 3.Zimbra Assistant dialog should get open
	 * 4.Put Task related commands like subject and body
	 * 5.Press More Details.
	 * 6.Press save button
	 * 6.Task should get created and show in the list
	 * @throws HarnessException
	 */
		@Bugs(ids="63199")
		@Test(description = "Create Task using assistant and save it", groups = { "functional" })
		public void CreateTask_02() throws HarnessException {
			String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
			String command = "task \"" + subject + "\" Notes(hello)";
			// Click Get Mail button
			app.zPageTasks.zToolbarPressButton(Button.B_REFRESH);

			//Click on shortcut '`' or backquote
			DialogAssistant assistant = (DialogAssistant) app.zPageTasks.zKeyboardShortcut(Shortcut.S_ASSISTANT);
			assistant.zEnterCommand(command);
			
			//Click More Details button
			assistant.zClickButton(Button.B_MORE_DETAILS);
			app.zPageTasks.zToolbarPressButton(Button.B_SAVE);
			
			//Verify created task			
			TaskItem task = TaskItem.importFromSOAP(app.zGetActiveAccount(), subject);
			ZAssert.assertEquals(task.getName(), subject, "Verify task subject");

		}

}
