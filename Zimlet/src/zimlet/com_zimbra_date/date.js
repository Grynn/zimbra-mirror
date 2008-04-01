/*
 * ***** BEGIN LICENSE BLOCK *****
 *
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2006, 2007 Zimbra, Inc.
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

function Com_Zimbra_Date() {
}
Com_Zimbra_Date.prototype = new ZmZimletBase();
Com_Zimbra_Date.prototype.constructor = Com_Zimbra_Date;

Com_Zimbra_Date.prototype.init =
function() {
	Com_Zimbra_Date.prototype._zimletContext = this._zimletContext;
	Com_Zimbra_Date.prototype._className = "Object";
	var pri = this._zimletContext.priority;
	if (appCtxt.get(ZmSetting.CALENDAR_ENABLED)) {
		ZmObjectManager.registerHandler("ZmDate1ObjectHandler", ZmObjectManager.DATE, pri);
		ZmObjectManager.registerHandler("ZmDate2ObjectHandler", ZmObjectManager.DATE, pri);
		ZmObjectManager.registerHandler("ZmDate3ObjectHandler", ZmObjectManager.DATE, pri);
		ZmObjectManager.registerHandler("ZmDate4ObjectHandler", ZmObjectManager.DATE, pri);
		ZmObjectManager.registerHandler("ZmDate5ObjectHandler", ZmObjectManager.DATE, pri);
		ZmObjectManager.registerHandler("ZmDate6ObjectHandler", ZmObjectManager.DATE, pri);
		ZmObjectManager.registerHandler("ZmDate7ObjectHandler", ZmObjectManager.DATE, pri);
		ZmObjectManager.registerHandler("ZmDate8ObjectHandler", ZmObjectManager.DATE, pri);
		ZmObjectManager.registerHandler("ZmDate9ObjectHandler", ZmObjectManager.DATE, pri);
// don't register this one by default, though it is used by the assistant.
//ZmObjectManager.registerHandler("ZmDate10ObjectHandler", ZmObjectManager.DATE, pri);
	}
};

Com_Zimbra_Date.prototype.TYPE = ZmObjectManager.DATE;
Com_Zimbra_Date.prototype.getActionMenu =
	function(obj, span, context) {
        /** not be needed, as it comes from propeties file now...
        *
        var actionMenu = ZmZimletBase.prototype.getActionMenu.call(this, obj, span, context);

        var op = actionMenu.getOp("DAYVIEW");
        if (op) {
            op.setText(ZmMsg.viewDay);
        }
        op = actionMenu.getOp("NEWAPPT");
        if (op) {
            op.setText(ZmMsg.appointmentNewTitle);
        }
        op = actionMenu.getOp("SEARCHMAIL");
        if (op) {
            op.setText(ZmMsg.searchForMessages);
        }*/

        if (this._zimletContext._contentActionMenu instanceof AjxCallback) {
			this._zimletContext._contentActionMenu = this._zimletContext._contentActionMenu.run();
		}
		// Set some global context since the parent Zimlet (Com_Zimbra_Date) will be called for
		// right click menu options, even though the getActionMenu will get called on the sub-classes.
		Com_Zimbra_Date._actionObject = obj;
		Com_Zimbra_Date._actionSpan = span;
		Com_Zimbra_Date._actionContext = context;
		return this._zimletContext._contentActionMenu;
	};


Com_Zimbra_Date.validate = function(day, month, year){

    //"Day must be between 1 and 31.";
    if (day < 1 || day > 31) {
        return false;
    }
    //"Month must be between 0 and 11."
    if (month < 0 || month > 11) {
        return false;
    }
    // Make sure month and day of month is valid
    if ((month == 3 || month == 5 || month == 8 || month == 10) && day == 31) {
        return  false;
    }
    // Check for February date validity (including leap years)
    if (year && month == 1) {
        // figure out if "year" is a leap year;
        var isleap=(year%4==0 && (year%100!=0 || year%400==0));
        if (day > 29 || (day == 29 && !isleap)) {
            return false;
        }
    }
    return true;
};

Com_Zimbra_Date.prototype.getCurrentDate =
function(date) {
	var d = this[ZmObjectManager.ATTR_CURRENT_DATE];
	return d ? d : new Date();
};

Com_Zimbra_Date.prototype.menuItemSelected = function(itemId) {
	switch (itemId) {
		case "DAYVIEW":
			this._dayViewListener();
			break;
		case "NEWAPPT":
			this._newApptListener();
			break;
		case "SEARCHMAIL":
			this._searchMailListener();
			break;
	}
};

