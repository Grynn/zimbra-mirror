package com.zimbra.qa.selenium.projects.ajax.tests.preferences.quickcommands;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxQuickCommandTest;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogWarning;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.TreePreferences.TreeItem;


public class DeleteQuickCommand extends AjaxQuickCommandTest {

	public DeleteQuickCommand() {
		
		super.startingPage = app.zPagePreferences;
		super.startingAccountPreferences = null;
	}
	


	@Bugs(ids = "71389")	// Hold off on GUI implementation of Quick Commands in 8.X
	@Test(
			description = "Delete a Quick Command",
			groups = { "deprecated" }
			)
	public void DeleteQuickCommand_01() throws HarnessException {
		
		String name = this.getQuickCommand01().getName();
		
		// Navigate to preferences -> notifications
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.QuickCommands);

		// Select the quick command
		String locator = "css=div[id='zl__QCV__rows'] div[id^='zli__QCV__'] td[id$='_na']:contains('"+ name +"')";
		ZAssert.assertTrue(app.zTreePreferences.sIsElementPresent(locator), "Verify quick command "+ name +" is in the list");
		app.zTreePreferences.zClickAt(locator, "");
		app.zTreePreferences.zWaitForBusyOverlay();
		
		// Click "Delete"
		DialogWarning dialog = (DialogWarning)app.zPagePreferences.zToolbarPressButton(Button.B_DELETE_QUICK_COMMAND);
		dialog.zClickButton(Button.B_YES);

		// Verify the item no longer appears in the list
		ZAssert.assertFalse(app.zTreePreferences.sIsElementPresent(locator), "Verify quick command "+ name +" is not in the list");

		
		
	}
}
