/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2013 Zimbra Software, LLC.
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
package com.zimbra.qa.selenium.framework.util;

import java.util.TimeZone;

public class ZTimeZone {

	public static final TimeZone TimeZoneUTC = TimeZone.getTimeZone("UTC");
	public static final TimeZone TimeZoneHST = TimeZone.getTimeZone("Pacific/Honolulu");
	public static final TimeZone TimeZoneAKST = TimeZone.getTimeZone("America/Juneau");
	public static final TimeZone TimeZonePST = TimeZone.getTimeZone("America/Los_Angeles");
	public static final TimeZone TimeZoneMST = TimeZone.getTimeZone("America/Denver");
	public static final TimeZone TimeZoneCST = TimeZone.getTimeZone("America/Chicago");
	public static final TimeZone TimeZoneEST = TimeZone.getTimeZone("America/New_York");

	/**
	 * Convert a timezone string identifier to a TimeZone object
	 * <p>
	 * @param timezone
	 * @return the TimeZone object
	 * @throws HarnessException, if the timezone cannot be found
	 */
	public static TimeZone getTimeZone(String timezone) throws HarnessException {
		if ( timezone == null )
			throw new HarnessException("TimeZone string cannot be null");

		for ( String t : TimeZone.getAvailableIDs() ) {
			if ( t.equals(timezone) ) {
				// Found it
				return (TimeZone.getTimeZone(timezone));
			}
		}
		
		throw new HarnessException("Unable to determine the TimeZone from the string: "+ timezone);
	}
	

}
