package com.zimbra.qa.selenium.projects.ajax.tests.preferences.quickcommands;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.QuickCommand;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxQuickCommandTest;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.TreePreferences.TreeItem;


public class GetQuickCommand extends AjaxQuickCommandTest {

	public GetQuickCommand() {

		super.startingPage = app.zPagePreferences;
		super.startingAccountPreferences = null;


	}

	@Test(
			description = "Get a list of basic Quick Commands",
			groups = { "functional" }
	)
	public void GetQuickCommand_01() throws HarnessException {

		// Navigate to preferences -> Quick Commands
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.QuickCommands);

		// Verify that the quick commands exist in the list
		int count = app.zTreePreferences.sGetCssCount("css=div[id='zl__QCV__rows'] div[id^='zli__QCV__']");
		ZAssert.assertEquals(count, 3, "Verify the correct number of quick commands exist in the list");

	}
	
	@Test(
			description = "Verify the Quick Command data in the list",
			groups = { "functional" }
	)
	public void GetQuickCommand_02() throws HarnessException {

				// Navigate to preferences -> notifications
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.QuickCommands);

		// Verify that the quick commands exist in the list
		int count = app.zTreePreferences.sGetCssCount("css=div[id='zl__QCV__rows'] div[id^='zli__QCV__']");
		ZAssert.assertEquals(count, 3, "Verify the two quick commands exist in the list");

		
		// See: https://bugzilla.zimbra.com/show_bug.cgi?id=63991
		
		QuickCommand found1 = null;
		QuickCommand found2 = null;
		QuickCommand found3 = null;
		for (int i = 1; i <= count; i++) {
			String locator = "css=div[id='zl__QCV__rows'] div[id^='zli__QCV__']:nth-of-type("+ i +")";
			
			// TODO: could probably make this into an IItem and use app.zTreePreferences.zGetListItems()
			boolean active = app.zTreePreferences.sIsElementPresent(locator + " td[id$='_ac'] div.ImgCheck");
			String name = app.zTreePreferences.sGetText(locator + " td[id$='_na']");
			String description = app.zTreePreferences.sGetText(locator + " td[id$='_count']");
			
			if ( active && name.equals(this.getQuickCommand01().getName()) && description.equals(this.getQuickCommand01().getDescription()))
				found1 = this.getQuickCommand01();
			if ( active && name.equals(this.getQuickCommand02().getName()) && description.equals(this.getQuickCommand02().getDescription()))
				found2 = this.getQuickCommand02();	
			if ( active && name.equals(this.getQuickCommand03().getName()) && description.equals(this.getQuickCommand03().getDescription()))
				found3 = this.getQuickCommand03();	
			
		}
		
		ZAssert.assertNotNull(found1, "Verify Quick Command #1 appears in the list");
		ZAssert.assertNotNull(found2, "Verify Quick Command #2 appears in the list");
		ZAssert.assertNotNull(found3, "Verify Quick Command #3 appears in the list");
		
	}

}
