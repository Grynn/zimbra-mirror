/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2012, 2013 Zimbra Software, LLC.
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

/**
 * 
 * @private
 */
AjxDateUtil = function() {
};

AjxDateUtil.YEAR		= 1;
AjxDateUtil.MONTH		= 2;
AjxDateUtil.WEEK		= 3;
AjxDateUtil.DAY			= 4;
AjxDateUtil.TWO_WEEKS	= 5;

AjxDateUtil.MSEC_PER_MINUTE = 60000;
AjxDateUtil.MSEC_PER_FIFTEEN_MINUTES = 900000;
AjxDateUtil.MSEC_PER_HALF_HOUR = 1800000;
AjxDateUtil.MSEC_PER_HOUR = 3600000;
AjxDateUtil.MSEC_PER_DAY = 24 * AjxDateUtil.MSEC_PER_HOUR;

AjxDateUtil.MINUTES_PER_DAY = 60 * 24;
AjxDateUtil.SECONDS_PER_DAY = 60 * 60 * 24;


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

AjxDateUtil.MAX_DAYS_PER_MONTH = 31;

AjxDateUtil.WEEK_ONE_JAN_DATE = 1;

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
	} else if (field == AjxDateUtil.TWO_WEEKS) {
		date.setDate(date.getDate() + 14*offset);
	} else {
		return date;
	}
	return date;
};

/**
 * checks whether given date is derived from DST shift
 */
AjxDateUtil.isDayShifted =
function(date) {
    var refDate = new Date(date.getTime());

    //advance it by 1 day and reset to beginning of the day
    refDate.setDate(refDate.getDate() +1)
    refDate.setHours(0,0,0,0);

    //if DST has no effect the advanced time should differ from given time
    return refDate.getTime() == date.getTime();
};

/**
 * rolls to next day. This can be used to roll to next day avoiding the daylight saving shift in time.
 */
AjxDateUtil.rollToNextDay =
function(date) {
    date.setHours(0,0,0,0);
    date.setTime(date.getTime() + AjxDateUtil.MSEC_PER_DAY);
};

// Computes the difference between now and <dateMSec>. Returns a string describing
// the difference
AjxDateUtil.computeDateDelta =
function(dateMSec) {
	var deltaMSec = (new Date()).getTime() - dateMSec;
	var durationStr = AjxDateUtil.computeDuration(deltaMSec);
	return durationStr ? (durationStr + " " + AjxMsg.ago) : null;
};

// Computes the difference between now and <dateMSec>. Returns a simplified string describing
// the difference
AjxDateUtil.agoTime =
function(dateMSec) {
	var deltaMSec = (new Date()).getTime() - dateMSec;
	var durationStr = AjxDateUtil.computeDuration(deltaMSec, false, true);
	return durationStr ? (durationStr + " " + AjxMsg.ago) : null;
};



