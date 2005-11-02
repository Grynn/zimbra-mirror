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

// NOTE: The API for the classes in this file are inspired by the Java text
//		 formatting classes but the implementation was NOT copied or ported
//		 from the Java sources.

//
// Format class
//

/** 
 * Base class for all formats. To format an object, instantiate the
 * format of your choice and call the <code>format</code> method which
 * returns the formatted string.
 */
function AjxFormat(pattern) {
	this._pattern = pattern;
	this._segments = [];
}

// Data

AjxFormat.prototype._pattern;
AjxFormat.prototype._segments;

// Public methods

/** 
 * This method does <em>not</em> need to be overridden unless
 * the subclass doesn't use format segments and takes complete 
 * responsibility for formatting.
 */
AjxFormat.prototype.format = function(object) { 
	var s = [];
	for (var i = 0; i < this._segments.length; i++) {
		s.push(this._segments[i].format(object));
	}
	return s.join("");
}

/** Returns string representation of this object. */
AjxFormat.prototype.toString = function() { 
	var s = [];
	s.push("pattern=\"",this._pattern,'"');
	if (this._segments.length > 0) {
		s.push(", segments={ ");
		for (var i = 0; i < this._segments.length; i++) {
			if (i > 0) { s.push(", "); }
			s.push(this._segments[i].toString());
		}
		s.push(" }");
	}
	return s.join("");
}

// Static methods

AjxFormat._zeroPad = function(s, length) {
	s = typeof s == "string" ? s : String(s);
	for (var i = s.length; i < length; i++) {
		s = '0' + s;
	}
	return s;
};

//
// Format exception base class
//

AjxFormat.FormatException = function(format, message) {
	this._format = format;
	this._message = message;
}
AjxFormat.FormatException.prototype.toString = function() { return this.message; }

// Data

AjxFormat.FormatException.prototype._format;
AjxFormat.FormatException.prototype._message;

//
// Formatting exception class
//

AjxFormat.FormattingException = function(format, segment, message) {
	FormatException.call(this, format, message);
	this._segment = segment;
}
AjxFormat.FormattingException.prototype = new AjxFormat.FormatException;
AjxFormat.FormattingException.prototype.constructor = AjxFormat.FormattingException;

// Data

AjxFormat.FormattingException.prototype._segment;

//
// Segment class
//

AjxFormat.Segment = function(format, s) {
	this._parent = format;
	this._s = s;
}

// Data

AjxFormat.Segment.prototype._parent;
AjxFormat.Segment.prototype._s;

// Public methods

AjxFormat.Segment.prototype.format = function(o) { 
	return this._s; 
}

AjxFormat.Segment.prototype.toString = function() { 
	return "segment: \""+this._s+'"'; 
}

//
// Date format class
//

/**
 * The AjxDateFormat class formats Date objects according to a specified 
 * pattern. The patterns are defined the same as the SimpleDateFormat
 * class in the Java libraries.
 * <p>
 * <strong>Note:</strong>
 * The date format differs from the Java patterns in one way: the pattern
 * "EEEEE" (5 'E's) denotes a <em>short</em> weekday name. This matches
 * the extended pattern found in the Common Locale Data Repository (CLDR)
 * found at: http://www.unicode.org/cldr/.
 */
