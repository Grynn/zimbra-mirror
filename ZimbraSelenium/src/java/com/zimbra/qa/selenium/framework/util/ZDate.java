package com.zimbra.qa.selenium.framework.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.*;

import com.zimbra.common.soap.Element;

@SuppressWarnings("deprecation")
public class ZDate {
	Logger logger = LogManager.getLogger(ZDate.class);

	protected Date calendar = null;
	
	public ZDate(int year, int month, int monthday, int hour, int minutes, int seconds) {
		// TODO: Handle errors (such as month = 0)
		calendar = new Date();
		calendar.setYear(year - 1901);
		calendar.setMonth(month);
		calendar.setDate(monthday);
		calendar.setHours(hour);
		calendar.setMinutes(minutes);
		calendar.setSeconds(seconds);
		
		logger.info("New "+ ZDate.class.getName());
	}
	
	public ZDate(Element e) throws HarnessException {
		
		String d = e.getAttribute("d", null);
		String tz = e.getAttribute("tz", null);
		String u = e.getAttribute("u", null);
		
		if ( tz == null ) {
			// Assume GMT
			// TODO
		}
		
		if ( u != null ) {
			
			// Parse the unix time
			long unix = new Long(u).longValue();
			calendar = new Date(unix);
			return;
			
		}
		
		if ( d != null ) {
			
			// TODO
			calendar = new Date();
			return;
			
		}
		
		throw new HarnessException("Unable to parse time element "+ e.prettyPrint());
	}
	
	public ZDate(ZDate other) {
		calendar = new Date();
		calendar.setYear(other.calendar.getYear());
		calendar.setMonth(other.calendar.getMonth());
		calendar.setDate(other.calendar.getDate());
		calendar.setHours(other.calendar.getHours());
		calendar.setMinutes(other.calendar.getMinutes());
		calendar.setSeconds(other.calendar.getSeconds());

		logger.info("New "+ ZDate.class.getName());
	}

	public long toMillis() throws HarnessException {
		return (calendar.getTime());
	}
	
	public String toYYYYMMDDTHHMMSSZ() throws HarnessException {
		return (format("yyyyMMdd'T'HHmmss'Z'"));
	}
	
	protected String format(String format) throws HarnessException {
		try {
			SimpleDateFormat converter = new SimpleDateFormat(format);
			return (converter.format(calendar));
		} catch (IllegalArgumentException e) {
			throw new HarnessException("Unable to format date: "+ calendar, e);
		}
	}

	public ZDate addDays(int days) {
		ZDate other = new ZDate(this);
		other.calendar.setDate(this.calendar.getDate() + days);
		return (other);
	}

	@Override
	public String toString() {
		try {
			return (format("MM/dd/yyyy HH:mm:ss z"));
		} catch (HarnessException e) {
			logger.error(e);
			return (calendar.toGMTString());
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
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ZDate other = (ZDate) obj;
		if (calendar == null) {
			if (other.calendar != null)
				return false;
		} else if (!calendar.equals(other.calendar))
			return false;
		return true;
	}


}
