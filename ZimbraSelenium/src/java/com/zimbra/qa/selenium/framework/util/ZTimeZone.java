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
