package com.zimbra.qa.selenium.projects.ajax.tests.preferences.quickcommands;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.DialogEditQuickCommand;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.DialogEditQuickCommand.*;
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
		
		String name = "name"+ ZimbraSeleniumProperties.getUniqueString();
		String description = "description"+ ZimbraSeleniumProperties.getUniqueString();
		
		

		// Navigate to preferences -> notifications
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.QuickCommands);

		
		// Click "New"
		DialogEditQuickCommand dialog = (DialogEditQuickCommand)app.zPagePreferences.zToolbarPressButton(Button.B_NEW_QUICK_COMMAND);
		ZAssert.assertTrue(dialog.zIsActive(), "Verify the dialog opened sucessfully, See bug 63932");
		
		// Fill out the dialog.  Click OK.
		dialog.zSetQuickCommandName(name);
		dialog.zSetQuickCommandDescription(description);
		dialog.zSetQuickCommandType(QuickCommandType.Message);
				
		dialog.zSetQuickCommandActionActive(1, true);
		dialog.zSetQuickCommandActionOperation(1, QuickCommandOperation.MarkAs);
		dialog.zSetQuickCommandActionTarget(1, QuickCommandTarget.MarkAsFlagged);
		
		dialog.zClickButton(Button.B_OK);
		
		
	}
}
