package com.zimbra.qa.selenium.projects.ajax.tests.calendar.performance;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.util.performance.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;


public class ZmCalendarApp_ViewDay_Appointment100 extends AjaxCommonTest {

	
	public ZmCalendarApp_ViewDay_Appointment100() throws HarnessException {
		logger.info("New "+ ZmCalendarApp_ViewDay_Appointment100.class.getCanonicalName());
		
		super.startingPage = app.zPageMail;
		
		super.startingAccountPreferences = new HashMap<String, String>() {
			private static final long serialVersionUID = 3038458962443347843L;
			{
				put("zimbraPrefCalendarInitialView", "day");
			}};


	}
	
	
	@Test(	description = "Measure the time to load the Calendar, day view, 100 appointment",
			groups = { "performance" })
	public void ZmCalendarApp_01() throws HarnessException {
		
		// What is today?
		String now = (new SimpleDateFormat("yyyyMMdd")).format(Calendar.getInstance().getTime());
		
		// Import 100 appointments using Calendar.ics and REST
		String filename = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/ics/calendar02/Calendar.ics";
		File file = null;

		// Modify the ICS in two ways:
		// 1. Make the current account the organizer
		// 2. Make the current date equal to today
		file = RestUtil.FileUtils.replaceInFile("user@domain.com", app.zGetActiveAccount().EmailAddress, new File(filename));
		file = RestUtil.FileUtils.replaceInFile("20111101", now, new File(filename));

		RestUtil rest = new RestUtil();
		rest.setAuthentication(app.zGetActiveAccount());
		rest.setPath("/service/home/~/Calendar");
		rest.setQueryParameter("fmt", "ics");
		rest.setUploadFile(file);
		rest.doPost();

		// Refresh to pick up the changes
		app.zPageMain.zToolbarPressButton(Button.B_REFRESH);
		
		// Start the perf token
		PerfToken token = PerfMetrics.startTimestamp(PerfKey.ZmCalendarApp, "Load the calendar app, day view, 100 appointments");

		// Go to calendar
		app.zPageCalendar.zNavigateTo();

		PerfMetrics.waitTimestamp(token);
				
		// Wait for the app to load
		app.zPageCalendar.zWaitForActive();
		
	}


}