Com_Zimbra_Date.prototype.toolTipPoppedUp =
function(spanElement, contentObjText, matchContext, canvas) {
	var cc = AjxDispatcher.run("GetCalController");
	canvas.innerHTML = cc.getDayToolTipText(matchContext ? matchContext.date : new Date());
};

Com_Zimbra_Date.prototype._getHtmlContent =
function(html, idx, obj, context) {
	html[idx++] = AjxStringUtil.htmlEncode(obj, true);
	return idx;
};

Com_Zimbra_Date.prototype._dayViewListener =
function() {
	var loadCallback = new AjxCallback(this, this._handleLoadDayView);
	AjxDispatcher.require(["CalendarCore", "Calendar", "CalendarAppt"], false, loadCallback, null, true);
};

Com_Zimbra_Date.prototype._handleLoadDayView =
function() {
	var calApp = appCtxt.getApp(ZmApp.CALENDAR);
	calApp.activate(true, ZmController.CAL_DAY_VIEW, Com_Zimbra_Date._actionContext.date);
};

Com_Zimbra_Date.prototype._newApptListener =
function() {
	var loadCallback = new AjxCallback(this, this._handleLoadNewAppt);
	AjxDispatcher.require(["CalendarCore", "Calendar", "CalendarAppt"], false, loadCallback, null, true);
};

Com_Zimbra_Date.prototype._handleLoadNewAppt =
function() {
	// TODO support ev.shiftKey
	appCtxt.getAppViewMgr().popView(true, ZmController.LOADING_VIEW);	// pop "Loading..." page
	AjxDispatcher.run("GetCalController").newAppointmentHelper(Com_Zimbra_Date._actionContext.date);
};

Com_Zimbra_Date.prototype._searchMailListener =
function() {
	appCtxt.getSearchController().dateSearch(Com_Zimbra_Date._actionContext.date);
};

Com_Zimbra_Date.prototype.clicked =
function(spanElement, contentObjText, matchContext, canvas) {
	var calController = AjxDispatcher.run("GetCalController");
	var miniCalendar = calController.getMiniCalendar();
	calController.setDate(matchContext.date, 0, miniCalendar.getForceRollOver());
	if (!calController._viewVisible) {
		calController.show(ZmController.CAL_DAY_VIEW);
	}
};

Com_Zimbra_Date.prototype.match = function(line, startIndex){
    var result = null;
    while(true){
       result = this.matchRegex(line,startIndex);
       if(result == null || (result && result.context.valid)) return result;
       startIndex = result.index + (result.matchLength || result[0].length);
    }
};

//Overwrite to implement the functionality
Com_Zimbra_Date.prototype.matchRegex = function(line,startIndex){
    return null;
};

Com_Zimbra_Date.MONTH = {
	january: 0, jan: 0, february: 1, feb: 1, march: 2, mar: 2, april: 3, apr: 3, may: 4, june: 5, jun: 5,
	july: 6, jul: 6, august: 7, aug: 7, september: 8, sept: 8, sep: 8, october: 9, oct: 9, november: 10, nov: 10,
	december: 11, dec: 11
};

Com_Zimbra_Date.DOW = {	su: 0, mo: 1, tu: 2, we: 3, th: 4, fr: 5, sa: 6};


//var $dateObject = new Com_Zimbra_Date();
//var $RE_DOW = "(Mon(?:d(?:ay?)?)?|Tue(?:s(?:d(?:ay?)?)?)?|Wed(?:n(?:e(?:s(?:d(?:ay?)?)?)?)?)?|Thu(?:r(?:s(?:d(?:ay?)?)?)?)?|Fri(?:d(?:ay?)?)?|Sat(?:u(?:r(?:d(?:ay?)?)?)?)?|Sun(?:d(?:ay?)?)?)";
//var $RE_DOW = "("+I18nMsg.weekdayMonLong+"|"+I18nMsg.weekdayMonMedium+"|"+I18nMsg.weekdayTueLong+"|"+I18nMsg.weekdayTueMedium+"|"+I18nMsg.weekdayWedLong+"|"+I18nMsg.weekdayWedMedium+"|"+I18nMsg.weekdayThuLong+"|"+I18nMsg.weekdayThuMedium+"|"+I18nMsg.weekdayFriLong+"|"+I18nMsg.weekdayFriMedium+"|"+I18nMsg.weekdaySatLong+"|"+I18nMsg.weekdaySatMedium+"|"+I18nMsg.weekdaySunLong+"|"+I18nMsg.weekdaySunMedium+")";
//var $RE_DOW_FULL = "(Mon|Tues|Wednes|Thurs|Fri|Satur|Sun)day";
//var $RE_DOW_FULL = "("+I18nMsg.weekdayMonLong+"|"+I18nMsg.weekdayTueLong+"|"+I18nMsg.weekdayWedLong+"|"+I18nMsg.weekdayThuLong+"|"+I18nMsg.weekdayFriLong+"|"+I18nMsg.weekdaySatLong+"|"+I18nMsg.weekdaySunLong+")";



