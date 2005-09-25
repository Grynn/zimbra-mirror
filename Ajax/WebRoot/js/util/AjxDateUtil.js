/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZAPL 1.1
 * 
 * The contents of this file are subject to the Zimbra AJAX Public
 * License Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra AJAX Toolkit.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

function AjxDateUtil() {
};

AjxDateUtil.YEAR = 1;
AjxDateUtil.MONTH = 2;
AjxDateUtil.WEEK = 3;
AjxDateUtil.DAY = 4;

AjxDateUtil.MSEC_PER_FIFTEEN_MINUTES = 900000;
AjxDateUtil.MSEC_PER_HALF_HOUR = 1800000;
AjxDateUtil.MSEC_PER_HOUR = 3600000;
AjxDateUtil.MSEC_PER_DAY = 24 * AjxDateUtil.MSEC_PER_HOUR;

AjxDateUtil.WEEKDAY_SHORT = [
	AjxMsg.weekdayShortSun, AjxMsg.weekdayShortMon, AjxMsg.weekdayShortTue, 
	AjxMsg.weekdayShortWed, AjxMsg.weekdayShortThu, AjxMsg.weekdayShortFri, 
	AjxMsg.weekdayShortSat
];
AjxDateUtil.WEEKDAY_MEDIUM = [
	AjxMsg.weekdayMediumSun, AjxMsg.weekdayMediumMon, AjxMsg.weekdayMediumTue, 
	AjxMsg.weekdayMediumWed, AjxMsg.weekdayMediumThu, AjxMsg.weekdayMediumFri, 
	AjxMsg.weekdayMediumSat
];
AjxDateUtil.WEEKDAY_LONG = [
	AjxMsg.weekdayLongSun, AjxMsg.weekdayLongMon, AjxMsg.weekdayLongTue, 
	AjxMsg.weekdayLongWed, AjxMsg.weekdayLongThu, AjxMsg.weekdayLongFri, 
	AjxMsg.weekdayLongSat
];

AjxDateUtil.MONTH_SHORT = [
	AjxMsg.monthShortJan, AjxMsg.monthShortFeb, AjxMsg.monthShortMar, 
	AjxMsg.monthShortApr, AjxMsg.monthShortMay, AjxMsg.monthShortJun, 
	AjxMsg.monthShortJul, AjxMsg.monthShortAug, AjxMsg.monthShortSep, 
	AjxMsg.monthShortOct, AjxMsg.monthShortNov, AjxMsg.monthShortDec
];
AjxDateUtil.MONTH_MEDIUM = [
	AjxMsg.monthMediumJan, AjxMsg.monthMediumFeb, AjxMsg.monthMediumMar,
	AjxMsg.monthMediumApr, AjxMsg.monthMediumMay, AjxMsg.monthMediumJun,
	AjxMsg.monthMediumJul, AjxMsg.monthMediumAug, AjxMsg.monthMediumSep,
	AjxMsg.monthMediumOct, AjxMsg.monthMediumNov, AjxMsg.monthMediumDec
	
];
AjxDateUtil.MONTH_LONG = [
	AjxMsg.monthLongJan, AjxMsg.monthLongFeb, AjxMsg.monthLongMar,
	AjxMsg.monthLongApr, AjxMsg.monthLongMay, AjxMsg.monthLongJun,
	AjxMsg.monthLongJul, AjxMsg.monthLongAug, AjxMsg.monthLongSep,
	AjxMsg.monthLongOct, AjxMsg.monthLongNov, AjxMsg.monthLongDec
];

AjxDateUtil._daysPerMonth = {
	0:31,
	1:29,
	2:31,
	3:30,
	4:31,
	5:30,
	6:31,
	7:31,
	8:30,
	9:31,
	10:30,
	11:31
};

