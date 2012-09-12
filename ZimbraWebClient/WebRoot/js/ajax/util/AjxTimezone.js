/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
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
 *
 * @class
 */
AjxTimezone = function() {};

//
// Static methods
//

AjxTimezone.convertTimezone = function(date, fromClientId, toClientId) {
	if (fromClientId == toClientId) {
		return date;
	}
	var offset1 = AjxTimezone.getOffset(toClientId, date);
	var offset2 = AjxTimezone.getOffset(fromClientId, date);
	//returning a new Date object since we might not always want to modify the parameter Date object
	return new Date(date.getTime() + (offset1 - offset2) * 60 * 1000);
};


AjxTimezone.getTransition = function(onset, year) {
	var trans = [ year || new Date().getFullYear(), onset.mon, 1 ];
	if (onset.mday) {
		trans[2] = onset.mday;
	}
	else if (onset.wkday) {
		var date = new Date(year, onset.mon - 1, 1, onset.hour, onset.min, onset.sec);

		// last wkday of month
		if (onset.week == -1) {
			// NOTE: This creates a date of the *last* day of specified month by
			//       setting the month to *next* month and setting day of month
			//       to zero (i.e. the day *before* the first day).
			var last = new Date(new Date(date.getTime()).setMonth(onset.mon, 0));
			var count = last.getDate();
			var wkday = last.getDay() + 1;
			var adjust = wkday >= onset.wkday ? wkday - onset.wkday : 7 - onset.wkday - wkday;
			trans[2] = count - adjust;
		}

		// Nth wkday of month
		else {
			var wkday = date.getDay() + 1;
			var adjust = onset.wkday == wkday ? 1 :0;
			trans[2] = onset.wkday + 7 * (onset.week - adjust) - wkday + 1;
		}
	}
	return trans;
};

AjxTimezone.createMDayTransition = function(date, offset) {
	if (date instanceof Date) {
		offset = offset != null ? offset : date.getTimezoneOffset();
		date = [
			date.getFullYear(), date.getMonth() + 1, date.getDate(),
			date.getHours(), date.getMinutes(), date.getSeconds()
		];
	}
	var onset = { offset: offset, trans: date };
	return AjxTimezone.addMDayTransition(onset);
};

AjxTimezone.addMDayTransition = function(onset) {
	var trans = onset.trans;
	onset.mon = trans[1];
	onset.mday = trans[2];
	onset.hour = trans[3];
	onset.min = trans[4];
	onset.sec = trans[5];
	return onset;
};

AjxTimezone.createWkDayTransition = function (date, offset) {
	if (date instanceof Date) {
		offset = offset != null ? offset : date.getTimezoneOffset();
		date = [
			date.getFullYear(), date.getMonth() + 1, date.getDate(),
			date.getHours(), date.getMinutes(), date.getSeconds()
		];
	}
	var onset = { offset: offset, trans: date };
	return AjxTimezone.addWkDayTransition(onset);
};

AjxTimezone.addWkDayTransition = function(onset) {
	var trans = onset.trans;
	var mon = trans[1];
	var monDay = trans[2];
	var week = Math.floor((monDay - 1) / 7);
	var date = new Date(trans[0], trans[1] - 1, trans[2], 12, 0, 0);

	// NOTE: This creates a date of the *last* day of specified month by
	//       setting the month to *next* month and setting day of month
	//       to zero (i.e. the day *before* the first day).
	var count = new Date(new Date(date.getTime()).setMonth(mon - 1, 0)).getDate();
	var last = count - monDay < 7;

	// set onset values
	onset.mon =  mon;
	onset.week = last ? -1 : week + 1;
	onset.wkday = date.getDay() + 1;
	onset.hour = trans[3];
	onset.min = trans[4];
	onset.sec = trans[5];
	return onset;
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
};

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
			AjxTimezone._PREF_ZONE_OPTIONS.push(AjxTimezone._ABBR_ZONE_OPTIONS[i].value); //use value is better, serverID is usd by compare operator.
		}
	}
	return AjxTimezone._PREF_ZONE_OPTIONS;
};

