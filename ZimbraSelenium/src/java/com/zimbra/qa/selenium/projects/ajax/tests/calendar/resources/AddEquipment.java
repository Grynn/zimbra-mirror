package com.zimbra.qa.selenium.projects.ajax.tests.calendar.resources;

import java.awt.event.KeyEvent;
import java.util.Calendar;
import org.testng.annotations.*;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.CalendarWorkWeekTest;
import com.zimbra.qa.selenium.projects.ajax.ui.*;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.DialogConfirmDeleteOrganizer;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.DialogFindEquipment;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.DialogFindLocation;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.DialogSendUpdatetoAttendees;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.PageCalendar;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew.Field;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew.Locators;
@SuppressWarnings("unused")
public class AddEquipment extends CalendarWorkWeekTest {	
	
	public AddEquipment() {
		logger.info("New "+ AddEquipment.class.getCanonicalName());
		super.startingPage = app.zPageCalendar;
	}
	
	@Test(description = "Add Equipment to existing appointment by typing equipment name and verify F/B",
			groups = { "functional" })
	public void AddLocation_01() throws HarnessException {
		
		// Create a meeting
		AppointmentItem appt = new AppointmentItem();
		ZimbraResource equipment = new ZimbraResource(ZimbraResource.Type.EQUIPMENT);
		String apptSubject = ZimbraSeleniumProperties.getUniqueString();
		String apptEquipment = equipment.EmailAddress;
		
		// Absolute dates in UTC zone
		String tz = ZTimeZone.TimeZoneEST.getID();
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
                     	"</inv>" +
                     	"<e a='"+ ZimbraAccount.AccountA().EmailAddress +"' t='t'/>" +
                     	"<mp content-type='text/plain'>" +
                     		"<content>"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
                     	"</mp>" +
                     "<su>"+ apptSubject +"</su>" +
                     "</m>" +
               "</CreateAppointmentRequest>");
        app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
      
        // set appt 
        appt.setEquipment(apptEquipment);
       
        // Add equipment by typing in the field and resend the appointment
        FormApptNew apptForm = (FormApptNew)app.zPageCalendar.zListItem(Action.A_DOUBLECLICK, apptSubject);
        apptForm.zClickAt(Locators.ShowEquipmentLink,"");
        apptForm.zFill(appt);
        SleepUtil.sleepMedium();
		apptForm.zSubmit();
        apptForm.zToolbarPressButton(Button.B_SEND);
        SleepUtil.sleepVeryLong(); // test fails while checking free/busy status, waitForPostqueue is not sufficient here
        // Tried sleepLong() as well but although fails so using sleepVeryLong()
   
        // Verify equipment in the appointment	
        AppointmentItem actual = AppointmentItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ apptSubject +")");
		ZAssert.assertEquals(actual.getSubject(), apptSubject, "Subject: Verify the appointment data");
		ZAssert.assertEquals(actual.getEquipment(), apptEquipment, "equipment: Verify the appointment data");
		
		// Verify equipment free/busy status
		String equipmentStatus = app.zGetActiveAccount().soapSelectValue("//mail:at[@a='"+ apptEquipment +"']", "ptst");
		ZAssert.assertEquals(equipmentStatus, "AC", "Verify equipment status shows accepted");
		
	}
	@Test(description = "Add equipment to exisiting appt by from Serach equipment dialog",
			groups = { "functional" })
	public void AddEquipment_02() throws HarnessException {
		
		// Create a meeting
		AppointmentItem appt = new AppointmentItem();
		ZimbraResource equipment = new ZimbraResource(ZimbraResource.Type.EQUIPMENT);
	
		String apptSubject = ZimbraSeleniumProperties.getUniqueString();
		String apptEquipment = equipment.EmailAddress;
		
		// Absolute dates in UTC zone
		String tz = ZTimeZone.TimeZoneEST.getID();
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
                     	"</inv>" +
                     	"<e a='"+ ZimbraAccount.AccountA().EmailAddress +"' t='t'/>" +
                     	"<mp content-type='text/plain'>" +
                     		"<content>"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
                     	"</mp>" +
                     "<su>"+ apptSubject +"</su>" +
                     "</m>" +
               "</CreateAppointmentRequest>");
        app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
        
        // Add equipment from search equipment send the appointment
        FormApptNew apptForm = (FormApptNew)app.zPageCalendar.zListItem(Action.A_DOUBLECLICK, apptSubject);
        apptForm.zClick(Locators.ShowEquipmentLink);
        apptForm.zToolbarPressButton(Button.B_EQUIPMENT);
        
        // search for equipment add and resend
        DialogFindEquipment dialogFindEquipment = (DialogFindEquipment) new DialogFindEquipment(app, app.zPageCalendar);
        dialogFindEquipment.zType(Locators.EquipmentName, apptEquipment);
        dialogFindEquipment.zClickButton(Button.B_SEARCH_EQUIPMENT);
        SleepUtil.sleepSmall(); 
        dialogFindEquipment.zClickButton(Button.B_SELECT_EQUIPMENT);
        dialogFindEquipment.zClickButton(Button.B_OK);
        apptForm.zToolbarPressButton(Button.B_SEND);
        SleepUtil.sleepVeryLong(); // test fails while checking free/busy status, waitForPostqueue is not sufficient here
        // Tried sleepLong() as well but although fails so using sleepVeryLong()
 
        // Verify that equipment present in the appointment
        AppointmentItem actual = AppointmentItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ apptSubject +")");
		ZAssert.assertEquals(actual.getSubject(), apptSubject, "Subject: Verify the appointment data");
		ZAssert.assertStringContains(actual.getEquipment(), apptEquipment, "Equipment: Verify the appointment data");
		
		// Verify equipment free/busy status
		String equipmentStatus = app.zGetActiveAccount().soapSelectValue("//mail:at[@a='"+ apptEquipment +"']", "ptst");
		ZAssert.assertEquals(equipmentStatus, "AC", "Verify equipment free/busy status");
		
	}
	

}
