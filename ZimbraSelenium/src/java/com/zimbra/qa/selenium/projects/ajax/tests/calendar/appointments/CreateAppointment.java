package com.zimbra.qa.selenium.projects.ajax.tests.calendar.appointments;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.AppointmentItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.*;
import com.zimbra.qa.selenium.projects.ajax.ui.*;

public class CreateAppointment extends AjaxCommonTest {


	public CreateAppointment() {
		logger.info("New "+ CreateAppointment.class.getCanonicalName());

		// All tests start at the Calendar page
		super.startingPage = app.zPageCalendar;

		// Make sure we are using an account with message view
		super.startingAccountPreferences = null;


	}
	private AppointmentItem createBasicAppt() throws HarnessException{
		// Create the message data to be sent
		AppointmentItem appt = new AppointmentItem();
		appt.setSubject("appointment" + ZimbraSeleniumProperties.getUniqueString());
		appt.setContent("content" + ZimbraSeleniumProperties.getUniqueString());
	

		// Open the new mail form
		FormApptNew apptForm = (FormApptNew) app.zPageCalendar.zToolbarPressButton(Button.B_NEW);
		ZAssert.assertNotNull(apptForm, "Verify the new form opened");

		// Fill out the form with the data
		apptForm.zFill(appt);

		// Send the message
		apptForm.zSubmit();
	
		return appt;
	}

	private void verifyApptCreatedOnServer(AppointmentItem appt) throws HarnessException{
		AppointmentItem actual = AppointmentItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ appt.getSubject() +")", appt.getStartTime().addDays(-7), appt.getEndTime().addDays(7));
		ZAssert.assertNotNull(actual, "Verify the new appointment is created");

		ZAssert.assertEquals(actual.getSubject(), appt.getSubject(), "Subject: Verify the appointment data");

	}
	@Test(	description = "Create a basic appointment",
			groups = { "sanity" }
	)
	public void CheckApptCreatedOnServer() throws HarnessException {
		// Create the message data to be sent
		AppointmentItem appt = new AppointmentItem();
		appt.setSubject("appointment" + ZimbraSeleniumProperties.getUniqueString());
		appt.setContent("content" + ZimbraSeleniumProperties.getUniqueString());
		appt.setStartTime(new ZDate(2014, 12, 25, 12, 0, 0));
		appt.setEndTime(new ZDate(2014, 12, 25, 14, 0, 0));


		// Open the new mail form
		FormApptNew apptForm = (FormApptNew) app.zPageCalendar.zToolbarPressButton(Button.B_NEW);
		ZAssert.assertNotNull(apptForm, "Verify the new form opened");

		// Fill out the form with the data
		apptForm.zFill(appt);

		// Send the message
		apptForm.zSubmit();
			
		//verify toasted message 'group created'  
        String expectedMsg ="Appointment Created";
        ZAssert.assertStringContains(app.zPageMain.zGetToaster().zGetToastMessage(),
        		        expectedMsg , "Verify toast message '" + expectedMsg + "'");
    
		
		verifyApptCreatedOnServer(appt);
	}

	
	@Test(	description = "Create a basic appointment with default time, verify appointment display in views",
			groups = { "smoke" }
	)
	public void CreateDefaultTimeApptVerifyApptDisplayInViews() throws HarnessException {
		AppointmentItem appt = createBasicAppt();
	
		//verify toasted message 'group created'  
        String expectedMsg ="Appointment Created";
        ZAssert.assertStringContains(app.zPageMain.zGetToaster().zGetToastMessage(),
        		        expectedMsg , "Verify toast message '" + expectedMsg + "'");
    
	
        //verify appt displayed in day view
		ApptView view= (ApptDayView) app.zPageCalendar.zToolbarPressPulldown(Button.B_LISTVIEW, Button.O_LISTVIEW_DAY);
	    ZAssert.assertTrue(view.isApptExist(appt), "Verify appt gets displayed in day view");
			
	    //verify appt displayed in workweek view
		view = (ApptWorkWeekView) app.zPageCalendar.zToolbarPressPulldown(Button.B_LISTVIEW, Button.O_LISTVIEW_WORKWEEK);
	    ZAssert.assertTrue(view.isApptExist(appt), "Verify appt gets displayed in work week view");
	    
	    //verify appt displayed in week view
		view = (ApptWeekView) app.zPageCalendar.zToolbarPressPulldown(Button.B_LISTVIEW, Button.O_LISTVIEW_WEEK);
	    ZAssert.assertTrue(view.isApptExist(appt), "Verify appt gets displayed in week view");
	    
	    //verify appt displayed in list view
		view = (ApptListView) app.zPageCalendar.zToolbarPressPulldown(Button.B_LISTVIEW, Button.O_LISTVIEW_LIST);
	    ZAssert.assertTrue(view.isApptExist(appt), "Verify appt gets displayed in list view");
	  
	    //verify appt displayed in month view
		view = (ApptMonthView) app.zPageCalendar.zToolbarPressPulldown(Button.B_LISTVIEW, Button.O_LISTVIEW_MONTH);
	    ZAssert.assertTrue(view.isApptExist(appt), "Verify appt gets displayed in month view");
	 
	    //verify appt displayed in schedule view
		view = (ApptScheduleView) app.zPageCalendar.zToolbarPressPulldown(Button.B_LISTVIEW, Button.O_LISTVIEW_SCHEDULE);
	    ZAssert.assertTrue(view.isApptExist(appt), "Verify appt gets displayed in schedule view");
	  
	}

}