function AjxDateFormat(pattern) {
	AjxFormat.call(this, pattern);
	if (typeof pattern == "number") {
		switch (pattern) {
			case AjxDateFormat.SHORT: pattern = "TODO"; break;
			case AjxDateFormat.MEDIUM: pattern = "TODO"; break;
			case AjxDateFormat.LONG: pattern = "TODO"; break;
			case AjxDateFormat.FULL: pattern = "TODO"; break;
		}
	}	
	for (var i = 0; i < pattern.length; i++) {
		// literal
		var c = pattern.charAt(i);
		if (c == "'") {
			var head = i + 1;
			for (i++ ; i < pattern.length; i++) {
				var c = pattern.charAt(i);
				if (c == "'") {
					break;
				}
			}
			if (i == pattern.length) {
				// TODO: i18n
				throw new FormatException(this, "unterminated string literal");
			}
			var tail = i;
			var segment = new AjxFormat.TextSegment(this, pattern.substring(head, tail));
			this._segments.push(segment);
			continue;
		}

		// non-meta chars
		var head = i;
		while(i < pattern.length) {
			c = pattern.charAt(i);
			if (AjxDateFormat._META_CHARS.indexOf(c) != -1 || c == "'") {
				break;
			}
			i++;
		}
		var tail = i;
		if (head != tail) {
			var segment = new AjxFormat.TextSegment(this, pattern.substring(head, tail));
			this._segments.push(segment);
			i--;
			continue;
		}
		
		// meta char
		var head = i;
		while(++i < pattern.length) {
			if (pattern.charAt(i) != c) {
				break;
			}		
		}
		var tail = i--;
		var count = tail - head;
		var field = pattern.substr(head, count);
		var segment = null;
		switch (c) {
			case 'G': segment = new AjxFormat.TextSegment(this, "(TODO:"+field+")"); break;
			case 'y': segment = new AjxDateFormat.YearSegment(this, field); break;
			case 'M': segment = new AjxDateFormat.MonthSegment(this, field); break;
			case 'w': segment = new AjxDateFormat.WeekSegment(this, field); break;
			case 'W': segment = new AjxDateFormat.WeekSegment(this, field); break;
			case 'D': segment = new AjxDateFormat.DaySegment(this, field); break;
			case 'd': segment = new AjxDateFormat.DaySegment(this, field); break;
			case 'F': segment = new AjxDateFormat.WeekdaySegment(this, field); break;
			case 'E': segment = new AjxDateFormat.WeekdaySegment(this, field); break;
			case 'a': segment = new AjxDateFormat.AmPmSegment(this, field); break;
			case 'H': segment = new AjxDateFormat.HourSegment(this, field); break;
			case 'k': segment = new AjxDateFormat.HourSegment(this, field); break;
			case 'K': segment = new AjxDateFormat.HourSegment(this, field); break;
			case 'h': segment = new AjxDateFormat.HourSegment(this, field); break;
			case 'm': segment = new AjxDateFormat.MinuteSegment(this, field); break;
			case 's': segment = new AjxDateFormat.SecondSegment(this, field); break;
			case 'S': segment = new AjxDateFormat.SecondSegment(this, field); break;
			case 'z': segment = new AjxFormat.TextSegment(this, "(TODO:"+field+")"); break;
			case 'Z': segment = new AjxFormat.TextSegment(this, "(TODO:"+field+")"); break;
		}
		if (segment != null) {
			this._segments.push(segment);
		}
	}
}
AjxDateFormat.prototype = new AjxFormat;
AjxDateFormat.prototype.constructor = AjxDateFormat;

// Constants

AjxDateFormat.SHORT = 0;
AjxDateFormat.MEDIUM = 1;
AjxDateFormat.LONG = 2;
AjxDateFormat.FULL = 3;
AjxDateFormat.DEFAULT = AjxDateFormat.MEDIUM;

AjxDateFormat._META_CHARS = "GyMwWDdFEaHkKhmsSzZ";

AjxDateFormat._dateFormats = [
	AjxMsg.dateFormatShort, AjxMsg.dateFormatMedium, 
	AjxMsg.dateFormatLong, AjxMsg.dateFormatFull
];
AjxDateFormat._timeFormats = [
	AjxMsg.timeFormatShort, AjxMsg.timeFormatMedium, 
	AjxMsg.timeFormatLong, AjxMsg.timeFormatFull
];

AjxDateFormat._DATE_FORMATTERS = {};
AjxDateFormat._TIME_FORMATTERS = {};
AjxDateFormat._DATETIME_FORMATTERS = {};

// Static methods

AjxDateFormat.getDateInstance = function(style) {
	// lazily create formatters
	style = style || AjxDateFormat.DEFAULT;
	if (!AjxDateFormat._DATE_FORMATTERS[style]) {
		AjxDateFormat._DATE_FORMATTERS[style] = new AjxDateFormat(AjxDateFormat._dateFormats[style]);
	}
	return AjxDateFormat._DATE_FORMATTERS[style];
}

AjxDateFormat.getTimeInstance = function(style) {
	// lazily create formatters
	style = style || AjxDateFormat.DEFAULT;
	if (!AjxDateFormat._TIME_FORMATTERS[style]) {
		AjxDateFormat._TIME_FORMATTERS[style] = new AjxDateFormat(AjxDateFormat._timeFormats[style]);
	}
	return AjxDateFormat._TIME_FORMATTERS[style];
}

