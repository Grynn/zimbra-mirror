package com.zimbra.qa.selenium.projects.ajax.tests.calendar.performance;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.AppointmentItem;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.util.performance.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;


public class ZmCalendarApp_ViewWorkWeek_Appointment1 extends AjaxCommonTest {

	
	public ZmCalendarApp_ViewWorkWeek_Appointment1() throws HarnessException {
		logger.info("New "+ ZmCalendarApp_ViewWorkWeek_Appointment1.class.getCanonicalName());
		
		super.startingPage = app.zPageMail;
		
		super.startingAccountPreferences = new HashMap<String, String>() {
			private static final long serialVersionUID = 3038458962443347843L;
			{
				put("zimbraPrefCalendarInitialView", "workWeek");
			}};


	}
	
	
	@Test(	description = "Measure the time to load the Calendar, work week view, initial load",
			groups = { "performance" })
	public void ZmCalendarApp_01() throws HarnessException {
		
		// Create an appointment
		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
		AppointmentItem.createAppointmentSingleDay(
				app.zGetActiveAccount(),
				Calendar.getInstance(),
				60,
				null,
				subject,
				"content" + ZimbraSeleniumProperties.getUniqueString(),
				"location" + ZimbraSeleniumProperties.getUniqueString(),
				null);


		// Start the perf token
		PerfToken token = PerfMetrics.startTimestamp(PerfKey.ZmCalendarApp, "Load the calendar app, work week view, initial load");

		// Go to calendar
		app.zPageCalendar.zNavigateTo();

		PerfMetrics.waitTimestamp(token);
				
		// Wait for the app to load
		app.zPageCalendar.zWaitForActive();
		
		
	}

	@Test(	description = "Measure the time to load the Calendar, work week view, 1 appointment",
			groups = { "performance" })
	public void ZmCalendarApp_02() throws HarnessException {
		
		// Create an appointment
		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
		AppointmentItem.createAppointmentSingleDay(
				app.zGetActiveAccount(),
				Calendar.getInstance(),
				60,
				null,
				subject,
				"content" + ZimbraSeleniumProperties.getUniqueString(),
				"location" + ZimbraSeleniumProperties.getUniqueString(),
				null);


		// Start the perf token
		PerfToken token = PerfMetrics.startTimestamp(PerfKey.ZmCalendarApp, "Load the calendar app, work week view, 1 appointment");

		// Go to calendar
		app.zPageCalendar.zNavigateTo();

		PerfMetrics.waitTimestamp(token);
				
		// Wait for the app to load
		app.zPageCalendar.zWaitForActive();
		
		
	}

	@Test(	description = "Measure the time to load the Calendar, work week view, 100 appointments",
			groups = { "performance" })
	public void ZmCalendarApp_03() throws HarnessException {
		
		// What is today?
		Calendar monday = Calendar.getInstance();		monday.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		Calendar tuesday = Calendar.getInstance();		tuesday.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
		Calendar wednesday = Calendar.getInstance();	wednesday.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
		Calendar thursday = Calendar.getInstance();		thursday.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
		Calendar friday = Calendar.getInstance();		friday.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);

		// Import 100 appointments using Calendar.ics and REST
		String filename = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/ics/calendar03/Calendar.ics";
		File file = null;

		// Modify the ICS in two ways:
		// 1. Make the current account the organizer
		// 2. Make the current date equal to today
		file = RestUtil.FileUtils.replaceInFile("user@domain.com", app.zGetActiveAccount().EmailAddress, new File(filename));
		file = RestUtil.FileUtils.replaceInFile("20111031", (new SimpleDateFormat("yyyyMMdd")).format(monday.getTime()), file);
		file = RestUtil.FileUtils.replaceInFile("20111101", (new SimpleDateFormat("yyyyMMdd")).format(tuesday.getTime()), file);
		file = RestUtil.FileUtils.replaceInFile("20111102", (new SimpleDateFormat("yyyyMMdd")).format(wednesday.getTime()), file);
		file = RestUtil.FileUtils.replaceInFile("20111103", (new SimpleDateFormat("yyyyMMdd")).format(thursday.getTime()), file);
		file = RestUtil.FileUtils.replaceInFile("20111104", (new SimpleDateFormat("yyyyMMdd")).format(friday.getTime()), file);

		RestUtil rest = new RestUtil();
		rest.setAuthentication(app.zGetActiveAccount());
		rest.setPath("/service/home/~/Calendar");
		rest.setQueryParameter("fmt", "ics");
		rest.setUploadFile(file);
		rest.doPost();


		// Start the perf token
		PerfToken token = PerfMetrics.startTimestamp(PerfKey.ZmCalendarApp, "Load the calendar app, work week view, 100 appointment");

		// Go to calendar
		app.zPageCalendar.zNavigateTo();

		PerfMetrics.waitTimestamp(token);
		
				
		// Wait for the app to load
		app.zPageCalendar.zWaitForActive();
		
		
		throw new HarnessException("snapshot");
	}


}
