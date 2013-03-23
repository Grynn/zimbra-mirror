/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.projects.ajax.core;

import java.util.*;

/**
 * A base class that sets
 *  1) the starting page to be the calendar app
 *  2) sets zimbraPrefCalendarInitialView=WorkWeek (default)
 *  3) sets weekDayUTC ... a Calendar object, on current week day
 *  
 * @author Matt Rhoades
 *
 */
public class CalendarWorkWeekTest extends AjaxCommonTest {
	
	public static boolean organizerTest;
	
	protected Calendar calendarWeekDayUTC = null;
	
	public CalendarWorkWeekTest() {
		
		super.startingPage = app.zPageCalendar;

		super.startingAccountPreferences = new HashMap<String, String>() {
			private static final long serialVersionUID = -109947857488617841L;
		{
		    put("zimbraPrefCalendarInitialView", "workWeek");
		}};
		
		calendarWeekDayUTC = Calendar.getInstance();
		
		if ( calendarWeekDayUTC.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY ) {
			
			// If UTC is Friday, tests may fail if the TZ offset
			// puts the date into Saturday.  Move the time to Thursday
			// just to be safe.
			
			calendarWeekDayUTC.add(Calendar.HOUR, -24);
			
		} else if ( calendarWeekDayUTC.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ) {
			
			// Change Saturdays to Thursday.
			
			calendarWeekDayUTC.add(Calendar.HOUR, -48);
			
		} else if ( calendarWeekDayUTC.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY ) {
			
			// Change Sundays to Tuesday.
			
			calendarWeekDayUTC.add(Calendar.HOUR, 48);
			
		} else if ( calendarWeekDayUTC.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY ) {
			
			// If UTC is Monday, tests may fail if the TZ offset
			// puts the date into Sunday.  Move the time to Tuesday
			// just to be safe.
			
			
			calendarWeekDayUTC.add(Calendar.HOUR, 24);
			
		}
	}
	
}