//var $RE_DOM = "(\\d{1,2})(?:st|nd|rd|th)?";
//var $RE_DOM = this.getMessage("RE_DOM");//"(\\d{1,2})(?:"+I18nMsg.st+"|"+I18nMsg.nd+"|"+I18nMsg.rd+"|"+I18nMsg.th+")?";

// needs to be kept in sync with Com_Zimbra_Date.MONTH
//var $RE_MONTH = "(Jan(?:uary)?|Feb(?:ruary)?|Mar(?:ch)?|Apr(?:il)?|May|June?|July?|Aug(?:ust)?|Sep(?:t(?:ember)?)?|Oct(?:ober)?|Nov(?:ember)?|Dec(?:ember)?)";
//var $RE_MONTH = "("+I18nMsg.monthJanLong+"|"+I18nMsg.monthFebLong+"|"+I18nMsg.monthMarLong+"|"+I18nMsg.monthAprLong+"|"+I18nMsg.monthMayLong+"|"+I18nMsg.monthJunLong+"|"+I18nMsg.monthJulLong+"|"+I18nMsg.monthAugLong+"|"+I18nMsg.monthSepLong+"|"+I18nMsg.monthOctLong+"|"+I18nMsg.monthNovLong+"|"+I18nMsg.monthDecLong+"|"+I18nMsg.monthJanMedium+"|"+I18nMsg.monthFebMedium+"|"+I18nMsg.monthMarMedium+"|"+I18nMsg.monthAprMedium+"|"+I18nMsg.monthMayMedium+"|"+I18nMsg.monthJunMedium+"|"+I18nMsg.monthJulMedium+"|"+I18nMsg.monthAugMedium+"|"+I18nMsg.monthSepMedium+"|"+I18nMsg.monthOctMedium+"|"+I18nMsg.monthNovMedium+"|"+I18nMsg.monthDecMedium+")";



//var $RE_TODAY_TOMORROW_YESTERDAY = "(today|tomorrow|yesterday)";
//var $RE_TODAY_TOMORROW_YESTERDAY = "("+this.getMessage("today")+"|"+this.getMessage("tomorrow")+"|"+this.getMessage("yesterday")+")";

//var $RE_NEXT_THIS_LAST = "(next|this|last)";
//var $RE_NEXT_THIS_LAST = "("+this.getMessage("next")+"|"+this.getMessage("dis")+"|"+this.getMessage("last")+")";

//var $RE_COMMA_OR_SP = this.getMessage("RE_COMMA_OR_SP");//"(?:\\s+|\\s*,\\s*)";

//var $RE_DASH = this.getMessage("RE_DASH");//"(?:-)";

//var $RE_DD = "(\\d{1,2})";

//var $RE_OP_DOW = "(?:\\s*" + $RE_DOW + "\\s*)?";

//var $RE_OP_YEAR42 = "(?:" + $RE_COMMA_OR_SP + $RE_YEAR42 + ")?";

//var $RE_OP_YEAR4 = "(?:" + $RE_COMMA_OR_SP + $RE_YEAR4 + ")?";

Com_Zimbra_Date.prototype.getRE_SP = function(){
    var $RE_SP = "\\s+";
    return $RE_SP;
};

Com_Zimbra_Date.prototype.getRE_SLASH = function(){
    var $RE_SLASH = "(?:\\/)";
    return $RE_SLASH ;
};

Com_Zimbra_Date.prototype.getRE_YEAR4 = function(){
    var $RE_YEAR4 = "(\\d{4})";
    return $RE_YEAR4;
};

Com_Zimbra_Date.prototype.getRE_YEAR42 = function(){
    var $RE_YEAR42 = "(\\d{4}|\\d{2})";
    return $RE_YEAR42;
};

Com_Zimbra_Date.prototype.getRE_MM = function(){
    var $RE_MM = "(\\d{1,2})";
    return $RE_MM;
};

Com_Zimbra_Date.prototype.getRE_DD = function(){
    return this.getRE_MM();
};

Com_Zimbra_Date.prototype.getRE_OP_TIME = function(){
    var $RE_OP_TIME = "(?:\\s+\\d{1,2}:\\d{2}:\\d{2})?";
    return $RE_OP_TIME;
};