AjxTimezone.getServerId = function(clientId) {
	return AjxTimezone._CLIENT2SERVER[clientId] || clientId;
};
AjxTimezone.getClientId = function(serverId) {
	return AjxTimezone._SERVER2CLIENT[serverId] || serverId;
};

AjxTimezone.getShortName = function(clientId) {
	var rule = AjxTimezone.getRule(clientId);
    if (rule && rule.shortName) return rule.shortName;
    var generatedShortName = ["GMT",AjxTimezone._SHORT_NAMES[clientId]].join("");
    if(rule) rule.shortName = generatedShortName;
	return generatedShortName;
};

AjxTimezone.getMediumName = function(clientId) {
	var rule = AjxTimezone.getRule(clientId);
    if (rule && rule.mediumName) return rule.mediumName;
    var generatedMediumName = AjxMsg[clientId] || ['(',AjxTimezone.getShortName(clientId),') ',clientId].join("");
    if(rule) rule.mediumName = generatedMediumName;
	return generatedMediumName;
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
	var rule = AjxTimezone.getRule(clientId || AjxTimezone.DEFAULT);
	if (rule && rule.daylight) {
		var year = date.getFullYear();

		var standard = rule.standard, daylight  = rule.daylight;
		var stdTrans = AjxTimezone.getTransition(standard, year);
		var dstTrans = AjxTimezone.getTransition(daylight, year);

		var month    = date.getMonth()+1, day = date.getDate();
		var stdMonth = stdTrans[1], stdDay = stdTrans[2];
		var dstMonth = dstTrans[1], dstDay = dstTrans[2];

		// northern hemisphere
		var isDST = false;
		if (dstMonth < stdMonth) {
			isDST = month > dstMonth && month < stdMonth;
			isDST = isDST || (month == dstMonth && day >= dstDay);
			isDST = isDST || (month == stdMonth && day <  stdDay);
		}

		// sorthern hemisphere
		else {
			isDST = month < stdMonth || month > dstMonth;
			isDST = isDST || (month == dstMonth && day >=  dstDay);
			isDST = isDST || (month == stdMonth && day < stdDay);
		}

		return isDST ? daylight.offset : standard.offset;
	}
	return rule ? rule.standard.offset : -(new Date().getTimezoneOffset());
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
				value: serverId,
				// these props used by sort comparator
				standard: rule.standard,
				serverId: serverId, //In _BY_OFFSET, the attribute name is serverId.
                clientId: clientId
			};
			AjxTimezone._ABBR_ZONE_OPTIONS.push(option);
		}
		AjxTimezone._ABBR_ZONE_OPTIONS.sort(AjxTimezone._BY_OFFSET);
	}
	return AjxTimezone._ABBR_ZONE_OPTIONS;
};

