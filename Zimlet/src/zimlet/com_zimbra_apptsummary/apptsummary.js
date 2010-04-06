/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
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
 * @author Raja Rao DV
 */

/**
 * Inserts email with current day's appointments on first login.
 * 
 */
com_zimbra_apptsummary_HandlerObject = function() {
};

com_zimbra_apptsummary_HandlerObject.prototype = new ZmZimletBase;
com_zimbra_apptsummary_HandlerObject.prototype.constructor = com_zimbra_apptsummary_HandlerObject;

/**
 * Defines the "regular only" type for appointment summary HTML.
 */
com_zimbra_apptsummary_HandlerObject.TYPE_SHOW_REGULAR_ONLY = "SHOW_REGULAR_ONLY";
/**
 * Defines the "free declined" type for appointment summary HTML.
 */
com_zimbra_apptsummary_HandlerObject.TYPE_SHOW_FREE_DECLINED = "SHOW_FREE_DECLINED";

/**
 * Defines the "last update date" user property.
 */
com_zimbra_apptsummary_HandlerObject.USER_PROP_LAST_UPDATE = "apptSummary_emailLastUpdateDate";
/**
 * Defines the "only when appointments exist" user property.
 */
com_zimbra_apptsummary_HandlerObject.USER_PROP_ONLY_SEND_APPTS = "apptSummary_onlySendSummaryWhenThereAreAppts";

/**
 * Defines the "only when appointments exist" element ID.
 */
com_zimbra_apptsummary_HandlerObject.ELEMENT_ONLY_SEND_APPTS = "apptSummary_onlySendSummaryWhenThereAreAppts";

/**
 * Initializes the zimlet.
 * 
 */
com_zimbra_apptsummary_HandlerObject.prototype.init =
function() {
	var emailLastUpdateDate = this.getUserProperty(com_zimbra_apptsummary_HandlerObject.USER_PROP_LAST_UPDATE);
	var todayStr = this._getTodayStr();
	if (true) { //emailLastUpdateDate != todayStr) {
		this.apptSummary_onlySendSummaryWhenThereAreAppts = this.getUserProperty(com_zimbra_apptsummary_HandlerObject.USER_PROP_ONLY_SEND_APPTS) == "true";
		var appts = this._getAppts(new Date());
		this._parseApptsAndSendEmail(appts);
		this.setUserProperty(com_zimbra_apptsummary_HandlerObject.USER_PROP_LAST_UPDATE, todayStr, true);
	}
};

/**
 * Gets the appointments.
 * 
 * @param	{Date}	date	the date
 * @param	{boolean}	noheader		not used
 * @return	{AjxVector} an array of {@link ZmAppt} objects or an empty array for none
 */
com_zimbra_apptsummary_HandlerObject.prototype._getAppts =
function(date, noheader) {
	try {
		this._startDate = new Date(date.getTime());
		this._startDate.setHours(0, 0, 0, 0);
		var startTime = this._startDate.getTime();
		var end = this._startDate.getTime() + AjxDateUtil.MSEC_PER_DAY;
		var params = {start:startTime, end:end, fanoutAllDay:true};
		this._calController = AjxDispatcher.run("GetCalController");
		return result = this._calController.getApptSummaries(params);
	} catch (ex) {
		DBG.println(ex);
		return new AjxVector();
	}
};

/**
 * Parses the appointment array and sends an email summary.
 * 
 * @param	{AjxVector}	appts		an array of {@link ZmAppt} objects
 */
com_zimbra_apptsummary_HandlerObject.prototype._parseApptsAndSendEmail =
function(appts) {
	var hasError = false;
	var body = "";
	if (appts instanceof AjxVector) {
		if (appts.size() == 0 && this.apptSummary_onlySendSummaryWhenThereAreAppts) {
			return;
		}
		body = this._getApptsSummaryBody(this._startDate, appts, this._calController, null);
	} else {
		hasError = true;
		body = this.getMessage("ApptSummary_error_unabletogetappointments");
	}
	this._sendEmail(body, hasError);
};

