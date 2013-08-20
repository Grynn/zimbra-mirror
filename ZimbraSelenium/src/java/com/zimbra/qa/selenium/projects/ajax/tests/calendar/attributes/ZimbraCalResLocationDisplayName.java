/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2013 Zimbra Software, LLC.
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
package com.zimbra.qa.selenium.projects.ajax.tests.calendar.attributes;

import java.util.Calendar;

import org.testng.annotations.*;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.CalendarWorkWeekTest;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.DialogFindLocation;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.PageCalendar.Locators;

public class ZimbraCalResLocationDisplayName extends CalendarWorkWeekTest {	
	
	public ZimbraCalResLocationDisplayName() {
		logger.info("New "+ ZimbraCalResLocationDisplayName.class.getCanonicalName());
	    super.startingPage =  app.zPageCalendar;
	    super.startingAccountPreferences = null;
	}
	
	@Test(description = "Bug 57039 : Verify the serach location dialog shows location display name for location ",
			groups = { "functional" })
	public void ZimbraCalResLocationDisplayName_01() throws HarnessException {
		
		ZimbraResource location = new ZimbraResource(ZimbraResource.Type.LOCATION);
		String resourceDisplayName = "DisplayName" +ZimbraSeleniumProperties.getUniqueString(); 
		
		// Modify the Location resource account and change zimbraCalResLocationDisplayName 
		ZimbraAdminAccount.GlobalAdmin().soapSend(
				"<ModifyCalendarResourceRequest xmlns='urn:zimbraAdmin'>" +
					"<name>" + location.EmailAddress + "</name>" +
					"<id> " + location.ZimbraId + "</id> " +
					"<a n='zimbraCalResLocationDisplayName'>"+ resourceDisplayName +"</a>" +
				"</ModifyCalendarResourceRequest>");
		
		Element[] ModifyCalendarResourceResponse = ZimbraAdminAccount.GlobalAdmin().soapSelectNodes("//admin:ModifyCalendarResourceRequest");
		logger.info("ModifyCalendarResourceResponse is')" + ModifyCalendarResourceResponse);
		
		if ( (ModifyCalendarResourceResponse == null) || (ModifyCalendarResourceResponse.length == 0)) {

			Element[] soapFault = ZimbraAdminAccount.GlobalAdmin().soapSelectNodes("//soap:Fault");
			if ( soapFault != null && soapFault.length > 0 ) {
			
				String error = ZimbraAdminAccount.GlobalAdmin().soapSelectValue("//zimbra:Code", null);
				throw new HarnessException("Unable to modify resource : "+ error);
				
			}
		
			
		}
		SleepUtil.sleepLong();
		
		// Logout and login to pick up the changes
		app.zPageLogin.zNavigateTo();
		this.startingPage.zNavigateTo();
		
		String tz = ZTimeZone.TimeZoneEST.getID();
		String apptSubject = ZimbraSeleniumProperties.getUniqueString();
		String apptAttendee = ZimbraAccount.AccountA().EmailAddress;
		
		// Absolute dates in UTC zone
		Calendar now = this.calendarWeekDayUTC;
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 15, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 17, 0, 0);
		
		app.zGetActiveAccount().soapSend(
                "<CreateAppointmentRequest xmlns='urn:zimbraMail'>" +
                     "<m>"+
                     	"<inv method='REQUEST' type='event' status='CONF' draft='0' class='PUB' fb='B' transp='O' allDay='0' name='"+ apptSubject +"'>"+
                     		"<s d='"+ startUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                     		"<e d='"+ endUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                     		"<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
                     		"<at role='REQ' ptst='AC' rsvp='1' a='" + apptAttendee + "' d='2'/>" +
                     	"</inv>" +
                     	"<e a='"+ ZimbraAccount.AccountA().EmailAddress +"' t='t'/>" +
                     	"<mp content-type='text/plain'>" +
                     		"<content>"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
                     	"</mp>" +
                     "<su>"+ apptSubject +"</su>" +
                     "</m>" +
               "</CreateAppointmentRequest>");
        app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
        
        FormApptNew apptForm = (FormApptNew)app.zPageCalendar.zListItem(Action.A_RIGHTCLICK, Button.O_OPEN, apptSubject);
        apptForm.zToolbarPressButton(Button.B_LOCATION);
        
        DialogFindLocation dialogFindLocation = (DialogFindLocation) new DialogFindLocation(app, app.zPageCalendar);
        dialogFindLocation.zType(Locators.LocationName, location.EmailAddress);
        dialogFindLocation.zClickButton(Button.B_SEARCH_LOCATION);
        
        // Verify the search dialog show name as email address and Location as Display name set above
        String searchResult = dialogFindLocation.zGetDisplayedText(Locators.LocationFirstSearchResult);
		ZAssert.assertStringContains(searchResult, resourceDisplayName, "verify if the Location dispaly name is being displayed in the results");
		ZAssert.assertStringContains(searchResult, location.EmailAddress, "verify if the Location  name is being displayed as email address in the results");
      
		// Close the search location dialog
		dialogFindLocation.zClickButton(Button.B_OK);
		
		// Close Edit appt form
        apptForm.zToolbarPressButton(Button.B_CLOSE);
        
	}
	
	
}