AjxDateFormat.getDateTimeInstance = function(dateStyle, timeStyle) {
	// lazily create formatters
	dateStyle = dateStyle || AjxDateFormat.DEFAULT;
	timeStyle = timeStyle || AjxDateFormat.DEFAULT;
	var style = dateStyle * 10 + timeStyle;
	if (!AjxDateFormat._DATETIME_FORMATTERS[style]) {
		var pattern = AjxMsg.dateTimeFormat;
		var params = [ AjxDateFormat._dateFormats[dateStyle], AjxDateFormat._timeFormats[timeStyle] ];
		
		var dateTimePattern = AjxMessageFormat.format(pattern, params);
		AjxDateFormat._DATETIME_FORMATTERS[style] = new AjxDateFormat(dateTimePattern);
	}
	return AjxDateFormat._DATETIME_FORMATTERS[style];
}

AjxDateFormat.format = function(pattern, date) {
	return new AjxDateFormat(pattern).format(date);
}

// Public methods

AjxDateFormat.prototype.toString = function() {
	return "[AjxDateFormat: "+AjxFormat.prototype.toString.call(this)+"]";
}

//
// Text segment class
//

AjxFormat.TextSegment = function(format, s) {
	AjxFormat.Segment.call(this, format, s);
}
AjxFormat.TextSegment.prototype = new AjxFormat.Segment;
AjxFormat.TextSegment.prototype.constructor = AjxFormat.TextSegment;

// Public methods

AjxFormat.TextSegment.prototype.toString = function() { 
	return "text: \""+this._s+'"'; 
}

//
// Date segment class
//

AjxDateFormat.DateSegment = function(format, s) {
	AjxFormat.Segment.call(this, format, s);
}
AjxDateFormat.DateSegment.prototype = new AjxFormat.Segment;
AjxDateFormat.DateSegment.prototype.constructor = AjxDateFormat.DateSegment;

//
// Date year segment class
//

AjxDateFormat.YearSegment = function(format, s) {
	AjxDateFormat.DateSegment.call(this, format, s);
}
AjxDateFormat.YearSegment.prototype = new AjxDateFormat.DateSegment;
AjxDateFormat.YearSegment.prototype.constructor = AjxDateFormat.YearSegment;

// Public methods

AjxDateFormat.YearSegment.prototype.format = function(date) { 
	var year = String(date.getFullYear());
	return this._s.length < 4 ? year.substr(year.length - 2) : AjxFormat._zeroPad(year, this._s.length);
}

AjxDateFormat.YearSegment.prototype.toString = function() { 
	return "dateYear: \""+this._s+'"'; 
}

//
// Date month segment class
//

AjxDateFormat.MonthSegment = function(format, s) {
	AjxDateFormat.DateSegment.call(this, format, s);
}
AjxDateFormat.MonthSegment.prototype = new AjxDateFormat.DateSegment;
AjxDateFormat.MonthSegment.prototype.constructor = AjxDateFormat.MonthSegment;

// Constants

AjxDateFormat.MonthSegment.MONTHS = {};
AjxDateFormat.MonthSegment.MONTHS[AjxDateFormat.MEDIUM] = [ 
	AjxMsg.monthMediumJan, AjxMsg.monthMediumFeb, AjxMsg.monthMediumMar,
	AjxMsg.monthMediumApr, AjxMsg.monthMediumMay, AjxMsg.monthMediumJun,
	AjxMsg.monthMediumJul, AjxMsg.monthMediumAug, AjxMsg.monthMediumSep,
	AjxMsg.monthMediumOct, AjxMsg.monthMediumNov, AjxMsg.monthMediumDec
];
AjxDateFormat.MonthSegment.MONTHS[AjxDateFormat.LONG] = [ 
	AjxMsg.monthLongJan, AjxMsg.monthLongFeb, AjxMsg.monthLongMar,
	AjxMsg.monthLongApr, AjxMsg.monthLongMay, AjxMsg.monthLongJun,
	AjxMsg.monthLongJul, AjxMsg.monthLongAug, AjxMsg.monthLongSep,
	AjxMsg.monthLongOct, AjxMsg.monthLongNov, AjxMsg.monthLongDec
];

// Public methods