/**
 * Gets the appointments summary email body.
 * 
 * @param	{date}		date	the date
 * @param	{AjxVector}	list	an array of {@link ZmAppt} objects
 * @param	{ZmCalViewController}	controller		the calendar view controller
 * @param	{boolean}	noheader	not used
 * @param	{string}	emptyMsg	the empty message body
 * @return	{string}	the appointment summary email body
 */
com_zimbra_apptsummary_HandlerObject.prototype._getApptsSummaryBody =
function(date, list, controller, noheader, emptyMsg) {
	this.__apptCount = 0;
	if (!emptyMsg) {
		emptyMsg = ZmMsg.noAppts;
	}
	var html = new AjxBuffer();
	var formatter = DwtCalendar.getDateFullFormatter();
	var title = formatter.format(date);
	html.append(this._getApptsHtml(date, list, controller, noheader, emptyMsg, com_zimbra_apptsummary_HandlerObject.TYPE_SHOW_REGULAR_ONLY));
	html.append("<br/>");
	html.append(this._getApptsHtml(date, list, controller, noheader, emptyMsg, com_zimbra_apptsummary_HandlerObject.TYPE_SHOW_FREE_DECLINED));
	return html.toString();
};

/**
 * Gets the appointments summary as HTML.
 * 
 * @param	{date}		date	the date
 * @param	{AjxVector}	list	an array of {@link ZmAppt} objects
 * @param	{ZmCalViewController}	controller		the calendar view controller
 * @param	{boolean}	noheader	not used
 * @param	{string}	emptyMsg	the empty message body
 * @param	{constant}	type		the summary type (see <code>TYPE_*</code> constants)
 * @return	{string}	the appointment summary email body
 */