Com_Zimbra_Date.prototype.getRE_DOW = function(){
	var $RE_DOW = "("+I18nMsg.weekdayMonLong+"|"+I18nMsg.weekdayMonMedium+"|"+I18nMsg.weekdayTueLong+"|"+I18nMsg.weekdayTueMedium+"|"+I18nMsg.weekdayWedLong+"|"+I18nMsg.weekdayWedMedium+"|"+I18nMsg.weekdayThuLong+"|"+I18nMsg.weekdayThuMedium+"|"+I18nMsg.weekdayFriLong+"|"+I18nMsg.weekdayFriMedium+"|"+I18nMsg.weekdaySatLong+"|"+I18nMsg.weekdaySatMedium+"|"+I18nMsg.weekdaySunLong+"|"+I18nMsg.weekdaySunMedium+")";
	return $RE_DOW;
};

Com_Zimbra_Date.prototype.getRE_DOW_FULL = function(){
	var $RE_DOW_FULL = "("+I18nMsg.weekdayMonLong+"|"+I18nMsg.weekdayTueLong+"|"+I18nMsg.weekdayWedLong+"|"+I18nMsg.weekdayThuLong+"|"+I18nMsg.weekdayFriLong+"|"+I18nMsg.weekdaySatLong+"|"+I18nMsg.weekdaySunLong+")";
	return $RE_DOW_FULL;
};

Com_Zimbra_Date.prototype.getRE_DOM = function(){
	var $RE_DOM = this.getMessage("RE_DOM");
	return $RE_DOM;
};

Com_Zimbra_Date.prototype.getRE_MONTH = function(){
	var $RE_MONTH = "("+I18nMsg.monthJanLong+"|"+I18nMsg.monthFebLong+"|"+I18nMsg.monthMarLong+"|"+I18nMsg.monthAprLong+"|"+I18nMsg.monthMayLong+"|"+I18nMsg.monthJunLong+"|"+I18nMsg.monthJulLong+"|"+I18nMsg.monthAugLong+"|"+I18nMsg.monthSepLong+"|"+I18nMsg.monthOctLong+"|"+I18nMsg.monthNovLong+"|"+I18nMsg.monthDecLong+"|"+I18nMsg.monthJanMedium+"|"+I18nMsg.monthFebMedium+"|"+I18nMsg.monthMarMedium+"|"+I18nMsg.monthAprMedium+"|"+I18nMsg.monthMayMedium+"|"+I18nMsg.monthJunMedium+"|"+I18nMsg.monthJulMedium+"|"+I18nMsg.monthAugMedium+"|"+I18nMsg.monthSepMedium+"|"+I18nMsg.monthOctMedium+"|"+I18nMsg.monthNovMedium+"|"+I18nMsg.monthDecMedium+")";
	return $RE_MONTH;
};

Com_Zimbra_Date.prototype.getRE_TODAY_TOMORROW_YESTERDAY = function(){
	var $RE_TODAY_TOMORROW_YESTERDAY = "("+this.getMessage("today")+"|"+this.getMessage("tomorrow")+"|"+this.getMessage("yesterday")+")";
	return $RE_TODAY_TOMORROW_YESTERDAY;
};

Com_Zimbra_Date.prototype.getRE_NEXT_THIS_LAST = function(){
	var $RE_NEXT_THIS_LAST = "("+this.getMessage("next")+"|"+this.getMessage("dis")+"|"+this.getMessage("last")+")";
	return $RE_NEXT_THIS_LAST;
};

Com_Zimbra_Date.prototype.getRE_COMMA_OR_SP = function(){
	var $RE_COMMA_OR_SP = this.getMessage("RE_COMMA_OR_SP");//"(?:\\s+|\\s*,\\s*)";
	return $RE_COMMA_OR_SP;
};

Com_Zimbra_Date.prototype.getRE_DASH = function(){
	var $RE_DASH = this.getMessage("RE_DASH");
	return $RE_DASH;
};

Com_Zimbra_Date.prototype.getRE_OP_DOW = function(){
	var $RE_OP_DOW = "(?:\\s*" + this.getRE_DOW() + "\\s*)?";
	return $RE_OP_DOW;
};

Com_Zimbra_Date.prototype.getRE_OP_YEAR42 = function(){
	var $RE_OP_YEAR42 = "(?:" + this.getRE_COMMA_OR_SP() + this.getRE_YEAR42() + ")?";
	return $RE_OP_YEAR42;
};

