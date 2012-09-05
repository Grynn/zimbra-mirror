package com.zimbra.qa.selenium.projects.ajax.tests.calendar.appointments.views.day.singleday;

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
	
	@Test(description = "Untag an appointment using toolbar button in day view",
			groups = { "sanity" })
	public void UnTagAppointment_02() throws HarnessException {
		
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
	
	@Test(description = "Create new tag using toolbar button and apply same tag to appointment using toolbar in day view",
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
	public void TagAppointment_05() throws HarnessException {
		
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
	
	
	@Test(description = "Untag tagged appointment using context menu in day view",
			groups = { "functional" })
	public void UnTagAppointment_06() throws HarnessException, AWTException {
		
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
        
        // Selenium doesn't select latest sub menu and clicks to wrong hidden menu so test fails.
        // Adding work around to refresh browser (which is not ideal work around) but application is not doing anything wrong
        // Manually everything works fine though.
        // Running this test individually also works fine because it doesn't find duplicate sub menu
        Robot Robot = new Robot();
        Robot.keyPress(KeyEvent.VK_F5); Robot.keyRelease(KeyEvent.VK_F5);
        SleepUtil.sleepVeryLong();
        Robot.keyPress(KeyEvent.VK_G); Robot.keyPress(KeyEvent.VK_C);
        Robot.keyRelease(KeyEvent.VK_G); Robot.keyRelease(KeyEvent.VK_C);
        SleepUtil.sleepLong();
        
		// Remove tag from appointment using context menu
        app.zPageCalendar.zListItem(Action.A_RIGHTCLICK, apptSubject);
        app.zPageCalendar.sMouseOver(PageCalendar.Locators.TagAppointmentMenu);
        SleepUtil.sleepSmall();
        app.zPageCalendar.zToolbarPressButton(Button.O_TAG_APPOINTMENT_REMOVE_TAG_SUB_MENU);
        SleepUtil.sleepSmall(); // give time to soap verification because it runs fast and test fails
        
        // Verify appointment is not tagged
        app.zGetActiveAccount().soapSend("<GetAppointmentRequest xmlns='urn:zimbraMail' id='" + apptId + "'/>");
        ZAssert.assertEquals(app.zGetActiveAccount().soapSelectValue("//mail:appt", "t"), null, "Verify appointment is not tagged");
        
	}
	
	@Test(description = "Apply multiple tags to appointment using toolbar button in day view and remove all tags in day view",
			groups = { "functional" })
	public void MultipleTagAppointment_07() throws HarnessException {
		
		// Create objects
		String tz, apptSubject, apptBody, tag1, tag2, tagID1, tagID2;
		TagItem getTag1, getTag2;
		tz = ZTimeZone.TimeZoneEST.getID();
		tag1 = ZimbraSeleniumProperties.getUniqueString();
		tag2 = ZimbraSeleniumProperties.getUniqueString();
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		apptBody = ZimbraSeleniumProperties.getUniqueString();
		
		// Create new appointment
		AppointmentItem appt = AppointmentItem.createAppointmentSingleDay(app.zGetActiveAccount(), Calendar.getInstance(), 120, null, apptSubject, apptBody, null, null);
        String apptId = appt.dApptID;
        
        // Create new tags and get tag IDs
		app.zPageCalendar.zCreateTag(app, tag1, 5);
		app.zPageCalendar.zCreateTag(app, tag2, 6);
		getTag1 = app.zPageCalendar.zGetTagItem(app.zGetActiveAccount(), tag1);
		getTag2 = app.zPageCalendar.zGetTagItem(app.zGetActiveAccount(), tag2);
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
		app.zGetActiveAccount().soapSend("<GetTagRequest xmlns='urn:zimbraMail'/>");;
		tagID1  = app.zGetActiveAccount().soapSelectValue("//mail:GetTagResponse//mail:tag[@name='"+ tag1 +"']", "id");
		tagID2 = app.zGetActiveAccount().soapSelectValue("//mail:GetTagResponse//mail:tag[@name='"+ tag2 +"']", "id");
		
		// Apply multiple tags to appointment
		app.zGetActiveAccount().soapSend("<ItemActionRequest xmlns='urn:zimbraMail'>" + "<action id='" + apptId +"' op='tag' tn='"+ tag1 +"'/>" + "</ItemActionRequest>");
		app.zGetActiveAccount().soapSend("<ItemActionRequest xmlns='urn:zimbraMail'>" + "<action id='" + apptId +"' op='tag' tn='"+ tag2 +"'/>" + "</ItemActionRequest>");
        SleepUtil.sleepSmall();
        
        // Verify applied tags        
        app.zGetActiveAccount().soapSend("<GetAppointmentRequest xmlns='urn:zimbraMail' id='" + apptId + "'/>");
        ZAssert.assertEquals(app.zGetActiveAccount().soapSelectValue("//mail:appt", "t"), tagID1 + "," + tagID2, "Verify the appointment is tagged with the correct tag");
		
		// Verify search result from UI
		app.zTreeCalendar.zTreeItem(Action.A_LEFTCLICK, tag1);
		ZAssert.assertEquals(app.zPageCalendar.sIsElementPresent(app.zPageCalendar.zGetApptLocator(apptSubject)), true, "Verify search result after clicking to tag1");
		app.zTreeCalendar.zTreeItem(Action.A_LEFTCLICK, tag2);
		ZAssert.assertEquals(app.zPageCalendar.sIsElementPresent(app.zPageCalendar.zGetApptLocator(apptSubject)), true, "Verify search result after clicking to tag2");
	}
	
	@Test(description = "Apply tag to appointment and rename tag name in day view",
			groups = { "functional" })
	public void RenameTagAppointment_08() throws HarnessException {
		
		// Create objects
		String tz, apptSubject, apptBody, tag1, renameTag1, tagID, renameTagID;
		TagItem tag;
		tz = ZTimeZone.TimeZoneEST.getID();
		tag1 = ZimbraSeleniumProperties.getUniqueString();
		renameTag1 = ZimbraSeleniumProperties.getUniqueString();
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		apptBody = ZimbraSeleniumProperties.getUniqueString();
		
		// Create new appointment
		AppointmentItem appt = AppointmentItem.createAppointmentSingleDay(app.zGetActiveAccount(), Calendar.getInstance(), 120, null, apptSubject, apptBody, null, null);
        String apptId = appt.dApptID;
        
        // Create new tag and get tag ID
        app.zPageCalendar.zCreateTag(app, tag1, 7);
		tag = app.zPageCalendar.zGetTagItem(app.zGetActiveAccount(), tag1);
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
		app.zGetActiveAccount().soapSend("<GetTagRequest xmlns='urn:zimbraMail'/>");;
		tagID = app.zGetActiveAccount().soapSelectValue("//mail:GetTagResponse//mail:tag[@name='"+ tag1 +"']", "id");
		
		// Apply tag to appointment
		app.zGetActiveAccount().soapSend("<ItemActionRequest xmlns='urn:zimbraMail'>" + "<action id='" + apptId +"' op='tag' tn='"+ tag1 +"'/>" + "</ItemActionRequest>");
        
        // Rename the tag using the context menu
		DialogRenameTag dialog = (DialogRenameTag) app.zTreeCalendar.zTreeItem(
				Action.A_RIGHTCLICK, Button.B_RENAME, tag);
		dialog.zSetNewName(renameTag1);
		dialog.zClickButton(Button.B_OK);
        app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
        
        // Verify applied tag for appointment
        app.zGetActiveAccount().soapSend("<GetAppointmentRequest xmlns='urn:zimbraMail' id='" + apptId + "'/>");
        ZAssert.assertEquals(app.zGetActiveAccount().soapSelectValue("//mail:appt", "t"), tagID, "Verify the appointment is tagged with the correct tag");
		
	}
	
	@Bugs(ids = "75711")
	@Test(description = "Apply tag to appointment and delete same tag in day view",
			groups = { "functional" })
	public void DeleteTagAppointment_09() throws HarnessException {
		
		// Create objects
		String tz, apptSubject, apptBody, tag1, renameTag1, tagID;
		TagItem tag;
		tz = ZTimeZone.TimeZoneEST.getID();
		tag1 = ZimbraSeleniumProperties.getUniqueString();
		renameTag1 = ZimbraSeleniumProperties.getUniqueString();
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		apptBody = ZimbraSeleniumProperties.getUniqueString();
		
		// Create new appointment
		AppointmentItem appt = AppointmentItem.createAppointmentSingleDay(app.zGetActiveAccount(), Calendar.getInstance(), 120, null, apptSubject, apptBody, null, null);
        String apptId = appt.dApptID;

        // Create new tag and get tag ID
        app.zPageCalendar.zCreateTag(app, tag1, 8);
		tag = app.zPageCalendar.zGetTagItem(app.zGetActiveAccount(), tag1);
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
		app.zGetActiveAccount().soapSend("<GetTagRequest xmlns='urn:zimbraMail'/>");;
		tagID = app.zGetActiveAccount().soapSelectValue("//mail:GetTagResponse//mail:tag[@name='"+ tag1 +"']", "id");
		
		// Apply tag to appointment
		app.zGetActiveAccount().soapSend("<ItemActionRequest xmlns='urn:zimbraMail'>" + "<action id='" + apptId +"' op='tag' tn='"+ tag1 +"'/>" + "</ItemActionRequest>");
        
        // Delete the tag using the context menu
		DialogDeleteTag dialog = (DialogDeleteTag) app.zTreeCalendar.zTreeItem(
				Action.A_RIGHTCLICK, Button.B_DELETE, tag);
		dialog.zClickButton(Button.B_YES);
        app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
        
        // Verify appointment is not tagged
        app.zGetActiveAccount().soapSend("<GetAppointmentRequest xmlns='urn:zimbraMail' id='" + apptId + "'/>");
        ZAssert.assertEquals(app.zGetActiveAccount().soapSelectValue("//mail:appt", "t"), null, "Verify appointment is not tagged");
		
	}
	
	@Test(description = "Apply tag to appointment and delete tagged appointment in day view",
			groups = { "functional" })
	public void DeleteTaggedAppointment_10() throws HarnessException {
		
		// Create objects
		String tz, apptSubject, apptBody, tag1, renameTag1, tagID;
		TagItem tag;
		tz = ZTimeZone.TimeZoneEST.getID();
		tag1 = ZimbraSeleniumProperties.getUniqueString();
		renameTag1 = ZimbraSeleniumProperties.getUniqueString();
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		apptBody = ZimbraSeleniumProperties.getUniqueString();
		
		// Create new appointment
		AppointmentItem appt = AppointmentItem.createAppointmentSingleDay(app.zGetActiveAccount(), Calendar.getInstance(), 120, null, apptSubject, apptBody, null, null);
        String apptId = appt.dApptID;

        // Create new tag and get tag ID
        app.zPageCalendar.zCreateTag(app, tag1, 8);
		tag = app.zPageCalendar.zGetTagItem(app.zGetActiveAccount(), tag1);
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
		app.zGetActiveAccount().soapSend("<GetTagRequest xmlns='urn:zimbraMail'/>");;
		tagID = app.zGetActiveAccount().soapSelectValue("//mail:GetTagResponse//mail:tag[@name='"+ tag1 +"']", "id");
		
		// Apply tag to appointment
		app.zGetActiveAccount().soapSend("<ItemActionRequest xmlns='urn:zimbraMail'>" + "<action id='" + apptId +"' op='tag' tn='"+ tag1 +"'/>" + "</ItemActionRequest>");

		// Right click to appointment and delete it
        app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
        app.zPageCalendar.zListItem(Action.A_LEFTCLICK, apptSubject);
        DialogConfirmDeleteAppointment dlgConfirm = (DialogConfirmDeleteAppointment)app.zPageCalendar.zToolbarPressButton(Button.B_DELETE);
		dlgConfirm.zClickButton(Button.B_YES);
		SleepUtil.sleepMedium(); //testcase fails here due to timing issue so added sleep
		ZAssert.assertEquals(app.zPageCalendar.sIsElementPresent(app.zPageCalendar.zGetApptLocator(apptSubject)), false, "Verify appointment is deleted");
		
	}
}
