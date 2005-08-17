function LsDateUtil() {
};

LsDateUtil.YEAR = 1;
LsDateUtil.MONTH = 2;
LsDateUtil.WEEK = 3;
LsDateUtil.DAY = 4;

LsDateUtil.MSEC_PER_HALF_HOUR = 1800000;
LsDateUtil.MSEC_PER_HOUR = 3600000;
LsDateUtil.MSEC_PER_DAY = 24 * LsDateUtil.MSEC_PER_HOUR;
LsDateUtil._months = [LsMsg.jan, LsMsg.feb, LsMsg.mar, LsMsg.apr, LsMsg.may, LsMsg.jun,
                      LsMsg.jul, LsMsg.aug, LsMsg.sep, LsMsg.oct, LsMsg.nov, LsMsg.dec];

LsDateUtil._daysOfTheWeek = [LsMsg.sunday, LsMsg.monday, LsMsg.tuesday, LsMsg.wednesday, LsMsg.thursday, LsMsg.friday,
							 LsMsg.saturday];

LsDateUtil._daysPerMonth = {
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

LsDateUtil._init =
function() {                                           
	LsDateUtil._dateSep = LsConfig.DATE_SEP;
	LsDateUtil._timeSep = LsConfig.TIME_SEP;
	LsDateUtil._dateFmt = new Array();
	var tmp = LsConfig.DATE_FMT;
	for (var i = 0; i < tmp.length; i++)
		LsDateUtil._dateFmt[i] = tmp.substr(i, 1);
};

LsDateUtil._init();                    

/* return true if the specified date (yyyy, m (0-11), d (1-31)) 
 * is valid or not.
 */
LsDateUtil.validDate =
function(y, m, d) {
	var date = new Date(y, m, d);
	return date.getMonth() == m && date.getDate() == d;
};

/* return number of days (1-31) in specified month (yyyy, mm (0-11))
 */
LsDateUtil.daysInMonth =
function(y, m) {
	var date = new Date(y, m, 1, 12);
	date.setMonth(date.getMonth()+1);
	date.setDate(date.getDate()-1);
	return date.getDate();
};

/* return true if year is a leap year
 */
LsDateUtil.isLeapYear =
function(y) {
	return (new Date(y, 1, 29)).getMonth() == 1;
};

/**
 * rolls the month/year. If the day of month in the date passed in is greater
 * then the max day in the new month, set it to the max. The date passed in is
 * modified and also returned.
 */
LsDateUtil.roll = 
function(date, field, offset) {
	var d = date.getDate();
	 // move back to first day before rolling in case previous
	 // month/year has less days

	if (field == LsDateUtil.MONTH) {
		date.setDate(1);	
		date.setMonth(date.getMonth() + offset);
		var max = LsDateUtil.daysInMonth(date.getFullYear(), date.getMonth());
		date.setDate(Math.min(d, max));		
	} else if (field == LsDateUtil.YEAR) {
		date.setDate(1);		
		date.setFullYear(date.getFullYear() + offset);
		var max = LsDateUtil.daysInMonth(date.getFullYear(), date.getMonth());
		date.setDate(Math.min(d, max));		
	} else if (field == LsDateUtil.WEEK) {
		date.setDate(date.getDate() + 7*offset);
	} else if (field == LsDateUtil.DAY) {
		date.setDate(date.getDate() + offset);		
	} else {
		return date;
	}
	return date;
};

// Computes the difference between now and <dateMSec>. Returns a string describing
// the difference
LsDateUtil.computeDateDelta =
function(dateMSec) {
	var deltaMSec = (new Date()).getTime() - dateMSec;
	var years =  Math.floor(deltaMSec / (LsDateUtil.MSEC_PER_DAY * 365));
	if (years != 0)
		deltaMSec -= years * LsDateUtil.MSEC_PER_DAY * 365;
	var months = Math.floor(deltaMSec / (LsDateUtil.MSEC_PER_DAY * 30.42));
	if (months > 0)
		deltaMSec -= Math.floor(months * LsDateUtil.MSEC_PER_DAY * 30.42);
	var days = Math.floor(deltaMSec / LsDateUtil.MSEC_PER_DAY);
	if (days > 0)
		deltaMSec -= days * LsDateUtil.MSEC_PER_DAY;
	var hours = Math.floor(deltaMSec / LsDateUtil.MSEC_PER_HOUR);
	if (hours > 0) 
		deltaMSec -= hours * LsDateUtil.MSEC_PER_HOUR;
	var mins = Math.floor(deltaMSec / 60000);
	if (mins > 0)
		deltaMSec -= mins * 60000;
	var secs = Math.floor(deltaMSec / 1000);
	
	var deltaStr = "";
	if (years > 0) {
		deltaStr =  years + " ";
		deltaStr += (years > 1) ? LsMsg.years : LsMsg.year;
		if (years <= 3 && months > 0) {
    		deltaStr += " " + months;
    		deltaStr += " " + ((months > 1) ? LsMsg.months : LsMsg.months);
		}
	} else if (months > 0) {
		deltaStr =  months + " ";
		deltaStr += (months > 1) ? LsMsg.months : LsMsg.month;
		if (months <= 3 && days > 0) {
    		deltaStr += " " + days;
    		deltaStr += " " + ((days > 1) ? LsMsg.days : LsMsg.day);
		}
	} else if (days > 0) {
		deltaStr = days + " ";
		deltaStr += (days > 1) ? LsMsg.days : LsMsg.day;
		if (days <= 2 && hours > 0) {
    		deltaStr += " " + hours;
    		deltaStr += " " + ((hours > 1) ? LsMsg.hours : LsMsg.hour);
		}
	} else if (hours > 0) {
		deltaStr = hours + " ";
		deltaStr += (hours > 1) ? LsMsg.hours : LsMsg.hour;
		if (hours < 5 && mins > 0) {
    		deltaStr += " " + mins;
    		deltaStr += " " + ((mins > 1) ? LsMsg.minutes : LsMsg.minute);
		}
	} else if (mins > 0) {
		deltaStr = mins + " ";
		deltaStr += ((mins > 1) ? LsMsg.minutes : LsMsg.minute);
		if (mins < 5 && secs > 0) {
    		deltaStr += " " + secs;
    		deltaStr += " " + ((secs > 1) ? LsMsg.seconds : LsMsg.second);
		}
	} else {
		deltaStr = secs;
		deltaStr += " " + ((secs > 1) ? LsMsg.seconds : LsMsg.second);
	}
	deltaStr += " " + LsMsg.ago;
	return deltaStr;
};

LsDateUtil.simpleComputeDateStr = 
function(date, stringToPrepend) {
	var year, month, day;
	var idx = 0;
	var dateArr = new Array();
	var written = false;
	if (stringToPrepend != null){
		dateArr[0] = stringToPrepend;
		written = true;
	}
	for (var i = 0; i < LsDateUtil._dateFmt.length; i++) {
		switch (LsDateUtil._dateFmt[i]) {
		case 'Y':
			year = date.getFullYear() % 100;
			dateArr[idx++] =  ((written)? "/" : "");
			dateArr[idx++] = LsDateUtil._pad(year);
			written = true;
			break;
		case 'M':
			month = date.getMonth() + 1;
			dateArr[idx++] = ( (written)? "/" : "");
			dateArr[idx++] = LsDateUtil._pad(month);
			written = true;
			break;
		case 'D':
			day = date.getDate();
			dateArr[idx++] = ((written) ? "/" : "");
			dateArr[idx++] = LsDateUtil._pad(day);
			written = true;
			break;
		}
	}
	return dateArr.join("");
};

LsDateUtil.computeDateStr =
function(now, dateMSec) {
	if (dateMSec == null)
		return "";
	
	var nowMSec = now.getTime();
	var nowDay = now.getDay();
	var nowYear = now.getFullYear();
	var date = new Date(dateMSec);
	var year = date.getFullYear();
	var dateStr = "";
	if (nowMSec - dateMSec < LsDateUtil.MSEC_PER_DAY && nowDay == date.getDay()) {
		var hours = date.getHours();
		var mins = date.getMinutes();
		dateStr = LsDateUtil._pad(hours) + LsDateUtil._timeSep + LsDateUtil._pad(mins);
	} else if (year == nowYear) {
		for (var i = 0; i < LsDateUtil._dateFmt.length; i++) {
			switch (LsDateUtil._dateFmt[i]) {
				case 'M':
					dateStr = dateStr + ((dateStr != "") ? " " : "") + LsDateUtil._months[date.getMonth()];
					break;
				case 'D':
					var day = date.getDate();
					dateStr = dateStr + ((dateStr != "") ? " " : "") + LsDateUtil._pad(day);
					break;
			}
		}
	} else {
		dateStr = LsDateUtil.simpleComputeDateStr(date);
	} 
	return dateStr;
};

LsDateUtil._getHoursStr = 
function(date, pad, useMilitary) {
	var myVal = date.getHours();
	if (!useMilitary) {
		myVal %= 12;
		if (myVal == 0) myVal = 12;
	}
	return pad ? LsDateUtil._pad(myVal) : myVal;
};

LsDateUtil._getMinutesStr = 
function(date) {
	return LsDateUtil._pad(date.getMinutes());
};

LsDateUtil._getSecondsStr = 
function(date) {
	return LsDateUtil._pad(date.getSeconds());
};

LsDateUtil._getAMPM = 
function (date, upper) {
	var myHour = date.getHours();
	return (myHour < 12) ? (upper ? 'AM' : 'am') : (upper ? 'PM' : 'pm');
};

LsDateUtil._getMonthName = 
function (date) {
	return LsDateUtil._months[date.getMonth()];
};

LsDateUtil._getDate = 
function(date, pad) {
	var myVal = date.getDate();
	return pad == true ? LsDateUtil._pad(myVal) : myVal;
};

LsDateUtil._getFullYear = 
function(date) {
	return date.getFullYear();
};

LsDateUtil.getTimeStr = 
function(date, format) {
	var s = format;
	s = s.replace(/%d/g, LsDateUtil._getDate(date, true));				// zero padded day of the month
	s = s.replace(/%D/g, LsDateUtil._getDate(date, false));				// day of the month without padding
	s = s.replace(/%M/g, LsDateUtil._getMonthName(date));				// full month name
	s = s.replace(/%Y/g, LsDateUtil._getFullYear(date));				// full year
	s = s.replace(/%h/g, LsDateUtil._getHoursStr(date, false, false));	// non-padded hours
	s = s.replace(/%H/g, LsDateUtil._getHoursStr(date, true, false ));	// padded hours
	s = s.replace(/%m/g, LsDateUtil._getMinutesStr(date));				// padded minutes
	s = s.replace(/%s/g, LsDateUtil._getSecondsStr(date));				// padded seconds
	s = s.replace(/%P/g, LsDateUtil._getAMPM(date, true));				// upper case AM PM
	s = s.replace(/%p/g, LsDateUtil._getAMPM(date, false));				// lower case AM PM
	return s;
};

LsDateUtil.getRoundedMins = 
function (date, roundTo) {
	var mins = date.getMinutes();
	if (mins != 0 && roundTo)
		mins = (Math.ceil( (mins/roundTo) )) * roundTo;
	return mins;
};

LsDateUtil.roundTimeMins = 
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

LsDateUtil.isInRange = 
function(startTime1, endTime1, startTime2, endTime2) {
	return (startTime1 < endTime2 && endTime1 > startTime2);
}

LsDateUtil._pad = 
function(n) {
	return n < 10 ? ('0' + n) : n;
};
