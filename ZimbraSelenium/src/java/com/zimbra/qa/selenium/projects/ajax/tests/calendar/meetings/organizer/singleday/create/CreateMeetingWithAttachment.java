/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.projects.ajax.tests.calendar.meetings.organizer.singleday.create;

import java.util.Calendar;
import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.AppointmentItem;
import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.CalendarWorkWeekTest;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.PageCalendar.Locators;

public class CreateMeetingWithAttachment extends CalendarWorkWeekTest {

	public CreateMeetingWithAttachment() {
		logger.info("New "+ CreateMeetingWithAttachment.class.getCanonicalName());
		super.startingPage = app.zPageCalendar;
	}
	
	@Test(	description = "Create a basic appointment with an attachment",
			groups = { "functional" }
	)
	public void CreateMeetingWithAttachment_01() throws HarnessException {
		
		// Create appointment & subject
		ZimbraAccount account = app.zGetActiveAccount();
		String apptSubject = "appointment" + ZimbraSeleniumProperties.getUniqueString();
		
		//upload file to server
		String filename = "BasicExcel2007.xlsx";
		String filePath = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/Files/Basic01/"+ filename;
		String dAttachmentId  = account.uploadFile(filePath);
		//create date object
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
                     	"<attach aid='"+dAttachmentId +"'/>"+
                     "<su>"+ apptSubject +"</su>" +
                     "</m>" +
               "</CreateAppointmentRequest>");
        app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
			
		// open the appt
    	app.zPageCalendar.zListItem(Action.A_DOUBLECLICK, apptSubject);
    	
		// Verify the new appointment has an attachment 
		ZAssert.assertTrue(app.zPageCalendar.sIsElementPresent(Locators.zAttachmentsLabel),"Verify Attachments: label");
		AppointmentItem actual = AppointmentItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ apptSubject +")");
		ZAssert.assertStringContains(actual.getGMultipart().toString(), filename , "check if multipart has above created file name" );	
		
	}
}
