package com.zimbra.qa.selenium.projects.ajax.tests.calendar.appointments.views.day.singleday;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.Calendar;
import java.util.HashMap;
import org.testng.annotations.Test;

import com.zimbra.client.ZInvite.ZRole;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.TagItem;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogRenameTag;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogTag;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogWarning;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.PageCalendar;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew.Locators;

@SuppressWarnings("unused")
public class TagAppointment extends AjaxCommonTest {

	public TagAppointment() {
		logger.info("New "+ TagAppointment.class.getCanonicalName());

		// All tests start at the Calendar page
		super.startingPage = app.zPageCalendar;

		// Make sure we are using an account with message view
		super.startingAccountPreferences = new HashMap<String, String>() {
			private static final long serialVersionUID = -2913827779459595178L;
		{
		    put("zimbraPrefCalendarInitialView", "day");
		}};
	}

	
	@Test(description = "Tag an appointment using toolbar button in day view",
			groups = { "smoke" })
	public void TagAppointment_01() throws HarnessException {
		
		// Creating objects for appointment data
		String tz, apptSubject, apptBody;
		tz = ZTimeZone.TimeZoneEST.getID();
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		apptBody = ZimbraSeleniumProperties.getUniqueString();
		
		// Absolute dates in UTC zone
		Calendar now = Calendar.getInstance();
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);
		
        app.zGetActiveAccount().soapSend(
    			"<CreateAppointmentRequest xmlns='urn:zimbraMail'>"
    		+		"<m>"
    		+			"<inv method='REQUEST' type='event' fb='B' transp='O' allDay='0' name='"+ apptSubject +"'>"
    		+				"<s d='"+ startUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>"
    		+				"<e d='"+ endUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>"
    		+				"<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" 
    		+			"</inv>" 
    		+			"<mp content-type='text/plain'>" 
    		+				"<content>" + apptBody + "</content>" 
    		+			"</mp>"
    		+			"<su>" + apptSubject + "</su>" 
    		+		"</m>" 
    		+	"</CreateAppointmentRequest>");

        String apptId = app.zGetActiveAccount().soapSelectValue("//mail:CreateAppointmentResponse", "apptId");
        String msgId = Integer.parseInt(apptId) + "-" + (Integer.parseInt(apptId)-1);
        
