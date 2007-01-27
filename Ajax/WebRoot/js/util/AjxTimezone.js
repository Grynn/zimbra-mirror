/*
 * Copyright (C) 2006, The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/**
 * This constructor is used to create a representation of a timezone
 * rule. Each timezone has, at a minimum, a unique identifier and the
 * offset to UTC in standard time. If the timezone defines daylight
 * savings time, then additional information must be provided (e.g.
 * when the DST takes effect and it's offset to UTC).
 * <p>
 * Both the standard and daylight information are specified as objects
 * with the following properties:
 * <dl>
 * <dt> offset
 *   <dd> The offset to UTC (in minutes)
 * <dt> mon
 *   <dd> The transition month of the year (January = 1, ...).
 * <dt> week
 *   <dd> The transition week of the month (First = 1, ..., Fourth = 4,
 *        Last = -1).
 * <dt> wkday
 *   <dd> The transition day of the week (Sunday = 1, ...).
 * <dt> mday
 *   <dd> The transition day of the month (1, ... 31).
 * <dt> hour
 *   <dd> The transition hour (midnight = 0, noon = 12, ...).
 * <dt> min
 *   <dd> The transition minute.
 * <dt> sec
 *   <dt> The transition second (which is usually 0).
 * </dl>
 *
 * <h5>Notes</h5>
 * <ul>
 * <li> Timezones with no DST only specify an id and a standard info
 *      object with a single "offset" property.
 * <li> Timezones with a DST <em>must</em> provide standard and
 *      daylight info objects.
 * <li> If timezone has DST, then the following properties of the
 *      standard and daylight info objects are <em>required</em>:
 *      offset, month, hour, min, sec.
 * <li> Transition dates are specified in only one of the following ways:
 *   <ul>
 *   <li> by specifying a specific date of the year (e.g. March 10);
 *   <li> or by specifying the day of a specific week within some
 *        month (e.g. Second Wednesday, Last Saturday, etc).
 *   </ul>
 * <li> If the transition date is specified as a specific date of the
 *      year, then the following field in the standard and/or daylight
 *      info objects are <em>required</em>: mday.
 * <li> If the transition date is specified as the day of a specific
 *      week, then the following fields in the standard and/or daylight
 *      info objects are <em>required</em>: week, wkday.
 * </ul>
 *
 * <h5>Examples</h5>
 * <dl>
 * <dt> Timezone with no DST
 *   <dd>
 *   <pre>
 *   var timezone = new AjxTimezone("My Timezone", { offset: -480 })
 *   </pre>
 * <dt> US/Pacific, 2007
 *   <dd>
 *   <pre>
 *   var standard = {
 *     offset: -480,
 *     mon: 11, week: 1, wkday: 1,
 *     hour: 2, min: 0, sec: 0    
 *   };
 *   var daylight = {
 *     offset: -420,
 *     mon: 3, week: 2, wkday: 1,
 *     hour: 2, min: 0, sec: 0
 *   };
 *   var timezone = new AjxTimezone("My Timezone", standard, daylight);
 *   </pre>
 * <dt> Custom US/Pacific using 11 Mar 2007 and 2 Dec 2007
 *   <dd>
 *   <pre>
 *   var standard = {
 *     offset: -480,
 *     mon: 11, mday: 2,
 *     hour: 2, min: 0, sec: 0
 *   };
 *   var daylight = {
 *     offset: -420,
 *     mon: 3, mday: 11,
 *     hour: 2, min: 0, sec: 0
 *   };
 *   var timezone = new AjxTimezone("My Timezone", standard, daylight);
 *   </pre>
 * </dl>
 * <p>
 * <strong>Note:</strong>
 * Specifying a transition date using a specific date of the year
 * <em>should</em> be avoided.
 *
 * <hr>
 *
 * <p>
 * This class stores mappings between client and server identifiers for
 * timezones as well as attempting to guess the default timezone. The 
 * application can override this value, through a user preference perhaps, 
 * by setting the <code>DEFAULT</code> property's value. The default 
 * timezone is specified using the client identifier (e.g. "US/Pacific").
 */
function AjxTimezone() {}

//
// Static methods
//

AjxTimezone.createMDayTransition = function(date, offset) {
    return {
        offset: offset != null ? offset : date.getTimezoneOffset(),
        mon: date.getMonth() + 1,
        mday: date.getDate(),
        hour: date.getHours(),
        min: date.getMinutes(),
        sec: date.getSeconds()
    };
};
AjxTimezone.createWkDayTransition = function (date, offset) {
    var mon = date.getMonth() + 1;
    var monDay = date.getDate(); 
    var week = Math.floor((monDay - 1) / 7);
    // NOTE: This creates a date of the *last* day of specified month by
    //       setting the month to *next* month and setting day of month
    //       to zero (i.e. the day *before* the first day).
    var count = new Date(new Date(date.getTime()).setMonth(mon, 0)).getDate();
    var last = count - monDay < 7;

    return {
        offset: offset != null ? offset : date.getTimezoneOffset(),
        mon: mon,
        week: last ? -1 : week + 1,
        wkday: date.getDay() + 1,
        hour: date.getHours(),
        min: date.getMinutes(),
        sec: date.getSeconds()
    };
};