Com_Zimbra_Date.prototype.getRE_OP_YEAR4 = function(){
	var $RE_OP_YEAR4 = "(?:" + this.getRE_COMMA_OR_SP() + this.getRE_YEAR4() + ")?";
	return $RE_OP_YEAR4;
};


// today/yesterday =======================

function ZmDate1ObjectHandler() {
	Com_Zimbra_Date.call(this);
	ZmDate1ObjectHandler.REGEX = new RegExp("\\b" + eval("''"+this.getMessage("datePattern1").replace(/{(RE_[a-zA-Z_]+[0-9]*)}/g,"+this.get$1()")) + "\\b", "ig");
}

ZmDate1ObjectHandler.prototype = new Com_Zimbra_Date();
ZmDate1ObjectHandler.prototype.constructor = ZmDate1ObjectHandler;
ZmDate1ObjectHandler.prototype.name = "com_zimbra_date1";

//ZmDate1ObjectHandler.REGEX = new RegExp("\\b" + $RE_TODAY_TOMORROW_YESTERDAY + "\\b", "ig");
//ZmDate1ObjectHandler.REGEX = new RegExp("\\b" + eval("''"+this.getMessage("datePattern1").replace(/{(RE_[a-zA-Z_]+[0-9]*)}/g,"+this.get$1()")) + "\\b", "ig");

ZmDate1ObjectHandler.prototype.matchRegex =
function(line, startIndex) {
	ZmDate1ObjectHandler.REGEX.lastIndex = startIndex;
	var result = ZmDate1ObjectHandler.REGEX.exec(line);
	if (!result) {return null;}

	var d = new Date(this.getCurrentDate().getTime());
	var when = result[1].toLowerCase();
	if (when == this.getMessage("yesterday")/*"yesterday"*/) {
		d.setDate(d.getDate() - 1);
	} else if (when == this.getMessage("tomorrow")/*"tomorrow"*/) {
		d.setDate(d.getDate() + 1);
	}
	result.context = {date: d, monthOnly: 0, valid: true};
	return result;
};

// {next Tuesday}, {last Monday}, etc

function ZmDate2ObjectHandler() {
	Com_Zimbra_Date.call(this);
	ZmDate2ObjectHandler.REGEX = new RegExp("\\b" + eval("''"+this.getMessage("datePattern2").replace(/{(RE_[a-zA-Z_]+[0-9]*)}/g,"+this.get$1()"))+ "\\b", "ig");

}

ZmDate2ObjectHandler.prototype = new Com_Zimbra_Date();
ZmDate2ObjectHandler.prototype.constructor = ZmDate2ObjectHandler;
ZmDate2ObjectHandler.prototype.name = "com_zimbra_date2";

//ZmDate2ObjectHandler.REGEX = new RegExp("\\b" + $RE_NEXT_THIS_LAST + $RE_SP + $RE_DOW + "\\b", "ig");
//ZmDate2ObjectHandler.REGEX = new RegExp("\\b" + eval("''"+this.getMessage("datePattern2").replace(/{(RE_[a-zA-Z_]+[0-9]*)}/g,"+this.get$1()"))+ "\\b", "ig");

ZmDate2ObjectHandler.prototype.matchRegex =
function(line, startIndex) {
	ZmDate2ObjectHandler.REGEX.lastIndex = startIndex;
	var result = ZmDate2ObjectHandler.REGEX.exec(line);
	if (!result) {return null;}

    //find the position of last_next_this and dow in the pattern
    var dowIndex = 2;
    var ntlIndex = 1;
    if(this.getMessage("datePattern2").indexOf("{RE_DOW}")==0){//reverse pattern
        dowIndex = 1;
        ntlIndex = 2;
    }
    var d = new Date(this.getCurrentDate().getTime());
	var dow = d.getDay();
	var ndow = Com_Zimbra_Date.DOW[result[dowIndex].toLowerCase().substring(0,2)];
	var addDays;

	if (result[ntlIndex].toLowerCase() == /*"next"*/this.getMessage("next")) {
		addDays = ndow - dow;
		addDays += 7;
	} else if (result[ntlIndex].toLowerCase() == /*"this"*/this.getMessage("dis")) {
		addDays = ndow - dow;
	} else { // last
		addDays = (-1 * (dow + 7 - ndow)) % 7;
		if (addDays === 0) {
			addDays = -7;
		}
	}
	d.setDate(d.getDate() + addDays);
	result.context = {date: d, monthOnly: 0, valid: true};
	return result;
};

// {25th December}, {6th, June}, {6 June 2004}, {25th December, 2005}