        // Create new tag
		String tag1 = ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
				"<CreateTagRequest xmlns='urn:zimbraMail'>" + 
					"<tag name='" + tag1 + "' color='1' />" + 
				"</CreateTagRequest>");
		TagItem tag = app.zPageCalendar.zGetTagItem(app.zGetActiveAccount(), tag1);
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
		
		// Get tag ID
		app.zGetActiveAccount().soapSend("<GetTagRequest xmlns='urn:zimbraMail'/>");;
		String tagID = app.zGetActiveAccount().soapSelectValue("//mail:GetTagResponse//mail:tag[@name='"+ tag1 +"']", "id");
		
		// Apply tag to appointment using toolbar button
        app.zPageCalendar.zListItem(Action.A_LEFTCLICK, apptSubject);
        app.zPageCalendar.zToolbarPressButton(Button.O_LISTVIEW_TAG);
        app.zPageCalendar.zTagListView(tag1);
        
        app.zGetActiveAccount().soapSend("<GetAppointmentRequest xmlns='urn:zimbraMail'> <m id='" + msgId + "'> </m> </GetAppointmentRequest>");
        app.zGetActiveAccount().soapSend("<GetMsgRequest xmlns='urn:zimbraMail'> <m id='" + msgId + "'> </m> </GetMsgRequest>");
        ZAssert.assertEquals(app.zGetActiveAccount().soapMatch("//mail:GetMsgResponse//mail:m", "tn", tag1), true, "");
        
        // Verify applied tag for appointment
		app.zGetActiveAccount().soapSend("<SearchRequest xmlns='urn:zimbraMail' types='appointment'>"
				+ "<query>" + apptSubject + "</query>" + "</SearchRequest>");
		String id = app.zGetActiveAccount().soapSelectValue("//mail:SearchResponse//mail:appt", "t");
		ZAssert.assertEquals(id, tagID, "Verify applied tag for appointment");
		
		// Verify search result from UI
		app.zTreeCalendar.zTreeItem(Action.A_LEFTCLICK, tag1);
		ZAssert.assertEquals(app.zPageCalendar.sIsElementPresent(app.zPageCalendar.zGetApptLocator(apptSubject)), true, "Verify search result after clicking tag");
	}
	
	@Test(description = "Untag an appointment using toolbar button in day view",
			groups = { "sanity" })
	public void UnTagAppointment_02() throws HarnessException {
		
		// Creating objects for appointment data
		String tz, apptSubject, apptBody;
		tz = ZTimeZone.TimeZoneEST.getID();
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		apptBody = ZimbraSeleniumProperties.getUniqueString();
		
		// Absolute dates in UTC zone
		Calendar now = Calendar.getInstance();
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);
		
        app.zGetActiveAccount().soapSend(
    			"<CreateAppointmentRequest xmlns='urn:zimbraMail'>"
    		+		"<m>"
    		+			"<inv method='REQUEST' type='event' fb='B' transp='O' allDay='0' name='"+ apptSubject +"'>"
    		+				"<s d='"+ startUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>"
    		+				"<e d='"+ endUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>"
    		+				"<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" 
    		+			"</inv>" 
    		+			"<mp content-type='text/plain'>" 
    		+				"<content>" + apptBody + "</content>" 
    		+			"</mp>"
    		+			"<su>" + apptSubject + "</su>" 
    		+		"</m>" 
    		+	"</CreateAppointmentRequest>");

        String apptId = app.zGetActiveAccount().soapSelectValue("//mail:CreateAppointmentResponse", "apptId");
        String msgId = Integer.parseInt(apptId) + "-" + (Integer.parseInt(apptId)-1);
        
        // Create new tag
		String tag1 = ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
				"<CreateTagRequest xmlns='urn:zimbraMail'>" + 
					"<tag name='" + tag1 + "' color='2' />" + 
				"</CreateTagRequest>");
		TagItem tag = app.zPageCalendar.zGetTagItem(app.zGetActiveAccount(), tag1);
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
		
		// Get tag ID
		app.zGetActiveAccount().soapSend("<GetTagRequest xmlns='urn:zimbraMail'/>");;
		String tagID = app.zGetActiveAccount().soapSelectValue("//mail:GetTagResponse//mail:tag[@name='"+ tag1 +"']", "id");
		
		// Apply tag to appointment using toolbar button		
        app.zPageCalendar.zListItem(Action.A_LEFTCLICK, apptSubject);
        app.zPageCalendar.zToolbarPressButton(Button.O_LISTVIEW_TAG);
        app.zPageCalendar.zTagListView(tag1);
        SleepUtil.sleepSmall();
        
        // Remove tag from appointment using toolbar button
        app.zPageCalendar.zListItem(Action.A_LEFTCLICK, apptSubject);
        app.zPageCalendar.zToolbarPressButton(Button.O_LISTVIEW_TAG);
        app.zPageCalendar.zToolbarPressButton(Button.O_LISTVIEW_REMOVETAG);
        
        app.zGetActiveAccount().soapSend("<GetAppointmentRequest xmlns='urn:zimbraMail'> <m id='" + msgId + "'> </m> </GetAppointmentRequest>");
        app.zGetActiveAccount().soapSend("<GetMsgRequest xmlns='urn:zimbraMail'> <m id='" + msgId + "'> </m> </GetMsgRequest>");
        ZAssert.assertEquals(app.zGetActiveAccount().soapMatch("//mail:GetMsgResponse//mail:m", "tn", tag1), false, "");
        
        // Verify removed tag for appointment
		app.zGetActiveAccount().soapSend("<SearchRequest xmlns='urn:zimbraMail' types='appointment'>"
				+ "<query>" + apptSubject + "</query>" + "</SearchRequest>");
		String id = app.zGetActiveAccount().soapSelectValue("//mail:SearchResponse//mail:appt", "t");
		ZAssert.assertEquals(id, "" , "Verify removed tag for appointment");
		
		// Verify search result from UI
		app.zTreeCalendar.zTreeItem(Action.A_LEFTCLICK, tag1);
		SleepUtil.sleepMedium();
		ZAssert.assertEquals(app.zPageCalendar.sIsElementPresent(app.zPageCalendar.zGetApptLocator(apptSubject)), false, "Verify search result after clicking tag");
	}
	
	@Test(description = "Create new tag using toolbar button and apply same tag to appointment using toolbar in day view",
			groups = { "functional" })
	public void TagAppointment_03() throws HarnessException {
		
		// Creating objects for appointment data
		String tz, tag1, apptSubject, apptBody;
		tz = ZTimeZone.TimeZoneEST.getID();
		tag1 = ZimbraSeleniumProperties.getUniqueString();		
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		apptBody = ZimbraSeleniumProperties.getUniqueString();
		
		// Absolute dates in UTC zone
		Calendar now = Calendar.getInstance();
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);
		
        app.zGetActiveAccount().soapSend(
    			"<CreateAppointmentRequest xmlns='urn:zimbraMail'>"
    		+		"<m>"
    		+			"<inv method='REQUEST' type='event' fb='B' transp='O' allDay='0' name='"+ apptSubject +"'>"
    		+				"<s d='"+ startUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>"
    		+				"<e d='"+ endUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>"
    		+				"<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" 
    		+			"</inv>" 
    		+			"<mp content-type='text/plain'>" 
    		+				"<content>" + apptBody + "</content>" 
    		+			"</mp>"
    		+			"<su>" + apptSubject + "</su>" 
    		+		"</m>" 
    		+	"</CreateAppointmentRequest>");

        String apptId = app.zGetActiveAccount().soapSelectValue("//mail:CreateAppointmentResponse", "apptId");
        String msgId = Integer.parseInt(apptId) + "-" + (Integer.parseInt(apptId)-1);
        app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
        
		// Apply tag to appointment using toolbar button
        app.zPageCalendar.zListItem(Action.A_LEFTCLICK, apptSubject);
        app.zPageCalendar.zToolbarPressButton(Button.O_LISTVIEW_TAG);
        app.zPageCalendar.zToolbarPressButton(Button.O_LISTVIEW_NEWTAG);
        SleepUtil.sleepSmall();
        
        // Create new tag
        DialogTag dialog = new DialogTag(app, startingPage);
        dialog.zSubmit(tag1);
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
		
		// Get tag ID
		app.zGetActiveAccount().soapSend("<GetTagRequest xmlns='urn:zimbraMail'/>");;
		String tagID = app.zGetActiveAccount().soapSelectValue("//mail:GetTagResponse//mail:tag[@name='"+ tag1 +"']", "id");
		
        app.zGetActiveAccount().soapSend("<GetAppointmentRequest xmlns='urn:zimbraMail'> <m id='" + msgId + "'> </m> </GetAppointmentRequest>");
        app.zGetActiveAccount().soapSend("<GetMsgRequest xmlns='urn:zimbraMail'> <m id='" + msgId + "'> </m> </GetMsgRequest>");
        ZAssert.assertEquals(app.zGetActiveAccount().soapMatch("//mail:GetMsgResponse//mail:m", "tn", tag1), true, "");
        
        // Verify applied tag for appointment
		app.zGetActiveAccount().soapSend("<SearchRequest xmlns='urn:zimbraMail' types='appointment'>"
				+ "<query>" + apptSubject + "</query>" + "</SearchRequest>");
		String id = app.zGetActiveAccount().soapSelectValue("//mail:SearchResponse//mail:appt", "t");
		ZAssert.assertEquals(id, tagID, "Verify applied tag for appointment");
		
		// Verify search result from UI
		app.zTreeCalendar.zTreeItem(Action.A_LEFTCLICK, tag1);
		ZAssert.assertEquals(app.zPageCalendar.sIsElementPresent(app.zPageCalendar.zGetApptLocator(apptSubject)), true, "Verify search result after clicking tag");
	}
	
	@Test(description = "Apply existing tag to appointment using context menu in day view",
			groups = { "functional" })
	public void TagAppointment_04() throws HarnessException {
		
		// Creating objects for appointment data
		String tz, apptSubject, apptBody;
		tz = ZTimeZone.TimeZoneEST.getID();
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		apptBody = ZimbraSeleniumProperties.getUniqueString();
		
		// Absolute dates in UTC zone
		Calendar now = Calendar.getInstance();
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);
		
        app.zGetActiveAccount().soapSend(
    			"<CreateAppointmentRequest xmlns='urn:zimbraMail'>"
    		+		"<m>"
    		+			"<inv method='REQUEST' type='event' fb='B' transp='O' allDay='0' name='"+ apptSubject +"'>"
    		+				"<s d='"+ startUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>"
    		+				"<e d='"+ endUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>"
    		+				"<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" 
    		+			"</inv>" 
    		+			"<mp content-type='text/plain'>" 
    		+				"<content>" + apptBody + "</content>" 
    		+			"</mp>"
    		+			"<su>" + apptSubject + "</su>" 
    		+		"</m>" 
    		+	"</CreateAppointmentRequest>");

        String apptId = app.zGetActiveAccount().soapSelectValue("//mail:CreateAppointmentResponse", "apptId");
        String msgId = Integer.parseInt(apptId) + "-" + (Integer.parseInt(apptId)-1);
        
        // Create new tag
		String tag1 = ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
				"<CreateTagRequest xmlns='urn:zimbraMail'>" + 
					"<tag name='" + tag1 + "' color='3' />" + 
				"</CreateTagRequest>");
		TagItem tag = app.zPageCalendar.zGetTagItem(app.zGetActiveAccount(), tag1);
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
		
		// Get tag ID
		app.zGetActiveAccount().soapSend("<GetTagRequest xmlns='urn:zimbraMail'/>");;
		String tagID = app.zGetActiveAccount().soapSelectValue("//mail:GetTagResponse//mail:tag[@name='"+ tag1 +"']", "id");
		
		// Apply tag to appointment using toolbar button
        app.zPageCalendar.zListItem(Action.A_RIGHTCLICK, apptSubject);
        app.zPageCalendar.sMouseOver(PageCalendar.Locators.TagAppointmentMenu);
        SleepUtil.sleepSmall();
        app.zPageCalendar.zTagContextMenuListView(tag1);
        
        app.zGetActiveAccount().soapSend("<GetAppointmentRequest xmlns='urn:zimbraMail'> <m id='" + msgId + "'> </m> </GetAppointmentRequest>");
        app.zGetActiveAccount().soapSend("<GetMsgRequest xmlns='urn:zimbraMail'> <m id='" + msgId + "'> </m> </GetMsgRequest>");
        ZAssert.assertEquals(app.zGetActiveAccount().soapMatch("//mail:GetMsgResponse//mail:m", "tn", tag1), true, "");
        
        // Verify applied tag for appointment
		app.zGetActiveAccount().soapSend("<SearchRequest xmlns='urn:zimbraMail' types='appointment'>"
				+ "<query>" + apptSubject + "</query>" + "</SearchRequest>");
		String id = app.zGetActiveAccount().soapSelectValue("//mail:SearchResponse//mail:appt", "t");
		ZAssert.assertEquals(id, tagID, "Verify applied tag for appointment");
		
		// Verify search result from UI
		app.zTreeCalendar.zTreeItem(Action.A_LEFTCLICK, tag1);
		ZAssert.assertEquals(app.zPageCalendar.sIsElementPresent(app.zPageCalendar.zGetApptLocator(apptSubject)), true, "Verify search result after clicking tag");
	}
	
	@Test(description = "Create new tag using context menu and apply same tag to appointment using context menu in day view",
			groups = { "functional" })
	public void TagAppointment_05() throws HarnessException {
		
		// Creating objects for appointment data
		String tz, apptSubject, apptBody;
		tz = ZTimeZone.TimeZoneEST.getID();
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		apptBody = ZimbraSeleniumProperties.getUniqueString();
		
		// Absolute dates in UTC zone
		Calendar now = Calendar.getInstance();
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);
		
        app.zGetActiveAccount().soapSend(
    			"<CreateAppointmentRequest xmlns='urn:zimbraMail'>"
    		+		"<m>"
    		+			"<inv method='REQUEST' type='event' fb='B' transp='O' allDay='0' name='"+ apptSubject +"'>"
    		+				"<s d='"+ startUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>"
    		+				"<e d='"+ endUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>"
    		+				"<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" 
    		+			"</inv>" 
    		+			"<mp content-type='text/plain'>" 
    		+				"<content>" + apptBody + "</content>" 
    		+			"</mp>"
    		+			"<su>" + apptSubject + "</su>" 
    		+		"</m>" 
    		+	"</CreateAppointmentRequest>");

        String apptId = app.zGetActiveAccount().soapSelectValue("//mail:CreateAppointmentResponse", "apptId");
        String msgId = Integer.parseInt(apptId) + "-" + (Integer.parseInt(apptId)-1);
        app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
        
        // Create new tag using context menu and apply it to appointment
		String tag1 = ZimbraSeleniumProperties.getUniqueString();
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
		String tagID = app.zGetActiveAccount().soapSelectValue("//mail:GetTagResponse//mail:tag[@name='"+ tag1 +"']", "id");
        
        app.zGetActiveAccount().soapSend("<GetAppointmentRequest xmlns='urn:zimbraMail'> <m id='" + msgId + "'> </m> </GetAppointmentRequest>");
        app.zGetActiveAccount().soapSend("<GetMsgRequest xmlns='urn:zimbraMail'> <m id='" + msgId + "'> </m> </GetMsgRequest>");
        ZAssert.assertEquals(app.zGetActiveAccount().soapMatch("//mail:GetMsgResponse//mail:m", "tn", tag1), true, "");
        
        // Verify applied tag for appointment
		app.zGetActiveAccount().soapSend("<SearchRequest xmlns='urn:zimbraMail' types='appointment'>"
				+ "<query>" + apptSubject + "</query>" + "</SearchRequest>");
		String id = app.zGetActiveAccount().soapSelectValue("//mail:SearchResponse//mail:appt", "t");
		ZAssert.assertEquals(id, tagID , "Verify applied tag for appointment");
		
		// Verify search result from UI
		app.zTreeCalendar.zTreeItem(Action.A_LEFTCLICK, tag1);
		SleepUtil.sleepMedium();
		ZAssert.assertEquals(app.zPageCalendar.sIsElementPresent(app.zPageCalendar.zGetApptLocator(apptSubject)), true, "Verify search result after clicking tag");
	}
	
	
	@Test(description = "Untag tagged appointment using context menu in day view",
			groups = { "functional" })
	public void UnTagAppointment_06() throws HarnessException, AWTException {
		
		// Creating objects for appointment data
		String tz, apptSubject, apptBody;
		tz = ZTimeZone.TimeZoneEST.getID();
		apptSubject = "untag" + ZimbraSeleniumProperties.getUniqueString();
		apptBody = ZimbraSeleniumProperties.getUniqueString();
		
		// Absolute dates in UTC zone
		Calendar now = Calendar.getInstance();
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);
		
        app.zGetActiveAccount().soapSend(
    			"<CreateAppointmentRequest xmlns='urn:zimbraMail'>"
    		+		"<m>"
    		+			"<inv method='REQUEST' type='event' fb='B' transp='O' allDay='0' name='"+ apptSubject +"'>"
    		+				"<s d='"+ startUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>"
    		+				"<e d='"+ endUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>"
    		+				"<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" 
    		+			"</inv>" 
    		+			"<mp content-type='text/plain'>" 
    		+				"<content>" + apptBody + "</content>" 
    		+			"</mp>"
    		+			"<su>" + apptSubject + "</su>" 
    		+		"</m>" 
    		+	"</CreateAppointmentRequest>");

        String apptId = app.zGetActiveAccount().soapSelectValue("//mail:CreateAppointmentResponse", "apptId");
        String msgId = Integer.parseInt(apptId) + "-" + (Integer.parseInt(apptId)-1);
        
        // Create new tag
		String tag1 = ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
				"<CreateTagRequest xmlns='urn:zimbraMail'>" + 
					"<tag name='" + tag1 + "' color='4' />" + 
				"</CreateTagRequest>");
		TagItem tag = app.zPageCalendar.zGetTagItem(app.zGetActiveAccount(), tag1);
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
		
		// Get tag ID
		app.zGetActiveAccount().soapSend("<GetTagRequest xmlns='urn:zimbraMail'/>");;
		String tagID = app.zGetActiveAccount().soapSelectValue("//mail:GetTagResponse//mail:tag[@name='"+ tag1 +"']", "id");
		
		// Apply tag to appointment using context menu
		app.zPageCalendar.zListItem(Action.A_RIGHTCLICK, apptSubject);
        app.zPageCalendar.sMouseOver(PageCalendar.Locators.TagAppointmentMenu);
        SleepUtil.sleepSmall();
        app.zPageCalendar.zTagContextMenuListView(tag1);
        SleepUtil.sleepMedium();
        
        // Selenium doesn't select latest sub menu and clicks to wrong hidden menu so test fails.
        // Adding work around to refresh browser (which is not ideal work around) but application is not doing anything wrong
        // Manually everything works fine though.
        // Running this test individually also works fine because it doesn't find duplicate sub menu
        Robot Robot = new Robot();
        Robot.keyPress(KeyEvent.VK_F5); Robot.keyRelease(KeyEvent.VK_F5);
        SleepUtil.sleepLong();
        Robot.keyPress(KeyEvent.VK_G); Robot.keyPress(KeyEvent.VK_C);
        Robot.keyRelease(KeyEvent.VK_G); Robot.keyRelease(KeyEvent.VK_C);
        SleepUtil.sleepMedium();
        
		// Remove tag from appointment using context menu
        app.zPageCalendar.zListItem(Action.A_RIGHTCLICK, apptSubject);
        app.zPageCalendar.sMouseOver(PageCalendar.Locators.TagAppointmentMenu);
        SleepUtil.sleepSmall();
        app.zPageCalendar.zToolbarPressButton(Button.O_TAG_APPOINTMENT_REMOVE_TAG_SUB_MENU);
        app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
        SleepUtil.sleepSmall(); // give time to soap verification because it fails
        
        app.zGetActiveAccount().soapSend("<GetAppointmentRequest xmlns='urn:zimbraMail'> <m id='" + msgId + "'> </m> </GetAppointmentRequest>");
        app.zGetActiveAccount().soapSend("<GetMsgRequest xmlns='urn:zimbraMail'> <m id='" + msgId + "'> </m> </GetMsgRequest>");
        ZAssert.assertEquals(app.zGetActiveAccount().soapMatch("//mail:GetMsgResponse//mail:m", "tn", tag1), false, "");
        
        // Verify removed tag for appointment
		app.zGetActiveAccount().soapSend("<SearchRequest xmlns='urn:zimbraMail' types='appointment'>"
				+ "<query>" + apptSubject + "</query>" + "</SearchRequest>");
		String id = app.zGetActiveAccount().soapSelectValue("//mail:SearchResponse//mail:appt", "t");
		ZAssert.assertEquals(id, "" , "Verify removed tag for appointment");
		
		// Verify search result from UI
		app.zTreeCalendar.zTreeItem(Action.A_LEFTCLICK, tag1);
		SleepUtil.sleepSmall();
		ZAssert.assertEquals(app.zPageCalendar.sIsElementPresent(app.zPageCalendar.zGetApptLocator(apptSubject)), false, "Verify search result after clicking tag");
	}
	
	@Test(description = "Apply multiple tags to appointment using toolbar button in day view and remove all tags in day view",
			groups = { "functional" })
	public void MultipleTagAppointment_07() throws HarnessException {
		
		// Creating objects for appointment data
		String tz, apptSubject, apptBody;
		tz = ZTimeZone.TimeZoneEST.getID();
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		apptBody = ZimbraSeleniumProperties.getUniqueString();
		
		// Absolute dates in UTC zone
		Calendar now = Calendar.getInstance();
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);
		
        app.zGetActiveAccount().soapSend(
    			"<CreateAppointmentRequest xmlns='urn:zimbraMail'>"
    		+		"<m>"
    		+			"<inv method='REQUEST' type='event' fb='B' transp='O' allDay='0' name='"+ apptSubject +"'>"
    		+				"<s d='"+ startUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>"
    		+				"<e d='"+ endUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>"
    		+				"<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" 
    		+			"</inv>" 
    		+			"<mp content-type='text/plain'>" 
    		+				"<content>" + apptBody + "</content>" 
    		+			"</mp>"
    		+			"<su>" + apptSubject + "</su>" 
    		+		"</m>" 
    		+	"</CreateAppointmentRequest>");

        String apptId = app.zGetActiveAccount().soapSelectValue("//mail:CreateAppointmentResponse", "apptId");
        String msgId = Integer.parseInt(apptId) + "-" + (Integer.parseInt(apptId)-1);
        
        // Create new tag
		String tag1 = ZimbraSeleniumProperties.getUniqueString();
		String tag2 = ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
				"<CreateTagRequest xmlns='urn:zimbraMail'>" + 
					"<tag name='" + tag1 + "' color='5' />" + 
				"</CreateTagRequest>");
		app.zGetActiveAccount().soapSend(
				"<CreateTagRequest xmlns='urn:zimbraMail'>" + 
					"<tag name='" + tag2 + "' color='6' />" + 
				"</CreateTagRequest>");
		TagItem getTag1 = app.zPageCalendar.zGetTagItem(app.zGetActiveAccount(), tag1);
		TagItem getTag2 = app.zPageCalendar.zGetTagItem(app.zGetActiveAccount(), tag2);
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
		
		// Get tag ID
		app.zGetActiveAccount().soapSend("<GetTagRequest xmlns='urn:zimbraMail'/>");;
		String tagID1 = app.zGetActiveAccount().soapSelectValue("//mail:GetTagResponse//mail:tag[@name='"+ tag1 +"']", "id");
		String tagID2 = app.zGetActiveAccount().soapSelectValue("//mail:GetTagResponse//mail:tag[@name='"+ tag2 +"']", "id");
		
		// Apply tag to appointment using toolbar button
        app.zPageCalendar.zListItem(Action.A_LEFTCLICK, apptSubject);
        app.zPageCalendar.zToolbarPressButton(Button.O_LISTVIEW_TAG);
        app.zPageCalendar.zTagListView(tag1);
        SleepUtil.sleepSmall();
        
        app.zPageCalendar.zListItem(Action.A_LEFTCLICK, apptSubject);
        app.zPageCalendar.zToolbarPressButton(Button.O_LISTVIEW_TAG);
        app.zPageCalendar.zTagListView(tag2);
        SleepUtil.sleepSmall();
        
        app.zGetActiveAccount().soapSend("<GetAppointmentRequest xmlns='urn:zimbraMail'> <m id='" + msgId + "'> </m> </GetAppointmentRequest>");
        app.zGetActiveAccount().soapSend("<GetMsgRequest xmlns='urn:zimbraMail'> <m id='" + msgId + "'> </m> </GetMsgRequest>");
        ZAssert.assertEquals(app.zGetActiveAccount().soapMatch("//mail:GetMsgResponse//mail:m", "tn", tag1 + "," + tag2), true, "");
        
        // Verify applied tag for appointment
		app.zGetActiveAccount().soapSend("<SearchRequest xmlns='urn:zimbraMail' types='appointment'>"
				+ "<query>" + apptSubject + "</query>" + "</SearchRequest>");
		String id = app.zGetActiveAccount().soapSelectValue("//mail:SearchResponse//mail:appt", "t");
		ZAssert.assertEquals(id, tagID1 + "," + tagID2, "Verify applied tags for appointment");
		
		// Verify search result from UI
		app.zTreeCalendar.zTreeItem(Action.A_LEFTCLICK, tag1);
		ZAssert.assertEquals(app.zPageCalendar.sIsElementPresent(app.zPageCalendar.zGetApptLocator(apptSubject)), true, "Verify search result after clicking tag1");
		app.zTreeCalendar.zTreeItem(Action.A_LEFTCLICK, tag2);
		ZAssert.assertEquals(app.zPageCalendar.sIsElementPresent(app.zPageCalendar.zGetApptLocator(apptSubject)), true, "Verify search result after clicking tag2");
	}
	
	@Test(description = "Apply tag to appointment and rename tag name in day view",
			groups = { "functional" })
	public void RenameTagAppointment_08() throws HarnessException {
		
		// Creating objects for appointment data
		String tz, apptSubject, apptBody, tag1, renameTag1;
		tz = ZTimeZone.TimeZoneEST.getID();
		tag1 = ZimbraSeleniumProperties.getUniqueString();
		renameTag1 = ZimbraSeleniumProperties.getUniqueString();
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		apptBody = ZimbraSeleniumProperties.getUniqueString();
		
		// Absolute dates in UTC zone
		Calendar now = Calendar.getInstance();
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);
		
        app.zGetActiveAccount().soapSend(
    			"<CreateAppointmentRequest xmlns='urn:zimbraMail'>"
    		+		"<m>"
    		+			"<inv method='REQUEST' type='event' fb='B' transp='O' allDay='0' name='"+ apptSubject +"'>"
    		+				"<s d='"+ startUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>"
    		+				"<e d='"+ endUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>"
    		+				"<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" 
    		+			"</inv>" 
    		+			"<mp content-type='text/plain'>" 
    		+				"<content>" + apptBody + "</content>" 
    		+			"</mp>"
    		+			"<su>" + apptSubject + "</su>" 
    		+		"</m>" 
    		+	"</CreateAppointmentRequest>");

        String apptId = app.zGetActiveAccount().soapSelectValue("//mail:CreateAppointmentResponse", "apptId");
        String msgId = Integer.parseInt(apptId) + "-" + (Integer.parseInt(apptId)-1);
        
        // Create new tag
		app.zGetActiveAccount().soapSend(
				"<CreateTagRequest xmlns='urn:zimbraMail'>" + 
					"<tag name='" + tag1 + "' color='7' />" + 
				"</CreateTagRequest>");
		TagItem tag = app.zPageCalendar.zGetTagItem(app.zGetActiveAccount(), tag1);
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
		
		// Get tag ID
		app.zGetActiveAccount().soapSend("<GetTagRequest xmlns='urn:zimbraMail'/>");;
		String tagID = app.zGetActiveAccount().soapSelectValue("//mail:GetTagResponse//mail:tag[@name='"+ tag1 +"']", "id");
		
		// Apply tag to appointment using toolbar button
        app.zPageCalendar.zListItem(Action.A_LEFTCLICK, apptSubject);
        app.zPageCalendar.zToolbarPressButton(Button.O_LISTVIEW_TAG);
        app.zPageCalendar.zTagListView(tag1);
        
        // Rename the tag using the context menu
		DialogRenameTag dialog = (DialogRenameTag) app.zTreeCalendar.zTreeItem(
				Action.A_RIGHTCLICK, Button.B_RENAME, tag);
		dialog.zSetNewName(renameTag1);
		dialog.zClickButton(Button.B_OK);
        app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
        
        app.zGetActiveAccount().soapSend("<GetAppointmentRequest xmlns='urn:zimbraMail'> <m id='" + msgId + "'> </m> </GetAppointmentRequest>");
        app.zGetActiveAccount().soapSend("<GetMsgRequest xmlns='urn:zimbraMail'> <m id='" + msgId + "'> </m> </GetMsgRequest>");
        ZAssert.assertEquals(app.zGetActiveAccount().soapMatch("//mail:GetMsgResponse//mail:m", "tn", renameTag1), true, "");
        
        // Verify applied tag for appointment
		app.zGetActiveAccount().soapSend("<SearchRequest xmlns='urn:zimbraMail' types='appointment'>"
				+ "<query>" + apptSubject + "</query>" + "</SearchRequest>");
		String id = app.zGetActiveAccount().soapSelectValue("//mail:SearchResponse//mail:appt", "t");
		ZAssert.assertEquals(id, tagID, "Verify applied tag for appointment");
		
		// Verify search result from UI
		app.zTreeCalendar.zTreeItem(Action.A_LEFTCLICK, renameTag1);
		ZAssert.assertEquals(app.zPageCalendar.sIsElementPresent(app.zPageCalendar.zGetApptLocator(apptSubject)), true, "Verify search result after clicking renamed tag");
	}
	
	@Test(description = "Apply tag to appointment and delete same tag in day view",
			groups = { "functional" })
	public void DeleteTagAppointment_09() throws HarnessException {
		
		// Creating objects for appointment data
		String tz, apptSubject, apptBody, tag1, renameTag1;
		tz = ZTimeZone.TimeZoneEST.getID();
		tag1 = ZimbraSeleniumProperties.getUniqueString();
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		apptBody = ZimbraSeleniumProperties.getUniqueString();
		
		// Absolute dates in UTC zone
		Calendar now = Calendar.getInstance();
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);
		
        app.zGetActiveAccount().soapSend(
    			"<CreateAppointmentRequest xmlns='urn:zimbraMail'>"
    		+		"<m>"
    		+			"<inv method='REQUEST' type='event' fb='B' transp='O' allDay='0' name='"+ apptSubject +"'>"
    		+				"<s d='"+ startUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>"
    		+				"<e d='"+ endUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>"
    		+				"<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" 
    		+			"</inv>" 
    		+			"<mp content-type='text/plain'>" 
    		+				"<content>" + apptBody + "</content>" 
    		+			"</mp>"
    		+			"<su>" + apptSubject + "</su>" 
    		+		"</m>" 
    		+	"</CreateAppointmentRequest>");

        String apptId = app.zGetActiveAccount().soapSelectValue("//mail:CreateAppointmentResponse", "apptId");
        String msgId = Integer.parseInt(apptId) + "-" + (Integer.parseInt(apptId)-1);
        
        // Create new tag
		app.zGetActiveAccount().soapSend(
				"<CreateTagRequest xmlns='urn:zimbraMail'>" + 
					"<tag name='" + tag1 + "' color='8' />" + 
				"</CreateTagRequest>");
		TagItem tag = app.zPageCalendar.zGetTagItem(app.zGetActiveAccount(), tag1);
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
		
		// Get tag ID
		app.zGetActiveAccount().soapSend("<GetTagRequest xmlns='urn:zimbraMail'/>");;
		String tagID = app.zGetActiveAccount().soapSelectValue("//mail:GetTagResponse//mail:tag[@name='"+ tag1 +"']", "id");
		
		// Apply tag to appointment using toolbar button
        app.zPageCalendar.zListItem(Action.A_LEFTCLICK, apptSubject);
        app.zPageCalendar.zToolbarPressButton(Button.O_LISTVIEW_TAG);
        app.zPageCalendar.zTagListView(tag1);
        
        // Delete the tag using the context menu
		DialogWarning dialog = (DialogWarning) app.zTreeCalendar.zTreeItem(
				Action.A_RIGHTCLICK, Button.B_DELETE, tag);
		dialog.zClickButton(Button.B_YES);
        app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
        
        app.zGetActiveAccount().soapSend("<GetAppointmentRequest xmlns='urn:zimbraMail'> <m id='" + msgId + "'> </m> </GetAppointmentRequest>");
        app.zGetActiveAccount().soapSend("<GetMsgRequest xmlns='urn:zimbraMail'> <m id='" + msgId + "'> </m> </GetMsgRequest>");
        ZAssert.assertEquals(app.zGetActiveAccount().soapMatch("//mail:GetMsgResponse//mail:m", "tn", tag1), false, "");
        
        // Verify deleted tag for appointment
		app.zGetActiveAccount().soapSend("<SearchRequest xmlns='urn:zimbraMail' types='appointment'>"
				+ "<query>" + apptSubject + "</query>" + "</SearchRequest>");
		String id = app.zGetActiveAccount().soapSelectValue("//mail:SearchResponse//mail:appt", "t");
		ZAssert.assertEquals(id, "", "Verify removed tag for appointment");
		
	}
}
