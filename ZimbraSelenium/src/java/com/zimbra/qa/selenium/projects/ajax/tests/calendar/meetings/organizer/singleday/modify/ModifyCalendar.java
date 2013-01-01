package com.zimbra.qa.selenium.projects.ajax.tests.calendar.meetings.organizer.singleday.modify;

import java.util.Calendar;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.AppointmentItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.CalendarWorkWeekTest;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew.Field;

public class ModifyCalendar extends CalendarWorkWeekTest {

	public ModifyCalendar() {
		logger.info("New "+ ModifyCalendar.class.getCanonicalName());
		super.startingPage = app.zPageCalendar;
	}

	@Bugs(ids = "69132")
	@Test(	description = "Modify meeting calendar",
			groups = { "functional" })
			
	public void ModifyMeetingCalendar_01() throws HarnessException {
		
		// Create data
		String tz, apptSubject, apptBody, apptAttendee, apptCalendar;
		tz = ZTimeZone.TimeZoneEST.getID();
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		apptBody = ZimbraSeleniumProperties.getUniqueString();
		apptAttendee = ZimbraAccount.AccountA().EmailAddress;
		apptCalendar = ZimbraSeleniumProperties.getUniqueString();
		
		// Create new calendar folder
		FolderItem root = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.UserRoot);
		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
                	"<folder name='"+ apptCalendar +"' l='"+ root.getId() +"' view='appointment'/>" +
                "</CreateFolderRequest>");
		FolderItem apptCal = FolderItem.importFromSOAP(app.zGetActiveAccount(), apptCalendar);
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
		
		// Absolute dates in UTC zone
		Calendar now = this.calendarWeekDayUTC;
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);
		
		app.zGetActiveAccount().soapSend(
                "<CreateAppointmentRequest xmlns='urn:zimbraMail'>" +
                     "<m>"+
                     "<inv method='REQUEST' type='event' status='CONF' draft='0' class='PUB' fb='B' transp='O' allDay='0' name='"+ apptSubject +"'>"+
                     "<s d='"+ startUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                     "<e d='"+ endUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                     "<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
                     "<at role='REQ' ptst='NE' rsvp='1' a='" + apptAttendee + "'/>" + 
                     "</inv>" +
                     "<e a='"+ apptAttendee +"' t='t'/>" +
                     "<mp content-type='text/plain'>" +
                     "<content>"+ apptBody +"</content>" +
                     "</mp>" +
                     "<su>"+ apptSubject +"</su>" +
                     "</m>" +
               "</CreateAppointmentRequest>");
		
        // Open appointment and modify calendar folder
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
        FormApptNew apptForm = (FormApptNew)app.zPageCalendar.zListItem(Action.A_DOUBLECLICK, apptSubject);       
        if(ZimbraSeleniumProperties.isWebDriver()){
            String locator = "css=td[id$='_folderSelect'] td[id$='_select_container']";
            apptForm.sClickAt(locator, "");            

            locator = "//div[@id='z_shell']/div[contains(@id,'_Menu_') and contains(@class, 'DwtMenu')]";   
            int count = apptForm.sGetXpathCount(locator);           
            for  (int  i = 1; i <= count; i++) {
        	String calPullDown = locator + "[position()=" + i + "]//tr//*[contains(text(),'" + apptCalendar + "')]";
        	if(apptForm.zIsVisiblePerPosition(calPullDown, 0, 0)){
        	    apptForm.sClickAt(calPullDown, "");
        	    break;
        	}        	
            }            
        }else{
            apptForm.zFillField(Field.CalendarFolder, apptCalendar);
        }
        apptForm.zToolbarPressButton(Button.B_SEND);
        
        // Verify calendar value
        AppointmentItem actual = AppointmentItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ apptSubject +")");
        ZAssert.assertEquals(actual.getFolder(), apptCal.getId(), "Verify calendar folder value");
		
	}
	
}