function ZmDate3ObjectHandler() {
	Com_Zimbra_Date.call(this);
	ZmDate3ObjectHandler.REGEX = new RegExp("\\b" + eval("''"+this.getMessage("datePattern3").replace(/{(RE_[a-zA-Z_]+[0-9]*)}/g,"+this.get$1()")) + "\\b", "ig");

}

ZmDate3ObjectHandler.prototype = new Com_Zimbra_Date();
ZmDate3ObjectHandler.prototype.constructor = ZmDate3ObjectHandler;
ZmDate3ObjectHandler.prototype.name = "com_zimbra_date3";
//ZmDate3ObjectHandler.REGEX = new RegExp("\\b" + eval("''"+this.getMessage("datePattern3").replace(/{(RE_[a-zA-Z_]+[0-9]*)}/g,"+this.get$1()")) + "\\b", "ig");

ZmDate3ObjectHandler.prototype.matchRegex =
function(line, startIndex) {
	ZmDate3ObjectHandler.REGEX.lastIndex = startIndex;
	var result = ZmDate3ObjectHandler.REGEX.exec(line);
	if (!result) {return null;}

	var d = new Date(this.getCurrentDate().getTime());
	var dom = parseInt(result[1], 10);
	var month = Com_Zimbra_Date.MONTH[result[2].toLowerCase()];
    d.setMonth(month, dom);
    var year = null;
	if (result[3]) {
		year = parseInt(result[3], 10);
		if (year < 20) {
			year += 2000;
		} else if (year < 100) {
			year += 1900;
		}
        d.setYear(year);
    }
    var isValid = Com_Zimbra_Date.validate(dom,month,year);
    result.context = {date: d, monthOnly: 0, valid: isValid};
	return result;
};

// {June 6th, 2005}, {June 6}, {May 24 10:11:26 2005},

function ZmDate4ObjectHandler() {
	Com_Zimbra_Date.call(this);
	ZmDate4ObjectHandler.REGEX = new RegExp("\\b" + eval("''"+this.getMessage("datePattern4").replace(/{(RE_[a-zA-Z_]+[0-9]*)}/g,"+this.get$1()")) + "\\b", "ig");

}

ZmDate4ObjectHandler.prototype = new Com_Zimbra_Date();
ZmDate4ObjectHandler.prototype.constructor = ZmDate4ObjectHandler;
ZmDate4ObjectHandler.prototype.name = "com_zimbra_date4";
//ZmDate4ObjectHandler.REGEX = new RegExp("\\b" + eval("''"+this.getMessage("datePattern4").replace(/{(RE_[a-zA-Z_]+[0-9]*)}/g,"+this.get$1()")) + "\\b", "ig");

ZmDate4ObjectHandler.prototype.matchRegex =
function(line, startIndex) {
	ZmDate4ObjectHandler.REGEX.lastIndex = startIndex;
	var result = ZmDate4ObjectHandler.REGEX.exec(line);
	if (!result) {return null;}

	var d = new Date(this.getCurrentDate().getTime());
	var month = Com_Zimbra_Date.MONTH[result[1].toLowerCase()];
	var dom = parseInt(result[2], 10);
    d.setMonth(month, dom);
    var year = null;
    if (result[4]) {
		year = parseInt(result[3], 10);
		if (year > 1000) {
			d.setYear(year);
		}
	} else if (result[3]) {
		year = parseInt(result[3], 10);
		if (year > 1000) {
			d.setYear(year);
		}
	}
    var isValid = Com_Zimbra_Date.validate(dom,month,year);
    result.context = {date: d, monthOnly: 0, valid: isValid};
	return result;
};

// {12-25-2005}, {06-06-05}, etc

function ZmDate5ObjectHandler() {
	Com_Zimbra_Date.call(this);
	ZmDate5ObjectHandler.REGEX = new RegExp("\\b" + eval("''"+this.getMessage("datePattern5").replace(/{(RE_[a-zA-Z_]+[0-9]*)}/g,"+this.get$1()")) + "\\b", "ig");

}

ZmDate5ObjectHandler.prototype = new Com_Zimbra_Date();
ZmDate5ObjectHandler.prototype.constructor = ZmDate5ObjectHandler;
ZmDate5ObjectHandler.prototype.name = "com_zimbra_date5";
//ZmDate5ObjectHandler.REGEX = new RegExp("\\b" + eval("''"+this.getMessage("datePattern5").replace(/{(RE_[a-zA-Z_]+[0-9]*)}/g,"+this.get$1()")) + "\\b", "ig");

