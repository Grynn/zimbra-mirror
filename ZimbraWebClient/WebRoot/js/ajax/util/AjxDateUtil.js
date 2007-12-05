/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */


AjxDateUtil = function() {
};

AjxDateUtil.YEAR = 1;
AjxDateUtil.MONTH = 2;
AjxDateUtil.WEEK = 3;
AjxDateUtil.DAY = 4;

AjxDateUtil.MSEC_PER_FIFTEEN_MINUTES = 900000;
AjxDateUtil.MSEC_PER_HALF_HOUR = 1800000;
AjxDateUtil.MSEC_PER_HOUR = 3600000;
AjxDateUtil.MSEC_PER_DAY = 24 * AjxDateUtil.MSEC_PER_HOUR;

AjxDateUtil.WEEKDAY_SHORT = AjxDateFormat.WeekdaySegment.WEEKDAYS[AjxDateFormat.SHORT];
AjxDateUtil.WEEKDAY_MEDIUM = AjxDateFormat.WeekdaySegment.WEEKDAYS[AjxDateFormat.MEDIUM];
AjxDateUtil.WEEKDAY_LONG = AjxDateFormat.WeekdaySegment.WEEKDAYS[AjxDateFormat.LONG];

AjxDateUtil.MONTH_SHORT = AjxDateFormat.MonthSegment.MONTHS[AjxDateFormat.SHORT];
AjxDateUtil.MONTH_MEDIUM = AjxDateFormat.MonthSegment.MONTHS[AjxDateFormat.MEDIUM];
AjxDateUtil.MONTH_LONG = AjxDateFormat.MonthSegment.MONTHS[AjxDateFormat.LONG];

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
	AjxDateUtil._dateFormat = AjxDateFormat.getDateInstance(AjxDateFormat.SHORT).clone();
	var segments = AjxDateUtil._dateFormat.getSegments();
	for (var i = 0; i < segments.length; i++) {
		if (segments[i] instanceof AjxDateFormat.YearSegment) {
			segments[i] = new AjxDateFormat.YearSegment(AjxDateUtil._dateFormat, "yyyy");
		}
	}
	AjxDateUtil._dateTimeFormat = 
		new AjxDateFormat(AjxDateUtil._dateFormat.toPattern() + " " + AjxDateFormat.getTimeInstance(AjxDateFormat.SHORT));
	
	AjxDateUtil._dateFormatNoYear = new AjxDateFormat(AjxMsg.formatDateMediumNoYear);
};

AjxDateUtil._init();                    

/* return true if the specified date (yyyy|yy, m (0-11), d (1-31)) 
 * is valid or not.
 */
