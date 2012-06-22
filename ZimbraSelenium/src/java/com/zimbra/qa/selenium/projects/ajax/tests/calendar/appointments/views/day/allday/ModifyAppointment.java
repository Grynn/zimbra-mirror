package com.zimbra.qa.selenium.projects.ajax.tests.calendar.appointments.views.day.allday;

import java.util.Calendar;
import java.util.HashMap;
import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew.Field;

public class ModifyAppointment extends AjaxCommonTest {

	public ModifyAppointment() {
		logger.info("New " + ModifyAppointment.class.getCanonicalName());

		// All tests start at the Calendar page
		super.startingPage = app.zPageCalendar;

		// Make sure we are using an account with message view
		super.startingAccountPreferences = new HashMap<String, String>() {
			private static final long serialVersionUID = -2913827779459595178L;
		{
		    put("zimbraPrefCalendarInitialView", "day");
		}};
	}

	@Bugs(ids = "69132")
	@Test(
			description = "Modify all-day appointment with subject & body and verify it in day view", 
			groups = { "functional" })
	public void ModifyAllDayAppointment_01() throws HarnessException {

		// Creating object for appointment data
		String tz, apptSubject, apptBody, editApptSubject, editApptBody;
		tz = ZTimeZone.TimeZoneEST.getID();
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		apptBody = ZimbraSeleniumProperties.getUniqueString();
		editApptSubject = ZimbraSeleniumProperties.getUniqueString();
        editApptBody = ZimbraSeleniumProperties.getUniqueString();
		
		// Absolute dates in UTC zone
        Calendar now = Calendar.getInstance();
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);
		
        app.zGetActiveAccount().soapSend(
                          "<CreateAppointmentRequest xmlns='urn:zimbraMail'>" +
                               "<m>"+
                               "<inv method='REQUEST' type='event' fb='B' transp='O' allDay='1' name='"+ apptSubject +"'>"+
                               "<s d='"+ startUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                               "<e d='"+ endUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                               "<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
                               "</inv>" +
                               "<mp content-type='text/plain'>" +
                               "<content>"+ apptBody +"</content>" +
                               "</mp>" +
                               "<su>"+ apptSubject +"</su>" +
                               "</m>" +
                         "</CreateAppointmentRequest>");

        String apptId = app.zGetActiveAccount().soapSelectValue("//mail:CreateAppointmentResponse", "apptId");
    
        // Switch to day view
        app.zPageCalendar.zToolbarPressPulldown(Button.B_LISTVIEW, Button.O_LISTVIEW_DAY);
        app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);

        // Open appointment & modify subject, body and save it
        FormApptNew form = (FormApptNew)app.zPageCalendar.zListItem(Action.A_DOUBLECLICK, apptSubject);
        ZAssert.assertNotNull(form, "Verify the appointment form oopens correctly");

        form.zFillField(Field.Subject, editApptSubject);
        form.zFillField(Field.Body, editApptBody);
        form.zToolbarPressButton(Button.B_SAVEANDCLOSE);
        
        // Use GetAppointmentRequest to verify the changes are saved
        app.zGetActiveAccount().soapSend("<GetAppointmentRequest  xmlns='urn:zimbraMail' id='"+ apptId +"'/>");
        ZAssert.assertEquals(app.zGetActiveAccount().soapSelectValue("//mail:GetAppointmentResponse//mail:comp", "name"), editApptSubject, "Verify the new appointment name matches");
        ZAssert.assertStringContains(app.zGetActiveAccount().soapSelectValue("//mail:GetAppointmentResponse//mail:desc", null), editApptBody, "Verify the new appointment body matches");
        ZAssert.assertEquals(app.zGetActiveAccount().soapSelectValue("//mail:GetAppointmentResponse//mail:comp", "allDay"), "1", "Verify the appointment remains as an allday='1'");
	}
}