AjxDateFormat.MonthSegment.prototype.format = function(date) {
	var month = date.getMonth();
	switch (this._s.length) {
		case 1: return String(month + 1);
		case 2: return AjxFormat._zeroPad(month + 1, 2);
		case 3: return AjxDateFormat.MonthSegment.MONTHS[AjxDateFormat.MEDIUM][month];
	}
	var s = AjxDateFormat.MonthSegment.MONTHS[AjxDateFormat.LONG][month];
	return AjxFormat._zeroPad(s, this._s.length);
};

AjxDateFormat.MonthSegment.prototype.toString = function() { 
	return "dateMonth: \""+this._s+'"'; 
}

//
// Date week segment class
//

AjxDateFormat.WeekSegment = function(format, s) {
	AjxDateFormat.DateSegment.call(this, format, s);
};
AjxDateFormat.WeekSegment.prototype = new AjxDateFormat.DateSegment;
AjxDateFormat.WeekSegment.prototype.constructor = AjxDateFormat.WeekSegment;

// Public methods

AjxDateFormat.WeekSegment.prototype.format = function(date) {
	var year = date.getYear();
	var month = date.getMonth();
	var day = date.getDate();
	
	var ofYear = /[w]/.test(this._s);
	var date2 = new Date(year, ofYear ? 0 : month, 1);

	var week = 0;
	while (true) {
		week++;
		if (date2.getMonth() > month || (date2.getMonth() == month && date2.getDate() >= day)) {
			break;
		}
		date2.setDate(date2.getDate() + 7);
	}

	return AjxFormat._zeroPad(week, this._s.length);
}

AjxDateFormat.WeekSegment.prototype.toString = function() { 
	return "weekMonth: \""+this._s+'"'; 
}

//
// Date day segment class
//

AjxDateFormat.DaySegment = function(format, s) {
	AjxDateFormat.DateSegment.call(this, format, s);
}
AjxDateFormat.DaySegment.prototype = new AjxDateFormat.DateSegment;
AjxDateFormat.DaySegment.prototype.constructor = AjxDateFormat.DaySegment;

// Public methods

AjxDateFormat.DaySegment.prototype.format = function(date) {
	var month = date.getMonth();
	var day = date.getDate();
	if (/[D]/.test(this._s) && month > 0) {
		var year = date.getYear();
		do {
			// set date to first day of month and then go back one day
			var date2 = new Date(year, month, 1);
			date2.setDate(0); 
			
			day += date2.getDate();
			month--;
		} while (month > 0);
	}
	return AjxFormat._zeroPad(day, this._s.length);
}

AjxDateFormat.DaySegment.prototype.toString = function() { 
	return "dateDay: \""+this._s+'"'; 
}

//
// Date weekday segment class
//

AjxDateFormat.WeekdaySegment = function(format, s) {
	AjxDateFormat.DateSegment.call(this, format, s);
};
AjxDateFormat.WeekdaySegment.prototype = new AjxDateFormat.DateSegment;
AjxDateFormat.WeekdaySegment.prototype.constructor = AjxDateFormat.WeekdaySegment;

// Constants

AjxDateFormat.WeekdaySegment.WEEKDAYS = {};
AjxDateFormat.WeekdaySegment.WEEKDAYS[AjxDateFormat.SHORT] = [ 
	AjxMsg.weekdayShortSun, AjxMsg.weekdayShortMon, AjxMsg.weekdayShortTue,
	AjxMsg.weekdayShortWed, AjxMsg.weekdayShortThu, AjxMsg.weekdayShortFri,
	AjxMsg.weekdayShortSat
];
AjxDateFormat.WeekdaySegment.WEEKDAYS[AjxDateFormat.MEDIUM] = [ 
	AjxMsg.weekdayMediumSun, AjxMsg.weekdayMediumMon, AjxMsg.weekdayMediumTue,
	AjxMsg.weekdayMediumWed, AjxMsg.weekdayMediumThu, AjxMsg.weekdayMediumFri,
	AjxMsg.weekdayMediumSat
];
AjxDateFormat.WeekdaySegment.WEEKDAYS[AjxDateFormat.LONG] = [ 
	AjxMsg.weekdayLongSun, AjxMsg.weekdayLongMon, AjxMsg.weekdayLongTue,
	AjxMsg.weekdayLongWed, AjxMsg.weekdayLongThu, AjxMsg.weekdayLongFri,
	AjxMsg.weekdayLongSat
];

