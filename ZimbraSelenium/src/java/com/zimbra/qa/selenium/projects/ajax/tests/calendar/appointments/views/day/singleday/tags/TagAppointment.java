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
public class TagAppointment extends AjaxCommonTest {

	public TagAppointment() {
		logger.info("New "+ TagAppointment.class.getCanonicalName());

		// All tests start at the Calendar page
		super.startingPage = app.zPageCalendar;

		super.startingAccountPreferences = new HashMap<String, String>() {
			private static final long serialVersionUID = -2913827779459595178L;
		{
		    put("zimbraPrefCalendarInitialView", "day");
		}};
	}

	
	@Test(description = "Tag an appointment using toolbar button in day view",
			groups = { "smoke" })
	public void TagAppointment_01() throws HarnessException {
		
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
		app.zPageCalendar.zCreateTag(app, tag1, 1);
		tag = app.zPageCalendar.zGetTagItem(app.zGetActiveAccount(), tag1);
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
		app.zGetActiveAccount().soapSend("<GetTagRequest xmlns='urn:zimbraMail'/>");;
		tagID = app.zGetActiveAccount().soapSelectValue("//mail:GetTagResponse//mail:tag[@name='"+ tag1 +"']", "id");
		
		// Apply tag to appointment using toolbar button
        app.zPageCalendar.zListItem(Action.A_LEFTCLICK, apptSubject);
        app.zPageCalendar.zToolbarPressButton(Button.O_LISTVIEW_TAG);
        app.zPageCalendar.zTagListView(tag1);
        
        // Verify applied tag for appointment
        app.zGetActiveAccount().soapSend("<GetAppointmentRequest xmlns='urn:zimbraMail' id='" + apptId + "'/>");
        ZAssert.assertEquals(app.zGetActiveAccount().soapSelectValue("//mail:appt", "t"), tagID, "Verify the appointment is tagged with the correct tag");

	}
	
	@Test(description = "Create new tag using toolbar button and apply same tag to appointment using toolbar in day view",
			groups = { "functional" })
	public void TagAppointment_02() throws HarnessException {
		
		// Create objects
		String apptSubject, apptBody, tag1, tagID;
		TagItem tag;
		tag1 = ZimbraSeleniumProperties.getUniqueString();
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		apptBody = ZimbraSeleniumProperties.getUniqueString();
		
		// Create new appointment
		AppointmentItem appt = AppointmentItem.createAppointmentSingleDay(app.zGetActiveAccount(), Calendar.getInstance(), 120, null, apptSubject, apptBody, null, null);
        String apptId = appt.dApptID;
        app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
        
		// Create tag using toolbar Tag -> New Tag using toolbar button
        app.zPageCalendar.zListItem(Action.A_LEFTCLICK, apptSubject);
        app.zPageCalendar.zToolbarPressButton(Button.O_LISTVIEW_TAG);
        app.zPageCalendar.zToolbarPressButton(Button.O_LISTVIEW_NEWTAG);
        SleepUtil.sleepSmall();
        
        DialogTag dialog = new DialogTag(app, startingPage);
        dialog.zSubmit(tag1);
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
		
		// Get tag ID
		app.zGetActiveAccount().soapSend("<GetTagRequest xmlns='urn:zimbraMail'/>");;
		tagID = app.zGetActiveAccount().soapSelectValue("//mail:GetTagResponse//mail:tag[@name='"+ tag1 +"']", "id");
		
		// Verify applied tag for appointment
		app.zGetActiveAccount().soapSend("<GetAppointmentRequest xmlns='urn:zimbraMail' id='" + apptId + "'/>");
        ZAssert.assertEquals(app.zGetActiveAccount().soapSelectValue("//mail:appt", "t"), tagID, "Verify the appointment is tagged with the correct tag");
		
	}
	
	@Test(description = "Apply existing tag to appointment using context menu in day view",
			groups = { "functional" })
	public void TagAppointment_03() throws HarnessException {
		
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
		app.zPageCalendar.zCreateTag(app, tag1, 3);
		tag = app.zPageCalendar.zGetTagItem(app.zGetActiveAccount(), tag1);
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
		app.zGetActiveAccount().soapSend("<GetTagRequest xmlns='urn:zimbraMail'/>");;
		tagID = app.zGetActiveAccount().soapSelectValue("//mail:GetTagResponse//mail:tag[@name='"+ tag1 +"']", "id");
		
		// Apply tag to appointment using context menu
        app.zPageCalendar.zListItem(Action.A_RIGHTCLICK, apptSubject);
        app.zPageCalendar.sMouseOver(PageCalendar.Locators.TagAppointmentMenu);
        SleepUtil.sleepSmall();
        app.zPageCalendar.zTagContextMenuListView(tag1);
        
        // Verify applied tag for appointment
        app.zGetActiveAccount().soapSend("<GetAppointmentRequest xmlns='urn:zimbraMail' id='" + apptId + "'/>");
        ZAssert.assertEquals(app.zGetActiveAccount().soapSelectValue("//mail:appt", "t"), tagID, "Verify the appointment is tagged with the correct tag");
        
	}
	
	@Test(description = "Create new tag using context menu and apply same tag to appointment using context menu in day view",
			groups = { "functional" })
	public void TagAppointment_04() throws HarnessException {
		
		// Create objects
		String apptSubject, apptBody, tag1, tagID;
		TagItem tag;
		tag1 = ZimbraSeleniumProperties.getUniqueString();
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		apptBody = ZimbraSeleniumProperties.getUniqueString();
		
		// Create new appointment
		AppointmentItem appt = AppointmentItem.createAppointmentSingleDay(app.zGetActiveAccount(), Calendar.getInstance(), 120, null, apptSubject, apptBody, null, null);
        String apptId = appt.dApptID;
        app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
        
        // Create new tag using context menu and apply it to appointment
		app.zPageCalendar.zListItem(Action.A_RIGHTCLICK, apptSubject);
        app.zPageCalendar.sMouseOver(PageCalendar.Locators.TagAppointmentMenu);
		SleepUtil.sleepSmall();
        app.zPageCalendar.zToolbarPressButton(Button.O_TAG_APPOINTMENT_NEW_TAG_SUB_MENU);

        DialogTag dialog = new DialogTag(app, startingPage);
        dialog.zSubmit(tag1);
        app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
        SleepUtil.sleepSmall();
        
        // Get tag ID
		app.zGetActiveAccount().soapSend("<GetTagRequest xmlns='urn:zimbraMail'/>");;
		tagID = app.zGetActiveAccount().soapSelectValue("//mail:GetTagResponse//mail:tag[@name='"+ tag1 +"']", "id");
        
		// Verify applied tag for appointment
		app.zGetActiveAccount().soapSend("<GetAppointmentRequest xmlns='urn:zimbraMail' id='" + apptId + "'/>");
        ZAssert.assertEquals(app.zGetActiveAccount().soapSelectValue("//mail:appt", "t"), tagID, "Verify the appointment is tagged with the correct tag");
		
	}
}