com_zimbra_apptsummary_HandlerObject.prototype._getApptsHtml =
function(date, list, controller, noheader, emptyMsg, type) {
	var html = new AjxBuffer();
	var apptsFound = false;
	if (type == com_zimbra_apptsummary_HandlerObject.TYPE_SHOW_REGULAR_ONLY) {
		var title = this.getMessage("ApptSummary_header_apts");
	} else {
		var title = this.getMessage("ApptSummary_header_notbusy");
	}
	var hdrDivStyle = " style='background-color:#D7CFBE;border-bottom:1px solid #A7A194;'";
	html.append("<table cellpadding='0' cellspacing='0' border='0' width=94% align=center><tr><td>");
	html.append("<div style='border-bottom:1px solid #A7A194;border-right:1px solid #A7A194;border-left:1px solid #CFCFCF'>");

	html.append("<table cellpadding='0' cellspacing='0' border='0' width=100% align=center>");
	html.append("<tr><td><div ", hdrDivStyle, ">");
	html.append("<table cellpadding='3' cellspacing='0' border='0' width=100% align=center>");
	html.append("<tr align=left><th><strong>", title, "</strong></th></tr></table>");
	html.append("</div>");
	html.append("</td></tr></table>");

	html.append("<table cellpadding='3' cellspacing='0' border='0' width=100% align=center>");
	html.append("<tr ", hdrDivStyle, " align=left><th width=15px  align=center valign=middle>#</th><th width=150px>", this.getMessage("ApptSummary_calendar"), "</th>");
	html.append("<th width=100px>", this.getMessage("ApptSummary_label_from"), "</th>");
	html.append("<th width=100px>", this.getMessage("ApptSummary_label_to"), "</th>");
	html.append("<th>", this.getMessage("ApptSummary_label_details"), "</th></tr>");
	var formatter_med = AjxDateFormat.getTimeInstance(AjxDateFormat.SHORT);
	var formatter_long = AjxDateFormat.getTimeInstance(AjxDateFormat.LONG);
	var isRowOdd = true;
	var size = list ? list.size() : 0;
	var freeDecCounter = 0;
	for (var i = 0; i < size; i++) {
		var ao = list.get(i);
		if (type == com_zimbra_apptsummary_HandlerObject.TYPE_SHOW_REGULAR_ONLY) {
			if (ao.ptst == "DE" || ao.fba == "F" || ao.fba == "O") {//ignore declined and/or free appointments
				continue;
			}
			var counter = ++this.__apptCount;
		} else if (type == com_zimbra_apptsummary_HandlerObject.TYPE_SHOW_FREE_DECLINED) {
			if (ao.ptst != "DE" && ao.fba != "F" && ao.fba != "O") {//only free/declined appts are allowed
				continue;
			}
			var counter = ++freeDecCounter;
		}
		apptsFound = true;
		var color = ZmCalendarApp.COLORS[controller.getCalendarColor(ao.folderId)];
		var bgColor = ao.getFolder().rgb || ZmOrganizer.COLOR_VALUES[ao.getFolder().color];
		if (isRowOdd) {
			html.append("<tr align=left style='background-color:#FBF9F4;border-top-color:#FBF9F4;'>");
		} else {
			html.append("<tr align=left>");
		}

		html.append("<td  align=center valign=middle style='background-color:", bgColor, ";'>", counter, "</td>");
		html.append("<td>", controller.getCalendarName(ao.folderId), "</td>");
		if (!ao.isAllDayEvent()) {
			if (ao.isMultiDay()) {
				var startTime = formatter_long.format(ao.startDate);
				var endTime = formatter_long.format(ao.endDate);
			} else {
				var startTime = formatter_med.format(ao.startDate);
				var endTime = formatter_med.format(ao.endDate);
			}
		} else {
			var startTime = this.getMessage("ApptSummary_allday");
			var endTime = this.getMessage("ApptSummary_allday");
		}

		var dur = "<td>" + startTime + "&nbsp;" + "</td><td width=100px>" + endTime;
		html.append(dur);
		if (dur != "") {
			html.append("&nbsp;");
		}
		html.append("</td><td>");
		var isNew = ao.ptst == ZmCalBaseItem.PSTATUS_NEEDS_ACTION;
		if (isNew) {
			html.append("<span style='color:red;font-weight:bold;font-size:11px'>[", this.getMessage("ApptSummary_apt_new"), "]</span>&nbsp;");
		} else if (ao.ptst == "DE") {
			html.append("<span style='color:gray;font-size:11px'>[", this.getMessage("ApptSummary_apt_declined"), "]</span>&nbsp;");
		} else if (ao.fba == "F") {
			html.append("<span style='color:gray;font-size:11px'>[", this.getMessage("ApptSummary_apt_free"), "]</span>&nbsp;");
		} else if (ao.fba == "O") {
			html.append("<span style='color:gray;font-size:11px'>[", this.getMessage("ApptSummary_apt_ooo"), "]</span>&nbsp;");
		}
		html.append(AjxStringUtil.htmlEncode(ao.getName()));
		var loc = AjxStringUtil.htmlEncode(ao.getLocation());
		if (loc != "") {
			html.append("&nbsp; <span style='color:gray;font-size:11px'> ", this.getMessage("ApptSummary_label_location"), " - ", loc, "</span>");
		}

		html.append("</td></tr>");
		isRowOdd = !isRowOdd;
	}
	if (size == 0) {
		html.append("<tr align=left><td colspan=3>" + emptyMsg + "</td></tr>");
	}
	html.append("</table>");

	html.append("</div>");
	html.append("</td></tr></table>");
	if (apptsFound) {
		return html.join("");
	} else {
		return "";
	}
};

/**
 * Sends the email.
 * 
 * @param	{string}	body		the message body
 * @param	{boolean}	hasError	if <code>true</code>, the email contains an error message
 */
