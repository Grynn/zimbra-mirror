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
 * This class stores mappings between client and server identifiers for
 * timezones as well as attempting to guess the default timezone. The 
 * application can override this value, through a user preference perhaps, 
 * by setting the <code>DEFAULT</code> property's value. The default 
 * timezone is specified using the client identifier (e.g. "US/Pacific").
 * <p>
 * <strong>Note:</strong>
 * The client timezone identifiers are the same identifiers used in the
 * Java TimeZone class. Only a subset of the timezones available in Java
 * are actually used in this class, though.
 */
function AjxTimezone () {}

// Static methods

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
		rule.mediumName = [AjxTimezone.getShortName(clientId),' ',clientId].join("");
	}
	return rule.mediumName;
};
AjxTimezone.getLongName = function(clientId) {
	var rule = AjxTimezone.getRule(clientId);
	if (!rule.longName) {
		rule.longName = [I18nMsg["timezoneName"+clientId+"Long"]," (",AjxTimezone.getShortName(clientId),")"].join("");
	}
	return rule.longName;
};

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
	var offset = rule ? rule.stdOffset : 0;
	if (rule && rule.dstOffset) {
		var month = date.getMonth();
		var day = date.getDate();
		if ((month == rule.changeD[1] && day >= rule.changeD[2]) ||
			(month == rule.changeStd[1] && day < rule.changeStd[2]) ||
			(month > rule.changeD[1] && month < rule.changeStd[1])) {
			offset = rule.dstOffset;
		}
	}
	return offset;
};

