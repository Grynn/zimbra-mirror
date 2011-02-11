package com.zimbra.qa.selenium.framework.util;

import java.text.*;
import java.util.*;

public class DateUtil {

	// TODO: Need to L10N
	
	/**
	 * 20101225120000Z
	 * (Useful for SOAP requests)
	 */
	public static final DateFormat yyyyMMddHHmmssZ		= new SimpleDateFormat("yyyyMMddHHmmssZ");
	
	/**
	 * 20101225
	 * (Useful for SOAP requests)
	 */
	public static final DateFormat yyyyMMdd				= new SimpleDateFormat("yyyyMMdd");
	
	/**
	 * Feb 14, 2011
	 * (Useful in date displays)
	 */
	public static final DateFormat MMM_dC_yyyy			= new SimpleDateFormat("MMM d, yyyy");

	/**
	 * Feb 15, 2011 @ 2:30 PM
	 * (Useful in appointment/task reminder display)
	 */
	public static final DateFormat MMM_dd_yyyy_A_hCmm_a = new SimpleDateFormat("MMM d, yyyy @ h:mm a");
	

	public static String convert(GregorianCalendar date, DateFormat format) {
		return (format.format(date));
	}
	
	public static String convert(GregorianCalendar date, String format) {
		return ((new SimpleDateFormat(format)).format(date));
	}
	
	
}