AjxDateUtil.validDate =
function(y, m, d) {
	var date = new Date(y, m, d);
	var year = y > 999 ? date.getFullYear() : date.getYear();
	return date.getMonth() == m && date.getDate() == d && year == y;
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

/* returns true if user's locale uses 24-hour time
 */
AjxDateUtil.isLocale24Hour =
function() {
	// XXX: is there better/easier way to determine this?!
	var timeFormatter = AjxDateFormat.getTimeInstance(AjxDateFormat.SHORT);
	var len = timeFormatter._segments.length;
	for (var j = 0; j < len; j++) {
		if (timeFormatter._segments[j]._s == "a")
			return false;
	}
	return true;
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
	var durationStr = AjxDateUtil.computeDuration(deltaMSec);
    return durationStr ? (durationStr + " " + AjxMsg.ago) : null;
};

// Returns a string describing the duration, which is in milliseconds.
AjxDateUtil.computeDuration =
function(duration, brief) {
	// bug fix #2203 - if delta is less than zero, dont bother computing
	if (duration < 0) return null;

	var years =  Math.floor(duration / (AjxDateUtil.MSEC_PER_DAY * 365));
	if (years != 0)
		duration -= years * AjxDateUtil.MSEC_PER_DAY * 365;
	var months = Math.floor(duration / (AjxDateUtil.MSEC_PER_DAY * 30.42));
	if (months > 0)
		duration -= Math.floor(months * AjxDateUtil.MSEC_PER_DAY * 30.42);
	var days = Math.floor(duration / AjxDateUtil.MSEC_PER_DAY);
	if (days > 0)
		duration -= days * AjxDateUtil.MSEC_PER_DAY;
	var hours = Math.floor(duration / AjxDateUtil.MSEC_PER_HOUR);
	if (hours > 0) 
		duration -= hours * AjxDateUtil.MSEC_PER_HOUR;
	var mins = Math.floor(duration / 60000);
	if (mins > 0)
		duration -= mins * 60000;
	var secs = Math.floor(duration / 1000);

	var formatter = brief ? AjxDurationFormatConcise : AjxDurationFormatVerbose;
	if (years > 0) {
		return formatter.formatYears(years, months);
	} else if (months > 0) {
		return formatter.formatMonths(months, days);
	} else if (days > 0) {
		return formatter.formatDays(days, hours);
	} else if (hours > 0) {
		return formatter.formatHours(hours, mins);
	} else if (mins > 0) {
		return formatter.formatMinutes(mins, secs);
	} else {
		return formatter.formatSeconds(secs);
	}
};

AjxDateUtil.simpleComputeDateStr = 
function(date, stringToPrepend) {
	var dateStr = AjxDateUtil._dateFormat.format(date);
	return stringToPrepend ? stringToPrepend + dateStr : dateStr;
};
AjxDateUtil.simpleParseDateStr =
function(dateStr) {
	return AjxDateUtil._dateFormat.parse(dateStr);
};

AjxDateUtil.simpleComputeDateTimeStr = 
function(date, stringToPrepend) {
	var dateTimeStr = AjxDateUtil._dateTimeFormat.format(date);
	return stringToPrepend ? stringToPrepend + dateTimeStr : dateTimeStr;
};
AjxDateUtil.simpleParseDateTimeStr =
function(dateTimeStr) {
	return AjxDateUtil._dateTimeFormat.parse(dateTimeStr);
};

AjxDateUtil.longComputeDateStr = 
function(date) {
	var formatter = AjxDateFormat.getDateInstance(AjxDateFormat.FULL);
	return formatter.format(date);
}

AjxDateUtil.computeDateStr =
function(now, dateMSec) {
	if (dateMSec == null)
		return "";

	var date = new Date(dateMSec);
	if (now.getTime() - dateMSec < AjxDateUtil.MSEC_PER_DAY &&
		now.getDay() == date.getDay()) {
		return AjxDateUtil.computeTimeString(date);
	}

	if (now.getFullYear() == date.getFullYear()) {
		return AjxDateUtil._dateFormatNoYear.format(date);
	}

	return AjxDateUtil.simpleComputeDateStr(date);
};


// Example output: "Today, 9:44 AM" "Yesterday, 12:22 PM" "Sun, 1/11/01 1:11 PM"
AjxDateUtil.computeWordyDateStr =
function(now, dateMSec) {
	if (dateMSec == null) {
		return "";
	}

	var date = new Date(dateMSec);
	if (now.getTime() - dateMSec < AjxDateUtil.MSEC_PER_DAY && now.getDay() == date.getDay()) {
		if (!AjxDateUtil._wordyDateToday) {
			AjxDateUtil._wordyDateToday = new AjxDateFormat(AjxMsg.formatWordyDateToday);
		}
		return AjxDateUtil._wordyDateToday.format(date);
	} else if ((now.getTime() - dateMSec) < (2 * AjxDateUtil.MSEC_PER_DAY) && (now.getDay() - 1) == date.getDay()) {
		if (!AjxDateUtil._wordyDateYesterday) {
			AjxDateUtil._wordyDateYesterday = new AjxDateFormat(AjxMsg.formatWordyDateYesterday);
		}
		return AjxDateUtil._wordyDateYesterday.format(date);
	} else {
		if (!AjxDateUtil._wordyDate) {
			AjxDateUtil._wordyDate = new AjxDateFormat(AjxMsg.formatWordyDate);
		}
		return AjxDateUtil._wordyDate.format(date);
	}
};

AjxDateUtil.computeTimeString =
function(date) {
	var formatter = AjxDateFormat.getTimeInstance(AjxDateFormat.SHORT);
	return formatter.format(date);
};

AjxDateUtil.computeDateTimeString =
function(date) {
	var formatter = AjxDateFormat.getDateTimeInstance(AjxDateFormat.LONG);
	return formatter.format(date);
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

AjxDateUtil._getMonth = 
function(date, pad) {
	var myMonth = date.getMonth() + 1;
	if (pad) {
		return AjxDateUtil._pad(myMonth);
	} else {
		return myMonth;
	}
};

AjxDateUtil._getDate = 
function(date, pad) {
	var myVal = date.getDate();
	return pad ? AjxDateUtil._pad(myVal) : myVal;
};

AjxDateUtil._getWeekday =
function (date) {
	var myVal = date.getDay();
	return AjxDateUtil.WEEKDAY_LONG[myVal];
};

// Returns "Mon", "Tue", etc.
AjxDateUtil._getWeekdayMedium =
function (date) {
	var myVal = date.getDay();
	return AjxDateUtil.WEEKDAY_MEDIUM[myVal];
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
	s = s.replace(/%n/g, AjxDateUtil._getMonth(date, true));		    // zero padded month
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

AjxDateUtil.getSimpleDateFormat =
function() {
	return AjxDateUtil._dateFormat;
};

/**
 * The following are helper routines for processing server date/time which comes
 * in this format: YYYYMMDDTHHMMSSZ
*/
AjxDateUtil.getServerDate = 
function(date) {
	if (!AjxDateUtil._serverDateFormatter) {
		AjxDateUtil._serverDateFormatter = new AjxDateFormat("yyyyMMdd");
	}
	return AjxDateUtil._serverDateFormatter.format(date);
};

AjxDateUtil.getServerDateTime = 
function(date, useUTC) {
	var newDate = date;
	var formatter = null;

	if (useUTC) {
		if (!AjxDateUtil._serverDateTimeFormatterUTC) {
			AjxDateUtil._serverDateTimeFormatterUTC = new AjxDateFormat("yyyyMMdd'T'HHmmss'Z'");
		}
		formatter = AjxDateUtil._serverDateTimeFormatterUTC;
		// add timezone offset to this UTC date
		newDate = new Date(date.getTime());
		newDate.setMinutes(newDate.getMinutes() + newDate.getTimezoneOffset());
	} else {
		if (!AjxDateUtil._serverDateTimeFormatter) {
			AjxDateUtil._serverDateTimeFormatter = new AjxDateFormat("yyyyMMdd'T'HHmmss");
		}
		formatter = AjxDateUtil._serverDateTimeFormatter;
	}

	return formatter.format(newDate);
};

AjxDateUtil.parseServerTime = 
function(serverStr, date) {
	if (serverStr.charAt(8) == 'T') {
		var hh = parseInt(serverStr.substr(9,2), 10);
		var mm = parseInt(serverStr.substr(11,2), 10);
		var ss = parseInt(serverStr.substr(13,2), 10);
		if (serverStr.charAt(15) == 'Z') {
			mm += AjxTimezone.getOffset(AjxTimezone.DEFAULT, date);
		}
		date.setHours(hh, mm, ss, 0);
	}
	return date;
};

AjxDateUtil.parseServerDateTime = 
function(serverStr) {
	if (serverStr == null) return null;

	var d = new Date();
	var yyyy = parseInt(serverStr.substr(0,4), 10);
	var MM = parseInt(serverStr.substr(4,2), 10);
	var dd = parseInt(serverStr.substr(6,2), 10);
	d.setFullYear(yyyy);
	d.setMonth(MM - 1);
	d.setMonth(MM - 1); // DON'T remove second call to setMonth (see bug #3839)
	d.setDate(dd);
	AjxDateUtil.parseServerTime(serverStr, d);
	return d;
};

AjxDateUtil._pad = 
function(n) {
	return n < 10 ? ('0' + n) : n;
};

AjxDurationFormatVerbose = function() { }

AjxDurationFormatVerbose.formatYears =
function(years, months) {
	var deltaStr =  years + " ";
	deltaStr += (years > 1) ? AjxMsg.years : AjxMsg.year;
	if (years <= 3 && months > 0) {
		deltaStr += " " + months;
		deltaStr += " " + ((months > 1) ? AjxMsg.months : AjxMsg.months);
	}
	return deltaStr;
};

AjxDurationFormatVerbose.formatMonths =
function(months, days) {
	var deltaStr =  months + " ";
	deltaStr += (months > 1) ? AjxMsg.months : AjxMsg.month;
	if (months <= 3 && days > 0) {
		deltaStr += " " + days;
		deltaStr += " " + ((days > 1) ? AjxMsg.days : AjxMsg.day);
	}
	return deltaStr;
};

AjxDurationFormatVerbose.formatDays =
function(days, hours) {
	var deltaStr = days + " ";
	deltaStr += (days > 1) ? AjxMsg.days : AjxMsg.day;
	if (days <= 2 && hours > 0) {
		deltaStr += " " + hours;
		deltaStr += " " + ((hours > 1) ? AjxMsg.hours : AjxMsg.hour);
	}
	return deltaStr;
};

AjxDurationFormatVerbose.formatHours =
function(hours, mins) {
	var deltaStr = hours + " ";
	deltaStr += (hours > 1) ? AjxMsg.hours : AjxMsg.hour;
	if (hours < 5 && mins > 0) {
		deltaStr += " " + mins;
		deltaStr += " " + ((mins > 1) ? AjxMsg.minutes : AjxMsg.minute);
	}
	return deltaStr;
};

AjxDurationFormatVerbose.formatMinutes =
function(mins, secs) {
	var deltaStr = mins + " ";
	deltaStr += ((mins > 1) ? AjxMsg.minutes : AjxMsg.minute);
	if (mins < 5 && secs > 0) {
		deltaStr += " " + secs;
		deltaStr += " " + ((secs > 1) ? AjxMsg.seconds : AjxMsg.second);
	}
	return deltaStr;
};

AjxDurationFormatVerbose.formatSeconds =
function(secs) {
	var deltaStr = secs + " " + ((secs > 1) ? AjxMsg.seconds : AjxMsg.second);
	return deltaStr;
};

AjxDurationFormatConcise = function() { }

AjxDurationFormatConcise.formatYears =
function(years, months) {
	return this._format(years, months);
};

AjxDurationFormatConcise.formatMonths =
function(months, days) {
	return this._format(months, days);
};

AjxDurationFormatConcise.formatDays =
function(days, hours) {
	return this._format(days, hours);
};

AjxDurationFormatConcise.formatHours =
function(hours, mins) {
	return this._format(hours, mins);
};

AjxDurationFormatConcise.formatMinutes =
function(mins, secs) {
	return this._format(mins, secs);
};

AjxDurationFormatConcise.formatSeconds =
function(secs) {
	return this._format(0, secs);
};

AjxDurationFormatConcise._format =
function(a, b) {
	var i = 0;
	var result = [];
	result[i++] = a;
	result[i++] = ':';
	if (b < 10) {
		result[i++] = '0';
	}
	result[i++] = b;
	return result.join('');
};

/**
 * Added more utility functions for date finding and navigating
 */

AjxDateUtil.SUNDAY = 0;
AjxDateUtil.MONDAY = 1;
AjxDateUtil.TUESDAY = 2;
AjxDateUtil.WEDNESDAY = 3;
AjxDateUtil.THURSDAY = 4;
AjxDateUtil.FRIDAY = 5;
AjxDateUtil.SATURDAY = 6;                                                                              

/**
 *
 * @param fromThisDate The searching starts from this date.
 * @param thisWeekday  The day to find ( eg. AjxDateUtil.SUNDAY)
 * @param count Which occurence, like first, second.. has to be always positive
 * 
 */
AjxDateUtil.getDateForNextDay =
function(fromThisDate,thisWeekday,count) {
    count = count?count:1;
    var r = new Date(fromThisDate);
    for(var i=0;i<count;i++){
        r = AjxDateUtil._getDateForNextWeekday(r,thisWeekday);
        if(i<count-1){
            r.setDate(r.getDate()+1);
        }
    }
    return r;
}
/**
 *
 * @param fromThisDate The starting point
 * @param thisWeekday  The day to find
 * @param count this many positions to navigate, if negative goes in reverse, if positive goes forward
 */
AjxDateUtil.getDateForThisDay =
function(fromThisDate,thisWeekday,count) {
    if(count < 0 ){
        return AjxDateUtil.getDateForPrevDay(fromThisDate,thisWeekday,-count); //-(-)  is plus
    }else{
        return AjxDateUtil.getDateForNextDay(fromThisDate,thisWeekday,count);
    }
}

/**
 *
 * @param fromThisDate The searching starts from this date in reverse direction. 
 * @param thisWeekday  The day to find ( eg. AjxDateUtil.SUNDAY)
 * @param count Which occurence, like first, second..has to be always positive
 */

AjxDateUtil.getDateForPrevDay =
function(fromThisDate,thisWeekday,count) {
    count = count?count:1;
    var r = new Date(fromThisDate);
    for(var i=0;i<count;i++){
        r = AjxDateUtil._getDateForPrevWeekday(r,thisWeekday);
        if(i<count-1){
            r.setDate(r.getDate()-1);
        }
    }
    return r;
}

AjxDateUtil._getDateForNextWeekday =
function(fromThisDate,thisWeekday) {
    var newDate = new Date(fromThisDate);
    var weekDay = fromThisDate.getDay();
    if(weekDay==thisWeekday){
        return newDate;
    }
   var diff = (thisWeekday-weekDay);
    if(diff > 0){
        newDate.setDate(fromThisDate.getDate() + diff);
    }else{
        newDate.setDate(fromThisDate.getDate() + (7 + diff));
    }
    return newDate;
}


AjxDateUtil._getDateForPrevWeekday =
function(fromThisDate,thisWeekday) {
    var newDate = new Date(fromThisDate);
    var weekDay = fromThisDate.getDay();
    if(weekDay==thisWeekday){
        return newDate;
    }
    var diff = (weekDay-thisWeekday);
    if(diff > 0){
        newDate.setDate(fromThisDate.getDate()-diff);
    }else{
        newDate.setDate(fromThisDate.getDate()- (7 + diff));
    }
    return newDate;
}