AjxTimezone.getMatchingTimezoneChoices = function() {
	if (AjxTimezone._MATCHING_ZONE_OPTIONS) {
		var count = AjxTimezone._MATCHING_ZONE_OPTIONS.length;
		var total = AjxTimezone.STANDARD_RULES.length + AjxTimezone.DAYLIGHT_RULES.length;
		if (count != total) {
			AjxTimezone._MATCHING_ZONE_OPTIONS = null;
		}
	}
	if (!AjxTimezone._MATCHING_ZONE_OPTIONS) {
		AjxTimezone._MATCHING_ZONE_OPTIONS = [];
		for (var i in AjxTimezone.MATCHING_RULES) {
			var rule = AjxTimezone.MATCHING_RULES[i];
			var clientId = rule.clientId;
			var serverId = rule.serverId;
            if(clientId == AjxTimezone.AUTO_DETECTED) continue;
			var option = {
				displayValue: AjxTimezone.getMediumName(clientId),
				value: serverId,
				// these props used by sort comparator
				standard: rule.standard,
				serverId: serverId, //In _BY_OFFSET, the attribute name is serverId.
                clientId: clientId
			};
			AjxTimezone._MATCHING_ZONE_OPTIONS.push(option);
		}
		AjxTimezone._MATCHING_ZONE_OPTIONS.sort(AjxTimezone._BY_OFFSET);
	}
	return AjxTimezone._MATCHING_ZONE_OPTIONS;
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
 */
AjxTimezone.GMT_NO_DST = "UTC";

/**
 * <strong>Note:</strong>
 * Do NOT change this value because it is used to reference messages.
 */
AjxTimezone.AUTO_DETECTED = "Auto-Detected";

/**
 * The default timezone is set by guessing the machine timezone later
 * in this file. See the static initialization section below for details.
 */

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
 * timezonePreference - optional value used to decide timezone rule in case of conflict 
 */
AjxTimezone._guessMachineTimezone = 
function(timezonePreference) {
	var dec1 = new Date(AjxTimezoneData.TRANSITION_YEAR, 11, 1, 0, 0, 0);
	var jun1 = new Date(AjxTimezoneData.TRANSITION_YEAR, 5, 1, 0, 0, 0);
	var dec1offset = -dec1.getTimezoneOffset();
	var jun1offset = -jun1.getTimezoneOffset();

    AjxTimezone.MATCHING_RULES = [];
    AjxTimezone.TIMEZONE_CONFLICT = false;
    var matchingRules = [];
    var matchingRulesMap = {};
    var offsetMatchingRules = [];
    var daylightMatchingFound = false;

    // if the offset for jun is the same as the offset in december,
	// then we have a timezone that doesn't deal with daylight savings.
	if (jun1offset == dec1offset) {
		var rules = AjxTimezone.STANDARD_RULES;
 		for (var i = 0; i < rules.length ; ++i ) {
            var rule = rules[i];
            if (rule.standard.offset == jun1offset) {
				 if(!matchingRulesMap[rule.serverId]) {
                     matchingRules.push(rule);
                     matchingRulesMap[rule.serverId] = true;
                 }
                 AjxTimezone.MATCHING_RULES.push(rule);
			}
		}
	}

    // we need to find a rule that matches both offsets
    else {
		var rules = AjxTimezone.DAYLIGHT_RULES;
		var dst = Math.max(dec1offset, jun1offset);
		var std = Math.min(dec1offset, jun1offset);
        var now = new Date();
        var currentOffset = -now.getTimezoneOffset();
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
                    if(!matchingRulesMap[rule.serverId]) {
                        matchingRules.push(rule);
                        matchingRulesMap[rule.serverId] = true;
                    }                    
                    daylightMatchingFound = true;
                }
            }
            //used for conflict resolution when server rules are wrong 
            if (rule.standard.offset == currentOffset || rule.daylight.offset == currentOffset) {
                    AjxTimezone.MATCHING_RULES.push(rule);
            }
		}
	}

    //when there is a timezone conflict use the preference to find better match
    if((matchingRules.length > 0) && timezonePreference != null) {
        var rules = matchingRules; 
        for(var i in rules) {
            if(rules[i].serverId == timezonePreference) {
                return rules[i];
            }
        }
    }

    if(matchingRules.length > 0) {
        // resolve conflict, if possible
        if (matchingRules.length > 1) {
            matchingRules.sort(AjxTimezone.__BY_SCORE);
            if (matchingRules[0].score != matchingRules[1].score) {
                matchingRules.length = 1;
            }
        }
        // mark if conflict and return best guess
        AjxTimezone.TIMEZONE_CONFLICT = (matchingRules.length > 1);  
        return matchingRules[0];        
    }

    if((AjxTimezone.MATCHING_RULES.length > 0) && timezonePreference != null) {
        var rules = AjxTimezone.MATCHING_RULES; 
        for(var i in rules) {
            if(rules[i].serverId == timezonePreference) {
                return rules[i];
            }
        }
    }

    // generate default rule
    return AjxTimezone._generateDefaultRule();
};

AjxTimezone.__BY_SCORE = function(a, b) {
    return b.score - a.score;
};

