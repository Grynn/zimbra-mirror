package com.zimbra.qa.selenium.framework.util;

import java.text.*;
import java.util.*;

import org.apache.log4j.*;

import com.zimbra.common.soap.Element;

public class ZDate {
	Logger logger = LogManager.getLogger(ZDate.class);


	protected Calendar calendar = null;

	/**
	 * Create a ZDate object in UTC timezone
	 * @param year, i.e. 2011
	 * @param month, i.e. 1 through 12
	 * @param monthday, i.e. 1 through 31
	 * @param hour, i.e. 0 through 24
	 * @param minutes, i.e. 0 through 59
	 * @param seconds, i.e. 0 through 59
	 */
	public ZDate(int year, int month, int monthday, int hour, int minutes, int seconds) {
		this(year, month, monthday, hour, minutes, seconds, ZTimeZone.TimeZoneUTC);
	}

	/**
	 * Create a ZDate object based on the given calendar object
	 * @param calendar
	 */
	public ZDate(Calendar calendar) {
	   this.calendar = calendar;
	}

	/**
	 * Create a ZDate object in the specified timezone
	 * @param year, i.e. 2011
	 * @param month, i.e. 1 through 12
	 * @param monthday, i.e. 1 through 31
	 * @param hour, i.e. 0 through 24
	 * @param minutes, i.e. 0 through 59
	 * @param seconds, i.e. 0 through 59
	 * @param timezone, i.e. 0 through 59
	 * @throws HarnessException if the timezone cannot be found
	 */
	public ZDate(int year, int month, int monthday, int hour, int minutes, int seconds, String timezone) throws HarnessException {
		this(year, month, monthday, hour, minutes, seconds, ZTimeZone.getTimeZone(timezone));
	}

