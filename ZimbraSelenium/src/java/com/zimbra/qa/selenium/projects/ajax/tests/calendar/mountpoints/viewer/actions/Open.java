package com.zimbra.qa.selenium.projects.ajax.tests.calendar.mountpoints.viewer.actions;

import java.util.Calendar;
import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.CalendarWorkWeekTest;

public class Open extends CalendarWorkWeekTest {

	public Open() {
		logger.info("New "+ Open.class.getCanonicalName());
		super.startingPage = app.zPageCalendar;
	}
	
	@Test(description = "Grantee opens appointment from grantor's calendar and close it without making any changes",
			groups = { "functional" })
			
	public void Open_01() throws HarnessException {
		
		String apptSubject = "appointment" + ZimbraSeleniumProperties.getUniqueString();
		String apptContent = ZimbraSeleniumProperties.getUniqueString();
		String foldername = "folder" + ZimbraSeleniumProperties.getUniqueString();
		String mountpointname = "mountpoint" + ZimbraSeleniumProperties.getUniqueString();
		
		Calendar now = this.calendarWeekDayUTC;
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);
		
		FolderItem calendarFolder = FolderItem.importFromSOAP(ZimbraAccount.AccountA(), FolderItem.SystemFolder.Calendar);
		
		// Create a folder to share
		ZimbraAccount.AccountA().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+		"<folder name='" + foldername + "' l='" + calendarFolder.getId() + "'/>"
				+	"</CreateFolderRequest>");
		
		FolderItem folder = FolderItem.importFromSOAP(ZimbraAccount.AccountA(), foldername);
		
		// Share it
		ZimbraAccount.AccountA().soapSend(
					"<FolderActionRequest xmlns='urn:zimbraMail'>"
				+		"<action id='"+ folder.getId() +"' op='grant'>"
				+			"<grant d='"+ app.zGetActiveAccount().EmailAddress +"' gt='usr' perm='r' view='appointment'/>"
				+		"</action>"
				+	"</FolderActionRequest>");
		
		// Mount it
		app.zGetActiveAccount().soapSend(
					"<CreateMountpointRequest xmlns='urn:zimbraMail'>"
				+		"<link l='1' name='"+ mountpointname +"'  rid='"+ folder.getId() +"' zid='"+ ZimbraAccount.AccountA().ZimbraId +"' view='appointment' color='5'/>"
				+	"</CreateMountpointRequest>");
		
		// Create appointment
		ZimbraAccount.AccountA().soapSend(
				"<CreateAppointmentRequest xmlns='urn:zimbraMail'>"
				+		"<m l='"+ folder.getId() +"' >"
				+			"<inv method='REQUEST' type='event' status='CONF' draft='0' class='PUB' fb='B' transp='O' allDay='0' name='"+ apptSubject +"'>"
				+				"<s d='"+ startUTC.toTimeZone(ZTimeZone.TimeZoneEST.getID()).toYYYYMMDDTHHMMSS() +"' tz='"+ ZTimeZone.TimeZoneEST.getID() +"'/>"
				+				"<e d='"+ endUTC.toTimeZone(ZTimeZone.TimeZoneEST.getID()).toYYYYMMDDTHHMMSS() +"' tz='"+ ZTimeZone.TimeZoneEST.getID() +"'/>"
				+				"<or a='"+ ZimbraAccount.AccountA().EmailAddress +"'/>"
				+				"<at role='REQ' ptst='NE' rsvp='1' a='" + app.zGetActiveAccount().EmailAddress + "'/>"
				+			"</inv>"
				+			"<e a='"+ app.zGetActiveAccount().EmailAddress +"' t='t'/>"
				+			"<su>"+ apptSubject +"</su>"
				+			"<mp content-type='text/plain'>"
				+				"<content>" + apptContent + "</content>"
				+			"</mp>"
				+		"</m>"
				+	"</CreateAppointmentRequest>");
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
		
		// Mark ON to mounted calendar folder and select the appointment
		app.zTreeCalendar.zDeSelectCalendarFolder("Calendar");
		app.zTreeCalendar.zSelectMountedFolder(mountpointname);
		
		// Open appointment
		app.zPageCalendar.zListItem(Action.A_RIGHTCLICK, Button.O_OPEN_MENU, apptSubject);
        ZAssert.assertEquals(app.zPageCalendar.zGetApptSubjectFromReadOnlyAppt(), apptSubject, "Verify appointment subject from read only appointment UI");
        ZAssert.assertEquals(app.zPageCalendar.zGetApptBodyFromReadOnlyAppt(), apptContent, "Verify appointment body from read only appointment UI");
        app.zPageCalendar.zToolbarPressButton(Button.B_CLOSE);
        
	}

}