ZmDate5ObjectHandler.prototype.matchRegex =
function(line, startIndex) {
	ZmDate5ObjectHandler.REGEX.lastIndex = startIndex;
	var result = ZmDate5ObjectHandler.REGEX.exec(line);
	if (!result) {return null;}

	var d = new Date(this.getCurrentDate().getTime());
	var domIndex = 2;
    var monIndex = 1;
    if(this.getMessage("datePattern5").indexOf("{RE_DD}")==0){ //its DD-MM-YYYY
       domIndex = 1;
       monIndex = 2;
    }
    var month = parseInt(result[monIndex], 10) - 1;
	var dom = parseInt(result[domIndex], 10);
    d.setMonth(month, dom);
	var year = parseInt(result[3], 10);
	if (year < 20) {
		year += 2000;
	} else if (year < 100) {
		year += 1900;
	}
	d.setYear(year);
    var isValid = Com_Zimbra_Date.validate(dom, month, year);
	result.context = {date: d, monthOnly: 0, valid: isValid};
	return result;
};

// {2005-06-24}

function ZmDate6ObjectHandler() {
	Com_Zimbra_Date.call(this);
	ZmDate6ObjectHandler.REGEX = new RegExp("\\b" + eval("''"+this.getMessage("datePattern6").replace(/{(RE_[a-zA-Z_]+[0-9]*)}/g,"+this.get$1()")) + "\\b", "ig");

}

ZmDate6ObjectHandler.prototype = new Com_Zimbra_Date();
ZmDate6ObjectHandler.prototype.constructor = ZmDate6ObjectHandler;
ZmDate6ObjectHandler.prototype.name = "com_zimbra_date6";
//ZmDate6ObjectHandler.REGEX = new RegExp("\\b" + eval("''"+this.getMessage("datePattern6").replace(/{(RE_[a-zA-Z_]+[0-9]*)}/g,"+this.get$1()")) + "\\b", "ig");

ZmDate6ObjectHandler.prototype.matchRegex =
function(line, startIndex) {
	ZmDate6ObjectHandler.REGEX.lastIndex = startIndex;
	var result = ZmDate6ObjectHandler.REGEX.exec(line);
	if (!result) {return null;}

	var d = new Date(this.getCurrentDate().getTime());
	var year = parseInt(result[1], 10);
	var month = parseInt(result[2], 10) - 1;
	var dom = parseInt(result[3], 10);
    d.setMonth(month, dom);
	d.setYear(year);
    var isValid = Com_Zimbra_Date.validate(dom,month,year);
	result.context = {date: d, monthOnly: 0, valid: isValid};
	return result;
};

//{12/25/2005}, {06/06/05}, etc

function ZmDate7ObjectHandler() {
	Com_Zimbra_Date.call(this);
	ZmDate7ObjectHandler.REGEX = new RegExp("\\b" + eval("''"+this.getMessage("datePattern7").replace(/{(RE_[a-zA-Z_]+[0-9]*)}/g,"+this.get$1()")) + "\\b", "ig");

}

ZmDate7ObjectHandler.prototype = new Com_Zimbra_Date();
ZmDate7ObjectHandler.prototype.constructor = ZmDate7ObjectHandler;
ZmDate7ObjectHandler.prototype.name = "com_zimbra_date7";
//ZmDate7ObjectHandler.REGEX = new RegExp("\\b" + eval("''"+this.getMessage("datePattern7").replace(/{(RE_[a-zA-Z_]+[0-9]*)}/g,"+this.get$1()")) + "\\b", "ig");

ZmDate7ObjectHandler.prototype.matchRegex =
function(line, startIndex) {
	ZmDate7ObjectHandler.REGEX.lastIndex = startIndex;
	var result = ZmDate7ObjectHandler.REGEX.exec(line);
	if (!result) {return null;}

	var d = new Date(this.getCurrentDate().getTime());
    var dIndex = 2;
    var mIndex  = 1;

    if(I18nMsg.formatDateShort.indexOf("d") == 0 || I18nMsg.formatDateShort.indexOf("D") == 0){
        dIndex = 1;
        mIndex = 2;
    }

    var month = parseInt(result[mIndex], 10) - 1;
	var dom = parseInt(result[dIndex], 10);

    d.setMonth(month, dom);
	var year = parseInt(result[3], 10);
	if (year < 20) {
		year += 2000;
	} else if (year < 100) {
		year += 1900;
	}
	d.setYear(year);
    var isValid = Com_Zimbra_Date.validate(dom,month,year);
	result.context = {date: d, monthOnly: 0, valid: isValid};
	return result;
};

// {2005/06/24}, {2005/12/25}

