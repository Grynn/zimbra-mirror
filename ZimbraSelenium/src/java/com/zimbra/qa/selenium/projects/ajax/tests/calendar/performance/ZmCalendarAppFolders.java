package com.zimbra.qa.selenium.projects.ajax.tests.calendar.performance;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.util.performance.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;



public class ZmCalendarAppFolders extends AjaxCommonTest {

	public ZmCalendarAppFolders() {
		logger.info("New "+ ZmCalendarAppFolders.class.getCanonicalName());


		super.startingPage = app.zPageMail;
		super.startingAccountPreferences = null;

	}

	@Test(	description = "Measure the time to load the calendar app, 1 calendar",
			groups = { "performance" })
	public void ZmCalendarAppFolders_01() throws HarnessException {

		// Create a folder
		FolderItem root = FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.UserRoot);
		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
					"<folder name='cal"+ ZimbraSeleniumProperties.getUniqueString() + "' view='appointment' l='"+ root.getId() +"'/>" +
				"</CreateFolderRequest>");


		// Sync the changes to the client (notification block)
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
		
		PerfToken token = PerfMetrics.startTimestamp(PerfKey.ZmCalendarAppOverviewPanel, "Load the calendar app, 1 calendar");

		// Currently in the mail app
		// Navigate to the addressbook
		app.zPageCalendar.zNavigateTo();

		PerfMetrics.waitTimestamp(token);

		// Wait for the app to load
		app.zPageCalendar.zWaitForActive();


	}

	@Test(	description = "Measure the time to load the calendar app, 100 calendars",
			groups = { "performance" })
	public void ZmCalendarAppFolders_02() throws HarnessException {

		// Create 100 folders
		FolderItem root = FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.UserRoot);
		for (int i = 0; i < 100; i++) {
			app.zGetActiveAccount().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
						"<folder name='cal"+ ZimbraSeleniumProperties.getUniqueString() + "' view='appointment' l='"+ root.getId() +"'/>" +
					"</CreateFolderRequest>");
		}


		// Sync the changes to the client (notification block)
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		PerfToken token = PerfMetrics.startTimestamp(PerfKey.ZmCalendarAppOverviewPanel, "Load the calendar app, 100 calendars");

		// Currently in the mail app
		// Navigate to the addressbook
		app.zPageCalendar.zNavigateTo();

		PerfMetrics.waitTimestamp(token);

		// Wait for the app to load
		app.zPageCalendar.zWaitForActive();


	}


}