AjxTimezone.getServerId = function(clientId) {
	return AjxTimezone._CLIENT2SERVER[clientId] || clientId;
};
AjxTimezone.getClientId = function(serverId) {
	return AjxTimezone._SERVER2CLIENT[serverId] || serverId;
};

AjxTimezone.getShortName = function(clientId) {
	var rule = AjxTimezone.getRule(clientId);
	if (!rule.shortName) {
		rule.shortName = ["GMT",AjxTimezone._SHORT_NAMES[clientId]].join("");
	}
	return rule.shortName;
};
AjxTimezone.getMediumName = function(clientId) {
	var rule = AjxTimezone.getRule(clientId);
	if (!rule.mediumName) {
		// NOTE: It's open to debate whether the medium name (which
		//       is used for drop-down timezone selection) should be
		//       shown translated. The problem, though, is that the
		//       translated names that we get from Java are long and
		//       can change depending on whether the current date is
		//       in daylight savings time or not. The identifiers,
		//       on the other hand, are clear and concise with the
		//       downside that they are only in English.
		rule.mediumName = ['(',AjxTimezone.getShortName(clientId),') ',clientId].join("");
	}
	return rule.mediumName;
};
AjxTimezone.getLongName = AjxTimezone.getMediumName;

AjxTimezone.getRule = function(clientId) {
	var rule = AjxTimezone._CLIENT2RULE[clientId];
    if (!rule) {
        // try to find the rule treating the clientId as the serverId
        clientId = AjxTimezone._SERVER2CLIENT[clientId];
        rule = AjxTimezone._CLIENT2RULE[clientId];
    }
    return rule;
};

AjxTimezone.getOffset = function(clientId, date) {
	var rule = AjxTimezone.getRule(clientId);
	var offset = rule ? rule.standard.offset : 0;
	if (rule && rule.daylight) {
		var month = date.getMonth();
		var day = date.getDate();
        var stdTrans = rule.standard.trans;
        var dstTrans = rule.daylight.trans;
        if ((month == dstTrans[1] && day >= dstTrans[2]) ||
			(month == stdTrans[1] && day < stdTrans[2]) ||
			(month > dstTrans[1] && month < stdTrans[1])) {
			offset = dstOffset;
		}
	}
	return offset;
};

AjxTimezone.guessMachineTimezone = function() {
	return AjxTimezone._guessMachineTimezone().clientId;
};

AjxTimezone.getAbbreviatedZoneChoices = function() {
	if (!AjxTimezone._ABBR_ZONE_OPTIONS) {
		AjxTimezone._ABBR_ZONE_OPTIONS = [];
		for (var clientId in AjxTimezone._CLIENT2SERVER) {
			var option = {
				displayValue: AjxTimezone.getMediumName(clientId),
				selectedValue: clientId,
				value: AjxTimezone._CLIENT2SERVER[clientId]
			};
			AjxTimezone._ABBR_ZONE_OPTIONS.push(option);
		}
		AjxTimezone._ABBR_ZONE_OPTIONS.sort(AjxTimezone._BY_OFFSET);
	}
	return AjxTimezone._ABBR_ZONE_OPTIONS;
};
AjxTimezone._BY_OFFSET = function(a, b) {
	var arule = AjxTimezone._CLIENT2RULE[AjxTimezone._SERVER2CLIENT[a.value]];
	var brule = AjxTimezone._CLIENT2RULE[AjxTimezone._SERVER2CLIENT[b.value]];
	// sort by offset and then by name
	var delta = arule.standard.offset - brule.standard.offset;
	if (delta == 0) {
		var aname = arule.serverId;
		var bname = brule.serverId;
		if (aname < bname) delta = -1;
		else if (aname > bname) delta = 1;
	}
	return delta;
};

// Constants

/**
 * Client identifier for GMT.
 * <p>
 * <strong>Note:</strong>
 * UK observes daylight savings time so this constant should
 * <em>not</em> be used as the reference point (i.e. UTC) --
 * use {@link AjxTimezone.GMT_NO_DST} instead. The name of
 * this constant is historical.
 */
AjxTimezone.GMT = "Europe/London";

