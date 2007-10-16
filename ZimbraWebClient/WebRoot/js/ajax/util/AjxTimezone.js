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

/**
 * This class holds all of the known timezone rules. Each timezone
 * is represented by an object that has a unique identifier and the
 * offset to UTC in standard time. If the timezone defines daylight
 * savings time, then additional information is provided (e.g. when
 * the DST takes effect and it's offset to UTC).
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
 *   var timezone = { clientId: "My Timezone", standard: { offset: -480 } };
 *   </pre>
 * <dt> America/Los_Angeles, 2007
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
 *   var timezone = { clientId: "My Timezone",
 *                    standard: standard, daylight: daylight };
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
 *   var timezone = { clientId: "My Timezone",
 *                    standard: standard, daylight: daylight };
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
 * timezone is specified using the client identifier.
 */
AjxTimezone = function() {}

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
AjxTimezone.createTransitionDate = function(onset) {
    var date = new Date(AjxTimezoneData.TRANSITION_YEAR, onset.mon - 1, 1, 12, 0, 0);
    if (onset.mday) {
        date.setDate(onset.mday);
    }
    else if (onset.week == -1) {
        date.setMonth(date.getMonth() + 1, 0);
        for (var i = 0; i < 7; i++) {
            if (date.getDay() + 1 == onset.wkday) {
                break;
            }
            date.setDate(date.getDate() - 1);
        }
    }
    else {
        for (var i = 0; i < 7; i++) {
            if (date.getDay() + 1 == onset.wkday) {
                break;
            }
            date.setDate(date.getDate() + 1);
        }
        date.setDate(date.getDate() + 7 * (onset.week - 1));
    }
    var trans = [ date.getFullYear(), date.getMonth() + 1, date.getDate() ];
    return trans;
};

AjxTimezone.getZonePreferences =
function() {
	if (AjxTimezone._PREF_ZONE_DISPLAY) {
		var count = AjxTimezone._PREF_ZONE_DISPLAY.length;
		var total = AjxTimezone.STANDARD_RULES.length + AjxTimezone.DAYLIGHT_RULES.length;
		if (count != total) {
			AjxTimezone._PREF_ZONE_DISPLAY = null;
		}
	}

	if (!AjxTimezone._PREF_ZONE_DISPLAY) {
		AjxTimezone._PREF_ZONE_DISPLAY = [];
		AjxTimezone.getAbbreviatedZoneChoices();
		for (var i = 0; i < AjxTimezone._ABBR_ZONE_OPTIONS.length; i++) {
			AjxTimezone._PREF_ZONE_DISPLAY.push(AjxTimezone._ABBR_ZONE_OPTIONS[i].displayValue);
		}
	}
	return AjxTimezone._PREF_ZONE_DISPLAY;
}

AjxTimezone.getZonePreferencesOptions =
function() {
	if (AjxTimezone._PREF_ZONE_OPTIONS) {
		var count = AjxTimezone._PREF_ZONE_OPTIONS.length;
		var total = AjxTimezone.STANDARD_RULES.length + AjxTimezone.DAYLIGHT_RULES.length;
		if (count != total) {
			AjxTimezone._PREF_ZONE_OPTIONS = null;
		}
	}

	if (!AjxTimezone._PREF_ZONE_OPTIONS) {
		AjxTimezone._PREF_ZONE_OPTIONS = [];
		AjxTimezone.getAbbreviatedZoneChoices();
		for (var i = 0; i < AjxTimezone._ABBR_ZONE_OPTIONS.length; i++) {
			AjxTimezone._PREF_ZONE_OPTIONS.push(AjxTimezone._ABBR_ZONE_OPTIONS[i].serverid);
		}
	}
	return AjxTimezone._PREF_ZONE_OPTIONS;
}

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

AjxTimezone.addRule = function(rule) {
    var serverId = rule.serverId;
    var clientId = rule.clientId;

    AjxTimezone._CLIENT2SERVER[clientId] = serverId;
    AjxTimezone._SERVER2CLIENT[serverId] = clientId;
    AjxTimezone._SHORT_NAMES[clientId] = AjxTimezone._generateShortName(rule.standard.offset);
    AjxTimezone._CLIENT2RULE[clientId] = rule;

    var array = rule.daylight ? AjxTimezone.DAYLIGHT_RULES : AjxTimezone.STANDARD_RULES;
    array.push(rule);
};