com_zimbra_apptsummary_HandlerObject.prototype._sendEmail =
function(body, hasError) {
	if (hasError) {
		var subject = this.getMessage("ApptSummary_subject_error");
	} else {
		var subject = this.getMessage("ApptSummary_subject_success").replace("{0}", this.__apptCount);
		if (this.__apptCount != 1) {
			subject = subject.replace("{1}", this.getMessage("ApptSummary_appointments"));
		} else {
			subject = subject.replace("{1}", this.getMessage("ApptSummary_appointment"));
		}
	}
	var jsonObj = {SendMsgRequest:{_jsns:"urn:zimbraMail"}};
	var request = jsonObj.SendMsgRequest;
	request.suid = (new Date()).getTime();
	var msgNode = request.m = {};
	var identity = appCtxt.getIdentityCollection().defaultIdentity;
	msgNode.idnt = identity.id;

	var isPrimary = identity == null || identity.isDefault;
	var mainAcct = appCtxt.accountList.mainAccount.getEmail();
	var addr = identity.sendFromAddress || mainAcct;
	var displayName = identity.sendFromDisplay;
	var addrNodes = msgNode.e = [];
	var f_addrNode = {t:"f", a:addr};
	if (displayName) {
		f_addrNode.p = displayName;
	}
	addrNodes.push(f_addrNode);

	var t_addrNode = {t:"t", a:addr};
	if (displayName) {
		t_addrNode.p = displayName;
	}
	addrNodes.push(t_addrNode);
	msgNode.su = {_content: subject};
	var topNode = {ct: "multipart/alternative"};
	msgNode.mp = [topNode];
	var partNodes = topNode.mp = [];

	//text part..
	//var content = "some text";
	//var partNode = {ct:"text/plain"};
	//partNode.content = {_content:content};
	//partNodes.push(partNode);

	//html part..
	var content = ["<html><head><style type='text/css'>p { margin: 0; }</style></head>",
		"<body><div style='font-family: Times New Roman; font-size: 12pt; color: #000000'>",
		body,"</div></body></html>"].join("");

	var partNode = {ct:"text/html"};
	partNode.content = {_content:content};
	partNodes.push(partNode);
	var callback = new AjxCallback(this, this._sendEmailCallack);
	var errCallback = new AjxCallback(this, this._sendEmailErrCallback);
	return appCtxt.getAppController().sendRequest({jsonObj:jsonObj, asyncMode:true, errorCallback:errCallback, callback:callback});
};

/**
 * Send email callback.
 * 
 * @see		_sendEmail
 */
com_zimbra_apptsummary_HandlerObject.prototype._sendEmailCallack =
function(param1, param2) {
	//do nothing on success
};

/**
 * Send email error callback.
 * 
 * @see		_sendEmail
 */
com_zimbra_apptsummary_HandlerObject.prototype._sendEmailErrCallback =
function(param1, param2) {
	appCtxt.getAppController().setStatusMsg(this.getMessage("ApptSummary_error_calendarparse"), ZmStatusView.LEVEL_WARNING);
};


/*
 * -------------------------------------
 * Preference Dialog related functions
 * -------------------------------------
 */

/**
 * This method is called when the panel is double-clicked.
 * 
 */
com_zimbra_apptsummary_HandlerObject.prototype.doubleClicked = function() {
	this.singleClicked();
};

/**
 * This method is called when the panel is single-clicked.
 * 
 */
com_zimbra_apptsummary_HandlerObject.prototype.singleClicked = function() {
	this._displayPrefDialog();
};

/**
 * Displays the preferences dialog.
 * 
 */
com_zimbra_apptsummary_HandlerObject.prototype._displayPrefDialog =
function() {
	//if zimlet dialog already exists...
	if (this.pbDialog) {
		this.pbDialog.popup();//simply popup the dialog
		return;
	}
	this.pView = new DwtComposite(this.getShell());
	//this.pView.setSize("150", "25");
	this.pView.getHtmlElement().style.overflow = "auto";
	this.pView.getHtmlElement().innerHTML = this._createPreferenceView();
	
	var dialog_args = {
			title	: this.getMessage("ApptSummary_dialog_preferences_title"),
			view	: this.pView,
			standardButtons : [DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON],
			parent	: this.getShell()
		};
	
	this.pbDialog = new ZmDialog(dialog_args);
	this.pbDialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._okBtnListner));
	this._setPreferencesChkBoxVal();
	this.pbDialog.popup();
};