/**
 * Client identifier for GMT with no daylight savings time.
 * <p>
 * <strong>Note:</strong>
 * This is a temporary solution at best because at some point in
 * the future, this timezone may observe daylight savings time.
 * For a real solution, there should be a UTC entry in the list
 * of known timezones.
 */
AjxTimezone.GMT_NO_DST = "Africa/Casablanca";

/**
 * <strong>Note:</strong>
 * Do NOT change this value because it is used to reference messages.
 */
AjxTimezone.AUTO_DETECTED = "Auto-Detected";

/**
 * The default timezone is set by guessing the machine timezone later
 * in this file. See the static initialization section below for details.
 */
AjxTimezone.DEFAULT;
AjxTimezone.DEFAULT_RULE;

AjxTimezone._CLIENT2SERVER = {};
AjxTimezone._SERVER2CLIENT = {};
AjxTimezone._SHORT_NAMES = {};
AjxTimezone._CLIENT2RULE = {};

/** 
 * The data is specified using the server identifiers for historical
 * reasons. Perhaps in the future we'll use the client (i.e. Java)
 * identifiers on the server as well.
 */
AjxTimezone.STANDARD_RULES = [];
AjxTimezone.DAYLIGHT_RULES = [];
(function() {
    for (var i = 0; i < AjxTimezoneData.TIMEZONE_RULES.length; i++) {
        var rule = AjxTimezoneData.TIMEZONE_RULES[i];
        var array = rule.daylight ? AjxTimezone.DAYLIGHT_RULES : AjxTimezone.STANDARD_RULES;
        array.push(rule);
    }
})();

/**
 * One problem with firefox, is if the timezone on the machine changes,
 * the browser isn't updated. You have to restart firefox for it to get the 
 * new machine timezone.
 * <p>
 * <strong>Note:</strong>
 * It looks like the current versions of FF always reflect the current
 * timezone w/o needing to restart the browser.
 */
AjxTimezone._guessMachineTimezone = 
function() {
	var dec1 = new Date(AjxTimezoneData.TRANSITION_YEAR, 12, 1, 0, 0, 0);
	var jun1 = new Date(AjxTimezoneData.TRANSITION_YEAR, 6, 1, 0, 0, 0);
	var dec1offset = dec1.getTimezoneOffset();
	var jun1offset = jun1.getTimezoneOffset();
	var pos = ((dec1.getHours() - dec1.getUTCHours()) > 0);
	if (!pos) {
		dec1offset = dec1offset * -1;
		jun1offset = jun1offset * -1;
	}

	// if the offset for jun is the same as the offset in december,
	// then we have a timezone that doesn't deal with daylight savings.
	if (jun1offset == dec1offset) {
		var rules = AjxTimezone.STANDARD_RULES;
 		for (var i = 0; i < rules.length ; ++i ) {
            var rule = rules[i];
            if (rule.standard.offset == jun1offset) {
				return rule;
			}
		}
	}

    // we need to find a rule that matches both offsets
    else {
		var rules = AjxTimezone.DAYLIGHT_RULES;
		var dst = Math.max(dec1offset, jun1offset);
		var std = Math.min(dec1offset, jun1offset);
 		for (var i = 0; i < rules.length ; ++i ) {
			var rule = rules[i];
			if (rule.standard.offset == std && rule.daylight.offset == dst) {
				if (AjxTimezone._compareRules(rule, std, dst, pos)) {
					return rule;
				}
			}
		}
	}

    // generate default rule
    return AjxTimezone._generateDefaultRule(pos);
};

AjxTimezone._compareRules = 
function(rule, std, dst, pos) {
	var equal = false;
    var stdTrans = rule.standard.trans;
    var d = new Date(stdTrans[0], stdTrans[1] - 1, (stdTrans[2] - 1)).getTimezoneOffset();
	var s = new Date(stdTrans[0], stdTrans[1] - 1, (stdTrans[2] + 1)).getTimezoneOffset();
	if (!pos) {
		s = s * -1;
		d = d * -1;
	}
	if ( (std == s) && (dst == d) ) {
        var dayTrans = rule.daylight.trans;
        s = new Date(dayTrans[0], dayTrans[1] - 1, (dayTrans[2] - 1)).getTimezoneOffset();
		d = new Date(dayTrans[0], dayTrans[1] - 1, (dayTrans[2] + 1)).getTimezoneOffset();
		if (!pos) {
			s = s * -1;
			d = d * -1;
		}
		if ((std == s) && (dst == d))
			equal = true;
	}
	return equal;
};

