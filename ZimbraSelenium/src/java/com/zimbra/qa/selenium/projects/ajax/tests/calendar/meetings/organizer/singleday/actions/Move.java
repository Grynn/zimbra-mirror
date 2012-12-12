package com.zimbra.qa.selenium.projects.ajax.tests.calendar.meetings.organizer.singleday.actions;

import java.util.Calendar;
import org.testng.annotations.*;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.CalendarWorkWeekTest;
import com.zimbra.qa.selenium.projects.ajax.ui.*;

public class Move extends CalendarWorkWeekTest {	
	
	public Move() {
		logger.info("New "+ Move.class.getCanonicalName());
		super.startingPage = app.zPageCalendar;
	}
	
	@Test(description = "Move meeting invite using context menu",
			groups = { "functional" })

	public void MoveMeeting_01() throws HarnessException {
		
		// Creating object for meeting data
		String tz, apptSubject, apptBody, apptAttendee1;
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		apptBody = ZimbraSeleniumProperties.getUniqueString();
		apptAttendee1 = ZimbraAccount.AccountA().EmailAddress;
		
		// Absolute dates in UTC zone
		tz = ZTimeZone.TimeZoneEST.getID();
		Calendar now = this.calendarWeekDayUTC;
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);
		
		//creating folder data to move appt to
		String name1 = "folder" + ZimbraSeleniumProperties.getUniqueString();
		FolderItem root = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.UserRoot);
		app.zGetActiveAccount().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+	  	"<folder name='"+ name1 +"' l='"+ root.getId() +"' view='appointment'/>"
				+	"</CreateFolderRequest>");

		FolderItem subfolder1 = FolderItem.importFromSOAP(app.zGetActiveAccount(), name1);
		ZAssert.assertNotNull(subfolder1, "Verify the first subfolder is available");
		  // Refresh the view
        app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
        
        // create meeting
		app.zGetActiveAccount().soapSend(
                "<CreateAppointmentRequest xmlns='urn:zimbraMail'>" +
                     "<m>"+
                     "<inv method='REQUEST' type='event' status='CONF' draft='0' class='PUB' fb='B' transp='O' allDay='0' name='"+ apptSubject +"'>"+
                     "<s d='"+ startUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                     "<e d='"+ endUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                     "<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
                     "<at role='REQ' ptst='NE' rsvp='1' a='" + apptAttendee1 + "' d='2'/>" + 
                     "</inv>" +
                     "<e a='"+ apptAttendee1 +"' t='t'/>" +
                     "<mp content-type='text/plain'>" +
                     "<content>"+ apptBody +"</content>" +
                     "</mp>" +
                     "<su>"+ apptSubject +"</su>" +
                     "</m>" +
               "</CreateAppointmentRequest>");
        
		//-- GUI actions
        // Refresh the view
        app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);

        // Right Click -> Move context menu
        DialogMove dialog = (DialogMove)app.zPageCalendar.zListItem(Action.A_RIGHTCLICK, Button.B_MOVE, apptSubject);
		dialog.zClickTreeFolder(subfolder1);
		dialog.zClickButton(Button.B_OK);
        
		//-- Server verification
		AppointmentItem newAppointment = AppointmentItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ apptSubject +")");
		ZAssert.assertEquals(newAppointment.getFolder(), subfolder1.getId(), "Verify the appointment moved folders");
	}
	
}