AjxTimezone.getRule = function(clientId, tz) {
	var rule = AjxTimezone._CLIENT2RULE[clientId];
    if (!rule) {
        // try to find the rule treating the clientId as the serverId
        clientId = AjxTimezone._SERVER2CLIENT[clientId];
        rule = AjxTimezone._CLIENT2RULE[clientId];
    }
    if (!rule && tz) {
        var names = [ "standard", "daylight" ];
        var rules = tz.daylight ? AjxTimezone.DAYLIGHT_RULES : AjxTimezone.STANDARD_RULES;
        for (var i = 0; i < rules.length; i++) {
            rule = rules[i];

            var found = true;
            for (var j = 0; j < names.length; j++) {
                var name = names[j];
                var onset = rule[name];
                if (!onset) continue;
			
				var breakOuter = false;

                for (var p in tz[name]) {
                    if (tz[name][p] != onset[p]) {
                        found = false;
                        breakOuter = true;
                        break;
                    }
                }
                
                if(breakOuter){
                	break;
                }
            }
            if (found) {
                return rule;
            }
        }
        return null;
    }

    return rule;
};

AjxTimezone.getOffset = function(clientId, date) {
	var rule = AjxTimezone.getRule(clientId);
	var offset = rule ? rule.standard.offset : 0;
	if (rule && rule.daylight) {
		var month = date.getMonth() + 1;
		var day = date.getDate();
        var stdTrans = rule.standard.trans;
        var dstTrans = rule.daylight.trans;
        if ((month == dstTrans[1] && day >= dstTrans[2]) ||
			(month == stdTrans[1] && day < stdTrans[2]) ||
			(month > dstTrans[1] && month < stdTrans[1])) {
			offset = rule.daylight.offset;
		}
	}
	return offset;
};

AjxTimezone.guessMachineTimezone = function() {
	return AjxTimezone._guessMachineTimezone().clientId;
};

AjxTimezone.getAbbreviatedZoneChoices = function() {
	if (AjxTimezone._ABBR_ZONE_OPTIONS) {
		var count = AjxTimezone._ABBR_ZONE_OPTIONS.length;
		var total = AjxTimezone.STANDARD_RULES.length + AjxTimezone.DAYLIGHT_RULES.length;
		if (count != total) {
			AjxTimezone._ABBR_ZONE_OPTIONS = null;
		}
	}
	if (!AjxTimezone._ABBR_ZONE_OPTIONS) {
		AjxTimezone._ABBR_ZONE_OPTIONS = [];
		for (var clientId in AjxTimezone._CLIENT2SERVER) {
			var rule = AjxTimezone._CLIENT2RULE[clientId];
			var serverId = rule.serverId;
			var option = {
				displayValue: AjxTimezone.getMediumName(clientId),
				selectedValue: clientId,
				value: serverId,
				// these props used by sort comparator
				standard: rule.standard,
				serverid: serverId
			};
			AjxTimezone._ABBR_ZONE_OPTIONS.push(option);
		}
		AjxTimezone._ABBR_ZONE_OPTIONS.sort(AjxTimezone._BY_OFFSET);
	}
	return AjxTimezone._ABBR_ZONE_OPTIONS;
};

AjxTimezone._BY_OFFSET = function(arule, brule) {
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
	var dec1 = new Date(AjxTimezoneData.TRANSITION_YEAR, 11, 1, 0, 0, 0);
	var jun1 = new Date(AjxTimezoneData.TRANSITION_YEAR, 5, 1, 0, 0, 0);
	var dec1offset = -dec1.getTimezoneOffset();
	var jun1offset = -jun1.getTimezoneOffset();
    var southernHemisphere = dec1offset > jun1offset;

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
                var strans = rule.standard.trans;
                var dtrans = rule.daylight.trans;

                var s0 = new Date(strans[0], strans[1]-1, strans[2]-1);
                var s1 = new Date(strans[0], strans[1]-1, strans[2]+2);
                var d0 = new Date(dtrans[0], dtrans[1]-1, dtrans[2]-1);
                var d1 = new Date(dtrans[0], dtrans[1]-1, dtrans[2]+2);
                if (-s1.getTimezoneOffset() == std && -d1.getTimezoneOffset() == dst &&
                    -s0.getTimezoneOffset() == dst && -d0.getTimezoneOffset() == std) {
                    return rule;
                }
            }
		}
	}

    // generate default rule
    return AjxTimezone._generateDefaultRule(southernHemisphere);
};

