package com.zimbra.qa.selenium.projects.ajax.tests.calendar.meetings.organizer.scheduler;

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
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.DialogSendUpdatetoAttendees;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.PageCalendar;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.PageCalendar.Locators;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew.Field;

@SuppressWarnings("unused")
public class AddEquipment extends CalendarWorkWeekTest {	
	
	public AddEquipment() {
		logger.info("New "+ AddEquipment.class.getCanonicalName());
		
	}
	
	@Bugs(ids = "77711")
	@DataProvider(name = "DataProviderShortcutKeys")
	public Object[][] DataProviderShortcutKeys() {
		return new Object[][] {
				new Object[] { "VK_ENTER", KeyEvent.VK_ENTER },
	//			new Object[] { "VK_TAB", KeyEvent.VK_TAB },
		};
	}
	@Test(description = "Add equipment from scheduler pane using keyboard Enter and Tab key",
			groups = { "sanity" },
			dataProvider = "DataProviderShortcutKeys")
	public void AddEquipment_01(String name, int keyEvent) throws HarnessException {
		
		// Create a meeting
		AppointmentItem appt = new AppointmentItem();
		ZimbraResource equipment = new ZimbraResource(ZimbraResource.Type.EQUIPMENT);
			
		String tz = ZTimeZone.TimeZoneEST.getID();
		String apptSubject = ZimbraSeleniumProperties.getUniqueString();
		String apptEquipment = equipment.EmailAddress;
		
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
                     	"</inv>" +
                     	"<e a='"+ ZimbraAccount.AccountA().EmailAddress +"' t='t'/>" +
                     	"<mp content-type='text/plain'>" +
                     		"<content>"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
                     	"</mp>" +
                     "<su>"+ apptSubject +"</su>" +
                     "</m>" +
               "</CreateAppointmentRequest>");
        app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
        
        // Add equipment using scheduler and send the appointment
        FormApptNew apptForm = (FormApptNew)app.zPageCalendar.zListItem(Action.A_DOUBLECLICK, apptSubject);
        apptForm.zAddEquipmentFromScheduler(apptEquipment);
		app.zPageCalendar.zKeyboard.zTypeKeyEvent(keyEvent);
        ZAssert.assertTrue(apptForm.zVerifyEquipment(apptEquipment), "Verify equipment bubble after adding equipment from scheduler");
        apptForm.zToolbarPressButton(Button.B_SEND);
        SleepUtil.sleepLong(); // test fails while checking location free/busy status, waitForPostqueue is not sufficient here
        // Tried sleepMedium() as well but although fails so using sleepLong()
 
        // Verify that equipment present in the appointment
        AppointmentItem actual = AppointmentItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ apptSubject +")");
		ZAssert.assertEquals(actual.getSubject(), apptSubject, "Subject: Verify the appointment data");
		ZAssert.assertStringContains(actual.getEquipment(), apptEquipment, "Equipment: Verify the appointment data");
		
		// Verify equipment free/busy status
		String equipmentStatus = app.zGetActiveAccount().soapSelectValue("//mail:at[@a='"+ apptEquipment +"']", "ptst");
		ZAssert.assertEquals(equipmentStatus, "AC", "Verify equipment free/busy status");
		
	}
	
}
