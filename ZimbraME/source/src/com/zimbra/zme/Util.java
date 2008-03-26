/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite J2ME Client
 * Copyright (C) 2007, 2008 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

package com.zimbra.zme;

import java.util.Calendar;
import java.util.Date;

import javax.microedition.lcdui.Font;

import de.enough.polish.util.Locale;

public class Util {

    public static final long MSEC_PER_HOUR = 3600 * 1000;
	public static final long MSEC_PER_DAY  = MSEC_PER_HOUR * 24;

	public static final String DATE_SEP = Locale.get("main.DateSep");
	public static final char[] DATE_FMT = Locale.get("main.DateFormat").toLowerCase().toCharArray();
	public static final String TIME_SEP = Locale.get("main.TimeSep");
	public static final int TIME_FMT = (Locale.get("main.TimeFormat").compareTo("12") == 0) ? 12 : 24;

	public static final String[] DAY_OF_WEEK = {
		Locale.get("main.Sun"), Locale.get("main.Mon"), Locale.get("main.Tue"), Locale.get("main.Wed"), 
		Locale.get("main.Thu"), Locale.get("main.Fri"), Locale.get("main.Sat") 
	};

	public static final String[] MONTHS = {
		Locale.get("main.Jan"), Locale.get("main.Feb"), Locale.get("main.Mar"), Locale.get("main.Apr"), 
		Locale.get("main.May"), Locale.get("main.Jun"), Locale.get("main.Jul"), Locale.get("main.Aug"), 
		Locale.get("main.Sep"), Locale.get("main.Oct"), Locale.get("main.Nov"), Locale.get("main.Dec") 
	};
	
	public static String getFullDateTime(Calendar cal,
										 boolean showTimeZone) {
		StringBuffer sb = new StringBuffer();
		sb.append(DAY_OF_WEEK[cal.get(Calendar.DAY_OF_WEEK) - 1]).append(", ");
		sb.append(cal.get(Calendar.DAY_OF_MONTH)).append(" ");
		sb.append(MONTHS[cal.get(Calendar.MONTH)]).append(" ");
		sb.append(cal.get(Calendar.YEAR)).append(" ");
		sb.append(Util.getTime(cal, true));
		
		if (showTimeZone) {
			sb.append(" (").append(getGMTOffset(cal)).append(") ").append(cal.getTimeZone().getID());
		}
		
		return sb.toString();
	}
	
	
	public static String getDate(Calendar cal) {

		StringBuffer sb = new StringBuffer();		
		Date date = cal.getTime();	
		Date now = new Date();
		Calendar nowCal = Calendar.getInstance();
		nowCal.setTime(now);
		
		if (now.getTime() - date.getTime() < MSEC_PER_DAY 
			&& nowCal.get(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH)) {
			int val;
				
			if (TIME_FMT == 24) {
				val = cal.get(Calendar.HOUR_OF_DAY); 
				if (val < 10)
					sb.append("0");
			} else {
				val = cal.get(Calendar.HOUR);
			}
			sb.append(val);
			
			sb.append(TIME_SEP);
			
			val = cal.get(Calendar.MINUTE);
			if (val < 10)
				sb.append("0");
			sb.append(val);
			
			if (TIME_FMT == 12)
				sb.append((cal.get(Calendar.AM_PM) == Calendar.PM) ? "PM" : "AM");
			
		} else if (nowCal.get(Calendar.YEAR) == cal.get(Calendar.YEAR)) {
			sb.append(MONTHS[cal.get(Calendar.MONTH)]).append(" ").append(cal.get(Calendar.DAY_OF_MONTH));	
		} else {
			for (int i = 0; i < DATE_FMT.length; i++) {
				switch (DATE_FMT[i]) {
					case 'm':
						sb.append(cal.get(Calendar.MONTH) + 1);
						break;
					case 'd':
						sb.append(cal.get(Calendar.DAY_OF_MONTH));
						break;
					case 'y':
						sb.append(Integer.toString(cal.get(Calendar.YEAR)).substring(2));
						break;
				}
				if (i < DATE_FMT.length - 1)
					sb.append(DATE_SEP);
			}
		}	
		return sb.toString();
	}
	
	/**
	 * Given <i>cal</i> returns a localized time string
	 * @param cal
	 * @return
	 */
	public static String getTime(Calendar cal,
								 boolean force24) {

		StringBuffer sb = new StringBuffer();
		int val;
		
		if (TIME_FMT == 24 || force24) {
			val = cal.get(Calendar.HOUR_OF_DAY); 
			if (val < 10)
				sb.append("0");
		} else {
			val = cal.get(Calendar.HOUR);
            if (val == 0)
                val = 12;
		}
		sb.append(val);	
		sb.append(TIME_SEP);
		
		val = cal.get(Calendar.MINUTE);
		if (val < 10)
			sb.append("0");
		sb.append(val);

		if (TIME_FMT == 12 && !force24)
			sb.append((cal.get(Calendar.AM_PM) == Calendar.PM) ? "PM" : "AM");
		
		return sb.toString();
		
	}
	
	public static String elidString(String str,
									int width,
									Font font) {
        if (str == null)
            return null;
		char[] a = str.toCharArray();
		int l = a.length;
		int curWidth;
		
		for (int i = l; i >= 0; i--) {
			curWidth = font.charsWidth(a, 0, i);
			if (curWidth <= width) {
				if (i != l) {
					if (i != l && l > 3)
						a[i-1] = a[i-2] = a[i-3] = '.';
					return new String(a, 0, i);
				} else {
					return str;
				}
			}
		}
		return str;
	}

	private static String getGMTOffset(Calendar cal) {
		int offset = cal.getTimeZone().getRawOffset() / 36000;
		StringBuffer sb;
		
		if (offset < 0)
			sb = new StringBuffer("GMT -");
		else if (offset > 0)
			sb = new StringBuffer("GMT +");
		else
			return  "GMT";

		offset = Math.abs(offset);
		if (offset < 1000)
			sb.append("0");
		sb.append(offset);
		return sb.toString();
	}
}
