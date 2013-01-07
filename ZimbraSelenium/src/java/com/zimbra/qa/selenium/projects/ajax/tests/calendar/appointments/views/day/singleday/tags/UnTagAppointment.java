package com.zimbra.qa.selenium.projects.ajax.tests.calendar.appointments.views.day.singleday.tags;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.Calendar;
import java.util.HashMap;
import org.testng.annotations.Test;
import com.zimbra.client.ZInvite.ZRole;
import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.AppointmentItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.TagItem;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogDeleteTag;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogRenameTag;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogTag;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogWarning;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.DialogConfirmDeleteAppointment;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.PageCalendar;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew.Locators;

@SuppressWarnings("unused")
public class UnTagAppointment extends AjaxCommonTest {

	public UnTagAppointment() {
		logger.info("New "+ UnTagAppointment.class.getCanonicalName());

		// All tests start at the Calendar page
		super.startingPage = app.zPageCalendar;

		super.startingAccountPreferences = new HashMap<String, String>() {
			private static final long serialVersionUID = -2913827779459595178L;
		{
		    put("zimbraPrefCalendarInitialView", "day");
		}};
	}

	
	@Test(description = "Untag an appointment using toolbar button in day view",
			groups = { "functional" })
	public void UnTagAppointment_01() throws HarnessException {
		
		// Create objects
		String apptSubject, apptBody, tag1, tagID;
		TagItem tag;
		tag1 = ZimbraSeleniumProperties.getUniqueString();
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		apptBody = ZimbraSeleniumProperties.getUniqueString();
		
		// Create new appointment
		AppointmentItem appt = AppointmentItem.createAppointmentSingleDay(app.zGetActiveAccount(), Calendar.getInstance(), 120, null, apptSubject, apptBody, null, null);
        String apptId = appt.dApptID;

        // Create new tag and get tag ID   
		app.zPageCalendar.zCreateTag(app, tag1, 2);
		tag = app.zPageCalendar.zGetTagItem(app.zGetActiveAccount(), tag1);
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
		app.zGetActiveAccount().soapSend("<GetTagRequest xmlns='urn:zimbraMail'/>");;
		tagID = app.zGetActiveAccount().soapSelectValue("//mail:GetTagResponse//mail:tag[@name='"+ tag1 +"']", "id");
		
		// Remove tag from appointment using toolbar button		
		app.zGetActiveAccount().soapSend("<ItemActionRequest xmlns='urn:zimbraMail'>" + "<action id='" + apptId +"' op='tag' tn='"+ tag1 +"'/>" + "</ItemActionRequest>");
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
        app.zPageCalendar.zListItem(Action.A_LEFTCLICK, apptSubject);
        app.zPageCalendar.zToolbarPressButton(Button.O_LISTVIEW_TAG);
        app.zPageCalendar.zToolbarPressButton(Button.O_LISTVIEW_REMOVETAG);
        SleepUtil.sleepSmall();
        
        // Verify appointment is not tagged
        app.zGetActiveAccount().soapSend("<GetAppointmentRequest xmlns='urn:zimbraMail' id='" + apptId + "'/>");
        ZAssert.assertEquals(app.zGetActiveAccount().soapSelectValue("//mail:appt", "t"), null, "Verify appointment is not tagged");
		
		// Verify search result from UI
		app.zTreeCalendar.zTreeItem(Action.A_LEFTCLICK, tag1);
		SleepUtil.sleepMedium();
		ZAssert.assertEquals(app.zPageCalendar.sIsElementPresent(app.zPageCalendar.zGetApptLocator(apptSubject)), false, "Verify search result after clicking tag");
	}
	
	@Test(description = "Untag tagged appointment using context menu in day view",
			groups = { "functional" })
	public void UnTagAppointment_02() throws HarnessException, AWTException {
		
		// Create objects
		String apptSubject, apptBody, tag1, tagID;
		TagItem tag;
		tag1 = ZimbraSeleniumProperties.getUniqueString();
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		apptBody = ZimbraSeleniumProperties.getUniqueString();
		
		// Create new appointment
		AppointmentItem appt = AppointmentItem.createAppointmentSingleDay(app.zGetActiveAccount(), Calendar.getInstance(), 120, null, apptSubject, apptBody, null, null);
        String apptId = appt.dApptID;
        
        // Create new tag and get tag ID      
		app.zPageCalendar.zCreateTag(app, tag1, 4);
		tag = app.zPageCalendar.zGetTagItem(app.zGetActiveAccount(), tag1);
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
		app.zGetActiveAccount().soapSend("<GetTagRequest xmlns='urn:zimbraMail'/>");;
		tagID = app.zGetActiveAccount().soapSelectValue("//mail:GetTagResponse//mail:tag[@name='"+ tag1 +"']", "id");
		app.zGetActiveAccount().soapSend("<ItemActionRequest xmlns='urn:zimbraMail'>" + "<action id='" + apptId +"' op='tag' tn='"+ tag1 +"'/>" + "</ItemActionRequest>");
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
		SleepUtil.sleepMedium();
		
		// Remove tag from appointment using context menu
        app.zPageCalendar.zListItem(Action.A_RIGHTCLICK, apptSubject);
        app.zPageCalendar.zMouseOver(Button.B_TAGAPPOINTMENTMENU);
        SleepUtil.sleepSmall(); //reqires to select sub menu otherwise test fails here
        app.zPageCalendar.zToolbarPressButton(Button.O_TAG_APPOINTMENT_REMOVE_TAG_SUB_MENU);
        SleepUtil.sleepSmall(); // give time to soap verification because it runs fast and test fails
        
        // Verify appointment is not tagged
        app.zGetActiveAccount().soapSend("<GetAppointmentRequest xmlns='urn:zimbraMail' id='" + apptId + "'/>");
        ZAssert.assertEquals(app.zGetActiveAccount().soapSelectValue("//mail:appt", "t"), null, "Verify appointment is not tagged");
        
	}
}