AjxTimezone.guessMachineTimezone = function() {
	return AjxTimezone._guessMachineTimezone().name;
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
	var delta = arule.stdOffset - brule.stdOffset;
	if (delta == 0) {
		var aname = arule.name;
		var bname = brule.name;
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
AjxTimezone._ruleLists = {
	noDSTList: [
		{ name:"(GMT-12.00) International Date Line West",				stdOffset: -720,hasDOffset: false },
		{ name:"(GMT-11.00) Midway Island / Samoa", 					stdOffset: -660,hasDOffset: false },
		{ name:"(GMT-10.00) Hawaii", 									stdOffset: -600,hasDOffset: false },
		{ name:"(GMT-07.00) Arizona",									stdOffset: -420,hasDOffset: false },
		{ name:"(GMT-06.00) Central America",							stdOffset: -360,hasDOffset: false },
		{ name:"(GMT-06.00) Saskatchewan",								stdOffset: -360,hasDOffset: false },
		{ name:"(GMT-05.00) Indiana (East)", 							stdOffset: -300,hasDOffset: false },
		{ name:"(GMT-05.00) Bogota / Lima / Quito", 					stdOffset: -300,hasDOffset: false },
		{ name:"(GMT-04.00) Caracas / La Paz", 							stdOffset: -240,hasDOffset: false },
		{ name:"(GMT-03.00) Buenos Aires / Georgetown", 				stdOffset: -180,hasDOffset: false },
		{ name:"(GMT-01.00) Cape Verde Is.", 							stdOffset: -60, hasDOffset: false },
		{ name:"(GMT) Casablanca / Monrovia",							stdOffset: 0, 	hasDOffset: false },
		{ name:"(GMT+01.00) West Central Africa",						stdOffset: 60, 	hasDOffset: false },
		{ name:"(GMT+02.00) Harare / Pretoria", 						stdOffset: 120, hasDOffset: false },
		{ name:"(GMT+02.00) Jerusalem", 								stdOffset: 120, hasDOffset: false },
		{ name:"(GMT+03.00) Kuwait / Riyadh", 							stdOffset: 180, hasDOffset: false },
		{ name:"(GMT+03.00) Nairobi", 									stdOffset: 180, hasDOffset: false },
		{ name:"(GMT+04.00) Abu Dhabi / Muscat", 						stdOffset: 240, hasDOffset: false },
		{ name:"(GMT+04.30) Kabul", 									stdOffset: 270, hasDOffset: false },
		{ name:"(GMT+05.00) Islamabad / Karachi / Tashkent",			stdOffset: 300, hasDOffset: false },
		{ name:"(GMT+05.30) Chennai / Kolkata / Mumbai / New Delhi", 	stdOffset: 330, hasDOffset: false },
		{ name:"(GMT+05.45) Kathmandu", 								stdOffset: 345, hasDOffset: false },
		{ name:"(GMT+06.00) Astana / Dhaka", 							stdOffset: 360, hasDOffset: false },
		{ name:"(GMT+06.00) Sri Jayawardenepura", 						stdOffset: 360, hasDOffset: false },
		{ name:"(GMT+06.30) Rangoon", 									stdOffset: 390, hasDOffset: false },
		{ name:"(GMT+07.00) Bangkok / Hanoi / Jakarta", 				stdOffset: 420, hasDOffset: false },
		{ name:"(GMT+08.00) Kuala Lumpur / Singapore", 					stdOffset: 480, hasDOffset: false },
		{ name:"(GMT+08.00) Perth", 									stdOffset: 480, hasDOffset: false },
		{ name:"(GMT+08.00) Taipei", 									stdOffset: 480, hasDOffset: false },
		{ name:"(GMT+08.00) Beijing / Chongqing / Hong Kong / Urumqi",	stdOffset: 480, hasDOffset: false },
		{ name:"(GMT+09.00) Osaka / Sapporo / Tokyo", 					stdOffset: +540,hasDOffset: false },
		{ name:"(GMT+09.00) Seoul", 									stdOffset: 540, hasDOffset: false },
		{ name:"(GMT+09.30) Darwin", 									stdOffset: 570, hasDOffset: false },
		{ name:"(GMT+10.00) Brisbane", 									stdOffset: 600, hasDOffset: false },
		{ name:"(GMT+10.00) Guam / Port Moresby", 						stdOffset: 600, hasDOffset: false },
		{ name:"(GMT+11.00) Magadan / Solomon Is. / New Caledonia", 	stdOffset: 660, hasDOffset: false },
		{ name:"(GMT+12.00) Fiji / Kamchatka / Marshall Is.", 			stdOffset: 720, hasDOffset: false },
		{ name:"(GMT+13.00) Nuku'alofa", 								stdOffset: 780, hasDOffset: false }
	],

	DSTList: [
		{ name:"(GMT-09.00) Alaska", 
			stdOffset: -540, changeStd:[2005, 9, 30], 
			dstOffset: -480, changeD:[2005, 3, 3] },
		{ name:"(GMT-08.00) Pacific Time (US & Canada) / Tijuana", 
			stdOffset: -480, changeStd:[2005, 9, 30],
			dstOffset: -420, changeD: [2005, 3, 3]},
		{ name:"(GMT-07.00) Mountain Time (US & Canada)", 
			stdOffset: -420, changeStd:[2005, 9, 30], 
			dstOffset: -360, changeD: [2005, 3, 3]},
		{ name:"(GMT-06.00) Central Time (US & Canada)", 
			stdOffset: -360, changeStd: [2005, 9, 30], 
			dstOffset: -300, changeD: [2005, 3, 3]},
		{ name:"(GMT-05.00) Eastern Time (US & Canada)", 
			stdOffset: -300, changeStd: [2005, 9, 30],
			dstOffset: -240, changeD: [2005, 3, 3] },
		{ name:"(GMT-04.00) Atlantic Time (Canada)", 
			stdOffset: -240, changeStd: [2005, 9, 30],
			dstOffset: -180, changeD: [2005, 3, 3] },
		{ name:"(GMT-04.00) Santiago", 
			stdOffset: -240, changeStd: [2005, 2, 13],
			dstOffset: -180, changeD: [2005, 9, 9] },
		{ name:"(GMT-03.30) Newfoundland", 
			stdOffset: -210, changeStd: [2005, 9, 30],
			dstOffset: -150, changeD: [2005, 3, 3] },
		{ name:"(GMT-03.00) Brasilia", 
			stdOffset: -180, changeStd: [2005, 1, 20],
			dstOffset: -120, changeD: [2005, 9, 16] },
		{ name:"(GMT-03.00) Greenland", 
			stdOffset: -180, changeStd: [2005, 9, 30],
			dstOffset: -120, changeD: [2005, 3, 3] },
		{ name:"(GMT-02.00) Mid-Atlantic", 
			stdOffset: -120, changeStd: [2005, 8, 25],
			dstOffset: -60, changeD: [2005, 2, 27] },
		{ name:"(GMT-01.00) Azores", 
			stdOffset: -60, changeStd: [2005, 9, 30], 
			dstOffset: 0, changeD: [2005, 2, 27] },
		{ name:"(GMT) Greenwich Mean Time - Dublin / Edinburgh / Lisbon / London", 
			stdOffset: 0, changeStd: [2005, 9, 30],
			dstOffset: 60, changeD: [2005, 2, 27] },
		{ name:"(GMT+01.00) Amsterdam / Berlin / Bern / Rome / Stockholm / Vienna", 
			stdOffset: 60, changeStd: [2005, 9, 30],
			dstOffset: 120, changeD: [2005, 2, 27] },
		{ name:"(GMT+02.00) Athens / Beirut / Istanbul / Minsk", 
			stdOffset: 120, changeStd: [2005, 9, 30],
			dstOffset: 180, changeD: [2005, 2, 27] },
		{ name:"(GMT+02.00) Cairo", 
			stdOffset: 120, changeStd:  [2005, 8, 28],
			dstOffset: 180, changeD:  [2005, 4, 6] },
		{ name:"(GMT+03.00) Baghdad", 
			stdOffset: 180, changeStd: [2005, 9, 2],
			dstOffset: 240, changeD: [2005, 3, 3]},
		{ name:"(GMT+03.00) Moscow / St. Petersburg / Volgograd", 
			stdOffset: 180, changeStd: [2005, 9, 30],
			dstOffset: 240, changeD: [2005, 2, 27] },
		{ name:"(GMT+03.30) Tehran",
			stdOffset: 210, changeStd:  [2005, 8, 28], 
			dstOffset: 270, changeD:  [2005, 2, 6] },
		{ name:"(GMT+04.00) Baku / Tbilisi / Yerevan", 
			stdOffset: 240, changeStd: [2005, 9, 30],
			dstOffset: 300, changeD: [2005, 2, 27] },
		{ name:"(GMT+05.00) Ekaterinburg", 
			stdOffset: 300, changeStd:  [2005, 9, 30],
			dstOffset: 360, changeD:  [2005, 2, 27]},
		{ name:"(GMT+06.00) Almaty / Novosibirsk", 
			stdOffset: 360, changeStd:  [2005, 9, 30],
			dstOffset: 420, changeD:  [2005, 2, 27]},
		{ name:"(GMT+07.00) Krasnoyarsk", 
			stdOffset: 420, changeStd:  [2005, 9, 30],
			dstOffset: 480, changeD:  [2005, 2, 27] },
		{ name:"(GMT+08.00) Irkutsk / Ulaan Bataar", 
			stdOffset: 480, changeStd:  [2005, 9, 30],
			dstOffset: 540, changeD:  [2005, 2, 27] },
		{ name:"(GMT+09.00) Yakutsk", 
			stdOffset: 540, changeStd:  [2005, 9, 30],
			dstOffset: 600, changeD:  [2005, 2, 27] },
		{ name:"(GMT+09.30) Adelaide", 
			stdOffset: 570, changeStd:  [2005, 2, 27], 
			dstOffset: 630, changeD:  [2005, 9, 30] },
		{ name:"(GMT+10.00) Canberra / Melbourne / Sydney", 
			stdOffset: 600, changeStd: [2005, 2, 27],
			dstOffset: 660, changeD: [2005, 9, 30] },
		{ name:"(GMT+10.00) Hobart", 
			stdOffset: 600, changeStd: [2005, 2, 27],
			dstOffset: 660, changeD: [2005, 9, 2] },
		{ name:"(GMT+10.00) Vladivostok", 
			stdOffset: 600, changeStd: [2005, 9, 30], 
			dstOffset: 660, changeD: [2005, 2, 27] },
		{ name:"(GMT+12.00) Auckland / Wellington", 
			stdOffset: 720, changeStd: [2005, 2, 20],
			dstOffset: 780, changeD: [2005, 9, 2] }
	]
};

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
	var dec1 = new Date(2005, 12, 1, 0, 0, 0);
	var jun1 = new Date(2005, 6, 1, 0, 0, 0);
	var dec1offset = dec1.getTimezoneOffset();
	var jun1offset = jun1.getTimezoneOffset();
	var pos = ((dec1.getHours() - dec1.getUTCHours()) > 0);
	if (!pos) {
		dec1offset = dec1offset * -1;
		jun1offset = jun1offset * -1;
	}
	var tz = null;
	// if the offset for jun is the same as the offset in december,
	// then we have a timezone that doesn't deal with daylight savings.
	if (jun1offset == dec1offset) {
		var list = AjxTimezone._ruleLists.noDSTList;
 		for (var i = 0; i < list.length ; ++i ) {
			if (list[i].stdOffset == jun1offset) {
				tz = list[i];
				break;
			}
		}
	} else {
		// we need to find a rule that matches both offsets
		var list = AjxTimezone._ruleLists.DSTList;
		var dst = Math.max(dec1offset, jun1offset);
		var std = Math.min(dec1offset, jun1offset);
		var rule;
 		for (var i = 0; i < list.length ; ++i ) {
			rule = list[i];
			if (rule.stdOffset == std && rule.dstOffset == dst) {
				if (AjxTimezone._compareRules(rule, std, dst, pos)) {
					tz = rule;
					break;
				}
			}
		}
	}
	return tz || AjxTimezone._generateDefaultRule(pos);
};

AjxTimezone._compareRules = 
function(rule, std, dst, pos) {
	var equal = false;
	var d = new Date(rule.changeStd[0], rule.changeStd[1], (rule.changeStd[2] - 1)).getTimezoneOffset();
	var s = new Date(rule.changeStd[0], rule.changeStd[1], (rule.changeStd[2] + 1)).getTimezoneOffset();
	if (!pos) {
		s = s * -1;
		d = d * -1;
	}
	//alert("name = " + rule.name + ' s = ' + s + " d = " + d + " std = " + std + " dst = " + dst);
	if ( (std == s) && (dst == d) ) {
		s = new Date(rule.changeD[0], rule.changeD[1], (rule.changeD[2] - 1)).getTimezoneOffset();
		d = new Date(rule.changeD[0], rule.changeD[1], (rule.changeD[2] + 1)).getTimezoneOffset();
		if (!pos) {
			s = s * -1;
			d = d * -1;
		}
		//alert("name = " + rule.name + ' s = ' + s + " d = " + d + " std = " + std + " dst = " + dst);
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

	var rule = {
		stdOffset: stdOff * (pos ? 1 : -1),
		autoDetected: true
	};
	rule.name = ["(GMT",AjxTimezone._generateShortName(rule.stdOffset, true),") ",AjxTimezone.AUTO_DETECTED].join("");

	// generate non-DST rule
	if (trans.length == 0) {
		rule.hasDOffset = false;

		AjxTimezone._ruleLists.noDSTList.unshift(rule);
	}

	// generate DST rule
	else {
		// is this the southern hemisphere?
		var tzo0 = trans[0].getTimezoneOffset();
		var tzo1 = trans[1].getTimezoneOffset();
		var flip = tzo0 > tzo1;

		var s2d = trans[flip ? 1 : 0];
		var d2s = trans[flip ? 0 : 1];

		rule.changeStd = [d2s.getFullYear(), d2s.getMonth(), d2s.getDate(), d2s.getHours() + 1, d2s.getMinutes(), d2s.getSeconds()];
		rule.dstOffset = s2d.getTimezoneOffset() * (pos ? 1 : -1);
		rule.changeD = [s2d.getFullYear(), s2d.getMonth(), s2d.getDate(), s2d.getHours() - 1, s2d.getMinutes(), s2d.getSeconds()];

		AjxTimezone._ruleLists.DSTList.unshift(rule);
	}

	/*** DEBUG ***
	var a = [];
	a.push(new Date().toString(),"\n\n");
	for (var p in rule) {
		var v = rule[p];
		a.push(p," = ",(v instanceof Array?v.join():v),"\n");
	}
	alert(a.join(""));
	/***/

	// add message entries for generated rule
	I18nMsg["timezoneMap"+AjxTimezone.AUTO_DETECTED] = rule.name;
	I18nMsg["timezoneName"+AjxTimezone.AUTO_DETECTED+"Long"] = AjxMsg.timezoneNameAutoDetectedLong;
	I18nMsg["timezoneName"+AjxTimezone.AUTO_DETECTED+"LongDST"] = AjxMsg.timezoneNameAutoDetectedLong;
	I18nMsg["timezoneName"+AjxTimezone.AUTO_DETECTED+"Short"] = AjxMsg.timezoneNameAutoDetectedShort;
	I18nMsg["timezoneName"+AjxTimezone.AUTO_DETECTED+"ShortDST"] = AjxMsg.timezoneNameAutoDetectedShort;

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

var length = "timezoneMap".length;
for (var prop in I18nMsg) {
	if (prop.match(/^timezoneMap/)) {
		var clientId = prop.substring(length);
		var serverId = I18nMsg[prop];
		AjxTimezone._CLIENT2SERVER[clientId] = serverId;
		AjxTimezone._SERVER2CLIENT[serverId] = clientId;
	}
}

var lists = [ AjxTimezone._ruleLists.noDSTList, AjxTimezone._ruleLists.DSTList ];
for (var i = 0; i < lists.length; i++) {
	var list = lists[i];
	for (var j = 0; j < list.length; j++) {
		var rule = list[j];
		var serverId = rule.name;
		var clientId = AjxTimezone.getClientId(serverId);
		AjxTimezone._SHORT_NAMES[clientId] = AjxTimezone._generateShortName(rule.stdOffset);
		AjxTimezone._CLIENT2RULE[clientId] = rule;
	}
}

AjxTimezone.DEFAULT = AjxTimezone.getClientId(AjxTimezone.DEFAULT_RULE.name);
