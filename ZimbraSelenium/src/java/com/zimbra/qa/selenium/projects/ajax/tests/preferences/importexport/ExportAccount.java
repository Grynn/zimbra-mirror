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

		String locator;
		
		
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

		// Make srue the fields are invisible first
		
		locator = "css=tr[id='ZmExportView_DATA_TYPES_row'][style='display: table-row;']";
		ZAssert.assertFalse(app.zPagePreferences.sIsElementPresent(locator), "Verify the advanced data is invisible");

		locator = "css=tr[id='ZmExportView_DATE_row'][style='display: table-row;']";
		ZAssert.assertFalse(app.zPagePreferences.sIsElementPresent(locator), "Verify the advanced data is invisible");

		locator = "css=tr[id='ZmExportView_SEARCH_FILTER_row'][style='display: table-row;']";
		ZAssert.assertFalse(app.zPagePreferences.sIsElementPresent(locator), "Verify the advanced data is invisible");

		locator = "css=tr[id='ZmExportView_SKIP_META_row'][style='display: table-row;']";
		ZAssert.assertFalse(app.zPagePreferences.sIsElementPresent(locator), "Verify the advanced data is invisible");

		// See https://bugzilla.zimbra.com/show_bug.cgi?id=63289
		// Although it looks like a checkbox, it seems sClick is ok for checking it.
		locator = "css=input[id='ZmExportView_ADVANCED']";
		app.zPagePreferences.sClick(locator);
		app.zPagePreferences.zWaitForBusyOverlay();

		// Verify the extra options now appear
		
		locator = "css=tr[id='ZmExportView_DATA_TYPES_row'][style='display: table-row;']";
		ZAssert.assertTrue(app.zPagePreferences.sIsElementPresent(locator), "Verify the advanced data is visible");

		locator = "css=tr[id='ZmExportView_DATE_row'][style='display: table-row;']";
		ZAssert.assertTrue(app.zPagePreferences.sIsElementPresent(locator), "Verify the advanced data is visible");

		locator = "css=tr[id='ZmExportView_SEARCH_FILTER_row'][style='display: table-row;']";
		ZAssert.assertTrue(app.zPagePreferences.sIsElementPresent(locator), "Verify the advanced data is visible");

		locator = "css=tr[id='ZmExportView_SKIP_META_row'][style='display: table-row;']";
		ZAssert.assertTrue(app.zPagePreferences.sIsElementPresent(locator), "Verify the advanced data is visible");


	}

}
