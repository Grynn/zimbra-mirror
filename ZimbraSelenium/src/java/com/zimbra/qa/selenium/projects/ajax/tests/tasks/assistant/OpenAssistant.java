package com.zimbra.qa.selenium.projects.ajax.tests.tasks.assistant;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.ui.Shortcut;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogAssistant;

public class OpenAssistant extends AjaxCommonTest {

	@SuppressWarnings("serial")
	public OpenAssistant() {
		logger.info("New " + OpenAssistant.class.getCanonicalName());
		
		super.startingPage = app.zPageTasks;
		super.startingAccountPreferences = new HashMap<String , String>() {{
			put("zimbraPrefTasksReadingPaneLocation", "bottom");
		}};

	}
/**
 * Test case: Open the assistant
 * 1.Go to Tasks
 * 2.Press '`' or backquote
 * Result:- Zimbra Assistant dialog should get open
 * @throws HarnessException
 */
	@Test(description = "Open the assistant", groups = { "deprecated" })
	public void OpenAssistant_01() throws HarnessException {

		// Click Get Mail button
		app.zPageTasks.zToolbarPressButton(Button.B_REFRESH);

		DialogAssistant assistant = (DialogAssistant) app.zPageTasks.zKeyboardShortcut(Shortcut.S_ASSISTANT);
		assistant.zClickButton(Button.B_CANCEL);
	}

}