// Public methods

AjxDateFormat.WeekdaySegment.prototype.format = function(date) {
	var weekday = date.getDay();
	if (/[E]/.test(this._s)) {
		var style;
		switch (this._s.length) {
			case 4: style = AjxDateFormat.LONG; break;
			case 5: style = AjxDateFormat.SHORT; break;
			default: style = AjxDateFormat.MEDIUM;
		}
		return AjxDateFormat.WeekdaySegment.WEEKDAYS[style][weekday];
	}
	return AjxFormat._zeroPad(weekday, this._s.length);
}

AjxDateFormat.DaySegment.prototype.toString = function() { 
	return "dateDay: \""+this._s+'"'; 
}

//
// Time segment class
//

AjxDateFormat.TimeSegment = function(format, s) {
	AjxFormat.Segment.call(this, format, s);
}
AjxDateFormat.TimeSegment.prototype = new AjxFormat.Segment;
AjxDateFormat.TimeSegment.prototype.constructor = AjxDateFormat.TimeSegment;

//
// Time hour segment class
//

AjxDateFormat.HourSegment = function(format, s) {
	AjxFormat.Segment.call(this, format, s);
}
AjxDateFormat.HourSegment.prototype = new AjxDateFormat.TimeSegment;
AjxDateFormat.HourSegment.prototype.constructor = AjxDateFormat.HourSegment;

// Public methods

AjxDateFormat.HourSegment.prototype.format = function(date) {
	var hours = date.getHours();
	if (hours > 12 && /[hK]/.test(this._s)) {
		hours -= 12;
	}
	/***
	// NOTE: This is commented out to match the Java formatter output
	//       but from the comments for these meta-chars, it doesn't
	//       seem right.
	if (/[Hk]/.test(this._s)) {
		hours--;
	}
	/***/
	return AjxFormat._zeroPad(hours, this._s.length);
}

AjxDateFormat.HourSegment.prototype.toString = function() { 
	return "timeHour: \""+this._s+'"'; 
}

//
// Time minute segment class
//

AjxDateFormat.MinuteSegment = function(format, s) {
	AjxFormat.Segment.call(this, format, s);
}
AjxDateFormat.MinuteSegment.prototype = new AjxDateFormat.TimeSegment;
AjxDateFormat.MinuteSegment.prototype.constructor = AjxDateFormat.MinuteSegment;

// Public methods

AjxDateFormat.MinuteSegment.prototype.format = function(date) {
	var minutes = date.getMinutes();
	return AjxFormat._zeroPad(minutes, this._s.length);
}

AjxDateFormat.MinuteSegment.prototype.toString = function() { 
	return "timeMinute: \""+this._s+'"'; 
}

//
// Time second segment class
//

AjxDateFormat.SecondSegment = function(format, s) {
	AjxFormat.Segment.call(this, format, s);
};
AjxDateFormat.SecondSegment.prototype = new AjxDateFormat.TimeSegment;
AjxDateFormat.SecondSegment.prototype.constructor = AjxDateFormat.SecondSegment;

// Public methods

AjxDateFormat.SecondSegment.prototype.format = function(date) {
	var minutes = /[s]/.test(this._s) ? date.getSeconds() : date.getMilliseconds();
	return AjxFormat._zeroPad(minutes, this._s.length);
}

AjxDateFormat.SecondSegment.prototype.toString = function() { 
	return "timeSecond: \""+this._s+'"'; 
}

//
// Time am/pm segment class
//

AjxDateFormat.AmPmSegment = function(format, s) {
	AjxFormat.Segment.call(this, format, s);
}
AjxDateFormat.AmPmSegment.prototype = new AjxDateFormat.TimeSegment;
AjxDateFormat.AmPmSegment.prototype.constructor = AjxDateFormat.AmPmSegment;

// Public methods

AjxDateFormat.AmPmSegment.prototype.format = function(date) {
	var hours = date.getHours();
	return hours < 12 ? AjxMsg.timeAm : AjxMsg.timePm;
}

AjxDateFormat.AmPmSegment.prototype.toString = function() { 
	return "timeAmPm: \""+this._s+'"'; 
}

//
// Message format class
//

