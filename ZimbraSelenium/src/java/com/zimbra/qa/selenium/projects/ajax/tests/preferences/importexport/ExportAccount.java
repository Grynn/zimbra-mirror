package com.zimbra.qa.selenium.projects.ajax.tests.preferences.importexport;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.TreePreferences.TreeItem;


public class ExportAccount extends AjaxCommonTest {

	public ExportAccount() {
		
		super.startingPage = app.zPagePreferences;
		super.startingAccountPreferences = null;
	}


	@Test(
			description = "Verify clicking on 'export account' radio button",
			groups = { "functional" }
			)
	public void ExportAccount_01() throws HarnessException {

		
		/**
		 * 
		 * TODO: Since selenium doesn't handle the
		 * 'download' system dialog, just execute
		 * as much as possible - click Account and
		 * verify the display.
		 * 
		 */
		
		
		// Navigate to preferences -> notifications
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.ImportExport);

		// TODO: See https://bugzilla.zimbra.com/show_bug.cgi?id=63289
		String locator = "css=div[id$='_TYPE_TGZ_control'] input[type='radio']";
		app.zPagePreferences.sFocus(locator);
		app.zPagePreferences.sClick(locator);
		app.zPagePreferences.zWaitForBusyOverlay();

		// Verify the help hint
		String hint = app.zPagePreferences.sGetText("css=div[id$='_TYPE_HINT']");
		ZAssert.assertStringContains(hint, "All account data can be exported", "Verify help hint text changed");
		
	}
	
	@Test(
			description = "Verify clicking on 'Advanced Settings' checkbox",
			groups = { "functional" }
			)
	public void ExportAccount_02() throws HarnessException {

		
		/**
		 * 
		 * TODO: Since selenium doesn't handle the
		 * 'download' system dialog, just execute
		 * as much as possible - click Account and
		 * verify the display.
		 * 
		 */
		
		
		// Navigate to preferences -> notifications
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.ImportExport);

		// TODO: See https://bugzilla.zimbra.com/show_bug.cgi?id=63289
		// I don't think this Locator is unique enough to find the Advanced Search checkbox
		String locator = "css=div[id$='_ADVANCED_control'] input[type='checkbox']";
		if ( !app.zPagePreferences.sIsChecked(locator) ) {
			app.zPagePreferences.sCheck(locator);
			app.zPagePreferences.zWaitForBusyOverlay();
		}

		throw new HarnessException("See https://bugzilla.zimbra.com/show_bug.cgi?id=63289");
		
		// TODO: add some verification for the other advanced options displayed text.
		
	}

}