	/**
	 * Create a ZDate object in the specified TimeZone
	 * @param year, i.e. 2011
	 * @param month, i.e. 1 through 12
	 * @param monthday, i.e. 1 through 31
	 * @param hour, i.e. 0 through 24
	 * @param minutes, i.e. 0 through 59
	 * @param seconds, i.e. 0 through 59
	 * @param timezone
	 */
	public ZDate(int year, int month, int monthday, int hour, int minutes, int seconds, TimeZone timezone) {

		// TODO: Handle errors (such as month = 0)

		calendar = Calendar.getInstance();

		calendar.setTimeZone(timezone);

		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DAY_OF_MONTH, monthday);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minutes);
		calendar.set(Calendar.SECOND, seconds);

		logger.info("New "+ ZDate.class.getName());
	}

	/**
	 * Create a ZDate object by parsing a Zimbra SOAP element, such as <s d="20140101T070000" u="1388577600000" tz="America/New_York"/>
	 * @param e
	 * @throws HarnessException
	 */
	public ZDate(Element e) throws HarnessException {

		String d = e.getAttribute("d", null);
		String tz = e.getAttribute("tz", null);
		String u = e.getAttribute("u", null);

		calendar = Calendar.getInstance();

		if ( u != null ) {

			// Parse the unix time, which is in GMT
			long unix = new Long(u).longValue();
			calendar.setTimeInMillis(unix);
			return;

		}

		if ( d != null ) {

			SimpleDateFormat formatter;
            Date tempDate = null;
			Calendar tempCalendar = null;
			formatter = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
			try {
			   tempDate = formatter.parse(d);
			   tempCalendar = Calendar.getInstance();
			   tempCalendar.setTime(tempDate);

			   if (tz == null || tz.trim().length() == 0) {
			      calendar.setTimeZone(ZTimeZone.TimeZoneUTC);
			   } else {
			      calendar.setTimeZone(TimeZone.getTimeZone(tz));
			   }

			   calendar.set(Calendar.YEAR, tempCalendar.get(Calendar.YEAR));
			   calendar.set(Calendar.MONTH, tempCalendar.get(Calendar.MONTH));
			   calendar.set(Calendar.DAY_OF_MONTH, tempCalendar.get(Calendar.DAY_OF_MONTH));
			   calendar.set(Calendar.HOUR, tempCalendar.get(Calendar.HOUR));
			   calendar.set(Calendar.MINUTE, tempCalendar.get(Calendar.MINUTE));
			   calendar.set(Calendar.SECOND, tempCalendar.get(Calendar.SECOND));

               return;

            } catch (ParseException ex) {
                  logger.warn("No match for yyyyMMdd'T'HHmmss");
            }

			formatter = new SimpleDateFormat("yyyyMMdd");
			try {
                tempDate = formatter.parse(d);
			    tempCalendar = Calendar.getInstance();
			    tempCalendar.setTime(tempDate);

                if (tz == null || tz.trim().length() == 0) {
			       calendar.setTimeZone(ZTimeZone.TimeZoneUTC);
			    } else {
			       calendar.setTimeZone(TimeZone.getTimeZone(tz));
			    }

                calendar.set(Calendar.YEAR, tempCalendar.get(Calendar.YEAR));
			    calendar.set(Calendar.MONTH, tempCalendar.get(Calendar.MONTH));
			    calendar.set(Calendar.DAY_OF_MONTH, tempCalendar.get(Calendar.DAY_OF_MONTH));

				return;
			} catch (ParseException ex) {
				logger.warn("yyyyMMdd");
			}


		}

		throw new HarnessException("Unable to parse time element "+ e.prettyPrint());
	}


	/**
	 * Convert the current time + timezone to the specified timezone
	 * @param timezone
	 * @return
	 * @throws HarnessException
	 */
	public ZDate toTimeZone(String timezone) throws HarnessException {
		if ( timezone == null )
			throw new HarnessException("TimeZone cannot be null");

		return (toTimeZone(ZTimeZone.getTimeZone(timezone)));
	}

	/**
	 * Convert the current time + timezone to the specified timezone
	 * @param timezone
	 * @return
	 * @throws HarnessException
	 */
	public ZDate toTimeZone(TimeZone timezone) throws HarnessException {
		if ( timezone == null )
			throw new HarnessException("TimeZone cannot be null");

		Calendar newCalendar = Calendar.getInstance(timezone);
		String currentDate = this.toYYYYMMDDTHHMMSS();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
		try {
		   newCalendar.setTime( formatter.parse(currentDate) );
		} catch (ParseException e) {
		   throw new HarnessException (e.getMessage(), e);
		}

		ZDate returnDate = new ZDate(newCalendar);

		// return it
		return (returnDate);
	}

	/**
	 * Return the ZDate as milliseconds since epoch
	 * @return
	 */
	/**
	 * @return
	 */
	public long toMillis() {
		if ( calendar == null ) {
			calendar = Calendar.getInstance();
		}
		long t = calendar.getTimeInMillis();
		return ( (t / 1000) * 1000); // strip any millisecond blah
	}

	public TimeZone getTimeZone() {
		return (calendar.getTimeZone());
	}

	public String toYYYYMMDDTHHMMSSZ() throws HarnessException {
		return (format("yyyyMMdd'T'HHmmss'Z'"));
	}

	public String toYYYYMMDDTHHMMSS() throws HarnessException {
		return (format("yyyyMMdd'T'HHmmss"));
	}

	/**
	 * MM/DD/YYYY (i.e. 12/25/2011)
	 * @return
	 * @throws HarnessException
	 */
	public String toMM_DD_YYYY() throws HarnessException {
		return (format("MM/dd/yyyy"));
	}

	/**
	 * hh:mm aa (i.e. 04:30 PM)
	 * @return
	 * @throws HarnessException
	 */
	public String tohh_mm_aa() throws HarnessException {
		return (format("hh:mm aa"));
	}


	public String toYYYYMMDD() throws HarnessException {
		return (format("yyyyMMdd"));
	}

	public String toYYYYMMDDHHMMSSZ() throws HarnessException {
		return (format("yyyyMMddHHmmss'Z'"));
	}

	public String toMMM_dC_yyyy() throws HarnessException {
		return (format("MMM d, yyyy"));
	}

	public String toMMM_dd_yyyy_A_hCmm_a() throws HarnessException {
		return (format("MMM d, yyyy @ h:mm a"));
	}


	protected String format(String format) throws HarnessException {
		try {
			SimpleDateFormat converter = new SimpleDateFormat(format);
			converter.setTimeZone(calendar.getTimeZone());
			return (converter.format(calendar.getTime()));
		} catch (IllegalArgumentException e) {
			throw new HarnessException("Unable to format date: "+ calendar, e);
		}
	}

	/**
	 * Return a new ZDate object with the adjusted offset (+/-)
	 * @param amount
	 * @return
	 */
	public ZDate addDays(int amount) {
		return (addHours(amount * 24));
	}

	/**
	 * Return a new ZDate object with the adjusted offset (+/-)
	 * @param amount
	 * @return
	 */
	public ZDate addHours(int amount) {
		return (addMinutes(amount * 60));
	}

	/**
	 * Return a new ZDate object with the adjusted offset (+/-)
	 * @param amount
	 * @return
	 */
	public ZDate addMinutes(int amount) {
		return (addSeconds(amount * 60));
	}

	/**
	 * Return a new ZDate object with the adjusted offset (+/-)
	 * @param amount
	 * @return
	 */
	public ZDate addSeconds(int amount) {

		// Create the new object to return
		ZDate other = new ZDate(
				this.calendar.get(Calendar.YEAR),
				this.calendar.get(Calendar.MONTH) + 1,
				this.calendar.get(Calendar.DAY_OF_MONTH),
				this.calendar.get(Calendar.HOUR_OF_DAY),
				this.calendar.get(Calendar.MINUTE),
				this.calendar.get(Calendar.SECOND),
				this.calendar.getTimeZone()
			);

		// Adjust it
		other.calendar.add(Calendar.SECOND, amount);

		// return it
		return (other);
	}


	@Override
	public String toString() {
		try {
			return (format("MM/dd/yyyy HH:mm:ss z"));
		} catch (HarnessException e) {
			logger.error(e);
			return (calendar.toString());
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((calendar == null) ? 0 : calendar.hashCode());
		return result;
	}

	@Override
	/**
	 * Return true/false whether the two ZDates are the same UTC time
	 * @param amount
	 * @return
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ZDate other = (ZDate) obj;
		return (toMillis() == other.toMillis());
	}


}