AjxTimezone._generateDefaultRule = function(southernHemisphere) {
    if (southernHemisphere == null) {
        var dec1 = new Date(AjxTimezoneData.TRANSITION_YEAR, 11, 1, 0, 0, 0);
        var jun1 = new Date(AjxTimezoneData.TRANSITION_YEAR, 5, 1, 0, 0, 0);
        var dec1offset = -dec1.getTimezoneOffset();
        var jun1offset = -jun1.getTimezoneOffset();
        southernHemisphere = dec1offset > jun1offset;
    }

    // create temp dates
	var d = new Date();
	d.setMonth(0, 1);
	d.setHours(0, 0, 0, 0);

	var d2 = new Date();
	d2.setHours(0, 0, 0, 0);

	// data
	var stdOff = -d.getTimezoneOffset();
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
			var curOff = -d.getTimezoneOffset();
			if (curOff != lastOff) {
                var td = new Date(d.getTime());
				td.setDate(md - 1);

				// now find exact hour where transition occurs
				for (var h = 0; h < 24; h++) {
					td.setHours(h, 0, 0, 0);
                    // REVISIT: Need to figure out transition TIME as well! because
                    //          it may not be on the hour (e.g. 23:59:59)
					var transOff = -td.getTimezoneOffset();
					if (transOff == curOff) {
                        break;
                    }
				}

                // save this transition
                trans.push({ date: td, hour: h });
                lastOff = curOff;
                break;
            }
		}
	}

    var offset = stdOff;
    var rule = {
        clientId: AjxTimezone.AUTO_DETECTED, 
        serverId: ["(GMT",AjxTimezone._generateShortName(offset, true),") ",AjxTimezone.AUTO_DETECTED].join(""),
		autoDetected: true
	};
    AjxTimezoneData.TIMEZONE_RULES.unshift(rule);

	// generate non-DST rule
	if (trans.length == 0) {
        rule.standard = { offset: offset };
        AjxTimezone.STANDARD_RULES.unshift(rule);
	}

	// generate DST rule
	else {
        // flip if southern hemisphere
        var s2d = trans[southernHemisphere ? 1 : 0].date;
        var d2s = trans[southernHemisphere ? 0 : 1].date;

        // standard
        rule.standard = AjxTimezone.createWkDayTransition(d2s);
        rule.standard.hour = trans[southernHemisphere ? 0 : 1].hour + 1;
        // HACK: Don't know how to handle certain timezone transitions
        if (rule.standard.hour > 23) {
            rule.standard.hour = 23;
        }
        rule.standard.trans = [ d2s.getFullYear(), d2s.getMonth() + 1, d2s.getDate() ];
        d2s.setDate(d2s.getDate() + 1);
        rule.standard.offset = -d2s.getTimezoneOffset();

        // daylight
        rule.daylight = AjxTimezone.createWkDayTransition(s2d);
        rule.daylight.hour = trans[southernHemisphere ? 1 : 0].hour - 1;
        // HACK: Don't know how to handle certain timezone transitions
        if (rule.daylight.hour > 23) {
            rule.daylight.hour = 23;
        }
        rule.daylight.trans = [ s2d.getFullYear(), s2d.getMonth() + 1, s2d.getDate() ]
        s2d.setDate(s2d.getDate() + 1);
        rule.daylight.offset = -s2d.getTimezoneOffset();

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

/*** DEBUG ***
// This forces the client to create an auto-detected timezone rule,
// regardless of whether the actual timezone was detected correctly
// from the known list.
AjxTimezone.DEFAULT_RULE = AjxTimezone._generateDefaultRule();
/***/

(function() {
    AjxTimezoneData.TIMEZONE_RULES.sort(AjxTimezone._BY_OFFSET);
    for (var j = 0; j < AjxTimezoneData.TIMEZONE_RULES.length; j++) {
        var rule = AjxTimezoneData.TIMEZONE_RULES[j];
        AjxTimezone.addRule(rule);
    }
})();

AjxTimezone.DEFAULT = AjxTimezone.getClientId(AjxTimezone.DEFAULT_RULE.serverId);