function ZmDate8ObjectHandler() {
	Com_Zimbra_Date.call(this);
	ZmDate8ObjectHandler.REGEX = new RegExp("\\b" + eval("''"+this.getMessage("datePattern8").replace(/{(RE_[a-zA-Z_]+[0-9]*)}/g,"+this.get$1()")) + "\\b", "ig");

}

ZmDate8ObjectHandler.prototype = new Com_Zimbra_Date();
ZmDate8ObjectHandler.prototype.constructor = ZmDate8ObjectHandler;
ZmDate8ObjectHandler.prototype.name = "com_zimbra_date8";
//ZmDate8ObjectHandler.REGEX = new RegExp("\\b" + eval("''"+this.getMessage("datePattern8").replace(/{(RE_[a-zA-Z_]+[0-9]*)}/g,"+this.get$1()")) + "\\b", "ig");

ZmDate8ObjectHandler.prototype.matchRegex =
function(line, startIndex) {
	ZmDate8ObjectHandler.REGEX.lastIndex = startIndex;
	var result = ZmDate8ObjectHandler.REGEX.exec(line);
	if (!result) {return null;}
	var d = new Date(this.getCurrentDate().getTime());
	var year = parseInt(result[1], 10);
	var month = parseInt(result[2], 10) - 1;
	var dom = parseInt(result[3], 10);
    d.setMonth(month, dom);
	d.setYear(year);
    var isValid = Com_Zimbra_Date.validate(dom, month, year);
    result.context = {date: d, monthOnly: 0, valid: isValid};
	return result;
};

// {June 2005}

function ZmDate9ObjectHandler() {
	Com_Zimbra_Date.call(this);
	ZmDate9ObjectHandler.REGEX = new RegExp("\\b" + eval("''"+this.getMessage("datePattern9").replace(/{(RE_[a-zA-Z_]+[0-9]*)}/g,"+this.get$1()")) + "\\b", "ig");

}

ZmDate9ObjectHandler.prototype = new Com_Zimbra_Date();
ZmDate9ObjectHandler.prototype.constructor = ZmDate9ObjectHandler;
ZmDate9ObjectHandler.prototype.name = "com_zimbra_date9";
//ZmDate9ObjectHandler.REGEX = new RegExp("\\b" + eval("''"+this.getMessage("datePattern9").replace(/{(RE_[a-zA-Z_]+[0-9]*)}/g,"+this.get$1()")) + "\\b", "ig");

ZmDate9ObjectHandler.prototype.matchRegex =
function(line, startIndex) {
	ZmDate9ObjectHandler.REGEX.lastIndex = startIndex;
	var result = ZmDate9ObjectHandler.REGEX.exec(line);
	if (!result) {return null;}

	var d = new Date(this.getCurrentDate().getTime());
	var month = Com_Zimbra_Date.MONTH[result[1].toLowerCase()];
	d.setMonth(month, 1);
	var year = result[2] ? parseInt(result[2], 10) : null;
	if (year) d.setYear(year);
    var isValid = Com_Zimbra_Date.validate(2, month, year); //Dummy dayOfMonth to validate the month,year
    result.context = {date: d, monthOnly: 1, valid: isValid};
	return result;
};


// {Tuesday}, {Monday}, etc

function ZmDate10ObjectHandler() {
	Com_Zimbra_Date.call(this);
	ZmDate10ObjectHandler.REGEX = new RegExp("\\b" + eval("''"+this.getMessage("datePattern10").replace(/{(RE_[a-zA-Z_]+[0-9]*)}/g,"+this.get$1()")) + "\\b", "ig");

}

ZmDate10ObjectHandler.prototype = new Com_Zimbra_Date();
ZmDate10ObjectHandler.prototype.constructor = ZmDate10ObjectHandler;
ZmDate10ObjectHandler.prototype.name = "com_zimbra_date10";

//ZmDate10ObjectHandler.REGEX = new RegExp("\\b" + eval("''"+this.getMessage("datePattern10").replace(/{(RE_[a-zA-Z_]+[0-9]*)}/g,"+this.get$1()")) + "\\b", "ig");

ZmDate10ObjectHandler.prototype.matchRegex =
function(line, startIndex) {
	ZmDate10ObjectHandler.REGEX.lastIndex = startIndex;
	var result = ZmDate10ObjectHandler.REGEX.exec(line);
	if (!result) {return null;}

	var d = new Date(this.getCurrentDate().getTime());
	var dow = d.getDay();
	var ndow = Com_Zimbra_Date.DOW[result[1].toLowerCase().substring(0,2)];
	var addDays = ndow - dow;
	d.setDate(d.getDate() + addDays);
	result.context = {date: d, monthOnly: 0, valid: true};
	return result;
};