/**
 * Sets the preferences.
 */
com_zimbra_apptsummary_HandlerObject.prototype._setPreferencesChkBoxVal =
function() {
	if (this.getUserProperty(com_zimbra_apptsummary_HandlerObject.USER_PROP_ONLY_SEND_APPTS) == "true") {
		document.getElementById(com_zimbra_apptsummary_HandlerObject.ELEMENT_ONLY_SEND_APPTS).checked = true;
	}
};

/**
 * Creates the preferences view.
 * 
 * @return	{string}	the view HTML
 * @see		_displayPrefDialog
 */
com_zimbra_apptsummary_HandlerObject.prototype._createPreferenceView =
function() {
	var html = new Array();
	var i = 0;
	html[i++] = "<DIV>";
	html[i++] = "<input id='";
	html[i++] = com_zimbra_apptsummary_HandlerObject.ELEMENT_ONLY_SEND_APPTS;
	html[i++] = "'  type='checkbox'/>";
	html[i++] = this.getMessage("ApptSummary_sendApptEmailOnlyWhenThereAreEmails");
	html[i++] = "</DIV>";
	return html.join("");
};

/**
 * Preferences dialog OK button listener.
 * 
 * @see		_displayPrefDialog
 */
com_zimbra_apptsummary_HandlerObject.prototype._okBtnListner =
function() {
	this.setUserProperty(com_zimbra_apptsummary_HandlerObject.USER_PROP_ONLY_SEND_APPTS, document.getElementById(com_zimbra_apptsummary_HandlerObject.ELEMENT_ONLY_SEND_APPTS).checked, true);
	appCtxt.getAppController().setStatusMsg(this.getMessage("ApptSummary_preferences_saved"), ZmStatusView.LEVEL_INFO);
	this.pbDialog.popdown();//hide the dialog
};

/*
 * -------------------------------------
 * Supporting functions
 * -------------------------------------
 */

/**
 * Gets today as a string.
 * 
 * @return	{string}	today as a string
 */
com_zimbra_apptsummary_HandlerObject.prototype._getTodayStr =
function() {
	var todayDate = new Date();
	var todayStart = new Date(todayDate.getFullYear(), todayDate.getMonth(), todayDate.getDate());
	return this._formatDate(todayStart.getMonth() + 1, todayStart.getDate(), todayStart.getFullYear());
};

/**
 * Formats the date.
 * 
 * @param	{string}	month		the month
 * @param	{string}	day		the day
 * @param	{string}	year		the year
 * @return	{string}	the formatted date
 */
com_zimbra_apptsummary_HandlerObject.prototype._formatDate =
function(month, day, year) {
	var fString = [];
	var ds = I18nMsg.formatDateShort.toLowerCase();
	var arry = [];
	arry.push({name:"m", indx:ds.indexOf("m")});
	arry.push({name:"yyyy", indx:ds.indexOf("yyyy")});
	arry.push({name:"d", indx:ds.indexOf("d")});
	var sArry = arry.sort(emailappts_sortTimeObjs);
	for (var i = 0; i < sArry.length; i++) {
		var name = sArry[i].name;
		if (name == "m") {
			fString.push(month);
		} else if (name == "yyyy") {
			fString.push(year);
		} else if (name == "d") {
			fString.push(day);
		}
	}
	return fString.join("/");
};

function emailappts_sortTimeObjs(a, b) {
	var x = parseInt(a.indx);
	var y = parseInt(b.indx);
	return ((x > y) ? 1 : ((x < y) ? -1 : 0));
}