function AjxMessageFormat(pattern) {
	AjxFormat.call(this, pattern);
	for (var i = 0; i < pattern.length; i++) {
		// literal
		var c = pattern.charAt(i);
		if (c == "'") {
			if (i + 1 < pattern.length && pattern.charAt(i + 1) == "'") {
				var segment = new AjxFormat.TextSegment(this, "'");
				this._segments.push(segment);
				i++;
				continue;
			}
			var head = i + 1;
			for (i++ ; i < pattern.length; i++) {
				var c = pattern.charAt(i);
				if (c == "'") {
					break;
				}
			}
			if (i == pattern.length) {
				// TODO: i18n
				throw new AjxFormat.FormatException(this, "unterminated string literal");
			}
			var tail = i;
			var segment = new AjxFormat.TextSegment(this, pattern.substring(head, tail));
			this._segments.push(segment);
			continue;
		}
		
		// non-meta chars
		var head = i;
		while(i < pattern.length) {
			c = pattern.charAt(i);
			if (c == '{' || c == "'") {
				break;
			}
			i++;
		}
		var tail = i;
		if (head != tail) {
			var segment = new AjxFormat.TextSegment(this, pattern.substring(head, tail));
			this._segments.push(segment);
			i--;
			continue;
		}
		
		// meta char
		var head = i + 1;
		while(++i < pattern.length) {
			if (pattern.charAt(i) == '}') {
				break;
			}		
		}
		var tail = i;
		var count = tail - head;
		var field = pattern.substr(head, count);
		var segment = new AjxMessageFormat.MessageSegment(this, field);		
		if (segment != null) {
			this._segments.push(segment);
		}
	}
}
AjxMessageFormat.prototype = new AjxFormat;
AjxMessageFormat.prototype.constructor = AjxMessageFormat;

// Static methods

AjxMessageFormat.format = function(pattern, params) {
	return new AjxMessageFormat(pattern).format(params);
}

// Public methods

AjxMessageFormat.prototype.format = function(params) {
	if (!(params instanceof Array)) {
		params = [ params ];
	}
	return AjxFormat.prototype.format.call(this, params);
}

AjxMessageFormat.prototype.toString = function() {
	return "[AjxMessageFormat: "+AjxFormat.prototype.toString.call(this)+"]";
}

//
// AjxMessageFormat.MessageSegment class
//

AjxMessageFormat.MessageSegment = function(format, s) {
	AjxFormat.Segment.call(this, format, s);
	var parts = s.split(',');
	this._index = Number(parts[0]);
	this._type = parts[1] || "string";
	this._style = parts[2];
	switch (this._type) {
		case "number": /*TODO*/ break;
		case "date": case "time": {
			switch (this._style) {
				case "short": this._style = AjxDateFormat.SHORT; break;
				case "medium": this._style = AjxDateFormat.MEDIUM; break;
				case "long": this._style = AjxDateFormat.LONG; break;
				case "full": this._style = AjxDateFormat.FULL; break;
			}
			break;
		}
		case "choice": /*TODO*/ break;
	}
	
}
AjxMessageFormat.MessageSegment.prototype = new AjxFormat.Segment;
AjxMessageFormat.MessageSegment.prototype.constructor = AjxMessageFormat.MessageSegment;

// Data

AjxMessageFormat.MessageSegment.prototype._index;
AjxMessageFormat.MessageSegment.prototype._type;
AjxMessageFormat.MessageSegment.prototype._style;

// Public methods

AjxMessageFormat.MessageSegment.prototype.format = function(args) {
	var object = args[this._index];
	switch (this._type) {
		case "number": {
			// TODO: Finish integer, percent, currency, pattern
			return Number(object);
		}
		case "date": {
			return AjxDateFormat.getDateInstance(this._style).format(object);
		}
		case "time": {
			return AjxDateFormat.getTimeInstance(this._style).format(object);
		}
		case "choice": /*TODO*/ break;
	}
	return String(object);
}

AjxMessageFormat.MessageSegment.prototype.toString = function() {
	return "message: \""+this._s+'"';
}

//
// AjxNumberFormat class
//

function AjxNumberFormat(pattern) {
	AjxFormat.call(this, pattern);
	// TODO
}
AjxNumberFormat.prototype = new AjxFormat;
AjxNumberFormat.prototype.constructor = AjxNumberFormat;