AjxDateUtil._init =
function() {                                           
	AjxDateUtil._dateSep = AjxConfig.DATE_SEP;
	AjxDateUtil._timeSep = AjxConfig.TIME_SEP;
	AjxDateUtil._dateFmt = new Array();
	var tmp = AjxConfig.DATE_FMT;
	for (var i = 0; i < tmp.length; i++)
		AjxDateUtil._dateFmt[i] = tmp.substr(i, 1);
};

AjxDateUtil._init();                    

/* return true if the specified date (yyyy, m (0-11), d (1-31)) 
 * is valid or not.
 */
AjxDateUtil.validDate =
function(y, m, d) {
	var date = new Date(y, m, d);
	return date.getMonth() == m && date.getDate() == d;
};

/* return number of days (1-31) in specified month (yyyy, mm (0-11))
 */
AjxDateUtil.daysInMonth =
function(y, m) {
	var date = new Date(y, m, 1, 12);
	date.setMonth(date.getMonth()+1);
	date.setDate(date.getDate()-1);
	return date.getDate();
};

/* return true if year is a leap year
 */
AjxDateUtil.isLeapYear =
function(y) {
	return (new Date(y, 1, 29)).getMonth() == 1;
};

/**
 * rolls the month/year. If the day of month in the date passed in is greater
 * then the max day in the new month, set it to the max. The date passed in is
 * modified and also returned.
 */
AjxDateUtil.roll = 
function(date, field, offset) {
	var d = date.getDate();
	 // move back to first day before rolling in case previous
	 // month/year has less days

	if (field == AjxDateUtil.MONTH) {
		date.setDate(1);	
		date.setMonth(date.getMonth() + offset);
		var max = AjxDateUtil.daysInMonth(date.getFullYear(), date.getMonth());
		date.setDate(Math.min(d, max));		
	} else if (field == AjxDateUtil.YEAR) {
		date.setDate(1);		
		date.setFullYear(date.getFullYear() + offset);
		var max = AjxDateUtil.daysInMonth(date.getFullYear(), date.getMonth());
		date.setDate(Math.min(d, max));		
	} else if (field == AjxDateUtil.WEEK) {
		date.setDate(date.getDate() + 7*offset);
	} else if (field == AjxDateUtil.DAY) {
		date.setDate(date.getDate() + offset);		
	} else {
		return date;
	}
	return date;
};

// Computes the difference between now and <dateMSec>. Returns a string describing
// the difference
AjxDateUtil.computeDateDelta =
function(dateMSec) {
	var deltaMSec = (new Date()).getTime() - dateMSec;
	var years =  Math.floor(deltaMSec / (AjxDateUtil.MSEC_PER_DAY * 365));
	if (years != 0)
		deltaMSec -= years * AjxDateUtil.MSEC_PER_DAY * 365;
	var months = Math.floor(deltaMSec / (AjxDateUtil.MSEC_PER_DAY * 30.42));
	if (months > 0)
		deltaMSec -= Math.floor(months * AjxDateUtil.MSEC_PER_DAY * 30.42);
	var days = Math.floor(deltaMSec / AjxDateUtil.MSEC_PER_DAY);
	if (days > 0)
		deltaMSec -= days * AjxDateUtil.MSEC_PER_DAY;
	var hours = Math.floor(deltaMSec / AjxDateUtil.MSEC_PER_HOUR);
	if (hours > 0) 
		deltaMSec -= hours * AjxDateUtil.MSEC_PER_HOUR;
	var mins = Math.floor(deltaMSec / 60000);
	if (mins > 0)
		deltaMSec -= mins * 60000;
	var secs = Math.floor(deltaMSec / 1000);
	
	var deltaStr = "";
	if (years > 0) {
		deltaStr =  years + " ";
		deltaStr += (years > 1) ? AjxMsg.years : AjxMsg.year;
		if (years <= 3 && months > 0) {
    		deltaStr += " " + months;
    		deltaStr += " " + ((months > 1) ? AjxMsg.months : AjxMsg.months);
		}
	} else if (months > 0) {
		deltaStr =  months + " ";
		deltaStr += (months > 1) ? AjxMsg.months : AjxMsg.month;
		if (months <= 3 && days > 0) {
    		deltaStr += " " + days;
    		deltaStr += " " + ((days > 1) ? AjxMsg.days : AjxMsg.day);
		}
	} else if (days > 0) {
		deltaStr = days + " ";
		deltaStr += (days > 1) ? AjxMsg.days : AjxMsg.day;
		if (days <= 2 && hours > 0) {
    		deltaStr += " " + hours;
    		deltaStr += " " + ((hours > 1) ? AjxMsg.hours : AjxMsg.hour);
		}
	} else if (hours > 0) {
		deltaStr = hours + " ";
		deltaStr += (hours > 1) ? AjxMsg.hours : AjxMsg.hour;
		if (hours < 5 && mins > 0) {
    		deltaStr += " " + mins;
    		deltaStr += " " + ((mins > 1) ? AjxMsg.minutes : AjxMsg.minute);
		}
	} else if (mins > 0) {
		deltaStr = mins + " ";
		deltaStr += ((mins > 1) ? AjxMsg.minutes : AjxMsg.minute);
		if (mins < 5 && secs > 0) {
    		deltaStr += " " + secs;
    		deltaStr += " " + ((secs > 1) ? AjxMsg.seconds : AjxMsg.second);
		}
	} else {
		deltaStr = secs;
		deltaStr += " " + ((secs > 1) ? AjxMsg.seconds : AjxMsg.second);
	}
	deltaStr += " " + AjxMsg.ago;
	return deltaStr;
};