AjxTimezone._generateDefaultRule = function(pos) {
	// create temp dates
	var d = new Date();
	d.setMonth(0, 1);
	d.setHours(0, 0, 0, 0);

	var d2 = new Date();
	d2.setHours(0, 0, 0, 0);

	// data
	var stdOff = d.getTimezoneOffset();
	var lastOff = stdOff;
	var trans = [];

	// find transition points
	for (var m = 0; m < 12; m++) {
		// get last day of this month
		d2.setMonth(m + 1, 0);
		var ld = d2.getDate();

		// check each day for a transition
		for (var md = 1; md <= ld; md++) {
			d.setMonth(m, md);
			var curOff = d.getTimezoneOffset();
			if (curOff != lastOff) {
				var td = new Date(d.getTime());
				td.setDate(md - 1);

				// now find exact hour where transition occurs
				for (var h = 0; h < 24; h++) {
					td.setHours(h, 0, 0, 0);
					var transOff = td.getTimezoneOffset();
					if (transOff == curOff) {
						break;
					}
				}
				trans.push(td);
			}
			lastOff = curOff;
		}
	}

    var offset = stdOff * (pos ? 1 : -1);
    var rule = {
        clientId: AjxTimezone.AUTO_DETECTED, 
        serverId: ["(GMT",AjxTimezone._generateShortName(offset, true),") ",AjxTimezone.AUTO_DETECTED].join(""),
		autoDetected: true
	};

	// generate non-DST rule
	if (trans.length == 0) {
        rule.standard = { offset: offset };
        AjxTimezone.STANDARD_RULES.unshift(rule);
	}

	// generate DST rule
	else {
		// is this the southern hemisphere?
		var tzo0 = trans[0].getTimezoneOffset();
		var tzo1 = trans[1].getTimezoneOffset();
		var flip = tzo0 > tzo1;

		var s2d = trans[flip ? 1 : 0];
		var d2s = trans[flip ? 0 : 1];

        // standard
        rule.standard = AjxTimezone.createWkDayTransition(d2s);
        rule.standard.hour += 1;
        rule.standard.offset = offset;
        rule.standard.trans = [ d2s.getFullYear(), d2s.getMonth() + 1, d2s.getDate() ];

        // daylight
        rule.daylight = AjxTimezone.createWkDayTransition(s2d);
        rule.daylight.hour -= 1;
        rule.daylight.offset = s2d.getTimezoneOffset() * (pos ? 1 : -1);
        rule.daylight.trans = [ s2d.getFullYear(), s2d.getMonth() + 1, s2d.getDate() ]

        AjxTimezone.DAYLIGHT_RULES.unshift(rule);
	}

	return rule;
};

AjxTimezone._generateShortName = function(offset, period) {
	if (offset == 0) return "";
	var sign = offset < 0 ? "-" : "+";
	var stdOffset = Math.abs(offset);
	var hours = Math.floor(stdOffset / 60);
	var minutes = stdOffset % 60;
	hours = hours < 10 ? '0' + hours : hours;
	minutes = minutes < 10 ? '0' + minutes : minutes;
	return [sign,hours,period?".":"",minutes].join("");
};

// Static initialization

AjxTimezone.DEFAULT_RULE = AjxTimezone._guessMachineTimezone();

(function() {
    for (var j = 0; j < AjxTimezoneData.TIMEZONE_RULES.length; j++) {
        var rule = AjxTimezoneData.TIMEZONE_RULES[j];
        var serverId = rule.serverId;
        var clientId = rule.clientId;

        AjxTimezone._CLIENT2SERVER[clientId] = serverId;
        AjxTimezone._SERVER2CLIENT[serverId] = clientId;
        AjxTimezone._SHORT_NAMES[clientId] = AjxTimezone._generateShortName(rule.standard.offset);
        AjxTimezone._CLIENT2RULE[clientId] = rule;
    }
})();

/** DEBUG ***
// This forces the client to create an auto-detected timezone rule,
// regardless of whether the actual timezone was detected correctly
// from the known list.
AjxTimezone.DEFAULT_RULE = AjxTimezone._generateDefaultRule();
AjxTimezone._CLIENT2SERVER[AjxTimezone.DEFAULT_RULE.clientId] = AjxTimezone.DEFAULT_RULE.serverId;
AjxTimezone._SERVER2CLIENT[AjxTimezone.DEFAULT_RULE.serverId] = AjxTimezone.DEFAULT_RULE.clientId;
AjxTimezone._SHORT_NAMES[AjxTimezone.DEFAULT_RULE.clientId] = AjxTimezone._generateShortName(AjxTimezone.DEFAULT_RULE.standard.offset);
AjxTimezone._CLIENT2RULE[AjxTimezone.DEFAULT_RULE.clientId] = AjxTimezone.DEFAULT_RULE;
/***/

AjxTimezone.DEFAULT = AjxTimezone.getClientId(AjxTimezone.DEFAULT_RULE.serverId);