// Returns a string describing the duration, which is in milliseconds.
AjxDateUtil.computeDuration =
function(duration, brief, simplified) {
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
		return simplified
            ? formatter.formatYears(years)
            : formatter.formatYears(years, months);
	} else if (months > 0) {
		return simplified
            ? formatter.formatMonths(months)
            : formatter.formatMonths(months, days);
	} else if (days > 0) {
		return simplified
            ? formatter.formatDays(days)
            : formatter.formatDays(days, hours);
	} else if (hours > 0) {
		return simplified
            ? formatter.formatHours(hours)
            : formatter.formatHours(hours, mins);
	} else if (mins > 0) {
		return simplified
            ? formatter.formatMinutes(mins)
            : formatter.formatMinutes(mins, secs);
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

AjxDateUtil.computeDateStrNoYear =
function(date) {
    return AjxDateUtil._dateFormatNoYear.format(date);
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

/* returns true if dateString is a valid and understandable date string
 * in compliance with the locale of the user ie. dd/mm/yy or mm/dd/yy etc.
 * Also for date strings like 1/32/2000 (that roll over to 2/1/2000), false is returned.
 */
AjxDateUtil.isValidSimpleDateStr =
function(str){
        if(!str) {return false};
        var dateValue = AjxDateUtil.getSimpleDateFormat().parse(str);
        if (!dateValue) {return false};
        var dateValueStr = AjxDateUtil.simpleComputeDateStr(dateValue);
        return (str == dateValueStr);
}

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

AjxDateUtil.getFirstDayOfWeek =
function (dt, startOfWeek) {
    startOfWeek = startOfWeek || 0;
    var dayOfWeekIndex = dt.getDay();
    var dayOfWeek = (dayOfWeekIndex - startOfWeek + 7) % 7;
    dt.setDate(dt.getDate() - dayOfWeek);
    return dt;
};

AjxDateUtil.getLastDayOfWeek =
function (dt, startOfWeek) {
    startOfWeek = startOfWeek || 0;
    var dayOfWeekIndex = dt.getDay();
    var dayOfWeek = (dayOfWeekIndex - startOfWeek + 7) % 7;
    dt.setDate(dt.getDate() - dayOfWeek + 6);
    dt.setHours(23, 59, 59, 999);
    return dt;
};

AjxDateUtil.getWeekNumber =
function(date, firstDayOfWeek, janDate, isISO8601WeekNum) {

    // Setup Defaults
    firstDayOfWeek = firstDayOfWeek || 0;
    janDate = janDate || AjxDateUtil.WEEK_ONE_JAN_DATE;
    date = date || new Date();

    date.setHours(12,0,0,0);
    var targetDate = date,
            startOfWeek,
            endOfWeek;

    if (targetDate.getDay() === firstDayOfWeek) {
        startOfWeek = targetDate;
    } else {
        startOfWeek = AjxDateUtil.getFirstDayOfWeek(targetDate, firstDayOfWeek);
    }

    var startYear = startOfWeek.getFullYear(),
            startTime = startOfWeek.getTime();

    // DST shouldn't be a problem here, math is quicker than setDate();
    endOfWeek = new Date(startOfWeek.getTime() + 6*AjxDateUtil.MSEC_PER_DAY);

    var weekNum;

    if(!isISO8601WeekNum) {
        if (startYear !== endOfWeek.getFullYear() && endOfWeek.getDate() >= janDate) {
            weekNum = 1;
        } else {
            var weekOne = (new Date(startYear, 0, janDate));
            weekOne.setHours(12,0,0,0);
            var weekOneDayOne = AjxDateUtil.getFirstDayOfWeek(weekOne, firstDayOfWeek);

            // Round days to smoothen out 1 hr DST diff
            var daysDiff  = Math.round((targetDate.getTime() - weekOneDayOne.getTime())/AjxDateUtil.MSEC_PER_DAY);

            // Calc. Full Weeks
            var rem = daysDiff % 7;
            var weeksDiff = (daysDiff - rem)/7;
            weekNum = weeksDiff + 1;
        }
        return weekNum;
    }else {

        var newYear = new Date(date.getFullYear(),0,1);
        var day = newYear.getDay() - 1;
        day = (day >= 0 ? day : day + 7);
        var dayOftheYear = Math.floor((date.getTime()-newYear.getTime() - (date.getTimezoneOffset()-newYear.getTimezoneOffset())*60000)/AjxDateUtil.MSEC_PER_DAY) + 1;

        if(day < 4)
        {
            weekNum = Math.floor((dayOftheYear+day-1)/7) + 1;
            if(weekNum > 52)
            {
                var nxtYear = new Date(date.getFullYear() + 1,0,1);
                var nxtDay = nxtYear.getDay() - 1;
                nxtDay = nxtDay >= 0 ? nxtDay : nxtDay + 7;
                weekNum = nxtDay < 4 ? 1 : 53;
            }
        }else {
            weekNum = Math.floor((dayOftheYear+day -1 )/7);
            if(weekNum == 0)
            {
                var prevYear = new Date(date.getFullYear()-1,0,1);
                var prevDay = prevYear.getDay()-1;
                prevDay = (prevDay >= 0 ? prevDay : prevDay + 7);
                weekNum = ( prevDay==3 || ( AjxDateUtil.isLeapYear(prevYear.getFullYear()) && prevDay==2 ) ) ? 53 : 52;
            }
        }
        return weekNum;
    }
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
function(serverStr, date, noSpecialUtcCase) {
	if (serverStr.charAt(8) == 'T') {
		var hh = parseInt(serverStr.substr(9,2), 10);
		var mm = parseInt(serverStr.substr(11,2), 10);
		var ss = parseInt(serverStr.substr(13,2), 10);
		if (!noSpecialUtcCase && serverStr.charAt(15) == 'Z') {
			mm += AjxTimezone.getOffset(AjxTimezone.DEFAULT, date);
		}
		date.setHours(hh, mm, ss, 0);
	}
	return date;
};

AjxDateUtil.parseISO8601Date = function(s) {
    var formatters = AjxDateUtil.__ISO8601_formats;
    if (!formatters) {
        formatters = AjxDateUtil.__ISO8601_formats = [
            new AjxDateUtil.TZDFormat("yyyy-MM-dd'T'HH:mm:ss.SZ"),
            new AjxDateUtil.TZDFormat("yyyy-MM-dd'T'HH:mm:ssZ"),
            new AjxDateUtil.TZDFormat("yyyy-MM-dd'T'HH:mmZ"),
            new AjxDateFormat("yyyy-MM-dd"),
            new AjxDateFormat("yyyy-MM"),
            new AjxDateFormat("yyyy")
        ];
    }
    for (var i = 0; i < formatters.length; i++) {
        var date = formatters[i].parse(s);
        if (date) return date;
    }
    return null;
};

AjxDateUtil.TZDFormat = function(pattern) {
    if (arguments.length == 0) return;
    AjxDateFormat.apply(this, arguments);
    var segments = this._segments || [];
    for (var i = 0; i < segments.length; i++) {
        var segment = segments[i];
        if (segment instanceof AjxDateFormat.TimezoneSegment) {
            segments[i] = new AjxDateUtil.TZDSegment(segment.toSubPattern());
        }
    }
};
AjxDateUtil.TZDFormat.prototype = new AjxDateFormat;
AjxDateUtil.TZDFormat.prototype.constructor = AjxDateUtil.TZDFormat;
AjxDateUtil.TZDFormat.prototype.toString = function() { return "TZDFormat"; };

AjxDateUtil.TZDSegment = function(pattern) {
    if (arguments.length == 0) return;
    AjxDateFormat.TimezoneSegment.apply(this, arguments);
};
AjxDateUtil.TZDSegment.prototype = new AjxDateFormat.TimezoneSegment;
AjxDateUtil.TZDSegment.prototype.constructor = AjxDateUtil.TZDSegment;
AjxDateUtil.TZDSegment.prototype.toString = function() { return "TZDSegment"; };

AjxDateUtil.TZDSegment.prototype.parse = function(o, s, i) {
    var m = /^(Z)|^(\+|\-)(\d\d):(\d\d)/.exec(s.substr(i));
    if (m) {
        var offset = new Date().getTimezoneOffset();
        if (m[1]) o.timezone = offset;
        else {
            var hours = parseInt(m[3],10), mins = parseInt(m[4],10);
            o.timezone = hours * 60 + mins;
            if (m[2] != "-") o.timezone *= -1;
            o.timezone -= offset;
        }
    }
    return i + (m ? m[0].length : 0);
};

AjxDateUtil.parseServerDateTime = 
function(serverStr, noSpecialUtcCase) {
	if (serverStr == null) return null;

	var d = new Date();
	var yyyy = parseInt(serverStr.substr(0,4), 10);
	var MM = parseInt(serverStr.substr(4,2), 10);
	var dd = parseInt(serverStr.substr(6,2), 10);
	d.setFullYear(yyyy);
	d.setMonth(MM - 1);
	d.setMonth(MM - 1); // DON'T remove second call to setMonth (see bug #3839)
	d.setDate(dd);
	AjxDateUtil.parseServerTime(serverStr, d, noSpecialUtcCase);
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
	return (secs + " " + ((secs > 1) ? AjxMsg.seconds : AjxMsg.second));
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
function(fromThisDate, thisWeekday, count) {
	count = count || 1;
	var r = new Date(fromThisDate);
	for (var i = 0; i < count; i++) {
		r = AjxDateUtil._getDateForNextWeekday(r, thisWeekday);
		if (i < count-1) {
			r.setDate(r.getDate() + 1);
		}
	}
	return r;
}

/**
 *
 * @param fromThisDate The searching work week days starting from this date
 * @param count Which occurence, like first, second.. has to be always positive
 *
 */
AjxDateUtil.getDateForNextWorkWeekDay =
function(fromThisDate, count) {
	count = count?count:1;
	var r = new Date(fromThisDate);
	for (var i = 0; i < count; i++) {
		r = AjxDateUtil._getDateForNextWorkWeekday(r);
		if (i < count-1) {
			r.setDate(r.getDate() + 1);
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
function(fromThisDate, thisWeekday, count) {
	if (count < 0 ) {
		return AjxDateUtil.getDateForPrevDay(fromThisDate, thisWeekday, -count);//-(-)  is plus
	} else {
		return AjxDateUtil.getDateForNextDay(fromThisDate, thisWeekday, count);
	}
}

/**
 *
 * @param fromThisDate The starting point
 * @param count this many positions to navigate, if negative goes in reverse, if positive goes forward
 */
AjxDateUtil.getDateForThisWorkWeekDay =
function(fromThisDate, count) {
	if (count < 0 ) {
		return AjxDateUtil.getDateForPrevWorkWeekDay(fromThisDate, -count);		//-(-)  is plus
	}else{
		return AjxDateUtil.getDateForNextWorkWeekDay(fromThisDate, count);
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
	count = count || 1;
	var r = new Date(fromThisDate);
	for (var i = 0; i < count; i++) {
		r = AjxDateUtil._getDateForPrevWeekday(r, thisWeekday);
		if (i < count-1) {
			r.setDate(r.getDate()-1);
		}
	}
	return r;
}

/**
 *
 * @param fromThisDate The searching for work week days starting from this date in reverse direction.
 * @param count Which occurence, like first, second..has to be always positive
 */

AjxDateUtil.getDateForPrevWorkWeekDay =
function(fromThisDate, count) {
	count = count || 1;
	var r = new Date(fromThisDate);
	for(var i = 0; i < count; i++) {
		r = AjxDateUtil._getDateForPrevWorkWeekday(r);
		if (i < count-1) {
			r.setDate(r.getDate()-1);
		}
	}
	return r;
}

/**
 * note - this deals with the format we save from Prefs page. Careful if using for other cases.
 * @param value
 * @return {String}
 */
AjxDateUtil.dateLocal2GMT =
function(value) {
	if (!value) { return ""; }

	var yr, mo, da, hr, mi, se; // really smart parsing.
	yr = parseInt(value.substr(0,  4), 10);
	mo = parseInt(value.substr(4,  2), 10);
	da = parseInt(value.substr(6,  2), 10);
	hr = parseInt(value.substr(8,  2), 10);
	mi = parseInt(value.substr(10, 2), 10);
	se = parseInt(value.substr(12, 2), 10);
	var date = new Date(yr, mo - 1, da, hr, mi, se, 0);
	yr = date.getUTCFullYear();
	mo = date.getUTCMonth() + 1;
	da = date.getUTCDate();
	hr = date.getUTCHours();
	mi = date.getUTCMinutes();
	se = date.getUTCSeconds();
	var a = [ yr, mo, da, hr, mi, se ];
	for (var i = a.length; --i > 0;) {
		var n = a[i];
		if (n < 10)
			a[i] = "0" + n;
	}
	return (a.join("") + "Z");
};

/**
 * note - this deals with the format we save from Prefs page. Careful if using for other cases.
 * @param value
 * @return {String}
 */
AjxDateUtil.dateGMT2Local =
function(value) {
	if (!value) { return ""; }

	var yr, mo, da, hr, mi, se; // really smart parsing.
	yr = parseInt(value.substr(0,  4), 10);
	mo = parseInt(value.substr(4,  2), 10);
	da = parseInt(value.substr(6,  2), 10);
	hr = parseInt(value.substr(8,  2), 10);
	mi = parseInt(value.substr(10, 2), 10);
	se = parseInt(value.substr(12, 2), 10);
	var date = new Date();
	date.setUTCMilliseconds(0);
	date.setUTCSeconds(se);
	date.setUTCMinutes(mi);
	date.setUTCHours(hr);
	date.setUTCDate(da);
	date.setUTCMonth(mo - 1);
	date.setUTCFullYear(yr);
	yr = date.getFullYear();
	mo = date.getMonth() + 1;
	da = date.getDate();
	hr = date.getHours();
	mi = date.getMinutes();
	se = date.getSeconds();
	var a = [yr, mo, da, hr, mi, se];
	for (var i = a.length; --i > 0;) {
		var n = a[i];
		if (n < 10)
			a[i] = "0" + n;
	}
	return (a.join("") + "Z");
};


AjxDateUtil._getDateForNextWeekday =
function(fromThisDate,thisWeekday) {
	var newDate = new Date(fromThisDate);
	var weekDay = fromThisDate.getDay();
	if (weekDay == thisWeekday) {
		return newDate;
	}
	var diff = (thisWeekday-weekDay);
	if (diff > 0) {
		newDate.setDate(fromThisDate.getDate() + diff);
	} else {
		newDate.setDate(fromThisDate.getDate() + (7 + diff));
	}
	return newDate;
}

AjxDateUtil._getDateForNextWorkWeekday =
function(fromThisDate) {
	var newDate = new Date(fromThisDate);
	var weekDay = fromThisDate.getDay();
	if (weekDay == AjxDateUtil.SUNDAY) {
		newDate.setDate(fromThisDate.getDate()+1);
	} else if (weekDay == AjxDateUtil.SATURDAY) {
		newDate.setDate(fromThisDate.getDate()+2);
	}
	return newDate;
}

AjxDateUtil._getDateForPrevWeekday =
function(fromThisDate, thisWeekday) {
	var newDate = new Date(fromThisDate);
	var weekDay = fromThisDate.getDay();
	if (weekDay == thisWeekday) {
		return newDate;
	}
	var diff = (weekDay-thisWeekday);
	if (diff > 0) {
		newDate.setDate(fromThisDate.getDate() - diff);
	} else {
		newDate.setDate(fromThisDate.getDate() - (7 + diff));
	}
	return newDate;
}

AjxDateUtil._getDateForPrevWorkWeekday =
function(fromThisDate) {
	var newDate = new Date(fromThisDate);
	var weekDay = fromThisDate.getDay();
	if (weekDay == AjxDateUtil.SUNDAY) {
		newDate.setDate(fromThisDate.getDate() - 2);
	} else if (weekDay == AjxDateUtil.SATURDAY) {
		newDate.setDate(fromThisDate.getDate() - 1);
	}
	return newDate;
}

//
// Date calculator functions
//

AjxDateUtil.calculate =
function(rule, date) {
	// initialize
	if (!AjxDateUtil.__calculate_initialized) {
		AjxDateUtil.__calculate_initialized = true;
		AjxDateUtil.__calculate_init();
	}

	var now = date || new Date;
	rule = rule.replace(/^\s*|\s*$/, "").replace(/\s*=\s*/g,"=").replace(/\s*,\s*/g,",");
	var a = rule.split(/\s+/g);
	var s, m, plusminus, number, type, amount, weekord, daynum;
	for (var i = 0; i < a.length; i++) {
		s = a[i];
		// comment
		if (s.match(AjxDateUtil.RE_COMMENT)) {
			break;
		}
		// context date
		if (s.match(AjxDateUtil.RE_NOW)) {
			date = new Date(now.getTime());
			continue;
		}
		// add
		if (m = s.match(AjxDateUtil.RE_ADD_NUMBER)) {
			plusminus = m[1];
			number = AjxDateUtil.__calculate_parseInt(m[2]);
			type = a[++i];
			amount = plusminus == '+' ? number : number * -1;
			AjxDateUtil.__calculate_add(date, type, amount);
			continue;
		}
		// set
		if (m = s.match(AjxDateUtil.RE_SET)) {
			AjxDateUtil.__calculate_set(date, m[1], m[2]);
			continue;
		}
		// try to parse as a date
		date = AjxDateFormat.parse("yyyyy-MM-dd", s);
		if (!date && (date = AjxDateFormat.parse("yyyy-MM-dd'T'hh:mm:ss'Z'", s))) {
			date.setMinutes(date.getMinutes() - date.getTimezoneOffset());
		}
		if (!date) date = AjxDateFormat.parse("yyyy-MM-dd'T'HH:mm:ss", s);
		if (!date) throw "invalid date pattern: \""+s+"\"";
	}
	return date;
};

//
// Date calculator constants
//

AjxDateUtil.S_DAYNAME = [
	AjxMsg["calc.dayname.sunday"],
	AjxMsg["calc.dayname.monday"],
	AjxMsg["calc.dayname.tuesday"],
	AjxMsg["calc.dayname.wednesday"],
	AjxMsg["calc.dayname.thursday"],
	AjxMsg["calc.dayname.friday"],
	AjxMsg["calc.dayname.saturday"]
].join("|");

AjxDateUtil.S_MONTHNAME = [
	AjxMsg["calc.monthname.january"],
	AjxMsg["calc.monthname.february"],
	AjxMsg["calc.monthname.march"],
	AjxMsg["calc.monthname.april"],
	AjxMsg["calc.monthname.may"],
	AjxMsg["calc.monthname.june"],
	AjxMsg["calc.monthname.july"],
	AjxMsg["calc.monthname.august"],
	AjxMsg["calc.monthname.september"],
	AjxMsg["calc.monthname.october"],
	AjxMsg["calc.monthname.november"],
	AjxMsg["calc.monthname.december"]
].join("|");

AjxDateUtil.S_WEEKORD = [
	AjxMsg["calc.ordinal.first"],
	AjxMsg["calc.ordinal.second"],
	AjxMsg["calc.ordinal.third"],
	AjxMsg["calc.ordinal.fourth"],
	AjxMsg["calc.ordinal.fifth"],
	AjxMsg["calc.ordinal.last"]
].join("|");

AjxDateUtil.WEEKORD_RE = [
    new RegExp("(first|"+AjxMsg["calc.ordinal.first"]+")",  "i"),
    new RegExp("(second|"+AjxMsg["calc.ordinal.second"]+")", "i"),
    new RegExp("(third|"+AjxMsg["calc.ordinal.third"]+")",  "i"),
    new RegExp("(fourth|"+AjxMsg["calc.ordinal.fourth"]+")", "i"),
    new RegExp("(last|"+AjxMsg["calc.ordinal.last"]+")",   "i")
];

// NOTE: Originally, the keywords for the date calculation rules
//       were in the message bundle so that they could be translated.
//       But while the keywords were translated, the rules were not
//       updated to use the translated keywords. So none of the date
//       matching worked in other languages. So I am reverting that
//       decision and hard-coding all of the relevant keywords. The
//       ordinals, day names, and month names still need to be
//       translated, though.

AjxMsg["calc.now"]	= "now";
AjxMsg["calc.date"]	= "date";

AjxMsg["calc.duration.year"]		= "year|years";
AjxMsg["calc.duration.month"]		= "mons|month|months";
AjxMsg["calc.duration.day"]			= "day|days";
AjxMsg["calc.duration.hour"]		= "hour|hours";
AjxMsg["calc.duration.minute"]		= "min|mins|minute|minutes";
AjxMsg["calc.duration.week"]        = "week";
AjxMsg["calc.duration.second"]		= "sec|secs|second|seconds";
AjxMsg["calc.duration.millisecond"]	= "milli|millis|millisecond|milliseconds";

AjxDateUtil.S_DURATION = [
	AjxMsg["calc.duration.year"],
	AjxMsg["calc.duration.month"],
    AjxMsg["calc.duration.week"],
	AjxMsg["calc.duration.day"],
	AjxMsg["calc.duration.hour"],
	AjxMsg["calc.duration.minute"],
	AjxMsg["calc.duration.second"],
	AjxMsg["calc.duration.millisecond"]
].join("|");

//
// Date calculator private functions
//

AjxDateUtil.__calculate_init =
function() {
	AjxDateUtil.WEEKDAYS = {};
	var weekdays = [
		"sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"
	];
	for (var i = 0; i < weekdays.length; i++) {
		var weekday = AjxMsg["calc.dayname."+weekdays[i]].split("|");
		for (var j = 0; j < weekday.length; j++) {
			AjxDateUtil.WEEKDAYS[weekday[j].toLowerCase()] = i;
		}
	}

	AjxDateUtil.MONTHNAME2MONTHNUM = {};
	var months = [
		"january", "february", "march", "april", "may", "june",
		"july", "august", "september", "october", "november", "december"
	];
	for (var i = 0; i < months.length; i++) {
		var month = AjxMsg["calc.monthname."+months[i]].split("|");
		for (var j = 0; j < month.length; j++) {
			AjxDateUtil.MONTHNAME2MONTHNUM[month[j].toLowerCase()] = i;
		}
	}

	AjxDateUtil.RE_YEAR = new RegExp("^("+AjxMsg["calc.duration.year"]+")$", "i");
	AjxDateUtil.RE_MONTH = new RegExp("^("+AjxMsg["calc.duration.month"]+")$", "i");
	AjxDateUtil.RE_WEEK = new RegExp("^("+AjxMsg["calc.duration.week"]+")$", "i");
	AjxDateUtil.RE_DAY = new RegExp("^("+AjxMsg["calc.duration.day"]+")$", "i");
	AjxDateUtil.RE_HOUR = new RegExp("^("+AjxMsg["calc.duration.hour"]+")$", "i");
	AjxDateUtil.RE_MINUTE = new RegExp("^("+AjxMsg["calc.duration.minute"]+")$", "i");
	AjxDateUtil.RE_SECOND = new RegExp("^("+AjxMsg["calc.duration.second"]+")$", "i");
	AjxDateUtil.RE_MILLISECOND = new RegExp("^("+AjxMsg["calc.duration.millisecond"]+")$", "i");

	AjxDateUtil.RE_DATE = new RegExp("^("+AjxMsg["calc.date"]+")$", "i");
	
	AjxDateUtil.RE_DAYNAME = new RegExp("^("+AjxDateUtil.S_DAYNAME+")$", "i");
	AjxDateUtil.RE_MONTHNAME = new RegExp("^("+AjxDateUtil.S_MONTHNAME+")$", "i");
	AjxDateUtil.RE_WEEKORD = new RegExp("^("+AjxDateUtil.S_WEEKORD+")$", "i");

	AjxDateUtil.RE_COMMENT = /^#/;
	AjxDateUtil.RE_NOW = new RegExp("^("+AjxMsg["calc.now"]+")$", "i");
	AjxDateUtil.RE_ADD_NUMBER = new RegExp("^([+\\-])(\\d+)$", "i");
	AjxDateUtil.RE_SET = new RegExp("^("+AjxDateUtil.S_DURATION+"|"+AjxMsg["calc.date"]+")=(.*)$", "i");
};

AjxDateUtil.__calculate_normalizeFullWidthDigit =
function(digit) {
	var charCode = "0".charCodeAt(0) + digit.charCodeAt(0) - "\uff10".charCodeAt(0);
	return String.fromCharCode(charCode);
};

/** This is needed to handle asian full-width digits. */
AjxDateUtil.__calculate_replaceFullWidthDigit =
function($0, digit) {
	return AjxDateUtil.__calculate_normalizeFullWidthDigit(digit);
};

AjxDateUtil.__calculate_parseInt =
function(s) {
	s = s.replace(/([\uFF10-\uFF19])/g, AjxDateUtil.__calculate_normalizeFullWidthDigit);
	return parseInt(s, 10);
};

AjxDateUtil.__calculate_add =
function(date, type, amount) {
	if (type.match(AjxDateUtil.RE_YEAR)) {
		date.setFullYear(date.getFullYear() + amount);
		return;
	}
	if (type.match(AjxDateUtil.RE_MONTH)) {
		var month = date.getMonth();
		date.setMonth(month + amount);
		// avoid roll
		if (Math.abs(month + amount) % 12 != date.getMonth()) {
			date.setDate(0);
		}
		return;
	}
	if (type.match(AjxDateUtil.RE_WEEK)) {
		date.setDate(date.getDate() + amount * 7);
		return;
	}
	if (type.match(AjxDateUtil.RE_DAY)) {
		date.setDate(date.getDate() + amount);
		return;
	}
	if (type.match(AjxDateUtil.RE_HOUR)) {
		date.setHours(date.getHours() + amount);
		return;
	}
	if (type.match(AjxDateUtil.RE_MINUTE)) {
		date.setMinutes(date.getMinutes() + amount);
		return;
	}
	if (type.match(AjxDateUtil.RE_SECOND)) {
		date.setSeconds(date.getSeconds() + amount);
		return;
	}
	if (type.match(AjxDateUtil.RE_MILLISECOND)) {
		date.setMilliseconds(date.getMilliseconds() + amount);
		return;
	}
	if (type.match(AjxDateUtil.RE_MONTHNAME)) {
		var monthnum = AjxDateUtil.MONTHNAME2MONTHNUM[type.toLowerCase()];
		if (monthnum < date.getMonth()) {
			amount += amount > 0 ? 0 : 1;
		}
		else if (monthnum > date.getMonth()) {
			amount += amount > 0 ? -1 : 0;
		}
		date.setFullYear(date.getFullYear() + amount, monthnum, 1);
		return;
	}
	if (type.match(AjxDateUtil.RE_DAYNAME)) {
		var daynum = AjxDateUtil.WEEKDAYS[type.toLowerCase()];
		if (daynum < date.getDay()) {
			amount += amount > 0 ? 0 : 1;
		}
		else if (daynum > date.getDay()) {
			amount += amount > 0 ? -1 : 0;
		}
		date.setDate(date.getDate() + (daynum - date.getDay()) + 7 * amount);
		return;
	}
	throw "unknown type: "+type;
};

AjxDateUtil.__calculate_add_ordinal =
function() {
	throw "TODO: not implemented";
};

AjxDateUtil.__calculate_set =
function(date, type, value) {
	var args = value.split(/,/);
	//Add support for Japanese Heisei year format represented by H{year-number}
	//The year is H23 in H23/12/31, means 2011/12/31; we get that by adding year 1988 to 23
	//For example: H23 = 23 + 1988 = 2011(English year)
	if(args[0].indexOf("H") == 0) {
		args[0] = parseInt(args[0].replace("H", "")) + 1988;
	}
	if (type.match(AjxDateUtil.RE_YEAR)) {
		args[0] = AjxDateUtil.__calculate_fullYear(args[0]); // year
		if (args[1] != null) args[1] = AjxDateUtil.__calculate_month(args[1]); // month
		if (args[2] != null) args[2] = parseInt(args[2], 10); // date
		date.setFullYear.apply(date, args);
		return;
	}
	if (type.match(AjxDateUtil.RE_MONTH)) {
		args[0] = AjxDateUtil.__calculate_month(args[0]); // month
		if (args[1] != null) args[1] = parseInt(args[1], 10); // date
		date.setMonth.apply(date, args);
		return;
	}
    if (type.match(AjxDateUtil.RE_WEEK)) {
        var ord = AjxDateUtil.__calculate_week(args[0]); // week
        var day = args[1] ? AjxDateUtil.__calculate_day(args[1]) : date.getDay(); // day

        var target;
        if (ord != -1) {
            var firstday = new Date(date.getFullYear(), date.getMonth(), 1, 12, 0, 0, 0);
            var firstdow = firstday.getDay();
            var delta = firstdow - day;

            target = new Date(firstday.getTime());
            target.setDate(1 - delta);
            if (delta > 0) {
                target.setDate(target.getDate() + 7);
            }
            target.setDate(target.getDate() + 7 * ord);
        }
        else {
            var lastday = new Date(date.getFullYear(), date.getMonth()+1, 0, 12, 0, 0, 0);

            target = new Date(lastday.getTime());
            target.setDate(target.getDate() - (target.getDay() - day));
            if (target.getMonth() != lastday.getMonth()) {
                target.setDate(target.getDate() - 7);
            }
        }

        if (target && (date.getMonth() == target.getMonth())) {
            date.setTime(target.getTime());
        }
        return;
    }
	if (type.match(AjxDateUtil.RE_DATE)) {
		args[0] = parseInt(args[0], 10); // date
		date.setDate.apply(date, args);
		return;
	}
	if (type.match(AjxDateUtil.RE_HOUR)) {
		args[0] = parseInt(args[0], 10); // hour
		if (args[1] != null) args[1] = parseInt(args[1], 10); // minutes
		if (args[2] != null) args[2] = parseInt(args[2], 10); // seconds
		if (args[3] != null) args[3] = parseInt(args[3], 10); // milliseconds
		date.setHours.apply(date, args);
		return;
	}
	if (type.match(AjxDateUtil.RE_MINUTE)) {
		args[0] = parseInt(args[0], 10); // minutes
		if (args[1] != null) args[1] = parseInt(args[1], 10); // seconds
		if (args[2] != null) args[2] = parseInt(args[2], 10); // milliseconds
		date.setMinutes.apply(date, args);
		return;
	}
	if (type.match(AjxDateUtil.RE_SECOND)) {
		args[0] = parseInt(args[0], 10); // seconds
		if (args[1] != null) args[1] = parseInt(args[1], 10); // milliseconds
		date.setSeconds.apply(date, args);
		return;
	}
	if (type.match(AjxDateUtil.RE_MILLISECOND)) {
		date.setMilliseconds.apply(date, args); // milliseconds
		return;
	}
	throw "unknown type: "+type;
};

AjxDateUtil.__calculate_fullYear =
function(value) {
	if (value.length == 2) {
		var d = new Date;
		d.setYear(parseInt(value, 10));
        var fullYear = d.getFullYear();
        if (fullYear <= AjxMsg.dateParsing2DigitStartYear) {
            value = String(fullYear + 100);
        }
        else {
            value = String(fullYear).substr(0,2) + value;
        }
	}
	return parseInt(value, 10);
};

AjxDateUtil.__calculate_month =
function(value) {
	var monthnum = AjxDateUtil.MONTHNAME2MONTHNUM[value.toLowerCase()];
	return monthnum != null ? monthnum : parseInt(value, 10) - 1;
};

AjxDateUtil.__calculate_week = function(value) {
    for (var i = 0; i < AjxDateUtil.WEEKORD_RE.length; i++) {
        if (value.match(AjxDateUtil.WEEKORD_RE[i])) {
            if (i == AjxDateUtil.WEEKORD_RE.length - 1) {
                return -1;
            }
            return i;
        }
    }
    return 0;
};

AjxDateUtil.__calculate_day =
function(value) {
	var daynum = AjxDateUtil.WEEKDAYS[value.toLowerCase()];
	return daynum != null ? daynum : parseInt(value, 10);
};