AjxDateUtil.simpleComputeDateStr = 
function(date, stringToPrepend) {
	var year, month, day;
	var idx = 0;
	var dateArr = new Array();
	var written = false;
	if (stringToPrepend != null){
		dateArr[0] = stringToPrepend;
		written = true;
	}
	for (var i = 0; i < AjxDateUtil._dateFmt.length; i++) {
		switch (AjxDateUtil._dateFmt[i]) {
		case 'Y':
			year = date.getFullYear() % 100;
			dateArr[idx++] =  ((written)? "/" : "");
			dateArr[idx++] = AjxDateUtil._pad(year);
			written = true;
			break;
		case 'M':
			month = date.getMonth() + 1;
			dateArr[idx++] = ( (written)? "/" : "");
			dateArr[idx++] = AjxDateUtil._pad(month);
			written = true;
			break;
		case 'D':
			day = date.getDate();
			dateArr[idx++] = ((written) ? "/" : "");
			dateArr[idx++] = AjxDateUtil._pad(day);
			written = true;
			break;
		}
	}
	return dateArr.join("");
};

AjxDateUtil.longComputeDateStr = 
function(date) {
	// TODO: i18n
	return AjxDateUtil.getTimeStr(date, "%w, %M %D, %Y");
}

AjxDateUtil.computeDateStr =
function(now, dateMSec) {
	if (dateMSec == null)
		return "";
	
	var nowMSec = now.getTime();
	var nowDay = now.getDay();
	var nowYear = now.getFullYear();
	var date = new Date(dateMSec);
	var year = date.getFullYear();
	var dateStr = "";
	if (nowMSec - dateMSec < AjxDateUtil.MSEC_PER_DAY && nowDay == date.getDay()) {
		var hours = date.getHours();
		var mins = date.getMinutes();
		dateStr = AjxDateUtil._pad(hours) + AjxDateUtil._timeSep + AjxDateUtil._pad(mins);
	} else if (year == nowYear) {
		for (var i = 0; i < AjxDateUtil._dateFmt.length; i++) {
			switch (AjxDateUtil._dateFmt[i]) {
				case 'M':
					dateStr = dateStr + ((dateStr != "") ? " " : "") + AjxDateUtil.MONTH_MEDIUM[date.getMonth()];
					break;
				case 'D':
					var day = date.getDate();
					dateStr = dateStr + ((dateStr != "") ? " " : "") + AjxDateUtil._pad(day);
					break;
			}
		}
	} else {
		dateStr = AjxDateUtil.simpleComputeDateStr(date);
	} 
	return dateStr;
};

