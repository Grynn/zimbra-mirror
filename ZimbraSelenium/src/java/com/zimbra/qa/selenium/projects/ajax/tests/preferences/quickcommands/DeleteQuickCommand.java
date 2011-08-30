package com.zimbra.qa.selenium.projects.ajax.tests.preferences.quickcommands;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.TreePreferences.TreeItem;


public class DeleteQuickCommand extends AjaxCommonTest {

	public DeleteQuickCommand() {
		
		super.startingPage = app.zPagePreferences;
		super.startingAccountPreferences = null;
	}
	


	@Test(
			description = "Delete a Quick Command",
			groups = { "functional" }
			)
	public void DeleteQuickCommand_01() throws HarnessException {
		
		
		// Navigate to preferences -> notifications
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.QuickCommands);

		// Select the quick command
		// TODO: implement me!

		// Click "Delete"
		app.zPagePreferences.zToolbarPressButton(Button.B_DELETE_QUICK_COMMAND);
		throw new HarnessException("See https://bugzilla.zimbra.com/show_bug.cgi?id=63931");
		
		// Get the quick commands from the server.  Verify the quick command is not there.
		
		
	}
}