// Thanks to Jiho for this new, improved logic for generating the timezone rule.
AjxTimezone._generateDefaultRule = function() {
	var byMonth = 0;
	var byDate = 1;
	var byHour = 2;
	var byMinute = 3;
	var bySecond = 4;

	// Sweep the range between d1 and d2 looking for DST transitions.
	// Iterate the range by "by" unit.  When a transition is detected,
	// sweep the range between before/after dates by increasingly
	// smaller unit, month then date then hour then minute then finally second.
	function sweepRange(d1, d2, by, rule) {
		var upperBound = d2.getTime();
		var d = new Date();
		d.setTime(d1.getTime());
		var prevD = new Date();
		prevD.setTime(d.getTime());
		var prevOffset = d1.getTimezoneOffset() * -1;

		// initialize rule
		if (!rule) {
			rule = {
				clientId: AjxTimezone.AUTO_DETECTED,
				autoDetected: true
			};
		}

		// perform sweep
		while (d.getTime() <= upperBound) {
			// Increment by the right unit.
			if (by == byMonth) {
				d.setUTCMonth(d.getUTCMonth() + 1);
			}
			else if (by == byDate) {
				d.setUTCDate(d.getUTCDate() + 1);
			}
			else if (by == byHour) {
				d.setUTCHours(d.getUTCHours() + 1);
			}
			else if (by == byMinute) {
				d.setUTCMinutes(d.getUTCMinutes() + 1);
			}
			else if (by == bySecond) {
				d.setUTCSeconds(d.getUTCSeconds() + 1);
			}
			else {
				return rule;
			}

			var offset = d.getTimezoneOffset() * -1;
			if (offset != prevOffset) {
				if (by < bySecond) {
					// Drill down.
					rule = sweepRange(prevD, d, by + 1, rule);
				}
				else {
					// Tricky:
					// Initialize a Date object whose UTC fields are set to prevD's local fields.
					// Then add 1 second to get UTC version of onset time.  We want to work in UTC
					// to prevent the date object from experiencing the DST jump when we add 1 second.
					var trans = new Date();
					trans.setUTCFullYear(prevD.getFullYear(), prevD.getMonth(), prevD.getDate());
					trans.setUTCHours(prevD.getHours(), prevD.getMinutes(),     prevD.getSeconds() + 1);

					var onset = rule[prevOffset < offset ? "daylight" : "standard"] = {
						offset: offset,
						trans: [
							trans.getUTCFullYear(), trans.getUTCMonth() + 1, trans.getUTCDate(),    // yyyy-MM-dd
							trans.getUTCHours(),    trans.getUTCMinutes(),   trans.getUTCSeconds()  //   HH:mm:ss
						]
					};
					AjxTimezone.addWkDayTransition(onset);
					return rule;
				}
			}

			prevD.setTime(d.getTime());
			prevOffset = offset;
		}

		return rule;
	}

	// Find DST transitions between yyyy/07/71 00:00:00 and yyyy+1/06/30 23:59:59.
	// We can detect transition on/around 12/31 and 01/01.  Assume no one will
	// transition on/around 6/30 and 07/01.
	var d1 = new Date();
	var d2 = new Date();

	// set sweep start to yesterday
	var year = d1.getFullYear();
	d1.setUTCFullYear(year, d1.getMonth(), d1.getDate() - 1);
	d1.setUTCHours(0, 0, 0, 0);

	// set sweep end to tomorrow + 1 year
	d2.setTime(d1.getTime());
	d2.setUTCFullYear(year + 1, d1.getMonth(), d1.getDate() + 1);

	// case 1: no onset returned -> TZ doesn't use DST
	// case 2: two onsets returned -> TZ uses DST
	// case 3: only one onset returned -> mid-year policy change -> simplify and assume it's non-DST
	// case 4: three or more onsets returned -> shouldn't happen
	var rule = sweepRange(d1, d2, byMonth);

	// handle case 1 and 3
	if (!rule.daylight || !rule.standard) {
		rule.standard = { offset: d1.getTimezoneOffset() * -1 };
		delete rule.daylight;
	}

	// now that standard offset is determined, set serverId
	rule.serverId = ["(GMT",AjxTimezone._generateShortName(rule.standard.offset, true),") ",AjxTimezone.AUTO_DETECTED].join("");

	// bug 33800: guard against inverted daylight/standard onsets
	if (rule.daylight && rule.daylight.offset < rule.standard.offset) {
		var onset = rule.daylight;
		rule.daylight = rule.standard;
		rule.standard = onset;
	}

	// add generated rule to proper list
	//AjxTimezoneData.TIMEZONE_RULES.unshift(rule);
	//var rules = rule.daylight ? AjxTimezone.DAYLIGHT_RULES : AjxTimezone.STANDARD_RULES;
	//rules.unshift(rule);

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
