package com.zimbra.qa.selenium.projects.ajax.tests.preferences.quickcommands;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.DialogEditQuickCommand;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.TreePreferences.TreeItem;


public class CreateQuickCommand extends AjaxCommonTest {

	public CreateQuickCommand() {
		
		super.startingPage = app.zPagePreferences;
		super.startingAccountPreferences = null;
	}
	


	@Test(
			description = "Create a basic Quick Command",
			groups = { "functional" }
			)
	public void CreateQuickCommand_01() throws HarnessException {
		
		
		// Navigate to preferences -> notifications
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.QuickCommands);

		
		// Click "New"
		DialogEditQuickCommand dialog = (DialogEditQuickCommand)app.zPagePreferences.zToolbarPressButton(Button.B_NEW_QUICK_COMMAND);
		ZAssert.assertTrue(dialog.zIsActive(), "Verify the dialog opened sucessfully, See bug 63932");
		
		// TODO:
		// Fill out the dialog.  Click OK.
		
		// Get the quick commands from the server.  Verify the new quick command appears.
		
		
	}
}