AjxDateUtil._getHoursStr = 
function(date, pad, useMilitary) {
	var myVal = date.getHours();
	if (!useMilitary) {
		myVal %= 12;
		if (myVal == 0) myVal = 12;
	}
	return pad ? AjxDateUtil._pad(myVal) : myVal;
};

AjxDateUtil._getMinutesStr = 
function(date) {
	return AjxDateUtil._pad(date.getMinutes());
};

AjxDateUtil._getSecondsStr = 
function(date) {
	return AjxDateUtil._pad(date.getSeconds());
};

AjxDateUtil._getAMPM = 
function (date, upper) {
	var myHour = date.getHours();
	return (myHour < 12) ? (upper ? 'AM' : 'am') : (upper ? 'PM' : 'pm');
};

AjxDateUtil._getMonthName = 
function(date, abbreviated) {
	return abbreviated
		? AjxDateUtil.MONTH_MEDIUM[date.getMonth()]
		: AjxDateUtil.MONTH_LONG[date.getMonth()];
};

AjxDateUtil._getDate = 
function(date, pad) {
	var myVal = date.getDate();
	return pad == true ? AjxDateUtil._pad(myVal) : myVal;
};

AjxDateUtil._getWeekday =
function (date) {
	var myVal = date.getDay();
	return AjxDateUtil.WEEKDAY_LONG[myVal];
};

AjxDateUtil._getFullYear = 
function(date) {
	return date.getFullYear();
};

AjxDateUtil.getTimeStr = 
function(date, format) {
	var s = format;
	s = s.replace(/%d/g, AjxDateUtil._getDate(date, true));				// zero padded day of the month
	s = s.replace(/%D/g, AjxDateUtil._getDate(date, false));			// day of the month without padding
	s = s.replace(/%w/g, AjxDateUtil._getWeekday(date));				// day of the week
	s = s.replace(/%M/g, AjxDateUtil._getMonthName(date));				// full month name
	s = s.replace(/%t/g, AjxDateUtil._getMonthName(date, true));		// abbr. month name
	s = s.replace(/%Y/g, AjxDateUtil._getFullYear(date));				// full year
	s = s.replace(/%h/g, AjxDateUtil._getHoursStr(date, false, false));	// non-padded hours
	s = s.replace(/%H/g, AjxDateUtil._getHoursStr(date, true, false ));	// padded hours
	s = s.replace(/%m/g, AjxDateUtil._getMinutesStr(date));				// padded minutes
	s = s.replace(/%s/g, AjxDateUtil._getSecondsStr(date));				// padded seconds
	s = s.replace(/%P/g, AjxDateUtil._getAMPM(date, true));				// upper case AM PM
	s = s.replace(/%p/g, AjxDateUtil._getAMPM(date, false));			// lower case AM PM
	return s;
};

AjxDateUtil.getRoundedMins = 
function (date, roundTo) {
	var mins = date.getMinutes();
	if (mins != 0 && roundTo)
		mins = (Math.ceil( (mins/roundTo) )) * roundTo;
	return mins;
};

AjxDateUtil.roundTimeMins = 
function(date, roundTo) {
	var mins = date.getMinutes();
	var hours = date.getHours();
	if (mins != 0 && roundTo){
		mins = (Math.ceil( (mins/roundTo) )) * roundTo;
		if (mins == 60) {
			mins = 0;
			hours++;
		}
		date.setMinutes(mins);
		date.setHours(hours);
	}
	return date;
};

AjxDateUtil.isInRange = 
function(startTime1, endTime1, startTime2, endTime2) {
	return (startTime1 < endTime2 && endTime1 > startTime2);
}

AjxDateUtil._pad = 
function(n) {
	return n < 10 ? ('0' + n) : n;
};
